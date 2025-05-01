package tn.fermista.controllers;

import eu.hansolo.medusa.Gauge;
import javafx.application.Platform;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.NumberAxis;
import javafx.scene.chart.CategoryAxis;
import javafx.scene.chart.XYChart;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.text.Text;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import tn.fermista.models.arg_temp;
import tn.fermista.models.arg_agit;
import tn.fermista.services.ServiceArgTemp;
import tn.fermista.services.ServiceArgAgit;

import javax.mail.*;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Comparator;
import java.util.List;
import java.util.Properties;
import java.util.ResourceBundle;
import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import javafx.event.ActionEvent;

public class TempChartController implements Initializable {

    @FXML private LineChart<String, Number> tempChart;
    @FXML private LineChart<String, Number> agitationChart;
    @FXML private Gauge tempGauge;
    @FXML private Button startButton;
    @FXML private Button stopButton;
    @FXML private Text tempText;
    @FXML private Text timeText;
    @FXML private Button historyButton;
    @FXML private HBox gaugeContainer;
    @FXML private VBox gaugeContent;
    @FXML private Gauge agitationGauge;
    @FXML private TableView<arg_agit> agitationTable;
    @FXML private Button agitationHistoryButton;
    @FXML private Text agitationTime;
    @FXML private Text agitationX;
    @FXML private Text agitationY;
    @FXML private Text agitationZ;
    @FXML private Text agitationText;
    @FXML private Text agitationTimeText;

    private final ServiceArgTemp tempService = new ServiceArgTemp();
    private final ServiceArgAgit agitService = new ServiceArgAgit();
    private final XYChart.Series<String, Number> tempSeries = new XYChart.Series<>();
    private final XYChart.Series<String, Number> xSeries = new XYChart.Series<>();
    private final XYChart.Series<String, Number> ySeries = new XYChart.Series<>();
    private final XYChart.Series<String, Number> zSeries = new XYChart.Series<>();
    private ScheduledExecutorService scheduler;
    private final DateTimeFormatter timeFormatter = DateTimeFormatter.ofPattern("HH:mm:ss");
    private static final int MAX_DATA_POINTS = 10;
    private LocalDateTime lastUpdateTime;

    private static final String MAILTRAP_HOST = "sandbox.smtp.mailtrap.io";
    private static final int MAILTRAP_PORT = 2525;
    private static final String MAILTRAP_USERNAME = "2ce990398763bd"; // À remplacer par votre username Mailtrap
    private static final String MAILTRAP_PASSWORD = "2518d5573ea489"; // À remplacer par votre password Mailtrap
    private static final String RECIPIENT_EMAIL = "yosrblaiech6@gmail.com";
    private LocalDateTime lastEmailSentTime = null;
    private static final int EMAIL_COOLDOWN_MINUTES = 5; // Délai minimum entre deux emails

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        tempSeries.setName("Température (°C)");
        tempChart.getData().add(tempSeries);
        tempChart.setAnimated(false);
        tempChart.setCreateSymbols(false);
        tempChart.setLegendVisible(true);

        xSeries.setName("Axe X");
        ySeries.setName("Axe Y");
        zSeries.setName("Axe Z");

        NumberAxis yAxis = (NumberAxis) agitationChart.getYAxis();
        yAxis.setAutoRanging(true);
        yAxis.setLabel("Valeur d'agitation");
        yAxis.setTickUnit(0.1);

        CategoryAxis xAxis = (CategoryAxis) agitationChart.getXAxis();
        xAxis.setLabel("Temps");

        agitationChart.getData().clear();
        agitationChart.getData().addAll(xSeries, ySeries, zSeries);
        agitationChart.setAnimated(false);
        agitationChart.setCreateSymbols(false);
        agitationChart.setLegendVisible(true);

        xSeries.getNode().setStyle("-fx-stroke: #e74c3c; -fx-stroke-width: 2;");
        ySeries.getNode().setStyle("-fx-stroke: #f39c12; -fx-stroke-width: 2;");
        zSeries.getNode().setStyle("-fx-stroke: #3498db; -fx-stroke-width: 2;");

        stopButton.setDisable(true);
        configureGauge();

        tempText.setText("Température: -- °C");
        timeText.setText("Heure: --:--:--");

        loadHistoricalData();
        updateGaugeWithLastValue();

        try {
            List<arg_agit> agitations = agitService.showAll();
            if (!agitations.isEmpty()) {
                arg_agit last = agitations.get(agitations.size() - 1);
                agitationTime.setText("Dernière Valeur");
                agitationX.setText(last.getAccelX());
                agitationY.setText(last.getAccelY());
                agitationZ.setText(last.getAccelZ());
            }
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void configureGauge() {
        tempGauge.setAnimated(true);
        tempGauge.setAnimationDuration(1000);
        tempGauge.setDecimals(1);
        tempGauge.setValueVisible(true);
        tempGauge.setNeedleColor(javafx.scene.paint.Color.valueOf("#2c3e50"));
        tempGauge.setThresholdColor(javafx.scene.paint.Color.RED);
        tempGauge.setThreshold(40.0);
        tempGauge.setThresholdVisible(true);
    }

    private void updateDisplayWithLastValue(arg_temp lastTemp) {
        if (lastTemp != null) {
            Platform.runLater(() -> {
                tempText.setText("Température: " + lastTemp.getTemperature() + " °C");
                timeText.setText("Heure: " + lastTemp.getTimeReceive().format(timeFormatter));
            });
        }
    }

    private void updateGaugeWithLastValue() {
        try {
            arg_temp lastTemp = tempService.getLastTemperature();
            if (lastTemp != null) {
                double tempValue = Double.parseDouble(lastTemp.getTemperature());
                Platform.runLater(() -> {
                    tempGauge.setValue(tempValue);
                    updateDisplayWithLastValue(lastTemp);
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void loadHistoricalData() {
        try {
            List<arg_temp> tempData = tempService.showAll();
            if (!tempData.isEmpty()) {
                tempData.sort(Comparator.comparing(arg_temp::getTimeReceive));
                int startIndex = Math.max(0, tempData.size() - MAX_DATA_POINTS);
                Platform.runLater(() -> {
                    tempSeries.getData().clear();
                    for (int i = startIndex; i < tempData.size(); i++) {
                        arg_temp temp = tempData.get(i);
                        String time = temp.getTimeReceive().format(timeFormatter);
                        double tempValue = Double.parseDouble(temp.getTemperature());
                        tempSeries.getData().add(new XYChart.Data<>(time, tempValue));
                    }
                });
                lastUpdateTime = tempData.get(tempData.size() - 1).getTimeReceive();
            }

            List<arg_agit> agitData = agitService.showAll();
            if (!agitData.isEmpty()) {
                agitData.sort(Comparator.comparing(arg_agit::getTimeReceive));
                int startIndex = Math.max(0, agitData.size() - MAX_DATA_POINTS);
                Platform.runLater(() -> {
                    xSeries.getData().clear();
                    ySeries.getData().clear();
                    zSeries.getData().clear();
                    for (int i = startIndex; i < agitData.size(); i++) {
                        arg_agit agit = agitData.get(i);
                        String time = agit.getTimeReceive().format(timeFormatter);
                        double x = Double.parseDouble(agit.getAccelX());
                        double y = Double.parseDouble(agit.getAccelY());
                        double z = Double.parseDouble(agit.getAccelZ());
                        xSeries.getData().add(new XYChart.Data<>(time, x));
                        ySeries.getData().add(new XYChart.Data<>(time, y));
                        zSeries.getData().add(new XYChart.Data<>(time, z));
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @FXML
    private void startMonitoring() {
        startButton.setDisable(true);
        stopButton.setDisable(false);
        scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleAtFixedRate(this::updateCharts, 0, 1, TimeUnit.SECONDS);
    }

    @FXML
    private void stopMonitoring() {
        if (scheduler != null) {
            scheduler.shutdown();
            startButton.setDisable(false);
            stopButton.setDisable(true);
        }
    }

    @FXML
    private void handleHistoryButton() {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/TempHistory.fxml"));
            Parent root = loader.load();
            Scene scene = historyButton.getScene();
            scene.setRoot(root);
        } catch (IOException e) {
            showError("Erreur de navigation", "Impossible d'accéder à l'historique: " + e.getMessage());
        }
    }

    @FXML
    private void handleAgitationHistoryButton(ActionEvent event) {
        try {
            FXMLLoader loader = new FXMLLoader(getClass().getResource("/AgitationHistory.fxml"));
            Parent root = loader.load();
            Scene scene = agitationHistoryButton.getScene();
            scene.setRoot(root);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void sendAlertEmail(double temperature, double agitation) {
        if (lastEmailSentTime != null && 
            LocalDateTime.now().minusMinutes(EMAIL_COOLDOWN_MINUTES).isBefore(lastEmailSentTime)) {
            return; // Ne pas envoyer d'email si le délai minimum n'est pas écoulé
        }

        Properties props = new Properties();
        props.put("mail.smtp.host", MAILTRAP_HOST);
        props.put("mail.smtp.port", MAILTRAP_PORT);
        props.put("mail.smtp.auth", "true");
        props.put("mail.smtp.starttls.enable", "true");

        Session session = Session.getInstance(props, new Authenticator() {
            @Override
            protected PasswordAuthentication getPasswordAuthentication() {
                return new PasswordAuthentication(MAILTRAP_USERNAME, MAILTRAP_PASSWORD);
            }
        });

        try {
            Message message = new MimeMessage(session);
            message.setFrom(new InternetAddress(MAILTRAP_USERNAME));
            message.setRecipients(Message.RecipientType.TO, InternetAddress.parse(RECIPIENT_EMAIL));
            message.setSubject("Alerte : Température et Agitation élevées");
            
            String emailContent = String.format(
                "Alerte de Fermista!\n\n" +
                "La température actuelle (%.1f°C) et l'agitation (%.1f) dépassent les seuils définis.\n" +
                "Heure de l'alerte : %s\n\n" +
                "Veuillez vérifier l'état de votre système.",
                temperature,
                agitation,
                LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm:ss"))
            );
            
            message.setText(emailContent);

            Transport.send(message);
            lastEmailSentTime = LocalDateTime.now();
            
            Platform.runLater(() -> {
                Alert alert = new Alert(Alert.AlertType.INFORMATION);
                alert.setTitle("Email envoyé");
                alert.setHeaderText(null);
                alert.setContentText("Un email d'alerte a été envoyé à " + RECIPIENT_EMAIL);
                alert.showAndWait();
            });
        } catch (MessagingException e) {
            e.printStackTrace();
            Platform.runLater(() -> {
                Alert alert = new Alert(Alert.AlertType.ERROR);
                alert.setTitle("Erreur d'envoi d'email");
                alert.setHeaderText(null);
                alert.setContentText("Impossible d'envoyer l'email d'alerte : " + e.getMessage());
                alert.showAndWait();
            });
        }
    }

    private void updateCharts() {
        try {
            // Variable pour stocker la température
            double currentTemp = 0;
            
            arg_temp lastTemp = tempService.getLastTemperature();
            if (lastTemp != null && (lastUpdateTime == null || lastTemp.getTimeReceive().isAfter(lastUpdateTime))) {
                Platform.runLater(() -> {
                    try {
                        String time = lastTemp.getTimeReceive().format(timeFormatter);
                        double tempValue = Double.parseDouble(lastTemp.getTemperature());
                        tempSeries.getData().add(new XYChart.Data<>(time, tempValue));
                        while (tempSeries.getData().size() > MAX_DATA_POINTS) {
                            tempSeries.getData().remove(0);
                        }
                        tempGauge.setValue(tempValue);
                        updateDisplayWithLastValue(lastTemp);
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    }
                });
                lastUpdateTime = lastTemp.getTimeReceive();
                currentTemp = Double.parseDouble(lastTemp.getTemperature());
            }

            List<arg_agit> agitData = agitService.showAll();
            if (!agitData.isEmpty()) {
                arg_agit lastAgit = agitData.get(agitData.size() - 1);
                final double finalTemp = currentTemp; // Pour utilisation dans le runLater
                Platform.runLater(() -> {
                    try {
                        String time = lastAgit.getTimeReceive().format(timeFormatter);
                        double x = Double.parseDouble(lastAgit.getAccelX());
                        double y = Double.parseDouble(lastAgit.getAccelY());
                        double z = Double.parseDouble(lastAgit.getAccelZ());
                        xSeries.getData().add(new XYChart.Data<>(time, x));
                        ySeries.getData().add(new XYChart.Data<>(time, y));
                        zSeries.getData().add(new XYChart.Data<>(time, z));
                        while (xSeries.getData().size() > MAX_DATA_POINTS) {
                            xSeries.getData().remove(0);
                            ySeries.getData().remove(0);
                            zSeries.getData().remove(0);
                        }
                        agitationTime.setText("Dernière Valeur");
                        agitationX.setText(String.format("x : %.2f", x));
                        agitationY.setText(String.format("y : %.2f", y));
                        agitationZ.setText(String.format("z : %.2f", z));
                        
                        double magnitude = Math.sqrt(x*x + y*y + z*z);
                        agitationGauge.setValue(magnitude);

                        // Vérifier les seuils et envoyer l'email si nécessaire
                        if (finalTemp > 20 && magnitude > 3) {
                            sendAlertEmail(finalTemp, magnitude);
                        }
                    } catch (NumberFormatException e) {
                        e.printStackTrace();
                    }
                });
            }
        } catch (Exception e) {
            e.printStackTrace();
            Platform.runLater(this::stopMonitoring);
        }
    }

    private void showError(String title, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle(title);
        alert.setHeaderText(null);
        alert.setContentText(content);
        alert.showAndWait();
    }

    public void onClose() {
        stopMonitoring();
    }
}
