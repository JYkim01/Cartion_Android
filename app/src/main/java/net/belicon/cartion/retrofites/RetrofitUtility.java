package net.belicon.cartion.retrofites;

public class RetrofitUtility {

    public static final String REAL_SERVER_URL = "https://api.cartion.co.kr:9983";
    public static final String TEST_SERVER_URL = "https://api.cartion.co.kr:9984";
    
    public static RetrofitInterface getRetrofitInterface() {
        return RetrofitClient.getClient(TEST_SERVER_URL).create(RetrofitInterface.class);
    }
}
