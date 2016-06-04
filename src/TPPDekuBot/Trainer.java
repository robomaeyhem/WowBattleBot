package TPPDekuBot;

import PircBot.*;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.security.SecureRandom;
import java.util.*;

public class Trainer {

    private ArrayList<Pokemon> pokemon;
    private ArrayList<Item> items;
    private String name;
    protected String trnClass;
    private int pokemonLeft;
    private int money;
    private ArrayList<Item> badges;
    private boolean defeatedChampion = false;
    private int eliteFour;
    private Region homeRegion;
    private boolean ai;

    public Trainer(String name, ArrayList<Pokemon> pokemon, ArrayList<Item> items) {
        this.pokemon = pokemon;
        this.items = items;
        this.name = name;
        this.pokemonLeft = pokemon.size();
        money = 0;
        getTrainerClass();
    }

    public Trainer(String name, ArrayList<Pokemon> pokemon, ArrayList<Item> items, int money) {
        this.pokemon = pokemon;
        this.items = items;
        this.name = name;
        this.pokemonLeft = pokemon.size();
        this.money = money;
        getTrainerClass();
    }

    public Trainer(String name, ArrayList<Pokemon> pokemon, ArrayList<Item> items, int pokemonLeft, int money, ArrayList<Item> badges, int eliteFour) {
        this.pokemon = pokemon;
        this.items = items;
        this.name = name;
        this.pokemonLeft = pokemonLeft;
        this.money = money;
        this.badges = badges;
        this.eliteFour = eliteFour;
        getTrainerClass();
    }

    public Trainer(User user, ArrayList<Pokemon> pokemon) {
        this.name = user.getNick();
        this.pokemon = pokemon;
        this.pokemonLeft = pokemon.size();
        money = 0;
        getTrainerClass();
    }

    public Trainer(String name) {
        this.name = name;
        this.pokemon = generatePokemon(1);
        this.pokemonLeft = 1;
        money = 0;
        getTrainerClass();
    }

    public Trainer(String name, int amt) {
        this.name = name;
        this.pokemon = generatePokemon(amt);
        this.pokemonLeft = amt;
        money = 0;
        getTrainerClass();
    }

    public Trainer(String name, int amt, int level) {
        this.name = name;
        this.pokemon = generatePokemon(amt, level);
        this.pokemonLeft = amt;
        money = 0;
        getTrainerClass();
    }

    public Trainer(String name, String trnClass, Region region, ArrayList<Pokemon> pokemon, boolean ai) {
        this.name = name;
        this.pokemon = pokemon;
        this.pokemonLeft = pokemon.size();
        this.homeRegion = region;
        money = 0;
        this.trnClass = trnClass;
        this.ai = ai;
    }

    public boolean hasDefeatedChampion() {
        return defeatedChampion;
    }

    public void setDefeatedChampion(boolean defeated) {
        this.defeatedChampion = defeated;
    }

    private void getTrainerClass() {
        try (FileInputStream f = new FileInputStream(BattleBot.BASE_PATH + "/trainerclasses.wdu"); ObjectInputStream o = new ObjectInputStream(f)) {
            HashMap<String, String> classes = (HashMap<String, String>) o.readObject();
            trnClass = (classes.containsKey(this.name.toLowerCase())) ? classes.get(this.name.toLowerCase()) : "Pokemon Trainer";
        } catch (Exception ex) {
            trnClass = "Pokemon Trainer";
        }
    }
    public static String getTrainerClass(String input){
        try (FileInputStream f = new FileInputStream(BattleBot.BASE_PATH + "/trainerclasses.wdu"); ObjectInputStream o = new ObjectInputStream(f)) {
            HashMap<String, String> classes = (HashMap<String, String>) o.readObject();
            return (classes.containsKey(input.toLowerCase())) ? classes.get(input.toLowerCase()) : "Pokemon Trainer";
        } catch (Exception ex) {
            return "Pokemon Trainer";
        }
    }

    private ArrayList<Pokemon> generatePokemon(int amt) {
        int level = new SecureRandom().nextInt((100 - 20) + 1) + 20;
        return generatePokemon(amt, level);
    }

    public static ArrayList<Pokemon> generatePokemon(int amt, int level) {
        if (amt > 6) {
            amt = 6;
        }
        if (amt < 1) {
            amt = 1;
        }
        ArrayList<Pokemon> toReturn = new ArrayList<>();
        for (int i = 0; i < amt; i++) {
            int pokemon = new SecureRandom().nextInt(719);
            while (Pokemon.isBannedPokemon(pokemon)) {
                pokemon = new SecureRandom().nextInt(719);
            }
            Pokemon pkm = new Pokemon(pokemon, level);
            pkm.assignMoves();
            toReturn.add(pkm);
        }
        return toReturn;
    }

    public void swapPokemon(int position1, int position2) {
        Collections.swap(pokemon, position1, position2);
    }

    public int getNumberOfPokemonRemaining() {
        pokemonLeft = 0;
        for (Pokemon el : this.pokemon) {
            if (!el.isFainted()) {
                pokemonLeft++;
            }
        }
        return pokemonLeft;
    }

    public void addPokemon(Pokemon pokemon) {
        this.pokemon.add(pokemon);
    }

    public void removePokemon(Pokemon pokemon) {
        this.pokemon.remove(pokemon);
    }

    public void removePokemon(int pos) {
        this.pokemon.remove(pos);
    }

    public void removeAllPokemon() {
        this.pokemon.clear();
    }

    public void changePokemon(int pos, Pokemon newPokemon) {
        this.pokemon.remove(pos);
        this.pokemon.add(pos, newPokemon);
    }

    public Pokemon getPokemon(int pos) {
        return this.pokemon.get(pos);
    }

    public int getMoney() {
        return money;
    }

    public void setMoney(int money) {
        this.money = money;
    }

    public void addMoney(int money) {
        this.money += money;
    }

    public void subtractMoney(int money) {
        this.money -= money;
    }

    public String getPokemonList() {
        String toReturn = "";
        int pos = 0;
        for (Pokemon el : pokemon) {
            toReturn += "(" + pos + ") ";
            String fainted = el.isFainted() ? " (FNT)" : "";
            toReturn += el.getName() + fainted + ", ";
            pos++;
        }
        toReturn = toReturn.substring(0, toReturn.length() - 2);
        return toReturn;
    }

    public ArrayList<Pokemon> getPokemon() {
        return this.pokemon;
    }

    public String getTrainerName() {
        return this.name;
    }

    public String getTrnClass() {
        return this.trnClass;
    }

    public boolean isAI() {
        return ai;
    }

    public Region getRegion() {
        return homeRegion;
    }

    public void setRegion(Region region) {
        this.homeRegion = region;
    }

    @Override
    public int hashCode() {
        int hash = 3;
        hash = 67 * hash + Objects.hashCode(this.pokemon);
        hash = 67 * hash + Objects.hashCode(this.name);
        hash = 67 * hash + this.pokemonLeft;
        return hash;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null) {
            return false;
        }
        if (getClass() != obj.getClass()) {
            return false;
        }
        final Trainer other = (Trainer) obj;
        if (this.money != other.money) {
            return false;
        }
        if (this.defeatedChampion != other.defeatedChampion) {
            return false;
        }
        if (this.eliteFour != other.eliteFour) {
            return false;
        }
        if (this.ai != other.ai) {
            return false;
        }
        if (!Objects.equals(this.name, other.name)) {
            return false;
        }
        if (!Objects.equals(this.trnClass, other.trnClass)) {
            return false;
        }
        if (!Objects.equals(this.items, other.items)) {
            return false;
        }
        if (!Objects.equals(this.badges, other.badges)) {
            return false;
        }
        return this.homeRegion == other.homeRegion;
    }



    @Override
    public String toString() {
        return this.trnClass + " " + this.name;
    }

    public static boolean isValidTrainerClass(String input) {
        String[] classes = {"Ace Trainer", "Beauty", "Biker", "Bird Keeper", "Blackbelt", "Bug Catcher", "Burglar", "Channeler", "Cue Ball", "Engineer", "Fisherman", "Gambler", "Gentleman", "Hiker", "Jr. Trainer", "Juggler", "Lass", "Leader", "PokeManiac", "Pokemon Trainer (default)", "Psychic", "Rocker", "Sailor", "Scientist", "Super Nerd", "Swimmer", "Tamer", "Youngster", "Boarder", "Camper", "Firebreather", "Guitarist", "Kimono Girl", "Medium", "Officer", "Picnicker", "Pokefan", "Sage", "Schoolboy", "Skier", "Swimmer", "Teacher", "Team Rocket", "Rocket Grunt", "Team Aqua", "Team Magma", "Team Aqua Grunt", "Team Magma Grunt", "Team Galactic", "Team Galactic Grunt", "Rocket Bomb", "Team Plasma", "Team Plasma Grunt", "Team Flare", "Team Flare Grunt"};
        for (String el : classes) {
            if (input.equals(el)) {
                return true;
            }
        }
        return false;
    }

    public static boolean isUserBot(String input) {
        String[] userBots = {"frunky5", "23forces", "groudonger"};
        for (String el : userBots) {
            if (el.equalsIgnoreCase(input)) {
                return true;
            }
        }
        return false;
    }
    

}
