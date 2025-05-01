package tn.fermista.tests;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

public class MainApp extends Application {
    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader loader = new FXMLLoader(getClass().getResource("/PaiementView.fxml"));
        Scene scene = new Scene(loader.load());
        primaryStage.setScene(scene);
        primaryStage.setTitle("Fermista");
        
        // Configurer la fenêtre en mode plein écran
        primaryStage.setMaximized(true);
        
        primaryStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }
}
