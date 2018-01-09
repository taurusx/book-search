package com.example.android.booksearch;


/**
 * A {@link Book} object contains information related to a single book.
 * Contains information about Title, Author, Date of publication and Publisher.
 */

public class Book {

    /**
     * String value of the book title
     */
    private String mTitle;

    /**
     * String value of the book author
     */
    private String mAuthor;

    /**
     * String value of the book publisher
     */
    private String mPublisher;

    /**
     * String value of the book publication date
     */
    private String mPublishedDate;

    /**
     * Create a new {@link Book} object with initial values of title, author and publisher.
     *
     * @param title     is the title of the book
     * @param author    is the author or authors of the book
     * @param publisher is the publisher of the book
     */
    public Book(String title, String author, String publisher) {
        mTitle = title;
        mAuthor = "Author(s): " + author;
        mPublisher = "Published by: " + publisher;
    }


    /**
     * Create a new {@link Book} object with initial values of title, author and publisher.
     *
     * @param title         is the title of the book
     * @param author        is the author or authors of the book
     * @param publisher     is the publisher of the book
     * @param publishedDate is the publication date of the book
     */
    public Book(String title, String author, String publisher, String publishedDate) {
        mTitle = title;
        mAuthor = "Author(s): " + author;
        mPublisher = "Published by: " + publisher;
        mPublishedDate = publishedDate;
    }


    /**
     * Get the string value representing Title of the Book.
     *
     * @return title of the book.
     */
    public String getTitle() {
        return mTitle;
    }

    /**
     * Get the string value representing Author of the Book.
     *
     * @return author of the book.
     */
    public String getAuthor() {
        return mAuthor;
    }

    /**
     * Get the string value representing Publisher of the Book.
     *
     * @return publisher of the book.
     */
    public String getPublisher() {
        return mPublisher;
    }

    /**
     * Get the string value of the Book class representing date of publication.
     *
     * @return publication date.
     */
    public String getPublishedDate() {
        return mPublishedDate;
    }


    @Override
    public String toString() {
        return "Book{" +
                "mTitle='" + mTitle + '\'' +
                ", mAuthor='" + mAuthor + '\'' +
                ", mPublisher='" + mPublisher + '\'' +
                ", mPublishedDate='" + mPublishedDate + '\'' +
                '}';
    }

}
