package tn.fermista.controllers;

import tn.fermista.models.Produit;
import tn.fermista.services.ServiceProduit;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.*;
import javafx.stage.Stage;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.text.NumberFormat;
import java.util.HashSet;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.function.Predicate;

public class ProductShopViewController implements Initializable {

    @FXML private FlowPane productCardsContainer;
    @FXML private ComboBox<String> categorieFilter;
    @FXML private ComboBox<String> etatFilter;
    @FXML private TextField searchField;

    private ServiceProduit serviceProduit = new ServiceProduit();
    private ObservableList<Produit> allProducts = FXCollections.observableArrayList();
    private FilteredList<Produit> filteredProducts;
    
    private final NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(Locale.FRANCE);

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Initialiser la liste filtrée
        filteredProducts = new FilteredList<>(allProducts, p -> true);
        
        // Configurer les filtres et la recherche
        setupFilters();
        
        // Charger les produits
        refreshProductList();
    }
    
    private void setupFilters() {
        // Configurer le filtre de catégorie
        categorieFilter.getItems().add("Toutes les catégories");
        categorieFilter.setValue("Toutes les catégories");
        categorieFilter.setOnAction(e -> applyFilters());
        
        // Configurer le filtre d'état
        etatFilter.getItems().addAll("Tous les états", "frais", "périmé");
        etatFilter.setValue("Tous les états");
        etatFilter.setOnAction(e -> applyFilters());
        
        // Configurer la recherche par texte
        searchField.textProperty().addListener((observable, oldValue, newValue) -> applyFilters());
    }
    
    private void applyFilters() {
        filteredProducts.setPredicate(product -> {
            boolean matchesCategory = categorieFilter.getValue().equals("Toutes les catégories") ||
                                     product.getCategorie().equalsIgnoreCase(categorieFilter.getValue());
            
            boolean matchesEtat = etatFilter.getValue().equals("Tous les états") ||
                                 product.getEtat().equalsIgnoreCase(etatFilter.getValue());
            
            boolean matchesSearch = searchField.getText().isEmpty() ||
                                   product.getNom().toLowerCase().contains(searchField.getText().toLowerCase()) ||
                                   product.getDescription().toLowerCase().contains(searchField.getText().toLowerCase());
            
            return matchesCategory && matchesEtat && matchesSearch;
        });
        
        displayProductCards();
    }

    @FXML
    public void refreshProductList() {
        try {
            // Récupérer tous les produits
            List<Produit> products = serviceProduit.showAll();
            allProducts.clear();
            allProducts.addAll(products);
            
            // Mettre à jour les filtres de catégorie disponibles
            updateCategoryFilters();
            
            // Appliquer les filtres actuels
            applyFilters();
        } catch (Exception e) {
            showAlert("Erreur", "Erreur lors du chargement des produits: " + e.getMessage());
        }
    }
    
    private void updateCategoryFilters() {
        // Collecter les catégories uniques
        Set<String> categories = new HashSet<>();
        for (Produit produit : allProducts) {
            if (produit.getCategorie() != null && !produit.getCategorie().isEmpty()) {
                categories.add(produit.getCategorie());
            }
        }
        
        // Mettre à jour le ComboBox
        String currentSelection = categorieFilter.getValue();
        categorieFilter.getItems().clear();
        categorieFilter.getItems().add("Toutes les catégories");
        categorieFilter.getItems().addAll(categories);
        
        // Restaurer la sélection précédente ou définir la valeur par défaut
        if (currentSelection != null && categorieFilter.getItems().contains(currentSelection)) {
            categorieFilter.setValue(currentSelection);
        } else {
            categorieFilter.setValue("Toutes les catégories");
        }
    }
    
    private void displayProductCards() {
        productCardsContainer.getChildren().clear();
        
        for (Produit produit : filteredProducts) {
            VBox productCard = createProductCard(produit);
            productCardsContainer.getChildren().add(productCard);
        }
    }
    
    private VBox createProductCard(Produit produit) {
        // Créer la carte de produit
        VBox card = new VBox();
        card.getStyleClass().add("product-card");
        card.setPrefWidth(220);
        card.setMaxWidth(220);
        card.setPrefHeight(320);
        
        // Conteneur pour l'image
        StackPane imageContainer = new StackPane();
        imageContainer.getStyleClass().add("product-image-container");
        imageContainer.setPrefHeight(150);
        imageContainer.setMaxHeight(150);
        
        // Image du produit
        ImageView imageView = new ImageView();
        imageView.setFitWidth(220);
        imageView.setFitHeight(150);
        imageView.setPreserveRatio(true);
        
        try {
            String imagePath = produit.getImage();
            if (imagePath != null && !imagePath.isEmpty()) {
                File imageFile = new File(imagePath);
                if (imageFile.exists()) {
                    Image image = new Image(imageFile.toURI().toString());
                    imageView.setImage(image);
                } else {
                    // Image par défaut si le fichier n'existe pas
                    Image defaultImage = new Image(getClass().getResourceAsStream("/images/default-product.png"));
                    imageView.setImage(defaultImage);
                }
            } else {
                // Image par défaut si pas de chemin d'image
                Image defaultImage = new Image(getClass().getResourceAsStream("/images/default-product.png"));
                imageView.setImage(defaultImage);
            }
        } catch (Exception e) {
            // En cas d'erreur, utiliser une image par défaut
            try {
                Image defaultImage = new Image(getClass().getResourceAsStream("/images/default-product.png"));
                imageView.setImage(defaultImage);
            } catch (Exception ex) {
                // Si même l'image par défaut échoue, ne rien faire
            }
        }
        
        imageContainer.getChildren().add(imageView);
        
        // Détails du produit
        VBox details = new VBox(5);
        details.getStyleClass().add("product-details");
        
        // Titre du produit
        Label titleLabel = new Label(produit.getNom());
        titleLabel.getStyleClass().add("product-title");
        titleLabel.setWrapText(true);
        titleLabel.setMaxWidth(200);
        
        // Description
        Label descriptionLabel = new Label(truncateText(produit.getDescription(), 100));
        descriptionLabel.getStyleClass().add("product-description");
        descriptionLabel.setWrapText(true);
        descriptionLabel.setMaxWidth(200);
        descriptionLabel.setMaxHeight(40);
        
        // Prix
        Label priceLabel = new Label(currencyFormat.format(produit.getPrix()));
        priceLabel.getStyleClass().add("product-price");
        
        // Tags (catégorie et état)
        HBox tags = new HBox(10);
        tags.setAlignment(Pos.CENTER_LEFT);
        
        Label categoryLabel = new Label(produit.getCategorie());
        categoryLabel.getStyleClass().add("product-category");
        
        Label statusLabel = new Label(produit.getEtat());
        if ("frais".equals(produit.getEtat())) {
            statusLabel.getStyleClass().add("product-status-fresh");
        } else {
            statusLabel.getStyleClass().add("product-status-expired");
        }
        
        tags.getChildren().addAll(categoryLabel, statusLabel);
        
        // Ajouter les détails à la carte
        details.getChildren().addAll(titleLabel, descriptionLabel, priceLabel, tags);
        
        // Assembler la carte
        card.getChildren().addAll(imageContainer, details);
        
        // Ajouter un gestionnaire de clic pour afficher les détails
        card.setOnMouseClicked(event -> showProductDetails(produit));
        
        return card;
    }
    
    private String truncateText(String text, int maxLength) {
        if (text == null) return "";
        return text.length() <= maxLength ? text : text.substring(0, maxLength) + "...";
    }
    
    private void showProductDetails(Produit produit) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Détails du produit");
        alert.setHeaderText(produit.getNom());
        
        // Créer une zone de texte pour afficher les détails complets
        TextArea textArea = new TextArea(
            "ID: " + produit.getId() + "\n" +
            "Nom: " + produit.getNom() + "\n" +
            "Description: " + produit.getDescription() + "\n" +
            "Prix: " + currencyFormat.format(produit.getPrix()) + "\n" +
            "Catégorie: " + produit.getCategorie() + "\n" +
            "État: " + produit.getEtat() + "\n" +
            "Image: " + produit.getImage()
        );
        textArea.setEditable(false);
        textArea.setWrapText(true);
        textArea.setPrefWidth(400);
        textArea.setPrefHeight(300);
        
        alert.getDialogPane().setContent(textArea);
        alert.showAndWait();
    }

    @FXML
    private void handleBackToManager() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/CrudProduit.fxml"));
            Parent root = loader.load();
            
            Stage stage = (Stage) productCardsContainer.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            showAlert("Erreur", "Impossible de retourner à la page de gestion: " + e.getMessage());
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
} 