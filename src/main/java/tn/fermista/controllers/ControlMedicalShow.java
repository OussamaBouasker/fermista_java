package tn.fermista.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
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
import javafx.scene.control.TextField;
import javafx.scene.control.DialogPane;
import javafx.scene.control.ButtonType;

public class ControlMedicalShow implements Initializable {
    @FXML
    private TableView<Consultation> consultationTable;

    @FXML
    private TextField txt_search;

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

    // Filtered lists
    private FilteredList<Consultation> filteredConsultations;
    private FilteredList<RapportMedical> filteredRapports;
    private FilteredList<RendezVous> filteredRendezVous;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Initialize filtered lists
        filteredConsultations = new FilteredList<>(consultationList, p -> true);
        filteredRapports = new FilteredList<>(rapportMedicalList, p -> true);
        filteredRendezVous = new FilteredList<>(rendezVousList, p -> true);

        // Setup search functionality
        txt_search.textProperty().addListener((observable, oldValue, newValue) -> {
            filterData(newValue);
        });

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

        // Create SortedList from FilteredList
        SortedList<Consultation> sortedConsultations = new SortedList<>(filteredConsultations);
        SortedList<RapportMedical> sortedRapports = new SortedList<>(filteredRapports);
        SortedList<RendezVous> sortedRendezVous = new SortedList<>(filteredRendezVous);

        // Bind sorted result with the tables
        sortedConsultations.comparatorProperty().bind(consultationTable.comparatorProperty());
        consultationTable.setItems(sortedConsultations);

        sortedRapports.comparatorProperty().bind(rapportMedicalTable.comparatorProperty());
        rapportMedicalTable.setItems(sortedRapports);

        sortedRendezVous.comparatorProperty().bind(rendezVousTable.comparatorProperty());
        rendezVousTable.setItems(sortedRendezVous);

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

    private void filterData(String searchText) {
        if (searchText == null || searchText.isEmpty()) {
            filteredConsultations.setPredicate(p -> true);
            filteredRapports.setPredicate(p -> true);
            filteredRendezVous.setPredicate(p -> true);
        } else {
            String lowerCaseFilter = searchText.toLowerCase();

            // Filter consultations
            filteredConsultations.setPredicate(consultation -> {
                return (consultation.getNom() != null && consultation.getNom().toLowerCase().contains(lowerCaseFilter)) ||
                       (consultation.getLieu() != null && consultation.getLieu().toLowerCase().contains(lowerCaseFilter)) ||
                       (consultation.getDate() != null && consultation.getDate().toString().contains(lowerCaseFilter));
            });

            // Filter rapports médicaux
            filteredRapports.setPredicate(rapport -> {
                return String.valueOf(rapport.getNum()).contains(lowerCaseFilter) ||
                       (rapport.getRace() != null && rapport.getRace().toLowerCase().contains(lowerCaseFilter)) ||
                       (rapport.getHistoriqueDeMaladie() != null && rapport.getHistoriqueDeMaladie().toLowerCase().contains(lowerCaseFilter)) ||
                       (rapport.getCasMedical() != null && rapport.getCasMedical().toLowerCase().contains(lowerCaseFilter)) ||
                       (rapport.getSolution() != null && rapport.getSolution().toLowerCase().contains(lowerCaseFilter));
            });

            // Filter rendez-vous
            filteredRendezVous.setPredicate(rdv -> {
                boolean matchesAgriculteur = false;
                boolean matchesVeterinaire = false;

                if (rdv.getAgriculteur() != null) {
                    matchesAgriculteur = (rdv.getAgriculteur().getFirstName() != null && 
                                        rdv.getAgriculteur().getFirstName().toLowerCase().contains(lowerCaseFilter)) ||
                                       (rdv.getAgriculteur().getLastName() != null && 
                                        rdv.getAgriculteur().getLastName().toLowerCase().contains(lowerCaseFilter));
                }

                if (rdv.getVeterinaire() != null) {
                    matchesVeterinaire = (rdv.getVeterinaire().getFirstName() != null && 
                                        rdv.getVeterinaire().getFirstName().toLowerCase().contains(lowerCaseFilter)) ||
                                       (rdv.getVeterinaire().getLastName() != null && 
                                        rdv.getVeterinaire().getLastName().toLowerCase().contains(lowerCaseFilter));
                }

                return (rdv.getSex() != null && rdv.getSex().toLowerCase().contains(lowerCaseFilter)) ||
                       (rdv.getCause() != null && rdv.getCause().toLowerCase().contains(lowerCaseFilter)) ||
                       (rdv.getStatus() != null && rdv.getStatus().toLowerCase().contains(lowerCaseFilter)) ||
                       (rdv.getDate() != null && rdv.getDate().toString().contains(lowerCaseFilter)) ||
                       matchesAgriculteur ||
                       matchesVeterinaire;
            });
        }
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
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                    alert.setTitle("Confirmation de suppression");
                    alert.setHeaderText(null);
                    alert.setContentText("Êtes-vous sûr de vouloir supprimer ce rapport médical ?");
                    
                    // Ajout de style CSS
                    DialogPane dialogPane = alert.getDialogPane();
                    dialogPane.getStylesheets().add(
                        getClass().getResource("/styles/alert.css").toExternalForm()
                    );
                    dialogPane.getStyleClass().add("custom-alert");
                    dialogPane.getStyleClass().add("confirm-alert");

                    alert.showAndWait().ifPresent(response -> {
                        if (response == ButtonType.OK) {
                            try {
                                serviceRapportMedical.delete(rapport);
                                rapportMedicalList.remove(rapport);
                                showAlert("Succès", "Rapport médical supprimé avec succès", Alert.AlertType.INFORMATION);
                            } catch (SQLException e) {
                                e.printStackTrace();
                                showAlert("Erreur", "Erreur lors de la suppression du rapport", Alert.AlertType.ERROR);
                            }
                        }
                    });
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
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Erreur", "Erreur lors du chargement des rapports médicaux", Alert.AlertType.ERROR);
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
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                    alert.setTitle("Confirmation de suppression");
                    alert.setHeaderText(null);
                    alert.setContentText("Êtes-vous sûr de vouloir supprimer cette consultation ?");
                    
                    // Ajout de style CSS
                    DialogPane dialogPane = alert.getDialogPane();
                    dialogPane.getStylesheets().add(
                        getClass().getResource("/styles/alert.css").toExternalForm()
                    );
                    dialogPane.getStyleClass().add("custom-alert");
                    dialogPane.getStyleClass().add("confirm-alert");

                    alert.showAndWait().ifPresent(response -> {
                        if (response == ButtonType.OK) {
                            try {
                                serviceConsultation.delete(consultation);
                                consultationList.remove(consultation);
                                showAlert("Succès", "Consultation supprimée avec succès", Alert.AlertType.INFORMATION);
                            } catch (SQLException e) {
                                e.printStackTrace();
                                showAlert("Erreur", "Erreur lors de la suppression de la consultation", Alert.AlertType.ERROR);
                            }
                        }
                    });
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
        } catch (SQLException e) {
            e.printStackTrace();
            showAlert("Erreur", "Erreur lors du chargement des consultations", Alert.AlertType.ERROR);
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
            
            // Récupérer le contrôleur et lui passer la référence du Stage
            AjoutRendezVous controller = loader.getController();
            controller.setStage(stage);
            
            stage.showAndWait();
            // Rafraîchir les données après l'ajout
            loadRendezVousData();
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
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                    alert.setTitle("Confirmation de suppression");
                    alert.setHeaderText(null);
                    alert.setContentText("Êtes-vous sûr de vouloir supprimer ce rendez-vous ?");
                    
                    // Ajout de style CSS
                    DialogPane dialogPane = alert.getDialogPane();
                    dialogPane.getStylesheets().add(
                        getClass().getResource("/styles/alert.css").toExternalForm()
                    );
                    dialogPane.getStyleClass().add("custom-alert");
                    dialogPane.getStyleClass().add("confirm-alert");

                    alert.showAndWait().ifPresent(response -> {
                        if (response == ButtonType.OK) {
                            try {
                                serviceRendezVous.delete(rdv);
                                rendezVousList.remove(rdv);
                                showAlert("Succès", "Rendez-vous supprimé avec succès", Alert.AlertType.INFORMATION);
                            } catch (SQLException e) {
                                e.printStackTrace();
                                showAlert("Erreur", "Erreur lors de la suppression du rendez-vous", Alert.AlertType.ERROR);
                            }
                        }
                    });
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
}
