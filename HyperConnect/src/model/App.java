package model;
import javafx.event.EventHandler;

import java.io.IOException;
import java.net.URL;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class App extends Application {

    public static void main(String[] args) throws Exception {
        launch(args);
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
        primaryStage.show();

    }

    public static void changeScene(URL path, Stage stage){
        try {
            Parent root = FXMLLoader.load(path);
            Scene scene = new Scene(root, 1024, 600); 
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
