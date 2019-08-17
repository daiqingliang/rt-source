package javax.sql.rowset;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
import java.sql.ResultSet;
import java.sql.SQLException;

public interface WebRowSet extends CachedRowSet {
  public static final String PUBLIC_XML_SCHEMA = "--//Oracle Corporation//XSD Schema//EN";
  
  public static final String SCHEMA_SYSTEM_ID = "http://java.sun.com/xml/ns/jdbc/webrowset.xsd";
  
  void readXml(Reader paramReader) throws SQLException;
  
  void readXml(InputStream paramInputStream) throws SQLException, IOException;
  
  void writeXml(ResultSet paramResultSet, Writer paramWriter) throws SQLException;
  
  void writeXml(ResultSet paramResultSet, OutputStream paramOutputStream) throws SQLException, IOException;
  
  void writeXml(Writer paramWriter) throws SQLException;
  
  void writeXml(OutputStream paramOutputStream) throws SQLException, IOException;
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\sql\rowset\WebRowSet.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */