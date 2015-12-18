package ru.fizteh.fivt.students.akormushin.moduletests.library;

import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;
import ru.fizteh.fivt.students.EkaterinaVishnevskaya.Moduletest.library.TwitterServiceImpl;
import ru.fizteh.fivt.students.EkaterinaVishnevskaya.TwitterStream.Printing;
import twitter4j.*;

import java.io.File;
import java.io.PrintStream;
import java.io.PrintWriter;
import java.lang.String;
import java.lang.System;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.mockito.Matchers.any;
import static org.mockito.Matchers.argThat;
import static org.mockito.Mockito.*;


@RunWith(MockitoJUnitRunner.class)
public class TwitterlTest {

    @Mock
    private Twitter twitter;

    @Mock
    private TwitterStream twitterStream;

    @BeforeClass
    public static void loadSampleData() {
        statuses = Twitter4jTestUtils.tweetsFromJson("/search-java-response.json");
    }

    @Before
    public void setUp() throws Exception {
        QueryResult queryResult = mock(QueryResult.class);
        when(queryResult.getTweets()).thenReturn(statuses);

        when(twitter.search(argThat(hasProperty("query", equalTo("java")))))
                .thenReturn(queryResult);

        QueryResult emptyQueryResult = mock(QueryResult.class);
        when(emptyQueryResult.getTweets()).thenReturn(Collections.emptyList());

        when(twitter.search(argThat(hasProperty("query", not(equalTo("java"))))))
                .thenReturn(emptyQueryResult);
    }

    @Test
    public void testGetFormattedTweets() throws Exception {
        List<String> tweets = twitterService.getFormattedTweets("java");

        assertThat(tweets, hasSize(15));
        assertThat(tweets, hasItems(
                "@lxwalls: RT @Space_Station: How do astronauts take their coffee?\n#NationalCoffeeDay \nhttp://t.co/fx4lQcu0Xp http://t.co/NbOZoQDags",
                "@Ankit__Tomar: #Hiring Java Lead Developer - Click here for job details : http://t.co/0slLn3YVTW"
        ));

        verify(twitter).search(argThat(hasProperty("query", equalTo("java"))));
    }


    @Test
    public void testListenForTweets() throws Exception {
        //Use ArgumentCaptor to remember argument between different stub invocations
        ArgumentCaptor<StatusListener> statusListener = ArgumentCaptor.forClass(StatusListener.class);
        //Mocking void method
        doNothing().when(twitterStream).addListener((StatusListener) statusListener.capture());
        doAnswer(i -> {
            statuses.forEach(s -> statusListener.getValue().onStatus(s));
            return null;
        }).when(twitterStream).filter(any(FilterQuery.class));

        List<String> tweets = new ArrayList<>();

        twitterService.listenForTweets("java", tweets::add);

        assertThat(tweets, hasSize(15));
        assertThat(tweets, hasItems(
                "@lxwalls: RT @Space_Station: How do astronauts take their coffee?\n#NationalCoffeeDay \nhttp://t.co/fx4lQcu0Xp http://t.co/NbOZoQDags",
                "@Ankit__Tomar: #Hiring Java Lead Developer - Click here for job details : http://t.co/0slLn3YVTW"
        ));

        verify(twitterStream).addListener((StatusListener) any(StatusAdapter.class));
        verify(twitterStream).filter(any(FilterQuery.class));
    }

    @Test
    public void testPrintingName() throws Exception {
        assert Printing.formatName("miptdihtjava2015") == "\u001B[36m" + "@mmiptdihtjava2015" + "+\u001B[0m" + ": ";
    }

    @Test
    public void testPrintingRTStatus() throws Exception {
        assert Printing.formatStatus("RT @mipt: a", true, 5) == "\u001B[31m" + " ретвитнул " + formatName("mipt") + "a";
    }

    @Test
    public void testPrintingNotRTStatus() throws Exception {
        assert Printing.formatStatus("This is java!!!", false, 5) =="This is java!!! (5 ретвитов)";
    }
}