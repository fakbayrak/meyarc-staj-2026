package com.meyarc.patterns.factory;

public class EmailNotifier implements Notifier {

    @Override
    public void send(String memberName, String message) {
        System.out.println("[EMAIL -> " + memberName + "] " + message);
    }
}
