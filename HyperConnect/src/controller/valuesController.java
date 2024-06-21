package controller;

import java.net.URL;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ResourceBundle;
import java.util.Timer;
import java.util.TimerTask;

import javafx.application.Platform;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.KeyCode;
import javafx.scene.input.KeyEvent;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import model.App;
import model.DatabaseController;
import model.JsonEditor;
import model.SerialPortController;

public class valuesController implements Initializable{

    @FXML
    private Button btnBomb;
    @FXML
    private Button btnConfig;
    @FXML
    private Button btnFlowTest;
    @FXML
    private Button btnPreFlowtTest;
    @FXML
    private Button btnReset;
    @FXML
    private ImageView imgComunication;
    @FXML
    private Label lblCG1Presure;
    @FXML
    private Label lblCG2Presure;
    @FXML
    private Label lblChopCurrentA;
    @FXML
    private Label lblChopCurrentB;
    @FXML
    private Label lblVoltageLine;
    @FXML
    private Label lblDate;
    @FXML
    private Label lblErrorDec;
    @FXML
    private Label lblErrorNumber;
    @FXML
    private Label lblFloProt;
    @FXML
    private Label lblFlowCut;
    @FXML
    private Label lblLiqRef;
    @FXML
    private Label lblPreFlowCut;
    @FXML
    private Label lblPreFlowProt;
    @FXML
    private Label lblStatusDesc;
    @FXML
    private Label lblStatusNumber;
    @FXML
    private StackPane pnValues;
    @FXML
    private Button btnBack;

    private TimerTask serialPortListener, preFlowTask, flowTask, bombTask;
    private Runnable updateValues, updateFlowLabel, updatePreFlowLabel, updateBombLabel;
    private SerialPortController serialPortController;
    private String serialStandartResponse, serialNewResponse, serialDiagnosticsResponse;
    private boolean serialStatus;
    private String[] serialStandartValues, serialDiagnosticValues;
    private String pfp, fp, pfc, fc, labelCurrent, error, state, cg1, cg2, lvo, cac, cbc, cfl;
    private int errorIndex, stateIndex;
    private LocalDate date;
    private String formattedDate;
    private DateTimeFormatter dateFormatter;
    private Timer updateTimer, actionTimer;
    private DatabaseController databaseController;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        date = LocalDate.now();
        updateTimer = new Timer();

        // Formatar a data
        dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");
        formattedDate = date.format(dateFormatter);

        lblDate.setText(formattedDate);
        serialStatus = false;

        databaseController = new DatabaseController("./hyperconnect.db");
        databaseController.CreateTables();

        serialStandartResponse = serialDiagnosticsResponse = "";

        serialPortController = new SerialPortController();
        serialPortController.openSerialPort("ttyS0", Integer.parseInt(JsonEditor.readJSON().get("baudrate").toString()), Integer.parseInt(JsonEditor.readJSON().get("stopBits").toString()));

        updateValues = new Runnable() {
            @Override
            public void run() {
                lblStatusDesc.setText(state);
                lblErrorDec.setText(error);
                lblFlowCut.setText(fc);
                lblPreFlowCut.setText(pfc);
                lblFloProt.setText(fp);
                lblPreFlowProt.setText(pfp);
                lblCG1Presure.setText(cg1);
                lblCG2Presure.setText(cg2);
                lblChopCurrentA.setText(cac);
                lblChopCurrentB.setText(cbc);
                lblVoltageLine.setText(lvo);
                lblStatusNumber.setText(Integer.toString(stateIndex));
                lblErrorNumber.setText(Integer.toString(errorIndex));
                lblLiqRef.setText(cfl);
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
                        System.out.println("RESPOSTA>>" + serialNewResponse);
                        if (!serialNewResponse.equals("")) {
                            if (!serialNewResponse.equals(serialStandartResponse)) {
                                serialStandartResponse = serialNewResponse;
                                System.out.println(serialStandartResponse);
                                processSerialStandartValues(serialStandartResponse);
                            }
                        } else {
                            serialStatus = false;
                        }

                        serialPortController.sendData(">10091<");
                        serialNewResponse = serialPortController.readData();
                        //serialNewResponse = ">100CAT0482 CBT0021 LVO0118 CFL0009 CTP0481 TTP0481 CAC0001 CBC0014 WLC0005 CAS0000 CBS0534 PWMA0000 PWMB00000B<";
                        System.out.println("RESPOSTA>>" + serialNewResponse);
                        if (!serialNewResponse.equals("")) {
                            if (!serialNewResponse.equals(serialDiagnosticsResponse)) {
                                serialDiagnosticsResponse = serialNewResponse;
                                System.out.println(serialDiagnosticsResponse);
                                processSerialDiagnosticsValues(serialDiagnosticsResponse);
                            }

                        } else {
                            serialStatus = false;
                        }


                        Platform.runLater(updateValues);

                    } else {
                        imgComunication.setImage(new Image("/view/Resources/OFFlabelHigh.png"));
                        serialPortController.sendData(">00090<");
                        serialNewResponse = serialPortController.readData();
                        System.out.println("TENTANDO COMUNICACAO");
                    if (serialNewResponse.equals(JsonEditor.readJSON().get("machine").toString())) {
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

    }


    @FXML
    void btnBackAction(ActionEvent event) {
        updateTimer.cancel();
        updateTimer.purge();
        serialPortController.closeSerialPort();
        App.changeScene(getClass().getResource("/view/homeLayout.fxml"), (Stage) pnValues.getScene().getWindow());
    
    }

    @FXML
    void btnBombAction(ActionEvent event) {
        actionTimer = new Timer();

        if (btnBomb.getText().equals("TESTE BOMBA")) {
            serialPortController.sendData(">0711C9<");
            btnBomb.setText("PARAR TESTE BOMBA");

            updateBombLabel = new Runnable() {
                @Override
                public void run() {
                    btnBomb.setText("TESTE BOMBA");
                }
            };
            bombTask = new TimerTask() {
                @Override
                public void run() {
                    serialPortController.sendData(">0710C8<");
                    System.out.println("5 SEGUNDOS");
                    Platform.runLater(updateBombLabel);
                }
            };
            actionTimer.schedule(bombTask, 30000);

        } else if (btnBomb.getText().equals("PARAR TESTE BOMBA")) {
            serialPortController.sendData(">0710C8<");
            System.out.println("executa");
            btnBomb.setText("TESTE BOMBA");
        }
    }

    @FXML
    void btnConfigAction(ActionEvent event) {
        updateTimer.cancel();
        updateTimer.purge();
        serialPortController.closeSerialPort();
        App.changeScene(getClass().getResource("/view/passwordLayout.fxml"), (Stage) pnValues.getScene().getWindow());
   
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
        serialPortController.sendData(">0689E<");
    }

    private void processSerialStandartValues(String serialResponse) {
        if (serialResponse.startsWith(">") && serialResponse.endsWith("<")) {
            serialStandartValues = serialResponse.replace(">079", "").split(" ");
            fc = serialStandartValues[0].replace("PC0", "") + " LB/POL2";
            pfc = serialStandartValues[1].replace("PP0", "") + " LB/POL2";
            fp = serialStandartValues[2].replace("SC0", "") + " LB/POL2";
            pfp = serialStandartValues[3].replace("SP0", "") + " LB/POL2";
            labelCurrent = serialStandartValues[4].replace("CS0", "") + "A";

            stateIndex = Integer.parseInt(serialStandartValues[5].replace("ST0", ""));
            errorIndex = Integer.parseInt(serialStandartValues[6].replace("ER0", ""));
            
            cg1 = serialStandartValues[7].replace("CG0", "")  + " LB/POL2";
            cg2 = serialStandartValues[8].replace("CG0", "")  + " LB/POL2";

            state = databaseController.getData("ESTADOS", "DESCRICAO", "ID", Integer.toString(stateIndex))[0]
                            .toString();
            error = databaseController.getData("ERROS", "DESCRICAO", "ID", Integer.toString(errorIndex))[0]
                            .toString();

            System.out.println(serialResponse);

        }
    }

    private void processSerialDiagnosticsValues(String serialResponse){
        if (serialResponse.startsWith(">") && serialResponse.endsWith("<")) {
            serialDiagnosticValues = serialResponse.replace(">100", "").split(" ");
            lvo = Integer.toString(Integer.parseInt(serialDiagnosticValues[2].replace("LVO0", ""))/100 )+ "V";
            cfl = Integer.toString(Integer.parseInt(serialDiagnosticValues[3].replace("CFL0", ""))/100 ) + " LB/POL2";
            cac = serialDiagnosticValues[6].replace("CAC0", "") + "A";
            cbc = serialDiagnosticValues[7].replace("CBC0", "") + "A";
        }
    }

}
