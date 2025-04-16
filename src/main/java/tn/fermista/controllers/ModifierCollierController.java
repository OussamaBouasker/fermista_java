package tn.fermista.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import tn.fermista.models.Collier;
import tn.fermista.models.Vache;
import tn.fermista.services.ServiceCollier;
import tn.fermista.services.ServiceVache;

import java.io.IOException;
import java.util.List;

public class ModifierCollierController {
    @FXML
    private TextField referenceField;
    @FXML
    private TextField tailleField;
    @FXML
    private TextField valeurTemperatureField;
    @FXML
    private TextField valeurAgitationField;
    @FXML
    private ComboBox<Vache> vacheComboBox;
    @FXML
    private Button saveButton;
    @FXML
    private Button backButton;
    @FXML
    private Button btn_workbench1121; // Bouton Suivis médical

    private Collier collier;
    private ServiceCollier serviceCollier;
    private ServiceVache serviceVache;

    @FXML
    public void initialize() {
        serviceCollier = new ServiceCollier();
        serviceVache = new ServiceVache();
        
        // Charger les vaches dans la ComboBox
        vacheComboBox.getItems().addAll(serviceVache.getAll());
        vacheComboBox.setCellFactory(lv -> new ListCell<Vache>() {
            @Override
            protected void updateItem(Vache item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? "" : item.getName());
            }
        });
        vacheComboBox.setButtonCell(new ListCell<Vache>() {
            @Override
            protected void updateItem(Vache item, boolean empty) {
                super.updateItem(item, empty);
                setText(empty ? "" : item.getName());
            }
        });

        // Ajouter l'action pour le bouton Suivis médical
        btn_workbench1121.setOnAction(event -> NavigationController.naviguerVersSuiviMedical(btn_workbench1121));
    }

    private boolean hasCollier(Vache vache) {
        if (vache == null) return false;
        List<Collier> colliers = serviceCollier.showAll();
        for (Collier c : colliers) {
            if (c.getVache() != null && c.getVache().getId() == vache.getId() && c.getId() != collier.getId()) {
                return true;
            }
        }
        return false;
    }

    public void setCollierAModifier(Collier collier) {
        this.collier = collier;
        if (collier != null) {
            referenceField.setText(collier.getReference());
            tailleField.setText(collier.getTaille());
            valeurTemperatureField.setText(String.valueOf(collier.getValeurTemperature()));
            valeurAgitationField.setText(String.valueOf(collier.getValeurAgitation()));
            vacheComboBox.setValue(collier.getVache());
        }
    }

    @FXML
    private void modifierCollier() {
        // Validation des champs
        if (!validerChamps()) {
            return;
        }

        try {
            // Récupérer les valeurs des champs
            String reference = referenceField.getText();
            String taille = tailleField.getText();
            double temperature = Double.parseDouble(valeurTemperatureField.getText());
            double agitation = Double.parseDouble(valeurAgitationField.getText());
            Vache vache = vacheComboBox.getValue();

            // Mettre à jour le collier
            collier.setReference(reference);
            collier.setTaille(taille);
            collier.setValeurTemperature(temperature);
            collier.setValeurAgitation(agitation);
            collier.setVache(vache);

            // Sauvegarder les modifications
            serviceCollier.update(collier);

            showAlert("Succès", "Collier modifié avec succès", Alert.AlertType.INFORMATION);
            retourALaListe();
        } catch (NumberFormatException e) {
            showAlert("Erreur", "Les valeurs de température et d'agitation doivent être des nombres valides", Alert.AlertType.ERROR);
        }
    }

    private boolean validerChamps() {
        // Vérification de la référence
        if (referenceField.getText().isEmpty()) {
            showAlert("Erreur", "La référence est obligatoire", Alert.AlertType.ERROR);
            return false;
        }
        if (!referenceField.getText().matches("[a-zA-Z0-9]+")) {
            showAlert("Erreur", "La référence ne doit contenir que des lettres et des chiffres", Alert.AlertType.ERROR);
            return false;
        }

        // Vérification de la taille
        if (tailleField.getText().isEmpty()) {
            showAlert("Erreur", "La taille est obligatoire", Alert.AlertType.ERROR);
            return false;
        }
        if (!tailleField.getText().matches("[a-zA-Z0-9]+")) {
            showAlert("Erreur", "La taille ne doit contenir que des lettres et des chiffres", Alert.AlertType.ERROR);
            return false;
        }

        // Vérification de la température
        if (valeurTemperatureField.getText().isEmpty()) {
            showAlert("Erreur", "La température est obligatoire", Alert.AlertType.ERROR);
            return false;
        }
        try {
            double temperature = Double.parseDouble(valeurTemperatureField.getText());
            if (temperature < 35.0 || temperature > 40.5) {
                showAlert("Erreur", "La température doit être entre 35.0 et 40.5", Alert.AlertType.ERROR);
                return false;
            }
        } catch (NumberFormatException e) {
            showAlert("Erreur", "La température doit être un nombre valide", Alert.AlertType.ERROR);
            return false;
        }

        // Vérification de l'agitation
        if (valeurAgitationField.getText().isEmpty()) {
            showAlert("Erreur", "L'agitation est obligatoire", Alert.AlertType.ERROR);
            return false;
        }
        try {
            double agitation = Double.parseDouble(valeurAgitationField.getText());
            if (agitation < 0 || agitation > 10) {
                showAlert("Erreur", "L'agitation doit être entre 0 et 10", Alert.AlertType.ERROR);
                return false;
            }
        } catch (NumberFormatException e) {
            showAlert("Erreur", "L'agitation doit être un nombre valide", Alert.AlertType.ERROR);
            return false;
        }

        // Vérification de la vache
        if (vacheComboBox.getValue() == null) {
            showAlert("Erreur", "La vache est obligatoire", Alert.AlertType.ERROR);
            return false;
        }

        // Vérification si la vache a déjà un collier
        Vache selectedVache = vacheComboBox.getValue();
        if (hasCollier(selectedVache)) {
            showAlert("Erreur", "Cette vache a déjà un collier attaché!", Alert.AlertType.WARNING);
            return false;
        }

        return true;
    }

    @FXML
    private void supprimerCollier() {
        Alert confirmAlert = new Alert(Alert.AlertType.CONFIRMATION);
        confirmAlert.setTitle("Confirmation");
        confirmAlert.setHeaderText("Supprimer le collier");
        confirmAlert.setContentText("Êtes-vous sûr de vouloir supprimer ce collier ?");

        if (confirmAlert.showAndWait().get() == ButtonType.OK) {
            try {
                serviceCollier.delete(collier);
                showAlert("Succès", "Collier supprimé avec succès", Alert.AlertType.INFORMATION);
                retourALaListe();
            } catch (Exception e) {
                showAlert("Erreur", "Erreur lors de la suppression: " + e.getMessage(), Alert.AlertType.ERROR);
            }
        }
    }

    @FXML
    private void retourALaListe() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ListeCollier.fxml"));
            Parent root = loader.load();
            Scene scene = backButton.getScene();
            scene.setRoot(root);
        } catch (IOException e) {
            showAlert("Erreur", "Erreur lors du retour à la liste: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void showAlert(String title, String content, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
} 