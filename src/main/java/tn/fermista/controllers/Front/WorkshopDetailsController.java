package tn.fermista.controllers.Front;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import tn.fermista.models.Workshop;
import tn.fermista.models.Reservation;
import tn.fermista.models.User;
import tn.fermista.services.ServiceReservation;
import tn.fermista.utils.EmailSender;
import tn.fermista.utils.EmailSender2;
import tn.fermista.utils.UserSession;
import java.time.LocalDateTime;
import java.sql.SQLException;
import java.util.logging.Level;
import java.util.logging.Logger;

public class WorkshopDetailsController {
    @FXML private ImageView workshopImage;
    @FXML private Label titleLabel;
    @FXML private Label typeLabel;
    @FXML private Label priceLabel;
    @FXML private Label formateurLabel;
    @FXML private Label descriptionLabel;
    @FXML private Label placesLabel;
    
    // Reservation form fields
    @FXML private TextField emailField;
    @FXML private TextField telField;
    @FXML private TextField cardNumberField;
    @FXML private TextArea commentField;
    @FXML private Button reserveButton;
    @FXML private ProgressIndicator reservationProgress;
    @FXML private VBox reservationForm;
    @FXML private Label loginMessageLabel;

    private Workshop currentWorkshop;
    private ServiceReservation serviceReservation;
    private User currentUser;
    private static final Logger LOGGER = Logger.getLogger(WorkshopDetailsController.class.getName());

    public WorkshopDetailsController() {
        serviceReservation = new ServiceReservation();
    }

    public void initData(Workshop workshop) {
        this.currentWorkshop = workshop;
        
        // Get current user from UserSession
        this.currentUser = UserSession.getCurrentUser();
        System.out.println("Current user from UserSession: " + (currentUser != null ? currentUser.getEmail() : "null"));
        
        // Set workshop details
        titleLabel.setText(workshop.getTitre());
        typeLabel.setText(workshop.getType());
        priceLabel.setText(workshop.getPrix() + " DT");
        descriptionLabel.setText(workshop.getDescription());

        // Update places info
        updatePlacesInfo();

        // Handle formateur info for live workshops
        if ("Atelier Live".equals(workshop.getType()) && workshop.getUser() != null) {
            formateurLabel.setText("Formateur: " + 
                workshop.getUser().getFirstName() + " " + 
                workshop.getUser().getLastName());
            formateurLabel.setVisible(true);
        } else {
            formateurLabel.setVisible(false);
        }

        // Load workshop image
        if (workshop.getImage() != null && !workshop.getImage().isEmpty()) {
            try {
                Image image = new Image(getClass().getResourceAsStream(workshop.getImage()));
                workshopImage.setImage(image);
            } catch (Exception e) {
                // Load default image if workshop image fails
                try {
                    workshopImage.setImage(new Image(getClass().getResourceAsStream("/images/default-workshop.png")));
                } catch (Exception ex) {
                    ex.printStackTrace();
                }
            }
        }

        // Setup reservation form based on user authentication
        setupReservationForm();
    }

    private void setupReservationForm() {
        if (currentUser == null) {
            // User is not logged in
            reservationForm.setVisible(false);
            loginMessageLabel.setVisible(true);
            loginMessageLabel.setText("Veuillez vous connecter pour réserver ce workshop");
            reserveButton.setDisable(true);
            System.out.println("User not logged in - Disabling reservation form");
        } else {
            // User is logged in
            reservationForm.setVisible(true);
            loginMessageLabel.setVisible(false);
            reserveButton.setDisable(false);
            
            // Pre-fill email from user account
            emailField.setText(currentUser.getEmail());
            emailField.setEditable(false); // Email is taken from account
            System.out.println("User logged in - Enabling reservation form for: " + currentUser.getEmail());
        }
    }

    private void updatePlacesInfo() {
        if (currentWorkshop != null) {
            int placesRestantes = currentWorkshop.getNbrPlacesRestantes();
            placesLabel.setText("Places disponibles: " + placesRestantes);
            
            // Disable reservation if no places left
            if (placesRestantes <= 0) {
                placesLabel.setStyle("-fx-text-fill: red;");
                placesLabel.setText("Complet");
                reserveButton.setDisable(true);
            }
        }
    }

    @FXML
    private void handleReservation() {
        // Refresh current user status before proceeding
        currentUser = UserSession.getCurrentUser();
        
        if (currentUser == null) {
            showAlert(Alert.AlertType.WARNING, "Non connecté", 
                     "Veuillez vous connecter pour effectuer une réservation.");
            return;
        }

        if (!validateForm()) {
            return;
        }

        // Disable form and show progress
        setFormDisabled(true);
        reservationProgress.setVisible(true);

        try {
            Reservation reservation = new Reservation();
            reservation.setWorkshop(currentWorkshop);
            reservation.setUser(currentUser);
            reservation.setEmail(currentUser.getEmail()); // Use current user's email
            reservation.setNumTel(Integer.parseInt(telField.getText()));
            reservation.setNumCarteBancaire(cardNumberField.getText());
            reservation.setCommentaire(commentField.getText());
            reservation.setStatus("en attente");
            reservation.setReservationDate(LocalDateTime.now());
            reservation.setPrix(currentWorkshop.getPrix());
            reservation.setConfirmation(false);

            serviceReservation.insert(reservation);

            // Update places remaining
            currentWorkshop.setNbrPlacesRestantes(currentWorkshop.getNbrPlacesRestantes() - 1);
            updatePlacesInfo();

            // Send confirmation email
            sendConfirmationEmail(reservation);

            // Show success message
            showAlert(Alert.AlertType.INFORMATION, "Succès", 
                     "Votre réservation a été enregistrée avec succès!\n" +
                     "Un email de confirmation vous a été envoyé.\n" +
                     "Votre réservation est en attente de confirmation.");

            // Clear form except email
            clearForm();
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", 
                     "Une erreur est survenue lors de la réservation: " + e.getMessage());
        } finally {
            setFormDisabled(false);
            reservationProgress.setVisible(false);
        }
    }

    private void sendConfirmationEmail(Reservation reservation) {
        try {
            EmailSender2.sendNewReservationEmail(reservation);
        } catch (Exception e) {
            LOGGER.log(Level.SEVERE, "Erreur lors de l'envoi de l'email de confirmation", e);
            System.err.println("Erreur lors de l'envoi de l'email: " + e.getMessage());
        }
    }

    private boolean validateForm() {
        StringBuilder errors = new StringBuilder();

        if (telField.getText().isEmpty() || !telField.getText().matches("\\d{8}")) {
            errors.append("Numéro de téléphone invalide (8 chiffres requis)\n");
        }
        if (cardNumberField.getText().isEmpty() || !cardNumberField.getText().matches("\\d{16}")) {
            errors.append("Numéro de carte bancaire invalide (16 chiffres requis)\n");
        }

        if (errors.length() > 0) {
            showAlert(Alert.AlertType.ERROR, "Erreur de validation", errors.toString());
            return false;
        }

        return true;
    }

    private void setFormDisabled(boolean disabled) {
        telField.setDisable(disabled);
        cardNumberField.setDisable(disabled);
        commentField.setDisable(disabled);
        reserveButton.setDisable(disabled);
    }

    private void clearForm() {
        telField.clear();
        cardNumberField.clear();
        commentField.clear();
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Platform.runLater(() -> {
            Alert alert = new Alert(type);
            alert.setTitle(title);
            alert.setContentText(content);
            alert.showAndWait();
        });
    }

    @FXML
    private void goBack() {
        Stage stage = (Stage) titleLabel.getScene().getWindow();
        stage.close();
    }
}