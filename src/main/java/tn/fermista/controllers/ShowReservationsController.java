package tn.fermista.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.stage.Stage;
import tn.fermista.models.Reservation;
import tn.fermista.models.User;
import tn.fermista.services.ServiceReservation;
import tn.fermista.services.ServiceClient;
import tn.fermista.services.ServiceAgriculteur;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.ResourceBundle;

public class ShowReservationsController implements Initializable {

    @FXML private TableView<Reservation> reservationTable;
    @FXML private TableColumn<Reservation, Integer> idColumn;
    @FXML private TableColumn<Reservation, String> dateColumn;
    @FXML private TableColumn<Reservation, String> statusColumn;
    @FXML private TableColumn<Reservation, String> prixColumn;
    @FXML private TableColumn<Reservation, String> workshopColumn;
    @FXML private TableColumn<Reservation, String> userColumn;
    @FXML private TableColumn<Reservation, String> emailColumn;
    @FXML private TableColumn<Reservation, Integer> numTelColumn;
    @FXML private TableColumn<Reservation, Void> actionsColumn;
    @FXML private TextField searchField;

    private ServiceReservation serviceReservation;
    private ServiceClient serviceClient;
    private ServiceAgriculteur serviceAgriculteur;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        serviceReservation = new ServiceReservation();
        serviceClient = new ServiceClient();
        serviceAgriculteur = new ServiceAgriculteur();

        // Initialize columns
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("reservationDate"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        prixColumn.setCellValueFactory(new PropertyValueFactory<>("prix"));
        workshopColumn.setCellValueFactory(cellData ->
            new javafx.beans.property.SimpleStringProperty(
                cellData.getValue().getWorkshop() != null ?
                cellData.getValue().getWorkshop().getTitre() : ""));
        userColumn.setCellValueFactory(cellData ->
            new javafx.beans.property.SimpleStringProperty(
                cellData.getValue().getUser() != null ?
                cellData.getValue().getUser().getFirstName() + " " + cellData.getValue().getUser().getLastName() : ""));
        emailColumn.setCellValueFactory(new PropertyValueFactory<>("email"));
        numTelColumn.setCellValueFactory(new PropertyValueFactory<>("numTel"));

        // Add action buttons
        actionsColumn.setCellFactory(param -> new TableCell<>() {
            private final Button editBtn = new Button("Modifier");
            private final Button deleteBtn = new Button("Supprimer");
            private final HBox buttons = new HBox(5, editBtn, deleteBtn);

            {
                editBtn.setOnAction(event -> {
                    Reservation reservation = getTableView().getItems().get(getIndex());
                    handleEditReservation(reservation);
                });

                deleteBtn.setOnAction(event -> {
                    Reservation reservation = getTableView().getItems().get(getIndex());
                    handleDeleteReservation(reservation);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : buttons);
            }
        });

        // Add search functionality with debounce
        searchField.textProperty().addListener((observable, oldValue, newValue) -> {
            filterTable(newValue);
        });

        // Load initial data
        refreshTable();
    }

    @FXML
    private void handleAddReservation() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AddReservation.fxml"));
            Scene scene = new Scene(loader.load());
            Stage stage = new Stage();
            stage.setTitle("Ajouter une réservation");
            stage.setScene(scene);

            AddReservationController controller = loader.getController();
            controller.setParentController(this);

            stage.show();
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de l'ouverture du formulaire: " + e.getMessage());
        }
    }

    private void handleEditReservation(Reservation reservation) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AddReservation.fxml"));
            Scene scene = new Scene(loader.load());
            Stage stage = new Stage();
            stage.setTitle("Modifier la réservation");
            stage.setScene(scene);

            AddReservationController controller = loader.getController();
            controller.setParentController(this);
            controller.setReservationForEdit(reservation);

            stage.show();
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de l'ouverture du formulaire: " + e.getMessage());
        }
    }

    private void handleDeleteReservation(Reservation reservation) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation");
        alert.setHeaderText("Supprimer la réservation");
        alert.setContentText("Êtes-vous sûr de vouloir supprimer cette réservation ?");

        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                serviceReservation.delete(reservation);
                refreshTable();
                showAlert(Alert.AlertType.INFORMATION, "Succès", "Réservation supprimée avec succès!");
            } catch (SQLException e) {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de la suppression: " + e.getMessage());
            }
        }
    }

    public void refreshTable() {
        try {
            List<Reservation> reservations = serviceReservation.showAll();
            reservationTable.setItems(FXCollections.observableArrayList(reservations));
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors du chargement des réservations: " + e.getMessage());
        }
    }

    private void filterTable(String searchText) {
        try {
            List<Reservation> allReservations = serviceReservation.showAll();
            List<Reservation> filteredReservations = new ArrayList<>();

            if (searchText == null || searchText.isEmpty()) {
                reservationTable.setItems(FXCollections.observableArrayList(allReservations));
                return;
            }

            String searchLower = searchText.toLowerCase();
            
            for (Reservation reservation : allReservations) {
                if (matchesSearch(reservation, searchLower)) {
                    filteredReservations.add(reservation);
                }
            }

            reservationTable.setItems(FXCollections.observableArrayList(filteredReservations));
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de la recherche: " + e.getMessage());
        }
    }

    private boolean matchesSearch(Reservation reservation, String searchText) {
        return String.valueOf(reservation.getId()).contains(searchText) ||
               reservation.getReservationDate().toString().toLowerCase().contains(searchText) ||
               reservation.getStatus().toLowerCase().contains(searchText) ||
               String.valueOf(reservation.getPrix()).contains(searchText) ||
               reservation.getEmail().toLowerCase().contains(searchText) ||
               String.valueOf(reservation.getNumTel()).contains(searchText) ||
               (reservation.getWorkshop() != null && 
                reservation.getWorkshop().getTitre().toLowerCase().contains(searchText)) ||
               (reservation.getUser() != null &&
                (reservation.getUser().getFirstName().toLowerCase().contains(searchText) ||
                 reservation.getUser().getLastName().toLowerCase().contains(searchText)));
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
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



    public void ControlMedicalShow(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ControlMedicalShow.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Erreur lors du chargement de ShowWorkshops.fxml: " + e.getMessage());
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



}