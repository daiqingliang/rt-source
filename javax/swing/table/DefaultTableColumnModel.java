package javax.swing.table;

import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.Serializable;
import java.util.Enumeration;
import java.util.Vector;
import javax.swing.DefaultListSelectionModel;
import javax.swing.ListSelectionModel;
import javax.swing.event.ChangeEvent;
import javax.swing.event.EventListenerList;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;
import javax.swing.event.TableColumnModelEvent;
import javax.swing.event.TableColumnModelListener;

public class DefaultTableColumnModel implements TableColumnModel, PropertyChangeListener, ListSelectionListener, Serializable {
  protected Vector<TableColumn> tableColumns = new Vector();
  
  protected ListSelectionModel selectionModel;
  
  protected int columnMargin;
  
  protected EventListenerList listenerList = new EventListenerList();
  
  protected ChangeEvent changeEvent = null;
  
  protected boolean columnSelectionAllowed;
  
  protected int totalColumnWidth;
  
  public DefaultTableColumnModel() {
    setSelectionModel(createSelectionModel());
    setColumnMargin(1);
    invalidateWidthCache();
    setColumnSelectionAllowed(false);
  }
  
  public void addColumn(TableColumn paramTableColumn) {
    if (paramTableColumn == null)
      throw new IllegalArgumentException("Object is null"); 
    this.tableColumns.addElement(paramTableColumn);
    paramTableColumn.addPropertyChangeListener(this);
    invalidateWidthCache();
    fireColumnAdded(new TableColumnModelEvent(this, 0, getColumnCount() - 1));
  }
  
  public void removeColumn(TableColumn paramTableColumn) {
    int i = this.tableColumns.indexOf(paramTableColumn);
    if (i != -1) {
      if (this.selectionModel != null)
        this.selectionModel.removeIndexInterval(i, i); 
      paramTableColumn.removePropertyChangeListener(this);
      this.tableColumns.removeElementAt(i);
      invalidateWidthCache();
      fireColumnRemoved(new TableColumnModelEvent(this, i, 0));
    } 
  }
  
  public void moveColumn(int paramInt1, int paramInt2) {
    if (paramInt1 < 0 || paramInt1 >= getColumnCount() || paramInt2 < 0 || paramInt2 >= getColumnCount())
      throw new IllegalArgumentException("moveColumn() - Index out of range"); 
    if (paramInt1 == paramInt2) {
      fireColumnMoved(new TableColumnModelEvent(this, paramInt1, paramInt2));
      return;
    } 
    TableColumn tableColumn = (TableColumn)this.tableColumns.elementAt(paramInt1);
    this.tableColumns.removeElementAt(paramInt1);
    boolean bool = this.selectionModel.isSelectedIndex(paramInt1);
    this.selectionModel.removeIndexInterval(paramInt1, paramInt1);
    this.tableColumns.insertElementAt(tableColumn, paramInt2);
    this.selectionModel.insertIndexInterval(paramInt2, 1, true);
    if (bool) {
      this.selectionModel.addSelectionInterval(paramInt2, paramInt2);
    } else {
      this.selectionModel.removeSelectionInterval(paramInt2, paramInt2);
    } 
    fireColumnMoved(new TableColumnModelEvent(this, paramInt1, paramInt2));
  }
  
  public void setColumnMargin(int paramInt) {
    if (paramInt != this.columnMargin) {
      this.columnMargin = paramInt;
      fireColumnMarginChanged();
    } 
  }
  
  public int getColumnCount() { return this.tableColumns.size(); }
  
  public Enumeration<TableColumn> getColumns() { return this.tableColumns.elements(); }
  
  public int getColumnIndex(Object paramObject) {
    if (paramObject == null)
      throw new IllegalArgumentException("Identifier is null"); 
    Enumeration enumeration = getColumns();
    for (byte b = 0; enumeration.hasMoreElements(); b++) {
      TableColumn tableColumn = (TableColumn)enumeration.nextElement();
      if (paramObject.equals(tableColumn.getIdentifier()))
        return b; 
    } 
    throw new IllegalArgumentException("Identifier not found");
  }
  
  public TableColumn getColumn(int paramInt) { return (TableColumn)this.tableColumns.elementAt(paramInt); }
  
  public int getColumnMargin() { return this.columnMargin; }
  
  public int getColumnIndexAtX(int paramInt) {
    if (paramInt < 0)
      return -1; 
    int i = getColumnCount();
    for (byte b = 0; b < i; b++) {
      paramInt -= getColumn(b).getWidth();
      if (paramInt < 0)
        return b; 
    } 
    return -1;
  }
  
  public int getTotalColumnWidth() {
    if (this.totalColumnWidth == -1)
      recalcWidthCache(); 
    return this.totalColumnWidth;
  }
  
  public void setSelectionModel(ListSelectionModel paramListSelectionModel) {
    if (paramListSelectionModel == null)
      throw new IllegalArgumentException("Cannot set a null SelectionModel"); 
    ListSelectionModel listSelectionModel = this.selectionModel;
    if (paramListSelectionModel != listSelectionModel) {
      if (listSelectionModel != null)
        listSelectionModel.removeListSelectionListener(this); 
      this.selectionModel = paramListSelectionModel;
      paramListSelectionModel.addListSelectionListener(this);
    } 
  }
  
  public ListSelectionModel getSelectionModel() { return this.selectionModel; }
  
  public void setColumnSelectionAllowed(boolean paramBoolean) { this.columnSelectionAllowed = paramBoolean; }
  
  public boolean getColumnSelectionAllowed() { return this.columnSelectionAllowed; }
  
  public int[] getSelectedColumns() {
    if (this.selectionModel != null) {
      int i = this.selectionModel.getMinSelectionIndex();
      int j = this.selectionModel.getMaxSelectionIndex();
      if (i == -1 || j == -1)
        return new int[0]; 
      int[] arrayOfInt1 = new int[1 + j - i];
      byte b = 0;
      for (int k = i; k <= j; k++) {
        if (this.selectionModel.isSelectedIndex(k))
          arrayOfInt1[b++] = k; 
      } 
      int[] arrayOfInt2 = new int[b];
      System.arraycopy(arrayOfInt1, 0, arrayOfInt2, 0, b);
      return arrayOfInt2;
    } 
    return new int[0];
  }
  
  public int getSelectedColumnCount() {
    if (this.selectionModel != null) {
      int i = this.selectionModel.getMinSelectionIndex();
      int j = this.selectionModel.getMaxSelectionIndex();
      byte b = 0;
      for (int k = i; k <= j; k++) {
        if (this.selectionModel.isSelectedIndex(k))
          b++; 
      } 
      return b;
    } 
    return 0;
  }
  
  public void addColumnModelListener(TableColumnModelListener paramTableColumnModelListener) { this.listenerList.add(TableColumnModelListener.class, paramTableColumnModelListener); }
  
  public void removeColumnModelListener(TableColumnModelListener paramTableColumnModelListener) { this.listenerList.remove(TableColumnModelListener.class, paramTableColumnModelListener); }
  
  public TableColumnModelListener[] getColumnModelListeners() { return (TableColumnModelListener[])this.listenerList.getListeners(TableColumnModelListener.class); }
  
  protected void fireColumnAdded(TableColumnModelEvent paramTableColumnModelEvent) {
    Object[] arrayOfObject = this.listenerList.getListenerList();
    for (int i = arrayOfObject.length - 2; i >= 0; i -= 2) {
      if (arrayOfObject[i] == TableColumnModelListener.class)
        ((TableColumnModelListener)arrayOfObject[i + 1]).columnAdded(paramTableColumnModelEvent); 
    } 
  }
  
  protected void fireColumnRemoved(TableColumnModelEvent paramTableColumnModelEvent) {
    Object[] arrayOfObject = this.listenerList.getListenerList();
    for (int i = arrayOfObject.length - 2; i >= 0; i -= 2) {
      if (arrayOfObject[i] == TableColumnModelListener.class)
        ((TableColumnModelListener)arrayOfObject[i + 1]).columnRemoved(paramTableColumnModelEvent); 
    } 
  }
  
  protected void fireColumnMoved(TableColumnModelEvent paramTableColumnModelEvent) {
    Object[] arrayOfObject = this.listenerList.getListenerList();
    for (int i = arrayOfObject.length - 2; i >= 0; i -= 2) {
      if (arrayOfObject[i] == TableColumnModelListener.class)
        ((TableColumnModelListener)arrayOfObject[i + 1]).columnMoved(paramTableColumnModelEvent); 
    } 
  }
  
  protected void fireColumnSelectionChanged(ListSelectionEvent paramListSelectionEvent) {
    Object[] arrayOfObject = this.listenerList.getListenerList();
    for (int i = arrayOfObject.length - 2; i >= 0; i -= 2) {
      if (arrayOfObject[i] == TableColumnModelListener.class)
        ((TableColumnModelListener)arrayOfObject[i + 1]).columnSelectionChanged(paramListSelectionEvent); 
    } 
  }
  
  protected void fireColumnMarginChanged() {
    Object[] arrayOfObject = this.listenerList.getListenerList();
    for (int i = arrayOfObject.length - 2; i >= 0; i -= 2) {
      if (arrayOfObject[i] == TableColumnModelListener.class) {
        if (this.changeEvent == null)
          this.changeEvent = new ChangeEvent(this); 
        ((TableColumnModelListener)arrayOfObject[i + 1]).columnMarginChanged(this.changeEvent);
      } 
    } 
  }
  
  public <T extends java.util.EventListener> T[] getListeners(Class<T> paramClass) { return (T[])this.listenerList.getListeners(paramClass); }
  
  public void propertyChange(PropertyChangeEvent paramPropertyChangeEvent) {
    String str = paramPropertyChangeEvent.getPropertyName();
    if (str == "width" || str == "preferredWidth") {
      invalidateWidthCache();
      fireColumnMarginChanged();
    } 
  }
  
  public void valueChanged(ListSelectionEvent paramListSelectionEvent) { fireColumnSelectionChanged(paramListSelectionEvent); }
  
  protected ListSelectionModel createSelectionModel() { return new DefaultListSelectionModel(); }
  
  protected void recalcWidthCache() {
    Enumeration enumeration = getColumns();
    this.totalColumnWidth = 0;
    while (enumeration.hasMoreElements())
      this.totalColumnWidth += ((TableColumn)enumeration.nextElement()).getWidth(); 
  }
  
  private void invalidateWidthCache() { this.totalColumnWidth = -1; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\table\DefaultTableColumnModel.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */