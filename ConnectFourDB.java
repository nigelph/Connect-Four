import java.sql.Statement;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.ArrayList;

public class ConnectFourDB {

    Connection conn = null;
    // String url = "jdbc:derby:BookStoreDB;create=true";  //url of the DB host
    String url = "jdbc:derby:ConnectFour;create=true";
    String username = null;  //your DB username
    String password = null;   //your DB password
    Statement statement;
    ResultSet rs;
    ResultSetMetaData data ;
    public void autoConnectFourDB() {
        try {
            conn = DriverManager.getConnection(url, username, password);
            if (!checkTableExisting("CONNECTFOUR")) {
                statement = conn.createStatement();
                statement.executeUpdate("CREATE TABLE CONNECTFOUR (SEQUENCE INT, POSITION INT, PLAYER_NUM INT, COUNTER_TYPE VARCHAR(1))"); //Create BOOK table
                System.out.println("Table Created");
            }
        } catch (SQLException ex) {
            System.err.println("SQL Exception: " + ex.getMessage());
        }
    }

    public void getData() {
        try {
            Statement slectstate = conn.createStatement();
            rs = slectstate.executeQuery("SELECT * FROM CONNECTFOUR");
            this.data =  rs.getMetaData();
        } catch (SQLException ex) {
            System.err.println("SQL Exception: " + ex.getMessage());
        }
    }

    private boolean checkTableExisting(String newTableName) {
        try {
            System.out.println("check existing tables.... ");
            DatabaseMetaData dbmd = conn.getMetaData();
            try (ResultSet rsDBMeta = dbmd.getTables(null, null, null, null)) {
                while (rsDBMeta.next()) {
                    String tableName = rsDBMeta.getString("TABLE_NAME");
                    if (tableName.compareToIgnoreCase(newTableName) == 0) {
                        System.out.println("found: " + tableName);
                        return true;
                    }
                }
            }
        } catch (SQLException ex) {
        }
        return false;
    }
    
    /**
     *
     * @param dataRow
     */
    public void InsertConnectFourDB(ArrayList<String> dataRow) {
        try {
            autoConnectFourDB();
            statement = conn.createStatement();
            for (String dr : dataRow) {
                String[] record = dr.split(",");
                int moveSequence = Integer.parseInt(record[0]);
                int movePosition = Integer.parseInt(record[1]);
                int playerNumber = Integer.parseInt(record[2]);
                String counterType = record[3];
                statement.executeUpdate("INSERT INTO CONNECTFOUR VALUES ("
                        +   moveSequence + ", " 
                        +   movePosition + ", "
                        +   playerNumber + ", '" 
                        +   counterType + "')");
            }
            
        } catch (SQLException ex) {
            System.err.println("SQL Exception: " + ex.getMessage());
        }
    }
    
    public void deleteTable() {
        try {
            conn = DriverManager.getConnection(url, username, password);
            System.out.println("check existing tables.... ");
            DatabaseMetaData dbmd = conn.getMetaData();
            Statement dropStatement;
            try (ResultSet rsDBMeta = dbmd.getTables(null, null, null, null)) {
                dropStatement = null;
                while (rsDBMeta.next()) {
                    String tableName = rsDBMeta.getString("TABLE_NAME");
                    //System.out.println("found: " + tableName);
                    if (tableName.compareToIgnoreCase("CONNECTFOUR") == 0) {
                        System.out.println(tableName + "  needs to be deleted");
                        String sqlDropTable = "DROP TABLE " + "CONNECTFOUR";
                        dropStatement = conn.createStatement();
                        dropStatement.executeUpdate(sqlDropTable);
                        System.out.println("table deleted");
                    }
                }
            }
            if (dropStatement != null) {
                dropStatement.close();
            }
        } catch (SQLException ex) {
        }
    }
}
