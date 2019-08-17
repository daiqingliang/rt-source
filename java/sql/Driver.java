package java.sql;

import java.util.Properties;
import java.util.logging.Logger;

public interface Driver {
  Connection connect(String paramString, Properties paramProperties) throws SQLException;
  
  boolean acceptsURL(String paramString) throws SQLException;
  
  DriverPropertyInfo[] getPropertyInfo(String paramString, Properties paramProperties) throws SQLException;
  
  int getMajorVersion();
  
  int getMinorVersion();
  
  boolean jdbcCompliant();
  
  Logger getParentLogger() throws SQLFeatureNotSupportedException;
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\sql\Driver.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */