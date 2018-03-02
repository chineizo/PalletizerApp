package m1app.com.albertsons.palletizerandroid.presenter;


import android.text.TextUtils;

import javax.annotation.Nullable;

import m1app.com.albertsons.palletizerandroid.R;
import m1app.com.albertsons.palletizerandroid.listener.LoginActivityMVP;

public class LoginActivityPresenter implements LoginActivityMVP.Presenter{


    @Nullable
    private LoginActivityMVP.View view;

    @Override
    public void setView(LoginActivityMVP.View view) {
        this.view = view;
    }

    @Override
    public void onButtonClick() {
        if (view != null) {
            final String username = view.getUsername();
            final String password = view.getPassword();
            if (TextUtils.isEmpty(username)) {
                view.onError("username", R.string.username_required);
            } else if (TextUtils.isEmpty(password)) {
                view.onError("password", R.string.password_required);
            } else {
                view.onComplete();
            }
        }
    }

}
