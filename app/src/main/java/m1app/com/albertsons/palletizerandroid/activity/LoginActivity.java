package m1app.com.albertsons.palletizerandroid.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TextInputEditText;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.TextView;

import javax.inject.Inject;

import m1app.com.albertsons.palletizerandroid.M1Application;
import m1app.com.albertsons.palletizerandroid.R;
import butterknife.BindView;
import butterknife.ButterKnife;
import m1app.com.albertsons.palletizerandroid.listener.LoginActivityMVP;
import m1app.com.albertsons.palletizerandroid.pojo.User;
import m1app.com.albertsons.palletizerandroid.utility.Prefs;
import okhttp3.Credentials;

/**
 * Created by chinedu on 12/30/17.
 */

public class LoginActivity extends BaseActivity implements LoginActivityMVP.View {

    private final String TAG = LoginActivity.class.getName();

    @BindView(R.id.username)
    TextInputEditText inputUsername;

    @BindView(R.id.password)
    TextInputEditText inputPassword;

    @BindView(R.id.btn_login)
    Button loginButton;

    @Inject
    LoginActivityMVP.Presenter presenter;

    private  Prefs prefs;

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.login_activity);

        ((M1Application) getApplication()).getComponent().inject(this);

        ButterKnife.bind(this);

        createToolbar();

        getSupportActionBar().setDisplayHomeAsUpEnabled(false);


        prefs = new Prefs(LoginActivity.this);
        prefs.setUser(new User());

        registerForSoftKeyBoardControl(getWindow().getDecorView().getRootView());
        requestFocus(getWindow().getDecorView().getRootView());//
        inputPassword.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (actionId == EditorInfo.IME_ACTION_DONE || actionId == EditorInfo.IME_ACTION_SEND) {
                    inputUsername.setError(null);
                    inputPassword.setError(null);
                    presenter.onButtonClick();
                    hideKeyboard();
                    return true;
                }
                return false;
            }
        });


        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                inputUsername.setError(null);
                inputPassword.setError(null);

                String basicAuth = Credentials
                        .basic(inputUsername.getText().toString().trim(), inputPassword.getText()
                                .toString().trim());

                User user = prefs.getUser();
                user.setAuthorization(basicAuth);
                prefs.setUser(user);
                presenter.onButtonClick();
            }
        });

    }

    @Override
    protected void onResume() {
        super.onResume();
        presenter.setView(this);
    }

    @Override
    public void showProgressView() {

    }

    @Override
    public void hideProgressView() {

    }

    @Override
    public String getUsername() {
        return inputUsername.getText().toString().trim();
    }

    @Override
    public String getPassword() {
        return inputPassword.getText().toString().trim();
    }

    @Override
    public void onError(String message, int resourceId) {
        if (message.equals("username")) {
            inputUsername.setError(getString(resourceId));
        } else if (message.equals("password")) {
            inputPassword.setError(getString(resourceId));
        }
    }

    @Override
    public void onComplete() {
//        Intent intent = new Intent(LoginActivity.this, MainActivity.class);
//        intent.putExtra(Config.Extra.COMING_FROM_LOGIN_ACTIVITY_PARAM, true);
//        startActivity(intent);
//        finish();
    }
}
