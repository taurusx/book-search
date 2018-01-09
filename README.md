# BookSearch App by [taurusx](https://taurusx.github.io/)

App preview:

- Search screen and Internet check:

![BookSearch App Search Screen][screenshot-1] ![BookSearch App Internet Check][screenshot-2]

- Results:

![BookSearch App Results][screenshot-3] 

## General Description

Done as a part of [Android Basics Udacity's course][udacity-course]

BookSearch App queries **Google Books API** for information from the user input. It displays a list of results for a given search phrase as a list of books with basic information (author, title, publication info).

## Main Goals

**Main Goals** of the task:
1. Using `AsyncTask` for background thread tasks (Internet querying)
2. Connecting to a network, querying public API (*Google Books API*) and fetching results.
3. Parsing received results (JSON file) and picking correct data.
4. Create custom object to store data about single book
5. Create custom `ArrayAdapter` and populate Views in a `ListView` with given data

## Related Work

Check out my next app: [NewsFeed][news-feed].

[udacity-course]: https://eu.udacity.com/course/android-basics-nanodegree-by-google--nd803
[screenshot-1]: https://raw.githubusercontent.com/taurusx/book-search/gh-pages/assets/images/book-search-screenshot-1.png
[screenshot-2]: https://raw.githubusercontent.com/taurusx/book-search/gh-pages/assets/images/book-search-screenshot-2.png
[screenshot-3]: https://raw.githubusercontent.com/taurusx/book-search/gh-pages/assets/images/book-search-screenshot-3.png
[news-feed]: https://github.com/taurusx/news-feed

