package m1app.com.albertsons.palletizerandroid.fragment;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TableRow;
import android.widget.Toast;

import java.util.Arrays;
import java.util.List;

import m1app.com.albertsons.palletizerandroid.R;
import butterknife.BindView;
import butterknife.ButterKnife;
import m1app.com.albertsons.palletizerandroid.activity.MainActivity;
import m1app.com.albertsons.palletizerandroid.pojo.LineCode;
import m1app.com.albertsons.palletizerandroid.pojo.Setting;
import m1app.com.albertsons.palletizerandroid.utility.Prefs;
import m1app.com.albertsons.palletizerandroid.utility.Utils;

/**
 * Created by chinedu on 12/29/17.
 */

public class SettingFragment extends BaseFragment {

    private final String TAG = SettingFragment.class.getSimpleName();

    @BindView(R.id.facility_code)
    TextInputEditText facilityCode;

    @BindView(R.id.company_number)
    TextInputEditText companyNumber;

    @BindView(R.id.lines_spinner)
    Spinner lineSpinner;

    @BindView(R.id.environment_spinner)
    Spinner environmentSpinner;

    @BindView(R.id.printer_address)
    TextInputEditText printerIpAddress;

    @BindView(R.id.printer_name)
    TextInputEditText printerName;

    @BindView(R.id.printer_description)
    TextInputEditText printerDescription;

    @BindView(R.id.number_series_type)
    TextInputEditText numberSeriesType;

    @BindView(R.id.number_series)
    TextInputEditText numberSeries;

    @BindView(R.id.environment_spinner_row)
    TableRow environmentSpinnerRow;

    private Prefs prefs;

    public SettingFragment() {
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setHasOptionsMenu(true);
        prefs = new Prefs(getContext());
    }


    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.setting_fragment_layout, container, false);
        ButterKnife.bind(this, view);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        registerForSoftKeyBoardControl(view);
        populateFields();
        if (Utils.isDebuggable()) {
            initializeEnvironmentSelector();
            enableViews(true);
        }
        initializeLines();
    }

    @Override
    public void showProgressView() {
    }

    @Override
    public void hideProgressView() {
    }

    private void initializeEnvironmentSelector() {
        environmentSpinnerRow.setVisibility(View.VISIBLE);
        List<String> environments = Arrays.asList(getResources()
                .getStringArray(R.array.spinner_data));


        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(getContext(),
                android.R.layout.simple_spinner_item, environments);
        arrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        environmentSpinner.setAdapter(arrayAdapter);
        final String env = prefs.getEnvironment();
        int selectedPosition = arrayAdapter.getPosition(env);
        environmentSpinner.setSelection(selectedPosition);
        environmentSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
                String environment = adapterView.getItemAtPosition(position).toString();
                prefs.setEnvironment(environment);
                Log.d(TAG, "ENVIRONMENT SELECTED:" + environment);
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

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

                LineCode lineCode = prefs.getLineCode(line);
                numberSeries.setText(lineCode.getNumberSeries());
                numberSeriesType.setText(lineCode.getNumberSeriesType());
                printerIpAddress.setText(lineCode.getIpAddress());
                printerDescription.setText(lineCode.getDescription());
                printerName.setText(lineCode.getPrinterName());
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

    }

//    private void initializeNumberSeries () {
//        List<String> numberSeries = Arrays.asList(getResources().getStringArray(R.array.number_series));
//        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(getContext(),
//                android.R.layout.simple_spinner_item, numberSeries);
//        numberSeriesSpinner.setAdapter(arrayAdapter);
//        String type = prefs.getNumberSeries();
//        int position = arrayAdapter.getPosition(type);
//        numberSeriesSpinner.setSelection(position);
//        numberSeriesSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
//            @Override
//            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long l) {
//                String type = adapterView.getItemAtPosition(position).toString();
//                prefs.setNumberSeries(type);
//            }
//
//            @Override
//            public void onNothingSelected(AdapterView<?> adapterView) {
//
//            }
//        });
//
//    }


    private void populateFields() {
        Setting setting = prefs.getSetting();
        facilityCode.setText(setting.getFacilityCode());
        companyNumber.setText(setting.getCompanyNumber());
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        super.onCreateOptionsMenu(menu, inflater);
        inflater.inflate(R.menu.setting_menu, menu);
    }

    private boolean validatePassword(String password) {
        return password.equals(prefs.getSettingsPassword());
    }

    private boolean validateAdminPassword(String password) {
        return password.equals(prefs.getAdminPassword());
    }

    public void enableViews(boolean isVisible) {
        companyNumber.setEnabled(isVisible);
        facilityCode.setEnabled(isVisible);
        numberSeries.setEnabled(isVisible);
        numberSeriesType.setEnabled(isVisible);
        printerIpAddress.setEnabled(isVisible);
        printerName.setEnabled(isVisible);
        printerDescription.setEnabled(isVisible);
    }

    public void requestAccessLoging() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
        View view = getLayoutInflater().inflate(R.layout.settings_login, null);
        builder.setView(view);
        final TextInputEditText passwordField = view.findViewById(R.id.password);
//        passwordField.setOnEditorActionListener(new TextView.OnEditorActionListener() {
//            @Override
//            public boolean onEditorAction(TextView textView, int i, KeyEvent keyEvent) {
//                return false;
//            }
//        });
        builder.setIcon(R.drawable.ic_vpn_key_black);
        builder.setTitle(R.string.access);

        builder.setCancelable(false)
                .setPositiveButton(R.string.ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        String password = passwordField.getText().toString().trim();
                        if (validateAdminPassword(password)) {
                            initializeEnvironmentSelector();
                            enableViews(true);
                        } else if (!validatePassword(password)) {
                            Toast.makeText(getContext(), R.string.see_supervisor, Toast.LENGTH_SHORT).show();
                            MainActivity mainActivity = (MainActivity) getActivity();
                            mainActivity.accessToChangeSettingsDenied();
                        } else {
                            enableViews(true);
                        }


                        dialogInterface.dismiss();
                    }
                }).setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.dismiss();
                MainActivity mainActivity = (MainActivity) getActivity();
                mainActivity.accessToChangeSettingsDenied();
            }
        });
        builder.show();

    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.save:
                Setting setting = prefs.getSetting();
                if (setting != null) {
                    setting.setCompanyNumber(companyNumber.getText().toString());
                    setting.setFacilityCode(facilityCode.getText().toString());
                    prefs.setSetting(setting);

                    LineCode lineCode = new LineCode();
                    lineCode.setDescription(printerDescription.getText().toString().trim());
                    lineCode.setIpAddress(printerIpAddress.getText().toString().trim());
                    lineCode.setNumberSeries(numberSeries.getText().toString().trim());
                    lineCode.setNumberSeriesType(numberSeriesType.getText().toString().trim());
                    lineCode.setPrinterName(printerName.getText().toString().trim());
                    prefs.setLineCode((String) lineSpinner.getSelectedItem(), lineCode);

                    Toast.makeText(getContext(), R.string.settings_saved, Toast.LENGTH_SHORT).show();
                }
                break;
            case R.id.edit:
                requestAccessLoging();
                break;
        }

        return super.onOptionsItemSelected(item);
    }
}
