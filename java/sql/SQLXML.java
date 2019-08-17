package java.sql;

import java.io.InputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;

public interface SQLXML {
  void free() throws SQLException;
  
  InputStream getBinaryStream() throws SQLException;
  
  OutputStream setBinaryStream() throws SQLException;
  
  Reader getCharacterStream() throws SQLException;
  
  Writer setCharacterStream() throws SQLException;
  
  String getString() throws SQLException;
  
  void setString(String paramString) throws SQLException;
  
  <T extends javax.xml.transform.Source> T getSource(Class<T> paramClass) throws SQLException;
  
  <T extends javax.xml.transform.Result> T setResult(Class<T> paramClass) throws SQLException;
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\sql\SQLXML.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */