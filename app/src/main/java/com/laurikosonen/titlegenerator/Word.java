package com.laurikosonen.titlegenerator;

import android.support.annotation.NonNull;

import java.util.ArrayList;

public class Word {
    private final String defaultModifierMarker = "-";
    private static final String vowels1 = "aeioué";
    private static final String vowels2 = "aeiouyé";
    private static final String consonants1 = "bcdfghjklmnpqrstvwxyz";
    private static final String consonants2 = "bcdfghjklmnpqrstvwxz";

    private String word;
    private String plural;
    private String noun;
    private String presentParticiple;
    private String presentTense;
    private String pastTense;
    private String pastPerfectTense;
    private String actor;
    private String comparative;
    private String superlative;
    private String manner;
    private String possessive;
    private String[] prepositions;

    Category category;
    boolean isPlaceholder;
    boolean canHaveArticle;
    boolean canBeLast;
    private boolean canBeLowercase;
    private boolean implicitPlural;
    private ArrayList<String> wordForms;

    public Word(String word, Category category, boolean implicitPlural) {
        if (word.charAt(0) == '[') {
            isPlaceholder = true;
            this.word = word;
        }
        else {
            this.word = capitalizeFirstLetter(word);
        }

        this.category = category;
        this.implicitPlural = implicitPlural;
    }

    private String capitalizeFirstLetter(String str) {
        if (str != null && str.length() > 0) {
            if (str.length() == 1) {
                return str.toUpperCase();
            }
            else {
                //str = str.toLowerCase();
                str = ("" + str.charAt(0)).toUpperCase() + str.substring(1);
                return str;
            }
        }
        else {
            return "ERROR";
        }
    }

    boolean startsWithVowel() {
        return startsWithChar(vowels1);
    }

    boolean startsWithConsonant() {
        return startsWithChar(consonants1);
    }

    private boolean startsWithChar(String chars) {
        char startingLetter = word.toLowerCase().charAt(0);

        for (int i = 0; i < chars.length(); i++) {
            if (startingLetter == chars.charAt(i))
                return true;
        }

        return false;
    }

    private static boolean indexHasChar(StringBuilder sb, int index, String chars) {
        for (int i = 0; i < chars.length(); i++) {
            if (sb.charAt(index) == chars.charAt(i))
                return true;
        }

        return false;
    }

    static boolean trimEndVowels(StringBuilder sb) {
        return trimEndChars(sb, vowels2);
    }

    static boolean trimEndConsonants(StringBuilder sb) {
        return trimEndChars(sb, consonants2);
    }

    private static boolean trimEndChars(StringBuilder sb, String chars) {
        if (sb.length() > 1) {
            if (sb.charAt(sb.length() - 1) == '-')
                sb.deleteCharAt(sb.length() - 1);

            boolean somethingWasRemoved = false;
            for (int i = sb.length() - 1; i >= 1; i--) {
                if (indexHasChar(sb, i, chars)) {
                    sb.deleteCharAt(i);
                    somethingWasRemoved = true;
                }
                else {
                    return somethingWasRemoved;
                }
            }
        }

        return false;
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
        if (actor != null) {
            wordForms.add(actor);
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

    String getRandomWordForm(boolean lowercaseIfPossible) {
        float baseWordChance;
        switch (category.type) {
            case kind:
                baseWordChance = 0.85f;
                break;
            case personAndCreature:
            case action:
                baseWordChance = 0.6f;
                break;
            default:
                baseWordChance = 0.66f;
                break;
        }

        if (isPlaceholder || wordForms == null || wordForms.size() == 0
            || Math.random() < baseWordChance) {
            return toString(lowercaseIfPossible);
        }

        if (wordForms.size() == 1) {
            return wordForms.get(0);
        }
        else {
            int index = (int) (Math.random() * wordForms.size());
            return wordForms.get(index);
        }
    }

    boolean usesPreposition() {
        if (category.type != Category.Type.action
            || prepositions == null || prepositions.length == 0) {
            return false;
        }
        else {
            double prepositionChance = 0.4;
            return Math.random() < prepositionChance;
        }
    }

    String getRandomPreposition(boolean lowercaseIfPossible) {
        if (prepositions == null || prepositions.length == 0)
            return null;

        String preposition;

        if (prepositions.length == 1) {
            preposition = prepositions[0];
        }
        else {
            int index = (int) (Math.random() * prepositions.length);
            preposition = prepositions[index];
        }

        return (lowercaseIfPossible ? preposition : capitalizeFirstLetter(preposition));
    }

    char getLastChar() {
        return getCharFromEnd(word, 1);
    }

    static char getLastChar(String str) {
        return getCharFromEnd(str, 1);
    }

    private static char getCharFromEnd(String str, int charsFromEnd) {
        return str.charAt(str.length() - charsFromEnd);
    }

    String getPlural() {
        return plural != null ? plural : word;
    }

    String getPluralNoun() {
        if (plural != null)
            return getPlural();

        return getNoun();
    }

    String getPluralPresentParticiple() {
        if (presentParticiple != null)
            return getModifiedWord(presentParticiple, getModifierEndingWithS(presentParticiple));

        return getPlural();
    }

    String getPluralActor() {
        if (actor != null)
            return getModifiedWord(actor, getModifierEndingWithS(actor));

        return getPlural();
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

    String getActor() {
        return actor != null ? actor : word;
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

        return str + getPossessiveEnding(str);
    }

    static String getPossessiveEnding(String str) {
        if (getLastChar(str) == 's')
            return "'";
        else
            return "'s";
    }

    void setPlural(String modifier) {
        String baseWord = word;

        if (modifier == null) {
            // Sets the plural forms of Action words to use the noun as the base;
            // noun must be set first!
            if (category.type == Category.Type.action && noun != null) {
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
            modifier = getModifierEndingWithS(baseWord);
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
        else if (category.type == Category.Type.action && modifier == null)
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
        if (category.type == Category.Type.action && modifier == null)
            modifier = defaultModifierMarker;
        else if (modifier == null || modifier.length() == 0)
            return;

        if (modifier.equals(defaultModifierMarker)) {
            modifier = getModifierEndingWithS(word);
        }

        presentTense = getModifiedWord(word, modifier);
    }

    void setPastTense(String modifier, String duplicatedConsonant) {

        // Duplicated consonants
        if (modifier == null && duplicatedConsonant != null)
            modifier = duplicatedConsonant + "ed";
        // Action words have implicit past forms
        else if (category.type == Category.Type.action && modifier == null)
            modifier = defaultModifierMarker;
        else if (modifier == null || modifier.length() == 0)
            return;

        if (modifier.equals(defaultModifierMarker)) {
            char lastChar = getLastChar();
            if (lastChar == 'e') {
                modifier = "d";
            }
            else if (endsInYWhichIsNotPrecededByVowel(word)) {
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
        if (category.type == Category.Type.action && modifier == null && pastTense != null) {
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

    void setActor(String modifier, String duplicatedConsonant) {

        // Duplicated consonants
        if (modifier == null && duplicatedConsonant != null)
            modifier = duplicatedConsonant + "er";
        // Action words have implicit actor forms
        else if (category.type == Category.Type.action && modifier == null)
            modifier = defaultModifierMarker;
        else if (modifier == null || modifier.length() == 0)
            return;

        if (modifier.equals(defaultModifierMarker)) {
            char lastChar = getLastChar();
            char secondToLastChar = '_';
            if (word.length() >= 2) {
                secondToLastChar = getCharFromEnd(word, 2);
            }

            if (secondToLastChar == 'c' && lastChar == 't') {
                modifier = "or";
            }
            else if (secondToLastChar == 't' && lastChar == 'e') {
                modifier = "1or"; // Remove one char, add "OR"
            }
            else if (lastChar == 'e') {
                modifier = "r";
            }
            else if (endsInYWhichIsNotPrecededByVowel(secondToLastChar, lastChar)) {
                modifier = "1ier"; // Remove one char, add "IER"
            }
            else {
                modifier = "er";
            }
        }

        actor = getModifiedWord(word, modifier);
    }

    void setComparative(String modifier) {
        final String moreStr = "More";

        if (modifier != null && modifier.length() == 0) {
            return;
        }
        // Kind words have implicit comparative forms
        else if (category.type == Category.Type.kind && modifier == null) {
            comparative = moreStr + ' ' + word;
            return;
        }
        else if (modifier == null) {
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

        if (modifier != null && modifier.length() == 0) {
            return;
        }
        // Kind words have implicit superlative forms
        else if (category.type == Category.Type.kind && modifier == null) {
            superlative = mostStr + ' ' + word;
            return;
        }
        else if (modifier == null) {
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

        if (modifier != null && modifier.length() == 0)
            return;
        else if (category.type == Category.Type.kind && modifier == null)
            modifier = defaultModifierMarker;
        else if (modifier == null)
            return;

        if (modifier.equals(defaultModifierMarker)) {
            char lastChar = getLastChar();
            char secondToLastChar = '_';
            if (word.length() >= 2) {
                secondToLastChar = getCharFromEnd(word, 2);
            }

            if (secondToLastChar == 'l' && lastChar == 'e') {
                modifier = "1y"; // Remove one char, add "Y"
            }
            else if (secondToLastChar == 'i' && lastChar == 'c') {
                modifier = "ally"; // "ALLY"
            }
            else if (secondToLastChar == 'l' && lastChar == 'l') {
                modifier = "y"; // "Y"
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

    void setPrepositions(String prepositions, String defaultPrepositions) {

        if ((prepositions != null && prepositions.length() == 0)
            || (prepositions == null
                && (defaultPrepositions == null || defaultPrepositions.length() == 0))) {
            this.prepositions = null;
            return;
        }

        String separator= ",";

        if (defaultPrepositions != null && defaultPrepositions.length() > 0) {
            prepositions =
                (prepositions != null ? prepositions + separator : "") + defaultPrepositions;
        }

        this.prepositions = prepositions.split("[" + separator + "]");
        for (int i = 0; i < this.prepositions.length; i++) {
            this.prepositions[i] = this.prepositions[i].trim();
        }
    }

    void setArticlePossibility(boolean canHaveArticle) {
        this.canHaveArticle = canHaveArticle;
    }

    void setLastWordPossibility(boolean canBeLast) {
        this.canBeLast = canBeLast;
    }

    void setLowercasePossibility(boolean canBeLowercase) {
        this.canBeLowercase = canBeLowercase;

        if (canBeLowercase)
            canHaveArticle = false;
    }

    private String getModifierEndingWithS(String str) {
        if (str == null || str.length() == 0)
            return "S_ERROR";

        char lastChar = getLastChar(str);
        char secondToLastChar = '_';
        if (str.length() >= 2) {
            secondToLastChar = getCharFromEnd(str, 2);
        }

        if (lastChar == 's' || lastChar == 'x' || lastChar == 'z'
            || (secondToLastChar == 'c' && lastChar == 'h')
            || (secondToLastChar == 's' && lastChar == 'h')) {
            str = "es";
        } else if (endsInYWhichIsNotPrecededByVowel(secondToLastChar, lastChar)) {
            str = "1ies"; // Remove one char, add "IES"
        }
        else {
            str = "s";
        }

        return str;
    }

    private boolean endsInYWhichIsNotPrecededByVowel(String str) {
        char lastChar = getLastChar(str);
        char secondToLastChar = '_';
        if (str.length() >= 2) {
            secondToLastChar = getCharFromEnd(str, 2);
        }

        return endsInYWhichIsNotPrecededByVowel(secondToLastChar, lastChar);
    }

    private boolean endsInYWhichIsNotPrecededByVowel(char secondToLastChar, char lastChar) {
        return (lastChar == 'y'
                && secondToLastChar != 'a'
                && secondToLastChar != 'e'
                && secondToLastChar != 'o'
                && secondToLastChar != 'u');
    }

    private String getModifiedWord(String baseWord, String modifier) {
        final String identicalWordMarker = "=";
        if (modifier == null || modifier.length() == 0 || modifier.equals(identicalWordMarker)) {
            return baseWord;
        }

        String modifiedWord = baseWord;
        int removedCharCount = 0;
        char modifierFirstChar = modifier.charAt(0);

        char replaceWordMarker = '!';
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

    String toString(boolean lowercaseIfPossible) {
        if (lowercaseIfPossible && canBeLowercase)
            return word.toLowerCase();
        else
            return word;
    }
}
