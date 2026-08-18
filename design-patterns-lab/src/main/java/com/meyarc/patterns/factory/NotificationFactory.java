package com.meyarc.patterns.factory;

public class NotificationFactory {

    public Notifier create(NotificationChannel channel) {
        return switch (channel) {
            case EMAIL -> new EmailNotifier();
            case SMS -> new SmsNotifier();
        };
    }
}
