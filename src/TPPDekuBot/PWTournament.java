package TPPDekuBot;

import java.security.SecureRandom;
import java.util.*;

public class PWTournament {

    private PWTType type;
    private PWTClass pwtclass;
    private int partNum;
    private ArrayList<Trainer> participants;

    public PWTournament(PWTType type, PWTClass pwtclass, ArrayList<Trainer> participants) {
        this.type = type;
        this.pwtclass = pwtclass;
        this.participants = participants;
        int humanPartSize = participants.size();
        if (humanPartSize == 2) {
            partNum = 4;
        } else if (humanPartSize > 2 && humanPartSize <= 4) {
            partNum = 8;
        } else if (humanPartSize > 4 && humanPartSize <= 8) {
            partNum = 16;
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
        for (int i = 0, j = 1; i < old.size() * 2; i++, j++) {
            Trainer el = old.get(i);
            if (!el.isAI()) {
                newBracket.add(el);
                newBracket.add(PWTournament.generateTrainer(type, pwtclass));
            }
        }
        participants = newBracket;
    }

    public void doTourney(BattleBot b) {        
        b.sendMessage("#_keredau_1413645868201", "The " + type + " tournament is starting!");
        PWTRound pwtround = PWTRound.FIRST_ROUND;
        int origPart = partNum;
        while (partNum >= 2) {
            if (partNum != origPart && pwtround == PWTRound.FIRST_ROUND) {
                pwtround = PWTRound.SEMIFINALS;
            } else if (partNum == 2) {
                pwtround = PWTRound.FINALS;
            }
            for (int i = 0; i < participants.size(); i += 2) {
                Trainer p1 = participants.get(i);
                Trainer p2 = participants.get(i + 1);
                b.sendMessage("#_keredau_1413645868201", "This " + pwtround + " match in the " + type + " tournament is " + p1.getTrainerName() + " vs " + p2.getTrainerName() + "!");
                PWTBattle battle = new PWTBattle(b, p1, p2, type, pwtclass, pwtround);
                b.music.play(PWTBattle.determineMusic(battle));
                b.battle = battle;
                partNum--;
            }
        }
    }

    public static Trainer generateTrainer(PWTType type, PWTClass pwtclass) {
        String[] classes = {"Ace Trainer", "Beauty", "Biker", "Bird Keeper", "Blackbelt", "Bug Catcher", "Burglar", "Channeler", "Cue Ball", "Engineer", "Fisherman", "Gambler", "Gentleman", "Hiker", "Jr. Trainer", "Juggler", "Lass", "Leader", "PokeManiac", "Pokemon Trainer", "Psychic", "Rocker", "Sailor", "Scientist", "Super Nerd", "Swimmer", "Tamer", "Youngster", "Boarder", "Camper", "Firebreather", "Guitarist", "Kimono Girl", "Medium", "Officer", "Picnicker", "Pokefan", "Sage", "Schoolboy", "Skier", "Swimmer", "Teacher"};
        String[] names = {"Samantha", "Robby", "Ray", "Carter", "Ellen", "Iris", "Dawn", "Kirk", "Terrell", "Toby", "Simon", "Charlie", "Michael", "Phillip", "Bryan", "Russ", "Noland", "Margret", "Parker", "Daniel", "Dave", "Chase", "Ruth", "Brian", "Kevin", "Wayne", "Mike", "Bill", "Alfred", "Braxton", "Miki", "Trevor", "Seth", "Brenda", "Fidel", "Abe", "Stanly", "Al", "Fritz", "Paul", "Nicole", "Heidi", "Doug", "Horton", "Neal", "Ben", "Harris", "Paula", "Tully", "Red", "Gaven", "Izzy", "Ronald", "Jenn", "Tim", "Jerome", "Todd", "Emma", "Pat", "Rex", "Cameron", "Miguel", "Brad", "Lisa", "Samuel", "Kenneth", "Perry", "Owen", "Jody", "Eddie", "Jason", "Ernest", "Shawn", "Brooks", "Denise", "Rodney", "Yoshi", "Johnny", "Benny", "Benjamin", "Dean", "Sid", "Helenna", "Brooke", "Teru", "Keith", "Hank", "Darin", "Miller", "Oak", "Ed", "Tucker", "Wade", "Gregory", "Edward", "Jake", "Larry", "Jaclyn", "Rick", "Rich", "Cindy", "Riley", "Henry", "Diana", "Brett", "Kaylee", "Kent", "Kuni", "Brandon", "Joel", "Mitch", "Ryan", "Timmy", "Joey", "Bret", "Lung", "John", "Chow", "Sammy", "George", "Reena", "Shirley", "Leonard", "Phil", "Don", "Victoria", "Mathew", "Wilton", "Spencer", "Troy", "Tom", "Douglas", "Paton", "Kiyo", "Robin", "Miriam", "Preston", "Flint", "Gilbert", "Kelsey", "Richard", "Theresa", "Ross", "Tara", "Jimmy", "Bernie", "Colin", "James", "Harold", "Ned", "Salma", "Julia", "Sally", "Julie", "Garrett", "Peter", "Lao", "Frank", "Hope", "Ali", "Tony", "Tyler", "Bob", "Albert", "Gwen", "Li", "Charles", "Rob", "Rod", "Alice", "Alan", "Brent", "Sidney", "Caroline", "Elijah", "Ron", "Ping", "David", "Bonita", "Rachael", "Roy", "Andy", "Dirk", "Ann", "Krise", "Dudley", "Zach", "Rebecca", "Irwin", "Otis", "Connie", "Quentin", "Zeke", "Zuki", "Jonah", "Berke", "Glenn", "Walt", "Koji", "Dwayne", "Burt", "Andre", "Valerie", "Leroy", "Franklin", "Cody", "Jay", "Ralph", "Kara", "Beth", "Gina", "Ivan", "Theo", "Parry", "Joshua", "Alex", "Kenji", "Sayo", "Lois", "Allen", "Briana", "Bridget", "Russell", "Ken", "Ian", "Sean", "Fran", "Denis", "Georgia", "Willy", "Debra", "Bethany", "Edgar", "Haley", "Kate", "Stan", "Vance", "Sharon", "Aaron", "Jose", "Josh", "Boris", "Thomas", "Norman", "Jed", "Cale", "Norton", "Martin", "Megan", "Olivia", "Issac", "Jasper", "Hal", "Clyde", "Calvin", "Hillary", "Lloyd", "Lola", "Liz", "Duncan", "Scott", "Huey", "Tanya", "Arthur", "Eric", "Elliot", "Tommy", "Erik", "Edna", "Erin", "Martha", "Nob", "Daryl", "Harry", "Erick", "Andrew", "Allan", "William", "Kim", "Kenny", "Barry", "Markus", "Jerry", "Randall", "Wai", "Marc", "Timothy", "Irene", "Mark", "Nancy", "Greg", "Linda", "Warren", "Crissy", "Jessica", "Mary", "Billy", "Vincent", "Barney", "Carlene", "Herman", "Janice", "Marvin", "Hugh", "Jin", "Jim", "Jeremy", "Dale", "Hugo", "Harvey", "Elaine", "Stephen", "Michelle", "Blake", "Keigo", "Gregg", "Colette", "Danny", "Kazu", "Katie", "Naoko", "Cassie", "Masa", "Wendy", "Roxanne", "Dana", "Barny", "Cara", "Nikki", "Dylan", "Tiffany", "Reli", "Eusine", "Carrie", "Jill", "Lori", "Cybil", "Chad", "Jaime", "Carol", "Mikey", "Colton", "Joyce", "Nathan", "Darian", "Kendra", "Doris", "Ethan", "Edmond", "Kelly", "Dan", "Ajdnnw", "Steve", "Jeff", "Marcos", "Corey", "Clarissa", "Shane", "Grace", "Dillon", "Quinn", "Shannon", "Beverly", "Robert", "Angelica", "Jared", "Nate", "Joe", "Nico", "Yasu", "Kyle", "Nick", "Cal", "Arnold", "Eugene", "Derek", "Sam", "Lyle", "Raymond", "Susie", "Gaku", "Donald", "Arnie", "Virgil", "Jovan", "Jack", "Justin", "Kipp", "Bailey", "Jeffrey", "Gordon", "Walter", "Ethel", "Anthony", "Dick", "Ricky", "Ted", "Laura", "Roland", "Lamar", "Lewis", "Veronica", "Brock", "Misty", "Lt. Surge", "Erika", "Koga", "Janine", "Sabrina", "Blaine", "Giovanni", "Blue", "Falkner", "Bugsy", "Whitney", "Morty", "Chuck", "Jasmine", "Pryce", "Clair", "Roxanne", "Brawly", "Wattson", "Flannery", "Norman", "Winona", "Tate", "Liza", "Wallace", "Juan", "Roark", "Gardenia", "Maylene", "Crusher Wake", "Fantina", "Byron", "Candice", "Volkner", "Cilan", "Chili", "Cress", "Lenora", "Burgh", "Elesa", "Clay", "Skyla", "Brycen", "Drayden", "Iris", "Cheren", "Roxie", "Burgh", "Elesa", "Clay", "Marlon", "Red", "Blue", "Lance", "Steven", "Wallace", "Cynthia", "Alder", "Iris"};
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
            case "Iris":
                trnClass = "Unova Champion";
                region = Region.UNOVA;
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
