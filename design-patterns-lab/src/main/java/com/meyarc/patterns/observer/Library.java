package com.meyarc.patterns.observer;

import java.util.ArrayList;
import java.util.List;

public class Library {

    private final List<BookAddedListener> listeners = new ArrayList<>();

    public void addListener(BookAddedListener listener) {
        listeners.add(listener);
    }

    public void addBook(String title) {
        for (BookAddedListener listener : listeners) {
            listener.onBookAdded(title);
        }
    }
}
