package TPPDekuBot;

import java.security.SecureRandom;

public enum Region {
    KANTO, JOHTO, HOENN, SINNOH, UNOVA, KALOS;

    @Override
    public String toString() {
        return this.name().charAt(0) + this.name().toLowerCase().substring(1);
    }

    public static Region getRandomRegion() {
        return Region.values()[new SecureRandom().nextInt(Region.values().length)];
    }
}
