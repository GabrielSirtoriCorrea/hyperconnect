package controller;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import model.App;

public class valuesController implements Initializable{

    @FXML
    private StackPane pnValues;

    @FXML
    private Button btnBack;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

    }


    @FXML
    void btnBackAction(ActionEvent event) {
        App.changeScene(getClass().getResource("/view/homeLayout.fxml"), (Stage) pnValues.getScene().getWindow());
    
    }

    @FXML
    void btnBombAction(ActionEvent event) {

    }

    @FXML
    void btnConfigAction(ActionEvent event) {
        App.changeScene(getClass().getResource("/view/passwordLayout.fxml"), (Stage) pnValues.getScene().getWindow());
   
    }

    @FXML
    void btnFlowTestAction(ActionEvent event) {

    }

    @FXML
    void btnPreFlowTestAction(ActionEvent event) {

    }

    @FXML
    void btnResetAction(ActionEvent event) {

    }

}
