package controller;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
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
    private String[] data;
    private Stage popupStage;


    @FXML
    void btnApplyAction(ActionEvent event) {
        if (txtkey.getText() != null) {
            try {
                accesskey = CryptoConverter.decrypt(Base64.getDecoder().decode(txtkey.getText()));

                data = accesskey.split(":");

                if (accesskey.contains("HYPERFORMANCE")) {
                    JsonEditor.updateJson("machine", data[0].toString());
                    System.out.println(data[1]);
                    JsonEditor.updateJson("expiration", Base64.getEncoder().encodeToString(CryptoConverter.encrypt(data[1].toString())));

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
