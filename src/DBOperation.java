import java.sql.*;

public class DBOperation {
    private static DBOperation  dbo=null;
    private Connection con = null;
    private PreparedStatement pst = null;
    private ResultSet resultSet = null;
    private final String url = "jdbc:mysql://localhost/smartstorage_survey";
    private final String user = "root";
    private final String password = "sahan";
    public static boolean conError=false;
    public DBOperation(){

    }

    public static DBOperation getInstance(){//singleton pattern to access database object
        if(dbo==null){
            synchronized(DBOperation.class){
                dbo=new DBOperation();
            }
        }
        return dbo;
    }

    public boolean setConenction() throws SQLException{
        boolean reachable = false;
        try {
            Class.forName("com.mysql.jdbc.Driver").newInstance();
            con = DriverManager.getConnection(url, user, password);
            conError=false;
            reachable = con.isValid(30);
            return true;
        } catch (InstantiationException | IllegalAccessException | ClassNotFoundException ex) {
            conError=true;
            return false;
        }catch(Exception ex){
            conError=true;
            return false;
        }
    }
    public void closeConnection() throws SQLException {
        try{
            con.close();
            pst.close();
            resultSet.close();
        }catch(NullPointerException ex){}
    }

    public int getFileNameValue(String path) throws SQLException{
        setConenction();
        pst = con.prepareStatement("SELECT file_id FROM 352150070229403_files WHERE path = ?");
        pst.setString(1,path);
        resultSet=pst.executeQuery();
        int fileValue=0;
        if(resultSet.next()){
            fileValue=resultSet.getInt(1);
        }
        closeConnection();
        return  fileValue;
    }

    public int getFileSize(String path) throws SQLException{
        setConenction();
        pst = con.prepareStatement("SELECT size FROM 352150070229403_files WHERE path = ?");
        pst.setString(1,path);
        resultSet=pst.executeQuery();
        int fileSize=0;
        if(resultSet.next()){
            fileSize=resultSet.getInt(1);
        }
        closeConnection();
        return fileSize;
    }
}
