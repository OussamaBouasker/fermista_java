package tn.fermista.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import tn.fermista.models.Consultation;
import tn.fermista.models.RapportMedical;
import tn.fermista.models.Vache;
import tn.fermista.services.ServiceConsultation;
import tn.fermista.services.ServiceRapportMedical;
import tn.fermista.services.ServiceVache;

import java.sql.Date;
import java.sql.Time;
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
    private final ServiceConsultation serviceConsultation = new ServiceConsultation();
    private final ServiceRapportMedical serviceRapportMedical = new ServiceRapportMedical();
    private final ServiceVache serviceVache = new ServiceVache();

    @FXML
    public void initialize() {
        try {
            List<RapportMedical> rapports = serviceRapportMedical.showAll();
            rapportMedicalComboBox.getItems().addAll(rapports);

            List<Vache> vaches = serviceVache.showAll();
            vacheComboBox.getItems().addAll(vaches);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void handleSubmit() {
        try {
            Consultation consultation = new Consultation();
            consultation.setNom(nomField.getText());
            consultation.setDate(Date.valueOf(datePicker.getValue()));
            consultation.setHeure(Time.valueOf(heureField.getText() + ":00")); // Ex: "10:30"
            consultation.setLieu(lieuField.getText());
            consultation.setRapportMedical(rapportMedicalComboBox.getValue());
            consultation.setVache(vacheComboBox.getValue());

            serviceConsultation.insert(consultation);

            Alert alert = new Alert(Alert.AlertType.INFORMATION, "Consultation enregistrée !");
            alert.show();

        } catch (Exception e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR, "Erreur lors de l'enregistrement.");
            alert.show();
        }
    }

    public void DashbordTemplate(ActionEvent actionEvent) {
    }

    public void ControlMedicalShow(ActionEvent actionEvent) {
    }
}

