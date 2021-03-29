package com.parkit.parkingsystem.integration.config;

import com.parkit.parkingsystem.config.DataBaseConfig;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.sql.*;
import java.util.Properties;
/**
 * The type Data base test config.
 */
public class DataBaseTestConfig extends DataBaseConfig {

    /** The Constant logger. */
    private static final Logger logger = LogManager.getLogger("DataBaseTestConfig");
    /**
     * Gets the connection.
     *
     * @return the connection
     * @throws ClassNotFoundException the class not found exception
     * @throws SQLException           the SQL exception
     */
    public Connection getConnection() throws ClassNotFoundException, SQLException, IOException {
        Properties properties = new Properties();
        FileInputStream in = new FileInputStream("src/main/resources/database.properties");
        properties.load(in);
        in.close();
        String driver = properties.getProperty("driver");
        String url = properties.getProperty("test");
        String username = properties.getProperty("username");
        String password = properties.getProperty("password");
        logger.info("Create DB connection");
        Class.forName(driver);
        return DriverManager.getConnection(
                url, username, password);
    }
    /**
     * Close prepared statement.
     */
    public void closeConnection(Connection con){
        if(con!=null){
            try {
                con.close();
                logger.info("Closing DB connection");
            } catch (SQLException e) {
                logger.error("Error while closing connection",e);
            }
        }
    }
    /**
     * Close prepared statement.
     */
    public void closePreparedStatement(PreparedStatement ps) {
        if(ps!=null){
            try {
                ps.close();
                logger.info("Closing Prepared Statement");
            } catch (SQLException e) {
                logger.error("Error while closing prepared statement",e);
            }
        }
    }
    /**
     * Close result set.
     */
    public void closeResultSet(ResultSet rs) {
        if(rs!=null){
            try {
                rs.close();
                logger.info("Closing Result Set");
            } catch (SQLException e) {
                logger.error("Error while closing result set",e);
            }
        }
    }
}
