package net.belicon.cartion.retrofites;

public class RetrofitUtility {
    public static final String BASE_URL = "http://49.50.172.53:9983/";

    public static RetrofitInterface getRetrofitInterface() {
        return RetrofitClient.getClient(BASE_URL).create(RetrofitInterface.class);
    }
}
