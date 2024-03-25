package controller;

import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.ResourceBundle;

import javafx.application.Platform;
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
    private Label lblGas;
    @FXML
    private StackPane pnHome;

    private DatabaseController databaseController;
    private Object thickness, current, material;
    private Object[] selectResult;
    private ArrayList<Object> cmbThicknessContent, cmbCurrentContent, cmbMaterialContent;
    private Runnable serialPortListener, updateValues;
    private SerialPortController serialPortController;
    private String serialResponse;
    private Thread serialPortListenerThread;
    private Object threadNotifier = new Object();
    private boolean runThread = true;
    private String[] serialValues;
    private String pfp, fp, pfc, fc, labelCurrent, error, state, flow, preflow;
    private int errorIndex, stateIndex, threadFlowCounter, threadPreFlowCounter;
    private LocalDate date;
    private String formattedDate;
    private DateTimeFormatter dateFormatter;

    @Override
    public void initialize(URL location, ResourceBundle resources) {

        date = LocalDate.now();
        
        // Formatar a data
        dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        formattedDate = date.format(dateFormatter);

        lblDate.setText(formattedDate);

        databaseController = new DatabaseController("./HyperConnect/teste.db");
        databaseController.CreateTables();

        cmbMaterial.setOnShowing(event -> btnMaterialsListAction());
        cmbThickness.setOnShowing(event -> btnThicknessListAction());
        cmbCurrent.setOnShowing(event -> btnCurrentListAction());
        threadFlowCounter = threadPreFlowCounter = 0;
        flow = "TESTE FLOW";
        preflow = "TESTE PRE FLOW";

        // serialPortController = new SerialPortController();
        // serialPortController.openSerialPort("ttyS0", 9600);

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

        serialPortListener = new Runnable() {
            @Override
            public void run() {
                synchronized (threadNotifier) {
                    while (true) {
                        try {
                            if (runThread) {
                                threadFlowCounter++;
                                threadPreFlowCounter++;
                                // serialPortController.sendData(">079A0<");
                                // serialResponse = serialPortController.readData();
                                serialResponse = ">079PC0044 PP0042 SC0034 SP0035 CS0130 ST0000 ER0009 CG0000 CG0000 MV0000 MV0000DE<";
                                processSerialValues(serialResponse);

                                Thread.sleep(100);

                                if (btnFlowTest.getText().equals("PARAR TESTE FLOW")) {
                                    if (threadFlowCounter == 300) {
                                        System.out.println("PARAR TESTE FLOW");
                                        flow = "TESTE FLOW";
                                    }
                                } else {
                                    threadFlowCounter = 0;
                                }

                                if (btnPreFlowtTest.getText().equals("PARAR TESTE PRE FLOW")) {
                                    if (threadPreFlowCounter == 300) {
                                        System.out.println("PARAR TESTE PRE FLOW");
                                        preflow = "TESTE PRE FLOW";
                                    }
                                } else {
                                    threadPreFlowCounter = 0;
                                }

                                Platform.runLater(updateValues);
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
        if (!cmbMaterial.getSelectionModel().isEmpty() && !cmbThickness.getSelectionModel().isEmpty()
                && !cmbCurrent.getSelectionModel().isEmpty() && (chkCut.isSelected() || chkMark.isSelected())) {
            
                    runThread = false;
            synchronized (threadNotifier) {
                if (chkCut.isSelected()) {
                    selectResult = databaseController.getData("COD_OPERACAO", "COD_CORTAR", "MATERIAL",
                            "'" + material.toString() + "'", "CORRENTE", current.toString(), "ESPESSURA",
                            thickness.toString());
                } else if (chkMark.isSelected()) {
                    selectResult = databaseController.getData("COD_OPERACAO", "COD_MARCAR", "MATERIAL",
                            "'" + material.toString() + "'", "CORRENTE", current.toString(), "ESPESSURA",
                            thickness.toString());
                }

                // serialPortController.sendData(selectResult[0].toString());

                System.out.println("ENVIO:" + selectResult[0].toString());
                runThread = true;
                threadNotifier.notifyAll();
            }
        }
    }

    @FXML
    void btnFlowTestAction(ActionEvent event) {
        runThread = false;
        synchronized (threadNotifier) {
            if (btnFlowTest.getText().equals("TESTE FLOW")) {
                // serialPortController.sendData(">0669C<");
                flow = "PARAR TESTE FLOW";

            } else if (btnFlowTest.getText().equals("PARAR TESTE FLOW")) {
                // serialPortController.sendData(">0679D<");
                flow = "TESTE FLOW";
            }
            runThread = true;
            threadNotifier.notifyAll();
        }
    }

    @FXML
    void btnPreFlowTestAction(ActionEvent event) {
        runThread = false;
        synchronized (threadNotifier) {
            if (btnPreFlowtTest.getText().equals("TESTE PRE FLOW")) {
                // serialPortController.sendData(">0649A<");
                preflow = "PARAR TESTE PRE FLOW";

            } else if (btnPreFlowtTest.getText().equals("PARAR TESTE PRE FLOW")) {
                // serialPortController.sendData(">0659B<");
                preflow = "TESTE PRE FLOW";
            }
            runThread = true;
            threadNotifier.notifyAll();
        }
    }

    @FXML
    void btnResetAction(ActionEvent event) {
        runThread = false;
        synchronized (threadNotifier) {
            System.out.println("TESTANDO RESET");
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

    private void updateGasValue() {
        btnFlowTest.setText(flow);
        btnPreFlowtTest.setText(preflow);

        material = cmbMaterial.getSelectionModel().getSelectedItem();
        thickness = cmbThickness.getSelectionModel().getSelectedItem();
        current = cmbCurrent.getSelectionModel().getSelectedItem();

        if (material != null && thickness != null && current != null) {
            runThread = false;
            synchronized (threadNotifier) {
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

                runThread = true;
                threadNotifier.notifyAll();
            }
        }

    }

    private void processSerialValues(String serialResponse) {
        serialValues = serialResponse.replace(">079", "").split(" ");
        fc = serialValues[0].replace("PC0", "") + " LB/POL2";
        pfc = serialValues[1].replace("PP0", "") + " LB/POL2";
        fp = serialValues[2].replace("SC0", "") + " LB/POL2";
        pfp = serialValues[3].replace("SP0", "") + " LB/POL2";
        labelCurrent = serialValues[4].replace("CS0", "") + "A";

        stateIndex = Integer.parseInt(serialValues[5].replace("ST0", ""));
        errorIndex = Integer.parseInt(serialValues[6].replace("ER0", ""));

        state = databaseController.getData("ESTADOS", "DESCRICAO", "ID", Integer.toString(stateIndex))[0].toString();
        error = databaseController.getData("ERROS", "DESCRICAO", "ID", Integer.toString(errorIndex))[0].toString();

        System.out.println(serialResponse);
    }

}
