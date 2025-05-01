package tn.fermista.utils;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Pattern;
import java.util.HashMap;
import java.util.Map;
import java.util.Arrays;

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

    // Connecteurs logiques pour enrichir le texte
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

        // 4. Ajout de connecteurs logiques
        correctedText = addLogicalConnectors(correctedText);

        // 5. Ajout de retour à la ligne
        correctedText = addLineBreaks(correctedText);

        return correctedText;
    }

    private static String correctSpellingAndGrammar(String text) {
        // 1. Correction automatique des motifs répétitifs
        for (Map.Entry<Pattern, String> entry : AUTO_CORRECTIONS.entrySet()) {
            text = entry.getKey().matcher(text).replaceAll(entry.getValue());
        }

        // 2. Correction mot par mot
        String[] words = text.split("\\s+");
        StringBuilder corrected = new StringBuilder();
        
        for (int i = 0; i < words.length; i++) {
            String word = words[i].toLowerCase();
            String correctedWord = word;
            
            // Vérifier si le mot est dans le dictionnaire
            if (CORRECTIONS.containsKey(word)) {
                correctedWord = CORRECTIONS.get(word);
            } else {
                // Essayer de corriger les fautes courantes
                correctedWord = tryAutoCorrect(word);
            }
            
            // Gestion des articles et déterminants
            if (i > 0) {
                String previousWord = words[i-1].toLowerCase();
                if (previousWord.equals("j'ai") || previousWord.equals("je") || previousWord.equals("c'est")) {
                    if (word.equals("le") || word.equals("la") || word.equals("les")) {
                        correctedWord = "le";
                    }
                }
            }
            
            corrected.append(correctedWord).append(" ");
        }
        
        return corrected.toString().trim();
    }

    private static String tryAutoCorrect(String word) {
        // Correction des accents
        word = word.replaceAll("é([^a-z]|$)", "e$1");
        word = word.replaceAll("è([^a-z]|$)", "e$1");
        word = word.replaceAll("ê([^a-z]|$)", "e$1");
        word = word.replaceAll("à([^a-z]|$)", "a$1");
        word = word.replaceAll("ù([^a-z]|$)", "u$1");
        
        // Correction des apostrophes
        word = word.replaceAll("([a-z])'([a-z])", "$1'$2");
        word = word.replaceAll("([a-z]) ([a-z])", "$1 $2");
        
        // Correction des répétitions de lettres
        word = word.replaceAll("(.)\\1{2,}", "$1$1");
        
        return word;
    }

    private static String addIntroduction(String text) {
        // Vérifier si le texte commence déjà par une introduction
        boolean hasIntroduction = false;
        for (String intro : INTRODUCTIONS) {
            if (text.toLowerCase().startsWith(intro.toLowerCase())) {
                hasIntroduction = true;
                break;
            }
        }

        if (!hasIntroduction) {
            // Sélectionner une introduction aléatoire
            String intro = INTRODUCTIONS[(int)(Math.random() * INTRODUCTIONS.length)];
            return intro + text.substring(0, 1).toLowerCase() + text.substring(1);
        }

        return text;
    }

    private static String normalizeText(String text) {
        return text.replaceAll("\\s+", " ")
                  .replaceAll("\\s+([.,!?])", "$1")
                  .replaceAll("([.,!?])\\s*", "$1 ")
                  .trim();
    }

    private static String addLogicalConnectors(String text) {
        String[] sentences = text.split("[.!?]");
        StringBuilder connected = new StringBuilder();
        
        for (int i = 0; i < sentences.length; i++) {
            String sentence = sentences[i].trim();
            if (!sentence.isEmpty()) {
                if (i > 0) {
                    // Ajouter un connecteur logique
                    String connector = CONNECTORS[(int)(Math.random() * CONNECTORS.length)];
                    connected.append(connector);
                }
                connected.append(sentence).append(". ");
            }
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
} 