package TPPDekuBot;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.security.SecureRandom;
import java.util.*;

public class PWTournament {

    private PWTType type;
    private PWTClass pwtclass;
    private int partNum;
    private ArrayList<Trainer> participants;
    private ArrayList<Trainer> bots;

    public PWTournament(PWTType type, PWTClass pwtclass, ArrayList<Trainer> participants, ArrayList<Trainer> bots) {
        this.type = type;
        this.pwtclass = pwtclass;
        this.participants = participants;
        this.bots = bots;
        partNum = 8;
        if (participants.size() < 4) {
            for (int i = 0; i < 3; i++) {
                try {
                    participants.get(i);
                } catch (IndexOutOfBoundsException ex) {
                    int at = new SecureRandom().nextInt(bots.size());
                    Trainer rand = bots.get(at);
                    participants.add(rand);
                    bots.remove(at);
                }
            }
        }
    }

    public PWTType getType() {
        return type;
    }

    public PWTClass getPwtclass() {
        return pwtclass;
    }

    public int getPartNum() {
        return partNum;
    }

    public ArrayList<Trainer> getParticipants() {
        return participants;
    }

    public void addParticipant(Trainer part) {
        participants.add(part);
    }

    public void removeParticipant(Trainer part) {
        participants.remove(part);
    }

    public void removeParticipant(String name) {
        Trainer toRemove = null;
        for (Trainer el : participants) {
            if (el.getTrainerName().equalsIgnoreCase(name)) {
                toRemove = el;
                break;
            }
        }
        if (toRemove != null) {
            participants.remove(toRemove);
        }
    }

    public void arrangeBracket() {
        ArrayList<Trainer> old = (ArrayList<Trainer>) participants.clone();
        ArrayList<Trainer> newBracket = new ArrayList<>();
        for (int i = 0; i < old.size(); i++) {
            Trainer el = old.get(i);
            if (!el.isAI() || Trainer.isUserBot(el.getTrainerName())) {
                newBracket.add(el);
                int at = new SecureRandom().nextInt(bots.size());
                Trainer add = bots.get(at);
                newBracket.add(add);
                bots.remove(at);
            }
        }
        while (newBracket.size() < 8) {
            int at = new SecureRandom().nextInt(bots.size());
            Trainer add = bots.get(at);
            newBracket.add(add);
            bots.remove(at);
        }        
        participants = newBracket;
    }

    public void doTourney(BattleBot b, String channel) throws Exception {
        PWTRound pwtround = PWTRound.FIRST_ROUND;
        int loop = 0;
        while (partNum > 1) {
            if (loop == 1) {
                pwtround = PWTRound.SEMIFINALS;
            }
            if (loop == 2) {
                pwtround = PWTRound.FINALS;
            }
            ArrayList<PWTBattle> battles = new ArrayList<>();
            for (int i = 0, j = i + 1; j < participants.size(); i++, j++) {
                participants.get(i).heal();
                participants.get(j).heal();
                battles.add(new PWTBattle(b, participants.get(i), participants.get(j), type, pwtclass, pwtround));
                i = j;
                j++;
                if ((battles.size() == 4 && pwtround == PWTRound.FIRST_ROUND) || (battles.size() == 2 && pwtround == PWTRound.SEMIFINALS) || (battles.size() == 1 && pwtround == PWTRound.FINALS)) {
                    break;
                }
            }
            ArrayList<Trainer> oldList = (ArrayList<Trainer>) participants.clone();
            participants = new ArrayList<>();
            for (PWTBattle el : battles) {
                b.sendMessage(channel, pwtround.getText() + "match of the " + type + " tournament! This match is between " + el.player1 + " and " + el.player2 + "!");
                if (pwtround != PWTRound.FINALS && (el.player1.isAI() && !Trainer.isUserBot(el.player1.getTrainerName())) && (el.player2.isAI() && !Trainer.isUserBot(el.player2.getTrainerName()))) {
                    Trainer winner = new SecureRandom().nextBoolean() ? el.player1 : el.player2;
                    Trainer loser = (winner.getTrainerName().equalsIgnoreCase(el.player1.getTrainerName()) ? el.player2 : el.player1);
                    b.sendMessage(channel, "After a hard fought battle, " + winner + " was victorious over " + loser + "! PogChamp");
                    partNum--;
                    for (int i = 0; i < oldList.size(); i++) {
                        if (oldList.get(i).getTrainerName().equalsIgnoreCase(winner.getTrainerName())) {
                            participants.add(oldList.get(i));
                            break;
                        }
                    }
                } else {
                    try {
                        b.music.play(PWTBattle.determineMusic(el));
                        b.battle = el;
                        Trainer winner = el.doBattle(channel);
                        b.battle = null;
                        if (winner == null) {
                            throw new Exception("No winner in last match, aborting PWT"); //¯\_(ツ)_/¯ for now
                        } else {
                            partNum--;
                            for (int i = 0; i < oldList.size(); i++) {
                                if (oldList.get(i).getTrainerName().equalsIgnoreCase(winner.getTrainerName())) {
                                    participants.add(oldList.get(i));
                                    break;
                                }
                            }
                        }
                    } catch (Exception ex) {
                        StringWriter sw = new StringWriter();
                        PrintWriter pw = new PrintWriter(sw);
                        ex.printStackTrace(pw);
                        b.music.sendMessage(b.music.getChannel(), b.music.CHEF.mention() + " ```An error occurred in the PWT!!\n" + sw.toString() + "```");
                        return;
                    }
                }
                if (pwtround != PWTRound.FINALS) {
                    b.sendMessage(channel, "Next match starting in less than 10 seconds. Get ready!");
                    try {
                        Thread.sleep(17000);
                    } catch (Exception ex) {

                    }
                } else {
                    break;
                }
            }
            loop++;
        }
        Trainer grandWinner = participants.get(0);
        b.sendMessage(channel, grandWinner + " has won the " + type + " Pokemon World Tournament! PagChomp");
        b.inPWT = false;
    }

    public static Trainer generateTrainer(PWTType type, PWTClass pwtclass) {
        String[] classes = {"Ace Trainer", "Beauty", "Biker", "Bird Keeper", "Blackbelt", "Bug Catcher", "Burglar", "Channeler", "Cue Ball", "Engineer", "Fisherman", "Gambler", "Gentleman", "Hiker", "Jr. Trainer", "Juggler", "Lass", "PokeManiac", "Pokemon Trainer", "Psychic", "Rocker", "Sailor", "Scientist", "Super Nerd", "Swimmer", "Tamer", "Youngster", "Boarder", "Camper", "Firebreather", "Guitarist", "Kimono Girl", "Medium", "Officer", "Picnicker", "Pokefan", "Sage", "Schoolboy", "Skier", "Swimmer", "Teacher"};
        String[] names = {"Samantha", "Robby", "Ray", "Carter", "Ellen", "Dawn", "Kirk", "Terrell", "Toby", "Simon", "Charlie", "Michael", "Phillip", "Bryan", "Russ", "Noland", "Margret", "Parker", "Daniel", "Dave", "Chase", "Ruth", "Brian", "Kevin", "Wayne", "Mike", "Bill", "Alfred", "Braxton", "Miki", "Trevor", "Seth", "Brenda", "Fidel", "Abe", "Stanly", "Al", "Fritz", "Paul", "Nicole", "Heidi", "Doug", "Horton", "Neal", "Ben", "Harris", "Paula", "Tully", "Red", "Gaven", "Izzy", "Ronald", "Jenn", "Tim", "Jerome", "Todd", "Emma", "Pat", "Rex", "Cameron", "Miguel", "Brad", "Lisa", "Samuel", "Kenneth", "Perry", "Owen", "Jody", "Eddie", "Jason", "Ernest", "Shawn", "Brooks", "Denise", "Rodney", "Yoshi", "Johnny", "Benny", "Benjamin", "Dean", "Sid", "Helenna", "Brooke", "Teru", "Keith", "Hank", "Darin", "Miller", "Oak", "Ed", "Tucker", "Wade", "Gregory", "Edward", "Jake", "Larry", "Jaclyn", "Rick", "Rich", "Cindy", "Riley", "Henry", "Diana", "Brett", "Kaylee", "Kent", "Kuni", "Brandon", "Joel", "Mitch", "Ryan", "Timmy", "Joey", "Bret", "Lung", "John", "Chow", "Sammy", "George", "Reena", "Shirley", "Leonard", "Phil", "Don", "Victoria", "Mathew", "Wilton", "Spencer", "Troy", "Tom", "Douglas", "Paton", "Kiyo", "Robin", "Miriam", "Preston", "Flint", "Gilbert", "Kelsey", "Richard", "Theresa", "Ross", "Tara", "Jimmy", "Bernie", "Colin", "James", "Harold", "Ned", "Salma", "Julia", "Sally", "Julie", "Garrett", "Peter", "Lao", "Frank", "Hope", "Ali", "Tony", "Tyler", "Bob", "Albert", "Gwen", "Li", "Charles", "Rob", "Rod", "Alice", "Alan", "Brent", "Sidney", "Caroline", "Elijah", "Ron", "Ping", "David", "Bonita", "Rachael", "Roy", "Andy", "Dirk", "Ann", "Krise", "Dudley", "Zach", "Rebecca", "Irwin", "Otis", "Connie", "Quentin", "Zeke", "Zuki", "Jonah", "Berke", "Glenn", "Walt", "Koji", "Dwayne", "Burt", "Andre", "Valerie", "Leroy", "Franklin", "Cody", "Jay", "Ralph", "Kara", "Beth", "Gina", "Ivan", "Theo", "Parry", "Joshua", "Alex", "Kenji", "Sayo", "Lois", "Allen", "Briana", "Bridget", "Russell", "Ken", "Ian", "Sean", "Fran", "Denis", "Georgia", "Willy", "Debra", "Bethany", "Edgar", "Haley", "Kate", "Stan", "Vance", "Sharon", "Aaron", "Jose", "Josh", "Boris", "Thomas", "Norman", "Jed", "Cale", "Norton", "Martin", "Megan", "Olivia", "Issac", "Jasper", "Hal", "Clyde", "Calvin", "Hillary", "Lloyd", "Lola", "Liz", "Duncan", "Scott", "Huey", "Tanya", "Arthur", "Eric", "Elliot", "Tommy", "Erik", "Edna", "Erin", "Martha", "Nob", "Daryl", "Harry", "Erick", "Andrew", "Allan", "William", "Kim", "Kenny", "Barry", "Markus", "Jerry", "Randall", "Wai", "Marc", "Timothy", "Irene", "Mark", "Nancy", "Greg", "Linda", "Warren", "Crissy", "Jessica", "Mary", "Billy", "Vincent", "Barney", "Carlene", "Herman", "Janice", "Marvin", "Hugh", "Jin", "Jim", "Jeremy", "Dale", "Hugo", "Harvey", "Elaine", "Stephen", "Michelle", "Blake", "Keigo", "Gregg", "Colette", "Danny", "Kazu", "Katie", "Naoko", "Cassie", "Masa", "Wendy", "Roxanne", "Dana", "Barny", "Cara", "Nikki", "Dylan", "Tiffany", "Reli", "Eusine", "Carrie", "Jill", "Lori", "Cybil", "Chad", "Jaime", "Carol", "Mikey", "Colton", "Joyce", "Nathan", "Darian", "Kendra", "Doris", "Ethan", "Edmond", "Kelly", "Dan", "Ajdnnw", "Steve", "Jeff", "Marcos", "Corey", "Clarissa", "Shane", "Grace", "Dillon", "Quinn", "Shannon", "Beverly", "Robert", "Angelica", "Jared", "Nate", "Joe", "Nico", "Yasu", "Kyle", "Nick", "Cal", "Arnold", "Eugene", "Derek", "Sam", "Lyle", "Raymond", "Susie", "Gaku", "Donald", "Arnie", "Virgil", "Jovan", "Jack", "Justin", "Kipp", "Bailey", "Jeffrey", "Gordon", "Walter", "Ethel", "Anthony", "Dick", "Ricky", "Ted", "Laura", "Roland", "Lamar", "Lewis", "Veronica", "Brock", "Misty", "Lt. Surge", "Erika", "Koga", "Janine", "Sabrina", "Blaine", "Giovanni", "Blue", "Falkner", "Bugsy", "Whitney", "Morty", "Chuck", "Jasmine", "Pryce", "Clair", "Roxanne", "Brawly", "Wattson", "Flannery", "Norman", "Winona", "Tate", "Liza", "Wallace", "Juan", "Roark", "Gardenia", "Maylene", "Crusher Wake", "Fantina", "Byron", "Candice", "Volkner", "Cilan", "Chili", "Cress", "Lenora", "Burgh", "Elesa", "Clay", "Skyla", "Brycen", "Drayden", "Iris", "Cheren", "Roxie", "Burgh", "Elesa", "Clay", "Marlon", "Red", "Blue", "Lance", "Steven", "Wallace", "Cynthia", "Alder", "23forces", "groudonger", "frunky5"};
        String name = names[new SecureRandom().nextInt(names.length)];
        String trnClass = "";
        Region region = null;
        switch (name) {
            case "Brock":
            case "Misty":
            case "Lt. Surge":
            case "Erika":
            case "Koga":
            case "Janine":
            case "Sabrina":
            case "Blaine":
            case "Giovanni":
                trnClass = "Gym Leader";
                region = Region.KANTO;
                break;
            case "Falkner":
            case "Bugsy":
            case "Whitney":
            case "Morty":
            case "Chuck":
            case "Jasmine":
            case "Pryce":
            case "Clair":
                trnClass = "Gym Leader";
                region = Region.JOHTO;
                break;
            case "Roxanne":
            case "Brawly":
            case "Wattson":
            case "Flannery":
            case "Norman":
            case "Winona":
            case "Tate":
            case "Liza":
            case "Juan":
            case "groudonger":
                trnClass = "Gym Leader";
                region = Region.HOENN;
                break;
            case "Roark":
            case "Gardenia":
            case "Maylene":
            case "Crusher Wake":
            case "Fantina":
            case "Byron":
            case "Candice":
            case "Volkner":
                trnClass = "Gym Leader";
                region = Region.SINNOH;
                break;
            case "Cilan":
            case "Chili":
            case "Cress":
            case "Lenora":
            case "Burgh":
            case "Elesa":
            case "Clay":
            case "Skyla":
            case "Brycen":
            case "Drayden":
            case "Cheren":
            case "Roxie":
            case "Marlon": //jesus fuck unova has a lot of gym leaders
                trnClass = "Gym Leader";
                region = Region.UNOVA;
                break;
            case "frunky5":
                trnClass = "Gym Leader";
                region = Region.getRandomRegion();
                break;
            case "Blue":
                trnClass = "Kanto Champion";
                region = Region.KANTO;
                break;
            case "Lance":
                trnClass = "Johto Champion";
                region = Region.JOHTO;
                break;
            case "Red":
                trnClass = "Kanto Champion";
                region = Region.JOHTO; //this is intentional. see the actual PWT for more info
                break;
            case "Wallace":
            case "Steven":
                trnClass = "Hoenn Champion";
                region = Region.HOENN;
                break;
            case "Cynthia":
                trnClass = "Sinnoh Champion";
                region = Region.SINNOH;
                break;
            case "Alder":
                trnClass = "Unova Champion";
                region = Region.UNOVA;
                break;
            case "23forces":
                trnClass = "Elite Four";
                region = Region.getRandomRegion();
                break;
            default:
                trnClass = classes[new SecureRandom().nextInt(classes.length)];
                region = Region.getRandomRegion();
                break;
        }
        ArrayList<Pokemon> pokemon = Trainer.generatePokemon(3, 50);
        Trainer t = new Trainer(name, trnClass, region, pokemon, true);
        return t;
    }
}
