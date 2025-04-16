package tn.fermista.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import javafx.beans.property.SimpleStringProperty;
import tn.fermista.models.Collier;
import tn.fermista.services.ServiceCollier;

public class ListeCollierController {

    @FXML
    private TableView<Collier> collierTable;
    @FXML
    private TableColumn<Collier, Integer> idColumn;
    @FXML
    private TableColumn<Collier, String> referenceColumn;
    @FXML
    private TableColumn<Collier, String> tailleColumn;
    @FXML
    private TableColumn<Collier, Double> temperatureColumn;
    @FXML
    private TableColumn<Collier, Double> agitationColumn;
    @FXML
    private TableColumn<Collier, String> vacheColumn;
    @FXML
    private TableColumn<Collier, Void> actionColumn;
    @FXML
    private Button btn_workbench1132;
    @FXML
    private Button btn_workbench1121; // Bouton Suivis médical

    private final ServiceCollier serviceCollier = new ServiceCollier();

    @FXML
    public void initialize() {
        setupColumns();
        loadColliers();
        
        // Ajouter l'action pour le bouton Suivis médical
        btn_workbench1121.setOnAction(event -> NavigationController.naviguerVersSuiviMedical(btn_workbench1121));
    }

    private void setupColumns() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        referenceColumn.setCellValueFactory(new PropertyValueFactory<>("reference"));
        tailleColumn.setCellValueFactory(new PropertyValueFactory<>("taille"));
        temperatureColumn.setCellValueFactory(new PropertyValueFactory<>("valeurTemperature"));
        agitationColumn.setCellValueFactory(new PropertyValueFactory<>("valeurAgitation"));
        vacheColumn.setCellValueFactory(cellData -> {
            Collier collier = cellData.getValue();
            if (collier.getVache() != null) {
                return new SimpleStringProperty(collier.getVache().getName());
            }
            return new SimpleStringProperty("");
        });

        actionColumn.setCellFactory(param -> new TableCell<>() {
            private final Button deleteBtn = new Button("Supprimer");
            private final Button editBtn = new Button("Modifier");
            private final HBox buttonsBox = new HBox(5, editBtn, deleteBtn);

            {
                deleteBtn.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white;");
                editBtn.setStyle("-fx-background-color: #3498db; -fx-text-fill: white;");

                deleteBtn.setOnAction(event -> {
                    Collier collier = getTableView().getItems().get(getIndex());
                    if (collier != null) {
                        serviceCollier.delete(collier);
                        loadColliers();
                    }
                });

                editBtn.setOnAction(event -> {
                    Collier collier = getTableView().getItems().get(getIndex());
                    if (collier != null) {
                        try {
                            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ModifierCollier.fxml"));
                            Parent root = loader.load();
                            
                            ModifierCollierController controller = loader.getController();
                            controller.setCollierAModifier(collier);
                            
                            Scene scene = collierTable.getScene();
                            scene.setRoot(root);
                        } catch (Exception e) {
                            Alert alert = new Alert(Alert.AlertType.ERROR);
                            alert.setTitle("Erreur");
                            alert.setHeaderText("Impossible d'ouvrir la fenêtre de modification");
                            alert.setContentText("Une erreur est survenue : " + e.getMessage());
                            alert.showAndWait();
                        }
                    }
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : buttonsBox);
            }
        });

        // Configuration du bouton Ajouter
        btn_workbench1132.setOnAction(event -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/AjoutCollier.fxml"));
                Parent root = loader.load();
                Scene scene = btn_workbench1132.getScene();
                scene.setRoot(root);
            } catch (Exception e) {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Erreur");
                alert.setHeaderText("Impossible d'ouvrir la fenêtre d'ajout");
                alert.setContentText("Une erreur est survenue : " + e.getMessage());
                alert.showAndWait();
            }
        });
    }

    private void loadColliers() {
        collierTable.getItems().clear();
        collierTable.getItems().addAll(serviceCollier.showAll());
    }
}
