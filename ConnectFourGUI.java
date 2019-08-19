/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Dimension;
import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.Graphics;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import javax.swing.JButton;
import java.awt.event.ActionListener;
import javax.swing.JLabel;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.FileNotFoundException;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Random;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.ImageIcon;
import javax.swing.JOptionPane;
import java.awt.event.WindowEvent;
import java.awt.event.WindowAdapter;

/**
 *
 * @author Nigel & Theo
 */
public class ConnectFourGUI extends JPanel implements ActionListener, MouseListener {

    //Define the GUI components 
    public final int TILE_SIZE = 80;
    public final int PANEL_WIDTH = 700;
    public final int PANEL_HEIGHT = 600;
    private final int COLUMNS = 7;
    private final int ROWS = 6;
    private final int MAX_SIZE = 42;
    private final JPanel drawPanel;
    private final JPanel coverPanel;
    private JPanel coverButtonPanel;
    private JLabel coverLabel;
    private JPanel northPanel;
    private DrawingPanel[] panels;
    private JButton startButton;
    private JButton saveButton;

    //Define the virables
    private boolean ai_flg;
    private boolean loadFlag;
    private int input_col;
    private int counter;
    private String winner;
    private final HashMap player_map;
    private final Random rand;
    private final ConnectFourBoard board;
    private final int Player1 = 1;
    private final int Player2 = 2;
    private final ConnectFourDB cfdb;

    public ConnectFourGUI() {
        super(new BorderLayout());
        //Initialise Buttons

        //Save Button
        saveButton = new JButton("SAVE");
        saveButton.setBackground(Color.LIGHT_GRAY);
        saveButton.addActionListener(this);

        //Start Button
        startButton = new JButton("START");
        startButton.setBackground(Color.LIGHT_GRAY);
        startButton.addActionListener(this);

        //StartPanel
        coverPanel = new JPanel(new BorderLayout());
        coverLabel = new JLabel(new ImageIcon("cover new.png"));
        coverButtonPanel = new JPanel(new GridLayout(1, 1));
        coverButtonPanel.add(startButton);
        coverPanel.add(coverButtonPanel, BorderLayout.NORTH);
        coverPanel.add(coverLabel, BorderLayout.CENTER);
        add(coverPanel, BorderLayout.PAGE_START);

        //NORTH PANEL
        northPanel = new JPanel(new GridLayout(1, 1));
        northPanel.add(saveButton);

        //Add the drawingPanel to the frame
        drawPanel = new DrawingPanel(6, 7);
        this.drawPanel.setSize(700, 600);
        panels = new DrawingPanel[42];

        //Draws ConnectFour Board
        int i = 0;
        for (int row = 0; row < this.ROWS; row++) {
            for (int col = 0; col < this.COLUMNS; col++) {
                panels[i] = new DrawingPanel();
                panels[i].setName(String.valueOf(i));
                //Adds mouselistener to each position
                panels[i].addMouseListener(this);
                this.drawPanel.add(panels[i]);
                i++;
            }
        }
        this.drawPanel.addMouseListener(this);

        this.ai_flg = false;
        this.loadFlag = false;
        this.counter = 0;
        this.winner = " ";
        this.board = new ConnectFourBoard();
        this.cfdb = new ConnectFourDB();
        this.cfdb.autoConnectFourDB();
        player_map = new HashMap();
        rand = new Random();
    }

    public boolean dropCounter(int playerNum) throws FileNotFoundException {
        //Counter Drop Success
        if (this.board.checkDrop(this.counter, this.input_col, playerNum, (String) player_map.get(playerNum))) {
            this.counter += 1;
            return true;
        }//Column Full 
        else {
            JOptionPane.showConfirmDialog(null, "This Column is Full! Please Choose Another!", "Error", JOptionPane.CLOSED_OPTION);
            return false;
        }
    }

    public void printBoard(ConnectFourBoard board) {
        for (int index = 0; index < this.MAX_SIZE; index++) {
            switch (board.getPawnArrayItem(index).counter) {
                case "X":
                    this.panels[index].setForeground(Color.RED);
                    break;
                case "O":
                    this.panels[index].setForeground(Color.YELLOW);
                    break;
                default:
                    this.panels[index].setForeground(null);
                    break;
            }
        }
    }

    @Override
    public void mouseClicked(MouseEvent e) {
        if (e.getComponent().getName() != null) {
            int index = Integer.parseInt(e.getComponent().getName());
            this.input_col = index % 7;
            //Player 1 Turn, Always human
            if (this.counter % 2 == 0) {
                try {
                    if (this.dropCounter(this.Player1)) {
                        this.printBoard(this.board);
                    }

                } catch (FileNotFoundException ex) {
                    Logger.getLogger(ConnectFourGUI.class.getName()).log(Level.SEVERE, null, ex);
                }
                if (this.ai_flg) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException ex) {
                        Logger.getLogger(ConnectFourGUI.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    this.input_col = rand.nextInt(7);
                    try {
                        while (!this.board.checkDrop(this.counter, this.input_col, 2, (String) this.player_map.get(2))) {
                            this.input_col = rand.nextInt(7);
                        }
                    } catch (FileNotFoundException ex) {
                        Logger.getLogger(ConnectFourGUI.class.getName()).log(Level.SEVERE, null, ex);
                    }
                    this.counter += 1;
                    this.printBoard(this.board);
                }

            }//Player 2 Turn,  Human/AI
            else {
                //Whether player is human or AI
                if (!this.ai_flg) {
                    try {
                        if (this.dropCounter(this.Player2)) {
                            this.printBoard(this.board);
                        }
                    } catch (FileNotFoundException ex) {
                        Logger.getLogger(ConnectFourGUI.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
            }
            this.winner = this.board.checkWinner();
            switch (this.winner) {
                case "X":
                    JOptionPane.showConfirmDialog(null, "Player One Wins!!", "Win", JOptionPane.CLOSED_OPTION);
                    break;
                case "O":
                    JOptionPane.showConfirmDialog(null, "Player Two Wins!!", "Win", JOptionPane.CLOSED_OPTION);
                    break;
                default:
                    break;
            }
            if (!" ".equals(this.winner)) {
                int ag = JOptionPane.showConfirmDialog(null, "Do you want to play it again? ", "Win", JOptionPane.YES_NO_OPTION);
                if (ag == JOptionPane.YES_OPTION) {
                    this.board.initialChessBoard();
                    this.board.initialHashMap();
                    this.board.initialDataRow();
                    this.printBoard(board);
                } else if (ag == JOptionPane.NO_OPTION) {
                    System.exit(0);
                }
            }
        }
    }

    @Override
    public void mousePressed(MouseEvent e) {
    }

    @Override
    public void mouseReleased(MouseEvent e) {
    }

    @Override
    public void mouseEntered(MouseEvent e) {
        if (e.getComponent().getName() != null) {
            int index = Integer.parseInt(e.getComponent().getName()) % 7;
            for (int i = index; i <= index + 35; i = i + 7) {
                panels[i].setBackground(Color.LIGHT_GRAY);
            }
        }
    }

    @Override
    public void mouseExited(MouseEvent e) {
        for (int index = 0; index < 42; index++) {
            panels[index].setBackground(Color.BLUE);
        }
    }

    private class DrawingPanel extends JPanel {

        public DrawingPanel() {
            super.setPreferredSize(new Dimension(100, 100));
            super.setBackground(Color.BLUE);
        }

        public DrawingPanel(int x, int y) {
            super(new GridLayout(x, y));
            super.setPreferredSize(new Dimension(700, 600));
        }

        @Override
        public void paintComponent(Graphics g) {
            super.paintComponent(g);
            if ((!g.getColor().equals(Color.RED)) && (!g.getColor().equals(Color.YELLOW))) {
                g.setColor(Color.WHITE);
            } else if (g.getColor().equals(Color.RED)) {
                g.setColor(Color.RED);
            } else {
                g.setColor(Color.YELLOW);
            }
            g.fillOval(7, 7, 80, 80);
        }
    }

    //Create drawing panel
    @Override
    public void actionPerformed(ActionEvent e) {
        Object source = e.getSource();
        int un = 1;
        if (source == startButton) {
            Object[] mode = {"Single Player", "Versus Player"};
            Object[] colors = {"Red", "Yellow"};
            int rc = 0;
            do {
                rc = JOptionPane.showOptionDialog(null, "Please Enter a Game Mode:", "Start Game", JOptionPane.YES_OPTION, JOptionPane.QUESTION_MESSAGE, null, mode, mode[0]);
                switch (rc) {
                    case 0:
                        JOptionPane.showConfirmDialog(null, "Single Mode Selected", "Start Game", JOptionPane.PLAIN_MESSAGE, JOptionPane.PLAIN_MESSAGE);
                        this.ai_flg = true;
                        break;
                    case 1:
                        JOptionPane.showConfirmDialog(null, "Versus Mode Selected", "Start Game", JOptionPane.PLAIN_MESSAGE, JOptionPane.PLAIN_MESSAGE);
                        this.ai_flg = false;
                        break;
                    default:
                        break;
                }
            } while (rc != 0 && rc != 1);

            int ld = -1;
            do {
                ld = JOptionPane.showOptionDialog(null, "Load Game?", "Start Game", JOptionPane.YES_NO_OPTION, JOptionPane.QUESTION_MESSAGE, null, null, null);
                if (ld == 0) {
                    this.cfdb.getData();
                    {
                        try {
                            while (this.cfdb.rs.next()) {
                                int moveSequence = this.cfdb.rs.getInt(1);
                                int movePosition = this.cfdb.rs.getInt(2);
                                int playerNumber = this.cfdb.rs.getInt(3);
                                String counterType = this.cfdb.rs.getString(4);

                                this.board.setPawnArrayItem(movePosition, playerNumber, counterType);
                                this.board.getHashPawn().replace(movePosition, playerNumber);
                                this.board.setChessBoard(movePosition, counterType);
                                if (moveSequence == 0) {
                                    //player one's counter piece
                                    this.player_map.put(this.Player1, counterType);
                                }
                                if (moveSequence == 1) {
                                    //player two's counter piece
                                    this.player_map.put(this.Player2, counterType);
                                }
                                //decide who is player 1;
                                this.counter += 1;
                            }
                        } catch (SQLException ex) {
                            Logger.getLogger(ConnectFourGUI.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    }
                    if (this.counter != 0) {
                        this.loadFlag = true;
                    } else {
                        this.loadFlag = false;
                        un = JOptionPane.showConfirmDialog(null, "Unable to Locate Saved Game! " + "Please Start a New Game!!", "Start Game", JOptionPane.CLOSED_OPTION);
                    }
                    this.printBoard(board);
                } else if ((ld == 1 || !loadFlag) && (ld == 0 || ld == 1)) {
                    //readToFile();
                    this.cfdb.deleteTable();
                    int cos = 0;
                    do {
                        cos = JOptionPane.showOptionDialog(null, " Player 1, Please Select a Color:", "Start Game", JOptionPane.YES_OPTION,
                                JOptionPane.QUESTION_MESSAGE, null, colors, colors[0]);
                        switch (cos) {
                            case 0:
                                this.player_map.put(this.Player1, "X");
                                this.player_map.put(this.Player2, "O");
                                break;
                            case 1:
                                this.player_map.put(this.Player1, "O");
                                this.player_map.put(this.Player2, "X");
                                break;
                            default:
                                break;
                        }
                    } while (cos != 0 && cos != 1);
                }
            } while (ld != 0 && ld != 1);
        } else if (source == saveButton) {
            this.cfdb.InsertConnectFourDB(this.board.getDataRow());
            JOptionPane.showConfirmDialog(null, "Your Game has been Saved!", "Game Saved", JOptionPane.CLOSED_OPTION);
        }
        this.coverPanel.setVisible(false);
        //Add the Panels to the frame
        add(northPanel, BorderLayout.PAGE_START);
        add(drawPanel, BorderLayout.CENTER);

    }

    public static void main(String[] args) {

        ConnectFourGUI myPanel = new ConnectFourGUI();
        JFrame frame = new JFrame("Connect Four"); //create frame to hold our JPanel subclass	
        frame.setResizable(false);
        frame.getContentPane().add(myPanel);  //add instance of MyGUI to the frame
        //Position frame on center of screen 
        frame.setLocation(500, 0);
        frame.setSize(700, 700);
        frame.setVisible(true);

        frame.addWindowListener(new WindowAdapter() {
            @Override
            //Ask user to quit game
            public void windowClosing(WindowEvent e) {
                int option = JOptionPane.showConfirmDialog(null, "Do you want to quit the game?", "Quit Game", JOptionPane.OK_CANCEL_OPTION);
                if (option == JOptionPane.OK_OPTION) {
                    System.exit(0);
                } else {
                    frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
                }
            }
        });
    }
}
