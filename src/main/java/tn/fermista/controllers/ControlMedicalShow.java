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
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableCell;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import tn.fermista.models.Consultation;
import tn.fermista.models.RapportMedical;
import tn.fermista.models.RendezVous;
import tn.fermista.models.Veterinaire;
import tn.fermista.models.Agriculteur;
import tn.fermista.services.ServiceConsultation;
import tn.fermista.services.ServiceRapportMedical;
import tn.fermista.services.ServiceRendezVous;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.sql.Date;
import java.sql.Time;
import java.util.ResourceBundle;
import javafx.beans.property.SimpleStringProperty;

public class ControlMedicalShow implements Initializable {
    @FXML
    private TableView<Consultation> consultationTable;

    @FXML
    private TableColumn<Consultation, Integer> idColumn;

    @FXML
    private TableColumn<Consultation, String> nomColumn;

    @FXML
    private TableColumn<Consultation, java.sql.Date> dateColumn;

    @FXML
    private TableColumn<Consultation, java.sql.Time> heureColumn;

    @FXML
    private TableColumn<Consultation, String> lieuColumn;

    @FXML
    private TableColumn<Consultation, Void> actionsColumn;

    @FXML
    private Button addConsultationBtn;

    @FXML
    private TableView<RapportMedical> rapportMedicalTable;

    @FXML
    private TableColumn<RapportMedical, Integer> rapportIdColumn;

    @FXML
    private TableColumn<RapportMedical, Integer> numColumn;

    @FXML
    private TableColumn<RapportMedical, String> raceColumn;

    @FXML
    private TableColumn<RapportMedical, String> historiqueColumn;

    @FXML
    private TableColumn<RapportMedical, String> casMedicalColumn;

    @FXML
    private TableColumn<RapportMedical, String> solutionColumn;

    @FXML
    private TableColumn<RapportMedical, Void> rapportActionsColumn;

    @FXML
    private Button addRapportBtn;

    @FXML
    private Button addRendezVousBtn;

    @FXML
    private TableView<RendezVous> rendezVousTable;
    @FXML
    private TableColumn<RendezVous, Integer> rdvIdColumn;
    @FXML
    private TableColumn<RendezVous, Date> rdvDateColumn;
    @FXML
    private TableColumn<RendezVous, Time> rdvHeureColumn;
    @FXML
    private TableColumn<RendezVous, String> rdvSexColumn;
    @FXML
    private TableColumn<RendezVous, String> rdvCauseColumn;
    @FXML
    private TableColumn<RendezVous, String> rdvStatusColumn;
    @FXML
    private TableColumn<RendezVous, Void> rdvActionsColumn;

    private final ServiceConsultation serviceConsultation = new ServiceConsultation();
    private final ServiceRapportMedical serviceRapportMedical = new ServiceRapportMedical();
    private final ServiceRendezVous serviceRendezVous = new ServiceRendezVous();
    private final ObservableList<Consultation> consultationList = FXCollections.observableArrayList();
    private final ObservableList<RapportMedical> rapportMedicalList = FXCollections.observableArrayList();
    private final ObservableList<RendezVous> rendezVousList = FXCollections.observableArrayList();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Initialize consultation table columns
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        nomColumn.setCellValueFactory(new PropertyValueFactory<>("nom"));
        dateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        heureColumn.setCellValueFactory(new PropertyValueFactory<>("heure"));
        lieuColumn.setCellValueFactory(new PropertyValueFactory<>("lieu"));

        // Initialize rapport medical table columns
        rapportIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        numColumn.setCellValueFactory(new PropertyValueFactory<>("num"));
        raceColumn.setCellValueFactory(new PropertyValueFactory<>("race"));
        historiqueColumn.setCellValueFactory(new PropertyValueFactory<>("historiqueDeMaladie"));
        casMedicalColumn.setCellValueFactory(new PropertyValueFactory<>("casMedical"));
        solutionColumn.setCellValueFactory(new PropertyValueFactory<>("solution"));

        // Initialize rendez-vous table columns
        rdvIdColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        rdvDateColumn.setCellValueFactory(new PropertyValueFactory<>("date"));
        rdvHeureColumn.setCellValueFactory(new PropertyValueFactory<>("heure"));
        rdvSexColumn.setCellValueFactory(new PropertyValueFactory<>("sex"));
        rdvCauseColumn.setCellValueFactory(new PropertyValueFactory<>("cause"));
        rdvStatusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));

        // Setup actions columns
        setupActionsColumn();
        setupRapportActionsColumn();
        setupRendezVousActionsColumn();

        // Load data
        loadConsultationData();
        loadRapportMedicalData();
        loadRendezVousData();

        // Setup add button
        addConsultationBtn.setOnAction(this::handleAddConsultation);

        // Setup add button for medical reports
        addRapportBtn.setOnAction(this::handleAddRapportMedical);

        // Setup add button for rendez-vous
        addRendezVousBtn.setOnAction(this::handleAddRendezVous);
    }

    private void setupRapportActionsColumn() {
        rapportActionsColumn.setCellFactory(param -> new TableCell<>() {
            private final Button modifyBtn = new Button("Modifier");
            private final Button deleteBtn = new Button("Supprimer");
            private final HBox buttons = new HBox(5, modifyBtn, deleteBtn);

            {
                modifyBtn.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
                deleteBtn.setStyle("-fx-background-color: #f44336; -fx-text-fill: white;");

                modifyBtn.setOnAction(event -> {
                    RapportMedical rapport = getTableView().getItems().get(getIndex());
                    handleModifyRapportMedical(rapport);
                });

                deleteBtn.setOnAction(event -> {
                    RapportMedical rapport = getTableView().getItems().get(getIndex());
                    try {
                        serviceRapportMedical.delete(rapport);
                        rapportMedicalList.remove(rapport);
                    } catch (SQLException e) {
                        e.printStackTrace();
                        // TODO: Show error message to user
                    }
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(buttons);
                }
            }
        });
    }

    private void loadRapportMedicalData() {
        try {
            rapportMedicalList.clear();
            rapportMedicalList.addAll(serviceRapportMedical.showAll());
            rapportMedicalTable.setItems(rapportMedicalList);
        } catch (SQLException e) {
            e.printStackTrace();
            // TODO: Show error message to user
        }
    }

    @FXML
    private void handleAddConsultation(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AjoutConsultation.fxml"));
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Ajouter une consultation");
            stage.setScene(new Scene(loader.load()));
            stage.showAndWait();
            
            // Rafraîchir les données après l'ajout
            loadConsultationData();
        } catch (IOException e) {
            e.printStackTrace();
            // TODO: Show error message to user
        }
    }

    private void setupActionsColumn() {
        actionsColumn.setCellFactory(param -> new TableCell<>() {
            private final Button modifyBtn = new Button("Modifier");
            private final Button deleteBtn = new Button("Supprimer");
            private final HBox buttons = new HBox(5, modifyBtn, deleteBtn);

            {
                modifyBtn.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
                deleteBtn.setStyle("-fx-background-color: #f44336; -fx-text-fill: white;");

                modifyBtn.setOnAction(event -> {
                    Consultation consultation = getTableView().getItems().get(getIndex());
                    openModifyPopup(consultation);
                });

                deleteBtn.setOnAction(event -> {
                    Consultation consultation = getTableView().getItems().get(getIndex());
                    try {
                        serviceConsultation.delete(consultation);
                        consultationList.remove(consultation);
                    } catch (SQLException e) {
                        e.printStackTrace();
                        // TODO: Show error message to user
                    }
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(buttons);
                }
            }
        });
    }

    private void openModifyPopup(Consultation consultation) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ModifyConsultation.fxml"));
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Modifier Consultation");
            stage.setScene(new Scene(loader.load()));

            ModifyConsultationController controller = loader.getController();
            controller.setConsultation(consultation);
            controller.setStage(stage);

            stage.showAndWait();
            
            // Rafraîchir les données après la modification
            loadConsultationData();
        } catch (IOException e) {
            e.printStackTrace();
            // TODO: Show error message to user
        }
    }

    private void loadConsultationData() {
        try {
            consultationList.clear();
            consultationList.addAll(serviceConsultation.showAll());
            consultationTable.setItems(consultationList);
        } catch (SQLException e) {
            e.printStackTrace();
            // You might want to show an error message to the user here
        }
    }

    @FXML
    private void handleModifyRapportMedical(RapportMedical rapportMedical) {
        try {
            // Charger le FXML de modification
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ModifyRapportMedical.fxml"));
            Parent root = loader.load();

            // Obtenir le contrôleur
            ModifyRapportMedicalController controller = loader.getController();
            controller.setRapportMedical(rapportMedical);

            // Créer la scène
            Scene scene = new Scene(root);
            Stage stage = new Stage();
            stage.setTitle("Modifier Rapport Médical");
            stage.setScene(scene);
            stage.initModality(Modality.APPLICATION_MODAL);
            
            // Passer la référence de la fenêtre au contrôleur
            controller.setStage(stage);

            // Afficher la fenêtre et attendre sa fermeture
            stage.showAndWait();

            // Rafraîchir les données après la modification
            loadRapportMedicalData();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erreur", "Erreur lors de l'ouverture de la fenêtre de modification", Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void handleAddRapportMedical(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AjoutRapport.fxml"));
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Ajouter un rapport médical");
            stage.setScene(new Scene(loader.load()));
            stage.showAndWait();
            
            // Rafraîchir les données après l'ajout
            loadRapportMedicalData();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erreur", "Erreur lors de l'ouverture de la fenêtre d'ajout", Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void handleAddRendezVous(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AjoutRendezVous.fxml"));
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Ajouter un rendez-vous");
            stage.setScene(new Scene(loader.load()));
            stage.showAndWait();
            // Rafraîchir les données après l'ajout
            // (à implémenter : loadRendezVousData();)
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erreur", "Erreur lors de l'ouverture de la fenêtre d'ajout de rendez-vous", Alert.AlertType.ERROR);
        }
    }

    public void AjoutRendezVous(ActionEvent actionEvent) {
        // Implementation for adding a new appointment
    }

    private void setupRendezVousActionsColumn() {
        rdvActionsColumn.setCellFactory(param -> new TableCell<>() {
            private final Button modifyBtn = new Button("Modifier");
            private final Button deleteBtn = new Button("Supprimer");
            private final HBox buttons = new HBox(5, modifyBtn, deleteBtn);

            {
                modifyBtn.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
                deleteBtn.setStyle("-fx-background-color: #f44336; -fx-text-fill: white;");

                modifyBtn.setOnAction(event -> {
                    RendezVous rdv = getTableView().getItems().get(getIndex());
                    handleModifyRendezVous(rdv);
                });

                deleteBtn.setOnAction(event -> {
                    RendezVous rdv = getTableView().getItems().get(getIndex());
                    try {
                        serviceRendezVous.delete(rdv);
                        rendezVousList.remove(rdv);
                    } catch (SQLException e) {
                        e.printStackTrace();
                        showAlert("Erreur", "Erreur lors de la suppression du rendez-vous", Alert.AlertType.ERROR);
                    }
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(buttons);
                }
            }
        });
    }

    private void loadRendezVousData() {
        try {
            rendezVousList.clear();
            rendezVousList.addAll(serviceRendezVous.showAll());
            rendezVousTable.setItems(rendezVousList);
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Erreur", "Erreur lors du chargement des rendez-vous", Alert.AlertType.ERROR);
        }
    }

    private void handleModifyRendezVous(RendezVous rendezVous) {
        try {
            // Charger le FXML de modification
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ModifyRendezVous.fxml"));
            Parent root = loader.load();

            // Obtenir le contrôleur
            ModifyRendezVousController controller = loader.getController();
            controller.setRendezVous(rendezVous);

            // Créer la scène
            Scene scene = new Scene(root);
            Stage stage = new Stage();
            stage.setTitle("Modifier Rendez-vous");
            stage.setScene(scene);
            stage.initModality(Modality.APPLICATION_MODAL);
            
            // Passer la référence de la fenêtre au contrôleur
            controller.setStage(stage);

            // Afficher la fenêtre et attendre sa fermeture
            stage.showAndWait();

            // Rafraîchir les données après la modification
            loadRendezVousData();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erreur", "Erreur lors de l'ouverture de la fenêtre de modification", Alert.AlertType.ERROR);
        }
    }

    private void showAlert(String title, String content, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
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
            showAlert("Erreur", "Erreur lors du chargement du dashboard", Alert.AlertType.ERROR);
        }
    }
}
