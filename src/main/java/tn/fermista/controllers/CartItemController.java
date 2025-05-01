package tn.fermista.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import tn.fermista.models.Panier;
import tn.fermista.models.Produit;

public class CartItemController {
    @FXML
    private ImageView productImage;
    @FXML
    private Label productName;
    @FXML
    private Label productPrice;
    @FXML
    private Label quantityLabel;
    @FXML
    private Button decreaseButton;
    @FXML
    private Button increaseButton;
    @FXML
    private Button removeButton;

    private Produit produit;
    private Panier panier;
    private Runnable onUpdate;

    public void setData(Produit produit, Panier panier, Runnable onUpdate) {
        this.produit = produit;
        this.panier = panier;
        this.onUpdate = onUpdate;

        // Charger l'image du produit
        try {
            if (produit.getImage() != null && !produit.getImage().isEmpty()) {
                productImage.setImage(new Image(produit.getImage()));
            } else {
                productImage.setImage(new Image(getClass().getResourceAsStream("/images/default-product.png")));
            }
        } catch (Exception e) {
            productImage.setImage(new Image(getClass().getResourceAsStream("/images/default-product.png")));
        }

        productName.setText(produit.getNom());
        productPrice.setText(String.format("%.2f DT", produit.getPrix()));
        updateQuantityLabel();

        // Configurer les boutons
        decreaseButton.setOnAction(e -> handleDecrease());
        increaseButton.setOnAction(e -> handleIncrease());
        removeButton.setOnAction(e -> handleRemove());
    }

    private void updateQuantityLabel() {
        int quantity = panier.getQuantite(produit);
        quantityLabel.setText(String.valueOf(quantity));
    }

    private void handleDecrease() {
        panier.decrementerQuantite(produit);
        updateQuantityLabel();
        if (onUpdate != null) {
            onUpdate.run();
        }
    }

    private void handleIncrease() {
        panier.ajouterProduit(produit);
        updateQuantityLabel();
        if (onUpdate != null) {
            onUpdate.run();
        }
    }

    private void handleRemove() {
        panier.supprimerProduit(produit);
        if (onUpdate != null) {
            onUpdate.run();
        }
    }
} 