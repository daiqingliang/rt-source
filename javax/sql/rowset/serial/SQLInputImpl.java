package javax.sql.rowset.serial;

import java.io.InputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.Array;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Date;
import java.sql.NClob;
import java.sql.Ref;
import java.sql.RowId;
import java.sql.SQLData;
import java.sql.SQLException;
import java.sql.SQLInput;
import java.sql.SQLXML;
import java.sql.Struct;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Arrays;
import java.util.Map;
import sun.reflect.misc.ReflectUtil;

public class SQLInputImpl implements SQLInput {
  private boolean lastValueWasNull;
  
  private int idx;
  
  private Object[] attrib;
  
  private Map<String, Class<?>> map;
  
  public SQLInputImpl(Object[] paramArrayOfObject, Map<String, Class<?>> paramMap) throws SQLException {
    if (paramArrayOfObject == null || paramMap == null)
      throw new SQLException("Cannot instantiate a SQLInputImpl object with null parameters"); 
    this.attrib = Arrays.copyOf(paramArrayOfObject, paramArrayOfObject.length);
    this.idx = -1;
    this.map = paramMap;
  }
  
  private Object getNextAttribute() throws SQLException {
    if (++this.idx >= this.attrib.length)
      throw new SQLException("SQLInputImpl exception: Invalid read position"); 
    this.lastValueWasNull = (this.attrib[this.idx] == null);
    return this.attrib[this.idx];
  }
  
  public String readString() throws SQLException { return (String)getNextAttribute(); }
  
  public boolean readBoolean() throws SQLException {
    Boolean bool = (Boolean)getNextAttribute();
    return (bool == null) ? false : bool.booleanValue();
  }
  
  public byte readByte() throws SQLException {
    Byte byte = (Byte)getNextAttribute();
    return (byte == null) ? 0 : byte.byteValue();
  }
  
  public short readShort() throws SQLException {
    Short short = (Short)getNextAttribute();
    return (short == null) ? 0 : short.shortValue();
  }
  
  public int readInt() throws SQLException {
    Integer integer = (Integer)getNextAttribute();
    return (integer == null) ? 0 : integer.intValue();
  }
  
  public long readLong() throws SQLException {
    Long long = (Long)getNextAttribute();
    return (long == null) ? 0L : long.longValue();
  }
  
  public float readFloat() throws SQLException {
    Float float = (Float)getNextAttribute();
    return (float == null) ? 0.0F : float.floatValue();
  }
  
  public double readDouble() throws SQLException {
    Double double = (Double)getNextAttribute();
    return (double == null) ? 0.0D : double.doubleValue();
  }
  
  public BigDecimal readBigDecimal() throws SQLException { return (BigDecimal)getNextAttribute(); }
  
  public byte[] readBytes() throws SQLException { return (byte[])getNextAttribute(); }
  
  public Date readDate() throws SQLException { return (Date)getNextAttribute(); }
  
  public Time readTime() throws SQLException { return (Time)getNextAttribute(); }
  
  public Timestamp readTimestamp() throws SQLException { return (Timestamp)getNextAttribute(); }
  
  public Reader readCharacterStream() throws SQLException { return (Reader)getNextAttribute(); }
  
  public InputStream readAsciiStream() throws SQLException { return (InputStream)getNextAttribute(); }
  
  public InputStream readBinaryStream() throws SQLException { return (InputStream)getNextAttribute(); }
  
  public Object readObject() throws SQLException {
    Object object = getNextAttribute();
    if (object instanceof Struct) {
      Struct struct = (Struct)object;
      Class clazz = (Class)this.map.get(struct.getSQLTypeName());
      if (clazz != null) {
        SQLData sQLData = null;
        try {
          sQLData = (SQLData)ReflectUtil.newInstance(clazz);
        } catch (Exception exception) {
          throw new SQLException("Unable to Instantiate: ", exception);
        } 
        Object[] arrayOfObject = struct.getAttributes(this.map);
        SQLInputImpl sQLInputImpl = new SQLInputImpl(arrayOfObject, this.map);
        sQLData.readSQL(sQLInputImpl, struct.getSQLTypeName());
        return sQLData;
      } 
    } 
    return object;
  }
  
  public Ref readRef() throws SQLException { return (Ref)getNextAttribute(); }
  
  public Blob readBlob() throws SQLException { return (Blob)getNextAttribute(); }
  
  public Clob readClob() throws SQLException { return (Clob)getNextAttribute(); }
  
  public Array readArray() throws SQLException { return (Array)getNextAttribute(); }
  
  public boolean wasNull() throws SQLException { return this.lastValueWasNull; }
  
  public URL readURL() throws SQLException { return (URL)getNextAttribute(); }
  
  public NClob readNClob() throws SQLException { return (NClob)getNextAttribute(); }
  
  public String readNString() throws SQLException { return (String)getNextAttribute(); }
  
  public SQLXML readSQLXML() throws SQLException { return (SQLXML)getNextAttribute(); }
  
  public RowId readRowId() throws SQLException { return (RowId)getNextAttribute(); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\sql\rowset\serial\SQLInputImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */