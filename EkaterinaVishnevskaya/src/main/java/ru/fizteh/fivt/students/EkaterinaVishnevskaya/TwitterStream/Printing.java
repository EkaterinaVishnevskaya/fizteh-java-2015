package ru.fizteh.fivt.students.EkaterinaVishnevskaya.TwitterStream;

import twitter4j.Status;

import java.io.IOException;
import java.io.PrintStream;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;

public class Printing {

    private static final  String NameColor = "\u001B[36m";
    private static final  String TimeColor = "\u001B[33m";
    private static final  String StandartColor = "\u001B[0m";
    private static final  String RTColor = "\u001B[31m";

    public static void printName(PrintStream out, String name) throws IOException {
        out.print(NameColor + "@" + name + StandartColor + ":");
    }

    public static void printTweet(PrintStream out, Status tweet, boolean time) throws IOException{
        if (time) {
            printTime(out, tweet.getCreatedAt().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime());
        };
        printName(out, tweet.getUser().getScreenName());
        if (tweet.isRetweet()) {
            out.print(RTColor+" ретвитнул ");
            String[] splited = tweet.getText().split(" ");
            printName(out, splited[1].split("@|:")[1]);
            for (int i = 2; i < splited.length; ++i)
            {
                out.println(splited[i]);
            }
            out.println();
        } else {
            out.println(tweet.getText() + " (" + tweet.getRetweetCount()+ " "+
                    RETWEET_FORMS[getCorrectForm(tweet.getRetweetCount()).getType()] + ")");

        }

    }

    private static final String[] MINUTES_FORMS = {"минуту", "минуты", "минут"};
    private static final String[] HOURS_FORMS = {"час", "часа", "часов"};
    private static final String[] DAYS_FORMS = {"день", "дня", "дней"};
    public static final String[] RETWEET_FORMS = {"ретвит", "ретвита", "ретвитов"};

    private static final String[][] TIME_FORMS = {MINUTES_FORMS, HOURS_FORMS, DAYS_FORMS};

    public enum ETime {
        MINUTE (0),
        HOUR (1),
        DAY (2);

        private int type;

        public int getType() {
            return type;
        }

        ETime(int type) {
            this.type = type;
        }
    }

    public static ETime getCorrectForm(long number) {
        if (number %10 == 1 && number % 100 != 11) {
            return ETime.MINUTE;
        }
        if (number % 10 > 1 && number % 10 < 5 && !(number % 100 >= 11 && number % 100 <= 19)) {
            return ETime.HOUR;
        }
        return ETime.DAY;
    }

    public static final int MILISEC_IN_SEC = 1000;

    public static void printTime(PrintStream out, LocalDateTime when) {
        LocalDateTime currentTime = LocalDateTime.now();
        long minute = ChronoUnit.MINUTES.between(when, currentTime);
        long hour = ChronoUnit.HOURS.between(when, currentTime);
        long day = ChronoUnit.DAYS.between(when, currentTime);
        out.print(TimeColor);
        if (minute < 1) {
            out.print("Только что ");
        }
        else if (hour  < 1) {
            out.print( minute+ DislForm(minute, Printing.ETime.MINUTE) +" назад ");
        }
        else if (day  < 1 ) {
            out.print(hour  + DislForm(hour, Printing.ETime.HOUR)+ " назад ");
        }
        else {
            if (day==1){
                out.print("Вчера");
            } else {
                out.print(day  + DislForm(day, Printing.ETime.DAY)+ " назад ");
            }
        }
        out.print(StandartColor);
    }

    public static String DislForm(long hours, ETime type){
        ETime correctForm = getCorrectForm(hours);
        return " " + TIME_FORMS[type.getType()][correctForm.getType()];
    }

}
