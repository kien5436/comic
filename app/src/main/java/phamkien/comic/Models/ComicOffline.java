package phamkien.comic.Models;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.Environment;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import okhttp3.ResponseBody;

public class ComicOffline extends SQLiteOpenHelper {

    public static final String TABLE = "toc";
    public static final String STORE_DIR = Environment.getExternalStorageDirectory() + "/comic/";
    private static final String DB_NAME = "comic";
    private static final int DB_VERSION = 1;
    private static ComicOffline instance = null;

    public ComicOffline(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    public static synchronized ComicOffline getInstance(Context context) {

        if (instance == null)
            instance = new ComicOffline(context.getApplicationContext());
        return instance;
    }

    public Comic getReading() {

        String q = String.format("select id, title, category, parent_id, cover, description, liked from %s where reading = 1", TABLE);
        Cursor cursor = getReadableDatabase().rawQuery(q, null);

        if (cursor != null) cursor.moveToFirst();

        String id = cursor.getString(cursor.getColumnIndex("id"));
        String parentId = cursor.getString(cursor.getColumnIndex("parent_id"));
        String title = cursor.getString(cursor.getColumnIndex("title"));
        String category = cursor.getString(cursor.getColumnIndex("category"));
        String cover = cursor.getString(cursor.getColumnIndex("cover"));
        String description = cursor.getString(cursor.getColumnIndex("description"));
        boolean like = cursor.getInt(cursor.getColumnIndex("liked")) != 0;

        if (cursor != null && !cursor.isClosed()) cursor.close();

        Comic comic = new Comic(id, title, category, cover, description, like);
        comic.setParentId(parentId);
        return comic;
    }

    public void saveImages(String uri, ResponseBody body) {

        try {
            String fileInfo[] = extractFileNameFromUri(uri),
                path = STORE_DIR + fileInfo[0];

            File file = new File(path);
            if (!file.isDirectory()) file.mkdirs();

            path += fileInfo[1];
            file = new File(path);
            if (file.exists()) return;

            InputStream inputStream = null;
            OutputStream outputStream = null;

            try {
                byte[] fileReader = new byte[4096];
                inputStream = body.byteStream();
                outputStream = new FileOutputStream(file);

                while (true) {

                    int read = inputStream.read(fileReader);
                    if (read == -1) break;
                    outputStream.write(fileReader, 0, read);
                }

                outputStream.flush();
            }
            catch (Exception e) {
                e.printStackTrace();
            }
            finally {
                if (inputStream != null) inputStream.close();
                if (outputStream != null) outputStream.close();
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
    }

    private String[] extractFileNameFromUri(String uri) {

        String[] fileInfo = new String[2];

        Matcher matcher = Pattern.compile("(up|down)loads(\\/.+\\/)(.+)").matcher(uri);
        if (matcher.find()) {

            fileInfo[0] = "downloads" + matcher.group(2);
            fileInfo[1] = matcher.group(3);
        }
        else {
            fileInfo[0] = "downloads/";
            fileInfo[1] = "chap.zip";
        }

        return fileInfo;
    }

    public ArrayList<Comic> getComics(boolean like) {

        ArrayList<Comic> comics = new ArrayList<>();
        String q = like ?
            String.format("select id, title, parent_id, cover, liked, category, description from %s where liked = 1 order by title", TABLE) :
            String.format("select id, title, parent_id, cover, liked, category, description from %s order by title", TABLE);
        Cursor cursor = getReadableDatabase().rawQuery(q, null);

        try {
            if (cursor.moveToFirst()) {

                do {
                    String id = cursor.getString(cursor.getColumnIndex("id"));
                    String title = cursor.getString(cursor.getColumnIndex("title"));
                    String category = cursor.getString(cursor.getColumnIndex("category"));
                    String cover = cursor.getString(cursor.getColumnIndex("cover"));
                    String description = cursor.getString(cursor.getColumnIndex("description"));
                    Comic comic = new Comic(id, title, category, cover, description, like);
                    comics.add(comic);
                } while (cursor.moveToNext());
            }
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            if (cursor != null && !cursor.isClosed()) cursor.close();
        }

        return comics;
    }

    public Comic getComic(String id) {

        String q = String.format("select id, title, parent_id, cover, liked, category, description from %s where id = %s order by title", TABLE, id);
        Cursor cursor = getReadableDatabase().rawQuery(q, null);

        if (cursor != null) cursor.moveToFirst();

        String title = cursor.getString(cursor.getColumnIndex("title"));
        String category = cursor.getString(cursor.getColumnIndex("category"));
        String cover = cursor.getString(cursor.getColumnIndex("cover"));
        String description = cursor.getString(cursor.getColumnIndex("description"));
        boolean like = cursor.getInt(cursor.getColumnIndex("liked")) != 0;

        if (cursor != null && !cursor.isClosed()) cursor.close();

        return new Comic(id, title, category, cover, description, like);
    }

    public long addOrUpdate(Comic comic, boolean isComic, boolean isReading) {

        long comicId = -1;
        SQLiteDatabase db = getWritableDatabase();

        db.beginTransaction();
        try {
            ContentValues values = new ContentValues();
            if (isComic) {

                values.put("id", comic.getId());
                values.put("title", comic.getTitle());
                values.put("cover", comic.getCover(false));
                values.put("category", comic.getCategory());
                values.put("description", comic.getDescription());
                values.put("liked", comic.isLike() ? 1 : 0);
            }
            else {
                values.put("id", comic.getId());
                values.put("title", comic.getTitle());
                values.put("parent_id", comic.getParentId());
                values.put("reading", isReading ? 1 : 0);

                // check for reading comic, only one can exist at any time
                ContentValues valueReading = new ContentValues();
                valueReading.put("reading", 0);
                db.update(TABLE, valueReading, "reading = 1", null);
            }

            if (db.update(TABLE, values, "id = ?", new String[]{comic.getId()}) == 1)
                comicId = Long.parseLong(comic.getId());
            else
                comicId = db.insertOrThrow(TABLE, null, values);
            db.setTransactionSuccessful();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            db.endTransaction();
        }

        return comicId;
    }

    public void delete(String id) {

        SQLiteDatabase db = getWritableDatabase();
        db.beginTransaction();

        try {
            db.delete(TABLE, "id = ?", new String[]{id});
            db.setTransactionSuccessful();
        }
        catch (Exception e) {
            e.printStackTrace();
        }
        finally {
            db.endTransaction();
        }
    }

    @Override
    public void onCreate(SQLiteDatabase db) {

        db.execSQL("create table " + TABLE
            + "(id INTEGER PRIMARY KEY,"
            + "title varchar(255) NOT NULL,"
            + "parent_id integer default NULL,"
            + "cover varchar(255) ,"
            + "liked tinyint default 0,"
            + "reading tinyint default 0,"
            + "category varchar(30) default NULL,"
            + "description varchar(255) default NULL)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
    }
}
