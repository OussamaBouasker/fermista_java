package tn.fermista.controllers;

import tn.fermista.models.Produit;
import tn.fermista.services.ServiceProduit;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.stage.FileChooser;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;
import java.util.regex.Pattern;

public class ProduitController implements Initializable {

    @FXML private TextField nomField;
    @FXML private TextArea descriptionField;
    @FXML private TextField imageField;
    @FXML private TextField prixField;
    @FXML private ComboBox<String> categorieField;
    @FXML private ComboBox<String> etatField;
    @FXML private TextField commandeField;

    private ServiceProduit serviceProduit = new ServiceProduit();
    private boolean isLoadingData = false;
    
    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Initialiser les validateurs
        setupValidators();
    }

    private void setupValidators() {
        // Validation du nom (lettres, chiffres et espaces uniquement)
        nomField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.isEmpty() && !Pattern.matches("[a-zA-ZÀ-ÿ0-9\\s]+", newValue)) {
                nomField.setText(oldValue);
            }
        });
        
        // Validation du prix (nombres entiers positifs uniquement)
        prixField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.isEmpty()) {
                try {
                    int value = Integer.parseInt(newValue);
                    if (value < 0) {
                        prixField.setText(oldValue);
                    }
                } catch (NumberFormatException e) {
                    prixField.setText(oldValue);
                }
            }
        });
        
        // Limiter la taille de la description
        descriptionField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (newValue.length() > 500) {
                descriptionField.setText(oldValue);
            }
        });
        
        // Validation de l'ID de commande (nombres entiers uniquement)
        commandeField.textProperty().addListener((observable, oldValue, newValue) -> {
            if (!newValue.isEmpty() && !Pattern.matches("[0-9]*", newValue)) {
                commandeField.setText(oldValue);
            }
        });
    }

    // Méthode pour choisir une image depuis l'ordinateur
    @FXML
    private void handleChooseImage() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choisir une image");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg", "*.gif")
        );
        
        Stage stage = (Stage) nomField.getScene().getWindow();
        File selectedFile = fileChooser.showOpenDialog(stage);
        
        if (selectedFile != null) {
            imageField.setText(selectedFile.getAbsolutePath());
        }
    }

    // Méthode pour naviguer vers la liste des produits
    @FXML
    private void navigateToProductList() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ProductShopView.fxml"));
            Parent root = loader.load();
            
            Stage stage = (Stage) nomField.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            showAlert("Erreur", "Impossible d'ouvrir la boutique des produits: " + e.getMessage());
        }
    }

    // Méthode pour ajouter un produit
    @FXML
    private void handleAddProduct() {
        try {
            if (validateInputs()) {
            String nom = nomField.getText();
            String description = descriptionField.getText();
            String image = imageField.getText();
            int prix = Integer.parseInt(prixField.getText());
                String categorie = categorieField.getValue();
                String etat = etatField.getValue();
                
                // ID de commande est facultatif
                Integer commandeId = null;
                if (!commandeField.getText().isEmpty()) {
                    commandeId = Integer.parseInt(commandeField.getText());
                }

            Produit produit = new Produit(0, nom, description, image, prix, categorie, etat, null);
                try {
            serviceProduit.insert(produit);
            clearFields();
            showAlert("Succès", "Produit ajouté avec succès!");
                } catch (Exception e) {
                    showAlert("Erreur", "Échec de l'ajout du produit: " + e.getMessage());
                }
            }
        } catch (Exception e) {
            showAlert("Erreur", "Erreur lors de l'ajout du produit: " + e.getMessage());
        }
    }

    // Méthode pour mettre à jour un produit
    @FXML
    private void handleUpdateProduct() {
        try {
            // Vérifier si un ID de produit est fourni
            if (commandeField.getText().isEmpty()) {
                showAlert("Erreur", "Veuillez entrer l'ID du produit à mettre à jour");
                return;
            }

            int idProduit = Integer.parseInt(commandeField.getText());

            if (!isLoadingData) {
                // Première étape : charger les données
                Produit produit = serviceProduit.getById(idProduit);
                if (produit == null) {
                    showAlert("Erreur", "Aucun produit trouvé avec cet ID");
                    return;
                }

                // Remplir les champs avec les données du produit existant
                nomField.setText(produit.getNom());
                descriptionField.setText(produit.getDescription());
                imageField.setText(produit.getImage());
                prixField.setText(String.valueOf(produit.getPrix()));
                categorieField.setValue(produit.getCategorie());
                etatField.setValue(produit.getEtat());
                
                isLoadingData = true;
                showAlert("Info", "Les données ont été chargées. Modifiez les champs souhaités puis cliquez à nouveau sur Mettre à jour pour confirmer");
            } else {
                // Deuxième étape : effectuer la mise à jour
                if (!validateInputs()) {
                    return;
                }

                // Créer un nouvel objet Produit avec les données mises à jour
                Produit updatedProduit = new Produit(
                    idProduit,
                    nomField.getText(),
                    descriptionField.getText(),
                    imageField.getText(),
                    Integer.parseInt(prixField.getText()),
                    categorieField.getValue(),
                    etatField.getValue(),
                    null  // pas de commande associée pour la mise à jour
                );

                System.out.println("Updating product: " + updatedProduit); // Debug log

                if (serviceProduit.update(updatedProduit)) {
                    showAlert("Succès", "Produit mis à jour avec succès!");
            clearFields();
                    isLoadingData = false;
                } else {
                    showAlert("Erreur", "La mise à jour du produit a échoué");
                }
            }
        } catch (NumberFormatException e) {
            showAlert("Erreur", "L'ID du produit ou le prix doit être un nombre valide");
            e.printStackTrace(); // Debug log
        } catch (SQLException e) {
            showAlert("Erreur", "Erreur lors de la mise à jour du produit: " + e.getMessage());
            e.printStackTrace(); // Debug log
        } catch (Exception e) {
            showAlert("Erreur", "Une erreur inattendue s'est produite: " + e.getMessage());
            e.printStackTrace(); // Debug log
        }
    }

    // Méthode pour supprimer un produit
    @FXML
    private void handleDeleteProduct() {
        try {
            if (commandeField.getText().isEmpty()) {
                showAlert("Erreur", "Veuillez spécifier l'ID du produit à supprimer");
                return;
            }
            
            try {
                int id = Integer.parseInt(commandeField.getText());
                serviceProduit.delete(id);
            clearFields();
                showAlert("Succès", "Produit supprimé avec succès!");
            } catch (NumberFormatException e) {
                showAlert("Erreur", "L'ID doit être un nombre entier valide");
            }
        } catch (Exception e) {
            showAlert("Erreur", "Erreur lors de la suppression du produit: " + e.getMessage());
        }
    }

    // Validation des champs obligatoires
    private boolean validateInputs() {
        StringBuilder errorMessage = new StringBuilder();
        
        if (nomField.getText().isEmpty()) {
            errorMessage.append("Le nom du produit est obligatoire\n");
        } else if (nomField.getText().length() < 3) {
            errorMessage.append("Le nom du produit doit comporter au moins 3 caractères\n");
        } else if (nomField.getText().length() > 50) {
            errorMessage.append("Le nom du produit ne doit pas dépasser 50 caractères\n");
        }
        
        if (descriptionField.getText().isEmpty()) {
            errorMessage.append("La description est obligatoire\n");
        } else if (descriptionField.getText().length() < 10) {
            errorMessage.append("La description doit comporter au moins 10 caractères\n");
        }
        
        if (imageField.getText().isEmpty()) {
            errorMessage.append("L'image est obligatoire\n");
        } else {
            File imageFile = new File(imageField.getText());
            if (!imageFile.exists() || !imageFile.isFile()) {
                errorMessage.append("Le fichier image spécifié n'existe pas ou n'est pas valide\n");
            }
        }
        
        if (prixField.getText().isEmpty()) {
            errorMessage.append("Le prix est obligatoire\n");
        } else {
            try {
                int prix = Integer.parseInt(prixField.getText());
                if (prix <= 0) {
                    errorMessage.append("Le prix doit être supérieur à 0\n");
            }
            } catch (NumberFormatException e) {
                errorMessage.append("Le prix doit être un nombre valide\n");
            }
        }
        
        if (categorieField.getValue() == null) {
            errorMessage.append("La catégorie est obligatoire\n");
        }
        
        if (etatField.getValue() == null) {
            errorMessage.append("L'état du produit est obligatoire\n");
        }
        
        if (errorMessage.length() > 0) {
            showAlert("Erreur de validation", errorMessage.toString());
            return false;
        }
        
        return true;
    }

    // Méthode pour effacer les champs du formulaire
    private void clearFields() {
        commandeField.clear();
        nomField.clear();
        descriptionField.clear();
        imageField.clear();
        prixField.clear();
        categorieField.setValue(null);
        etatField.setValue(null);
        isLoadingData = false;
    }

    // Méthode pour afficher des alertes
    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
}
