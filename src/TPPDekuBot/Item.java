package TPPDekuBot;

public class Item implements Cloneable {

    private ItemType type;
    private String name;

    public Item(String name, ItemType type) {
        this.name = name;
        this.type = type;
    }

    @Override
    protected Object clone() throws CloneNotSupportedException {
        return super.clone();
    }
    
}

enum ItemType {

    BALL, HP, STAT, MAIL, KEY, OTHER;
}
