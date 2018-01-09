package com.example.android.booksearch;

import android.text.TextUtils;
import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.nio.charset.Charset;
import java.util.ArrayList;

/**
 * Helper methods related to requesting and receiving books data from Google Books API.
 */

public final class QueryUtils {

    /**
     * Tag for the log messages
     */
    public static final String LOG_TAG = QueryUtils.class.getSimpleName();

    /**
     * Create a private constructor because no one should ever create a {@link QueryUtils} object.
     * This class is only meant to hold static variables and methods, which can be accessed
     * directly from the class name QueryUtils (and an object instance of QueryUtils is not needed).
     */
    private QueryUtils() {
    }

    /**
     * Query the Books API and return an {@link ArrayList<Book>} object to represent
     * books to show to the user.
     */
    public static ArrayList<Book> fetchBookData(String requestUrl) {
        // Create URL object
        URL url = createUrl(requestUrl);

        // Perform HTTP request to the URL and receive a JSON response back
        String jsonResponse = null;
        try {
            jsonResponse = makeHttpRequest(url);
        } catch (IOException e) {
            Log.e(LOG_TAG, "Error closing input stream", e);
        }

        // Extract relevant fields from the JSON response and create an {@link ArrayList<Book>} object
        ArrayList<Book> book = extractBooksFromJson(jsonResponse);

        // Return the {@link ArrayList<Book>}
        return book;
    }

    /**
     * Returns new URL object from the given string URL.
     */
    private static URL createUrl(String stringUrl) {
        URL url = null;
        try {
            url = new URL(stringUrl);
        } catch (MalformedURLException e) {
            Log.e(LOG_TAG, "Error with creating URL ", e);
        }
        return url;
    }


    /**
     * Make an HTTP request to the given URL and return a String as the response.
     */
    private static String makeHttpRequest(URL url) throws IOException {
        String jsonResponse = "";

        // If the URL is null, then return early.
        if (url == null) {
            return jsonResponse;
        }

        HttpURLConnection urlConnection = null;
        InputStream inputStream = null;
        try {
            urlConnection = (HttpURLConnection) url.openConnection();
            urlConnection.setReadTimeout(10000 /* milliseconds */);
            urlConnection.setConnectTimeout(15000 /* milliseconds */);
            urlConnection.setRequestMethod("GET");
            urlConnection.connect();

            // If the request was successful (response code 200),
            // then read the input stream and parse the response.
            if (urlConnection.getResponseCode() == 200) {
                inputStream = urlConnection.getInputStream();
                jsonResponse = readFromStream(inputStream);
            } else {
                Log.e(LOG_TAG, "Error response code: " + urlConnection.getResponseCode());
            }
        } catch (IOException e) {
            Log.e(LOG_TAG, "Problem retrieving the JSON results.", e);
        } finally {
            if (urlConnection != null) {
                urlConnection.disconnect();
            }
            if (inputStream != null) {
                // Closing the input stream could throw an IOException, which is why
                // the makeHttpRequest(URL url) method signature specifies than an IOException
                // could be thrown.
                inputStream.close();
            }
        }
        return jsonResponse;
    }

    /**
     * Convert the {@link InputStream} into a String which contains the
     * whole JSON response from the server.
     */
    private static String readFromStream(InputStream inputStream) throws IOException {
        StringBuilder output = new StringBuilder();
        if (inputStream != null) {
            InputStreamReader inputStreamReader = new InputStreamReader(inputStream, Charset.forName("UTF-8"));
            BufferedReader reader = new BufferedReader(inputStreamReader);
            String line = reader.readLine();
            while (line != null) {
                output.append(line);
                line = reader.readLine();
            }
        }
        return output.toString();
    }

    /**
     * Return an {@link ArrayList<Book>} , a list of {@link Book} objects
     * that has been built up from parsing the input bookJSON string response.
     */
    private static ArrayList<Book> extractBooksFromJson(String bookJSON) {
        // If the JSON string is empty or null, then return early.
        if (TextUtils.isEmpty(bookJSON)) {
            return null;
        }

        // Create an empty ArrayList that we can start adding books to
        ArrayList<Book> books = new ArrayList<>();

        // Try to parse the JSON response string. If there's a problem with the way the JSON
        // is formatted, a JSONException exception object will be thrown.
        // Catch the exception so the app doesn't crash, and print the error message to the logs.
        try {

            // Parse the response given by bookJSON string and
            // build up a list of Book objects with the corresponding data.
            JSONObject rootJsonResponse = new JSONObject(bookJSON);

            // Check if any book has been found (JSONObject should contain array "items").
            // If there was none search results, stop parsing.
            if (rootJsonResponse.isNull("items")) {
                return null;
            }

            // Extract the JSONArray associated with the key called "items",
            // which represents a list of items (books).
            JSONArray bookItemsArray = rootJsonResponse.optJSONArray("items");

            // For each book in the bookArray, create a {@link Book} object
            for (int i = 0; i < bookItemsArray.length(); i++) {

                // Get a single book at position i within the list of books
                JSONObject currentBook = bookItemsArray.optJSONObject(i);

                // For a given book, extract the JSONObject associated with the
                // key called "volumeInfo", which represents a list of all volumeInfo
                // for that book.
                JSONObject volumeInfo = currentBook.optJSONObject("volumeInfo");

                // Extract the values for the keys called "title", "authors" (an array),
                // "publisher", "publishedDate" and "imageLinks" (a JSONObject containing
                // "thumbnail" key with cover's URL).
                String title = volumeInfo.optString("title");

                // Not every JSON response contains authors array,
                // show information if author name is not available:
                String authors;
                if (!volumeInfo.isNull("authors")) {
                    JSONArray authorsArray = volumeInfo.getJSONArray("authors");
                    StringBuilder stringBuilder = new StringBuilder();
                    stringBuilder.append(authorsArray.getString(0));
                    if (authorsArray.length() > 1) {
                        for (int j = 1; j < authorsArray.length(); j++) {
                            stringBuilder.append(", ").append(authorsArray.getString(j));
                        }
                    }
                    authors = stringBuilder.toString();
                } else {
                    authors = "(not available)";
                }

                // Not every JSON response contains publisher name,
                // show information if publisher name is not available:
                String publisher = volumeInfo.optString("publisher", "(not available)");


                // Create a new {@link Book} object with the title, authors,
                // and publishing info from the JSON response.
                Book nextBookObject;

                // Not every JSON response contains publishedDate information:
                if (!volumeInfo.isNull("publishedDate")) {
                    String publishedDate = volumeInfo.getString("publishedDate");
                    nextBookObject = new Book(title, authors, publisher, publishedDate);
                } else {
                    nextBookObject = new Book(title, authors, publisher);
                }

                // Add the new {@link Book} to the list of books.
                books.add(nextBookObject);
            }
            // Return the list of books {@link ArrayList<Book>}
            return books;

        } catch (JSONException e) {
            // If an error is thrown when executing any of the above statements in the "try" block,
            // catch the exception here, so the app doesn't crash. Print a log message
            // with the message from the exception.
            Log.e(LOG_TAG, "Problem parsing the book JSON results", e);
        }
        return null;
    }
}
