package tn.fermista.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.control.cell.TextFieldTableCell;
import javafx.scene.layout.HBox;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import tn.fermista.models.Vache;
import tn.fermista.services.ServiceVache;
import java.io.IOException;
import java.util.List;

public class ListeVacheController {

    @FXML
    private TableView<Vache> vacheTable;
    @FXML
    private TableColumn<Vache, Integer> idColumn;
    @FXML
    private TableColumn<Vache, String> nameIdColumn;
    @FXML
    private TableColumn<Vache, Integer> ageIdColumn;
    @FXML
    private TableColumn<Vache, String> raceolumn;
    @FXML
    private TableColumn<Vache, String> etat_medicalColumn;
    @FXML
    private TableColumn<Vache, Void> ActionColumn;
    @FXML
    private Button btnAjouter;
    @FXML
    private Button btn_workbench1132;
    @FXML
    private Button btn_workbench1121;
    @FXML
    private FlowPane vacheListPane;
    @FXML
    private TextField searchField;
    @FXML
    private Button applyButton;

    private final ServiceVache serviceVache = new ServiceVache();

    @FXML
    public void initialize() {
        setupColumns();
        loadVaches();
        btn_workbench1132.setOnAction(event -> naviguerVersAjoutVache());
        
        btn_workbench1121.setOnAction(event -> {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/choixvachecollier.fxml"));
                Parent root = loader.load();
                Scene scene = new Scene(root);
                Stage stage = (Stage) btn_workbench1121.getScene().getWindow();
                stage.setScene(scene);
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Erreur");
                alert.setHeaderText("Erreur de navigation");
                alert.setContentText("Impossible d'ouvrir la page de suivi médical : " + e.getMessage());
                alert.showAndWait();
            }
        });
        afficherVaches();
    }

    private void setupColumns() {
        idColumn.setCellValueFactory(new PropertyValueFactory<>("id"));
        nameIdColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        ageIdColumn.setCellValueFactory(new PropertyValueFactory<>("age"));
        raceolumn.setCellValueFactory(new PropertyValueFactory<>("race"));
        etat_medicalColumn.setCellValueFactory(new PropertyValueFactory<>("etat_medical"));

        nameIdColumn.setCellFactory(TextFieldTableCell.forTableColumn());
        nameIdColumn.setOnEditCommit(event -> {
            Vache vache = event.getRowValue();
            vache.setName(event.getNewValue());
            serviceVache.update(vache);
        });

        ActionColumn.setCellFactory(param -> new TableCell<>() {
            private final Button deleteBtn = new Button("Supprimer");
            private final Button editBtn = new Button("Modifier");
            private final HBox buttonsBox = new HBox(5, deleteBtn, editBtn);

            {
                deleteBtn.setStyle("-fx-background-color: #e74c3c; -fx-text-fill: white;");
                editBtn.setStyle("-fx-background-color: #3498db; -fx-text-fill: white;");

                deleteBtn.setOnAction(event -> {
                    Vache vache = getTableView().getItems().get(getIndex());
                    
                    // Créer une boîte de dialogue de confirmation
                    Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
                    alert.setTitle("Confirmation de suppression");
                    alert.setHeaderText("Suppression de vache");
                    alert.setContentText("Êtes-vous sûr de vouloir supprimer la vache " + vache.getName() + " ?");
                    
                    // Afficher la boîte de dialogue et attendre la réponse
                    alert.showAndWait().ifPresent(response -> {
                        if (response == ButtonType.OK) {
                            serviceVache.delete(vache);
                            loadVaches();
                        }
                    });
                });

                editBtn.setOnAction(event -> {
                    Vache vache = getTableView().getItems().get(getIndex());
                    handleModifier(vache);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                setGraphic(empty ? null : buttonsBox);
            }
        });

        vacheTable.setEditable(true);
        vacheTable.setOnMouseClicked(event -> {
            if (event.getClickCount() == 2 && !vacheTable.getSelectionModel().isEmpty()) {
                TablePosition<Vache, ?> pos = vacheTable.getSelectionModel().getSelectedCells().get(0);
                int row = pos.getRow();
                TableColumn<Vache, ?> col = pos.getTableColumn();
                if (col == nameIdColumn) {
                    vacheTable.edit(row, col);
                }
            }
        });
    }

    @FXML
    private void ouvrirFenetreAjout() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AjoutVache.fxml"));
            Parent root = loader.load();
            
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Ajouter une vache");
            stage.show();
        } catch (Exception e) {
            System.out.println("Erreur lors de l'ouverture de la fenêtre d'ajout : " + e.getMessage());
        }
    }

    private void handleModifier(Vache vache) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ModifierVache.fxml"));
            Pane root = loader.load();
            
            ModifierVacheController controller = loader.getController();
            controller.setVache(vache, null);  // null car nous n'avons pas de FrontVacheCard ici
            
            // Configurer le callback pour rafraîchir la liste
            controller.setOnModificationCallback(() -> {
                // Rafraîchir la liste des vaches
                afficherVaches();
                // Rafraîchir également la table si nécessaire
                loadVaches();
            });
            
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Modifier Vache");
            stage.show();
        } catch (IOException e) {
            showAlert("Erreur", "Erreur lors de l'ouverture de la fenêtre de modification : " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    private void loadVaches() {
        vacheTable.getItems().clear();
        vacheTable.getItems().addAll(serviceVache.getAll());
    }

    private void naviguerVersAjoutVache() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AjoutVache.fxml"));
            Parent root = loader.load();
            Scene scene = btn_workbench1132.getScene();
            scene.setRoot(root);
        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText("Erreur de navigation");
            alert.setContentText("Impossible d'ouvrir la page d'ajout de vache : " + e.getMessage());
            alert.showAndWait();
        }
    }

    private void afficherVaches() {
        vacheListPane.getChildren().clear();
        List<Vache> vaches = serviceVache.getAll();
        for (Vache v : vaches) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/FrontVacheCard.fxml"));
                Pane card = loader.load();
                FrontVacheCard controller = loader.getController();
                controller.setVache(v);
                controller.setOnModificationCallback(this::afficherVaches);
                vacheListPane.getChildren().add(card);
            } catch (IOException e) {
                e.printStackTrace();
            }
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
