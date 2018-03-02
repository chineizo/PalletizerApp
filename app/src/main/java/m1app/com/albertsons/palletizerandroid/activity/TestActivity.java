package m1app.com.albertsons.palletizerandroid.activity;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.widget.EditText;
import android.widget.Toast;

import m1app.com.albertsons.palletizerandroid.R;


public class TestActivity extends BaseActivity{
    private EditText scannerData;
    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.test_layout);
        scannerData = findViewById(R.id.scanner_view);
    }

    @Override
    public void showProgressView() {}

    @Override
    public void hideProgressView() {}

    @Override
    protected void onResume() {
        super.onResume();
//        IntentFilter filter = new IntentFilter();
//        filter.addCategory(Intent.CATEGORY_DEFAULT);
//        filter.addAction(getResources().getString(R.string.activity_intent_filter_action));
//        registerReceiver(myBroadcastReceiver, filter);
    }

    @Override
    protected void onPause() {
        super.onPause();
        //unregisterReceiver(myBroadcastReceiver);
    }

//    private BroadcastReceiver myBroadcastReceiver = new BroadcastReceiver() {
//        @Override
//        public void onReceive(Context context, Intent intent) {
//            String action = intent.getAction();
//            if (action.equals(getResources().getString(R.string.activity_intent_filter_action))) {
//                //  Received a barcode scan
//                try {
//                    displayScanResult(intent, "via Broadcast");
//                } catch (Exception e) {
//                    //  Catch if the UI does not exist when we receive the broadcast... this is not designed to be a production app
//                }
//            }
//        }
//    };

    private void displayScanResult(Intent initiatingIntent, String howDataReceived) {
        String decodedSource = initiatingIntent.getStringExtra(getResources().getString(R.string.datawedge_intent_key_source));
        String decodedData = initiatingIntent.getStringExtra(getResources().getString(R.string.datawedge_intent_key_data));
        String decodedLabelType = initiatingIntent.getStringExtra(getResources().getString(R.string.datawedge_intent_key_label_type));

        if (null == decodedSource)
        {
            decodedSource = initiatingIntent.getStringExtra(getResources().getString(R.string.datawedge_intent_key_source_legacy));
            decodedData = initiatingIntent.getStringExtra(getResources().getString(R.string.datawedge_intent_key_data_legacy));
            decodedLabelType = initiatingIntent.getStringExtra(getResources().getString(R.string.datawedge_intent_key_label_type_legacy));
        }
        Toast.makeText(this,decodedData, Toast.LENGTH_LONG).show();
        scannerData.setText(decodedData);

//
//        final TextView lblScanSource = (TextView) findViewById(R.id.lblScanSource);
//        final TextView lblScanData = (TextView) findViewById(R.id.lblScanData);
//        final TextView lblScanLabelType = (TextView) findViewById(R.id.lblScanDecoder);
//        lblScanSource.setText(decodedSource + " " + howDataReceived);
//        lblScanData.setText(decodedData);
//        lblScanLabelType.setText(decodedLabelType);
    }
}
