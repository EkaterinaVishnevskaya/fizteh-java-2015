package ru.fizteh.fivt.students.EkaterinaVishnevskaya.TwitterStream;

import com.beust.jcommander.Parameter;

public class CommandParser {
    @Parameter(names = {"--query", "-q"},
            description = "Ключевые слова поиска.")
    private String queryWord = null;

    @Parameter(names = {"--help", "-h"}, description = "Вывод справки.")
    private boolean help = false;

    @Parameter(names = {"--stream", "-s"},
            description = "Режим Stream: равномерный вывод твитов на экран с задержкой в 1 секунду.")
    private boolean stream = false;

    @Parameter(names = {"--hideRetweets", "-hrt"},
            description = "Фильтровать ретвиты.")
    private boolean isFilter = false;

    @Parameter(names = {"--limit", "-l"},
            description = "Режим Limited: вывод только заданного количества твитов. Несовместим с режимом Stream.")
    private int number = -1;

    public boolean isHelpOn() {
        return help;
    }
    public int getNumber() {
        return number;
    }
    public boolean isStream() {
        return stream;
    }
    public boolean isLimit() {
        return (number != -1);
    }
    public String getQueryWord() {
        return queryWord;
    }
    public boolean isFilterRetweets() {
        return isFilter;
    }
}
