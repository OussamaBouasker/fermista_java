package tn.fermista.utils;

import com.twilio.Twilio;
import com.twilio.rest.api.v2010.account.Message;
import com.twilio.type.PhoneNumber;

public class SMSSender {
    // Twilio credentials - replace with your actual credentials
    private static final String ACCOUNT_SID = "ACcafa8d82a7a3ce702e28e8d13c84f92b";
    private static final String AUTH_TOKEN = "f332fa6eb6f1a3f3079d43a84cb4e215";
    private static final String TWILIO_PHONE_NUMBER = "+16592469825";

    static {
        System.out.println("Initializing Twilio...");
        Twilio.init(ACCOUNT_SID, AUTH_TOKEN);
        System.out.println("Twilio initialized successfully");
    }

    public static void sendReclamationStatusUpdate(String userPhoneNumber, String reclamationTitle, String newStatus) {
        System.out.println("Attempting to send SMS to: " + userPhoneNumber);
        System.out.println("Reclamation title: " + reclamationTitle);
        System.out.println("New status: " + newStatus);

        if (userPhoneNumber == null || userPhoneNumber.isEmpty()) {
            System.err.println("Phone number is null or empty");
            return;
        }

        String message = String.format(
            "Bonjour, le statut de votre réclamation '%s' a été mis à jour à '%s'. - Fermista",
            reclamationTitle,
            newStatus
        );

        try {
            System.out.println("Creating message with Twilio...");
            Message twilioMessage = Message.creator(
                new PhoneNumber(userPhoneNumber),
                new PhoneNumber(TWILIO_PHONE_NUMBER),
                message
            ).create();
            
            System.out.println("SMS sent successfully! Message SID: " + twilioMessage.getSid());
        } catch (Exception e) {
            System.err.println("Failed to send SMS: " + e.getMessage());
            e.printStackTrace();
        }
    }
} 