package tn.fermista.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Dialog;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.Pagination;
import javafx.scene.control.TextField;
import javafx.scene.control.ComboBox;
import javafx.scene.control.ListCell;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.Pane;
import javafx.geometry.Insets;
import javafx.stage.Modality;
import tn.fermista.models.Collier;
import tn.fermista.models.Vache;
import tn.fermista.services.ServiceCollier;
import tn.fermista.services.ServiceVache;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

public class FrontListeCollierController {
    private static FrontListeCollierController instance;

    @FXML
    private FlowPane collierListPane;
    @FXML
    private TextField searchField;
    @FXML
    private Button applyButton;
    @FXML
    private Button addButton;
    @FXML
    private Pagination pagination;

    private final ServiceCollier serviceCollier = new ServiceCollier();
    private final ServiceVache serviceVache = new ServiceVache();
    private List<Collier> allColliers;
    private List<Collier> filteredColliers;
    private static final int ITEMS_PER_PAGE = 6;

    @FXML
    public void initialize() {
        instance = this;
        
        // Configuration de la pagination
        pagination.setPageCount(1);
        pagination.setMaxPageIndicatorCount(5); // Nombre de boutons de page visibles
        pagination.setCurrentPageIndex(0);
        
        // Style de la pagination
        pagination.getStyleClass().add("custom-pagination");
        
        // Ajouter un écouteur pour la pagination
        pagination.currentPageIndexProperty().addListener((obs, oldIndex, newIndex) -> {
            afficherColliers(newIndex.intValue());
        });
        
        refreshCollierList();
        
        // Ajouter un écouteur pour la recherche en temps réel
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            searchColliers(newValue);
        });

        // Configuration du bouton Appliquer
        applyButton.setOnAction(event -> {
            searchColliers(searchField.getText());
        });
    }

    private void searchColliers(String searchText) {
        if (searchText == null || searchText.isEmpty()) {
            filteredColliers = allColliers;
        } else {
            searchText = searchText.toLowerCase();
            final String searchTermFinal = searchText;
            
            filteredColliers = allColliers.stream()
                .filter(collier -> 
                    collier.getReference().toLowerCase().contains(searchTermFinal) ||
                    collier.getTaille().toLowerCase().contains(searchTermFinal) ||
                    (collier.getVache() != null && collier.getVache().getName().toLowerCase().contains(searchTermFinal)))
                .collect(Collectors.toList());
        }

        // Mettre à jour la pagination
        int pageCount = (int) Math.ceil((double) filteredColliers.size() / ITEMS_PER_PAGE);
        pagination.setPageCount(Math.max(1, pageCount));
        pagination.setCurrentPageIndex(0);
        
        // Afficher les résultats
        afficherColliers(0);
    }

    public static FrontListeCollierController getInstance() {
        return instance;
    }

    public void refreshCollierList() {
        try {
            allColliers = serviceCollier.showAll();
            filteredColliers = allColliers;
            int pageCount = (int) Math.ceil((double) filteredColliers.size() / ITEMS_PER_PAGE);
            pagination.setPageCount(pageCount == 0 ? 1 : pageCount);
            pagination.setCurrentPageIndex(0);
            pagination.currentPageIndexProperty().addListener((obs, oldIndex, newIndex) -> 
                afficherColliers(newIndex.intValue()));
            afficherColliers(0);
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText(null);
            alert.setContentText("Erreur lors du chargement des colliers : " + e.getMessage());
            alert.showAndWait();
            
            // En cas d'erreur, initialiser des listes vides
            allColliers = new ArrayList<>();
            filteredColliers = new ArrayList<>();
            pagination.setPageCount(1);
            pagination.setCurrentPageIndex(0);
            collierListPane.getChildren().clear();
        }
    }

    private void afficherColliers(int pageIndex) {
        collierListPane.getChildren().clear();
        
        if (filteredColliers.isEmpty()) {
            // Afficher un message si aucun collier n'est trouvé
            Label noResultsLabel = new Label("Aucun collier trouvé");
            noResultsLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #666666;");
            collierListPane.getChildren().add(noResultsLabel);
            return;
        }

        int fromIndex = pageIndex * ITEMS_PER_PAGE;
        int toIndex = Math.min(fromIndex + ITEMS_PER_PAGE, filteredColliers.size());
        
        if (fromIndex <= toIndex) {
            List<Collier> colliersPage = filteredColliers.subList(fromIndex, toIndex);
            for (Collier c : colliersPage) {
                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/FrontCollierCard.fxml"));
                    Pane card = loader.load();
                    FrontCollierCard controller = loader.getController();
                    controller.setCollier(c);
                    controller.setOnModificationCallback(this::refreshCollierList);
                    collierListPane.getChildren().add(card);
                } catch (IOException e) {
                    Alert alert = new Alert(Alert.AlertType.ERROR);
                    alert.setTitle("Erreur");
                    alert.setHeaderText(null);
                    alert.setContentText("Erreur lors du chargement de la carte : " + e.getMessage());
                    alert.showAndWait();
                }
            }
        }
    }

    @FXML
    public void onAddButtonClick() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AjouterCollierFront.fxml"));
            DialogPane dialogPane = loader.load();
            AjouterCollierFrontController controller = loader.getController();

            Dialog<ButtonType> dialog = new Dialog<>();
            dialog.setTitle("Ajouter un collier");
            dialog.setDialogPane(dialogPane);
            dialog.initModality(Modality.APPLICATION_MODAL);

            Optional<ButtonType> result = dialog.showAndWait();
            if (result.isPresent() && result.get() == ButtonType.OK) {
                // Valider les champs avant d'ajouter
                if (!controller.validateFields()) {
                    return; // Ne pas ajouter si la validation échoue
                }
                
                // Créer le collier à partir des champs
                Collier collier = new Collier();
                collier.setReference(controller.getReference());
                collier.setTaille(controller.getTaille());
                collier.setValeurTemperature(Double.parseDouble(controller.getTemperature()));
                collier.setValeurAgitation(Double.parseDouble(controller.getAgitation()));
                collier.setVache(controller.getSelectedVache());
                // Sauvegarder
                serviceCollier.insert(collier);
                // Rafraîchir la liste
                refreshCollierList();
                // Message de succès
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Succès");
                alert.setHeaderText(null);
                alert.setContentText("Le collier a été ajouté avec succès.");
                alert.showAndWait();
            }
        } catch (Exception e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText(null);
            alert.setContentText("Erreur lors de l'ouverture de la fenêtre d'ajout : " + e.getMessage());
            alert.showAndWait();
        }
    }

    @FXML
    public void onAddButtonHover() {
        addButton.setStyle("-fx-background-color: transparent; -fx-cursor: hand; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 8, 0, 0, 0);");
    }

    @FXML
    public void onAddButtonExit() {
        addButton.setStyle("-fx-background-color: transparent; -fx-cursor: hand; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.2), 5, 0, 0, 0);");
    }
} 