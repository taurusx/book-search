package com.example.android.booksearch;

import android.app.Activity;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * A custom adapter {@link BookAdapter} knows how to create a list item layout for each book
 * in the data source (a list of {@link Book} objects).
 * <p>
 * These list item layouts will be provided to an adapter view like ListView
 * to be populated and displayed to the user.
 */

public class BookAdapter extends ArrayAdapter<Book> {

    /**
     * This is custom constructor (it doesn't mirror a superclass constructor).
     * The context is used to inflate the layout file, and the list is the data we want
     * to populate into the lists (Book details).
     *
     * @param context The current context. Used to inflate the layout file.
     * @param books   A List of Book objects to display in a list
     */
    public BookAdapter(Activity context, ArrayList<Book> books) {
        // Here, we initialize the ArrayAdapter's internal storage for the context and the list.
        // The second argument is used when the ArrayAdapter is populating a single TextView.
        // Because this is a custom adapter for few TextViews and an ImageView, the adapter is not
        // going to use this second argument, so it can be any value. Here, we used 0.
        super(context, 0, books);
    }


    /**
     * Provides a view for an AdapterView (ListView, GridView, etc.)
     *
     * @param position    The position in the list of data that should be displayed in the
     *                    list item view.
     * @param convertView The recycled view to populate.
     * @param parent      The parent ViewGroup that is used for inflation.
     * @return The View for the position in the AdapterView.
     */
    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {

        // Check if the existing view is being reused, otherwise inflate the view
        View listItemView = convertView;
        if (listItemView == null) {
            listItemView = LayoutInflater.from(getContext()).inflate(
                    R.layout.item_book, parent, false);
        }

        // Get the {@link Book} object located at this position in the list
        Book currentBook = getItem(position);

        // Find the TextView in the item_book.xml layout with the ID textview_listview_title
        TextView titleTextView = listItemView.findViewById(R.id.textview_listview_title);
        // Get the title from the current Book object and
        // set this text on the Title TextView
        titleTextView.setText(currentBook.getTitle());

        // Find the TextView in the item_book.xml layout with the ID textview_listview_author
        TextView authorTextView = listItemView.findViewById(R.id.textview_listview_author);
        // Get the author from the current Book object and
        // set this text on the Author TextView
        authorTextView.setText(currentBook.getAuthor());

        // Find the TextView in the item_book.xml layout with the ID textview_listview_info
        TextView infoTextView = listItemView.findViewById(R.id.textview_listview_info);
        // Get the date of publication and publisher from the current Book object and
        // set this text on the Info TextView
        String infoText = currentBook.getPublisher();
        String date = currentBook.getPublishedDate();
        if (date != null) {
            infoText += ", " + date;
        }
        infoTextView.setText(infoText);


        // Return the whole list item layout (containing 3 TextViews and 1 ImageView)
        // so that it can be shown in the ListView
        return listItemView;
    }

}
