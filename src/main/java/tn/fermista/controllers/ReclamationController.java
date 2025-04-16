package tn.fermista.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Stage;
import javafx.util.Callback;
import tn.fermista.models.Reclamation;
import tn.fermista.models.User;
import tn.fermista.services.ServiceReclamation;
import tn.fermista.services.ServiceUser;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ResourceBundle;

public class ReclamationController implements Initializable {

    @FXML
    private TableView<Reclamation> reclamationTable;

    @FXML
    private TableColumn<Reclamation, String> titreColumn;

    @FXML
    private TableColumn<Reclamation, String> statusColumn;

    @FXML
    private TableColumn<Reclamation, LocalDateTime> dateSoumissionColumn;

    @FXML
    private TableColumn<Reclamation, String> userColumn;

    @FXML
    private TableColumn<Reclamation, Integer> actionsColumn;

    @FXML
    private VBox updatePopup;

    @FXML
    private ComboBox<String> popup_statusComboBox;

    @FXML
    private TextField txt_search;
    
    @FXML
    private Button btn_workbench11;

    @FXML
    private Button btn_workbench114;

    private ServiceReclamation serviceReclamation;
    private ServiceUser serviceUser;
    private ObservableList<Reclamation> reclamationList;
    private Reclamation selectedReclamation;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        serviceReclamation = new ServiceReclamation();
        serviceUser = new ServiceUser();
        reclamationList = FXCollections.observableArrayList();

        // Initialize columns
        titreColumn.setCellValueFactory(new PropertyValueFactory<>("titre"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        dateSoumissionColumn.setCellValueFactory(new PropertyValueFactory<>("dateSoumission"));
        userColumn.setCellValueFactory(cellData -> {
            Reclamation reclamation = cellData.getValue();
            User user = reclamation.getUser();
            return new javafx.beans.property.SimpleStringProperty(
                    user != null ? user.getFirstName() + " " + user.getLastName() : "N/A"
            );
        });

        // Initialize actions column
        actionsColumn.setCellFactory(createActionsCellFactory());

        // Initialize status combo box
        popup_statusComboBox.getItems().addAll("pending", "confirmed", "canceled");
        popup_statusComboBox.setValue("pending");

        // Load data
        loadReclamations();

        // Setup search functionality
        setupSearch();
        
        // Setup navigation
        if (btn_workbench11 != null) {
            btn_workbench11.setOnAction(event -> navigateToCrudUser());
        }
        if (btn_workbench114 != null) {
            btn_workbench114.setOnAction(event -> navigateToDashboard());
        }
    }
    
    private void navigateToCrudUser() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/CrudUser.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            Stage stage = (Stage) btn_workbench11.getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void navigateToDashboard() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/DashboardTemplate.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            Stage stage = (Stage) btn_workbench114.getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void setupSearch() {
        if (reclamationList == null) {
            reclamationList = FXCollections.observableArrayList();
        }

        FilteredList<Reclamation> filteredData = new FilteredList<>(reclamationList, b -> true);

        txt_search.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(reclamation -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }

                String lowerCaseFilter = newValue.toLowerCase();

                // Recherche par titre
                if (reclamation.getTitre().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                }
                // Recherche par description
                else if (reclamation.getDescription().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                }
                // Recherche par statut
                else if (reclamation.getStatus().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                }
                // Recherche par nom d'utilisateur
                else if (reclamation.getUser() != null) {
                    String userName = reclamation.getUser().getFirstName() + " " + reclamation.getUser().getLastName();
                    if (userName.toLowerCase().contains(lowerCaseFilter)) {
                        return true;
                    }
                }
                // Recherche par date (format: dd/MM/yyyy)
                else if (reclamation.getDateSoumission() != null) {
                    DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
                    String dateStr = reclamation.getDateSoumission().format(formatter);
                    if (dateStr.contains(lowerCaseFilter)) {
                        return true;
                    }
                }

                return false;
            });
        });

        SortedList<Reclamation> sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(reclamationTable.comparatorProperty());
        reclamationTable.setItems(sortedData);
    }

    private Callback<TableColumn<Reclamation, Integer>, TableCell<Reclamation, Integer>> createActionsCellFactory() {
        return new Callback<>() {
            @Override
            public TableCell<Reclamation, Integer> call(TableColumn<Reclamation, Integer> param) {
                return new TableCell<>() {
                    private final Button updateButton = new Button("Modifier");
                    private final Button deleteButton = new Button("Supprimer");

                    {
                        updateButton.getStyleClass().add("btn-update");
                        deleteButton.getStyleClass().add("btn-delete");

                        updateButton.setOnAction(event -> {
                            Reclamation reclamation = getTableView().getItems().get(getIndex());
                            showUpdatePopup(reclamation);
                        });

                        deleteButton.setOnAction(event -> {
                            Reclamation reclamation = getTableView().getItems().get(getIndex());
                            handleDeleteReclamation(reclamation);
                        });
                    }

                    @Override
                    protected void updateItem(Integer item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            HBox buttons = new HBox(10, updateButton, deleteButton);
                            buttons.setStyle("-fx-alignment: center;");
                            setGraphic(buttons);
                        }
                    }
                };
            }
        };
    }

    private void showUpdatePopup(Reclamation reclamation) {
        selectedReclamation = reclamation;
        popup_statusComboBox.setValue(reclamation.getStatus());
        updatePopup.setVisible(true);
    }

    @FXML
    private void handleSaveUpdate() {
        if (selectedReclamation == null) return;

        try {
            selectedReclamation.setStatus(popup_statusComboBox.getValue());
            serviceReclamation.update(selectedReclamation);
            loadReclamations();
            updatePopup.setVisible(false);
        } catch (SQLException e) {
            showAlert("Error", "Failed to update reclamation: " + e.getMessage());
        }
    }

    @FXML
    private void handleCancelUpdate() {
        updatePopup.setVisible(false);
    }

    private void handleDeleteReclamation(Reclamation reclamation) {
        try {
            serviceReclamation.delete(reclamation);
            loadReclamations();
        } catch (SQLException e) {
            showAlert("Error", "Failed to delete reclamation: " + e.getMessage());
        }
    }

    private void loadReclamations() {
        try {
            List<Reclamation> reclamations = serviceReclamation.showAll();
            reclamationList.setAll(reclamations);
            reclamationTable.setItems(reclamationList);

            // Apply the search filter if it exists
            setupSearch();
        } catch (SQLException e) {
            showAlert("Error", "Failed to load reclamations: " + e.getMessage());
        }
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}