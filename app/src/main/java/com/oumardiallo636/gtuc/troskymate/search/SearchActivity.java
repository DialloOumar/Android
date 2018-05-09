package com.oumardiallo636.gtuc.troskymate.search;

import android.app.SearchManager;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.oumardiallo636.gtuc.troskymate.Entities.Direction.Polyline;
import com.oumardiallo636.gtuc.troskymate.Map.BaseActivity;
import com.oumardiallo636.gtuc.troskymate.R;

import java.util.List;

public class SearchActivity extends BaseActivity implements SearchActivityMVP.View{

    private static final String TAG = "SearchActivity";
    private String distance;
    private List<Polyline> route;
    private String time;
    private SearchActivityMVP.Presenter presenter;
    private MenuItem search;

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);
        activateToolbar(true);

        presenter = new Presenter(this);

        if (getIntent()!=null)
        handleIntent(getIntent());

        presenter.findPlace();
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);

        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {

        if (Intent.ACTION_SEARCH.equals(intent.getAction())) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            //use the query to search your data somehow

            Toast.makeText(getApplicationContext(),query,Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        getMenuInflater().inflate(R.menu.search_menu, menu);

        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);

        SearchView searchView =(SearchView) menu.findItem(R.id.search).getActionView();
        search = menu.findItem(R.id.search);
        searchView.setIconifiedByDefault(false);

        searchView.setIconified(false);
        Log.d(TAG, "onCreateOptionsMenu: +"+searchView.isIconfiedByDefault());

        searchView.setSearchableInfo(searchManager.getSearchableInfo(getComponentName()));


        return true;
    }

    @Override
    public void submitQuery(View view) {
        presenter.findPlace();
    }

    @Override
    public void displayRecentQueries() {
        
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
    }
}
