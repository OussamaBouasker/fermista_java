package tn.fermista.controllers;

import tn.fermista.models.Produit;
import tn.fermista.services.ServiceProduit;

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
import java.util.List;
import java.util.ResourceBundle;

public class ProductListViewController implements Initializable {

    @FXML private TableView<Produit> productsTable;
    @FXML private TableColumn<Produit, Integer> idColumn;
    @FXML private TableColumn<Produit, String> nomColumn;
    @FXML private TableColumn<Produit, String> descriptionColumn;
    @FXML private TableColumn<Produit, Integer> prixColumn;
    @FXML private TableColumn<Produit, String> categorieColumn;
    @FXML private TableColumn<Produit, String> etatColumn;

    private ServiceProduit serviceProduit = new ServiceProduit();
    private ObservableList<Produit> productList = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        configureTableColumns();
        refreshProductList();
    }

    private void configureTableColumns() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        nomColumn.setCellValueFactory(new PropertyValueFactory<>("nom"));
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        prixColumn.setCellValueFactory(new PropertyValueFactory<>("prix"));
        categorieColumn.setCellValueFactory(new PropertyValueFactory<>("categorie"));
        etatColumn.setCellValueFactory(new PropertyValueFactory<>("etat"));
    }

    @FXML
    public void refreshProductList() {
        try {
            List<Produit> products = serviceProduit.showAll();
            productList.clear();
            productList.addAll(products);
            productsTable.setItems(productList);
        } catch (Exception e) {
            showAlert("Erreur", "Erreur lors du chargement des produits: " + e.getMessage());
        }
    }

    @FXML
    private void handleBackToManager() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/CrudProduit.fxml"));
            Parent root = loader.load();
            
            Stage stage = (Stage) productsTable.getScene().getWindow();
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