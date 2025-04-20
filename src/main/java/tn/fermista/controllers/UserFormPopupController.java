package tn.fermista.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;  // Add this import
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import tn.fermista.models.*;
import tn.fermista.utils.PasswordUtils;

public class UserFormPopupController {

    @FXML
    private TextField txt_email;
    @FXML
    private TextField txt_password;
    @FXML
    private TextField txt_firstName;
    @FXML
    private TextField txt_lastName;
    @FXML
    private TextField txt_number;
    @FXML
    private ComboBox<String> roleComboBox;
    @FXML
    private Label formTitle; // Add this field

    private User user;
    private boolean isUpdate;
    private UserController parentController;

    @FXML
    public void initialize() {
        roleComboBox.getItems().addAll("ROLE_ADMIN", "ROLE_AGRICULTOR", "ROLE_CLIENT", "ROLE_VETERINAIR","ROLE_FORMATEUR");
        roleComboBox.setValue("ROLE_ADMIN");
    }

    public void setUser(User user, boolean isUpdate, UserController parentController) {
        this.user = user;
        this.isUpdate = isUpdate;
        this.parentController = parentController;

        // Update form title based on operation
        formTitle.setText(isUpdate ? "Modification d'un utilisateur" : "Ajout d'un utilisateur");

        if (user != null) {
            txt_email.setText(user.getEmail());
            txt_password.setText(user.getPassword());
            txt_firstName.setText(user.getFirstName());
            txt_lastName.setText(user.getLastName());
            txt_number.setText(user.getNumber());
            roleComboBox.setValue(user.getRoles().toString());
        }
    }

    @FXML
    private void handleSave() {
        if (txt_email.getText().isEmpty() ||
                txt_password.getText().isEmpty() ||
                txt_firstName.getText().isEmpty() ||
                txt_lastName.getText().isEmpty() ||
                txt_number.getText().isEmpty() ||
                roleComboBox.getValue() == null) {
            showAlert("Error", "Please fill in all fields");
            return;
        }

        String role = roleComboBox.getValue();
        User updatedUser = createUserFromInput(role);
        if (updatedUser == null) {
            return;
        }

        if (isUpdate && user != null) {
            updatedUser.setId(user.getId());
        }

        if (isUpdate) {
            parentController.updateUser(updatedUser);
        } else {
            parentController.addUser(updatedUser);
        }

        closePopup();
    }

    @FXML
    private void handleCancel() {
        closePopup();
    }

    private void closePopup() {
        Stage stage = (Stage) txt_email.getScene().getWindow();
        stage.close();
    }

    private User createUserFromInput(String role) {
        String email = txt_email.getText();
        String password = txt_password.getText();
        String firstName = txt_firstName.getText();
        String lastName = txt_lastName.getText();
        String number = txt_number.getText();

        User newUser = null;
        try {
            Roles roleEnum = Roles.valueOf(role);

            switch (role) {
                case "ROLE_ADMIN":
                    newUser = new Admin();
                    break;
                case "ROLE_AGRICULTOR":
                    newUser = new Agriculteur();
                    break;
                case "ROLE_CLIENT":
                    newUser = new Client();
                    break;
                case "ROLE_VETERINAIR":
                    newUser = new Veterinaire();
                    break;
                case "ROLE_FORMATEUR":
                    newUser = new Formateur();
                    break;
            }

            if (newUser != null) {
                newUser.setEmail(email);
                newUser.setPassword(PasswordUtils.hashPassword(password));
                newUser.setFirstName(firstName);
                newUser.setLastName(lastName);
                newUser.setNumber(number);
                newUser.setRoles(roleEnum);
                newUser.setState(true);
                newUser.setVerified(true);
            }
        } catch (IllegalArgumentException e) {
            showAlert("Error", "Invalid role selected");
            return null;
        }

        return newUser;
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}