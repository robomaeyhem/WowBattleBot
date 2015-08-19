package TPPDekuBot;

import java.io.*;
import java.util.HashMap;

public enum ExpLevel {

    SLOW, MEDIUM_SLOW, MEDIUM_FAST, FAST, ERRATIC, FLUCTUATING;

    public static int getLevel(int exp/*,ExpLevel explevel*/) {
        return (int)Math.cbrt(exp);
    }

    public static int getExp(int level) {
        return level * level * level; //topkek
    }

    public static int getBaseExp(String pokemon) throws Exception {
        HashMap<String, Integer> baseExp = null;
        try (FileInputStream f = new FileInputStream(BattleBot.BASE_PATH + "/pokemonbaseexp.wdu"); ObjectInputStream o = new ObjectInputStream(f)) {
            baseExp = (HashMap<String, Integer>) o.readObject();
        } catch (Exception ex) {
            System.err.println("[ERROR] Error reading file!! " + ex);
            throw ex;
        }
        return baseExp.get(pokemon);
    }
}
