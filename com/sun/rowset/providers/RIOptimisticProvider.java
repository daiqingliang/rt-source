package com.sun.rowset.providers;

import com.sun.rowset.JdbcRowSetResourceBundle;
import com.sun.rowset.internal.CachedRowSetReader;
import com.sun.rowset.internal.CachedRowSetWriter;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.sql.SQLException;
import javax.sql.RowSetReader;
import javax.sql.RowSetWriter;
import javax.sql.rowset.spi.SyncProvider;
import javax.sql.rowset.spi.SyncProviderException;

public final class RIOptimisticProvider extends SyncProvider implements Serializable {
  private CachedRowSetReader reader;
  
  private CachedRowSetWriter writer;
  
  private String providerID = "com.sun.rowset.providers.RIOptimisticProvider";
  
  private String vendorName = "Oracle Corporation";
  
  private String versionNumber = "1.0";
  
  private JdbcRowSetResourceBundle resBundle;
  
  static final long serialVersionUID = -3143367176751761936L;
  
  public RIOptimisticProvider() {
    this.providerID = getClass().getName();
    this.reader = new CachedRowSetReader();
    this.writer = new CachedRowSetWriter();
    try {
      this.resBundle = JdbcRowSetResourceBundle.getJdbcRowSetResourceBundle();
    } catch (IOException iOException) {
      throw new RuntimeException(iOException);
    } 
  }
  
  public String getProviderID() { return this.providerID; }
  
  public RowSetWriter getRowSetWriter() {
    try {
      this.writer.setReader(this.reader);
    } catch (SQLException sQLException) {}
    return this.writer;
  }
  
  public RowSetReader getRowSetReader() { return this.reader; }
  
  public int getProviderGrade() { return 2; }
  
  public void setDataSourceLock(int paramInt) throws SyncProviderException {
    if (paramInt != 1)
      throw new SyncProviderException(this.resBundle.handleGetObject("riop.locking").toString()); 
  }
  
  public int getDataSourceLock() { return 1; }
  
  public int supportsUpdatableView() { return 6; }
  
  public String getVersion() { return this.versionNumber; }
  
  public String getVendor() { return this.vendorName; }
  
  private void readObject(ObjectInputStream paramObjectInputStream) throws IOException, ClassNotFoundException {
    paramObjectInputStream.defaultReadObject();
    try {
      this.resBundle = JdbcRowSetResourceBundle.getJdbcRowSetResourceBundle();
    } catch (IOException iOException) {
      throw new RuntimeException(iOException);
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\rowset\providers\RIOptimisticProvider.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */