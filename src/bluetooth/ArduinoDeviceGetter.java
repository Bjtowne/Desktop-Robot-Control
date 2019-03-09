package bluetooth;

import com.intel.bluetooth.RemoteDeviceHelper;

import javax.bluetooth.DeviceClass;
import javax.bluetooth.DiscoveryListener;
import javax.bluetooth.RemoteDevice;
import javax.bluetooth.ServiceRecord;
import java.io.IOException;
import java.util.function.BiConsumer;
import java.util.function.Consumer;

/**
 * Listener used to obtain and store the address of the bluetooth device with the given friendly name.
 */
public class ArduinoDeviceGetter implements DiscoveryListener {

    private String artifact;
    private String pin;
    private boolean friendlyName = false;

    private Consumer<RemoteDevice> deviceInquiryCallback;
    private Consumer<String> serviceInquiryCallback;

    private RemoteDevice device;
    private String address = null;

    public ArduinoDeviceGetter(Consumer<RemoteDevice> deviceInquiryCallback, Consumer<String> serviceInquiryCallback) {
        this.deviceInquiryCallback = deviceInquiryCallback;
        this.serviceInquiryCallback = serviceInquiryCallback;
    }

    public void setFriendlyName(String name, String pin) {
        friendlyName = true;
        artifact = name;
        this.pin = pin;
    }

    public void setAddress(String address, String pin) {
        friendlyName = false;
        artifact = address;
        this.pin = pin;
    }

    @Override
    public void deviceDiscovered(RemoteDevice remoteDevice, DeviceClass deviceClass) {
        String name;
        try {
            name = remoteDevice.getFriendlyName(true);
            System.out.println("Device found: " + name);
            if(friendlyName && name.equals(artifact) || !friendlyName && remoteDevice.getBluetoothAddress()
                                                                                     .equals(artifact)) {
                device = remoteDevice;
                if(!RemoteDeviceHelper.authenticate(remoteDevice, pin)) {
                    throw new RuntimeException("Authentication failed!");
                }
            }
        }
        catch(IOException e) {
            throw new RuntimeException("Unexpected exception", e);
        }
    }

    @Override
    public void servicesDiscovered(int i, ServiceRecord[] serviceRecords) {
        System.out.println("Service discovered");
        for(ServiceRecord j: serviceRecords){
            if(address == null){
                address = j.getConnectionURL(ServiceRecord.NOAUTHENTICATE_NOENCRYPT,false);
            }
        }
    }

    @Override
    public void serviceSearchCompleted(int i, int i1) {
        System.out.println("Services search complete");
        if(address != null){
            serviceInquiryCallback.accept(address);
        }else{
            throw new IllegalStateException("Service not found!");
        }
    }

    @Override
    public void inquiryCompleted(int i) {
        System.out.println("Inquiry completed");
        if(device != null) {
            deviceInquiryCallback.accept(device);
        }
        else {
            throw new IllegalStateException("Bluetooth device not found!");
        }
    }
}
