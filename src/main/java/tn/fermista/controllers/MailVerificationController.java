package tn.fermista.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import tn.fermista.services.ServiceUser;
import tn.fermista.utils.EmailSender;
import tn.fermista.utils.OTPGenerator;

import java.io.IOException;

public class MailVerificationController {
    @FXML
    private TextField emailField;
    @FXML
    private Button sendButton;
    @FXML
    private Text errorText;
    @FXML
    private Text title;
    @FXML
    private Text loginLink;

    private ServiceUser serviceUser = new ServiceUser();
    private String generatedOTP;

    @FXML
    public void initialize() {
        errorText.setVisible(false);
    }

    @FXML
    private void navigateToLogin() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/login.fxml"));
        Parent root = loader.load();
        Stage stage = (Stage) loginLink.getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.show();
    }

    @FXML
    private void handleSend() throws IOException {
        String email = emailField.getText().trim();
        
        if (email.isEmpty()) {
            showError("Please enter your email address");
            return;
        }

        if (!serviceUser.emailExists(email)) {
            showError("Email not found in our database");
            return;
        }

        // Generate OTP
        generatedOTP = OTPGenerator.generateOTP();
        
        // Send OTP via email
        try {
            EmailSender.sendOTP(email, generatedOTP);
            
            // Navigate to OTP verification page
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/OTPVerification.fxml"));
            Parent root = loader.load();
            
            // Pass the email and OTP to the next controller
            OTPVerificationController otpController = loader.getController();
            otpController.setEmailAndOTP(email, generatedOTP);
            
            Stage stage = (Stage) sendButton.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            showError("Failed to send OTP. Please try again.");
            e.printStackTrace();
        }
    }

    private void showError(String message) {
        errorText.setText(message);
        errorText.setVisible(true);
    }
}
