package javax.sql.rowset;

import java.io.Serializable;
import java.lang.reflect.Field;
import java.sql.SQLException;
import javax.sql.RowSetMetaData;

public class RowSetMetaDataImpl implements RowSetMetaData, Serializable {
  private int colCount;
  
  private ColInfo[] colInfo;
  
  static final long serialVersionUID = 6893806403181801867L;
  
  private void checkColRange(int paramInt) throws SQLException {
    if (paramInt <= 0 || paramInt > this.colCount)
      throw new SQLException("Invalid column index :" + paramInt); 
  }
  
  private void checkColType(int paramInt) throws SQLException {
    try {
      Class clazz = java.sql.Types.class;
      Field[] arrayOfField = clazz.getFields();
      int i = 0;
      for (byte b = 0; b < arrayOfField.length; b++) {
        i = arrayOfField[b].getInt(clazz);
        if (i == paramInt)
          return; 
      } 
    } catch (Exception exception) {
      throw new SQLException(exception.getMessage());
    } 
    throw new SQLException("Invalid SQL type for column");
  }
  
  public void setColumnCount(int paramInt) throws SQLException {
    if (paramInt <= 0)
      throw new SQLException("Invalid column count. Cannot be less or equal to zero"); 
    this.colCount = paramInt;
    if (this.colCount != Integer.MAX_VALUE) {
      this.colInfo = new ColInfo[this.colCount + 1];
      for (byte b = 1; b <= this.colCount; b++)
        this.colInfo[b] = new ColInfo(null); 
    } 
  }
  
  public void setAutoIncrement(int paramInt, boolean paramBoolean) throws SQLException {
    checkColRange(paramInt);
    (this.colInfo[paramInt]).autoIncrement = paramBoolean;
  }
  
  public void setCaseSensitive(int paramInt, boolean paramBoolean) throws SQLException {
    checkColRange(paramInt);
    (this.colInfo[paramInt]).caseSensitive = paramBoolean;
  }
  
  public void setSearchable(int paramInt, boolean paramBoolean) throws SQLException {
    checkColRange(paramInt);
    (this.colInfo[paramInt]).searchable = paramBoolean;
  }
  
  public void setCurrency(int paramInt, boolean paramBoolean) throws SQLException {
    checkColRange(paramInt);
    (this.colInfo[paramInt]).currency = paramBoolean;
  }
  
  public void setNullable(int paramInt1, int paramInt2) throws SQLException {
    if (paramInt2 < 0 || paramInt2 > 2)
      throw new SQLException("Invalid nullable constant set. Must be either columnNoNulls, columnNullable or columnNullableUnknown"); 
    checkColRange(paramInt1);
    (this.colInfo[paramInt1]).nullable = paramInt2;
  }
  
  public void setSigned(int paramInt, boolean paramBoolean) throws SQLException {
    checkColRange(paramInt);
    (this.colInfo[paramInt]).signed = paramBoolean;
  }
  
  public void setColumnDisplaySize(int paramInt1, int paramInt2) throws SQLException {
    if (paramInt2 < 0)
      throw new SQLException("Invalid column display size. Cannot be less than zero"); 
    checkColRange(paramInt1);
    (this.colInfo[paramInt1]).columnDisplaySize = paramInt2;
  }
  
  public void setColumnLabel(int paramInt, String paramString) throws SQLException {
    checkColRange(paramInt);
    if (paramString != null) {
      (this.colInfo[paramInt]).columnLabel = paramString;
    } else {
      (this.colInfo[paramInt]).columnLabel = "";
    } 
  }
  
  public void setColumnName(int paramInt, String paramString) throws SQLException {
    checkColRange(paramInt);
    if (paramString != null) {
      (this.colInfo[paramInt]).columnName = paramString;
    } else {
      (this.colInfo[paramInt]).columnName = "";
    } 
  }
  
  public void setSchemaName(int paramInt, String paramString) throws SQLException {
    checkColRange(paramInt);
    if (paramString != null) {
      (this.colInfo[paramInt]).schemaName = paramString;
    } else {
      (this.colInfo[paramInt]).schemaName = "";
    } 
  }
  
  public void setPrecision(int paramInt1, int paramInt2) throws SQLException {
    if (paramInt2 < 0)
      throw new SQLException("Invalid precision value. Cannot be less than zero"); 
    checkColRange(paramInt1);
    (this.colInfo[paramInt1]).colPrecision = paramInt2;
  }
  
  public void setScale(int paramInt1, int paramInt2) throws SQLException {
    if (paramInt2 < 0)
      throw new SQLException("Invalid scale size. Cannot be less than zero"); 
    checkColRange(paramInt1);
    (this.colInfo[paramInt1]).colScale = paramInt2;
  }
  
  public void setTableName(int paramInt, String paramString) throws SQLException {
    checkColRange(paramInt);
    if (paramString != null) {
      (this.colInfo[paramInt]).tableName = paramString;
    } else {
      (this.colInfo[paramInt]).tableName = "";
    } 
  }
  
  public void setCatalogName(int paramInt, String paramString) throws SQLException {
    checkColRange(paramInt);
    if (paramString != null) {
      (this.colInfo[paramInt]).catName = paramString;
    } else {
      (this.colInfo[paramInt]).catName = "";
    } 
  }
  
  public void setColumnType(int paramInt1, int paramInt2) throws SQLException {
    checkColType(paramInt2);
    checkColRange(paramInt1);
    (this.colInfo[paramInt1]).colType = paramInt2;
  }
  
  public void setColumnTypeName(int paramInt, String paramString) throws SQLException {
    checkColRange(paramInt);
    if (paramString != null) {
      (this.colInfo[paramInt]).colTypeName = paramString;
    } else {
      (this.colInfo[paramInt]).colTypeName = "";
    } 
  }
  
  public int getColumnCount() throws SQLException { return this.colCount; }
  
  public boolean isAutoIncrement(int paramInt) throws SQLException {
    checkColRange(paramInt);
    return (this.colInfo[paramInt]).autoIncrement;
  }
  
  public boolean isCaseSensitive(int paramInt) throws SQLException {
    checkColRange(paramInt);
    return (this.colInfo[paramInt]).caseSensitive;
  }
  
  public boolean isSearchable(int paramInt) throws SQLException {
    checkColRange(paramInt);
    return (this.colInfo[paramInt]).searchable;
  }
  
  public boolean isCurrency(int paramInt) throws SQLException {
    checkColRange(paramInt);
    return (this.colInfo[paramInt]).currency;
  }
  
  public int isNullable(int paramInt) throws SQLException {
    checkColRange(paramInt);
    return (this.colInfo[paramInt]).nullable;
  }
  
  public boolean isSigned(int paramInt) throws SQLException {
    checkColRange(paramInt);
    return (this.colInfo[paramInt]).signed;
  }
  
  public int getColumnDisplaySize(int paramInt) throws SQLException {
    checkColRange(paramInt);
    return (this.colInfo[paramInt]).columnDisplaySize;
  }
  
  public String getColumnLabel(int paramInt) throws SQLException {
    checkColRange(paramInt);
    return (this.colInfo[paramInt]).columnLabel;
  }
  
  public String getColumnName(int paramInt) throws SQLException {
    checkColRange(paramInt);
    return (this.colInfo[paramInt]).columnName;
  }
  
  public String getSchemaName(int paramInt) throws SQLException {
    checkColRange(paramInt);
    String str = "";
    if ((this.colInfo[paramInt]).schemaName != null)
      str = (this.colInfo[paramInt]).schemaName; 
    return str;
  }
  
  public int getPrecision(int paramInt) throws SQLException {
    checkColRange(paramInt);
    return (this.colInfo[paramInt]).colPrecision;
  }
  
  public int getScale(int paramInt) throws SQLException {
    checkColRange(paramInt);
    return (this.colInfo[paramInt]).colScale;
  }
  
  public String getTableName(int paramInt) throws SQLException {
    checkColRange(paramInt);
    return (this.colInfo[paramInt]).tableName;
  }
  
  public String getCatalogName(int paramInt) throws SQLException {
    checkColRange(paramInt);
    String str = "";
    if ((this.colInfo[paramInt]).catName != null)
      str = (this.colInfo[paramInt]).catName; 
    return str;
  }
  
  public int getColumnType(int paramInt) throws SQLException {
    checkColRange(paramInt);
    return (this.colInfo[paramInt]).colType;
  }
  
  public String getColumnTypeName(int paramInt) throws SQLException {
    checkColRange(paramInt);
    return (this.colInfo[paramInt]).colTypeName;
  }
  
  public boolean isReadOnly(int paramInt) throws SQLException {
    checkColRange(paramInt);
    return (this.colInfo[paramInt]).readOnly;
  }
  
  public boolean isWritable(int paramInt) throws SQLException {
    checkColRange(paramInt);
    return (this.colInfo[paramInt]).writable;
  }
  
  public boolean isDefinitelyWritable(int paramInt) throws SQLException {
    checkColRange(paramInt);
    return true;
  }
  
  public String getColumnClassName(int paramInt) throws SQLException {
    String str = String.class.getName();
    int i = getColumnType(paramInt);
    switch (i) {
      case 2:
      case 3:
        str = java.math.BigDecimal.class.getName();
        break;
      case -7:
        str = Boolean.class.getName();
        break;
      case -6:
        str = Byte.class.getName();
        break;
      case 5:
        str = Short.class.getName();
        break;
      case 4:
        str = Integer.class.getName();
        break;
      case -5:
        str = Long.class.getName();
        break;
      case 7:
        str = Float.class.getName();
        break;
      case 6:
      case 8:
        str = Double.class.getName();
        break;
      case -4:
      case -3:
      case -2:
        str = "byte[]";
        break;
      case 91:
        str = java.sql.Date.class.getName();
        break;
      case 92:
        str = java.sql.Time.class.getName();
        break;
      case 93:
        str = java.sql.Timestamp.class.getName();
        break;
      case 2004:
        str = java.sql.Blob.class.getName();
        break;
      case 2005:
        str = java.sql.Clob.class.getName();
        break;
    } 
    return str;
  }
  
  public <T> T unwrap(Class<T> paramClass) throws SQLException {
    if (isWrapperFor(paramClass))
      return (T)paramClass.cast(this); 
    throw new SQLException("unwrap failed for:" + paramClass);
  }
  
  public boolean isWrapperFor(Class<?> paramClass) throws SQLException { return paramClass.isInstance(this); }
  
  private class ColInfo implements Serializable {
    public boolean autoIncrement;
    
    public boolean caseSensitive;
    
    public boolean currency;
    
    public int nullable;
    
    public boolean signed;
    
    public boolean searchable;
    
    public int columnDisplaySize;
    
    public String columnLabel;
    
    public String columnName;
    
    public String schemaName;
    
    public int colPrecision;
    
    public int colScale;
    
    public String tableName = "";
    
    public String catName;
    
    public int colType;
    
    public String colTypeName;
    
    public boolean readOnly = false;
    
    public boolean writable = true;
    
    static final long serialVersionUID = 5490834817919311283L;
    
    private ColInfo() {}
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\sql\rowset\RowSetMetaDataImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */