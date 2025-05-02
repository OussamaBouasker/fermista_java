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
import java.util.HashSet;
import java.util.List;
import java.util.Set;
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
        String title = titreField.getText();

        if (title.isEmpty()) {
            showAlert(Alert.AlertType.WARNING, "Titre vide",
                    "Veuillez d'abord entrer un titre pour votre réclamation.");
            return;
        }

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

                // Si la description est vide, générer une description basée sur le titre
                if (currentText.isEmpty()) {
                    String fullText = generateStructuredReclamation(title);
                    // Résumer le texte généré en une ligne et demie (environ 150 caractères)
                    String summary = ReclamationAssistant.summarizeText(fullText, 150);
                    return fullText + "\n\nRésumé: " + summary;
                } else {
                    // Sinon, améliorer la description existante en tenant compte du titre
                    String improvedText = improveDescriptionWithTitle(currentText, title);
                    // Résumer le texte amélioré en une ligne et demie
                    String summary = ReclamationAssistant.summarizeText(improvedText, 150);
                    return improvedText + "\n\nRésumé: " + summary;
                }
            }
        };

        // Gérer la fin de la tâche
        improvementTask.setOnSucceeded(e -> {
            String improvedText = improvementTask.getValue();
            animateTextGeneration(improvedText);
        });

        // Démarrer la tâche dans un nouveau thread
        new Thread(improvementTask).start();
    }

    private String improveDescriptionWithTitle(String description, String title) {
        // Améliorer la description en tenant compte du titre
        String improvedText = ReclamationAssistant.improveDescription(description);

        // Analyser le titre pour extraire des mots-clés
        String[] keywords = title.toLowerCase().split("\\s+");

        // Vérifier si la description améliorée contient déjà des éléments du titre
        boolean containsTitleElements = false;
        for (String keyword : keywords) {
            if (keyword.length() > 3 && improvedText.toLowerCase().contains(keyword)) {
                containsTitleElements = true;
                break;
            }
        }

        // Si la description ne contient pas d'éléments du titre, les ajouter
        if (!containsTitleElements) {
            // Ajouter une référence au titre au début de la description avec une formulation variée
            String[] titleReferences = {
                    "Concernant \"" + title + "\", ",
                    "Au sujet de \"" + title + "\", ",
                    "À propos de \"" + title + "\", ",
                    "En référence à \"" + title + "\", ",
                    "Relativement à \"" + title + "\", "
            };
            String titleRef = titleReferences[(int)(Math.random() * titleReferences.length)];
            improvedText = titleRef + improvedText;

            // Ajouter des détails spécifiques basés sur les mots-clés du titre
            StringBuilder additionalDetails = new StringBuilder();

            // Créer un ensemble pour suivre les catégories déjà traitées
            Set<String> processedCategories = new HashSet<>();

            for (String keyword : keywords) {
                if (keyword.length() > 3) { // Ignorer les mots courts
                    String category = getCategoryFromKeyword(keyword);

                    // Éviter de traiter plusieurs fois la même catégorie
                    if (category != null && !processedCategories.contains(category)) {
                        processedCategories.add(category);

                        switch (category) {
                            // Catégories existantes
                            case "livraison":
                                additionalDetails.append("\nLa livraison n'a pas été effectuée dans les délais prévus.");
                                additionalDetails.append("\nJ'attends depuis plusieurs jours sans avoir reçu de mise à jour sur le statut de ma commande.");
                                break;
                            case "produit":
                                additionalDetails.append("\nLe produit reçu ne correspond pas à la description.");
                                additionalDetails.append("\nLes caractéristiques annoncées ne correspondent pas à ce que j'ai reçu.");
                                break;
                            case "service":
                                additionalDetails.append("\nLe service fourni n'est pas à la hauteur de mes attentes.");
                                additionalDetails.append("\nLa qualité du service est bien inférieure à ce qui était promis.");
                                break;
                            case "facture":
                                additionalDetails.append("\nJ'ai rencontré des difficultés avec le processus de paiement.");
                                additionalDetails.append("\nLe montant facturé ne correspond pas au prix annoncé initialement.");
                                break;
                            case "qualité":
                                additionalDetails.append("\nLa qualité du produit/service n'est pas satisfaisante.");
                                additionalDetails.append("\nJ'ai constaté plusieurs défauts qui rendent l'utilisation difficile.");
                                break;

                            // Nouvelles catégories spécifiques à l'agriculture
                            case "vache":
                                additionalDetails.append("\nJ'ai constaté un problème concernant l'état de santé ou le comportement de la vache.");
                                additionalDetails.append("\nLes symptômes observés sont préoccupants et nécessitent une attention immédiate.");
                                additionalDetails.append("\nJ'ai remarqué une baisse significative de la production laitière ces derniers jours.");
                                break;
                            case "collier":
                                additionalDetails.append("\nLe collier connecté ne fonctionne pas correctement ou ne transmet pas les données.");
                                additionalDetails.append("\nJ'ai déjà essayé de le réinitialiser sans succès.");
                                additionalDetails.append("\nL'application mobile associée au collier affiche des erreurs fréquentes.");
                                break;
                            case "formation":
                                additionalDetails.append("\nLa formation n'a pas couvert tous les aspects mentionnés dans le programme.");
                                additionalDetails.append("\nLe formateur n'a pas été en mesure de répondre à toutes mes questions techniques.");
                                additionalDetails.append("\nLes supports de cours fournis étaient incomplets ou obsolètes.");
                                break;
                            case "commande":
                                additionalDetails.append("\nMa commande n'est pas arrivée dans les délais indiqués lors de l'achat.");
                                additionalDetails.append("\nCertains articles de ma commande sont manquants ou endommagés.");
                                additionalDetails.append("\nJe n'ai pas reçu de confirmation de livraison comme promis.");
                                break;
                            case "équipement":
                                additionalDetails.append("\nL'équipement agricole présente des dysfonctionnements importants.");
                                additionalDetails.append("\nLes pièces de rechange fournies ne sont pas compatibles avec le modèle.");
                                break;
                            case "alimentation":
                                additionalDetails.append("\nLa qualité des aliments pour bétail n'est pas conforme aux standards annoncés.");
                                additionalDetails.append("\nJ'ai observé des effets négatifs sur la santé de mes animaux après utilisation.");
                                break;
                        }
                    }
                }
            }

            if (additionalDetails.length() > 0) {
                improvedText += "\n\nDétails supplémentaires concernant \"" + title + "\" :" + additionalDetails.toString();
            }
        }

        return improvedText;
    }

    // Nouvelle méthode pour déterminer la catégorie à partir d'un mot-clé
    private String getCategoryFromKeyword(String keyword) {
        // Catégories de livraison
        if (keyword.equals("livraison") || keyword.equals("livrer") ||
                keyword.equals("expédition") || keyword.equals("transport") ||
                keyword.equals("délai") || keyword.equals("retard")) {
            return "livraison";
        }

        // Catégories de produit
        if (keyword.equals("produit") || keyword.equals("article") ||
                keyword.equals("marchandise") || keyword.equals("colis") ||
                keyword.equals("défectueux") || keyword.equals("endommagé")) {
            return "produit";
        }

        // Catégories de service
        if (keyword.equals("service") || keyword.equals("prestation") ||
                keyword.equals("assistance") || keyword.equals("aide") ||
                keyword.equals("support") || keyword.equals("conseil")) {
            return "service";
        }

        // Catégories de facturation
        if (keyword.equals("facture") || keyword.equals("paiement") ||
                keyword.equals("prix") || keyword.equals("coût") ||
                keyword.equals("tarif") || keyword.equals("remboursement")) {
            return "facture";
        }

        // Catégories de qualité
        if (keyword.equals("qualité") || keyword.equals("mauvais") ||
                keyword.equals("insatisfaisant") || keyword.equals("médiocre") ||
                keyword.equals("défaut") || keyword.equals("problème")) {
            return "qualité";
        }

        // Catégories spécifiques à l'agriculture
        if (keyword.equals("vache") || keyword.equals("vaches") ||
                keyword.equals("bovin") || keyword.equals("bovins") ||
                keyword.equals("lait") || keyword.equals("traite")) {
            return "vache";
        }

        if (keyword.equals("collier") || keyword.equals("colliers") ||
                keyword.equals("tracker") || keyword.equals("traceur") ||
                keyword.equals("connecté") || keyword.equals("suivi")) {
            return "collier";
        }

        if (keyword.equals("formation") || keyword.equals("formations") ||
                keyword.equals("cours") || keyword.equals("atelier") ||
                keyword.equals("séminaire") || keyword.equals("apprentissage")) {
            return "formation";
        }

        if (keyword.equals("commande") || keyword.equals("commandes") ||
                keyword.equals("achat") || keyword.equals("acheter") ||
                keyword.equals("commander") || keyword.equals("acquisition")) {
            return "commande";
        }

        if (keyword.equals("équipement") || keyword.equals("matériel") ||
                keyword.equals("machine") || keyword.equals("outil") ||
                keyword.equals("appareil") || keyword.equals("instrument")) {
            return "équipement";
        }

        if (keyword.equals("alimentation") || keyword.equals("nourriture") ||
                keyword.equals("fourrage") || keyword.equals("aliment") ||
                keyword.equals("nutrition") || keyword.equals("foin")) {
            return "alimentation";
        }

        return null;
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
                    // Catégories existantes
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

                    // Nouvelles catégories spécifiques à l'agriculture
                    case "vache":
                    case "vaches":
                    case "bovin":
                    case "bovins":
                        structuredText.append("J'ai constaté un problème avec ma vache qui présente les symptômes suivants :\n");
                        structuredText.append("- Changement de comportement inhabituel\n");
                        structuredText.append("- Possible problème de santé nécessitant une intervention\n");
                        structuredText.append("- Inquiétudes concernant la production laitière\n");
                        break;
                    case "collier":
                    case "colliers":
                    case "tracker":
                    case "traceur":
                        structuredText.append("Le collier connecté présente les dysfonctionnements suivants :\n");
                        structuredText.append("- Problèmes de connexion avec l'application\n");
                        structuredText.append("- Données incorrectes ou manquantes\n");
                        structuredText.append("- Batterie se déchargeant trop rapidement\n");
                        break;
                    case "formation":
                    case "formations":
                    case "cours":
                    case "atelier":
                        structuredText.append("La formation n'a pas répondu à mes attentes pour les raisons suivantes :\n");
                        structuredText.append("- Contenu incomplet par rapport au programme annoncé\n");
                        structuredText.append("- Manque d'exercices pratiques\n");
                        structuredText.append("- Niveau inadapté à mes besoins\n");
                        break;
                    case "commande":
                    case "commandes":
                    case "achat":
                        structuredText.append("Ma commande présente les problèmes suivants :\n");
                        structuredText.append("- Retard de livraison significatif\n");
                        structuredText.append("- Articles manquants ou endommagés\n");
                        structuredText.append("- Erreur dans la facturation\n");
                        break;
                }
            }
        }

        // Conclusion
        structuredText.append("\nJe vous serais reconnaissant de bien vouloir examiner cette situation et de me proposer une solution appropriée.\n");
        structuredText.append("Dans l'attente de votre retour, je vous prie d'agréer mes salutations distinguées.");

        return structuredText.toString();
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

    // Supprimer cette méthode car elle est dupliquée
    // private String generateStructuredReclamation(String title) {
    //     StringBuilder structuredText = new StringBuilder();
    //
    //     // Introduction
    //     structuredText.append("Je souhaite porter à votre attention un problème concernant : ").append(title).append(".\n\n");
    //
    //     // Analyse des mots-clés du titre
    //     String[] keywords = title.toLowerCase().split("\\s+");
    //
    //     // Ajout de détails contextuels basés sur les mots-clés
    //     for (String keyword : keywords) {
    //         if (keyword.length() > 3) { // Ignorer les mots courts
    //             switch (keyword) {
    //                 case "livraison":
    //                 case "livrer":
    //                     structuredText.append("La livraison n'a pas été effectuée dans les délais prévus.\n");
    //                     break;
    //                 case "produit":
    //                 case "article":
    //                     structuredText.append("Le produit reçu ne correspond pas à la description.\n");
    //                     break;
    //                 case "service":
    //                     structuredText.append("Le service fourni n'est pas à la hauteur de mes attentes.\n");
    //                     break;
    //                 case "facture":
    //                 case "paiement":
    //                     structuredText.append("J'ai rencontré des difficultés avec le processus de paiement.\n");
    //                     break;
    //                 case "qualité":
    //                     structuredText.append("La qualité du produit/service n'est pas satisfaisante.\n");
    //                     break;
    //             }
    //         }
    //     }
    //
    //     // Conclusion
    //     structuredText.append("\nJe vous serais reconnaissant de bien vouloir examiner cette situation et de me proposer une solution appropriée.\n");
    //     structuredText.append("Dans l'attente de votre retour, je vous prie d'agréer mes salutations distinguées.");
    //
    //     return structuredText.toString();
    // }

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