package tn.fermista.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import tn.fermista.models.Consultation;
import tn.fermista.models.RapportMedical;
import tn.fermista.models.Vache;
import tn.fermista.services.ServiceConsultation;
import tn.fermista.services.ServiceRapportMedical;
import tn.fermista.services.ServiceVache;

import java.io.IOException;
import java.sql.Date;
import java.sql.SQLException;
import java.sql.Time;
import java.time.LocalTime;
import java.util.List;

public class AjoutConsultation {

    @FXML
    private ButtonBar btn_workbench;

    @FXML
    private Button btn_workbench11;

    @FXML
    private Button btn_workbench111;

    @FXML
    private Button btn_workbench112;

    @FXML
    private Button btn_workbench1121;

    @FXML
    private Button btn_workbench11211;

    @FXML
    private DatePicker datePicker;

    @FXML
    private TextField heureField;

    @FXML
    private Pane inner_pane;

    @FXML
    private TextField lieuField;

    @FXML
    private Pane most_inner_pane;

    @FXML
    private TextField nomField;

    @FXML
    private HBox root;

    @FXML
    private AnchorPane side_ankerpane;

    @FXML
    private TextField txt_search;

    @FXML private ComboBox<RapportMedical> rapportMedicalComboBox;
    @FXML private ComboBox<Vache> vacheComboBox;
    @FXML private Button submit;

    private final ServiceConsultation serviceConsultation = new ServiceConsultation();
    private final ServiceRapportMedical serviceRapportMedical = new ServiceRapportMedical();
    private final ServiceVache serviceVache = new ServiceVache();

    @FXML
    public void initialize() {
        try {
            // Charger les rapports médicaux
            List<RapportMedical> rapports = serviceRapportMedical.showAll();
            rapportMedicalComboBox.getItems().addAll(rapports);
            rapportMedicalComboBox.setCellFactory(param -> new ListCell<>() {
                @Override
                protected void updateItem(RapportMedical item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                    } else {
                        setText("Rapport #" + item.getId());
                    }
                }
            });

            // Charger les vaches
            List<Vache> vaches = serviceVache.showAll();
            vacheComboBox.getItems().addAll(vaches);
            vacheComboBox.setCellFactory(param -> new ListCell<>() {
                @Override
                protected void updateItem(Vache item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty || item == null) {
                        setText(null);
                    } else {
                        setText("Vache #" + item.getId());
                    }
                }
            });

            // Configurer le bouton submit
            submit.setOnAction(this::handleSubmit);
        } catch (SQLException e) {
            e.printStackTrace();
            // TODO: Afficher un message d'erreur
        }
    }

    @FXML
    private void handleSubmit(ActionEvent event) {
        try {
            // Vérifier que tous les champs sont remplis
            if (nomField.getText().isEmpty() || datePicker.getValue() == null ||
                heureField.getText().isEmpty() || lieuField.getText().isEmpty() ||
                rapportMedicalComboBox.getValue() == null || vacheComboBox.getValue() == null) {
                showAlert("Erreur", "Veuillez remplir tous les champs", Alert.AlertType.ERROR);
                return;
            }

            // Créer la nouvelle consultation
            Consultation consultation = new Consultation();
            consultation.setNom(nomField.getText());
            consultation.setDate(Date.valueOf(datePicker.getValue()));
            consultation.setHeure(Time.valueOf(LocalTime.parse(heureField.getText())));
            consultation.setLieu(lieuField.getText());
            consultation.setRapportMedical(rapportMedicalComboBox.getValue());
            consultation.setVache(vacheComboBox.getValue());

            // Insérer dans la base de données
            serviceConsultation.insert(consultation);

            // Afficher un message de succès
            showAlert("Succès", "Consultation ajoutée avec succès", Alert.AlertType.INFORMATION);

            // Fermer la fenêtre
            Stage stage = (Stage) submit.getScene().getWindow();
            stage.close();

        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Erreur", "Erreur lors de l'ajout de la consultation", Alert.AlertType.ERROR);
        }
    }

    private void showAlert(String title, String content, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    @FXML
    private void ControlMedicalShow(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ControlMedicalShow.fxml"));
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.show();
            
            // Fermer la fenêtre actuelle
            Stage currentStage = (Stage) submit.getScene().getWindow();
            currentStage.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void DashbordTemplate(ActionEvent event) {
        // TODO: Implémenter la navigation vers le dashboard
    }
}

