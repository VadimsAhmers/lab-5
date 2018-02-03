import java.sql.*;
import java.util.concurrent.Callable;

public class DBTester {

    Connection connection = null;

    void connectToDB() throws SQLException{

        String macosUrl = "jdbc:hsqldb:hsql://localhost/groupdb";

        connection = DriverManager.getConnection(macosUrl, "SA", "");
    }

    void doWork(Connection c) throws SQLException{
        String group = "МО-104м";
        System.out.printf("ID группы %s: %d \n",group, getGroupID(group));
        //System.out.println("Some work is done");
        viewItemsInGroup("РБП-107м");
        viewItemsInGroupUsingJoin("ИТ-107м");

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

    // "УНИВЕРСАЛЬНЫЙ" МЕТОД ПЕЧАТИ ВСЕГО СОДЕРЖИМОГО ТАБЛИЦЫ С ЗАГОЛОВКАМИ ИЗ НАЗВАНИЙ СТОЛБЦОВ (П.6 ДОП)
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

    int getGroupID(String key){
        ResultSet result = null;
        int id = -1;
        try(PreparedStatement preparedStatement =
                connection.prepareStatement("SELECT ID FROM ITEMGROUP WHERE TITLE=?")){
                    preparedStatement.setString(1, key);
                    result = preparedStatement.executeQuery();
                    if (result.next())id = result.getInt(1);

        }catch (SQLException e){
            e.printStackTrace();
        }finally {
            try{
                if (result!=null) result.close();
            }catch (SQLException e){
                e.printStackTrace();
            }
        }
        if (id==-1) System.out.println("Что-то пошло не так");
        return id;
    }

    void viewItemsInGroup(int groupID){
        ResultSet resultSet = null;

        try (PreparedStatement preparedStatement
                     = connection.prepareStatement("SELECT * FROM ITEM WHERE GROUPID=?")) {
            preparedStatement.setInt(1, groupID);
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()){
                System.out.printf("%3d %10s\n", resultSet.getInt(1), resultSet.getString(2));
            }

        }catch (SQLException e){
            e.printStackTrace();
        }finally {
            try{
                if (resultSet!=null) resultSet.close();
            }catch (SQLException e){
                e.printStackTrace();
            }
        }
    }

    void viewItemsInGroup(String groupName){
        int groupID = getGroupID(groupName);
        viewItemsInGroup(groupID);
    }

    void viewItemsInGroupUsingJoin(String groupName){
        ResultSet resultSet = null;

        try (PreparedStatement preparedStatement
                     = connection.prepareStatement("SELECT ID, TITLE FROM ITEM JOIN ITEMGROUP ON ITEM.GROUPID = ITEMGROUP.ID WHERE ITEMGROUP.TITLE=?")) {
            preparedStatement.setString(1, groupName);
            resultSet = preparedStatement.executeQuery();
            while (resultSet.next()){
                System.out.printf("%3d %10s\n", resultSet.getInt(1), resultSet.getString(2));
            }

        }catch (SQLException e){
            e.printStackTrace();
        }finally {
            try{
                if (resultSet!=null) resultSet.close();
            }catch (SQLException e){
                e.printStackTrace();
            }
        }
    }

    void createTablesIfNeeded(){
        

    }
}
