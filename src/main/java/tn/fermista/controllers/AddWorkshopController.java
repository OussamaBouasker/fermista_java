package tn.fermista.controllers;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import tn.fermista.models.Workshop;
import tn.fermista.models.Formateur;
import tn.fermista.services.ServiceWorkshop;
import tn.fermista.services.ServiceFormateur;
import tn.fermista.services.GeminiService;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.Base64;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ResourceBundle;
import java.util.concurrent.CompletableFuture;
import javafx.animation.FadeTransition;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.util.Duration;

public class AddWorkshopController implements Initializable {

    @FXML private TextField titreField;
    @FXML private TextArea descriptionField;
    @FXML private DatePicker dateField;
    @FXML private TextField timeField;
    @FXML private TextField prixField;
    @FXML private TextField themeField;
    @FXML private TextField durationField;
    @FXML private TextField nbrPlacesMaxField;
    @FXML private ComboBox<String> typeComboBox;
    @FXML private ComboBox<Formateur> formateurComboBox;
    @FXML private TextField imageField;
    @FXML private TextField meetlinkField;
    @FXML private Button generateDescriptionButton;
    @FXML private TextField keywordsField;
    @FXML private ProgressIndicator generationProgress;
    @FXML private Label generationLabel;
    @FXML private StackPane generationProgressContainer;

    private ServiceWorkshop serviceWorkshop;
    private ServiceFormateur serviceFormateur;
    private GeminiService geminiService;
    private ShowWorkshopsController parentController;
    private Workshop workshopToEdit;
    private boolean isEditMode = false;

    public void setParentController(ShowWorkshopsController parentController) {
        this.parentController = parentController;
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        serviceWorkshop = new ServiceWorkshop();
        serviceFormateur = new ServiceFormateur();
        geminiService = new GeminiService();

        typeComboBox.getItems().addAll(
                Workshop.TYPE_LIVE_WORKSHOP,
                Workshop.TYPE_SELF_PACED_WORKSHOP
        );

        loadFormateurs();

        typeComboBox.valueProperty().addListener((obs, oldVal, newVal) -> {
            boolean isLiveWorkshop = Workshop.TYPE_LIVE_WORKSHOP.equals(newVal);
            formateurComboBox.setDisable(!isLiveWorkshop);
            if (!isLiveWorkshop) {
                formateurComboBox.setValue(null);
            }
        });

        formateurComboBox.setDisable(true);
    }

    @FXML private Button imageButton;
    @FXML private Label selectedImageLabel;

    @FXML
    private void handleChooseImage() {
        FileChooser fileChooser = new FileChooser();
        fileChooser.setTitle("Choisir une image");
        fileChooser.getExtensionFilters().addAll(
                new FileChooser.ExtensionFilter("Images", "*.png", "*.jpg", "*.jpeg", "*.gif")
        );
    
        Stage stage = (Stage) imageField.getScene().getWindow();
        File selectedFile = fileChooser.showOpenDialog(stage);
    
        if (selectedFile != null) {
            try {
                File destDir = new File("src/main/resources/images");
                if (!destDir.exists()) {
                    destDir.mkdirs();
                }
                
                String fileName = System.currentTimeMillis() + "_" + selectedFile.getName();
                File destFile = new File(destDir, fileName);
                Files.copy(selectedFile.toPath(), destFile.toPath());
                
                imageField.setText("/images/" + fileName);
                
            } catch (IOException e) {
                showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de la copie de l'image: " + e.getMessage());
            }
        }
    }

    public void setWorkshopForEdit(Workshop workshop) {
        this.workshopToEdit = workshop;
        this.isEditMode = true;
    
        titreField.setText(workshop.getTitre());
        descriptionField.setText(workshop.getDescription());
        if (workshop.getDate() != null) {
            dateField.setValue(workshop.getDate().toLocalDate());
            timeField.setText(workshop.getDate().toLocalTime().format(DateTimeFormatter.ofPattern("HH:mm")));
        }
        prixField.setText(workshop.getPrix());
        themeField.setText(workshop.getTheme());
        if (workshop.getDuration() != null) {
            durationField.setText(workshop.getDuration().format(DateTimeFormatter.ofPattern("HH:mm")));
        }
        nbrPlacesMaxField.setText(String.valueOf(workshop.getNbrPlacesMax()));
        typeComboBox.setValue(workshop.getType());
        imageField.setText(workshop.getImage());

        if (Workshop.TYPE_LIVE_WORKSHOP.equals(workshop.getType()) && workshop.getUser() != null) {
            formateurComboBox.getItems().forEach(formateur -> {
                if (formateur.getId() == workshop.getUser().getId()) {
                    formateurComboBox.setValue(formateur);
                }
            });
        }
    }

    @FXML
    private void handleSave() {
        if (!validateInputs()) {
            return;
        }

        try {
            Workshop workshop = isEditMode ? workshopToEdit : new Workshop();
            
            if (isEditMode) {
                workshop.setId(workshopToEdit.getId());
                workshop.setNbrPlacesRestantes(workshopToEdit.getNbrPlacesRestantes());
            } else {
                workshop.setNbrPlacesRestantes(Integer.parseInt(nbrPlacesMaxField.getText()));
            }

            workshop.setTitre(titreField.getText());
            workshop.setDescription(descriptionField.getText());
            workshop.setImage(imageField.getText());
            
            LocalDate date = dateField.getValue();
            LocalTime time = LocalTime.parse(timeField.getText(), DateTimeFormatter.ofPattern("HH:mm"));
            workshop.setDate(LocalDateTime.of(date, time));
            
            workshop.setPrix(prixField.getText());
            workshop.setTheme(themeField.getText());
            workshop.setDuration(LocalTime.parse(durationField.getText(), DateTimeFormatter.ofPattern("HH:mm")));
            workshop.setNbrPlacesMax(Integer.parseInt(nbrPlacesMaxField.getText()));
            workshop.setType(typeComboBox.getValue());
            
            if (Workshop.TYPE_LIVE_WORKSHOP.equals(workshop.getType())) {
                if (!isEditMode || workshop.getMeetlink() == null || workshop.getMeetlink().isEmpty()) {
                    String roomName = workshop.getTitre().replaceAll("\\s+", "-").toLowerCase() + 
                                    "-" + System.currentTimeMillis();
                    workshop.setMeetlink("https://meet.jit.si/" + roomName);
                }
                workshop.setUser(formateurComboBox.getValue());
            } else {
                workshop.setMeetlink(null);
                workshop.setUser(null);
            }

            if (isEditMode) {
                serviceWorkshop.update(workshop);
                showAlert(Alert.AlertType.INFORMATION, "Succès", "Workshop modifié avec succès!");
            } else {
                serviceWorkshop.insert(workshop);
                showAlert(Alert.AlertType.INFORMATION, "Succès", "Workshop ajouté avec succès!");
            }

            if (parentController != null) {
                parentController.refreshTable();
            }

            closeForm();

        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de l'opération: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur inattendue: " + e.getMessage());
            e.printStackTrace();
        }
    }

    @FXML
    private void handleCancel() {
        closeForm();
    }

    private boolean validateInputs() {
        StringBuilder errors = new StringBuilder();

        if (titreField.getText().isEmpty()) errors.append("Le titre est requis\n");
        if (descriptionField.getText().isEmpty()) errors.append("La description est requise\n");
        if (dateField.getValue() == null) errors.append("La date est requise\n");
        if (timeField.getText().isEmpty()) errors.append("L'heure est requise\n");
        if (prixField.getText().isEmpty()) errors.append("Le prix est requis\n");
        if (themeField.getText().isEmpty()) errors.append("Le thème est requis\n");
        if (durationField.getText().isEmpty()) errors.append("La durée est requise\n");
        if (nbrPlacesMaxField.getText().isEmpty()) errors.append("Le nombre de places est requis\n");
        if (typeComboBox.getValue() == null) errors.append("Le type est requis\n");

        if (Workshop.TYPE_LIVE_WORKSHOP.equals(typeComboBox.getValue()) && formateurComboBox.getValue() == null) {
            errors.append("Le formateur est requis pour un atelier live\n");
        }

        if (errors.length() > 0) {
            showAlert(Alert.AlertType.ERROR, "Erreur de validation", errors.toString());
            return false;
        }

        return true;
    }

    private void loadFormateurs() {
        try {
            ObservableList<Formateur> formateurs = FXCollections.observableArrayList(serviceFormateur.rechercher());
            formateurComboBox.setItems(formateurs);

            formateurComboBox.setCellFactory(param -> new ListCell<Formateur>() {
                @Override
                protected void updateItem(Formateur formateur, boolean empty) {
                    super.updateItem(formateur, empty);
                    if (empty || formateur == null) {
                        setText(null);
                    } else {
                        setText(formateur.getFirstName() + " " + formateur.getLastName());
                    }
                }
            });

            formateurComboBox.setConverter(new javafx.util.StringConverter<Formateur>() {
                @Override
                public String toString(Formateur formateur) {
                    if (formateur == null) {
                        return null;
                    }
                    return formateur.getFirstName() + " " + formateur.getLastName();
                }

                @Override
                public Formateur fromString(String string) {
                    return null;
                }
            });
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors du chargement des formateurs: " + e.getMessage());
        }
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }

    private void closeForm() {
        ((Stage) titreField.getScene().getWindow()).close();
    }

    @FXML
    private void handleGenerateDescription() {
        String keywords = keywordsField.getText();
        if (keywords == null || keywords.trim().isEmpty()) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Veuillez saisir des mots-clés pour la génération");
            return;
        }

        // Disable input and show progress
        generateDescriptionButton.setDisable(true);
        keywordsField.setDisable(true);
        generationProgressContainer.setVisible(true);
        
        // Add fade-in animation for progress container
        FadeTransition fadeIn = new FadeTransition(Duration.millis(300), generationProgressContainer);
        fadeIn.setFromValue(0);
        fadeIn.setToValue(1);
        fadeIn.play();

        CompletableFuture.supplyAsync(() -> {
            try {
                // Simulate some processing time for visual effect
                Thread.sleep(1000);
                return geminiService.generateDescription(keywords);
            } catch (Exception e) {
                throw new RuntimeException(e);
            }
        }).thenAccept(generatedDescription -> {
            Platform.runLater(() -> {
                try {
                    // Add typing animation effect
                    animateText(descriptionField, generatedDescription);
                } catch (Exception e) {
                    showAlert(Alert.AlertType.ERROR, "Erreur", 
                        "Erreur lors de la génération de la description: " + e.getMessage());
                } finally {
                    // Add fade-out animation for progress container
                    FadeTransition fadeOut = new FadeTransition(Duration.millis(300), generationProgressContainer);
                    fadeOut.setFromValue(1);
                    fadeOut.setToValue(0);
                    fadeOut.setOnFinished(event -> {
                        generationProgressContainer.setVisible(false);
                        generateDescriptionButton.setDisable(false);
                        keywordsField.setDisable(false);
                    });
                    fadeOut.play();
                }
            });
        }).exceptionally(throwable -> {
            Platform.runLater(() -> {
                showAlert(Alert.AlertType.ERROR, "Erreur", 
                    "Erreur lors de la génération de la description: " + throwable.getMessage());
                generationProgressContainer.setVisible(false);
                generateDescriptionButton.setDisable(false);
                keywordsField.setDisable(false);
            });
            return null;
        });
    }

    private void animateText(TextArea textArea, String finalText) {
        textArea.clear();
        Timeline timeline = new Timeline();
        String[] characters = finalText.split("");
        
        for (int i = 0; i < characters.length; i++) {
            final int index = i;
            KeyFrame keyFrame = new KeyFrame(Duration.millis(20 * i), event -> 
                textArea.appendText(characters[index])
            );
            timeline.getKeyFrames().add(keyFrame);
        }
        
        timeline.play();
    }
}