package com.brt.fleetmanagementservice;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.openfeign.EnableFeignClients;

@EnableFeignClients
@SpringBootApplication
public class FleetManagementServiceApplication {

	public static void main(String[] args) {
		SpringApplication.run(FleetManagementServiceApplication.class, args);
	}

}
