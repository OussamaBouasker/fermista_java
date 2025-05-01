package tn.fermista.utils;

import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.KeyValue;
import javafx.animation.Timeline;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.effect.DropShadow;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Color;
import javafx.scene.text.Font;
import javafx.scene.text.FontWeight;
import javafx.scene.text.TextAlignment;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javafx.scene.shape.Circle;
import javafx.scene.text.Text;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;

/**
 * Une classe d'alertes personnalisées et plus attrayantes
 * pour remplacer les alertes standards de JavaFX
 */
public class CustomAlert {

    public enum AlertType {
        INFORMATION, ERROR, WARNING, CONFIRMATION
    }

    private Stage dialog;
    private boolean result = false;

    /**
     * Affiche une alerte personnalisée avec les détails fournis
     * 
     * @param parentStage Le stage parent
     * @param type Le type d'alerte
     * @param title Le titre de l'alerte
     * @param message Le message de l'alerte
     * @return true si OK a été cliqué, false sinon
     */
    public boolean show(Stage parentStage, AlertType type, String title, String message) {
        dialog = new Stage();
        dialog.initOwner(parentStage);
        dialog.initModality(Modality.APPLICATION_MODAL);
        dialog.initStyle(StageStyle.TRANSPARENT);
        
        // Conteneur principal
        BorderPane root = new BorderPane();
        root.getStyleClass().add("custom-alert");
        
        // Ajouter un effet d'ombre
        root.setEffect(new DropShadow(15, Color.rgb(0, 0, 0, 0.2)));
        
        // En-tête avec icône et titre
        HBox header = createHeader(type, title);
        root.setTop(header);
        
        // Contenu avec message
        VBox content = new VBox(20);
        content.setPadding(new Insets(20, 30, 20, 30));
        content.setAlignment(Pos.CENTER);
        
        Label messageLabel = new Label(message);
        messageLabel.setWrapText(true);
        messageLabel.setTextAlignment(TextAlignment.CENTER);
        messageLabel.setMaxWidth(400);
        messageLabel.setFont(Font.font("System", 14));
        
        content.getChildren().add(messageLabel);
        root.setCenter(content);
        
        // Boutons en bas
        HBox buttonBar = new HBox(15);
        buttonBar.setAlignment(Pos.CENTER);
        buttonBar.setPadding(new Insets(0, 30, 20, 30));
        
        Button okButton = new Button("OK");
        okButton.getStyleClass().add("custom-alert-button");
        okButton.setPrefWidth(100);
        okButton.setOnAction(e -> {
            result = true;
            closeWithAnimation();
        });
        
        // Ajouter des boutons supplémentaires pour les alertes de confirmation
        if (type == AlertType.CONFIRMATION) {
            Button cancelButton = new Button("Annuler");
            cancelButton.getStyleClass().add("custom-alert-button-secondary");
            cancelButton.setPrefWidth(100);
            cancelButton.setOnAction(e -> {
                result = false;
                closeWithAnimation();
            });
            buttonBar.getChildren().addAll(cancelButton, okButton);
        } else {
            buttonBar.getChildren().add(okButton);
        }
        
        root.setBottom(buttonBar);
        
        // Ajouter les styles CSS
        Scene scene = new Scene(root);
        scene.setFill(Color.TRANSPARENT);
        
        // Ajouter le CSS
        scene.getStylesheets().add(getClass().getResource("/styles.css").toExternalForm());
        
        // Préparer le dialogue et rendre le contenu initialement transparent
        dialog.setScene(scene);
        root.setOpacity(0);
        
        // Ajouter un effet de pulsation sur le bouton OK
        addPulseEffect(okButton);
        
        // Montrer le dialogue et animer son apparition
        dialog.show();
        
        // Animation d'entrée (fade in)
        FadeTransition fadeIn = new FadeTransition(Duration.millis(300), root);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);
        fadeIn.play();
        
        dialog.showAndWait();
        
        return result;
    }
    
    /**
     * Ferme l'alerte avec une animation de fondu
     */
    private void closeWithAnimation() {
        BorderPane root = (BorderPane) dialog.getScene().getRoot();
        
        // Animation de sortie (fade out)
        FadeTransition fadeOut = new FadeTransition(Duration.millis(200), root);
        fadeOut.setFromValue(1);
        fadeOut.setToValue(0);
        fadeOut.setOnFinished(e -> dialog.close());
        fadeOut.play();
    }
    
    /**
     * Ajoute un subtil effet de pulsation au bouton principal
     */
    private void addPulseEffect(Button button) {
        Timeline pulse = new Timeline(
            new KeyFrame(Duration.ZERO, new KeyValue(button.scaleXProperty(), 1)),
            new KeyFrame(Duration.ZERO, new KeyValue(button.scaleYProperty(), 1)),
            new KeyFrame(Duration.millis(1000), new KeyValue(button.scaleXProperty(), 1.03)),
            new KeyFrame(Duration.millis(1000), new KeyValue(button.scaleYProperty(), 1.03)),
            new KeyFrame(Duration.millis(2000), new KeyValue(button.scaleXProperty(), 1)),
            new KeyFrame(Duration.millis(2000), new KeyValue(button.scaleYProperty(), 1))
        );
        pulse.setCycleCount(Timeline.INDEFINITE);
        pulse.play();
    }
    
    /**
     * Crée l'en-tête de l'alerte avec une icône et un titre
     */
    private HBox createHeader(AlertType type, String title) {
        HBox header = new HBox(15);
        header.setAlignment(Pos.CENTER_LEFT);
        header.setPadding(new Insets(15, 30, 15, 30));
        
        // Déterminer les couleurs et l'icône en fonction du type
        String backgroundColor;
        String iconText;
        
        switch (type) {
            case INFORMATION:
                backgroundColor = "#4CAF50"; // vert
                iconText = "i";
                break;
            case ERROR:
                backgroundColor = "#F44336"; // rouge
                iconText = "✕";
                break;
            case WARNING:
                backgroundColor = "#FF9800"; // orange
                iconText = "!";
                break;
            case CONFIRMATION:
                backgroundColor = "#2196F3"; // bleu
                iconText = "?";
                break;
            default:
                backgroundColor = "#4CAF50";
                iconText = "i";
        }
        
        header.setStyle("-fx-background-color: " + backgroundColor + ";");
        
        // Créer l'icône circulaire
        Circle circle = new Circle(15);
        circle.setFill(Color.WHITE);
        
        Text icon = new Text(iconText);
        icon.setFont(Font.font("System", FontWeight.BOLD, 18));
        icon.setFill(Color.web(backgroundColor));
        
        StackPane iconContainer = new StackPane();
        iconContainer.getChildren().addAll(circle, icon);
        
        // Créer le titre
        Label titleLabel = new Label(title);
        titleLabel.setFont(Font.font("System", FontWeight.BOLD, 18));
        titleLabel.setTextFill(Color.WHITE);
        
        header.getChildren().addAll(iconContainer, titleLabel);
        
        return header;
    }
    
    /**
     * Méthode statique d'assistance pour montrer une alerte d'information
     */
    public static void showInformation(Stage parentStage, String title, String message) {
        // Au lieu d'attendre que l'alerte soit fermée, on continue l'exécution
        CustomAlert alert = new CustomAlert();
        javafx.application.Platform.runLater(() -> {
            alert.show(parentStage, AlertType.INFORMATION, title, message);
        });
    }
    
    /**
     * Méthode statique d'assistance pour montrer une alerte d'erreur
     */
    public static void showError(Stage parentStage, String title, String message) {
        new CustomAlert().show(parentStage, AlertType.ERROR, title, message);
    }
    
    /**
     * Méthode statique d'assistance pour montrer une alerte d'avertissement
     */
    public static void showWarning(Stage parentStage, String title, String message) {
        new CustomAlert().show(parentStage, AlertType.WARNING, title, message);
    }
    
    /**
     * Méthode statique d'assistance pour montrer une alerte de confirmation
     */
    public static boolean showConfirmation(Stage parentStage, String title, String message) {
        return new CustomAlert().show(parentStage, AlertType.CONFIRMATION, title, message);
    }
} 