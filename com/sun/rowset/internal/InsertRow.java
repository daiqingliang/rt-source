package com.sun.rowset.internal;

import com.sun.rowset.JdbcRowSetResourceBundle;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.sql.SQLException;
import java.util.BitSet;
import javax.sql.RowSetMetaData;

public class InsertRow extends BaseRow implements Serializable, Cloneable {
  private BitSet colsInserted;
  
  private int cols;
  
  private JdbcRowSetResourceBundle resBundle;
  
  static final long serialVersionUID = 1066099658102869344L;
  
  public InsertRow(int paramInt) {
    this.origVals = new Object[paramInt];
    this.colsInserted = new BitSet(paramInt);
    this.cols = paramInt;
    try {
      this.resBundle = JdbcRowSetResourceBundle.getJdbcRowSetResourceBundle();
    } catch (IOException iOException) {
      throw new RuntimeException(iOException);
    } 
  }
  
  protected void markColInserted(int paramInt) { this.colsInserted.set(paramInt); }
  
  public boolean isCompleteRow(RowSetMetaData paramRowSetMetaData) throws SQLException {
    for (byte b = 0; b < this.cols; b++) {
      if (!this.colsInserted.get(b) && paramRowSetMetaData.isNullable(b + 1) == 0)
        return false; 
    } 
    return true;
  }
  
  public void initInsertRow() {
    for (byte b = 0; b < this.cols; b++)
      this.colsInserted.clear(b); 
  }
  
  public Object getColumnObject(int paramInt) throws SQLException {
    if (!this.colsInserted.get(paramInt - 1))
      throw new SQLException(this.resBundle.handleGetObject("insertrow.novalue").toString()); 
    return this.origVals[paramInt - 1];
  }
  
  public void setColumnObject(int paramInt, Object paramObject) {
    this.origVals[paramInt - 1] = paramObject;
    markColInserted(paramInt - 1);
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream) throws IOException, ClassNotFoundException {
    paramObjectInputStream.defaultReadObject();
    try {
      this.resBundle = JdbcRowSetResourceBundle.getJdbcRowSetResourceBundle();
    } catch (IOException iOException) {
      throw new RuntimeException(iOException);
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\rowset\internal\InsertRow.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */