package m1app.com.albertsons.palletizerandroid.listener;


import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.zebra.sdk.comm.ConnectionException;
import com.zebra.sdk.comm.TcpConnection;
import com.zebra.sdk.printer.PrinterStatus;
import com.zebra.sdk.printer.ZebraPrinter;
import com.zebra.sdk.printer.ZebraPrinterFactory;
import com.zebra.sdk.printer.ZebraPrinterLanguageUnknownException;

import java.util.List;

import m1app.com.albertsons.palletizerandroid.R;
import m1app.com.albertsons.palletizerandroid.config.Config;
import m1app.com.albertsons.palletizerandroid.pojo.LineCode;
import m1app.com.albertsons.palletizerandroid.pojo.MIRecord;
import m1app.com.albertsons.palletizerandroid.pojo.MIResult;
import m1app.com.albertsons.palletizerandroid.pojo.NameValue;
import m1app.com.albertsons.palletizerandroid.pojo.PalletJob;
import m1app.com.albertsons.palletizerandroid.utility.Prefs;
import m1app.com.albertsons.palletizerandroid.utility.Utils;

public class ZplPrinterManager {

    public final static int PORT_NUMBER = 9100;
    public final static String TASK_PRINT_LABEL = "print_label";
    public final static String CLEAR_PRINTER_BUFFER = "clear_printer_buffer";
    public final static String FONT_TEST = "font_test";

    private final static String TAG = ZplPrinterManager.class.getSimpleName();
    private Context context;
    private PrintJobTask printJobTask;
    private PrintStatusListener printStatusListener;


    public ZplPrinterManager(Context context) {
        this.context = context;
        if (context instanceof PrintStatusListener) {
            printStatusListener = (PrintStatusListener) context;
        }
    }



    public void submitPrintJob(Bundle bundle) {
        PalletJob palletJob = new PalletJob();
        Prefs prefs = new Prefs(context);
        palletJob.setNumberOfLabels(bundle.getString(Config.Extra.NUMBER_OF_LABELS_PARAM));
        LineCode lineCode = prefs.getLineCode(prefs.getLine());
        palletJob.setIpAddress(lineCode.getIpAddress());
        Log.d(TAG, "Printer IP Address:" + lineCode.getIpAddress());

        palletJob.setQuantity(Integer.valueOf(bundle.getString(Config.Extra.QUANTITY_PARAM))); //10.0
        MIResult miResult = (MIResult) bundle.getSerializable(Config.Extra.MI_RESULT_PARAM);
        MIRecord miRecord = miResult.getMIRecord().get(0);
        String lotNumber = bundle.getString(Config.Extra.LOT_NUMBER_PARAM);
        // barcode data
        palletJob.setSeriesData(bundle.getString(Config.Extra.SERIAL_NUMBER_PARAM));
        palletJob.setPrintedDate(Utils.getTime(Utils.TODAYS_DATE_AND_TIME));

        List<NameValue> nameValues = miRecord.getNameValue();
        for (NameValue value : nameValues) {
            if (Config.Field.VHPRNO.equals(value.getName())) {
                palletJob.setItemNumber(value.getValue().trim());
            } else if (Config.Field.VHBANO.equals(value.getName())) {
                if (TextUtils.isEmpty(lotNumber)) {
                    palletJob.setLotNumber(value.getValue().trim());
                } else {
                    palletJob.setLotNumber(lotNumber);
                }
            } else if (Config.Field.VHMAUN.equals(value.getName())) {
                palletJob.setUom(value.getValue().trim());
            } else if (Config.Field.MMFUDS.equals(value.getName())) {
                palletJob.setDescription(value.getValue().trim());
            } else if (Config.Field.MBSLDY.equals(value.getName().trim())) {
                try {
                    palletJob.setPullDate(Utils.addDays(Utils.TODAYS_DATE, Integer.valueOf(value.getValue().trim())));
                    Log.d(TAG, "SLDY:" + value.getValue().trim());
                } catch (Exception e) {
                    Log.e(TAG, "Couldn't parse Shelf Life Days  parameter:", e);
                }
                palletJob.setProductionDate(Utils.getTime(Utils.TODAYS_DATE));
            }
        }

        executeTask(palletJob, TASK_PRINT_LABEL);
    }

    public void cancelPrintJob(String jobType){
        PalletJob palletJob = new PalletJob();
        Prefs prefs = new Prefs(context);
        LineCode lineCode = prefs.getLineCode(prefs.getLine());
        if (lineCode == null) {
            Toast.makeText(context, context.getString(R.string.configure_ip_address), Toast.LENGTH_LONG).show();
            return;
        }

        palletJob.setIpAddress(lineCode.getIpAddress());
        printJobTask = new PrintJobTask(printStatusListener, palletJob);
        printJobTask.setJobType(jobType);
        printJobTask.execute();
    }

    private void executeTask(PalletJob palletJob, String jobType) {
        printJobTask = new PrintJobTask(printStatusListener, palletJob);
        printJobTask.setJobType(jobType);
        printJobTask.execute();
    }

//    public void printFontTypes () {
//        PalletJob palletJob = new PalletJob();
//        Prefs prefs = new Prefs(context);
//        LineCode lineCode = prefs.getLineCode(prefs.getLine());
//        palletJob.setIpAddress(lineCode.getIpAddress());
//        printJobTask = new PrintJobTask(printStatusListener, palletJob.getIpAddress());
//        printJobTask.execute(getFontCommands());
//    }

//    private String getFontCommands () {
//        //return "^XA^WD*:*.FNT*^XZ";
//        return "^XA^FO50,50^A@N,50,50,E:TT0003M_.FNT^FDSAMPLE ARIALI^FS ^XZ";
//    }


    public interface PrintStatusListener {
        void onPrintSent(int resourceId);

        void onPrinterError(int resourceId);
    }

    public static class PrintJobTask extends AsyncTask<Void, Void, Void> {

        private PrintStatusListener printStatusListener;
        private PalletJob palletJob;
        private String jobType;

        public PrintJobTask(PrintStatusListener printStatusListener, PalletJob palletJob) {
            this.palletJob = palletJob;
            this.printStatusListener = printStatusListener;
        }

        public void setJobType (String jobType) {
            this.jobType = jobType;
        }

        public String getJobType () {
            return jobType;
        }

        private String cancelPrintJob() {
            // Cancel all print job
            return "^XA\n" +
                    "~JA^\n" +
                    "XZ";
        }

        private String fontTestPrintJob () {
            String job = "" +
                    "" +
                    "";
            return job;
        }

        private String generatePrintJob(PalletJob palletJob, int numberOfLabelsToPrint) {
            long serialNumber = Long.valueOf(palletJob.getSeriesData()) + numberOfLabelsToPrint;

            String printThis = "^XA\n" +
                    "^FT170,1140^BY7\n" +
                    "^B3B,N,150,N,N\n" +
                    "^FD" + serialNumber +"^FS\n" +
                    "^FT360,1080\n" +
                    "^A0B,230,260^FD" + serialNumber + "^FS\n" +
                    "^FT390,1180\n" +
                    "^A0B,30,30^FDItem^FS\n" +
                    "^FT390,680\n" +
                    "^A0B,30,30^FDLot^FS\n" +
                    "^FT390,240\n" +
                    "^A0B,30,30^FDUOM^FS\n" +
                    "^FT430,1180\n" +
                    "^A0B,40,40^FD" + palletJob.getItemNumber() +" ^FS\n" +
                    "^FT430,680\n" +
                    "^A0B,40,40^FD" +palletJob.getLotNumber() +"^FS\n" +
                    "^FT430,240\n" +
                    "^A0B,40,40^FD" + palletJob.getUom() + "^FS\n" +
                    "^FT500,1180^BY2\n" +
                    "^B3B,N,60,N,N\n" +
                    "^FD" + palletJob.getItemNumber() + "^FS\n" +
                    "^FT520,1180\n" +
                    "^A0B,30,30^FDDescription^FS\n" +
                    "^FT580,1200\n" +
                    "^A0B,50,50^FD" + palletJob.getDescription() +"^FS\n" +
                    "^FT660,1140\n" +
                    "^A0B,40,40^FDProd Date^FS\n" +
                    "^FT660,700\n" +
                    "^A0B,40,40^FDPull Date^FS\n" +
                    "^FT660,240\n" +
                    "^A0B,40,40^FDQty^FS\n" +
                    "^FT700,1160\n" +
                    "^A0B,50,50^FD" + palletJob.getProductionDate() +"^FS\n" +
                    "^FT700,720\n" +
                    "^A0B,50,50^FD" +palletJob.getPullDate()+ "^FS\n" +
                    "^FT750,245\n" +
                    "^A0B,108,60^FD" + palletJob.getQuantity()+"^FS\n" +
                    "^FT780,400\n" +
                    "^A0B,30,30^FD" + palletJob.getPrintedDate() +"^FS\n" +
                    "^FT780,500\n" +
                    "^A0B,30,30^FDPrinted:^FS\n" +
                    "^XZ\n";

            return printThis;
        }

        @Override
        protected Void doInBackground(Void... v) {

            ZebraPrinter zebraPrinter;
            TcpConnection tcpConnection = new TcpConnection(palletJob.getIpAddress(), PORT_NUMBER);
            try {
                tcpConnection.open();
                zebraPrinter = ZebraPrinterFactory.getInstance(tcpConnection);
                PrinterStatus printerStatus = zebraPrinter.getCurrentStatus();
                if (printerStatus.isReadyToPrint) {
                    Log.d(TAG, "Ready To Print");

                    if (TASK_PRINT_LABEL.equals(getJobType())) {
                        int numberOfLabels = Integer.valueOf(palletJob.getNumberOfLabels());
                        for (int x = 0; x < numberOfLabels; x++) {
                            Log.d(TAG, "Print Job:" + generatePrintJob(palletJob, x));
                            zebraPrinter.sendCommand(generatePrintJob(palletJob, x));
                            Thread.sleep(500l);
                        }
                    }else if (CLEAR_PRINTER_BUFFER.equals(getJobType())) {
                        zebraPrinter.sendCommand(cancelPrintJob());
                    } else if (FONT_TEST.equals(getJobType())) {

                    }

                    if (printStatusListener != null) {
                        printStatusListener.onPrintSent(R.string.printer_ready);
                    }
                } else if (printerStatus.isPaused) {
                    if (printStatusListener != null) {
                        printStatusListener.onPrinterError(R.string.printer_paused);
                    }
                } else if (printerStatus.isHeadOpen) {
                    if (printStatusListener != null) {
                        printStatusListener.onPrinterError(R.string.printer_head_open);
                    }
                } else if (printerStatus.isPaperOut) {
                    if (printStatusListener != null) {
                        printStatusListener.onPrinterError(R.string.paper_is_out);
                    }
                } else {
                    if (printStatusListener != null) {
                        printStatusListener.onPrinterError(R.string.cannot_print);
                    }
                }
            } catch (InterruptedException e) {
                Log.e(TAG, "Interrupted Exception occured:", e);
            } catch (ConnectionException e) {
                Log.e(TAG, "A connection error occurred:", e);
            } catch (ZebraPrinterLanguageUnknownException e) {
                Log.e(TAG, "ZebraPrinterLanguageUnknownException Error:", e);
            } finally {
                try {
                    Log.d(TAG, "Closing tcp connection!");
                    tcpConnection.close();
                } catch (ConnectionException e) {
                    Log.e(TAG, "Error closing tcp connection:", e);
                }
            }
            return null;
        }
    }
}
