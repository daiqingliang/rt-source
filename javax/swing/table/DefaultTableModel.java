package javax.swing.table;

import java.io.Serializable;
import java.util.Vector;
import javax.swing.event.TableModelEvent;

public class DefaultTableModel extends AbstractTableModel implements Serializable {
  protected Vector dataVector;
  
  protected Vector columnIdentifiers;
  
  public DefaultTableModel() { this(0, 0); }
  
  private static Vector newVector(int paramInt) {
    Vector vector = new Vector(paramInt);
    vector.setSize(paramInt);
    return vector;
  }
  
  public DefaultTableModel(int paramInt1, int paramInt2) { this(newVector(paramInt2), paramInt1); }
  
  public DefaultTableModel(Vector paramVector, int paramInt) { setDataVector(newVector(paramInt), paramVector); }
  
  public DefaultTableModel(Object[] paramArrayOfObject, int paramInt) { this(convertToVector(paramArrayOfObject), paramInt); }
  
  public DefaultTableModel(Vector paramVector1, Vector paramVector2) { setDataVector(paramVector1, paramVector2); }
  
  public DefaultTableModel(Object[][] paramArrayOfObject, Object[] paramArrayOfObject1) { setDataVector(paramArrayOfObject, paramArrayOfObject1); }
  
  public Vector getDataVector() { return this.dataVector; }
  
  private static Vector nonNullVector(Vector paramVector) { return (paramVector != null) ? paramVector : new Vector(); }
  
  public void setDataVector(Vector paramVector1, Vector paramVector2) {
    this.dataVector = nonNullVector(paramVector1);
    this.columnIdentifiers = nonNullVector(paramVector2);
    justifyRows(0, getRowCount());
    fireTableStructureChanged();
  }
  
  public void setDataVector(Object[][] paramArrayOfObject, Object[] paramArrayOfObject1) { setDataVector(convertToVector(paramArrayOfObject), convertToVector(paramArrayOfObject1)); }
  
  public void newDataAvailable(TableModelEvent paramTableModelEvent) { fireTableChanged(paramTableModelEvent); }
  
  private void justifyRows(int paramInt1, int paramInt2) {
    this.dataVector.setSize(getRowCount());
    for (int i = paramInt1; i < paramInt2; i++) {
      if (this.dataVector.elementAt(i) == null)
        this.dataVector.setElementAt(new Vector(), i); 
      ((Vector)this.dataVector.elementAt(i)).setSize(getColumnCount());
    } 
  }
  
  public void newRowsAdded(TableModelEvent paramTableModelEvent) {
    justifyRows(paramTableModelEvent.getFirstRow(), paramTableModelEvent.getLastRow() + 1);
    fireTableChanged(paramTableModelEvent);
  }
  
  public void rowsRemoved(TableModelEvent paramTableModelEvent) { fireTableChanged(paramTableModelEvent); }
  
  public void setNumRows(int paramInt) {
    int i = getRowCount();
    if (i == paramInt)
      return; 
    this.dataVector.setSize(paramInt);
    if (paramInt <= i) {
      fireTableRowsDeleted(paramInt, i - 1);
    } else {
      justifyRows(i, paramInt);
      fireTableRowsInserted(i, paramInt - 1);
    } 
  }
  
  public void setRowCount(int paramInt) { setNumRows(paramInt); }
  
  public void addRow(Vector paramVector) { insertRow(getRowCount(), paramVector); }
  
  public void addRow(Object[] paramArrayOfObject) { addRow(convertToVector(paramArrayOfObject)); }
  
  public void insertRow(int paramInt, Vector paramVector) {
    this.dataVector.insertElementAt(paramVector, paramInt);
    justifyRows(paramInt, paramInt + 1);
    fireTableRowsInserted(paramInt, paramInt);
  }
  
  public void insertRow(int paramInt, Object[] paramArrayOfObject) { insertRow(paramInt, convertToVector(paramArrayOfObject)); }
  
  private static int gcd(int paramInt1, int paramInt2) { return (paramInt2 == 0) ? paramInt1 : gcd(paramInt2, paramInt1 % paramInt2); }
  
  private static void rotate(Vector paramVector, int paramInt1, int paramInt2, int paramInt3) {
    int i = paramInt2 - paramInt1;
    int j = i - paramInt3;
    int k = gcd(i, j);
    for (byte b = 0; b < k; b++) {
      int m = b;
      Object object = paramVector.elementAt(paramInt1 + m);
      int n;
      for (n = (m + j) % i; n != b; n = (m + j) % i) {
        paramVector.setElementAt(paramVector.elementAt(paramInt1 + n), paramInt1 + m);
        m = n;
      } 
      paramVector.setElementAt(object, paramInt1 + m);
    } 
  }
  
  public void moveRow(int paramInt1, int paramInt2, int paramInt3) {
    int k;
    int j;
    int i = paramInt3 - paramInt1;
    if (i < 0) {
      j = paramInt3;
      k = paramInt2;
    } else {
      j = paramInt1;
      k = paramInt3 + paramInt2 - paramInt1;
    } 
    rotate(this.dataVector, j, k + 1, i);
    fireTableRowsUpdated(j, k);
  }
  
  public void removeRow(int paramInt) {
    this.dataVector.removeElementAt(paramInt);
    fireTableRowsDeleted(paramInt, paramInt);
  }
  
  public void setColumnIdentifiers(Vector paramVector) { setDataVector(this.dataVector, paramVector); }
  
  public void setColumnIdentifiers(Object[] paramArrayOfObject) { setColumnIdentifiers(convertToVector(paramArrayOfObject)); }
  
  public void setColumnCount(int paramInt) {
    this.columnIdentifiers.setSize(paramInt);
    justifyRows(0, getRowCount());
    fireTableStructureChanged();
  }
  
  public void addColumn(Object paramObject) { addColumn(paramObject, (Vector)null); }
  
  public void addColumn(Object paramObject, Vector paramVector) {
    this.columnIdentifiers.addElement(paramObject);
    if (paramVector != null) {
      int i = paramVector.size();
      if (i > getRowCount())
        this.dataVector.setSize(i); 
      justifyRows(0, getRowCount());
      int j = getColumnCount() - 1;
      for (byte b = 0; b < i; b++) {
        Vector vector = (Vector)this.dataVector.elementAt(b);
        vector.setElementAt(paramVector.elementAt(b), j);
      } 
    } else {
      justifyRows(0, getRowCount());
    } 
    fireTableStructureChanged();
  }
  
  public void addColumn(Object paramObject, Object[] paramArrayOfObject) { addColumn(paramObject, convertToVector(paramArrayOfObject)); }
  
  public int getRowCount() { return this.dataVector.size(); }
  
  public int getColumnCount() { return this.columnIdentifiers.size(); }
  
  public String getColumnName(int paramInt) {
    Object object = null;
    if (paramInt < this.columnIdentifiers.size() && paramInt >= 0)
      object = this.columnIdentifiers.elementAt(paramInt); 
    return (object == null) ? super.getColumnName(paramInt) : object.toString();
  }
  
  public boolean isCellEditable(int paramInt1, int paramInt2) { return true; }
  
  public Object getValueAt(int paramInt1, int paramInt2) {
    Vector vector = (Vector)this.dataVector.elementAt(paramInt1);
    return vector.elementAt(paramInt2);
  }
  
  public void setValueAt(Object paramObject, int paramInt1, int paramInt2) {
    Vector vector = (Vector)this.dataVector.elementAt(paramInt1);
    vector.setElementAt(paramObject, paramInt2);
    fireTableCellUpdated(paramInt1, paramInt2);
  }
  
  protected static Vector convertToVector(Object[] paramArrayOfObject) {
    if (paramArrayOfObject == null)
      return null; 
    Vector vector = new Vector(paramArrayOfObject.length);
    for (Object object : paramArrayOfObject)
      vector.addElement(object); 
    return vector;
  }
  
  protected static Vector convertToVector(Object[][] paramArrayOfObject) {
    if (paramArrayOfObject == null)
      return null; 
    Vector vector = new Vector(paramArrayOfObject.length);
    for (Object[] arrayOfObject : paramArrayOfObject)
      vector.addElement(convertToVector(arrayOfObject)); 
    return vector;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\table\DefaultTableModel.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */