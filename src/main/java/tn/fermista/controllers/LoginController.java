package tn.fermista.controllers;

import tn.fermista.models.User;
import tn.fermista.models.Roles;
import tn.fermista.services.ServiceUser;
import tn.fermista.utils.UserSession;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import javafx.scene.image.ImageView;
import javafx.scene.image.Image;

import java.io.IOException;

public class LoginController {

    public Text signUP;
    public Text forgotPass;
    public TextField VloginPasswd;
    public CheckBox rememberMe;

    ServiceUser serviceUser = new ServiceUser();

    @FXML
    private Button login;
    @FXML
    private TextField loginEmail;
    @FXML
    private PasswordField loginPasswd;
    @FXML
    private Text welcome;
    @FXML
    private Button togglePasswordButton;
    private boolean isPasswordVisible = false;

    @FXML
    public void initialize() {
        // Vérifier si un utilisateur est déjà connecté
        User savedUser = UserSession.loadUser();
        if (savedUser != null && rememberMe != null) {
            loginEmail.setText(savedUser.getEmail());
            rememberMe.setSelected(true);
        }
        
        // Initialize password fields
        VloginPasswd.setVisible(false);
        loginPasswd.setVisible(true);
    }

    @FXML
    private void login() throws IOException {
        if (loginEmail.getText().isEmpty() || loginPasswd.getText().isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Veuillez remplir tous les champs");
            return;
        }

        String password = isPasswordVisible ? VloginPasswd.getText() : loginPasswd.getText();

        User user = serviceUser.signIn(loginEmail.getText(), password);

        if (user != null) {
            UserSession.setCurrentUser(user);

            if (rememberMe != null && rememberMe.isSelected()) {
                UserSession.saveUser(user);
            } else {
                UserSession.clearSavedUser();
            }

            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/HomePage.fxml"));
                Parent root = loader.load();
                Scene scene = new Scene(root);
                Stage stage = (Stage) login.getScene().getWindow();
                stage.setScene(scene);
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible de charger la page d'accueil");
            }
        } else {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Email ou mot de passe incorrect");
        }
    }

    private String getFxmlPathForRole(Roles role) {
        switch (role) {
            case ROLE_ADMIN:
            case ROLE_CLIENT:
            case ROLE_FORMATEUR:
            case ROLE_AGRICULTOR:
            case ROLE_VETERINAIR:
                return "/DashboardTemplate.fxml";
            default:
                return "/DashboardTemplate.fxml";
        }
    }

    @FXML
    private void signUp() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/register.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root);
        Stage stage = (Stage) signUP.getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    private void forgetPass() throws IOException {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/MailVerification.fxml"));
        Parent root = loader.load();
        Scene scene = new Scene(root);
        Stage stage = (Stage) forgotPass.getScene().getWindow();
        stage.setScene(scene);
        stage.show();
    }

    @FXML
    private void togglePasswordVisibility() {
        isPasswordVisible = !isPasswordVisible;

        if (isPasswordVisible) {
            // Copier le texte du PasswordField vers le TextField
            VloginPasswd.setText(loginPasswd.getText());
            // Afficher le TextField et cacher le PasswordField
            VloginPasswd.setVisible(true);
            loginPasswd.setVisible(false);
            // Mettre le focus sur le TextField
            VloginPasswd.requestFocus();
            VloginPasswd.positionCaret(VloginPasswd.getText().length());
        } else {
            // Copier le texte du TextField vers le PasswordField
            loginPasswd.setText(VloginPasswd.getText());
            // Afficher le PasswordField et cacher le TextField
            loginPasswd.setVisible(true);
            VloginPasswd.setVisible(false);
            // Mettre le focus sur le PasswordField
            loginPasswd.requestFocus();
            loginPasswd.positionCaret(loginPasswd.getText().length());
        }
    }

    private void showAlert(Alert.AlertType alertType, String title, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        // Style pour le contenu

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
}
