package controller;

import java.net.URL;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ToggleButton;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import model.App;
import model.JsonEditor;

public class configController implements Initializable{

    @FXML
    private ToggleButton btn130;

    @FXML
    private ToggleButton btn130Xd;

    @FXML
    private ToggleButton btn260;

    @FXML
    private ToggleButton btn260Xd;

    @FXML
    private ToggleButton btn400Xd;
    
    @FXML
    private ImageView icon130;

    @FXML
    private ImageView icon130xd;

    @FXML
    private ImageView icon260;

    @FXML
    private ImageView icon260xd;

    @FXML
    private ImageView icon400xd;

    @FXML
    private Button btnBack;

    @FXML
    private Button btnSettings;

    @FXML
    private StackPane pnConfig;

    private Image imageActivate;
    private Image imageDesactivate;
    private Object machine;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        imageDesactivate = new Image("/view/Resources/btnDesactivate.png");
        imageActivate = new Image("/view/Resources/btnActivate.png");

        machine = JsonEditor.readJSON().get("machine");
        System.out.println("IFS");
        if(machine != null){
            switch (machine.toString()) {
                case ">000HYPERFORMANCE130AUTO30<":
                    icon130.setImage(imageActivate);
                    btn130.setSelected(true);
                    break;
    
                case ">000HYPERFORMANCE130XDAUTO30<":
                    icon130xd.setImage(imageActivate);
                    btn130Xd.setSelected(true);
                    break;
    
                case ">000HYPERFORMANCE260AUTO30<":
                    icon260.setImage(imageActivate);
                    btn260.setSelected(true);
                    break;
    
                case ">000HYPERFORMANCE260XDAUTO30<":
                    icon260xd.setImage(imageActivate);
                    btn260Xd.setSelected(true);
                    break;
    
                case ">000HYPERFORMANCE400XDAUTO30<":
                    icon400xd.setImage(imageActivate);
                    btn400Xd.setSelected(true);
                    break;
            
                default:
                    break;
            }
        }

    }

    @FXML
    void btn130Action(ActionEvent event) {
        if(btn130.isSelected()){
            icon130.setImage(imageActivate);
            icon130xd.setImage(imageDesactivate);
            btn130Xd.setSelected(false);
            icon260.setImage(imageDesactivate);
            btn260.setSelected(false);
            icon260xd.setImage(imageDesactivate);
            btn260Xd.setSelected(false);
            icon400xd.setImage(imageDesactivate);
            btn400Xd.setSelected(false);

            JsonEditor.updateJson("machine", ">000HYPERFORMANCE130AUTO30<");


        }else{
            icon130.setImage(imageDesactivate);
            JsonEditor.updateJson("machine", null);
        }

    }

    @FXML
    void btn130XdAction(ActionEvent event) {
        if(btn130Xd.isSelected()){
            icon130.setImage(imageDesactivate);
            btn130.setSelected(false);
            icon130xd.setImage(imageActivate);
            icon260.setImage(imageDesactivate);
            btn260.setSelected(false);
            icon260xd.setImage(imageDesactivate);
            btn260Xd.setSelected(false);
            icon400xd.setImage(imageDesactivate);
            btn400Xd.setSelected(false);
            JsonEditor.updateJson("machine", ">000HYPERFORMANCE130XDAUTO30<");
        }else{
            icon130xd.setImage(imageDesactivate);
            JsonEditor.updateJson("machine", null);
        }

    }

    @FXML
    void btn260Action(ActionEvent event) {
        if(btn260.isSelected()){
            icon130.setImage(imageDesactivate);
            btn130.setSelected(false);
            icon130xd.setImage(imageDesactivate);
            btn130Xd.setSelected(false);
            icon260.setImage(imageActivate);
            icon260xd.setImage(imageDesactivate);
            btn260Xd.setSelected(false);
            icon400xd.setImage(imageDesactivate);
            btn400Xd.setSelected(false);
            JsonEditor.updateJson("machine", ">000HYPERFORMANCE260AUTO30<");
        }else{
            icon260.setImage(imageDesactivate);
            JsonEditor.updateJson("machine", null);
        }
    }

    @FXML
    void btn260XdAction(ActionEvent event) {
        if(btn260Xd.isSelected()){
            icon130.setImage(imageDesactivate);
            btn130.setSelected(false);
            icon130xd.setImage(imageDesactivate);
            btn260.setSelected(false);
            icon260.setImage(imageDesactivate);
            btn260.setSelected(false);
            icon260xd.setImage(imageActivate);
            icon400xd.setImage(imageDesactivate);
            btn400Xd.setSelected(false);
            JsonEditor.updateJson("machine", ">000HYPERFORMANCE260XDAUTO30<");
        }else{
            icon260xd.setImage(imageDesactivate);

        }
    }

    @FXML
    void btn400XdAction(ActionEvent event) {
        if(btn400Xd.isSelected()){
            icon130.setImage(imageDesactivate);
            btn130.setSelected(false);
            icon130xd.setImage(imageDesactivate);
            btn260.setSelected(false);
            icon260.setImage(imageDesactivate);
            btn260.setSelected(false);
            icon260xd.setImage(imageDesactivate);
            btn260Xd.setSelected(false);
            icon400xd.setImage(imageActivate);
            JsonEditor.updateJson("machine", ">000HYPERFORMANCE400XDAUTO30<");
        }else{
            icon400xd.setImage(imageDesactivate);
            JsonEditor.updateJson("machine", null);
        }
    }

    @FXML
    void btnBackAction(ActionEvent event) {
        App.changeScene(getClass().getResource("/view/valuesLayout.fxml"), (Stage) pnConfig.getScene().getWindow());

    }

    @FXML
    void btnSettingsAction(ActionEvent event) {

    }

}
