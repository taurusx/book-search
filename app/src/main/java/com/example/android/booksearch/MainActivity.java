package com.example.android.booksearch;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.InputFilter;
import android.text.Spanned;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    /**
     * URL for books data from the Books API (base).
     */
    private static final String BOOK_API_BASE_URL =
            "https://www.googleapis.com/books/v1/volumes?q=";

    /**
     * API KEY for BookSearch app to use in Books API requests.
     */
    private static final String API_KEY = "&key=AIzaSyBEkmk_Dxw5aySBT8HiuNG7UFJ1a_HQJZ0";

    /**
     * Search (request) phrase from EditText field.
     */
    private String searchPhrase;

    /**
     * Final search (request) URL.
     */
    private String bookRequestUrl;

    /**
     * Adapter for the list of books
     */
    private BookAdapter mAdapter;

    /**
     * TextView that is displayed when the list is empty
     */
    private TextView mEmptyStateTextView;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Restrict characters in EditText to letters, digits and spaces.
        final EditText editTextSearch = findViewById(R.id.main_edittext_search);
        InputFilter filter = new InputFilter() {
            @Override
            public CharSequence filter(CharSequence source, int start, int end,
                                       Spanned dest, int dstart, int dend) {
                for (int i = start; i < end; i++) {
                    if (!Character.isLetterOrDigit(source.charAt(i))
                            && !Character.isSpaceChar(source.charAt(i))) {
                        Toast.makeText(getApplicationContext(),
                                getString(R.string.main_toast_characters_notallowed),
                                Toast.LENGTH_SHORT).show();
                        return "";
                    }
                }
                return null;
            }
        };
        editTextSearch.setFilters(new InputFilter[]{filter});

        // Create an {@link BookAdapter}, whose data source is an empty list of {@link Book}s.
        mAdapter = new BookAdapter(this, new ArrayList<Book>());

        // Find the {@link ListView} object in the view hierarchy of the {@link Activity}.
        // declared in the layout file.
        ListView bookListView = findViewById(R.id.list);

        // Make the {@link ListView} use the {@link BookAdapter} above, so it displays
        // list items for each {@link Book} in the list.
        bookListView.setAdapter(mAdapter);

        mEmptyStateTextView = findViewById(R.id.empty_view);
        bookListView.setEmptyView(mEmptyStateTextView);

        // Set empty state text to display when the app starts.
        mEmptyStateTextView.setText(R.string.main_textview_callforaction);

        // Set search button to react when clicked and fetch the data from Internet.
        final ImageButton searchButton = findViewById(R.id.button_search);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                // Read the text from EditText input.
                searchPhrase = editTextSearch.getText().toString().trim().toLowerCase().replaceAll(" ", "+");

                // Check if the EditText is filled with requested phrase
                if (TextUtils.isEmpty(searchPhrase)) {
                    editTextSearch.setError(getString(R.string.main_search_errormessage));
                } else {

                    // Get a reference to the ConnectivityManager to check state of network connectivity
                    ConnectivityManager connMgr =
                            (ConnectivityManager) getApplicationContext().getSystemService(Context.CONNECTIVITY_SERVICE);

                    // Get details on the currently active default data network
                    NetworkInfo activeNetwork = connMgr.getActiveNetworkInfo();

                    // Method getActiveNetworkInfo() may return null - check isConnected().
                    if (activeNetwork != null &&
                            activeNetwork.isConnectedOrConnecting()) {

                        bookRequestUrl =
                                BOOK_API_BASE_URL + searchPhrase + "&maxResults=20" + API_KEY;
                        // Start the AsyncTask to fetch the book data
                        new BookAsyncTask().execute(bookRequestUrl);
                    } else {
                        mAdapter.clear();

                        // Set empty state text to display "No Internet connection."
                        mEmptyStateTextView.setText(R.string.main_textview_nointernet);
                    }
                }
            }
        });
    }

    /**
     * Update the UI with the given book information.
     */
    private void updateUi(ArrayList<Book> books) {

        // Create a new {@link BookAdapter} whose data source is a list of {@link Book}s.
        // The adapter knows how to create list items for each item in the list.
        mAdapter = new BookAdapter(this, books);

        // Make the {@link ListView} use the {@link BookAdapter} above, so it displays
        // list items for each {@link Book} in the list.
        ListView bookListView = findViewById(R.id.list);
        bookListView.setAdapter(mAdapter);
    }

    /**
     * {@link AsyncTask} to perform the network request on a background thread, and then
     * update the UI with the books in the response.
     */
    private class BookAsyncTask extends AsyncTask<String, Void, ArrayList<Book>> {

        /**
         * This method is invoked (or called) before background thread starts to prepare the app.
         * Show search indicator (will be visible only when ListView is empty).
         */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            ProgressBar progressBar = findViewById(R.id.loading_indicator);
            progressBar.setVisibility(View.VISIBLE);
        }

        /**
         * This method is invoked (or called) on a background thread, so we can perform
         * long-running operations like making a network request.
         * <p>
         * It is NOT okay to update the UI from a background thread, so we just return an
         * {@link ArrayList<Book>} object as the result.
         */
        protected ArrayList<Book> doInBackground(String... urls) {
            // Don't perform the request if there are no URLs, or the first URL is null.
            if (urls.length < 1 || urls[0] == null) {
                return null;
            }
            // Perform the HTTP request for book data and process the response.
            ArrayList<Book> result = QueryUtils.fetchBookData(urls[0]);
            return result;
        }

        /**
         * This method is invoked on the main UI thread after the background work has been
         * completed.
         * <p>
         * It IS okay to modify the UI within this method. We take the {@link ArrayList<Book>} object
         * (which was returned from the doInBackground() method) and update the views on the screen.
         */
        protected void onPostExecute(ArrayList<Book> result) {
            // Clear the adapter of previous book data
            mAdapter.clear();

            // Hide search indicator.
            ProgressBar progressBar = findViewById(R.id.loading_indicator);
            progressBar.setVisibility(View.GONE);

            // If there is a valid list of {@link Book}s, then add them to the adapter's
            // data set. This will trigger the ListView to update.
            if (result != null && !result.isEmpty()) {
                // Update the information displayed to the user.
                updateUi(result);
            } else {
                // Set empty state text to display when the app starts.
                mEmptyStateTextView.setText(R.string.main_textview_nobooks);
            }
        }
    }
}
