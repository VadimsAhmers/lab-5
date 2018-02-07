import java.io.BufferedReader;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.sql.*;
import java.util.HashSet;
import java.util.Set;
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

    void createTablesIfNeeded() {
        ResultSet databaseTables = null;
        boolean table1Exists = false, table2Exists = false;

        try {
            DatabaseMetaData metaData = connection.getMetaData();
            databaseTables =
                    metaData.getTables(null, null, "%", null);
            while (databaseTables.next()) {
                if (databaseTables.getString(3).equals("ITEM")) table1Exists = true;
                if (databaseTables.getString(3).equals("ITEMGROUP")) table2Exists = true;
            }
            if (!table1Exists & !table2Exists) createAndFillTablesFromFile();
        } catch (SQLException e) {
            e.printStackTrace();
        } finally {
            try {
                if (databaseTables != null) databaseTables.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

        void createAndFillTablesFromFile(){

        try(Statement statement = connection.createStatement(); BufferedReader br = new BufferedReader(new InputStreamReader
                (new FileInputStream("createTables.sql")))){

                String s1, s2="";
                while ((s1=br.readLine())!=null) {
                    s2 += s1;
                    if (s1.endsWith(";")) {
                        statement.executeQuery(s2);
                        s2 = "";
                    }
                }
            System.out.println("Таблицы созданы и заполнены");
            }catch (Exception e){
            e.printStackTrace();
        }
    }

    boolean addItemToGroup(String itemName, String groupName){
        try(PreparedStatement statement =
                    connection.prepareStatement("INSERT INTO ITEM(TITLE, GROUPID) VALUES (?,?)")){
            statement.setString(1, itemName);
            statement.setInt(2, getGroupID(groupName));
            statement.execute();
            System.out.printf("Студент %s добавлен(а)\n", itemName);
            return true;

        }catch (SQLException e){
            e.printStackTrace();
            return false;
        }
    }

    boolean removeItemFromGroup(String itemName, String groupName){
        try(PreparedStatement statement =
                    connection.prepareStatement
                            ("DELETE FROM ITEM WHERE TITLE=? AND GROUPID=?")){
            statement.setString(1, itemName);
            statement.setInt(2, getGroupID(groupName));
            statement.execute();
            System.out.printf("Студент %s удален(а)\n", itemName);
            return true;

        }catch (SQLException e){
            e.printStackTrace();
            return false;
        }
    }

    void editTablseFromFile(){
        boolean everythingIsOk = false;
        try(Statement statement = connection.createStatement();
            BufferedReader br = new BufferedReader(new InputStreamReader
                (new FileInputStream("items.txt")))){

            connection.setAutoCommit(false);
            String s;

            while ((s=br.readLine())!=null) {
                if (s.contains("++")) {
                    String[] str = s.split("\\++");
                    everythingIsOk = addItemToGroup(str[1], str[0]);
                }else
                    if (s.contains("--")) {
                    String[] str = s.split("--");
                    everythingIsOk = removeItemFromGroup(str[1], str[0]);
                }
                if (!everythingIsOk){
                    connection.rollback();
                    break;
                }
            }
            if (everythingIsOk) connection.commit();

        }catch (SQLException|IOException e){
            e.printStackTrace();
        } finally {
            try {
                connection.setAutoCommit(true);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
    }

    void editGroupsFromFile(){
        Set<String> groupsToAdd = new HashSet<>();
        Set<String> groupsToRemove = new HashSet<>();
        int lastGroupID=0;

        try(Statement statement = connection.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);
            BufferedReader br = new BufferedReader(new InputStreamReader
                    (new FileInputStream("groups.txt")));
            ResultSet resultSet = statement.executeQuery("SELECT * FROM ITEMGROUP")){

            connection.setAutoCommit(false);
            String s;

            while ((s=br.readLine())!=null) {
                if (s.contains("++")) {
                    groupsToAdd.add(s.replace("++", ""));
                }else
                if (s.contains("--")) {
                    String toDelete = s.replace("--", "");
                    if (groupsToAdd.contains(toDelete)) groupsToAdd.remove(toDelete);
                    else groupsToRemove.add(toDelete);
                }
            }

            while (resultSet.next()){
                String groupName = resultSet.getString(2);
                if (groupsToRemove.contains(groupName)) resultSet.deleteRow();
                if (groupsToAdd.contains(groupName)) groupsToAdd.remove(groupName);
                lastGroupID++;
            }

            for (String str : groupsToAdd) {
                    resultSet.moveToInsertRow();
                    resultSet.updateInt(1, lastGroupID++);
                    resultSet.updateString(2, str);
                    resultSet.insertRow();
                }
            connection.commit();
        }catch (Exception e){
            e.printStackTrace();
        }finally {
            try {
                connection.setAutoCommit(true);
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }

    }
}
