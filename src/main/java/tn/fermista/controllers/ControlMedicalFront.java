package tn.fermista.controllers;

import javafx.fxml.FXML;
import javafx.scene.layout.VBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.skin.DatePickerSkin;
import java.time.LocalDate;
import javafx.util.StringConverter;
import java.time.format.DateTimeFormatter;
import java.time.format.FormatStyle;
import java.util.Locale;
import javafx.scene.Node;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import javafx.stage.Modality;
import javafx.scene.control.*;
import javafx.scene.layout.GridPane;
import javafx.scene.paint.Color;
import tn.fermista.models.Consultation;
import tn.fermista.services.ServiceConsultation;
import java.sql.Date;
import java.util.List;
import java.util.Map;
import java.util.HashMap;
import java.util.stream.Collectors;
import java.sql.SQLException;
import javafx.scene.layout.HBox;
import javafx.scene.layout.*;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import tn.fermista.models.RapportMedical;
import tn.fermista.services.ServiceRapportMedical;

public class ControlMedicalFront {
    @FXML
    private VBox calendarContainer;


    private Button btnAjouterRapport;
    private DatePicker datePicker;
    private ServiceConsultation serviceConsultation;
    private Map<LocalDate, List<Consultation>> consultationsParDate;

    @FXML
    private TextField searchField;

    @FXML
    public void initialize() {
        serviceConsultation = new ServiceConsultation();
        consultationsParDate = new HashMap<>();
        
        // Configurer la locale en français
        Locale.setDefault(new Locale("fr", "FR"));
        
        // Créer un DatePicker moderne
        datePicker = new DatePicker(LocalDate.now());
        datePicker.setShowWeekNumbers(true);
        
        // Personnaliser le format d'affichage de la date
        StringConverter<LocalDate> converter = new StringConverter<>() {
            private DateTimeFormatter dateFormatter = 
                DateTimeFormatter.ofLocalizedDate(FormatStyle.FULL).withLocale(new Locale("fr", "FR"));

            @Override
            public String toString(LocalDate date) {
                if (date != null) {
                    return dateFormatter.format(date);
                } else {
                    return "";
                }
            }

            @Override
            public LocalDate fromString(String string) {
                if (string != null && !string.isEmpty()) {
                    return LocalDate.parse(string, dateFormatter);
                } else {
                    return null;
                }
            }
        };
        
        datePicker.setConverter(converter);
        
        // Charger les consultations
        loadConsultations();
        
        // Créer le skin du DatePicker pour avoir accès au popup calendar
        DatePickerSkin datePickerSkin = new DatePickerSkin(datePicker);
        Node popupContent = datePickerSkin.getPopupContent();
        
        // Appliquer les styles
        popupContent.getStyleClass().add("modern-date-picker");
        
        // Gérer le clic sur une date
        datePicker.setOnAction(e -> {
            LocalDate selectedDate = datePicker.getValue();
            if (selectedDate != null) {
                showConsultationsForDate(selectedDate);
            }
        });
        
        // Ajouter directement le popup calendar au conteneur
        calendarContainer.getChildren().add(popupContent);
        
        // Marquer les dates avec des consultations
        updateCalendarCellFactory();
    }

    private void loadConsultations() {
        try {
            List<Consultation> consultations = serviceConsultation.showAll();
            consultationsParDate = consultations.stream()
                .collect(Collectors.groupingBy(c -> c.getDate().toLocalDate()));
        } catch (Exception e) {
            showAlert("Erreur", "Impossible de charger les consultations", Alert.AlertType.ERROR);
        }
    }

    private void updateCalendarCellFactory() {
        datePicker.setDayCellFactory(picker -> new DateCell() {
            @Override
            public void updateItem(LocalDate date, boolean empty) {
                super.updateItem(date, empty);

                if (date != null && !empty) {
                    if (consultationsParDate.containsKey(date)) {
                        getStyleClass().add("has-consultation");
                    } else {
                        getStyleClass().remove("has-consultation");
                    }
                }
            }
        });
    }

    @FXML
    private void handleAjouterConsultation() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ConsultationPopup.fxml"));
            Parent root = loader.load();

            ConsultationPopupController controller = loader.getController();
            controller.setSelectedDate(datePicker.getValue());

            Stage popupStage = new Stage();
            popupStage.initModality(Modality.APPLICATION_MODAL);
            popupStage.setTitle("Nouvelle Consultation");
            
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/styles/StyleCalendar.css").toExternalForm());
            
            popupStage.setScene(scene);
            popupStage.showAndWait();

            // Recharger les consultations après l'ajout
            loadConsultations();
            updateCalendarCellFactory();

        } catch (Exception e) {
            showAlert("Erreur", "Erreur lors de l'ouverture du formulaire", Alert.AlertType.ERROR);
        }
    }

    private void showConsultationsForDate(LocalDate date) {
        List<Consultation> consultations = consultationsParDate.get(date);
        if (consultations == null || consultations.isEmpty()) {
            showAlert("Information", "Aucune consultation pour cette date", Alert.AlertType.INFORMATION);
            return;
        }

        // Créer une fenêtre popup pour afficher la liste des consultations
        Stage popup = new Stage();
        popup.initModality(Modality.APPLICATION_MODAL);
        popup.setTitle("Consultations du " + date.format(DateTimeFormatter.ofPattern("dd/MM/yyyy")));

        VBox content = new VBox(10);
        content.getStyleClass().add("popup-container");
        content.setPadding(new javafx.geometry.Insets(20));

        for (Consultation consultation : consultations) {
            HBox consultationItem = new HBox(10);
            consultationItem.setAlignment(javafx.geometry.Pos.CENTER_LEFT);
            consultationItem.getStyleClass().add("consultation-item");
            consultationItem.setCursor(javafx.scene.Cursor.HAND);

            Label nomLabel = new Label(consultation.getNom());
            Label heureLabel = new Label(consultation.getHeure().toString());
            
            consultationItem.getChildren().addAll(nomLabel, new Label(" - "), heureLabel);
            
            // Ajouter un gestionnaire de clic pour afficher les détails
            consultationItem.setOnMouseClicked(e -> showConsultationDetails(consultation));
            
            content.getChildren().add(consultationItem);
        }

        ScrollPane scrollPane = new ScrollPane(content);
        scrollPane.setFitToWidth(true);
        
        Scene scene = new Scene(scrollPane);
        scene.getStylesheets().add(getClass().getResource("/styles/StyleCalendar.css").toExternalForm());
        
        popup.setScene(scene);
        popup.showAndWait();
    }

    private void showConsultationDetails(Consultation consultation) {
        Stage detailsStage = new Stage();
        detailsStage.initModality(Modality.APPLICATION_MODAL);
        detailsStage.setTitle("Détails de la consultation");

        VBox content = new VBox(15);
        content.getStyleClass().add("popup-container");
        content.setPadding(new javafx.geometry.Insets(20));

        // Section Consultation
        VBox consultationSection = new VBox(10);
        Label consultationTitle = new Label("Détails de la consultation");
        consultationTitle.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
        
        GridPane consultationGrid = createConsultationGrid(consultation);

        // Section Rapport Médical
        VBox rapportSection = new VBox(10);
        Label rapportTitle = new Label("Rapport Médical");
        rapportTitle.setStyle("-fx-font-size: 18px; -fx-font-weight: bold;");
        
        GridPane rapportGrid = createRapportGrid(consultation.getRapportMedical());

        // Boutons pour Consultation
        HBox consultationButtons = new HBox(10);
        Button modifyConsultationBtn = new Button("Modifier Consultation");
        Button deleteConsultationBtn = new Button("Supprimer Consultation");
        
        modifyConsultationBtn.getStyleClass().add("action-button");
        deleteConsultationBtn.getStyleClass().add("delete-button");
        
        modifyConsultationBtn.setOnAction(e -> {
            handleModifyConsultation(consultation, consultationGrid, detailsStage);
        });
        deleteConsultationBtn.setOnAction(e -> handleDeleteConsultation(consultation, detailsStage));
        
        consultationButtons.getChildren().addAll(modifyConsultationBtn, deleteConsultationBtn);

        // Boutons pour Rapport Médical
        HBox rapportButtons = new HBox(10);
        Button modifyRapportBtn = new Button("Modifier Rapport");
        Button deleteRapportBtn = new Button("Supprimer Rapport");
        
        modifyRapportBtn.getStyleClass().add("action-button");
        deleteRapportBtn.getStyleClass().add("delete-button");
        
        modifyRapportBtn.setOnAction(e -> {
            handleModifyRapport(consultation.getRapportMedical(), rapportGrid, detailsStage);
        });
        deleteRapportBtn.setOnAction(e -> handleDeleteRapport(consultation.getRapportMedical(), detailsStage));
        
        rapportButtons.getChildren().addAll(modifyRapportBtn, deleteRapportBtn);

        // Assembler les sections
        consultationSection.getChildren().addAll(consultationTitle, consultationGrid, consultationButtons);
        rapportSection.getChildren().addAll(rapportTitle, rapportGrid, rapportButtons);
        
        content.getChildren().addAll(consultationSection, new Separator(), rapportSection);

        Scene scene = new Scene(content);
        scene.getStylesheets().add(getClass().getResource("/styles/StyleCalendar.css").toExternalForm());
        
        detailsStage.setScene(scene);
        detailsStage.show();
    }

    private GridPane createConsultationGrid(Consultation consultation) {
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.addRow(0, new Label("Nom:"), new Label(consultation.getNom()));
        grid.addRow(1, new Label("Date:"), new Label(consultation.getDate().toString()));
        grid.addRow(2, new Label("Heure:"), new Label(consultation.getHeure().toString()));
        grid.addRow(3, new Label("Lieu:"), new Label(consultation.getLieu()));
        grid.addRow(4, new Label("Vache:"), new Label(consultation.getVache() != null ? consultation.getVache().toString() : "N/A"));
        return grid;
    }

    private GridPane createRapportGrid(RapportMedical rapport) {
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        
        if (rapport != null) {
            grid.addRow(0, new Label("Numéro:"), new Label(String.valueOf(rapport.getNum())));
            grid.addRow(1, new Label("Race:"), new Label(rapport.getRace()));
            grid.addRow(2, new Label("Historique:"), new Label(rapport.getHistoriqueDeMaladie()));
            grid.addRow(3, new Label("Cas médical:"), new Label(rapport.getCasMedical()));
            grid.addRow(4, new Label("Solution:"), new Label(rapport.getSolution()));
        }
        return grid;
    }

    private void handleModifyConsultation(Consultation consultation, GridPane consultationGrid, Stage detailsStage) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ModifyConsultation.fxml"));
            Parent root = loader.load();
            
            ModifyConsultationController controller = loader.getController();
            controller.initData(consultation);
            
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Modifier Consultation");
            
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/styles/StyleCalendar.css").toExternalForm());
            
            stage.setScene(scene);
            
            // Ajouter un écouteur pour la fermeture de la fenêtre
            stage.setOnHiding(e -> {
                // Rafraîchir l'affichage des détails
                VBox parent = (VBox) consultationGrid.getParent();
                parent.getChildren().set(parent.getChildren().indexOf(consultationGrid), createConsultationGrid(consultation));
                
                // Rafraîchir le calendrier
                loadConsultations();
                updateCalendarCellFactory();
            });
            
            stage.showAndWait(); // Utiliser showAndWait au lieu de show pour attendre la fermeture
        } catch (Exception e) {
            showAlert("Erreur", "Erreur lors de la modification de la consultation", Alert.AlertType.ERROR);
        }
    }

    private void handleModifyRapport(RapportMedical rapport, GridPane rapportGrid, Stage detailsStage) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/ModifyRapportMedical.fxml"));
            Parent root = loader.load();
            
            ModifyRapportMedicalController controller = loader.getController();
            controller.initData(rapport);
            
            Stage stage = new Stage();
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setTitle("Modifier Rapport Médical");
            
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/styles/StyleCalendar.css").toExternalForm());
            
            stage.setScene(scene);
            
            // Ajouter un écouteur pour la fermeture de la fenêtre
            stage.setOnHiding(e -> {
                // Rafraîchir l'affichage des détails
                VBox parent = (VBox) rapportGrid.getParent();
                parent.getChildren().set(parent.getChildren().indexOf(rapportGrid), createRapportGrid(rapport));
                
                // Rafraîchir le calendrier
                loadConsultations();
                updateCalendarCellFactory();
            });
            
            stage.showAndWait(); // Utiliser showAndWait au lieu de show pour attendre la fermeture
        } catch (Exception e) {
            showAlert("Erreur", "Erreur lors de la modification du rapport médical", Alert.AlertType.ERROR);
        }
    }

    private void handleDeleteConsultation(Consultation consultation, Stage currentStage) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation de suppression");
        alert.setHeaderText("Supprimer la consultation");
        alert.setContentText("Êtes-vous sûr de vouloir supprimer cette consultation ?");

        if (alert.showAndWait().get() == ButtonType.OK) {
            try {
                serviceConsultation.delete(consultation);
                currentStage.close();
                loadConsultations();
                updateCalendarCellFactory();
                showAlert("Succès", "Consultation supprimée avec succès", Alert.AlertType.INFORMATION);
            } catch (SQLException e) {
                showAlert("Erreur", "Erreur lors de la suppression de la consultation", Alert.AlertType.ERROR);
            }
        }
    }

    private void handleDeleteRapport(RapportMedical rapport, Stage currentStage) {
        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation de suppression");
        alert.setHeaderText("Supprimer le rapport médical");
        alert.setContentText("Êtes-vous sûr de vouloir supprimer ce rapport médical ?");

        if (alert.showAndWait().get() == ButtonType.OK) {
            try {
                ServiceRapportMedical serviceRapportMedical = new ServiceRapportMedical();
                serviceRapportMedical.delete(rapport);
                currentStage.close();
                loadConsultations();
                updateCalendarCellFactory();
                showAlert("Succès", "Rapport médical supprimé avec succès", Alert.AlertType.INFORMATION);
            } catch (SQLException e) {
                showAlert("Erreur", "Erreur lors de la suppression du rapport médical", Alert.AlertType.ERROR);
            }
        }
    }

    private void showAlert(String title, String content, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
    @FXML
    private void handleAjouterRapportMedical() {
        try {
            // Charger le fichier FXML du popup
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/RapportMedicalPopup.fxml"));
            Parent root = loader.load();

            // Créer une nouvelle fenêtre
            Stage popupStage = new Stage();
            popupStage.initModality(Modality.APPLICATION_MODAL); // Rend la fenêtre modale
            popupStage.setTitle("Nouveau Rapport Médical");

            // Appliquer les styles
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/styles/StyleCalendar.css").toExternalForm());

            popupStage.setScene(scene);
            popupStage.showAndWait(); // Afficher la fenêtre et attendre qu'elle soit fermée

        } catch (Exception e) {
            e.printStackTrace();
            // Gérer l'erreur de manière appropriée
        }
    }

    @FXML
    private void handleSearch() {
        String searchTerm = searchField.getText().trim();
        if (searchTerm.isEmpty()) {
            showAlert("Erreur", "Veuillez entrer un nom de consultation", Alert.AlertType.WARNING);
            return;
        }

        try {
            // Récupérer toutes les consultations correspondantes
            List<Consultation> matchingConsultations = serviceConsultation.showAll().stream()
                .filter(c -> c.getNom().toLowerCase().contains(searchTerm.toLowerCase()))
                .collect(Collectors.toList());

            if (matchingConsultations.isEmpty()) {
                showAlert("Information", "Aucune consultation trouvée avec ce nom", Alert.AlertType.INFORMATION);
                return;
            }

            // Ouvrir le popup avec les résultats
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/SearchResultsPopup.fxml"));
            Parent root = loader.load();
            
            Stage popupStage = new Stage();
            popupStage.initModality(Modality.APPLICATION_MODAL);
            popupStage.setTitle("Résultats de la recherche");
            
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/styles/StyleCalendar.css").toExternalForm());
            
            popupStage.setScene(scene);
            
            SearchResultsPopup controller = loader.getController();
            controller.setStage(popupStage);
            controller.setData(matchingConsultations);
            
            popupStage.show();

        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Erreur", "Erreur lors de la recherche", Alert.AlertType.ERROR);
        }
    }
}
