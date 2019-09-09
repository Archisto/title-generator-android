package com.laurikosonen.titlegenerator;

public class Word {
    public String word;
    public String plural;
    public String actor;
    public String infinitive;

    public boolean isPlural;
    public String categoryName;
    public String categoryShortName;
    public int categoryId;

    public Word(String word,
                boolean isPlural,
                String categoryName,
                String categoryShortName,
                int categoryId) {
        this.word = word;
        // TODO: Parse words' modifiers in CustomXmlParser and set plural, actor and infinitive.
        //this.word = getModifiedWord("4olo");
        this.plural = word;
        this.infinitive = word;
        this.isPlural = isPlural;
        this.categoryName = categoryName;
        this.categoryShortName = categoryShortName;
        this.categoryId = categoryId;
    }

    public void setPlural(String modifier) {
        plural = getModifiedWord(modifier);
    }

    public void setActor(String modifier) {
        actor = getModifiedWord(modifier);
    }

    public void setInfinitive(String modifier) {
        infinitive = getModifiedWord(modifier);
    }

    private String getModifiedWord(String modifier) {
        String modifiedWord = word;

        if (modifier != null && modifier.length() > 0) {
            int removedLetterCount = 0;
            try {
                removedLetterCount = Integer.parseInt("" + modifier.charAt(0));
            } catch (NumberFormatException e) {
                // No removed letters
            }

            if (removedLetterCount > 0) {
                if (word.length() <= removedLetterCount) {
                    // Error message
                    modifiedWord += "ERROR(" + removedLetterCount + ")";
                }
                else {
                    modifiedWord = word.substring(0, word.length() - removedLetterCount);
                    modifiedWord += modifier.substring(1);
                }
            }
            else {
                modifiedWord += modifier;
            }
        }

        return modifiedWord;
    }

    @Override
    public String toString() {
        return word;
    }
}
