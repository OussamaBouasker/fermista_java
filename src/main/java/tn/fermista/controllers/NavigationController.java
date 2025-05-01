package tn.fermista.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import tn.fermista.models.User;
import tn.fermista.services.ServiceAgriculteur;
import tn.fermista.services.ServiceClient;
import tn.fermista.services.ServiceFormateur;
import tn.fermista.services.ServiceVeterinaire;

import tn.fermista.services.ServiceReclamation;
import tn.fermista.utils.UserSession;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

public class NavigationController implements Initializable {
    public Button btn_workbench11212;
    @FXML
    private Button btn_workbench11;

    @FXML
    private Button btn_workbench113;

    @FXML
    private Button logoutButton;

    @FXML
    private Label userNameLabel;

    private static User currentUser;

    public static void setCurrentUser(User user) {
        currentUser = user;
    }

    @FXML
    private Label nbrFormateur;
    @FXML
    private Label nbrClient;
    @FXML
    private Label nbreAgriculteur;
    @FXML
    private Label nbrReclamation;
    @FXML
    private Label nbrVeterinaire;
    @FXML
    private Label nbrePendingReclamation;
    @FXML
    private Label nbreCancelledReclamation;
    @FXML
    private Label nbreConfirmedReclamation;

    private ServiceFormateur serviceFormateur;
    private ServiceClient serviceClient;
    private ServiceAgriculteur serviceAgriculteur;
    private ServiceReclamation serviceReclamation;
    private ServiceVeterinaire serviceveterinaire;

    public static void naviguerVersSuiviMedical(Button sourceButton) {
        try {
            FXMLLoader loader = new FXMLLoader(NavigationController.class.getResource("/choixvachecollier.fxml"));
            Parent root = loader.load();
            Scene scene = sourceButton.getScene();
            scene.setRoot(root);
        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText("Erreur de navigation");
            alert.setContentText("Impossible d'ouvrir la page de suivi médical. Veuillez vérifier que le fichier choixvachecollier.fxml existe dans le dossier resources.");
            alert.showAndWait();
            e.printStackTrace(); // Pour le débogage
        }
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Initialize services
        serviceFormateur = new ServiceFormateur();
        serviceClient = new ServiceClient();
        serviceAgriculteur = new ServiceAgriculteur();
        serviceReclamation = new ServiceReclamation();
        serviceveterinaire = new ServiceVeterinaire();
        try {
            // Nombre de formateurs
            int formateurCount = serviceFormateur.rechercher().size();
            nbrFormateur.setText(String.valueOf(formateurCount));

            // Nombre de clients
            int clientCount = serviceClient.rechercher().size();
            nbrClient.setText(String.valueOf(clientCount));

            // Nombre d'agriculteurs
            int agriculteurCount = serviceAgriculteur.rechercher().size();
            nbreAgriculteur.setText(String.valueOf(agriculteurCount));

            int veterinaireCount = serviceveterinaire.rechercher().size();
            nbrVeterinaire.setText(String.valueOf(veterinaireCount));

            // Nombre de réclamations
            // Count reclamations by status
            int pendingCount = serviceReclamation.countReclamationsByStatus("pending");
            nbrePendingReclamation.setText(String.valueOf(pendingCount));

            int cancelledCount = serviceReclamation.countReclamationsByStatus("canceled");
            nbreCancelledReclamation.setText(String.valueOf(cancelledCount));

            int confirmedCount = serviceReclamation.countReclamationsByStatus("confirmed");
            nbreConfirmedReclamation.setText(String.valueOf(confirmedCount));

            // Total reclamations count
            int reclamationCount = serviceReclamation.showAll().size();
            nbrReclamation.setText(String.valueOf(reclamationCount));

        } catch (SQLException e) {
            e.printStackTrace();
            // Set default values in case of error
            nbrePendingReclamation.setText("0");
            nbreCancelledReclamation.setText("0");
            nbreConfirmedReclamation.setText("0");
            nbrReclamation.setText("0");
        }

        // Mettre à jour les labels avec les nombres depuis la BD
        try {
            // Nombre de formateurs
            int formateurCount = serviceFormateur.rechercher().size();
            nbrFormateur.setText(String.valueOf(formateurCount));

            // Nombre de clients
            int clientCount = serviceClient.rechercher().size();
            nbrClient.setText(String.valueOf(clientCount));

            // Nombre d'agriculteurs
            int agriculteurCount = serviceAgriculteur.rechercher().size();
            nbreAgriculteur.setText(String.valueOf(agriculteurCount));

            int veterinaireCount = serviceveterinaire.rechercher().size();
            nbrVeterinaire.setText(String.valueOf(veterinaireCount));

            // Nombre de réclamations
            int reclamationCount = serviceReclamation.showAll().size();
            nbrReclamation.setText(String.valueOf(reclamationCount));
        } catch (SQLException e) {
            e.printStackTrace();
            // En cas d'erreur, afficher un message ou mettre une valeur par défaut
            nbrFormateur.setText("0");
            nbrClient.setText("0");
            nbreAgriculteur.setText("0");
            nbrReclamation.setText("0");
        }

        // Garder le reste du code initialize() existant
        if (btn_workbench11 != null) {
            btn_workbench11.setOnAction(event -> navigateToCrudUser());
        }
        if (btn_workbench113 != null) {
            btn_workbench113.setOnAction(event -> navigateToCrudReclamation());
        }

        User currentUser = UserSession.getCurrentUser();

// Mettre à jour le label de l'utilisateur
        if (currentUser != null && userNameLabel != null) {
            userNameLabel.setText("Hello, " + currentUser.getFirstName() );
        }
    }

    @FXML
    private void handleLogout() {
        try {
            // Réinitialiser l'utilisateur courant
            currentUser = null;

            // Rediriger vers la page de login
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/login.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            Stage stage = (Stage) logoutButton.getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void navigateToCrudUser() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/CrudUser.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            Stage stage = (Stage) btn_workbench11.getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void navigateToCrudReclamation() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/CrudReclamation.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            Stage stage = (Stage) btn_workbench113.getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



    public void CrudReclamation(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/CrudReclamation.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Erreur lors du chargement de ShowWorkshops.fxml: " + e.getMessage());
        }
    }



    public void ControlMedicalShow(javafx.event.ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ControlMedicalShow.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Erreur lors du chargement de ControlMedicalShow.fxml: " + e.getMessage());
        }
    }

    public void ShowReservations(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ShowReservations.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Erreur lors du chargement de ShowReservations.fxml: " + e.getMessage());
        }
    }

    public void ShowWorkshops(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ShowWorkshops.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Erreur lors du chargement de ShowWorkshops.fxml: " + e.getMessage());
        }
    }



    public void DashboardTemplate(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/DashboardTemplate.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Erreur lors du chargement de ShowWorkshops.fxml: " + e.getMessage());
        }
    }

    public void choixvachecollier(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/choixvachecollier.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Erreur lors du chargement de ShowWorkshops.fxml: " + e.getMessage());
        }
    }

    public void CrudProduit(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/CrudProduit.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Erreur lors du chargement de ShowWorkshops.fxml: " + e.getMessage());
        }
    }

    public void CrudUser(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/CrudUser.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Erreur lors du chargement de ShowWorkshops.fxml: " + e.getMessage());
        }
    }
    @FXML
    private Button btn_home;

    @FXML
    private void NavigateToHomePage() {
        try {
            // Debug the path to ensure the resource is found
            String fxmlPath = "/HomePage.fxml";
            java.net.URL fxmlUrl = getClass().getResource(fxmlPath);
            if (fxmlUrl == null) {
                throw new IOException("Cannot find FXML file at path: " + fxmlPath);
            }

            // Load the FXML file
            Parent root = FXMLLoader.load(fxmlUrl);
            Stage stage = (Stage) btn_home.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur de navigation", "Impossible de charger la page d'accueil: " + e.getMessage());
        }
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.show();
    }

    public void StatTemplate(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/StatTemplate.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Erreur lors du chargement de StatTemplate.fxml: " + e.getMessage());
        }
    }

    public void test(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Statistiques_Evenement.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Erreur lors du chargement de ShowWorkshops.fxml: " + e.getMessage());
        }
    }
}