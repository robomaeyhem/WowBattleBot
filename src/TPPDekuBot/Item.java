package TPPDekuBot;

public class Item {

    private ItemType type;
    private String name;

    public Item(String name, ItemType type) {
        this.name = name;
        this.type = type;
    }
}

enum ItemType {

    BALL, HP, STAT, MAIL, KEY, OTHER;
}
