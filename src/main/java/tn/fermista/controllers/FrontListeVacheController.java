package tn.fermista.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Button;
import javafx.scene.control.Pagination;
import javafx.scene.control.TextField;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Pane;
import javafx.scene.effect.DropShadow;
import javafx.scene.paint.Color;
import javafx.stage.Stage;
import javafx.stage.Modality;
import javafx.scene.Scene;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import tn.fermista.models.Vache;
import tn.fermista.services.ServiceVache;

import java.io.IOException;
import java.util.List;
import java.util.stream.Collectors;

public class FrontListeVacheController {
    private static FrontListeVacheController instance;

    @FXML
    private FlowPane vacheListPane;
    @FXML
    private TextField searchField;
    @FXML
    private Button applyButton;
    @FXML
    private Button addButton;
    @FXML
    private Pagination pagination;

    private final ServiceVache serviceVache = new ServiceVache();
    private List<Vache> allVaches;
    private List<Vache> filteredVaches;
    private static final int ITEMS_PER_PAGE = 6;

    @FXML
    public void initialize() {
        instance = this;
        refreshVacheList();
        
        // Ajouter un écouteur pour la recherche en temps réel
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            searchVaches(newValue);
        });

        // Configuration du bouton Appliquer
        applyButton.setOnAction(event -> {
            searchVaches(searchField.getText());
        });
    }

    private void searchVaches(String searchText) {
        if (searchText == null || searchText.isEmpty()) {
            filteredVaches = allVaches;
        } else {
            searchText = searchText.toLowerCase();
            final String searchTermFinal = searchText;
            
            filteredVaches = allVaches.stream()
                .filter(vache -> 
                    vache.getName().toLowerCase().contains(searchTermFinal) ||
                    vache.getRace().toLowerCase().contains(searchTermFinal) ||
                    vache.getEtat_medical().toLowerCase().contains(searchTermFinal) ||
                    String.valueOf(vache.getAge()).contains(searchTermFinal))
                .collect(Collectors.toList());
        }

        // Mettre à jour la pagination
        int pageCount = (int) Math.ceil((double) filteredVaches.size() / ITEMS_PER_PAGE);
        pagination.setPageCount(pageCount == 0 ? 1 : pageCount);
        pagination.setCurrentPageIndex(0);
        
        // Afficher les résultats
        afficherVaches(0);
    }

    public static FrontListeVacheController getInstance() {
        return instance;
    }

    public void refreshVacheList() {
        allVaches = serviceVache.getAll();
        filteredVaches = allVaches;
        int pageCount = (int) Math.ceil((double) filteredVaches.size() / ITEMS_PER_PAGE);
        pagination.setPageCount(pageCount == 0 ? 1 : pageCount);
        pagination.setCurrentPageIndex(0);
        pagination.currentPageIndexProperty().addListener((obs, oldIndex, newIndex) -> 
            afficherVaches(newIndex.intValue()));
        afficherVaches(0);
    }

    private void afficherVaches(int pageIndex) {
        vacheListPane.getChildren().clear();
        int fromIndex = pageIndex * ITEMS_PER_PAGE;
        int toIndex = Math.min(fromIndex + ITEMS_PER_PAGE, filteredVaches.size());
        
        if (fromIndex <= toIndex) {
            List<Vache> vachesPage = filteredVaches.subList(fromIndex, toIndex);
            for (Vache v : vachesPage) {
                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/FrontVacheCard.fxml"));
                    Pane card = loader.load();
                    FrontVacheCard controller = loader.getController();
                    controller.setVache(v);
                    vacheListPane.getChildren().add(card);
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    @FXML
    public void onAddButtonHover() {
        DropShadow shadow = new DropShadow();
        shadow.setColor(Color.ORANGE);
        shadow.setRadius(15);
        addButton.setEffect(shadow);
    }

    @FXML
    public void onAddButtonExit() {
        DropShadow shadow = new DropShadow();
        shadow.setColor(Color.rgb(0, 0, 0, 0.2));
        shadow.setRadius(5);
        addButton.setEffect(shadow);
    }

    @FXML
    public void onAddButtonClick() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AjouterVache.fxml"));
            Pane addVachePane = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Ajouter une vache");
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(new Scene(addVachePane));
            stage.showAndWait();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}