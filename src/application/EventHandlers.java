package application;

import bluetooth.BluetoothManager;

import java.awt.event.ActionEvent;
import java.io.IOException;

/**
 * Event handlers for the window. Follows singleton pattern.
 */
public class EventHandlers {
    public static final String FRIENDLY_NAME = "Mobile Hub";
    public static final String PIN = "1234";
    public static final String HC05_ADDRESS = "98D351FD99C0";

    private static boolean initialized = false;

    private static boolean redLed = false;
    private static boolean blueLed = false;
    private static boolean motor = false;

    public static void initialize() {
        BluetoothManager.establishConnectionAddress(HC05_ADDRESS, PIN);
        initialized = true;
    }

    public static void blueLedOnOff(ActionEvent e) {
        System.out.println("blue button pressed");
        checkInitialized();
        if(blueLed) {
            blueLed = false;
            sendData(1);
        }
        else {
            sendData(2);
            blueLed = true;
        }
    }

    public static void redLedOnOff(ActionEvent e) {
        System.out.println("red button pressed");
        checkInitialized();
        if(redLed) {
            sendData(3);
            redLed = false;
        }
        else {
            sendData(4);
            redLed = true;
        }
    }

    public static void changeMotorDirection(ActionEvent e) {
        System.out.println("Motor direction pressed");
        checkInitialized();
        sendData(5);
    }

    public static void motorOnOff(ActionEvent e) {
        System.out.println("Motor on/off pressed");
        checkInitialized();
        if(motor) {
            sendData(7);
            motor = false;
        }
        else {
            sendData(6);
            motor = true;
        }
    }

    private static void checkInitialized() {
        if(!initialized) {
            throw new IllegalStateException("Event Handlers must be initialized");
        }
    }

    private static void sendData(int b) {
        try {
            if(BluetoothManager.getOutputStream() == null) {
                System.out.println("Connection not yet established!");
            }
            else {
                BluetoothManager.getOutputStream()
                                .write(b);
            }
        }
        catch(IOException e) {
            throw new RuntimeException("Unexpected exception", e);
        }
    }

}
