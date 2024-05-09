package controller;

import java.util.Base64;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.TextField;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import model.CryptoConverter;
import model.JsonEditor;

public class popupAcessKeyController {

    @FXML
    private StackPane pnAccessPopup;

    @FXML
    private TextField txtkey;

    @FXML
    private Label lblIncorrectKey;

    private String accesskey;

    private Stage popupStage;

    @FXML
    void btnApplyAction(ActionEvent event) {
        if (txtkey.getText() != null) {
            try {
                accesskey = CryptoConverter.decrypt(Base64.getDecoder().decode(txtkey.getText()));

                if (accesskey.contains("HYPERFORMANCE")) {
                    if (accesskey.contains("130AUTO30")) {
                        JsonEditor.updateJson("access-key", txtkey.getText());
                        JsonEditor.updateJson("machine", ">000HYPERFORMANCE130AUTO30<");
                    } else if (accesskey.contains("130XDAUTO30")) {
                        JsonEditor.updateJson("access-key", txtkey.getText());
                        JsonEditor.updateJson("machine", ">000HYPERFORMANCE130XDAUTO30<");
                    } else if (accesskey.contains("260AUTO30")) {
                        JsonEditor.updateJson("access-key", txtkey.getText());
                        JsonEditor.updateJson("machine", ">000HYPERFORMANCE260AUTO30<");
                    } else if (accesskey.contains("260XDAUTO30")) {
                        JsonEditor.updateJson("access-key", txtkey.getText());
                        JsonEditor.updateJson("machine", ">000HYPERFORMANCE260XDAUTO30<");
                    } else if (accesskey.contains("400XDAUTO30")) {
                        JsonEditor.updateJson("access-key", txtkey.getText());
                        JsonEditor.updateJson("machine", ">000HYPERFORMANCE400XDAUTO30<");
                    }

                    popupStage = (Stage) pnAccessPopup.getScene().getWindow();
                    popupStage.close();
                } else {
                    lblIncorrectKey.setVisible(true);
                }
            } catch (Exception e) {
                lblIncorrectKey.setVisible(true);
            }
        }
    }

}
