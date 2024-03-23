import javafx.event.EventHandler;
import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.image.Image;
import javafx.stage.Stage;
import javafx.stage.WindowEvent;

public class App extends Application{
    public static void main(String[] args) throws Exception {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) throws Exception {
        //HyperConnect\src\view\homeLayout.fxml
        FXMLLoader fxmlLayout = new FXMLLoader(getClass().getResource("/view/homeLayout.fxml"));
        Parent root = fxmlLayout.load();
        Scene scene = new Scene(root);
        Image icon = new Image(getClass().getResourceAsStream("/view/Resources/Icon.png"));

        primaryStage.getIcons().add(icon);
        primaryStage.setTitle("HYPERCONNECT");
        primaryStage.setOnCloseRequest(new EventHandler<WindowEvent>() {

            @Override
            public void handle(WindowEvent event) {
                // TODO Auto-generated method stub
                System.exit(0);
            }
        });

        primaryStage.setScene(scene);
        primaryStage.show();

    }
}
