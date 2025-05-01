package tn.fermista.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.ButtonBar.ButtonData;
import javafx.scene.layout.GridPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import tn.fermista.controllers.MapViewController.LocationCallback;
import tn.fermista.models.Commande;
import tn.fermista.models.Panier;
import tn.fermista.services.ServiceCommande;
import tn.fermista.utils.FullScreenUtil;
import tn.fermista.utils.WebViewUtils;
import tn.fermista.utils.EmailService;
import tn.fermista.utils.SMSService;
import tn.fermista.controllers.PaiementController;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;
import java.util.regex.Pattern;
import javafx.application.Platform;

public class CommandeFormController implements Initializable {

    @FXML private TextField fullNameField;
    @FXML private TextField emailField;
    @FXML private TextField phoneField;
    @FXML private TextField locationField; // Champ texte pour afficher la localisation sélectionnée
    @FXML private TextField addressField;
    @FXML private TextField postalCodeField;
    @FXML private TextArea notesField;
    
    @FXML private Label fullNameError;
    @FXML private Label emailError;
    @FXML private Label phoneError;
    @FXML private Label locationError;
    @FXML private Label addressError;
    @FXML private Label postalCodeError;
    
    private final Panier panier = Panier.getInstance();
    private final ServiceCommande serviceCommande = new ServiceCommande();
    
    // Informations de localisation sélectionnées sur la carte
    private double selectedLat;
    private double selectedLng;
    private String selectedAddress;
    private String selectedCity;
    private String selectedState;
    private String selectedCountry;
    
    // Regex pour la validation d'email
    private static final Pattern EMAIL_PATTERN = 
        Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);
    
    // Regex pour la validation du numéro de téléphone (format tunisien)
    private static final Pattern PHONE_PATTERN = 
        Pattern.compile("^[2-9]\\d{7}$");
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        // Configurer les styles d'erreur
        configureErrorLabels();
        
        // Appliquer le mode plein écran
        Platform.runLater(() -> {
            try {
                Stage stage = (Stage) fullNameField.getScene().getWindow();
                if (stage != null) {
                    FullScreenUtil.setFullScreen(stage);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
    
    private void configureErrorLabels() {
        fullNameError.getStyleClass().add("error-label");
        emailError.getStyleClass().add("error-label");
        phoneError.getStyleClass().add("error-label");
        locationError.getStyleClass().add("error-label");
        addressError.getStyleClass().add("error-label");
        postalCodeError.getStyleClass().add("error-label");
    }
    
    @FXML
    private void handleShowMap() {
        try {
            // Vérifier si WebView est disponible
            if (!tn.fermista.utils.WebViewUtils.isWebViewAvailable()) {
                // Si WebView n'est pas disponible, afficher une boîte de dialogue pour saisir l'adresse manuellement
                showManualLocationDialog();
                return;
            }
            
            // Charger la vue de la carte
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/MapView.fxml"));
            Parent root = loader.load();
            
            // Configurer le contrôleur de la carte
            MapViewController controller = loader.getController();
            controller.setCallback(new LocationCallback() {
                @Override
                public void onLocationSelected(double lat, double lng, String address, String city, String state, String country) {
                    // Stocker les informations de localisation sélectionnées
                    selectedLat = lat;
                    selectedLng = lng;
                    selectedAddress = address;
                    selectedCity = city;
                    selectedState = state;
                    selectedCountry = country;
                    
                    // Mettre à jour le champ de texte de localisation
                    locationField.setText(address);
                    
                    // Suggérer la ville dans le champ d'adresse si celle-ci est vide
                    if (addressField.getText().trim().isEmpty() && city != null && !city.isEmpty()) {
                        addressField.setText(city);
                    }
                }
            });
            
            // Afficher la carte dans une nouvelle fenêtre modale
            Stage mapStage = new Stage();
            mapStage.initModality(Modality.APPLICATION_MODAL);
            mapStage.setTitle("Sélectionner votre localisation");
            mapStage.setScene(new Scene(root, 800, 600));
            mapStage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible d'ouvrir la carte: " + e.getMessage());
            // En cas d'erreur, proposer la saisie manuelle
            showManualLocationDialog();
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Erreur", "Une erreur est survenue lors de l'ouverture de la carte: " + e.getMessage());
            // En cas d'erreur, proposer la saisie manuelle
            showManualLocationDialog();
        }
    }
    
    /**
     * Affiche une boîte de dialogue pour saisir manuellement la localisation
     */
    private void showManualLocationDialog() {
        try {
            // Créer une boîte de dialogue
            Dialog<String> dialog = new Dialog<>();
            dialog.setTitle("Saisir votre localisation");
            dialog.setHeaderText("Veuillez saisir votre localisation manuellement");
            
            // Configurer les boutons
            ButtonType confirmButtonType = new ButtonType("Confirmer", ButtonBar.ButtonData.OK_DONE);
            dialog.getDialogPane().getButtonTypes().addAll(confirmButtonType, ButtonType.CANCEL);
            
            // Créer une liste déroulante avec les régions tunisiennes
            ComboBox<String> regionComboBox = new ComboBox<>();
            regionComboBox.getItems().addAll(
                "Tunis", "Ariana", "Ben Arous", "Manouba", "Nabeul", "Zaghouan", 
                "Bizerte", "Béja", "Jendouba", "Kef", "Siliana", "Sousse", 
                "Monastir", "Mahdia", "Sfax", "Kairouan", "Kasserine", "Sidi Bouzid", 
                "Gabès", "Medenine", "Tataouine", "Gafsa", "Tozeur", "Kebili"
            );
            regionComboBox.setPromptText("Sélectionnez votre région");
            
            // Créer un champ pour la ville
            TextField cityField = new TextField();
            cityField.setPromptText("Entrez votre ville");
            
            // Créer la disposition
            GridPane grid = new GridPane();
            grid.setHgap(10);
            grid.setVgap(10);
            grid.setPadding(new Insets(20, 150, 10, 10));
            grid.add(new Label("Région:"), 0, 0);
            grid.add(regionComboBox, 1, 0);
            grid.add(new Label("Ville:"), 0, 1);
            grid.add(cityField, 1, 1);
            
            dialog.getDialogPane().setContent(grid);
            
            // Activer/désactiver le bouton de confirmation en fonction des champs
            Node confirmButton = dialog.getDialogPane().lookupButton(confirmButtonType);
            confirmButton.setDisable(true);
            
            // Validation
            regionComboBox.valueProperty().addListener((observable, oldValue, newValue) -> {
                confirmButton.setDisable(newValue == null || newValue.isEmpty());
            });
            
            // Configurer le résultat
            dialog.setResultConverter(dialogButton -> {
                if (dialogButton == confirmButtonType) {
                    String region = regionComboBox.getValue();
                    String city = cityField.getText().trim();
                    return region + (city.isEmpty() ? "" : ", " + city);
                }
                return null;
            });
            
            // Afficher la boîte de dialogue et traiter le résultat
            dialog.showAndWait().ifPresent(result -> {
                locationField.setText(result);
                selectedCity = cityField.getText().trim();
                selectedState = regionComboBox.getValue();
                selectedCountry = "Tunisie";
                selectedAddress = result + ", Tunisie";
                
                // Suggérer la ville dans le champ d'adresse si celle-ci est vide
                if (addressField.getText().trim().isEmpty() && !cityField.getText().trim().isEmpty()) {
                    addressField.setText(cityField.getText().trim());
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible d'afficher la boîte de dialogue: " + e.getMessage());
        }
    }
    
    @FXML
    private void handleSubmit() {
        // Réinitialiser les messages d'erreur
        resetErrorMessages();
        
        // Valider le formulaire
        if (validateForm()) {
            try {
                // Créer la commande
                Commande commande = new Commande();
                commande.setDate(LocalDate.now());
                commande.setTotal(panier.getTotal());
                commande.setStatus("En attente");
                
                // Stocker les informations client dans l'objet Commande, même si elles ne sont pas sauvegardées en base
                commande.setNomClient(fullNameField.getText());
                commande.setEmail(emailField.getText());
                commande.setTelephone(phoneField.getText());
                
                // Constructeur l'adresse complète
                String adresseComplete = addressField.getText();
                if (selectedAddress != null && !selectedAddress.isEmpty()) {
                    adresseComplete += " (Coordonnées GPS: " + selectedLat + ", " + selectedLng + ")";
                }
                commande.setAdresse(adresseComplete);
                
                commande.setNotes(notesField.getText());
                
                // Sauvegarder la commande avec une structure simplifiée (sans les données client dans la table)
                serviceCommande.insert(commande);
                
                // Essayer d'envoyer un email, mais ne pas bloquer le processus en cas d'échec
                boolean emailEnvoye = false;
                try {
                    emailEnvoye = EmailService.envoyerEmailConfirmation(commande);
                } catch (Exception emailEx) {
                    System.err.println("Exception lors de l'envoi de l'email (ignorée): " + emailEx.getMessage());
                    emailEx.printStackTrace();
                }
                
                // Essayer d'envoyer un SMS, mais ne pas bloquer le processus en cas d'échec
                boolean smsEnvoye = false;
                try {
                    // Vérifier d'abord si le service SMS est configuré
                    if (SMSService.isConfigured()) {
                        smsEnvoye = SMSService.envoyerSMSConfirmation(commande);
                    } else {
                        System.out.println("Service SMS non configuré - SMS non envoyé");
                    }
                } catch (com.twilio.exception.ApiException apiEx) {
                    if ("Authenticate".equals(apiEx.getMessage()) || apiEx.getStatusCode() == 401) {
                        System.err.println("ERREUR D'AUTHENTIFICATION TWILIO: Impossible d'envoyer le SMS");
                        System.err.println("Veuillez vérifier vos identifiants Twilio dans src/main/resources/sms.properties");
                        
                        // Afficher une erreur spécifique pour l'authentification
                        showTwilioAuthError();
                    } else {
                        System.err.println("Erreur API Twilio: " + apiEx.getMessage());
                    }
                } catch (Exception smsEx) {
                    System.err.println("Exception lors de l'envoi du SMS (ignorée): " + smsEx.getMessage());
                    smsEx.printStackTrace();
                }
                
                // Afficher un message de succès
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Commande enregistrée");
                alert.setHeaderText(null);
                String message = "Votre commande a été enregistrée avec succès!";
                
                if (emailEnvoye) {
                    message += " Nous vous avons envoyé un email de confirmation.";
                }
                
                if (smsEnvoye) {
                    message += " Un SMS de confirmation a également été envoyé à votre numéro de téléphone.";
                }
                
                message += "\n\nVous allez être redirigé vers la page de paiement.";
                
                alert.setContentText(message);
                alert.showAndWait();
                
                // Rediriger vers la page de paiement
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/PaiementView.fxml"));
                Parent root = loader.load();
                PaiementController paiementController = loader.getController();
                paiementController.setCommande(commande);

                // Utiliser les dimensions standard pour la scène
                Scene scene = FullScreenUtil.createStandardScene(root);

                // Ajouter explicitement la feuille de style
                scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());

                Stage stage = (Stage) fullNameField.getScene().getWindow();
                stage.setScene(scene);
                FullScreenUtil.setFullScreen(stage);
                stage.show();

                // Initialiser le formulaire de paiement Stripe
                paiementController.initializeStripeForm();
                
            } catch (Exception e) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Erreur");
                alert.setHeaderText(null);
                alert.setContentText("Une erreur est survenue lors de la création de la commande: " + e.getMessage());
                alert.showAndWait();
            }
        }
    }
    
    @FXML
    private void handleCancel() {
        try {
            // Récupérer la scène actuelle
            Stage currentStage = (Stage) fullNameField.getScene().getWindow();
            
            // Retourner à la vue du panier
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/PanierView.fxml"));
            Parent root = loader.load();
            
            // Créer une scène avec les dimensions standard
            Scene scene = FullScreenUtil.createStandardScene(root);
            
            // Ajouter explicitement la feuille de style
            String cssPath = getClass().getResource("/styles.css").toExternalForm();
            scene.getStylesheets().add(cssPath);
            
            currentStage.setScene(scene);
            FullScreenUtil.setFullScreen(currentStage);
            currentStage.show();
        } catch (IOException e) {
            showAlert("Erreur", "Impossible de retourner à la vue du panier: " + e.getMessage());
        }
    }
    
    private boolean validateForm() {
        boolean isValid = true;
        
        // Validation du nom complet
        if (fullNameField.getText().trim().isEmpty()) {
            fullNameError.setText("Veuillez entrer votre nom complet");
            fullNameError.setVisible(true);
            fullNameError.setManaged(true);
            isValid = false;
        } else if (fullNameField.getText().trim().length() < 3) {
            fullNameError.setText("Le nom doit contenir au moins 3 caractères");
            fullNameError.setVisible(true);
            fullNameError.setManaged(true);
            isValid = false;
        }
        
        // Validation de l'email
        if (emailField.getText().trim().isEmpty()) {
            emailError.setText("Veuillez entrer votre adresse email");
            emailError.setVisible(true);
            emailError.setManaged(true);
            isValid = false;
        } else if (!EMAIL_PATTERN.matcher(emailField.getText().trim()).matches()) {
            emailError.setText("Veuillez entrer une adresse email valide");
            emailError.setVisible(true);
            emailError.setManaged(true);
            isValid = false;
        }
        
        // Validation du téléphone
        if (phoneField.getText().trim().isEmpty()) {
            phoneError.setText("Veuillez entrer votre numéro de téléphone");
            phoneError.setVisible(true);
            phoneError.setManaged(true);
            isValid = false;
        } else if (!PHONE_PATTERN.matcher(phoneField.getText().trim()).matches()) {
            phoneError.setText("Veuillez entrer un numéro de téléphone valide (8 chiffres)");
            phoneError.setVisible(true);
            phoneError.setManaged(true);
            isValid = false;
        }
        
        // Validation de la localisation
        if (locationField.getText().trim().isEmpty()) {
            locationError.setText("Veuillez sélectionner votre localisation sur la carte");
            locationError.setVisible(true);
            locationError.setManaged(true);
            isValid = false;
        }
        
        // Validation de l'adresse
        if (addressField.getText().trim().isEmpty()) {
            addressError.setText("Veuillez entrer votre adresse complète");
            addressError.setVisible(true);
            addressError.setManaged(true);
            isValid = false;
        }
        
        // Validation du code postal
        if (postalCodeField.getText().trim().isEmpty()) {
            postalCodeError.setText("Veuillez entrer votre code postal");
            postalCodeError.setVisible(true);
            postalCodeError.setManaged(true);
            isValid = false;
        } else {
            try {
                int codePostal = Integer.parseInt(postalCodeField.getText().trim());
                if (codePostal < 1000 || codePostal > 9999) {
                    postalCodeError.setText("Le code postal doit être entre 1000 et 9999");
                    postalCodeError.setVisible(true);
                    postalCodeError.setManaged(true);
                    isValid = false;
                }
            } catch (NumberFormatException e) {
                postalCodeError.setText("Le code postal doit être un nombre");
                postalCodeError.setVisible(true);
                postalCodeError.setManaged(true);
                isValid = false;
            }
        }
        
        return isValid;
    }
    
    private void resetErrorMessages() {
        fullNameError.setVisible(false);
        fullNameError.setManaged(false);
        emailError.setVisible(false);
        emailError.setManaged(false);
        phoneError.setVisible(false);
        phoneError.setManaged(false);
        locationError.setVisible(false);
        locationError.setManaged(false);
        addressError.setVisible(false);
        addressError.setManaged(false);
        postalCodeError.setVisible(false);
        postalCodeError.setManaged(false);
    }
    
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }

    /**
     * Affiche une alerte d'erreur spécifique aux problèmes d'authentification Twilio
     */
    private void showTwilioAuthError() {
        try {
            // Récupérer la fenêtre actuelle
            Stage currentStage = (Stage) fullNameField.getScene().getWindow();
            
            // Utiliser l'alerte personnalisée si disponible
            if (tn.fermista.utils.CustomAlert.class != null) {
                tn.fermista.utils.CustomAlert.showError(
                    currentStage,
                    "Erreur de configuration SMS",
                    "Impossible d'envoyer le SMS de confirmation : erreur d'authentification Twilio.\n\n" +
                    "Les identifiants Twilio dans le fichier sms.properties semblent être invalides ou expirés.\n" +
                    "Veuillez contacter l'administrateur pour mettre à jour la configuration."
                );
            } else {
                // Fallback sur l'alerte standard
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Erreur de configuration SMS");
                alert.setHeaderText("Impossible d'envoyer le SMS de confirmation");
                alert.setContentText(
                    "Erreur d'authentification Twilio. Les identifiants dans le fichier sms.properties semblent être invalides ou expirés.\n" +
                    "Veuillez contacter l'administrateur pour mettre à jour la configuration."
                );
                alert.initOwner(currentStage);
                alert.showAndWait();
            }
        } catch (Exception e) {
            // En cas d'erreur, afficher une alerte standard
            System.err.println("Erreur lors de l'affichage de l'alerte d'erreur Twilio: " + e.getMessage());
        }
    }
} 