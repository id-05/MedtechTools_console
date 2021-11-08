package sample;

import org.usb4java.*;

import java.nio.ByteBuffer;
import java.nio.ByteOrder;
import java.nio.IntBuffer;

public class SpiroDevice {

    private static final byte IN_ENDPOINT = (byte) 0x81;
    private static final byte OUT_ENDPOINT = 0x01;
    private static final short VENDOR_ID = (short) 1155;
    private static final short PRODUCT_ID = (short) 22352;
    private static final byte INTERFACE = (byte) 0x00;
    private static boolean state = false;
    Context context;
    static DeviceHandle handle;
    Device device;

    public void open(){
        context = new Context();
        int result = LibUsb.init(context);
        if (result != LibUsb.SUCCESS)
        {
            throw new LibUsbException("Unable to initialize libusb", result);
        }

        handle = LibUsb.openDeviceWithVidPid(null, VENDOR_ID, PRODUCT_ID);
        if (handle == null)
        {
            System.err.println("Test device not found.");
            System.exit(1);
        }

        result = LibUsb.claimInterface(handle, INTERFACE);
        if (result != LibUsb.SUCCESS)
        {
            throw new LibUsbException("Unable to claim interface", result);
        }
        state = true;
    }

    public void close(){
        int result = LibUsb.releaseInterface(handle, INTERFACE);
        if (result != LibUsb.SUCCESS)
        {
            throw new LibUsbException("Unable to release interface", result);
        }
        LibUsb.close(handle);
        LibUsb.exit(context);
        state = false;
    }

    public static ByteBuffer read(int size)
    {
        ByteBuffer buffer = BufferUtils.allocateByteBuffer(size).order(ByteOrder.LITTLE_ENDIAN);
        IntBuffer transferred = BufferUtils.allocateIntBuffer();
        if(!(handle == null)) {
            int result = LibUsb.bulkTransfer(handle, IN_ENDPOINT, buffer, transferred, 1000);
            if (result != LibUsb.SUCCESS) {
                throw new LibUsbException("Unable to read data", result);
            }
        }
        return buffer;
    }

    public static Double readADCResult(int size)
    {
        ByteBuffer buffer = read(size);
        return ((((buffer.get(0) & 0xff) << 8)) | (buffer.get(1)& 0xff)) *  0.000805;
    }

    public static ByteBuffer readBufResult(int size)
    {
        return read(size);
    }

    public void write(byte[] data)
    {
        ByteBuffer buffer = BufferUtils.allocateByteBuffer(data.length);
        buffer.put(data);
        IntBuffer transferred = BufferUtils.allocateIntBuffer();
        int result = LibUsb.bulkTransfer(handle, OUT_ENDPOINT, buffer,
                transferred, 1000);
        if (result != LibUsb.SUCCESS)
        {
            throw new LibUsbException("Unable to send data", result);
        }
    }

    public boolean isCheckDevice(){
        device = findDevice(VENDOR_ID, PRODUCT_ID);
        boolean buf = false;
        if(device!=null){
            buf = true;
        }
        return buf;
    }

    public Device findDevice(short vendorId, short productId)
    {
        context = new Context();
        LibUsb.init(context);
        DeviceList list = new DeviceList();
        int result = LibUsb.getDeviceList(context, list);
        if (result < 0) throw new LibUsbException("Unable to get device list", result);
        try
        {
            for (Device device: list)
            {
                DeviceDescriptor descriptor = new DeviceDescriptor();
                result = LibUsb.getDeviceDescriptor(device, descriptor);
                if (result != LibUsb.SUCCESS) throw new LibUsbException("Unable to read device descriptor", result);
                if (descriptor.idVendor() == vendorId && descriptor.idProduct() == productId)
                    return device;
            }
        }
        finally
        {
            LibUsb.freeDeviceList(list, true);
            LibUsb.exit(context);
        }
        return null;
    }

}
