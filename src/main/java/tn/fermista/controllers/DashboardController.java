package tn.fermista.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonBar;
import javafx.scene.control.TextField;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Pane;
import javafx.stage.Stage;
import java.io.IOException;

public class DashboardController {

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

    @FXML
    void CrudTemplate(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/CrudTemplate.fxml"));
            
            // Créer et assigner le contrôleur
            ReclamationController reclamationController = new ReclamationController();
            loader.setController(reclamationController);
            
            Parent root = loader.load();
            Stage stage = new Stage();
            stage.setTitle("Liste des Réclamations");
            stage.setScene(new Scene(root));
            stage.show();
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Erreur: " + e.getMessage());
        }
    }
}
