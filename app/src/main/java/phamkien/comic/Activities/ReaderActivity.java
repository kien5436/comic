package phamkien.comic.Activities;

import android.content.Intent;
import android.graphics.Point;
import android.os.Build;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.util.TypedValue;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import java.util.ArrayList;

import phamkien.comic.Adapters.ReaderAdapter;
import phamkien.comic.Models.Comic;
import phamkien.comic.Models.Page;
import phamkien.comic.R;
import phamkien.comic.Services.ComicAPI;
import phamkien.comic.Services.DataService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class ReaderActivity extends AppCompatActivity {

    private RecyclerView rvPages;
    private Toolbar toolbar;
    private ArrayList<Comic> chapters;
    private ComicAPI comicAPI = DataService.getService();

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reader);
        Intent intent = getIntent();

        rvPages = findViewById(R.id.rvPages);
        toolbar = findViewById(R.id.toolbar);
        toolbar.setTitle(intent.getStringExtra(ChapterActivity.INTENT_COMIC_NAME));
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });

        chapters = intent.getParcelableArrayListExtra(ChapterActivity.INTENT_CHAPTER);
        String curChap = intent.getStringExtra(ChapterActivity.INTENT_CURRENT_CHAPTER);

        setPages(curChap);
    }

    private void setPages(String chapId) {

        Call<ArrayList<Page>> callback = comicAPI.getPages(chapId);
        callback.enqueue(new Callback<ArrayList<Page>>() {
            @Override
            public void onResponse(Call<ArrayList<Page>> call, Response<ArrayList<Page>> response) {

                ArrayList<Page> pages = response.body();
                ReaderAdapter readerAdapter = new ReaderAdapter(getApplication(), pages);
                readerAdapter.setScrSize(getScreenSize());
                if (rvPages.getLayoutManager() == null)
                    rvPages.setLayoutManager(new LinearLayoutManager(getApplicationContext(), LinearLayoutManager.HORIZONTAL, false));
                rvPages.setAdapter(readerAdapter);
            }

            @Override
            public void onFailure(Call<ArrayList<Page>> call, Throwable t) {

                Log.e("ReaderActivity", "onFailure: ", t);
                t.printStackTrace();
            }
        });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        for (int i = 0; i < chapters.size(); i++)
            menu.add(Menu.NONE, Integer.parseInt(chapters.get(i).getId()), Menu.NONE, chapters.get(i).getTitle());
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        setPages(String.valueOf(item.getItemId()));
        return true;
    }

    private Point getScreenSize() {

        Point size = new Point();
        int actionBarHeight = 48;
        final TypedValue tv = new TypedValue();
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB &&
            getTheme().resolveAttribute(android.R.attr.actionBarSize, tv, true))
            actionBarHeight = TypedValue.complexToDimensionPixelSize(tv.data, getResources().getDisplayMetrics());

        getWindowManager().getDefaultDisplay().getSize(size);
        size.y -= actionBarHeight;

        return size;
    }
}
