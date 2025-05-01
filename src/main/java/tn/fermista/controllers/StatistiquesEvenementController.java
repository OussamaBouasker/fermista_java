package tn.fermista.controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.chart.*;
import javafx.scene.control.Label;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import tn.fermista.models.Reservation;
import tn.fermista.models.Workshop;
import tn.fermista.models.User;
import tn.fermista.services.ServiceReservation;
import tn.fermista.services.ServiceWorkshop;
import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.stage.Stage;
import java.io.IOException;

import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.stream.Collectors;

public class StatistiquesEvenementController implements Initializable {
    @FXML private BarChart<String, Number> userReservationsChart;
    @FXML private PieChart workshopPopularityChart;
    @FXML private LineChart<String, Number> reservationsTimeChart;
    @FXML private PieChart workshopTypeChart;
    
    @FXML private Label totalWorkshopsLabel;
    @FXML private Label totalReservationsLabel;
    @FXML private Label avgReservationsLabel;
    @FXML private Label mostActiveUserLabel;

    private ServiceReservation serviceReservation;
    private ServiceWorkshop serviceWorkshop;

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        serviceReservation = new ServiceReservation();
        serviceWorkshop = new ServiceWorkshop();

        try {
            loadAllStatistics();
        } catch (SQLException e) {
            e.printStackTrace();
            // Gérer l'erreur de manière appropriée
        }
    }

    private void loadAllStatistics() throws SQLException {
        List<Workshop> workshops = serviceWorkshop.showAll();
        List<Reservation> reservations = serviceReservation.showAll();

        loadUserReservationsChart(reservations);
        loadWorkshopPopularityChart(workshops, reservations);
        loadReservationsTimeChart(reservations);
        loadWorkshopTypeChart(workshops);
        updateGeneralStatistics(workshops, reservations);
    }

    private void loadUserReservationsChart(List<Reservation> reservations) {
        // Compter les réservations par utilisateur
        Map<User, Long> userReservationCount = reservations.stream()
            .filter(r -> r.getUser() != null)
            .collect(Collectors.groupingBy(Reservation::getUser, Collectors.counting()));

        // Trier et prendre les top 10 utilisateurs
        List<Map.Entry<User, Long>> topUsers = userReservationCount.entrySet().stream()
            .sorted(Map.Entry.<User, Long>comparingByValue().reversed())
            .limit(10)
            .collect(Collectors.toList());

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Nombre de réservations");

        for (Map.Entry<User, Long> entry : topUsers) {
            User user = entry.getKey();
            String userName = user.getFirstName() + " " + user.getLastName();
            series.getData().add(new XYChart.Data<>(userName, entry.getValue()));
        }

        userReservationsChart.getData().clear();
        userReservationsChart.getData().add(series);
    }

    private void loadWorkshopPopularityChart(List<Workshop> workshops, List<Reservation> reservations) {
        // Compter les réservations par workshop
        Map<Workshop, Long> workshopReservationCount = reservations.stream()
            .filter(r -> r.getWorkshop() != null)
            .collect(Collectors.groupingBy(Reservation::getWorkshop, Collectors.counting()));

        // Prendre les top 5 workshops
        List<PieChart.Data> pieChartData = workshopReservationCount.entrySet().stream()
            .sorted(Map.Entry.<Workshop, Long>comparingByValue().reversed())
            .limit(5)
            .map(entry -> new PieChart.Data(entry.getKey().getTitre(), entry.getValue()))
            .collect(Collectors.toList());

        workshopPopularityChart.setData(FXCollections.observableArrayList(pieChartData));
    }

    private void loadReservationsTimeChart(List<Reservation> reservations) {
        // Grouper les réservations par mois
        Map<String, Long> monthlyReservations = reservations.stream()
            .filter(r -> r.getReservationDate() != null)
            .collect(Collectors.groupingBy(
                r -> r.getReservationDate().format(DateTimeFormatter.ofPattern("yyyy-MM")),
                Collectors.counting()
            ));

        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Réservations par mois");

        // Trier les mois chronologiquement
        monthlyReservations.entrySet().stream()
            .sorted(Map.Entry.comparingByKey())
            .forEach(entry -> series.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue())));

        reservationsTimeChart.getData().clear();
        reservationsTimeChart.getData().add(series);
    }

    private void loadWorkshopTypeChart(List<Workshop> workshops) {
        // Compter les workshops par type
        Map<String, Long> typeCount = workshops.stream()
            .filter(w -> w.getType() != null)
            .collect(Collectors.groupingBy(Workshop::getType, Collectors.counting()));

        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();
        typeCount.forEach((type, count) -> 
            pieChartData.add(new PieChart.Data(type, count))
        );

        workshopTypeChart.setData(pieChartData);
    }

    private void updateGeneralStatistics(List<Workshop> workshops, List<Reservation> reservations) {
        // Nombre total de workshops
        totalWorkshopsLabel.setText("Nombre total de workshops : " + workshops.size());

        // Nombre total de réservations
        totalReservationsLabel.setText("Nombre total de réservations : " + reservations.size());

        // Moyenne de réservations par workshop
        double avgReservations = workshops.isEmpty() ? 0 : 
            (double) reservations.size() / workshops.size();
        avgReservationsLabel.setText(String.format("Moyenne de réservations par workshop : %.2f", avgReservations));

        // Utilisateur le plus actif
        Map<User, Long> userReservationCount = reservations.stream()
            .filter(r -> r.getUser() != null)
            .collect(Collectors.groupingBy(Reservation::getUser, Collectors.counting()));

        Optional<Map.Entry<User, Long>> mostActiveUser = userReservationCount.entrySet().stream()
            .max(Map.Entry.comparingByValue());

        mostActiveUserLabel.setText("Utilisateur le plus actif : " + 
            mostActiveUser.map(entry -> entry.getKey().getFirstName() + " " + 
                entry.getKey().getLastName() + " (" + entry.getValue() + " réservations)")
            .orElse("Aucun"));
    }

    @FXML
    private void handleReturn(ActionEvent event) {
        try {
            Parent root = FXMLLoader.load(getClass().getResource("/DashboardTemplate.fxml"));
            Scene scene = new Scene(root);
            Stage stage = (Stage) ((Node) event.getSource()).getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
            // Gérer l'erreur de manière appropriée
        }
    }
} 