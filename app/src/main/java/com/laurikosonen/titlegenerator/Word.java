package com.laurikosonen.titlegenerator;

import java.util.ArrayList;

public class Word {
    private final String defaultModifierMarker = "-";

    public String word;
    private String plural;
    private String noun;
    private String infinitive;
    private String presentTense;
    private String pastTense;
    private String pastPerfectTense;
    private String comparative;
    private String superlative;
    private String manner;

    public String categoryName;
    public String categoryShortName;
    public int categoryId;
    public Category category;
    private ArrayList<String> wordForms;

    enum Category {
        feature,
        concept,
        substance,
        thing,
        peopleAndCreatures,
        action,
        placeAndTime,
        unknown
    }

    public Word(String word,
                String categoryName,
                String categoryShortName,
                int categoryId,
                Category category) {
        this.word = capitalizeFirstLetter(word);
        this.categoryName = categoryName;
        this.categoryShortName = categoryShortName;
        this.categoryId = categoryId;
        this.category = category;
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
        if (infinitive != null) {
            wordForms.add(infinitive);
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
//            Log.d("TitleGnr", "--- " + "IN/" + infinitive);
//            Log.d("TitleGnr", "--- " + "PR/" + presentTense);
//            Log.d("TitleGnr", "--- " + "PA/" + pastTense);
//            Log.d("TitleGnr", "--- " + "PE/" + pastPerfectTense);
//            Log.d("TitleGnr", "--- " + "CO/" + comparative);
//            Log.d("TitleGnr", "--- " + "SU/" + superlative);
//            Log.d("TitleGnr", "--- " + "MA/" + manner);
//        }
    }

    String getRandomWordForm() {
        float baseWordChance =
            category == Category.thing ? 0.7f :
            category == Category.peopleAndCreatures ? 0.6f :
            category == Category.action ? 0.6f :
            0.5f;
//        float baseWordChance = 0f;
        if (wordForms == null || wordForms.size() == 0 || Math.random() < baseWordChance) {
            return word;
        }

        //return getInfinitive();

        int index = (int) (Math.random() * wordForms.size());
        return wordForms.get(index);
    }

    String getPlural() {
        return plural != null ? plural : word;
    }

    String getNoun() {
        return noun != null ? noun : word;
    }

    String getInfinitive() {
        return infinitive != null ? infinitive : word;
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

    void setPlural(String modifier) {
        String baseWord = word;

        if (modifier == null
            && (category == Category.thing || category == Category.peopleAndCreatures)) {
            modifier = defaultModifierMarker;
        }
        // Sets the plural forms of Action words to use the noun as the base;
        // noun must be set first!
        else if (modifier == null && category == Category.action && noun != null) {
            baseWord = noun;
            modifier = defaultModifierMarker;
        }
        else if (modifier == null || modifier.length() == 0) {
            return;
        }

        String defaultModifier = "s";
        if (modifier.equals(defaultModifierMarker)) {
            char lastChar = baseWord.charAt(baseWord.length() - 1);
            char secondToLastChar = '_';
            if (baseWord.length() >= 2) {
                secondToLastChar = baseWord.charAt(baseWord.length() - 2);
            }

            if (lastChar == 's' || lastChar == 'x' || lastChar == 'z'
                || (secondToLastChar == 'c' && lastChar == 'h')
                || (secondToLastChar == 's' && lastChar == 'h')) {
                defaultModifier = "es";
            } else if (lastChar == 'y') {
                defaultModifier = "1ies"; // Remove one char, add "ies"
            }
        }

        plural = getModifiedWord(baseWord, modifier, defaultModifier);
    }

    void setNoun(String modifier) {
        if (modifier == null || modifier.length() == 0)
            return;

        String defaultModifier = "ion";
        if (modifier.equals(defaultModifierMarker)) {
            char lastChar = word.charAt(word.length() - 1);
            if (lastChar == 'e') {
                defaultModifier = "1ion"; // Remove one char, add "ion"
            }
        }

        noun = getModifiedWord(word, modifier, defaultModifier);
    }

    void setInfinitive(String modifier) {
        if (modifier == null && category == Category.action)
            modifier = defaultModifierMarker;
        else if (modifier == null || modifier.length() == 0)
            return;

        String defaultModifier = "ing";
        if (modifier.equals(defaultModifierMarker)) {
            char lastChar = word.charAt(word.length() - 1);
            if (lastChar == 'e') {
                defaultModifier = "1ing"; // Remove one char, add "ing"
            }
        }

        infinitive = getModifiedWord(word, modifier, defaultModifier);
    }

    void setPresentTense(String modifier) {
        if (modifier == null && category == Category.action)
            modifier = defaultModifierMarker;
        else if (modifier == null || modifier.length() == 0)
            return;

        String defaultModifier = "s";
        if (modifier.equals(defaultModifierMarker)) {
            char lastChar = word.charAt(word.length() - 1);
            char secondToLastChar = '_';
            if (word.length() >= 2) {
                secondToLastChar = word.charAt(word.length() - 2);
            }

            if (lastChar == 's' || lastChar == 'x' || lastChar == 'z'
                || (secondToLastChar == 'c' && lastChar == 'h')
                || (secondToLastChar == 's' && lastChar == 'h')) {
                defaultModifier = "es";
            }
        }

        presentTense = getModifiedWord(word, modifier, defaultModifier);
    }

    void setPastTense(String modifier) {
        if (modifier == null && category == Category.action)
            modifier = defaultModifierMarker;
        else if (modifier == null || modifier.length() == 0)
            return;

        String defaultModifier = "ed";
        if (modifier.equals(defaultModifierMarker)) {
            char lastChar = word.charAt(word.length() - 1);
            if (lastChar == 'e') {
                defaultModifier = "d";
            }
            else if (lastChar == 'y') {
                defaultModifier = "1ied"; // Remove one char, add "IED"
            }
        }

        pastTense = getModifiedWord(word, modifier, defaultModifier);
    }

    void setPastPerfectTense(String modifier) {
        // Sets the past perfect tense to be same as the past tense for Action words;
        // past tense must be set first!
        if (modifier == null && category == Category.action && pastTense != null) {
            pastPerfectTense = pastTense;
            return;
        }
        else if (modifier == null || modifier.length() == 0) {
            return;
        }

        pastPerfectTense = getModifiedWord(word, modifier, "n");
    }

    void setComparative(String modifier) {
        if (modifier == null)
            return;

        String defaultModifier = "er";
        if (modifier.equals(defaultModifierMarker)) {
            char lastChar = word.charAt(word.length() - 1);
            if (lastChar == 'e') {
                defaultModifier = "r";
            }
            else if (lastChar == 'y') {
                defaultModifier = "1ier"; // Remove one char, add "IER"
            }
        }

        comparative = getModifiedWord(word, modifier, defaultModifier);
    }

    void setSuperlative(String modifier) {
        if (modifier == null)
            return;

        String defaultModifier = "est";
        if (modifier.equals(defaultModifierMarker)) {
            char lastChar = word.charAt(word.length() - 1);
            if (lastChar == 'e') {
                defaultModifier = "st";
            }
            else if (lastChar == 'y') {
                defaultModifier = "1iest"; // Remove one char, add "IEST"
            }
        }

        superlative = getModifiedWord(word, modifier, defaultModifier);
    }

    void setManner(String modifier) {
        if (modifier == null)
            return;

        String defaultModifier = "ly"; // "LY"
        if (modifier.equals(defaultModifierMarker)) {
            char lastChar = word.charAt(word.length() - 1);
            char secondToLastChar = '_';
            if (word.length() >= 2) {
                secondToLastChar = word.charAt(word.length() - 2);
            }

            if (lastChar == 'y') {
                defaultModifier = "1ily"; // Remove one char, add "ILY"
            }
            if (secondToLastChar == 'l' && lastChar == 'e') {
                defaultModifier = "1y"; // Remove one char, add 'Y'
            }
            else if (secondToLastChar == 'i' && lastChar == 'c') {
                defaultModifier = "ally";
            }
            else if (secondToLastChar == 'l' && lastChar == 'l') {
                defaultModifier = "y";
            }
        }

        manner = getModifiedWord(word, modifier, defaultModifier);
    }

    private String getModifiedWord(String baseWord, String modifier, String defaultModifier) {
        if (modifier == null || modifier.equals("=")) {
            return baseWord;
        }

        String modifiedWord = baseWord;

        if (modifier.equals(defaultModifierMarker)
            && defaultModifier != null && defaultModifier.length() > 0) {
            modifier = defaultModifier;
        }

        int removedCharCount = 0;
        char modifierFirstChar = modifier.charAt(0);

        if (modifierFirstChar == '!') {
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
    public String toString() {
        return word;
    }
}
