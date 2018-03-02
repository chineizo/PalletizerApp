package m1app.com.albertsons.palletizerandroid.activity;

import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Base64;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.WriterException;
import com.google.zxing.common.BitMatrix;

import org.apache.commons.lang3.StringUtils;

import java.io.ByteArrayOutputStream;
import java.util.Arrays;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;

import m1app.com.albertsons.palletizerandroid.R;
import butterknife.BindView;
import butterknife.ButterKnife;
import m1app.com.albertsons.palletizerandroid.config.Config;
import m1app.com.albertsons.palletizerandroid.listener.ZplPrinterManager;
import m1app.com.albertsons.palletizerandroid.pojo.LineCode;
import m1app.com.albertsons.palletizerandroid.pojo.MIRecord;
import m1app.com.albertsons.palletizerandroid.pojo.MIResult;
import m1app.com.albertsons.palletizerandroid.pojo.NameValue;
import m1app.com.albertsons.palletizerandroid.utility.Prefs;
import m1app.com.albertsons.palletizerandroid.utility.Utils;

/**
 * Created by chinedu on 12/26/17.
 */

public class PrinterScreenActivity extends BaseActivity implements ZplPrinterManager.PrintStatusListener {

    private static final int WHITE = 0xFFFFFFFF;
    private static final int BLACK = 0xFF000000;

    private final String TAG = PrinterScreenActivity.class.getSimpleName();

    @BindView(R.id.webview)
    WebView webView;

    @BindView(R.id.progress_layout)
    View progressLayout;

    private Prefs prefs;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.print_screen_layout);
        ButterKnife.bind(this);


        createToolbar();

        showProgressView();
        webView.setWebViewClient(new WebViewClient() {

            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                return false;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                hideProgressView();
            }
        });

        prefs = new Prefs(this);
        Bundle bundle = getIntent().getExtras();
        //String numberOfLabels = bundle.getString(Config.Extra.NUMBER_OF_LABELS_PARAM);
        //LineCode lineCode = prefs.getLineCode(prefs.getLine());
        //String printIpAddress = lineCode.getIpAddress();
        //int portNumber = 9100;
        //Log.d(TAG, "Printer IP Address:" + printIpAddress);


        int quantity = Integer.valueOf(bundle.getString(Config.Extra.QUANTITY_PARAM)); //10
        MIResult miResult = (MIResult) bundle.getSerializable(Config.Extra.MI_RESULT_PARAM);
        MIRecord miRecord = miResult.getMIRecord().get(0);
        // barcode data
        String seriesData = bundle.getString(Config.Extra.SERIAL_NUMBER_PARAM);//"2358212";
        String lotNumber = bundle.getString(Config.Extra.LOT_NUMBER_PARAM);
        String itemNumber = StringUtils.EMPTY;
        //String lotNumber = StringUtils.EMPTY;
        String uom = StringUtils.EMPTY;
        String description = StringUtils.EMPTY;
        String pullDate = StringUtils.EMPTY;
        String barcode1 = "Not available";
        String barcode2 = "Not available";
        List<NameValue> nameValues = miRecord.getNameValue();
        for (NameValue value : nameValues) {
            if (Config.Field.VHPRNO.equals(value.getName())) {
                itemNumber = value.getValue().trim();
            } else if (Config.Field.VHBANO.equals(value.getName())) {
                if (TextUtils.isEmpty(lotNumber)) {
                    lotNumber = value.getValue();
                }
            } else if (Config.Field.VHMAUN.equals(value.getName())) {
                uom = value.getValue();
            } else if (Config.Field.MMFUDS.equals(value.getName())) {
                description = value.getValue();
            } else if (Config.Field.MBSLDY.equals(value.getName())) {

                try {
                    pullDate = Utils.addDays(Utils.TODAYS_DATE, Integer.valueOf(value.getValue().trim()));
                    Log.d(TAG, "SLDY:" + value.getValue().trim());
                } catch (Exception e) {
                    Log.e(TAG, "Couldn't parse Shelf Life Days  parameter:", e);
                }
                //productionDate = Utils.getTime(Utils.TODAYS_DATE);
            }
        }

        try {
            if (!TextUtils.isEmpty(seriesData)) {
                Bitmap bitmap = encodeAsBitmap(seriesData, BarcodeFormat.CODE_39, 300, 30);
                //Generates the series number barcode image
                ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
                byte[] byteArray = byteArrayOutputStream.toByteArray();
                String seriesNumber = Base64.encodeToString(byteArray, Base64.DEFAULT);

                //Generates the item number barcode image
                bitmap = encodeAsBitmap(itemNumber, BarcodeFormat.CODE_39, 150, 20);
                byteArrayOutputStream.reset();
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream);
                byteArray = byteArrayOutputStream.toByteArray();
                String itemNumberImage = Base64.encodeToString(byteArray, Base64.DEFAULT);

                barcode1 = "<center><img src='data:image/png;base64," + seriesNumber
                        + "' bgcolor='ff0000' alt='Series Number'/></center>";

                barcode2 = "<img src='data:image/png;base64," + itemNumberImage
                        + "' bgcolor='ff0000' alt='Series Number'/>";

            }

            String htmlDocument = "<html>" +
                    "<head>\n" +
                    "<style>\n" +
                    "h1 { \n" +
//                    "    display: block;\n" +
                    "font: 1px Times New Roman, serif;\n" +
                    "font-size: 4.5em;\n" +
//                    "    font-weight: bold;\n" +
                    "    margin: 0;\n" +
                    "    padding: 0;\n" +
                    "}\n" +

                    "h2 { \n" +
                    "    display: block;\n" +
                    "    font-size: 2em;\n" +
                    "font: 1px Times New Roman, serif;\n" +
//                    "    font-weight: bold;\n" +
                    "    margin: 0;\n" +
                    "    padding: 0;\n" +
                    "}\n" +


                    "h3 { \n" +
//                    "    display: block;\n" +
                    "    font-size: 0.5em;\n" +
                    "font: 1px Times New Roman, serif;\n" +
//                    "    font-weight: bold;\n" +
                    "    margin: 0;\n" +
                    "    padding: 0;\n" +
                    "}\n" +

                    "h4 { \n" +
//                    "    display: block;\n" +
                    "    font-size: 0.5em;\n" +
                    "font: 10px Times New Roman, serif;\n" +
//                    "    font-weight: bold;\n" +
//                    "    margin: 0;\n" +
//                    "    padding: 0;\n" +
                    "}\n" +


                    "</style>\n" +
                    "</head>" +


                    "<body>" + barcode1 + "<center><h1>" + seriesData + "</h1></center>" +

                    "<table style=\"width:100%\">" +
                    "<tr>" +
                    "<td><b><small><h4>Item</h4></small></b></td>" +
                    "<td><b><small><h4>Lot</h4></small></b></td> " +
                    "<td><b><small><h4>UOM</h4></small></b></td>" +
                    "</tr>" +
                    "<tr>" +
                    "<td><h4>" + itemNumber + "</h4></td>" +
                    "<td><h4>" + lotNumber + "</h4></td>" +
                    "<td><h4>" + uom + "</h4></td>" +
                    "</tr>" +

                    "<tr>" +
                    "<td>" +
                    barcode2 +
                    "</td>" +
                    "</tr>" +

                    "<tr>" +
                    "<td><b><small><h4>Description</h4></small><b></td>" +
                    "</tr>" +

                    "<tr>" +
                    "<td colspan=3><h4>" + description + "</h4></td>" +
                    "</tr>" +

                    "<tr>" +
                    "<td><b><small><h4>Prod Date</h4></small></b></th>" +
                    "<td><b><small><h4>Pull Date</h4></small></b></th> " +
                    "<td><b><small>Qty</small></b></th>" +
                    "</tr>" +

                    "<tr>" +
                    "<td>" + Utils.getTime(Utils.TODAYS_DATE) + "</td>" +
                    "<td><h4>" + pullDate + "</h4></td>" +
                    "<td><h2>" + quantity + "</h2></td>" +
                    "</tr>" +

                    "<tr>" +
                    "<td></td>" +
                    "<td colspan=2><h3><b>Printed:</b>  &nbsp;&nbsp; " + Utils.getTime(Utils.TODAYS_DATE_AND_TIME) + "</h3></td>" +
                    "</tr>" +

                    "</table>" +


                    "</body>" +
                    "</html>";
            webView.loadData(htmlDocument, "text/html", null);
        } catch (WriterException e) {
            Log.e(TAG, "Error occurred writing bitmap to screen", e);
        } catch (Exception e) {
            Log.e(TAG, "Some error occurred parsing label information:", e);
        }
    }

    @Override
    public boolean onOptionsItemSelected(final MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            case R.id.printer:
                displayPrinterLineSelector();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void promptToConfigureIpAddressToPrinter() {
        Utils.showAlertDialog(this,
                getString(R.string.contact_admin_for_printer_setup),
                R.drawable.ic_report_problem_black,
                getString(R.string.configure_ip_address),
                getString(R.string.ok), null);
    }

    private void displayPrinterLineSelector () {
        List<String> lines = Arrays.asList(getResources().getStringArray(R.array.lines));
        final CharSequence[] items = lines.toArray(new CharSequence[lines.size()]);
        int position = lines.indexOf(prefs.getLine());
        AlertDialog dialog = new AlertDialog.Builder(this)
                .setTitle(getString(R.string.select_line_to_print))
                .setSingleChoiceItems(items, position, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Log.d(TAG, "Item Selected:" + items[which]);
                        Log.d(TAG, "Line:" + prefs.getLine());
                        String line = items[which].toString();
                        LineCode lineCode = prefs.getLineCode(line);
                        if (lineCode == null || TextUtils.isEmpty(lineCode.getIpAddress())) {
                            Toast.makeText(PrinterScreenActivity.this,
                                    getString(R.string.configure_ip_address), Toast.LENGTH_SHORT).show();
                        } else {
                            prefs.setLine(line);
                        }
                    }
                })
                .setCancelable(false)
                .setIcon(R.drawable.ic_print_black)
                .setPositiveButton(getString(R.string.ok), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {

                        LineCode lineCode = prefs.getLineCode(prefs.getLine());
                        if (lineCode == null || TextUtils.isEmpty(lineCode.getIpAddress())) {
                            promptToConfigureIpAddressToPrinter();
                        } else {
                            sendPrintJob();

                            //send message to MainActivity to clear the Label Information currently on the screen
                            Intent intent = new Intent(Config.Extra.ACTION_CLEAR_SCREEN);
                            LocalBroadcastManager.getInstance(PrinterScreenActivity.this).sendBroadcast(intent);

                            finish();
                        }


                    }
                }).setNegativeButton(getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {

                    }
                }).create();
        dialog.show();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater menuInflater = getMenuInflater();
        menuInflater.inflate(R.menu.app_menu, menu);
        menu.removeItem(R.id.clear_fields);
        return true;
    }

    @Override
    public void showProgressView() {
        progressLayout.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideProgressView() {
        progressLayout.setVisibility(View.GONE);
    }

    private void sendPrintJob() {
        Bundle bundle = getIntent().getExtras();
        ZplPrinterManager zplPrinterManager = new ZplPrinterManager(this);
        zplPrinterManager.submitPrintJob(bundle);
    }

    private String guessAppropriateEncoding(CharSequence contents) {
        // Very crude at the moment
        for (int i = 0; i < contents.length(); i++) {
            if (contents.charAt(i) > 0xFF) {
                return "UTF-8";
            }
        }
        return null;
    }

    private Bitmap encodeAsBitmap(String content, BarcodeFormat barcodeFormat, int width, int height) throws WriterException {
        String contentsToEncode = content;
        Map<EncodeHintType, Object> hintTypeObjectMap;
        String encoding = guessAppropriateEncoding(contentsToEncode);
        if (encoding != null) {
            hintTypeObjectMap = new EnumMap<>(EncodeHintType.class);
            hintTypeObjectMap.put(EncodeHintType.CHARACTER_SET, encoding);
        }
        MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
        BitMatrix result = multiFormatWriter.encode(contentsToEncode, barcodeFormat, width, height);
        int w = result.getWidth();
        int h = result.getHeight();
        int[] pixels = new int[w * h];
        for (int y = 0; y < h; y++) {
            int offSet = y * w;
            for (int x = 0; x < w; x++) {
                pixels[offSet + x] = result.get(x, y) ? BLACK : WHITE;
            }
        }

        Bitmap bitmap = Bitmap.createBitmap(w, h, Bitmap.Config.ARGB_8888);
        bitmap.setPixels(pixels, 0, w, 0, 0, w, h);
        return bitmap;
    }

    @Override
    public void onPrintSent(final int resourceId) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "Print Job has been sent!");
                Toast.makeText(PrinterScreenActivity.this, resourceId, Toast.LENGTH_LONG).show();
                finish();
            }
        });
    }

    @Override
    public void onPrinterError(final int resourceId) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "A print error occurred: " + getString(resourceId));
                Utils.showAlertDialog(PrinterScreenActivity.this,
                        getString(R.string.printer_error), R.drawable.ic_report_problem_black,
                        getString(resourceId), getString(R.string.ok), null);
            }
        });

    }
}
