package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import model.App;

public class valuesController {

    @FXML
    private StackPane pnValues;

    @FXML
    void btnHomeAction(ActionEvent event) {
        App.changeScene(getClass().getResource("/view/homeLayout.fxml"), (Stage) pnValues.getScene().getWindow());
    
    }

}
