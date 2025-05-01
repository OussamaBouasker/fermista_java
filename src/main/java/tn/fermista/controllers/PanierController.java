package tn.fermista.controllers;

import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.Priority;
import javafx.scene.layout.Region;
import javafx.scene.layout.VBox;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import tn.fermista.models.Commande;
import tn.fermista.models.Produit;
import tn.fermista.models.Panier;
import tn.fermista.services.ServiceCommande;
import tn.fermista.utils.FullScreenUtil;
import tn.fermista.utils.LocalizationManager;
import tn.fermista.utils.NavigationUtil;
import javafx.geometry.Insets;
import javafx.geometry.Pos;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.util.ResourceBundle;
import java.util.Map;

public class PanierController implements Initializable {

    @FXML
    private VBox cartItemsContainer;

    @FXML
    private Label totalLabel;

    @FXML
    private Label finalTotalLabel;

    @FXML
    private Button commanderButton;

    @FXML
    private Label emptyCartLabel;

    @FXML
    private ScrollPane scrollPane;

    @FXML
    private Button retourBoutiqueButton;

    private final Panier panier = Panier.getInstance();
    private final ServiceCommande serviceCommande = new ServiceCommande();
    private LocalizationManager localizationManager;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            localizationManager = LocalizationManager.getInstance();
            Platform.runLater(() -> {
                try {
                    updateUI();
                    updateCartView();
                    
                    // Appliquer le mode plein écran
                    Stage stage = (Stage) cartItemsContainer.getScene().getWindow();
                    if (stage != null) {
                        FullScreenUtil.setFullScreen(stage);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Erreur", "Erreur d'initialisation du panier: " + e.getMessage());
        }
    }

    private void updateUI() {
        try {
            if (retourBoutiqueButton != null) {
                try {
                    retourBoutiqueButton.setText(localizationManager.getString("panier.retour_boutique"));
                } catch (Exception e) {
                    retourBoutiqueButton.setText("Continuer mes achats");
                }
            }
            if (commanderButton != null) {
                try {
                    commanderButton.setText(localizationManager.getString("panier.commander"));
                } catch (Exception e) {
                    commanderButton.setText("Finaliser ma commande");
                }
            }
            if (emptyCartLabel != null) {
                try {
                    emptyCartLabel.setText(localizationManager.getString("panier.empty"));
                } catch (Exception e) {
                    emptyCartLabel.setText("Votre panier est vide");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public void updateCartView() {
        try {
            cartItemsContainer.getChildren().clear();
            Map<Produit, Integer> items = panier.getProduits();
            
            if (items.isEmpty()) {
                emptyCartLabel.setVisible(true);
                emptyCartLabel.setManaged(true);
                commanderButton.setDisable(true);
            } else {
                emptyCartLabel.setVisible(false);
                emptyCartLabel.setManaged(false);
                commanderButton.setDisable(false);
                
                for (Map.Entry<Produit, Integer> entry : items.entrySet()) {
                    createCartItemView(entry.getKey(), entry.getValue());
                }
            }
            
            updateTotal();
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Erreur", "Erreur lors de la mise à jour du panier: " + e.getMessage());
        }
    }

    private void createCartItemView(Produit produit, Integer quantity) {
        try {
            HBox itemContainer = new HBox();
            itemContainer.getStyleClass().add("cart-item");
            itemContainer.setSpacing(15);
            itemContainer.setAlignment(Pos.CENTER_LEFT);

            // Product image
            StackPane imageContainer = new StackPane();
            imageContainer.getStyleClass().add("product-image-container");
            imageContainer.setPrefWidth(80);
            imageContainer.setPrefHeight(80);
            imageContainer.setMaxWidth(80);
            imageContainer.setMaxHeight(80);
            
            ImageView imageView = new ImageView();
            try {
                if (produit.getImage() != null && !produit.getImage().isEmpty()) {
                    File imageFile = new File(produit.getImage());
                    if (imageFile.exists()) {
                        imageView.setImage(new Image(imageFile.toURI().toString()));
                    } else {
                        imageView.setImage(new Image(getClass().getResourceAsStream("/images/default-product.png")));
                    }
                } else {
                    imageView.setImage(new Image(getClass().getResourceAsStream("/images/default-product.png")));
                }
            } catch (Exception e) {
                imageView.setImage(new Image(getClass().getResourceAsStream("/images/default-product.png")));
            }
            imageView.setFitHeight(70);
            imageView.setFitWidth(70);
            imageView.setPreserveRatio(true);
            
            imageContainer.getChildren().add(imageView);

            // Product details
            VBox detailsBox = new VBox(5);
            detailsBox.setAlignment(Pos.CENTER_LEFT);
            HBox.setHgrow(detailsBox, Priority.ALWAYS);
            
            // Prix avec monnaie
            Label priceLabel = new Label(String.format("%.2f DT", produit.getPrix()));
            priceLabel.getStyleClass().add("product-price");
            
            // Nom du produit
            Label nameLabel = new Label(produit.getNom());
            nameLabel.getStyleClass().add("product-name");
            nameLabel.setWrapText(true);
            
            // Description courte
            String description = "Réf: " + produit.getId() + " - " + produit.getCategorie();
            Label descriptionLabel = new Label(description);
            descriptionLabel.getStyleClass().add("product-description");
            
            detailsBox.getChildren().addAll(priceLabel, nameLabel, descriptionLabel);

            // Quantity controls
            HBox quantityBox = new HBox(0);
            quantityBox.getStyleClass().add("quantity-controls");
            quantityBox.setMinWidth(100);
            quantityBox.setMaxWidth(100);
            quantityBox.setAlignment(Pos.CENTER);
            
            Button decreaseBtn = new Button("-");
            decreaseBtn.getStyleClass().addAll("quantity-button", "circle-button");
            
            Label quantityLabel = new Label(String.valueOf(quantity));
            quantityLabel.getStyleClass().add("quantity-label");
            quantityLabel.setAlignment(Pos.CENTER);
            
            Button increaseBtn = new Button("+");
            increaseBtn.getStyleClass().addAll("quantity-button", "circle-button");

            decreaseBtn.setOnAction(e -> {
                panier.decrementerQuantite(produit);
                updateCartView();
            });

            increaseBtn.setOnAction(e -> {
                panier.ajouterProduit(produit);
                updateCartView();
            });

            quantityBox.getChildren().addAll(decreaseBtn, quantityLabel, increaseBtn);

            // Total price for this product
            Label totalItemPrice = new Label(String.format("%.2f DT", produit.getPrix() * quantity));
            totalItemPrice.getStyleClass().add("total-item-price");
            totalItemPrice.setMinWidth(80);
            totalItemPrice.setAlignment(Pos.CENTER_RIGHT);

            // Remove button
            Button removeBtn = new Button("×");
            removeBtn.getStyleClass().addAll("remove-button");
            removeBtn.setOnAction(e -> {
                panier.supprimerProduit(produit);
                updateCartView();
            });

            // Add all to container
            itemContainer.getChildren().addAll(imageContainer, detailsBox, quantityBox, totalItemPrice, removeBtn);
            cartItemsContainer.getChildren().add(itemContainer);
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Erreur", "Erreur lors de la création de l'élément du panier: " + e.getMessage());
        }
    }

    private String truncateText(String text, int maxLength) {
        if (text == null) return "";
        return text.length() <= maxLength ? text : text.substring(0, maxLength) + "...";
    }

    private void updateTotal() {
        double total = panier.getTotal();
        totalLabel.setText(String.format("%.2f DT", total));
        finalTotalLabel.setText(String.format("%.2f DT", total));
    }

    @FXML
    private void handleCommander() {
        if (panier.getProduits().isEmpty()) {
            showAlert("Panier vide", "Votre panier est vide. Ajoutez des produits avant de commander.");
            return;
        }

        try {
            // Récupérer la scène actuelle
            Stage currentStage = (Stage) commanderButton.getScene().getWindow();
            
            // Charger la vue du formulaire de commande
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/CommandeForm.fxml"));
            Parent root = loader.load();
            
            // Créer et configurer la nouvelle scène avec dimensions standard
            Scene scene = FullScreenUtil.createStandardScene(root);
            
            // Ajouter explicitement la feuille de style
            scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());
            
            currentStage.setScene(scene);
            FullScreenUtil.setFullScreen(currentStage);
            currentStage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible de charger le formulaire de commande: " + e.getMessage());
        }
    }

    @FXML
    private void handleRetourBoutique() {
        try {
            // Récupérer la scène actuelle
            Stage currentStage = (Stage) retourBoutiqueButton.getScene().getWindow();
            
            // Charger la vue de la boutique
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ProductShopView.fxml"));
            Parent root = loader.load();
            
            // Créer et configurer la nouvelle scène avec dimensions standard
            Scene scene = FullScreenUtil.createStandardScene(root);
            
            // Ajouter explicitement la feuille de style
            scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());
            
            currentStage.setScene(scene);
            FullScreenUtil.setFullScreen(currentStage);
            currentStage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible de retourner à la boutique: " + e.getMessage());
        }
    }

    private void showAlert(String title, String message) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle(title);
        alert.setContentText(message);
        alert.showAndWait();
    }
} 