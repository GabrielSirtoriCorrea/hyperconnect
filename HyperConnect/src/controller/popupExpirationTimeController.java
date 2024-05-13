package controller;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import model.JsonEditor;

public class popupExpirationTimeController {

    @FXML
    private StackPane pnExpirationTimePopup;

    private Stage popupStage;

    @FXML
    void btnOk(ActionEvent event) {
        JsonEditor.updateJson("expiration", null);
        System.exit(0);
    }

}
