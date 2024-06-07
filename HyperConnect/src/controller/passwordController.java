package controller;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.PasswordField;
import javafx.scene.image.Image;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.StackPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.App;

public class passwordController implements Initializable {

    @FXML
    private Button btnBack;

    @FXML
    private StackPane pnPassword;

    @FXML
    private PasswordField txtPassword;

    private FXMLLoader popupLoader;
    private Image icon;
    private Stage popUp;
    private Scene popUpScene;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        EventHandler<KeyEvent> keyEventHandler = new EventHandler<KeyEvent>() {
            @Override
            public void handle(KeyEvent event) {
                if (event.getCode() == KeyCode.ENTER) {
                    if (txtPassword.getText().equals("METALWIZESUPPORT")) {
                        App.changeScene(getClass().getResource("/view/configLayout.fxml"),
                                (Stage) pnPassword.getScene().getWindow());
                    } else {
                        try {
                            popupLoader = new FXMLLoader(getClass().getResource("/view/popupPasswordlayout.fxml"));
    
                            icon = new Image(getClass().getResourceAsStream("/view/Resources/Icon.png"));
                            // Configura o Stage do pop-up
                            popUp = new Stage();
                            popUp.getIcons().add(icon);
                            popUp.initModality(Modality.APPLICATION_MODAL);
                            popUp.setTitle("Erro de autenticação");
                            popUp.initOwner((Stage) pnPassword.getScene().getWindow());

                            // Define a cena do pop-up
                            popUpScene = new Scene(popupLoader.load());
                            popUp.setScene(popUpScene);

                            // Exibe o pop-up
                            popUp.showAndWait();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        };

        pnPassword.setOnKeyPressed(keyEventHandler);
    }

    @FXML
    void btnBackAction(ActionEvent event) {
        App.changeScene(getClass().getResource("/view/valuesLayout.fxml"), (Stage) pnPassword.getScene().getWindow());
    }

}
