package db;

import javax.crypto.spec.SecretKeySpec;
import javax.swing.*;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class ManagerDB
{
  private final ConnectionDB connectionDB;
  private final Connection connection;
  private final SecretKeySpec key;
  private final SimpleDateFormat df;

  public ManagerDB()
  {
    connectionDB = ConnectionDB.getInstance();
    connection = connectionDB.getConnection();
    key = Encryptor.createDefaultSecretKey();
    df = new SimpleDateFormat("yyyy-MM-dd HH:mm");
  }

  public boolean userIsExist(String login, String password)
  {
    try (Statement statement = connection.createStatement())
    {
      try (ResultSet resultSet = statement.executeQuery("select * from users where login='" + login + "'"))
      {
        if (resultSet.next())
        {
          return Encryptor.decrypt(resultSet.getString(2), key).equals(password);
        }
        else
        {
          return false;
        }
      }
    }
    catch (Exception e)
    {
      e.printStackTrace();
      return false;
    }
  }

  public boolean userIsExist(String login)
  {
    try (Statement statement = connection.createStatement())
    {
      try (ResultSet resultSet = statement.executeQuery("select * from users where login='" + login + "'"))
      {
        return resultSet.next();
      }
    }
    catch (Exception e)
    {
      e.printStackTrace();
      return false;
    }
  }

  public boolean addUser(String login, String password)
  {
    if (userIsExist(login))
    {
      return false;
    }
    try (Statement statement = connection.createStatement())
    {
      statement.executeUpdate(
        "insert into users (login, password) values ('" + login + "','" + Encryptor.encrypt(password, key) + "')");
      return true;
    }
    catch (Exception e)
    {
      e.printStackTrace();
      return false;
    }
  }

  public ArrayList<ArrayList<String>> getGoods()
  {
    return execSelect("select * from goods");
  }

  public Integer getGoodId(String name)
  {
    ArrayList<String> j = execSelect("select * from goods where name = '" + name + "'").get(0);
    return j == null ? null : Integer.parseInt(j.get(0));
  }

  public ArrayList<ArrayList<String>> getWarehouse(int ind)
  {
    return execSelect(
      "select warehouse" + ind + ".id, name, good_count from goods join warehouse" + ind + " on goods.id = good_id");
  }

  public ArrayList<ArrayList<String>> getSales()
  {
    return execSelect(
      "select sales.id, name, good_count, create_date from goods join sales on goods.id = good_id order by create_date desc");
  }

  public ArrayList<ArrayList<String>> getForecast(String from, String to, String good, int dayCount)
  {
    return execSelect("select * from table ( getDemandForecast(to_date('" + from + "', 'yyyy-MM-dd'), to_date('" + to +
                      "','yyyy-MM-dd'), " + getGoodId(good) + ", " + dayCount + ") )");
  }

  public ArrayList<ArrayList<String>> getDemand(String from, String to, String good)
  {
//    ArrayList<ArrayList<String>> tmp = execSelect(
//      "select TO_DATE(to_char(create_date, 'dd.mm.yyyy'), 'dd.mm.yyyy') - TO_DATE('" + from +
//      "','yyyy-MM-dd'), sum(good_count) from sales " + "where good_id = " + getGoodId(good) +
//      " and create_date between TO_DATE('" + from + "','yyyy-MM-dd') and TO_DATE('" + to + "','yyyy-MM-dd')" +
//      "group by good_id, TO_DATE(to_char(create_date, 'dd.mm.yyyy'), 'dd.mm.yyyy')" +
//      "order by TO_DATE(to_char(create_date, 'dd.mm.yyyy'), 'dd.mm.yyyy')");
//    ArrayList<ArrayList<Integer>> res = new ArrayList<>();
//    for (ArrayList<String> row : tmp)
//    {
//      if (row == null)
//      {
//        res.add(new ArrayList<>(Arrays.asList(0, 0)));
//        return res;
//      }
//      ArrayList<Integer> tmpRes = new ArrayList<>();
//      for (String el : row)
//      {
//        tmpRes.add(Integer.parseInt(el));
//      }
//      res.add(tmpRes);
//    }
//    return res;
    ArrayList<ArrayList<String>> tmp = execSelect(
      "select TO_DATE(to_char(create_date, 'dd.mm.yyyy'), 'dd.mm.yyyy'), sum(good_count) from sales " +
      "where good_id = " + getGoodId(good) + " and create_date between TO_DATE('" + from +
      "','yyyy-MM-dd') and TO_DATE('" + to + "','yyyy-MM-dd')" +
      "group by good_id, TO_DATE(to_char(create_date, 'dd.mm.yyyy'), 'dd.mm.yyyy')" +
      "order by TO_DATE(to_char(create_date, 'dd.mm.yyyy'), 'dd.mm.yyyy')");
    return tmp;
  }

  public ArrayList<ArrayList<String>> getPopularGoods(String from, String to, int count)
  {
    return execSelect(
      "select goods.id, tmp.name, tmp.count from (select name, sum(good_count) as \"COUNT\" from sales join goods on sales.good_id = goods.id where create_date between TO_DATE('" +
      from + "', 'yyyy-MM-dd') and TO_DATE('" + to +
      "', 'yyyy-MM-dd') group by goods.name order by sum(good_count) desc FETCH NEXT " + count +
      " ROWS ONLY) tmp join goods on goods.name = tmp.name");
  }

  private ArrayList<ArrayList<String>> execSelect(String sql)
  {
    ArrayList<ArrayList<String>> rows = new ArrayList<>();
    try (Statement statement = connection.createStatement())
    {
      try (ResultSet resultSet = statement.executeQuery(sql))
      {
        ResultSetMetaData rsm = resultSet.getMetaData();
        while (resultSet.next())
        {
          ArrayList<String> row = new ArrayList<>();
          for (int i = 1; i <= rsm.getColumnCount(); i++)
          {
            if (rsm.getColumnTypeName(i).equals("TIMESTAMP"))
            {
              row.add(df.format(resultSet.getDate(i)));
            }
            else
            {
              row.add(resultSet.getString(i));
            }
          }
          rows.add(row);
        }
        if (rows.isEmpty())
        {
          rows.add(null);
        }
      }
    }
    catch (Exception e)
    {
      e.printStackTrace();
      JOptionPane.showMessageDialog(null, e.getMessage());
    }
    return rows;
  }

  public boolean updateSales(List<ArrayList<String>> data)
  {
    try (Statement statement = connection.createStatement())
    {
      for (ArrayList<String> row : data)
      {
        String id = row.get(0);
        String count = row.get(2) == null ? "1" : row.get(2);
        String date = row.get(3) == null ? getCurTime() : row.get(3);
        int good = getGoodId(row.get(1));
        if (!validCount(count))
        {
          throw new Exception("Count should be number >= 1");
        }
        if (!validDate(date))
        {
          throw new Exception("Incorrect date should be format \"yyyy-MM-dd hh24:mi\"");
        }
        String state = row.get(row.size() - 1);
        if (state.equals("m"))
        {
          statement.executeUpdate(
            "update sales set good_id=" + good + ", good_count=" + count + ", create_date=TO_DATE('" + date +
            "','yyyy-MM-dd hh24:mi') where id = " + id);
        }
        if (state.equals("i"))
        {
          statement.executeUpdate(
            "insert into sales (good_id, good_count, create_date) values (" + good + ", " + count + ",TO_DATE('" +
            date + "','yyyy-MM-dd hh24:mi'))");
          statement.execute("call takeFromWarehouses(" + good + ", " + count + ")");
        }
        if (state.equals("d"))
        {
          statement.executeUpdate("delete from sales where id=" + id);
        }
      }
    }
    catch (Exception e)
    {
      e.printStackTrace();
      JOptionPane.showMessageDialog(null, parseError(e.getMessage()));
      return false;
    }
    return true;
  }

  public boolean updateWarehouses(List<ArrayList<String>> data, int ind)
  {
    String warehouse = "warehouse" + ind;
    try (Statement statement = connection.createStatement())
    {
      for (ArrayList<String> row : data)
      {
        String id = row.get(0);
        String count = row.get(2) == null ? "1" : row.get(2);
        int good = getGoodId(row.get(1));
        if (!validCount(count))
        {
          throw new Exception("Count should be number >= 1");
        }
        String state = row.get(row.size() - 1);
        if (state.equals("m"))
        {
          statement.executeUpdate(
            "update " + warehouse + " set good_id=" + good + ", good_count=" + count + " where id = " + id);
        }
        if (state.equals("i"))
        {
          statement
            .executeUpdate("insert into " + warehouse + " (good_id, good_count) values (" + good + ", " + count + ")");
        }
        if (state.equals("d"))
        {
          statement.executeUpdate("delete from " + warehouse + " where id=" + id);
        }
      }
    }
    catch (Exception e)
    {
      e.printStackTrace();
      JOptionPane.showMessageDialog(null, parseError(e.getMessage()));
      return false;
    }
    return true;
  }

  public boolean updateGoods(List<ArrayList<String>> data)
  {
    try (Statement statement = connection.createStatement())
    {
      for (ArrayList<String> row : data)
      {
        String id = row.get(0);
        String name = row.get(1);
        String priority = row.get(2) == null ? "0" : row.get(2);
        if (!validPriority(priority))
        {
          throw new Exception("Priority should be float number > 0");
        }
        String state = row.get(row.size() - 1);
        if (state.equals("m"))
        {
          statement.executeUpdate("update goods set name='" + name + "', priority=" + priority + " where id = " + id);
        }
        if (state.equals("i"))
        {
          statement.executeUpdate("insert into goods (name, priority) values ('" + name + "', " + priority + ")");
        }
        if (state.equals("d"))
        {
          statement.executeUpdate("delete from goods where id=" + id);
        }
      }
    }
    catch (Exception e)
    {
      e.printStackTrace();
      JOptionPane.showMessageDialog(null, parseError(e.getMessage()));
      return false;
    }
    return true;
  }

  private String getCurTime()
  {
    return df.format(Calendar.getInstance().getTime());
  }

  private String parseError(String er)
  {
    return er.contains("\n") ? er.split("\n")[0].split(": ")[1] : er;
  }

  private boolean validDate(String s)
  {
    return s.matches("\\d{4}-\\d{2}-\\d{2} \\d{2}:\\d{2}");
  }

  private boolean validCount(String s)
  {
    return s.matches("\\d+") && !s.equals("0");
  }

  private boolean validPriority(String s)
  {
    return s.matches("\\d+\\.?\\d*");
  }
}
