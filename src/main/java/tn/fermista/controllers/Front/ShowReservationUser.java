package tn.fermista.controllers.Front;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.HBox;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import tn.fermista.models.Reservation;
import tn.fermista.models.User;
import tn.fermista.services.ReservationService;
import tn.fermista.utils.PDFGenerator;
import tn.fermista.utils.UserSession;
import javafx.geometry.Pos;
import javafx.scene.layout.Priority;
import javafx.util.Callback;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.ResourceBundle;

public class ShowReservationUser implements Initializable {

    @FXML
    private TableView<Reservation> reservationsTable;

    @FXML
    private TableColumn<Reservation, String> workshopColumn;

    @FXML
    private TableColumn<Reservation, String> dateColumn;

    @FXML
    private TableColumn<Reservation, String> statusColumn;

    @FXML
    private TableColumn<Reservation, String> priceColumn;

    @FXML
    private TableColumn<Reservation, Void> actionsColumn;

    private ReservationService reservationService;
    private ObservableList<Reservation> reservationsList;
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        reservationService = new ReservationService();
        reservationsList = FXCollections.observableArrayList();

        // Get current user from session
        User currentUser = UserSession.getCurrentUser();
        
        if (currentUser != null) {
            // Configure table columns
            setupTableColumns();
            
            // Load user's reservations
            loadUserReservations(currentUser);
            
            // Set items to table
            reservationsTable.setItems(reservationsList);
        } else {
            // Return to workshops page if not logged in
            handleBack();
        }
    }

    @FXML
    private void handleBack() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Front-Office/ShowWorkshop.fxml"));
            Parent root = loader.load();
            
            Stage stage = (Stage) reservationsTable.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible de retourner à la page précédente");
        }
    }

    private void setupTableColumns() {
        // Workshop name column
        workshopColumn.setCellValueFactory(cellData -> {
            if (cellData.getValue().getWorkshop() != null) {
                return new javafx.beans.property.SimpleStringProperty(
                    cellData.getValue().getWorkshop().getTitre()
                );
            }
            return new javafx.beans.property.SimpleStringProperty("");
        });

        // Date column with formatted date
        dateColumn.setCellValueFactory(cellData -> {
            if (cellData.getValue().getReservationDate() != null) {
                return new javafx.beans.property.SimpleStringProperty(
                    cellData.getValue().getReservationDate().format(dateFormatter)
                );
            }
            return new javafx.beans.property.SimpleStringProperty("");
        });

        // Price column
        priceColumn.setCellValueFactory(new PropertyValueFactory<>("prix"));

        // Status column with custom style
        statusColumn.setCellFactory(column -> {
            return new TableCell<Reservation, String>() {
                private final Label label = new Label();
                
                @Override
                protected void updateItem(String item, boolean empty) {
                    super.updateItem(item, empty);
                    if (empty) {
                        setGraphic(null);
                    } else {
                        Reservation reservation = getTableView().getItems().get(getIndex());
                        label.setText(reservation.getStatus());
                        label.getStyleClass().setAll("status-" + reservation.getStatus().toLowerCase());
                        setGraphic(label);
                    }
                }
            };
        });
        statusColumn.setCellValueFactory(cellData -> 
            new javafx.beans.property.SimpleStringProperty(cellData.getValue().getStatus()));

        // Actions column with buttons
        actionsColumn.setCellFactory(column -> {
            return new TableCell<Reservation, Void>() {
                private final Button viewButton = new Button("View");
                private final Button downloadButton = new Button("Download PDF");
                private final HBox buttonsBox = new HBox(10);

                {
                    // Style the buttons
                    viewButton.getStyleClass().addAll("action-button", "view");
                    downloadButton.getStyleClass().addAll("action-button", "download");
                    
                    buttonsBox.setAlignment(Pos.CENTER);
                    buttonsBox.getChildren().addAll(viewButton, downloadButton);
                    
                    // Button actions
                    viewButton.setOnAction(event -> {
                        Reservation reservation = getTableView().getItems().get(getIndex());
                        handleViewReservation(reservation);
                    });
                    
                    downloadButton.setOnAction(event -> {
                        Reservation reservation = getTableView().getItems().get(getIndex());
                        handleDownloadPDF(reservation);
                    });
                }

                @Override
                protected void updateItem(Void item, boolean empty) {
                    super.updateItem(item, empty);
                    setGraphic(empty ? null : buttonsBox);
                }
            };
        });
    }

    private void handleDownloadPDF(Reservation reservation) {
        try {
            // Generate PDF
            File pdfFile = PDFGenerator.generateReservationPDF(reservation);

            // Create file chooser
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Enregistrer le PDF");
            fileChooser.setInitialFileName("reservation_" + reservation.getId() + ".pdf");
            fileChooser.getExtensionFilters().add(
                new FileChooser.ExtensionFilter("PDF Files", "*.pdf")
            );

            // Show save dialog
            File savedFile = fileChooser.showSaveDialog(reservationsTable.getScene().getWindow());
            
            if (savedFile != null) {
                // Copy the generated PDF to the chosen location
                java.nio.file.Files.copy(
                    pdfFile.toPath(),
                    savedFile.toPath(),
                    java.nio.file.StandardCopyOption.REPLACE_EXISTING
                );
                
                // Delete the temporary file
                pdfFile.delete();
                
                showAlert("Succès", "Le PDF a été généré avec succès!");
            }
        } catch (IOException e) {
            e.printStackTrace();
            showAlert("Erreur", "Impossible de générer le PDF: " + e.getMessage());
        }
    }

    private void loadUserReservations(User user) {
        List<Reservation> userReservations = reservationService.getReservationsByUserId(user.getId());
        reservationsList.clear();
        reservationsList.addAll(userReservations);
    }

    private void handleViewReservation(Reservation reservation) {
        // Implement the logic to view reservation details
        System.out.println("Viewing reservation: " + reservation.getId());
    }

    private void showAlert(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.WARNING);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
