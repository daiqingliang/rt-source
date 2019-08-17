package com.sun.rowset;

import com.sun.rowset.internal.BaseRow;
import com.sun.rowset.internal.CachedRowSetReader;
import com.sun.rowset.internal.CachedRowSetWriter;
import com.sun.rowset.internal.InsertRow;
import com.sun.rowset.internal.Row;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.OptionalDataException;
import java.io.Reader;
import java.io.Serializable;
import java.io.StreamCorruptedException;
import java.io.StringBufferInputStream;
import java.io.StringReader;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URL;
import java.sql.Array;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.Date;
import java.sql.NClob;
import java.sql.Ref;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.RowId;
import java.sql.SQLData;
import java.sql.SQLException;
import java.sql.SQLFeatureNotSupportedException;
import java.sql.SQLWarning;
import java.sql.SQLXML;
import java.sql.Savepoint;
import java.sql.Statement;
import java.sql.Struct;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.MessageFormat;
import java.text.ParseException;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Collection;
import java.util.Date;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Map;
import java.util.TreeMap;
import java.util.Vector;
import javax.sql.RowSet;
import javax.sql.RowSetEvent;
import javax.sql.RowSetInternal;
import javax.sql.RowSetMetaData;
import javax.sql.RowSetReader;
import javax.sql.RowSetWriter;
import javax.sql.rowset.BaseRowSet;
import javax.sql.rowset.CachedRowSet;
import javax.sql.rowset.RowSetMetaDataImpl;
import javax.sql.rowset.RowSetWarning;
import javax.sql.rowset.serial.SQLInputImpl;
import javax.sql.rowset.serial.SerialArray;
import javax.sql.rowset.serial.SerialBlob;
import javax.sql.rowset.serial.SerialClob;
import javax.sql.rowset.serial.SerialRef;
import javax.sql.rowset.serial.SerialStruct;
import javax.sql.rowset.spi.SyncFactory;
import javax.sql.rowset.spi.SyncProvider;
import javax.sql.rowset.spi.SyncProviderException;
import javax.sql.rowset.spi.TransactionalWriter;
import sun.reflect.misc.ReflectUtil;

public class CachedRowSetImpl extends BaseRowSet implements RowSet, RowSetInternal, Serializable, Cloneable, CachedRowSet {
  private SyncProvider provider;
  
  private RowSetReader rowSetReader;
  
  private RowSetWriter rowSetWriter;
  
  private Connection conn;
  
  private ResultSetMetaData RSMD;
  
  private RowSetMetaDataImpl RowSetMD;
  
  private int[] keyCols;
  
  private String tableName;
  
  private Vector<Object> rvh;
  
  private int cursorPos;
  
  private int absolutePos;
  
  private int numDeleted;
  
  private int numRows;
  
  private InsertRow insertRow;
  
  private boolean onInsertRow;
  
  private int currentRow;
  
  private boolean lastValueNull;
  
  private SQLWarning sqlwarn;
  
  private String strMatchColumn = "";
  
  private int iMatchColumn = -1;
  
  private RowSetWarning rowsetWarning;
  
  private String DEFAULT_SYNC_PROVIDER = "com.sun.rowset.providers.RIOptimisticProvider";
  
  private boolean dbmslocatorsUpdateCopy;
  
  private ResultSet resultSet;
  
  private int endPos;
  
  private int prevEndPos;
  
  private int startPos;
  
  private int startPrev;
  
  private int pageSize;
  
  private int maxRowsreached;
  
  private boolean pagenotend = true;
  
  private boolean onFirstPage;
  
  private boolean onLastPage;
  
  private int populatecallcount;
  
  private int totalRows;
  
  private boolean callWithCon;
  
  private CachedRowSetReader crsReader;
  
  private Vector<Integer> iMatchColumns;
  
  private Vector<String> strMatchColumns;
  
  private boolean tXWriter = false;
  
  private TransactionalWriter tWriter = null;
  
  protected JdbcRowSetResourceBundle resBundle;
  
  private boolean updateOnInsert;
  
  static final long serialVersionUID = 1884577171200622428L;
  
  public CachedRowSetImpl() throws SQLException {
    try {
      this.resBundle = JdbcRowSetResourceBundle.getJdbcRowSetResourceBundle();
    } catch (IOException iOException) {
      throw new RuntimeException(iOException);
    } 
    this.provider = SyncFactory.getInstance(this.DEFAULT_SYNC_PROVIDER);
    if (!(this.provider instanceof com.sun.rowset.providers.RIOptimisticProvider))
      throw new SQLException(this.resBundle.handleGetObject("cachedrowsetimpl.invalidp").toString()); 
    this.rowSetReader = (CachedRowSetReader)this.provider.getRowSetReader();
    this.rowSetWriter = (CachedRowSetWriter)this.provider.getRowSetWriter();
    initParams();
    initContainer();
    initProperties();
    this.onInsertRow = false;
    this.insertRow = null;
    this.sqlwarn = new SQLWarning();
    this.rowsetWarning = new RowSetWarning();
  }
  
  public CachedRowSetImpl(Hashtable paramHashtable) throws SQLException {
    try {
      this.resBundle = JdbcRowSetResourceBundle.getJdbcRowSetResourceBundle();
    } catch (IOException iOException) {
      throw new RuntimeException(iOException);
    } 
    if (paramHashtable == null)
      throw new SQLException(this.resBundle.handleGetObject("cachedrowsetimpl.nullhash").toString()); 
    String str = (String)paramHashtable.get("rowset.provider.classname");
    this.provider = SyncFactory.getInstance(str);
    this.rowSetReader = this.provider.getRowSetReader();
    this.rowSetWriter = this.provider.getRowSetWriter();
    initParams();
    initContainer();
    initProperties();
  }
  
  private void initContainer() throws SQLException {
    this.rvh = new Vector(100);
    this.cursorPos = 0;
    this.absolutePos = 0;
    this.numRows = 0;
    this.numDeleted = 0;
  }
  
  private void initProperties() throws SQLException {
    if (this.resBundle == null)
      try {
        this.resBundle = JdbcRowSetResourceBundle.getJdbcRowSetResourceBundle();
      } catch (IOException iOException) {
        throw new RuntimeException(iOException);
      }  
    setShowDeleted(false);
    setQueryTimeout(0);
    setMaxRows(0);
    setMaxFieldSize(0);
    setType(1004);
    setConcurrency(1008);
    if (this.rvh.size() > 0 && !isReadOnly()) {
      setReadOnly(false);
    } else {
      setReadOnly(true);
    } 
    setTransactionIsolation(2);
    setEscapeProcessing(true);
    checkTransactionalWriter();
    this.iMatchColumns = new Vector(10);
    byte b;
    for (b = 0; b < 10; b++)
      this.iMatchColumns.add(b, Integer.valueOf(-1)); 
    this.strMatchColumns = new Vector(10);
    for (b = 0; b < 10; b++)
      this.strMatchColumns.add(b, null); 
  }
  
  private void checkTransactionalWriter() throws SQLException {
    if (this.rowSetWriter != null) {
      Class clazz = this.rowSetWriter.getClass();
      if (clazz != null) {
        Class[] arrayOfClass = clazz.getInterfaces();
        for (byte b = 0; b < arrayOfClass.length; b++) {
          if (arrayOfClass[b].getName().indexOf("TransactionalWriter") > 0) {
            this.tXWriter = true;
            establishTransactionalWriter();
          } 
        } 
      } 
    } 
  }
  
  private void establishTransactionalWriter() throws SQLException { this.tWriter = (TransactionalWriter)this.provider.getRowSetWriter(); }
  
  public void setCommand(String paramString) throws SQLException {
    super.setCommand(paramString);
    if (!buildTableName(paramString).equals(""))
      setTableName(buildTableName(paramString)); 
  }
  
  public void populate(ResultSet paramResultSet) throws SQLException {
    Map map = getTypeMap();
    if (paramResultSet == null)
      throw new SQLException(this.resBundle.handleGetObject("cachedrowsetimpl.populate").toString()); 
    this.resultSet = paramResultSet;
    this.RSMD = paramResultSet.getMetaData();
    this.RowSetMD = new RowSetMetaDataImpl();
    initMetaData(this.RowSetMD, this.RSMD);
    this.RSMD = null;
    int i = this.RowSetMD.getColumnCount();
    int j = getMaxRows();
    byte b = 0;
    Row row = null;
    while (paramResultSet.next()) {
      row = new Row(i);
      if (b > j && j > 0)
        this.rowsetWarning.setNextWarning(new RowSetWarning("Populating rows setting has exceeded max row setting")); 
      for (byte b1 = 1; b1 <= i; b1++) {
        Object object;
        if (map == null || map.isEmpty()) {
          object = paramResultSet.getObject(b1);
        } else {
          object = paramResultSet.getObject(b1, map);
        } 
        if (object instanceof Struct) {
          object = new SerialStruct((Struct)object, map);
        } else if (object instanceof SQLData) {
          object = new SerialStruct((SQLData)object, map);
        } else if (object instanceof Blob) {
          object = new SerialBlob((Blob)object);
        } else if (object instanceof Clob) {
          object = new SerialClob((Clob)object);
        } else if (object instanceof Array) {
          if (map != null) {
            object = new SerialArray((Array)object, map);
          } else {
            object = new SerialArray((Array)object);
          } 
        } 
        row.initColumnObject(b1, object);
      } 
      b++;
      this.rvh.add(row);
    } 
    this.numRows = b;
    notifyRowSetChanged();
  }
  
  private void initMetaData(RowSetMetaDataImpl paramRowSetMetaDataImpl, ResultSetMetaData paramResultSetMetaData) throws SQLException {
    int i = paramResultSetMetaData.getColumnCount();
    paramRowSetMetaDataImpl.setColumnCount(i);
    for (byte b = 1; b <= i; b++) {
      paramRowSetMetaDataImpl.setAutoIncrement(b, paramResultSetMetaData.isAutoIncrement(b));
      if (paramResultSetMetaData.isAutoIncrement(b))
        this.updateOnInsert = true; 
      paramRowSetMetaDataImpl.setCaseSensitive(b, paramResultSetMetaData.isCaseSensitive(b));
      paramRowSetMetaDataImpl.setCurrency(b, paramResultSetMetaData.isCurrency(b));
      paramRowSetMetaDataImpl.setNullable(b, paramResultSetMetaData.isNullable(b));
      paramRowSetMetaDataImpl.setSigned(b, paramResultSetMetaData.isSigned(b));
      paramRowSetMetaDataImpl.setSearchable(b, paramResultSetMetaData.isSearchable(b));
      int j = paramResultSetMetaData.getColumnDisplaySize(b);
      if (j < 0)
        j = 0; 
      paramRowSetMetaDataImpl.setColumnDisplaySize(b, j);
      paramRowSetMetaDataImpl.setColumnLabel(b, paramResultSetMetaData.getColumnLabel(b));
      paramRowSetMetaDataImpl.setColumnName(b, paramResultSetMetaData.getColumnName(b));
      paramRowSetMetaDataImpl.setSchemaName(b, paramResultSetMetaData.getSchemaName(b));
      int k = paramResultSetMetaData.getPrecision(b);
      if (k < 0)
        k = 0; 
      paramRowSetMetaDataImpl.setPrecision(b, k);
      int m = paramResultSetMetaData.getScale(b);
      if (m < 0)
        m = 0; 
      paramRowSetMetaDataImpl.setScale(b, m);
      paramRowSetMetaDataImpl.setTableName(b, paramResultSetMetaData.getTableName(b));
      paramRowSetMetaDataImpl.setCatalogName(b, paramResultSetMetaData.getCatalogName(b));
      paramRowSetMetaDataImpl.setColumnType(b, paramResultSetMetaData.getColumnType(b));
      paramRowSetMetaDataImpl.setColumnTypeName(b, paramResultSetMetaData.getColumnTypeName(b));
    } 
    if (this.conn != null)
      this.dbmslocatorsUpdateCopy = this.conn.getMetaData().locatorsUpdateCopy(); 
  }
  
  public void execute(Connection paramConnection) throws SQLException {
    setConnection(paramConnection);
    if (getPageSize() != 0) {
      this.crsReader = (CachedRowSetReader)this.provider.getRowSetReader();
      this.crsReader.setStartPosition(1);
      this.callWithCon = true;
      this.crsReader.readData(this);
    } else {
      this.rowSetReader.readData(this);
    } 
    this.RowSetMD = (RowSetMetaDataImpl)getMetaData();
    if (paramConnection != null)
      this.dbmslocatorsUpdateCopy = paramConnection.getMetaData().locatorsUpdateCopy(); 
  }
  
  private void setConnection(Connection paramConnection) throws SQLException { this.conn = paramConnection; }
  
  public void acceptChanges() throws SQLException {
    if (this.onInsertRow == true)
      throw new SyncProviderException(this.resBundle.handleGetObject("cachedrowsetimpl.invalidop").toString()); 
    int i = this.cursorPos;
    boolean bool = false;
    boolean bool1 = false;
    try {
      if (this.rowSetWriter != null) {
        i = this.cursorPos;
        bool1 = this.rowSetWriter.writeData(this);
        this.cursorPos = i;
      } 
      if (this.tXWriter)
        if (!bool1) {
          this.tWriter = (TransactionalWriter)this.rowSetWriter;
          this.tWriter.rollback();
          bool = false;
        } else {
          this.tWriter = (TransactionalWriter)this.rowSetWriter;
          if (this.tWriter instanceof CachedRowSetWriter) {
            ((CachedRowSetWriter)this.tWriter).commit(this, this.updateOnInsert);
          } else {
            this.tWriter.commit();
          } 
          bool = true;
        }  
      if (bool == true) {
        setOriginal();
      } else if (!bool) {
        throw new SyncProviderException(this.resBundle.handleGetObject("cachedrowsetimpl.accfailed").toString());
      } 
    } catch (SyncProviderException syncProviderException) {
      throw syncProviderException;
    } catch (SQLException sQLException) {
      sQLException.printStackTrace();
      throw new SyncProviderException(sQLException.getMessage());
    } catch (SecurityException securityException) {
      throw new SyncProviderException(securityException.getMessage());
    } 
  }
  
  public void acceptChanges(Connection paramConnection) throws SQLException {
    setConnection(paramConnection);
    acceptChanges();
  }
  
  public void restoreOriginal() throws SQLException {
    Iterator iterator = this.rvh.iterator();
    while (iterator.hasNext()) {
      Row row = (Row)iterator.next();
      if (row.getInserted() == true) {
        iterator.remove();
        this.numRows--;
        continue;
      } 
      if (row.getDeleted() == true)
        row.clearDeleted(); 
      if (row.getUpdated() == true)
        row.clearUpdated(); 
    } 
    this.cursorPos = 0;
    notifyRowSetChanged();
  }
  
  public void release() throws SQLException {
    initContainer();
    notifyRowSetChanged();
  }
  
  public void undoDelete() throws SQLException {
    if (!getShowDeleted())
      return; 
    checkCursor();
    if (this.onInsertRow == true)
      throw new SQLException(this.resBundle.handleGetObject("cachedrowsetimpl.invalidcp").toString()); 
    Row row = (Row)getCurrentRow();
    if (row.getDeleted() == true) {
      row.clearDeleted();
      this.numDeleted--;
      notifyRowChanged();
    } 
  }
  
  public void undoInsert() throws SQLException {
    checkCursor();
    if (this.onInsertRow == true)
      throw new SQLException(this.resBundle.handleGetObject("cachedrowsetimpl.invalidcp").toString()); 
    Row row = (Row)getCurrentRow();
    if (row.getInserted() == true) {
      this.rvh.remove(this.cursorPos - 1);
      this.numRows--;
      notifyRowChanged();
    } else {
      throw new SQLException(this.resBundle.handleGetObject("cachedrowsetimpl.illegalop").toString());
    } 
  }
  
  public void undoUpdate() throws SQLException {
    moveToCurrentRow();
    undoDelete();
    undoInsert();
  }
  
  public RowSet createShared() throws SQLException {
    RowSet rowSet;
    try {
      rowSet = (RowSet)clone();
    } catch (CloneNotSupportedException cloneNotSupportedException) {
      throw new SQLException(cloneNotSupportedException.getMessage());
    } 
    return rowSet;
  }
  
  protected Object clone() throws CloneNotSupportedException { return super.clone(); }
  
  public CachedRowSet createCopy() throws SQLException {
    ObjectInputStream objectInputStream;
    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream();
    try {
      ObjectOutputStream objectOutputStream = new ObjectOutputStream(byteArrayOutputStream);
      objectOutputStream.writeObject(this);
    } catch (IOException null) {
      throw new SQLException(MessageFormat.format(this.resBundle.handleGetObject("cachedrowsetimpl.clonefail").toString(), new Object[] { objectInputStream.getMessage() }));
    } 
    try {
      ByteArrayInputStream byteArrayInputStream = new ByteArrayInputStream(byteArrayOutputStream.toByteArray());
      objectInputStream = new ObjectInputStream(byteArrayInputStream);
    } catch (StreamCorruptedException streamCorruptedException) {
      throw new SQLException(MessageFormat.format(this.resBundle.handleGetObject("cachedrowsetimpl.clonefail").toString(), new Object[] { streamCorruptedException.getMessage() }));
    } catch (IOException iOException) {
      throw new SQLException(MessageFormat.format(this.resBundle.handleGetObject("cachedrowsetimpl.clonefail").toString(), new Object[] { iOException.getMessage() }));
    } 
    try {
      CachedRowSetImpl cachedRowSetImpl = (CachedRowSetImpl)objectInputStream.readObject();
      cachedRowSetImpl.resBundle = this.resBundle;
      return cachedRowSetImpl;
    } catch (ClassNotFoundException classNotFoundException) {
      throw new SQLException(MessageFormat.format(this.resBundle.handleGetObject("cachedrowsetimpl.clonefail").toString(), new Object[] { classNotFoundException.getMessage() }));
    } catch (OptionalDataException optionalDataException) {
      throw new SQLException(MessageFormat.format(this.resBundle.handleGetObject("cachedrowsetimpl.clonefail").toString(), new Object[] { optionalDataException.getMessage() }));
    } catch (IOException iOException) {
      throw new SQLException(MessageFormat.format(this.resBundle.handleGetObject("cachedrowsetimpl.clonefail").toString(), new Object[] { iOException.getMessage() }));
    } 
  }
  
  public CachedRowSet createCopySchema() throws SQLException {
    int i = this.numRows;
    this.numRows = 0;
    CachedRowSet cachedRowSet = createCopy();
    this.numRows = i;
    return cachedRowSet;
  }
  
  public CachedRowSet createCopyNoConstraints() throws SQLException {
    CachedRowSetImpl cachedRowSetImpl = (CachedRowSetImpl)createCopy();
    cachedRowSetImpl.initProperties();
    try {
      cachedRowSetImpl.unsetMatchColumn(cachedRowSetImpl.getMatchColumnIndexes());
    } catch (SQLException sQLException) {}
    try {
      cachedRowSetImpl.unsetMatchColumn(cachedRowSetImpl.getMatchColumnNames());
    } catch (SQLException sQLException) {}
    return cachedRowSetImpl;
  }
  
  public Collection<?> toCollection() throws SQLException {
    TreeMap treeMap = new TreeMap();
    for (byte b = 0; b < this.numRows; b++)
      treeMap.put(Integer.valueOf(b), this.rvh.get(b)); 
    return treeMap.values();
  }
  
  public Collection<?> toCollection(int paramInt) throws SQLException {
    int i = this.numRows;
    Vector vector = new Vector(i);
    CachedRowSetImpl cachedRowSetImpl = (CachedRowSetImpl)createCopy();
    while (i != 0) {
      cachedRowSetImpl.next();
      vector.add(cachedRowSetImpl.getObject(paramInt));
      i--;
    } 
    return vector;
  }
  
  public Collection<?> toCollection(String paramString) throws SQLException { return toCollection(getColIdxByName(paramString)); }
  
  public SyncProvider getSyncProvider() throws SQLException { return this.provider; }
  
  public void setSyncProvider(String paramString) throws SQLException {
    this.provider = SyncFactory.getInstance(paramString);
    this.rowSetReader = this.provider.getRowSetReader();
    this.rowSetWriter = this.provider.getRowSetWriter();
  }
  
  public void execute() throws SQLException { execute(null); }
  
  public boolean next() throws SQLException {
    if (this.cursorPos < 0 || this.cursorPos >= this.numRows + 1)
      throw new SQLException(this.resBundle.handleGetObject("cachedrowsetimpl.invalidcp").toString()); 
    boolean bool = internalNext();
    notifyCursorMoved();
    return bool;
  }
  
  protected boolean internalNext() throws SQLException {
    boolean bool = false;
    do {
      if (this.cursorPos < this.numRows) {
        this.cursorPos++;
        bool = true;
      } else if (this.cursorPos == this.numRows) {
        this.cursorPos++;
        bool = false;
        break;
      } 
    } while (!getShowDeleted() && rowDeleted() == true);
    if (bool == true) {
      this.absolutePos++;
    } else {
      this.absolutePos = 0;
    } 
    return bool;
  }
  
  public void close() throws SQLException {
    this.cursorPos = 0;
    this.absolutePos = 0;
    this.numRows = 0;
    this.numDeleted = 0;
    initProperties();
    this.rvh.clear();
  }
  
  public boolean wasNull() throws SQLException { return this.lastValueNull; }
  
  private void setLastValueNull(boolean paramBoolean) { this.lastValueNull = paramBoolean; }
  
  private void checkIndex(int paramInt) throws SQLException {
    if (paramInt < 1 || paramInt > this.RowSetMD.getColumnCount())
      throw new SQLException(this.resBundle.handleGetObject("cachedrowsetimpl.invalidcol").toString()); 
  }
  
  private void checkCursor() throws SQLException {
    if (isAfterLast() == true || isBeforeFirst() == true)
      throw new SQLException(this.resBundle.handleGetObject("cachedrowsetimpl.invalidcp").toString()); 
  }
  
  private int getColIdxByName(String paramString) throws SQLException {
    this.RowSetMD = (RowSetMetaDataImpl)getMetaData();
    int i = this.RowSetMD.getColumnCount();
    for (byte b = 1; b <= i; b++) {
      String str = this.RowSetMD.getColumnName(b);
      if (str != null && paramString.equalsIgnoreCase(str))
        return b; 
    } 
    throw new SQLException(this.resBundle.handleGetObject("cachedrowsetimpl.invalcolnm").toString());
  }
  
  protected BaseRow getCurrentRow() { return (this.onInsertRow == true) ? this.insertRow : (BaseRow)this.rvh.get(this.cursorPos - 1); }
  
  protected void removeCurrentRow() throws SQLException {
    ((Row)getCurrentRow()).setDeleted();
    this.rvh.remove(this.cursorPos - 1);
    this.numRows--;
  }
  
  public String getString(int paramInt) throws SQLException {
    checkIndex(paramInt);
    checkCursor();
    setLastValueNull(false);
    Object object = getCurrentRow().getColumnObject(paramInt);
    if (object == null) {
      setLastValueNull(true);
      return null;
    } 
    return object.toString();
  }
  
  public boolean getBoolean(int paramInt) throws SQLException {
    checkIndex(paramInt);
    checkCursor();
    setLastValueNull(false);
    Object object = getCurrentRow().getColumnObject(paramInt);
    if (object == null) {
      setLastValueNull(true);
      return false;
    } 
    if (object instanceof Boolean)
      return ((Boolean)object).booleanValue(); 
    try {
      return (Double.compare(Double.parseDouble(object.toString()), 0.0D) != 0);
    } catch (NumberFormatException numberFormatException) {
      throw new SQLException(MessageFormat.format(this.resBundle.handleGetObject("cachedrowsetimpl.boolfail").toString(), new Object[] { object.toString().trim(), Integer.valueOf(paramInt) }));
    } 
  }
  
  public byte getByte(int paramInt) throws SQLException {
    checkIndex(paramInt);
    checkCursor();
    setLastValueNull(false);
    Object object = getCurrentRow().getColumnObject(paramInt);
    if (object == null) {
      setLastValueNull(true);
      return 0;
    } 
    try {
      return Byte.valueOf(object.toString()).byteValue();
    } catch (NumberFormatException numberFormatException) {
      throw new SQLException(MessageFormat.format(this.resBundle.handleGetObject("cachedrowsetimpl.bytefail").toString(), new Object[] { object.toString().trim(), Integer.valueOf(paramInt) }));
    } 
  }
  
  public short getShort(int paramInt) throws SQLException {
    checkIndex(paramInt);
    checkCursor();
    setLastValueNull(false);
    Object object = getCurrentRow().getColumnObject(paramInt);
    if (object == null) {
      setLastValueNull(true);
      return 0;
    } 
    try {
      return Short.valueOf(object.toString().trim()).shortValue();
    } catch (NumberFormatException numberFormatException) {
      throw new SQLException(MessageFormat.format(this.resBundle.handleGetObject("cachedrowsetimpl.shortfail").toString(), new Object[] { object.toString().trim(), Integer.valueOf(paramInt) }));
    } 
  }
  
  public int getInt(int paramInt) throws SQLException {
    checkIndex(paramInt);
    checkCursor();
    setLastValueNull(false);
    Object object = getCurrentRow().getColumnObject(paramInt);
    if (object == null) {
      setLastValueNull(true);
      return 0;
    } 
    try {
      return Integer.valueOf(object.toString().trim()).intValue();
    } catch (NumberFormatException numberFormatException) {
      throw new SQLException(MessageFormat.format(this.resBundle.handleGetObject("cachedrowsetimpl.intfail").toString(), new Object[] { object.toString().trim(), Integer.valueOf(paramInt) }));
    } 
  }
  
  public long getLong(int paramInt) throws SQLException {
    checkIndex(paramInt);
    checkCursor();
    setLastValueNull(false);
    Object object = getCurrentRow().getColumnObject(paramInt);
    if (object == null) {
      setLastValueNull(true);
      return 0L;
    } 
    try {
      return Long.valueOf(object.toString().trim()).longValue();
    } catch (NumberFormatException numberFormatException) {
      throw new SQLException(MessageFormat.format(this.resBundle.handleGetObject("cachedrowsetimpl.longfail").toString(), new Object[] { object.toString().trim(), Integer.valueOf(paramInt) }));
    } 
  }
  
  public float getFloat(int paramInt) throws SQLException {
    checkIndex(paramInt);
    checkCursor();
    setLastValueNull(false);
    Object object = getCurrentRow().getColumnObject(paramInt);
    if (object == null) {
      setLastValueNull(true);
      return 0.0F;
    } 
    try {
      return (new Float(object.toString())).floatValue();
    } catch (NumberFormatException numberFormatException) {
      throw new SQLException(MessageFormat.format(this.resBundle.handleGetObject("cachedrowsetimpl.floatfail").toString(), new Object[] { object.toString().trim(), Integer.valueOf(paramInt) }));
    } 
  }
  
  public double getDouble(int paramInt) throws SQLException {
    checkIndex(paramInt);
    checkCursor();
    setLastValueNull(false);
    Object object = getCurrentRow().getColumnObject(paramInt);
    if (object == null) {
      setLastValueNull(true);
      return 0.0D;
    } 
    try {
      return (new Double(object.toString().trim())).doubleValue();
    } catch (NumberFormatException numberFormatException) {
      throw new SQLException(MessageFormat.format(this.resBundle.handleGetObject("cachedrowsetimpl.doublefail").toString(), new Object[] { object.toString().trim(), Integer.valueOf(paramInt) }));
    } 
  }
  
  @Deprecated
  public BigDecimal getBigDecimal(int paramInt1, int paramInt2) throws SQLException {
    checkIndex(paramInt1);
    checkCursor();
    setLastValueNull(false);
    Object object = getCurrentRow().getColumnObject(paramInt1);
    if (object == null) {
      setLastValueNull(true);
      return new BigDecimal(0);
    } 
    BigDecimal bigDecimal = getBigDecimal(paramInt1);
    return bigDecimal.setScale(paramInt2);
  }
  
  public byte[] getBytes(int paramInt) throws SQLException {
    checkIndex(paramInt);
    checkCursor();
    if (!isBinary(this.RowSetMD.getColumnType(paramInt)))
      throw new SQLException(this.resBundle.handleGetObject("cachedrowsetimpl.dtypemismt").toString()); 
    return (byte[])getCurrentRow().getColumnObject(paramInt);
  }
  
  public Date getDate(int paramInt) throws SQLException {
    checkIndex(paramInt);
    checkCursor();
    setLastValueNull(false);
    Object object = getCurrentRow().getColumnObject(paramInt);
    if (object == null) {
      setLastValueNull(true);
      return null;
    } 
    switch (this.RowSetMD.getColumnType(paramInt)) {
      case 91:
        l = ((Date)object).getTime();
        return new Date(l);
      case 93:
        l = ((Timestamp)object).getTime();
        return new Date(l);
      case -1:
      case 1:
      case 12:
        try {
          DateFormat dateFormat = DateFormat.getDateInstance();
          return (Date)dateFormat.parse(object.toString());
        } catch (ParseException l) {
          ParseException parseException;
          throw new SQLException(MessageFormat.format(this.resBundle.handleGetObject("cachedrowsetimpl.datefail").toString(), new Object[] { object.toString().trim(), Integer.valueOf(paramInt) }));
        } 
    } 
    throw new SQLException(MessageFormat.format(this.resBundle.handleGetObject("cachedrowsetimpl.datefail").toString(), new Object[] { object.toString().trim(), Integer.valueOf(paramInt) }));
  }
  
  public Time getTime(int paramInt) throws SQLException {
    checkIndex(paramInt);
    checkCursor();
    setLastValueNull(false);
    Object object = getCurrentRow().getColumnObject(paramInt);
    if (object == null) {
      setLastValueNull(true);
      return null;
    } 
    switch (this.RowSetMD.getColumnType(paramInt)) {
      case 92:
        return (Time)object;
      case 93:
        l = ((Timestamp)object).getTime();
        return new Time(l);
      case -1:
      case 1:
      case 12:
        try {
          DateFormat dateFormat = DateFormat.getTimeInstance();
          return (Time)dateFormat.parse(object.toString());
        } catch (ParseException l) {
          ParseException parseException;
          throw new SQLException(MessageFormat.format(this.resBundle.handleGetObject("cachedrowsetimpl.timefail").toString(), new Object[] { object.toString().trim(), Integer.valueOf(paramInt) }));
        } 
    } 
    throw new SQLException(MessageFormat.format(this.resBundle.handleGetObject("cachedrowsetimpl.timefail").toString(), new Object[] { object.toString().trim(), Integer.valueOf(paramInt) }));
  }
  
  public Timestamp getTimestamp(int paramInt) throws SQLException {
    checkIndex(paramInt);
    checkCursor();
    setLastValueNull(false);
    Object object = getCurrentRow().getColumnObject(paramInt);
    if (object == null) {
      setLastValueNull(true);
      return null;
    } 
    switch (this.RowSetMD.getColumnType(paramInt)) {
      case 93:
        return (Timestamp)object;
      case 92:
        l = ((Time)object).getTime();
        return new Timestamp(l);
      case 91:
        l = ((Date)object).getTime();
        return new Timestamp(l);
      case -1:
      case 1:
      case 12:
        try {
          DateFormat dateFormat = DateFormat.getTimeInstance();
          return (Timestamp)dateFormat.parse(object.toString());
        } catch (ParseException l) {
          ParseException parseException;
          throw new SQLException(MessageFormat.format(this.resBundle.handleGetObject("cachedrowsetimpl.timefail").toString(), new Object[] { object.toString().trim(), Integer.valueOf(paramInt) }));
        } 
    } 
    throw new SQLException(MessageFormat.format(this.resBundle.handleGetObject("cachedrowsetimpl.timefail").toString(), new Object[] { object.toString().trim(), Integer.valueOf(paramInt) }));
  }
  
  public InputStream getAsciiStream(int paramInt) throws SQLException {
    this.asciiStream = null;
    checkIndex(paramInt);
    checkCursor();
    Object object = getCurrentRow().getColumnObject(paramInt);
    if (object == null) {
      this.lastValueNull = true;
      return null;
    } 
    try {
      if (isString(this.RowSetMD.getColumnType(paramInt))) {
        this.asciiStream = new ByteArrayInputStream(((String)object).getBytes("ASCII"));
      } else {
        throw new SQLException(this.resBundle.handleGetObject("cachedrowsetimpl.dtypemismt").toString());
      } 
    } catch (UnsupportedEncodingException unsupportedEncodingException) {
      throw new SQLException(unsupportedEncodingException.getMessage());
    } 
    return this.asciiStream;
  }
  
  @Deprecated
  public InputStream getUnicodeStream(int paramInt) throws SQLException {
    this.unicodeStream = null;
    checkIndex(paramInt);
    checkCursor();
    if (!isBinary(this.RowSetMD.getColumnType(paramInt)) && !isString(this.RowSetMD.getColumnType(paramInt)))
      throw new SQLException(this.resBundle.handleGetObject("cachedrowsetimpl.dtypemismt").toString()); 
    Object object = getCurrentRow().getColumnObject(paramInt);
    if (object == null) {
      this.lastValueNull = true;
      return null;
    } 
    this.unicodeStream = new StringBufferInputStream(object.toString());
    return this.unicodeStream;
  }
  
  public InputStream getBinaryStream(int paramInt) throws SQLException {
    this.binaryStream = null;
    checkIndex(paramInt);
    checkCursor();
    if (!isBinary(this.RowSetMD.getColumnType(paramInt)))
      throw new SQLException(this.resBundle.handleGetObject("cachedrowsetimpl.dtypemismt").toString()); 
    Object object = getCurrentRow().getColumnObject(paramInt);
    if (object == null) {
      this.lastValueNull = true;
      return null;
    } 
    this.binaryStream = new ByteArrayInputStream((byte[])object);
    return this.binaryStream;
  }
  
  public String getString(String paramString) throws SQLException { return getString(getColIdxByName(paramString)); }
  
  public boolean getBoolean(String paramString) throws SQLException { return getBoolean(getColIdxByName(paramString)); }
  
  public byte getByte(String paramString) throws SQLException { return getByte(getColIdxByName(paramString)); }
  
  public short getShort(String paramString) throws SQLException { return getShort(getColIdxByName(paramString)); }
  
  public int getInt(String paramString) throws SQLException { return getInt(getColIdxByName(paramString)); }
  
  public long getLong(String paramString) throws SQLException { return getLong(getColIdxByName(paramString)); }
  
  public float getFloat(String paramString) throws SQLException { return getFloat(getColIdxByName(paramString)); }
  
  public double getDouble(String paramString) throws SQLException { return getDouble(getColIdxByName(paramString)); }
  
  @Deprecated
  public BigDecimal getBigDecimal(String paramString, int paramInt) throws SQLException { return getBigDecimal(getColIdxByName(paramString), paramInt); }
  
  public byte[] getBytes(String paramString) throws SQLException { return getBytes(getColIdxByName(paramString)); }
  
  public Date getDate(String paramString) throws SQLException { return getDate(getColIdxByName(paramString)); }
  
  public Time getTime(String paramString) throws SQLException { return getTime(getColIdxByName(paramString)); }
  
  public Timestamp getTimestamp(String paramString) throws SQLException { return getTimestamp(getColIdxByName(paramString)); }
  
  public InputStream getAsciiStream(String paramString) throws SQLException { return getAsciiStream(getColIdxByName(paramString)); }
  
  @Deprecated
  public InputStream getUnicodeStream(String paramString) throws SQLException { return getUnicodeStream(getColIdxByName(paramString)); }
  
  public InputStream getBinaryStream(String paramString) throws SQLException { return getBinaryStream(getColIdxByName(paramString)); }
  
  public SQLWarning getWarnings() { return this.sqlwarn; }
  
  public void clearWarnings() throws SQLException { this.sqlwarn = null; }
  
  public String getCursorName() throws SQLException { throw new SQLException(this.resBundle.handleGetObject("cachedrowsetimpl.posupdate").toString()); }
  
  public ResultSetMetaData getMetaData() throws SQLException { return this.RowSetMD; }
  
  public Object getObject(int paramInt) throws SQLException {
    checkIndex(paramInt);
    checkCursor();
    setLastValueNull(false);
    Object object = getCurrentRow().getColumnObject(paramInt);
    if (object == null) {
      setLastValueNull(true);
      return null;
    } 
    if (object instanceof Struct) {
      Struct struct = (Struct)object;
      Map map = getTypeMap();
      Class clazz = (Class)map.get(struct.getSQLTypeName());
      if (clazz != null) {
        SQLData sQLData = null;
        try {
          sQLData = (SQLData)ReflectUtil.newInstance(clazz);
        } catch (Exception exception) {
          throw new SQLException("Unable to Instantiate: ", exception);
        } 
        Object[] arrayOfObject = struct.getAttributes(map);
        SQLInputImpl sQLInputImpl = new SQLInputImpl(arrayOfObject, map);
        sQLData.readSQL(sQLInputImpl, struct.getSQLTypeName());
        return sQLData;
      } 
    } 
    return object;
  }
  
  public Object getObject(String paramString) throws SQLException { return getObject(getColIdxByName(paramString)); }
  
  public int findColumn(String paramString) throws SQLException { return getColIdxByName(paramString); }
  
  public Reader getCharacterStream(int paramInt) throws SQLException {
    checkIndex(paramInt);
    checkCursor();
    if (isBinary(this.RowSetMD.getColumnType(paramInt))) {
      Object object = getCurrentRow().getColumnObject(paramInt);
      if (object == null) {
        this.lastValueNull = true;
        return null;
      } 
      this.charStream = new InputStreamReader(new ByteArrayInputStream((byte[])object));
    } else if (isString(this.RowSetMD.getColumnType(paramInt))) {
      Object object = getCurrentRow().getColumnObject(paramInt);
      if (object == null) {
        this.lastValueNull = true;
        return null;
      } 
      this.charStream = new StringReader(object.toString());
    } else {
      throw new SQLException(this.resBundle.handleGetObject("cachedrowsetimpl.dtypemismt").toString());
    } 
    return this.charStream;
  }
  
  public Reader getCharacterStream(String paramString) throws SQLException { return getCharacterStream(getColIdxByName(paramString)); }
  
  public BigDecimal getBigDecimal(int paramInt) throws SQLException {
    checkIndex(paramInt);
    checkCursor();
    setLastValueNull(false);
    Object object = getCurrentRow().getColumnObject(paramInt);
    if (object == null) {
      setLastValueNull(true);
      return null;
    } 
    try {
      return new BigDecimal(object.toString().trim());
    } catch (NumberFormatException numberFormatException) {
      throw new SQLException(MessageFormat.format(this.resBundle.handleGetObject("cachedrowsetimpl.doublefail").toString(), new Object[] { object.toString().trim(), Integer.valueOf(paramInt) }));
    } 
  }
  
  public BigDecimal getBigDecimal(String paramString) throws SQLException { return getBigDecimal(getColIdxByName(paramString)); }
  
  public int size() { return this.numRows; }
  
  public boolean isBeforeFirst() throws SQLException { return (this.cursorPos == 0 && this.numRows > 0); }
  
  public boolean isAfterLast() throws SQLException { return (this.cursorPos == this.numRows + 1 && this.numRows > 0); }
  
  public boolean isFirst() throws SQLException {
    int i = this.cursorPos;
    int j = this.absolutePos;
    internalFirst();
    if (this.cursorPos == i)
      return true; 
    this.cursorPos = i;
    this.absolutePos = j;
    return false;
  }
  
  public boolean isLast() throws SQLException {
    int i = this.cursorPos;
    int j = this.absolutePos;
    boolean bool = getShowDeleted();
    setShowDeleted(true);
    internalLast();
    if (this.cursorPos == i) {
      setShowDeleted(bool);
      return true;
    } 
    setShowDeleted(bool);
    this.cursorPos = i;
    this.absolutePos = j;
    return false;
  }
  
  public void beforeFirst() throws SQLException {
    if (getType() == 1003)
      throw new SQLException(this.resBundle.handleGetObject("cachedrowsetimpl.beforefirst").toString()); 
    this.cursorPos = 0;
    this.absolutePos = 0;
    notifyCursorMoved();
  }
  
  public void afterLast() throws SQLException {
    if (this.numRows > 0) {
      this.cursorPos = this.numRows + 1;
      this.absolutePos = 0;
      notifyCursorMoved();
    } 
  }
  
  public boolean first() throws SQLException {
    if (getType() == 1003)
      throw new SQLException(this.resBundle.handleGetObject("cachedrowsetimpl.first").toString()); 
    boolean bool = internalFirst();
    notifyCursorMoved();
    return bool;
  }
  
  protected boolean internalFirst() throws SQLException {
    boolean bool = false;
    if (this.numRows > 0) {
      this.cursorPos = 1;
      if (!getShowDeleted() && rowDeleted() == true) {
        bool = internalNext();
      } else {
        bool = true;
      } 
    } 
    if (bool == true) {
      this.absolutePos = 1;
    } else {
      this.absolutePos = 0;
    } 
    return bool;
  }
  
  public boolean last() throws SQLException {
    if (getType() == 1003)
      throw new SQLException(this.resBundle.handleGetObject("cachedrowsetimpl.last").toString()); 
    boolean bool = internalLast();
    notifyCursorMoved();
    return bool;
  }
  
  protected boolean internalLast() throws SQLException {
    boolean bool = false;
    if (this.numRows > 0) {
      this.cursorPos = this.numRows;
      if (!getShowDeleted() && rowDeleted() == true) {
        bool = internalPrevious();
      } else {
        bool = true;
      } 
    } 
    if (bool == true) {
      this.absolutePos = this.numRows - this.numDeleted;
    } else {
      this.absolutePos = 0;
    } 
    return bool;
  }
  
  public int getRow() { return (this.numRows > 0 && this.cursorPos > 0 && this.cursorPos < this.numRows + 1 && !getShowDeleted() && !rowDeleted()) ? this.absolutePos : ((getShowDeleted() == true) ? this.cursorPos : 0); }
  
  public boolean absolute(int paramInt) throws SQLException {
    if (paramInt == 0 || getType() == 1003)
      throw new SQLException(this.resBundle.handleGetObject("cachedrowsetimpl.absolute").toString()); 
    if (paramInt > 0) {
      if (paramInt > this.numRows) {
        afterLast();
        return false;
      } 
      if (this.absolutePos <= 0)
        internalFirst(); 
    } else {
      if (this.cursorPos + paramInt < 0) {
        beforeFirst();
        return false;
      } 
      if (this.absolutePos >= 0)
        internalLast(); 
    } 
    do {
    
    } while (this.absolutePos != paramInt && ((this.absolutePos < paramInt) ? !internalNext() : !internalPrevious()));
    notifyCursorMoved();
    return !(isAfterLast() || isBeforeFirst());
  }
  
  public boolean relative(int paramInt) throws SQLException {
    if (this.numRows == 0 || isBeforeFirst() || isAfterLast() || getType() == 1003)
      throw new SQLException(this.resBundle.handleGetObject("cachedrowsetimpl.relative").toString()); 
    if (paramInt == 0)
      return true; 
    if (paramInt > 0) {
      if (this.cursorPos + paramInt > this.numRows) {
        afterLast();
      } else {
        for (byte b = 0; b < paramInt && internalNext(); b++);
      } 
    } else if (this.cursorPos + paramInt < 0) {
      beforeFirst();
    } else {
      for (int i = paramInt; i < 0 && internalPrevious(); i++);
    } 
    notifyCursorMoved();
    return !(isAfterLast() || isBeforeFirst());
  }
  
  public boolean previous() throws SQLException {
    if (getType() == 1003)
      throw new SQLException(this.resBundle.handleGetObject("cachedrowsetimpl.last").toString()); 
    if (this.cursorPos < 0 || this.cursorPos > this.numRows + 1)
      throw new SQLException(this.resBundle.handleGetObject("cachedrowsetimpl.invalidcp").toString()); 
    boolean bool = internalPrevious();
    notifyCursorMoved();
    return bool;
  }
  
  protected boolean internalPrevious() throws SQLException {
    boolean bool = false;
    do {
      if (this.cursorPos > 1) {
        this.cursorPos--;
        bool = true;
      } else if (this.cursorPos == 1) {
        this.cursorPos--;
        bool = false;
        break;
      } 
    } while (!getShowDeleted() && rowDeleted() == true);
    if (bool == true) {
      this.absolutePos--;
    } else {
      this.absolutePos = 0;
    } 
    return bool;
  }
  
  public boolean rowUpdated() throws SQLException {
    checkCursor();
    if (this.onInsertRow == true)
      throw new SQLException(this.resBundle.handleGetObject("cachedrowsetimpl.invalidop").toString()); 
    return ((Row)getCurrentRow()).getUpdated();
  }
  
  public boolean columnUpdated(int paramInt) throws SQLException {
    checkCursor();
    if (this.onInsertRow == true)
      throw new SQLException(this.resBundle.handleGetObject("cachedrowsetimpl.invalidop").toString()); 
    return ((Row)getCurrentRow()).getColUpdated(paramInt - 1);
  }
  
  public boolean columnUpdated(String paramString) throws SQLException { return columnUpdated(getColIdxByName(paramString)); }
  
  public boolean rowInserted() throws SQLException {
    checkCursor();
    if (this.onInsertRow == true)
      throw new SQLException(this.resBundle.handleGetObject("cachedrowsetimpl.invalidop").toString()); 
    return ((Row)getCurrentRow()).getInserted();
  }
  
  public boolean rowDeleted() throws SQLException {
    if (isAfterLast() == true || isBeforeFirst() == true || this.onInsertRow == true)
      throw new SQLException(this.resBundle.handleGetObject("cachedrowsetimpl.invalidcp").toString()); 
    return ((Row)getCurrentRow()).getDeleted();
  }
  
  private boolean isNumeric(int paramInt) throws SQLException {
    switch (paramInt) {
      case -7:
      case -6:
      case -5:
      case 2:
      case 3:
      case 4:
      case 5:
      case 6:
      case 7:
      case 8:
        return true;
    } 
    return false;
  }
  
  private boolean isString(int paramInt) throws SQLException {
    switch (paramInt) {
      case -1:
      case 1:
      case 12:
        return true;
    } 
    return false;
  }
  
  private boolean isBinary(int paramInt) throws SQLException {
    switch (paramInt) {
      case -4:
      case -3:
      case -2:
        return true;
    } 
    return false;
  }
  
  private boolean isTemporal(int paramInt) throws SQLException {
    switch (paramInt) {
      case 91:
      case 92:
      case 93:
        return true;
    } 
    return false;
  }
  
  private boolean isBoolean(int paramInt) throws SQLException {
    switch (paramInt) {
      case -7:
      case 16:
        return true;
    } 
    return false;
  }
  
  private Object convertNumeric(Object paramObject, int paramInt1, int paramInt2) throws SQLException {
    if (paramInt1 == paramInt2)
      return paramObject; 
    if (!isNumeric(paramInt2) && !isString(paramInt2))
      throw new SQLException(this.resBundle.handleGetObject("cachedrowsetimpl.dtypemismt").toString() + paramInt2); 
    try {
      Integer integer;
      switch (paramInt2) {
        case -7:
          return integer.equals((integer = Integer.valueOf(paramObject.toString().trim())).valueOf(0)) ? Boolean.valueOf(false) : Boolean.valueOf(true);
        case -6:
          return Byte.valueOf(paramObject.toString().trim());
        case 5:
          return Short.valueOf(paramObject.toString().trim());
        case 4:
          return Integer.valueOf(paramObject.toString().trim());
        case -5:
          return Long.valueOf(paramObject.toString().trim());
        case 2:
        case 3:
          return new BigDecimal(paramObject.toString().trim());
        case 6:
        case 7:
          return new Float(paramObject.toString().trim());
        case 8:
          return new Double(paramObject.toString().trim());
        case -1:
        case 1:
        case 12:
          return paramObject.toString();
      } 
      throw new SQLException(this.resBundle.handleGetObject("cachedrowsetimpl.dtypemismt").toString() + paramInt2);
    } catch (NumberFormatException numberFormatException) {
      throw new SQLException(this.resBundle.handleGetObject("cachedrowsetimpl.dtypemismt").toString() + paramInt2);
    } 
  }
  
  private Object convertTemporal(Object paramObject, int paramInt1, int paramInt2) throws SQLException {
    if (paramInt1 == paramInt2)
      return paramObject; 
    if (isNumeric(paramInt2) == true || (!isString(paramInt2) && !isTemporal(paramInt2)))
      throw new SQLException(this.resBundle.handleGetObject("cachedrowsetimpl.dtypemismt").toString()); 
    try {
      switch (paramInt2) {
        case 91:
          if (paramInt1 == 93)
            return new Date(((Timestamp)paramObject).getTime()); 
          throw new SQLException(this.resBundle.handleGetObject("cachedrowsetimpl.dtypemismt").toString());
        case 93:
          return (paramInt1 == 92) ? new Timestamp(((Time)paramObject).getTime()) : new Timestamp(((Date)paramObject).getTime());
        case 92:
          if (paramInt1 == 93)
            return new Time(((Timestamp)paramObject).getTime()); 
          throw new SQLException(this.resBundle.handleGetObject("cachedrowsetimpl.dtypemismt").toString());
        case -1:
        case 1:
        case 12:
          return paramObject.toString();
      } 
      throw new SQLException(this.resBundle.handleGetObject("cachedrowsetimpl.dtypemismt").toString());
    } catch (NumberFormatException numberFormatException) {
      throw new SQLException(this.resBundle.handleGetObject("cachedrowsetimpl.dtypemismt").toString());
    } 
  }
  
  private Object convertBoolean(Object paramObject, int paramInt1, int paramInt2) throws SQLException {
    if (paramInt1 == paramInt2)
      return paramObject; 
    if (isNumeric(paramInt2) == true || (!isString(paramInt2) && !isBoolean(paramInt2)))
      throw new SQLException(this.resBundle.handleGetObject("cachedrowsetimpl.dtypemismt").toString()); 
    try {
      Integer integer;
      switch (paramInt2) {
        case -7:
          return integer.equals((integer = Integer.valueOf(paramObject.toString().trim())).valueOf(0)) ? Boolean.valueOf(false) : Boolean.valueOf(true);
        case 16:
          return Boolean.valueOf(paramObject.toString().trim());
      } 
      throw new SQLException(this.resBundle.handleGetObject("cachedrowsetimpl.dtypemismt").toString() + paramInt2);
    } catch (NumberFormatException numberFormatException) {
      throw new SQLException(this.resBundle.handleGetObject("cachedrowsetimpl.dtypemismt").toString() + paramInt2);
    } 
  }
  
  public void updateNull(int paramInt) throws SQLException {
    checkIndex(paramInt);
    checkCursor();
    BaseRow baseRow = getCurrentRow();
    baseRow.setColumnObject(paramInt, null);
  }
  
  public void updateBoolean(int paramInt, boolean paramBoolean) throws SQLException {
    checkIndex(paramInt);
    checkCursor();
    Object object = convertBoolean(Boolean.valueOf(paramBoolean), -7, this.RowSetMD.getColumnType(paramInt));
    getCurrentRow().setColumnObject(paramInt, object);
  }
  
  public void updateByte(int paramInt, byte paramByte) throws SQLException {
    checkIndex(paramInt);
    checkCursor();
    Object object = convertNumeric(Byte.valueOf(paramByte), -6, this.RowSetMD.getColumnType(paramInt));
    getCurrentRow().setColumnObject(paramInt, object);
  }
  
  public void updateShort(int paramInt, short paramShort) throws SQLException {
    checkIndex(paramInt);
    checkCursor();
    Object object = convertNumeric(Short.valueOf(paramShort), 5, this.RowSetMD.getColumnType(paramInt));
    getCurrentRow().setColumnObject(paramInt, object);
  }
  
  public void updateInt(int paramInt1, int paramInt2) throws SQLException {
    checkIndex(paramInt1);
    checkCursor();
    Object object = convertNumeric(Integer.valueOf(paramInt2), 4, this.RowSetMD.getColumnType(paramInt1));
    getCurrentRow().setColumnObject(paramInt1, object);
  }
  
  public void updateLong(int paramInt, long paramLong) throws SQLException {
    checkIndex(paramInt);
    checkCursor();
    Object object = convertNumeric(Long.valueOf(paramLong), -5, this.RowSetMD.getColumnType(paramInt));
    getCurrentRow().setColumnObject(paramInt, object);
  }
  
  public void updateFloat(int paramInt, float paramFloat) throws SQLException {
    checkIndex(paramInt);
    checkCursor();
    Object object = convertNumeric(Float.valueOf(paramFloat), 7, this.RowSetMD.getColumnType(paramInt));
    getCurrentRow().setColumnObject(paramInt, object);
  }
  
  public void updateDouble(int paramInt, double paramDouble) throws SQLException {
    checkIndex(paramInt);
    checkCursor();
    Object object = convertNumeric(Double.valueOf(paramDouble), 8, this.RowSetMD.getColumnType(paramInt));
    getCurrentRow().setColumnObject(paramInt, object);
  }
  
  public void updateBigDecimal(int paramInt, BigDecimal paramBigDecimal) throws SQLException {
    checkIndex(paramInt);
    checkCursor();
    Object object = convertNumeric(paramBigDecimal, 2, this.RowSetMD.getColumnType(paramInt));
    getCurrentRow().setColumnObject(paramInt, object);
  }
  
  public void updateString(int paramInt, String paramString) throws SQLException {
    checkIndex(paramInt);
    checkCursor();
    getCurrentRow().setColumnObject(paramInt, paramString);
  }
  
  public void updateBytes(int paramInt, byte[] paramArrayOfByte) throws SQLException {
    checkIndex(paramInt);
    checkCursor();
    if (!isBinary(this.RowSetMD.getColumnType(paramInt)))
      throw new SQLException(this.resBundle.handleGetObject("cachedrowsetimpl.dtypemismt").toString()); 
    getCurrentRow().setColumnObject(paramInt, paramArrayOfByte);
  }
  
  public void updateDate(int paramInt, Date paramDate) throws SQLException {
    checkIndex(paramInt);
    checkCursor();
    Object object = convertTemporal(paramDate, 91, this.RowSetMD.getColumnType(paramInt));
    getCurrentRow().setColumnObject(paramInt, object);
  }
  
  public void updateTime(int paramInt, Time paramTime) throws SQLException {
    checkIndex(paramInt);
    checkCursor();
    Object object = convertTemporal(paramTime, 92, this.RowSetMD.getColumnType(paramInt));
    getCurrentRow().setColumnObject(paramInt, object);
  }
  
  public void updateTimestamp(int paramInt, Timestamp paramTimestamp) throws SQLException {
    checkIndex(paramInt);
    checkCursor();
    Object object = convertTemporal(paramTimestamp, 93, this.RowSetMD.getColumnType(paramInt));
    getCurrentRow().setColumnObject(paramInt, object);
  }
  
  public void updateAsciiStream(int paramInt1, InputStream paramInputStream, int paramInt2) throws SQLException {
    checkIndex(paramInt1);
    checkCursor();
    if (!isString(this.RowSetMD.getColumnType(paramInt1)) && !isBinary(this.RowSetMD.getColumnType(paramInt1)))
      throw new SQLException(this.resBundle.handleGetObject("cachedrowsetimpl.dtypemismt").toString()); 
    byte[] arrayOfByte = new byte[paramInt2];
    try {
      int i = 0;
      do {
        i += paramInputStream.read(arrayOfByte, i, paramInt2 - i);
      } while (i != paramInt2);
    } catch (IOException iOException) {
      throw new SQLException(this.resBundle.handleGetObject("cachedrowsetimpl.asciistream").toString());
    } 
    String str = new String(arrayOfByte);
    getCurrentRow().setColumnObject(paramInt1, str);
  }
  
  public void updateBinaryStream(int paramInt1, InputStream paramInputStream, int paramInt2) throws SQLException {
    checkIndex(paramInt1);
    checkCursor();
    if (!isBinary(this.RowSetMD.getColumnType(paramInt1)))
      throw new SQLException(this.resBundle.handleGetObject("cachedrowsetimpl.dtypemismt").toString()); 
    byte[] arrayOfByte = new byte[paramInt2];
    try {
      int i = 0;
      do {
        i += paramInputStream.read(arrayOfByte, i, paramInt2 - i);
      } while (i != -1);
    } catch (IOException iOException) {
      throw new SQLException(this.resBundle.handleGetObject("cachedrowsetimpl.binstream").toString());
    } 
    getCurrentRow().setColumnObject(paramInt1, arrayOfByte);
  }
  
  public void updateCharacterStream(int paramInt1, Reader paramReader, int paramInt2) throws SQLException {
    checkIndex(paramInt1);
    checkCursor();
    if (!isString(this.RowSetMD.getColumnType(paramInt1)) && !isBinary(this.RowSetMD.getColumnType(paramInt1)))
      throw new SQLException(this.resBundle.handleGetObject("cachedrowsetimpl.dtypemismt").toString()); 
    char[] arrayOfChar = new char[paramInt2];
    try {
      int i = 0;
      do {
        i += paramReader.read(arrayOfChar, i, paramInt2 - i);
      } while (i != paramInt2);
    } catch (IOException iOException) {
      throw new SQLException(this.resBundle.handleGetObject("cachedrowsetimpl.binstream").toString());
    } 
    String str = new String(arrayOfChar);
    getCurrentRow().setColumnObject(paramInt1, str);
  }
  
  public void updateObject(int paramInt1, Object paramObject, int paramInt2) throws SQLException {
    checkIndex(paramInt1);
    checkCursor();
    int i = this.RowSetMD.getColumnType(paramInt1);
    if (i == 3 || i == 2)
      ((BigDecimal)paramObject).setScale(paramInt2); 
    getCurrentRow().setColumnObject(paramInt1, paramObject);
  }
  
  public void updateObject(int paramInt, Object paramObject) throws SQLException {
    checkIndex(paramInt);
    checkCursor();
    getCurrentRow().setColumnObject(paramInt, paramObject);
  }
  
  public void updateNull(String paramString) throws SQLException { updateNull(getColIdxByName(paramString)); }
  
  public void updateBoolean(String paramString, boolean paramBoolean) throws SQLException { updateBoolean(getColIdxByName(paramString), paramBoolean); }
  
  public void updateByte(String paramString, byte paramByte) throws SQLException { updateByte(getColIdxByName(paramString), paramByte); }
  
  public void updateShort(String paramString, short paramShort) throws SQLException { updateShort(getColIdxByName(paramString), paramShort); }
  
  public void updateInt(String paramString, int paramInt) throws SQLException { updateInt(getColIdxByName(paramString), paramInt); }
  
  public void updateLong(String paramString, long paramLong) throws SQLException { updateLong(getColIdxByName(paramString), paramLong); }
  
  public void updateFloat(String paramString, float paramFloat) throws SQLException { updateFloat(getColIdxByName(paramString), paramFloat); }
  
  public void updateDouble(String paramString, double paramDouble) throws SQLException { updateDouble(getColIdxByName(paramString), paramDouble); }
  
  public void updateBigDecimal(String paramString, BigDecimal paramBigDecimal) throws SQLException { updateBigDecimal(getColIdxByName(paramString), paramBigDecimal); }
  
  public void updateString(String paramString1, String paramString2) throws SQLException { updateString(getColIdxByName(paramString1), paramString2); }
  
  public void updateBytes(String paramString, byte[] paramArrayOfByte) throws SQLException { updateBytes(getColIdxByName(paramString), paramArrayOfByte); }
  
  public void updateDate(String paramString, Date paramDate) throws SQLException { updateDate(getColIdxByName(paramString), paramDate); }
  
  public void updateTime(String paramString, Time paramTime) throws SQLException { updateTime(getColIdxByName(paramString), paramTime); }
  
  public void updateTimestamp(String paramString, Timestamp paramTimestamp) throws SQLException { updateTimestamp(getColIdxByName(paramString), paramTimestamp); }
  
  public void updateAsciiStream(String paramString, InputStream paramInputStream, int paramInt) throws SQLException { updateAsciiStream(getColIdxByName(paramString), paramInputStream, paramInt); }
  
  public void updateBinaryStream(String paramString, InputStream paramInputStream, int paramInt) throws SQLException { updateBinaryStream(getColIdxByName(paramString), paramInputStream, paramInt); }
  
  public void updateCharacterStream(String paramString, Reader paramReader, int paramInt) throws SQLException { updateCharacterStream(getColIdxByName(paramString), paramReader, paramInt); }
  
  public void updateObject(String paramString, Object paramObject, int paramInt) throws SQLException { updateObject(getColIdxByName(paramString), paramObject, paramInt); }
  
  public void updateObject(String paramString, Object paramObject) throws SQLException { updateObject(getColIdxByName(paramString), paramObject); }
  
  public void insertRow() throws SQLException {
    int i;
    if (!this.onInsertRow || !this.insertRow.isCompleteRow(this.RowSetMD))
      throw new SQLException(this.resBundle.handleGetObject("cachedrowsetimpl.failedins").toString()); 
    Object[] arrayOfObject = getParams();
    for (byte b = 0; b < arrayOfObject.length; b++)
      this.insertRow.setColumnObject(b + true, arrayOfObject[b]); 
    Row row = new Row(this.RowSetMD.getColumnCount(), this.insertRow.getOrigRow());
    row.setInserted();
    if (this.currentRow >= this.numRows || this.currentRow < 0) {
      i = this.numRows;
    } else {
      i = this.currentRow;
    } 
    this.rvh.add(i, row);
    this.numRows++;
    notifyRowChanged();
  }
  
  public void updateRow() throws SQLException {
    if (this.onInsertRow == true)
      throw new SQLException(this.resBundle.handleGetObject("cachedrowsetimpl.updateins").toString()); 
    ((Row)getCurrentRow()).setUpdated();
    notifyRowChanged();
  }
  
  public void deleteRow() throws SQLException {
    checkCursor();
    ((Row)getCurrentRow()).setDeleted();
    this.numDeleted++;
    notifyRowChanged();
  }
  
  public void refreshRow() throws SQLException {
    checkCursor();
    if (this.onInsertRow == true)
      throw new SQLException(this.resBundle.handleGetObject("cachedrowsetimpl.invalidcp").toString()); 
    Row row = (Row)getCurrentRow();
    row.clearUpdated();
  }
  
  public void cancelRowUpdates() throws SQLException {
    checkCursor();
    if (this.onInsertRow == true)
      throw new SQLException(this.resBundle.handleGetObject("cachedrowsetimpl.invalidcp").toString()); 
    Row row = (Row)getCurrentRow();
    if (row.getUpdated() == true) {
      row.clearUpdated();
      notifyRowChanged();
    } 
  }
  
  public void moveToInsertRow() throws SQLException {
    if (getConcurrency() == 1007)
      throw new SQLException(this.resBundle.handleGetObject("cachedrowsetimpl.movetoins").toString()); 
    if (this.insertRow == null) {
      if (this.RowSetMD == null)
        throw new SQLException(this.resBundle.handleGetObject("cachedrowsetimpl.movetoins1").toString()); 
      int i = this.RowSetMD.getColumnCount();
      if (i > 0) {
        this.insertRow = new InsertRow(i);
      } else {
        throw new SQLException(this.resBundle.handleGetObject("cachedrowsetimpl.movetoins2").toString());
      } 
    } 
    this.onInsertRow = true;
    this.currentRow = this.cursorPos;
    this.cursorPos = -1;
    this.insertRow.initInsertRow();
  }
  
  public void moveToCurrentRow() throws SQLException {
    if (!this.onInsertRow)
      return; 
    this.cursorPos = this.currentRow;
    this.onInsertRow = false;
  }
  
  public Statement getStatement() throws SQLException { return null; }
  
  public Object getObject(int paramInt, Map<String, Class<?>> paramMap) throws SQLException {
    checkIndex(paramInt);
    checkCursor();
    setLastValueNull(false);
    Object object = getCurrentRow().getColumnObject(paramInt);
    if (object == null) {
      setLastValueNull(true);
      return null;
    } 
    if (object instanceof Struct) {
      Struct struct = (Struct)object;
      Class clazz = (Class)paramMap.get(struct.getSQLTypeName());
      if (clazz != null) {
        SQLData sQLData = null;
        try {
          sQLData = (SQLData)ReflectUtil.newInstance(clazz);
        } catch (Exception exception) {
          throw new SQLException("Unable to Instantiate: ", exception);
        } 
        Object[] arrayOfObject = struct.getAttributes(paramMap);
        SQLInputImpl sQLInputImpl = new SQLInputImpl(arrayOfObject, paramMap);
        sQLData.readSQL(sQLInputImpl, struct.getSQLTypeName());
        return sQLData;
      } 
    } 
    return object;
  }
  
  public Ref getRef(int paramInt) throws SQLException {
    checkIndex(paramInt);
    checkCursor();
    if (this.RowSetMD.getColumnType(paramInt) != 2006)
      throw new SQLException(this.resBundle.handleGetObject("cachedrowsetimpl.dtypemismt").toString()); 
    setLastValueNull(false);
    Ref ref = (Ref)getCurrentRow().getColumnObject(paramInt);
    if (ref == null) {
      setLastValueNull(true);
      return null;
    } 
    return ref;
  }
  
  public Blob getBlob(int paramInt) throws SQLException {
    checkIndex(paramInt);
    checkCursor();
    if (this.RowSetMD.getColumnType(paramInt) != 2004) {
      System.out.println(MessageFormat.format(this.resBundle.handleGetObject("cachedrowsetimpl.type").toString(), new Object[] { Integer.valueOf(this.RowSetMD.getColumnType(paramInt)) }));
      throw new SQLException(this.resBundle.handleGetObject("cachedrowsetimpl.dtypemismt").toString());
    } 
    setLastValueNull(false);
    Blob blob = (Blob)getCurrentRow().getColumnObject(paramInt);
    if (blob == null) {
      setLastValueNull(true);
      return null;
    } 
    return blob;
  }
  
  public Clob getClob(int paramInt) throws SQLException {
    checkIndex(paramInt);
    checkCursor();
    if (this.RowSetMD.getColumnType(paramInt) != 2005) {
      System.out.println(MessageFormat.format(this.resBundle.handleGetObject("cachedrowsetimpl.type").toString(), new Object[] { Integer.valueOf(this.RowSetMD.getColumnType(paramInt)) }));
      throw new SQLException(this.resBundle.handleGetObject("cachedrowsetimpl.dtypemismt").toString());
    } 
    setLastValueNull(false);
    Clob clob = (Clob)getCurrentRow().getColumnObject(paramInt);
    if (clob == null) {
      setLastValueNull(true);
      return null;
    } 
    return clob;
  }
  
  public Array getArray(int paramInt) throws SQLException {
    checkIndex(paramInt);
    checkCursor();
    if (this.RowSetMD.getColumnType(paramInt) != 2003)
      throw new SQLException(this.resBundle.handleGetObject("cachedrowsetimpl.dtypemismt").toString()); 
    setLastValueNull(false);
    Array array = (Array)getCurrentRow().getColumnObject(paramInt);
    if (array == null) {
      setLastValueNull(true);
      return null;
    } 
    return array;
  }
  
  public Object getObject(String paramString, Map<String, Class<?>> paramMap) throws SQLException { return getObject(getColIdxByName(paramString), paramMap); }
  
  public Ref getRef(String paramString) throws SQLException { return getRef(getColIdxByName(paramString)); }
  
  public Blob getBlob(String paramString) throws SQLException { return getBlob(getColIdxByName(paramString)); }
  
  public Clob getClob(String paramString) throws SQLException { return getClob(getColIdxByName(paramString)); }
  
  public Array getArray(String paramString) throws SQLException { return getArray(getColIdxByName(paramString)); }
  
  public Date getDate(int paramInt, Calendar paramCalendar) throws SQLException {
    checkIndex(paramInt);
    checkCursor();
    setLastValueNull(false);
    Object object = getCurrentRow().getColumnObject(paramInt);
    if (object == null) {
      setLastValueNull(true);
      return null;
    } 
    object = convertTemporal(object, this.RowSetMD.getColumnType(paramInt), 91);
    Calendar calendar = Calendar.getInstance();
    calendar.setTime((Date)object);
    paramCalendar.set(1, calendar.get(1));
    paramCalendar.set(2, calendar.get(2));
    paramCalendar.set(5, calendar.get(5));
    return new Date(paramCalendar.getTime().getTime());
  }
  
  public Date getDate(String paramString, Calendar paramCalendar) throws SQLException { return getDate(getColIdxByName(paramString), paramCalendar); }
  
  public Time getTime(int paramInt, Calendar paramCalendar) throws SQLException {
    checkIndex(paramInt);
    checkCursor();
    setLastValueNull(false);
    Object object = getCurrentRow().getColumnObject(paramInt);
    if (object == null) {
      setLastValueNull(true);
      return null;
    } 
    object = convertTemporal(object, this.RowSetMD.getColumnType(paramInt), 92);
    Calendar calendar = Calendar.getInstance();
    calendar.setTime((Date)object);
    paramCalendar.set(11, calendar.get(11));
    paramCalendar.set(12, calendar.get(12));
    paramCalendar.set(13, calendar.get(13));
    return new Time(paramCalendar.getTime().getTime());
  }
  
  public Time getTime(String paramString, Calendar paramCalendar) throws SQLException { return getTime(getColIdxByName(paramString), paramCalendar); }
  
  public Timestamp getTimestamp(int paramInt, Calendar paramCalendar) throws SQLException {
    checkIndex(paramInt);
    checkCursor();
    setLastValueNull(false);
    Object object = getCurrentRow().getColumnObject(paramInt);
    if (object == null) {
      setLastValueNull(true);
      return null;
    } 
    object = convertTemporal(object, this.RowSetMD.getColumnType(paramInt), 93);
    Calendar calendar = Calendar.getInstance();
    calendar.setTime((Date)object);
    paramCalendar.set(1, calendar.get(1));
    paramCalendar.set(2, calendar.get(2));
    paramCalendar.set(5, calendar.get(5));
    paramCalendar.set(11, calendar.get(11));
    paramCalendar.set(12, calendar.get(12));
    paramCalendar.set(13, calendar.get(13));
    return new Timestamp(paramCalendar.getTime().getTime());
  }
  
  public Timestamp getTimestamp(String paramString, Calendar paramCalendar) throws SQLException { return getTimestamp(getColIdxByName(paramString), paramCalendar); }
  
  public Connection getConnection() throws SQLException { return this.conn; }
  
  public void setMetaData(RowSetMetaData paramRowSetMetaData) throws SQLException { this.RowSetMD = (RowSetMetaDataImpl)paramRowSetMetaData; }
  
  public ResultSet getOriginal() throws SQLException {
    CachedRowSetImpl cachedRowSetImpl = new CachedRowSetImpl();
    cachedRowSetImpl.RowSetMD = this.RowSetMD;
    cachedRowSetImpl.numRows = this.numRows;
    cachedRowSetImpl.cursorPos = 0;
    int i = this.RowSetMD.getColumnCount();
    Iterator iterator = this.rvh.iterator();
    while (iterator.hasNext()) {
      Row row = new Row(i, ((Row)iterator.next()).getOrigRow());
      cachedRowSetImpl.rvh.add(row);
    } 
    return cachedRowSetImpl;
  }
  
  public ResultSet getOriginalRow() throws SQLException {
    CachedRowSetImpl cachedRowSetImpl = new CachedRowSetImpl();
    cachedRowSetImpl.RowSetMD = this.RowSetMD;
    cachedRowSetImpl.numRows = 1;
    cachedRowSetImpl.cursorPos = 0;
    cachedRowSetImpl.setTypeMap(getTypeMap());
    Row row = new Row(this.RowSetMD.getColumnCount(), getCurrentRow().getOrigRow());
    cachedRowSetImpl.rvh.add(row);
    return cachedRowSetImpl;
  }
  
  public void setOriginalRow() throws SQLException {
    if (this.onInsertRow == true)
      throw new SQLException(this.resBundle.handleGetObject("cachedrowsetimpl.invalidop").toString()); 
    Row row = (Row)getCurrentRow();
    makeRowOriginal(row);
    if (row.getDeleted() == true)
      removeCurrentRow(); 
  }
  
  private void makeRowOriginal(Row paramRow) {
    if (paramRow.getInserted() == true)
      paramRow.clearInserted(); 
    if (paramRow.getUpdated() == true)
      paramRow.moveCurrentToOrig(); 
  }
  
  public void setOriginal() throws SQLException {
    Iterator iterator = this.rvh.iterator();
    while (iterator.hasNext()) {
      Row row = (Row)iterator.next();
      makeRowOriginal(row);
      if (row.getDeleted() == true) {
        iterator.remove();
        this.numRows--;
      } 
    } 
    this.numDeleted = 0;
    notifyRowSetChanged();
  }
  
  public String getTableName() throws SQLException { return this.tableName; }
  
  public void setTableName(String paramString) throws SQLException {
    if (paramString == null)
      throw new SQLException(this.resBundle.handleGetObject("cachedrowsetimpl.tablename").toString()); 
    this.tableName = paramString;
  }
  
  public int[] getKeyColumns() throws SQLException {
    int[] arrayOfInt = this.keyCols;
    return (arrayOfInt == null) ? null : Arrays.copyOf(arrayOfInt, arrayOfInt.length);
  }
  
  public void setKeyColumns(int[] paramArrayOfInt) throws SQLException {
    int i = 0;
    if (this.RowSetMD != null) {
      i = this.RowSetMD.getColumnCount();
      if (paramArrayOfInt.length > i)
        throw new SQLException(this.resBundle.handleGetObject("cachedrowsetimpl.keycols").toString()); 
    } 
    this.keyCols = new int[paramArrayOfInt.length];
    for (byte b = 0; b < paramArrayOfInt.length; b++) {
      if (this.RowSetMD != null && (paramArrayOfInt[b] <= 0 || paramArrayOfInt[b] > i))
        throw new SQLException(this.resBundle.handleGetObject("cachedrowsetimpl.invalidcol").toString() + paramArrayOfInt[b]); 
      this.keyCols[b] = paramArrayOfInt[b];
    } 
  }
  
  public void updateRef(int paramInt, Ref paramRef) throws SQLException {
    checkIndex(paramInt);
    checkCursor();
    getCurrentRow().setColumnObject(paramInt, new SerialRef(paramRef));
  }
  
  public void updateRef(String paramString, Ref paramRef) throws SQLException { updateRef(getColIdxByName(paramString), paramRef); }
  
  public void updateClob(int paramInt, Clob paramClob) throws SQLException {
    checkIndex(paramInt);
    checkCursor();
    if (this.dbmslocatorsUpdateCopy) {
      getCurrentRow().setColumnObject(paramInt, new SerialClob(paramClob));
    } else {
      throw new SQLException(this.resBundle.handleGetObject("cachedrowsetimpl.opnotsupp").toString());
    } 
  }
  
  public void updateClob(String paramString, Clob paramClob) throws SQLException { updateClob(getColIdxByName(paramString), paramClob); }
  
  public void updateBlob(int paramInt, Blob paramBlob) throws SQLException {
    checkIndex(paramInt);
    checkCursor();
    if (this.dbmslocatorsUpdateCopy) {
      getCurrentRow().setColumnObject(paramInt, new SerialBlob(paramBlob));
    } else {
      throw new SQLException(this.resBundle.handleGetObject("cachedrowsetimpl.opnotsupp").toString());
    } 
  }
  
  public void updateBlob(String paramString, Blob paramBlob) throws SQLException { updateBlob(getColIdxByName(paramString), paramBlob); }
  
  public void updateArray(int paramInt, Array paramArray) throws SQLException {
    checkIndex(paramInt);
    checkCursor();
    getCurrentRow().setColumnObject(paramInt, new SerialArray(paramArray));
  }
  
  public void updateArray(String paramString, Array paramArray) throws SQLException { updateArray(getColIdxByName(paramString), paramArray); }
  
  public URL getURL(int paramInt) throws SQLException {
    checkIndex(paramInt);
    checkCursor();
    if (this.RowSetMD.getColumnType(paramInt) != 70)
      throw new SQLException(this.resBundle.handleGetObject("cachedrowsetimpl.dtypemismt").toString()); 
    setLastValueNull(false);
    URL uRL = (URL)getCurrentRow().getColumnObject(paramInt);
    if (uRL == null) {
      setLastValueNull(true);
      return null;
    } 
    return uRL;
  }
  
  public URL getURL(String paramString) throws SQLException { return getURL(getColIdxByName(paramString)); }
  
  public RowSetWarning getRowSetWarnings() {
    try {
      notifyCursorMoved();
    } catch (SQLException sQLException) {}
    return this.rowsetWarning;
  }
  
  private String buildTableName(String paramString) throws SQLException {
    String str = "";
    paramString = paramString.trim();
    if (paramString.toLowerCase().startsWith("select")) {
      int i = paramString.toLowerCase().indexOf("from");
      int j = paramString.indexOf(",", i);
      if (j == -1) {
        str = paramString.substring(i + "from".length(), paramString.length()).trim();
        String str1 = str;
        int k = str1.toLowerCase().indexOf("where");
        if (k != -1)
          str1 = str1.substring(0, k).trim(); 
        str = str1;
      } 
    } else if (!paramString.toLowerCase().startsWith("insert") && paramString.toLowerCase().startsWith("update")) {
    
    } 
    return str;
  }
  
  public void commit() throws SQLException { this.conn.commit(); }
  
  public void rollback() throws SQLException { this.conn.rollback(); }
  
  public void rollback(Savepoint paramSavepoint) throws SQLException { this.conn.rollback(paramSavepoint); }
  
  public void unsetMatchColumn(int[] paramArrayOfInt) throws SQLException {
    byte b;
    for (b = 0; b < paramArrayOfInt.length; b++) {
      int i = Integer.parseInt(((Integer)this.iMatchColumns.get(b)).toString());
      if (paramArrayOfInt[b] != i)
        throw new SQLException(this.resBundle.handleGetObject("cachedrowsetimpl.matchcols").toString()); 
    } 
    for (b = 0; b < paramArrayOfInt.length; b++)
      this.iMatchColumns.set(b, Integer.valueOf(-1)); 
  }
  
  public void unsetMatchColumn(String[] paramArrayOfString) throws SQLException {
    byte b;
    for (b = 0; b < paramArrayOfString.length; b++) {
      if (!paramArrayOfString[b].equals(this.strMatchColumns.get(b)))
        throw new SQLException(this.resBundle.handleGetObject("cachedrowsetimpl.matchcols").toString()); 
    } 
    for (b = 0; b < paramArrayOfString.length; b++)
      this.strMatchColumns.set(b, null); 
  }
  
  public String[] getMatchColumnNames() throws SQLException {
    String[] arrayOfString = new String[this.strMatchColumns.size()];
    if (this.strMatchColumns.get(false) == null)
      throw new SQLException(this.resBundle.handleGetObject("cachedrowsetimpl.setmatchcols").toString()); 
    this.strMatchColumns.copyInto(arrayOfString);
    return arrayOfString;
  }
  
  public int[] getMatchColumnIndexes() throws SQLException {
    Integer[] arrayOfInteger = new Integer[this.iMatchColumns.size()];
    int[] arrayOfInt = new int[this.iMatchColumns.size()];
    int i = ((Integer)this.iMatchColumns.get(0)).intValue();
    if (i == -1)
      throw new SQLException(this.resBundle.handleGetObject("cachedrowsetimpl.setmatchcols").toString()); 
    this.iMatchColumns.copyInto(arrayOfInteger);
    for (byte b = 0; b < arrayOfInteger.length; b++)
      arrayOfInt[b] = arrayOfInteger[b].intValue(); 
    return arrayOfInt;
  }
  
  public void setMatchColumn(int[] paramArrayOfInt) throws SQLException {
    byte b;
    for (b = 0; b < paramArrayOfInt.length; b++) {
      if (paramArrayOfInt[b] < 0)
        throw new SQLException(this.resBundle.handleGetObject("cachedrowsetimpl.matchcols1").toString()); 
    } 
    for (b = 0; b < paramArrayOfInt.length; b++)
      this.iMatchColumns.add(b, Integer.valueOf(paramArrayOfInt[b])); 
  }
  
  public void setMatchColumn(String[] paramArrayOfString) throws SQLException {
    byte b;
    for (b = 0; b < paramArrayOfString.length; b++) {
      if (paramArrayOfString[b] == null || paramArrayOfString[b].equals(""))
        throw new SQLException(this.resBundle.handleGetObject("cachedrowsetimpl.matchcols2").toString()); 
    } 
    for (b = 0; b < paramArrayOfString.length; b++)
      this.strMatchColumns.add(b, paramArrayOfString[b]); 
  }
  
  public void setMatchColumn(int paramInt) throws SQLException {
    if (paramInt < 0)
      throw new SQLException(this.resBundle.handleGetObject("cachedrowsetimpl.matchcols1").toString()); 
    this.iMatchColumns.set(0, Integer.valueOf(paramInt));
  }
  
  public void setMatchColumn(String paramString) throws SQLException {
    if (paramString == null || (paramString = paramString.trim()).equals(""))
      throw new SQLException(this.resBundle.handleGetObject("cachedrowsetimpl.matchcols2").toString()); 
    this.strMatchColumns.set(0, paramString);
  }
  
  public void unsetMatchColumn(int paramInt) throws SQLException {
    if (!((Integer)this.iMatchColumns.get(0)).equals(Integer.valueOf(paramInt)))
      throw new SQLException(this.resBundle.handleGetObject("cachedrowsetimpl.unsetmatch").toString()); 
    if (this.strMatchColumns.get(false) != null)
      throw new SQLException(this.resBundle.handleGetObject("cachedrowsetimpl.unsetmatch1").toString()); 
    this.iMatchColumns.set(0, Integer.valueOf(-1));
  }
  
  public void unsetMatchColumn(String paramString) throws SQLException {
    paramString = paramString.trim();
    if (!((String)this.strMatchColumns.get(0)).equals(paramString))
      throw new SQLException(this.resBundle.handleGetObject("cachedrowsetimpl.unsetmatch").toString()); 
    if (((Integer)this.iMatchColumns.get(0)).intValue() > 0)
      throw new SQLException(this.resBundle.handleGetObject("cachedrowsetimpl.unsetmatch2").toString()); 
    this.strMatchColumns.set(0, null);
  }
  
  public void rowSetPopulated(RowSetEvent paramRowSetEvent, int paramInt) throws SQLException {
    if (paramInt < 0 || paramInt < getFetchSize())
      throw new SQLException(this.resBundle.handleGetObject("cachedrowsetimpl.numrows").toString()); 
    if (size() % paramInt == 0) {
      RowSetEvent rowSetEvent = new RowSetEvent(this);
      paramRowSetEvent = rowSetEvent;
      notifyRowSetChanged();
    } 
  }
  
  public void populate(ResultSet paramResultSet, int paramInt) throws SQLException {
    Map map = getTypeMap();
    this.cursorPos = 0;
    if (this.populatecallcount == 0) {
      if (paramInt < 0)
        throw new SQLException(this.resBundle.handleGetObject("cachedrowsetimpl.startpos").toString()); 
      if (getMaxRows() == 0) {
        paramResultSet.absolute(paramInt);
        while (paramResultSet.next())
          this.totalRows++; 
        this.totalRows++;
      } 
      this.startPos = paramInt;
    } 
    this.populatecallcount++;
    this.resultSet = paramResultSet;
    if (this.endPos - this.startPos >= getMaxRows() && getMaxRows() > 0) {
      this.endPos = this.prevEndPos;
      this.pagenotend = false;
      return;
    } 
    if ((this.maxRowsreached != getMaxRows() || this.maxRowsreached != this.totalRows) && this.pagenotend)
      this.startPrev = paramInt - getPageSize(); 
    if (this.pageSize == 0) {
      this.prevEndPos = this.endPos;
      this.endPos = paramInt + getMaxRows();
    } else {
      this.prevEndPos = this.endPos;
      this.endPos = paramInt + getPageSize();
    } 
    if (paramInt == 1) {
      this.resultSet.beforeFirst();
    } else {
      this.resultSet.absolute(paramInt - 1);
    } 
    if (this.pageSize == 0) {
      this.rvh = new Vector(getMaxRows());
    } else {
      this.rvh = new Vector(getPageSize());
    } 
    if (paramResultSet == null)
      throw new SQLException(this.resBundle.handleGetObject("cachedrowsetimpl.populate").toString()); 
    this.RSMD = paramResultSet.getMetaData();
    this.RowSetMD = new RowSetMetaDataImpl();
    initMetaData(this.RowSetMD, this.RSMD);
    this.RSMD = null;
    int i = this.RowSetMD.getColumnCount();
    int j = getMaxRows();
    byte b = 0;
    Row row = null;
    if (!paramResultSet.next() && j == 0) {
      this.endPos = this.prevEndPos;
      this.pagenotend = false;
      return;
    } 
    paramResultSet.previous();
    while (paramResultSet.next()) {
      row = new Row(i);
      if (this.pageSize == 0) {
        if (b >= j && j > 0) {
          this.rowsetWarning.setNextException(new SQLException("Populating rows setting has exceeded max row setting"));
          break;
        } 
      } else if (b >= this.pageSize || (this.maxRowsreached >= j && j > 0)) {
        this.rowsetWarning.setNextException(new SQLException("Populating rows setting has exceeded max row setting"));
        break;
      } 
      for (byte b1 = 1; b1 <= i; b1++) {
        Object object;
        if (map == null) {
          object = paramResultSet.getObject(b1);
        } else {
          object = paramResultSet.getObject(b1, map);
        } 
        if (object instanceof Struct) {
          object = new SerialStruct((Struct)object, map);
        } else if (object instanceof SQLData) {
          object = new SerialStruct((SQLData)object, map);
        } else if (object instanceof Blob) {
          object = new SerialBlob((Blob)object);
        } else if (object instanceof Clob) {
          object = new SerialClob((Clob)object);
        } else if (object instanceof Array) {
          object = new SerialArray((Array)object, map);
        } 
        row.initColumnObject(b1, object);
      } 
      b++;
      this.maxRowsreached++;
      this.rvh.add(row);
    } 
    this.numRows = b;
    notifyRowSetChanged();
  }
  
  public boolean nextPage() throws SQLException {
    if (this.populatecallcount == 0)
      throw new SQLException(this.resBundle.handleGetObject("cachedrowsetimpl.nextpage").toString()); 
    this.onFirstPage = false;
    if (this.callWithCon) {
      this.crsReader.setStartPosition(this.endPos);
      this.crsReader.readData(this);
      this.resultSet = null;
    } else {
      populate(this.resultSet, this.endPos);
    } 
    return this.pagenotend;
  }
  
  public void setPageSize(int paramInt) throws SQLException {
    if (paramInt < 0)
      throw new SQLException(this.resBundle.handleGetObject("cachedrowsetimpl.pagesize").toString()); 
    if (paramInt > getMaxRows() && getMaxRows() != 0)
      throw new SQLException(this.resBundle.handleGetObject("cachedrowsetimpl.pagesize1").toString()); 
    this.pageSize = paramInt;
  }
  
  public int getPageSize() { return this.pageSize; }
  
  public boolean previousPage() throws SQLException {
    int i = getPageSize();
    int j = this.maxRowsreached;
    if (this.populatecallcount == 0)
      throw new SQLException(this.resBundle.handleGetObject("cachedrowsetimpl.nextpage").toString()); 
    if (!this.callWithCon && this.resultSet.getType() == 1003)
      throw new SQLException(this.resBundle.handleGetObject("cachedrowsetimpl.fwdonly").toString()); 
    this.pagenotend = true;
    if (this.startPrev < this.startPos) {
      this.onFirstPage = true;
      return false;
    } 
    if (this.onFirstPage)
      return false; 
    int k = j % i;
    if (k == 0) {
      this.maxRowsreached -= 2 * i;
      if (this.callWithCon) {
        this.crsReader.setStartPosition(this.startPrev);
        this.crsReader.readData(this);
        this.resultSet = null;
      } else {
        populate(this.resultSet, this.startPrev);
      } 
      return true;
    } 
    this.maxRowsreached -= i + k;
    if (this.callWithCon) {
      this.crsReader.setStartPosition(this.startPrev);
      this.crsReader.readData(this);
      this.resultSet = null;
    } else {
      populate(this.resultSet, this.startPrev);
    } 
    return true;
  }
  
  public void setRowInserted(boolean paramBoolean) {
    checkCursor();
    if (this.onInsertRow == true)
      throw new SQLException(this.resBundle.handleGetObject("cachedrowsetimpl.invalidop").toString()); 
    if (paramBoolean) {
      ((Row)getCurrentRow()).setInserted();
    } else {
      ((Row)getCurrentRow()).clearInserted();
    } 
  }
  
  public SQLXML getSQLXML(int paramInt) throws SQLException { throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("cachedrowsetimpl.opnotysupp").toString()); }
  
  public SQLXML getSQLXML(String paramString) throws SQLException { throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("cachedrowsetimpl.opnotysupp").toString()); }
  
  public RowId getRowId(int paramInt) throws SQLException { throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("cachedrowsetimpl.opnotysupp").toString()); }
  
  public RowId getRowId(String paramString) throws SQLException { throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("cachedrowsetimpl.opnotysupp").toString()); }
  
  public void updateRowId(int paramInt, RowId paramRowId) throws SQLException { throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("cachedrowsetimpl.opnotysupp").toString()); }
  
  public void updateRowId(String paramString, RowId paramRowId) throws SQLException { throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("cachedrowsetimpl.opnotysupp").toString()); }
  
  public int getHoldability() { throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("cachedrowsetimpl.opnotysupp").toString()); }
  
  public boolean isClosed() throws SQLException { throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("cachedrowsetimpl.opnotysupp").toString()); }
  
  public void updateNString(int paramInt, String paramString) throws SQLException { throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("cachedrowsetimpl.opnotysupp").toString()); }
  
  public void updateNString(String paramString1, String paramString2) throws SQLException { throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("cachedrowsetimpl.opnotysupp").toString()); }
  
  public void updateNClob(int paramInt, NClob paramNClob) throws SQLException { throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("cachedrowsetimpl.opnotysupp").toString()); }
  
  public void updateNClob(String paramString, NClob paramNClob) throws SQLException { throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("cachedrowsetimpl.opnotysupp").toString()); }
  
  public NClob getNClob(int paramInt) throws SQLException { throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("cachedrowsetimpl.opnotysupp").toString()); }
  
  public NClob getNClob(String paramString) throws SQLException { throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("cachedrowsetimpl.opnotysupp").toString()); }
  
  public <T> T unwrap(Class<T> paramClass) throws SQLException { return null; }
  
  public boolean isWrapperFor(Class<?> paramClass) throws SQLException { return false; }
  
  public void setSQLXML(int paramInt, SQLXML paramSQLXML) throws SQLException { throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("cachedrowsetimpl.opnotysupp").toString()); }
  
  public void setSQLXML(String paramString, SQLXML paramSQLXML) throws SQLException { throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("cachedrowsetimpl.opnotysupp").toString()); }
  
  public void setRowId(int paramInt, RowId paramRowId) throws SQLException { throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("cachedrowsetimpl.opnotysupp").toString()); }
  
  public void setRowId(String paramString, RowId paramRowId) throws SQLException { throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("cachedrowsetimpl.opnotysupp").toString()); }
  
  public void setNCharacterStream(int paramInt, Reader paramReader) throws SQLException { throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("cachedrowsetimpl.featnotsupp").toString()); }
  
  public void setNClob(String paramString, NClob paramNClob) throws SQLException { throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("cachedrowsetimpl.opnotysupp").toString()); }
  
  public Reader getNCharacterStream(int paramInt) throws SQLException { throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("cachedrowsetimpl.opnotysupp").toString()); }
  
  public Reader getNCharacterStream(String paramString) throws SQLException { throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("cachedrowsetimpl.opnotysupp").toString()); }
  
  public void updateSQLXML(int paramInt, SQLXML paramSQLXML) throws SQLException { throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("cachedrowsetimpl.opnotysupp").toString()); }
  
  public void updateSQLXML(String paramString, SQLXML paramSQLXML) throws SQLException { throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("cachedrowsetimpl.opnotysupp").toString()); }
  
  public String getNString(int paramInt) throws SQLException { throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("cachedrowsetimpl.opnotysupp").toString()); }
  
  public String getNString(String paramString) throws SQLException { throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("cachedrowsetimpl.opnotysupp").toString()); }
  
  public void updateNCharacterStream(int paramInt, Reader paramReader, long paramLong) throws SQLException { throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("cachedrowsetimpl.opnotysupp").toString()); }
  
  public void updateNCharacterStream(String paramString, Reader paramReader, long paramLong) throws SQLException { throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("cachedrowsetimpl.opnotysupp").toString()); }
  
  public void updateNCharacterStream(int paramInt, Reader paramReader) throws SQLException { throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("cachedrowsetimpl.featnotsupp").toString()); }
  
  public void updateNCharacterStream(String paramString, Reader paramReader) throws SQLException { throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("cachedrowsetimpl.featnotsupp").toString()); }
  
  public void updateBlob(int paramInt, InputStream paramInputStream, long paramLong) throws SQLException { throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("cachedrowsetimpl.featnotsupp").toString()); }
  
  public void updateBlob(String paramString, InputStream paramInputStream, long paramLong) throws SQLException { throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("cachedrowsetimpl.featnotsupp").toString()); }
  
  public void updateBlob(int paramInt, InputStream paramInputStream) throws SQLException { throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("cachedrowsetimpl.featnotsupp").toString()); }
  
  public void updateBlob(String paramString, InputStream paramInputStream) throws SQLException { throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("cachedrowsetimpl.featnotsupp").toString()); }
  
  public void updateClob(int paramInt, Reader paramReader, long paramLong) throws SQLException { throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("cachedrowsetimpl.featnotsupp").toString()); }
  
  public void updateClob(String paramString, Reader paramReader, long paramLong) throws SQLException { throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("cachedrowsetimpl.featnotsupp").toString()); }
  
  public void updateClob(int paramInt, Reader paramReader) throws SQLException { throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("cachedrowsetimpl.featnotsupp").toString()); }
  
  public void updateClob(String paramString, Reader paramReader) throws SQLException { throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("cachedrowsetimpl.featnotsupp").toString()); }
  
  public void updateNClob(int paramInt, Reader paramReader, long paramLong) throws SQLException { throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("cachedrowsetimpl.featnotsupp").toString()); }
  
  public void updateNClob(String paramString, Reader paramReader, long paramLong) throws SQLException { throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("cachedrowsetimpl.featnotsupp").toString()); }
  
  public void updateNClob(int paramInt, Reader paramReader) throws SQLException { throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("cachedrowsetimpl.featnotsupp").toString()); }
  
  public void updateNClob(String paramString, Reader paramReader) throws SQLException { throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("cachedrowsetimpl.featnotsupp").toString()); }
  
  public void updateAsciiStream(int paramInt, InputStream paramInputStream, long paramLong) throws SQLException {}
  
  public void updateBinaryStream(int paramInt, InputStream paramInputStream, long paramLong) throws SQLException {}
  
  public void updateCharacterStream(int paramInt, Reader paramReader, long paramLong) throws SQLException { throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("cachedrowsetimpl.featnotsupp").toString()); }
  
  public void updateCharacterStream(String paramString, Reader paramReader, long paramLong) throws SQLException { throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("cachedrowsetimpl.featnotsupp").toString()); }
  
  public void updateAsciiStream(String paramString, InputStream paramInputStream, long paramLong) throws SQLException {}
  
  public void updateBinaryStream(String paramString, InputStream paramInputStream, long paramLong) throws SQLException {}
  
  public void updateBinaryStream(int paramInt, InputStream paramInputStream) throws SQLException { throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("cachedrowsetimpl.featnotsupp").toString()); }
  
  public void updateBinaryStream(String paramString, InputStream paramInputStream) throws SQLException { throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("cachedrowsetimpl.featnotsupp").toString()); }
  
  public void updateCharacterStream(int paramInt, Reader paramReader) throws SQLException { throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("cachedrowsetimpl.featnotsupp").toString()); }
  
  public void updateCharacterStream(String paramString, Reader paramReader) throws SQLException { throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("cachedrowsetimpl.featnotsupp").toString()); }
  
  public void updateAsciiStream(int paramInt, InputStream paramInputStream) throws SQLException { throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("cachedrowsetimpl.featnotsupp").toString()); }
  
  public void updateAsciiStream(String paramString, InputStream paramInputStream) throws SQLException {}
  
  public void setURL(int paramInt, URL paramURL) throws SQLException { throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("cachedrowsetimpl.featnotsupp").toString()); }
  
  public void setNClob(int paramInt, Reader paramReader) throws SQLException { throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("cachedrowsetimpl.featnotsupp").toString()); }
  
  public void setNClob(String paramString, Reader paramReader, long paramLong) throws SQLException { throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("cachedrowsetimpl.featnotsupp").toString()); }
  
  public void setNClob(String paramString, Reader paramReader) throws SQLException { throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("cachedrowsetimpl.featnotsupp").toString()); }
  
  public void setNClob(int paramInt, Reader paramReader, long paramLong) throws SQLException { throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("cachedrowsetimpl.featnotsupp").toString()); }
  
  public void setNClob(int paramInt, NClob paramNClob) throws SQLException { throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("cachedrowsetimpl.featnotsupp").toString()); }
  
  public void setNString(int paramInt, String paramString) throws SQLException { throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("cachedrowsetimpl.featnotsupp").toString()); }
  
  public void setNString(String paramString1, String paramString2) throws SQLException { throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("cachedrowsetimpl.featnotsupp").toString()); }
  
  public void setNCharacterStream(int paramInt, Reader paramReader, long paramLong) throws SQLException { throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("cachedrowsetimpl.featnotsupp").toString()); }
  
  public void setNCharacterStream(String paramString, Reader paramReader, long paramLong) throws SQLException { throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("cachedrowsetimpl.featnotsupp").toString()); }
  
  public void setNCharacterStream(String paramString, Reader paramReader) throws SQLException { throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("cachedrowsetimpl.featnotsupp").toString()); }
  
  public void setTimestamp(String paramString, Timestamp paramTimestamp, Calendar paramCalendar) throws SQLException { throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("cachedrowsetimpl.featnotsupp").toString()); }
  
  public void setClob(String paramString, Reader paramReader, long paramLong) throws SQLException { throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("cachedrowsetimpl.featnotsupp").toString()); }
  
  public void setClob(String paramString, Clob paramClob) throws SQLException { throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("cachedrowsetimpl.featnotsupp").toString()); }
  
  public void setClob(String paramString, Reader paramReader) throws SQLException { throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("cachedrowsetimpl.featnotsupp").toString()); }
  
  public void setDate(String paramString, Date paramDate) throws SQLException { throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("cachedrowsetimpl.featnotsupp").toString()); }
  
  public void setDate(String paramString, Date paramDate, Calendar paramCalendar) throws SQLException { throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("cachedrowsetimpl.featnotsupp").toString()); }
  
  public void setTime(String paramString, Time paramTime) throws SQLException { throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("cachedrowsetimpl.featnotsupp").toString()); }
  
  public void setTime(String paramString, Time paramTime, Calendar paramCalendar) throws SQLException { throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("cachedrowsetimpl.featnotsupp").toString()); }
  
  public void setClob(int paramInt, Reader paramReader) throws SQLException { throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("cachedrowsetimpl.featnotsupp").toString()); }
  
  public void setClob(int paramInt, Reader paramReader, long paramLong) throws SQLException { throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("cachedrowsetimpl.featnotsupp").toString()); }
  
  public void setBlob(int paramInt, InputStream paramInputStream, long paramLong) throws SQLException { throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("cachedrowsetimpl.featnotsupp").toString()); }
  
  public void setBlob(int paramInt, InputStream paramInputStream) throws SQLException { throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("cachedrowsetimpl.featnotsupp").toString()); }
  
  public void setBlob(String paramString, InputStream paramInputStream, long paramLong) throws SQLException { throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("cachedrowsetimpl.featnotsupp").toString()); }
  
  public void setBlob(String paramString, Blob paramBlob) throws SQLException { throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("cachedrowsetimpl.featnotsupp").toString()); }
  
  public void setBlob(String paramString, InputStream paramInputStream) throws SQLException { throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("cachedrowsetimpl.featnotsupp").toString()); }
  
  public void setObject(String paramString, Object paramObject, int paramInt1, int paramInt2) throws SQLException { throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("cachedrowsetimpl.featnotsupp").toString()); }
  
  public void setObject(String paramString, Object paramObject, int paramInt) throws SQLException { throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("cachedrowsetimpl.featnotsupp").toString()); }
  
  public void setObject(String paramString, Object paramObject) throws SQLException { throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("cachedrowsetimpl.featnotsupp").toString()); }
  
  public void setAsciiStream(String paramString, InputStream paramInputStream, int paramInt) throws SQLException { throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("cachedrowsetimpl.featnotsupp").toString()); }
  
  public void setBinaryStream(String paramString, InputStream paramInputStream, int paramInt) throws SQLException { throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("cachedrowsetimpl.featnotsupp").toString()); }
  
  public void setCharacterStream(String paramString, Reader paramReader, int paramInt) throws SQLException { throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("cachedrowsetimpl.featnotsupp").toString()); }
  
  public void setAsciiStream(String paramString, InputStream paramInputStream) throws SQLException { throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("cachedrowsetimpl.featnotsupp").toString()); }
  
  public void setBinaryStream(String paramString, InputStream paramInputStream) throws SQLException { throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("cachedrowsetimpl.featnotsupp").toString()); }
  
  public void setCharacterStream(String paramString, Reader paramReader) throws SQLException { throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("cachedrowsetimpl.featnotsupp").toString()); }
  
  public void setBigDecimal(String paramString, BigDecimal paramBigDecimal) throws SQLException { throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("cachedrowsetimpl.featnotsupp").toString()); }
  
  public void setString(String paramString1, String paramString2) throws SQLException { throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("cachedrowsetimpl.featnotsupp").toString()); }
  
  public void setBytes(String paramString, byte[] paramArrayOfByte) throws SQLException { throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("cachedrowsetimpl.featnotsupp").toString()); }
  
  public void setTimestamp(String paramString, Timestamp paramTimestamp) throws SQLException { throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("cachedrowsetimpl.featnotsupp").toString()); }
  
  public void setNull(String paramString, int paramInt) throws SQLException { throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("cachedrowsetimpl.featnotsupp").toString()); }
  
  public void setNull(String paramString1, int paramInt, String paramString2) throws SQLException { throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("cachedrowsetimpl.featnotsupp").toString()); }
  
  public void setBoolean(String paramString, boolean paramBoolean) throws SQLException { throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("cachedrowsetimpl.featnotsupp").toString()); }
  
  public void setByte(String paramString, byte paramByte) throws SQLException { throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("cachedrowsetimpl.featnotsupp").toString()); }
  
  public void setShort(String paramString, short paramShort) throws SQLException { throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("cachedrowsetimpl.featnotsupp").toString()); }
  
  public void setInt(String paramString, int paramInt) throws SQLException { throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("cachedrowsetimpl.featnotsupp").toString()); }
  
  public void setLong(String paramString, long paramLong) throws SQLException { throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("cachedrowsetimpl.featnotsupp").toString()); }
  
  public void setFloat(String paramString, float paramFloat) throws SQLException { throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("cachedrowsetimpl.featnotsupp").toString()); }
  
  public void setDouble(String paramString, double paramDouble) throws SQLException { throw new SQLFeatureNotSupportedException(this.resBundle.handleGetObject("cachedrowsetimpl.featnotsupp").toString()); }
  
  private void readObject(ObjectInputStream paramObjectInputStream) throws IOException, ClassNotFoundException {
    paramObjectInputStream.defaultReadObject();
    try {
      this.resBundle = JdbcRowSetResourceBundle.getJdbcRowSetResourceBundle();
    } catch (IOException iOException) {
      throw new RuntimeException(iOException);
    } 
  }
  
  public <T> T getObject(int paramInt, Class<T> paramClass) throws SQLException { throw new SQLFeatureNotSupportedException("Not supported yet."); }
  
  public <T> T getObject(String paramString, Class<T> paramClass) throws SQLException { throw new SQLFeatureNotSupportedException("Not supported yet."); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\rowset\CachedRowSetImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */