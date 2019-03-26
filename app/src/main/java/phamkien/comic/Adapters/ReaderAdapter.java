package phamkien.comic.Adapters;

import android.content.Context;
import android.graphics.Point;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.bumptech.glide.Glide;

import java.util.ArrayList;

import phamkien.comic.Models.Page;
import phamkien.comic.R;

public class ReaderAdapter extends RecyclerView.Adapter<ReaderAdapter.ViewHolder> {

    private Context context;
    private ArrayList<Page> pages;
    private Point scrSize;

    public ReaderAdapter(Context context, ArrayList<Page> pages) {

        this.context = context;
        this.pages = pages;
    }

    public void setScrSize(Point scrSize) {
        this.scrSize = scrSize;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int i) {

        View view = LayoutInflater.from(context).inflate(R.layout.adapter_reader, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(ViewHolder viewHolder, int i) {

        Page page = pages.get(i);
        Glide.with(context).load(page.getContent()).override(scrSize.x, scrSize.y).into(viewHolder.ivPage);
    }

    @Override
    public int getItemCount() {
        return pages.size();
    }

    class ViewHolder extends RecyclerView.ViewHolder {

        ImageView ivPage;

        public ViewHolder(View itemView) {

            super(itemView);
            ivPage = itemView.findViewById(R.id.ivPage);
        }
    }
}
