package tn.fermista.controllers;

import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import tn.fermista.models.arg_agit;
import tn.fermista.services.ServiceArgAgit;

import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class AgitationHistoryController implements Initializable {
    @FXML private TableView<arg_agit> agitationTable;
    @FXML private TableColumn<arg_agit, String> dateColumn;
    @FXML private TableColumn<arg_agit, String> xColumn;
    @FXML private TableColumn<arg_agit, String> yColumn;
    @FXML private TableColumn<arg_agit, String> zColumn;
    @FXML private TextField searchField;
    @FXML private Label pageInfo;
    @FXML private Button firstPageBtn, prevPageBtn, nextPageBtn, lastPageBtn, backButton;

    private final ServiceArgAgit agitService = new ServiceArgAgit();
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
    private List<arg_agit> allData = new ArrayList<>();
    private List<arg_agit> filteredData = new ArrayList<>();
    private static final int ROWS_PER_PAGE = 15;
    private int currentPage = 1;
    private int totalPages = 1;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        dateColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getTimeReceive().format(formatter)));
        xColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getAccelX()));
        yColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getAccelY()));
        zColumn.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getAccelZ()));
        loadData();
    }

    private void loadData() {
        try {
            allData = agitService.showAll();
            filteredData = new ArrayList<>(allData);
            currentPage = 1;
            updateTable();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateTable() {
        totalPages = (int) Math.ceil((double) filteredData.size() / ROWS_PER_PAGE);
        if (totalPages == 0) totalPages = 1;
        int fromIndex = (currentPage - 1) * ROWS_PER_PAGE;
        int toIndex = Math.min(fromIndex + ROWS_PER_PAGE, filteredData.size());
        ObservableList<arg_agit> pageData = FXCollections.observableArrayList();
        if (fromIndex < toIndex) {
            pageData.addAll(filteredData.subList(fromIndex, toIndex));
        }
        agitationTable.setItems(pageData);
        pageInfo.setText("Page " + currentPage + " sur " + totalPages);
        updateNavButtons();
    }

    private void updateNavButtons() {
        firstPageBtn.setDisable(currentPage == 1);
        prevPageBtn.setDisable(currentPage == 1);
        nextPageBtn.setDisable(currentPage == totalPages);
        lastPageBtn.setDisable(currentPage == totalPages);
    }

    @FXML
    private void handleSearch(ActionEvent event) {
        String search = searchField.getText().trim().toLowerCase();
        if (search.isEmpty()) {
            filteredData = new ArrayList<>(allData);
        } else {
            filteredData = allData.stream().filter(a ->
                a.getTimeReceive().format(formatter).toLowerCase().contains(search) ||
                a.getAccelX().toLowerCase().contains(search) ||
                a.getAccelY().toLowerCase().contains(search) ||
                a.getAccelZ().toLowerCase().contains(search)
            ).collect(Collectors.toList());
        }
        currentPage = 1;
        updateTable();
    }

    @FXML
    private void handleFirstPage(ActionEvent event) {
        currentPage = 1;
        updateTable();
    }

    @FXML
    private void handlePrevPage(ActionEvent event) {
        if (currentPage > 1) {
            currentPage--;
            updateTable();
        }
    }

    @FXML
    private void handleNextPage(ActionEvent event) {
        if (currentPage < totalPages) {
            currentPage++;
            updateTable();
        }
    }

    @FXML
    private void handleLastPage(ActionEvent event) {
        currentPage = totalPages;
        updateTable();
    }

    @FXML
    private void handleBack(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/TempChart.fxml"));
            Parent root = loader.load();
            Scene scene = backButton.getScene();
            scene.setRoot(root);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
} 