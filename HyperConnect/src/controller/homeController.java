package controller;

import java.net.URL;
import java.util.ArrayList;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.image.ImageView;
import javafx.scene.layout.StackPane;
import model.DatabaseController;
import model.SerialPortController;

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
    private ImageView imgComunicationOff;
    @FXML
    private ImageView imgComunicationOn;
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
    private Button btnGas;
    @FXML
    private StackPane pnHome;

    private DatabaseController databaseController;
    private Object thickness, current, material;
    private Object[] selectResult;
    private ArrayList<Object> cmbThicknessContent, cmbCurrentContent, cmbMaterialContent;
    private Runnable serialPortListener;
    private SerialPortController serialPortListenerController;
    private String serialResponse;
    private Thread serialPortListenerThread;
    private Object threadNotifier = new Object();
    private boolean runThread = true;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        databaseController = new DatabaseController("./HyperConnect/teste.db");
        databaseController.CreateTables();

        cmbMaterial.setOnShowing(event -> btnMaterialsListAction());
        cmbThickness.setOnShowing(event -> btnThicknessListAction());
        cmbCurrent.setOnShowing(event -> btnCurrentListAction());

        // serialPortListenerController = new SerialPortController();
        // serialPortListenerController.openSerialPort("ttyS0", 9600);

        serialPortListener = new Runnable() {
            @Override
            public void run() {
                synchronized (threadNotifier) {
                    while (true) {
                        try {
                            if (runThread) {
                                // serialPortListenerController.sendData("079A0");
                                // serialResponse = serialPortListenerController.readData();

                                // TRATAR STRING PARA COLOCAR AS INFORMAÇÕES NA TELA

                                serialResponse = ">079PC0044 PP0042 SC0034 SP0035 CS0130 ST0003 ER0093 CG0000 CG0000 MV0000 MV0000DE<";
                                System.out.println(serialResponse);
                                Thread.sleep(1000);
                            } else {
                                threadNotifier.wait();
                            }
                        } catch (InterruptedException e) {
                            System.out.println("TESTANDO");
                        }

                    }
                }
            }
        };

        serialPortListenerThread = new Thread(serialPortListener);
        serialPortListenerThread.start();

    }

    @FXML
    void btnSendAction(ActionEvent event) {
        runThread = false;
        synchronized (threadNotifier) {
            System.out.println("TESTANDO");
            runThread = true;
            threadNotifier.notifyAll();
        }
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
    private void btnGasAction() {
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
            btnGas.setText(selectResult[0].toString());
        }
    }
}
