package m1app.com.albertsons.palletizerandroid.module;

import javax.inject.Singleton;
import m1app.com.albertsons.palletizerandroid.R;
import dagger.Module;
import dagger.Provides;
import m1app.com.albertsons.palletizerandroid.listener.M1FragmentMVP;
import m1app.com.albertsons.palletizerandroid.model.M1FragmentModel;
import m1app.com.albertsons.palletizerandroid.presenter.M1FragmentPresenter;
import m1app.com.albertsons.palletizerandroid.repository.M1FragmentRepository;
import m1app.com.albertsons.palletizerandroid.repository.M1FragmentRepositoryImpl;
import m1app.com.albertsons.palletizerandroid.service.ApiService;
import m1app.com.albertsons.palletizerandroid.service.ApiServiceDev;
import m1app.com.albertsons.palletizerandroid.service.ApiServiceQA;

@Module
public class M1FragmentModule {

    @Provides
    public M1FragmentMVP.Presenter provideM1FragmentPresenter(M1FragmentMVP.Model model) {
        return new M1FragmentPresenter(model);
    }

    @Provides
    public M1FragmentMVP.Model provideM1FragmentModel(M1FragmentRepository repository) {
        return new M1FragmentModel(repository);
    }

    @Singleton
    @Provides
    public M1FragmentRepository provideM1FragmentRepository(ApiService apiService, ApiServiceQA apiServiceQA, ApiServiceDev apiServiceDev) {
        return new M1FragmentRepositoryImpl(apiService, apiServiceDev, apiServiceQA);
    }
}
