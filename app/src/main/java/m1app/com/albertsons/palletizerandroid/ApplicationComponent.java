package m1app.com.albertsons.palletizerandroid;


import javax.inject.Singleton;

import dagger.Component;
import m1app.com.albertsons.palletizerandroid.activity.LoginActivity;
import m1app.com.albertsons.palletizerandroid.activity.MainActivity;
import m1app.com.albertsons.palletizerandroid.fragment.PalletizerFragment;
import m1app.com.albertsons.palletizerandroid.module.ApiModule;
import m1app.com.albertsons.palletizerandroid.module.ApiModuleDev;
import m1app.com.albertsons.palletizerandroid.module.ApiModuleQA;
import m1app.com.albertsons.palletizerandroid.module.ApplicationModule;
import m1app.com.albertsons.palletizerandroid.module.LoginActivityModule;
import m1app.com.albertsons.palletizerandroid.module.M1FragmentModule;

@Singleton
@Component(modules = {ApplicationModule.class, ApiModule.class, ApiModuleDev.class, ApiModuleQA.class, LoginActivityModule.class, M1FragmentModule.class})
public interface ApplicationComponent {

    void inject(MainActivity target);

    void inject(LoginActivity target);

    void inject(PalletizerFragment target);
}
