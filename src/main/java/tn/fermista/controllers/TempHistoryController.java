package tn.fermista.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;
import tn.fermista.models.arg_temp;
import tn.fermista.services.ServiceArgTemp;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ResourceBundle;
import java.util.function.Predicate;
import java.util.ArrayList;

public class TempHistoryController implements Initializable {

    @FXML
    private TableView<arg_temp> tempTable;
    @FXML
    private TableColumn<arg_temp, String> dateColumn;
    @FXML
    private TableColumn<arg_temp, String> temperatureColumn;
    @FXML
    private Button firstPageBtn;
    @FXML
    private Button prevPageBtn;
    @FXML
    private Button nextPageBtn;
    @FXML
    private Button lastPageBtn;
    @FXML
    private Label pageInfo;
    @FXML
    private TextField searchField;
    @FXML
    private Button backButton;

    private final ServiceArgTemp service = new ServiceArgTemp();
    private final ObservableList<arg_temp> tempData = FXCollections.observableArrayList();
    private FilteredList<arg_temp> filteredData;
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd-MM-yyyy HH:mm:ss");

    private static final int ROWS_PER_PAGE = 10;
    private int currentPage = 1;
    private int totalPages = 1;
    private List<arg_temp> allData = new ArrayList<>();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Configuration des colonnes
        dateColumn.setCellValueFactory(cellData -> {
            String formattedDate = cellData.getValue().getTimeReceive().format(formatter);
            return javafx.beans.binding.Bindings.createStringBinding(() -> formattedDate);
        });
        temperatureColumn.setCellValueFactory(new PropertyValueFactory<>("temperature"));

        // Ajout du symbole °C à la colonne température
        temperatureColumn.setCellFactory(column -> new TableCell<arg_temp, String>() {
            @Override
            protected void updateItem(String item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item + " °C");
                }
            }
        });

        // Configuration du style des lignes alternées
        tempTable.setRowFactory(tv -> new TableRow<arg_temp>() {
            @Override
            protected void updateItem(arg_temp item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setStyle("");
                } else if (getIndex() % 2 == 0) {
                    setStyle("-fx-background-color: white;");
                } else {
                    setStyle("-fx-background-color: #f8f9fa;");
                }
            }
        });

        // Chargement des données
        loadData();
        updateTable();

        // Configuration de la recherche
        filteredData = new FilteredList<>(tempData, p -> true);
        tempTable.setItems(filteredData);

        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(createPredicate(newValue));
        });
    }

    private void loadData() {
        try {
            allData.clear();
            allData.addAll(service.showAll());
            totalPages = (int) Math.ceil((double) allData.size() / ROWS_PER_PAGE);
            updatePageInfo();
        } catch (SQLException e) {
            showError("Erreur de chargement", "Impossible de charger les données: " + e.getMessage());
        }
    }

    private void updateTable() {
        int fromIndex = (currentPage - 1) * ROWS_PER_PAGE;
        int toIndex = Math.min(fromIndex + ROWS_PER_PAGE, allData.size());
        
        ObservableList<arg_temp> pageData = FXCollections.observableArrayList(
            allData.subList(fromIndex, toIndex)
        );
        
        tempData.clear();
        tempData.addAll(pageData);
        tempTable.setItems(tempData);
        updatePageInfo();
    }

    private void updatePageInfo() {
        pageInfo.setText("Page " + currentPage + " sur " + totalPages);
        
        // Update button states
        firstPageBtn.setDisable(currentPage == 1);
        prevPageBtn.setDisable(currentPage == 1);
        nextPageBtn.setDisable(currentPage == totalPages);
        lastPageBtn.setDisable(currentPage == totalPages);
    }

    @FXML
    private void handleFirstPage() {
        currentPage = 1;
        updateTable();
    }

    @FXML
    private void handlePrevPage() {
        if (currentPage > 1) {
            currentPage--;
            updateTable();
        }
    }

    @FXML
    private void handleNextPage() {
        if (currentPage < totalPages) {
            currentPage++;
            updateTable();
        }
    }

    @FXML
    private void handleLastPage() {
        currentPage = totalPages;
        updateTable();
    }

    @FXML
    private void handleSearch() {
        String searchText = searchField.getText().toLowerCase();
        if (searchText.isEmpty()) {
            loadData();
        } else {
            List<arg_temp> filteredData = new ArrayList<>();
            for (arg_temp data : allData) {
                if (data.getTimeReceive().format(formatter).toLowerCase().contains(searchText) ||
                    data.getTemperature().toLowerCase().contains(searchText)) {
                    filteredData.add(data);
                }
            }
            allData = filteredData;
            totalPages = (int) Math.ceil((double) allData.size() / ROWS_PER_PAGE);
            currentPage = 1;
            updateTable();
        }
    }

    @FXML
    private void handleBack() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/TempChart.fxml"));
            Parent root = loader.load();
            Scene scene = backButton.getScene();
            scene.setRoot(root);
        } catch (IOException e) {
            showError("Erreur de navigation", "Impossible de retourner à la page précédente: " + e.getMessage());
        }
    }

    private void showError(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private Predicate<arg_temp> createPredicate(String searchText) {
        return temp -> {
            if (searchText == null || searchText.isEmpty()) {
                return true;
            }

            String lowerCaseFilter = searchText.toLowerCase();

            String date = temp.getTimeReceive().format(formatter);
            String temperature = temp.getTemperature();

            return date.toLowerCase().contains(lowerCaseFilter) ||
                   temperature.toLowerCase().contains(lowerCaseFilter);
        };
    }
} 