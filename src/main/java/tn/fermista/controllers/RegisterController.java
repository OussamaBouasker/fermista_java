package tn.fermista.controllers;

import javafx.application.Application;
import javafx.event.ActionEvent;
import javafx.stage.Stage;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Label;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.text.Text;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import com.github.cage.Cage;
import com.github.cage.GCage;
import tn.fermista.models.User;
import tn.fermista.models.Roles;
import tn.fermista.models.Admin;
import tn.fermista.models.Client;
import tn.fermista.models.Formateur;
import tn.fermista.models.Agriculteur;
import tn.fermista.models.Veterinaire;
import tn.fermista.services.ServiceUser;
import tn.fermista.services.ServiceAdmin;
import tn.fermista.services.ServiceClient;
import tn.fermista.services.ServiceFormateur;
import tn.fermista.services.ServiceAgriculteur;
import tn.fermista.services.ServiceVeterinaire;
import tn.fermista.utils.PasswordUtils;

import java.util.regex.Pattern;
import java.io.IOException;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import javax.imageio.ImageIO;

public class RegisterController extends Application {

    @FXML
    private TextField firstname;
    
    @FXML
    private TextField lastname;
    
    @FXML
    private TextField number;
    
    @FXML
    private TextField email;
    
    @FXML
    private TextField visiblePassword;
    
    @FXML
    private TextField password;
    
    @FXML
    private Button togglePasswordButton;
    
    @FXML
    private ComboBox<String> rolesComboBox;
    
    @FXML
    private Button submit;
    
    @FXML
    private Text loginLink;

    @FXML
    private Label captchaLabel;

    @FXML
    private ImageView captchaImageView;

    @FXML
    private TextField captchaInput;

    private ServiceUser serviceUser;
    private ServiceAdmin serviceAdmin;
    private ServiceClient serviceClient;
    private ServiceFormateur serviceFormateur;
    private ServiceAgriculteur serviceAgriculteur;
    private ServiceVeterinaire serviceVeterinaire;

    private Cage cage;
    private String currentCaptcha;

    // Patterns pour la validation
    private static final Pattern EMAIL_PATTERN = Pattern.compile("^[A-Za-z0-9+_.-]+@(.+)$");
    private static final Pattern PHONE_PATTERN = Pattern.compile("^[0-9]{8}$");
    private static final Pattern NAME_PATTERN = Pattern.compile("^[A-Za-zÀ-ÿ\\s]{2,50}$");
    private static final Pattern PASSWORD_PATTERN = Pattern.compile("^(?=.*[0-9])(?=.*[a-z])(?=.*[A-Z])(?=.*[@#$%^&+=])(?=\\S+$).{8,}$");

    private boolean isPasswordVisible = false;

    @FXML
    public void initialize() {
        serviceUser = new ServiceUser();
        serviceAdmin = new ServiceAdmin();
        serviceClient = new ServiceClient();
        serviceFormateur = new ServiceFormateur();
        serviceAgriculteur = new ServiceAgriculteur();
        serviceVeterinaire = new ServiceVeterinaire();
        
        // Initialize SimpleCaptcha
        cage = new GCage();
        
        // Generate initial CAPTCHA
        generateCaptcha();
    }

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {

    }

    private void generateCaptcha() {
        // Generate a new CAPTCHA
        currentCaptcha = cage.getTokenGenerator().next();
        byte[] imageBytes = cage.draw(currentCaptcha);

        // Convert CAPTCHA to JavaFX Image
        try {
            ByteArrayInputStream inputStream = new ByteArrayInputStream(imageBytes);
            Image image = new Image(inputStream);
            captchaImageView.setImage(image);
        } catch (Exception e) {
            showAlert(AlertType.ERROR, "Erreur", "Erreur lors de la génération du CAPTCHA");
        }
    }

    @FXML
    public void refreshCaptcha(ActionEvent event) {
        generateCaptcha();
        captchaInput.clear();
    }

    @FXML
    public void togglePasswordVisibility() {
        isPasswordVisible = !isPasswordVisible;

        if (isPasswordVisible) {
            // Copier le texte du PasswordField vers le TextField
            visiblePassword.setText(password.getText());
            // Afficher le TextField et cacher le PasswordField
            visiblePassword.setVisible(true);
            password.setVisible(false);
            // Mettre le focus sur le TextField
            visiblePassword.requestFocus();
            visiblePassword.positionCaret(visiblePassword.getText().length());
        } else {
            // Copier le texte du TextField vers le PasswordField
            password.setText(visiblePassword.getText());
            // Afficher le PasswordField et cacher le TextField
            password.setVisible(true);
            visiblePassword.setVisible(false);
            // Mettre le focus sur le PasswordField
            password.requestFocus();
            password.positionCaret(password.getText().length());
        }
    }

    @FXML
    public void signUp(javafx.event.ActionEvent actionEvent) {
        // Vérification des champs vides
        if (firstname.getText().isEmpty() || lastname.getText().isEmpty() || 
            number.getText().isEmpty() || email.getText().isEmpty() || 
            (password.getText().isEmpty() && visiblePassword.getText().isEmpty()) || 
            rolesComboBox.getValue() == null ||
            captchaInput.getText().isEmpty()) {
            showAlert(AlertType.ERROR, "Erreur", "Veuillez remplir tous les champs");
            return;
        }

        // Get the password from either visible or hidden field
        String passwordText = isPasswordVisible ? visiblePassword.getText() : password.getText();

        // Vérification du CAPTCHA
        if (!captchaInput.getText().equals(currentCaptcha)) {
            showAlert(AlertType.ERROR, "Erreur", "Le code CAPTCHA est incorrect");
            generateCaptcha();
            captchaInput.clear();
            return;
        }

        // Validation des champs avec le bon mot de passe
        if (!validateFields(passwordText)) {
            return;
        }

        try {
            String roleStr = rolesComboBox.getValue();
            Roles role = Roles.valueOf(roleStr);
            
            // Hachage du mot de passe
            String hashedPassword = PasswordUtils.hashPassword(passwordText);

            // Dans la méthode signUp, avant le switch case
            User user = null;
            switch (role) {
                case ROLE_ADMIN:
                    Admin admin = new Admin();
                    admin.setFirstName(firstname.getText());
                    admin.setLastName(lastname.getText());
                    admin.setNumber(number.getText());
                    admin.setEmail(email.getText());
                    admin.setPassword(hashedPassword);
                    admin.setRoles(role);
                    admin.setState(true); // Définir state à true
                    serviceAdmin.ajouter(admin);
                    user = admin;
                    break;

                case ROLE_CLIENT:
                    Client client = new Client();
                    client.setFirstName(firstname.getText());
                    client.setLastName(lastname.getText());
                    client.setNumber(number.getText());
                    client.setEmail(email.getText());
                    client.setPassword(hashedPassword);
                    client.setRoles(role);
                    client.setState(true); // Définir state à true
                    serviceClient.ajouter(client);
                    break;

                case ROLE_FORMATEUR:
                    Formateur formateur = new Formateur();
                    formateur.setFirstName(firstname.getText());
                    formateur.setLastName(lastname.getText());
                    formateur.setNumber(number.getText());
                    formateur.setEmail(email.getText());
                    formateur.setPassword(hashedPassword);
                    formateur.setRoles(role);
                    formateur.setState(true);
                    serviceFormateur.ajouter(formateur);
                    break;

                case ROLE_AGRICULTOR:
                    Agriculteur agriculteur = new Agriculteur();
                    agriculteur.setFirstName(firstname.getText());
                    agriculteur.setLastName(lastname.getText());
                    agriculteur.setNumber(number.getText());
                    agriculteur.setEmail(email.getText());
                    agriculteur.setPassword(hashedPassword);
                    agriculteur.setRoles(role);
                    agriculteur.setState(true);
                    serviceAgriculteur.ajouter(agriculteur);
                    break;

                case ROLE_VETERINAIR:
                    Veterinaire veterinaire = new Veterinaire();
                    veterinaire.setFirstName(firstname.getText());
                    veterinaire.setLastName(lastname.getText());
                    veterinaire.setNumber(number.getText());
                    veterinaire.setEmail(email.getText());
                    veterinaire.setPassword(hashedPassword);
                    veterinaire.setRoles(role);
                    veterinaire.setState(true);
                    serviceVeterinaire.ajouter(veterinaire);
                    break;
            }

            showAlert(AlertType.INFORMATION, "Succès", "Inscription réussie !");
            
            // Redirection vers la page de login
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/login.fxml"));
                Parent root = loader.load();
                Scene scene = new Scene(root);
                Stage stage = (Stage) submit.getScene().getWindow();
                stage.setScene(scene);
                stage.show();
            } catch (IOException e) {
                showAlert(AlertType.ERROR, "Erreur", "Erreur lors de la redirection : " + e.getMessage());
            }

        } catch (Exception e) {
            showAlert(AlertType.ERROR, "Erreur", "Erreur lors de l'inscription : " + e.getMessage());
        }
    }

    @FXML
    public void navigateToLogin(javafx.scene.input.MouseEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/login.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            Stage stage = (Stage) loginLink.getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            showAlert(AlertType.ERROR, "Erreur", "Erreur lors de la redirection : " + e.getMessage());
        }
    }

    private boolean validateFields(String passwordText) {
        // Validation du prénom
        if (!NAME_PATTERN.matcher(firstname.getText()).matches()) {
            showAlert(AlertType.ERROR, "Erreur", "Le prénom doit contenir entre 2 et 50 caractères et ne peut contenir que des lettres");
            return false;
        }

        // Validation du nom
        if (!NAME_PATTERN.matcher(lastname.getText()).matches()) {
            showAlert(AlertType.ERROR, "Erreur", "Le nom doit contenir entre 2 et 50 caractères et ne peut contenir que des lettres");
            return false;
        }

        // Validation du numéro de téléphone
        if (!PHONE_PATTERN.matcher(number.getText()).matches()) {
            showAlert(AlertType.ERROR, "Erreur", "Le numéro de téléphone doit contenir exactement 8 chiffres");
            return false;
        }

        // Validation de l'email
        if (!EMAIL_PATTERN.matcher(email.getText()).matches()) {
            showAlert(AlertType.ERROR, "Erreur", "L'adresse email n'est pas valide");
            return false;
        }

        // Validation du mot de passe
        if (!PASSWORD_PATTERN.matcher(passwordText).matches()) {
            showAlert(AlertType.ERROR, "Erreur", 
                "Le mot de passe doit contenir au moins 8 caractères, une majuscule, une minuscule, un chiffre et un caractère spécial (@#$%^&+=)");
            return false;
        }

        return true;
    }

    private void clearFields() {
        firstname.clear();
        lastname.clear();
        number.clear();
        email.clear();
        password.clear();
        visiblePassword.clear();
        rolesComboBox.setValue(null);
    }

    private void showAlert(AlertType alertType, String title, String content) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
