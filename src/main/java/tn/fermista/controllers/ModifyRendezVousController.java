package tn.fermista.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.control.*;
import javafx.stage.Stage;
import tn.fermista.models.Agriculteur;
import tn.fermista.models.RendezVous;
import tn.fermista.models.Veterinaire;
import tn.fermista.services.ServiceAgriculteur;
import tn.fermista.services.ServiceRendezVous;
import tn.fermista.services.ServiceVeterinaire;

import java.net.URL;
import java.sql.Date;
import java.sql.SQLException;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.ResourceBundle;

public class ModifyRendezVousController implements Initializable {
    @FXML
    private ComboBox<Agriculteur> agriculteurCombo;
    @FXML
    private ComboBox<Veterinaire> veterinaireCombo;
    @FXML
    private DatePicker datePicker;
    @FXML
    private TextField heureField;
    @FXML
    private TextField sexField;
    @FXML
    private TextField causeField;
    @FXML
    private TextField statusField;
    @FXML
    private Button saveButton;
    @FXML
    private Button cancelButton;

    private RendezVous rendezVous;
    private Stage stage;
    private final ServiceRendezVous serviceRendezVous = new ServiceRendezVous();
    private final ServiceAgriculteur serviceAgriculteur = new ServiceAgriculteur();
    private final ServiceVeterinaire serviceVeterinaire = new ServiceVeterinaire();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Charger les listes d'agriculteurs et de vétérinaires
        ObservableList<Agriculteur> agriculteurs = FXCollections.observableArrayList(serviceAgriculteur.rechercher());
        ObservableList<Veterinaire> veterinaires = FXCollections.observableArrayList(serviceVeterinaire.rechercher());
        
        agriculteurCombo.setItems(agriculteurs);
        veterinaireCombo.setItems(veterinaires);

        // Configuration de l'affichage des noms dans les ComboBox
        agriculteurCombo.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(Agriculteur item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getFirstName() + " " + item.getLastName());
            }
        });
        agriculteurCombo.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(Agriculteur item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getFirstName() + " " + item.getLastName());
            }
        });

        veterinaireCombo.setCellFactory(lv -> new ListCell<>() {
            @Override
            protected void updateItem(Veterinaire item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getFirstName() + " " + item.getLastName());
            }
        });
        veterinaireCombo.setButtonCell(new ListCell<>() {
            @Override
            protected void updateItem(Veterinaire item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty || item == null ? null : item.getFirstName() + " " + item.getLastName());
            }
        });
    }

    public void setRendezVous(RendezVous rendezVous) {
        this.rendezVous = rendezVous;
        // Remplir les champs avec les données du rendez-vous
        agriculteurCombo.setValue(rendezVous.getAgriculteur());
        veterinaireCombo.setValue(rendezVous.getVeterinaire());
        datePicker.setValue(rendezVous.getDate().toLocalDate());
        heureField.setText(rendezVous.getHeure().toString());
        sexField.setText(rendezVous.getSex());
        causeField.setText(rendezVous.getCause());
        statusField.setText(rendezVous.getStatus());
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    @FXML
    private void handleSave() {
        try {
            // Validation des champs vides
            if (agriculteurCombo.getValue() == null || veterinaireCombo.getValue() == null ||
                datePicker.getValue() == null || heureField.getText().isEmpty() ||
                sexField.getText().isEmpty() || causeField.getText().isEmpty() ||
                statusField.getText().isEmpty()) {
                showAlert("Erreur", "Veuillez remplir tous les champs", Alert.AlertType.ERROR);
                return;
            }

            // Validation de la date (doit être après aujourd'hui)
            LocalDate selectedDate = datePicker.getValue();
            if (!selectedDate.isAfter(LocalDate.now())) {
                showAlert("Erreur", "La date doit être postérieure à aujourd'hui", Alert.AlertType.ERROR);
                return;
            }

            // Validation de l'heure (entre 9:00 et 18:00)
            LocalTime time;
            try {
                time = LocalTime.parse(heureField.getText());
                LocalTime startTime = LocalTime.of(9, 0);
                LocalTime endTime = LocalTime.of(18, 0);
                if (time.isBefore(startTime) || time.isAfter(endTime)) {
                    showAlert("Erreur", "L'heure doit être comprise entre 09:00 et 18:00", Alert.AlertType.ERROR);
                    return;
                }
            } catch (Exception e) {
                showAlert("Erreur", "Format d'heure invalide. Utilisez le format HH:mm", Alert.AlertType.ERROR);
                return;
            }

            // Validation du sexe (male ou female)
            String sex = sexField.getText().toLowerCase();
            if (!sex.equals("male") && !sex.equals("female")) {
                showAlert("Erreur", "Le sexe doit être 'male' ou 'female'", Alert.AlertType.ERROR);
                return;
            }

            // Validation de la cause (minimum 10 caractères)
            if (causeField.getText().length() < 10) {
                showAlert("Erreur", "La cause doit contenir au moins 10 caractères", Alert.AlertType.ERROR);
                return;
            }

            // Mise à jour des données du rendez-vous
            rendezVous.setAgriculteur(agriculteurCombo.getValue());
            rendezVous.setVeterinaire(veterinaireCombo.getValue());
            rendezVous.setDate(Date.valueOf(datePicker.getValue()));
            rendezVous.setHeure(Time.valueOf(time));
            rendezVous.setSex(sex);
            rendezVous.setCause(causeField.getText());
            rendezVous.setStatus(statusField.getText());

            // Mise à jour dans la base de données
            serviceRendezVous.update(rendezVous);

            // Message de succès
            showAlert("Succès", "Rendez-vous modifié avec succès", Alert.AlertType.INFORMATION);

            // Fermer la fenêtre
            closeWindow();
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Erreur", "Erreur lors de la modification du rendez-vous", Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void handleCancel() {
        closeWindow();
    }

    private void closeWindow() {
        if (stage != null) {
            stage.close();
        } else {
            // Si stage est null, essayer de fermer la fenêtre parente
            Node node = saveButton.getScene().getRoot();
            if (node != null) {
                Stage currentStage = (Stage) node.getScene().getWindow();
                if (currentStage != null) {
                    currentStage.close();
                }
            }
        }
    }

    private void showAlert(String title, String content, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        
        // Ajout de style CSS
        DialogPane dialogPane = alert.getDialogPane();
        dialogPane.getStylesheets().add(
            getClass().getResource("/styles/alert.css").toExternalForm()
        );
        dialogPane.getStyleClass().add("custom-alert");
        
        // Personnalisation des icônes selon le type d'alerte
        switch (type) {
            case ERROR:
                dialogPane.getStyleClass().add("error-alert");
                break;
            case INFORMATION:
                dialogPane.getStyleClass().add("info-alert");
                break;
            case WARNING:
                dialogPane.getStyleClass().add("warning-alert");
                break;
            case CONFIRMATION:
                dialogPane.getStyleClass().add("confirm-alert");
                break;
        }
        
        alert.showAndWait();
    }
} 