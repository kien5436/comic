package phamkien.comic.Services;

import java.util.ArrayList;

import okhttp3.ResponseBody;
import phamkien.comic.Models.Category;
import phamkien.comic.Models.Comic;
import phamkien.comic.Models.Page;
import retrofit2.Call;
import retrofit2.http.GET;
import retrofit2.http.Query;
import retrofit2.http.Streaming;

public interface ComicAPI {

    @GET("index.php?toc=0")
    Call<ArrayList<Comic>> getComics();

    @GET("index.php")
    Call<ArrayList<Comic>> getToC(@Query("toc") String comicId);

    @GET("index.php?category")
    Call<ArrayList<Category>> getCategories();

    @GET("index.php")
    Call<ArrayList<Page>> getPages(@Query("chapid") String chapId);

    @GET("index.php")
    Call<ArrayList<Comic>> getComicsByCategory(@Query("cid") String cid);

    @GET("index.php")
    Call<ArrayList<Comic>> searchComics(@Query("search") String title);

    @Streaming
    @GET("index.php")
    Call<ResponseBody> download(@Query("image") String uri);
}
