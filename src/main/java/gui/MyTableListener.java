package gui;

import javax.swing.*;
import javax.swing.event.TableModelEvent;
import javax.swing.event.TableModelListener;
import javax.swing.table.DefaultTableModel;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class MyTableListener implements TableModelListener
{
  private final ArrayList<ArrayList<String>> data;
  private final boolean errOnRepeat;
  private boolean ignore;
  private boolean wait;

  public MyTableListener(boolean err)
  {
    super();
    data = new ArrayList<>();
    errOnRepeat = err;
    ignore = false;
    wait = false;
  }

  @Override
  public void tableChanged(TableModelEvent e)
  {
    if (wait)
    {
      return;
    }
    if (ignore)
    {
      ignore = false;
      return;
    }
    DefaultTableModel model = (DefaultTableModel) e.getSource();
    int stateCol = model.getColumnCount() - 1;
    int column = e.getColumn();
    if (column == -1 || column == stateCol - 1)
    {
      return;
    }
    int row = e.getLastRow();
    String data = (String) model.getValueAt(row, column);
    if (model.getValueAt(row, 1) == null && data != null && !data.isEmpty() && column != 1)
    {
      ignore = true;
      model.setValueAt(this.data.get(row).get(column), row, column);
      JOptionPane.showMessageDialog(null, "First you should set good");
      return;
    }
    if (column == stateCol && !data.equals("n"))
    {
      this.data.get(row).set(column, data);
    }
    String state = (String) model.getValueAt(row, stateCol);
    if (data == null || data.isEmpty())
    {
      return;
    }
    if (state == null)
    {
      if (!data.equals(this.data.get(row).get(column)))
      {
        if (checkRep(model, column, row, data))
        {
          return;
        }
        model.setValueAt("m", row, stateCol);
        this.data.get(row).set(column, data);
      }
      return;
    }
    if (state.equals("n"))
    {
      if (checkRep(model, column, row, data))
      {
        return;
      }
      model.setValueAt("i", row, stateCol);
      String[] ar = new String[stateCol + 1];
      ar[stateCol] = "n";
      model.addRow(ar);
      this.data.add(new ArrayList<>(Arrays.asList(ar)));
      this.data.get(row).set(column, data);
      return;
    }
    if (state.equals("m"))
    {
      if (checkRep(model, column, row, data))
      {
        return;
      }
      this.data.get(row).set(column, data);
    }
    if (state.equals("i"))
    {
      this.data.get(row).set(column, data);
    }
  }

  private boolean checkRep(DefaultTableModel model, int column, int row, String data)
  {
    if (errOnRepeat && column == 1)
    {
      if (getIdRepeat(data) != null)
      {
        ignore = true;
        model.setValueAt(this.data.get(row).get(column), row, column);
        JOptionPane.showMessageDialog(null, "This good already exist");
        return true;
      }
    }
    return false;
  }

  public void setData(JTable table)
  {
    data.clear();
    for (int i = 0; i < table.getModel().getRowCount(); i++)
    {
      ArrayList<String> row = new ArrayList<>();
      for (int j = 0; j < table.getModel().getColumnCount(); j++)
      {
        row.add((String) table.getValueAt(i, j));
      }
      data.add(row);
    }
  }

  public List<ArrayList<String>> getDiffData()
  {
    return data.stream().filter(row -> (row.get(row.size() - 1) != null && !row.get(row.size() - 1).equals("n")))
               .collect(Collectors.toList());
  }

  private String getIdRepeat(String s)
  {
    for (ArrayList<String> row : data)
    {
      if (s.equals(row.get(1)))
      {
        return row.get(0);
      }
    }
    return null;
  }

  public void setWait(boolean wait)
  {
    this.wait = wait;
  }
}
