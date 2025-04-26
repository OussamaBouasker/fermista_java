package tn.fermista.controllers;

import tn.fermista.models.Commande;
import tn.fermista.models.Livraison;
import tn.fermista.services.ServiceCommande;

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

public class CommandeListViewController implements Initializable {

    @FXML private TableView<Commande> commandesTable;
    @FXML private TableColumn<Commande, Integer> idColumn;
    @FXML private TableColumn<Commande, Date> dateColumn;
    @FXML private TableColumn<Commande, String> statutColumn;
    @FXML private TableColumn<Commande, Integer> montantColumn;
    @FXML private TableColumn<Commande, Livraison> livraisonColumn;

    private ServiceCommande serviceCommande = new ServiceCommande();
    private ObservableList<Commande> commandeList = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        configureTableColumns();
        refreshCommandeList();
    }

    private void configureTableColumns() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("date_commander"));
        statutColumn.setCellValueFactory(new PropertyValueFactory<>("statut"));
        montantColumn.setCellValueFactory(new PropertyValueFactory<>("montant_total"));
        livraisonColumn.setCellValueFactory(new PropertyValueFactory<>("livcom_id"));
    }

    @FXML
    public void refreshCommandeList() {
        try {
            List<Commande> commandes = serviceCommande.showAll();
            commandeList.clear();
            commandeList.addAll(commandes);
            commandesTable.setItems(commandeList);
        } catch (Exception e) {
            showAlert("Erreur", "Erreur lors du chargement des commandes: " + e.getMessage());
        }
    }

    @FXML
    private void handleBackToManager() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/CrudCommande.fxml"));
            Parent root = loader.load();
            
            Stage stage = (Stage) commandesTable.getScene().getWindow();
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