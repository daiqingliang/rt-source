package javax.swing.table;

import java.text.Collator;
import java.util.Comparator;
import javax.swing.DefaultRowSorter;

public class TableRowSorter<M extends TableModel> extends DefaultRowSorter<M, Integer> {
  private static final Comparator COMPARABLE_COMPARATOR = new ComparableComparator(null);
  
  private M tableModel;
  
  private TableStringConverter stringConverter;
  
  public TableRowSorter() { this(null); }
  
  public TableRowSorter(M paramM) { setModel(paramM); }
  
  public void setModel(M paramM) {
    this.tableModel = paramM;
    setModelWrapper(new TableRowSorterModelWrapper(null));
  }
  
  public void setStringConverter(TableStringConverter paramTableStringConverter) { this.stringConverter = paramTableStringConverter; }
  
  public TableStringConverter getStringConverter() { return this.stringConverter; }
  
  public Comparator<?> getComparator(int paramInt) {
    Comparator comparator = super.getComparator(paramInt);
    if (comparator != null)
      return comparator; 
    Class clazz = ((TableModel)getModel()).getColumnClass(paramInt);
    return (clazz == String.class) ? Collator.getInstance() : (Comparable.class.isAssignableFrom(clazz) ? COMPARABLE_COMPARATOR : Collator.getInstance());
  }
  
  protected boolean useToString(int paramInt) {
    Comparator comparator = super.getComparator(paramInt);
    if (comparator != null)
      return false; 
    Class clazz = ((TableModel)getModel()).getColumnClass(paramInt);
    return (clazz == String.class) ? false : (!Comparable.class.isAssignableFrom(clazz));
  }
  
  private static class ComparableComparator implements Comparator {
    private ComparableComparator() {}
    
    public int compare(Object param1Object1, Object param1Object2) { return ((Comparable)param1Object1).compareTo(param1Object2); }
  }
  
  private class TableRowSorterModelWrapper extends DefaultRowSorter.ModelWrapper<M, Integer> {
    private TableRowSorterModelWrapper() {}
    
    public M getModel() { return (M)TableRowSorter.this.tableModel; }
    
    public int getColumnCount() { return (TableRowSorter.this.tableModel == null) ? 0 : TableRowSorter.this.tableModel.getColumnCount(); }
    
    public int getRowCount() { return (TableRowSorter.this.tableModel == null) ? 0 : TableRowSorter.this.tableModel.getRowCount(); }
    
    public Object getValueAt(int param1Int1, int param1Int2) { return TableRowSorter.this.tableModel.getValueAt(param1Int1, param1Int2); }
    
    public String getStringValueAt(int param1Int1, int param1Int2) {
      TableStringConverter tableStringConverter = TableRowSorter.this.getStringConverter();
      if (tableStringConverter != null) {
        String str1 = tableStringConverter.toString(TableRowSorter.this.tableModel, param1Int1, param1Int2);
        return (str1 != null) ? str1 : "";
      } 
      Object object = getValueAt(param1Int1, param1Int2);
      if (object == null)
        return ""; 
      String str = object.toString();
      return (str == null) ? "" : str;
    }
    
    public Integer getIdentifier(int param1Int) { return Integer.valueOf(param1Int); }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\table\TableRowSorter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */