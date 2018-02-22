package NameEntities;

import com.aliasi.chunk.RegExChunker;

class TweetSelect extends RegExChunker {

    private final static String TWEET_SELECT
            = "<\\s*TweetText[^>]*>(.*?)<\\s*/\\s*TweetText>";

    private final static String TYPE = "tweet";

    private final static double SCORE = 0.0;

    TweetSelect() {
        super(TWEET_SELECT,TYPE,SCORE);
    }
}
