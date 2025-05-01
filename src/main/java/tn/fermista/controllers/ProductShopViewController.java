package tn.fermista.controllers;

import tn.fermista.models.Panier;
import tn.fermista.models.Produit;
import tn.fermista.services.ServiceProduit;
import tn.fermista.utils.FullScreenUtil;

import javafx.application.Platform;
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
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.stage.Window;
import javafx.util.Duration;
import javafx.animation.FadeTransition;
import javafx.scene.paint.Color;
import javafx.scene.control.ScrollPane.ScrollBarPolicy;

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
    @FXML private Button buttonRetourGestion;
    @FXML private Label cartCountLabel;

    private ServiceProduit serviceProduit = new ServiceProduit();
    private ObservableList<Produit> allProducts = FXCollections.observableArrayList();
    private FilteredList<Produit> filteredProducts;
    
    private final NumberFormat currencyFormat = NumberFormat.getCurrencyInstance(Locale.FRANCE);
    private Panier panier = Panier.getInstance();

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        // Initialiser la liste filtrée
        filteredProducts = new FilteredList<>(allProducts, p -> true);
        
        // Configurer les filtres et la recherche
        setupFilters();
        
        // Charger les produits
        refreshProductList();
        
        // Appliquer le mode plein écran après le chargement de la vue
        Platform.runLater(() -> {
            Stage stage = (Stage) productCardsContainer.getScene().getWindow();
            if (stage != null) {
                FullScreenUtil.setFullScreen(stage);
            }
        });
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
        VBox card = new VBox();
        card.getStyleClass().add("product-card");
        card.setPrefWidth(220);
        card.setMaxWidth(220);
        card.setPrefHeight(320);
        
        // Rendre toute la carte cliquable pour afficher les détails
        card.setOnMouseClicked(event -> showProductDetails(produit));
        card.setCursor(javafx.scene.Cursor.HAND);
        
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
                    Image defaultImage = new Image(getClass().getResourceAsStream("/images/default-product.png"));
                    imageView.setImage(defaultImage);
                }
            } else {
                Image defaultImage = new Image(getClass().getResourceAsStream("/images/default-product.png"));
                imageView.setImage(defaultImage);
            }
        } catch (Exception e) {
            try {
                Image defaultImage = new Image(getClass().getResourceAsStream("/images/default-product.png"));
                imageView.setImage(defaultImage);
            } catch (Exception ex) {
                // Si même l'image par défaut échoue, ne rien faire
            }
        }
        
        imageContainer.getChildren().add(imageView);
        
        // Ajouter un indicateur "Cliquez pour détails"
        Label infoLabel = new Label("Cliquez pour détails");
        infoLabel.setStyle("-fx-background-color: rgba(0,0,0,0.6); -fx-text-fill: white; -fx-padding: 5; -fx-background-radius: 3;");
        infoLabel.setOpacity(0);
        
        // Animation au survol
        card.setOnMouseEntered(e -> {
            FadeTransition fadeIn = new FadeTransition(Duration.millis(200), infoLabel);
            fadeIn.setToValue(1);
            fadeIn.play();
        });
        
        card.setOnMouseExited(e -> {
            FadeTransition fadeOut = new FadeTransition(Duration.millis(200), infoLabel);
            fadeOut.setToValue(0);
            fadeOut.play();
        });
        
        // Positionner l'indicateur en bas de l'image
        StackPane.setAlignment(infoLabel, Pos.BOTTOM_CENTER);
        StackPane.setMargin(infoLabel, new Insets(0, 0, 10, 0));
        imageContainer.getChildren().add(infoLabel);
        
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
        
        // Bouton Ajouter au panier
        Button addToCartButton = new Button("Ajouter au panier");
        addToCartButton.getStyleClass().add("add-to-cart-button");
        addToCartButton.setOnAction(e -> {
            addToCart(produit);
            e.consume(); // Empêcher la propagation de l'événement
        });
        
        // Empêcher le clic sur le bouton de déclencher l'événement de clic de la carte
        addToCartButton.setOnMouseClicked(e -> e.consume());
        
        // Ajouter les détails à la carte
        details.getChildren().addAll(titleLabel, descriptionLabel, priceLabel, tags, addToCartButton);
        
        // Assembler la carte
        card.getChildren().addAll(imageContainer, details);
        
        return card;
    }
    
    private String truncateText(String text, int maxLength) {
        if (text == null) return "";
        return text.length() <= maxLength ? text : text.substring(0, maxLength) + "...";
    }
    
    private void showProductDetails(Produit produit) {
        try {
            // Créer un stage pour le popup
            Stage stage = (Stage) productCardsContainer.getScene().getWindow();
            
            // Créer la mise en page du contenu détaillé
            VBox detailsContent = new VBox(15);
            detailsContent.setPadding(new Insets(0, 10, 0, 10));
            detailsContent.setMaxWidth(480);
            detailsContent.setMaxHeight(500);
            
            // Conteneur pour l'image du produit
            StackPane imageContainer = new StackPane();
            imageContainer.setPrefHeight(200);
            imageContainer.getStyleClass().add("product-detail-image-container");
            imageContainer.setStyle("-fx-background-color: white; -fx-border-color: #eaeaea; -fx-border-radius: 5px;");
            
            // Image du produit
            ImageView imageView = new ImageView();
            imageView.setFitHeight(180);
            imageView.setFitWidth(280);
            imageView.setPreserveRatio(true);
            
            try {
                String imagePath = produit.getImage();
                if (imagePath != null && !imagePath.isEmpty()) {
                    File imageFile = new File(imagePath);
                    if (imageFile.exists()) {
                        Image image = new Image(imageFile.toURI().toString());
                        imageView.setImage(image);
                    } else {
                        Image defaultImage = new Image(getClass().getResourceAsStream("/images/default-product.png"));
                        imageView.setImage(defaultImage);
                    }
                } else {
                    Image defaultImage = new Image(getClass().getResourceAsStream("/images/default-product.png"));
                    imageView.setImage(defaultImage);
                }
            } catch (Exception e) {
                try {
                    Image defaultImage = new Image(getClass().getResourceAsStream("/images/default-product.png"));
                    imageView.setImage(defaultImage);
                } catch (Exception ex) {
                    // Si même l'image par défaut échoue, ne rien faire
                }
            }
            
            imageContainer.getChildren().add(imageView);
            
            // Informations du produit en grille
            GridPane infoGrid = new GridPane();
            infoGrid.setHgap(15);
            infoGrid.setVgap(10);
            infoGrid.setPadding(new Insets(10));
            infoGrid.setStyle("-fx-background-color: #f9f9f9; -fx-border-color: #eaeaea; -fx-border-radius: 5px;");
            
            // Définir les contraintes de colonnes
            ColumnConstraints col1 = new ColumnConstraints();
            col1.setPrefWidth(120);
            col1.setHalignment(javafx.geometry.HPos.RIGHT);
            
            ColumnConstraints col2 = new ColumnConstraints();
            col2.setHgrow(Priority.ALWAYS);
            col2.setFillWidth(true);
            
            infoGrid.getColumnConstraints().addAll(col1, col2);
            
            // Ajouter les informations du produit
            int row = 0;
            
            Label idLabel = new Label("Référence:");
            idLabel.setStyle("-fx-font-weight: bold;");
            Label idValue = new Label(String.valueOf(produit.getId()));
            infoGrid.add(idLabel, 0, row);
            infoGrid.add(idValue, 1, row++);
            
            Label nameLabel = new Label("Nom:");
            nameLabel.setStyle("-fx-font-weight: bold;");
            Label nameValue = new Label(produit.getNom());
            nameValue.setWrapText(true);
            infoGrid.add(nameLabel, 0, row);
            infoGrid.add(nameValue, 1, row++);
            
            Label descLabel = new Label("Description:");
            descLabel.setStyle("-fx-font-weight: bold;");
            Label descValue = new Label(produit.getDescription());
            descValue.setWrapText(true);
            infoGrid.add(descLabel, 0, row);
            infoGrid.add(descValue, 1, row++);
            
            Label priceLabel = new Label("Prix:");
            priceLabel.setStyle("-fx-font-weight: bold;");
            Label priceValue = new Label(currencyFormat.format(produit.getPrix()));
            priceValue.setStyle("-fx-font-weight: bold; -fx-text-fill: #4CAF50;");
            infoGrid.add(priceLabel, 0, row);
            infoGrid.add(priceValue, 1, row++);
            
            Label categoryLabel = new Label("Catégorie:");
            categoryLabel.setStyle("-fx-font-weight: bold;");
            Label categoryValue = new Label(produit.getCategorie());
            infoGrid.add(categoryLabel, 0, row);
            infoGrid.add(categoryValue, 1, row++);
            
            Label stateLabel = new Label("État:");
            stateLabel.setStyle("-fx-font-weight: bold;");
            Label stateValue = new Label(produit.getEtat());
            if ("frais".equals(produit.getEtat())) {
                stateValue.setStyle("-fx-text-fill: #4CAF50;");
            } else {
                stateValue.setStyle("-fx-text-fill: #F44336;");
            }
            infoGrid.add(stateLabel, 0, row);
            infoGrid.add(stateValue, 1, row++);
            
            // Ajouter les détails au conteneur principal
            detailsContent.getChildren().addAll(imageContainer, infoGrid);
            
            // Boutons d'action
            HBox actionButtons = new HBox(15);
            actionButtons.setAlignment(Pos.CENTER);
            actionButtons.setPadding(new Insets(15, 0, 0, 0));
            
            Button addToCartButton = new Button("Ajouter au panier");
            addToCartButton.getStyleClass().add("action-button");
            addToCartButton.setStyle("-fx-background-color: #4CAF50; -fx-text-fill: white;");
            addToCartButton.setOnAction(e -> {
                addToCart(produit);
                // Fermer le dialogue parent
                if (e.getSource() instanceof Button) {
                    Stage parent = (Stage) ((Button) e.getSource()).getScene().getWindow();
                    parent.close();
                }
            });
            
            actionButtons.getChildren().add(addToCartButton);
            detailsContent.getChildren().add(actionButtons);
            
            // Créer et configurer le dialogue personnalisé
            Stage dialogStage = new Stage();
            dialogStage.initOwner(stage);
            dialogStage.initModality(Modality.APPLICATION_MODAL);
            dialogStage.initStyle(StageStyle.TRANSPARENT);
            
            // Créer le conteneur principal du dialogue
            BorderPane root = new BorderPane();
            root.setStyle("-fx-background-color: white; -fx-background-radius: 5px; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.3), 10, 0.5, 0.0, 0.0);");
            
            // En-tête avec titre et bouton de fermeture
            HBox header = new HBox();
            header.setStyle("-fx-background-color: #4CAF50; -fx-background-radius: 5px 5px 0 0;");
            header.setPadding(new Insets(15));
            header.setAlignment(Pos.CENTER_LEFT);
            
            Label titleLabel = new Label("Détails du produit - " + produit.getNom());
            titleLabel.setStyle("-fx-text-fill: white; -fx-font-size: 18px; -fx-font-weight: bold;");
            titleLabel.setMaxWidth(Double.MAX_VALUE);
            HBox.setHgrow(titleLabel, Priority.ALWAYS);
            
            Button closeButton = new Button("×");
            closeButton.setStyle("-fx-background-color: transparent; -fx-text-fill: white; -fx-font-size: 18px; -fx-cursor: hand;");
            closeButton.setOnAction(e -> dialogStage.close());
            
            header.getChildren().addAll(titleLabel, closeButton);
            
            // Ajouter tout au conteneur principal
            root.setTop(header);
            root.setCenter(new ScrollPane(detailsContent) {{
                setFitToWidth(true);
                setHbarPolicy(ScrollBarPolicy.NEVER);
                setVbarPolicy(ScrollBarPolicy.AS_NEEDED);
                setStyle("-fx-background-color: transparent;");
            }});
            
            // Configurer la scène
            Scene scene = new Scene(root, 500, 650);
            scene.setFill(Color.TRANSPARENT);
            
            // Ajouter le CSS
            scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());
            
            // Animation d'entrée
            root.setOpacity(0);
            dialogStage.setScene(scene);
            dialogStage.show();
            
            FadeTransition fadeIn = new FadeTransition(Duration.millis(300), root);
            fadeIn.setFromValue(0);
            fadeIn.setToValue(1);
            fadeIn.play();
            
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible d'afficher les détails du produit: " + e.getMessage());
        }
    }

    @FXML
    private void handleBackToManager() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/DashboardTemplate.fxml"));
            Parent root = loader.load();
            
            // Utiliser une scène avec dimensions standard
            Scene scene = FullScreenUtil.createStandardScene(root);
            
            Stage stage = (Stage) buttonRetourGestion.getScene().getWindow();
            stage.setScene(scene);
            FullScreenUtil.setFullScreen(stage);
            stage.show();
        } catch (IOException e) {
            showAlert("Erreur", "Impossible de retourner au dashboard: " + e.getMessage());
        }
    }

    @FXML
    private void addToCart(Produit produit) {
        if (produit != null) {
            try {
                // Ajouter le produit au panier
                panier.ajouterProduit(produit);
                updateCartCount();
                
                // Ouvrir la vue du panier
                try {
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/PanierView.fxml"));
                    Parent root = loader.load();
                    
                    // Obtenir le contrôleur du panier
                    PanierController panierController = loader.getController();
                    
                    // Mettre à jour la vue du panier pour afficher les nouveaux produits
                    if (panierController != null) {
                        panierController.updateCartView();
                    }
                    
                    // Afficher la vue du panier avec dimensions standard
                    Stage stage = (Stage) productCardsContainer.getScene().getWindow();
                    Scene scene = FullScreenUtil.createStandardScene(root);
                    
                    // Ajouter explicitement la feuille de style
                    scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());
                    
                    stage.setScene(scene);
                    FullScreenUtil.setFullScreen(stage);
                    stage.show();
                } catch (Exception e) {
                    e.printStackTrace();
                    showAlert("Erreur", "Impossible d'ouvrir le panier: " + e.getMessage());
                }
            } catch (Exception e) {
                e.printStackTrace();
                showAlert("Erreur", "Impossible d'ajouter le produit au panier: " + e.getMessage());
            }
        }
    }

    private void updateCartCount() {
        try {
            int count = panier.getProduits().size();
            if (cartCountLabel != null) {
                cartCountLabel.setText(String.valueOf(count));
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void showSuccessMessage(String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Succès");
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.showAndWait();
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
} 