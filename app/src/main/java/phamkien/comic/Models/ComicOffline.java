package phamkien.comic.Models;

import android.database.sqlite.SQLiteDatabase;

import static android.content.Context.MODE_PRIVATE;
import static android.database.sqlite.SQLiteDatabase.openOrCreateDatabase;

public class ComicOffline {

    private static SQLiteDatabase db = null;
    private String id;
    private String title;
    private String category;
    private String cover;

    public ComicOffline() {

        connectDb();
    }

    public ComicOffline(String id, String title, String category, String cover) {
        this.id = id;
        this.title = title;
        this.category = category;
        this.cover = cover;
    }

    private void connectDb() {

        if (db == null)
            db =  openOrCreateDatabase("/data/data/phamkien.comic/comic.db", null);
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getCover() {
        return cover;
    }

    public void setCover(String cover) {
        this.cover = cover;
    }
}
