import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Calendar;
import java.util.Date;
import java.util.Scanner;

public class LibraryManagementSystem {

	private Connection conn;
	private Statement stmt;

	public static void main(String[] args) throws SQLException {
		LibraryManagementSystem program = new LibraryManagementSystem();
		program.cleanUpOnStartUp();
		program.setupLibrary();
//		program.cleanUpOnStartUp();
//		program.createTable();
//		program.printInformation();
//		program.takeUserInput();
		System.out.println("Good bye");
	}

	private void setupLibrary() {

		createTables();
		loadData();

	}

	/**
	 * NOTES: 
	 * You need to add the isbn into the checkout table. Also, in your
	 * inventory table you mention that there are copies of each book but in your
	 * email below (the first email). you mention that there is only 1 copy of each
	 * book. Your book table should only contain information on each book not
	 * checked out details. Your customer should only contain info on the customer.
	 * and your checkoutbook can contain the checkout information for the book and
	 * the customer and checkout dates.
	 */

	private void createTables() {

		try {
			getStatement().executeUpdate(
					"CREATE TABLE IF NOT EXISTS book (isbn BIGINT PRIMARY KEY, name VARCHAR(35), author VARCHAR(30), publisher VARCHAR(45), yearPublished TIMESTAMP)");

			getStatement().executeUpdate(
					"CREATE TABLE IF NOT EXISTS inventory (isbn BIGINT, location VARCHAR(5), quantity SMALLINT)");

			getStatement().executeUpdate(
					"CREATE TABLE IF NOT EXISTS custInfo (custId SMALLINT PRIMARY KEY, custFirstName VARCHAR(20), custLastName VARCHAR(20))");

			getStatement().executeUpdate(
					"CREATE TABLE IF NOT EXISTS checkedoutBooks (id SMALLINT PRIMARY KEY, isbn INTEGER, checkoutdate DATE, duedate DATE, custId INTEGER, foreign key (custId) references custInfo(custId) )");

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private void loadData() {
		try {
			getStatement().executeUpdate("INSERT INTO book " + "(isbn, name, author, publisher, yearPublished) "
					+ "VALUES "
					+ "(1841598984, 'In Search of Lost Time', 'Marcel Proust', 'Grasset and Gallimard', parsedatetime('1913', 'yyyy')),"
					+ "(0486821951, 'Don Quixote', 'Miguel de Cervantes', 'Dover Publications', parsedatetime('1605', 'yyyy')),"
					+ "(1847175902, 'Ulysses', 'James Joyce', 'OBrien Press', parsedatetime('1922', 'yyyy')),"
					+ "(7222176233, 'The Great Gatsby', 'F. Scott Fitzgerald', 'Yunnan Peoples Publishing House', parsedatetime('1925', 'yyyy')),"
					+ "(1503280780, 'Moby Dick', 'Herman Melville', 'CreateSpace Independent Publishing Platform', parsedatetime('1851', 'yyyy')),"
					+ "(1795093838, 'Hamlet', 'William Shakespeare', 'Independently published', parsedatetime('1599', 'yyyy')),"
					+ "(0140447938, 'War and Peace', 'Leo Tolstoy', 'Everymans Library', parsedatetime('1869', 'yyyy')),"
					+ "(0060531045, 'One Hundred Years of Solitude', 'Gabriel Garcia Marquez', 'Harper', parsedatetime('1967', 'yyyy')),"
					+ "(0679410031, 'The Brothers Karamazov', 'Fyodor Dostoyevsky', 'Everymans Library', parsedatetime('1879', 'yyyy')),"
					+ "(1514637618, 'The Adventures of Huckleberry Finn', 'Mark Twain', 'CreateSpace Independent Publishing Platform', parsedatetime('1885', 'yyyy'))");
			getStatement().executeUpdate(
					"INSERT INTO inventory (isbn, location, quantity)" + "VALUES" + "(1841598984, '1-3B', 5),"
							+ "(0486821951, '2-2F', 5)," + "(1847175902, '1-1B', 5)," + "(7222176233, '2-3C', 5),"
							+ "(1503280780, '2-2C', 5)," + "(1795093838, '1-1A', 5)," + "(0140447938, '2-3B', 5),"
							+ "(0060531045, '1-2F', 5)," + "(0679410031, '1-2D', 5)," + "(1514637618, '2-3D', 5)");
		} catch (SQLException e) {
			e.printStackTrace();
		}
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

//	try {
//		getStatement().executeUpdate(
//				"INSERT INTO custInfo (custId, custFirstName, custLastName) VALUES (1, 'faraz', 'durrani')");
//
//		getStatement().executeUpdate(
//				"INSERT INTO checkedoutBooks (id, ISBN, CHECKOUTDATE, DUEDATE, CUSTID) VALUES (2, 'ISBNNO1', sysdate, sysdate, 1)");
//
//		ResultSet res = getStatement().executeQuery("select * from CHECKEDOUTBOOKS");
//		while (res.next()) {
//			System.err.println(res.getInt("id") + " | " + res.getString("ISBN"));
//		}
//	} catch (SQLException e) {
//		e.printStackTrace();
//	}
}
