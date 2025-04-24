package tn.fermista.controllers;

import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.animation.TranslateTransition;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.effect.MotionBlur;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.util.Duration;

import java.util.ArrayList;
import java.util.List;

public class HomePage {

    @FXML
    private Button next;
    @FXML
    private Button prev;
    @FXML
    private StackPane stackPane;

    private List<Image> imageList = new ArrayList<>();
    private int currentIndex = 0;

    // Timeline for automatic image switching
    private Timeline carouselTimeline;

    @FXML
    private void initialize() {
        // Load predefined images
        loadPredefinedImages();

        // Create a Timeline to change images every 3 seconds
        carouselTimeline = new Timeline(
                new KeyFrame(Duration.seconds(3), event -> next())
        );
        carouselTimeline.setCycleCount(Timeline.INDEFINITE); // Loop indefinitely
        carouselTimeline.play(); // Start the automatic image change
    }

    private void loadPredefinedImages() {
        // Predefine a list of image URLs (You can add your image URLs here)
        imageList.add(new Image("/images/carousel/home6.png"));// Adjust the path as needed
        imageList.add(new Image("/images/carousel/home2.png")); // Adjust the path as needed

        imageList.add(new Image("/images/carousel/veterinaire2.png")); // Adjust the path as needed



        // If there are predefined images, display the first one
        if (!imageList.isEmpty()) {
            currentIndex = 0;
            ImageView imageView = new ImageView(imageList.get(currentIndex));
            imageView.setPreserveRatio(true);
            imageView.setFitHeight(500);
            imageView.setFitWidth(500);
            stackPane.getChildren().add(imageView);
        }
    }

    @FXML
    private void prev() {
        if (imageList == null || imageList.isEmpty()) {
            return;
        }
        currentIndex = (currentIndex - 1 + imageList.size()) % imageList.size();
        slideImage(-600);
    }

    @FXML
    private void next() {
        if (imageList == null || imageList.isEmpty()) {
            return;
        }
        currentIndex = (currentIndex + 1) % imageList.size();
        slideImage(600);
    }

    private void slideImage(double distance) {
        if (stackPane.getChildren().isEmpty()) {
            return;  // No images to slide
        }

        // Remove the current image
        stackPane.getChildren().clear();

        // Create and add the new image
        ImageView newImage = new ImageView(imageList.get(currentIndex));
        newImage.setFitHeight(500);
        newImage.setFitWidth(500);
        newImage.setPreserveRatio(true);

        stackPane.getChildren().add(newImage);
    }

}
