package tn.fermista.controllers;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import tn.fermista.models.Vache;
import tn.fermista.services.ServiceVache;

import java.util.Optional;

public class FrontVacheCard {
    @FXML
    private Label nameLabel;
    @FXML
    private Label detailsLabel;
    @FXML
    private VBox card;
    @FXML
    private Button deleteBtn;
    @FXML
    private Button analyseBtn;
    @FXML
    private Button modifierBtn;
    @FXML
    private HBox btnBox;

    private Vache vache;
    private final ServiceVache serviceVache = new ServiceVache();
    private Runnable onModificationCallback;

    public void setOnModificationCallback(Runnable callback) {
        this.onModificationCallback = callback;
    }

    public void setVache(Vache vache) {
        this.vache = vache;
        updateCardDisplay();
    }

    private void updateCardDisplay() {
        nameLabel.setText(vache.getName());
        detailsLabel.setText(String.format("Age: %d ans\nRace: %s\nÉtat médical: %s",
                vache.getAge(), vache.getRace(), vache.getEtat_medical()));
    }

    @FXML
    public void handleDelete() {
        // Créer une boîte de dialogue de confirmation
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation de suppression");
        alert.setHeaderText("Supprimer la vache");
        alert.setContentText("Êtes-vous sûr de vouloir supprimer la vache " + vache.getName() + " ?");
        alert.initModality(Modality.APPLICATION_MODAL);

        // Personnaliser les boutons
        ((Button) alert.getDialogPane().lookupButton(ButtonType.OK)).setText("Supprimer");
        ((Button) alert.getDialogPane().lookupButton(ButtonType.CANCEL)).setText("Annuler");

        // Afficher la boîte de dialogue et attendre la réponse
        Optional<ButtonType> result = alert.showAndWait();
        if (result.isPresent() && result.get() == ButtonType.OK) {
            try {
                // Supprimer la vache
                serviceVache.delete(vache);

                // Masquer la carte immédiatement
                card.setVisible(false);
                card.setManaged(false);

                // Afficher une confirmation
                Alert successAlert = new Alert(Alert.AlertType.INFORMATION);
                successAlert.setTitle("Succès");
                successAlert.setHeaderText(null);
                successAlert.setContentText("La vache " + vache.getName() + " a été supprimée avec succès.");
                successAlert.show();

                if (onModificationCallback != null) {
                    onModificationCallback.run();
                }
            } catch (Exception e) {
                // Afficher une erreur si la suppression échoue
                Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                errorAlert.setTitle("Erreur");
                errorAlert.setHeaderText(null);
                errorAlert.setContentText("Une erreur est survenue lors de la suppression : " + e.getMessage());
                errorAlert.show();
            }
        }
    }

    @FXML
    private void handleAnalyse(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/TempChart.fxml"));
            Parent root = loader.load();
            Scene scene = ((Button) event.getSource()).getScene();
            scene.setRoot(root);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    public void handleModifier() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ModifierVache.fxml"));
            VBox modifierPane = loader.load();
            
            ModifierVacheController controller = loader.getController();
            controller.setVache(vache, this);
            
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Modifier la vache");
            stage.setScene(new Scene(modifierPane));
            stage.showAndWait();

            if (onModificationCallback != null) {
                onModificationCallback.run();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updateCard(Vache updatedVache) {
        this.vache = updatedVache;
        updateCardDisplay();
        if (onModificationCallback != null) {
            onModificationCallback.run();
        }
    }
} 