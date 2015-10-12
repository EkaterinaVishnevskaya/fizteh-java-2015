package ru.fizteh.fivt.students.EkaterinaVishnevskaya.TwitterStream;

import com.beust.jcommander.JCommander;
import twitter4j.*;

import java.io.IOException;
import java.util.List;

import static java.lang.Thread.sleep;

import static ru.fizteh.fivt.students.EkaterinaVishnevskaya.TwitterStream.Printing.*;

public class TwitterStreamMain {
    public static void main(String[] args) throws IOException {
        CommandParser parsed = new CommandParser();
        JCommander command = new JCommander(parsed, args);
        if (parsed.isHelpOn()) {
            printHelp(command);
        }

        if (parsed.isStream() && parsed.isLimit()) {
            System.out.println("Несовместимые режимы Stream и Limited.");
            return;
        }
        if (parsed.isStream()) {
            streamMode(parsed.isFilterRetweets(), parsed.getQueryWord());
        }
        else{
            limitedMode(parsed.isFilterRetweets(), parsed.getNumber(), parsed.getQueryWord());
        }

    }

    private static void printHelp (JCommander command){
        command.usage();
    }

    private static void streamMode (boolean filterRetweets, String keyWord) throws IOException{
            TwitterStream twitter = new TwitterStreamFactory().getInstance();
            if (keyWord == null){
                System.out.println("Пустой запрос.");
                return;
            }
            twitter.addListener(new StatusAdapter() {
                @Override
                public void onStatus (Status tweet) {
                    try {
                        if ((tweet.isRetweet() && !filterRetweets) || !tweet.isRetweet()) {
                            printTweet(System.out, tweet, false);
                        }
                    }
                    catch (IOException e)
                    {
                        e.printStackTrace();
                    }
                    try {
                        sleep(MILISEC_IN_SEC);
                    }catch (InterruptedException e)
                    {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onException(Exception ex) {
                    ex.printStackTrace();
                }
            });

            twitter.filter(new FilterQuery().track(new String[] {keyWord}));
    }

    private static void limitedMode (boolean filterRetweets, int limit,  String keyWord) throws IOException{
        try{
            Twitter twitter = new TwitterFactory().getInstance();
            if (keyWord == null){
                System.out.println("Пустой запрос.");
                return;
            }
            Query query = new Query();
            query.setQuery(keyWord);
            QueryResult result;
            int count =0;
            boolean isAnyTweets= false;
            result = twitter.search(query);
            List<Status> tweets = result.getTweets();
            for (Status tweet : tweets) {
                if ((tweet.isRetweet() && !filterRetweets) || !tweet.isRetweet()) {
                        printTweet(System.out, tweet, true);
                        count++;
                        isAnyTweets = true;
                        if ((count==limit)&&(count!=-1)) break;
                }
            }
            if (!isAnyTweets) {
                System.out.println("\nНе найдено ни одного твита по запросу #" +
                        keyWord + ".\n");
            } else
            if ((count<limit)&&(count!=-1)){
                System.out.println("\nНайдено меньшее количество твитов по запросу #" +
                        keyWord + ".\n");
            }

        }
        catch (TwitterException te) {
            //te.printStackTrace();
            System.out.println(te.getMessage());
        }
    }
}
