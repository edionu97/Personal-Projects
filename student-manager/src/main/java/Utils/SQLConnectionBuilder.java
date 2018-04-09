package Utils;

import java.sql.Connection;
import java.sql.DriverManager;

public class SQLConnectionBuilder {

    public static Connection connectTo(String databaseName) throws  Exception{

        Connection connection = null;

        Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");//loading the jbdc driver

        connection = DriverManager.getConnection("jdbc:sqlserver://localhost;database="+databaseName+";integratedSecurity=true;");

        return connection;
    }

}
