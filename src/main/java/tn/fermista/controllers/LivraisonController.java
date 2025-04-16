package tn.fermista.controllers;

import tn.fermista.models.Livraison;
import tn.fermista.services.ServiceLivraison;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.ResourceBundle;
import java.util.regex.Pattern;

public class LivraisonController implements Initializable {

    @FXML private DatePicker datePicker;
    @FXML private TextField livreurField;
    @FXML private TextField lieuField;

    private ServiceLivraison serviceLivraison = new ServiceLivraison();
    
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Définir la date minimale (aujourd'hui)
        datePicker.setValue(LocalDate.now());
        
        // Ajouter des validateurs pour les champs
        setupValidators();
    }
    
    private void setupValidators() {
        // Validation du champ livreur (lettres et espaces uniquement)
        livreurField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.isEmpty() && !Pattern.matches("[a-zA-ZÀ-ÿ\\s]+", newValue)) {
                livreurField.setText(oldValue);
            }
        });
        
        // Validation du champ lieu
        lieuField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.length() > 100) {
                lieuField.setText(oldValue);
            }
        });
        
        // Validation de la date (>= aujourd'hui)
        datePicker.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                LocalDate today = LocalDate.now();
                setDisable(empty || date.compareTo(today) < 0);
            }
        });
    }

    // Méthode pour naviguer vers la liste des livraisons
    @FXML
    private void navigateToLivraisonList() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/LivraisonListView.fxml"));
            Parent root = loader.load();
            
            Stage stage = (Stage) livreurField.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            showAlert("Erreur", "Impossible d'ouvrir la liste des livraisons: " + e.getMessage());
        }
    }

    // Méthode pour ajouter une livraison
    @FXML
    private void handleAddLivraison() {
        try {
            if (validateInputs()) {
                LocalDate localDate = datePicker.getValue();
                Date date = Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
                String livreur = livreurField.getText();
                String lieu = lieuField.getText();
    
                Livraison livraison = new Livraison(0, date, livreur, lieu);
                try {
                    serviceLivraison.insert(livraison);
                    clearFields();
                    showAlert("Succès", "Livraison ajoutée avec succès!");
                } catch (Exception e) {
                    showAlert("Erreur", "Échec de l'ajout de la livraison: " + e.getMessage());
                }
            }
        } catch (Exception e) {
            showAlert("Erreur", "Erreur lors de l'ajout de la livraison: " + e.getMessage());
        }
    }

    // Méthode pour mettre à jour une livraison
    @FXML
    private void handleUpdateLivraison() {
        try {
            if (validateInputs()) {
                // Pour une mise à jour, l'ID est nécessaire
                String idText = showInputDialog("ID de la livraison", "Entrez l'ID de la livraison à mettre à jour:");
                if (idText == null || idText.isEmpty()) {
                    showAlert("Annulé", "Mise à jour annulée");
                    return;
                }
                
                try {
                    int id = Integer.parseInt(idText);
                    LocalDate localDate = datePicker.getValue();
                    Date date = Date.from(localDate.atStartOfDay(ZoneId.systemDefault()).toInstant());
                    String livreur = livreurField.getText();
                    String lieu = lieuField.getText();
                    
                    Livraison livraison = new Livraison(id, date, livreur, lieu);
                    try {
                        serviceLivraison.update(livraison);
                        clearFields();
                        showAlert("Succès", "Livraison mise à jour avec succès!");
                    } catch (Exception e) {
                        showAlert("Erreur", "Échec de la mise à jour de la livraison: " + e.getMessage());
                    }
                } catch (NumberFormatException e) {
                    showAlert("Erreur", "L'ID doit être un nombre entier valide");
                }
            }
        } catch (Exception e) {
            showAlert("Erreur", "Erreur lors de la mise à jour de la livraison: " + e.getMessage());
        }
    }

    // Méthode pour supprimer une livraison
    @FXML
    private void handleDeleteLivraison() {
        try {
            String idText = showInputDialog("ID de la livraison", "Entrez l'ID de la livraison à supprimer:");
            if (idText == null || idText.isEmpty()) {
                showAlert("Annulé", "Suppression annulée");
                return;
            }
            
            try {
                int id = Integer.parseInt(idText);
                Livraison livraison = new Livraison();
                livraison.setId(id);
                
                try {
                    serviceLivraison.delete(livraison);
                    clearFields();
                    showAlert("Succès", "Livraison supprimée avec succès!");
                } catch (Exception e) {
                    showAlert("Erreur", "Échec de la suppression de la livraison: " + e.getMessage());
                }
            } catch (NumberFormatException e) {
                showAlert("Erreur", "L'ID doit être un nombre entier valide");
            }
        } catch (Exception e) {
            showAlert("Erreur", "Erreur lors de la suppression de la livraison: " + e.getMessage());
        }
    }

    // Méthode pour retourner à la page principale
    @FXML
    private void handleBackToMain() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/MainView.fxml"));
            Parent root = loader.load();
            
            Stage stage = (Stage) livreurField.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            showAlert("Erreur", "Impossible de revenir à la page principale: " + e.getMessage());
        }
    }

    // Validation des champs obligatoires
    private boolean validateInputs() {
        StringBuilder errorMessage = new StringBuilder();
        
        if (datePicker.getValue() == null) {
            errorMessage.append("La date est obligatoire\n");
        } else if (datePicker.getValue().isBefore(LocalDate.now())) {
            errorMessage.append("La date de livraison doit être aujourd'hui ou une date future\n");
        }
        
        if (livreurField.getText().isEmpty()) {
            errorMessage.append("Le nom du livreur est obligatoire\n");
        } else if (livreurField.getText().length() < 3) {
            errorMessage.append("Le nom du livreur doit comporter au moins 3 caractères\n");
        }
        
        if (lieuField.getText().isEmpty()) {
            errorMessage.append("Le lieu de livraison est obligatoire\n");
        } else if (lieuField.getText().length() < 5) {
            errorMessage.append("Le lieu de livraison doit comporter au moins 5 caractères\n");
        }
        
        if (errorMessage.length() > 0) {
            showAlert("Erreur de validation", errorMessage.toString());
            return false;
        }
        
        return true;
    }
    
    // Méthode pour montrer une boîte de dialogue de saisie
    private String showInputDialog(String title, String content) {
        TextInputDialog dialog = new TextInputDialog();
        dialog.setTitle(title);
        dialog.setHeaderText(null);
        dialog.setContentText(content);
        return dialog.showAndWait().orElse(null);
    }

    // Méthode pour effacer les champs du formulaire
    private void clearFields() {
        datePicker.setValue(LocalDate.now());
        livreurField.clear();
        lieuField.clear();
    }

    // Méthode pour afficher des alertes
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
} 