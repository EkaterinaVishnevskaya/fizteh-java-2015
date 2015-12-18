package ru.fizteh.fivt.students.EkaterinaVishnevskaya.ModuleTests.library;

import twitter4j.*;

import java.util.List;
import java.util.function.Consumer;

import static java.util.stream.Collectors.toList;


public class TwitterServiceImpl implements TwitterService {

    private final Twitter twitter;
    private final TwitterStream twitterStream;


    public TwitterServiceImpl(Twitter twitter, TwitterStream twitterStream) {
        this.twitter = twitter;
        this.twitterStream = twitterStream;
    }


    @Override
    public List<String> getFormattedTweets(String query) throws TwitterException {
        if (query == null) {
            throw new IllegalArgumentException("Query is required");
        }

        return twitter
                .search(new Query(query)).getTweets().stream()
                .map(this::formatTweet)
                .collect(toList());
    }

    /**
     * @param query
     * @param listener
     */
    @Override
    public void listenForTweets(String query, Consumer<String> listener) {
        twitterStream.addListener(new StatusAdapter() {
            @Override
            public void onStatus(Status status) {
                listener.accept(formatTweet(status));
            }

            @Override
            public void onException(Exception ex) {
                ex.printStackTrace();
            }
        });

        String[] s = new String[1];
        s[0] = query;
        twitterStream.filter(new FilterQuery().track(s));
    }

    private String formatTweet(Status status) {
        return "@" + status.getUser().getScreenName() + ": " + status.getText();
    }
}
