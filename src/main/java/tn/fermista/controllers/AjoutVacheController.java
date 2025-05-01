package tn.fermista.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import tn.fermista.models.Vache;
import tn.fermista.services.ServiceVache;
import tn.fermista.models.User;
import java.io.IOException;

public class AjoutVacheController {

    @FXML
    private Button btnAjouter;

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
    private Button btn_workbench113;

    @FXML
    private Pane inner_pane;

    @FXML
    private Pane most_inner_pane;

    @FXML
    private HBox root;

    @FXML
    private AnchorPane side_ankerpane;

    @FXML
    private TextField tfAge;

    @FXML
    private TextField tfEtatMedical;

    @FXML
    private TextField tfName;

    @FXML
    private TextField tfRace;

    private final ServiceVache serviceVache = new ServiceVache();
    private Runnable onVacheAdded;

    @FXML
    private void ajouterVache() {
        // Validation des champs
        if (!validerChamps()) {
            return;
        }

        try {
            int age = Integer.parseInt(tfAge.getText());
            String race = tfRace.getText();
            String etat = tfEtatMedical.getText();
            String name = tfName.getText();

            Vache v = new Vache(age, race, etat, name);
            serviceVache.insert(v);

            showAlert("Succès", "Vache ajoutée avec succès", Alert.AlertType.INFORMATION);
            resetForm();
            if (onVacheAdded != null) onVacheAdded.run();
        } catch (NumberFormatException e) {
            showAlert("Erreur", "L'âge doit être un nombre valide", Alert.AlertType.ERROR);
        }
    }

    private boolean validerChamps() {
        // Vérification des champs obligatoires
        if (tfName.getText().isEmpty()) {
            showAlert("Erreur", "Le nom est obligatoire", Alert.AlertType.ERROR);
            return false;
        }
        if (tfRace.getText().isEmpty()) {
            showAlert("Erreur", "La race est obligatoire", Alert.AlertType.ERROR);
            return false;
        }
        if (tfEtatMedical.getText().isEmpty()) {
            showAlert("Erreur", "L'état médical est obligatoire", Alert.AlertType.ERROR);
            return false;
        }
        if (tfAge.getText().isEmpty()) {
            showAlert("Erreur", "L'âge est obligatoire", Alert.AlertType.ERROR);
            return false;
        }

        // Vérification que le nom ne contient que des lettres
        if (!tfName.getText().matches("[a-zA-Z\\s]+")) {
            showAlert("Erreur", "Le nom ne doit contenir que des lettres", Alert.AlertType.ERROR);
            return false;
        }

        // Vérification que la race ne contient que des lettres
        if (!tfRace.getText().matches("[a-zA-Z\\s]+")) {
            showAlert("Erreur", "La race ne doit contenir que des lettres", Alert.AlertType.ERROR);
            return false;
        }

        // Vérification que l'état médical ne contient que des lettres
        if (!tfEtatMedical.getText().matches("[a-zA-Z\\s]+")) {
            showAlert("Erreur", "L'état médical ne doit contenir que des lettres", Alert.AlertType.ERROR);
            return false;
        }

        // Vérification que l'âge est un nombre positif
        try {
            int age = Integer.parseInt(tfAge.getText());
            if (age <= 0) {
                showAlert("Erreur", "L'âge doit être un nombre positif", Alert.AlertType.ERROR);
                return false;
            }
        } catch (NumberFormatException e) {
            showAlert("Erreur", "L'âge doit être un nombre valide", Alert.AlertType.ERROR);
            return false;
        }

        return true;
    }

    private void showAlert(String title, String content, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void resetForm() {
        tfAge.clear();
        tfRace.clear();
        tfEtatMedical.clear();
        tfName.clear();
    }

    @FXML
    private void retourALaListe() {
        try {
            // Fermer la fenêtre actuelle
            Stage stage = (Stage) tfName.getScene().getWindow();
            stage.close();

            // Charger la fenêtre de liste des vaches
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ListeVache.fxml"));
            Parent root = loader.load();
            Stage newStage = new Stage();
            newStage.setScene(new Scene(root));
            newStage.setTitle("Liste des Vaches");
            newStage.show();
        } catch (Exception e) {
            System.out.println("Erreur lors du retour à la liste : " + e.getMessage());
        }
    }

    @FXML
    public void initialize() {
        // Ajouter l'action pour le bouton Suivis médical
        btn_workbench1121.setOnAction(event -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/choixvachecollier.fxml"));
                Parent root = loader.load();
                Scene scene = new Scene(root);
                Stage stage = (Stage) btn_workbench1121.getScene().getWindow();
                stage.setScene(scene);
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Erreur");
                alert.setHeaderText("Erreur de navigation");
                alert.setContentText("Impossible d'ouvrir la page de suivi médical : " + e.getMessage());
                alert.showAndWait();
            }
        });
    }

    public void setOnVacheAdded(Runnable callback) {
        this.onVacheAdded = callback;
    }
}


