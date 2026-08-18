package com.meyarc.patterns.factory;

public class SmsNotifier implements Notifier {

    @Override
    public void send(String memberName, String message) {
        System.out.println("[SMS -> " + memberName + "] " + message);
    }
}
