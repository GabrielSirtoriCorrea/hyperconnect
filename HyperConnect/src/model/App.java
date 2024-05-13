package model;

import javafx.event.EventHandler;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Base64;

import org.json.simple.JSONObject;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class App extends Application {
    private FXMLLoader popupLoader;
    private Image icon;
    private Stage popUpAccessKeyStage, popUpExpirationTimeStage;
    private Scene popUpAccessKeyScene, popUpExpirationTimeScene;
    private Object expiration;
    private String now, dateDecrypted;

    public static void main(String[] args) throws Exception {
        launch(args);
        // chave LXlT6PeP6yU3rEnPCgNWAXviZ/2S8hqyFOLzsuZB8jEN7nt0akxX6B5Shz+GN7a4
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        FXMLLoader fxmlLayout = new FXMLLoader(getClass().getResource("/view/homeLayout.fxml"));
        Parent root = fxmlLayout.load();
        Scene scene = new Scene(root);
        Image icon = new Image(getClass().getResourceAsStream("/view/Resources/Icon.png"));

        primaryStage.getIcons().add(icon);
        primaryStage.setTitle("HYPERCONNECT");
        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {

            @Override
            public void handle(WindowEvent event) {
                System.exit(0);
            }
        });

        primaryStage.setScene(scene);
        primaryStage.setFullScreenExitHint("");
        primaryStage.setFullScreen(true);
        primaryStage.show();

        System.out.println(Base64.getEncoder()
                .encodeToString(CryptoConverter.encrypt(">000HYPERFORMANCE400XDAUTO30<:01-01-3000")));

        expiration = JsonEditor.readJSON().get("expiration");

        if (expiration == null) {

            try {
                popUpAccessKeyStage = new Stage();
                popupLoader = new FXMLLoader(getClass().getResource("/view/popupAccessKeylayout.fxml"));
                icon = new Image(getClass().getResourceAsStream("/view/Resources/Icon.png"));
                popUpAccessKeyStage.getIcons().add(icon);
                popUpAccessKeyStage.initModality(Modality.APPLICATION_MODAL);
                popUpAccessKeyStage.setTitle("Licença");
                popUpAccessKeyStage.initOwner(scene.getWindow());
                popUpAccessKeyScene = new Scene(popupLoader.load());
                popUpAccessKeyStage.setScene(popUpAccessKeyScene);

                popUpAccessKeyStage.showAndWait();
            } catch (Exception e) {
                e.printStackTrace();
            }
        } else {
            dateDecrypted = CryptoConverter.decrypt(Base64.getDecoder().decode(expiration.toString()));
            if (LocalDate.parse(dateDecrypted, DateTimeFormatter.ofPattern("dd-MM-yyyy"))
                    .isBefore(LocalDate.now())) {
                try {
                    popUpExpirationTimeStage = new Stage();
                    popupLoader = new FXMLLoader(getClass().getResource("/view/expirationTimelayout.fxml"));
                    icon = new Image(getClass().getResourceAsStream("/view/Resources/Icon.png"));
                    popUpExpirationTimeStage.getIcons().add(icon);
                    popUpExpirationTimeStage.initModality(Modality.APPLICATION_MODAL);
                    popUpExpirationTimeStage.setTitle("Licença expirada");
                    popUpExpirationTimeStage.initOwner(scene.getWindow());
                    popUpExpirationTimeScene = new Scene(popupLoader.load());
                    popUpExpirationTimeStage.setScene(popUpExpirationTimeScene);

                    popUpExpirationTimeStage.showAndWait();
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }

    public static void changeScene(URL path, Stage stage) {
        try {
            Parent root = FXMLLoader.load(path);
            Scene scene = new Scene(root, 1024, 600);
            stage.setScene(scene);
            stage.setFullScreenExitHint("");
            stage.setFullScreen(true);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
