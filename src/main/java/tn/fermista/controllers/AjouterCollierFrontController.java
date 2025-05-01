package tn.fermista.controllers;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import tn.fermista.models.Collier;
import tn.fermista.models.Vache;
import tn.fermista.services.ServiceCollier;
import tn.fermista.services.ServiceVache;

import java.util.List;
import java.util.stream.Collectors;

public class AjouterCollierFrontController {
    @FXML
    private TextField referenceField;
    @FXML
    private TextField tailleField;
    @FXML
    private TextField temperatureField;
    @FXML
    private TextField agitationField;
    @FXML
    private ComboBox<Vache> vacheComboBox;

    private final ServiceVache serviceVache = new ServiceVache();
    private final ServiceCollier serviceCollier = new ServiceCollier();

    @FXML
    public void initialize() {
        List<Vache> vaches = serviceVache.getAll();
        // Filtrer les vaches qui n'ont pas de collier
        List<Vache> vachesDisponibles = vaches.stream()
                .filter(v -> !serviceCollier.vacheHasCollier(v.getId()))
                .collect(Collectors.toList());
        vacheComboBox.setItems(FXCollections.observableArrayList(vachesDisponibles));
        vacheComboBox.setCellFactory(param -> new javafx.scene.control.ListCell<>() {
            @Override
            protected void updateItem(Vache vache, boolean empty) {
                super.updateItem(vache, empty);
                setText(empty || vache == null ? null : vache.getName());
            }
        });
        vacheComboBox.setButtonCell(new javafx.scene.control.ListCell<>() {
            @Override
            protected void updateItem(Vache vache, boolean empty) {
                super.updateItem(vache, empty);
                setText(empty || vache == null ? null : vache.getName());
            }
        });
    }

    public boolean validateFields() {
        // Vérifier que tous les champs sont remplis
        if (referenceField.getText().isEmpty() || 
            tailleField.getText().isEmpty() || 
            temperatureField.getText().isEmpty() || 
            agitationField.getText().isEmpty() || 
            vacheComboBox.getValue() == null) {
            showAlert("Erreur", "Tous les champs sont obligatoires !");
            return false;
        }

        try {
            // Vérifier la température (entre 35 et 40.5)
            double temperature = Double.parseDouble(temperatureField.getText());
            if (temperature < 35 || temperature > 40.5) {
                showAlert("Erreur", "La température doit être comprise entre 35 et 40.5 !");
                return false;
            }

            // Vérifier l'agitation (entre 0 et 10)
            double agitation = Double.parseDouble(agitationField.getText());
            if (agitation < 0 || agitation > 10) {
                showAlert("Erreur", "L'agitation doit être comprise entre 0 et 10 !");
                return false;
            }
        } catch (NumberFormatException e) {
            showAlert("Erreur", "La température et l'agitation doivent être des nombres valides !");
            return false;
        }

        return true;
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    public String getReference() { return referenceField.getText(); }
    public String getTaille() { return tailleField.getText(); }
    public String getTemperature() { return temperatureField.getText(); }
    public String getAgitation() { return agitationField.getText(); }
    public Vache getSelectedVache() { return vacheComboBox.getValue(); }
} 