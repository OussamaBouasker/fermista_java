package tn.fermista.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.AnchorPane;
import javafx.stage.Stage;
import java.io.IOException;
import javafx.scene.control.MenuButton;

public class NavbarController {

    @FXML
    private AnchorPane rootPane;

    @FXML
    private MenuButton userMenu;

    private void loadPage(String fxmlPath, String pageName) {
        System.out.println("Navigation vers " + pageName);
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();
            Scene scene = rootPane.getScene();
            scene.setRoot(root);
        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText("Erreur de navigation");
            alert.setContentText("Impossible de charger la page " + pageName + " : " + e.getMessage());
            alert.showAndWait();
        }
    }

    @FXML
    private void handleHome() {
        loadPage("/Home.fxml", "Accueil");
    }

    @FXML
    private void handleShop() {
        loadPage("/Shop.fxml", "Boutique");
    }

    @FXML
    private void handleCollier() {
        loadPage("/Collier.fxml", "Gestion des colliers");
    }

    @FXML
    private void handleWorkshops() {
        loadPage("/Workshops.fxml", "Ateliers");
    }

    @FXML
    private void handleControlMedical() {
        loadPage("/ControlMedical.fxml", "Contrôle Médical");
    }

    @FXML
    private void handleRendezVous() {
        loadPage("/RendezVous.fxml", "Rendez-vous");
    }

    @FXML
    private void handleListeRendezVous(ActionEvent actionEvent) {
        loadPage("/ListeRendezVous.fxml", "Liste des Rendez-vous");
    }

    @FXML
    private void handleProduits() {
        loadPage("/Produits.fxml", "Produits");
    }

    @FXML
    private void handleMesVaches() {
        loadPage("/FrontListeVache.fxml", "Mes Vaches");
    }

    @FXML
    private void handleMesColliers() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/FrontListeCollier.fxml"));
            Parent root = loader.load();
            
            // Obtenir la scène actuelle
            Scene currentScene = rootPane.getScene();
            if (currentScene != null) {
                // Remplacer le contenu de la scène
                currentScene.setRoot(root);
            } else {
                // Si la scène n'existe pas, créer une nouvelle scène
                Scene scene = new Scene(root);
                Stage stage = (Stage) rootPane.getScene().getWindow();
                stage.setScene(scene);
            }
        } catch (IOException e) {
            e.printStackTrace();
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur de Navigation");
            alert.setHeaderText(null);
            alert.setContentText("Erreur lors du chargement de la liste des colliers : " + e.getMessage());
            alert.showAndWait();
        }
    }

    @FXML
    private void handleLogin() {
        loadPage("/Login.fxml", "Connexion");
    }

    @FXML
    private void handleLogout() {
        Alert confirmation = new Alert(Alert.AlertType.CONFIRMATION);
        confirmation.setTitle("Déconnexion");
        confirmation.setHeaderText("Confirmation de déconnexion");
        confirmation.setContentText("Êtes-vous sûr de vouloir vous déconnecter ?");
        
        confirmation.showAndWait().ifPresent(response -> {
            if (response == javafx.scene.control.ButtonType.OK) {
                // TODO: Nettoyer les données de session si nécessaire
                loadPage("/Login.fxml", "Connexion");
            }
        });
    }

    @FXML
    private void handleReclamations() {
        loadPage("/Reclamations.fxml", "Réclamations");
    }
}
