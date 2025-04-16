package tn.fermista.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
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

public class AjoutRendezVous implements Initializable {
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
    private Button submitButton;
    @FXML
    private Button cancelButton;

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

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    @FXML
    private void handleSubmit() {
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

            // Création du nouveau rendez-vous
            RendezVous rendezVous = new RendezVous();
            rendezVous.setAgriculteur(agriculteurCombo.getValue());
            rendezVous.setVeterinaire(veterinaireCombo.getValue());
            rendezVous.setDate(Date.valueOf(datePicker.getValue()));
            rendezVous.setHeure(Time.valueOf(time));
            rendezVous.setSex(sex);
            rendezVous.setCause(causeField.getText());
            rendezVous.setStatus(statusField.getText());

            // Ajout dans la base de données
            serviceRendezVous.insert(rendezVous);

            // Message de succès
            showAlert("Succès", "Rendez-vous ajouté avec succès", Alert.AlertType.INFORMATION);

            // Fermer la fenêtre
            stage.close();
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Erreur", "Erreur lors de l'ajout du rendez-vous", Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void handleCancel() {
        stage.close();
    }

    private void showAlert(String title, String content, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    public void ControlMedicalShow(ActionEvent actionEvent) {
    }

    public void DashbordTemplate(ActionEvent actionEvent) {
    }
}
