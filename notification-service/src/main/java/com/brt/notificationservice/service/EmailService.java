package com.brt.notificationservice.service;

import lombok.RequiredArgsConstructor;
import lombok.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
// EmailService.java — version simulation, aucune dépendance mail
public class EmailService {

    public void envoyer(String destinataire, String sujet, String contenu) {
        System.out.println("=== EMAIL ENVOYÉ ===");
        System.out.println("À       : " + destinataire);
        System.out.println("Sujet   : " + sujet);
        System.out.println("Contenu : " + contenu);
        System.out.println("===================");
    }
}