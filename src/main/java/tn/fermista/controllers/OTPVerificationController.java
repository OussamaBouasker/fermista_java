package tn.fermista.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.io.IOException;

public class OTPVerificationController {
    @FXML
    private TextField otpField;
    @FXML
    private Button validateButton;
    @FXML
    private Button backButton;
    @FXML
    private Text errorText;
    @FXML
    private Text title;
    @FXML
    private Text loginLink;

    private String email;
    private String correctOTP;

    public void setEmailAndOTP(String email, String otp) {
        this.email = email;
        this.correctOTP = otp;
    }

    @FXML
    public void initialize() {
        errorText.setVisible(false);
    }

    @FXML
    private void handleBack() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/MailVerification.fxml"));
        Parent root = loader.load();
        Stage stage = (Stage) backButton.getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.show();
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
    private void handleValidate() throws IOException {
        String enteredOTP = otpField.getText().trim();
        
        if (enteredOTP.isEmpty()) {
            showError("Please enter the OTP");
            return;
        }

        if (!enteredOTP.equals(correctOTP)) {
            showError("Invalid OTP. Please try again.");
            return;
        }

        // Navigate to reset password page
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/ResetPassword.fxml"));
        Parent root = loader.load();
        
        // Pass the email to the reset password controller
        ResetPasswordController resetController = loader.getController();
        resetController.setEmail(email);
        
        Stage stage = (Stage) validateButton.getScene().getWindow();
        stage.setScene(new Scene(root));
        stage.show();
    }

    private void showError(String message) {
        errorText.setText(message);
        errorText.setVisible(true);
    }
} 