package java.sql;

public interface ResultSetMetaData extends Wrapper {
  public static final int columnNoNulls = 0;
  
  public static final int columnNullable = 1;
  
  public static final int columnNullableUnknown = 2;
  
  int getColumnCount() throws SQLException;
  
  boolean isAutoIncrement(int paramInt) throws SQLException;
  
  boolean isCaseSensitive(int paramInt) throws SQLException;
  
  boolean isSearchable(int paramInt) throws SQLException;
  
  boolean isCurrency(int paramInt) throws SQLException;
  
  int isNullable(int paramInt) throws SQLException;
  
  boolean isSigned(int paramInt) throws SQLException;
  
  int getColumnDisplaySize(int paramInt) throws SQLException;
  
  String getColumnLabel(int paramInt) throws SQLException;
  
  String getColumnName(int paramInt) throws SQLException;
  
  String getSchemaName(int paramInt) throws SQLException;
  
  int getPrecision(int paramInt) throws SQLException;
  
  int getScale(int paramInt) throws SQLException;
  
  String getTableName(int paramInt) throws SQLException;
  
  String getCatalogName(int paramInt) throws SQLException;
  
  int getColumnType(int paramInt) throws SQLException;
  
  String getColumnTypeName(int paramInt) throws SQLException;
  
  boolean isReadOnly(int paramInt) throws SQLException;
  
  boolean isWritable(int paramInt) throws SQLException;
  
  boolean isDefinitelyWritable(int paramInt) throws SQLException;
  
  String getColumnClassName(int paramInt) throws SQLException;
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\sql\ResultSetMetaData.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */