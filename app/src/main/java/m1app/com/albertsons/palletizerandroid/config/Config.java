package m1app.com.albertsons.palletizerandroid.config;

import android.support.annotation.StringDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * Created by chinedu on 12/30/17.
 */

public class Config {

    @StringDef({Api.DEV_URL, Api.QA_URL, Api.PROD_URL})
    @Retention(RetentionPolicy.RUNTIME)
    public @interface Api {
        String DEV_URL = "https://albertsonsdev-bel1.cloud.infor.com:63906/m3api-rest/";
        String QA_URL = "https://albertsonsqa-bel1.cloud.infor.com:63906/m3api-rest/";
        String PROD_URL = "https://albertsonsprod-bel1.cloud.infor.com:63906/m3api-rest/";
    }

    @StringDef({Environment.DEV, Environment.QA, Environment.PROD})
    @Retention(RetentionPolicy.RUNTIME)
    public @interface Environment {
        String QA = "QA";
        String DEV = "DEV";
        String PROD = "PROD";
    }


    @StringDef({Line.A, Line.B, Line.C})
    @Retention(RetentionPolicy.RUNTIME)
    public @interface Line {
        String A = "A";
        String B = "B";
        String C = "C";
    }


    @StringDef({ErrorType.FATAL, ErrorType.MEDIUM})
    public @interface ErrorType {
        String FATAL = "fatal";
        String MEDIUM = "medium";
    }

    @StringDef({Field.VHFACI, Field.VHPRNO, Field.VHMFNO, Field.MMFUDS, Field.VHBANO, Field.VHMAUN, Field.MBSLDY, Field.VHMAQT, Field.LMMFDT})
    public @interface Field {
        String VHFACI = "VHFACI";
        String VHPRNO = "VHPRNO";
        String VHMFNO = "VHMFNO";
        String MMFUDS = "MMFUDS";
        String VHBANO = "VHBANO";
        String VHMAUN = "VHMAUN";
        String MBSLDY = "MBSLDY";
        String VHMAQT = "VHMAQT";
        String LMMFDT = "LMMFDT";
    }

    @StringDef({InputType.KEYBOARD, InputType.SCANNER})
    public @interface InputType {
        String KEYBOARD = "Keyboard";
        String SCANNER = "Scanner";
    }

    public interface Extra {
        String QUANTITY_PARAM = "QUANTITY";
        String MATERIAL_NUMBER_PARAM = "MATERIAL_NUMBER";
        String NUMBER_OF_LABELS_PARAM = "NUMBER_OF_LABELS";
        String LOT_NUMBER_PARAM = "LOT_NUMBER";
        String MI_RESULT_PARAM = "MI_RESULT";
        String PRINT_JOB_PARAM = "PRINT_JOB";
        String SERIAL_NUMBER_PARAM = "SERIAL_NUMBER";
        String ACTION_CLEAR_SCREEN = "ACTION_CLEAR_SCREEN";

        String SCANNER_BROADCAST_ACTION = "albertsons.com.m1app.RECVR";
        String MOTOROLA_DATA_WEDGE_EXTRA = "com.motorolasolutions.emdk.datawedge.source";
        String MOTOROLA_DATA_WEDGE_EXTRA_STRING = "com.motorolasolutions.emdk.datawedge.data_string";

    }

}
