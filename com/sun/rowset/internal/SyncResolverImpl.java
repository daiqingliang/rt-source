package com.sun.rowset.internal;

import com.sun.rowset.CachedRowSetImpl;
import com.sun.rowset.JdbcRowSetResourceBundle;
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
import java.sql.Date;
import java.sql.Ref;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.SQLWarning;
import java.sql.Savepoint;
import java.sql.Statement;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Map;
import javax.sql.RowSet;
import javax.sql.RowSetEvent;
import javax.sql.RowSetMetaData;
import javax.sql.rowset.CachedRowSet;
import javax.sql.rowset.RowSetMetaDataImpl;
import javax.sql.rowset.RowSetWarning;
import javax.sql.rowset.spi.SyncProvider;
import javax.sql.rowset.spi.SyncProviderException;
import javax.sql.rowset.spi.SyncResolver;

public class SyncResolverImpl extends CachedRowSetImpl implements SyncResolver {
  private CachedRowSetImpl crsRes;
  
  private CachedRowSetImpl crsSync;
  
  private ArrayList<?> stats;
  
  private CachedRowSetWriter crw;
  
  private int rowStatus;
  
  private int sz;
  
  private Connection con;
  
  private CachedRowSet row;
  
  private JdbcRowSetResourceBundle resBundle;
  
  static final long serialVersionUID = -3345004441725080251L;
  
  public SyncResolverImpl() throws SQLException {
    try {
      this.crsSync = new CachedRowSetImpl();
      this.crsRes = new CachedRowSetImpl();
      this.crw = new CachedRowSetWriter();
      this.row = new CachedRowSetImpl();
      this.rowStatus = 1;
      try {
        this.resBundle = JdbcRowSetResourceBundle.getJdbcRowSetResourceBundle();
      } catch (IOException iOException) {
        throw new RuntimeException(iOException);
      } 
    } catch (SQLException sQLException) {}
  }
  
  public int getStatus() { return ((Integer)this.stats.get(this.rowStatus - 1)).intValue(); }
  
  public Object getConflictValue(int paramInt) throws SQLException {
    try {
      return this.crsRes.getObject(paramInt);
    } catch (SQLException sQLException) {
      throw new SQLException(sQLException.getMessage());
    } 
  }
  
  public Object getConflictValue(String paramString) throws SQLException {
    try {
      return this.crsRes.getObject(paramString);
    } catch (SQLException sQLException) {
      throw new SQLException(sQLException.getMessage());
    } 
  }
  
  public void setResolvedValue(int paramInt, Object paramObject) throws SQLException {
    try {
      if (paramInt <= 0 || paramInt > this.crsSync.getMetaData().getColumnCount())
        throw new SQLException(this.resBundle.handleGetObject("syncrsimpl.indexval").toString() + paramInt); 
      if (this.crsRes.getObject(paramInt) == null)
        throw new SQLException(this.resBundle.handleGetObject("syncrsimpl.noconflict").toString()); 
    } catch (SQLException sQLException) {
      throw new SQLException(sQLException.getMessage());
    } 
    try {
      boolean bool = true;
      if (this.crsSync.getObject(paramInt).toString().equals(paramObject.toString()) || this.crsRes.getObject(paramInt).toString().equals(paramObject.toString())) {
        this.crsRes.updateNull(paramInt);
        this.crsRes.updateRow();
        if (this.row.size() != 1)
          this.row = buildCachedRow(); 
        this.row.updateObject(paramInt, paramObject);
        this.row.updateRow();
        for (b = 1; b < this.crsRes.getMetaData().getColumnCount(); b++) {
          if (this.crsRes.getObject(b) != null) {
            bool = false;
            break;
          } 
        } 
        if (bool)
          try {
            writeData(this.row);
          } catch (SyncProviderException b) {
            SyncProviderException syncProviderException;
            throw new SQLException(this.resBundle.handleGetObject("syncrsimpl.syncnotpos").toString());
          }  
      } else {
        throw new SQLException(this.resBundle.handleGetObject("syncrsimpl.valtores").toString());
      } 
    } catch (SQLException sQLException) {
      throw new SQLException(sQLException.getMessage());
    } 
  }
  
  private void writeData(CachedRowSet paramCachedRowSet) throws SQLException { this.crw.updateResolvedConflictToDB(paramCachedRowSet, this.crw.getReader().connect(this.crsSync)); }
  
  private CachedRowSet buildCachedRow() throws SQLException {
    CachedRowSetImpl cachedRowSetImpl = new CachedRowSetImpl();
    RowSetMetaDataImpl rowSetMetaDataImpl1 = new RowSetMetaDataImpl();
    RowSetMetaDataImpl rowSetMetaDataImpl2 = (RowSetMetaDataImpl)this.crsSync.getMetaData();
    RowSetMetaDataImpl rowSetMetaDataImpl3 = new RowSetMetaDataImpl();
    int i = rowSetMetaDataImpl2.getColumnCount();
    rowSetMetaDataImpl3.setColumnCount(i);
    for (b = 1; b <= i; b++) {
      rowSetMetaDataImpl3.setColumnType(b, rowSetMetaDataImpl2.getColumnType(b));
      rowSetMetaDataImpl3.setColumnName(b, rowSetMetaDataImpl2.getColumnName(b));
      rowSetMetaDataImpl3.setNullable(b, 2);
      try {
        rowSetMetaDataImpl3.setCatalogName(b, rowSetMetaDataImpl2.getCatalogName(b));
        rowSetMetaDataImpl3.setSchemaName(b, rowSetMetaDataImpl2.getSchemaName(b));
      } catch (SQLException sQLException) {
        sQLException.printStackTrace();
      } 
    } 
    cachedRowSetImpl.setMetaData(rowSetMetaDataImpl3);
    cachedRowSetImpl.moveToInsertRow();
    for (b = 1; b <= this.crsSync.getMetaData().getColumnCount(); b++)
      cachedRowSetImpl.updateObject(b, this.crsSync.getObject(b)); 
    cachedRowSetImpl.insertRow();
    cachedRowSetImpl.moveToCurrentRow();
    cachedRowSetImpl.absolute(1);
    cachedRowSetImpl.setOriginalRow();
    try {
      cachedRowSetImpl.setUrl(this.crsSync.getUrl());
    } catch (SQLException b) {
      SQLException sQLException;
    } 
    try {
      cachedRowSetImpl.setDataSourceName(this.crsSync.getCommand());
    } catch (SQLException b) {
      SQLException sQLException;
    } 
    try {
      if (this.crsSync.getTableName() != null)
        cachedRowSetImpl.setTableName(this.crsSync.getTableName()); 
    } catch (SQLException b) {
      SQLException sQLException;
    } 
    try {
      if (this.crsSync.getCommand() != null)
        cachedRowSetImpl.setCommand(this.crsSync.getCommand()); 
    } catch (SQLException b) {
      SQLException sQLException;
    } 
    try {
      cachedRowSetImpl.setKeyColumns(this.crsSync.getKeyColumns());
    } catch (SQLException b) {
      SQLException sQLException;
    } 
    return cachedRowSetImpl;
  }
  
  public void setResolvedValue(String paramString, Object paramObject) throws SQLException {}
  
  void setCachedRowSet(CachedRowSet paramCachedRowSet) throws SQLException { this.crsSync = (CachedRowSetImpl)paramCachedRowSet; }
  
  void setCachedRowSetResolver(CachedRowSet paramCachedRowSet) throws SQLException {
    try {
      this.crsRes = (CachedRowSetImpl)paramCachedRowSet;
      this.crsRes.afterLast();
      this.sz = this.crsRes.size();
    } catch (SQLException sQLException) {}
  }
  
  void setStatus(ArrayList paramArrayList) { this.stats = paramArrayList; }
  
  void setCachedRowSetWriter(CachedRowSetWriter paramCachedRowSetWriter) { this.crw = paramCachedRowSetWriter; }
  
  public boolean nextConflict() throws SQLException {
    boolean bool = false;
    this.crsSync.setShowDeleted(true);
    while (this.crsSync.next()) {
      this.crsRes.previous();
      this.rowStatus++;
      if (this.rowStatus - 1 >= this.stats.size()) {
        bool = false;
        break;
      } 
      if (((Integer)this.stats.get(this.rowStatus - 1)).intValue() == 3)
        continue; 
      bool = true;
    } 
    this.crsSync.setShowDeleted(false);
    return bool;
  }
  
  public boolean previousConflict() throws SQLException { throw new UnsupportedOperationException(); }
  
  public void setCommand(String paramString) throws SQLException { throw new UnsupportedOperationException(); }
  
  public void populate(ResultSet paramResultSet) throws SQLException { throw new UnsupportedOperationException(); }
  
  public void execute(Connection paramConnection) throws SQLException { throw new UnsupportedOperationException(); }
  
  public void acceptChanges() throws SQLException { throw new UnsupportedOperationException(); }
  
  public void acceptChanges(Connection paramConnection) throws SQLException { throw new UnsupportedOperationException(); }
  
  public void restoreOriginal() throws SQLException { throw new UnsupportedOperationException(); }
  
  public void release() throws SQLException { throw new UnsupportedOperationException(); }
  
  public void undoDelete() throws SQLException { throw new UnsupportedOperationException(); }
  
  public void undoInsert() throws SQLException { throw new UnsupportedOperationException(); }
  
  public void undoUpdate() throws SQLException { throw new UnsupportedOperationException(); }
  
  public RowSet createShared() throws SQLException { throw new UnsupportedOperationException(); }
  
  protected Object clone() throws CloneNotSupportedException { throw new UnsupportedOperationException(); }
  
  public CachedRowSet createCopy() throws SQLException { throw new UnsupportedOperationException(); }
  
  public CachedRowSet createCopySchema() throws SQLException { throw new UnsupportedOperationException(); }
  
  public CachedRowSet createCopyNoConstraints() throws SQLException { throw new UnsupportedOperationException(); }
  
  public Collection toCollection() throws SQLException { throw new UnsupportedOperationException(); }
  
  public Collection toCollection(int paramInt) throws SQLException { throw new UnsupportedOperationException(); }
  
  public Collection toCollection(String paramString) throws SQLException { throw new UnsupportedOperationException(); }
  
  public SyncProvider getSyncProvider() throws SQLException { throw new UnsupportedOperationException(); }
  
  public void setSyncProvider(String paramString) throws SQLException { throw new UnsupportedOperationException(); }
  
  public void execute() throws SQLException { throw new UnsupportedOperationException(); }
  
  public boolean next() throws SQLException { throw new UnsupportedOperationException(); }
  
  protected boolean internalNext() throws SQLException { throw new UnsupportedOperationException(); }
  
  public void close() throws SQLException { throw new UnsupportedOperationException(); }
  
  public boolean wasNull() throws SQLException { throw new UnsupportedOperationException(); }
  
  protected BaseRow getCurrentRow() { throw new UnsupportedOperationException(); }
  
  protected void removeCurrentRow() throws SQLException { throw new UnsupportedOperationException(); }
  
  public String getString(int paramInt) throws SQLException { throw new UnsupportedOperationException(); }
  
  public boolean getBoolean(int paramInt) throws SQLException { throw new UnsupportedOperationException(); }
  
  public byte getByte(int paramInt) throws SQLException { throw new UnsupportedOperationException(); }
  
  public short getShort(int paramInt) throws SQLException { throw new UnsupportedOperationException(); }
  
  public int getInt(int paramInt) throws SQLException { throw new UnsupportedOperationException(); }
  
  public long getLong(int paramInt) throws SQLException { throw new UnsupportedOperationException(); }
  
  public float getFloat(int paramInt) throws SQLException { throw new UnsupportedOperationException(); }
  
  public double getDouble(int paramInt) throws SQLException { throw new UnsupportedOperationException(); }
  
  @Deprecated
  public BigDecimal getBigDecimal(int paramInt1, int paramInt2) throws SQLException { throw new UnsupportedOperationException(); }
  
  public byte[] getBytes(int paramInt) throws SQLException { throw new UnsupportedOperationException(); }
  
  public Date getDate(int paramInt) throws SQLException { throw new UnsupportedOperationException(); }
  
  public Time getTime(int paramInt) throws SQLException { throw new UnsupportedOperationException(); }
  
  public Timestamp getTimestamp(int paramInt) throws SQLException { throw new UnsupportedOperationException(); }
  
  public InputStream getAsciiStream(int paramInt) throws SQLException { throw new UnsupportedOperationException(); }
  
  @Deprecated
  public InputStream getUnicodeStream(int paramInt) throws SQLException { throw new UnsupportedOperationException(); }
  
  public InputStream getBinaryStream(int paramInt) throws SQLException { throw new UnsupportedOperationException(); }
  
  public String getString(String paramString) throws SQLException { throw new UnsupportedOperationException(); }
  
  public boolean getBoolean(String paramString) throws SQLException { throw new UnsupportedOperationException(); }
  
  public byte getByte(String paramString) throws SQLException { throw new UnsupportedOperationException(); }
  
  public short getShort(String paramString) throws SQLException { throw new UnsupportedOperationException(); }
  
  public int getInt(String paramString) throws SQLException { throw new UnsupportedOperationException(); }
  
  public long getLong(String paramString) throws SQLException { throw new UnsupportedOperationException(); }
  
  public float getFloat(String paramString) throws SQLException { throw new UnsupportedOperationException(); }
  
  public double getDouble(String paramString) throws SQLException { throw new UnsupportedOperationException(); }
  
  @Deprecated
  public BigDecimal getBigDecimal(String paramString, int paramInt) throws SQLException { throw new UnsupportedOperationException(); }
  
  public byte[] getBytes(String paramString) throws SQLException { throw new UnsupportedOperationException(); }
  
  public Date getDate(String paramString) throws SQLException { throw new UnsupportedOperationException(); }
  
  public Time getTime(String paramString) throws SQLException { throw new UnsupportedOperationException(); }
  
  public Timestamp getTimestamp(String paramString) throws SQLException { throw new UnsupportedOperationException(); }
  
  public InputStream getAsciiStream(String paramString) throws SQLException { throw new UnsupportedOperationException(); }
  
  @Deprecated
  public InputStream getUnicodeStream(String paramString) throws SQLException { throw new UnsupportedOperationException(); }
  
  public InputStream getBinaryStream(String paramString) throws SQLException { throw new UnsupportedOperationException(); }
  
  public SQLWarning getWarnings() { throw new UnsupportedOperationException(); }
  
  public void clearWarnings() throws SQLException { throw new UnsupportedOperationException(); }
  
  public String getCursorName() throws SQLException { throw new UnsupportedOperationException(); }
  
  public ResultSetMetaData getMetaData() throws SQLException { throw new UnsupportedOperationException(); }
  
  public Object getObject(int paramInt) throws SQLException { throw new UnsupportedOperationException(); }
  
  public Object getObject(String paramString) throws SQLException { throw new UnsupportedOperationException(); }
  
  public int findColumn(String paramString) throws SQLException { throw new UnsupportedOperationException(); }
  
  public Reader getCharacterStream(int paramInt) throws SQLException { throw new UnsupportedOperationException(); }
  
  public Reader getCharacterStream(String paramString) throws SQLException { throw new UnsupportedOperationException(); }
  
  public BigDecimal getBigDecimal(int paramInt) throws SQLException { throw new UnsupportedOperationException(); }
  
  public BigDecimal getBigDecimal(String paramString) throws SQLException { throw new UnsupportedOperationException(); }
  
  public int size() { throw new UnsupportedOperationException(); }
  
  public boolean isBeforeFirst() throws SQLException { throw new UnsupportedOperationException(); }
  
  public boolean isAfterLast() throws SQLException { throw new UnsupportedOperationException(); }
  
  public boolean isFirst() throws SQLException { throw new UnsupportedOperationException(); }
  
  public boolean isLast() throws SQLException { throw new UnsupportedOperationException(); }
  
  public void beforeFirst() throws SQLException { throw new UnsupportedOperationException(); }
  
  public void afterLast() throws SQLException { throw new UnsupportedOperationException(); }
  
  public boolean first() throws SQLException { throw new UnsupportedOperationException(); }
  
  protected boolean internalFirst() throws SQLException { throw new UnsupportedOperationException(); }
  
  public boolean last() throws SQLException { throw new UnsupportedOperationException(); }
  
  protected boolean internalLast() throws SQLException { throw new UnsupportedOperationException(); }
  
  public int getRow() { return this.crsSync.getRow(); }
  
  public boolean absolute(int paramInt) throws SQLException { throw new UnsupportedOperationException(); }
  
  public boolean relative(int paramInt) throws SQLException { throw new UnsupportedOperationException(); }
  
  public boolean previous() throws SQLException { throw new UnsupportedOperationException(); }
  
  protected boolean internalPrevious() throws SQLException { throw new UnsupportedOperationException(); }
  
  public boolean rowUpdated() throws SQLException { throw new UnsupportedOperationException(); }
  
  public boolean columnUpdated(int paramInt) throws SQLException { throw new UnsupportedOperationException(); }
  
  public boolean columnUpdated(String paramString) throws SQLException { throw new UnsupportedOperationException(); }
  
  public boolean rowInserted() throws SQLException { throw new UnsupportedOperationException(); }
  
  public boolean rowDeleted() throws SQLException { throw new UnsupportedOperationException(); }
  
  public void updateNull(int paramInt) throws SQLException { throw new UnsupportedOperationException(); }
  
  public void updateBoolean(int paramInt, boolean paramBoolean) throws SQLException { throw new UnsupportedOperationException(); }
  
  public void updateByte(int paramInt, byte paramByte) throws SQLException { throw new UnsupportedOperationException(); }
  
  public void updateShort(int paramInt, short paramShort) throws SQLException { throw new UnsupportedOperationException(); }
  
  public void updateInt(int paramInt1, int paramInt2) throws SQLException { throw new UnsupportedOperationException(); }
  
  public void updateLong(int paramInt, long paramLong) throws SQLException { throw new UnsupportedOperationException(); }
  
  public void updateFloat(int paramInt, float paramFloat) throws SQLException { throw new UnsupportedOperationException(); }
  
  public void updateDouble(int paramInt, double paramDouble) throws SQLException { throw new UnsupportedOperationException(); }
  
  public void updateBigDecimal(int paramInt, BigDecimal paramBigDecimal) throws SQLException { throw new UnsupportedOperationException(); }
  
  public void updateString(int paramInt, String paramString) throws SQLException { throw new UnsupportedOperationException(); }
  
  public void updateBytes(int paramInt, byte[] paramArrayOfByte) throws SQLException { throw new UnsupportedOperationException(); }
  
  public void updateDate(int paramInt, Date paramDate) throws SQLException { throw new UnsupportedOperationException(); }
  
  public void updateTime(int paramInt, Time paramTime) throws SQLException { throw new UnsupportedOperationException(); }
  
  public void updateTimestamp(int paramInt, Timestamp paramTimestamp) throws SQLException { throw new UnsupportedOperationException(); }
  
  public void updateAsciiStream(int paramInt1, InputStream paramInputStream, int paramInt2) throws SQLException { throw new UnsupportedOperationException(); }
  
  public void updateBinaryStream(int paramInt1, InputStream paramInputStream, int paramInt2) throws SQLException { throw new UnsupportedOperationException(); }
  
  public void updateCharacterStream(int paramInt1, Reader paramReader, int paramInt2) throws SQLException { throw new UnsupportedOperationException(); }
  
  public void updateObject(int paramInt1, Object paramObject, int paramInt2) throws SQLException { throw new UnsupportedOperationException(); }
  
  public void updateObject(int paramInt, Object paramObject) throws SQLException { throw new UnsupportedOperationException(); }
  
  public void updateNull(String paramString) throws SQLException { throw new UnsupportedOperationException(); }
  
  public void updateBoolean(String paramString, boolean paramBoolean) throws SQLException { throw new UnsupportedOperationException(); }
  
  public void updateByte(String paramString, byte paramByte) throws SQLException { throw new UnsupportedOperationException(); }
  
  public void updateShort(String paramString, short paramShort) throws SQLException { throw new UnsupportedOperationException(); }
  
  public void updateInt(String paramString, int paramInt) throws SQLException { throw new UnsupportedOperationException(); }
  
  public void updateLong(String paramString, long paramLong) throws SQLException { throw new UnsupportedOperationException(); }
  
  public void updateFloat(String paramString, float paramFloat) throws SQLException { throw new UnsupportedOperationException(); }
  
  public void updateDouble(String paramString, double paramDouble) throws SQLException { throw new UnsupportedOperationException(); }
  
  public void updateBigDecimal(String paramString, BigDecimal paramBigDecimal) throws SQLException { throw new UnsupportedOperationException(); }
  
  public void updateString(String paramString1, String paramString2) throws SQLException { throw new UnsupportedOperationException(); }
  
  public void updateBytes(String paramString, byte[] paramArrayOfByte) throws SQLException { throw new UnsupportedOperationException(); }
  
  public void updateDate(String paramString, Date paramDate) throws SQLException { throw new UnsupportedOperationException(); }
  
  public void updateTime(String paramString, Time paramTime) throws SQLException { throw new UnsupportedOperationException(); }
  
  public void updateTimestamp(String paramString, Timestamp paramTimestamp) throws SQLException { throw new UnsupportedOperationException(); }
  
  public void updateAsciiStream(String paramString, InputStream paramInputStream, int paramInt) throws SQLException { throw new UnsupportedOperationException(); }
  
  public void updateBinaryStream(String paramString, InputStream paramInputStream, int paramInt) throws SQLException { throw new UnsupportedOperationException(); }
  
  public void updateCharacterStream(String paramString, Reader paramReader, int paramInt) throws SQLException { throw new UnsupportedOperationException(); }
  
  public void updateObject(String paramString, Object paramObject, int paramInt) throws SQLException { throw new UnsupportedOperationException(); }
  
  public void updateObject(String paramString, Object paramObject) throws SQLException { throw new UnsupportedOperationException(); }
  
  public void insertRow() throws SQLException { throw new UnsupportedOperationException(); }
  
  public void updateRow() throws SQLException { throw new UnsupportedOperationException(); }
  
  public void deleteRow() throws SQLException { throw new UnsupportedOperationException(); }
  
  public void refreshRow() throws SQLException { throw new UnsupportedOperationException(); }
  
  public void cancelRowUpdates() throws SQLException { throw new UnsupportedOperationException(); }
  
  public void moveToInsertRow() throws SQLException { throw new UnsupportedOperationException(); }
  
  public void moveToCurrentRow() throws SQLException { throw new UnsupportedOperationException(); }
  
  public Statement getStatement() throws SQLException { throw new UnsupportedOperationException(); }
  
  public Object getObject(int paramInt, Map<String, Class<?>> paramMap) throws SQLException { throw new UnsupportedOperationException(); }
  
  public Ref getRef(int paramInt) throws SQLException { throw new UnsupportedOperationException(); }
  
  public Blob getBlob(int paramInt) throws SQLException { throw new UnsupportedOperationException(); }
  
  public Clob getClob(int paramInt) throws SQLException { throw new UnsupportedOperationException(); }
  
  public Array getArray(int paramInt) throws SQLException { throw new UnsupportedOperationException(); }
  
  public Object getObject(String paramString, Map<String, Class<?>> paramMap) throws SQLException { throw new UnsupportedOperationException(); }
  
  public Ref getRef(String paramString) throws SQLException { throw new UnsupportedOperationException(); }
  
  public Blob getBlob(String paramString) throws SQLException { throw new UnsupportedOperationException(); }
  
  public Clob getClob(String paramString) throws SQLException { throw new UnsupportedOperationException(); }
  
  public Array getArray(String paramString) throws SQLException { throw new UnsupportedOperationException(); }
  
  public Date getDate(int paramInt, Calendar paramCalendar) throws SQLException { throw new UnsupportedOperationException(); }
  
  public Date getDate(String paramString, Calendar paramCalendar) throws SQLException { throw new UnsupportedOperationException(); }
  
  public Time getTime(int paramInt, Calendar paramCalendar) throws SQLException { throw new UnsupportedOperationException(); }
  
  public Time getTime(String paramString, Calendar paramCalendar) throws SQLException { throw new UnsupportedOperationException(); }
  
  public Timestamp getTimestamp(int paramInt, Calendar paramCalendar) throws SQLException { throw new UnsupportedOperationException(); }
  
  public Timestamp getTimestamp(String paramString, Calendar paramCalendar) throws SQLException { throw new UnsupportedOperationException(); }
  
  public Connection getConnection() throws SQLException { throw new UnsupportedOperationException(); }
  
  public void setMetaData(RowSetMetaData paramRowSetMetaData) throws SQLException { throw new UnsupportedOperationException(); }
  
  public ResultSet getOriginal() throws SQLException { throw new UnsupportedOperationException(); }
  
  public ResultSet getOriginalRow() throws SQLException { throw new UnsupportedOperationException(); }
  
  public void setOriginalRow() throws SQLException { throw new UnsupportedOperationException(); }
  
  public void setOriginal() throws SQLException { throw new UnsupportedOperationException(); }
  
  public String getTableName() throws SQLException { throw new UnsupportedOperationException(); }
  
  public void setTableName(String paramString) throws SQLException { throw new UnsupportedOperationException(); }
  
  public int[] getKeyColumns() throws SQLException { throw new UnsupportedOperationException(); }
  
  public void setKeyColumns(int[] paramArrayOfInt) throws SQLException { throw new UnsupportedOperationException(); }
  
  public void updateRef(int paramInt, Ref paramRef) throws SQLException { throw new UnsupportedOperationException(); }
  
  public void updateRef(String paramString, Ref paramRef) throws SQLException { throw new UnsupportedOperationException(); }
  
  public void updateClob(int paramInt, Clob paramClob) throws SQLException { throw new UnsupportedOperationException(); }
  
  public void updateClob(String paramString, Clob paramClob) throws SQLException { throw new UnsupportedOperationException(); }
  
  public void updateBlob(int paramInt, Blob paramBlob) throws SQLException { throw new UnsupportedOperationException(); }
  
  public void updateBlob(String paramString, Blob paramBlob) throws SQLException { throw new UnsupportedOperationException(); }
  
  public void updateArray(int paramInt, Array paramArray) throws SQLException { throw new UnsupportedOperationException(); }
  
  public void updateArray(String paramString, Array paramArray) throws SQLException { throw new UnsupportedOperationException(); }
  
  public URL getURL(int paramInt) throws SQLException { throw new UnsupportedOperationException(); }
  
  public URL getURL(String paramString) throws SQLException { throw new UnsupportedOperationException(); }
  
  public RowSetWarning getRowSetWarnings() { throw new UnsupportedOperationException(); }
  
  public void commit() throws SQLException { throw new UnsupportedOperationException(); }
  
  public void rollback() throws SQLException { throw new UnsupportedOperationException(); }
  
  public void rollback(Savepoint paramSavepoint) throws SQLException { throw new UnsupportedOperationException(); }
  
  public void unsetMatchColumn(int[] paramArrayOfInt) throws SQLException { throw new UnsupportedOperationException(); }
  
  public void unsetMatchColumn(String[] paramArrayOfString) throws SQLException { throw new UnsupportedOperationException(); }
  
  public String[] getMatchColumnNames() throws SQLException { throw new UnsupportedOperationException(); }
  
  public int[] getMatchColumnIndexes() throws SQLException { throw new UnsupportedOperationException(); }
  
  public void setMatchColumn(int[] paramArrayOfInt) throws SQLException { throw new UnsupportedOperationException(); }
  
  public void setMatchColumn(String[] paramArrayOfString) throws SQLException { throw new UnsupportedOperationException(); }
  
  public void setMatchColumn(int paramInt) throws SQLException { throw new UnsupportedOperationException(); }
  
  public void setMatchColumn(String paramString) throws SQLException { throw new UnsupportedOperationException(); }
  
  public void unsetMatchColumn(int paramInt) throws SQLException { throw new UnsupportedOperationException(); }
  
  public void unsetMatchColumn(String paramString) throws SQLException { throw new UnsupportedOperationException(); }
  
  public void rowSetPopulated(RowSetEvent paramRowSetEvent, int paramInt) throws SQLException { throw new UnsupportedOperationException(); }
  
  public void populate(ResultSet paramResultSet, int paramInt) throws SQLException { throw new UnsupportedOperationException(); }
  
  public boolean nextPage() throws SQLException { throw new UnsupportedOperationException(); }
  
  public void setPageSize(int paramInt) throws SQLException { throw new UnsupportedOperationException(); }
  
  public int getPageSize() { throw new UnsupportedOperationException(); }
  
  public boolean previousPage() throws SQLException { throw new UnsupportedOperationException(); }
  
  public void updateNCharacterStream(int paramInt1, Reader paramReader, int paramInt2) throws SQLException { throw new UnsupportedOperationException("Operation not yet supported"); }
  
  public void updateNCharacterStream(String paramString, Reader paramReader, int paramInt) throws SQLException { throw new UnsupportedOperationException("Operation not yet supported"); }
  
  private void readObject(ObjectInputStream paramObjectInputStream) throws IOException, ClassNotFoundException {
    paramObjectInputStream.defaultReadObject();
    try {
      this.resBundle = JdbcRowSetResourceBundle.getJdbcRowSetResourceBundle();
    } catch (IOException iOException) {
      throw new RuntimeException(iOException);
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\rowset\internal\SyncResolverImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */