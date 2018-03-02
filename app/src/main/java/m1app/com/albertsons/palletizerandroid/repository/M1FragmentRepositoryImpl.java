package m1app.com.albertsons.palletizerandroid.repository;

import android.text.TextUtils;
import android.util.Log;

import org.apache.commons.lang3.StringUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;


import m1app.com.albertsons.palletizerandroid.M1Application;
import m1app.com.albertsons.palletizerandroid.config.Config;
import m1app.com.albertsons.palletizerandroid.pojo.LineCode;
import m1app.com.albertsons.palletizerandroid.pojo.MIRecord;
import m1app.com.albertsons.palletizerandroid.pojo.MIResult;
import m1app.com.albertsons.palletizerandroid.pojo.NameValue;
import m1app.com.albertsons.palletizerandroid.pojo.Setting;
import m1app.com.albertsons.palletizerandroid.service.ApiService;
import m1app.com.albertsons.palletizerandroid.service.ApiServiceDev;
import m1app.com.albertsons.palletizerandroid.service.ApiServiceQA;
import m1app.com.albertsons.palletizerandroid.utility.Prefs;
import m1app.com.albertsons.palletizerandroid.utility.Utils;
import rx.Observable;
import rx.functions.Func1;

public class M1FragmentRepositoryImpl implements M1FragmentRepository {
    private final String TAG = M1FragmentRepositoryImpl.class.getSimpleName();
    private final static String MAX_RECORDS = ";maxrecs=2";
    private ApiService apiService;
    private ApiServiceDev apiServiceDev;
    private ApiServiceQA apiServiceQA;

    private Setting setting;
    private Prefs prefs;
    private String vmmaqt = StringUtils.EMPTY;
    private String lmmfdt = StringUtils.EMPTY;
    private String lotNumber = StringUtils.EMPTY;

    public M1FragmentRepositoryImpl(ApiService apiService, ApiServiceDev apiServiceDev, ApiServiceQA apiServiceQA) {
        this.apiService = apiService;
        this.apiServiceDev = apiServiceDev;
        this.apiServiceQA = apiServiceQA;
        prefs  = new Prefs(M1Application.getApplication());
        setting = prefs.getSetting();
    }

    public Observable<String> getProgramNumber(String manufacturingNumber) {

        Map<String, String> queryParams1 = new HashMap<>();
        queryParams1.put("SQRY", "MFNO:" + manufacturingNumber);
        queryParams1.put("FACI", setting.getFacilityCode());

        if (Config.Environment.PROD.equals(prefs.getEnvironment())) {
            return apiService.getProductNumberObservable("PMS100MI", "SearchMO", MAX_RECORDS, queryParams1)
                    .flatMap(new Func1<MIResult, Observable<MIRecord>>() {
                        @Override
                        public Observable<MIRecord> call(MIResult miResult) {
                            return Observable.from(miResult.getMIRecord());
                        }
                    }).flatMap(new Func1<MIRecord, Observable<String>>() {
                        @Override
                        public Observable<String> call(MIRecord miRecord) {
                            return Observable.just(miRecord.getNameValue().get(0).getValue()
                            );
                        }
                    });
        } else if (Config.Environment.QA.equals(prefs.getEnvironment())) {
            return apiServiceQA.getProductNumberObservable("PMS100MI", "SearchMO", MAX_RECORDS, queryParams1)
                    .flatMap(new Func1<MIResult, Observable<MIRecord>>() {
                        @Override
                        public Observable<MIRecord> call(MIResult miResult) {
                            return Observable.from(miResult.getMIRecord());
                        }
                    }).flatMap(new Func1<MIRecord, Observable<String>>() {
                        @Override
                        public Observable<String> call(MIRecord miRecord) {
                            return Observable.just(miRecord.getNameValue().get(0).getValue()
                            );
                        }
                    });
        } else if (Config.Environment.DEV.equals(prefs.getEnvironment())) {
             return apiServiceDev.getProductNumberObservable("PMS100MI", "SearchMO", MAX_RECORDS, queryParams1)
                    .flatMap(new Func1<MIResult, Observable<MIRecord>>() {
                        @Override
                        public Observable<MIRecord> call(MIResult miResult) {
                            return Observable.from(miResult.getMIRecord());
                        }
                    }).flatMap(new Func1<MIRecord, Observable<String>>() {
                        @Override
                        public Observable<String> call(MIRecord miRecord) {
                            return Observable.just(miRecord.getNameValue().get(0).getValue()
                            );
                        }
                    });
        }

        return null;
    }

    @Override
    public Observable<MIRecord> updateLotNumber (final String manufacturingNumber){

        return lookup(manufacturingNumber)
                .flatMap(new Func1<MIResult, Observable<MIRecord>>() {
            @Override
            public Observable<MIRecord> call(MIResult miResult) {
                final MIRecord miRecord = miResult.getMIRecord().get(0);
                List<NameValue> nameValues = miRecord.getNameValue();

                for (NameValue name : nameValues) {
                    if (Config.Field.VHMAQT.equals(name.getName())) {
                        vmmaqt = name.getValue().trim();
                    } else if (Config.Field.LMMFDT.equals(name.getName())) {
                        lmmfdt = name.getValue().trim();
                    } else if (Config.Field.VHBANO.equals(name.getName())) {
                        lotNumber = name.getValue().trim();
                    }
                }

                try {
                    if (!TextUtils.isEmpty(vmmaqt) && !TextUtils.isEmpty(lmmfdt)) {
                        double vmmqatToInteger = Double.valueOf(vmmaqt);
                        if (vmmqatToInteger > 0.0) {
                            //compare lmmfdt to today's date
                            String todaysDate = Utils.getTime(Utils.TODAYS_DATE_1);
                            if (!lmmfdt.equals(todaysDate)) {
                                //find index of '-'
                                int position = lotNumber.lastIndexOf("-");
                                if (position > -1) {
                                    //it found a -
                                    String[] split = lotNumber.split("-");
                                    if (split.length > 0) {
                                        String appendedNumber = split[1].trim();
                                        int appendedNumberToInteger = Integer.valueOf(appendedNumber) + 1;
                                        lotNumber = split[0] + "-" + appendedNumberToInteger;
                                    }
                                } else {
                                    lotNumber = lotNumber.concat("-1");
                                }

                                return updateLot(manufacturingNumber, lotNumber)
                                        .flatMap(new Func1<MIResult, Observable<MIRecord>>() {
                                    @Override
                                    public Observable<MIRecord> call(MIResult miResult) {
                                        MIRecord record = null;
                                        if (miResult.getMIRecord() != null) {
                                            record = miResult.getMIRecord().get(0);
                                            record.setLotNumber(lotNumber); //grabs the new lot number

                                            Log.d(TAG, "We got the record:" + record);
                                        }
                                        return Observable.just(record);
                                    }
                                });
                            }
                        }
                    }
                } catch (Exception e) {
                    Log.d(TAG, "Exception occurred parsing value:",e);
                }
                return Observable.just(miRecord);
            }
        });
    }

    public Observable<MIResult> lookup (final String manufacturingNumber) {
     return getLabelInformation(manufacturingNumber);
    }


    public Observable<MIResult> updateLot (final String manufacturingNumber, final String lotNumber) {
        Map<String, String> queryParams = new HashMap<>();
        queryParams.put("MFNO", manufacturingNumber);
        queryParams.put("BANO", lotNumber);
        queryParams.put("CONO", setting.getCompanyNumber());
        queryParams.put("FACI", setting.getFacilityCode());
        queryParams.put("DSP1","1" );
        queryParams.put("DSP2","1" );
        queryParams.put("DSP3","1" );
        queryParams.put("DSP4","1" );

        if (Config.Environment.PROD.equals(prefs.getEnvironment())) {
            return apiService.updateLotNumberObservable("PMS050MI", "RptReceipt",MAX_RECORDS, queryParams);
        } else if (Config.Environment.QA.equals(prefs.getEnvironment())) {
            return apiServiceQA.updateLotNumberObservable("PMS050MI", "RptReceipt",MAX_RECORDS, queryParams);
        } else if (Config.Environment.DEV.equals(prefs.getEnvironment())) {
            return apiServiceDev.updateLotNumberObservable("PMS050MI", "RptReceipt",MAX_RECORDS, queryParams);
        }
        return apiServiceDev.updateLotNumberObservable("PMS050MI", "RptReceipt",MAX_RECORDS, queryParams);
    }

    @Override
    public Observable<MIResult> getLabelInformation(final String manufacturingNumber) {
//        Map<String, String> queryParams1 = new HashMap<>();
//        queryParams1.put("SQRY", "MFNO:" + manufacturingNumber);
//        queryParams1.put("FACI", setting.getFacilityCode());

        return getProgramNumber(manufacturingNumber)
                .concatMap(new Func1<String, Observable<MIResult>>() {
                    @Override
                    public Observable<MIResult> call(String productNumber) {

                        final Map<String, String> queryParams = new HashMap<>();
                        queryParams.put("SQRY", "MFNO:" + manufacturingNumber);
                        queryParams.put("PRNO", productNumber);
                        queryParams.put("FACI", setting.getFacilityCode());

                        if (Config.Environment.PROD.equals(prefs.getEnvironment())) {
                            return apiService.getLabelInformationObservable("CMS100MI", "LstLabelInfo", MAX_RECORDS, queryParams);
                        } else if (Config.Environment.QA.equals(prefs.getEnvironment())) {
                            return apiServiceQA.getLabelInformationObservable("CMS100MI", "LstLabelInfo", MAX_RECORDS, queryParams);
                        } else if (Config.Environment.DEV.equals(prefs.getEnvironment())) {
                            return apiServiceDev.getLabelInformationObservable("CMS100MI", "LstLabelInfo", MAX_RECORDS, queryParams);
                        }

                        return null;
                    }
                });
    }

    @Override
    public Observable<MIResult> getSerialNumber(final int numberOfLabelsToPrint) {
        final Map<String,String> result = new HashMap<>();
        final Map<String, String> deleteMap = new HashMap<>();

        return getLastRecord(numberOfLabelsToPrint)
                .flatMap(new Func1<MIResult, Observable<String>>() {
                    @Override
                    public Observable<String> call(MIResult miResult) {
                        String serialNumber = null;
                        if (miResult != null) {
                            MIRecord miRecord = miResult.getMIRecord().get(0);

                            List<NameValue> nameValues = miRecord.getNameValue();
                            result.put("FILE", "PALLETIZER");
                            deleteMap.put("FILE", "PALLETIZER");

                            for (NameValue nameValue : nameValues) {
                                String name = nameValue.getName().trim();
                                String value = nameValue.getValue().trim();
                                if ("PK01".equals(name)) {
                                    result.put(name, value);
                                    deleteMap.put(name, value);
                                } else if ("PK02".equals(name)) {
                                    result.put(name, value);
                                    deleteMap.put(name, value);
                                } else if ("PK03".equals(name)) {
                                    result.put(name, value);
                                    deleteMap.put(name, value);
                                } else if ("PK04".equals(name)) {
                                    result.put(name, value);
                                    deleteMap.put(name, value);
                                } else if ("PK05".equals(name)) {
                                    result.put(name, value);
                                    deleteMap.put(name, value);
                                } else if ("PK06".equals(name)) {
                                    serialNumber = value;
                                    deleteMap.put(name, value);
                                    long number = Long.valueOf(value) + numberOfLabelsToPrint;
                                    result.put(name, String.valueOf(number));
                                } else if ("A130".equals(name)) {
                                    result.put(name, value);
                                } else if ("A121".equals(name)) {
                                    result.put(name, value);
                                }
                            }
                        }
                        return Observable.just(serialNumber);
                    }
        }).concatMap(new Func1<String, Observable<MIResult>>() {
                    @Override
                    public Observable<MIResult> call(final String serialNumber) {
                        Log.d(TAG, "Input from previous:" + serialNumber);
                        return updateLastRecordObservable(result)
                                .concatMap(new Func1<MIResult, Observable< MIResult>>() {
                            @Override
                            public Observable<MIResult> call(MIResult miResult) {
                                return Observable.just(miResult)
                                        .concatMap(new Func1<MIResult, Observable< MIResult>>() {
                                    @Override
                                    public Observable<MIResult> call(MIResult miResult) {

                                        if (miResult != null && miResult.getMessage() != null) {
                                            String message = miResult.getMessage();
                                            Log.d(TAG, "ErrorMessage occurred, now pass up:" + message);

                                            return Observable.just(miResult);
                                        }


                                        return deletePreviousRecordObservable(deleteMap)
                                                .concatMap(new Func1<MIResult, Observable<MIResult>>() {
                                            @Override
                                            public Observable<MIResult> call(MIResult miResult) {
                                                    //if deletion failed then exit with error
                                                    if (miResult != null && miResult.getMessage() != null) {
                                                        return Observable.just(miResult);
                                                    }
                                                    Log.d(TAG, "Now in the Delete Function to remove the record:");
                                                    miResult.setSerialNumber(serialNumber);
                                                return Observable.just(miResult);
                                            }
                                        });
                                    }
                                });
                            }
                        });
                    }
                });

    }

    private Observable<MIResult> getLastRecord(int numberOfLabelsToPrint) {
        Prefs prefs = new Prefs(M1Application.getApplication());
        Setting setting = prefs.getSetting();
        LineCode lineCode = prefs.getLineCode(prefs.getLine());
        Map<String, String> params = new HashMap<>();
        params.put("PK01", setting.getFacilityCode());
        params.put("PK02", lineCode.getNumberSeriesType());
        params.put("PK03", lineCode.getNumberSeries());
        params.put("FILE", "PALLETIZER");


        if (Config.Environment.PROD.equals(prefs.getEnvironment())) {
            return apiService.getLastRecordObservable("CUSEXTMI", "LstFieldValue", MAX_RECORDS, params);
        } else if (Config.Environment.QA.equals(prefs.getEnvironment())) {
            return apiServiceQA.getLastRecordObservable("CUSEXTMI", "LstFieldValue", MAX_RECORDS, params);
        } else  if (Config.Environment.DEV.equals(prefs.getEnvironment())) {
            return apiServiceDev.getLastRecordObservable("CUSEXTMI", "LstFieldValue", MAX_RECORDS, params);
        }
        return apiService.getLastRecordObservable("CUSEXTMI", "LstFieldValue", MAX_RECORDS, params);
    }


    private Observable<MIResult> updateLastRecordObservable (Map<String, String> params) {
        if (Config.Environment.PROD.equals(prefs.getEnvironment())) {
            return apiService.updateLastRecordObservable("CUSEXTMI", "AddFieldValue", MAX_RECORDS, params);
        }else if (Config.Environment.QA.equals(prefs.getEnvironment())) {
            return apiServiceQA.updateLastRecordObservable("CUSEXTMI", "AddFieldValue", MAX_RECORDS, params);
        }else  if (Config.Environment.DEV.equals(prefs.getEnvironment())) {
            return apiServiceDev.updateLastRecordObservable("CUSEXTMI", "AddFieldValue", MAX_RECORDS, params);
        }

        return apiService.updateLastRecordObservable("CUSEXTMI", "AddFieldValue", MAX_RECORDS, params);
    }


    private Observable<MIResult> deletePreviousRecordObservable (Map<String, String> params) {
        if (Config.Environment.PROD.equals(prefs.getEnvironment())) {
            return apiService.deletePreviousRecordObservable("CUSEXTMI","DelFieldValue", MAX_RECORDS, params);
        }else if (Config.Environment.QA.equals(prefs.getEnvironment())) {
            return apiServiceQA.deletePreviousRecordObservable("CUSEXTMI","DelFieldValue", MAX_RECORDS, params);
        }else  if (Config.Environment.DEV.equals(prefs.getEnvironment())) {
            return apiServiceDev.deletePreviousRecordObservable("CUSEXTMI","DelFieldValue", MAX_RECORDS, params);
        }
        return apiService.deletePreviousRecordObservable("CUSEXTMI","DelFieldValue", MAX_RECORDS, params);
    }
}


