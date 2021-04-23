package gui;

import db.ManagerDB;
import net.miginfocom.swing.MigLayout;
import org.jdesktop.swingx.*;
import org.jdesktop.swingx.renderer.DefaultTableRenderer;
import org.jdesktop.swingx.table.TableColumnExt;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.DateAxis;
import org.jfree.chart.axis.NumberAxis;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;
import org.jfree.data.time.Day;
import org.jfree.data.time.TimeSeries;
import org.jfree.data.time.TimeSeriesCollection;

import javax.swing.*;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import java.awt.*;
import java.text.SimpleDateFormat;
import java.util.List;
import java.util.*;
import java.util.stream.Collectors;


public class MainGUI
{

  private final JXFrame frame;
  private final ArrayList<MyTableListener> tl;
  private final ManagerDB db;

  public MainGUI(String login)
  {
    db = new ManagerDB();
    tl = new ArrayList<>(Arrays.asList(new MyTableListener(false), new MyTableListener(true), new MyTableListener(true),
                                       new MyTableListener(true)));

    frame = new JXFrame("User: " + login);
    frame.setLayout(new MigLayout("", "[fill, grow]", "[fill, grow]"));
    JTabbedPane tabbedPane = new JTabbedPane();
    JXPanel tabSales = new JXPanel(new MigLayout("", "[fill, grow][fill, grow]", "[][fill, grow][]"));
    tabSales.add(new JXLabel("Sales"), "align center, span");
    JXTable tableSales = createTable(new ArrayList<>(Arrays.asList("id", "name", "count", "date")),
                                     Arrays.asList(1, 2, 3, 4), true, true);
    tabSales.add(new JScrollPane(tableSales), "span");
    JXButton refreshSales = new JXButton("refresh");
    refreshSales.addActionListener(e -> fillTable(tableSales, db.getSales(), true, 0));
    tabSales.add(refreshSales);
    JXButton applySales = new JXButton("apply");
    tabSales.add(applySales);
    tableSales.setRowHeight(20);
    fillTable(tableSales, db.getSales(), true, 0);
    tabbedPane.addTab("Sales", tabSales);

    JXPanel tabWarehouses = new JXPanel(new MigLayout("", "[fill, grow][fill, grow]", "[][fill, grow][]"));
    tabWarehouses.add(new JXLabel("Warehouse1"));
    tabWarehouses.add(new JXLabel("Warehouse2"), "span");
    JXTable tableWarehouse1 = createTable(new ArrayList<>(Arrays.asList("id", "name", "count")), Arrays.asList(1, 2, 3),
                                          true, true);
    tabWarehouses.add(new JScrollPane(tableWarehouse1));
    JXTable tableWarehouse2 = createTable(new ArrayList<>(Arrays.asList("id", "name", "count")), Arrays.asList(1, 2, 3),
                                          true, true);
    tabWarehouses.add(new JScrollPane(tableWarehouse2), "span");
    JXButton refreshWarehouses = new JXButton("refresh");
    refreshWarehouses.addActionListener(e -> {
      fillTable(tableWarehouse1, db.getWarehouse(1), true, 1);
      fillTable(tableWarehouse2, db.getWarehouse(2), true, 2);
    });
    tabWarehouses.add(refreshWarehouses);
    JXButton applyWarehouses = new JXButton("apply");
    applyWarehouses.addActionListener(e -> {
      if (db.updateWarehouses(tl.get(1).getDiffData(), 1))
      {
        fillTable(tableWarehouse1, db.getWarehouse(1), true, 1);
      }
      if (db.updateWarehouses(tl.get(2).getDiffData(), 2))
      {
        fillTable(tableWarehouse2, db.getWarehouse(2), true, 2);
      }
    });
    tabWarehouses.add(applyWarehouses);
    tableWarehouse1.setRowHeight(20);
    tableWarehouse2.setRowHeight(20);
    fillTable(tableWarehouse1, db.getWarehouse(1), true, 1);
    fillTable(tableWarehouse2, db.getWarehouse(2), true, 2);
    tabbedPane.addTab("Warehouses", tabWarehouses);

    JXPanel tabGoods = new JXPanel(new MigLayout("", "[fill, grow][fill, grow]", "[][fill, grow][]"));
    tabGoods.add(new JXLabel("Goods"), "span");
    JXTable tableGoods = createTable(new ArrayList<>(Arrays.asList("id", "name", "priority")), Arrays.asList(1, 2, 3),
                                     true, false);
    tabGoods.add(new JScrollPane(tableGoods), "span");
    JXButton refreshGoods = new JXButton("refresh");
    refreshGoods.addActionListener(e -> fillTable(tableGoods, db.getGoods(), true, 3));
    tabGoods.add(refreshGoods);
    JXButton applyGoods = new JXButton("apply");
    tabGoods.add(applyGoods);
    fillTable(tableGoods, db.getGoods(), true, 3);
    tabbedPane.addTab("Goods", tabGoods);

    JXPanel tabForecast = new JXPanel(new MigLayout("", "[][fill, grow]", "[][][][][fill, grow]"));
    tabForecast.add(new JXLabel("From:"));
    JXDatePicker dateFrom = new JXDatePicker();
    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
    dateFrom.setFormats(format);
    tabForecast.add(dateFrom, "span");
    tabForecast.add(new JXLabel("To:"));
    JXDatePicker dateTo = new JXDatePicker();
    dateTo.setFormats(format);
    tabForecast.add(dateTo, "span");
    tabForecast.add(new JXLabel("Good:"));
    Vector<String> goods = getGoods();
    JComboBox<String> goodsBox = new JComboBox<>(goods);
    tabForecast.add(goodsBox, "span");
    tabForecast.add(new JXLabel("Day count:"));
    JXTextField dayCount = new JXTextField();
    tabForecast.add(dayCount);
    JXTable tableForecast = createTable(new ArrayList<>(Arrays.asList("day", "count")), null, false, false);
    JXButton buttonForecast = new JXButton("forecast");
    buttonForecast.addActionListener(e -> {
      if (dateFrom.getDate() == null || dateTo.getDate() == null)
      {
        JOptionPane.showMessageDialog(null, "All fields should be set (day count is optional, default = 7)");
        return;
      }
      if (dateFrom.getDate().after(dateTo.getDate()))
      {
        JOptionPane.showMessageDialog(null, "From should be before To");
        return;
      }
      if (!dayCount.getText().isEmpty() && !dayCount.getText().matches("\\d+"))
      {
        JOptionPane.showMessageDialog(null, "day count should be int > 0");
        return;
      }
      int count = dayCount.getText().isEmpty() ? 7 : Integer.parseInt(dayCount.getText());
      if (count == 0)
      {
        JOptionPane.showMessageDialog(null, "day count should be int > 0");
        return;
      }
      String from = format.format(dateFrom.getDate());
      String to = format.format(dateTo.getDate());
      String good = (String) goodsBox.getSelectedItem();
      fillTable(tableForecast, db.getForecast(from, to, good, count), false, null);
    });
    tabForecast.add(buttonForecast, "grow x, span");
    tabForecast.add(new JScrollPane(tableForecast), "grow x, span");
    tabbedPane.addTab("Forecast", tabForecast);

    JXPanel tabDemand = new JXPanel(new MigLayout("", "[][fill, grow][][fill, grow]", "[][][][][][][][fill, grow]"));
    tabDemand.add(new JXLabel("[Most popular]"), "span");
    tabDemand.add(new JXLabel("From:"));
    JXDatePicker popularFrom = new JXDatePicker();
    popularFrom.setFormats(format);
    tabDemand.add(popularFrom);
    tabDemand.add(new JXLabel("To:"));
    JXDatePicker popularTo = new JXDatePicker();
    popularTo.setFormats(format);
    tabDemand.add(popularTo, "span");
    tabDemand.add(new JXLabel("Good Count:"));
    JXTextField popularField = new JXTextField();
    tabDemand.add(popularField);
    JXButton popularButton = new JXButton("getPopular");
    tabDemand.add(popularButton, "grow x, span");
    JXTable popularTable = createTable(new ArrayList<>(Arrays.asList("id", "name", "count")), null, false, false);
    popularButton.addActionListener(e -> {
      if (popularFrom.getDate() == null || popularTo.getDate() == null)
      {
        JOptionPane.showMessageDialog(null, "{Popular} All fields should be set (good count is optional, default = 5)");
        return;
      }
      if (popularFrom.getDate().after(popularTo.getDate()))
      {
        JOptionPane.showMessageDialog(null, "{Popular} From should be before To");
        return;
      }
      if (!popularField.getText().isEmpty() && !popularField.getText().matches("\\d+"))
      {
        JOptionPane.showMessageDialog(null, "{Popular} Good count should be int > 0");
        return;
      }
      int count = popularField.getText().isEmpty() ? 5 : Integer.parseInt(popularField.getText());
      if (count == 0)
      {
        JOptionPane.showMessageDialog(null, "{Popular} day count should be int > 0");
        return;
      }
      String from = format.format(popularFrom.getDate());
      String to = format.format(popularTo.getDate());
      fillTable(popularTable, db.getPopularGoods(from, to, count), false, null);
    });
    tabDemand.add(new JScrollPane(popularTable), "grow x, height max(550px,25%), span");

    tabDemand.add(new JXLabel("[Demand chart]"), "span");
    tabDemand.add(new JXLabel("From:"));
    JXDatePicker demandFrom = new JXDatePicker();
    demandFrom.setFormats(format);
    tabDemand.add(demandFrom);
    tabDemand.add(new JXLabel("To:"));
    JXDatePicker demandTo = new JXDatePicker();
    demandTo.setFormats(format);
    tabDemand.add(demandTo, "span");
    tabDemand.add(new JXLabel("Good:"));
    JComboBox<String> demandGoods = new JComboBox<>(goods);
    tabDemand.add(demandGoods);
    JXButton demandButton = new JXButton("Get Demand");
    tabDemand.add(demandButton, "grow x, span");
    JFreeChart demandChart = createTimeChart("Demand", "day", "count");
    tabDemand.add(new ChartPanel(demandChart), "grow x, span");
    tabbedPane.addTab("Demand", tabDemand);
    demandButton.addActionListener(e -> {
      if (demandFrom.getDate() == null || demandTo.getDate() == null)
      {
        JOptionPane.showMessageDialog(null, "{Demand} All fields should be set");
        return;
      }
      if (demandFrom.getDate().after(demandTo.getDate()))
      {
        JOptionPane.showMessageDialog(null, "{Demand} From should be before To");
        return;
      }
      String from = format.format(demandFrom.getDate());
      String to = format.format(demandTo.getDate());
      String good = (String) demandGoods.getSelectedItem();
      ArrayList<ArrayList<String>> data = db.getDemand(from, to, good);
      demandChart.getXYPlot().setDataset(null);
      TimeSeries series = new TimeSeries("demand of " + good + " in [" + from + "; " + to + "]");
      for (ArrayList<String> row : data)
      {
        if (row == null)
        {
          break;
        }
        try
        {
          series.add(new Day(format.parse(row.get(0))), Integer.parseInt(row.get(1)));
        }
        catch (Exception ex)
        {
          ex.printStackTrace();
        }
      }
      demandChart.getXYPlot().setDataset(new TimeSeriesCollection(series));
    });
    frame.add(tabbedPane);


    applySales.addActionListener(e -> {
      if (db.updateSales(tl.get(0).getDiffData()))
      {
        fillTable(tableSales, db.getSales(), true, 0);
        fillTable(tableWarehouse1, db.getWarehouse(1), true, 1);
        fillTable(tableWarehouse2, db.getWarehouse(2), true, 2);
      }
    });

    applyGoods.addActionListener(e -> {
      if (db.updateGoods(tl.get(3).getDiffData()))
      {
        fillTable(tableSales, db.getSales(), true, 0);
        fillTable(tableWarehouse1, db.getWarehouse(1), true, 1);
        fillTable(tableWarehouse2, db.getWarehouse(2), true, 2);
        fillTable(tableGoods, db.getGoods(), true, 3);

        Vector<String> g_list = getGoods();
        goodsBox.setModel(new DefaultComboBoxModel<>(g_list));
        demandGoods.setModel(new DefaultComboBoxModel<>(g_list));
        tableSales.getColumn(1).setCellEditor(new DefaultCellEditor(new JXComboBox(g_list)));
        ((DefaultCellEditor) tableSales.getColumnExt(1).getCellEditor()).setClickCountToStart(2);
        tableWarehouse1.getColumn(1).setCellEditor(new DefaultCellEditor(new JXComboBox(g_list)));
        ((DefaultCellEditor) tableWarehouse1.getColumnExt(1).getCellEditor()).setClickCountToStart(2);
        tableWarehouse2.getColumn(1).setCellEditor(new DefaultCellEditor(new JXComboBox(g_list)));
        ((DefaultCellEditor) tableWarehouse2.getColumnExt(1).getCellEditor()).setClickCountToStart(2);
      }
    });

    tableSales.getColumn(1).setCellEditor(new DefaultCellEditor(new JXComboBox(goods)));
    ((DefaultCellEditor) tableSales.getColumnExt(1).getCellEditor()).setClickCountToStart(2);
    tableWarehouse1.getColumn(1).setCellEditor(new DefaultCellEditor(new JXComboBox(goods)));
    ((DefaultCellEditor) tableWarehouse1.getColumnExt(1).getCellEditor()).setClickCountToStart(2);
    tableWarehouse2.getColumn(1).setCellEditor(new DefaultCellEditor(new JXComboBox(goods)));
    ((DefaultCellEditor) tableWarehouse2.getColumnExt(1).getCellEditor()).setClickCountToStart(2);

    setIntSorter(tableSales, Collections.singletonList(2));
    setIntSorter(tableWarehouse1, Collections.singletonList(2));
    setIntSorter(tableWarehouse2, Collections.singletonList(2));
    setIntSorter(tableGoods, Arrays.asList(0, 2));
    setIntSorter(popularTable, Arrays.asList(0, 2));

  }

  public void show()
  {
    frame.setDefaultCloseOperation(JXFrame.EXIT_ON_CLOSE);
    frame.setStartPosition(JXFrame.StartPosition.CenterInScreen);
    frame.setSize(620, 700);
    frame.setVisible(true);
  }


  private void fillTable(JXTable table, ArrayList<ArrayList<String>> data, boolean enableInsert, Integer listenerInd)
  {
    MyTableListener listener = null;
    if (listenerInd != null)
    {
      listener = tl.get(listenerInd);
      listener.setWait(true);
    }
    ((DefaultTableModel) table.getModel()).setRowCount(0);
    int size = data.get(0) == null ? (enableInsert ? 1 : 0) : data.size() + (enableInsert ? 1 : 0);
    ((DefaultTableModel) table.getModel()).setRowCount(size);
    table.setRowHeight(table.getRowHeight());
    if (size == 0)
    {
      return;
    }
    for (int i = 0; i < data.size(); i++)
    {
      ArrayList<String> row = data.get(i);
      for (int j = 0; j < row.size(); j++)
      {
        table.setValueAt(row.get(j), i, j);
      }
    }
    if (enableInsert)
    {
      table.setValueAt("n", size - 1, table.getColumnCount() - 1);
    }
    if (listenerInd != null)
    {
      table.getModel().addTableModelListener(listener);
      listener.setData(table);
      listener.setWait(false);
    }
  }

  private JXTable createTable(ArrayList<String> headers, List<Integer> editable, boolean enableDelete,
                              boolean hideFirst)
  {
    if (enableDelete)
    {
      headers.add("del");
    }
    headers.add("");
    JXTable table = new JXTable(new DefaultTableModel(headers.toArray(), 0))
    {
      private final List<Integer> editableColumns = editable;

      @Override
      public boolean isCellEditable(int row, int column)
      {
        if (editableColumns != null)
        {
          return editableColumns.isEmpty() || editableColumns.contains(column);
        }
        return false;
      }

      @Override
      public void setValueAt(Object aValue, int row, int column)
      {
        getModel().setValueAt(aValue, convertRowIndexToModel(row), convertColumnIndexToModel(column));
      }
    };

    if (enableDelete)
    {
      TableColumnExt del = table.getColumnExt(table.getColumnCount() - 2);
      del.setCellRenderer((table1, value, isSelected, hasFocus, row, column) -> {
        String st = (String) table1.getValueAt(row, table1.getColumnCount() - 1);
        if (st != null && (st.equals("d") || st.equals("i") || st.equals("n")))
        {
          return new JXLabel();
        }
        return new JXButton("d");
      });
      del.setCellEditor(new DefaultCellEditor(new JCheckBox())
      {
        @Override
        public Component getTableCellEditorComponent(JTable table, Object value, boolean isSelected, int row,
                                                     int column)
        {
          int stCol = table.getColumnCount() - 1;
          String st = (String) table.getValueAt(row, stCol);
          if (st != null && (st.equals("n") || st.equals("i") || st.equals("d")))
          {
            return new JXLabel();
          }
          table.setValueAt("d", row, stCol);
          return new JXButton("d");
        }

        @Override
        public Object getCellEditorValue()
        {
          return "";
        }
      });
      del.setMaxWidth(50);
      del.setMinWidth(50);
    }

    table.setCellSelectionEnabled(false);
    table.setFocusable(false);
    table.getTableHeader().setReorderingAllowed(false);
    for (TableColumn col : table.getColumns())
    {
      col.setResizable(false);
    }
    TableColumnExt col = table.getColumnExt(table.getColumnCount() - 1);
    col.setMaxWidth(10);
    if (hideFirst)
    {
      hideColumn(table, 0);
    }
    return table;
  }

  private void hideColumn(JXTable table, int index)
  {
    TableColumnExt col = table.getColumnExt(index);
    col.setMaxWidth(0);
    col.setMinWidth(0);
    col.setPreferredWidth(0);
  }

  private Vector<String> getGoods()
  {
    return db.getGoods().stream().map(x -> x.get(1)).collect(Collectors.toCollection(Vector::new));
  }

  private void setIntSorter(JXTable table, List<Integer> columns)
  {
    Comparator<String> comp = (o1, o2) -> {
      Integer i1 = Integer.parseInt(o1.replaceAll("\\D+", ""));
      Integer i2 = Integer.parseInt(o2.replaceAll("\\D+", ""));

      return i1.compareTo(i2);
    };
    TableRowSorter<TableModel> sorter = new TableRowSorter<>(table.getModel());
    for (int col : columns)
    {
      sorter.setComparator(col, comp);
    }
    table.setRowSorter(sorter);
  }

  private JFreeChart createTimeChart(String title, String xl, String yl)
  {
    JFreeChart chart = ChartFactory.createTimeSeriesChart(title, xl, yl, null, true, true, true);
    XYPlot plot = chart.getXYPlot();
    plot.getRangeAxis().setStandardTickUnits(NumberAxis.createIntegerTickUnits());
//    ((DateAxis) plot.getDomainAxis()).setTickUnit(new DateTickUnit(DateTickUnitType.DAY, 1));
    ((DateAxis) plot.getDomainAxis()).setDateFormatOverride(new SimpleDateFormat("yyyy-MM-dd"));

    XYLineAndShapeRenderer renderer = new XYLineAndShapeRenderer(true, true);
    plot.setRenderer(renderer);

//    Stroke stroke = new BasicStroke(2f, BasicStroke.CAP_ROUND, BasicStroke.JOIN_BEVEL);
//    renderer.setDefaultStroke(stroke);

//    NumberFormat format = NumberFormat.getNumberInstance();
//    format.setMaximumFractionDigits(0);
//    XYItemLabelGenerator generator = new StandardXYItemLabelGenerator(StandardXYItemLabelGenerator.DEFAULT_ITEM_LABEL_FORMAT, format, format);
//    renderer.setDefaultItemLabelGenerator(generator);
//    renderer.setDefaultItemLabelsVisible(true);

    return chart;
  }
}

