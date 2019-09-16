package com.laurikosonen.titlegenerator;

import android.content.res.Resources;
import android.content.res.XmlResourceParser;
import android.util.Log;
import java.util.List;
import java.util.ArrayList;
import java.io.IOException;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

class CustomXmlResourceParser {

    private static final String categoryStr = "category";
    private static final String wordStr = "word";
    private static final String nameStr = "name";
    private static final String idStr = "id";
    private static final String implicitPluralStr = "implicitPlural";
    private static final String pluralStr = "plural";
    private static final String nounStr = "noun";
    private static final String presParticipleStr = "prtc";
    private static final String duplicateConsonantStr = "dupcon";
    private static final String presentTenseStr = "pres";
    private static final String pastTenseStr = "past";
    private static final String pastPerfTenseStr = "perf";
    private static final String actorStr = "actor";
    private static final String comparativeStr = "compa";
    private static final String superlativeStr = "super";
    private static final String mannerStr = "manner";
    private static final String possessiveStr = "poss";
    private static final String prepositionStr = "prepos";
    private static final String defaultPreposStr = "defaultPrepos";
    private static final String noDefPreposStr = "noDefPrepos";

    private static int parseInt(String str) {
        int result = -1;
        if (str != null && str.length() > 0) {
            try {
                result = Integer.parseInt(str);
            }
            catch (NumberFormatException e) {
                // Not a number
                e.printStackTrace();
            }
        }

        return result;
    }

    static void parseWords(Resources resources,
                           int resourceID,
                           List<List<Word>> pools,
                           List<Word> poolAll) {
        XmlResourceParser parser = resources.getXml(resourceID);

        try {
            parser.next();
            int eventType = parser.getEventType();
            String startTagName = "_";
            int categoryId = 0;
            boolean isImplicitPlural = false;
            String defaultPreposition = "";

            while (eventType != XmlPullParser.END_DOCUMENT) {
                if (eventType == XmlPullParser.START_TAG) {
                    startTagName = parser.getName();
//                    Log.d("TitleGnr", "startTagName: " + startTagName);

                    // Parses the category
                    if (startTagName.equalsIgnoreCase(categoryStr)) {
                        String strId = parser.getAttributeValue(null, idStr);
                        categoryId = parseInt(strId);
                        isImplicitPlural =
                            parser.getAttributeValue(null, implicitPluralStr) != null;
                        defaultPreposition = parser.getAttributeValue(null, defaultPreposStr);

                        // Increases the pool count if the ID is too large
                        if (pools.size() <= categoryId) {
                            List<Word> newPool = new ArrayList<>();
                            pools.add(newPool);
                        }
                    }
                    else if (startTagName.equalsIgnoreCase(wordStr)) {
                        // Creates a new word
                        Word word = getParsedWord(parser, categoryId, isImplicitPlural, defaultPreposition);
                        if (word != null) {
                            pools.get(categoryId).add(word);
                            poolAll.add(word);
                        }
                    }
                }

                eventType = parser.next();
            }
        }
        catch (IOException e) {
            e.printStackTrace();
        }
        catch (XmlPullParserException e) {
            e.printStackTrace();
        }
        finally {
            Log.d("TitleGnr", "Word parsing complete");
        }
    }

    /**
     * Creates and returns a Word object.
     * @param categoryId    The word's category's id
     * @return              The Word object
     */
    private static Word getParsedWord(XmlResourceParser parser,
                                      int categoryId,
                                      boolean isImplicitPlural,
                                      String defaultPreposition) {
        String text = parser.getAttributeValue(null, nameStr);
        if (text == null || text.isEmpty()) {
            return null;
        }

        Word word = new Word(text, categoryId, isImplicitPlural);

        word.setNoun(parser.getAttributeValue(null, nounStr));
        word.setPlural(parser.getAttributeValue(null, pluralStr));
        word.setPresentParticiple(parser.getAttributeValue(null, presParticipleStr),
            parser.getAttributeValue(null, duplicateConsonantStr));
        word.setPresentTense(parser.getAttributeValue(null, presentTenseStr));
        word.setPastTense(parser.getAttributeValue(null, pastTenseStr),
            parser.getAttributeValue(null, duplicateConsonantStr));
        word.setPastPerfectTense(parser.getAttributeValue(null, pastPerfTenseStr));
        word.setActor(parser.getAttributeValue(null, actorStr),
            parser.getAttributeValue(null, duplicateConsonantStr));
        word.setComparative(parser.getAttributeValue(null, comparativeStr));
        word.setSuperlative(parser.getAttributeValue(null, superlativeStr));
        word.setManner(parser.getAttributeValue(null, mannerStr));
        word.setPossessive(parser.getAttributeValue(null, possessiveStr));
        word.setPrepositions(parser.getAttributeValue(null, prepositionStr),
            parser.getAttributeValue(null, noDefPreposStr) != null ? null : defaultPreposition);

        word.initWordFormList();
        return word;
    }
}