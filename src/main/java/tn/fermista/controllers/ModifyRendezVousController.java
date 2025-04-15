package tn.fermista.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
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
            // Validation des champs
            if (agriculteurCombo.getValue() == null || veterinaireCombo.getValue() == null ||
                datePicker.getValue() == null || heureField.getText().isEmpty() ||
                sexField.getText().isEmpty() || causeField.getText().isEmpty() ||
                statusField.getText().isEmpty()) {
                showAlert("Erreur", "Veuillez remplir tous les champs", Alert.AlertType.ERROR);
                return;
            }

            // Mise à jour des données du rendez-vous
            rendezVous.setAgriculteur(agriculteurCombo.getValue());
            rendezVous.setVeterinaire(veterinaireCombo.getValue());
            rendezVous.setDate(Date.valueOf(datePicker.getValue()));
            rendezVous.setHeure(Time.valueOf(LocalTime.parse(heureField.getText())));
            rendezVous.setSex(sexField.getText());
            rendezVous.setCause(causeField.getText());
            rendezVous.setStatus(statusField.getText());

            // Validation métier
            rendezVous.validate();

            // Mise à jour dans la base de données
            serviceRendezVous.update(rendezVous);

            // Fermer la fenêtre
            stage.close();
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Erreur", "Erreur lors de la modification du rendez-vous", Alert.AlertType.ERROR);
        } catch (IllegalArgumentException e) {
            showAlert("Erreur", e.getMessage(), Alert.AlertType.ERROR);
        } catch (Exception e) {
            showAlert("Erreur", "Format d'heure invalide. Utilisez le format HH:mm:ss", Alert.AlertType.ERROR);
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
} 