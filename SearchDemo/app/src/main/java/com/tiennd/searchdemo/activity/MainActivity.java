package com.tiennd.searchdemo.activity;

import android.app.SearchManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.provider.SearchRecentSuggestions;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.tiennd.searchdemo.EndlessRecyclerViewScrollListener;
import com.tiennd.searchdemo.MySuggestionProvider;
import com.tiennd.searchdemo.ProductAdapter;
import com.tiennd.searchdemo.R;
import com.tiennd.searchdemo.ResultListener;
import com.tiennd.searchdemo.SearchTask;
import com.tiennd.searchdemo.model.Product;

import java.util.List;

public class MainActivity extends AppCompatActivity implements ResultListener, SearchView.OnQueryTextListener {

    public static final String LINK = "LINK";
    private static final String TAG = "MainActivity";
    private SearchView searchView;
    private Button button;
    private RecyclerView listSearch;
    private String text = "";
    private Handler handler = new Handler();
    private ProgressBar progress;
    private TextView tvEnd;
    private int page = 1;
    private ProductAdapter productAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Intent intent = getIntent();
        Log.d(TAG, "ACTION ONCREATE = " + intent.getAction());
        initViews();
    }

    private void initViews() {
        tvEnd = findViewById(R.id.tvend);
        progress = findViewById(R.id.progress_bar);
        searchView = findViewById(R.id.searchView);
        listSearch = findViewById(R.id.list_search);
        button = findViewById(R.id.bt_clear);
        searchView = findViewById(R.id.searchView);

        searchView.setOnQueryTextListener(this);
        SearchManager searchManager = (SearchManager) getSystemService(Context.SEARCH_SERVICE);
        searchView.setSearchableInfo(searchManager.getSearchableInfo(new ComponentName(this, MainActivity.class)));
        searchView.setIconifiedByDefault(false);

        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                clearHistory();
            }
        });
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        if (newText.length() <= 0) return false;
        text = newText;
        MainActivity.this.page = 1;
        productAdapter = null;


        handler.removeCallbacksAndMessages(null);
        handler.postDelayed(new Runnable() {
            @Override
            public void run() {
                showWaiting(true);
                saveRecent(text);
                new SearchTask(MainActivity.this).execute(text, "" + page);

            }
        }, 300);
        return true;
    }

    @Override
    public void onComplete(final List<Product> productList) {
        showWaiting(false);
        if (productList.size() > 0) {
            tvEnd.setVisibility(View.GONE);
        } else {
            tvEnd.setVisibility(View.VISIBLE);
        }

        listSearch.setHasFixedSize(true);
        LinearLayoutManager linearLayoutManager = new LinearLayoutManager(this);
        listSearch.setLayoutManager(linearLayoutManager);

        EndlessRecyclerViewScrollListener scrollListener = new EndlessRecyclerViewScrollListener(linearLayoutManager) {
            @Override
            public void onLoadMore(int page, int totalItemsCount, RecyclerView view) {
                showWaiting(true);
                Log.d(TAG, "PAGE = " + MainActivity.this.page);
                if (productList.size() > 0) {
                    MainActivity.this.page++;
                    new SearchTask(MainActivity.this).execute(text, "" + MainActivity.this.page);
                } else {
                    showWaiting(false);
                    tvEnd.setVisibility(View.GONE);
                }
            }
        };
        listSearch.addOnScrollListener(scrollListener);

        if (productAdapter == null) {
            productAdapter = new ProductAdapter(productList, this);
        } else {
            int pos = productAdapter.getItemCount();
            productAdapter.addProduct(productList);
            productAdapter.notifyDataSetChanged();
            listSearch.scrollToPosition(pos - 3);
        }
        Log.d(TAG, "COUNT ITEM = " + productAdapter.getItemCount());
        listSearch.setAdapter(productAdapter);
    }

    public void showWaiting(boolean b) {
        if (b) {
            progress.setVisibility(View.VISIBLE);
        } else {
            progress.setVisibility(View.GONE);
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        handleIntent(intent);
    }

    private void handleIntent(Intent intent) {
        if (TextUtils.equals(intent.getAction(), Intent.ACTION_SEARCH)) {
            String query = intent.getStringExtra(SearchManager.QUERY);
            if (!TextUtils.isEmpty(query)) {
                saveRecent(query);
                setTitle(query);
                searchView.setQuery(query, false);
                searchView.clearFocus();
            }
        }
    }

    private void saveRecent(String query) {
        SearchRecentSuggestions suggestions = new SearchRecentSuggestions(this,
                MySuggestionProvider.AUTHORITY,
                MySuggestionProvider.MODE);
        suggestions.saveRecentQuery(query, null);
    }


    public void clearHistory() {
        SearchRecentSuggestions suggestions = new SearchRecentSuggestions(this,
                MySuggestionProvider.AUTHORITY, MySuggestionProvider.MODE);
        suggestions.clearHistory();
    }
}
