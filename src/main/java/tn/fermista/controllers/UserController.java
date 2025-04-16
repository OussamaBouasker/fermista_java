package tn.fermista.controllers;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.collections.transformation.FilteredList;
import javafx.collections.transformation.SortedList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.util.Callback;
import tn.fermista.models.*;
import tn.fermista.services.*;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class UserController implements Initializable {

    @FXML
    private TableView<User> userTable;
    @FXML
    private TableColumn<User, String> emailColumn;
    @FXML
    private TableColumn<User, String> firstNameColumn;
    @FXML
    private TableColumn<User, String> lastNameColumn;
    @FXML
    private TableColumn<User, String> numberColumn;
    @FXML
    private TableColumn<User, String> roleColumn;
    @FXML
    private TableColumn<User, Void> actionsColumn;
    @FXML
    private TextField txt_search;
    @FXML
    private Button btn_workbench113;
    @FXML
    private Button btn_workbench114;

    private ObservableList<User> userList;
    private ServiceUser serviceUser;
    private ServiceAdmin serviceAdmin;
    private ServiceAgriculteur serviceAgriculteur;
    private ServiceClient serviceClient;
    private ServiceVeterinaire serviceVeterinaire;
    private ServiceFormateur serviceFormateur;


    @Override
    public void initialize(URL location, ResourceBundle resources) {
        // Initialize services
        serviceUser = new ServiceUser();
        serviceAdmin = new ServiceAdmin();
        serviceAgriculteur = new ServiceAgriculteur();
        serviceClient = new ServiceClient();
        serviceVeterinaire = new ServiceVeterinaire();
        serviceFormateur = new ServiceFormateur();

        // Initialize table columns with custom cell value factories
        emailColumn.setCellValueFactory(cellData -> {
            User user = cellData.getValue();
            return new javafx.beans.property.SimpleStringProperty(user.getEmail());
        });

        firstNameColumn.setCellValueFactory(cellData -> {
            User user = cellData.getValue();
            return new javafx.beans.property.SimpleStringProperty(user.getFirstName());
        });

        lastNameColumn.setCellValueFactory(cellData -> {
            User user = cellData.getValue();
            return new javafx.beans.property.SimpleStringProperty(user.getLastName());
        });

        numberColumn.setCellValueFactory(cellData -> {
            User user = cellData.getValue();
            return new javafx.beans.property.SimpleStringProperty(user.getNumber());
        });

        roleColumn.setCellValueFactory(cellData -> {
            User user = cellData.getValue();
            return new javafx.beans.property.SimpleStringProperty(user.getRoles().toString());
        });

        // Initialize actions column
        actionsColumn.setCellFactory(createActionCellFactory());

        // Load initial data
        loadUserData();

        // Setup search functionality
        setupSearch();
        
        // Setup navigation
        if (btn_workbench113 != null) {
            btn_workbench113.setOnAction(event -> navigateToCrudReclamation());
        }
        if (btn_workbench114 != null) {
            btn_workbench114.setOnAction(event -> navigateToDashboard());
        }
    }
    
    private void navigateToCrudReclamation() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/CrudReclamation.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            Stage stage = (Stage) btn_workbench113.getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private void navigateToDashboard() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/DashboardTemplate.fxml"));
            Parent root = loader.load();
            Scene scene = new Scene(root);
            Stage stage = (Stage) btn_workbench114.getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private Callback<TableColumn<User, Void>, TableCell<User, Void>> createActionCellFactory() {
        return new Callback<TableColumn<User, Void>, TableCell<User, Void>>() {
            @Override
            public TableCell<User, Void> call(final TableColumn<User, Void> param) {
                return new TableCell<User, Void>() {
                    private final Button updateButton = new Button("Update");
                    private final Button deleteButton = new Button("Delete");
                    private final HBox buttons = new HBox(5, updateButton, deleteButton);

                    {
                        updateButton.getStyleClass().add("btn-success");
                        deleteButton.getStyleClass().add("btn-danger");

                        updateButton.setOnAction(event -> {
                            User user = getTableView().getItems().get(getIndex());
                            showUserFormPopup(user, true);
                        });

                        deleteButton.setOnAction(event -> {
                            User user = getTableView().getItems().get(getIndex());
                            handleDeleteUser(user);
                        });
                    }

                    @Override
                    protected void updateItem(Void item, boolean empty) {
                        super.updateItem(item, empty);
                        if (empty) {
                            setGraphic(null);
                        } else {
                            setGraphic(buttons);
                        }
                    }
                };
            }
        };
    }

    private void showUserFormPopup(User user, boolean isUpdate) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/UserFormPopup.fxml"));
            Parent root = loader.load();
            UserFormPopupController controller = loader.getController();
            controller.setUser(user, isUpdate, this);

            Stage stage = new Stage();
            stage.setTitle(isUpdate ? "Modifier un utilisateur" : "Ajouter un utilisateur");
            stage.setScene(new Scene(root));
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.showAndWait();
        } catch (Exception e) {
            e.printStackTrace();
            showAlert("Error", "Impossible d'ouvrir le formulaire: " + e.getMessage());
        }
    }

    @FXML
    private void handleAddUser() {
        showUserFormPopup(null, false);
    }

    public void addUser(User user) {
        switch (user.getRoles().toString()) {
            case "ROLE_ADMIN":
                serviceAdmin.ajouter((Admin) user);
                break;
            case "ROLE_AGRICULTOR":
                serviceAgriculteur.ajouter((Agriculteur) user);
                break;
            case "ROLE_CLIENT":
                serviceClient.ajouter((Client) user);
                break;
            case "ROLE_VETERINAIR":
                serviceVeterinaire.ajouter((Veterinaire) user);
                break;
            case "ROLE_FORMATEUR":
                serviceFormateur.ajouter((Formateur) user);
                break;
        }
        loadUserData();
    }

    public void updateUser(User user) {
        switch (user.getRoles().toString()) {
            case "ROLE_ADMIN":
                serviceAdmin.modifier((Admin) user);
                break;
            case "ROLE_AGRICULTOR":
                serviceAgriculteur.modifier((Agriculteur) user);
                break;
            case "ROLE_CLIENT":
                serviceClient.modifier((Client) user);
                break;
            case "ROLE_VETERINAIR":
                serviceVeterinaire.modifier((Veterinaire) user);
                break;
            case "ROLE_FORMATEUR":
                serviceFormateur.modifier((Formateur) user);
                break;
        }
        loadUserData();
    }

    private void handleDeleteUser(User user) {
        if (user == null) return;

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation de suppression");
        alert.setHeaderText(null);
        alert.setContentText("Êtes-vous sûr de vouloir supprimer cet utilisateur ?");

        if (alert.showAndWait().get() == ButtonType.OK) {
            switch (user.getRoles().toString()) {
                case "ROLE_ADMIN":
                    serviceAdmin.supprimer((Admin) user);
                    break;
                case "ROLE_AGRICULTOR":
                    serviceAgriculteur.supprimer((Agriculteur) user);
                    break;
                case "ROLE_CLIENT":
                    serviceClient.supprimer((Client) user);
                    break;
                case "ROLE_VETERINAIR":
                    serviceVeterinaire.supprimer((Veterinaire) user);
                    break;
                case "ROLE_FORMATEUR":
                    serviceFormateur.supprimer((Formateur) user);
                    break;
            }
            loadUserData();
        }
    }

    @FXML
    private void handleDeleteUser() {
        User selectedUser = userTable.getSelectionModel().getSelectedItem();
        if (selectedUser == null) {
            showAlert("Error", "Please select a user to delete");
            return;
        }

        Alert alert = new Alert(Alert.AlertType.CONFIRMATION);
        alert.setTitle("Confirmation de suppression");
        alert.setHeaderText(null);
        alert.setContentText("Êtes-vous sûr de vouloir supprimer cet utilisateur ?");

        if (alert.showAndWait().get() == ButtonType.OK) {
            switch (selectedUser.getRoles().toString()) {
                case "ROLE_ADMIN":
                    serviceAdmin.supprimer((Admin) selectedUser);
                    break;
                case "ROLE_AGRICULTOR":
                    serviceAgriculteur.supprimer((Agriculteur) selectedUser);
                    break;
                case "ROLE_CLIENT":
                    serviceClient.supprimer((Client) selectedUser);
                    break;
                case "ROLE_VETERINAIR":
                    serviceVeterinaire.supprimer((Veterinaire) selectedUser);
                    break;
                case "ROLE_FORMATEUR":
                    serviceFormateur.supprimer((Formateur) selectedUser);
                    break;
            }
            loadUserData();
        }
    }

    private void loadUserData() {
        userList = FXCollections.observableArrayList();

        // Add all types of users to the list
        userList.addAll(serviceAdmin.rechercher());
        userList.addAll(serviceAgriculteur.rechercher());
        userList.addAll(serviceClient.rechercher());
        userList.addAll(serviceVeterinaire.rechercher());
        userList.addAll(serviceFormateur.rechercher());

        // Apply the search filter if it exists
        setupSearch();
    }

    private void setupSearch() {
        if (userList == null) {
            userList = FXCollections.observableArrayList();
        }

        FilteredList<User> filteredData = new FilteredList<>(userList, b -> true);

        txt_search.textProperty().addListener((observable, oldValue, newValue) -> {
            filteredData.setPredicate(user -> {
                if (newValue == null || newValue.isEmpty()) {
                    return true;
                }

                String lowerCaseFilter = newValue.toLowerCase();

                if (user.getEmail().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                } else if (user.getFirstName().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                } else if (user.getLastName().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                } else if (user.getNumber().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                } else if (user.getRoles().toString().toLowerCase().contains(lowerCaseFilter)) {
                    return true;
                }
                return false;
            });
        });

        SortedList<User> sortedData = new SortedList<>(filteredData);
        sortedData.comparatorProperty().bind(userTable.comparatorProperty());
        userTable.setItems(sortedData);
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
