package com.tiennd.searchdemo;

import android.content.SearchRecentSuggestionsProvider;

/**
 * Created by nguyentien on 12/19/17.
 */

public class MySuggestionProvider extends SearchRecentSuggestionsProvider {
    public final static String AUTHORITY = "com.tiennd.searchdemo.MySuggestionProvider";
    public final static int MODE = DATABASE_MODE_QUERIES;

    public MySuggestionProvider() {
        setupSuggestions(AUTHORITY, MODE);
    }
}
