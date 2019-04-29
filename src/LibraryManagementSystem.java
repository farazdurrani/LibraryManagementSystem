import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Calendar;
import java.util.Scanner;

public class LibraryManagementSystem {
	
	public static void main(String[] args) throws SQLException {
		LibraryManagementSystem program = new LibraryManagementSystem();
		program.cleanUpOnStartUp();
		program.setupLibrary();
		program.printInformation();
		program.takeUserInput();
		System.out.println("Thanks for using Library Management System. \nGood bye");
	}
	
	private Connection conn;
	private Statement stmt;
	private boolean welcome = true;


	private void setupLibrary() {
		createTables();
		createView();
		loadData();
	}

	private void createView() {
		try {
			getStatement().executeUpdate(
					"CREATE VIEW INVENTORYBYQUANTITYDESC AS SELECT * FROM INVENTORY ORDER BY quantity DESC");

		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private void createTables() {

		try {
			getStatement().executeUpdate(
					"CREATE TABLE IF NOT EXISTS book (isbn BIGINT PRIMARY KEY, name VARCHAR(35), author VARCHAR(25), publisher VARCHAR(45), yearPublished TIMESTAMP)");

			getStatement().executeUpdate(
					"CREATE TABLE IF NOT EXISTS inventory (isbn BIGINT, location VARCHAR(5), quantity SMALLINT)");

			getStatement().executeUpdate(
					"CREATE TABLE IF NOT EXISTS customer (custId SMALLINT PRIMARY KEY auto_increment, name VARCHAR(50))");

			getStatement().executeUpdate(
					"CREATE TABLE IF NOT EXISTS checkedoutBooks (id SMALLINT PRIMARY KEY auto_increment, isbn INTEGER, checkoutdate DATE, duedate DATE, custId INTEGER, foreign key (custId) references customer(custId) )");

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
					"INSERT INTO inventory (isbn, location, quantity)" + "VALUES" + "(1841598984, '1-3B', 3),"
							+ "(0486821951, '2-2F', 9)," + "(1847175902, '1-1B', 2)," + "(7222176233, '2-3C', 4),"
							+ "(1503280780, '2-2C', 6)," + "(1795093838, '1-1A', 5)," + "(0140447938, '2-3B', 10),"
							+ "(0060531045, '1-2F', 8)," + "(0679410031, '1-2D', 16)," + "(1514637618, '2-3D', 12)");
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private void takeUserInput() {
		Scanner sc = new Scanner(System.in);
		String input = sc.nextLine();
		while (!input.equals("exit")) {
			if (input.equals("books")) {
				printAllBooks();
			} else if (input.equals("inventory")) {
				printInventory();
			} else if (input.equals("inventoryDesc")) {
				printInventoryView();
			} else if (input.equals("checkout")) {
				checkout(sc);
			} else if (input.equals("loanedout")) {
				printLoanedOutInfo();
			} else {
				System.out.println("Invalid key");
				printInformation();
			}
			input = sc.nextLine();
			continue;
		}
		sc.close();
	}

	private void printInventoryView() {
		ResultSet rs = null;
		try {
			String query = "SELECT * from INVENTORYBYQUANTITYDESC";

			rs = getStatement().executeQuery(query);
			System.out.printf("%10s%10s%10s%n", "ISBN", "LOCATION", "QUANTITY");
			while (rs.next()) {
				System.out.printf("%010d%10s%10s%n", rs.getLong("isbn"), rs.getString("location"),
						rs.getInt("quantity"));
			}
		} catch (SQLException e) {
			System.err.println("Error while print Inventory View by Desc");
		}
	}

	private void printLoanedOutInfo() {
		ResultSet rs = null;
		try {
			String joinQuery = "SELECT book.name, book.isbn, checkedoutBooks.checkoutDate, checkedoutBooks.dueDate , customer.name as custName "
					+ "FROM book " + "INNER JOIN checkedoutBooks ON book.isbn=checkedoutBooks.isbn "
					+ "INNER JOIN customer ON checkedoutBooks.custId = customer.custId";

			rs = getStatement().executeQuery(joinQuery);
			if (!rs.isBeforeFirst()) {
				System.err.println("No book is loaned out from library yet.");
			}
			while (rs.next()) {
				System.out.println("Book '" + rs.getString("name") + "' is loaned out to " + rs.getString("custName")
						+ ". It is due back by " + rs.getDate("dueDate") + ".");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private void printCheckoutBookInfo() {
		System.out.println("To checkout a book, Enter books ISBN number, followed by your full name");
		System.out.println("You must have a first and last name and a space between them");
		System.out.println("For example, 0060531045 Jordan Peterson");
		System.out.println("Type done when done checking out books");
	}

	private void checkout(Scanner sc) {
		printCheckoutBookInfo();
		String input = null;
		while (true) {
			input = sc.nextLine();
			if (input.equals("done")) {
				System.out.println("done checking out the book. Going to main screen.");
				printInformation();
				break;
			}
			String[] split = input.split(" ");
			String name = null;
			long isbn = 0L;
			try {
				isbn = Long.valueOf(split[0]);
				name = split[1] + " " + split[2];
			} catch (Exception e) {
				System.err.println("Invalid input. ");
				printCheckoutBookInfo();
				continue;
			}
			if (isbn == 0L || name == null) {
				System.err.println("Invalid isbn number or name. Try again");
				printInformation();
				continue;
			}
			try {
				if (checkInventory(isbn)) {
					insertIntoCustomerTable(name);
					insertIntoCheckoutBookTable(getLastCustomerId(), isbn);
					decreaseQuantity(isbn);
					printSuccessfulCheckoutMessage(isbn, name);
				}
			} catch (SQLException e) {
				System.err.println("Problem while inserting into database. try again");
				printInformation();
				continue;
			}
		}
	}

	private void decreaseQuantity(long isbn) {
		String query = "UPDATE inventory SET quantity = " + (getQuantity(isbn) - 1) + " WHERE isbn=" + isbn;
		try {
			getStatement().executeUpdate(query);
		} catch (SQLException e) {
			System.err.println("Error while decreasing book quantity in inventory during checkout");
		}
	}

	private int getQuantity(long isbn) {
		ResultSet rs = null;
		try {
			rs = getStatement().executeQuery("select quantity from inventory where isbn=" + isbn);
			while (rs.next()) {
				return rs.getInt("quantity");
			}
		} catch (SQLException e) {
			System.err.println("Error while getting book quantity from inventory during checkout");
		}
		return -1;
	}

	private void printSuccessfulCheckoutMessage(long isbn, String name) {
		System.out.println("checkout of book '" + getBookInfo(isbn) + "' with isbn " + isbn
				+ " successful to customer '" + name + "'");
		Calendar c = Calendar.getInstance();
		c.add(Calendar.DATE, 28);
		System.out.println("Book is due in 28 days: " + c.getTime());
		System.out.println("checkout another book or type done.");
	}

	private void insertIntoCustomerTable(String name) throws SQLException {
		getStatement().executeUpdate("INSERT INTO customer (name) values ('" + name + "')");
	}

	private void insertIntoCheckoutBookTable(long lastCustomerId, long isbn) throws SQLException {
		getStatement().executeUpdate("INSERT INTO checkedoutBooks (isbn, checkoutdate, duedate, custId) values (" + isbn
				+ ", sysdate, sysdate + 28, " + lastCustomerId + ")");
	}

	private long getLastCustomerId() {
		ResultSet rs = null;
		long result = 0L;
		try {
			rs = getStatement()
					.executeQuery("select custId from customer where custId= (SELECT MAX(custId) FROM customer)");
			while (rs.next()) {
				result = rs.getInt("custId");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return result;
	}

	private boolean checkInventory(long isbn) {
		ResultSet rs = null;
		boolean result = false;
		try {
			rs = getStatement().executeQuery("select * from inventory where isbn=" + isbn);
			if (!rs.isBeforeFirst()) {
				System.err.println("isbn " + isbn + " doesn't exist in the database");
			}
			while (rs.next()) {
				if (rs.getInt("quantity") > 0) {
					result = true;
				} else {
					System.out
							.println("Unfortunately, library has run out of copies of " + getBookInfo(isbn) + " book");
				}
			}
		} catch (SQLException e) {
			System.err.println("Error while checking inventory");
		}
		return result;
	}

	private String getBookInfo(long isbn) {
		ResultSet rs = null;
		String result = "";
		try {
			rs = getStatement().executeQuery("select * from book where isbn=" + isbn);
			while (rs.next()) {
				result = rs.getString("name");
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
		return result;
	}

	private void printInventory() {
		System.out.printf("%10s%10s%10s%n", "ISBN", "LOCATION", "QUANTITY");
		ResultSet rs = null;
		try {
			rs = getStatement().executeQuery("select * from inventory");
			while (rs.next()) {
				System.out.printf("%010d%10s%10s%n", rs.getLong("isbn"), rs.getString("location"),
						rs.getInt("quantity"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private void printAllBooks() {
		System.out.printf("%10s%35s%25s%45s %s\n", "ISBN", "NAME", "AUTHOR", "PUBLISHER", "YEAR PUBLISHED");
		ResultSet rs = null;
		try {
			rs = getStatement().executeQuery("select * from book");
			while (rs.next()) {
				System.out.printf("%010d%35s%25s%45s %14tY %n", rs.getLong("isbn"), rs.getString("name"),
						rs.getString("author"), rs.getString("publisher"), rs.getDate("yearPublished"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		}
	}

	private void printInformation() {
		if (welcome) {
			System.out.println(
					"Welcome to Library Management System. This library has a limited selection of Top 10 books. \nYou can only checkout books from this limited selection.");
			welcome = false;
		}
		System.out.println("You can view books, check inventory, and enter your information to checkout books.");
		System.out.println("Type books to view all books in the library,");
		System.out.println("Type inventory to check the inventory,");
		System.out.println("Type inventoryDesc to view the inventory sorted by quantity descending,");
		System.out.println("Type checkout to checkout a book,");
		System.out.println("Type loanedout to see checkout details,");
		System.out.println("type exit to exit the program.");
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
}
