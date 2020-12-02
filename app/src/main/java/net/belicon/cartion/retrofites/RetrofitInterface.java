package net.belicon.cartion.retrofites;

import net.belicon.cartion.models.Banner;
import net.belicon.cartion.models.Cartion;
import net.belicon.cartion.models.Category;
import net.belicon.cartion.models.Coupon;
import net.belicon.cartion.models.Duplicate;
import net.belicon.cartion.models.Faq;
import net.belicon.cartion.models.Horn;
import net.belicon.cartion.models.Learn;
import net.belicon.cartion.models.Login;
import net.belicon.cartion.models.Manual;
import net.belicon.cartion.models.MobileSwitch;
import net.belicon.cartion.models.Music;
import net.belicon.cartion.models.MyPage;
import net.belicon.cartion.models.Notice;
import net.belicon.cartion.models.PasswordModify;
import net.belicon.cartion.models.PhoneModify;
import net.belicon.cartion.models.RefreshToken;
import net.belicon.cartion.models.User;
import net.belicon.cartion.models.UserMobile;
import net.belicon.cartion.models.UserMobileList;

import java.util.List;

import okhttp3.ResponseBody;
import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.Header;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.Path;
import retrofit2.http.Query;

public interface RetrofitInterface {

    // Login
    @POST("/login")
    Call<Login> postLogin(@Body User body);

    @GET("/noauth/user/{userId}")
    Call<Duplicate> getDuplicate(@Path("userId") String userId);

    @POST("/join")
    Call<Login> postJoin(@Body User body);

    @Headers("Content-Type: application/json")
    @PUT("/token")
    Call<Login> putToken(@Body RefreshToken body);

    @PUT("/api/agreement")
    Call<Login> putAgreement(@Header("Authorization") String token);

    @Headers("Content-Type: application/json")
    @GET("/api/categories")
    Call<Category> getCategory(@Header("Authorization") String token);

    @GET("/api/horns")
    Call<Horn> getMusicList(@Header("Authorization") String token, @Query("offset") String offset, @Query("limit") String limit, @Query("categoryId") String id);

    @Headers("Content-Type: application/json")
    @GET("/api/customHorns")
    Call<Horn> getMyMusicList(@Header("Authorization") String token);

    @GET("api/horn/wav/{hornId}/")
    Call<ResponseBody> getPCM(@Header("Authorization") String token, @Path("hornId") String hornId);

    @GET("api/horn/ADPCM/{hornId}/")
    Call<ResponseBody> getADPCM(@Header("Authorization") String token, @Path("hornId") String hornId);

    @GET("api/{userId}/horn")
    Call<MobileSwitch> getMobileSwitch(@Header("Authorization") String token, @Path("userId") String userId);

    @GET("api/user/{userId}")
    Call<MyPage> getUserData(@Header("Authorization") String token, @Path("userId") String userId);

    @PUT("api/user/{userId}")
    Call<MyPage> putPasswordModify(@Header("Authorization") String token, @Path("userId") String userId, @Body PasswordModify body);

    @PUT("api/user/{userId}")
    Call<MyPage> putPhoneModify(@Header("Authorization") String token, @Path("userId") String userId, @Body PhoneModify body);

    @POST("api/{userId}/device")
    Call<MyPage> postCartion(@Header("Authorization") String token, @Path("userId") String userId, @Body Cartion body);

    @PUT("api/{userId}/horn/{mobileSwitch}")
    Call<MyPage> putMobileSw(@Header("Authorization") String token, @Path("userId") String userId, @Path("mobileSwitch") String mobileSwitch, @Body UserMobile body);

    @PUT("api/{userId}/horn-index")
    Call<MyPage> putMobileList(@Header("Authorization") String token, @Path("userId") String userId, @Body List<UserMobile> body);

    @GET("api/banners")
    Call<Banner> getBannerList(@Header("Authorization") String token);

    @GET("api/use-apps")
    Call<Learn> getLearnList(@Header("Authorization") String token);

    @GET("api/notices")
    Call<Notice> getNotice(@Header("Authorization") String token);

    @GET("api/faqs")
    Call<Faq> getFaq(@Header("Authorization") String token);

    @GET("api/app-manual")
    Call<Manual> getManual(@Header("Authorization") String token);

    @GET("api/user/{userId}/coupon")
    Call<Coupon> getCoupon(@Header("Authorization") String token, @Path("userId") String userId);

    @PUT("api/user/{userId}/coupon/{couponValue}")
    Call<MyPage> putCoupon(@Header("Authorization") String token, @Path("userId") String userId, @Path("couponValue") String couponValue);
}