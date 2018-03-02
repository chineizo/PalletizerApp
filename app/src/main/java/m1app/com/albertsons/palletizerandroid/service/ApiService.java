package m1app.com.albertsons.palletizerandroid.service;

import java.util.Map;

import m1app.com.albertsons.palletizerandroid.pojo.MIResult;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Path;
import retrofit2.http.QueryMap;
import rx.Observable;


public interface ApiService {

    @GET("execute/{program}/{transaction}{option}")
    Call<MIResult> getData(@Path("program") String program,
                           @Path("transaction") String transaction,
                           @Path("option") String option,
                           @QueryMap Map<String, String> queryParameters);


    @GET("execute/{program}/{transaction}{option}")
    Observable<MIResult> getProductNumberObservable(@Path("program") String program,
                                                    @Path("transaction") String transaction,
                                                    @Path("option") String option,
                                                    @QueryMap Map<String, String> queryParameters);


    @GET("execute/{program}/{transaction}{option}")
    Observable<MIResult> getLabelInformationObservable(@Path("program") String program,
                                                       @Path("transaction") String transaction,
                                                       @Path("option") String option,
                                                       @QueryMap Map<String, String> queryParameters);


    @GET("execute/{program}/{transaction}{option}")
    Observable<MIResult> getLastRecordObservable(@Path("program") String program,
                                                 @Path("transaction") String transaction,
                                                 @Path("option") String option,
                                                 @QueryMap Map<String, String> queryParameters);


    @GET("execute/{program}/{transaction}{option}")
    Observable<MIResult> updateLastRecordObservable(@Path("program") String program,
                                                    @Path("transaction") String transaction,
                                                    @Path("option") String option,
                                                    @QueryMap Map<String, String> queryParameters);


    @GET("execute/{program}/{transaction}{option}")
    Observable<MIResult> deletePreviousRecordObservable(@Path("program") String program,
                                                        @Path("transaction") String transaction,
                                                        @Path("option") String option,
                                                        @QueryMap Map<String, String> queryParameters);


    @GET("execute/{program}/{transaction}{option}")
    Observable<MIResult> updateLotNumberObservable (@Path("program") String program,
                                                    @Path("transaction") String transaction,
                                                    @Path("option") String option,
                                                    @QueryMap Map<String, String> queryParameters);
}
