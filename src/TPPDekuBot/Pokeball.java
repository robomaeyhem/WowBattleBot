package TPPDekuBot;

public class Pokeball extends Item {

    public enum Type {

        POKEBALL, GREATBALL, ULTRABALL, MASTERBALL, SAFARIBALL;
    }
    private double catchModifier;
    private Pokeball.Type type;

    public Pokeball(String name, ItemType iType, Pokeball.Type type) {
        super(name, iType);
        this.type = type;
        switch (type) {
            case POKEBALL:
                catchModifier = 1.0;
                break;
            case GREATBALL:
                catchModifier = 1.5;
                break;
            case ULTRABALL:
                catchModifier = 2.0;
                break;
            case MASTERBALL:
                catchModifier = 255.0;
                break;
            case SAFARIBALL:
                catchModifier = 1.5;
                break;
        }
    }

    public double getCatchModifier() {
        return catchModifier;
    }

    public Type getType() {
        return type;
    }

}
