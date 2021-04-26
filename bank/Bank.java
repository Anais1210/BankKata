package bank;


import javax.swing.plaf.nimbus.State;
import java.sql.*;
import java.util.ArrayList;
import java.util.Arrays;

public class Bank {

    // Variables

    private ArrayList<Account> user = new ArrayList<>();


    // Strings de connection à la base mysql

    private static final String JDBC_DRIVER = "com.mysql.cj.jdbc.Driver";
    private static final String DB_URL = "jdbc:mysql://localhost:3306/bank_db";
    private static final String DB_USER = "root";

    private static final String DB_PASS = "root";

    private static final String TABLE_NAME = "accounts";

    private Connection c;

    // Constructors :

    public Bank() {
        initDb();
    }

    // Initialize the connection with the database and create the table Account with :
    //  -> name VARCHAR(100)
    //  -> balance INTEGER
    //  -> threshold INTEGER
    //  -> suspension VARCHAR(1) <- this last parameters will be the equivalent of our false/true in the database represent by a simple char f/t

    private void initDb() {
        try {
            Class.forName(JDBC_DRIVER);
            c = DriverManager.getConnection(DB_URL, DB_USER, DB_PASS);
            System.out.println("Opened database successfully");
            Statement q = c.createStatement();
            q.executeUpdate(
                    "CREATE TABLE " + TABLE_NAME + "(" + "name VARCHAR(100), balance INTEGER, threshold INTEGER, suspension VARCHAR(1));"
            );
            System.out.println("Create database successfully");

        } catch (Exception e) {
            System.err.println(e.getClass().getName() + ": " + e.getMessage());
            System.exit(0);
        }
    }

    // Close the connection with the database

    public void closeDb() {
        try {
            c.close();
        } catch (SQLException e) {
            System.out.println("Could not close the database : " + e);
        }
    }

    // Drop all the table Account contain in the database

    void dropAllTables() {
        try (Statement s = c.createStatement()) {
            s.executeUpdate("DROP TABLE " + TABLE_NAME);
        } catch (Exception e) {
            System.out.println(e.toString());
        }
        //"DROP SCHEMA public CASCADE;" +
        //"CREATE SCHEMA public;" +
        //"GRANT ALL ON SCHEMA public TO root;" +
        //"GRANT ALL ON SCHEMA public TO public;"
    }

    // Create a new Account in a local version and then a persistent version contain in the table Account of our database
    // @param{String} name
    // @param{int} balance
    // @param{int} threshold

    public void createNewAccount(String name, int balance, int threshold) {
        // First of all check if the threshold is superior or equal to 0
        if(threshold <= 0){
            // Create a new instance of Account that will be our temporary variable for creation then store the variable in this.user
            Account tempAccount = new Account();
            tempAccount.setName(name);
            tempAccount.setThreshold(threshold);
            tempAccount.setBalance(balance);
            user.add(tempAccount);
            try (Statement s = c.createStatement()){
                // After creating the local variable we will insert it in the database first we have to determine the state of suspension
                // and rename it by a character for our database false -> f and true -> t
                char status;
                if(tempAccount.isSuspension() == false){
                    status = 'f';
                }else{
                    status = 't';
                }
                // Execute the request
                s.executeUpdate("INSERT INTO " + TABLE_NAME + " (name, balance,threshold,suspension) VALUES ("
                        + "'" + tempAccount.getName() + "' , "
                        +  tempAccount.getBalance() + " , "
                        +  tempAccount.getThreshold() + " ,"
                        + "'" + status + "');");
                System.out.println("Account successfully created");
            }catch(Exception e){
                System.out.println(e.toString());
            }
        }else{
            System.out.println("You cannot create an account because the threshold is superior to 0, threshold have to be inferior or equal to 0");
        }
    }

    // Change the amount of the balance using the name of the account

    public void changeBalanceByName(String name, int balanceModifier) {
        for(int i = 0; i < this.user.size(); i++){
            // check if the given name is present in the array
            if(this.user.get(i).getName().compareTo(name) == 0){
                // check if the user with the given name is not suspended
                if(this.user.get(i).isSuspension() == false){
                    // Establish the new balance for the account
                    int tmp = this.user.get(i).getBalance() + balanceModifier;
                    // Check if the new balance is not under the Threshlod if it so don't make any modification
                    if(tmp > this.user.get(i).getThreshold()){
                        // If it's ok make the modification in local variable this.user and in the database using UPDATE request
                        this.user.get(i).setBalance(tmp);
                        try (Statement s = c.createStatement()){
                            s.executeUpdate("UPDATE " + TABLE_NAME + " SET balance = " + this.user.get(i).getBalance() + " WHERE name = '" + name + "'");
                            System.out.println("\nBalance change for the account named : " + this.user.get(i).getName());
                        }catch(Exception e){
                            System.out.println(e.toString());
                        }
                    }
                }else{
                    System.out.println("\nCannot change the balance of a unlocked account");
                }
            }
        }
    }

    // Block an account by a given name

    public void blockAccount(String name) {
        for(int i = 0; i < this.user.size(); i++){
            // check if the given name is present in the array
            if(this.user.get(i).getName().compareTo(name) == 0){
                // Set the suspension state at true (user is suspended) and then update the database to insert a 't' -> true for the suspension column
                this.user.get(i).setSuspension(true);
                try (Statement s = c.createStatement()){
                    s.executeUpdate("UPDATE " + TABLE_NAME + " SET suspension = 't' WHERE name = '" + name + "'");
                    System.out.println("\nAccount " + this.user.get(i).getName() + " Successfully block.");
                }catch(Exception e){
                    System.out.println(e.toString());
                }
            }
        }
    }

    //Methods

    // Print every account store in local variable this.user

    public String printAllAccounts() {
        String status = "";
        for (Integer i = 0; i < this.user.size(); i++){
            status += this.user.get(i).getName() + " | " + this.user.get(i).getBalance() + " | " + this.user.get(i).getThreshold() + " | " + this.user.get(i).isSuspension() + "\n";
        }
        return status;
    }

    // For testing purpose
    String getTableDump() {
        String query = "select * from " + TABLE_NAME;
        String res = "";

        try (PreparedStatement s = c.prepareStatement(query)) {
            ResultSet r = s.executeQuery();

            // Getting nb colmun from meta data
            int nbColumns = r.getMetaData().getColumnCount();

            // while there is a next row
            while (r.next()){
                String[] currentRow = new String[nbColumns];

                // For each column in the row
                for (int i = 1 ; i <= nbColumns ; i++) {
                    currentRow[i - 1] = r.getString(i);
                }
                res += Arrays.toString(currentRow);
            }

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }

        return res;
    }
}
