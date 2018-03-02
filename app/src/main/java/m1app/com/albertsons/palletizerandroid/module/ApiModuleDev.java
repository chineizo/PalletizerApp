package m1app.com.albertsons.palletizerandroid.module;

import java.io.IOException;

import m1app.com.albertsons.palletizerandroid.M1Application;
import m1app.com.albertsons.palletizerandroid.R;
import dagger.Module;
import dagger.Provides;
import m1app.com.albertsons.palletizerandroid.service.ApiServiceDev;
import m1app.com.albertsons.palletizerandroid.utility.Prefs;
import okhttp3.Interceptor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;

/**
 * Created by chinedu on 1/21/18.
 */
@Module
public class ApiModuleDev {



    @Provides
    public OkHttpClient provideClient() {
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        loggingInterceptor.setLevel(HttpLoggingInterceptor.Level.BODY);
        OkHttpClient.Builder builder = new OkHttpClient.Builder();
        builder.addInterceptor(loggingInterceptor);
        builder.addInterceptor(new Interceptor() {
            @Override
            public Response intercept(Chain chain) throws IOException {
                Request originalRequest = chain.request();

                Prefs prefs = new Prefs(M1Application.getApplication());
                final String authorization = prefs.getUser().getAuthorization();

                Request.Builder requestBuilder = originalRequest
                        .newBuilder()
                        .addHeader("Authorization", authorization)
                        .addHeader("Accept", "application/json");
                return chain.proceed(requestBuilder.build());
            }
        });


        return builder.build();
    }

    public String getBaseURL () {
        return  "https://albertsonsdev-bel1.cloud.infor.com:63906/m3api-rest/";
    }

    @Provides
    public Retrofit provideRetrofit(String baseUrl, OkHttpClient client) {
        return new Retrofit.Builder()
                .baseUrl(baseUrl)
                .client(client)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                .build();
    }

    @Provides
    public ApiServiceDev provideApiService() {
        return provideRetrofit(getBaseURL(), provideClient()).create(ApiServiceDev.class);
    }

}
