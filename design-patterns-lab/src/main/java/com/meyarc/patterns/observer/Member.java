package com.meyarc.patterns.observer;

import com.meyarc.patterns.factory.Notifier;

public class Member implements BookAddedListener {

    private final String name;
    private final Notifier notifier;

    public Member(String name, Notifier notifier) {
        this.name = name;
        this.notifier = notifier;
    }

    @Override
    public void onBookAdded(String bookTitle) {
        notifier.send(name, "Yeni kitap eklendi: " + bookTitle);
    }
}
