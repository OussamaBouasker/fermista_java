package tn.fermista.controllers;

import tn.fermista.models.User;
import tn.fermista.models.Roles;
import tn.fermista.services.ServiceUser;
import tn.fermista.utils.UserSession;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.text.Text;
import javafx.stage.Stage;

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
    private boolean isPasswordVisible = false;

    @FXML
    public void initialize() {
        // Vérifier si un utilisateur est déjà connecté
        User savedUser = UserSession.loadUser();
        if (savedUser != null && rememberMe != null) {
            // Pré-remplir les champs si l'utilisateur a choisi "Remember Me"
            loginEmail.setText(savedUser.getEmail());
            // Ne pas pré-remplir le mot de passe pour des raisons de sécurité
            rememberMe.setSelected(true);
        }
    }

    @FXML
    private void login() throws IOException {
        // Vérifier si les champs sont vides
        if (loginEmail.getText().isEmpty() || loginPasswd.getText().isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Veuillez remplir tous les champs");
            return;
        }

        // Récupérer le mot de passe en fonction de sa visibilité
        String password;
        if (isPasswordVisible) {
            password = VloginPasswd.getText();
        } else {
            password = loginPasswd.getText();
        }

        // Tenter de connecter l'utilisateur
        User user = serviceUser.signIn(loginEmail.getText(), password);
        
        if (user != null) {
            // Sauvegarder la session si "Remember Me" est coché
            if (rememberMe != null && rememberMe.isSelected()) {
                UserSession.saveUser(user);
            } else {
                // Effacer la session précédente si "Remember Me" n'est pas coché
                UserSession.clearUser();
            }
            
            // Définir l'utilisateur courant pour le NavigationController
            NavigationController.setCurrentUser(user);
            
            // Rediriger vers la page appropriée selon le rôle
            String fxmlPath = getFxmlPathForRole(user.getRoles());
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            Stage stage = (Stage) login.getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } else {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Email ou mot de passe incorrect");
        }
    }

    private String getFxmlPathForRole(Roles role) {
        switch (role) {
            case ROLE_ADMIN:
                return "/DashboardTemplate.fxml";
            case ROLE_CLIENT:
                return "/DashboardTemplate.fxml";
            case ROLE_FORMATEUR:
                return "/DashboardTemplate.fxml";
            case ROLE_AGRICULTOR:
                return "/DashboardTemplate.fxml";
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
        // Redirection vers la page de récupération de mot de passe
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/forgot-password.fxml"));
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
            VloginPasswd.setText(loginPasswd.getText());
            VloginPasswd.setVisible(true);
            loginPasswd.setVisible(false);
        } else {
            loginPasswd.setText(VloginPasswd.getText());
            loginPasswd.setVisible(true);
            VloginPasswd.setVisible(false);
        }
    }

    private void showAlert(Alert.AlertType alertType, String title, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}

