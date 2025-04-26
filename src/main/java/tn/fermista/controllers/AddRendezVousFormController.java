package tn.fermista.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.stage.Stage;
import tn.fermista.models.*;
import tn.fermista.services.ServiceRendezVous;
import tn.fermista.utils.UserSession;
import tn.fermista.services.ServiceAgriculteur;
import tn.fermista.utils.EmailService;

import java.sql.Date;
import java.sql.Time;
import java.time.LocalDate;
import java.time.LocalTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

public class AddRendezVousFormController {
    @FXML private DatePicker datePicker;
    @FXML private TextField heureField;
    @FXML private TextField sexField;
    @FXML private TextArea causeField;
    @FXML private TextField statusField;

    private Stage stage;
    private Veterinaire selectedVeterinaire;
    private ServiceRendezVous serviceRendezVous;
    private ServiceAgriculteur serviceAgriculteur;
    private Agriculteur currentAgriculteur;

    public void initialize() {
        serviceRendezVous = new ServiceRendezVous();
        serviceAgriculteur = new ServiceAgriculteur();
        // Set default status
        statusField.setText("en attente");
        
        // Récupérer l'utilisateur connecté
        User currentUser = UserSession.getCurrentUser();
        if (currentUser == null) {
            System.out.println("Aucun utilisateur connecté");
            return;
        }
        
        // Vérifier le rôle et récupérer l'agriculteur
        if (currentUser.getRoles() == Roles.ROLE_AGRICULTOR) {
            // Récupérer l'agriculteur complet depuis la base de données
            List<Agriculteur> agriculteurs = serviceAgriculteur.rechercher();
            currentAgriculteur = agriculteurs.stream()
                .filter(a -> a.getId().equals(currentUser.getId()))
                .findFirst()
                .orElse(null);
                
            if (currentAgriculteur != null) {
                System.out.println("Agriculteur connecté: " + currentAgriculteur.getFirstName() + " " + currentAgriculteur.getLastName());
            } else {
                System.out.println("Agriculteur non trouvé dans la base de données");
            }
        } else {
            System.out.println("L'utilisateur n'a pas le rôle d'agriculteur");
        }
    }

    public void setStage(Stage stage) {
        this.stage = stage;
    }

    public void setVeterinaire(Veterinaire veterinaire) {
        this.selectedVeterinaire = veterinaire;
    }

    @FXML
    private void handleSubmit() {
        try {
            // Vérifier que l'agriculteur est bien défini
            if (currentAgriculteur == null) {
                showAlert("Erreur", "Vous devez être connecté en tant qu'agriculteur pour prendre un rendez-vous", Alert.AlertType.ERROR);
                return;
            }

            // Validation des champs vides
            if (datePicker.getValue() == null || heureField.getText().isEmpty() ||
                sexField.getText().isEmpty() || causeField.getText().isEmpty()) {
                showAlert("Erreur", "Veuillez remplir tous les champs", Alert.AlertType.ERROR);
                return;
            }

            // Validation de la date (doit être après aujourd'hui)
            LocalDate selectedDate = datePicker.getValue();
            if (!selectedDate.isAfter(LocalDate.now())) {
                showAlert("Erreur", "La date doit être postérieure à aujourd'hui", Alert.AlertType.ERROR);
                return;
            }

            // Validation de l'heure (entre 9:00 et 18:00)
            LocalTime time;
            try {
                time = LocalTime.parse(heureField.getText());
                LocalTime startTime = LocalTime.of(9, 0);
                LocalTime endTime = LocalTime.of(18, 0);
                if (time.isBefore(startTime) || time.isAfter(endTime)) {
                    showAlert("Erreur", "L'heure doit être comprise entre 09:00 et 18:00", Alert.AlertType.ERROR);
                    return;
                }
            } catch (DateTimeParseException e) {
                showAlert("Erreur", "Format d'heure invalide. Utilisez le format HH:mm", Alert.AlertType.ERROR);
                return;
            }

            // Validation du sexe (male ou female)
            String sex = sexField.getText().toLowerCase();
            if (!sex.equals("male") && !sex.equals("female")) {
                showAlert("Erreur", "Le sexe doit être 'male' ou 'female'", Alert.AlertType.ERROR);
                return;
            }

            // Validation de la cause (minimum 10 caractères)
            if (causeField.getText().length() < 10) {
                showAlert("Erreur", "La cause doit contenir au moins 10 caractères", Alert.AlertType.ERROR);
                return;
            }

            // Création du nouveau rendez-vous
            RendezVous rendezVous = new RendezVous();
            rendezVous.setVeterinaire(selectedVeterinaire);
            rendezVous.setAgriculteur(currentAgriculteur);
            rendezVous.setDate(Date.valueOf(datePicker.getValue()));
            rendezVous.setHeure(Time.valueOf(time));
            rendezVous.setSex(sex);
            rendezVous.setCause(causeField.getText());
            rendezVous.setStatus(statusField.getText());

            // Ajout dans la base de données
            serviceRendezVous.insert(rendezVous);

            // Envoi de l'email de notification au vétérinaire
            EmailService.sendRendezVousNotification(rendezVous);

            // Message de succès
            showAlert("Succès", "Rendez-vous ajouté avec succès. Veuillez attender la confirmation du vétérinaire ", Alert.AlertType.INFORMATION);

            // Fermer la fenêtre
            stage.close();
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Erreur", "Erreur lors de l'ajout du rendez-vous: " + e.getMessage(), Alert.AlertType.ERROR);
        }
    }

    @FXML
    private void handleCancel() {
        stage.close();
    }

    private void showAlert(String title, String content, Alert.AlertType type) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
} 