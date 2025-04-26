package tn.fermista.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.MenuItem;

public class NavbarController {

    @FXML
    private void handleHome() {
        System.out.println("Navigating to Home");
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
    private void handleControlMedical() {
        System.out.println("Navigating to Control Medical");
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
