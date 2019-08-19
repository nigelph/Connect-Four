import java.io.FileNotFoundException;
import java.util.ArrayList;
import java.util.HashMap;

public final class ConnectFourBoard {

    private HashMap boardMap;
    private String[][] chessBoard;
    private Pawn[] pawn;
    private ArrayList<String> dataRow;

    private final int MAX_SIZE = 42;
    private final int MAX_ROW = 6;
    private final int MAX_COLUMN = 7;

    public ConnectFourBoard() {
        this.initialChessBoard();
        this.initialHashMap();
        this.initialDataRow();
    }
    
    public void initialDataRow()
    {
        this.dataRow = new ArrayList<>();
    }
    
    public void initialChessBoard() {
        this.chessBoard = new String[this.MAX_ROW][this.MAX_COLUMN];
        for (int horizontal = 0; horizontal < this.MAX_ROW; horizontal++) {
            for (int vertical = 0; vertical < this.MAX_COLUMN; vertical++) {
                this.chessBoard[horizontal][vertical] = " ";
            }
        }
    }

    public void setChessBoard(int cPosition, String counter) {
        this.chessBoard[cPosition / 7][cPosition % 7] = counter;
    }

    public void setPawnArrayItem(int position, int player, String counter) {
        this.pawn[position] = new Pawn(position, player, counter);
    }

    public Pawn getPawnArrayItem(int sequence_number) {
        return this.pawn[sequence_number];
    }

    public void initialHashMap() {
        this.boardMap = new HashMap();
        this.pawn = new Pawn[this.MAX_SIZE];
        for (int index = 0; index < this.MAX_SIZE; index++) {
            this.pawn[index] = new Pawn(index);
            this.boardMap.put(index, pawn[index].getPlayerNumber());
        }
    }

    public HashMap getHashPawn() {
        return this.boardMap;
    }

    public boolean checkDrop(int sequence_number, int col_input, int playerNum, String counter) throws FileNotFoundException {
        if ((int) (this.boardMap.get(col_input)) != 0) {
            return false;
        } else {
            for (int index = col_input + 35; index >= col_input; index = index - 7) {
                if ((int) (this.boardMap.get(index)) == 0) {
                    this.pawn[index].setPlayerNumber(playerNum);
                    this.pawn[index].setCounter(counter);
                    this.boardMap.replace(index, playerNum);
                    this.chessBoard[index / 7][index % 7] = counter;
                    
                    this.dataRow.add(sequence_number + "," + index + "," + playerNum + "," + counter );
                    break;
                }
            }
            return true;
        }
    }
    
    public ArrayList<String> getDataRow()
    {
        return this.dataRow;
    }
    
    public void printBoard(HashMap board) {
        System.out.println("|1|2|3|4|5|6|7|");
        for (int index = 0; index < this.MAX_SIZE; index++) {
            if (index % 7 == 0) {
                System.out.print("|");
            }

            System.out.print(this.pawn[index].getCounter() + "|");

            if ((index + 1) % 7 == 0) {
                System.out.print("\n");
            }
        }
        System.out.print("\n\n");
    }

    public String checkWinner() {
        int[][] directions = {{1, 0}, {1, -1}, {1, 1}, {0, 1}};
        for (int[] d : directions) {
            int dx = d[0];
            int dy = d[1];
            for (int x = 0; x < this.MAX_ROW; x++) {
                for (int y = 0; y < this.MAX_COLUMN; y++) {
                    int lastx = x + 3 * dx;
                    int lasty = y + 3 * dy;
                    if (0 <= lastx && lastx < this.MAX_ROW && 0 <= lasty && lasty < this.MAX_COLUMN) {
                        String ChessCounter = this.chessBoard[x][y];
                        if (!" ".equals(ChessCounter) && (ChessCounter.equals(this.chessBoard[x + dx][y + dy]))
                                && (ChessCounter.equals(this.chessBoard[x + 2 * dx][y + 2 * dy]))
                                && (ChessCounter.equals(this.chessBoard[lastx][lasty]))) {
                            return ChessCounter;
                        }
                    }
                }
            }
        }
        return " "; // no winner
    }
}
