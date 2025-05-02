package tn.fermista.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import tn.fermista.models.arg_agit;
import tn.fermista.services.ServiceArgAgit;

import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

public class ListeArgAgitController implements Initializable {

    @FXML
    private TableView<arg_agit> tableArgAgit;
    @FXML
    private TableColumn<arg_agit, Long> colId;
    @FXML
    private TableColumn<arg_agit, String> colX;
    @FXML
    private TableColumn<arg_agit, String> colY;
    @FXML
    private TableColumn<arg_agit, String> colZ;
    @FXML
    private TableColumn<arg_agit, LocalDateTime> colTime;
    @FXML
    private Label statusLabel;

    private final ServiceArgAgit service = new ServiceArgAgit();
    private final ObservableList<arg_agit> data = FXCollections.observableArrayList();
    private final DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        setupTableColumns();
        refreshData();
    }

    private void setupTableColumns() {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colX.setCellValueFactory(new PropertyValueFactory<>("accelX"));
        colY.setCellValueFactory(new PropertyValueFactory<>("accelY"));
        colZ.setCellValueFactory(new PropertyValueFactory<>("accelZ"));
        colTime.setCellValueFactory(new PropertyValueFactory<>("timeReceive"));

        // Formater la colonne de temps
        colTime.setCellFactory(column -> new TableCell<arg_agit, LocalDateTime>() {
            @Override
            protected void updateItem(LocalDateTime item, boolean empty) {
                super.updateItem(item, empty);
                if (empty || item == null) {
                    setText(null);
                } else {
                    setText(item.format(formatter));
                }
            }
        });
    }

    @FXML
    public void refreshData() {
        try {
            data.clear();
            data.addAll(service.showAll());
            tableArgAgit.setItems(data);
            statusLabel.setText("Données chargées avec succès");
        } catch (SQLException e) {
            showError("Erreur lors du chargement des données", e.getMessage());
        }
    }

    private void showError(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
        statusLabel.setText("Erreur: " + message);
    }
}
