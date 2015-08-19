package TPPDekuBot;

import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.*;

public class BattleTester {

    public static void main(String[] args) {
        Pokemon user = new Pokemon(384, 79);
        Pokemon computer = new Pokemon(594, 38);
        //        ArrayList<String> possibleMoves = Pokemon.getCompatableMoves(665);
        //        System.out.println("User moves = "+possibleMoves);
        //        possibleMoves = Pokemon.getCompatableMoves(420);
        //        System.out.println("Computer moves = "+possibleMoves);
        user.assignMoves();
        computer.assignMoves();
        System.err.println("User = " + user.getMove1().getName() + ", " + user.getMove2().getName() + ", " + user.getMove3().getName() + ", " + user.getMove4().getName() + "\nComputer = " + computer.getMove1().getName() + ", " + computer.getMove2().getName() + ", " + computer.getMove3().getName() + ", " + computer.getMove4().getName());
        System.out.println("A wild " + computer.getName() + " (level " + computer.getLevel() + ") appeared! Go " + user.getName() + "! (Level " + user.getLevel() + ")");
        while (!user.isFainted() && !computer.isFainted()) {
            if (user.getStat(Stats.SPEED) > computer.getStat(Stats.SPEED)) {
                System.out.println(user.attack(computer, Move.selectBestMove(user, computer)) + "\n---");
                if (!computer.isFainted()) {
                    System.out.println(computer.attack(user, Move.selectBestMove(computer, user)) + "\n---");
                }
            } else if (user.getStat(Stats.SPEED) < computer.getStat(Stats.SPEED)) {
                System.out.println(computer.attack(user, Move.selectBestMove(computer, user)) + "\n---");
                if (!user.isFainted()) {
                    System.out.println(user.attack(computer, Move.selectBestMove(user, computer)) + "\n---");
                }
            } else {
                System.out.println(user.attack(computer, Move.selectBestMove(user, computer)) + "\n---");
                if (!computer.isFainted()) {
                    System.out.println(computer.attack(user, Move.selectBestMove(user, computer)) + "\n---");
                }
            }
        }
        if (user.isFainted()) {
            System.out.println("You lose!");
        } else {
            System.out.println("You win!");
        }
    }

    private static HashMap<Integer, ArrayList<String>> reloadPokemonMoveList() {
        try (FileInputStream fileIn = new FileInputStream(BattleBot.BASE_PATH+"/pokemonMovesList.dat"); ObjectInputStream in = new ObjectInputStream(fileIn)) {
            HashMap<Integer, ArrayList<String>> pokemon = (HashMap<Integer, ArrayList<String>>) in.readObject();
            return pokemon;
        } catch (Exception ex) {
            System.err.println("[WARNING] Failed to read the Pokemon Moves list!! " + ex);
            return null;
        }
    }
}
