package m1app.com.albertsons.palletizerandroid;

import android.support.multidex.MultiDex;
import android.support.multidex.MultiDexApplication;

import com.crashlytics.android.Crashlytics;

import io.fabric.sdk.android.Fabric;
import m1app.com.albertsons.palletizerandroid.module.ApiModule;
import m1app.com.albertsons.palletizerandroid.module.ApplicationModule;

public class M1Application extends MultiDexApplication {

    private static M1Application m1Application;
    private ApplicationComponent component;
    @Override
    public void onCreate() {
        super.onCreate();
        Fabric.with(this, new Crashlytics()); //THIS DISABLES THE SERIAL SCANNER TO THE VC80x (Not sure why)
        MultiDex.install(this);
        m1Application = this;

        component = DaggerApplicationComponent.builder()
                .applicationModule(new ApplicationModule(this))
                .apiModule(new ApiModule())
                .build();

    }

    public ApplicationComponent getComponent () {
        return component;
    }

    public static M1Application getApplication () {
        return m1Application;
    }
}
