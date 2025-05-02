package tn.fermista.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.HashMap;
import java.util.Map;
import java.util.Arrays;
import java.util.Random;
import java.util.Set;
import java.util.HashSet;

public class ReclamationAssistant {
    // Liste des problèmes fréquents
    private static final String[] COMMON_PROBLEMS = {
        "Problème de livraison",
        "Produit endommagé",
        "Retard de paiement",
        "Service client insatisfaisant",
        "Problème technique",
        "Facturation incorrecte",
        "Produit manquant",
        "Qualité insatisfaisante"
    };

    // Liste des mots-clés pour la catégorisation
    private static final String[][] CATEGORY_KEYWORDS = {
        {"livraison", "livrer", "expédition", "transport"}, // Livraison
        {"endommager", "cassé", "brisé", "défectueux"}, // Produit
        {"paiement", "facture", "argent", "remboursement"}, // Paiement
        {"service", "client", "réponse", "assistance"}, // Service
        {"technique", "bug", "erreur", "dysfonctionnement"}, // Technique
        {"facturation", "prix", "coût", "tarif"}, // Facturation
        {"manquant", "absent", "perdu", "disparu"}, // Manquant
        {"qualité", "mauvais", "insatisfaisant", "défectueux"} // Qualité
    };

    // Liste des suggestions de formulation
    private static final String[] FORMULATION_SUGGESTIONS = {
        "Je souhaite signaler un problème concernant...",
        "J'ai rencontré une difficulté avec...",
        "Je vous informe d'un incident sur...",
        "Je vous fais part d'une préoccupation concernant..."
    };

    // Dictionnaire de corrections orthographiques et grammaticales
    private static final Map<String, String> CORRECTIONS = new HashMap<>();
    static {
        // Corrections de base
        CORRECTIONS.put("jé", "je");
        CORRECTIONS.put("souhaité", "souhaite");
        CORRECTIONS.put("votré", "votre");
        CORRECTIONS.put("problèmé", "problème");
        CORRECTIONS.put("intélligent", "intelligent");
        CORRECTIONS.put("non", "ne");
        CORRECTIONS.put("fonctionnel", "fonctionne");
        CORRECTIONS.put("cest", "c'est");
        CORRECTIONS.put("uné", "un");
        CORRECTIONS.put("problemé", "problème");
        CORRECTIONS.put("gravé", "grave");
        CORRECTIONS.put("jai", "j'ai");
        CORRECTIONS.put("améliration", "amélioration");
        CORRECTIONS.put("améliore", "améliorer");
        CORRECTIONS.put("quy", "qui");
        
        // Articles et déterminants
        CORRECTIONS.put("le", "le");
        CORRECTIONS.put("la", "la");
        CORRECTIONS.put("les", "les");
        CORRECTIONS.put("un", "un");
        CORRECTIONS.put("une", "une");
        CORRECTIONS.put("des", "des");
        
        // Verbes courants
        CORRECTIONS.put("souhaite", "souhaite");
        CORRECTIONS.put("porter", "porter");
        CORRECTIONS.put("fonctionne", "fonctionne");
        CORRECTIONS.put("est", "est");
        CORRECTIONS.put("ai", "ai");
        CORRECTIONS.put("cliqué", "cliqué");
    }

    // Règles de correction automatique
    private static final Map<Pattern, String> AUTO_CORRECTIONS = new HashMap<>();
    static {
        // Correction des accents
        AUTO_CORRECTIONS.put(Pattern.compile("é([^a-z]|$)"), "e$1");
        AUTO_CORRECTIONS.put(Pattern.compile("è([^a-z]|$)"), "e$1");
        AUTO_CORRECTIONS.put(Pattern.compile("ê([^a-z]|$)"), "e$1");
        AUTO_CORRECTIONS.put(Pattern.compile("à([^a-z]|$)"), "a$1");
        AUTO_CORRECTIONS.put(Pattern.compile("ù([^a-z]|$)"), "u$1");
        
        // Correction des apostrophes
        AUTO_CORRECTIONS.put(Pattern.compile("([a-z])'([a-z])"), "$1'$2");
        AUTO_CORRECTIONS.put(Pattern.compile("([a-z]) ([a-z])"), "$1 $2");
        
        // Correction des répétitions de lettres
        AUTO_CORRECTIONS.put(Pattern.compile("(.)\\1{2,}"), "$1$1");
    }

    // Phrases d'introduction pour différents types de problèmes
    private static final String[] INTRODUCTIONS = {
        "Je souhaite vous informer que ",
        "Je tiens à vous signaler que ",
        "Je vous fais part du fait que ",
        "Je vous informe que ",
        "Je constate que "
    };

    // Liste des connecteurs logiques pour enrichir le texte
    private static final String[] CONNECTORS = {
        "En effet, ",
        "De plus, ",
        "Par ailleurs, ",
        "En outre, ",
        "En conséquence, ",
        "Par conséquent, ",
        "C'est pourquoi, ",
        "Ainsi, ",
        "De ce fait, ",
        "En l'occurrence, "
    };

    // Liste des mots de transition pour améliorer la liaison entre phrases
    private static final String[] TRANSITION_WORDS = {
        "Tout d'abord, ", "Premièrement, ", "Pour commencer, ",
        "Ensuite, ", "De plus, ", "Par ailleurs, ", "En outre, ",
        "Également, ", "Puis, ", "D'autre part, ", "En ce qui concerne, ",
        "À propos de, ", "Concernant, ", "Quant à, ", "Relativement à, ",
        "Finalement, ", "En conclusion, ", "Pour conclure, ", "En résumé, ",
        "En définitive, ", "Pour terminer, "
    };

    public static List<String> getCommonProblems() {
        List<String> problems = new ArrayList<>();
        for (String problem : COMMON_PROBLEMS) {
            problems.add(problem);
        }
        return problems;
    }

    public static String suggestFormulation(String input) {
        // Vérifier la grammaire de base
        if (!input.matches("^[A-Z].*")) {
            input = input.substring(0, 1).toUpperCase() + input.substring(1);
        }
        if (!input.endsWith(".")) {
            input += ".";
        }

        // Ajouter une formulation professionnelle
        String randomSuggestion = FORMULATION_SUGGESTIONS[(int)(Math.random() * FORMULATION_SUGGESTIONS.length)];
        return randomSuggestion + " " + input;
    }

    public static String categorizeProblem(String description) {
        String lowerDesc = description.toLowerCase();
        for (int i = 0; i < CATEGORY_KEYWORDS.length; i++) {
            for (String keyword : CATEGORY_KEYWORDS[i]) {
                if (lowerDesc.contains(keyword)) {
                    return COMMON_PROBLEMS[i];
                }
            }
        }
        return "Autre problème";
    }

    public static String improveDescription(String description) {
        if (description == null || description.isEmpty()) {
            return "";
        }

        // 1. Correction des fautes d'orthographe et de grammaire
        String correctedText = correctSpellingAndGrammar(description);

        // 2. Ajout d'une introduction appropriée
        correctedText = addIntroduction(correctedText);

        // 3. Normalisation des espaces et de la ponctuation
        correctedText = normalizeText(correctedText);

        // 4. Amélioration de la liaison entre les phrases
        correctedText = improveTextCoherence(correctedText);

        // 5. Ajout de connecteurs logiques
        correctedText = addLogicalConnectors(correctedText);

        // 6. Ajout de retour à la ligne
        correctedText = addLineBreaks(correctedText);

        return correctedText;
    }

    // Nouvelle méthode pour améliorer la cohérence du texte
    private static String improveTextCoherence(String text) {
        String[] sentences = text.split("[.!?]");
        StringBuilder coherentText = new StringBuilder();
        
        for (int i = 0; i < sentences.length; i++) {
            String sentence = sentences[i].trim();
            if (!sentence.isEmpty()) {
                // Ajouter des mots de transition pour les phrases intermédiaires
                if (i > 0 && i < sentences.length - 1) {
                    // Utiliser un mot de transition aléatoire pour les phrases du milieu
                    Random random = new Random();
                    String transitionWord = TRANSITION_WORDS[random.nextInt(TRANSITION_WORDS.length)];
                    
                    // Éviter d'ajouter un mot de transition si la phrase commence déjà par un
                    boolean alreadyHasTransition = false;
                    for (String transition : TRANSITION_WORDS) {
                        if (sentence.toLowerCase().startsWith(transition.toLowerCase())) {
                            alreadyHasTransition = true;
                            break;
                        }
                    }
                    
                    if (!alreadyHasTransition) {
                        sentence = transitionWord + sentence.substring(0, 1).toLowerCase() + sentence.substring(1);
                    }
                }
                
                coherentText.append(sentence).append(". ");
            }
        }
        
        return coherentText.toString().trim();
    }
    
    /**
     * Résume un texte en une longueur spécifiée (approximativement)
     * @param text Le texte à résumer
     * @param maxLength La longueur maximale approximative du résumé
     * @return Le résumé du texte
     */
    public static String summarizeText(String text, int maxLength) {
        if (text == null || text.isEmpty()) {
            return "";
        }
        
        // Extraire les phrases du texte
        String[] sentences = text.split("[.!?]");
        StringBuilder summary = new StringBuilder();
        
        // Sélectionner les phrases les plus importantes (première phrase et phrases avec des mots-clés)
        if (sentences.length > 0) {
            // Toujours inclure la première phrase qui contient généralement l'information principale
            String firstSentence = sentences[0].trim();
            if (!firstSentence.isEmpty()) {
                summary.append(firstSentence).append(". ");
            }
            
            // Chercher des phrases avec des mots-clés importants
            Set<String> importantKeywords = new HashSet<>(Arrays.asList(
                "problème", "urgent", "important", "critique", "défaut", "erreur", 
                "dysfonctionnement", "panne", "réclamation", "insatisfaction"
            ));
            
            // Parcourir les autres phrases pour trouver celles avec des mots-clés
            for (int i = 1; i < sentences.length && summary.length() < maxLength; i++) {
                String sentence = sentences[i].trim();
                if (!sentence.isEmpty()) {
                    String lowerSentence = sentence.toLowerCase();
                    
                    // Vérifier si la phrase contient des mots-clés importants
                    boolean containsKeyword = false;
                    for (String keyword : importantKeywords) {
                        if (lowerSentence.contains(keyword)) {
                            containsKeyword = true;
                            break;
                        }
                    }
                    
                    if (containsKeyword) {
                        // Ajouter la phrase au résumé
                        if (summary.length() + sentence.length() + 2 <= maxLength) {
                            summary.append(sentence).append(". ");
                        } else {
                            // Si la phrase est trop longue, la tronquer
                            int remainingLength = maxLength - summary.length() - 3;
                            if (remainingLength > 10) { // Assurer qu'il y a assez d'espace pour une phrase significative
                                summary.append(sentence.substring(0, remainingLength)).append("...");
                            }
                            break;
                        }
                    }
                }
            }
        }
        
        // Si le résumé est trop court, ajouter d'autres phrases jusqu'à atteindre la longueur maximale
        if (summary.length() < maxLength / 2 && sentences.length > 1) {
            for (int i = 1; i < sentences.length && summary.length() < maxLength; i++) {
                String sentence = sentences[i].trim();
                if (!sentence.isEmpty() && !summary.toString().contains(sentence)) {
                    if (summary.length() + sentence.length() + 2 <= maxLength) {
                        summary.append(sentence).append(". ");
                    } else {
                        break;
                    }
                }
            }
        }
        
        return summary.toString().trim();
    }
    
    private static String addLogicalConnectors(String text) {
        // Diviser le texte en phrases
        String[] sentences = text.split("[.!?]");
        StringBuilder connected = new StringBuilder();
        
        // Classifier les connecteurs par type
        Map<String, List<String>> connectorsByType = new HashMap<>();
        
        // Connecteurs d'introduction (pour la première phrase)
        List<String> introConnectors = Arrays.asList(
            "Je souhaite signaler que ", 
            "Je tiens à vous informer que ", 
            "Je vous fais part du fait que "
        );
        
        // Connecteurs d'addition (pour ajouter des informations)
        List<String> additionConnectors = Arrays.asList(
            "De plus, ", 
            "En outre, ", 
            "Par ailleurs, ", 
            "Également, "
        );
        
        // Connecteurs de cause (pour expliquer pourquoi)
        List<String> causeConnectors = Arrays.asList(
            "En effet, ", 
            "Car ", 
            "Étant donné que ", 
            "Puisque "
        );
        
        // Connecteurs de conséquence (pour montrer les résultats)
        List<String> consequenceConnectors = Arrays.asList(
            "Par conséquent, ", 
            "En conséquence, ", 
            "C'est pourquoi ", 
            "Ainsi, "
        );
        
        // Connecteurs de conclusion (pour la dernière phrase)
        List<String> conclusionConnectors = Arrays.asList(
            "En conclusion, ", 
            "Pour conclure, ", 
            "En résumé, ", 
            "Finalement, "
        );
        
        // Garder trace des connecteurs déjà utilisés
        Set<String> usedConnectors = new HashSet<>();
        
        // Traiter chaque phrase
        for (int i = 0; i < sentences.length; i++) {
            String sentence = sentences[i].trim();
            if (sentence.isEmpty()) continue;
            
            // Déterminer le type de connecteur à utiliser en fonction de la position et du contenu
            List<String> appropriateConnectors;
            
            if (i == 0) {
                // Première phraseutiliser un connecteur d'introduction
                appropriateConnectors = introConnectors;
            } else if (i == sentences.length - 1) {
                // Dernière phrasetiliser un connecteur de conclusion
                appropriateConnectors = conclusionConnectors;
            } else {
                // Phrases intermédiairesanalyser le contenu pour choisir le bon type
                String lowerSentence = sentence.toLowerCase();
                
                if (lowerSentence.contains("car") || lowerSentence.contains("parce") || 
                    lowerSentence.contains("raison") || lowerSentence.contains("cause")) {
                    // Phrase explicative
                    appropriateConnectors = causeConnectors;
                } else if (lowerSentence.contains("donc") || lowerSentence.contains("ainsi") || 
                           lowerSentence.contains("conséquence") || lowerSentence.contains("résultat")) {
                    // Phrase de conséquence
                    appropriateConnectors = consequenceConnectors;
                } else {
                    // Par défaut, utiliser des connecteurs d'addition
                    appropriateConnectors = additionConnectors;
                }
            }
            
            // Vérifier si la phrase commence déjà par un connecteur
            boolean alreadyHasConnector = false;
            for (List<String> connectorList : Arrays.asList(introConnectors, additionConnectors, 
                                                           causeConnectors, consequenceConnectors, 
                                                           conclusionConnectors)) {
                for (String conn : connectorList) {
                    if (sentence.toLowerCase().startsWith(conn.toLowerCase())) {
                        alreadyHasConnector = true;
                        break;
                    }
                }
                if (alreadyHasConnector) break;
            }
            
            // Si la phrase n'a pas déjà un connecteur, en ajouter un
            if (!alreadyHasConnector) {
                // Choisir un connecteur non utilisé si possible
                String connector = null;
                List<String> availableConnectors = new ArrayList<>(appropriateConnectors);
                availableConnectors.removeAll(usedConnectors);
                
                if (!availableConnectors.isEmpty()) {
                    // Utiliser un connecteur non utilisé
                    connector = availableConnectors.get(new Random().nextInt(availableConnectors.size()));
                } else {
                    // Tous les connecteurs de ce type ont été utilisés, en choisir un au hasard
                    connector = appropriateConnectors.get(new Random().nextInt(appropriateConnectors.size()));
                }
                
                usedConnectors.add(connector);
                connected.append(connector);
            }
            
            // Ajouter la phrase
            connected.append(sentence).append(". ");
        }
        
        return connected.toString().trim();
    }

    private static String addLineBreaks(String text) {
        String[] sentences = text.split("[.!?]");
        StringBuilder formatted = new StringBuilder();
        
        for (String sentence : sentences) {
            String trimmed = sentence.trim();
            if (!trimmed.isEmpty()) {
                String[] words = trimmed.split("\\s+");
                int wordCount = 0;
                
                for (String word : words) {
                    formatted.append(word).append(" ");
                    wordCount++;
                    
                    if (wordCount % 13 == 0) {
                        formatted.append("\n");
                    }
                }
                
                formatted.append(". ");
            }
        }
        
        return formatted.toString().trim();
    }

    public static String generateSmartTitle(String description) {
        String category = categorizeProblem(description);
        String[] words = description.split("\\s+");
        
        // Prendre les 3-5 premiers mots significatifs
        StringBuilder title = new StringBuilder();
        int count = 0;
        for (String word : words) {
            if (word.length() > 3 && !word.matches("^(le|la|les|un|une|des|et|ou|mais|donc|or|ni|car)$")) {
                title.append(word).append(" ");
                count++;
                if (count >= 5) break;
            }
        }
        
        return (title.length() > 0) ? title.toString().trim() : category;
    }

    private static String correctSpellingAndGrammar(String text) {
        if (text == null || text.isEmpty()) {
            return "";
        }
        
        // Correction des mots mal orthographiés
        String[] words = text.split("\\s+");
        StringBuilder corrected = new StringBuilder();
        
        for (String word : words) {
            String lowerWord = word.toLowerCase();
            if (CORRECTIONS.containsKey(lowerWord)) {
                corrected.append(CORRECTIONS.get(lowerWord)).append(" ");
            } else {
                corrected.append(word).append(" ");
            }
        }
        
        String result = corrected.toString().trim();
        
        // Application des règles de correction automatique
        for (Map.Entry<Pattern, String> entry : AUTO_CORRECTIONS.entrySet()) {
            result = entry.getKey().matcher(result).replaceAll(entry.getValue());
        }
        
        // Correction de la première lettre (majuscule)
        if (!result.isEmpty()) {
            result = result.substring(0, 1).toUpperCase() + result.substring(1);
        }
        
        return result;
    }

    private static String addIntroduction(String text) {
        if (text == null || text.isEmpty()) {
            return "";
        }
        
        // Vérifier si le texte commence déjà par une introduction
        for (String intro : INTRODUCTIONS) {
            if (text.startsWith(intro)) {
                return text; // Le texte a déjà une introduction
            }
        }
        
        // Ajouter une introduction aléatoire
        String randomIntro = INTRODUCTIONS[(int)(Math.random() * INTRODUCTIONS.length)];
        
        // Mettre la première lettre du texte en minuscule si elle est en majuscule
        if (!text.isEmpty() && Character.isUpperCase(text.charAt(0))) {
            text = text.substring(0, 1).toLowerCase() + text.substring(1);
        }
        
        return randomIntro + text;
    }

    private static String normalizeText(String text) {
        if (text == null || text.isEmpty()) {
            return "";
        }
        
        // Normaliser les espaces
        text = text.replaceAll("\\s+", " ").trim();
        
        // Normaliser la ponctuation
        text = text.replaceAll("\\s+([.,;:!?])", "$1");
        text = text.replaceAll("([.,;:!?])([A-Za-zÀ-ÿ])", "$1 $2");
        
        // Normaliser les apostrophes
        text = text.replaceAll("\\s+'", "'");
        
        // Ajouter un point final si nécessaire
        if (!text.endsWith(".") && !text.endsWith("!") && !text.endsWith("?")) {
            text += ".";
        }
        
        return text;
    }
}