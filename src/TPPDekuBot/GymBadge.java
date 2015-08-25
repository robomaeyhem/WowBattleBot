/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package TPPDekuBot;

/**
 *
 * @author Michael
 */
public class GymBadge {

    private String givenBy;
    private GymBadge.Type type;

    public enum Type {

        BOULDER, CASCADE, THUNDER, RAINBOW, SOUL, MARSH, VOLCANO, EARTH, ZEPHYR, HIVE, PLAIN, FOG, STORM, MINERAL, GLACIER, RISING, STONE, KNUCKLE, DYNAMO, HEAT, BALANCE, FEATHER, MIND, RAIN, COAL, FOREST, COBBLE, FEN, RELIC, MINE, ICICLE, BEACON, TRIO, FREEZE, BASIC, TOXIC, INSECT, BOLT, QUAKE, JET, LEGEND, WAVE, BUG, CLIFF, RUMBLE, PLANT, VOLTAGE, FAIRY, PSYCHIC, ICEBERG;

        @Override
        public String toString() {
            return this.name().charAt(0) + "" + this.name().substring(1).toLowerCase() + " Badge";
        }
    }

    public GymBadge(String givenBy, Type type) {
        this.givenBy = givenBy;
        this.type = type;
    }

    public String getGivenBy() {
        return givenBy;
    }

    public Type getType() {
        return type;
    }

    @Override
    public String toString() {
        return type.toString();
    }

}
