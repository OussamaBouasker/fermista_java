package tn.fermista.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import tn.fermista.services.ServiceUser;
import tn.fermista.utils.PasswordUtils;

import java.io.IOException;

public class ResetPasswordController {
    @FXML
    private PasswordField passwordField;
    @FXML
    private PasswordField confirmPasswordField;
    @FXML
    private Button resetButton;
    @FXML
    private Text errorText;
    @FXML
    private Text title;
    @FXML
    private Text loginLink;

    private String email;
    private ServiceUser serviceUser = new ServiceUser();

    public void setEmail(String email) {
        this.email = email;
    }

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
    private void handleReset() throws IOException {
        String newPassword = passwordField.getText().trim();
        String confirmPassword = confirmPasswordField.getText().trim();
        
        if (newPassword.isEmpty() || confirmPassword.isEmpty()) {
            showError("Please fill in all fields");
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            showError("Passwords do not match");
            return;
        }

        if (newPassword.length() < 8) {
            showError("Password must be at least 8 characters long");
            return;
        }

        try {
            // Update password in database
            serviceUser.updatePassword(email, PasswordUtils.hashPassword(newPassword));
            
            // Navigate back to login page
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/login.fxml"));
            Parent root = loader.load();
            
            Stage stage = (Stage) resetButton.getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            showError("Failed to reset password. Please try again.");
            e.printStackTrace();
        }
    }

    private void showError(String message) {
        errorText.setText(message);
        errorText.setVisible(true);
    }
}
