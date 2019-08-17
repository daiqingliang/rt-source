package com.sun.rowset;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.Reader;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.Array;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.NClob;
import java.sql.ParameterMetaData;
import java.sql.PreparedStatement;
import java.sql.Ref;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.RowId;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.sql.SQLWarning;
import java.sql.SQLXML;
import java.sql.Savepoint;
import java.sql.Statement;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.Calendar;
import java.util.Map;
import java.util.Vector;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import javax.sql.RowSetMetaData;
import javax.sql.rowset.BaseRowSet;
import javax.sql.rowset.JdbcRowSet;
import javax.sql.rowset.Joinable;
import javax.sql.rowset.RowSetMetaDataImpl;
import javax.sql.rowset.RowSetWarning;

public class JdbcRowSetImpl extends BaseRowSet implements JdbcRowSet, Joinable {
  private Connection conn = null;
  
  private PreparedStatement ps = null;
  
  private ResultSet rs = null;
  
  private RowSetMetaDataImpl rowsMD;
  
  private ResultSetMetaData resMD;
  
  private Vector<Integer> iMatchColumns;
  
  private Vector<String> strMatchColumns;
  
  protected JdbcRowSetResourceBundle resBundle;
  
  static final long serialVersionUID = -3591946023893483003L;
  
  public JdbcRowSetImpl() {
    try {
      this.resBundle = JdbcRowSetResourceBundle.getJdbcRowSetResourceBundle();
    } catch (IOException iOException) {
      throw new RuntimeException(iOException);
    } 
    initParams();
    try {
      setShowDeleted(false);
    } catch (SQLException sQLException) {
      System.err.println(this.resBundle.handleGetObject("jdbcrowsetimpl.setshowdeleted").toString() + sQLException.getLocalizedMessage());
    } 
    try {
      setQueryTimeout(0);
    } catch (SQLException sQLException) {
      System.err.println(this.resBundle.handleGetObject("jdbcrowsetimpl.setquerytimeout").toString() + sQLException.getLocalizedMessage());
    } 
    try {
      setMaxRows(0);
    } catch (SQLException sQLException) {
      System.err.println(this.resBundle.handleGetObject("jdbcrowsetimpl.setmaxrows").toString() + sQLException.getLocalizedMessage());
    } 
    try {
      setMaxFieldSize(0);
    } catch (SQLException sQLException) {
      System.err.println(this.resBundle.handleGetObject("jdbcrowsetimpl.setmaxfieldsize").toString() + sQLException.getLocalizedMessage());
    } 
    try {
      setEscapeProcessing(true);
    } catch (SQLException sQLException) {
      System.err.println(this.resBundle.handleGetObject("jdbcrowsetimpl.setescapeprocessing").toString() + sQLException.getLocalizedMessage());
    } 
    try {
      setConcurrency(1008);
    } catch (SQLException sQLException) {
      System.err.println(this.resBundle.handleGetObject("jdbcrowsetimpl.setconcurrency").toString() + sQLException.getLocalizedMessage());
    } 
    setTypeMap(null);
    try {
      setType(1004);
    } catch (SQLException sQLException) {
      System.err.println(this.resBundle.handleGetObject("jdbcrowsetimpl.settype").toString() + sQLException.getLocalizedMessage());
    } 
    setReadOnly(true);
    try {
      setTransactionIsolation(2);
    } catch (SQLException sQLException) {
      System.err.println(this.resBundle.handleGetObject("jdbcrowsetimpl.settransactionisolation").toString() + sQLException.getLocalizedMessage());
    } 
    this.iMatchColumns = new Vector(10);
    byte b;
    for (b = 0; b < 10; b++)
      this.iMatchColumns.add(b, Integer.valueOf(-1)); 
    this.strMatchColumns = new Vector(10);
    for (b = 0; b < 10; b++)
      this.strMatchColumns.add(b, null); 
  }
  
  public JdbcRowSetImpl(Connection paramConnection) throws SQLException {
    try {
      this.resBundle = JdbcRowSetResourceBundle.getJdbcRowSetResourceBundle();
    } catch (IOException iOException) {
      throw new RuntimeException(iOException);
    } 
    initParams();
    setShowDeleted(false);
    setQueryTimeout(0);
    setMaxRows(0);
    setMaxFieldSize(0);
    setParams();
    setReadOnly(true);
    setTransactionIsolation(2);
    setEscapeProcessing(true);
    setTypeMap(null);
    this.iMatchColumns = new Vector(10);
    byte b;
    for (b = 0; b < 10; b++)
      this.iMatchColumns.add(b, Integer.valueOf(-1)); 
    this.strMatchColumns = new Vector(10);
    for (b = 0; b < 10; b++)
      this.strMatchColumns.add(b, null); 
  }
  
  public JdbcRowSetImpl(String paramString1, String paramString2, String paramString3) throws SQLException {
    try {
      this.resBundle = JdbcRowSetResourceBundle.getJdbcRowSetResourceBundle();
    } catch (IOException iOException) {
      throw new RuntimeException(iOException);
    } 
    initParams();
    setUsername(paramString2);
    setPassword(paramString3);
    setUrl(paramString1);
    setShowDeleted(false);
    setQueryTimeout(0);
    setMaxRows(0);
    setMaxFieldSize(0);
    setParams();
    setReadOnly(true);
    setTransactionIsolation(2);
    setEscapeProcessing(true);
    setTypeMap(null);
    this.iMatchColumns = new Vector(10);
    byte b;
    for (b = 0; b < 10; b++)
      this.iMatchColumns.add(b, Integer.valueOf(-1)); 
    this.strMatchColumns = new Vector(10);
    for (b = 0; b < 10; b++)
      this.strMatchColumns.add(b, null); 
  }
  
  public JdbcRowSetImpl(ResultSet paramResultSet) throws SQLException {
    try {
      this.resBundle = JdbcRowSetResourceBundle.getJdbcRowSetResourceBundle();
    } catch (IOException iOException) {
      throw new RuntimeException(iOException);
    } 
    initParams();
    setShowDeleted(false);
    setQueryTimeout(0);
    setMaxRows(0);
    setMaxFieldSize(0);
    setParams();
    setReadOnly(true);
    setTransactionIsolation(2);
    setEscapeProcessing(true);
    setTypeMap(null);
    this.resMD = this.rs.getMetaData();
    this.rowsMD = new RowSetMetaDataImpl();
    initMetaData(this.rowsMD, this.resMD);
    this.iMatchColumns = new Vector(10);
    byte b;
    for (b = 0; b < 10; b++)
      this.iMatchColumns.add(b, Integer.valueOf(-1)); 
    this.strMatchColumns = new Vector(10);
    for (b = 0; b < 10; b++)
      this.strMatchColumns.add(b, null); 
  }
  
  protected void initMetaData(RowSetMetaData paramRowSetMetaData, ResultSetMetaData paramResultSetMetaData) throws SQLException {
    int i = paramResultSetMetaData.getColumnCount();
    paramRowSetMetaData.setColumnCount(i);
    for (byte b = 1; b <= i; b++) {
      paramRowSetMetaData.setAutoIncrement(b, paramResultSetMetaData.isAutoIncrement(b));
      paramRowSetMetaData.setCaseSensitive(b, paramResultSetMetaData.isCaseSensitive(b));
      paramRowSetMetaData.setCurrency(b, paramResultSetMetaData.isCurrency(b));
      paramRowSetMetaData.setNullable(b, paramResultSetMetaData.isNullable(b));
      paramRowSetMetaData.setSigned(b, paramResultSetMetaData.isSigned(b));
      paramRowSetMetaData.setSearchable(b, paramResultSetMetaData.isSearchable(b));
      paramRowSetMetaData.setColumnDisplaySize(b, paramResultSetMetaData.getColumnDisplaySize(b));
      paramRowSetMetaData.setColumnLabel(b, paramResultSetMetaData.getColumnLabel(b));
      paramRowSetMetaData.setColumnName(b, paramResultSetMetaData.getColumnName(b));
      paramRowSetMetaData.setSchemaName(b, paramResultSetMetaData.getSchemaName(b));
      paramRowSetMetaData.setPrecision(b, paramResultSetMetaData.getPrecision(b));
      paramRowSetMetaData.setScale(b, paramResultSetMetaData.getScale(b));
      paramRowSetMetaData.setTableName(b, paramResultSetMetaData.getTableName(b));
      paramRowSetMetaData.setCatalogName(b, paramResultSetMetaData.getCatalogName(b));
      paramRowSetMetaData.setColumnType(b, paramResultSetMetaData.getColumnType(b));
      paramRowSetMetaData.setColumnTypeName(b, paramResultSetMetaData.getColumnTypeName(b));
    } 
  }
  
  protected void checkState() {
    if (this.conn == null && this.ps == null && this.rs == null)
      throw new SQLException(this.resBundle.handleGetObject("jdbcrowsetimpl.invalstate").toString()); 
  }
  
  public void execute() {
    prepare();
    setProperties(this.ps);
    decodeParams(getParams(), this.ps);
    this.rs = this.ps.executeQuery();
    notifyRowSetChanged();
  }
  
  protected void setProperties(PreparedStatement paramPreparedStatement) throws SQLException {
    try {
      paramPreparedStatement.setEscapeProcessing(getEscapeProcessing());
    } catch (SQLException sQLException) {
      System.err.println(this.resBundle.handleGetObject("jdbcrowsetimpl.setescapeprocessing").toString() + sQLException.getLocalizedMessage());
    } 
    try {
      paramPreparedStatement.setMaxFieldSize(getMaxFieldSize());
    } catch (SQLException sQLException) {
      System.err.println(this.resBundle.handleGetObject("jdbcrowsetimpl.setmaxfieldsize").toString() + sQLException.getLocalizedMessage());
    } 
    try {
      paramPreparedStatement.setMaxRows(getMaxRows());
    } catch (SQLException sQLException) {
      System.err.println(this.resBundle.handleGetObject("jdbcrowsetimpl.setmaxrows").toString() + sQLException.getLocalizedMessage());
    } 
    try {
      paramPreparedStatement.setQueryTimeout(getQueryTimeout());
    } catch (SQLException sQLException) {
      System.err.println(this.resBundle.handleGetObject("jdbcrowsetimpl.setquerytimeout").toString() + sQLException.getLocalizedMessage());
    } 
  }
  
  private Connection connect() throws SQLException {
    if (this.conn != null)
      return this.conn; 
    if (getDataSourceName() != null)
      try {
        InitialContext initialContext = new InitialContext();
        DataSource dataSource = (DataSource)initialContext.lookup(getDataSourceName());
        return (getUsername() != null && !getUsername().equals("")) ? dataSource.getConnection(getUsername(), getPassword()) : dataSource.getConnection();
      } catch (NamingException namingException) {
        throw new SQLException(this.resBundle.handleGetObject("jdbcrowsetimpl.connect").toString());
      }  
    return (getUrl() != null) ? DriverManager.getConnection(getUrl(), getUsername(), getPassword()) : null;
  }
  
  protected PreparedStatement prepare() throws SQLException {
    this.conn = connect();
    try {
      Map map = getTypeMap();
      if (map != null)
        this.conn.setTypeMap(map); 
      this.ps = this.conn.prepareStatement(getCommand(), 1004, 1008);
    } catch (SQLException sQLException) {
      System.err.println(this.resBundle.handleGetObject("jdbcrowsetimpl.prepare").toString() + sQLException.getLocalizedMessage());
      if (this.ps != null)
        this.ps.close(); 
      if (this.conn != null)
        this.conn.close(); 
      throw new SQLException(sQLException.getMessage());
    } 
    return this.ps;
  }
  
  private void decodeParams(Object[] paramArrayOfObject, PreparedStatement paramPreparedStatement) throws SQLException {
    Object[] arrayOfObject = null;
    for (byte b = 0; b < paramArrayOfObject.length; b++) {
      if (paramArrayOfObject[b] instanceof Object[]) {
        arrayOfObject = (Object[])paramArrayOfObject[b];
        if (arrayOfObject.length == 2) {
          if (arrayOfObject[false] == null) {
            paramPreparedStatement.setNull(b + true, ((Integer)arrayOfObject[1]).intValue());
          } else if (arrayOfObject[0] instanceof Date || arrayOfObject[0] instanceof Time || arrayOfObject[0] instanceof Timestamp) {
            System.err.println(this.resBundle.handleGetObject("jdbcrowsetimpl.detecteddate"));
            if (arrayOfObject[1] instanceof Calendar) {
              System.err.println(this.resBundle.handleGetObject("jdbcrowsetimpl.detectedcalendar"));
              paramPreparedStatement.setDate(b + true, (Date)arrayOfObject[0], (Calendar)arrayOfObject[1]);
            } else {
              throw new SQLException(this.resBundle.handleGetObject("jdbcrowsetimpl.paramtype").toString());
            } 
          } else if (arrayOfObject[0] instanceof Reader) {
            paramPreparedStatement.setCharacterStream(b + true, (Reader)arrayOfObject[0], ((Integer)arrayOfObject[1]).intValue());
          } else if (arrayOfObject[1] instanceof Integer) {
            paramPreparedStatement.setObject(b + true, arrayOfObject[0], ((Integer)arrayOfObject[1]).intValue());
          } 
        } else if (arrayOfObject.length == 3) {
          if (arrayOfObject[false] == null) {
            paramPreparedStatement.setNull(b + true, ((Integer)arrayOfObject[1]).intValue(), (String)arrayOfObject[2]);
          } else {
            if (arrayOfObject[0] instanceof InputStream)
              switch (((Integer)arrayOfObject[2]).intValue()) {
                case 0:
                  paramPreparedStatement.setUnicodeStream(b + true, (InputStream)arrayOfObject[0], ((Integer)arrayOfObject[1]).intValue());
                  break;
                case 1:
                  paramPreparedStatement.setBinaryStream(b + true, (InputStream)arrayOfObject[0], ((Integer)arrayOfObject[1]).intValue());
                  break;
                case 2:
                  paramPreparedStatement.setAsciiStream(b + true, (InputStream)arrayOfObject[0], ((Integer)arrayOfObject[1]).intValue());
                  break;
                default:
                  throw new SQLException(this.resBundle.handleGetObject("jdbcrowsetimpl.paramtype").toString());
              }  
            if (arrayOfObject[1] instanceof Integer && arrayOfObject[2] instanceof Integer) {
              paramPreparedStatement.setObject(b + true, arrayOfObject[0], ((Integer)arrayOfObject[1]).intValue(), ((Integer)arrayOfObject[2]).intValue());
            } else {
              throw new SQLException(this.resBundle.handleGetObject("jdbcrowsetimpl.paramtype").toString());
            } 
          } 
        } else {
          paramPreparedStatement.setObject(b + true, paramArrayOfObject[b]);
        } 
      } else {
        paramPreparedStatement.setObject(b + true, paramArrayOfObject[b]);
      } 
    } 
  }
  
  public boolean next() throws SQLException {
    checkState();
    boolean bool = this.rs.next();
    notifyCursorMoved();
    return bool;
  }
  
  public void close() {
    if (this.rs != null)
      this.rs.close(); 
    if (this.ps != null)
      this.ps.close(); 
    if (this.conn != null)
      this.conn.close(); 
  }
  
  public boolean wasNull() throws SQLException {
    checkState();
    return this.rs.wasNull();
  }
  
  public String getString(int paramInt) throws SQLException {
    checkState();
    return this.rs.getString(paramInt);
  }
  
  public boolean getBoolean(int paramInt) throws SQLException {
    checkState();
    return this.rs.getBoolean(paramInt);
  }
  
  public byte getByte(int paramInt) throws SQLException {
    checkState();
    return this.rs.getByte(paramInt);
  }
  
  public short getShort(int paramInt) throws SQLException {
    checkState();
    return this.rs.getShort(paramInt);
  }
  
  public int getInt(int paramInt) throws SQLException {
    checkState();
    return this.rs.getInt(paramInt);
  }
  
  public long getLong(int paramInt) throws SQLException {
    checkState();
    return this.rs.getLong(paramInt);
  }
  
  public float getFloat(int paramInt) throws SQLException {
    checkState();
    return this.rs.getFloat(paramInt);
  }
  
  public double getDouble(int paramInt) throws SQLException {
    checkState();
    return this.rs.getDouble(paramInt);
  }
  
  @Deprecated
  public BigDecimal getBigDecimal(int paramInt1, int paramInt2) throws SQLException {
    checkState();
    return this.rs.getBigDecimal(paramInt1, paramInt2);
  }
  
  public byte[] getBytes(int paramInt) throws SQLException {
    checkState();
    return this.rs.getBytes(paramInt);
  }
  
  public Date getDate(int paramInt) throws SQLException {
    checkState();
    return this.rs.getDate(paramInt);
  }
  
  public Time getTime(int paramInt) throws SQLException {
    checkState();
    return this.rs.getTime(paramInt);
  }
  
  public Timestamp getTimestamp(int paramInt) throws SQLException {
    checkState();
    return this.rs.getTimestamp(paramInt);
  }
  
  public InputStream getAsciiStream(int paramInt) throws SQLException {
    checkState();
    return this.rs.getAsciiStream(paramInt);
  }
  
  @Deprecated
  public InputStream getUnicodeStream(int paramInt) throws SQLException {
    checkState();
    return this.rs.getUnicodeStream(paramInt);
  }
  
  public InputStream getBinaryStream(int paramInt) throws SQLException {
    checkState();
    return this.rs.getBinaryStream(paramInt);
  }
  
  public String getString(String paramString) throws SQLException { return getString(findColumn(paramString)); }
  
  public boolean getBoolean(String paramString) throws SQLException { return getBoolean(findColumn(paramString)); }
  
  public byte getByte(String paramString) throws SQLException { return getByte(findColumn(paramString)); }
  
  public short getShort(String paramString) throws SQLException { return getShort(findColumn(paramString)); }
  
  public int getInt(String paramString) throws SQLException { return getInt(findColumn(paramString)); }
  
  public long getLong(String paramString) throws SQLException { return getLong(findColumn(paramString)); }
  
  public float getFloat(String paramString) throws SQLException { return getFloat(findColumn(paramString)); }
  
  public double getDouble(String paramString) throws SQLException { return getDouble(findColumn(paramString)); }
  
  @Deprecated
  public BigDecimal getBigDecimal(String paramString, int paramInt) throws SQLException { return getBigDecimal(findColumn(paramString), paramInt); }
  
  public byte[] getBytes(String paramString) throws SQLException { return getBytes(findColumn(paramString)); }
  
  public Date getDate(String paramString) throws SQLException { return getDate(findColumn(paramString)); }
  
  public Time getTime(String paramString) throws SQLException { return getTime(findColumn(paramString)); }
  
  public Timestamp getTimestamp(String paramString) throws SQLException { return getTimestamp(findColumn(paramString)); }
  
  public InputStream getAsciiStream(String paramString) throws SQLException { return getAsciiStream(findColumn(paramString)); }
  
  @Deprecated
  public InputStream getUnicodeStream(String paramString) throws SQLException { return getUnicodeStream(findColumn(paramString)); }
  
  public InputStream getBinaryStream(String paramString) throws SQLException { return getBinaryStream(findColumn(paramString)); }
  
  public SQLWarning getWarnings() throws SQLException {
    checkState();
    return this.rs.getWarnings();
  }
  
  public void clearWarnings() {
    checkState();
    this.rs.clearWarnings();
  }
  
  public String getCursorName() throws SQLException {
    checkState();
    return this.rs.getCursorName();
  }
  
  public ResultSetMetaData getMetaData() throws SQLException {
    checkState();
    try {
      checkState();
    } catch (SQLException sQLException) {
      prepare();
      return this.ps.getMetaData();
    } 
    return this.rs.getMetaData();
  }
  
  public Object getObject(int paramInt) throws SQLException {
    checkState();
    return this.rs.getObject(paramInt);
  }
  
  public Object getObject(String paramString) throws SQLException { return getObject(findColumn(paramString)); }
  
  public int findColumn(String paramString) throws SQLException {
    checkState();
    return this.rs.findColumn(paramString);
  }
  
  public Reader getCharacterStream(int paramInt) throws SQLException {
    checkState();
    return this.rs.getCharacterStream(paramInt);
  }
  
  public Reader getCharacterStream(String paramString) throws SQLException { return getCharacterStream(findColumn(paramString)); }
  
  public BigDecimal getBigDecimal(int paramInt) throws SQLException {
    checkState();
    return this.rs.getBigDecimal(paramInt);
  }
  
  public BigDecimal getBigDecimal(String paramString) throws SQLException { return getBigDecimal(findColumn(paramString)); }
  
  public boolean isBeforeFirst() throws SQLException {
    checkState();
    return this.rs.isBeforeFirst();
  }
  
  public boolean isAfterLast() throws SQLException {
    checkState();
    return this.rs.isAfterLast();
  }
  
  public boolean isFirst() throws SQLException {
    checkState();
    return this.rs.isFirst();
  }
  
  public boolean isLast() throws SQLException {
    checkState();
    return this.rs.isLast();
  }
  
  public void beforeFirst() {
    checkState();
    this.rs.beforeFirst();
    notifyCursorMoved();
  }
  
  public void afterLast() {
    checkState();
    this.rs.afterLast();
    notifyCursorMoved();
  }
  
  public boolean first() throws SQLException {
    checkState();
    boolean bool = this.rs.first();
    notifyCursorMoved();
    return bool;
  }
  
  public boolean last() throws SQLException {
    checkState();
    boolean bool = this.rs.last();
    notifyCursorMoved();
    return bool;
  }
  
  public int getRow() throws SQLException {
    checkState();
    return this.rs.getRow();
  }
  
  public boolean absolute(int paramInt) throws SQLException {
    checkState();
    boolean bool = this.rs.absolute(paramInt);
    notifyCursorMoved();
    return bool;
  }
  
  public boolean relative(int paramInt) throws SQLException {
    checkState();
    boolean bool = this.rs.relative(paramInt);
    notifyCursorMoved();
    return bool;
  }
  
  public boolean previous() throws SQLException {
    checkState();
    boolean bool = this.rs.previous();
    notifyCursorMoved();
    return bool;
  }
  
  public void setFetchDirection(int paramInt) throws SQLException {
    checkState();
    this.rs.setFetchDirection(paramInt);
  }
  
  public int getFetchDirection() throws SQLException {
    try {
      checkState();
    } catch (SQLException sQLException) {
      super.getFetchDirection();
    } 
    return this.rs.getFetchDirection();
  }
  
  public void setFetchSize(int paramInt) throws SQLException {
    checkState();
    this.rs.setFetchSize(paramInt);
  }
  
  public int getType() throws SQLException {
    try {
      checkState();
    } catch (SQLException sQLException) {
      return super.getType();
    } 
    return (this.rs == null) ? super.getType() : this.rs.getType();
  }
  
  public int getConcurrency() throws SQLException {
    try {
      checkState();
    } catch (SQLException sQLException) {
      super.getConcurrency();
    } 
    return this.rs.getConcurrency();
  }
  
  public boolean rowUpdated() throws SQLException {
    checkState();
    return this.rs.rowUpdated();
  }
  
  public boolean rowInserted() throws SQLException {
    checkState();
    return this.rs.rowInserted();
  }
  
  public boolean rowDeleted() throws SQLException {
    checkState();
    return this.rs.rowDeleted();
  }
  
  public void updateNull(int paramInt) throws SQLException {
    checkState();
    checkTypeConcurrency();
    this.rs.updateNull(paramInt);
  }
  
  public void updateBoolean(int paramInt, boolean paramBoolean) throws SQLException {
    checkState();
    checkTypeConcurrency();
    this.rs.updateBoolean(paramInt, paramBoolean);
  }
  
  public void updateByte(int paramInt, byte paramByte) throws SQLException {
    checkState();
    checkTypeConcurrency();
    this.rs.updateByte(paramInt, paramByte);
  }
  
  public void updateShort(int paramInt, short paramShort) throws SQLException {
    checkState();
    checkTypeConcurrency();
    this.rs.updateShort(paramInt, paramShort);
  }
  
  public void updateInt(int paramInt1, int paramInt2) throws SQLException {
    checkState();
    checkTypeConcurrency();
    this.rs.updateInt(paramInt1, paramInt2);
  }
  
  public void updateLong(int paramInt, long paramLong) throws SQLException {
    checkState();
    checkTypeConcurrency();
    this.rs.updateLong(paramInt, paramLong);
  }
  
  public void updateFloat(int paramInt, float paramFloat) throws SQLException {
    checkState();
    checkTypeConcurrency();
    this.rs.updateFloat(paramInt, paramFloat);
  }
  
  public void updateDouble(int paramInt, double paramDouble) throws SQLException {
    checkState();
    checkTypeConcurrency();
    this.rs.updateDouble(paramInt, paramDouble);
  }
  
  public void updateBigDecimal(int paramInt, BigDecimal paramBigDecimal) throws SQLException {
    checkState();
    checkTypeConcurrency();
    this.rs.updateBigDecimal(paramInt, paramBigDecimal);
  }
  
  public void updateString(int paramInt, String paramString) throws SQLException {
    checkState();
    checkTypeConcurrency();
    this.rs.updateString(paramInt, paramString);
  }
  
  public void updateBytes(int paramInt, byte[] paramArrayOfByte) throws SQLException {
    checkState();
    checkTypeConcurrency();
    this.rs.updateBytes(paramInt, paramArrayOfByte);
  }
  
  public void updateDate(int paramInt, Date paramDate) throws SQLException {
    checkState();
    checkTypeConcurrency();
    this.rs.updateDate(paramInt, paramDate);
  }
  
  public void updateTime(int paramInt, Time paramTime) throws SQLException {
    checkState();
    checkTypeConcurrency();
    this.rs.updateTime(paramInt, paramTime);
  }
  
  public void updateTimestamp(int paramInt, Timestamp paramTimestamp) throws SQLException {
    checkState();
    checkTypeConcurrency();
    this.rs.updateTimestamp(paramInt, paramTimestamp);
  }
  
  public void updateAsciiStream(int paramInt1, InputStream paramInputStream, int paramInt2) throws SQLException {
    checkState();
    checkTypeConcurrency();
    this.rs.updateAsciiStream(paramInt1, paramInputStream, paramInt2);
  }
  
  public void updateBinaryStream(int paramInt1, InputStream paramInputStream, int paramInt2) throws SQLException {
    checkState();
    checkTypeConcurrency();
    this.rs.updateBinaryStream(paramInt1, paramInputStream, paramInt2);
  }
  
  public void updateCharacterStream(int paramInt1, Reader paramReader, int paramInt2) throws SQLException {
    checkState();
    checkTypeConcurrency();
    this.rs.updateCharacterStream(paramInt1, paramReader, paramInt2);
  }
  
  public void updateObject(int paramInt1, Object paramObject, int paramInt2) throws SQLException {
    checkState();
    checkTypeConcurrency();
    this.rs.updateObject(paramInt1, paramObject, paramInt2);
  }
  
  public void updateObject(int paramInt, Object paramObject) throws SQLException {
    checkState();
    checkTypeConcurrency();
    this.rs.updateObject(paramInt, paramObject);
  }
  
  public void updateNull(String paramString) throws SQLException { updateNull(findColumn(paramString)); }
  
  public void updateBoolean(String paramString, boolean paramBoolean) throws SQLException { updateBoolean(findColumn(paramString), paramBoolean); }
  
  public void updateByte(String paramString, byte paramByte) throws SQLException { updateByte(findColumn(paramString), paramByte); }
  
  public void updateShort(String paramString, short paramShort) throws SQLException { updateShort(findColumn(paramString), paramShort); }
  
  public void updateInt(String paramString, int paramInt) throws SQLException { updateInt(findColumn(paramString), paramInt); }
  
  public void updateLong(String paramString, long paramLong) throws SQLException { updateLong(findColumn(paramString), paramLong); }
  
  public void updateFloat(String paramString, float paramFloat) throws SQLException { updateFloat(findColumn(paramString), paramFloat); }
  
  public void updateDouble(String paramString, double paramDouble) throws SQLException { updateDouble(findColumn(paramString), paramDouble); }
  
  public void updateBigDecimal(String paramString, BigDecimal paramBigDecimal) throws SQLException { updateBigDecimal(findColumn(paramString), paramBigDecimal); }
  
  public void updateString(String paramString1, String paramString2) throws SQLException { updateString(findColumn(paramString1), paramString2); }
  
  public void updateBytes(String paramString, byte[] paramArrayOfByte) throws SQLException { updateBytes(findColumn(paramString), paramArrayOfByte); }
  
  public void updateDate(String paramString, Date paramDate) throws SQLException { updateDate(findColumn(paramString), paramDate); }
  
  public void updateTime(String paramString, Time paramTime) throws SQLException { updateTime(findColumn(paramString), paramTime); }
  
  public void updateTimestamp(String paramString, Timestamp paramTimestamp) throws SQLException { updateTimestamp(findColumn(paramString), paramTimestamp); }
  
  public void updateAsciiStream(String paramString, InputStream paramInputStream, int paramInt) throws SQLException { updateAsciiStream(findColumn(paramString), paramInputStream, paramInt); }
  
  public void updateBinaryStream(String paramString, InputStream paramInputStream, int paramInt) throws SQLException { updateBinaryStream(findColumn(paramString), paramInputStream, paramInt); }
  
  public void updateCharacterStream(String paramString, Reader paramReader, int paramInt) throws SQLException { updateCharacterStream(findColumn(paramString), paramReader, paramInt); }
  
  public void updateObject(String paramString, Object paramObject, int paramInt) throws SQLException { updateObject(findColumn(paramString), paramObject, paramInt); }
  
  public void updateObject(String paramString, Object paramObject) throws SQLException { updateObject(findColumn(paramString), paramObject); }
  
  public void insertRow() {
    checkState();
    this.rs.insertRow();
    notifyRowChanged();
  }
  
  public void updateRow() {
    checkState();
    this.rs.updateRow();
    notifyRowChanged();
  }
  
  public void deleteRow() {
    checkState();
    this.rs.deleteRow();
    notifyRowChanged();
  }
  
  public void refreshRow() {
    checkState();
    this.rs.refreshRow();
  }
  
  public void cancelRowUpdates() {
    checkState();
    this.rs.cancelRowUpdates();
    notifyRowChanged();
  }
  
  public void moveToInsertRow() {
    checkState();
    this.rs.moveToInsertRow();
  }
  
  public void moveToCurrentRow() {
    checkState();
    this.rs.moveToCurrentRow();
  }
  
  public Statement getStatement() throws SQLException { return (this.rs != null) ? this.rs.getStatement() : null; }
  
  public Object getObject(int paramInt, Map<String, Class<?>> paramMap) throws SQLException {
    checkState();
    return this.rs.getObject(paramInt, paramMap);
  }
  
  public Ref getRef(int paramInt) throws SQLException {
    checkState();
    return this.rs.getRef(paramInt);
  }
  
  public Blob getBlob(int paramInt) throws SQLException {
    checkState();
    return this.rs.getBlob(paramInt);
  }
  
  public Clob getClob(int paramInt) throws SQLException {
    checkState();
    return this.rs.getClob(paramInt);
  }
  
  public Array getArray(int paramInt) throws SQLException {
    checkState();
    return this.rs.getArray(paramInt);
  }
  
  public Object getObject(String paramString, Map<String, Class<?>> paramMap) throws SQLException { return getObject(findColumn(paramString), paramMap); }
  
  public Ref getRef(String paramString) throws SQLException { return getRef(findColumn(paramString)); }
  
  public Blob getBlob(String paramString) throws SQLException { return getBlob(findColumn(paramString)); }
  
  public Clob getClob(String paramString) throws SQLException { return getClob(findColumn(paramString)); }
  
  public Array getArray(String paramString) throws SQLException { return getArray(findColumn(paramString)); }
  
  public Date getDate(int paramInt, Calendar paramCalendar) throws SQLException {
    checkState();
    return this.rs.getDate(paramInt, paramCalendar);
  }
  
  public Date getDate(String paramString, Calendar paramCalendar) throws SQLException { return getDate(findColumn(paramString), paramCalendar); }
  
  public Time getTime(int paramInt, Calendar paramCalendar) throws SQLException {
    checkState();
    return this.rs.getTime(paramInt, paramCalendar);
  }
  
  public Time getTime(String paramString, Calendar paramCalendar) throws SQLException { return getTime(findColumn(paramString), paramCalendar); }
  
  public Timestamp getTimestamp(int paramInt, Calendar paramCalendar) throws SQLException {
    checkState();
    return this.rs.getTimestamp(paramInt, paramCalendar);
  }
  
  public Timestamp getTimestamp(String paramString, Calendar paramCalendar) throws SQLException { return getTimestamp(findColumn(paramString), paramCalendar); }
  
  public void updateRef(int paramInt, Ref paramRef) throws SQLException {
    checkState();
    this.rs.updateRef(paramInt, paramRef);
  }
  
  public void updateRef(String paramString, Ref paramRef) throws SQLException { updateRef(findColumn(paramString), paramRef); }
  
  public void updateClob(int paramInt, Clob paramClob) throws SQLException {
    checkState();
    this.rs.updateClob(paramInt, paramClob);
  }
  
  public void updateClob(String paramString, Clob paramClob) throws SQLException { updateClob(findColumn(paramString), paramClob); }
  
  public void updateBlob(int paramInt, Blob paramBlob) throws SQLException {
    checkState();
    this.rs.updateBlob(paramInt, paramBlob);
  }
  
  public void updateBlob(String paramString, Blob paramBlob) throws SQLException { updateBlob(findColumn(paramString), paramBlob); }
  
  public void updateArray(int paramInt, Array paramArray) throws SQLException {
    checkState();
    this.rs.updateArray(paramInt, paramArray);
  }
  
  public void updateArray(String paramString, Array paramArray) throws SQLException { updateArray(findColumn(paramString), paramArray); }
  
  public URL getURL(int paramInt) throws SQLException {
    checkState();
    return this.rs.getURL(paramInt);
  }
  
  public URL getURL(String paramString) throws SQLException { return getURL(findColumn(paramString)); }
  
  public RowSetWarning getRowSetWarnings() throws SQLException { return null; }
  
  public void unsetMatchColumn(int[] paramArrayOfInt) throws SQLException {
    byte b;
    for (b = 0; b < paramArrayOfInt.length; b++) {
      int i = Integer.parseInt(((Integer)this.iMatchColumns.get(b)).toString());
      if (paramArrayOfInt[b] != i)
        throw new SQLException(this.resBundle.handleGetObject("jdbcrowsetimpl.matchcols").toString()); 
    } 
    for (b = 0; b < paramArrayOfInt.length; b++)
      this.iMatchColumns.set(b, Integer.valueOf(-1)); 
  }
  
  public void unsetMatchColumn(String[] paramArrayOfString) throws SQLException {
    byte b;
    for (b = 0; b < paramArrayOfString.length; b++) {
      if (!paramArrayOfString[b].equals(this.strMatchColumns.get(b)))
        throw new SQLException(this.resBundle.handleGetObject("jdbcrowsetimpl.matchcols").toString()); 
    } 
    for (b = 0; b < paramArrayOfString.length; b++)
      this.strMatchColumns.set(b, null); 
  }
  
  public String[] getMatchColumnNames() throws SQLException {
    String[] arrayOfString = new String[this.strMatchColumns.size()];
    if (this.strMatchColumns.get(false) == null)
      throw new SQLException(this.resBundle.handleGetObject("jdbcrowsetimpl.setmatchcols").toString()); 
    this.strMatchColumns.copyInto(arrayOfString);
    return arrayOfString;
  }
  
  public int[] getMatchColumnIndexes() throws SQLException {
    Integer[] arrayOfInteger = new Integer[this.iMatchColumns.size()];
    int[] arrayOfInt = new int[this.iMatchColumns.size()];
    int i = ((Integer)this.iMatchColumns.get(0)).intValue();
    if (i == -1)
      throw new SQLException(this.resBundle.handleGetObject("jdbcrowsetimpl.setmatchcols").toString()); 
    this.iMatchColumns.copyInto(arrayOfInteger);
    for (byte b = 0; b < arrayOfInteger.length; b++)
      arrayOfInt[b] = arrayOfInteger[b].intValue(); 
    return arrayOfInt;
  }
  
  public void setMatchColumn(int[] paramArrayOfInt) throws SQLException {
    byte b;
    for (b = 0; b < paramArrayOfInt.length; b++) {
      if (paramArrayOfInt[b] < 0)
        throw new SQLException(this.resBundle.handleGetObject("jdbcrowsetimpl.matchcols1").toString()); 
    } 
    for (b = 0; b < paramArrayOfInt.length; b++)
      this.iMatchColumns.add(b, Integer.valueOf(paramArrayOfInt[b])); 
  }
  
  public void setMatchColumn(String[] paramArrayOfString) throws SQLException {
    byte b;
    for (b = 0; b < paramArrayOfString.length; b++) {
      if (paramArrayOfString[b] == null || paramArrayOfString[b].equals(""))
        throw new SQLException(this.resBundle.handleGetObject("jdbcrowsetimpl.matchcols2").toString()); 
    } 
    for (b = 0; b < paramArrayOfString.length; b++)
      this.strMatchColumns.add(b, paramArrayOfString[b]); 
  }
  
  public void setMatchColumn(int paramInt) throws SQLException {
    if (paramInt < 0)
      throw new SQLException(this.resBundle.handleGetObject("jdbcrowsetimpl.matchcols1").toString()); 
    this.iMatchColumns.set(0, Integer.valueOf(paramInt));
  }
  
  public void setMatchColumn(String paramString) throws SQLException {
    if (paramString == null || (paramString = paramString.trim()).equals(""))
      throw new SQLException(this.resBundle.handleGetObject("jdbcrowsetimpl.matchcols2").toString()); 
    this.strMatchColumns.set(0, paramString);
  }
  
  public void unsetMatchColumn(int paramInt) throws SQLException {
    if (!((Integer)this.iMatchColumns.get(0)).equals(Integer.valueOf(paramInt)))
      throw new SQLException(this.resBundle.handleGetObject("jdbcrowsetimpl.unsetmatch").toString()); 
    if (this.strMatchColumns.get(false) != null)
      throw new SQLException(this.resBundle.handleGetObject("jdbcrowsetimpl.usecolname").toString()); 
    this.iMatchColumns.set(0, Integer.valueOf(-1));
  }
  
  public void unsetMatchColumn(String paramString) throws SQLException {
    paramString = paramString.trim();
    if (!((String)this.strMatchColumns.get(0)).equals(paramString))
      throw new SQLException(this.resBundle.handleGetObject("jdbcrowsetimpl.unsetmatch").toString()); 
    if (((Integer)this.iMatchColumns.get(0)).intValue() > 0)
      throw new SQLException(this.resBundle.handleGetObject("jdbcrowsetimpl.usecolid").toString()); 
    this.strMatchColumns.set(0, null);
  }
  
  public DatabaseMetaData getDatabaseMetaData() throws SQLException {
    Connection connection = connect();
    return connection.getMetaData();
  }
  
  public ParameterMetaData getParameterMetaData() throws SQLException {
    prepare();
    return this.ps.getParameterMetaData();
  }
  
  public void commit() {
    this.conn.commit();
    if (this.conn.getHoldability() != 1)
      this.rs = null; 
  }
  
  public void setAutoCommit(boolean paramBoolean) throws SQLException {
    if (this.conn != null) {
      this.conn.setAutoCommit(paramBoolean);
    } else {
      this.conn = connect();
      this.conn.setAutoCommit(paramBoolean);
    } 
  }
  
  public boolean getAutoCommit() throws SQLException { return this.conn.getAutoCommit(); }
  
  public void rollback() {
    this.conn.rollback();
    this.rs = null;
  }
  
  public void rollback(Savepoint paramSavepoint) throws SQLException { this.conn.rollback(paramSavepoint); }
  
  protected void setParams() {
    if (this.rs == null) {
      setType(1004);
      setConcurrency(1008);
    } else {
      setType(this.rs.getType());
      setConcurrency(this.rs.getConcurrency());
    } 
  }
  
  private void checkTypeConcurrency() {
    if (this.rs.getType() == 1003 || this.rs.getConcurrency() == 1007)
      throw new SQLException(this.resBundle.handleGetObject("jdbcrowsetimpl.resnotupd").toString()); 
  }
  
  protected Connection getConnection() throws SQLException { return this.conn; }
  
  protected void setConnection(Connection paramConnection) throws SQLException { this.conn = paramConnection; }
  
  protected PreparedStatement getPreparedStatement() throws SQLException { return this.ps; }
  
  protected void setPreparedStatement(PreparedStatement paramPreparedStatement) throws SQLException { this.ps = paramPreparedStatement; }
  
  protected ResultSet getResultSet() throws SQLException {
    checkState();
    return this.rs;
  }
  
  protected void setResultSet(ResultSet paramResultSet) throws SQLException { this.rs = paramResultSet; }
  
  public void setCommand(String paramString) throws SQLException {
    if (getCommand() != null) {
      if (!getCommand().equals(paramString)) {
        super.setCommand(paramString);
        this.ps = null;
        this.rs = null;
      } 
    } else {
      super.setCommand(paramString);
    } 
  }
  
  public void setDataSourceName(String paramString) throws SQLException {
    if (getDataSourceName() != null) {
      if (!getDataSourceName().equals(paramString)) {
        super.setDataSourceName(paramString);
        this.conn = null;
        this.ps = null;
        this.rs = null;
      } 
    } else {
      super.setDataSourceName(paramString);
    } 
  }
  
  public void setUrl(String paramString) throws SQLException {
    if (getUrl() != null) {
      if (!getUrl().equals(paramString)) {
        super.setUrl(paramString);
        this.conn = null;
        this.ps = null;
        this.rs = null;
      } 
    } else {
      super.setUrl(paramString);
    } 
  }
  
  public void setUsername(String paramString) throws SQLException {
    if (getUsername() != null) {
      if (!getUsername().equals(paramString)) {
        super.setUsername(paramString);
        this.conn = null;
        this.ps = null;
        this.rs = null;
      } 
    } else {
      super.setUsername(paramString);
    } 
  }
  
  public void setPassword(String paramString) throws SQLException {
    if (getPassword() != null) {
      if (!getPassword().equals(paramString)) {
        super.setPassword(paramString);
        this.conn = null;
        this.ps = null;
        this.rs = null;
      } 
    } else {
      super.setPassword(paramString);
    } 
  }
  
  public void setType(int paramInt) throws SQLException {
    boolean bool;
    try {
      bool = getType();
    } catch (SQLException sQLException) {
      bool = false;
    } 
    if (bool != paramInt)
      super.setType(paramInt); 
  }
  
  public void setConcurrency(int paramInt) throws SQLException {
    boolean bool;
    try {
      bool = getConcurrency();
    } catch (NullPointerException nullPointerException) {
      bool = false;
    } 
    if (bool != paramInt)
      super.setConcurrency(paramInt); 
  }
  
  public SQLXML getSQLXML(int paramInt) throws SQLException { throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("jdbcrowsetimpl.featnotsupp").toString()); }
  
  public SQLXML getSQLXML(String paramString) throws SQLException { throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("jdbcrowsetimpl.featnotsupp").toString()); }
  
  public RowId getRowId(int paramInt) throws SQLException { throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("jdbcrowsetimpl.featnotsupp").toString()); }
  
  public RowId getRowId(String paramString) throws SQLException { throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("jdbcrowsetimpl.featnotsupp").toString()); }
  
  public void updateRowId(int paramInt, RowId paramRowId) throws SQLException { throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("jdbcrowsetimpl.featnotsupp").toString()); }
  
  public void updateRowId(String paramString, RowId paramRowId) throws SQLException { throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("jdbcrowsetimpl.featnotsupp").toString()); }
  
  public int getHoldability() throws SQLException { throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("jdbcrowsetimpl.featnotsupp").toString()); }
  
  public boolean isClosed() throws SQLException { throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("jdbcrowsetimpl.featnotsupp").toString()); }
  
  public void updateNString(int paramInt, String paramString) throws SQLException { throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("jdbcrowsetimpl.featnotsupp").toString()); }
  
  public void updateNString(String paramString1, String paramString2) throws SQLException { throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("jdbcrowsetimpl.featnotsupp").toString()); }
  
  public void updateNClob(int paramInt, NClob paramNClob) throws SQLException { throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("jdbcrowsetimpl.featnotsupp").toString()); }
  
  public void updateNClob(String paramString, NClob paramNClob) throws SQLException { throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("jdbcrowsetimpl.featnotsupp").toString()); }
  
  public NClob getNClob(int paramInt) throws SQLException { throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("jdbcrowsetimpl.featnotsupp").toString()); }
  
  public NClob getNClob(String paramString) throws SQLException { throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("jdbcrowsetimpl.featnotsupp").toString()); }
  
  public <T> T unwrap(Class<T> paramClass) throws SQLException { return null; }
  
  public boolean isWrapperFor(Class<?> paramClass) throws SQLException { return false; }
  
  public void setSQLXML(int paramInt, SQLXML paramSQLXML) throws SQLException { throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("jdbcrowsetimpl.featnotsupp").toString()); }
  
  public void setSQLXML(String paramString, SQLXML paramSQLXML) throws SQLException { throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("jdbcrowsetimpl.featnotsupp").toString()); }
  
  public void setRowId(int paramInt, RowId paramRowId) throws SQLException { throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("jdbcrowsetimpl.featnotsupp").toString()); }
  
  public void setRowId(String paramString, RowId paramRowId) throws SQLException { throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("jdbcrowsetimpl.featnotsupp").toString()); }
  
  public void setNString(int paramInt, String paramString) throws SQLException { throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("jdbcrowsetimpl.featnotsupp").toString()); }
  
  public void setNCharacterStream(int paramInt, Reader paramReader) throws SQLException { throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("jdbcrowsetimpl.featnotsupp").toString()); }
  
  public void setNClob(String paramString, NClob paramNClob) throws SQLException { throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("jdbcrowsetimpl.featnotsupp").toString()); }
  
  public Reader getNCharacterStream(int paramInt) throws SQLException { throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("jdbcrowsetimpl.featnotsupp").toString()); }
  
  public Reader getNCharacterStream(String paramString) throws SQLException { throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("jdbcrowsetimpl.featnotsupp").toString()); }
  
  public void updateSQLXML(int paramInt, SQLXML paramSQLXML) throws SQLException { throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("jdbcrowsetimpl.featnotsupp").toString()); }
  
  public void updateSQLXML(String paramString, SQLXML paramSQLXML) throws SQLException { throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("jdbcrowsetimpl.featnotsupp").toString()); }
  
  public String getNString(int paramInt) throws SQLException { throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("jdbcrowsetimpl.featnotsupp").toString()); }
  
  public String getNString(String paramString) throws SQLException { throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("jdbcrowsetimpl.featnotsupp").toString()); }
  
  public void updateNCharacterStream(int paramInt, Reader paramReader, long paramLong) throws SQLException { throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("jdbcrowsetimpl.featnotsupp").toString()); }
  
  public void updateNCharacterStream(String paramString, Reader paramReader, long paramLong) throws SQLException { throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("jdbcrowsetimpl.featnotsupp").toString()); }
  
  public void updateNCharacterStream(int paramInt, Reader paramReader) throws SQLException { throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("jdbcrowsetimpl.featnotsupp").toString()); }
  
  public void updateNCharacterStream(String paramString, Reader paramReader) throws SQLException { throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("jdbcrowsetimpl.featnotsupp").toString()); }
  
  public void updateBlob(int paramInt, InputStream paramInputStream, long paramLong) throws SQLException { throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("jdbcrowsetimpl.featnotsupp").toString()); }
  
  public void updateBlob(String paramString, InputStream paramInputStream, long paramLong) throws SQLException { throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("jdbcrowsetimpl.featnotsupp").toString()); }
  
  public void updateBlob(int paramInt, InputStream paramInputStream) throws SQLException { throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("jdbcrowsetimpl.featnotsupp").toString()); }
  
  public void updateBlob(String paramString, InputStream paramInputStream) throws SQLException { throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("jdbcrowsetimpl.featnotsupp").toString()); }
  
  public void updateClob(int paramInt, Reader paramReader, long paramLong) throws SQLException { throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("jdbcrowsetimpl.featnotsupp").toString()); }
  
  public void updateClob(String paramString, Reader paramReader, long paramLong) throws SQLException { throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("jdbcrowsetimpl.featnotsupp").toString()); }
  
  public void updateClob(int paramInt, Reader paramReader) throws SQLException { throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("jdbcrowsetimpl.featnotsupp").toString()); }
  
  public void updateClob(String paramString, Reader paramReader) throws SQLException { throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("jdbcrowsetimpl.featnotsupp").toString()); }
  
  public void updateNClob(int paramInt, Reader paramReader, long paramLong) throws SQLException { throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("jdbcrowsetimpl.featnotsupp").toString()); }
  
  public void updateNClob(String paramString, Reader paramReader, long paramLong) throws SQLException { throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("jdbcrowsetimpl.featnotsupp").toString()); }
  
  public void updateNClob(int paramInt, Reader paramReader) throws SQLException { throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("jdbcrowsetimpl.featnotsupp").toString()); }
  
  public void updateNClob(String paramString, Reader paramReader) throws SQLException { throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("jdbcrowsetimpl.featnotsupp").toString()); }
  
  public void updateAsciiStream(int paramInt, InputStream paramInputStream, long paramLong) throws SQLException { throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("jdbcrowsetimpl.featnotsupp").toString()); }
  
  public void updateBinaryStream(int paramInt, InputStream paramInputStream, long paramLong) throws SQLException { throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("jdbcrowsetimpl.featnotsupp").toString()); }
  
  public void updateCharacterStream(int paramInt, Reader paramReader, long paramLong) throws SQLException { throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("jdbcrowsetimpl.featnotsupp").toString()); }
  
  public void updateAsciiStream(String paramString, InputStream paramInputStream, long paramLong) throws SQLException { throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("jdbcrowsetimpl.featnotsupp").toString()); }
  
  public void updateAsciiStream(int paramInt, InputStream paramInputStream) throws SQLException { throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("jdbcrowsetimpl.featnotsupp").toString()); }
  
  public void updateAsciiStream(String paramString, InputStream paramInputStream) throws SQLException { throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("jdbcrowsetimpl.featnotsupp").toString()); }
  
  public void updateBinaryStream(String paramString, InputStream paramInputStream, long paramLong) throws SQLException { throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("jdbcrowsetimpl.featnotsupp").toString()); }
  
  public void updateBinaryStream(int paramInt, InputStream paramInputStream) throws SQLException { throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("jdbcrowsetimpl.featnotsupp").toString()); }
  
  public void updateBinaryStream(String paramString, InputStream paramInputStream) throws SQLException { throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("jdbcrowsetimpl.featnotsupp").toString()); }
  
  public void updateCharacterStream(String paramString, Reader paramReader, long paramLong) throws SQLException { throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("jdbcrowsetimpl.featnotsupp").toString()); }
  
  public void updateCharacterStream(int paramInt, Reader paramReader) throws SQLException { throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("jdbcrowsetimpl.featnotsupp").toString()); }
  
  public void updateCharacterStream(String paramString, Reader paramReader) throws SQLException { throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("jdbcrowsetimpl.featnotsupp").toString()); }
  
  public void setURL(int paramInt, URL paramURL) throws SQLException { throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("jdbcrowsetimpl.featnotsupp").toString()); }
  
  public void setNClob(int paramInt, Reader paramReader) throws SQLException { throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("jdbcrowsetimpl.featnotsupp").toString()); }
  
  public void setNClob(String paramString, Reader paramReader, long paramLong) throws SQLException { throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("jdbcrowsetimpl.featnotsupp").toString()); }
  
  public void setNClob(String paramString, Reader paramReader) throws SQLException { throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("jdbcrowsetimpl.featnotsupp").toString()); }
  
  public void setNClob(int paramInt, Reader paramReader, long paramLong) throws SQLException { throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("jdbcrowsetimpl.featnotsupp").toString()); }
  
  public void setNClob(int paramInt, NClob paramNClob) throws SQLException { throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("jdbcrowsetimpl.featnotsupp").toString()); }
  
  public void setNString(String paramString1, String paramString2) throws SQLException { throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("jdbcrowsetimpl.featnotsupp").toString()); }
  
  public void setNCharacterStream(int paramInt, Reader paramReader, long paramLong) throws SQLException { throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("jdbcrowsetimpl.featnotsupp").toString()); }
  
  public void setNCharacterStream(String paramString, Reader paramReader, long paramLong) throws SQLException { throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("jdbcrowsetimpl.featnotsupp").toString()); }
  
  public void setNCharacterStream(String paramString, Reader paramReader) throws SQLException { throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("jdbcrowsetimpl.featnotsupp").toString()); }
  
  public void setTimestamp(String paramString, Timestamp paramTimestamp, Calendar paramCalendar) throws SQLException { throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("jdbcrowsetimpl.featnotsupp").toString()); }
  
  public void setClob(String paramString, Reader paramReader, long paramLong) throws SQLException { throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("jdbcrowsetimpl.featnotsupp").toString()); }
  
  public void setClob(String paramString, Clob paramClob) throws SQLException { throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("jdbcrowsetimpl.featnotsupp").toString()); }
  
  public void setClob(String paramString, Reader paramReader) throws SQLException { throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("jdbcrowsetimpl.featnotsupp").toString()); }
  
  public void setDate(String paramString, Date paramDate) throws SQLException { throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("jdbcrowsetimpl.featnotsupp").toString()); }
  
  public void setDate(String paramString, Date paramDate, Calendar paramCalendar) throws SQLException { throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("jdbcrowsetimpl.featnotsupp").toString()); }
  
  public void setTime(String paramString, Time paramTime) throws SQLException { throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("jdbcrowsetimpl.featnotsupp").toString()); }
  
  public void setTime(String paramString, Time paramTime, Calendar paramCalendar) throws SQLException { throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("jdbcrowsetimpl.featnotsupp").toString()); }
  
  public void setClob(int paramInt, Reader paramReader) throws SQLException { throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("jdbcrowsetimpl.featnotsupp").toString()); }
  
  public void setClob(int paramInt, Reader paramReader, long paramLong) throws SQLException { throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("jdbcrowsetimpl.featnotsupp").toString()); }
  
  public void setBlob(int paramInt, InputStream paramInputStream, long paramLong) throws SQLException { throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("jdbcrowsetimpl.featnotsupp").toString()); }
  
  public void setBlob(int paramInt, InputStream paramInputStream) throws SQLException { throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("jdbcrowsetimpl.featnotsupp").toString()); }
  
  public void setBlob(String paramString, InputStream paramInputStream, long paramLong) throws SQLException { throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("jdbcrowsetimpl.featnotsupp").toString()); }
  
  public void setBlob(String paramString, Blob paramBlob) throws SQLException { throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("jdbcrowsetimpl.featnotsupp").toString()); }
  
  public void setBlob(String paramString, InputStream paramInputStream) throws SQLException { throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("jdbcrowsetimpl.featnotsupp").toString()); }
  
  public void setObject(String paramString, Object paramObject, int paramInt1, int paramInt2) throws SQLException { throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("jdbcrowsetimpl.featnotsupp").toString()); }
  
  public void setObject(String paramString, Object paramObject, int paramInt) throws SQLException { throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("jdbcrowsetimpl.featnotsupp").toString()); }
  
  public void setObject(String paramString, Object paramObject) throws SQLException { throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("jdbcrowsetimpl.featnotsupp").toString()); }
  
  public void setAsciiStream(String paramString, InputStream paramInputStream, int paramInt) throws SQLException { throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("jdbcrowsetimpl.featnotsupp").toString()); }
  
  public void setBinaryStream(String paramString, InputStream paramInputStream, int paramInt) throws SQLException { throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("jdbcrowsetimpl.featnotsupp").toString()); }
  
  public void setCharacterStream(String paramString, Reader paramReader, int paramInt) throws SQLException { throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("jdbcrowsetimpl.featnotsupp").toString()); }
  
  public void setAsciiStream(String paramString, InputStream paramInputStream) throws SQLException { throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("jdbcrowsetimpl.featnotsupp").toString()); }
  
  public void setBinaryStream(String paramString, InputStream paramInputStream) throws SQLException { throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("jdbcrowsetimpl.featnotsupp").toString()); }
  
  public void setCharacterStream(String paramString, Reader paramReader) throws SQLException { throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("jdbcrowsetimpl.featnotsupp").toString()); }
  
  public void setBigDecimal(String paramString, BigDecimal paramBigDecimal) throws SQLException { throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("jdbcrowsetimpl.featnotsupp").toString()); }
  
  public void setString(String paramString1, String paramString2) throws SQLException { throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("jdbcrowsetimpl.featnotsupp").toString()); }
  
  public void setBytes(String paramString, byte[] paramArrayOfByte) throws SQLException { throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("jdbcrowsetimpl.featnotsupp").toString()); }
  
  public void setTimestamp(String paramString, Timestamp paramTimestamp) throws SQLException { throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("jdbcrowsetimpl.featnotsupp").toString()); }
  
  public void setNull(String paramString, int paramInt) throws SQLException { throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("jdbcrowsetimpl.featnotsupp").toString()); }
  
  public void setNull(String paramString1, int paramInt, String paramString2) throws SQLException { throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("jdbcrowsetimpl.featnotsupp").toString()); }
  
  public void setBoolean(String paramString, boolean paramBoolean) throws SQLException { throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("jdbcrowsetimpl.featnotsupp").toString()); }
  
  public void setByte(String paramString, byte paramByte) throws SQLException { throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("jdbcrowsetimpl.featnotsupp").toString()); }
  
  public void setShort(String paramString, short paramShort) throws SQLException { throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("jdbcrowsetimpl.featnotsupp").toString()); }
  
  public void setInt(String paramString, int paramInt) throws SQLException { throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("jdbcrowsetimpl.featnotsupp").toString()); }
  
  public void setLong(String paramString, long paramLong) throws SQLException { throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("jdbcrowsetimpl.featnotsupp").toString()); }
  
  public void setFloat(String paramString, float paramFloat) throws SQLException { throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("jdbcrowsetimpl.featnotsupp").toString()); }
  
  public void setDouble(String paramString, double paramDouble) throws SQLException { throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("jdbcrowsetimpl.featnotsupp").toString()); }
  
  private void readObject(ObjectInputStream paramObjectInputStream) throws IOException, ClassNotFoundException {
    paramObjectInputStream.defaultReadObject();
    try {
      this.resBundle = JdbcRowSetResourceBundle.getJdbcRowSetResourceBundle();
    } catch (IOException iOException) {}
  }
  
  public <T> T getObject(int paramInt, Class<T> paramClass) throws SQLException { throw new SQLFeatureNotSupportedException("Not supported yet."); }
  
  public <T> T getObject(String paramString, Class<T> paramClass) throws SQLException { throw new SQLFeatureNotSupportedException("Not supported yet."); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\rowset\JdbcRowSetImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */