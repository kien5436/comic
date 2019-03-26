package phamkien.comic.Fragments;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import java.util.ArrayList;

import phamkien.comic.Adapters.BookshelfAdapter;
import phamkien.comic.Models.Comic;
import phamkien.comic.R;
import phamkien.comic.Services.ComicAPI;
import phamkien.comic.Services.DataService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class HomeFragment extends android.support.v4.app.Fragment {

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

        final ComicAPI comicAPI = DataService.getService();
        Call<ArrayList<Comic>> callback = comicAPI.getComics();
        callback.enqueue(new Callback<ArrayList<Comic>>() {
            @Override
            public void onResponse(Call<ArrayList<Comic>> call, Response<ArrayList<Comic>> response) {

                ArrayList<Comic> comics = response.body();
                bookshelfAdapter = new BookshelfAdapter(getContext(), comics);
                gvBookShelf.setAdapter(bookshelfAdapter);
            }

            @Override
            public void onFailure(Call<ArrayList<Comic>> call, Throwable t) {

                Log.e("HomeFragment", "onFailure: " + t.getMessage());
                t.printStackTrace();
            }
        });
    }
}
