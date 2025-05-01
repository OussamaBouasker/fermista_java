package tn.fermista.controllers;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Side;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.*;
import javafx.scene.Node;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.HBox;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;
import javafx.scene.shape.Circle;
import javafx.stage.Stage;
import javafx.stage.FileChooser;
import tn.fermista.models.User;
import tn.fermista.models.Reclamation;
import tn.fermista.services.*;
import javafx.scene.image.WritableImage;
import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import com.itextpdf.text.Document;
import com.itextpdf.text.Image;
import com.itextpdf.text.pdf.PdfWriter;
import com.itextpdf.text.PageSize;

import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class StatController implements Initializable {
    // Add color definitions
    private final String orangePastel = "#FFB347";  // Pastel Orange
    private final String lavande = "#E6E6FA";       // Lavender
    private final String vertMenthe = "#98FF98";    // Mint Green

    @FXML private PieChart rolesPieChart;
    @FXML private BarChart<String, Number> usersStateBarChart;
    @FXML private BarChart<String, Number> reclamationsStatusBarChart;
    @FXML private BarChart<String, Number> reclamationsByUserBarChart;
    @FXML private LineChart<String, Number> reclamationsEvolutionLineChart;
    @FXML private HBox userBarLegend;
    @FXML private AnchorPane rootAnchorPane;

    @FXML private CategoryAxis usersStateXAxis;
    @FXML private NumberAxis usersStateYAxis;
    @FXML private CategoryAxis reclamationsStatusXAxis;
    @FXML private NumberAxis reclamationsStatusYAxis;
    @FXML private CategoryAxis reclamationsByUserXAxis;
    @FXML private NumberAxis reclamationsByUserYAxis;
    @FXML private CategoryAxis reclamationsEvolutionXAxis;
    @FXML private NumberAxis reclamationsEvolutionYAxis;

    private final ServiceUser serviceUser = new ServiceUser();
    private final ServiceReclamation serviceReclamation = new ServiceReclamation();
    private final ServiceAdmin serviceAdmin = new ServiceAdmin();
    private final ServiceAgriculteur serviceAgriculteur = new ServiceAgriculteur();
    private final ServiceClient serviceClient = new ServiceClient();
    private final ServiceFormateur serviceFormateur = new ServiceFormateur();
    private final ServiceVeterinaire serviceVeterinaire = new ServiceVeterinaire();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        try {
            // Configurer les styles des graphiques
            configureChartStyles();
            
            // Initialiser les graphiques
            initializeRolesPieChart();
            initializeUsersStateBarChart();
            initializeReclamationsStatusBarChart();
            initializeReclamationsByUserBarChart();
            initializeReclamationsEvolutionLineChart();
            
            // Appliquer les couleurs pastel
            applyPastelColors();
            
            // Ajouter des animations
            addAnimations();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void configureChartStyles() {
        // Styles communs pour tous les graphiques
        String commonStyle = "-fx-font-family: 'Century Gothic'; -fx-font-size: 14px;";
        String titleStyle = "-fx-font-family: 'Century Gothic'; -fx-font-weight: bold; -fx-font-style: italic; -fx-font-size: 24px; -fx-text-fill: #2C3E50; -fx-effect: dropshadow(gaussian, rgba(0,0,0,0.1), 2, 0, 0, 1);";
        
        // Configurer les styles des titres
        if (rolesPieChart != null) {
            rolesPieChart.setStyle(commonStyle);
            rolesPieChart.setTitle("Distribution des Utilisateurs par Rôle");
            rolesPieChart.setTitleSide(Side.TOP);
            rolesPieChart.setLegendVisible(false);
            rolesPieChart.lookup(".chart-title").setStyle(titleStyle);
        }

        if (usersStateBarChart != null) {
            usersStateBarChart.setStyle(commonStyle);
            usersStateBarChart.setTitle("État des Utilisateurs");
            usersStateBarChart.setTitleSide(Side.TOP);
            usersStateBarChart.setLegendVisible(false);
            usersStateBarChart.lookup(".chart-title").setStyle(titleStyle);
        }

        if (reclamationsStatusBarChart != null) {
            reclamationsStatusBarChart.setStyle(commonStyle);
            reclamationsStatusBarChart.setTitle("Statut des Réclamations");
            reclamationsStatusBarChart.setTitleSide(Side.TOP);
            reclamationsStatusBarChart.setLegendVisible(false);
            reclamationsStatusBarChart.lookup(".chart-title").setStyle(titleStyle);
        }

        if (reclamationsByUserBarChart != null) {
            reclamationsByUserBarChart.setStyle(commonStyle);
            reclamationsByUserBarChart.setTitle("Réclamations par Utilisateur");
            reclamationsByUserBarChart.setTitleSide(Side.TOP);
            reclamationsByUserBarChart.setLegendVisible(false);
            reclamationsByUserBarChart.lookup(".chart-title").setStyle(titleStyle);
        }

        if (reclamationsEvolutionLineChart != null) {
            reclamationsEvolutionLineChart.setStyle(commonStyle);
            reclamationsEvolutionLineChart.setTitle("Évolution des Réclamations");
            reclamationsEvolutionLineChart.setTitleSide(Side.TOP);
            reclamationsEvolutionLineChart.setLegendVisible(true);
            reclamationsEvolutionLineChart.lookup(".chart-title").setStyle(titleStyle);
        }
    }

    private void addAnimations() {
        // Animation pour le PieChart
        if (rolesPieChart != null) {
            rolesPieChart.getData().forEach(data -> {
                data.getNode().setOnMouseEntered(e -> {
                    data.getNode().setScaleX(1.1);
                    data.getNode().setScaleY(1.1);
                });
                data.getNode().setOnMouseExited(e -> {
                    data.getNode().setScaleX(1.0);
                    data.getNode().setScaleY(1.0);
                });
            });
        }

        // Animation pour les BarCharts
        animateBarChart(usersStateBarChart);
        animateBarChart(reclamationsStatusBarChart);
        animateBarChart(reclamationsByUserBarChart);

        // Animation pour le LineChart
        if (reclamationsEvolutionLineChart != null) {
            reclamationsEvolutionLineChart.getData().forEach(series -> {
                series.getNode().setOnMouseEntered(e -> {
                    series.getNode().setScaleX(1.05);
                    series.getNode().setScaleY(1.05);
                });
                series.getNode().setOnMouseExited(e -> {
                    series.getNode().setScaleX(1.0);
                    series.getNode().setScaleY(1.0);
                });
            });
        }
    }

    private void animateBarChart(BarChart<String, Number> chart) {
        if (chart != null) {
            chart.getData().forEach(series -> {
                series.getData().forEach(data -> {
                    Node node = data.getNode();
                    if (node != null) {
                        node.setOnMouseEntered(e -> {
                            node.setScaleX(1.1);
                            node.setScaleY(1.1);
                        });
                        node.setOnMouseExited(e -> {
                            node.setScaleX(1.0);
                            node.setScaleY(1.0);
                        });
                    }
                });
            });
        }
    }

    private void initializeRolesPieChart() throws SQLException {
        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();
        
        // Compter les utilisateurs par rôle
        int adminCount = serviceAdmin.rechercher().size();
        int agriculteurCount = serviceAgriculteur.rechercher().size();
        int clientCount = serviceClient.rechercher().size();
        int formateurCount = serviceFormateur.rechercher().size();
        int veterinaireCount = serviceVeterinaire.rechercher().size();

        pieChartData.add(new PieChart.Data("Admin", adminCount));
        pieChartData.add(new PieChart.Data("Agriculteur", agriculteurCount));
        pieChartData.add(new PieChart.Data("Client", clientCount));
        pieChartData.add(new PieChart.Data("Formateur", formateurCount));
        pieChartData.add(new PieChart.Data("Vétérinaire", veterinaireCount));

        rolesPieChart.setData(pieChartData);
    }

    private void initializeUsersStateBarChart() throws SQLException {
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("État des utilisateurs");

        List<User> allUsers = serviceUser.showAll();
        long activeCount = allUsers.stream().filter(User::getState).count();
        long inactiveCount = allUsers.size() - activeCount;

        series.getData().add(new XYChart.Data<>("Actifs", activeCount));
        series.getData().add(new XYChart.Data<>("Inactifs", inactiveCount));

        usersStateBarChart.getData().add(series);
        usersStateYAxis.setLabel("Nombre d'utilisateurs");
    }

    private void initializeReclamationsStatusBarChart() throws SQLException {
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Statut des réclamations");

        List<Reclamation> allReclamations = serviceReclamation.showAll();
        Map<String, Long> statusCount = allReclamations.stream()
                .collect(Collectors.groupingBy(Reclamation::getStatus, Collectors.counting()));

        statusCount.forEach((status, count) -> 
            series.getData().add(new XYChart.Data<>(status, count)));

        reclamationsStatusBarChart.getData().add(series);
        reclamationsStatusYAxis.setLabel("Nombre de réclamations");
    }

    private void initializeReclamationsByUserBarChart() throws SQLException {
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Réclamations par utilisateur");

        List<Reclamation> allReclamations = serviceReclamation.showAll();
        Map<String, Long> userReclamations = allReclamations.stream()
                .collect(Collectors.groupingBy(
                    r -> r.getUser().getFirstName() + " " + r.getUser().getLastName(),
                    Collectors.counting()
                ));

        userReclamations.forEach((user, count) -> 
            series.getData().add(new XYChart.Data<>(user, count)));

        reclamationsByUserBarChart.getData().add(series);
        reclamationsByUserYAxis.setLabel("Nombre de réclamations");
    }

    private void initializeReclamationsEvolutionLineChart() throws SQLException {
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Évolution des réclamations");

        List<Reclamation> allReclamations = serviceReclamation.showAll();
        Map<String, Long> reclamationsByDate = allReclamations.stream()
                .collect(Collectors.groupingBy(
                    r -> r.getDateSoumission().format(DateTimeFormatter.ofPattern("dd/MM/yyyy")),
                    Collectors.counting()
                ));

        // Trier les dates
        List<String> sortedDates = new ArrayList<>(reclamationsByDate.keySet());
        Collections.sort(sortedDates, (d1, d2) -> {
            String[] parts1 = d1.split("/");
            String[] parts2 = d2.split("/");
            return LocalDateTime.of(
                Integer.parseInt(parts1[2]),
                Integer.parseInt(parts1[1]),
                Integer.parseInt(parts1[0]),
                0, 0
            ).compareTo(LocalDateTime.of(
                Integer.parseInt(parts2[2]),
                Integer.parseInt(parts2[1]),
                Integer.parseInt(parts2[0]),
                0, 0
            ));
        });

        // Limiter le nombre de points à afficher pour une meilleure lisibilité
        int maxPoints = 10;
        int step = Math.max(1, sortedDates.size() / maxPoints);
        
        for (int i = 0; i < sortedDates.size(); i += step) {
            String date = sortedDates.get(i);
            series.getData().add(new XYChart.Data<>(date, reclamationsByDate.get(date)));
        }

        reclamationsEvolutionLineChart.getData().add(series);
        reclamationsEvolutionYAxis.setLabel("Nombre de réclamations");
    }

    private void applyPastelColors() {
        // Définir les couleurs pastel
        String rosePastel = "#FFB6C1";  // Rose pastel pour Admin
        String vertPastel = "#98FB98";  // Vert pastel pour Agriculteur
        String bleuPastel = "#87CEEB";  // Bleu pastel pour Client
        String violetPastel = "#DDA0DD"; // Violet pastel pour Formateur
        String jaunePastel = "#F0E68C";  // Jaune pastel pour Vétérinaire

        // Appliquer les couleurs au PieChart avec une légende synchronisée
        if (rolesPieChart != null && rolesPieChart.getData() != null) {
            Map<String, String> roleColors = new HashMap<>();
            roleColors.put("Admin", rosePastel);
            roleColors.put("Agriculteur", vertPastel);
            roleColors.put("Client", bleuPastel);
            roleColors.put("Formateur", violetPastel);
            roleColors.put("Vétérinaire", jaunePastel);

            for (PieChart.Data data : rolesPieChart.getData()) {
                String role = data.getName();
                String color = roleColors.get(role);
                if (color != null && data.getNode() != null) {
                    data.getNode().setStyle("-fx-pie-color: " + color + "; -fx-background-radius: 10;");
                }
            }

            // Créer une légende personnalisée
            HBox legend = new HBox(10); // 10 pixels d'espacement
            legend.setStyle("-fx-padding: 10;");
            legend.setAlignment(javafx.geometry.Pos.CENTER);

            for (Map.Entry<String, String> entry : roleColors.entrySet()) {
                HBox item = new HBox(5);
                Circle circle = new Circle(6);
                circle.setStyle("-fx-fill: " + entry.getValue() + ";");
                Label label = new Label(entry.getKey());
                label.setStyle("-fx-font-size: 12px;");
                item.getChildren().addAll(circle, label);
                legend.getChildren().add(item);
            }

            // Ajouter la légende sous le graphique
            VBox container = (VBox) rolesPieChart.getParent();
            if (container != null) {
                container.getChildren().add(legend);
            }
        }

        // Appliquer les couleurs spécifiques au BarChart des états des utilisateurs
        if (usersStateBarChart != null && usersStateBarChart.getData() != null) {
            for (XYChart.Series<String, Number> series : usersStateBarChart.getData()) {
                for (XYChart.Data<String, Number> data : series.getData()) {
                    if (data.getNode() != null) {
                        String color = data.getXValue().equals("Inactifs") ? rosePastel : vertPastel;
                        data.getNode().setStyle("-fx-bar-fill: " + color + "; -fx-background-radius: 5;");
                    }
                }
            }
        }

        // Appliquer les couleurs spécifiques au BarChart des statuts des réclamations
        if (reclamationsStatusBarChart != null && reclamationsStatusBarChart.getData() != null) {
            for (XYChart.Series<String, Number> series : reclamationsStatusBarChart.getData()) {
                for (XYChart.Data<String, Number> data : series.getData()) {
                    if (data.getNode() != null) {
                        String xVal = data.getXValue().toLowerCase();
                        String color;
                        if (xVal.contains("cancel")) {
                            color = rosePastel;
                        } else if (xVal.contains("pending")) {
                            color = jaunePastel;
                        } else if (xVal.contains("confirm")) {
                            color = vertPastel;
                        } else {
                            color = bleuPastel;
                        }
                        data.getNode().setStyle("-fx-bar-fill: " + color + "; -fx-background-radius: 5 !important;");
                    }
                }
            }
        }

        // Appliquer des couleurs différentes pour chaque utilisateur dans le BarChart des réclamations par utilisateur
        if (reclamationsByUserBarChart != null && reclamationsByUserBarChart.getData() != null && userBarLegend != null) {
            userBarLegend.getChildren().clear();
            int colorIndex = 0;
            for (XYChart.Series<String, Number> series : reclamationsByUserBarChart.getData()) {
                for (XYChart.Data<String, Number> data : series.getData()) {
                    if (data.getNode() != null) {
                        String color;
                        switch (colorIndex % 8) {
                            case 0: color = rosePastel; break;
                            case 1: color = vertPastel; break;
                            case 2: color = bleuPastel; break;
                            case 3: color = violetPastel; break;
                            case 4: color = jaunePastel; break;
                            case 5: color = orangePastel; break;
                            case 6: color = lavande; break;
                            default: color = vertMenthe; break;
                        }
                        data.getNode().setStyle("-fx-bar-fill: " + color + "; -fx-background-radius: 5;");
                        // Ajout à la légende
                        Circle circle = new Circle(7);
                        circle.setStyle("-fx-fill: " + color + ";");
                        Label label = new Label(data.getXValue());
                        label.setStyle("-fx-font-size: 14px; -fx-text-fill: #333333;");
                        userBarLegend.getChildren().addAll(circle, label);
                        colorIndex++;
                    }
                }
            }
        }

        // Appliquer les couleurs au LineChart
        if (reclamationsEvolutionLineChart != null && reclamationsEvolutionLineChart.getData() != null) {
            int colorIndex = 0;
            for (XYChart.Series<String, Number> series : reclamationsEvolutionLineChart.getData()) {
                if (series.getNode() != null) {
                    String color;
                    switch (colorIndex % 8) {
                        case 0: color = rosePastel; break;
                        case 1: color = vertPastel; break;
                        case 2: color = bleuPastel; break;
                        case 3: color = violetPastel; break;
                        case 4: color = jaunePastel; break;
                        case 5: color = orangePastel; break;
                        case 6: color = lavande; break;
                        default: color = vertMenthe; break;
                    }
                    series.getNode().setStyle("-fx-stroke: " + color + "; -fx-stroke-width: 3;");
                    colorIndex++;
                }
            }
        }
    }

    public void DashboardTemplate(ActionEvent actionEvent) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/DashboardTemplate.fxml"));
            Parent root = loader.load();
            Stage stage = (Stage) ((Node) actionEvent.getSource()).getScene().getWindow();
            stage.setScene(new Scene(root));
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            System.err.println("Erreur lors du chargement de DashboardTemplate.fxml: " + e.getMessage());
        }
    }

    public void ExporterPDF(ActionEvent actionEvent) {
        try {
            // 1. Ouvrir le FileChooser
            FileChooser fileChooser = new FileChooser();
            fileChooser.setTitle("Enregistrer le PDF");
            fileChooser.getExtensionFilters().add(new FileChooser.ExtensionFilter("Fichier PDF", "*.pdf"));
            java.io.File file = fileChooser.showSaveDialog(((Node) actionEvent.getSource()).getScene().getWindow());
            if (file == null) return;

            // 2. Créer une copie temporaire de l'interface sans les boutons
            Node[] nodesToHide = rootAnchorPane.lookupAll("Button").toArray(new Node[0]);
            boolean[] originalVisibility = new boolean[nodesToHide.length];
            
            // Sauvegarder l'état de visibilité et cacher les boutons
            for (int i = 0; i < nodesToHide.length; i++) {
                originalVisibility[i] = nodesToHide[i].isVisible();
                nodesToHide[i].setVisible(false);
            }

            // 3. Capturer l'AnchorPane sans les boutons
            WritableImage snapshot = rootAnchorPane.snapshot(null, null);
            
            // 4. Restaurer la visibilité des boutons
            for (int i = 0; i < nodesToHide.length; i++) {
                nodesToHide[i].setVisible(originalVisibility[i]);
            }

            // 5. Sauvegarder l'image temporaire
            java.io.File tempImg = java.io.File.createTempFile("stat_snapshot", ".png");
            ImageIO.write(javafx.embed.swing.SwingFXUtils.fromFXImage(snapshot, null), "png", tempImg);

            // 6. Générer le PDF avec iText
            Document document = new Document(PageSize.A4.rotate());
            PdfWriter.getInstance(document, new java.io.FileOutputStream(file));
            document.open();
            Image img = Image.getInstance(tempImg.getAbsolutePath());
            
            // Ajuster l'image à la taille de la page
            float pageWidth = document.getPageSize().getWidth() - document.leftMargin() - document.rightMargin();
            float pageHeight = document.getPageSize().getHeight() - document.topMargin() - document.bottomMargin();
            img.scaleToFit(pageWidth, pageHeight);
            
            // Centrer l'image sur la page
            img.setAlignment(Image.MIDDLE);
            
            document.add(img);
            document.close();
            
            // Supprimer le fichier temporaire
            tempImg.delete();
            
        } catch (Exception e) {
            e.printStackTrace();
            System.err.println("Erreur lors de l'exportation en PDF: " + e.getMessage());
        }
    }
}
