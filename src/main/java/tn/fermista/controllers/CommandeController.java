package tn.fermista.controllers;

import tn.fermista.models.Commande;
import tn.fermista.models.Livraison;
import tn.fermista.services.ServiceCommande;
import tn.fermista.services.ServiceLivraison;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.Stage;
import javafx.util.StringConverter;
import javafx.event.ActionEvent;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.ZoneId;
import java.util.Date;
import java.util.List;
import java.util.ResourceBundle;

public class CommandeController implements Initializable {

    @FXML private DatePicker datePicker;
    @FXML private ComboBox<String> statutComboBox;
    @FXML private TextField montantField;
    @FXML private ComboBox<Livraison> livraisonComboBox;

    private ServiceCommande serviceCommande = new ServiceCommande();
    private ServiceLivraison serviceLivraison = new ServiceLivraison();
    private Integer currentCommandeId = null;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Initialiser les valeurs du ComboBox statut
        statutComboBox.getItems().addAll("En attente", "En cours", "Livrée", "Annulée");
        
        // Définir la date minimale (aujourd'hui)
        datePicker.setValue(LocalDate.now());
        
        // Configurer les validateurs
        setupValidators();
        
        // Charger la liste des livraisons
        loadLivraisons();
    }
    
    private void setupValidators() {
        // Validation de la date (>= aujourd'hui)
        datePicker.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);
                LocalDate today = LocalDate.now();
                setDisable(empty || date.compareTo(today) < 0);
            }
        });
        
        // Validation du montant (nombres positifs uniquement)
        montantField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.isEmpty()) {
                try {
                    double value = Double.parseDouble(newValue);
                    if (value < 0) {
                        montantField.setText(oldValue);
                    }
                } catch (NumberFormatException e) {
                    montantField.setText(oldValue);
                }
            }
        });
        
        // Configurer l'affichage des livraisons dans le ComboBox
        livraisonComboBox.setConverter(new StringConverter<Livraison>() {
            @Override
            public String toString(Livraison livraison) {
                if (livraison == null) return null;
                return String.format("#%d - %s (%s)", livraison.getId(), livraison.getLivreur(), livraison.getLieu());
            }

            @Override
            public Livraison fromString(String string) {
                return null;
            }
        });
    }
    
    private void loadLivraisons() {
        try {
            List<Livraison> livraisons = serviceLivraison.showAll();
            
            // Vérifier si la liste n'est pas null
            if (livraisons != null) {
                ObservableList<Livraison> options = FXCollections.observableArrayList(livraisons);
                livraisonComboBox.setItems(options);
            } else {
                // Initialiser avec une liste vide si aucune livraison n'est disponible
                livraisonComboBox.setItems(FXCollections.observableArrayList());
                // Informer l'utilisateur qu'aucune livraison n'est disponible
                showAlert("Information", "Aucune livraison n'est disponible actuellement.");
            }
        } catch (Exception e) {
            // Gérer l'exception mais ne pas empêcher le chargement du formulaire
            livraisonComboBox.setItems(FXCollections.observableArrayList());
            // Afficher un message d'information au lieu d'une erreur bloquante
            showAlert("Information", "Impossible de charger les livraisons.");
            // Journaliser l'erreur pour le débogage
            e.printStackTrace();
        }
    }

    // Méthode pour naviguer vers la liste des commandes
    @FXML
    private void navigateToCommandeList() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/CommandeListView.fxml"));
            Parent root = loader.load();
            
            Stage stage = (Stage) datePicker.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            showAlert("Erreur", "Impossible d'ouvrir la liste des commandes: " + e.getMessage());
        }
    }

    // Méthode pour ajouter une commande
    @FXML
    private void handleAddCommande() {
        try {
            if (validateInputs()) {
                Commande commande = new Commande();
                commande.setDate(datePicker.getValue());
                commande.setStatus(statutComboBox.getValue());
                commande.setTotal(Double.parseDouble(montantField.getText()));
                
                Livraison selectedLivraison = livraisonComboBox.getValue();
                if (selectedLivraison != null) {
                    commande.setLivraisonId(selectedLivraison.getId());
                }

                try {
                    serviceCommande.insert(commande);
                    clearFields();
                    showAlert("Succès", "Commande ajoutée avec succès!");
                } catch (Exception e) {
                    showAlert("Erreur", "Échec de l'ajout de la commande: " + e.getMessage());
                }
            }
        } catch (Exception e) {
            showAlert("Erreur", "Erreur lors de l'ajout de la commande: " + e.getMessage());
        }
    }

    // Méthode pour mettre à jour une commande
    @FXML
    private void handleUpdateCommande() {
        try {
            if (validateInputs()) {
                String idText = showInputDialog("ID de la commande", "Entrez l'ID de la commande à mettre à jour:");
                if (idText == null || idText.isEmpty()) {
                    showAlert("Annulé", "Mise à jour annulée");
                    return;
                }
                
                try {
                    int id = Integer.parseInt(idText);
                    Commande commande = new Commande();
                    commande.setId(id);
                    commande.setDate(datePicker.getValue());
                    commande.setStatus(statutComboBox.getValue());
                    commande.setTotal(Double.parseDouble(montantField.getText()));
                    
                    Livraison selectedLivraison = livraisonComboBox.getValue();
                    if (selectedLivraison != null) {
                        commande.setLivraisonId(selectedLivraison.getId());
                    }

                    try {
                        serviceCommande.update(commande);
                        clearFields();
                        showAlert("Succès", "Commande mise à jour avec succès!");
                    } catch (Exception e) {
                        showAlert("Erreur", "Échec de la mise à jour de la commande: " + e.getMessage());
                    }
                } catch (NumberFormatException e) {
                    showAlert("Erreur", "L'ID doit être un nombre entier valide");
                }
            }
        } catch (Exception e) {
            showAlert("Erreur", "Erreur lors de la mise à jour de la commande: " + e.getMessage());
        }
    }

    // Méthode pour supprimer une commande
    @FXML
    private void handleDeleteCommande() {
        try {
            String idText = showInputDialog("ID de la commande", "Entrez l'ID de la commande à supprimer:");
            if (idText == null || idText.isEmpty()) {
                showAlert("Annulé", "Suppression annulée");
                return;
            }
            
            try {
                int id = Integer.parseInt(idText);
                Commande commande = new Commande();
                commande.setId(id);
                
                try {
                    serviceCommande.delete(commande);
                    clearFields();
                    showAlert("Succès", "Commande supprimée avec succès!");
                } catch (Exception e) {
                    showAlert("Erreur", "Échec de la suppression de la commande: " + e.getMessage());
                }
            } catch (NumberFormatException e) {
                showAlert("Erreur", "L'ID doit être un nombre entier valide");
            }
        } catch (Exception e) {
            showAlert("Erreur", "Erreur lors de la suppression de la commande: " + e.getMessage());
        }
    }

    // Méthode pour retourner à la page principale
    @FXML
    private void handleBackToMain() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/MainView.fxml"));
            Parent root = loader.load();
            
            Stage stage = (Stage) datePicker.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            showAlert("Erreur", "Impossible de revenir à la page principale: " + e.getMessage());
        }
    }

    // Méthode utilitaire pour convertir en toute sécurité une Date en LocalDate
    private LocalDate convertToLocalDate(Date date) {
        if (date == null) {
            return null;
        }
        
        // Gérer différents types de Date
        if (date instanceof java.sql.Date) {
            return ((java.sql.Date) date).toLocalDate();
        } else {
            // Pour les java.util.Date standards
            return date.toInstant()
                .atZone(ZoneId.systemDefault())
                .toLocalDate();
        }
    }

    // Validation des champs obligatoires
    private boolean validateInputs() {
        StringBuilder errorMessage = new StringBuilder();
        
        if (datePicker.getValue() == null) {
            errorMessage.append("La date est obligatoire\n");
        } else if (datePicker.getValue().isBefore(LocalDate.now())) {
            errorMessage.append("La date de commande doit être aujourd'hui ou une date future\n");
        }
        
        if (statutComboBox.getValue() == null) {
            errorMessage.append("Le statut est obligatoire\n");
        }
        
        if (montantField.getText().isEmpty()) {
            errorMessage.append("Le montant total est obligatoire\n");
        } else {
            try {
                double montant = Double.parseDouble(montantField.getText());
                if (montant <= 0) {
                    errorMessage.append("Le montant total doit être supérieur à 0\n");
                }
            } catch (NumberFormatException e) {
                errorMessage.append("Le montant total doit être un nombre valide\n");
            }
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
        statutComboBox.setValue(null);
        montantField.clear();
        livraisonComboBox.setValue(null);
        currentCommandeId = null;
    }

    // Méthode pour afficher des alertes
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }

    // Méthode pour afficher une boîte de dialogue de confirmation
    private boolean showConfirmationDialog(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        
        ButtonType buttonTypeYes = new ButtonType("Oui", ButtonBar.ButtonData.YES);
        ButtonType buttonTypeNo = new ButtonType("Non", ButtonBar.ButtonData.NO);
        
        alert.getButtonTypes().setAll(buttonTypeYes, buttonTypeNo);
        
        return alert.showAndWait().orElse(buttonTypeNo) == buttonTypeYes;
    }

    private void loadCommandes() {
        // Cette méthode serait implémentée si on avait besoin de charger 
        // une liste de commandes dans une TableView ou autre composant d'affichage
    }

    private void clearForm() {
        datePicker.setValue(LocalDate.now());
        statutComboBox.setValue(null);
        montantField.clear();
        livraisonComboBox.setValue(null);
        currentCommandeId = null;
    }

    @FXML
    void saveCommande(ActionEvent event) {
        if (validateInputs()) {
            try {
                // Création de l'objet Commande avec vérification des valeurs
                Commande commande = new Commande();
                
                // Date - obligatoire
                LocalDate selectedDate = datePicker.getValue();
                if (selectedDate == null) {
                    showAlert("Erreur", "La date est obligatoire");
                    return;
                }
                commande.setDate(selectedDate);
                
                // Statut - obligatoire
                String statut = statutComboBox.getValue();
                if (statut == null || statut.trim().isEmpty()) {
                    showAlert("Erreur", "Le statut est obligatoire");
                    return;
                }
                commande.setStatus(statut);
                
                // Montant - obligatoire et > 0
                try {
                    double montant = Double.parseDouble(montantField.getText());
                    if (montant <= 0) {
                        showAlert("Erreur", "Le montant total doit être supérieur à 0");
                        return;
                    }
                    commande.setTotal(montant);
                } catch (NumberFormatException e) {
                    showAlert("Erreur", "Le montant total doit être un nombre valide");
                    return;
                }
                
                // Livraison - optionnelle
                Livraison selectedLivraison = livraisonComboBox.getValue();
                if (selectedLivraison != null) {
                    commande.setLivraisonId(selectedLivraison.getId());
                }
                
                System.out.println("Tentative d'enregistrement de la commande:");
                System.out.println("- Date: " + commande.getDate());
                System.out.println("- Statut: " + commande.getStatus());
                System.out.println("- Montant: " + commande.getTotal());
                System.out.println("- Livraison: " + (commande.getLivraisonId() != 0 ? commande.getLivraisonId() : "aucune"));
                
                // Enregistrement en base de données
                if (currentCommandeId != null) {
                    // Mise à jour
                    commande.setId(currentCommandeId);
                    boolean updated = serviceCommande.update(commande);
                    if (updated) {
                        showAlert("Succès", "Commande mise à jour avec succès");
                        clearForm();
                    } else {
                        showAlert("Erreur", "Échec de la mise à jour de la commande");
                    }
                } else {
                    // Création
                    boolean created = serviceCommande.insert(commande);
                    if (created) {
                        showAlert("Succès", "Commande créée avec succès");
                        clearForm();
                    } else {
                        showAlert("Erreur", "Échec de la création de la commande");
                    }
                }
                
            } catch (SQLException e) {
                e.printStackTrace();
                showAlert("Erreur", "Erreur lors de l'enregistrement: " + 
                        (e.getMessage() != null ? e.getMessage() : "Erreur inconnue de base de données"));
            } catch (Exception e) {
                e.printStackTrace();
                showAlert("Erreur", "Erreur lors de l'enregistrement: " + 
                        (e.getMessage() != null ? e.getMessage() : "Erreur inconnue"));
            }
        }
    }
} 