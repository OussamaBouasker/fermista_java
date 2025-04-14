package tn.fermista.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import tn.fermista.models.Reclamation;
import tn.fermista.services.ServiceReclamation;


import java.sql.SQLException;
import java.time.LocalDateTime;

public class ReclamationController {

    @FXML
    private TableView<Reclamation> reclamationTable;

    @FXML
    private TableColumn<Reclamation, Integer> idColumn;

    @FXML
    private TableColumn<Reclamation, String> titreColumn;

    @FXML
    private TableColumn<Reclamation, String> descriptionColumn;

    @FXML
    private TableColumn<Reclamation, String> statusColumn;

    @FXML
    private TableColumn<Reclamation, LocalDateTime> dateSoumissionColumn;

    @FXML
    private TableColumn<Reclamation, String> userColumn;

    private ServiceReclamation serviceReclamation;

    @FXML
    public void initialize() {
        // Initialize the service
        serviceReclamation = new ServiceReclamation();

        // Set up the columns (optional, since FXML already defines them)
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        titreColumn.setCellValueFactory(new PropertyValueFactory<>("titre"));
        descriptionColumn.setCellValueFactory(new PropertyValueFactory<>("description"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        dateSoumissionColumn.setCellValueFactory(new PropertyValueFactory<>("dateSoumission"));
        userColumn.setCellValueFactory(cellData -> {
            Reclamation reclamation = cellData.getValue();
            return new javafx.beans.property.SimpleStringProperty(
                    reclamation.getUser() != null
                            ? reclamation.getUser().getFirstName() + " " + reclamation.getUser().getLastName()
                            : "N/A"
            );
        });

        // Load data into the TableView
        loadReclamations();
    }

        private void loadReclamations() {
            try {
                reclamationTable.getItems().setAll(serviceReclamation.showAll());
            } catch (SQLException e) {
                e.printStackTrace(); // Tu peux aussi afficher une alerte à l'utilisateur
            }
        }
}
