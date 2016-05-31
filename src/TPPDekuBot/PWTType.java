package TPPDekuBot;

public enum PWTType {
    RANDOM, KANTO_GYM, JOHTO_GYM, HOENN_GYM, SINNOH_GYM, UNOVA_GYM, WORLD_GYM, CHAMPIONS, SPECIAL, TYPE_NORMAL, TYPE_FIRE, TYPE_FIGHTING, TYPE_WATER, TYPE_FLYING, TYPE_GRASS, TYPE_POISON, TYPE_ELECTRIC, TYPE_GROUND, TYPE_PSYCHIC, TYPE_ROCK, TYPE_ICE, TYPE_BUG, TYPE_DRAGON, TYPE_GHOST, TYPE_DARK, TYPE_STEEL, TYPE_FAIRY;

    public static PWTType typeConverter(String type) {
        type = type.toUpperCase().replace("-", "_");
        for (PWTType el : PWTType.values()) {
            if (el.name().equalsIgnoreCase(type)) {
                return el;
            }
        }
        System.err.println("Type input mismatch, inputted \"" + type + "\"");
        return null;
    }

    @Override
    public String toString() {
        String toReturn = "";
        switch (this) {
            case KANTO_GYM:
            case JOHTO_GYM:
            case HOENN_GYM:
            case SINNOH_GYM:
            case UNOVA_GYM:
            case WORLD_GYM:
                toReturn += this.name().split("_", 2)[0];
                toReturn = toReturn.charAt(0) + toReturn.substring(1).toLowerCase() + "Gym Leader's";
                break;
            case RANDOM:
            case CHAMPIONS:
            case SPECIAL:
                toReturn = toReturn.charAt(0) + toReturn.substring(1).toLowerCase();
                break;
            case TYPE_NORMAL:
            case TYPE_FIRE:
            case TYPE_FIGHTING:
            case TYPE_WATER:
            case TYPE_FLYING:
            case TYPE_GRASS:
            case TYPE_POISON:
            case TYPE_ELECTRIC:
            case TYPE_GROUND:
            case TYPE_PSYCHIC:
            case TYPE_ROCK:
            case TYPE_ICE:
            case TYPE_BUG:
            case TYPE_DRAGON:
            case TYPE_GHOST:
            case TYPE_DARK:
            case TYPE_STEEL:
            case TYPE_FAIRY:
                toReturn = this.name().split("_", 2)[1];
                toReturn = toReturn.charAt(0) + toReturn.substring(1).toLowerCase();
                break;
        }
        return toReturn;
    }
}

enum PWTClass {
    NORMAL, MASTER
}
