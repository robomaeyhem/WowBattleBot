package TPPDekuBot;

public enum PWTRound {
    FIRST_ROUND, SECOND_ROUND, THIRD_ROUND, SEMIFINALS, FINALS;

    public String getText() {
        switch (this) {
            case FIRST_ROUND:
                return "This is a First Round ";
            case SECOND_ROUND:
                return "This is a Second Round ";
            case THIRD_ROUND:
                return "This is a Third Round ";
            case SEMIFINALS:
                return "This is a Semifinal ";
            case FINALS:
                return "PagChomp This is the FINAL ";
            default:
                return "";
        }

    }
}
