package java.sql;

public interface SQLData {
  String getSQLTypeName() throws SQLException;
  
  void readSQL(SQLInput paramSQLInput, String paramString) throws SQLException;
  
  void writeSQL(SQLOutput paramSQLOutput) throws SQLException;
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\sql\SQLData.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */