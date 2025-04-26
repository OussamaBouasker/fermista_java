package tn.fermista.controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.stage.Stage;
import tn.fermista.models.Reservation;
import tn.fermista.models.Workshop;
import tn.fermista.models.User;
import tn.fermista.services.ServiceReservation;
import tn.fermista.services.ServiceWorkshop;
import tn.fermista.services.ServiceClient;
import tn.fermista.services.ServiceAgriculteur;

import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ResourceBundle;

public class AddReservationController implements Initializable {
    @FXML private DatePicker datePicker;
    @FXML private ComboBox<String> statusComboBox;
    @FXML private TextField prixField;
    @FXML private ComboBox<Workshop> workshopComboBox;
    @FXML private ComboBox<User> userComboBox;
    @FXML private TextField emailField;
    @FXML private TextField phoneField;
    @FXML private Button saveButton;
    @FXML private Button cancelButton;

    private ServiceReservation serviceReservation;
    private ServiceWorkshop serviceWorkshop;
    private ServiceClient serviceClient;
    private ServiceAgriculteur serviceAgriculteur;
    private ShowReservationsController parentController;
    private Reservation reservationToEdit;
    private boolean isEditMode = false;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        serviceReservation = new ServiceReservation();
        serviceWorkshop = new ServiceWorkshop();
        serviceClient = new ServiceClient();
        serviceAgriculteur = new ServiceAgriculteur();

        // Initialize status options
        statusComboBox.getItems().addAll("En attente", "Confirmé", "Annulé");

        try {
            // Load workshops
            var workshops = serviceWorkshop.showAll();
            workshopComboBox.getItems().addAll(workshops);
            
            // Load users (both clients and agriculteurs)
            var clients = serviceClient.showAll();
            var agriculteurs = serviceAgriculteur.showAll();
            userComboBox.getItems().addAll(clients);
            userComboBox.getItems().addAll(agriculteurs);
            
            // Debug print
            System.out.println("Loaded " + workshops.size() + " workshops");
            System.out.println("Loaded " + (clients.size() + agriculteurs.size()) + " users");
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors du chargement des données: " + e.getMessage());
            e.printStackTrace();
        }

        // Set display for workshop selection
        workshopComboBox.setConverter(new javafx.util.StringConverter<Workshop>() {
            @Override
            public String toString(Workshop workshop) {
                return workshop == null ? "" : workshop.getTitre();
            }

            @Override
            public Workshop fromString(String string) {
                return workshopComboBox.getItems().stream()
                    .filter(workshop -> workshop.getTitre().equals(string))
                    .findFirst().orElse(null);
            }
        });

        // Set display for user selection
        userComboBox.setConverter(new javafx.util.StringConverter<User>() {
            @Override
            public String toString(User user) {
                return user == null ? "" : user.getFirstName() + " " + user.getLastName();
            }

            @Override
            public User fromString(String string) {
                return userComboBox.getItems().stream()
                    .filter(user -> (user.getFirstName() + " " + user.getLastName()).equals(string))
                    .findFirst().orElse(null);
            }
        });

        // Add change listeners for debugging
        workshopComboBox.valueProperty().addListener((obs, oldVal, newVal) -> {
            System.out.println("Workshop selected: " + (newVal != null ? newVal.getTitre() : "null"));
        });

        userComboBox.valueProperty().addListener((obs, oldVal, newVal) -> {
            System.out.println("User selected: " + (newVal != null ? newVal.getFirstName() + " " + newVal.getLastName() : "null"));
        });
    }

    public void setParentController(ShowReservationsController controller) {
        this.parentController = controller;
    }

    public void setReservationForEdit(Reservation reservation) {
        this.reservationToEdit = reservation;
        this.isEditMode = true;
        
        try {
            // Populate fields with reservation data
            LocalDateTime dateTime = reservation.getReservationDate();
            if (dateTime != null) {
                datePicker.setValue(dateTime.toLocalDate());
            }
            statusComboBox.setValue(reservation.getStatus());
            prixField.setText(String.valueOf(reservation.getPrix()));
            
            // Set workshop
            Workshop workshop = reservation.getWorkshop();
            if (workshop != null) {
                // Refresh workshop from database to ensure all properties are loaded
                workshop = serviceWorkshop.getById(workshop.getId());
                workshopComboBox.setValue(workshop);
            }
            
            // Set user
            User user = reservation.getUser();
            if (user != null) {
                // Try to find the user in both client and agriculteur services
                try {
                    user = serviceClient.getById(user.getId());
                } catch (Exception e) {
                    try {
                        user = serviceAgriculteur.getById(user.getId());
                    } catch (Exception ex) {
                        System.out.println("User not found in either service");
                    }
                }
                userComboBox.setValue(user);
            }
            
            emailField.setText(reservation.getEmail());
            phoneField.setText(String.valueOf(reservation.getNumTel()));
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors du chargement des données: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleSave() {
        try {
            if (!validateInputs()) {
                return;
            }

            Reservation reservation = isEditMode ? reservationToEdit : new Reservation();
            
            // Convert LocalDate to LocalDateTime
            LocalDateTime dateTime = LocalDateTime.of(datePicker.getValue(), LocalTime.now());
            reservation.setReservationDate(dateTime);
            
            reservation.setStatus(statusComboBox.getValue());
            reservation.setPrix(prixField.getText());

            // Get selected workshop and user
            Workshop selectedWorkshop = workshopComboBox.getValue();
            User selectedUser = userComboBox.getValue();

            // Debug print
            System.out.println("Saving reservation with workshop: " + 
                (selectedWorkshop != null ? selectedWorkshop.getTitre() + " (ID: " + selectedWorkshop.getId() + ")" : "null"));
            System.out.println("Saving reservation with user: " + 
                (selectedUser != null ? selectedUser.getFirstName() + " " + selectedUser.getLastName() + " (ID: " + selectedUser.getId() + ")" : "null"));

            reservation.setWorkshop(selectedWorkshop);
            reservation.setUser(selectedUser);
            reservation.setEmail(emailField.getText());
            reservation.setNumTel(Integer.parseInt(phoneField.getText()));

            if (isEditMode) {
                serviceReservation.update(reservation);
            } else {
                serviceReservation.insert(reservation);
            }

            parentController.refreshTable();
            closeWindow();
            showAlert(Alert.AlertType.INFORMATION, "Succès", 
                     isEditMode ? "Réservation modifiée avec succès!" : "Réservation ajoutée avec succès!");
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", 
                     "Erreur lors de l'enregistrement: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleCancel() {
        closeWindow();
    }

    private boolean validateInputs() {
        StringBuilder errors = new StringBuilder();

        if (datePicker.getValue() == null) {
            errors.append("- La date est requise\n");
        }
        if (statusComboBox.getValue() == null) {
            errors.append("- Le status est requis\n");
        }
        if (prixField.getText().isEmpty()) {
            errors.append("- Le prix est requis\n");
        } else {
            try {
                Double.parseDouble(prixField.getText());
            } catch (NumberFormatException e) {
                errors.append("- Le prix doit être un nombre valide\n");
            }
        }
        if (workshopComboBox.getValue() == null) {
            errors.append("- Le workshop est requis\n");
        }
        if (userComboBox.getValue() == null) {
            errors.append("- L'utilisateur est requis\n");
        }
        if (emailField.getText().isEmpty()) {
            errors.append("- L'email est requis\n");
        }
        if (phoneField.getText().isEmpty()) {
            errors.append("- Le numéro de téléphone est requis\n");
        } else {
            try {
                Integer.parseInt(phoneField.getText());
            } catch (NumberFormatException e) {
                errors.append("- Le numéro de téléphone doit être un nombre valide\n");
            }
        }

        if (errors.length() > 0) {
            showAlert(Alert.AlertType.ERROR, "Erreur de validation", errors.toString());
            return false;
        }
        return true;
    }

    private void closeWindow() {
        Stage stage = (Stage) saveButton.getScene().getWindow();
        stage.close();
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
