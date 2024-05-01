package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Button;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import model.App;

public class valuesController {

    @FXML
    private StackPane pnValues;

    @FXML
    private Button btnBack;

    @FXML
    void btnBackAction(ActionEvent event) {
        App.changeScene(getClass().getResource("/view/homeLayout.fxml"), (Stage) pnValues.getScene().getWindow());
    
    }

    @FXML
    void btnBombAction(ActionEvent event) {

    }

    @FXML
    void btnConfigAction(ActionEvent event) {

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

    @FXML
    void btnSettingsAction(ActionEvent event) {

    }

}
