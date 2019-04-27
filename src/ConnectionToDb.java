import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

public class ConnectionToDb {

    private Connection conn;
    private Statement stmt;

    public static void main(String[] args) throws SQLException {
	ConnectionToDb program = new ConnectionToDb();
	program.cleanUpOnStartUp();
	program.createTable();
	program.printInformation();
	program.takeUserInput();
	System.out.println("Good bye");
    }

    private void takeUserInput() {
	Scanner sc = new Scanner(System.in);
	String input = sc.nextLine();
	while (!input.equals("exit")) {
	    if (input.equals("show all")) {
		printAllPresidents();
		input = sc.nextLine();
		continue;
	    }
	    String[] tokens = input.split(" ");
	    String id = null;
	    String name = null;
	    try {
		id = tokens[0];
		name = tokens[1] + " " + tokens[2];
	    } catch (Exception e) {
		System.err
			.println("Wrong Input. Kindly enter a digit followed by a space followed by first name followed by space followed by last name");
	    }
	    if (id != null && name != null) {
		insertPresident(id, name);
	    }
	    input = sc.nextLine();
	}
	sc.close();
    }

    private void insertPresident(String id, String name) {
	try {
	    getStatement().executeUpdate("INSERT INTO PRESIDENTS (id, name) values (" + id + ",'" + name + "')");
	} catch (SQLException e) {
	    e.printStackTrace();
	}
	System.out.println("Insert successful");
    }

    private void printInformation() {
	System.out.println("Hi! This program allows you to store president info in the database.");
	System.out.println("You can enter which president number he was and his name.");
	System.out.println("For example, 44 Barack Obama");
	System.out.println("He will be assigned '44' as an ID and 'Barack Obama' as a name");
	System.out.println("You can type 'show all' (without quotes) to print all the entered president names");
	System.out.println("type exit to exit the program");
    }

    private void createTable() {
	try {
	    getStatement()
		    .executeUpdate("CREATE TABLE IF NOT EXISTS PRESIDENTS (id INTEGER NOT NULL, name VARCHAR(30))");

	} catch (SQLException e) {
	    e.printStackTrace();
	}
    }

    public void cleanUpOnStartUp() {
	System.out.println("Setting up database, tables...");
	try {
	    getStatement().execute("DROP TABLE IF EXISTS PRESIDENTS");
	} catch (SQLException e) {
	    e.printStackTrace();
	}
    }

    private Connection getConnection() {
	try {
	    Class.forName("org.h2.Driver");
	    if (conn != null) {
		return conn;
	    } else {
		conn = DriverManager.getConnection("jdbc:h2:~/test", "sa", "");
	    }
	} catch (ClassNotFoundException | SQLException e) {
	    e.printStackTrace();
	}
	return conn;
    }

    private Statement getStatement() {
	try {
	    if (stmt != null) {
		return stmt;
	    } else {
		stmt = getConnection().createStatement();
	    }
	} catch (SQLException e) {
	    e.printStackTrace();
	}
	return stmt;
    }

    private void printAllPresidents() {
	ResultSet rs = null;
	boolean any = true;
	try {
	    rs = getStatement().executeQuery("select * from PRESIDENTS");
	    while (rs.next()) {
		any = false;
		System.out.println(rs.getInt("id") + " " + rs.getString("name"));
	    }
	    if (any) {
		System.out
			.println("There are no presidents on records. \nFirst enter a few president names and then try again");
	    }
	} catch (SQLException e) {
	    e.printStackTrace();
	}
    }
}
