package java.sql;

public interface RowId {
  boolean equals(Object paramObject);
  
  byte[] getBytes();
  
  String toString();
  
  int hashCode();
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\sql\RowId.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */