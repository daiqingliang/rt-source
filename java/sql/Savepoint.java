package java.sql;

public interface Savepoint {
  int getSavepointId() throws SQLException;
  
  String getSavepointName() throws SQLException;
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\sql\Savepoint.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */