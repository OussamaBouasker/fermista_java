package tn.fermista.controllers.Front;

// Add these new imports
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import javafx.scene.Node;  // Add this import too for createPage method
import javafx.scene.layout.*;
import javafx.scene.image.ImageView;
import javafx.scene.image.Image;
import tn.fermista.models.Workshop;
import tn.fermista.services.ServiceWorkshop;
import java.util.ArrayList;
import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;
import java.util.stream.Collectors;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;
import tn.fermista.utils.UserSession;

public class ShowWorkshop implements Initializable {
    // FXML injected fields
    // Remove these fields and constants
    @FXML private FlowPane workshopsContainer;
    @FXML private TextField searchField;
    @FXML private Button allWorkshopsButton;
    @FXML private Button atelierLiveButton;
    @FXML private Button formationAutonomeButton;
    @FXML private Button prevPageButton;
    @FXML private Button nextPageButton;
    @FXML private Label pageInfoLabel;
    @FXML private Button myReservationsButton;

    // Add these new fields for pagination
    private static final int ITEMS_PER_PAGE = 6;
    private int currentPage = 0;
    private int totalPages = 0;
    
    // Class fields
    private ServiceWorkshop serviceWorkshop;
    private List<Workshop> currentWorkshops;

    // Simplified loadWorkshops method
    private void loadWorkshops() {
        try {
            currentWorkshops = new ArrayList<>(serviceWorkshop.showAll());
            updatePagination();
            displayCurrentPage();
        } catch (SQLException e) {
            e.printStackTrace();
            currentWorkshops = new ArrayList<>();
            workshopsContainer.getChildren().clear();
        }
    }

    private void updatePagination() {
        totalPages = (int) Math.ceil((double) currentWorkshops.size() / ITEMS_PER_PAGE);
        currentPage = Math.min(currentPage, Math.max(0, totalPages - 1));
        updatePaginationControls();
    }

    private void displayCurrentPage() {
        workshopsContainer.getChildren().clear();
        int startIndex = currentPage * ITEMS_PER_PAGE;
        int endIndex = Math.min(startIndex + ITEMS_PER_PAGE, currentWorkshops.size());
        
        if (startIndex < currentWorkshops.size()) {
            for (Workshop workshop : currentWorkshops.subList(startIndex, endIndex)) {
                workshopsContainer.getChildren().add(createWorkshopCard(workshop));
            }
        }
    }

    private Node createWorkshopCard(Workshop workshop) {
        VBox card = new VBox();
        card.getStyleClass().add("workshop-card");
        card.setPrefWidth(330);
        card.setMaxWidth(330);
        card.setMinWidth(330);
        
        // Image Container
        StackPane imageContainer = new StackPane();
        imageContainer.getStyleClass().add("card-image-container");
        imageContainer.setPrefWidth(330);
        imageContainer.setMaxWidth(330);
        imageContainer.setMinWidth(330);
        
        ImageView imageView = new ImageView();
        imageView.setFitWidth(330);
        imageView.setFitHeight(200);
        imageView.setPreserveRatio(false);
        
        // Load workshop image
        try {
            if (workshop.getImage() != null && !workshop.getImage().isEmpty()) {
                Image image = new Image(getClass().getResourceAsStream(workshop.getImage()));
                imageView.setImage(image);
            } else {
                imageView.setImage(new Image(getClass().getResourceAsStream("/images/client.jpg")));
            }
        } catch (Exception e) {
            try {
                imageView.setImage(new Image(getClass().getResourceAsStream("/images/client.jpg")));
            } catch (Exception ex) {
                e.printStackTrace();
            }
        }
        
        imageView.getStyleClass().add("card-image");
        
        // Type badge overlay
        Label typeBadge = new Label(workshop.getType());
        typeBadge.getStyleClass().add("card-type-badge");
        StackPane.setAlignment(typeBadge, Pos.TOP_LEFT);
        StackPane.setMargin(typeBadge, new Insets(10, 0, 0, 10));
        
        imageContainer.getChildren().addAll(imageView, typeBadge);
        
        // Content Container
        VBox content = new VBox(15);
        content.getStyleClass().add("card-content");
        content.setPrefWidth(330);
        content.setMaxWidth(330);
        
        // Title
        Label title = new Label(workshop.getTitre());
        title.getStyleClass().add("card-title");
        title.setWrapText(true);
        
        // Description
        Label description = new Label(workshop.getDescription());
        description.getStyleClass().add("card-description");
        description.setWrapText(true);
        description.setMaxHeight(60); // Approximately 3 lines
        
        // Bottom section with price and button
        HBox bottomSection = new HBox();
        bottomSection.setAlignment(Pos.CENTER_LEFT);
        bottomSection.setSpacing(20);
        bottomSection.setPrefWidth(290);
        
        Label price = new Label(workshop.getPrix() + " DT");
        price.getStyleClass().add("card-price");
        
        Button detailsButton = new Button("Afficher détails");
        detailsButton.getStyleClass().add("card-button");
        
        // Formateur info (if live workshop)
        VBox infoBox = new VBox(5);
        if ("Atelier Live".equals(workshop.getType()) && workshop.getUser() != null) {
            Label formateur = new Label("Par " + workshop.getUser().getFirstName() + " " + workshop.getUser().getLastName());
            formateur.getStyleClass().add("card-formateur");
            infoBox.getChildren().add(formateur);
        }
        
        HBox.setHgrow(infoBox, Priority.ALWAYS);
        bottomSection.getChildren().addAll(price, detailsButton);
        
        content.getChildren().addAll(title, description, infoBox, bottomSection);
        card.getChildren().addAll(imageContainer, content);
        
        // Make the entire card clickable
        card.setOnMouseClicked(e -> showWorkshopDetails(workshop));
        detailsButton.setOnAction(e -> {
            e.consume(); // Prevent event bubbling
            showWorkshopDetails(workshop);
        });
        
        return card;
    }

    private void updatePaginationControls() {
        pageInfoLabel.setText(String.format("Page %d/%d", currentPage + 1, totalPages));
        prevPageButton.setDisable(currentPage == 0);
        nextPageButton.setDisable(currentPage >= totalPages - 1);
    }

    @FXML
    private void handlePrevPage() {
        if (currentPage > 0) {
            currentPage--;
            displayCurrentPage();
            updatePaginationControls();
        }
    }

    @FXML
    private void handleNextPage() {
        if (currentPage < totalPages - 1) {
            currentPage++;
            displayCurrentPage();
            updatePaginationControls();
        }
    }

    // Simplified filterByType method
    private void filterByType(String type) {
        try {
            List<Workshop> allWorkshops = serviceWorkshop.showAll();
            currentWorkshops = type.equals("all") ? allWorkshops :
                allWorkshops.stream()
                    .filter(workshop -> workshop.getType().equals(type))
                    .collect(Collectors.toList());
            
            currentPage = 0;
            updatePagination();
            displayCurrentPage();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Simplified filterWorkshops method
    private void filterWorkshops(String searchText) {
        try {
            if (searchText == null || searchText.trim().isEmpty()) {
                loadWorkshops();
                return;
            }

            List<Workshop> allWorkshops = serviceWorkshop.showAll();
            String searchLower = searchText.toLowerCase().trim();
            
            currentWorkshops = allWorkshops.stream()
                .filter(workshop -> matchesSearch(workshop, searchLower))
                .collect(Collectors.toList());
            
            currentPage = 0;
            updatePagination();
            displayCurrentPage();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    // Add this to your initialize method
    @Override
    public void initialize(URL url, ResourceBundle rb) {
        serviceWorkshop = new ServiceWorkshop();
        
        // Setup search field
        searchField.textProperty().addListener((obs, oldVal, newVal) -> filterWorkshops(newVal));
        
        // Setup filter buttons
        setupFilterButtons();
        
        // Setup reservation button
        myReservationsButton.setOnAction(event -> showReservations());
        
        // Initial load
        loadWorkshops();
    }

    // Add this method to handle reservation navigation
    private void showReservations() {
        // Check if user is logged in
        if (UserSession.getCurrentUser() == null) {
            showAlert(Alert.AlertType.WARNING, "Accès refusé", 
                     "Vous devez être connecté pour accéder à vos réservations.");
            return; // Stop here if user is not logged in
        }
        
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Front-Office/show-reservation-user.fxml"));
            Parent root = loader.load();
            
            Stage stage = (Stage) myReservationsButton.getScene().getWindow();
            Scene scene = new Scene(root);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible d'afficher les réservations");
        }
    }
    
    // Add these constants for workshop types
    private static final String TYPE_LIVE_WORKSHOP = "Atelier Live";
    private static final String TYPE_SELF_PACED_WORKSHOP = "Formation Autonome";

    // Update setupFilterButtons to use the constants

    private void setupFilterButtons() {
        allWorkshopsButton.setOnAction(e -> {
            updateFilterButtonStyles(allWorkshopsButton);
            filterByType("all");
        });

        atelierLiveButton.setOnAction(e -> {
            updateFilterButtonStyles(atelierLiveButton);
            filterByType(Workshop.TYPE_LIVE_WORKSHOP);
        });

        formationAutonomeButton.setOnAction(e -> {
            updateFilterButtonStyles(formationAutonomeButton);
            filterByType(Workshop.TYPE_SELF_PACED_WORKSHOP);
        });

        // Set initial active state
        updateFilterButtonStyles(allWorkshopsButton);
    }

    private void updateFilterButtonStyles(Button activeButton) {
        allWorkshopsButton.getStyleClass().remove("filter-button-active");
        atelierLiveButton.getStyleClass().remove("filter-button-active");
        formationAutonomeButton.getStyleClass().remove("filter-button-active");
        activeButton.getStyleClass().add("filter-button-active");
    }

    private boolean matchesSearch(Workshop workshop, String searchText) {
        return workshop.getTitre().toLowerCase().contains(searchText) ||
               workshop.getType().toLowerCase().contains(searchText) ||
               workshop.getPrix().toLowerCase().contains(searchText) ||
               (workshop.getUser() != null && 
                (workshop.getUser().getFirstName().toLowerCase().contains(searchText) ||
                 workshop.getUser().getLastName().toLowerCase().contains(searchText)));
    }

    private void showWorkshopDetails(Workshop workshop) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/Front-Office/workshopDetails.fxml"));
            Parent root = loader.load();
            
            Stage detailsStage = new Stage();
            detailsStage.setTitle("Détails du Workshop");
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/Front-Office/StyleWorkshops.css").toExternalForm());
            detailsStage.setScene(scene);
            
            WorkshopDetailsController controller = loader.getController();
            controller.initData(workshop);
            
            detailsStage.show();
        } catch (IOException e) {
            e.printStackTrace();
            showAlert(Alert.AlertType.ERROR, "Erreur", "Impossible d'afficher les détails du workshop");
        }
    }

    private void showAlert(Alert.AlertType type, String title, String content) {
        Alert alert = new Alert(type);
        alert.setTitle(title);
        alert.setContentText(content);
        alert.showAndWait();
    }
}
