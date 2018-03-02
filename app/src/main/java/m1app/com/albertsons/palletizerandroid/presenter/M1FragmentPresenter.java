package m1app.com.albertsons.palletizerandroid.presenter;


import android.text.TextUtils;
import android.util.Log;

import javax.annotation.Nullable;

import m1app.com.albertsons.palletizerandroid.M1Application;
import m1app.com.albertsons.palletizerandroid.R;
import m1app.com.albertsons.palletizerandroid.config.Config;
import m1app.com.albertsons.palletizerandroid.listener.M1FragmentMVP;
import m1app.com.albertsons.palletizerandroid.pojo.LineCode;
import m1app.com.albertsons.palletizerandroid.pojo.MIRecord;
import m1app.com.albertsons.palletizerandroid.pojo.MIResult;
import m1app.com.albertsons.palletizerandroid.utility.Prefs;
import rx.Observer;
import rx.Subscription;
import rx.android.schedulers.AndroidSchedulers;
import rx.schedulers.Schedulers;

public class M1FragmentPresenter implements M1FragmentMVP.Presenter {

    private final String TAG = M1FragmentPresenter.class.getSimpleName();

    private M1FragmentMVP.Model model;
    private Subscription subscription;

    @Nullable
    private M1FragmentMVP.View view;

    public M1FragmentPresenter(M1FragmentMVP.Model model) {
        this.model = model;
    }

    @Override
    public void setView(M1FragmentMVP.View view) {
        this.view = view;
    }

    @Override
    public void rxUnsubscribe() {
        if (subscription != null) {
            if (!subscription.isUnsubscribed()) {
                subscription.unsubscribe();
                Log.d(TAG, "---RX Unsubscribe called---");
            }
        }
    }

    private boolean validateInputParams () {
        if (view != null) {
            Prefs prefs = new Prefs(M1Application.getApplication());
            LineCode lineCode = prefs.getLineCode(prefs.getLine());
            if (TextUtils.isEmpty(lineCode.getIpAddress())) {
                view.onError(R.string.configure_ip_address, null, Config.ErrorType.MEDIUM);
                return true;
            } else if (TextUtils.isEmpty(lineCode.getNumberSeries())) {
                view.onError(R.string.configure_number_series, null, Config.ErrorType.MEDIUM);
                return true;
            } else if (TextUtils.isEmpty(lineCode.getNumberSeriesType())) {
                view.onError(R.string.configure_number_series_type, null, Config.ErrorType.MEDIUM);
                return true;
            }
        }
        return false;
    }

    @Override
    public void getLabelInformation() {
        if (view != null) {
            if (TextUtils.isEmpty(view.getMaterialNumber())) {
                view.onError(R.string.material_number_error, null, Config.ErrorType.MEDIUM);
            } else if (TextUtils.isEmpty(view.getQuantity())) {
                view.onError(R.string.quantity_error, null, Config.ErrorType.MEDIUM);
            } else if (TextUtils.isEmpty(view.getNumberOfLabels())) {
                view.onError(R.string.number_of_labels_required, null, Config.ErrorType.MEDIUM);
            } else if (validateInputParams()){

            } else {
                subscription = model
                        .getLabelInformation(view.getMaterialNumber())
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Observer<MIResult>() {
                            @Override
                            public void onCompleted() {
                            }

                            @Override
                            public void onError(Throwable e) {
                                if (view != null) {
                                    if ("iterable must not be null".equals(e.getMessage().trim())) {
                                        view.onError(0, "No results found!", Config.ErrorType.FATAL);
                                    } else {
                                        view.onError(0, e.getMessage(), Config.ErrorType.FATAL);
                                    }
                                }
                            }

                            @Override
                            public void onNext(MIResult result) {
                                if (view != null) {
                                    view.onLabelInformationComplete(result);
                                }
                            }
                        });
            }
        }
    }

    @Override
    public void getSerialNumber() {
        if (view != null) {
            if (TextUtils.isEmpty(view.getMaterialNumber())) {
                view.onError(R.string.material_number_error, null, Config.ErrorType.MEDIUM);
            } else if (TextUtils.isEmpty(view.getQuantity())) {
                view.onError(R.string.quantity_error, null, Config.ErrorType.MEDIUM);
            } else if (TextUtils.isEmpty(view.getNumberOfLabels())) {
                view.onError(R.string.number_of_labels_required, null, Config.ErrorType.MEDIUM);
            } else if (validateInputParams()) {
            } else {
                subscription = model.getSerialNumber(Integer.valueOf(view.getNumberOfLabels()))
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Observer<MIResult>() {
                            @Override
                            public void onCompleted() {
                                Log.d(TAG, "Serial Number has been retrieved");
                            }

                            @Override
                            public void onError(Throwable e) {
                                Log.e(TAG, "Error occurred retrieving serial number:" + e.getMessage());
                                if (view != null) {
                                    view.onError(0, e.getMessage(), Config.ErrorType.MEDIUM);
                                }
                            }

                            @Override
                            public void onNext(MIResult result) {
                                if (view != null) {
                                    //view.onSerialNumberComplete(result);
                                    Log.d(TAG, "Serial Number Completed");
                                    updateLotNumber(result);
                                }
                            }
                        });
            }
        }

    }

    @Override
    public void updateLotNumber(final MIResult miResult) {
        subscription = model.updateLotNumber(view.getMaterialNumber())
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<MIRecord>() {
                    @Override
                    public void onCompleted() {
                        Log.d(TAG, "Updated lot number");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.e(TAG, "Error occurred retrieving updating lot number" + e.getMessage());
                        if (view != null) {
                            view.onError(0, e.getMessage(), Config.ErrorType.MEDIUM);
                        }
                    }

                    @Override
                    public void onNext(MIRecord result) {
                        if (view != null) {
                            Log.d(TAG, "Lot number updated:" + result);
                            miResult.setLotNumber(result.getLotNumber());
                            view.onSerialNumberComplete(miResult);
                        }
                    }
                });
    }
}
