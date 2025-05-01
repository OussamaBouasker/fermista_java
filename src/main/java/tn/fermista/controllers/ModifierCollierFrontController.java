package tn.fermista.controllers;

import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import tn.fermista.models.Collier;
import tn.fermista.models.Vache;
import tn.fermista.services.ServiceCollier;
import tn.fermista.services.ServiceVache;

import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

public class ModifierCollierFrontController {
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

    private Collier collier;
    private final ServiceVache serviceVache = new ServiceVache();
    private final ServiceCollier serviceCollier = new ServiceCollier();

    @FXML
    public void initialize() {
        List<Vache> vaches = serviceVache.getAll();
        List<Collier> colliers = serviceCollier.showAll();
        Set<Integer> vacheIdsAvecCollier = colliers.stream()
                .filter(c -> c.getVache() != null)
                .map(c -> c.getVache().getId())
                .collect(Collectors.toSet());
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
        // Le filtrage se fait dans setCollier pour inclure la vache liée au collier en cours
    }

    public void setCollier(Collier collier) {
        this.collier = collier;
        referenceField.setText(collier.getReference());
        tailleField.setText(collier.getTaille());
        temperatureField.setText(String.valueOf(collier.getValeurTemperature()));
        agitationField.setText(String.valueOf(collier.getValeurAgitation()));
        List<Vache> vaches = serviceVache.getAll();
        List<Collier> colliers = serviceCollier.showAll();
        Set<Integer> vacheIdsAvecCollier = colliers.stream()
                .filter(c -> c.getVache() != null && (collier.getVache() == null || c.getVache().getId() != collier.getVache().getId()))
                .map(c -> c.getVache().getId())
                .collect(Collectors.toSet());
        List<Vache> vachesDisponibles = vaches.stream()
                .filter(v -> !vacheIdsAvecCollier.contains(v.getId()) || (collier.getVache() != null && v.getId() == collier.getVache().getId()))
                .collect(Collectors.toList());
        vacheComboBox.setItems(FXCollections.observableArrayList(vachesDisponibles));
        if (collier.getVache() != null) {
            vacheComboBox.getSelectionModel().select(collier.getVache());
        }
    }

    public String getReference() { return referenceField.getText(); }
    public String getTaille() { return tailleField.getText(); }
    public String getTemperature() { return temperatureField.getText(); }
    public String getAgitation() { return agitationField.getText(); }
    public Vache getSelectedVache() { return vacheComboBox.getValue(); }
} 