package io.bistroo.plugins;

import android.Manifest;
import android.util.Log;

import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;
import com.getcapacitor.annotation.CapacitorPlugin;
import com.getcapacitor.annotation.Permission;

import com.sunmi.printerx.PrinterSdk;
import com.sunmi.printerx.SdkException;

import java.nio.charset.StandardCharsets;
import java.util.List;

@CapacitorPlugin(
        name = "SunmiPlugin",
        permissions = {
                @Permission(
                        alias = "bluetooth",
                        strings = {
                                Manifest.permission.BLUETOOTH,
                                Manifest.permission.BLUETOOTH_SCAN
                        }
                ),
        })
public class SunmiPlugin extends Plugin {
    private PrinterSdk.Printer mSelectedPrinter;

    @PluginMethod(returnType = PluginMethod.RETURN_PROMISE)
    public void printText(PluginCall call) throws SdkException {
        String value = call.getString("value");

        Log.d("SunmiPlugin", value);

        mSelectedPrinter.commandApi().sendEscCommand(value.getBytes(StandardCharsets.UTF_8));
        mSelectedPrinter.commandApi().sendEscCommand(new byte[]{0x0A});
        mSelectedPrinter.commandApi().sendEscCommand(new byte[]{0x0A});

        call.resolve();
    }

    @Override
    public void load() {
        super.load();

        try {
            PrinterSdk.getInstance().getPrinter(getBridge().getContext(), new PrinterSdk.PrinterListen() {
                @Override
                public void onDefPrinter(PrinterSdk.Printer printer) {
                    Log.d("SunmiPlugin", printer.toString());

                    mSelectedPrinter = printer;
                }

                @Override
                public void onPrinters(List<PrinterSdk.Printer> printers) {}
            });
        } catch (SdkException e) {
            e.printStackTrace();
        }
    }
}
