package model;    
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import com.fazecast.jSerialComm.SerialPort;

public class SerialPortController {
    SerialPort serialPort;
    SerialPort[] serialPortsList;
    String[] serialPortsListName, serialPortsListDescription;
    InputStream portInput;
    OutputStream portOutput;
    byte[] newData;
    String response;
    int i;

    public String[] getSerialPortsName() {
        serialPortsList = SerialPort.getCommPorts();
        i = 0;

        for (SerialPort serialPort : serialPortsList) {
            serialPortsListName[i] = serialPort.getSystemPortName();
        }

        return serialPortsListName;
    }

    public String[] getSerialPortsDescription() {
        serialPortsList = SerialPort.getCommPorts();
        i = 0;

        for (SerialPort serialPort : serialPortsList) {
            serialPortsListName[i] = serialPort.getPortDescription();
        }

        return serialPortsListDescription;
    }

    public boolean openSerialPort(String portName, int baudrate) {
        serialPort = SerialPort.getCommPort(portName);
        serialPort.flushIOBuffers();
        serialPort.setBaudRate(baudrate);
        serialPort.openPort();
        portOutput = serialPort.getOutputStream();
        portInput = serialPort.getInputStream();

        return serialPort.isOpen();
    }

    public void sendData(String data) {
        try {
            portOutput.flush();
            portOutput.write(data.getBytes());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String readData() {
        try {
            Thread.sleep(350);
            newData = new byte[portInput.available()];
            portInput.read(newData);
            response = new String(newData);
        } catch (InterruptedException | IOException e) {
            e.printStackTrace();
        }

        return response;
    }

    public void closeSerialPort() {
        try {
            portInput.close();
            portOutput.close();
            serialPort.closePort();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

}
