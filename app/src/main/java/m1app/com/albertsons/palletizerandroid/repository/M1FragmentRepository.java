package m1app.com.albertsons.palletizerandroid.repository;
import m1app.com.albertsons.palletizerandroid.R;
import m1app.com.albertsons.palletizerandroid.pojo.MIRecord;
import m1app.com.albertsons.palletizerandroid.pojo.MIResult;
import rx.Observable;

public interface M1FragmentRepository {
    Observable<MIRecord> updateLotNumber (String materialNumber);
    Observable<MIResult> getLabelInformation (String materialNumber);
    Observable<MIResult> getSerialNumber(int numberOfLabelsToPrint);
}
