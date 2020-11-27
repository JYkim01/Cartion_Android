package net.belicon.cartion.retrofites;

public class RetrofitUtility {
    public static final String BASE_URL = "https://api.cartion.co.kr:9983/";

    public static RetrofitInterface getRetrofitInterface() {
        return RetrofitClient.getClient(BASE_URL).create(RetrofitInterface.class);
    }
}
