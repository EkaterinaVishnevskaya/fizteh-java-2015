package ru.fizteh.fivt.students.EkaterinaVishnevskaya.TwitterStream;

import twitter4j.Status;

import java.io.IOException;
import java.io.PrintStream;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.temporal.ChronoUnit;

public class Printing {

    private static final  String NAME_COLOR = "\u001B[36m";
    private static final  String TIME_COLOR = "\u001B[33m";
    private static final  String STANDART_COLOR = "\u001B[0m";
    private static final  String RT_COLOR = "\u001B[31m";

    public static String formatTime(LocalDateTime when)  throws IOException {
        String out = "";
        LocalDateTime currentTime = LocalDateTime.now();
        long minute = ChronoUnit.MINUTES.between(when, currentTime);
        long hour = ChronoUnit.HOURS.between(when, currentTime);
        long day = ChronoUnit.DAYS.between(when, currentTime);
        out = out + TIME_COLOR;
        if (minute < 1) {
            out = out + "Только что ";
        } else if (hour  < 1) {
            out = out + minute + dislForm(minute, Printing.ETime.MINUTE) + " назад ";
        } else if (day < 1) {
            out = out + hour + dislForm(hour, Printing.ETime.HOUR) + " назад ";
        } else {
            if (day == 1) {
                out = out + "Вчера ";
            } else {
                out = out + day + dislForm(day, Printing.ETime.DAY) + " назад ";
            }
        }
        out = out + STANDART_COLOR;
        return out;
    }

    private static String formatName(String name) throws IOException {
        String out = "";
        out = out + NAME_COLOR + "@" + name + STANDART_COLOR + ": ";
        return out;
    }

    private static String formatStatus(String text, boolean rt, int rtCount)  throws IOException {
        String out = "";
        if (rt) {
            out = out + RT_COLOR + " ретвитнул ";
            String[] splited = text.split(" ");
            out = out + formatName(splited[1].split("@|:")[1]);
            for (int i = 2; i < splited.length; ++i) {
                out = out + splited[i] + "\n";
            }
            out = out + "\n";
        } else {
            out = out + (text + " (" + rtCount + " "
                + RETWEET_FORMS[getCorrectForm(rtCount).getType()] + ")\n");
        }
        return out;
    }

    public static void printTweet(PrintStream out, Status tweet, boolean time) throws IOException {
        if (time) {
            out.print(formatTime(tweet.getCreatedAt().toInstant().atZone(ZoneId.systemDefault()).toLocalDateTime()));
        }
        out.print(formatName(tweet.getUser().getScreenName()));
        out.print(formatStatus(tweet.getText(), tweet.isRetweet(), tweet.getRetweetCount()));
    }

    private static final int HUNDRED = 100;
    private static final int ELEVEN = 11;
    private static final int TEN = 10;
    private static final int FIVE = 5;
    private static final int ONE = 1;
    private static final int NINETEEN = 19;

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
        if (number % TEN == ONE && number % HUNDRED != ELEVEN) {
            return ETime.MINUTE;
        }
        if (number % TEN > ONE && number % TEN < FIVE
                && !(number % HUNDRED >= ELEVEN && number % HUNDRED <= NINETEEN)) {
            return ETime.HOUR;
        }
        return ETime.DAY;
    }

    public static final int MILISEC_IN_SEC = 1000;

    public static String dislForm(long hours, ETime type) {
        ETime correctForm = getCorrectForm(hours);
        return " " + TIME_FORMS[type.getType()][correctForm.getType()];
    }

}
