package io.bistroo.plugins;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.RemoteException;
import android.util.Base64;

import com.getcapacitor.JSArray;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;
import com.getcapacitor.annotation.CapacitorPlugin;

import com.sunmi.peripheral.printer.InnerPrinterCallback;
import com.sunmi.peripheral.printer.InnerPrinterException;
import com.sunmi.peripheral.printer.InnerPrinterManager;
import com.sunmi.peripheral.printer.SunmiPrinterService;

import org.json.JSONException;

import java.util.ArrayList;

@CapacitorPlugin(
        name = "SunmiPlugin"
)
public class SunmiPlugin extends Plugin {
    private static final String TAG = "SunmiPlugin";

    SunmiPrinterService mPrinterService;

    @PluginMethod(returnType = PluginMethod.RETURN_NONE)
    public void raw(PluginCall call) throws RemoteException {
        String value = call.getString("value");

        mPrinterService.sendRAWData(Base64.decode(value, Base64.DEFAULT), null);

        call.resolve();
    }

    @PluginMethod(returnType = PluginMethod.RETURN_NONE)
    public void table(PluginCall call) throws RemoteException, JSONException {
        JSArray objects = call.getArray("rows");

        ArrayList<String> rows = new ArrayList<>();
        ArrayList<Integer> sizes = new ArrayList<>();
        ArrayList<Integer> alignments = new ArrayList<>();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            for (int i = 0; i < objects.length(); i++) {
                rows.add(objects.getJSONObject(i).getString("value"));
                sizes.add(objects.getJSONObject(i).getInt("size"));
                alignments.add(objects.getJSONObject(i).getInt("alignment"));
            }

            mPrinterService.printColumnsText(
                    rows.toArray(new String[rows.size()]),
                    sizes.stream().mapToInt(i->i).toArray(),
                    alignments.stream().mapToInt(i->i).toArray(),
                    null
            );
        }

        call.resolve();
    }

    @PluginMethod(returnType = PluginMethod.RETURN_NONE)
    public void line(PluginCall call) throws RemoteException {
        mPrinterService.lineWrap(1, null);
        mPrinterService.printText(call.getString("text"), null);

        call.resolve();
    }

    @PluginMethod(returnType = PluginMethod.RETURN_NONE)
    public void text(PluginCall call) throws RemoteException {
        mPrinterService.printText(call.getString("text"), null);

        call.resolve();
    }

    @PluginMethod(returnType = PluginMethod.RETURN_NONE)
    public void bold(PluginCall call) throws RemoteException {
        mPrinterService.sendRAWData(new byte[]{0x1B, 0x45, 0x1}, null);

        call.resolve();
    }

    @PluginMethod(returnType = PluginMethod.RETURN_NONE)
    public void normal(PluginCall call) throws RemoteException {
        mPrinterService.sendRAWData(new byte[]{0x1B, 0x45, 0x0}, null);

        call.resolve();
    }

    @PluginMethod(returnType = PluginMethod.RETURN_NONE)
    public void align(PluginCall call) throws RemoteException {
        switch (call.getString("direction")) {
            case "CENTER":
                mPrinterService.setAlignment(1, null);
                break;
            case "RIGHT":
                mPrinterService.setAlignment(2, null);
                break;
            default:
                mPrinterService.setAlignment(0, null);
        }

        call.resolve();
    }

    @PluginMethod(returnType = PluginMethod.RETURN_NONE)
    public void start(PluginCall call) throws RemoteException {
        mPrinterService.enterPrinterBuffer(true);

        call.resolve();
    }

    @PluginMethod
    public void print(PluginCall call) throws RemoteException {
        mPrinterService.commitPrinterBuffer();

        call.resolve();
    }

    @PluginMethod
    public void image(PluginCall call) throws RemoteException {
        byte[] decodedString = Base64.decode(call.getString("image"), Base64.DEFAULT);
        Bitmap image = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);

        mPrinterService.printBitmap(image, null);

        call.resolve();
    }

    @Override
    public void load() {
        super.load();

        InnerPrinterCallback innerPrinterCallback = new InnerPrinterCallback() {
            @Override
            protected void onConnected(SunmiPrinterService service) {
                mPrinterService = service;
            }

            @Override
            protected void onDisconnected() {

            }
        };

        try {
            InnerPrinterManager.getInstance().bindService(getBridge().getContext(), innerPrinterCallback);
        } catch (InnerPrinterException e) {
            e.printStackTrace();
        }
    }
}
