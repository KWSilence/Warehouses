package db;

import com.google.gson.Gson;

import java.io.File;
import java.io.FileReader;
import java.sql.Connection;
import java.sql.DriverManager;

public class ConnectionDB
{
  private static ConnectionDB connectionDB = null;
  private Connection connection;

  private class ConfigJSON
  {
    private final String db_url;
    private final String db_user;
    private final String db_password;

    ConfigJSON()
    {
      db_url = "";
      db_user = "";
      db_password = "";
    }
  }

  public static ConnectionDB getInstance()
  {
    if (connectionDB == null)
    {
      connectionDB = new ConnectionDB();
    }
    return connectionDB;
  }

  private ConnectionDB()
  {
    Gson gson = new Gson();
    ConfigJSON config = null;
    try
    {
      File f = new File("config.json");
      String fileName = f.exists() ? "config.json" : "src/main/resources/config.json";
      config = gson.fromJson(new FileReader(fileName), ConfigJSON.class);
      DriverManager.registerDriver(new oracle.jdbc.OracleDriver());
      connection = DriverManager.getConnection(config.db_url, config.db_user, config.db_password);
    }
    catch (Exception e)
    {
      e.printStackTrace();
    }
  }

  public Connection getConnection()
  {
    return connection;
  }
}
