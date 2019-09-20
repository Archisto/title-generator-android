package com.laurikosonen.titlegenerator;

class Category {
    String name;
    String shortName;
    int id;
    Category.Type type;

    enum Type {
        all,
        kind,
        concept,
        substance,
        thing,
        personAndCreature,
        action,
        placeAndTime,
        conjunctionAndPreposition,
        unknown
    }

    Category(String name, String shortName, int id) {
        init(name, shortName, id);
    }

    private void init(String name, String shortName, int id) {
        this.name = name;
        this.shortName = shortName;
        this.id = id;
        type = getCategoryFromId(id);
    }

    private Category.Type getCategoryFromId(int id) {
        switch (id) {
            case -1:
                return Category.Type.all;
            case 0:
                return Category.Type.kind;
            case 1:
                return Category.Type.concept;
            case 2:
                return Category.Type.substance;
            case 3:
                return Category.Type.thing;
            case 4:
                return Category.Type.personAndCreature;
            case 5:
                return Category.Type.action;
            case 6:
                return Category.Type.placeAndTime;
            case 7:
                return Category.Type.conjunctionAndPreposition;
            default:
                return Category.Type.unknown;
        }
    }
}
