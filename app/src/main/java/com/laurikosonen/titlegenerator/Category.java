package com.laurikosonen.titlegenerator;

public class Category {
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

    Category() {
        init("ERROR", "ERR", -10);
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
        return
            id == -1 ? Category.Type.all :
            id == 0 ? Category.Type.kind :
            id == 1 ? Category.Type.concept :
            id == 2 ? Category.Type.substance :
            id == 3 ? Category.Type.thing :
            id == 4 ? Category.Type.personAndCreature :
            id == 5 ? Category.Type.action :
            id == 6 ? Category.Type.placeAndTime :
            id == 7 ? Category.Type.conjunctionAndPreposition :
            Category.Type.unknown;
    }
}
