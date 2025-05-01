package tn.fermista.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import tn.fermista.utils.FullScreenUtil;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class DashboardController implements Initializable {

    @FXML
    private Button btn_workbench11211; // Bouton E-commerce

    @FXML
    private Button Crud;

    @FXML
    private ButtonBar btn_workbench;

    @FXML
    private ButtonBar btn_workbench1;

    @FXML
    private ButtonBar btn_workbench2;

    @FXML
    private ButtonBar btn_workbench3;

    @FXML
    private ButtonBar btn_workbench4;

    @FXML
    private ButtonBar btn_workbench5;

    @FXML
    private ButtonBar btn_workbench6;

    @FXML
    private Pane inner_pane;

    @FXML
    private Pane inner_pane1;

    @FXML
    private Pane most_inner_pane;

    @FXML
    private Pane pane_1;

    @FXML
    private Pane pane_11;

    @FXML
    private Pane pane_111;

    @FXML
    private Pane pane_1111;

    @FXML
    private Pane pane_12;

    @FXML
    private Pane pane_121;

    @FXML
    private Pane pane_122;

    @FXML
    private Pane pane_123;

    @FXML
    private Pane pane_14;

    @FXML
    private HBox root;

    @FXML
    private AnchorPane side_ankerpane;

    @FXML
    private TextField txt_search;
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Initialisation des composants si nécessaire
    }

    @FXML
    void CrudTemplate(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/CrudTemplate.fxml"));
            ReclamationController reclamationController = new ReclamationController();
            loader.setController(reclamationController);
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Liste des Réclamations");
            Scene scene = new Scene(root);
            stage.setScene(scene);
            FullScreenUtil.setFullScreen(scene);
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Erreur: " + e.getMessage());
        }
    }

    @FXML
    private void handleEcommerce() {
        try {
            // Charger la vue du shop
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ProductShopView.fxml"));
            Parent root = loader.load();
            
            // Créer une nouvelle scène aux dimensions standard
            Scene scene = FullScreenUtil.createStandardScene(root);
            
            // Ajouter explicitement la feuille de style
            scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());
            
            // Obtenir la fenêtre actuelle à partir d'un élément de la scène actuelle
            Stage stage = (Stage) btn_workbench11211.getScene().getWindow();
            
            // Changer la scène
            stage.setScene(scene);
            FullScreenUtil.setFullScreen(stage);
            stage.show();
        } catch (IOException e) {
            showAlert("Erreur", "Impossible de charger la page du shop: " + e.getMessage());
        }
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
