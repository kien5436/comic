package phamkien.comic.Services;

import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class DataService {

    private static Retrofit retrofit = null;
    private static final String baseUrl = "http://192.168.1.5/comic/";

    public static ComicAPI getService() {
        return DataService.getClient().create(ComicAPI.class);
    }

    private static Retrofit getClient() {

        if (retrofit == null) {

            retrofit = new Retrofit.Builder()
                .baseUrl(baseUrl)
                .addConverterFactory(GsonConverterFactory.create())
                .build();
        }

        return retrofit;
    }

    public static String getBaseUrl() {
        return baseUrl;
    }
}
