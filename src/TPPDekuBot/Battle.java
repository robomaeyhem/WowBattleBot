package TPPDekuBot;

public abstract class Battle {

    private Move lastMoveTeam1;
    private Move lastMoveTeam2;
    private Move delayedMove;
    private Weather weather = Weather.NORMAL;
    public BattleBot b;

    Battle(BattleBot b) {
        this.b = b;
    }

    public Move getLastMoveTeam1() {
        return lastMoveTeam1;
    }

    public Move getLastMoveTeam2() {
        return lastMoveTeam2;
    }

    public Weather getWeather() {
        return weather;
    }

    public void setWeather(Weather weather) {
        this.weather = weather;
    }

    public void setLastMoveTeam1(Move lastMoveTeam1) {
        this.lastMoveTeam1 = lastMoveTeam1;
    }

    public void setLastMoveTeam2(Move lastMoveTeam2) {
        this.lastMoveTeam2 = lastMoveTeam2;
    }

    public Move getDelayedMove() {
        return delayedMove;
    }

    public void setDelayedMove(Move delayedMove) {
        this.delayedMove = delayedMove;
    }

}
