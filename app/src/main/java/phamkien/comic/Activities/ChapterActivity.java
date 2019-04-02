package phamkien.comic.Activities;

import android.annotation.SuppressLint;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Environment;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import okhttp3.ResponseBody;
import phamkien.comic.Adapters.BookshelfAdapter;
import phamkien.comic.Models.Comic;
import phamkien.comic.Models.ComicOffline;
import phamkien.comic.R;
import phamkien.comic.Services.ComicAPI;
import phamkien.comic.Services.DataService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChapterActivity extends AppCompatActivity implements View.OnClickListener {

    public static final String INTENT_CHAPTERS = "chapters";
    public static final String INTENT_CURRENT_CHAPTER = "current chapter";
    public static final String INTENT_COMIC_NAME = "comic name";
    private Toolbar toolbar;
    private TextView tvTitle, tvCategory, tvDescription;
    private ImageView ivCover, ivBackground;
    private FloatingActionButton fabRead, fabFav;
    private ListView lvChapters;
    private Comic comic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_chapter);

        toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        tvTitle = findViewById(R.id.tvTitle);
        tvDescription = findViewById(R.id.tvDescription);
        tvCategory = findViewById(R.id.tvCategory);
        ivCover = findViewById(R.id.ivCover);
        ivBackground = findViewById(R.id.ivBackground);
        fabRead = findViewById(R.id.fabRead);
        fabFav = findViewById(R.id.fabFav);
        lvChapters = findViewById(R.id.lvChapters);

        comic = getIntent().getParcelableExtra(BookshelfAdapter.INTENT_COMIC);
        boolean online = getIntent().getBooleanExtra(BookshelfAdapter.INTENT_IS_ONLINE, true);
        Uri imageUri;
        String cover = comic.getCover(online);
        imageUri = online ? Uri.parse(cover) : Uri.fromFile(new File(ComicOffline.STORE_DIR + cover));

        tvTitle.setText(comic.getTitle());
        tvDescription.setText(comic.getDescription());
        tvCategory.setText(comic.getCategory());
        Glide.with(this).load(imageUri).into(ivCover);
        Glide.with(this).load(imageUri).into(ivBackground);
        setChapters(comic.getId());

        fabFav.setOnClickListener(this);
        fabRead.setOnClickListener(this);
    }

    private void setChapters(final String comicId) {

        ComicAPI comicAPI = DataService.getService();
        Call<ArrayList<Comic>> callback = comicAPI.getToC(comicId);
        callback.enqueue(new Callback<ArrayList<Comic>>() {
            @Override
            public void onResponse(Call<ArrayList<Comic>> call, Response<ArrayList<Comic>> response) {

                final ArrayList<Comic> chapters = response.body();
                List<String> chapterName = new ArrayList<>();

                for (int i = 0; i < chapters.size(); i++)
                    chapterName.add(chapters.get(i).getTitle());

                ArrayAdapter<String> chapterAdapter = new ArrayAdapter<>(getApplication(), R.layout.adapter_list, R.id.tvListItem, chapterName);
                lvChapters.setAdapter(chapterAdapter);

                lvChapters.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                        chapters.get(position).setParentId(comicId);
                        ComicOffline comicOffline = ComicOffline.getInstance(getApplication());
                        comicOffline.addOrUpdate(comic, true, false);
                        comicOffline.addOrUpdate(chapters.get(position), false, true);

                        startActivity(new Intent(getApplication(), ReaderActivity.class)
                            .putExtra(INTENT_COMIC_NAME, comic.getTitle())
                            .putExtra(INTENT_CURRENT_CHAPTER, chapters.get(position).getId())
                            .putExtra(INTENT_CHAPTERS, chapters));
                    }
                });

                lvChapters.setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
                    @Override
                    public boolean onItemLongClick(AdapterView<?> parent, View view, final int position, long id) {

                        AlertDialog.Builder builder = new AlertDialog.Builder(ChapterActivity.this);
                        builder.setMessage("Tải chương này?")
                            .setPositiveButton("Có", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    downloadChap(chapters.get(position).getId());
                                }
                            })
                            .setNegativeButton("Không", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                }
                            })
                            .show();
                        return true;
                    }
                });
            }

            @Override
            public void onFailure(Call<ArrayList<Comic>> call, Throwable t) {

                Log.e("ChapterActivity", "onFailure: " + t.getMessage());
                t.printStackTrace();
            }
        });
    }

    private void downloadChap(String chapId) {

        ComicAPI comicAPI = DataService.getService();
        Call<ResponseBody> callback = comicAPI.download(chapId);
        callback.enqueue(new Callback<ResponseBody>() {
            @SuppressLint("StaticFieldLeak")
            @Override
            public void onResponse(Call<ResponseBody> call, final Response<ResponseBody> response) {

                if (response.isSuccessful())
                    new DownloadTask().execute(
                        Environment.getExternalStorageDirectory() + "/downloads/chap.zip", response.body(), false
                    );
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("ChapterActivity", "onFailure: ", t);
            }
        });
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {

            case R.id.fabFav:
                setFavorite();
                break;
            case R.id.fabRead:
                read();
                break;
        }
    }

    /** get newest chapter to read */
    private void read() {

        ComicAPI comicAPI = DataService.getService();
        Call<ArrayList<Comic>> callback = comicAPI.getToC(comic.getId());
        callback.enqueue(new Callback<ArrayList<Comic>>() {
            @Override
            public void onResponse(Call<ArrayList<Comic>> call, Response<ArrayList<Comic>> response) {

                ArrayList<Comic> chapters = response.body();

                startActivity(new Intent(getApplication(), ReaderActivity.class)
                    .putExtra(INTENT_COMIC_NAME, comic.getTitle())
                    .putExtra(INTENT_CURRENT_CHAPTER, chapters.get(chapters.size() - 1).getId())
                    .putExtra(INTENT_CHAPTERS, chapters));
            }

            @Override
            public void onFailure(Call<ArrayList<Comic>> call, Throwable t) {
                Log.e("ChapterActivity", "onFailure: ", t);
            }
        });
    }

    private void setFavorite() {

        comic.setLike(true);
        ComicAPI comicAPI = DataService.getService();
        Call<ResponseBody> callback = comicAPI.download(comic.getCover(true));
        callback.enqueue(new Callback<ResponseBody>() {
            @Override
            public void onResponse(Call<ResponseBody> call, Response<ResponseBody> response) {

                if (response.isSuccessful()) {

                    String cover = comic.getCover(false);
                    comic.setCover(cover.replace("uploads", "downloads"));
                    new DownloadTask().execute(comic.getCover(false), response.body(), true);
                }
            }

            @Override
            public void onFailure(Call<ResponseBody> call, Throwable t) {
                Log.e("ChapterActivity", "onFailure: ", t);
            }
        });
    }

    private class DownloadTask extends AsyncTask<Object, Void, String> {

        @Override
        protected String doInBackground(Object... objects) {

            String uri = (String) objects[0];
            ResponseBody body = (ResponseBody) objects[1];
            boolean isComic = (Boolean) objects[2];

            ComicOffline comicOffline = ComicOffline.getInstance(ChapterActivity.this);
            comicOffline.saveImages(uri, body);
            comicOffline.addOrUpdate(comic, isComic, false);

            String res = isComic ? "Truyện của tôi" : "Tải về";
            return "Đã thêm vào " + res;
        }

        @Override
        protected void onPostExecute(String res) {
            Toast.makeText(getApplication(), res, Toast.LENGTH_SHORT).show();
        }
    }
}
