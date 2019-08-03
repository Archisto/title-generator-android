package com.laurikosonen.titlegenerator;

public class Word {
    public String word;
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
        this.isPlural = isPlural;
        this.categoryName = categoryName;
        this.categoryShortName = categoryShortName;
        this.categoryId = categoryId;
    }

    @Override
    public String toString() {
        return word;
    }
}
