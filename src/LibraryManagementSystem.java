import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

public class LibraryManagementSystem {

	private Connection conn;
	private Statement stmt;

	public static void main(String[] args) throws SQLException {
		LibraryManagementSystem program = new LibraryManagementSystem();
		program.cleanUpOnStartUp();
		program.loadInventory();
//		program.cleanUpOnStartUp();
//		program.createTable();
//		program.printInformation();
//		program.takeUserInput();
		System.out.println("Good bye");
	}

	private void loadInventory() {
		// You need to add the isbn into the checkout table.
		// Also, in your inventory table you mention that there are copies of each book
		// but in your email below (the first email).
		// you mention that there is only 1 copy of each book.
		// Your book table should only contain information on each book not checked out
		// details.
		// Your customer should only contain info on the customer.
		// and your checkoutbook can contain the checkout information for the book and
		// the customer and checkout dates.

		// faraz: CheckedoutBooks(checkoutId, custId, checkedoutDate, dueDate)
		// custId is a foreign key in CheckedoutBooks and it is a primary key in
		// CustInfo table.

//		CustInfo(custId, custFirstName, custLastName)

		try {
			getStatement().executeUpdate(
					"CREATE TABLE IF NOT EXISTS custInfo (custId integer PRIMARY KEY, custFirstName VARCHAR(20), custLastName VARCHAR(20))");
			getStatement().executeUpdate(
					"CREATE TABLE IF NOT EXISTS checkedoutBooks (id integer PRIMARY KEY, isbn VARCHAR(10), checkoutdate DATE, duedate DATE, custId INTEGER, foreign key (custId) references custInfo(custId) )");
			
			getStatement().executeUpdate(
					"INSERT INTO custInfo (custId, custFirstName, custLastName) VALUES (1, 'faraz', 'durrani')");
			
			getStatement().executeUpdate(
					"INSERT INTO checkedoutBooks (id, ISBN, CHECKOUTDATE, DUEDATE, CUSTID) VALUES (1, 'ISBNNO1', sysdate, sysdate, 1)");

			ResultSet res = getStatement().executeQuery("select * from CHECKEDOUTBOOKS");
			while (res.next()) {
				System.err.println(res.getInt("id") + " | " + res.getString("ISBN"));
			}

		} catch (SQLException e) {
			e.printStackTrace();
		}

	}

//	private void loadInventory() {
//		try {
//			getStatement() // Book(isbn, name, author, publisher, yearpublished, checkedOut, checkoutId)
////			checkoutId in Book is foreign key and it is a primary key in CheckedoutBooks. 
//					.executeUpdate(
//							"CREATE TABLE IF NOT EXISTS BOOK (ISBN VARCHAR(10) NOT NULL, NAME VARCHAR(30), AUTHOR VARCHAR(30), PUBLISHER VARCHAR(30) VARCHAR(5), YEARPUBLISHED DATE, CHECKEDOUT BOOLEAN, checkoutId INTEGER)");
//			getStatement() // id, isbn, aisleNo, shelfLevel, numberOfCopies
//					// Isbn is a foreign key which maps to isbn in Book table. Isbn is a primary key
//					// in Book table.
//					.executeUpdate(
//							"CREATE TABLE IF NOT EXISTS INVENTORY (id INTEGER NOT NULL, ISBN VARCHAR(10), aisleNo VARCHAR(5), shelfLevel VARCHAR(5), numOfCopies INTEGER)");
//		} catch (SQLException e) {
//			e.printStackTrace();
//		}
//	}

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
				System.err.println(
						"Wrong Input. Kindly enter a digit followed by a space followed by first name followed by space followed by last name");
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
		try {
			getStatement().execute("DROP ALL OBJECTS");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private Connection getConnection() {
		if (conn != null) {
			return conn;
		} else {
			try {
				Class.forName("org.h2.Driver");
				return conn = DriverManager.getConnection("jdbc:h2:~/test", "sa", "");
			} catch (ClassNotFoundException | SQLException e) {
				throw new RuntimeException(e);
			}
		}
	}

	private Statement getStatement() {
		if (stmt != null) {
			return stmt;
		} else {
			try {
				return stmt = getConnection().createStatement();
			} catch (SQLException e) {
				throw new RuntimeException(e);
			}
		}
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
				System.out.println(
						"There are no presidents on records. \nFirst enter a few president names and then try again");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}
}
