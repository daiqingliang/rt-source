package java.sql;

import java.util.Map;

public interface Array {
  String getBaseTypeName() throws SQLException;
  
  int getBaseType() throws SQLException;
  
  Object getArray() throws SQLException;
  
  Object getArray(Map<String, Class<?>> paramMap) throws SQLException;
  
  Object getArray(long paramLong, int paramInt) throws SQLException;
  
  Object getArray(long paramLong, int paramInt, Map<String, Class<?>> paramMap) throws SQLException;
  
  ResultSet getResultSet() throws SQLException;
  
  ResultSet getResultSet(Map<String, Class<?>> paramMap) throws SQLException;
  
  ResultSet getResultSet(long paramLong, int paramInt) throws SQLException;
  
  ResultSet getResultSet(long paramLong, int paramInt, Map<String, Class<?>> paramMap) throws SQLException;
  
  void free() throws SQLException;
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\sql\Array.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */