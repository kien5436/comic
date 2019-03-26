package phamkien.comic.Adapters;

import android.content.Context;
import android.content.Intent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import phamkien.comic.Activities.ChapterActivity;
import phamkien.comic.Models.Comic;
import phamkien.comic.R;

public class BookshelfAdapter extends BaseAdapter {

    public static final String INTENT_COMIC = "comic";
    private Context context;
    private ArrayList<Comic> comics;

    public BookshelfAdapter(Context context, ArrayList<Comic> comics) {

        this.context = context;
        this.comics = comics;
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
    public View getView(final int position, View convertView, ViewGroup parent) {

        if (convertView == null)
            convertView = LayoutInflater.from(context).inflate(R.layout.adapter_book, parent, false);

        ImageView ivCover = convertView.findViewById(R.id.ivCover);
        TextView tvTitle = convertView.findViewById(R.id.tvTitle);

        Glide.with(context).load(comics.get(position).getCover()).into(ivCover);
        tvTitle.setText(comics.get(position).getTitle());

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                context.startActivity(new Intent(context, ChapterActivity.class)
                    .putExtra(INTENT_COMIC, comics.get(position)));
            }
        });


        return convertView;
    }
}
