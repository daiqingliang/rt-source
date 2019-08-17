package com.sun.rowset;

import com.sun.rowset.internal.WebRowSetXmlReader;
import com.sun.rowset.internal.WebRowSetXmlWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Hashtable;
import javax.sql.rowset.WebRowSet;
import javax.sql.rowset.spi.SyncFactory;
import javax.sql.rowset.spi.SyncProvider;

public class WebRowSetImpl extends CachedRowSetImpl implements WebRowSet {
  private WebRowSetXmlReader xmlReader;
  
  private WebRowSetXmlWriter xmlWriter;
  
  private int curPosBfrWrite;
  
  private SyncProvider provider;
  
  static final long serialVersionUID = -8771775154092422943L;
  
  public WebRowSetImpl() throws SQLException {
    this.xmlReader = new WebRowSetXmlReader();
    this.xmlWriter = new WebRowSetXmlWriter();
  }
  
  public WebRowSetImpl(Hashtable paramHashtable) throws SQLException {
    try {
      this.resBundle = JdbcRowSetResourceBundle.getJdbcRowSetResourceBundle();
    } catch (IOException iOException) {
      throw new RuntimeException(iOException);
    } 
    if (paramHashtable == null)
      throw new SQLException(this.resBundle.handleGetObject("webrowsetimpl.nullhash").toString()); 
    String str = (String)paramHashtable.get("rowset.provider.classname");
    this.provider = SyncFactory.getInstance(str);
  }
  
  public void writeXml(ResultSet paramResultSet, Writer paramWriter) throws SQLException {
    populate(paramResultSet);
    this.curPosBfrWrite = getRow();
    writeXml(paramWriter);
  }
  
  public void writeXml(Writer paramWriter) throws SQLException {
    if (this.xmlWriter != null) {
      this.curPosBfrWrite = getRow();
      this.xmlWriter.writeXML(this, paramWriter);
    } else {
      throw new SQLException(this.resBundle.handleGetObject("webrowsetimpl.invalidwr").toString());
    } 
  }
  
  public void readXml(Reader paramReader) throws SQLException {
    try {
      if (paramReader != null) {
        this.xmlReader.readXML(this, paramReader);
        if (this.curPosBfrWrite == 0) {
          beforeFirst();
        } else {
          absolute(this.curPosBfrWrite);
        } 
      } else {
        throw new SQLException(this.resBundle.handleGetObject("webrowsetimpl.invalidrd").toString());
      } 
    } catch (Exception exception) {
      throw new SQLException(exception.getMessage());
    } 
  }
  
  public void readXml(InputStream paramInputStream) throws SQLException, IOException {
    if (paramInputStream != null) {
      this.xmlReader.readXML(this, paramInputStream);
      if (this.curPosBfrWrite == 0) {
        beforeFirst();
      } else {
        absolute(this.curPosBfrWrite);
      } 
    } else {
      throw new SQLException(this.resBundle.handleGetObject("webrowsetimpl.invalidrd").toString());
    } 
  }
  
  public void writeXml(OutputStream paramOutputStream) throws SQLException, IOException {
    if (this.xmlWriter != null) {
      this.curPosBfrWrite = getRow();
      this.xmlWriter.writeXML(this, paramOutputStream);
    } else {
      throw new SQLException(this.resBundle.handleGetObject("webrowsetimpl.invalidwr").toString());
    } 
  }
  
  public void writeXml(ResultSet paramResultSet, OutputStream paramOutputStream) throws SQLException, IOException {
    populate(paramResultSet);
    this.curPosBfrWrite = getRow();
    writeXml(paramOutputStream);
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


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\rowset\WebRowSetImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */