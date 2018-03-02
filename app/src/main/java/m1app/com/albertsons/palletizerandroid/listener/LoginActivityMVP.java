package m1app.com.albertsons.palletizerandroid.listener;


public interface LoginActivityMVP {

    interface View {

        String getUsername();

        String getPassword();

        void onError(String message, int resourceId);

        void onComplete();

    }

    interface Presenter {

        void setView(View view);

        void onButtonClick();
    }

}
