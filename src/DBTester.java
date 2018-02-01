import java.sql.*;
import java.util.concurrent.Callable;

public class DBTester {

    Connection connection = null;

    void connectToDB() throws SQLException{

        connection = DriverManager.getConnection("jdbc:hsqldb:hsql://localhost/groupdb", "SA", "");
    }

    void doWork(Connection c) throws SQLException{
        System.out.println("Some work is done");

    }

    void test() throws SQLException{
        doWork(connection);

    }
    void viewGroups(){
        ResultSet result = null;

        try (Statement statement = connection.createStatement()){
            statement.execute("SELECT * FROM ITEMGROUP");
            result = statement.getResultSet();

            while (result.next()) {
                System.out.println(result.getString("TITLE"));
            }
        }catch (SQLException e){
            e.printStackTrace();
        }finally {
            try {
                if (result !=null) result.close();
            }catch (SQLException e){
                e.printStackTrace();
            }
        }
    }

    void viewItems(){

        ResultSet result = null;
        try(Statement statement = connection.createStatement()) {
            result = statement.executeQuery("SELECT * FROM ITEM");
            while (result.next()) {
                System.out.println(result.getString("TITLE"));
            }
        }catch (SQLException e){
            e.printStackTrace();
        }finally {
            try {
                if (result !=null) result.close();
            }catch (SQLException e){
                e.printStackTrace();
            }
        }
    }

    void printContent(){
        ResultSet result = null;
        try(Statement statement = connection.createStatement()) {
            result = statement.executeQuery("SELECT * FROM ITEM");

            ResultSetMetaData metaData = result.getMetaData();
            int columnCount = metaData.getColumnCount();
            for (int i = 1; i <= columnCount; i++) {
                System.out.printf("%20s", metaData.getColumnName(i));
            }

            while (result.next()) {
                System.out.println("");

                for (int i = 1; i <=columnCount ; i++) {
                    System.out.printf("%20s", result.getString(i));
                }
            }
        }catch (SQLException e){
            e.printStackTrace();
        }finally {
            try {
                if (result !=null) result.close();
            }catch (SQLException e){
                e.printStackTrace();
            }
        }
    }

    /*void viewContent() throws SQLException{
        Connection c = connectToDB();

        Statement statement = c.createStatement();

        statement.execute("SELECT * FROM ITEM");
        ResultSet itemgroups = statement.getResultSet();

        statement.execute("SELECT * FROM ITEMGROUP");
        ResultSet items = statement.getResultSet();


        while (items.next()) {
            String student = items.getString("TITLE");
            String group =
            System.out.printf("%10s из %5s", );
        }
        c.close();
    }*/

    int getGroupID(String key){
        //Connection c = connectToDB();

        return 1;
    }

}
