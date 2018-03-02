package m1app.com.albertsons.palletizerandroid.fragment;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.support.v7.widget.DividerItemDecoration;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Arrays;
import java.util.List;

import javax.inject.Inject;

import m1app.com.albertsons.palletizerandroid.M1Application;
import m1app.com.albertsons.palletizerandroid.R;
import butterknife.BindView;
import butterknife.ButterKnife;
import m1app.com.albertsons.palletizerandroid.activity.PrinterScreenActivity;
import m1app.com.albertsons.palletizerandroid.adapter.M1Adapter;
import m1app.com.albertsons.palletizerandroid.config.Config;
import m1app.com.albertsons.palletizerandroid.listener.M1FragmentMVP;
import m1app.com.albertsons.palletizerandroid.listener.ZplPrinterManager;
import m1app.com.albertsons.palletizerandroid.pojo.MIResult;
import m1app.com.albertsons.palletizerandroid.service.ApiService;
import m1app.com.albertsons.palletizerandroid.utility.Prefs;
import m1app.com.albertsons.palletizerandroid.utility.Utils;
import m1app.com.albertsons.palletizerandroid.view.RecyclerViewTouchListener;



/**
 * Created by chinedu on 12/29/17.
 */

public class PalletizerFragment extends BaseFragment implements M1FragmentMVP.View, ZplPrinterManager.PrintStatusListener {

    private final String TAG = PalletizerFragment.class.getSimpleName();

    @Inject
    ApiService apiService;

    @BindView(R.id.progress_layout)
    View progressLayout;

    @BindView(R.id.recyclerview)
    RecyclerView recyclerView;

    @BindView(R.id.material_number)
    TextInputEditText materialNumberField;

    @BindView(R.id.quantity)
    TextInputEditText quantityField;

    @BindView(R.id.label_search)
    Button labelSearch;

    @BindView(R.id.number_of_labels)
    TextInputEditText numberOfLabels;

    @BindView(R.id.lines_spinner)
    Spinner lineSpinner;

    @BindView(R.id.clear_buffer)
    Button clearPrinterBuffer;

    @Inject
    M1FragmentMVP.Presenter presenter;

    private M1Adapter m1Adapter;
    private Bundle bundle;
    private Prefs prefs;
    //private M1ScannerManager2 m1ScannerManager;


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((M1Application) getActivity().getApplication()).getComponent().inject(this);
        setHasOptionsMenu(true);
        prefs = new Prefs(getContext());
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.m1_layout, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        registerForSoftKeyBoardControl(view);
//        m1ScannerManager = new M1ScannerManager(getContext());
//        m1ScannerManager = new M1ScannerManager2(getActivity());
//        m1ScannerManager.addOnM1ScannerCompleteListener(this);

        recyclerView.setLayoutManager(new LinearLayoutManager(getActivity()));

        initializeLines();
        //materialNumberField.setText("0001000195");//("0001000079");//"0001000107");
        materialNumberField.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView textView, int actionId, KeyEvent keyEvent) {
                if (actionId == EditorInfo.IME_NULL
                        && keyEvent.getAction() == KeyEvent.ACTION_DOWN) {
                    //materialNumberField.clearFocus();
                    //quantityField.requestFocus();
                    Log.d(TAG, "Enter Key Pressed!");
                    //quantityField.requestFocus();
                    return true;
                }

                return false;
            }
        });


        labelSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                executeLabelSearch();
            }
        });
        clearPrinterBuffer.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Utils.showAlertDialog(getContext(), getString(R.string.printer), R.drawable.ic_print_white,
                        getString(R.string.want_to_clear_buffer),
                        getString(R.string.ok), new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(getContext(), R.string.clearing_printer_buffer, Toast.LENGTH_LONG).show();
                                ZplPrinterManager zplPrinterManager = new ZplPrinterManager(getContext());
                                zplPrinterManager.cancelPrintJob(ZplPrinterManager.CLEAR_PRINTER_BUFFER);
                                //zplPrinterManager.printFontTypes();
                            }
                        }, getString(R.string.cancel), null);
            }
        });
//        materialNumberField.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View view) {
//                materialNumberField.setFocusableInTouchMode(true);
//                materialNumberField.requestFocus();
//            }
//        });


    }

    private void initializeLines() {
        List<String> lines = Arrays.asList(getResources().getStringArray(R.array.lines));
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_spinner_item, lines);
        lineSpinner.setAdapter(arrayAdapter);
        int position = arrayAdapter.getPosition(prefs.getLine());
        lineSpinner.setSelection(position);

        lineSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                String line = adapterView.getItemAtPosition(position).toString();
                prefs.setLine(line);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

    }

    private void executeLabelSearch() {
        hideKeyboard();
        showProgressView();
        presenter.getLabelInformation();
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.app_menu, menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                getActivity().finish();
                return true;
            case R.id.clear_fields:
                clearFields();
                break;
            case R.id.printer:
                if (getPrinterData() != null) {
                    getPrinterData().putString(Config.Extra.QUANTITY_PARAM, getQuantity());
                    getPrinterData().putString(Config.Extra.MATERIAL_NUMBER_PARAM, getMaterialNumber());
                    getPrinterData().putString(Config.Extra.NUMBER_OF_LABELS_PARAM, getNumberOfLabels());

                    Intent intent = new Intent(getContext(), PrinterScreenActivity.class);
                    intent.putExtras(getPrinterData());
                    startActivity(intent);
                } else {
                    Utils.showAlertDialog(getActivity(),
                            getString(R.string.print_job),
                            R.drawable.ic_report_problem_black,
                            getString(R.string.no_print_job_error),
                            getString(R.string.ok), null);
                }
                break;
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "-----OnResume-----");
        this.presenter.setView(this);
        //m1ScannerManager.initializeScanner();
        //m1ScannerManager.attachBroadcastListener();
        registerBroadcastReceiver();
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "----OnPause----");
      getActivity().unregisterReceiver(myBroadcastReceiver);
        //m1ScannerManager.unRegisterBroadcastReceiver();
        //m1ScannerManager.unInitializeScanner();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        presenter.rxUnsubscribe();
        ///m1ScannerManager.unInitializeScanner();
        //m1ScannerManager.cleanUp();
    }

    private void registerBroadcastReceiver () {
        IntentFilter filter = new IntentFilter();
        filter.addCategory(Intent.CATEGORY_DEFAULT);
        filter.addAction(getString(R.string.activity_intent_filter_action));
        //LocalBroadcastManager.getInstance(getActivity()).registerReceiver(myBroadcastReceiver, filter);
        getActivity().registerReceiver(myBroadcastReceiver, filter);
    }

    @Override
    public String getMaterialNumber() {
        return materialNumberField.getText().toString().trim();
    }

    @Override
    public String getQuantity() {
        return quantityField.getText().toString().trim();
    }

    public String getNumberOfLabels() {
        return numberOfLabels.getText().toString().trim();
    }

    public Bundle getPrinterData() {
        return bundle;
    }

    public void setPrinterData(Bundle bundle) {
        this.bundle = bundle;
    }

    private void clearFields() {
        materialNumberField.setText("");
        numberOfLabels.setText("");
        quantityField.setText("");
        materialNumberField.requestFocus();
    }

    private void refreshLines () {
        ArrayAdapter<String> arrayAdapter =  (ArrayAdapter)lineSpinner.getAdapter();
        int position = arrayAdapter.getPosition(prefs.getLine());
        lineSpinner.setSelection(position);
    }

    public void updateViews () {
        refreshLines();
    }

    public void resetFields() {
        numberOfLabels.setText("");
        quantityField.setText("");
        materialNumberField.requestFocus();

        refreshLines();

        setPrinterData(null);

        if (m1Adapter != null) {
            m1Adapter.clearAdapter();
        }
    }

    private void displayPalletLabel(MIResult result) {
        if (result != null) {
            Bundle bundle = new Bundle();
            bundle.putSerializable(Config.Extra.MI_RESULT_PARAM, result);
            setPrinterData(bundle);

            m1Adapter = new M1Adapter();
            m1Adapter.addRecord(result.getMIRecord());

            //Drawable drawable = ContextCompat.getDrawable(MainActivity.this, R.drawable.divider_line);
            DividerItemDecoration dividerItemDecoration = new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL);
            recyclerView.addItemDecoration(dividerItemDecoration);
            recyclerView.setAdapter(m1Adapter);
            recyclerView.addOnItemTouchListener(new RecyclerViewTouchListener(getContext(), recyclerView,
                    new RecyclerViewTouchListener.OnItemClickListener() {
                        @Override
                        public void onItemClick(View view, int position) {

                        }

                        @Override
                        public void onItemLongClick(View view, int position) {

                        }
                    }));
        }
    }

    @Override
    public void showProgressView() {
        progressLayout.setVisibility(View.VISIBLE);
    }

    @Override
    public void hideProgressView() {
        progressLayout.setVisibility(View.GONE);
    }

    @Override
    public void onError(int resourceId, String message, String errorType) {

        hideProgressView();

        if (!TextUtils.isEmpty(message)) {
            Utils.showAlertDialog(getActivity(),
                    getString(R.string.error), R.drawable.ic_report_problem_black,
                    message,
                    getString(R.string.ok), null);
        } else {
            Utils.showAlertDialog(getActivity(),
                    getString(R.string.error), R.drawable.ic_report_problem_black,
                    getString(resourceId),
                    getString(R.string.ok), null);
        }


    }

    @Override
    public void onSerialNumberComplete(MIResult result) {
        hideProgressView();
        if (result != null) {
            if (result.getMessage() != null) {
                Utils.showAlertDialog(getActivity(),
                        getString(R.string.error),
                        R.drawable.ic_report_problem_black,
                        result.getMessage(),
                        getString(R.string.ok),
                        null);
            } else {
                Log.d(TAG, "Do we have a serial number?" + result.getSerialNumber());

                getPrinterData().putString(Config.Extra.SERIAL_NUMBER_PARAM, result.getSerialNumber());
                getPrinterData().putString(Config.Extra.LOT_NUMBER_PARAM, result.getLotNumber());
                setPrinterData(getPrinterData());
                m1Adapter.addSerialNumber(result.getSerialNumber(), result.getLotNumber());
            }
        } else {
            Utils.showAlertDialog(getActivity(),
                    getString(R.string.error),
                    R.drawable.ic_report_problem_black,
                    getString(R.string.no_results_found),
                    getString(R.string.ok),
                    null);
        }

    }

//    @Override
//    public void onScanComplete(final String barcode, String labelType) {
//        Log.d(TAG, "OnScanComplete:" + barcode);
//
//        materialNumberField.post(new Runnable() {
//            @Override
//            public void run() {
//                hideKeyboard();
//                materialNumberField.setText("");
//                materialNumberField.setText(barcode);
//                //executeLabelSearch();
//            }
//        });
//
//    }

    @Override
    public void onLabelInformationComplete(MIResult result) {
        if (result != null) {
            displayPalletLabel(result);
            presenter.getSerialNumber();
        } else {
            Utils.showAlertDialog(getActivity(),
                    getString(R.string.error),
                    R.drawable.ic_report_problem_black,
                    getString(R.string.label_information_not_found),
                    getString(R.string.ok),
                    null);
        }
    }

    @Override
    public void onPrintSent(final int resourceId) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "Print Job has been sent!");
                Toast.makeText(getContext(), resourceId, Toast.LENGTH_LONG).show();
            }
        });
    }

    @Override
    public void onPrinterError(final int resourceId) {
        getActivity().runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Log.d(TAG, "A print error occurred: " + getString(resourceId));
                Utils.showAlertDialog(getContext(),
                        getString(R.string.printer_error), R.drawable.ic_report_problem_black,
                        getString(resourceId), getString(R.string.ok), null);
            }
        });

    }

    private void displayScanResult(Intent initiatingIntent, String howDataReceived) {
        String decodedSource = initiatingIntent.getStringExtra(getResources().getString(R.string.datawedge_intent_key_source));
        String decodedData = initiatingIntent.getStringExtra(getResources().getString(R.string.datawedge_intent_key_data));
        //String decodedLabelType = initiatingIntent.getStringExtra(getResources().getString(R.string.datawedge_intent_key_label_type));
        Log.d(TAG, "Decoded Output:" + decodedData);
        if (null == decodedSource) {
            //decodedSource = initiatingIntent.getStringExtra(getResources().getString(R.string.datawedge_intent_key_source_legacy));
            decodedData = initiatingIntent.getStringExtra(getResources().getString(R.string.datawedge_intent_key_data_legacy));
            Log.d(TAG, "Decoded Output:" + decodedData);
            //decodedLabelType = initiatingIntent.getStringExtra(getResources().getString(R.string.datawedge_intent_key_label_type_legacy));
        }
        hideKeyboard();
        materialNumberField.setText("");
        materialNumberField.setText(decodedData);

    }

    private BroadcastReceiver myBroadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.d(TAG, "Intent Action is:" + action);
            if (action.equals(getResources().getString(R.string.activity_intent_filter_action))) {
                //  Received a barcode scan
                try {
                    displayScanResult(intent, "via Broadcast");
                } catch (Exception e) {
                    //  Catch if the UI does not exist when we receive the broadcast... this is not designed to be a production app
                }
            }
        }
    };
}
