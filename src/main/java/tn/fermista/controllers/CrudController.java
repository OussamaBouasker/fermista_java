package tn.fermista.controllers;

import javafx.fxml.FXML;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;

public class CrudController {

    @FXML
    private TableView reclamationTable;

    @FXML
    private TextField txt_search;

    // Initialisation du contrôleur
    @FXML
    public void initialize() {
        // Initialiser les composants ici
        System.out.println("CrudController initialisé");
    }
} 