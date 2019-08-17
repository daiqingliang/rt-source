package java.sql;

public interface ParameterMetaData extends Wrapper {
  public static final int parameterNoNulls = 0;
  
  public static final int parameterNullable = 1;
  
  public static final int parameterNullableUnknown = 2;
  
  public static final int parameterModeUnknown = 0;
  
  public static final int parameterModeIn = 1;
  
  public static final int parameterModeInOut = 2;
  
  public static final int parameterModeOut = 4;
  
  int getParameterCount() throws SQLException;
  
  int isNullable(int paramInt) throws SQLException;
  
  boolean isSigned(int paramInt) throws SQLException;
  
  int getPrecision(int paramInt) throws SQLException;
  
  int getScale(int paramInt) throws SQLException;
  
  int getParameterType(int paramInt) throws SQLException;
  
  String getParameterTypeName(int paramInt) throws SQLException;
  
  String getParameterClassName(int paramInt) throws SQLException;
  
  int getParameterMode(int paramInt) throws SQLException;
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\sql\ParameterMetaData.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */