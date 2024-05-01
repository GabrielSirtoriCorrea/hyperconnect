package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;

public class popupPasswordController {

    @FXML
    private StackPane pnPasswordPopup;

    private Stage popupStage;

    @FXML
    void btnCloseAction(ActionEvent event) {
        popupStage = (Stage) pnPasswordPopup.getScene().getWindow();
        popupStage.close();
    }

}
