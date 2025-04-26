package tn.fermista.controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.FlowPane;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import tn.fermista.models.Veterinaire;
import tn.fermista.services.ServiceVeterinaire;

import java.io.IOException;
import java.net.URL;
import java.util.List;
import java.util.ResourceBundle;

public class ListVet implements Initializable {
    @FXML
    private FlowPane veterinairesContainer;

    private final ServiceVeterinaire serviceVeterinaire = new ServiceVeterinaire();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        loadVeterinaires();
    }

    private void loadVeterinaires() {
        List<Veterinaire> veterinaires = serviceVeterinaire.rechercher();
        veterinairesContainer.getChildren().clear();

        for (Veterinaire veterinaire : veterinaires) {
            veterinairesContainer.getChildren().add(createVeterinaireCard(veterinaire));
        }
    }

    private VBox createVeterinaireCard(Veterinaire veterinaire) {
        VBox card = new VBox(10);
        card.getStyleClass().add("veterinaire-card");

        // Nom et prénom
        Label nameLabel = new Label(veterinaire.getFirstName() + " " + veterinaire.getLastName());
        nameLabel.getStyleClass().addAll("label", "name-label");

        // Email
        Label emailLabel = new Label(veterinaire.getEmail());
        emailLabel.getStyleClass().addAll("label", "info-label");

        // Numéro
        Label numberLabel = new Label(veterinaire.getNumber());
        numberLabel.getStyleClass().addAll("label", "info-label");

        // Bouton de rendez-vous
        Button rdvButton = new Button("Prendre rendez-vous");
        rdvButton.getStyleClass().add("rdv-button");

        rdvButton.setOnAction(e -> openRendezVousForm(veterinaire));

        // Ajouter tous les éléments à la carte
        card.getChildren().addAll(nameLabel, emailLabel, numberLabel, rdvButton);
        
        return card;
    }

    private void openRendezVousForm(Veterinaire veterinaire) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AddRendezVousForm.fxml"));
            Parent root = loader.load();

            AddRendezVousFormController controller = loader.getController();
            Stage stage = new Stage();
            controller.setStage(stage);
            controller.setVeterinaire(veterinaire);

            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Nouveau rendez-vous");
            stage.setScene(new Scene(root));
            stage.showAndWait();

        } catch (IOException ex) {
            ex.printStackTrace();
            showAlert("Erreur", "Impossible d'ouvrir le formulaire de rendez-vous");
        }
    }

    private void showAlert(String title, String content) {
        javafx.scene.control.Alert alert = new javafx.scene.control.Alert(javafx.scene.control.Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
