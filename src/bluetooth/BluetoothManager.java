package bluetooth;

import javax.bluetooth.*;
import javax.microedition.io.Connector;
import javax.microedition.io.StreamConnection;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Manages the creation of a bluetooth connection. This manager will search all the currently available bluetooth
 * devices that match the given search criteria and match with them.
 * <p>
 * NOTE: The computer needs to have already connected with the device for this to work. If not, the connection will be
 * refused.
 */
public class BluetoothManager {

    private static OutputStream output;
    private static InputStream input;
    private static ArduinoDeviceGetter getter;

    /**
     * Established a connection with the bluetooth device using its bluetooth address
     *
     * @param bluetoothAddress
     *         The address of the device.
     * @param pin
     *         The device's pin.
     */
    public static void establishConnectionAddress(String bluetoothAddress, String pin) {
        ArduinoDeviceGetter getter = new ArduinoDeviceGetter(BluetoothManager::serviceSearch,
                                                             BluetoothManager::finishConnection);
        getter.setAddress(bluetoothAddress, pin);
        BluetoothManager.getter = getter;
        establishConnection(getter);
    }

    /**
     * Establishes a connection with a bluetooth device using the device's friendly name.
     *
     * @param friendlyName
     *         The friendly name of the device to connect to.
     * @param pin
     *         The device's pin.
     */
    public static void establishConnectionFriendly(String friendlyName, String pin) {
        ArduinoDeviceGetter getter = new ArduinoDeviceGetter(BluetoothManager::serviceSearch,
                                                             BluetoothManager::finishConnection);
        getter.setFriendlyName(friendlyName, pin);
        BluetoothManager.getter = getter;
        establishConnection(getter);
    }

    /**
     * Starts the process of establishing a connection.
     *
     * @param getter
     *         The getter to use to establish the connection.
     */
    private static void establishConnection(ArduinoDeviceGetter getter) {
        try {
            LocalDevice.getLocalDevice()
                       .getDiscoveryAgent()
                       .startInquiry(DiscoveryAgent.GIAC, getter);
        }
        catch(BluetoothStateException e) {
            throw new RuntimeException("Unexpected exception", e);
        }
    }

    /**
     * Search for the first service offered by the given device.
     *
     * @param device
     *         The device whoes service we are interested in.
     */
    private static void serviceSearch(RemoteDevice device) {
        try {
            int[] attr = {0x0100};
            UUID[] uuids = {new UUID(0x1101)};
            LocalDevice.getLocalDevice()
                       .getDiscoveryAgent()
                       .searchServices(attr, uuids, device, getter);

        }
        catch(Exception e) {
            throw new RuntimeException("Unexpected exception", e);
        }
    }

    /**
     * Use the given bluetooth service address to complete the connection to the device.
     *
     * @param address
     *         The address of the service to connect too.
     */
    private synchronized static void finishConnection(String address) {
        StreamConnection connection;
        try {
            connection = (StreamConnection) Connector.open(address);
            output = connection.openOutputStream();
            input = connection.openInputStream();
        }
        catch(IOException e) {
            throw new RuntimeException("Connection failed, did you make sure that your computer trusts the device?", e);
        }
        System.out.println("Connection complete");
    }

    /**
     * @return The OutputStream once the connection is established, null otherwise.
     */
    public synchronized static OutputStream getOutputStream() {
        return output;
    }

    /**
     * @return The OutputStream once the connection is established, null otherwise.
     */
    public synchronized static InputStream getInputStream() {
        return input;
    }
}
