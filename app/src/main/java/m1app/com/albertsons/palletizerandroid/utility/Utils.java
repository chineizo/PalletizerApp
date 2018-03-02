package m1app.com.albertsons.palletizerandroid.utility;


import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.ApplicationInfo;
import android.support.annotation.NonNull;
import android.text.TextUtils;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import m1app.com.albertsons.palletizerandroid.M1Application;
import m1app.com.albertsons.palletizerandroid.config.Config;


public class Utils {

    public static String TODAYS_DATE_AND_TIME = "yyyy-MM-dd HH:mm:ss z";
    public static String TODAYS_DATE = "yyyy-MM-dd";
    public static String TODAYS_DATE_1 = "yyyyMMdd";

    public static void showAlertDialog(Context context, final String title,
                                       final String message, final String okMessage, final Runnable okTask,
                                       final String cancelMessage, final Runnable cancelTask) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setCancelable(false);
        if (!TextUtils.isEmpty(title)) {
            builder.setTitle(title);
        }

        if (!TextUtils.isEmpty(message)) {
            builder.setMessage(message);
        }

        if (!TextUtils.isEmpty(okMessage)) {
            builder.setPositiveButton(okMessage, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (okTask != null) {
                        okTask.run();
                    }
                    dialog.dismiss();
                }
            });
        }

        if (!TextUtils.isEmpty(cancelMessage)) {
            builder.setNegativeButton(cancelMessage, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (cancelTask != null) {
                        cancelTask.run();
                    }

                    dialog.cancel();
                }
            });
        }

        builder.show();
    }

    public static void showAlertDialog(Context context, final String title,
                                       final String message, final String okMessage, final Runnable okTask) {
        showAlertDialog(context, title, message, okMessage, okTask, null, null);
    }

    public static void showAlertDialog(Context context, final String title, final int iconResourceId,
                                       final String message, final String okMessage, final Runnable okTask,
                                       final String cancelMessage, final Runnable cancelTask) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        initializeAlertDialog(builder, context, title, iconResourceId, message, okMessage, okTask, cancelMessage, cancelTask);
    }

    public static void showAlertDialog(Context context, final String title, final int resourceId,
                                       final String message, final String okMessage, final Runnable okTask) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        initializeAlertDialog(builder, context, title, resourceId, message, okMessage, okTask, null, null);
    }

    private static void initializeAlertDialog(AlertDialog.Builder builder, Context context, final String title, final int resourceId,
                                              final String message, final String okMessage, final Runnable okTask,
                                              final String cancelMessage, final Runnable cancelTask) {
        builder.setCancelable(false);
        if (resourceId != 0) {
            builder.setIcon(resourceId);
        }

        if (!TextUtils.isEmpty(title)) {
            builder.setTitle(title);
        }

        if (!TextUtils.isEmpty(message)) {
            builder.setMessage(message);
        }

        if (!TextUtils.isEmpty(okMessage)) {
            builder.setPositiveButton(okMessage, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (okTask != null) {
                        okTask.run();
                    }
                    dialog.dismiss();
                }
            });
        }

        if (!TextUtils.isEmpty(cancelMessage)) {
            builder.setNegativeButton(cancelMessage, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (cancelTask != null) {
                        cancelTask.run();
                    }

                    dialog.cancel();
                }
            });
        }

        builder.show();
    }

    public static final String getBaseUrl(@Config.Environment String key) {
        HashMap<String, String> api = new HashMap<>();
        api.put(Config.Environment.DEV, Config.Api.DEV_URL);
        api.put(Config.Environment.QA, Config.Api.QA_URL);
        api.put(Config.Environment.PROD, Config.Api.PROD_URL);
        return api.get(key);
    }

    public static boolean isDebuggable () {
        boolean isDebug = ((M1Application.getApplication().getApplicationInfo().flags &
                ApplicationInfo.FLAG_DEBUGGABLE) != 0);
        return isDebug;
    }

    public static String getTime(@NonNull String pattern) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(pattern, Locale.getDefault());
        return simpleDateFormat.format(new Date());
    }

    public static String addDays (@NonNull String patten, int days) {
        SimpleDateFormat simpleDateFormat = new SimpleDateFormat(patten);
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.DATE, days);
        return simpleDateFormat.format(calendar.getTime());

    }



//    private void pullParser() {
//        XmlPul//        materialNumberField.addTextChangedListener(new TextWatcher() {
//            @Override
//            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//                Log.d(TAG, "Before Text Changed:" + charSequence);
//            }
//
//            @Override
//            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
//                Log.d(TAG, "OnTextChanged:" + charSequence);
//            }
//
//            @Override
//            public void afterTextChanged(Editable editable) {
//                Log.d(TAG, "AfterTextChanged:" + editable.toString());
//            }
//        });lParserFactory pullParserFactory;
//        try {
//            pullParserFactory = XmlPullParserFactory.newInstance();
//            XmlPullParser pullParser = pullParserFactory.newPullParser();
//            pullParser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, false);
//            URL xml = new URL("");
//            //pullParser.setInput(xml.openStream(), null);
//            InputStream is = getAssets().open("xlm");
//            pullParser.setInput(is, null);
//            processParsing(pullParser);
//
//        } catch (XmlPullParserException e) {
//
//        } catch (IOException e) {
//
//        }
//    }


//    private void processParsing(XmlPullParser pullParser) throws IOException, XmlPullParserException {
//        ArrayList<String> players = new ArrayList<>();
//        int eventType = pullParser.getEventType();
//        String currentPlayer = null;
//        while (eventType != XmlPullParser.END_DOCUMENT) {
//            String eltName = null;
//            switch (eventType) {
//                case XmlPullParser.START_DOCUMENT:
//                    players = new ArrayList<>();
//                    break;
//                case XmlPullParser.START_TAG:
//                    eltName = pullParser.getName();
//                    if ("player".equals(eltName)) {
//                        currentPlayer = new String();
//                    } else if (currentPlayer != null) {
//                        if ("name".equals("")) {
//                            pullParser.nextText();
//                        }
//                    }
//            }
//            eventType = pullParser.next();
//        }
//
//    }

}
