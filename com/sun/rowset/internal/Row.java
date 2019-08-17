package com.sun.rowset.internal;

import java.io.Serializable;
import java.sql.SQLException;
import java.util.BitSet;

public class Row extends BaseRow implements Serializable, Cloneable {
  static final long serialVersionUID = 5047859032611314762L;
  
  private Object[] currentVals;
  
  private BitSet colsChanged;
  
  private boolean deleted;
  
  private boolean updated;
  
  private boolean inserted;
  
  private int numCols;
  
  public Row(int paramInt) {
    this.origVals = new Object[paramInt];
    this.currentVals = new Object[paramInt];
    this.colsChanged = new BitSet(paramInt);
    this.numCols = paramInt;
  }
  
  public Row(int paramInt, Object[] paramArrayOfObject) {
    this.origVals = new Object[paramInt];
    System.arraycopy(paramArrayOfObject, 0, this.origVals, 0, paramInt);
    this.currentVals = new Object[paramInt];
    this.colsChanged = new BitSet(paramInt);
    this.numCols = paramInt;
  }
  
  public void initColumnObject(int paramInt, Object paramObject) { this.origVals[paramInt - 1] = paramObject; }
  
  public void setColumnObject(int paramInt, Object paramObject) {
    this.currentVals[paramInt - 1] = paramObject;
    setColUpdated(paramInt - 1);
  }
  
  public Object getColumnObject(int paramInt) throws SQLException { return getColUpdated(paramInt - 1) ? this.currentVals[paramInt - 1] : this.origVals[paramInt - 1]; }
  
  public boolean getColUpdated(int paramInt) { return this.colsChanged.get(paramInt); }
  
  public void setDeleted() { this.deleted = true; }
  
  public boolean getDeleted() { return this.deleted; }
  
  public void clearDeleted() { this.deleted = false; }
  
  public void setInserted() { this.inserted = true; }
  
  public boolean getInserted() { return this.inserted; }
  
  public void clearInserted() { this.inserted = false; }
  
  public boolean getUpdated() { return this.updated; }
  
  public void setUpdated() {
    for (byte b = 0; b < this.numCols; b++) {
      if (getColUpdated(b) == true) {
        this.updated = true;
        return;
      } 
    } 
  }
  
  private void setColUpdated(int paramInt) { this.colsChanged.set(paramInt); }
  
  public void clearUpdated() {
    this.updated = false;
    for (byte b = 0; b < this.numCols; b++) {
      this.currentVals[b] = null;
      this.colsChanged.clear(b);
    } 
  }
  
  public void moveCurrentToOrig() {
    for (byte b = 0; b < this.numCols; b++) {
      if (getColUpdated(b) == true) {
        this.origVals[b] = this.currentVals[b];
        this.currentVals[b] = null;
        this.colsChanged.clear(b);
      } 
    } 
    this.updated = false;
  }
  
  public BaseRow getCurrentRow() { return null; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\rowset\internal\Row.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */