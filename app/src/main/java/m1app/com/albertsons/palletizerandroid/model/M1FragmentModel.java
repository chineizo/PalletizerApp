package m1app.com.albertsons.palletizerandroid.model;

import m1app.com.albertsons.palletizerandroid.R;
import m1app.com.albertsons.palletizerandroid.listener.M1FragmentMVP;
import m1app.com.albertsons.palletizerandroid.pojo.MIRecord;
import m1app.com.albertsons.palletizerandroid.pojo.MIResult;
import m1app.com.albertsons.palletizerandroid.repository.M1FragmentRepository;
import rx.Observable;

public class M1FragmentModel implements M1FragmentMVP.Model {

    private M1FragmentRepository m1FragmentRepository;

    public M1FragmentModel(M1FragmentRepository repository) {
        this.m1FragmentRepository = repository;
    }

    @Override
    public Observable<MIResult> getLabelInformation(String materialNumber) {
        return m1FragmentRepository.getLabelInformation(materialNumber);
    }

    @Override
    public Observable<MIResult> getSerialNumber(int numberOfLabelsToPrint) {
        return m1FragmentRepository.getSerialNumber(numberOfLabelsToPrint);
    }

    @Override
    public Observable<MIRecord> updateLotNumber(String materialNumber) {
        return m1FragmentRepository.updateLotNumber(materialNumber);
    }
}
