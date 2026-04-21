package com.brt.gateway.filter;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.node.ArrayNode;
import com.fasterxml.jackson.databind.node.ObjectNode;
import org.reactivestreams.Publisher;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.gateway.filter.GatewayFilterChain;
import org.springframework.cloud.gateway.filter.GlobalFilter;
import org.springframework.core.Ordered;
import org.springframework.core.io.buffer.DataBuffer;
import org.springframework.core.io.buffer.DataBufferFactory;
import org.springframework.core.io.buffer.DataBufferUtils;
import org.springframework.http.server.reactive.ServerHttpResponse;
import org.springframework.http.server.reactive.ServerHttpResponseDecorator;
import org.springframework.stereotype.Component;
import org.springframework.web.server.ServerWebExchange;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.StreamSupport;

@Component
public class SwaggerServerRewriteFilter implements GlobalFilter, Ordered {

    @Value("${server.port:8080}")
    private int gatewayPort;

    @Override
    public Mono<Void> filter(ServerWebExchange exchange, GatewayFilterChain chain) {
        String path = exchange.getRequest().getURI().getPath();

        // Appliquer uniquement sur les routes de docs
        if (!path.endsWith("/v3/api-docs")) {
            return chain.filter(exchange);
        }

        ServerHttpResponse originalResponse = exchange.getResponse();
        DataBufferFactory bufferFactory = originalResponse.bufferFactory();

        ServerHttpResponseDecorator decoratedResponse = new ServerHttpResponseDecorator(originalResponse) {
            @Override
            public Mono<Void> writeWith(Publisher<? extends DataBuffer> body) {
                if (body instanceof Flux) {
                    Flux<? extends DataBuffer> fluxBody = (Flux<? extends DataBuffer>) body;
                    return super.writeWith(fluxBody.buffer().map(dataBuffers -> {
                        // Assembler tous les chunks
                        DataBuffer joinedBuffer = bufferFactory.join(dataBuffers);
                        byte[] content = new byte[joinedBuffer.readableByteCount()];
                        joinedBuffer.read(content);
                        DataBufferUtils.release(joinedBuffer);

                        // Remplacer les URLs de serveur
                        String bodyStr = new String(content, StandardCharsets.UTF_8);
                        bodyStr = rewriteServerUrls(bodyStr, exchange);

                        byte[] newContent = bodyStr.getBytes(StandardCharsets.UTF_8);
                        getDelegate().getHeaders().setContentLength(newContent.length);
                        return bufferFactory.wrap(newContent);
                    }));
                }
                return super.writeWith(body);
            }
        };

        return chain.filter(exchange.mutate().response(decoratedResponse).build());
    }

    private String rewriteServerUrls(String body, ServerWebExchange exchange) {
        try {
            ObjectMapper mapper = new ObjectMapper();
            JsonNode root = mapper.readTree(body);
            ObjectNode objNode = (ObjectNode) root;

            // 1. Réécrire les servers (déjà fait)
            ArrayNode servers = mapper.createArrayNode();
            ObjectNode server = mapper.createObjectNode();
            server.put("url", "http://localhost:" + gatewayPort);
            server.put("description", "API Gateway");
            servers.add(server);
            objNode.set("servers", servers);

            // 2. Ajouter le Bearer scheme
            ObjectNode components = (ObjectNode) objNode.get("components");
            if (components == null) {
                components = mapper.createObjectNode();
                objNode.set("components", components);
            }
            ObjectNode securitySchemes = mapper.createObjectNode();
            ObjectNode bearerScheme = mapper.createObjectNode();
            bearerScheme.put("type", "http");
            bearerScheme.put("scheme", "bearer");
            bearerScheme.put("bearerFormat", "JWT");
            securitySchemes.set("Bearer Authentication", bearerScheme);
            components.set("securitySchemes", securitySchemes);

            // 3. Supprimer X-User-* des paramètres de chaque opération
            List<String> headersToHide = List.of("X-User-Id", "X-User-Role", "X-User-Email");
            JsonNode paths = objNode.get("paths");
            if (paths != null) {
                paths.fields().forEachRemaining(pathEntry -> {
                    pathEntry.getValue().fields().forEachRemaining(methodEntry -> {
                        JsonNode parameters = methodEntry.getValue().get("parameters");
                        if (parameters instanceof ArrayNode arrayNode) {
                            for (int i = arrayNode.size() - 1; i >= 0; i--) {
                                JsonNode param = arrayNode.get(i);
                                if ("header".equals(param.path("in").asText())
                                        && headersToHide.contains(param.path("name").asText())) {
                                    arrayNode.remove(i);
                                }
                            }
                        }
                    });
                });
            }

            // 4. Ajouter security globale
            ArrayNode securityArray = mapper.createArrayNode();
            ObjectNode securityItem = mapper.createObjectNode();
            securityItem.set("Bearer Authentication", mapper.createArrayNode());
            securityArray.add(securityItem);
            objNode.set("security", securityArray);

            return mapper.writeValueAsString(root);
        } catch (Exception e) {
            return body;
        }
    }

    @Override
    public int getOrder() {
        return -2; // Avant les autres filtres
    }
}
