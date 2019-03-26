package phamkien.comic.Activities;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;
import java.util.List;

import phamkien.comic.Adapters.BookshelfAdapter;
import phamkien.comic.Models.Comic;
import phamkien.comic.R;
import phamkien.comic.Services.ComicAPI;
import phamkien.comic.Services.DataService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ChapterActivity extends AppCompatActivity {

    public static final String INTENT_CHAPTER = "chapter";
    public static final String INTENT_CURRENT_CHAPTER = "current chapter";
    public static final String INTENT_COMIC_NAME = "comic name";
    private Toolbar toolbar;
    private TextView tvTitle, tvCategory;
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
        tvCategory = findViewById(R.id.tvCategory);
        ivCover = findViewById(R.id.ivCover);
        ivBackground = findViewById(R.id.ivBackground);
        fabRead = findViewById(R.id.fabRead);
        fabFav = findViewById(R.id.fabFav);
        lvChapters = findViewById(R.id.lvChapters);

        comic = getIntent().getParcelableExtra(BookshelfAdapter.INTENT_COMIC);
        tvTitle.setText(comic.getTitle());
        tvCategory.setText(comic.getCategory());
        Glide.with(this).load(comic.getCover()).into(ivCover);
        Glide.with(this).load(comic.getCover()).into(ivBackground);
        setChapters(comic.getId());
    }

    private void setChapters(String comicId) {

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

                        startActivity(new Intent(getApplication(), ReaderActivity.class)
                            .putExtra(INTENT_COMIC_NAME, comic.getTitle())
                            .putExtra(INTENT_CURRENT_CHAPTER, chapters.get(position).getId())
                            .putExtra(INTENT_CHAPTER, chapters));
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
}
