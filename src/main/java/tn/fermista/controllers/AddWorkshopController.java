package tn.fermista.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import tn.fermista.models.Workshop;
import tn.fermista.models.Formateur;
import tn.fermista.services.ServiceWorkshop;
import tn.fermista.services.ServiceFormateur;

import java.io.File;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;

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
//    @FXML private TextField keywordsField;

    private ServiceWorkshop serviceWorkshop;
    private ServiceFormateur serviceFormateur;
    private ShowWorkshopsController parentController;
    private Workshop workshopToEdit;
    private boolean isEditMode = false;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        serviceWorkshop = new ServiceWorkshop();
        serviceFormateur = new ServiceFormateur();

        // Initialize the type combo box
        typeComboBox.getItems().addAll(
                Workshop.TYPE_LIVE_WORKSHOP,
                Workshop.TYPE_SELF_PACED_WORKSHOP
        );

        // Load formateurs into the combo box
        loadFormateurs();

        // Add listener to type combo box to handle formateur selection visibility
        typeComboBox.valueProperty().addListener((obs, oldVal, newVal) -> {
            boolean isLiveWorkshop = Workshop.TYPE_LIVE_WORKSHOP.equals(newVal);
            formateurComboBox.setDisable(!isLiveWorkshop);
            if (!isLiveWorkshop) {
                formateurComboBox.setValue(null);
            }
        });

        // Set initial state
        formateurComboBox.setDisable(true);
    }

    public void setWorkshopForEdit(Workshop workshop) {
        this.workshopToEdit = workshop;
        this.isEditMode = true;

        // Populate the form with workshop data
        titreField.setText(workshop.getTitre());
        descriptionField.setText(workshop.getDescription());
        dateField.setValue(workshop.getDate().toLocalDate());
        timeField.setText(workshop.getDate().toLocalTime().format(DateTimeFormatter.ofPattern("HH:mm")));
        prixField.setText(workshop.getPrix());
        themeField.setText(workshop.getTheme());
        durationField.setText(workshop.getDuration().format(DateTimeFormatter.ofPattern("HH:mm")));
        nbrPlacesMaxField.setText(String.valueOf(workshop.getNbrPlacesMax()));
        typeComboBox.setValue(workshop.getType());
        imageField.setText(workshop.getImage());
        meetlinkField.setText(workshop.getMeetlink());
//        keywordsField.setText(workshop.getKeywords());

        // Set formateur if it's a live workshop
        if (Workshop.TYPE_LIVE_WORKSHOP.equals(workshop.getType()) && workshop.getUser() != null) {
            formateurComboBox.getItems().forEach(formateur -> {
                if (formateur.getId().equals(workshop.getUser().getId())) {
                    formateurComboBox.setValue(formateur);
                }
            });
        }
    }

    public void setParentController(ShowWorkshopsController parentController) {
        this.parentController = parentController;
    }

    @FXML
    private void handleSave() {
        if (!validateInputs()) {
            return;
        }

        try {
            Workshop workshop = isEditMode ? workshopToEdit : new Workshop();
            workshop.setTitre(titreField.getText());
            workshop.setDescription(descriptionField.getText());

            // Combine date and time
            LocalDate date = dateField.getValue();
            LocalTime time = LocalTime.parse(timeField.getText(), DateTimeFormatter.ofPattern("HH:mm"));
            workshop.setDate(LocalDateTime.of(date, time));

            workshop.setPrix(prixField.getText());
            workshop.setTheme(themeField.getText());
            workshop.setDuration(LocalTime.parse(durationField.getText(), DateTimeFormatter.ofPattern("HH:mm")));
            workshop.setNbrPlacesMax(Integer.parseInt(nbrPlacesMaxField.getText()));

            // Only update nbrPlacesRestantes if it's a new workshop
            if (!isEditMode) {
                workshop.setNbrPlacesRestantes(Integer.parseInt(nbrPlacesMaxField.getText()));
            }

            workshop.setType(typeComboBox.getValue());
            workshop.setImage(imageField.getText());
            workshop.setMeetlink(meetlinkField.getText());
//            workshop.setKeywords(keywordsField.getText());

            // Set formateur as user if it's a live workshop
            if (Workshop.TYPE_LIVE_WORKSHOP.equals(workshop.getType())) {
                workshop.setUser(formateurComboBox.getValue());
            } else {
                workshop.setUser(null);
            }

            if (isEditMode) {
                serviceWorkshop.update(workshop);
                showAlert(Alert.AlertType.INFORMATION, "Succès", "Workshop modifié avec succès!");
            } else {
                serviceWorkshop.insert(workshop);
                showAlert(Alert.AlertType.INFORMATION, "Succès", "Workshop ajouté avec succès!");
            }

            // Refresh the parent table
            if (parentController != null) {
                parentController.refreshTable();
            }

            // Close the form
            closeForm();

        } catch (SQLException e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur lors de l'opération: " + e.getMessage());
            e.printStackTrace();
        } catch (Exception e) {
            showAlert(Alert.AlertType.ERROR, "Erreur", "Erreur: " + e.getMessage());
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

        // Validate formateur selection for live workshops
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

            // Set cell factory to display formateur name
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

            // Set converter to display selected formateur name
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
} 