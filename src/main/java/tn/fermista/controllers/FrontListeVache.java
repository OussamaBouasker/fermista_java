package tn.fermista.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Pagination;
import javafx.scene.control.TextField;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Pane;
import tn.fermista.models.Vache;
import tn.fermista.services.ServiceVache;

import java.io.IOException;
import java.util.List;

public class FrontListeVache {
    @FXML
    private FlowPane vacheListPane;
    @FXML
    private TextField searchField;
    @FXML
    private Button applyButton;
    @FXML
    private Pagination pagination;

    private final ServiceVache serviceVache = new ServiceVache();

    @FXML
    public void initialize() {
        afficherVaches();
    }

    private void afficherVaches() {
        vacheListPane.getChildren().clear();
        List<Vache> vaches = serviceVache.getAll();
        for (Vache v : vaches) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/FrontVacheCard.fxml"));
                Pane card = loader.load();
                FrontVacheCard controller = loader.getController();
                controller.setVache(v);
                controller.setOnModificationCallback(this::afficherVaches);
                vacheListPane.getChildren().add(card);
            } catch (IOException e) {
                e.printStackTrace();
    }
}
    }
} 