package m1app.com.albertsons.palletizerandroid.module;
import m1app.com.albertsons.palletizerandroid.R;
import dagger.Module;
import dagger.Provides;
import m1app.com.albertsons.palletizerandroid.listener.LoginActivityMVP;
import m1app.com.albertsons.palletizerandroid.presenter.LoginActivityPresenter;

@Module
public class LoginActivityModule {

    @Provides
    public LoginActivityMVP.Presenter provideLoginActivityPresenter () {
        return new LoginActivityPresenter();
    }
}
