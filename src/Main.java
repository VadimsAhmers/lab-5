import java.sql.SQLException;

public class Main {
    public static void main(String[] args)  {

        try {
            DBTester tester = new DBTester();
            tester.connectToDB();
            tester.test();
            tester.viewGroups();
            tester.viewItems();
            tester.printContent();
            tester.connection.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
