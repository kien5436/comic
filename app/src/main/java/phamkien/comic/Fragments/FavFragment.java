package phamkien.comic.Fragments;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import java.util.ArrayList;

import phamkien.comic.Adapters.BookshelfAdapter;
import phamkien.comic.Models.Comic;
import phamkien.comic.Models.ComicOffline;
import phamkien.comic.R;

public class FavFragment extends android.support.v4.app.Fragment {

    private GridView gvBookShelf;
    private Toolbar toolbar;
    private BookshelfAdapter bookshelfAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_book_shelf, container, false);
        gvBookShelf = view.findViewById(R.id.gvBookShelf);
        toolbar = view.findViewById(R.id.toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);

        setBookList();

        return view;
    }

    private void setBookList() {

        ComicOffline comicOffline = ComicOffline.getInstance(getContext());
        ArrayList<Comic> comics = comicOffline.getComics(true);
        bookshelfAdapter = new BookshelfAdapter(getContext(), comics, FavFragment.this);
        gvBookShelf.setAdapter(bookshelfAdapter);
    }
}
