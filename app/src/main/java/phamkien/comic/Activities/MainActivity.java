package phamkien.comic.Activities;

import android.app.SearchManager;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.NavigationView;
import android.support.v4.app.Fragment;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import phamkien.comic.Fragments.CategoryFragment;
import phamkien.comic.Fragments.FavFragment;
import phamkien.comic.Fragments.HomeFragment;
import phamkien.comic.Fragments.SearchFragment;
import phamkien.comic.Models.ComicOffline;
import phamkien.comic.R;

public class MainActivity extends AppCompatActivity {

    public static final String INTENT_SEARCH = "search";
    private static ComicOffline comicOffline = null;
    private DrawerLayout drawerLayout;
    private NavigationView nav;
    private FloatingActionButton fab;

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        drawerLayout = findViewById(R.id.drawerLayout);
        nav = findViewById(R.id.nav);
        nav.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(MenuItem menuItem) {

                setContent(menuItem);
                return true;
            }
        });

        setContent(nav.getMenu().findItem(R.id.itemHome));

        fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (comicOffline == null)
                    comicOffline = new ComicOffline();
            }
        });
    }

    private void setContent(MenuItem menuItem) {

        Fragment fragment = null;

        switch (menuItem.getItemId()) {

            case R.id.itemHome:
                fragment = new HomeFragment();
                break;
            case R.id.itemCat:
                fragment = new CategoryFragment();
                break;
            case R.id.itemFav:
                fragment = new FavFragment();
                break;
        }

        getSupportFragmentManager().beginTransaction().replace(R.id.flContent, fragment).commit();
        drawerLayout.closeDrawers();
    }

    @Override
    public void onBackPressed() {

        if (drawerLayout.isDrawerOpen(GravityCompat.START))
            drawerLayout.closeDrawers();
        else
            super.onBackPressed();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.nav_menu, menu);

        SearchManager manager = (SearchManager) getSystemService(SEARCH_SERVICE);
        final SearchView search = (SearchView) menu.findItem(R.id.search_button).getActionView();
        search.setSearchableInfo(manager.getSearchableInfo(getComponentName()));
        search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String s) {

                if (!s.trim().isEmpty()) {

                    Bundle bundle = new Bundle();
                    bundle.putString(INTENT_SEARCH, s.trim());

                    SearchFragment searchFragment = new SearchFragment();
                    searchFragment.setArguments(bundle);
                    getSupportFragmentManager().beginTransaction().replace(R.id.flContent, searchFragment).commit();

                    return true;
                }

                return false;
            }

            @Override
            public boolean onQueryTextChange(String s) {
                return false;
            }

        });

        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {

            case android.R.id.home:
                drawerLayout.openDrawer(GravityCompat.START);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
