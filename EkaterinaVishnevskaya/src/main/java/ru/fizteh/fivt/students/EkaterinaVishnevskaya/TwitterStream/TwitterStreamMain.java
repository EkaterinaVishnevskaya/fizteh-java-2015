package ru.fizteh.fivt.students.EkaterinaVishnevskaya.TwitterStream;


import twitter4j.*;


public class TwitterStreamMain {
    public static void main(String[] args) throws TwitterException {
        TwitterStream twitterStream = new TwitterStreamFactory().getInstance();
        twitterStream.addListener(new StatusAdapter() {
            @Override
            public void onStatus(Status status) {
                System.out.println(status.getUser().getName() + " : " + status.getText());
            }

            @Override
            public void onException(Exception ex) {
                ex.printStackTrace();
            }
        });
        twitterStream.filter(new FilterQuery()
                .track(new String[]{"java"})
                .language(new String[]{"ru"}));
    }
}
