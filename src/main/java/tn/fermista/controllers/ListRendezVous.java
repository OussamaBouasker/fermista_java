package tn.fermista.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;
import tn.fermista.models.RendezVous;
import tn.fermista.models.User;
import tn.fermista.models.Veterinaire;
import tn.fermista.models.Roles;
import tn.fermista.services.ServiceRendezVous;
import tn.fermista.utils.UserSession;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;

public class ListRendezVous implements Initializable {

    @FXML
    private TableView<RendezVous> rendezVousTable;
    
    @FXML
    private TableColumn<RendezVous, java.sql.Date> dateColumn;
    
    @FXML
    private TableColumn<RendezVous, java.sql.Time> heureColumn;
    
    @FXML
    private TableColumn<RendezVous, String> sexColumn;
    
    @FXML
    private TableColumn<RendezVous, String> causeColumn;
    
    @FXML
    private TableColumn<RendezVous, String> statusColumn;
    
    @FXML
    private TableColumn<RendezVous, Void> actionsColumn;
    
    private ServiceRendezVous serviceRendezVous;
    private ObservableList<RendezVous> rendezVousList;
    
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        serviceRendezVous = new ServiceRendezVous();
        rendezVousList = FXCollections.observableArrayList();
        
        // Configure table columns
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        heureColumn.setCellValueFactory(new PropertyValueFactory<>("heure"));
        sexColumn.setCellValueFactory(new PropertyValueFactory<>("sex"));
        causeColumn.setCellValueFactory(new PropertyValueFactory<>("cause"));
        statusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
        
        // Configure actions column with buttons
        setupActionsColumn();
        
        // Load data
        loadRendezVous();
    }
    
    private void setupActionsColumn() {
        actionsColumn.setCellFactory(new Callback<TableColumn<RendezVous, Void>, TableCell<RendezVous, Void>>() {
            @Override
            public TableCell<RendezVous, Void> call(final TableColumn<RendezVous, Void> param) {
                final TableCell<RendezVous, Void> cell = new TableCell<RendezVous, Void>() {
                    private final Button accepterBtn = new Button("Accepter");
                    private final Button modifierBtn = new Button("Modifier");
                    private final Button refuserBtn = new Button("Refuser");
                    private final HBox buttons = new HBox(5, accepterBtn, modifierBtn, refuserBtn);
                    
                    {
                        accepterBtn.getStyleClass().add("button-accept");
                        modifierBtn.getStyleClass().add("button-edit");
                        refuserBtn.getStyleClass().add("button-reject");
                        
                        accepterBtn.setOnAction(event -> {
                            RendezVous rdv = getTableView().getItems().get(getIndex());
                            accepterRendezVous(rdv);
                        });
                        
                        modifierBtn.setOnAction(event -> {
                            RendezVous rdv = getTableView().getItems().get(getIndex());
                            modifierRendezVous(rdv);
                        });
                        
                        refuserBtn.setOnAction(event -> {
                            RendezVous rdv = getTableView().getItems().get(getIndex());
                            refuserRendezVous(rdv);
                        });
                    }
                    
                    @Override
                    protected void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                            setText(null);
                        } else {
                            setGraphic(buttons);
                            setText(null);
                        }
                    }
                };
                return cell;
            }
        });
    }
    
    private void loadRendezVous() {
        try {
            User currentUser = UserSession.getCurrentUser();
            
            if (currentUser != null) {
                // Check if the user has the ROLE_VETERINAIR role
                boolean isVeterinaire = false;
                
                // Check if the user has the ROLE_VETERINAIR role
                if (currentUser.getRoles() == Roles.ROLE_VETERINAIR) {
                    isVeterinaire = true;
                }
                
                if (isVeterinaire) {
                    List<RendezVous> allRendezVous = serviceRendezVous.showAll();
                    
                    // Filter appointments for the current veterinarian
                    List<RendezVous> filteredRendezVous = allRendezVous.stream()
                            .filter(rdv -> rdv.getVeterinaire().getId().equals(currentUser.getId()))
                            .collect(Collectors.toList());
                    
                    // Ensure all appointments have a status, default to "en attente" if not set
                    for (RendezVous rdv : filteredRendezVous) {
                        if (isStatusEmpty(rdv.getStatus())) {
                            rdv.setStatus("en attente");
                            // Update the appointment in the database with the default status
                            serviceRendezVous.update(rdv);
                        }
                    }
                    
                    rendezVousList.setAll(filteredRendezVous);
                    rendezVousTable.setItems(rendezVousList);
                } else {
                    showAlert("Erreur", "Vous devez être connecté en tant que vétérinaire pour voir cette page.");
                }
            } else {
                showAlert("Erreur", "Aucun utilisateur connecté.");
            }
        } catch (SQLException e) {
            showAlert("Erreur", "Impossible de charger les rendez-vous: " + e.getMessage());
        }
    }
    
    private boolean isStatusEmpty(String status) {
        return status == null || status.trim().isEmpty();
    }
    
    private void accepterRendezVous(RendezVous rdv) {
        try {
            rdv.setStatus("accepté");
            serviceRendezVous.update(rdv);
            loadRendezVous(); // Refresh the table
            showAlert("Succès", "Le rendez-vous a été accepté.");
        } catch (SQLException e) {
            showAlert("Erreur", "Impossible d'accepter le rendez-vous: " + e.getMessage());
        }
    }
    
    private void modifierRendezVous(RendezVous rdv) {
        try {
            // Load the popup FXML
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ModifierRendezVousPopup.fxml"));
            Scene scene = new Scene(loader.load());
            
            // Get the controller and set the rendez-vous
            ModifierRendezVousPopupController controller = loader.getController();
            controller.setRendezVous(rdv, this);
            
            // Create and show the popup
            Stage popupStage = new Stage();
            popupStage.setTitle("Modifier le rendez-vous");
            popupStage.setScene(scene);
            popupStage.initModality(Modality.APPLICATION_MODAL);
            popupStage.showAndWait();
            
        } catch (IOException e) {
            showAlert("Erreur", "Impossible d'ouvrir la fenêtre de modification: " + e.getMessage());
        }
    }
    
    public void updateRendezVous(RendezVous rdv) {
        try {
            serviceRendezVous.update(rdv);
            loadRendezVous(); // Refresh the table
            showAlert("Succès", "Le rendez-vous a été modifié avec succès.");
        } catch (SQLException e) {
            showAlert("Erreur", "Impossible de modifier le rendez-vous: " + e.getMessage());
        }
    }
    
    private void refuserRendezVous(RendezVous rdv) {
        try {
            rdv.setStatus("refusé");
            serviceRendezVous.update(rdv);
            loadRendezVous(); // Refresh the table
            showAlert("Succès", "Le rendez-vous a été refusé.");
        } catch (SQLException e) {
            showAlert("Erreur", "Impossible de refuser le rendez-vous: " + e.getMessage());
        }
    }
    
    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}