package com.laurikosonen.titlegenerator;

import android.support.annotation.NonNull;

import java.util.ArrayList;

public class Word {
    private final String defaultModifierMarker = "-";
    private final char replaceWordMarker = '!';

    private String word;
    private String plural;
    private String noun;
    private String presentParticiple;
    private String presentTense;
    private String pastTense;
    private String pastPerfectTense;
    private String comparative;
    private String superlative;
    private String manner;
    private String possessive;

    public Category category;
    public int categoryId;
    public boolean implicitPlural;
    private ArrayList<String> wordForms;

    enum Category {
        kind,
        concept,
        substance,
        thing,
        personAndCreature,
        action,
        placeAndTime,
        unknown
    }

    public Word(String word,
                int categoryId,
                boolean implicitPlural) {
        this.word = capitalizeFirstLetter(word);
        this.categoryId = categoryId;
        category = getCategoryFromInt(categoryId);
        this.implicitPlural = implicitPlural;
    }

    private String capitalizeFirstLetter(String str) {
        if (str != null && str.length() > 0) {
            StringBuilder result = new StringBuilder(str);
            result.setCharAt(0, ("" + str.charAt(0)).toUpperCase().charAt(0));
            return result.toString();
        }
        else {
            return "ERROR";
        }
    }

    void initWordFormList() {
        wordForms = new ArrayList<>();

        // The base word is used separately
        //wordForms.add(word);

        if (plural != null) {
            wordForms.add(plural);
        }
        if (noun != null) {
            wordForms.add(noun);
        }
        if (presentParticiple != null) {
            wordForms.add(presentParticiple);
        }
        if (presentTense != null) {
            wordForms.add(presentTense);
        }
        if (pastTense != null) {
            wordForms.add(pastTense);
        }
        if (pastPerfectTense != null) {
            wordForms.add(pastPerfectTense);
        }
        if (comparative != null) {
            wordForms.add(comparative);
        }
        if (superlative != null) {
            wordForms.add(superlative);
        }
        if (manner != null) {
            wordForms.add(manner);
        }

//        if (word.equals("False")) {
//            Log.d("TitleGnr", "--- " + "PL/" + plural);
//            Log.d("TitleGnr", "--- " + "NO/" + noun);
//            Log.d("TitleGnr", "--- " + "IN/" + presentParticiple);
//            Log.d("TitleGnr", "--- " + "PR/" + presentTense);
//            Log.d("TitleGnr", "--- " + "PA/" + pastTense);
//            Log.d("TitleGnr", "--- " + "PE/" + pastPerfectTense);
//            Log.d("TitleGnr", "--- " + "CO/" + comparative);
//            Log.d("TitleGnr", "--- " + "SU/" + superlative);
//            Log.d("TitleGnr", "--- " + "MA/" + manner);
//        }
    }

    static Category getCategoryFromInt(int number) {
        return
            number == 0 ? Category.kind :
            number == 1 ? Category.concept :
            number == 2 ? Category.substance :
            number == 3 ? Category.thing :
            number == 4 ? Category.personAndCreature :
            number == 5 ? Category.action :
            number == 6 ? Category.placeAndTime :
            Category.unknown;
    }

    String getRandomWordForm() {
        float baseWordChance =
            category == Category.kind ? 0.85f :
            category == Category.thing ? 0.7f :
            category == Category.personAndCreature ? 0.6f :
            category == Category.action ? 0.6f :
            0.5f;

        if (wordForms == null || wordForms.size() == 0 || Math.random() < baseWordChance) {
            return word;
        }

        if (wordForms.size() == 1) {
            return wordForms.get(0);
        }
        else {
            int index = (int) (Math.random() * wordForms.size());
            return wordForms.get(index);
        }
    }

    char getLastChar() {
        return getCharFromEnd(word, 1);
    }

    static char getLastChar(String str) {
        return getCharFromEnd(str, 1);
    }

    static char getCharFromEnd(String str, int charsFromEnd) {
        return str.charAt(str.length() - charsFromEnd);
    }

    String getPlural() {
        return plural != null ? plural : word;
    }

    String getNoun() {
        return noun != null ? noun : word;
    }

    String getPresentParticiple() {
        return presentParticiple != null ? presentParticiple : word;
    }

    String getPresentTense() {
        return presentTense != null ? presentTense : word;
    }

    String getPastTense() {
        return pastTense != null ? pastTense : word;
    }

    String getPastPerfectTense() {
        return pastPerfectTense != null ? pastPerfectTense : getPastTense();
    }

    String getComparative() {
        return comparative != null ? comparative : word;
    }

    String getSuperlative() {
        return superlative != null ? superlative : word;
    }

    String getManner() {
        return manner != null ? manner : word;
    }

    String getPossessive(String str) {
        if (possessive != null)
            return possessive;

        char lastLetter = str.charAt(str.length() - 1);
        if (lastLetter == 's')
            return str + "'";
        else
            return str + "'s";
    }

    void setPlural(String modifier) {
        String baseWord = word;

        if (modifier == null) {
            // Sets the plural forms of Action words to use the noun as the base;
            // noun must be set first!
            if (category == Category.action && noun != null) {
                baseWord = noun;
                modifier = defaultModifierMarker;
            }
            else if (implicitPlural) {
                modifier = defaultModifierMarker;
            }
            else {
                return;
            }
        }
        else if (modifier.length() == 0) {
            return;
        }

        if (modifier.equals(defaultModifierMarker)) {
            char lastChar = getLastChar(baseWord);
            char secondToLastChar = '_';
            if (baseWord.length() >= 2) {
                secondToLastChar = getCharFromEnd(baseWord, 2);
            }

            if (lastChar == 's' || lastChar == 'x' || lastChar == 'z'
                || (secondToLastChar == 'c' && lastChar == 'h')
                || (secondToLastChar == 's' && lastChar == 'h')) {
                modifier = "es";
            } else if (lastChar == 'y') {
                modifier = "1ies"; // Remove one char, add "IES"
            }
            else {
                modifier = "s";
            }
        }

        plural = getModifiedWord(baseWord, modifier);
    }

    void setNoun(String modifier) {
        if (modifier == null || modifier.length() == 0)
            return;

        if (modifier.equals(defaultModifierMarker)) {
            char lastChar = getLastChar();
            if (lastChar == 'e') {
                modifier = "1ion"; // Remove one char, add "ION"
            }
            else {
                modifier = "ion";
            }
        }

        noun = getModifiedWord(word, modifier);
    }

    void setPresentParticiple(String modifier, String duplicatedConsonant) {
        String defaultModifier = "ing";

        // Duplicated consonants
        if (modifier == null && duplicatedConsonant != null)
            modifier = duplicatedConsonant + defaultModifier;
        // Action words have implicit present participle forms
        else if (category == Category.action && modifier == null)
            modifier = defaultModifierMarker;
        else if (modifier == null || modifier.length() == 0)
            return;

        if (modifier.equals(defaultModifierMarker)) {
            char lastChar = getLastChar();
            char secondToLastChar = '_';
            if (word.length() >= 2) {
                secondToLastChar = getCharFromEnd(word, 2);
            }

            if (lastChar == 'e' && secondToLastChar != 'e') {
                modifier = "1" + defaultModifier; // Remove one char, add "ING"
            }
            else {
                modifier = defaultModifier;
            }
        }

        presentParticiple = getModifiedWord(word, modifier);
    }

    void setPresentTense(String modifier) {

        // Action words have implicit present forms
        if (category == Category.action && modifier == null)
            modifier = defaultModifierMarker;
        else if (modifier == null || modifier.length() == 0)
            return;

        if (modifier.equals(defaultModifierMarker)) {
            char lastChar = getLastChar();
            char secondToLastChar = '_';
            if (word.length() >= 2) {
                secondToLastChar = getCharFromEnd(word, 2);
            }

            if (lastChar == 's' || lastChar == 'x' || lastChar == 'z'
                || (secondToLastChar == 'c' && lastChar == 'h')
                || (secondToLastChar == 's' && lastChar == 'h')) {
                modifier = "es";
            }
            else {
                modifier = "s";
            }
        }

        presentTense = getModifiedWord(word, modifier);
    }

    void setPastTense(String modifier, String duplicatedConsonant) {

        // Duplicated consonants
        if (modifier == null && duplicatedConsonant != null)
            modifier = duplicatedConsonant + "ed";
        // Action words have implicit past forms
        else if (category == Category.action && modifier == null)
            modifier = defaultModifierMarker;
        else if (modifier == null || modifier.length() == 0)
            return;

        if (modifier.equals(defaultModifierMarker)) {
            char lastChar = getLastChar();
            if (lastChar == 'e') {
                modifier = "d";
            }
            else if (lastChar == 'y') {
                modifier = "1ied"; // Remove one char, add "IED"
            }
            else {
                modifier = "ed";
            }
        }

        pastTense = getModifiedWord(word, modifier);
    }

    void setPastPerfectTense(String modifier) {

        // Action words have implicit present participle forms.
        // Sets the past perfect tense to be same as the past tense;
        // past tense must be set first!
        if (category == Category.action && modifier == null && pastTense != null) {
            pastPerfectTense = pastTense;
            return;
        }
        else if (modifier == null || modifier.length() == 0) {
            return;
        }
        else if (modifier.equals(defaultModifierMarker)) {
            modifier = "n";
        }

        pastPerfectTense = getModifiedWord(word, modifier);
    }

    void setComparative(String modifier) {
        final String moreStr = "More";

        if ((category != Category.kind && modifier == null)
            || (modifier != null && modifier.length() == 0))
            return;

        if (modifier == null) {
            comparative = moreStr + ' ' + word;
            return;
        }
        else if (modifier.equals(defaultModifierMarker)) {
            char lastChar = getLastChar();
            if (lastChar == 'e') {
                modifier = "r";
            }
            else if (lastChar == 'y') {
                modifier = "1ier"; // Remove one char, add "IER"
            }
            else {
                modifier = "er";
            }
        }

        comparative = getModifiedWord(word, modifier);
    }

    void setSuperlative(String modifier) {
        final String mostStr = "Most";

        if ((category != Category.kind && modifier == null)
            || (modifier != null && modifier.length() == 0))
            return;

        if (modifier == null) {
            superlative = mostStr + ' ' + word;
            return;
        }
        else if (modifier.equals(defaultModifierMarker)) {
            char lastChar = getLastChar();
            if (lastChar == 'y') {
                modifier = "1iest"; // Remove one char, add "IEST"
            }
            else if (lastChar == 'e') {
                modifier = "st";
            }
            else {
                modifier = "est";
            }
        }

        superlative = getModifiedWord(word, modifier);
    }

    void setManner(String modifier) {
        if (category == Category.kind && modifier == null)
            modifier = defaultModifierMarker;
        else if (modifier == null || modifier.length() == 0)
            return;

        if (modifier.equals(defaultModifierMarker)) {
            char lastChar = getLastChar();
            char secondToLastChar = '_';
            if (word.length() >= 2) {
                secondToLastChar = getCharFromEnd(word, 2);
            }

            if (secondToLastChar == 'l' && lastChar == 'e') {
                modifier = "1y"; // Remove one char, add 'Y'
            }
            else if (secondToLastChar == 'i' && lastChar == 'c') {
                modifier = "ally";
            }
            else if (secondToLastChar == 'l' && lastChar == 'l') {
                modifier = "y";
            }
            else if (lastChar == 'y') {
                modifier = "1ily"; // Remove one char, add "ILY"
            }
            else {
                modifier = "ly"; // "LY"
            }
        }

        manner = getModifiedWord(word, modifier);
    }

    void setPossessive(String modifier) {
        if (modifier == null || modifier.length() == 0)
            possessive = null;
        else
            possessive = getModifiedWord(word, modifier);
    }

    private String getModifiedWord(String baseWord, String modifier) {
        if (modifier == null || modifier.length() == 0 || modifier.equals("=")) {
            return baseWord;
        }

        String modifiedWord = baseWord;
        int removedCharCount = 0;
        char modifierFirstChar = modifier.charAt(0);

        if (modifierFirstChar == replaceWordMarker) {
            modifiedWord = modifier.substring(1);
            return modifiedWord;
        }
        else {
            try {
                removedCharCount = Integer.parseInt("" + modifierFirstChar);
            } catch (NumberFormatException e) {
                // No removed characters
            }
        }

        if (removedCharCount > 0) {
            modifier = modifier.substring(1);

            if (baseWord.length() <= removedCharCount) {
                // Faulty modifier string error
                modifiedWord += "ERROR(-" + removedCharCount + ")";
            } else {
                modifiedWord = baseWord.substring(0, baseWord.length() - removedCharCount);
                modifiedWord += modifier;
            }
        }
        else {
            modifiedWord += modifier;
        }

        return modifiedWord;
    }

    @Override
    @NonNull
    public String toString() {
        return word;
    }
}
