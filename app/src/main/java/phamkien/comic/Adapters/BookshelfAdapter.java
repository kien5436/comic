package phamkien.comic.Adapters;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.io.File;
import java.util.ArrayList;

import phamkien.comic.Activities.ChapterActivity;
import phamkien.comic.Models.Comic;
import phamkien.comic.Models.ComicOffline;
import phamkien.comic.R;

public class BookshelfAdapter extends BaseAdapter {

    public static final String INTENT_COMIC = "comic";
    public static final String INTENT_IS_ONLINE = "online";
    private boolean online = true;
    private Context context;
    private ArrayList<Comic> comics;
    private Fragment fragment;

    public BookshelfAdapter(Context context, ArrayList<Comic> comics, Fragment fragment) {

        this.context = context;
        this.comics = comics;
        this.fragment = fragment;
    }

    @Override
    public int getCount() {
        return comics.size();
    }

    @Override
    public Object getItem(int position) {
        return comics.get(position);
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(final int position, View convertView, final ViewGroup parent) {

        if (convertView == null)
            convertView = LayoutInflater.from(context).inflate(R.layout.adapter_book, parent, false);

        ImageView ivCover = convertView.findViewById(R.id.ivCover);
        TextView tvTitle = convertView.findViewById(R.id.tvTitle);

        Uri imageUri;
        switch (fragment.getClass().getSimpleName()) {

            case "FavFragment":
                online = false;
                String cover = comics.get(position).getCover(online);
                comics.get(position).setCover(cover.replace("uploads", "downloads"));
                imageUri = Uri.fromFile(new File(ComicOffline.STORE_DIR + cover));
                break;
            default:
                online = true;
                imageUri = Uri.parse(comics.get(position).getCover(online));
                break;
        }

        Glide.with(context).load(imageUri).into(ivCover);
        tvTitle.setText(comics.get(position).getTitle());

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                context.startActivity(new Intent(context, ChapterActivity.class)
                    .putExtra(INTENT_IS_ONLINE, online)
                    .putExtra(INTENT_COMIC, comics.get(position)));
            }
        });

        convertView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {

                switch (fragment.getClass().getSimpleName()) {

                    case "FavFragment":
                        deleteFavorite(comics.get(position));
                        break;
                    default:
                        break;
                }
                return true;
            }
        });

        return convertView;
    }

    private void deleteFavorite(final Comic selectedComic) {

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setMessage("Xóa khỏi Truyện của tôi?")
            .setPositiveButton("Có", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    ComicOffline comicOffline = ComicOffline.getInstance(context);
                    Comic comic = comicOffline.getComic(selectedComic.getId());

                    comicOffline.delete(selectedComic.getId());
                    (new File(ComicOffline.STORE_DIR + comic.getCover(false))).delete();
                    comics.remove(selectedComic);
                    notifyDataSetChanged();
                }
            })
            .setNegativeButton("Không", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                }
            })
            .show();
    }
}
