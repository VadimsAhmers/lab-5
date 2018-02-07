import java.sql.SQLException;

public class Main {
    public static void main(String[] args)  {

        try {
            DBTester tester = new DBTester();
            tester.connectToDB();
            //tester.createTablesIfNeeded();
            //tester.test();
            System.out.println(tester.addItemToGroup("Иванова И.И.", "РБП-107м"));
            System.out.println(tester.removeItemFromGroup("Иванова И.И.", "РБП-107м"));
            //tester.viewGroups();
            //tester.viewItems();
            //tester.printContent();
            //tester.viewItemsInGroup(1);
            tester.createTablesIfNeeded();
            //tester.editTablseFromFile();
            tester.editGroupsFromFile();
            tester.connection.close();

        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
