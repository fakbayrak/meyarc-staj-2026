package com.meyarc.patterns;

import com.meyarc.patterns.factory.NotificationChannel;
import com.meyarc.patterns.factory.NotificationFactory;
import com.meyarc.patterns.factory.Notifier;
import com.meyarc.patterns.observer.Library;
import com.meyarc.patterns.observer.Member;
import com.meyarc.patterns.singleton.LibraryConfig;
import com.meyarc.patterns.strategy.BookReturn;
import com.meyarc.patterns.strategy.FlatLateFeeStrategy;
import com.meyarc.patterns.strategy.PerDayLateFeeStrategy;

public class Main {

    public static void main(String[] args) {
        System.out.println("== Singleton ==");
        System.out.println("Max odunc suresi: " + LibraryConfig.INSTANCE.getMaxBorrowDays() + " gun");

        System.out.println();
        System.out.println("== Factory + Observer ==");
        NotificationFactory notificationFactory = new NotificationFactory();
        Notifier emailNotifier = notificationFactory.create(NotificationChannel.EMAIL);
        Notifier smsNotifier = notificationFactory.create(NotificationChannel.SMS);

        Library library = new Library();
        library.addListener(new Member("Ayse", emailNotifier));
        library.addListener(new Member("Mehmet", smsNotifier));
        library.addBook("Effective Java");

        System.out.println();
        System.out.println("== Strategy ==");
        int daysLate = 5;
        BookReturn flatReturn = new BookReturn(new FlatLateFeeStrategy());
        BookReturn perDayReturn = new BookReturn(new PerDayLateFeeStrategy());
        System.out.println(daysLate + " gun gecikme, sabit ucret: " + flatReturn.calculateFee(daysLate) + " TL");
        System.out.println(daysLate + " gun gecikme, gun basi ucret: " + perDayReturn.calculateFee(daysLate) + " TL");
    }
}
