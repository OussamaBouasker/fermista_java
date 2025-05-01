package tn.fermista.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.TextArea;
import javafx.scene.control.TextField;
import javafx.scene.control.Alert;
import javafx.scene.control.DialogPane;
import javafx.scene.control.Label;
import javafx.scene.control.ButtonType;
import javafx.scene.control.Button;
import tn.fermista.models.Reclamation;
import tn.fermista.models.User;
import tn.fermista.services.ServiceReclamation;
import tn.fermista.utils.ReclamationAssistant;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Tooltip;
import javafx.animation.PauseTransition;
import javafx.util.Duration;
import javafx.concurrent.Task;
import javafx.concurrent.Service;
import javafx.concurrent.WorkerStateEvent;
import javafx.event.EventHandler;

import java.sql.SQLException;
import java.time.LocalDateTime;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class AjoutReclamationController {

    @FXML
    private TextField titreField;

    @FXML
    private TextArea descriptionArea;

    @FXML
    private ComboBox<String> commonProblemsComboBox;

    @FXML
    private Button suggestButton;

    @FXML
    private Button improveButton;

    @FXML
    private Button formulateButton;

    private ServiceReclamation serviceReclamation = new ServiceReclamation();

    // Suppose que tu as l'utilisateur courant déjà chargé
    private User currentUser;
    private static final int MAX_RECLAMATIONS_PER_DAY = 3; // Limite de réclamations par jour

    // Liste des mots interdits
    private static final String[] BAD_WORDS = {
        "bitch", "asshole", "bastard", "damn", "hell", "piss", "crap", "dick", "pussy"
    };

    private Service<String> textGenerationService;
    private String currentTextToGenerate;

    public void setCurrentUser(User user) {
        this.currentUser = user;
    }

    @FXML
    public void initialize() {
        if (currentUser == null) {
            currentUser = tn.fermista.utils.UserSession.getCurrentUser();
        }

        // Initialiser le ComboBox avec les problèmes courants
        commonProblemsComboBox.getItems().addAll(ReclamationAssistant.getCommonProblems());
        commonProblemsComboBox.setPromptText("Sélectionnez un problème courant");

        // Ajouter les tooltips
        suggestButton.setTooltip(new Tooltip("Obtenir des suggestions de formulation"));
        improveButton.setTooltip(new Tooltip("Améliorer la description"));
        formulateButton.setTooltip(new Tooltip("Générer une réclamation structurée basée sur le titre"));

        // Ajouter les listeners
        commonProblemsComboBox.setOnAction(e -> handleCommonProblemSelection());
        descriptionArea.textProperty().addListener((obs, old, newValue) -> handleDescriptionChange());
        titreField.textProperty().addListener((obs, old, newValue) -> handleTitleChange());

        // Initialisation du service de génération de texte
        textGenerationService = new Service<String>() {
            @Override
            protected Task<String> createTask() {
                return new Task<String>() {
                    @Override
                    protected String call() throws Exception {
                        StringBuilder currentText = new StringBuilder();
                        
                        for (int i = 0; i < currentTextToGenerate.length(); i++) {
                            currentText.append(currentTextToGenerate.charAt(i));
                            updateValue(currentText.toString());
                            Thread.sleep(50); // Vitesse d'écriture
                        }
                        return currentTextToGenerate;
                    }
                };
            }
        };

        textGenerationService.setOnSucceeded(e -> {
            descriptionArea.setEditable(true);
            suggestButton.setDisable(false);
            improveButton.setDisable(false);
            formulateButton.setDisable(false);
        });
    }

    private void handleTitleChange() {
        if (!titreField.getText().isEmpty() && descriptionArea.getText().isEmpty()) {
            generateDescriptionFromTitle();
        }
    }

    private void generateDescriptionFromTitle() {
        String title = titreField.getText();
        String suggestedDescription = ReclamationAssistant.suggestFormulation(title);
        animateTextGeneration(suggestedDescription);
    }

    private void handleCommonProblemSelection() {
        String selectedProblem = commonProblemsComboBox.getValue();
        if (selectedProblem != null) {
            titreField.setText(selectedProblem);
            String suggestedDescription = ReclamationAssistant.suggestFormulation(selectedProblem);
            animateTextGeneration(suggestedDescription);
        }
    }

    private void animateTextGeneration(String text) {
        descriptionArea.setEditable(false);
        suggestButton.setDisable(true);
        improveButton.setDisable(true);
        formulateButton.setDisable(true);
        
        currentTextToGenerate = text;
        textGenerationService.cancel();
        textGenerationService.restart();

        textGenerationService.valueProperty().addListener((obs, old, newValue) -> {
            if (newValue != null) {
                descriptionArea.setText(newValue);
            }
        });
    }

    private void handleDescriptionChange() {
        // Si le titre est vide, générer un titre intelligent
        if (titreField.getText().isEmpty() && !descriptionArea.getText().isEmpty()) {
            String smartTitle = ReclamationAssistant.generateSmartTitle(descriptionArea.getText());
            titreField.setText(smartTitle);
        }
    }

    @FXML
    private void handleSuggest() {
        String currentText = descriptionArea.getText();
        if (!currentText.isEmpty()) {
            String suggestedText = ReclamationAssistant.suggestFormulation(currentText);
            animateTextGeneration(suggestedText);
        }
    }

    @FXML
    private void handleImprove() {
        String currentText = descriptionArea.getText();
        if (!currentText.isEmpty()) {
            // Désactiver les boutons pendant le traitement
            suggestButton.setDisable(true);
            improveButton.setDisable(true);
            formulateButton.setDisable(true);
            descriptionArea.setEditable(false);

            // Créer une tâche pour l'amélioration du texte
            Task<String> improvementTask = new Task<String>() {
                @Override
                protected String call() throws Exception {
                    // Simuler un temps de traitement
                    Thread.sleep(500);
                    return ReclamationAssistant.improveDescription(currentText);
                }
            };

            // Gérer la fin de la tâche
            improvementTask.setOnSucceeded(e -> {
                String improvedText = improvementTask.getValue();
                animateTextGeneration(improvedText);
            });

            // Démarrer la tâche dans un nouveau thread
            new Thread(improvementTask).start();
        } else {
            showAlert(Alert.AlertType.WARNING, "Description vide", 
                "Veuillez d'abord entrer une description à améliorer.");
        }
    }

    @FXML
    private void handleFormulate() {
        String title = titreField.getText();
        if (title.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Titre vide", "Veuillez d'abord entrer un titre pour votre réclamation.");
            return;
        }

        // Générer une réclamation structurée basée sur le titre
        String structuredReclamation = generateStructuredReclamation(title);
        animateTextGeneration(structuredReclamation);
    }

    private String generateStructuredReclamation(String title) {
        StringBuilder structuredText = new StringBuilder();
        
        // Introduction
        structuredText.append("Je souhaite porter à votre attention un problème concernant : ").append(title).append(".\n\n");
        
        // Analyse des mots-clés du titre
        String[] keywords = title.toLowerCase().split("\\s+");
        
        // Ajout de détails contextuels basés sur les mots-clés
        for (String keyword : keywords) {
            if (keyword.length() > 3) { // Ignorer les mots courts
                switch (keyword) {
                    case "livraison":
                    case "livrer":
                        structuredText.append("La livraison n'a pas été effectuée dans les délais prévus.\n");
                        break;
                    case "produit":
                    case "article":
                        structuredText.append("Le produit reçu ne correspond pas à la description.\n");
                        break;
                    case "service":
                        structuredText.append("Le service fourni n'est pas à la hauteur de mes attentes.\n");
                        break;
                    case "facture":
                    case "paiement":
                        structuredText.append("J'ai rencontré des difficultés avec le processus de paiement.\n");
                        break;
                    case "qualité":
                        structuredText.append("La qualité du produit/service n'est pas satisfaisante.\n");
                        break;
                }
            }
        }
        
        // Conclusion
        structuredText.append("\nJe vous serais reconnaissant de bien vouloir examiner cette situation et de me proposer une solution appropriée.\n");
        structuredText.append("Dans l'attente de votre retour, je vous prie d'agréer mes salutations distinguées.");
        
        return structuredText.toString();
    }

    private boolean containsBadWords(String text) {
        if (text == null || text.isEmpty()) {
            return false;
        }
        
        String lowerText = text.toLowerCase();
        for (String badWord : BAD_WORDS) {
            if (lowerText.contains(badWord)) {
                return true;
            }
        }
        return false;
    }

    private boolean hasReachedDailyLimit() {
        try {
            int count = serviceReclamation.countUserReclamationsToday(currentUser.getId());
            return count >= MAX_RECLAMATIONS_PER_DAY;
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de la vérification des réclamations : " + e.getMessage());
            return true; // En cas d'erreur, on empêche la soumission
        }
    }

    private boolean isSimilarToRecentReclamation(String titre, String description) {
        try {
            List<Reclamation> recentReclamations = serviceReclamation.getUserRecentReclamations(currentUser.getId());
            for (Reclamation recent : recentReclamations) {
                // Vérifier la similarité du titre
                if (calculateSimilarity(titre, recent.getTitre()) > 0.8) {
                    return true;
                }
                // Vérifier la similarité de la description
                if (calculateSimilarity(description, recent.getDescription()) > 0.8) {
                    return true;
                }
            }
            return false;
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de la vérification des réclamations similaires : " + e.getMessage());
            return true; // En cas d'erreur, on empêche la soumission
        }
    }

    private double calculateSimilarity(String text1, String text2) {
        if (text1 == null || text2 == null) return 0.0;
        
        // Convertir en minuscules et supprimer les espaces
        text1 = text1.toLowerCase().replaceAll("\\s+", "");
        text2 = text2.toLowerCase().replaceAll("\\s+", "");
        
        // Calculer la distance de Levenshtein
        int distance = levenshteinDistance(text1, text2);
        
        // Calculer la similarité (1 - distance/maxLength)
        int maxLength = Math.max(text1.length(), text2.length());
        return maxLength == 0 ? 1.0 : 1.0 - (double) distance / maxLength;
    }

    private int levenshteinDistance(String s1, String s2) {
        int[][] dp = new int[s1.length() + 1][s2.length() + 1];

        for (int i = 0; i <= s1.length(); i++) {
            dp[i][0] = i;
        }
        for (int j = 0; j <= s2.length(); j++) {
            dp[0][j] = j;
        }

        for (int i = 1; i <= s1.length(); i++) {
            for (int j = 1; j <= s2.length(); j++) {
                if (s1.charAt(i - 1) == s2.charAt(j - 1)) {
                    dp[i][j] = dp[i - 1][j - 1];
                } else {
                    dp[i][j] = 1 + Math.min(dp[i - 1][j - 1], Math.min(dp[i - 1][j], dp[i][j - 1]));
                }
            }
        }
        return dp[s1.length()][s2.length()];
    }

    @FXML
    private void handleSubmit() {
        String titre = titreField.getText();
        String description = descriptionArea.getText();

        if (titre.isEmpty() || description.isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Champs vides", "Veuillez remplir tous les champs !");
            return;
        }

        // Vérification des mots interdits
        if (containsBadWords(titre) || containsBadWords(description)) {
            showAlert(Alert.AlertType.ERROR, "Contenu inapproprié", 
                "Votre réclamation contient des mots inappropriés. Veuillez modifier votre texte.");
            return;
        }

        if (currentUser == null) {
            showAlert(Alert.AlertType.ERROR, "Utilisateur non connecté", "Impossible de soumettre sans utilisateur !");
            return;
        }

        // Vérification de la limite quotidienne
        if (hasReachedDailyLimit()) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setTitle("Limite atteinte");
            alert.setHeaderText(null);
            alert.setContentText("Vous avez atteint la limite de " + MAX_RECLAMATIONS_PER_DAY + " réclamations par jour.");
            
            // Style personnalisé pour l'alerte
            DialogPane dialogPane = alert.getDialogPane();
            dialogPane.setStyle("-fx-background-color: #FFF0F5;"); // Rose pastel clair
            dialogPane.getStyleClass().add("custom-alert");
            
            // Style pour le contenu
            Label contentLabel = new Label("Vous avez atteint la limite de " + MAX_RECLAMATIONS_PER_DAY + " réclamations par jour.");
            contentLabel.setStyle("-fx-text-fill: #333333; -fx-font-size: 14px; -fx-font-family: 'Segoe UI';");
            dialogPane.setContent(contentLabel);
            
            // Style pour les boutons
            ButtonType buttonType = alert.getButtonTypes().get(0);
            Button button = (Button) dialogPane.lookupButton(buttonType);
            button.setStyle("-fx-background-color: #bd454f; -fx-text-fill: white; -fx-font-weight: bold;");
            
            // Attendre que l'utilisateur clique sur OK
            alert.showAndWait();
            
            // Rediriger vers la page d'accueil
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/HomePage.fxml"));
                Parent root = loader.load();
                Stage stage = (Stage) titreField.getScene().getWindow();
                stage.setScene(new Scene(root));
                stage.show();
            } catch (IOException e) {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de la redirection : " + e.getMessage());
            }
            return;
        }

        // Vérification des réclamations similaires
        if (isSimilarToRecentReclamation(titre, description)) {
            showAlert(Alert.AlertType.ERROR, "Réclamation similaire", 
                "Vous avez déjà soumis une réclamation similaire récemment.");
            return;
        }

        Reclamation reclamation = new Reclamation();
        reclamation.setTitre(titre);
        reclamation.setDescription(description);
        reclamation.setStatus(Reclamation.STATUS_PENDING);
        reclamation.setDateSoumission(LocalDateTime.now());
        reclamation.setUser(currentUser);

        try {
            serviceReclamation.insert(reclamation);
            showAlert(Alert.AlertType.INFORMATION, "Succès", "Réclamation soumise avec succès !");
            clearForm();
            navigateToHomePage();
        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur SQL", "Erreur lors de l'insertion: " + e.getMessage());
        }
    }

    private void navigateToHomePage() {
        try {
            // Simplified loading of HomePage.fxml using static FXMLLoader.load()
            Parent root = FXMLLoader.load(getClass().getResource("/HomePage.fxml"));
            Stage stage = (Stage) titreField.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur de navigation", "Impossible de charger la page d'accueil: " + e.getMessage());
        }
    }

    private void clearForm() {
        titreField.clear();
        descriptionArea.clear();
    }

    private void showAlert(Alert.AlertType alertType, String title, String message) {
        Alert alert = new Alert(alertType);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(message);
        alert.show();
    }
}