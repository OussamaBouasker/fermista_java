package tn.fermista.controllers;

import tn.fermista.models.Livraison;
import tn.fermista.services.ServiceLivraison;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;

public class LivraisonListViewController implements Initializable {

    @FXML private TableView<Livraison> livraisonsTable;
    @FXML private TableColumn<Livraison, Integer> idColumn;
    @FXML private TableColumn<Livraison, Date> dateColumn;
    @FXML private TableColumn<Livraison, String> livreurColumn;
    @FXML private TableColumn<Livraison, String> lieuColumn;

    private ServiceLivraison serviceLivraison = new ServiceLivraison();
    private ObservableList<Livraison> livraisonList = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        configureTableColumns();
        refreshLivraisonList();
    }

    private void configureTableColumns() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        livreurColumn.setCellValueFactory(new PropertyValueFactory<>("livreur"));
        lieuColumn.setCellValueFactory(new PropertyValueFactory<>("lieu"));
    }

    @FXML
    public void refreshLivraisonList() {
        try {
            List<Livraison> livraisons = serviceLivraison.showAll();
            livraisonList.clear();
            livraisonList.addAll(livraisons);
            livraisonsTable.setItems(livraisonList);
        } catch (Exception e) {
            showAlert("Erreur", "Erreur lors du chargement des livraisons: " + e.getMessage());
        }
    }

    @FXML
    private void handleBackToManager() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/CrudLivraison.fxml"));
            Parent root = loader.load();
            
            Stage stage = (Stage) livraisonsTable.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            showAlert("Erreur", "Impossible de retourner à la page de gestion: " + e.getMessage());
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
} 