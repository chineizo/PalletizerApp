package m1app.com.albertsons.palletizerandroid.activity;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v4.view.ViewPager;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

import m1app.com.albertsons.palletizerandroid.M1Application;
import m1app.com.albertsons.palletizerandroid.R;
import butterknife.BindView;
import butterknife.ButterKnife;
import m1app.com.albertsons.palletizerandroid.config.Config;
import m1app.com.albertsons.palletizerandroid.fragment.BaseFragment;
import m1app.com.albertsons.palletizerandroid.fragment.PalletizerFragment;
import m1app.com.albertsons.palletizerandroid.fragment.SettingFragment;
import m1app.com.albertsons.palletizerandroid.pojo.User;
import m1app.com.albertsons.palletizerandroid.utility.Prefs;
import m1app.com.albertsons.palletizerandroid.utility.Utils;
import okhttp3.Credentials;

public class MainActivity extends BaseActivity implements BaseFragment.OnFragmentListener {

    private final String TAG = MainActivity.class.getSimpleName();
    @BindView(R.id.viewpager)
    ViewPager viewPager;
    @BindView(R.id.tabs)
    TabLayout tabLayout;
    private MainActivityReceiver mainActivityReceiver;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.main_layout);
//
//        boolean isTablet = getResources().getBoolean(R.bool.isTablet);
//        if (!isTablet) {
//            setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
//        }

        ((M1Application) getApplication()).getComponent().inject(this);

        authorizeDevice();

        ButterKnife.bind(this);

        createToolbar();

        setupReceiver();

        getSupportActionBar().setDisplayHomeAsUpEnabled(false);

        ViewPagerAdapter adapter = new ViewPagerAdapter(getSupportFragmentManager());
        adapter.addFragment(new PalletizerFragment(), getString(R.string.label));
        adapter.addFragment(new SettingFragment(), getString(R.string.setting));
        viewPager.setAdapter(adapter);
        tabLayout.setupWithViewPager(viewPager);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {

            }

            @Override
            public void onPageSelected(int position) {
                switch (position) {
                    case 0:
                        PalletizerFragment m1Fragment = (PalletizerFragment)
                                getSupportFragmentManager()
                                        .findFragmentByTag("android:switcher:"
                                                + R.id.viewpager + ":" + viewPager.getCurrentItem());

                        if (m1Fragment != null) {
                            m1Fragment.updateViews();
                        }
                        break;
                    case 1:
                        SettingFragment settingFragment = (SettingFragment) getSupportFragmentManager()
                                .findFragmentByTag("android:switcher:"
                                        + R.id.viewpager + ":" + viewPager.getCurrentItem());
                        if (settingFragment != null) {
                            settingFragment.enableViews(false);
                        }
                        break;
                }
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });
    }

    private void setupReceiver () {
        mainActivityReceiver = new MainActivityReceiver();
        LocalBroadcastManager.getInstance(this).registerReceiver(mainActivityReceiver,
                new IntentFilter(Config.Extra.ACTION_CLEAR_SCREEN));
    }

    public void accessToChangeSettingsDenied() {
        viewPager.setCurrentItem(0);
    }

    private void authorizeDevice() {
        System.getenv();
        Prefs prefs = new Prefs(this);
        User user = prefs.getUser();
        if (user.getAuthorization() == null) {
            String basicAuth = Credentials.basic(prefs.getUserName(), prefs.getPassword());
            user.setAuthorization(basicAuth);
            prefs.setUser(user);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mainActivityReceiver);
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.d(TAG, "OnStart called!");
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.d(TAG, "OnStop called!");
    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    @Override
    public void showProgressView() {
    }

    @Override
    public void hideProgressView() {
    }

    @Override
    public void onFragmentEvent(int requestCode, Bundle bundle) {
        Log.d(TAG, "OnFragmentEvent called");
    }

    @Override
    public void onBackPressed() {
        Utils.showAlertDialog(this, getString(R.string.exit),
                getString(R.string.want_to_quit), getString(R.string.ok), new Runnable() {
                    @Override
                    public void run() {
                        MainActivity.super.onBackPressed();
                    }
                }, getString(R.string.cancel), null);
    }

    private class ViewPagerAdapter extends FragmentPagerAdapter {
        private final List<Fragment> fragmentList = new ArrayList<>();
        private final List<String> titles = new ArrayList<>();

        public ViewPagerAdapter(FragmentManager fragmentManager) {
            super(fragmentManager);
        }

        @Override
        public Fragment getItem(int position) {
            return fragmentList.get(position);
        }

        @Override
        public int getCount() {
            return fragmentList.size();
        }

        public void addFragment(Fragment fragment, String title) {
            fragmentList.add(fragment);
            titles.add(title);
        }

        @Nullable
        @Override
        public CharSequence getPageTitle(int position) {
            return titles.get(position);
        }
    }

//    @Override
//    public boolean onCreateOptionsMenu(Menu menu) {
//        MenuInflater inflater = getMenuInflater();
//        inflater.inflate(R.menu.app_menu, menu);
//        return true;
//    }


    private class MainActivityReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "Broadcast received for clearing the fragments inputs");
            if (Config.Extra.ACTION_CLEAR_SCREEN.equals(intent.getAction())) {
                Log.d(TAG, "Action received to call fragment, which I found!");
                PalletizerFragment palletizerFragment = (PalletizerFragment) getSupportFragmentManager()
                        .findFragmentByTag("android:switcher:"
                                + R.id.viewpager + ":" + viewPager.getCurrentItem());

                if (palletizerFragment != null) {
                    palletizerFragment.resetFields();
                }
            }
        }
    }

}
