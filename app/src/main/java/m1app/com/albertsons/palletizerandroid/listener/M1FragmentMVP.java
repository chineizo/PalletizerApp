package m1app.com.albertsons.palletizerandroid.listener;


import m1app.com.albertsons.palletizerandroid.pojo.MIRecord;
import m1app.com.albertsons.palletizerandroid.pojo.MIResult;
import rx.Observable;

public interface M1FragmentMVP {

    interface View {

        String getMaterialNumber();

        String getQuantity();

        String getNumberOfLabels();

        void onError(int resourceId, String message, String errorTypes);

        void onSerialNumberComplete(MIResult result);

        void onLabelInformationComplete(MIResult result);

        //void onUpdateLotNumber ();
    }

    interface Presenter {

        void setView(M1FragmentMVP.View view);

        void getLabelInformation();

        void getSerialNumber();

        void updateLotNumber(MIResult miResult);

        void rxUnsubscribe();
    }

    interface Model {

        Observable<MIResult> getLabelInformation(String materialNumber);

        Observable<MIResult> getSerialNumber(int numberOfLabelsToPrint);

        Observable<MIRecord> updateLotNumber (String materialNumber);
    }
}
