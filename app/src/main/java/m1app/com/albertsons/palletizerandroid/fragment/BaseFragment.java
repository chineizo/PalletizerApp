package m1app.com.albertsons.palletizerandroid.fragment;

import android.app.Activity;
import android.os.Bundle;
import android.os.IBinder;
import android.support.v4.app.Fragment;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;

/**
 * Created by chinedu on 12/29/17.
 */

public abstract class BaseFragment extends Fragment {

    public abstract void showProgressView();

    public abstract void hideProgressView();

    public void registerForSoftKeyBoardControl(View view) {
        //set up touch listener for non textbox views to hide keyboard
        if (view != null && !(view instanceof EditText)) {
            view.setOnTouchListener(new View.OnTouchListener() {
                @Override
                public boolean onTouch(View v, MotionEvent event) {
                    hideKeyboard();
                    return false;
                }
            });
        }

        // if a layout is a container, iterate over the children and seed recursion
        if (view instanceof ViewGroup) {
            int count = ((ViewGroup) view).getChildCount();
            for (int i = 0; i < count; i++) {
                View innerView = ((ViewGroup) view).getChildAt(i);
                registerForSoftKeyBoardControl(innerView);
            }
        }
    }

    public void hideKeyboard() {
        if (getActivity() != null && getActivity().getCurrentFocus() != null) {
            InputMethodManager inputMethodManager = (InputMethodManager) getActivity()
                    .getSystemService(Activity.INPUT_METHOD_SERVICE);
            IBinder iBinder = getActivity().getCurrentFocus().getWindowToken();
            if (iBinder != null) {
                inputMethodManager.hideSoftInputFromWindow(getActivity()
                        .getCurrentFocus().getWindowToken(), 0);
            }
        }
    }

    public interface OnFragmentListener {
        void onFragmentEvent(int requestCode, Bundle bundle);
    }
}
