package tn.fermista.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.MenuButton;
import javafx.scene.control.MenuItem;
import javafx.scene.layout.HBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import tn.fermista.models.Roles;
import tn.fermista.models.User;
import tn.fermista.utils.UserSession;

import java.io.IOException;

public class NavbarController {

    public Button homeButton;
    public Button workshopsButton;
    public Button controleMedicalButton;
    public Button rendezVousButton;
    public Button produitsButton;
    public Button listeRendezVousButton;
    public MenuButton suivisMedicalMenu;
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
        updateNavigationButtons();
    }

    @FXML
    private HBox navigationBox;

    private void updateNavigationButtons() {
        User currentUser = UserSession.getCurrentUser();
        
        // Par défaut, cacher tous les boutons sauf Home, Produits et Workshops
        homeButton.setVisible(true);
        homeButton.setManaged(true);
        produitsButton.setVisible(true);
        produitsButton.setManaged(true);
        workshopsButton.setVisible(true);
        workshopsButton.setManaged(true);
        
        controleMedicalButton.setVisible(false);
        controleMedicalButton.setManaged(false);
        rendezVousButton.setVisible(false);
        rendezVousButton.setManaged(false);
        listeRendezVousButton.setVisible(false);
        listeRendezVousButton.setManaged(false);
        suivisMedicalMenu.setVisible(false);
        suivisMedicalMenu.setManaged(false);

        if (currentUser != null) {
            switch (currentUser.getRoles()) {
                case ROLE_ADMIN:
                    // Afficher tous les boutons pour l'admin
                    controleMedicalButton.setVisible(true);
                    controleMedicalButton.setManaged(true);
                    rendezVousButton.setVisible(true);
                    rendezVousButton.setManaged(true);
                    listeRendezVousButton.setVisible(true);
                    listeRendezVousButton.setManaged(true);
                    suivisMedicalMenu.setVisible(true);
                    suivisMedicalMenu.setManaged(true);
                    break;
                    
                case ROLE_AGRICULTOR:
                    // Boutons spécifiques pour l'agriculteur
                    rendezVousButton.setVisible(true);
                    rendezVousButton.setManaged(true);
                    suivisMedicalMenu.setVisible(true);
                    suivisMedicalMenu.setManaged(true);
                    break;
                    
                case ROLE_VETERINAIR:
                    // Boutons spécifiques pour le vétérinaire
                    controleMedicalButton.setVisible(true);
                    controleMedicalButton.setManaged(true);
                    listeRendezVousButton.setVisible(true);
                    listeRendezVousButton.setManaged(true);
                    break;
            }
        }

        // Mettre à jour l'espacement
        navigationBox.setSpacing(0.0);
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
        updateNavigationButtons(); // Mettre à jour les boutons après déconnexion
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

    @FXML private void handleWorkshops() { navigateTo("/Front-Office/showworkshops.fxml"); }
    @FXML private void handleControlMedical() { navigateTo("/ControlMedicalFront.fxml"); }
    @FXML private void handleRendezVous() { navigateTo("/ListVet.fxml"); }
    @FXML private void handleListeRendezVous(ActionEvent actionEvent) { navigateTo("/ListRendezVous.fxml"); }
    @FXML private void handleProduits() { navigateTo("/ProductShopView.fxml"); }
    @FXML private void handleMesVaches() { navigateTo("/FrontListeVache.fxml"); }
    @FXML private void handleMesColliers() { navigateTo("/FrontListeCollier.fxml"); }
}
