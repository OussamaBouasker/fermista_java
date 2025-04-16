package tn.fermista.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.stage.Stage;
import tn.fermista.models.User;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class NavigationController implements Initializable {
    @FXML
    private Button btn_workbench11;
    
    @FXML
    private Button btn_workbench113;

    @FXML
    private Label userNameLabel;

    private static User currentUser;

    public static void setCurrentUser(User user) {
        currentUser = user;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        if (btn_workbench11 != null) {
            btn_workbench11.setOnAction(event -> navigateToCrudUser());
        }
        if (btn_workbench113 != null) {
            btn_workbench113.setOnAction(event -> navigateToCrudReclamation());
        }
        
        // Afficher le nom de l'utilisateur connecté
        if (currentUser != null && userNameLabel != null) {
            userNameLabel.setText("Bienvenue, " + currentUser.getFirstName() + " " + currentUser.getLastName());
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
} 