package tn.fermista.controllers;

import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import tn.fermista.models.arg_temp;
import tn.fermista.services.ServiceArgTemp;

import java.net.URL;
import java.sql.SQLException;
import java.util.List;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class ListeArgTempController implements Initializable {

    @FXML
    private TableView<arg_temp> tableArgTemp;

    @FXML
    private TableColumn<arg_temp, Long> colId;

    @FXML
    private TableColumn<arg_temp, String> colTemp;

    @FXML
    private TableColumn<arg_temp, String> colTime;

    private final ServiceArgTemp serviceArgTemp = new ServiceArgTemp();

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        colId.setCellValueFactory(new PropertyValueFactory<>("id"));
        colTemp.setCellValueFactory(new PropertyValueFactory<>("temperature"));
        colTime.setCellValueFactory(new PropertyValueFactory<>("timeReceive"));

        try {
            List<arg_temp> list = serviceArgTemp.showAll();
            ObservableList<arg_temp> observableList = FXCollections.observableArrayList(list);
            tableArgTemp.setItems(observableList);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
