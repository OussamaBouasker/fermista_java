package tn.fermista.controllers;

import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;

import java.io.IOException;

public class NavigationController {

    public static void naviguerVersSuiviMedical(Button sourceButton) {
        try {
            FXMLLoader loader = new FXMLLoader(NavigationController.class.getResource("/choixvachecollier.fxml"));
            Parent root = loader.load();
            Scene scene = sourceButton.getScene();
            scene.setRoot(root);
        } catch (IOException e) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Erreur");
            alert.setHeaderText("Erreur de navigation");
            alert.setContentText("Impossible d'ouvrir la page de suivi médical. Veuillez vérifier que le fichier choixvachecollier.fxml existe dans le dossier resources.");
            alert.showAndWait();
            e.printStackTrace(); // Pour le débogage
        }
    }
}