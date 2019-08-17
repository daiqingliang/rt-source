package javax.sql;

import java.sql.ResultSetMetaData;
import java.sql.SQLException;

public interface RowSetMetaData extends ResultSetMetaData {
  void setColumnCount(int paramInt) throws SQLException;
  
  void setAutoIncrement(int paramInt, boolean paramBoolean) throws SQLException;
  
  void setCaseSensitive(int paramInt, boolean paramBoolean) throws SQLException;
  
  void setSearchable(int paramInt, boolean paramBoolean) throws SQLException;
  
  void setCurrency(int paramInt, boolean paramBoolean) throws SQLException;
  
  void setNullable(int paramInt1, int paramInt2) throws SQLException;
  
  void setSigned(int paramInt, boolean paramBoolean) throws SQLException;
  
  void setColumnDisplaySize(int paramInt1, int paramInt2) throws SQLException;
  
  void setColumnLabel(int paramInt, String paramString) throws SQLException;
  
  void setColumnName(int paramInt, String paramString) throws SQLException;
  
  void setSchemaName(int paramInt, String paramString) throws SQLException;
  
  void setPrecision(int paramInt1, int paramInt2) throws SQLException;
  
  void setScale(int paramInt1, int paramInt2) throws SQLException;
  
  void setTableName(int paramInt, String paramString) throws SQLException;
  
  void setCatalogName(int paramInt, String paramString) throws SQLException;
  
  void setColumnType(int paramInt1, int paramInt2) throws SQLException;
  
  void setColumnTypeName(int paramInt, String paramString) throws SQLException;
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\sql\RowSetMetaData.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */