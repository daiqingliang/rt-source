package com.sun.rowset.providers;

import com.sun.rowset.JdbcRowSetResourceBundle;
import java.io.IOException;
import java.sql.SQLException;
import javax.sql.RowSetReader;
import javax.sql.RowSetWriter;
import javax.sql.rowset.spi.SyncProvider;
import javax.sql.rowset.spi.SyncProviderException;
import javax.sql.rowset.spi.XmlReader;
import javax.sql.rowset.spi.XmlWriter;

public final class RIXMLProvider extends SyncProvider {
  private String providerID = "com.sun.rowset.providers.RIXMLProvider";
  
  private String vendorName = "Oracle Corporation";
  
  private String versionNumber = "1.0";
  
  private JdbcRowSetResourceBundle resBundle;
  
  private XmlReader xmlReader;
  
  private XmlWriter xmlWriter;
  
  public RIXMLProvider() {
    this.providerID = getClass().getName();
    try {
      this.resBundle = JdbcRowSetResourceBundle.getJdbcRowSetResourceBundle();
    } catch (IOException iOException) {
      throw new RuntimeException(iOException);
    } 
  }
  
  public String getProviderID() { return this.providerID; }
  
  public void setXmlReader(XmlReader paramXmlReader) throws SQLException { this.xmlReader = paramXmlReader; }
  
  public void setXmlWriter(XmlWriter paramXmlWriter) throws SQLException { this.xmlWriter = paramXmlWriter; }
  
  public XmlReader getXmlReader() throws SQLException { return this.xmlReader; }
  
  public XmlWriter getXmlWriter() throws SQLException { return this.xmlWriter; }
  
  public int getProviderGrade() { return 1; }
  
  public int supportsUpdatableView() { return 6; }
  
  public int getDataSourceLock() { return 1; }
  
  public void setDataSourceLock(int paramInt) throws SyncProviderException { throw new UnsupportedOperationException(this.resBundle.handleGetObject("rixml.unsupp").toString()); }
  
  public RowSetWriter getRowSetWriter() { return null; }
  
  public RowSetReader getRowSetReader() { return null; }
  
  public String getVersion() { return this.versionNumber; }
  
  public String getVendor() { return this.vendorName; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\rowset\providers\RIXMLProvider.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */