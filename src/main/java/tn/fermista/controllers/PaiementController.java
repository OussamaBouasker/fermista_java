package tn.fermista.controllers;

import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import javafx.beans.property.SimpleStringProperty;
import javafx.collections.FXCollections;
import javafx.concurrent.Worker;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.VBox;
import javafx.scene.web.WebEngine;
import javafx.scene.web.WebView;
import javafx.stage.Stage;
import netscape.javascript.JSObject;
import tn.fermista.models.Commande;
import tn.fermista.models.Panier;
import tn.fermista.models.PanierItem;
import tn.fermista.models.Produit;
import tn.fermista.utils.CustomAlert;
import tn.fermista.utils.FullScreenUtil;
import tn.fermista.utils.StripeService;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

public class PaiementController implements Initializable {

    @FXML private TableView<PanierItem> panierTableView;
    @FXML private TableColumn<PanierItem, String> produitColumn;
    @FXML private TableColumn<PanierItem, Integer> quantiteColumn;
    @FXML private TableColumn<PanierItem, String> prixColumn;
    @FXML private TableColumn<PanierItem, String> totalColumn;
    @FXML private Label totalLabel;
    @FXML private WebView webView;
    @FXML private VBox stripeContainer;
    @FXML private ProgressBar progressBar;
    @FXML private Label statusLabel;
    @FXML private Button payButton;
    @FXML private Button cancelButton;
    
    private Commande commande;
    private final Panier panier = Panier.getInstance();
    private PaymentIntent paymentIntent;
    private WebEngine webEngine;
    
    /**
     * Définit la commande à payer
     * @param commande La commande à payer
     */
    public void setCommande(Commande commande) {
        this.commande = commande;
    }
    
    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Configuration des colonnes du tableau
        produitColumn.setCellValueFactory(cellData -> {
            Produit produit = cellData.getValue().getProduit();
            return new SimpleStringProperty(produit.getNom());
        });
        
        quantiteColumn.setCellValueFactory(new PropertyValueFactory<>("quantity"));
        
        prixColumn.setCellValueFactory(cellData -> {
            Produit produit = cellData.getValue().getProduit();
            return new SimpleStringProperty(String.format("%.2f €", produit.getPrix()));
        });
        
        totalColumn.setCellValueFactory(cellData -> {
            PanierItem item = cellData.getValue();
            double total = item.getProduit().getPrix() * item.getQuantity();
            return new SimpleStringProperty(String.format("%.2f €", total));
        });
        
        // Charger les éléments du panier
        refreshPanierItems();
        
        // Initialiser le WebView
        webEngine = webView.getEngine();
        
        // Désactiver le bouton de paiement jusqu'à ce que Stripe soit chargé
        payButton.setDisable(true);
        
        // Afficher l'indicateur de progression pendant le chargement
        progressBar.setVisible(true);
        statusLabel.setText("Chargement du formulaire de paiement...");
        statusLabel.setVisible(true);
        
        // Appliquer le mode plein écran
        javafx.application.Platform.runLater(() -> {
            Stage stage = (Stage) panierTableView.getScene().getWindow();
            if (stage != null) {
                FullScreenUtil.setFullScreen(stage);
            }
        });
    }
    
    /**
     * Rafraîchit les éléments du panier dans le tableau
     */
    private void refreshPanierItems() {
        panierTableView.setItems(FXCollections.observableArrayList(panier.getItems()));
        totalLabel.setText(String.format("%.2f €", panier.getTotal()));
    }
    
    /**
     * Initialise le formulaire de paiement Stripe
     */
    public void initializeStripeForm() {
        if (!StripeService.isConfigured()) {
            showAlert(CustomAlert.AlertType.ERROR, "Erreur de configuration", 
                    "La configuration Stripe n'est pas disponible. Veuillez configurer vos clés API.");
            return;
        }
        
        // Attendre un court instant pour laisser JavaFX terminer son initialisation
        CompletableFuture.delayedExecutor(500, TimeUnit.MILLISECONDS).execute(() -> {
            try {
                // Créer une intention de paiement
                paymentIntent = StripeService.createPaymentIntent(commande);
                
                // Charger le fichier HTML
                String htmlTemplate = loadHtmlTemplate();
                
                // Remplacer les variables dans le template
                String html = htmlTemplate
                    .replace("STRIPE_PUBLIC_KEY", StripeService.getPublishableKey())
                    .replace("PAYMENT_INTENT_CLIENT_SECRET", paymentIntent.getClientSecret());
                
                // Charger le HTML dans le WebView (sur le thread JavaFX)
                javafx.application.Platform.runLater(() -> {
                    // Charger le HTML dans le WebView
                    webEngine.loadContent(html);
                    
                    // Ajouter un écouteur pour le chargement de la page
                    webEngine.getLoadWorker().stateProperty().addListener((observable, oldValue, newValue) -> {
                        if (newValue == Worker.State.SUCCEEDED) {
                            // Obtenir l'objet window du JavaScript
                            JSObject window = (JSObject) webEngine.executeScript("window");
                            
                            // Ajouter un connecteur Java au JavaScript
                            window.setMember("javaConnector", new JavaConnector());
                            
                            // Activer le bouton de paiement
                            payButton.setDisable(false);
                            
                            // Masquer l'indicateur de progression
                            progressBar.setVisible(false);
                            statusLabel.setVisible(false);
                        } else if (newValue == Worker.State.FAILED) {
                            // En cas d'échec de chargement
                            progressBar.setVisible(false);
                            statusLabel.setText("Erreur de chargement du formulaire de paiement");
                            statusLabel.setVisible(true);
                        }
                    });
                });
                
            } catch (StripeException e) {
                javafx.application.Platform.runLater(() -> {
                    progressBar.setVisible(false);
                    statusLabel.setText("Erreur: " + e.getMessage());
                    statusLabel.setVisible(true);
                    
                    showAlert(CustomAlert.AlertType.ERROR, "Erreur Stripe", 
                            "Une erreur est survenue lors de l'initialisation du paiement: " + e.getMessage());
                    e.printStackTrace();
                });
            } catch (IOException e) {
                javafx.application.Platform.runLater(() -> {
                    progressBar.setVisible(false);
                    statusLabel.setText("Erreur: " + e.getMessage());
                    statusLabel.setVisible(true);
                    
                    showAlert(CustomAlert.AlertType.ERROR, "Erreur de chargement", 
                            "Impossible de charger le formulaire de paiement: " + e.getMessage());
                    e.printStackTrace();
                });
            }
        });
    }
    
    /**
     * Charge le template HTML pour le formulaire Stripe
     * @return Le contenu du fichier HTML
     * @throws IOException En cas d'erreur de lecture du fichier
     */
    private String loadHtmlTemplate() throws IOException {
        InputStream is = getClass().getResourceAsStream("/stripe-form.html");
        if (is == null) {
            throw new IOException("Le fichier stripe-form.html est introuvable");
        }
        
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(is))) {
            return reader.lines().collect(Collectors.joining("\n"));
        }
    }
    
    /**
     * Gère le clic sur le bouton de paiement
     */
    @FXML
    private void handlePay() {
        try {
            // Cacher le bouton de paiement et désactiver le bouton d'annulation
            payButton.setVisible(false);
            cancelButton.setDisable(true);
            
            // Vider le panier
            panier.viderPanier();
            
            // Récupérer la fenêtre actuelle
            Stage currentStage = (Stage) payButton.getScene().getWindow();
            
            // Rediriger directement vers la page de confirmation sans attendre la fermeture de l'alerte
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/SuccessPaymentView.fxml"));
            Parent root = loader.load();
            
            // Créer une scène avec dimensions standard
            Scene scene = FullScreenUtil.createStandardScene(root);
            
            // Ajouter explicitement la feuille de style
            scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());
            
            currentStage.setScene(scene);
            FullScreenUtil.setFullScreen(currentStage);
            currentStage.show();
            
            // Afficher le message de confirmation APRÈS avoir changé de scène
            // pour ne pas bloquer la redirection
            Stage stage = currentStage;
            CustomAlert.showInformation(stage, "Paiement réussi", 
                    "Merci pour votre confiance. Votre commande a été confirmée et sera traitée dans les plus brefs délais.");
            
        } catch (Exception e) {
            e.printStackTrace();
            Stage stage = (Stage) payButton.getScene().getWindow();
            CustomAlert.showError(stage, "Erreur", 
                    "Impossible de rediriger vers la page de confirmation: " + e.getMessage());
        }
    }
    
    /**
     * Gère le clic sur le bouton d'annulation
     */
    @FXML
    private void handleCancel() {
        try {
            // Retourner à la vue du panier
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/PanierView.fxml"));
            Parent root = loader.load();
            
            Stage stage = (Stage) cancelButton.getScene().getWindow();
            
            // Créer une scène avec les dimensions standard
            Scene scene = FullScreenUtil.createStandardScene(root);
            
            // Ajouter explicitement la feuille de style
            String cssPath = getClass().getResource("/styles.css").toExternalForm();
            scene.getStylesheets().add(cssPath);
            
            stage.setScene(scene);
            FullScreenUtil.setFullScreen(stage);
            stage.show();
        } catch (IOException e) {
            Stage stage = (Stage) cancelButton.getScene().getWindow();
            CustomAlert.showError(stage, "Erreur", 
                    "Impossible de retourner à la vue du panier: " + e.getMessage());
        }
    }
    
    /**
     * Affiche une boîte de dialogue d'alerte
     * @param type Le type d'alerte
     * @param title Le titre de l'alerte
     * @param message Le message de l'alerte
     */
    private void showAlert(CustomAlert.AlertType type, String title, String message) {
        Stage stage = (Stage) (payButton != null ? payButton.getScene().getWindow() : null);
        switch (type) {
            case INFORMATION:
                CustomAlert.showInformation(stage, title, message);
                break;
            case ERROR:
                CustomAlert.showError(stage, title, message);
                break;
            case WARNING:
                CustomAlert.showWarning(stage, title, message);
                break;
            case CONFIRMATION:
                CustomAlert.showConfirmation(stage, title, message);
                break;
        }
    }
    
    /**
     * Gère le succès du paiement
     */
    private void handlePaymentSuccess() {
        try {
            progressBar.setVisible(false);
            statusLabel.setText("Paiement réussi! Redirection vers la page de confirmation...");
            statusLabel.setVisible(true);
            
            // Cacher le bouton de paiement et désactiver le bouton d'annulation
            payButton.setVisible(false);
            cancelButton.setDisable(true);
            
            // Vider le panier
            panier.viderPanier();
            
            // Rediriger vers la page de confirmation
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/SuccessPaymentView.fxml"));
            Parent root = loader.load();
            
            // Créer une scène avec dimensions standard
            Scene scene = FullScreenUtil.createStandardScene(root);
            
            // Ajouter explicitement la feuille de style
            scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());
            
            Stage stage = (Stage) payButton.getScene().getWindow();
            stage.setScene(scene);
            FullScreenUtil.setFullScreen(stage);
            stage.show();
            
            // Afficher le message de confirmation APRÈS avoir changé de scène
            CustomAlert.showInformation(stage, "Paiement réussi", 
                    "Merci pour votre confiance. Votre commande a été confirmée et sera traitée dans les plus brefs délais.");
            
        } catch (IOException e) {
            Stage stage = (Stage) payButton.getScene().getWindow();
            CustomAlert.showError(stage, "Erreur", 
                    "Impossible de rediriger vers la page de confirmation: " + e.getMessage());
        }
    }
    
    /**
     * Gère l'erreur de paiement
     * @param errorMessage Le message d'erreur
     */
    private void handlePaymentError(String errorMessage) {
        progressBar.setVisible(false);
        statusLabel.setText("Erreur de paiement: " + errorMessage);
        statusLabel.setVisible(true);
        
        Stage stage = (Stage) payButton.getScene().getWindow();
        CustomAlert.showError(stage, "Erreur de paiement", errorMessage);
    }
    
    /**
     * Classe pour connecter Java et JavaScript
     */
    public class JavaConnector {
        /**
         * Appelé par JavaScript quand le paiement réussit
         */
        public void onPaymentSuccess() {
            System.out.println("Paiement réussi! Événement reçu du JavaScript");
            // Exécuter sur le thread JavaFX
            javafx.application.Platform.runLater(() -> {
                try {
                    // Cacher le bouton de paiement et désactiver le bouton d'annulation
                    payButton.setVisible(false);
                    cancelButton.setDisable(true);
                    
                    // Vider le panier
                    panier.viderPanier();
                    
                    // Rediriger vers la page de confirmation
                    FXMLLoader loader = new FXMLLoader(getClass().getResource("/SuccessPaymentView.fxml"));
                    Parent root = loader.load();
                    
                    // Créer une scène avec dimensions standard
                    Scene scene = FullScreenUtil.createStandardScene(root);
                    
                    // Ajouter explicitement la feuille de style
                    scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());
                    
                    Stage stage = (Stage) payButton.getScene().getWindow();
                    stage.setScene(scene);
                    FullScreenUtil.setFullScreen(stage);
                    stage.show();
                    
                    // Afficher le message de confirmation APRÈS avoir changé de scène
                    CustomAlert.showInformation(stage, "Paiement réussi", 
                            "Merci pour votre confiance. Votre commande a été confirmée et sera traitée dans les plus brefs délais.");
                } catch (Exception e) {
                    e.printStackTrace();
                    Stage stage = (Stage) payButton.getScene().getWindow();
                    CustomAlert.showError(stage, "Erreur", 
                            "Impossible de rediriger vers la page de confirmation: " + e.getMessage());
                }
            });
        }
        
        /**
         * Appelé par JavaScript quand le paiement échoue
         * @param errorMessage Le message d'erreur
         */
        public void onPaymentError(String errorMessage) {
            System.out.println("Erreur de paiement: " + errorMessage);
            // Exécuter sur le thread JavaFX
            javafx.application.Platform.runLater(() -> handlePaymentError(errorMessage));
        }
    }
} 