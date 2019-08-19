

public final class Pawn {

    private int cPosition;
    private int playerNumber = 0;
    public String counter = "_";

    public Pawn(int position) {
        this.cPosition = position;
    }

    public Pawn(int position, int player, String counter) {
        this.setPosition(position);
        this.setPlayerNumber(player);
        this.setCounter(counter);
    }

    public int getPlayerNumber() {
        return this.playerNumber;
    }

    public void setPosition(int position){
        this.cPosition = position;
    }
    
    public void setPlayerNumber(int player) {
        this.playerNumber = player;
    }

    public String getCounter() {
        return this.counter;
    }

    public void setCounter(String counter) {
        this.counter = counter;
    }

    public int getPosition() {
        return this.cPosition;
    }
}
