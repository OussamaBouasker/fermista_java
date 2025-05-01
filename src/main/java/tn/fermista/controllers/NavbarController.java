package tn.fermista.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import tn.fermista.models.Roles;
import tn.fermista.models.User;
import tn.fermista.utils.UserSession;

import java.io.IOException;

public class NavbarController {

    @FXML
    private MenuButton userMenu;

    @FXML
    private Text userNameText;


    @FXML
    public void initialize() {
        if (UserSession.getCurrentUser() == null) {
            User loadedUser = UserSession.loadUser();
            UserSession.setCurrentUser(loadedUser);
        }
        updateMenuItems();
    }

    public void updateMenuItems() {
        userMenu.getItems().clear();
        User currentUser = UserSession.getCurrentUser();

        // Ajoute ton print ici directement
        // Ajoute ton print ici directement
        if (currentUser != null) {
            System.out.println("Role actuel de l'utilisateur : " + currentUser.getRoles());
        }

        if (currentUser == null) {
            // Personne n'est connecté
            MenuItem loginItem = new MenuItem("Se connecter");
            loginItem.setOnAction(this::handleLogin);
            userMenu.getItems().add(loginItem);
            userNameText.setText("");
        } else {
            // Utilisateur connecté
            MenuItem reclamationsItem = new MenuItem("Réclamations");
            reclamationsItem.setOnAction(this::handleReclamations);
            userMenu.getItems().add(reclamationsItem);

            // Vérifier si c'est un admin
            if (currentUser.getRoles() == Roles.ROLE_ADMIN) {
                MenuItem dashboardItem = new MenuItem("Dashboard");
                dashboardItem.setOnAction(this::handleDashboard);
                userMenu.getItems().add(dashboardItem);
            }

            MenuItem logoutItem = new MenuItem("Se déconnecter");
            logoutItem.setOnAction(this::handleLogout);
            userMenu.getItems().add(logoutItem);

            userNameText.setText("Hello, " + currentUser.getFirstName() + " " + currentUser.getLastName());
        }
    }


    @FXML
    private void handleLogin(ActionEvent event) {
        navigateTo("/login.fxml");
    }

    @FXML
    private void handleLogout(ActionEvent event) {
        UserSession.clearCurrentUser();
        UserSession.clearSavedUser();
        updateMenuItems();
        navigateTo("/HomePage.fxml");
    }

    @FXML
    private void handleReclamations(ActionEvent event) {
        navigateTo("/AjoutReclamation.fxml"); // À toi de mettre le bon chemin
    }

    @FXML
    private void handleDashboard(ActionEvent event) {
        navigateTo("/DashboardTemplate.fxml");
    }

    private void navigateTo(String fxmlPath) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource(fxmlPath));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            Stage stage = (Stage) userMenu.getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // ========== Navigation autres boutons ==========
    @FXML private void handleHome() { navigateTo("/HomePage.fxml"); }
    @FXML private void handleShop() { navigateTo("/ShopPage.fxml"); }
    @FXML private void handleCollier() { navigateTo("/CollierPage.fxml"); }
    @FXML private void handleWorkshops() { navigateTo("/Front-office/showworkshops.fxml"); }
    @FXML private void handleControlMedical() { navigateTo("/ControlMedicalPage.fxml"); }
    @FXML private void handleRendezVous() { navigateTo("/RendezVousPage.fxml"); }
    @FXML private void handleListeRendezVous(ActionEvent actionEvent) { navigateTo("/ListeRendezVousPage.fxml"); }
    @FXML private void handleProduits() { navigateTo("/ProduitsPage.fxml"); }
    @FXML private void handleMesVaches() { navigateTo("/MesVachesPage.fxml"); }
    @FXML private void handleMesColliers() { navigateTo("/MesColliersPage.fxml"); }
}