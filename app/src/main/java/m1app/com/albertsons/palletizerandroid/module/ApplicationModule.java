package m1app.com.albertsons.palletizerandroid.module;


import android.app.Application;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class ApplicationModule {

    private Application application;

    public ApplicationModule (Application application) {
        this.application = application;
    }

    @Provides
    @Singleton
    public Application provideApplication () {
        return application;
    }
}
