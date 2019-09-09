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
    private static final String shortNameStr = "shortName";
    private static final String idStr = "id";
    private static final String pluralStr = "plural";
    private static final String nounStr = "noun";
    private static final String infinitiveStr = "inf";
    private static final String presentTenseStr = "pres";
    private static final String pastTenseStr = "past";
    private static final String pastPerfTenseStr = "perf";
    private static final String comparativeStr = "compa";
    private static final String superlativeStr = "super";
    private static final String mannerStr = "manner";
    private static final String catThingStr = "Thing";
    private static final String catPeopleAndCreaturesStr = "People & Creatures";
    private static final String catActionStr = "Action";

    private static int parseInt(String str) {
        int result = -1;
        if (str != null && str.length() > 0) {
            try {
                result = Integer.parseInt(str);
            }
            catch (NumberFormatException e) {
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
            String categoryName = "ERROR";
            String categoryShortName = "ERR";
            int categoryId = 0;
            boolean isPlural = false;

            while (eventType != XmlPullParser.END_DOCUMENT) {
                if (eventType == XmlPullParser.START_TAG) {
                    startTagName = parser.getName();
//                    Log.d("TitleGnr", "startTagName: " + startTagName);

                    // Parses the category
                    if (startTagName.equalsIgnoreCase(categoryStr)) {
                        String strId = parser.getAttributeValue(null, idStr);
                        categoryName = parser.getAttributeValue(null, nameStr);
                        categoryShortName = parser.getAttributeValue(null, shortNameStr);
                        categoryId = parseInt(strId);

                        // Increases the pool count if the ID is too large
                        if (pools.size() <= categoryId) {
                            List<Word> newPool = new ArrayList<>();
                            pools.add(newPool);
                        }
                    }
                    else if (startTagName.equalsIgnoreCase(wordStr)) {
                        // Creates a new word
                        Word word = getParsedWord(parser, categoryName, categoryShortName, categoryId);
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
     * @param categoryName      The Word's category's name
     * @param categoryShortName The Word's category's short name
     * @param id                The Word's category's id
     * @return                  The Word object
     */
    private static Word getParsedWord(XmlResourceParser parser,
                                      String categoryName,
                                      String categoryShortName,
                                      int id) {
        String text = parser.getAttributeValue(null, nameStr);
        if (text == null || text.isEmpty()) {
            return null;
        }

        Word.Category wordCategory =
            categoryName.equals(catActionStr) ? Word.Category.action :
            categoryName.equals(catThingStr) ? Word.Category.thing :
            categoryName.equals(catPeopleAndCreaturesStr) ? Word.Category.peopleAndCreatures :
            Word.Category.unknown;
        Word word = new Word(text, categoryName, categoryShortName, id, wordCategory);

        word.setNoun(parser.getAttributeValue(null, nounStr));
        word.setPlural(parser.getAttributeValue(null, pluralStr));
        word.setInfinitive(parser.getAttributeValue(null, infinitiveStr));
        word.setPresentTense(parser.getAttributeValue(null, presentTenseStr));
        word.setPastTense(parser.getAttributeValue(null, pastTenseStr));
        word.setPastPerfectTense(parser.getAttributeValue(null, pastPerfTenseStr));
        word.setComparative(parser.getAttributeValue(null, comparativeStr));
        word.setSuperlative(parser.getAttributeValue(null, superlativeStr));
        word.setManner(parser.getAttributeValue(null, mannerStr));
        word.initWordFormList();
        return word;
    }
}