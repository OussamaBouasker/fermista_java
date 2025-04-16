package tn.fermista.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import tn.fermista.models.Workshop;
import tn.fermista.services.ServiceWorkshop;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Optional;
import java.util.ResourceBundle;

public class ShowWorkshopsController implements Initializable {

    @FXML private TableView<Workshop> workshopTable;
    @FXML private TableColumn<Workshop, Integer> idColumn;
    @FXML private TableColumn<Workshop, String> titreColumn;
    @FXML private TableColumn<Workshop, LocalDateTime> dateColumn;
    @FXML private TableColumn<Workshop, String> prixColumn;
    @FXML private TableColumn<Workshop, String> themeColumn;
    @FXML private TableColumn<Workshop, String> typeColumn;
    @FXML private TableColumn<Workshop, Integer> placesColumn;
    @FXML private TableColumn<Workshop, LocalTime> durationColumn;
    @FXML private TableColumn<Workshop, String> formateurColumn;
    @FXML private TableColumn<Workshop, Void> actionsColumn;
    @FXML private Button addButton;

    private ServiceWorkshop serviceWorkshop;
    private ObservableList<Workshop> workshopList;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        try {
            serviceWorkshop = new ServiceWorkshop();
            workshopList = FXCollections.observableArrayList();

            // Initialize columns
            idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
            titreColumn.setCellValueFactory(new PropertyValueFactory<>("titre"));
            dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
            prixColumn.setCellValueFactory(new PropertyValueFactory<>("prix"));
            themeColumn.setCellValueFactory(new PropertyValueFactory<>("theme"));
            typeColumn.setCellValueFactory(new PropertyValueFactory<>("type"));
            placesColumn.setCellValueFactory(new PropertyValueFactory<>("nbrPlacesRestantes"));
            durationColumn.setCellValueFactory(new PropertyValueFactory<>("duration"));
            
            formateurColumn.setCellValueFactory(cellData -> {
                Workshop workshop = cellData.getValue();
                if (workshop.getUser() != null) {
                    return javafx.beans.binding.Bindings.createStringBinding(
                        () -> workshop.getUser().getFirstName() + " " + workshop.getUser().getLastName()
                    );
                }
                return javafx.beans.binding.Bindings.createStringBinding(() -> "");
            });

            // Configure the action column
            actionsColumn.setCellFactory(param -> new TableCell<>() {
                private final Button editButton = new Button("Modifier");
                private final Button deleteButton = new Button("Supprimer");
                private final HBox buttons = new HBox(5, editButton, deleteButton);

                {
                    editButton.getStyleClass().add("button-primary");
                    deleteButton.getStyleClass().add("button-danger");
                    
                    editButton.setOnAction(event -> {
                        Workshop workshop = getTableView().getItems().get(getIndex());
                        handleEdit(workshop);
                    });

                    deleteButton.setOnAction(event -> {
                        Workshop workshop = getTableView().getItems().get(getIndex());
                        handleDelete(workshop);
                    });
                }

                @Override
                protected void updateItem(Void item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) {
                        setGraphic(null);
                    } else {
                        setGraphic(buttons);
                    }
                }
            });

            loadWorkshops();
        } catch (Exception e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de l'initialisation: " + e.getMessage());
        }
    }

    private void handleEdit(Workshop workshop) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AddWorkshopForm.fxml"));
            Parent root = loader.load();
            
            AddWorkshopController controller = loader.getController();
            controller.setParentController(this);
            controller.setWorkshopForEdit(workshop);
            
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Modifier le Workshop");
            stage.setScene(new Scene(root));
            stage.show();
            
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de l'ouverture du formulaire de modification");
        }
    }

    private void handleDelete(Workshop workshop) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation de suppression");
        alert.setHeaderText("Supprimer le workshop");
        alert.setContentText("Êtes-vous sûr de vouloir supprimer ce workshop ?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                serviceWorkshop.delete(workshop);
                refreshTable();
                showAlert(Alert.AlertType.INFORMATION, "Succès", "Workshop supprimé avec succès");
            } catch (SQLException e) {
                e.printStackTrace();
                showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de la suppression du workshop");
            }
        }
    }

    @FXML
    private void handleAddWorkshop() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AddWorkshopForm.fxml"));
            Parent root = loader.load();
            
            AddWorkshopController controller = loader.getController();
            controller.setParentController(this);
            
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Ajouter un Workshop");
            stage.setScene(new Scene(root));
            stage.show();
            
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de l'ouverture du formulaire d'ajout");
        }
    }

    private void loadWorkshops() {
        try {
            workshopList.clear();
            workshopList.addAll(serviceWorkshop.showAll());
            workshopTable.setItems(workshopList);
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors du chargement des workshops");
        }
    }

    public void refreshTable() {
        loadWorkshops();
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }
} 