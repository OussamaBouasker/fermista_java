package tn.fermista.controllers;

import com.mysql.cj.jdbc.MysqlSQLXML;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.MenuItem;
import javafx.stage.Stage;

import java.io.IOException;

public class NavbarController {

    @FXML
    private void handleHome(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/HomePage.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Erreur lors du chargement de HomePage.fxml: " + e.getMessage());
        }
    }

    @FXML
    private void handleShop() {
        System.out.println("Navigating to Shop");
    }

    @FXML
    private void handleCollier() {
        System.out.println("Navigating to Collier");
    }

    @FXML
    private void handleMesVaches() {
        System.out.println("Navigating to Mes Vaches");
    }

    @FXML
    private void handleMesColliers() {
        System.out.println("Navigating to Mes Colliers");
    }

    @FXML
    private void handleWorkshops() {
        System.out.println("Navigating to Workshops");
    }

    @FXML
    public void handleControlMedical(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ControlMedicalFront.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Erreur lors du chargement de ControlMedicalFront.fxml: " + e.getMessage());
        }
    }

    @FXML
    private void handleRendezVous() {
        System.out.println("Navigating to Rendez Vous");
    }

    @FXML
    private void handleProduits() {
        System.out.println("Navigating to Produits");
    }

    @FXML
    private void handleLogin() {
        System.out.println("Logging in...");
    }

    @FXML
    private void handleLogout() {
        System.out.println("Logging out...");
    }

    @FXML
    private void handleReclamations() {
        System.out.println("Navigating to Réclamations");
    }

    public void handleListeRendezVous(ActionEvent actionEvent) {
        System.out.println("Navigating to ListeRendezVous");

    }
}
