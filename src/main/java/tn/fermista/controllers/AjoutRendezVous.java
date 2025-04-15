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
    private Button submitButton;

    private final ServiceAgriculteur serviceAgriculteur = new ServiceAgriculteur();
    private final ServiceVeterinaire serviceVeterinaire = new ServiceVeterinaire();
    private final ServiceRendezVous serviceRendezVous = new ServiceRendezVous();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Charger les agriculteurs et vétérinaires
        ObservableList<Agriculteur> agriculteurs = FXCollections.observableArrayList(serviceAgriculteur.rechercher());
        ObservableList<Veterinaire> veterinaires = FXCollections.observableArrayList(serviceVeterinaire.rechercher());
        agriculteurCombo.setItems(agriculteurs);
        veterinaireCombo.setItems(veterinaires);
        // Affichage personnalisé dans la ComboBox
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
        submitButton.setOnAction(this::handleSubmit);
    }

    private void handleSubmit(ActionEvent event) {
        try {
            // Validation des champs
            if (agriculteurCombo.getValue() == null || veterinaireCombo.getValue() == null ||
                datePicker.getValue() == null || heureField.getText().isEmpty() ||
                sexField.getText().isEmpty() || causeField.getText().isEmpty()) {
                showAlert("Erreur", "Veuillez remplir tous les champs", Alert.AlertType.ERROR);
                return;
            }
            LocalDate localDate = datePicker.getValue();
            LocalTime localTime = LocalTime.parse(heureField.getText());
            Date sqlDate = Date.valueOf(localDate);
            Time sqlTime = Time.valueOf(localTime);
            String sex = sexField.getText();
            String cause = causeField.getText();
            Agriculteur agriculteur = agriculteurCombo.getValue();
            Veterinaire veterinaire = veterinaireCombo.getValue();
            RendezVous rendezVous = new RendezVous();
            rendezVous.setDate(sqlDate);
            rendezVous.setHeure(sqlTime);
            rendezVous.setSex(sex);
            rendezVous.setCause(cause);
            rendezVous.setAgriculteur(agriculteur);
            rendezVous.setVeterinaire(veterinaire);
            rendezVous.setStatus("en attente");
            // Validation métier (date future, pas week-end)
            rendezVous.validate();
            // Insertion
            serviceRendezVous.insert(rendezVous);
            // Fermer la fenêtre
            Stage stage = (Stage) submitButton.getScene().getWindow();
            stage.close();
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Erreur", "Erreur lors de l'ajout du rendez-vous", Alert.AlertType.ERROR);
        } catch (Exception e) {
            showAlert("Erreur", e.getMessage(), Alert.AlertType.ERROR);
        }
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
