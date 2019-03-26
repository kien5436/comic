package phamkien.comic.Fragments;

import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.GridView;

import java.util.ArrayList;
import java.util.List;

import phamkien.comic.Adapters.BookshelfAdapter;
import phamkien.comic.Models.Category;
import phamkien.comic.Models.Comic;
import phamkien.comic.R;
import phamkien.comic.Services.ComicAPI;
import phamkien.comic.Services.DataService;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class CategoryFragment extends Fragment {

    private ComicAPI comicAPI = DataService.getService();
    private Toolbar toolbar;
    private GridView gvBookShelf;
    private BookshelfAdapter bookshelfAdapter;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_book_shelf, container, false);
        toolbar = view.findViewById(R.id.toolbar);
        ((AppCompatActivity) getActivity()).setSupportActionBar(toolbar);
        gvBookShelf = view.findViewById(R.id.gvBookShelf);

        showCatList();

        return view;
    }

    private void showCatList() {

        Call<ArrayList<Category>> callback = comicAPI.getCategories();
        callback.enqueue(new Callback<ArrayList<Category>>() {
            @Override
            public void onResponse(Call<ArrayList<Category>> call, Response<ArrayList<Category>> response) {

                final ArrayList<Category> categories = response.body();
                List<String> catName = new ArrayList<>();

                for (int i = 0; i < categories.size(); i++)
                    catName.add(categories.get(i).getName());

                final ArrayAdapter<String> categoryAdapter = new ArrayAdapter<>(getContext(), R.layout.adapter_list, R.id.tvListItem, catName);
                AlertDialog.Builder builder = new AlertDialog.Builder(getContext());
                builder.setTitle("Danh sách thể loại")
                    .setAdapter(categoryAdapter, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            setBookList(categories.get(which).getCid());
                        }
                    }).show();
            }

            @Override
            public void onFailure(Call<ArrayList<Category>> call, Throwable t) {

                Log.e("CategoryFragment", "onFailure: ", t);
                t.printStackTrace();
            }
        });
    }

    private void setBookList(String cid) {

        Call<ArrayList<Comic>> callback = comicAPI.getComicsByCategory(cid);
        callback.enqueue(new Callback<ArrayList<Comic>>() {
            @Override
            public void onResponse(Call<ArrayList<Comic>> call, Response<ArrayList<Comic>> response) {

                ArrayList<Comic> comics = response.body();
                bookshelfAdapter = new BookshelfAdapter(getContext(), comics);
                gvBookShelf.setAdapter(bookshelfAdapter);
            }

            @Override
            public void onFailure(Call<ArrayList<Comic>> call, Throwable t) {

                Log.e("CategoryFragment", "onFailure: ", t);
                t.printStackTrace();
            }
        });
    }
}
