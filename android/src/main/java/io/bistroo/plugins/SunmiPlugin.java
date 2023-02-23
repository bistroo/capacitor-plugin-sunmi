package io.bistroo.plugins;

import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.RemoteException;
import android.util.Base64;
import android.util.Log;

import com.getcapacitor.JSArray;
import com.getcapacitor.JSObject;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;
import com.getcapacitor.annotation.CapacitorPlugin;

import com.sunmi.peripheral.printer.InnerPrinterCallback;
import com.sunmi.peripheral.printer.InnerPrinterException;
import com.sunmi.peripheral.printer.InnerPrinterManager;
import com.sunmi.peripheral.printer.InnerResultCallback;
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
        String text = call.getString("text");
        Boolean wrap = call.getBoolean("wrap");

        if (text != null) {
            mPrinterService.printText(text, null);
        }

        if (wrap != null && wrap) {
            mPrinterService.lineWrap(1, null);
        }

        call.resolve();
    }

    @PluginMethod(returnType = PluginMethod.RETURN_NONE)
    public void wrap(PluginCall call) throws RemoteException {
        mPrinterService.lineWrap(1, null);

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
        mPrinterService.commitPrinterBufferWithCallback(new InnerResultCallback() {
            @Override
            public void onRunResult(boolean isSuccess) {
                Log.d(TAG, String.format("%b", isSuccess));
            }

            @Override
            public void onReturnString(String result) {
                Log.d(TAG, result);
            }

            @Override
            public void onRaiseException(int code, String msg) {
                Log.e(TAG, String.format("%s, %d", msg, code));

                call.reject(msg, String.valueOf(code));
            }

            @Override
            public void onPrintResult(int code, String msg) {
                if (code == 0) {
                    Log.d(TAG, msg);

                    call.resolve();
                } else {
                    Log.e(TAG, String.format("%s, %d", msg, code));

                    call.reject(msg, String.valueOf(code));
                }
            }
        });

        call.resolve();
    }

    @PluginMethod(returnType = PluginMethod.RETURN_NONE)
    public void image(PluginCall call) throws RemoteException {
        byte[] decodedString = Base64.decode(call.getString("image"), Base64.DEFAULT);
        Bitmap image = BitmapFactory.decodeByteArray(decodedString, 0, decodedString.length);

        mPrinterService.printBitmap(image, null);

        call.resolve();
    }

    @PluginMethod
    public void deviceInfo(PluginCall call) throws RemoteException {
        String serialNumber = mPrinterService.getPrinterSerialNo();
        String model = mPrinterService.getPrinterModal();

        JSObject data = new JSObject();
        data.put("serial_number", serialNumber);
        data.put("model", model);

        call.resolve(data);
    }

    @Override
    public void load() {
        super.load();

        Log.d(TAG, "Start plugin");

        Intent intent = new Intent();
        intent.setPackage("woyou.aidlservice.jiuiv5");
        intent.setAction("woyou.aidlservice.jiuiv5.IWoyouService");
        getContext().startService(intent);

        IntentFilter filter = new IntentFilter();
        filter.addAction("woyou.aidlservice.jiuv5.INIT_ACTION");
        filter.addAction("woyou.aidlservice.jiuv5.FIRMWARE_UPDATING_ACITON");
        filter.addAction("woyou.aidlservice.jiuv5.NORMAL_ACTION");
        filter.addAction("woyou.aidlservice.jiuv5.ERROR_ACTION");
        filter.addAction("woyou.aidlservice.jiuv5.OUT_OF_PAPER_ACTION");
        filter.addAction("woyou.aidlservice.jiuv5.OVER_HEATING_ACITON");
        filter.addAction("woyou.aidlservice.jiuv5.NORMAL_HEATING_ACITON");
        filter.addAction("woyou.aidlservice.jiuv5.COVER_OPEN_ACTION");
        filter.addAction("woyou.aidlservice.jiuv5.COVER_ERROR_ACTION");
        filter.addAction("woyou.aidlservice.jiuv5.KNIFE_ERROR_ACTION_1");
        filter.addAction("woyou.aidlservice.jiuv5.KNIFE_ERROR_ACTION_2");
        filter.addAction("woyou.aidlservice.jiuv5.FIRMWARE_UPDATING_ACITON");
        filter.addAction("woyou.aidlservice.jiuv5.FIRMWARE_FAILURE_ACITON");
        filter.addAction("woyou.aidlservice.jiuv5.PRINTER_NON_EXISTENT_ACITON");
        filter.addAction("woyou.aidlservice.jiuv5.BLACKLABEL_NON_EXISTENT_ACITON");

        getContext().registerReceiver(new SunmiBroadcastReceiver(), filter);

        InnerPrinterCallback innerPrinterCallback = new InnerPrinterCallback() {
            @Override
            protected void onConnected(SunmiPrinterService service) {
                Log.d(TAG, "Connected to printer");

                mPrinterService = service;
            }

            @Override
            protected void onDisconnected() {
                Log.d(TAG, "Disconnected");
            }
        };

        try {
            InnerPrinterManager.getInstance().bindService(getContext(), innerPrinterCallback);
        } catch (InnerPrinterException e) {
            e.printStackTrace();
        }
    }
}
