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
import javafx.stage.Stage;
import tn.fermista.models.Vache;
import tn.fermista.services.ServiceVache;
import java.io.IOException;

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

    private final ServiceVache serviceVache = new ServiceVache();

    @FXML
    public void initialize() {
        setupColumns();
        loadVaches();
        btn_workbench1132.setOnAction(event -> naviguerVersAjoutVache());
        
        btn_workbench1121.setOnAction(event -> NavigationController.naviguerVersSuiviMedical(btn_workbench1121));
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
                    serviceVache.delete(vache);
                    loadVaches();
                });

                editBtn.setOnAction(event -> {
                    Vache vache = getTableView().getItems().get(getIndex());
                    ouvrirFenetreModification(vache);
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

    private void ouvrirFenetreModification(Vache vache) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ModifierVache.fxml"));
            Parent root = loader.load();
            
            ModifierVacheController controller = loader.getController();
            controller.setVacheAModifier(vache);
            
            Stage stage = new Stage();
            stage.setScene(new Scene(root));
            stage.setTitle("Modifier une vache");
            stage.show();
        } catch (Exception e) {
            System.out.println("Erreur lors de l'ouverture de la fenêtre de modification : " + e.getMessage());
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
