package tn.fermista.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import tn.fermista.models.Vache;
import tn.fermista.services.ServiceVache;

public class ModifierVacheController {

    @FXML
    private TextField tfName;
    @FXML
    private TextField tfRace;
    @FXML
    private TextField tfAge;
    @FXML
    private TextField tfEtatMedical;
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
    private Button btnAjouter11; // Bouton Supprimer
    @FXML
    private Button btnAjouter; // Bouton Retour à la liste

    private final ServiceVache serviceVache = new ServiceVache();
    private Vache vacheAModifier;

    @FXML
    public void initialize() {
        // Ajouter l'action pour le bouton Suivis médical
        btn_workbench1121.setOnAction(event -> NavigationController.naviguerVersSuiviMedical(btn_workbench1121));
    }

    public void setVacheAModifier(Vache vache) {
        this.vacheAModifier = vache;
        if (vache != null) {
            tfName.setText(vache.getName());
            tfRace.setText(vache.getRace());
            tfAge.setText(String.valueOf(vache.getAge()));
            tfEtatMedical.setText(vache.getEtat_medical());
        }
    }

    @FXML
    private void modifierVache() {
        // Validation des champs
        if (!validerChamps()) {
            return;
        }

        try {
            if (vacheAModifier != null) {
                // Mettre à jour les valeurs de la vache
                vacheAModifier.setName(tfName.getText());
                vacheAModifier.setRace(tfRace.getText());
                vacheAModifier.setAge(Integer.parseInt(tfAge.getText()));
                vacheAModifier.setEtat_medical(tfEtatMedical.getText());

                // Mettre à jour dans la base de données
                serviceVache.update(vacheAModifier);
                showAlert("Succès", "Vache modifiée avec succès", Alert.AlertType.INFORMATION);
                
                // Retourner à la liste des vaches
                retourALaListe();
            }
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

    @FXML
    private void supprimerVache() {
        if (vacheAModifier != null) {
            Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
            confirmAlert.setTitle("Confirmation");
            confirmAlert.setHeaderText("Supprimer la vache");
            confirmAlert.setContentText("Êtes-vous sûr de vouloir supprimer cette vache ?");

            if (confirmAlert.showAndWait().get() == ButtonType.OK) {
                try {
                    serviceVache.delete(vacheAModifier);
                    showAlert("Succès", "Vache supprimée avec succès", Alert.AlertType.INFORMATION);
                    retourALaListe();
                } catch (Exception e) {
                    showAlert("Erreur", "Erreur lors de la suppression : " + e.getMessage(), Alert.AlertType.ERROR);
                }
            }
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
    private void retourALaListe() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ListeVache.fxml"));
            Parent root = loader.load();
            Scene scene = tfName.getScene();
            scene.setRoot(root);
        } catch (Exception e) {
            showAlert("Erreur", "Erreur lors du retour à la liste : " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }
}
