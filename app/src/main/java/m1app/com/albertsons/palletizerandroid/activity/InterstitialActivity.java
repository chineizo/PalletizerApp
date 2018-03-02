package m1app.com.albertsons.palletizerandroid.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;

import m1app.com.albertsons.palletizerandroid.pojo.User;
import m1app.com.albertsons.palletizerandroid.utility.Prefs;


/**
 * Created by chinedu on 1/2/18.
 */

public class InterstitialActivity extends AppCompatActivity {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Prefs prefs = new Prefs(this);
        User user = prefs.getUser();
        if (user.getAuthorization() == null) {
            startActivity(new Intent(this, LoginActivity.class));
        } else {
            startActivity(new Intent(this, MainActivity.class));
        }
        finish();
    }
}
