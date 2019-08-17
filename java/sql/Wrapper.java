package java.sql;

public interface Wrapper {
  <T> T unwrap(Class<T> paramClass) throws SQLException;
  
  boolean isWrapperFor(Class<?> paramClass) throws SQLException;
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\sql\Wrapper.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */