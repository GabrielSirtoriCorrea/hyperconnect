package controller;

import java.io.IOException;
import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;

import javafx.application.Platform;
import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.fxml.LoadException;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import javafx.stage.Modality;
import javafx.stage.Stage;
import model.DatabaseController;
import model.JsonEditor;
import model.SerialPortController;
import model.App;

public class homeController implements Initializable {

    @FXML
    private Button bntSend;
    @FXML
    private Button btnFlowTest;
    @FXML
    private Button btnPreFlowtTest;
    @FXML
    private Button btnReset;
    @FXML
    private CheckBox chkCut;
    @FXML
    private CheckBox chkMark;
    @FXML
    private ComboBox<Object> cmbCurrent;
    @FXML
    private ComboBox<Object> cmbMaterial;
    @FXML
    private ComboBox<Object> cmbThickness;
    @FXML
    private ImageView imgComunication;
    @FXML
    private Label lblCurrent;
    @FXML
    private Label lblDate;
    @FXML
    private Label lblError;
    @FXML
    private Label lblFloProt;
    @FXML
    private Label lblFlowCut;
    @FXML
    private Label lblPreFlowCut;
    @FXML
    private Label lblPreFlowProt;
    @FXML
    private Label lblStatus;
    @FXML
    private Label lblGas;
    @FXML
    private StackPane pnHome;

    private DatabaseController databaseController;
    private Object thickness, current, material;
    private Object[] selectResult;
    private ArrayList<Object> cmbThicknessContent, cmbCurrentContent, cmbMaterialContent;
    private TimerTask serialPortListener, preFlowTask, flowTask;
    private Runnable updateValues, updateFlowLabel, updatePreFlowLabel;
    private SerialPortController serialPortController;
    private String serialResponse, serialNewResponse;
    private boolean serialStatus;
    private String[] serialValues;
    private String pfp, fp, pfc, fc, labelCurrent, error, state;
    private int errorIndex, stateIndex;
    private LocalDate date;
    private String formattedDate;
    private DateTimeFormatter dateFormatter;
    private Timer updateTimer, actionTimer;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        date = LocalDate.now();
        updateTimer = new Timer();

        // Formatar a data
        dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        formattedDate = date.format(dateFormatter);

        lblDate.setText(formattedDate);
        chkCut.setSelected(true);
        serialStatus = false;

        databaseController = new DatabaseController("./HyperConnect/teste.db");
        databaseController.CreateTables();
        
        //Object[][] inserObjects = {{"MATERIAL", "Aço Carbono"}};
        //databaseController.deleteData("COD_OPERACAO", "ID", "104");
        //Object[][] inserObjects = {{"MATERIAL", "Alumínio"}, {"ESPESSURA", 25}, {"CORRENTE", 400}, {"GAS_CORTAR", "N2/AR"}, {"GAS_MARCAR", "N2/N2"}, {"COD_CORTAR", ">05826035< >0966 52A< >07870 31 30 50 00 0092<"}, {"COD_MARCAR", ">05802231<  >0966 62B< >07810 10 10 10 00 0083<"}};
        //databaseController.updateData("COD_OPERACAO", inserObjects, "dasjdn", "dasdklsml");

        cmbMaterial.setOnShowing(event -> btnMaterialsListAction());
        cmbThickness.setOnShowing(event -> btnThicknessListAction());
        cmbCurrent.setOnShowing(event -> btnCurrentListAction());
        serialResponse = "";

        serialPortController = new SerialPortController();
        serialPortController.openSerialPort("ttyS0", 9600);

        updateValues = new Runnable() {
            @Override
            public void run() {
                updateGasValue();
                lblCurrent.setText(labelCurrent);
                lblStatus.setText(state);
                lblError.setText(error);
                lblFlowCut.setText(fc);
                lblPreFlowCut.setText(pfc);
                lblFloProt.setText(fp);
                lblPreFlowProt.setText(pfp);

            }

        };

        serialPortListener = new TimerTask() {
            @Override
            public void run() {
                try {
                    System.out.println(serialStatus);
                    if (serialStatus) {
                        imgComunication.setImage(new Image("/view/Resources/ONlabelHigh.png"));
                        serialPortController.sendData(">079A0<");
                        serialNewResponse = serialPortController.readData();
                        // serialNewResponse = ">079PC0044 PP0042 SC0034 SP0035 CS0130 ST0000 ER0009
                        // CG0000 CG0000 MV0000 MV0000DE<";
                        System.out.println("RESPOSTA>>" + serialResponse);
                        if (!serialNewResponse.equals("")) {
                            if (!serialNewResponse.equals(serialResponse)) {
                                serialResponse = serialNewResponse;
                                System.out.println(serialResponse);
                                processSerialValues(serialResponse);
                            }
                        } else {
                            serialStatus = false;
                        }

                        Platform.runLater(updateValues);

                    } else {
                        imgComunication.setImage(new Image("/view/Resources/OFFlabelHigh.png"));
                        serialPortController.sendData(">00090<");
                        serialNewResponse = serialPortController.readData();
                        serialResponse = serialNewResponse;
                        System.out.println("TENTANDO COMUNICACAO");
                        if (serialResponse.equals(JsonEditor.readJSON().get("machine").toString())) {
                            serialStatus = true;
                        } else {
                            serialStatus = false;
                        }
                    }

                } catch (Exception e) {
                    System.out.println("SEM COMUNICACAO");
                    serialStatus = false;

                }
            }
        };

        updateTimer.scheduleAtFixedRate(serialPortListener, 0, 100);

        ArrayList<Object> cmbMaterialContentStart = new ArrayList<>();
        cmbMaterialContentStart.add("Aço Carbono");
        cmbMaterial.setItems(FXCollections.observableArrayList(cmbMaterialContentStart));
        cmbMaterial.getSelectionModel().selectFirst();

        ArrayList<Object> cmbThicknesContentStart = new ArrayList<>();
        cmbThicknesContentStart.add(1);
        cmbThickness.setItems(FXCollections.observableArrayList(cmbThicknesContentStart));

        cmbThickness.getSelectionModel().selectFirst();

        ArrayList<Object> cmbCurrentContentStart = new ArrayList<>();
        cmbCurrentContentStart.add(30);
        cmbCurrent.setItems(FXCollections.observableArrayList(cmbCurrentContentStart));
        cmbCurrent.getSelectionModel().selectFirst();

    }

    @FXML
    void btnSendAction(ActionEvent event) {

        if (!cmbMaterial.getSelectionModel().isEmpty() && !cmbThickness.getSelectionModel().isEmpty()
                && !cmbCurrent.getSelectionModel().isEmpty() && (chkCut.isSelected() || chkMark.isSelected())) {

            if (chkCut.isSelected()) {
                selectResult = databaseController.getData("COD_OPERACAO", "COD_CORTAR", "MATERIAL",
                        "'" + material.toString() + "'", "CORRENTE", current.toString(), "ESPESSURA",
                        thickness.toString());
            } else if (chkMark.isSelected()) {
                selectResult = databaseController.getData("COD_OPERACAO", "COD_MARCAR", "MATERIAL",
                        "'" + material.toString() + "'", "CORRENTE", current.toString(), "ESPESSURA",
                        thickness.toString());
            }
            serialPortController.sendData(selectResult[0].toString());
            System.out.println("ENVIO:" + selectResult[0].toString());

        }
    }

    @FXML
    void btnFlowTestAction(ActionEvent event) {
        actionTimer = new Timer();

        if (btnFlowTest.getText().equals("TESTE FLUXO")) {
            serialPortController.sendData(">0669C<");
            btnFlowTest.setText("PARAR TESTE FLUXO");

            updateFlowLabel = new Runnable() {
                @Override
                public void run() {
                    btnFlowTest.setText("TESTE FLUXO");
                }
            };
            flowTask = new TimerTask() {
                @Override
                public void run() {
                    serialPortController.sendData(">0679D<");
                    System.out.println("5 SEGUNDOS");
                    Platform.runLater(updateFlowLabel);
                }
            };
            actionTimer.schedule(flowTask, 30000);

        } else if (btnFlowTest.getText().equals("PARAR TESTE FLUXO")) {
            serialPortController.sendData(">0679D<");
            System.out.println("executa");
            btnFlowTest.setText("TESTE FLUXO");
        }

    }

    @FXML
    void btnPreFlowTestAction(ActionEvent event) {
        actionTimer = new Timer();

        if (btnPreFlowtTest.getText().equals("TESTE PRE FLUXO")) {
            serialPortController.sendData(">0649A<");
            btnPreFlowtTest.setText("PARAR TESTE PRE FLUXO");

            updatePreFlowLabel = new Runnable() {
                @Override
                public void run() {
                    btnPreFlowtTest.setText("TESTE PRE FLUXO");
                }
            };
            preFlowTask = new TimerTask() {
                @Override
                public void run() {
                    serialPortController.sendData(">0659B<");
                    System.out.println("5 SEGUNDOS");
                    Platform.runLater(updatePreFlowLabel);
                }
            };
            actionTimer.schedule(preFlowTask, 30000);

        } else if (btnPreFlowtTest.getText().equals("PARAR TESTE PRE FLUXO")) {
            serialPortController.sendData(">0679D<");
            System.out.println("executa");
            btnPreFlowtTest.setText("TESTE PRE FLUXO");
        }
    }

    @FXML
    void btnResetAction(ActionEvent event) {
        //updateTimer.cancel();
        //updateTimer.purge();
        serialPortController.sendData(">0689E<");
        //updateTimer.scheduleAtFixedRate(serialPortListener, 0, 100);

    }

    private void btnMaterialsListAction() {
        cmbMaterial.getSelectionModel().clearSelection();
        thickness = cmbThickness.getSelectionModel().getSelectedItem();
        current = cmbCurrent.getSelectionModel().getSelectedItem();

        if (thickness != null && current != null) {
            selectResult = databaseController.getData("COD_OPERACAO", "MATERIAL", "ESPESSURA", thickness.toString(),
                    "CORRENTE", current.toString());
        } else {
            if (current != null) {
                selectResult = databaseController.getData("COD_OPERACAO", "MATERIAL", "CORRENTE", current.toString());
            } else {
                if (thickness != null) {
                    selectResult = databaseController.getData("COD_OPERACAO", "MATERIAL", "ESPESSURA",
                            thickness.toString());

                } else {
                    selectResult = databaseController.getData("COD_OPERACAO", "MATERIAL");

                }
            }
        }

        cmbMaterialContent = new ArrayList<>();

        for (Object object : selectResult) {
            if (!cmbMaterialContent.contains(object)) {
                cmbMaterialContent.add(object);
            }
        }

        cmbMaterial.setItems(FXCollections.observableArrayList(cmbMaterialContent));

    }

    private void btnThicknessListAction() {
        cmbThickness.getSelectionModel().clearSelection();
        material = cmbMaterial.getSelectionModel().getSelectedItem();
        current = cmbCurrent.getSelectionModel().getSelectedItem();

        if (material != null && current != null) {
            selectResult = databaseController.getData("COD_OPERACAO", "ESPESSURA", "MATERIAL",
                    "'" + material.toString() + "'",
                    "CORRENTE", current.toString());
        } else {
            if (current != null) {
                selectResult = databaseController.getData("COD_OPERACAO", "ESPESSURA", "CORRENTE", current.toString());
            } else {
                if (material != null) {
                    selectResult = databaseController.getData("COD_OPERACAO", "ESPESSURA", "MATERIAL",
                            "'" + material.toString() + "'");

                } else {
                    selectResult = databaseController.getData("COD_OPERACAO", "ESPESSURA");

                }
            }
        }

        cmbThicknessContent = new ArrayList<>();

        for (Object object : selectResult) {
            if (!cmbThicknessContent.contains(object)) {
                cmbThicknessContent.add(object);
            }
        }

        cmbThickness.setItems(FXCollections.observableArrayList(cmbThicknessContent));

    }

    private void btnCurrentListAction() {
        cmbCurrent.getSelectionModel().clearSelection();
        material = cmbMaterial.getSelectionModel().getSelectedItem();
        thickness = cmbThickness.getSelectionModel().getSelectedItem();

        if (material != null && thickness != null) {
            selectResult = databaseController.getData("COD_OPERACAO", "CORRENTE", "MATERIAL",
                    "'" + material.toString() + "'",
                    "ESPESSURA", thickness.toString());
        } else {
            if (thickness != null) {
                selectResult = databaseController.getData("COD_OPERACAO", "CORRENTE", "ESPESSURA",
                        thickness.toString());
            } else {
                if (material != null) {
                    selectResult = databaseController.getData("COD_OPERACAO", "CORRENTE", "MATERIAL",
                            "'" + material.toString() + "'");

                } else {
                    selectResult = databaseController.getData("COD_OPERACAO", "CORRENTE");

                }
            }
        }

        cmbCurrentContent = new ArrayList<>();

        for (Object object : selectResult) {
            if (!cmbCurrentContent.contains(object)) {
                cmbCurrentContent.add(object);
            }
        }

        cmbCurrent.setItems(FXCollections.observableArrayList(cmbCurrentContent));

    }

    @FXML
    void chkCutCheck(ActionEvent event) {
        chkMark.setSelected(false);

    }

    @FXML
    void chkMarkCheck(ActionEvent event) {
        chkCut.setSelected(false);

    }

    @FXML
    void btnSettingsAction(ActionEvent event) {
        updateTimer.cancel();
        updateTimer.purge();
        serialPortController.closeSerialPort();
        App.changeScene(getClass().getResource("/view/valuesLayout.fxml"), (Stage) pnHome.getScene().getWindow());
    }

    private void updateGasValue() {

        material = cmbMaterial.getSelectionModel().getSelectedItem();
        thickness = cmbThickness.getSelectionModel().getSelectedItem();
        current = cmbCurrent.getSelectionModel().getSelectedItem();

        if (material != null && thickness != null && current != null) {
            if (chkCut.isSelected()) {
                selectResult = databaseController.getData("COD_OPERACAO", "GAS_CORTAR", "MATERIAL",
                        "'" + material.toString() + "'", "CORRENTE", current.toString(), "ESPESSURA",
                        thickness.toString());
            } else if (chkMark.isSelected()) {
                selectResult = databaseController.getData("COD_OPERACAO", "GAS_MARCAR", "MATERIAL",
                        "'" + material.toString() + "'", "CORRENTE", current.toString(), "ESPESSURA",
                        thickness.toString());

            }
            lblGas.setText(selectResult[0].toString());
        }

    }

    private void processSerialValues(String serialResponse) {
        if (serialResponse.startsWith(">") && serialResponse.endsWith("<")) {
            serialValues = serialResponse.replace(">079", "").split(" ");
            fc = serialValues[0].replace("PC0", "") + " LB/POL2";
            pfc = serialValues[1].replace("PP0", "") + " LB/POL2";
            fp = serialValues[2].replace("SC0", "") + " LB/POL2";
            pfp = serialValues[3].replace("SP0", "") + " LB/POL2";
            labelCurrent = serialValues[4].replace("CS0", "") + "A";

            stateIndex = Integer.parseInt(serialValues[5].replace("ST0", ""));
            errorIndex = Integer.parseInt(serialValues[6].replace("ER0", ""));

            state = "0" + Integer.toString(stateIndex) + " - "
                    + databaseController.getData("ESTADOS", "DESCRICAO", "ID", Integer.toString(stateIndex))[0]
                            .toString();
            error = "0" + Integer.toString(errorIndex) + " - "
                    + databaseController.getData("ERROS", "DESCRICAO", "ID", Integer.toString(errorIndex))[0]
                            .toString();

            System.out.println(serialResponse);

        }
    }

}
