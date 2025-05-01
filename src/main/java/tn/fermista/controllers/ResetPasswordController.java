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
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.ButtonType;

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

    private void showAlert(AlertType alertType, String title, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        
        // Style personnalisé pour l'alerte
        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.setStyle("-fx-background-color: #FFF0F5;"); // Rose pastel clair
        dialogPane.getStyleClass().add("custom-alert");
        
        // Style pour le contenu
        Label contentLabel = new Label(content);
        contentLabel.setStyle("-fx-text-fill: #333333; -fx-font-size: 14px; -fx-font-family: 'Segoe UI';");
        dialogPane.setContent(contentLabel);
        
        // Style pour les boutons
        ButtonType buttonType = alert.getButtonTypes().get(0);
        Button button = (Button) dialogPane.lookupButton(buttonType);
        button.setStyle("-fx-background-color: #bd454f; -fx-text-fill: white; -fx-font-weight: bold;");
        
        alert.showAndWait();
    }

    @FXML
    private void handleReset() throws IOException {
        String newPassword = passwordField.getText().trim();
        String confirmPassword = confirmPasswordField.getText().trim();
        
        if (newPassword.isEmpty() || confirmPassword.isEmpty()) {
            showAlert(AlertType.ERROR, "Erreur", "Veuillez remplir tous les champs");
            return;
        }

        if (!newPassword.equals(confirmPassword)) {
            showAlert(AlertType.ERROR, "Erreur", "Les mots de passe ne correspondent pas");
            return;
        }

        if (newPassword.length() < 8) {
            showAlert(AlertType.ERROR, "Erreur", "Le mot de passe doit contenir au moins 8 caractères");
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
            showAlert(AlertType.ERROR, "Erreur", "Failed to reset password. Please try again.");
            e.printStackTrace();
        }
    }
}
