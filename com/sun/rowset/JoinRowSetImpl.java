package com.sun.rowset;

import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.io.Reader;
import java.io.Writer;
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
import java.sql.Statement;
import java.sql.Time;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collection;
import java.util.Map;
import java.util.Vector;
import javax.sql.RowSet;
import javax.sql.RowSetListener;
import javax.sql.RowSetMetaData;
import javax.sql.rowset.CachedRowSet;
import javax.sql.rowset.JoinRowSet;
import javax.sql.rowset.Joinable;
import javax.sql.rowset.RowSetMetaDataImpl;
import javax.sql.rowset.WebRowSet;
import javax.sql.rowset.spi.SyncProvider;

public class JoinRowSetImpl extends WebRowSetImpl implements JoinRowSet {
  private Vector<CachedRowSetImpl> vecRowSetsInJOIN = new Vector();
  
  private CachedRowSetImpl crsInternal = new CachedRowSetImpl();
  
  private Vector<Integer> vecJoinType = new Vector();
  
  private Vector<String> vecTableNames = new Vector();
  
  private int iMatchKey = -1;
  
  private String strMatchKey = null;
  
  boolean[] supportedJOINs = { false, true, false, false, false };
  
  private WebRowSet wrs;
  
  static final long serialVersionUID = -5590501621560008453L;
  
  public JoinRowSetImpl() throws SQLException {
    try {
      this.resBundle = JdbcRowSetResourceBundle.getJdbcRowSetResourceBundle();
    } catch (IOException iOException) {
      throw new RuntimeException(iOException);
    } 
  }
  
  public void addRowSet(Joinable paramJoinable) throws SQLException {
    CachedRowSetImpl cachedRowSetImpl;
    boolean bool1 = false;
    boolean bool2 = false;
    if (!(paramJoinable instanceof RowSet))
      throw new SQLException(this.resBundle.handleGetObject("joinrowsetimpl.notinstance").toString()); 
    if (paramJoinable instanceof JdbcRowSetImpl) {
      cachedRowSetImpl = new CachedRowSetImpl();
      cachedRowSetImpl.populate((RowSet)paramJoinable);
      if (cachedRowSetImpl.size() == 0)
        throw new SQLException(this.resBundle.handleGetObject("joinrowsetimpl.emptyrowset").toString()); 
      try {
        byte b1 = 0;
        for (byte b2 = 0; b2 < paramJoinable.getMatchColumnIndexes().length && paramJoinable.getMatchColumnIndexes()[b2] != -1; b2++)
          b1++; 
        int[] arrayOfInt = new int[b1];
        for (byte b3 = 0; b3 < b1; b3++)
          arrayOfInt[b3] = paramJoinable.getMatchColumnIndexes()[b3]; 
        cachedRowSetImpl.setMatchColumn(arrayOfInt);
      } catch (SQLException sQLException) {}
    } else {
      cachedRowSetImpl = (CachedRowSetImpl)paramJoinable;
      if (cachedRowSetImpl.size() == 0)
        throw new SQLException(this.resBundle.handleGetObject("joinrowsetimpl.emptyrowset").toString()); 
    } 
    try {
      this.iMatchKey = cachedRowSetImpl.getMatchColumnIndexes()[0];
    } catch (SQLException sQLException) {
      bool1 = true;
    } 
    try {
      this.strMatchKey = cachedRowSetImpl.getMatchColumnNames()[0];
    } catch (SQLException sQLException) {
      bool2 = true;
    } 
    if (bool1 && bool2)
      throw new SQLException(this.resBundle.handleGetObject("joinrowsetimpl.matchnotset").toString()); 
    if (bool1) {
      ArrayList arrayList = new ArrayList();
      for (byte b1 = 0; b1 < cachedRowSetImpl.getMatchColumnNames().length && (this.strMatchKey = cachedRowSetImpl.getMatchColumnNames()[b1]) != null; b1++) {
        this.iMatchKey = cachedRowSetImpl.findColumn(this.strMatchKey);
        arrayList.add(Integer.valueOf(this.iMatchKey));
      } 
      int[] arrayOfInt = new int[arrayList.size()];
      for (byte b2 = 0; b2 < arrayList.size(); b2++)
        arrayOfInt[b2] = ((Integer)arrayList.get(b2)).intValue(); 
      cachedRowSetImpl.setMatchColumn(arrayOfInt);
    } 
    initJOIN(cachedRowSetImpl);
  }
  
  public void addRowSet(RowSet paramRowSet, int paramInt) throws SQLException {
    ((CachedRowSetImpl)paramRowSet).setMatchColumn(paramInt);
    addRowSet((Joinable)paramRowSet);
  }
  
  public void addRowSet(RowSet paramRowSet, String paramString) throws SQLException {
    ((CachedRowSetImpl)paramRowSet).setMatchColumn(paramString);
    addRowSet((Joinable)paramRowSet);
  }
  
  public void addRowSet(RowSet[] paramArrayOfRowSet, int[] paramArrayOfInt) throws SQLException {
    if (paramArrayOfRowSet.length != paramArrayOfInt.length)
      throw new SQLException(this.resBundle.handleGetObject("joinrowsetimpl.numnotequal").toString()); 
    for (byte b = 0; b < paramArrayOfRowSet.length; b++) {
      ((CachedRowSetImpl)paramArrayOfRowSet[b]).setMatchColumn(paramArrayOfInt[b]);
      addRowSet((Joinable)paramArrayOfRowSet[b]);
    } 
  }
  
  public void addRowSet(RowSet[] paramArrayOfRowSet, String[] paramArrayOfString) throws SQLException {
    if (paramArrayOfRowSet.length != paramArrayOfString.length)
      throw new SQLException(this.resBundle.handleGetObject("joinrowsetimpl.numnotequal").toString()); 
    for (byte b = 0; b < paramArrayOfRowSet.length; b++) {
      ((CachedRowSetImpl)paramArrayOfRowSet[b]).setMatchColumn(paramArrayOfString[b]);
      addRowSet((Joinable)paramArrayOfRowSet[b]);
    } 
  }
  
  public Collection getRowSets() throws SQLException { return this.vecRowSetsInJOIN; }
  
  public String[] getRowSetNames() throws SQLException {
    Object[] arrayOfObject = this.vecTableNames.toArray();
    String[] arrayOfString = new String[arrayOfObject.length];
    for (byte b = 0; b < arrayOfObject.length; b++)
      arrayOfString[b] = arrayOfObject[b].toString(); 
    return arrayOfString;
  }
  
  public CachedRowSet toCachedRowSet() throws SQLException { return this.crsInternal; }
  
  public boolean supportsCrossJoin() { return this.supportedJOINs[0]; }
  
  public boolean supportsInnerJoin() { return this.supportedJOINs[1]; }
  
  public boolean supportsLeftOuterJoin() { return this.supportedJOINs[2]; }
  
  public boolean supportsRightOuterJoin() { return this.supportedJOINs[3]; }
  
  public boolean supportsFullJoin() { return this.supportedJOINs[4]; }
  
  public void setJoinType(int paramInt) throws SQLException {
    if (paramInt >= 0 && paramInt <= 4) {
      if (paramInt != 1)
        throw new SQLException(this.resBundle.handleGetObject("joinrowsetimpl.notsupported").toString()); 
      Integer integer = Integer.valueOf(1);
      this.vecJoinType.add(integer);
    } else {
      throw new SQLException(this.resBundle.handleGetObject("joinrowsetimpl.notdefined").toString());
    } 
  }
  
  private boolean checkforMatchColumn(Joinable paramJoinable) throws SQLException {
    int[] arrayOfInt = paramJoinable.getMatchColumnIndexes();
    return !(arrayOfInt.length <= 0);
  }
  
  private void initJOIN(CachedRowSet paramCachedRowSet) throws SQLException {
    try {
      CachedRowSetImpl cachedRowSetImpl1 = (CachedRowSetImpl)paramCachedRowSet;
      CachedRowSetImpl cachedRowSetImpl2 = new CachedRowSetImpl();
      RowSetMetaDataImpl rowSetMetaDataImpl = new RowSetMetaDataImpl();
      if (this.vecRowSetsInJOIN.isEmpty()) {
        this.crsInternal = (CachedRowSetImpl)paramCachedRowSet.createCopy();
        this.crsInternal.setMetaData((RowSetMetaDataImpl)cachedRowSetImpl1.getMetaData());
        this.vecRowSetsInJOIN.add(cachedRowSetImpl1);
      } else {
        if (this.vecRowSetsInJOIN.size() - this.vecJoinType.size() == 2) {
          setJoinType(1);
        } else if (this.vecRowSetsInJOIN.size() - this.vecJoinType.size() == 1) {
        
        } 
        this.vecTableNames.add(this.crsInternal.getTableName());
        this.vecTableNames.add(cachedRowSetImpl1.getTableName());
        int i = cachedRowSetImpl1.size();
        int j = this.crsInternal.size();
        int k = 0;
        byte b1;
        for (b1 = 0; b1 < this.crsInternal.getMatchColumnIndexes().length && this.crsInternal.getMatchColumnIndexes()[b1] != -1; b1++)
          k++; 
        rowSetMetaDataImpl.setColumnCount(this.crsInternal.getMetaData().getColumnCount() + cachedRowSetImpl1.getMetaData().getColumnCount() - k);
        cachedRowSetImpl2.setMetaData(rowSetMetaDataImpl);
        this.crsInternal.beforeFirst();
        cachedRowSetImpl1.beforeFirst();
        for (b1 = 1; b1 <= j && !this.crsInternal.isAfterLast(); b1++) {
          if (this.crsInternal.next()) {
            cachedRowSetImpl1.beforeFirst();
            for (byte b = 1; b <= i && !cachedRowSetImpl1.isAfterLast(); b++) {
              if (cachedRowSetImpl1.next()) {
                boolean bool = true;
                byte b3;
                for (b3 = 0; b3 < k; b3++) {
                  if (!this.crsInternal.getObject(this.crsInternal.getMatchColumnIndexes()[b3]).equals(cachedRowSetImpl1.getObject(cachedRowSetImpl1.getMatchColumnIndexes()[b3]))) {
                    bool = false;
                    break;
                  } 
                } 
                if (bool) {
                  byte b4 = 0;
                  cachedRowSetImpl2.moveToInsertRow();
                  for (b3 = 1; b3 <= this.crsInternal.getMetaData().getColumnCount(); b3++) {
                    bool = false;
                    for (byte b6 = 0; b6 < k; b6++) {
                      if (b3 == this.crsInternal.getMatchColumnIndexes()[b6]) {
                        bool = true;
                        break;
                      } 
                    } 
                    if (!bool) {
                      cachedRowSetImpl2.updateObject(++b4, this.crsInternal.getObject(b3));
                      rowSetMetaDataImpl.setColumnName(b4, this.crsInternal.getMetaData().getColumnName(b3));
                      rowSetMetaDataImpl.setTableName(b4, this.crsInternal.getTableName());
                      rowSetMetaDataImpl.setColumnType(b3, this.crsInternal.getMetaData().getColumnType(b3));
                      rowSetMetaDataImpl.setAutoIncrement(b3, this.crsInternal.getMetaData().isAutoIncrement(b3));
                      rowSetMetaDataImpl.setCaseSensitive(b3, this.crsInternal.getMetaData().isCaseSensitive(b3));
                      rowSetMetaDataImpl.setCatalogName(b3, this.crsInternal.getMetaData().getCatalogName(b3));
                      rowSetMetaDataImpl.setColumnDisplaySize(b3, this.crsInternal.getMetaData().getColumnDisplaySize(b3));
                      rowSetMetaDataImpl.setColumnLabel(b3, this.crsInternal.getMetaData().getColumnLabel(b3));
                      rowSetMetaDataImpl.setColumnType(b3, this.crsInternal.getMetaData().getColumnType(b3));
                      rowSetMetaDataImpl.setColumnTypeName(b3, this.crsInternal.getMetaData().getColumnTypeName(b3));
                      rowSetMetaDataImpl.setCurrency(b3, this.crsInternal.getMetaData().isCurrency(b3));
                      rowSetMetaDataImpl.setNullable(b3, this.crsInternal.getMetaData().isNullable(b3));
                      rowSetMetaDataImpl.setPrecision(b3, this.crsInternal.getMetaData().getPrecision(b3));
                      rowSetMetaDataImpl.setScale(b3, this.crsInternal.getMetaData().getScale(b3));
                      rowSetMetaDataImpl.setSchemaName(b3, this.crsInternal.getMetaData().getSchemaName(b3));
                      rowSetMetaDataImpl.setSearchable(b3, this.crsInternal.getMetaData().isSearchable(b3));
                      rowSetMetaDataImpl.setSigned(b3, this.crsInternal.getMetaData().isSigned(b3));
                    } else {
                      cachedRowSetImpl2.updateObject(++b4, this.crsInternal.getObject(b3));
                      rowSetMetaDataImpl.setColumnName(b4, this.crsInternal.getMetaData().getColumnName(b3));
                      rowSetMetaDataImpl.setTableName(b4, this.crsInternal.getTableName() + "#" + cachedRowSetImpl1.getTableName());
                      rowSetMetaDataImpl.setColumnType(b3, this.crsInternal.getMetaData().getColumnType(b3));
                      rowSetMetaDataImpl.setAutoIncrement(b3, this.crsInternal.getMetaData().isAutoIncrement(b3));
                      rowSetMetaDataImpl.setCaseSensitive(b3, this.crsInternal.getMetaData().isCaseSensitive(b3));
                      rowSetMetaDataImpl.setCatalogName(b3, this.crsInternal.getMetaData().getCatalogName(b3));
                      rowSetMetaDataImpl.setColumnDisplaySize(b3, this.crsInternal.getMetaData().getColumnDisplaySize(b3));
                      rowSetMetaDataImpl.setColumnLabel(b3, this.crsInternal.getMetaData().getColumnLabel(b3));
                      rowSetMetaDataImpl.setColumnType(b3, this.crsInternal.getMetaData().getColumnType(b3));
                      rowSetMetaDataImpl.setColumnTypeName(b3, this.crsInternal.getMetaData().getColumnTypeName(b3));
                      rowSetMetaDataImpl.setCurrency(b3, this.crsInternal.getMetaData().isCurrency(b3));
                      rowSetMetaDataImpl.setNullable(b3, this.crsInternal.getMetaData().isNullable(b3));
                      rowSetMetaDataImpl.setPrecision(b3, this.crsInternal.getMetaData().getPrecision(b3));
                      rowSetMetaDataImpl.setScale(b3, this.crsInternal.getMetaData().getScale(b3));
                      rowSetMetaDataImpl.setSchemaName(b3, this.crsInternal.getMetaData().getSchemaName(b3));
                      rowSetMetaDataImpl.setSearchable(b3, this.crsInternal.getMetaData().isSearchable(b3));
                      rowSetMetaDataImpl.setSigned(b3, this.crsInternal.getMetaData().isSigned(b3));
                    } 
                  } 
                  for (byte b5 = 1; b5 <= cachedRowSetImpl1.getMetaData().getColumnCount(); b5++) {
                    bool = false;
                    for (byte b6 = 0; b6 < k; b6++) {
                      if (b5 == cachedRowSetImpl1.getMatchColumnIndexes()[b6]) {
                        bool = true;
                        break;
                      } 
                    } 
                    if (!bool) {
                      cachedRowSetImpl2.updateObject(++b4, cachedRowSetImpl1.getObject(b5));
                      rowSetMetaDataImpl.setColumnName(b4, cachedRowSetImpl1.getMetaData().getColumnName(b5));
                      rowSetMetaDataImpl.setTableName(b4, cachedRowSetImpl1.getTableName());
                      rowSetMetaDataImpl.setColumnType(b3 + b5 - 1, cachedRowSetImpl1.getMetaData().getColumnType(b5));
                      rowSetMetaDataImpl.setAutoIncrement(b3 + b5 - 1, cachedRowSetImpl1.getMetaData().isAutoIncrement(b5));
                      rowSetMetaDataImpl.setCaseSensitive(b3 + b5 - 1, cachedRowSetImpl1.getMetaData().isCaseSensitive(b5));
                      rowSetMetaDataImpl.setCatalogName(b3 + b5 - 1, cachedRowSetImpl1.getMetaData().getCatalogName(b5));
                      rowSetMetaDataImpl.setColumnDisplaySize(b3 + b5 - 1, cachedRowSetImpl1.getMetaData().getColumnDisplaySize(b5));
                      rowSetMetaDataImpl.setColumnLabel(b3 + b5 - 1, cachedRowSetImpl1.getMetaData().getColumnLabel(b5));
                      rowSetMetaDataImpl.setColumnType(b3 + b5 - 1, cachedRowSetImpl1.getMetaData().getColumnType(b5));
                      rowSetMetaDataImpl.setColumnTypeName(b3 + b5 - 1, cachedRowSetImpl1.getMetaData().getColumnTypeName(b5));
                      rowSetMetaDataImpl.setCurrency(b3 + b5 - 1, cachedRowSetImpl1.getMetaData().isCurrency(b5));
                      rowSetMetaDataImpl.setNullable(b3 + b5 - 1, cachedRowSetImpl1.getMetaData().isNullable(b5));
                      rowSetMetaDataImpl.setPrecision(b3 + b5 - 1, cachedRowSetImpl1.getMetaData().getPrecision(b5));
                      rowSetMetaDataImpl.setScale(b3 + b5 - 1, cachedRowSetImpl1.getMetaData().getScale(b5));
                      rowSetMetaDataImpl.setSchemaName(b3 + b5 - 1, cachedRowSetImpl1.getMetaData().getSchemaName(b5));
                      rowSetMetaDataImpl.setSearchable(b3 + b5 - 1, cachedRowSetImpl1.getMetaData().isSearchable(b5));
                      rowSetMetaDataImpl.setSigned(b3 + b5 - 1, cachedRowSetImpl1.getMetaData().isSigned(b5));
                    } else {
                      b3--;
                    } 
                  } 
                  cachedRowSetImpl2.insertRow();
                  cachedRowSetImpl2.moveToCurrentRow();
                } 
              } 
            } 
          } 
        } 
        cachedRowSetImpl2.setMetaData(rowSetMetaDataImpl);
        cachedRowSetImpl2.setOriginal();
        int[] arrayOfInt = new int[k];
        for (byte b2 = 0; b2 < k; b2++)
          arrayOfInt[b2] = this.crsInternal.getMatchColumnIndexes()[b2]; 
        this.crsInternal = (CachedRowSetImpl)cachedRowSetImpl2.createCopy();
        this.crsInternal.setMatchColumn(arrayOfInt);
        this.crsInternal.setMetaData(rowSetMetaDataImpl);
        this.vecRowSetsInJOIN.add(cachedRowSetImpl1);
      } 
    } catch (SQLException sQLException) {
      sQLException.printStackTrace();
      throw new SQLException(this.resBundle.handleGetObject("joinrowsetimpl.initerror").toString() + sQLException);
    } catch (Exception exception) {
      exception.printStackTrace();
      throw new SQLException(this.resBundle.handleGetObject("joinrowsetimpl.genericerr").toString() + exception);
    } 
  }
  
  public String getWhereClause() throws SQLException {
    String str1 = "Select ";
    String str2 = "";
    String str3 = "";
    int i = this.vecRowSetsInJOIN.size();
    byte b;
    for (b = 0; b < i; b++) {
      CachedRowSetImpl cachedRowSetImpl = (CachedRowSetImpl)this.vecRowSetsInJOIN.get(b);
      int j = cachedRowSetImpl.getMetaData().getColumnCount();
      str2 = str2.concat(cachedRowSetImpl.getTableName());
      str3 = str3.concat(str2 + ", ");
      byte b1 = 1;
      while (b1 < j) {
        str1 = str1.concat(str2 + "." + cachedRowSetImpl.getMetaData().getColumnName(b1++));
        str1 = str1.concat(", ");
      } 
    } 
    str1 = str1.substring(0, str1.lastIndexOf(","));
    str1 = str1.concat(" from ");
    str1 = str1.concat(str3);
    str1 = str1.substring(0, str1.lastIndexOf(","));
    str1 = str1.concat(" where ");
    for (b = 0; b < i; b++) {
      str1 = str1.concat(((CachedRowSetImpl)this.vecRowSetsInJOIN.get(b)).getMatchColumnNames()[0]);
      if (b % 2 != 0) {
        str1 = str1.concat("=");
      } else {
        str1 = str1.concat(" and");
      } 
      str1 = str1.concat(" ");
    } 
    return str1;
  }
  
  public boolean next() { return this.crsInternal.next(); }
  
  public void close() throws SQLException { this.crsInternal.close(); }
  
  public boolean wasNull() { return this.crsInternal.wasNull(); }
  
  public String getString(int paramInt) throws SQLException { return this.crsInternal.getString(paramInt); }
  
  public boolean getBoolean(int paramInt) throws SQLException { return this.crsInternal.getBoolean(paramInt); }
  
  public byte getByte(int paramInt) throws SQLException { return this.crsInternal.getByte(paramInt); }
  
  public short getShort(int paramInt) throws SQLException { return this.crsInternal.getShort(paramInt); }
  
  public int getInt(int paramInt) throws SQLException { return this.crsInternal.getInt(paramInt); }
  
  public long getLong(int paramInt) throws SQLException { return this.crsInternal.getLong(paramInt); }
  
  public float getFloat(int paramInt) throws SQLException { return this.crsInternal.getFloat(paramInt); }
  
  public double getDouble(int paramInt) throws SQLException { return this.crsInternal.getDouble(paramInt); }
  
  @Deprecated
  public BigDecimal getBigDecimal(int paramInt1, int paramInt2) throws SQLException { return this.crsInternal.getBigDecimal(paramInt1); }
  
  public byte[] getBytes(int paramInt) throws SQLException { return this.crsInternal.getBytes(paramInt); }
  
  public Date getDate(int paramInt) throws SQLException { return this.crsInternal.getDate(paramInt); }
  
  public Time getTime(int paramInt) throws SQLException { return this.crsInternal.getTime(paramInt); }
  
  public Timestamp getTimestamp(int paramInt) throws SQLException { return this.crsInternal.getTimestamp(paramInt); }
  
  public InputStream getAsciiStream(int paramInt) throws SQLException { return this.crsInternal.getAsciiStream(paramInt); }
  
  @Deprecated
  public InputStream getUnicodeStream(int paramInt) throws SQLException { return this.crsInternal.getUnicodeStream(paramInt); }
  
  public InputStream getBinaryStream(int paramInt) throws SQLException { return this.crsInternal.getBinaryStream(paramInt); }
  
  public String getString(String paramString) throws SQLException { return this.crsInternal.getString(paramString); }
  
  public boolean getBoolean(String paramString) throws SQLException { return this.crsInternal.getBoolean(paramString); }
  
  public byte getByte(String paramString) throws SQLException { return this.crsInternal.getByte(paramString); }
  
  public short getShort(String paramString) throws SQLException { return this.crsInternal.getShort(paramString); }
  
  public int getInt(String paramString) throws SQLException { return this.crsInternal.getInt(paramString); }
  
  public long getLong(String paramString) throws SQLException { return this.crsInternal.getLong(paramString); }
  
  public float getFloat(String paramString) throws SQLException { return this.crsInternal.getFloat(paramString); }
  
  public double getDouble(String paramString) throws SQLException { return this.crsInternal.getDouble(paramString); }
  
  @Deprecated
  public BigDecimal getBigDecimal(String paramString, int paramInt) throws SQLException { return this.crsInternal.getBigDecimal(paramString); }
  
  public byte[] getBytes(String paramString) throws SQLException { return this.crsInternal.getBytes(paramString); }
  
  public Date getDate(String paramString) throws SQLException { return this.crsInternal.getDate(paramString); }
  
  public Time getTime(String paramString) throws SQLException { return this.crsInternal.getTime(paramString); }
  
  public Timestamp getTimestamp(String paramString) throws SQLException { return this.crsInternal.getTimestamp(paramString); }
  
  public InputStream getAsciiStream(String paramString) throws SQLException { return this.crsInternal.getAsciiStream(paramString); }
  
  @Deprecated
  public InputStream getUnicodeStream(String paramString) throws SQLException { return this.crsInternal.getUnicodeStream(paramString); }
  
  public InputStream getBinaryStream(String paramString) throws SQLException { return this.crsInternal.getBinaryStream(paramString); }
  
  public SQLWarning getWarnings() { return this.crsInternal.getWarnings(); }
  
  public void clearWarnings() throws SQLException { this.crsInternal.clearWarnings(); }
  
  public String getCursorName() throws SQLException { return this.crsInternal.getCursorName(); }
  
  public ResultSetMetaData getMetaData() throws SQLException { return this.crsInternal.getMetaData(); }
  
  public Object getObject(int paramInt) throws SQLException { return this.crsInternal.getObject(paramInt); }
  
  public Object getObject(int paramInt, Map<String, Class<?>> paramMap) throws SQLException { return this.crsInternal.getObject(paramInt, paramMap); }
  
  public Object getObject(String paramString) throws SQLException { return this.crsInternal.getObject(paramString); }
  
  public Object getObject(String paramString, Map<String, Class<?>> paramMap) throws SQLException { return this.crsInternal.getObject(paramString, paramMap); }
  
  public Reader getCharacterStream(int paramInt) throws SQLException { return this.crsInternal.getCharacterStream(paramInt); }
  
  public Reader getCharacterStream(String paramString) throws SQLException { return this.crsInternal.getCharacterStream(paramString); }
  
  public BigDecimal getBigDecimal(int paramInt) throws SQLException { return this.crsInternal.getBigDecimal(paramInt); }
  
  public BigDecimal getBigDecimal(String paramString) throws SQLException { return this.crsInternal.getBigDecimal(paramString); }
  
  public int size() { return this.crsInternal.size(); }
  
  public boolean isBeforeFirst() { return this.crsInternal.isBeforeFirst(); }
  
  public boolean isAfterLast() { return this.crsInternal.isAfterLast(); }
  
  public boolean isFirst() { return this.crsInternal.isFirst(); }
  
  public boolean isLast() { return this.crsInternal.isLast(); }
  
  public void beforeFirst() throws SQLException { this.crsInternal.beforeFirst(); }
  
  public void afterLast() throws SQLException { this.crsInternal.afterLast(); }
  
  public boolean first() { return this.crsInternal.first(); }
  
  public boolean last() { return this.crsInternal.last(); }
  
  public int getRow() { return this.crsInternal.getRow(); }
  
  public boolean absolute(int paramInt) throws SQLException { return this.crsInternal.absolute(paramInt); }
  
  public boolean relative(int paramInt) throws SQLException { return this.crsInternal.relative(paramInt); }
  
  public boolean previous() { return this.crsInternal.previous(); }
  
  public int findColumn(String paramString) throws SQLException { return this.crsInternal.findColumn(paramString); }
  
  public boolean rowUpdated() { return this.crsInternal.rowUpdated(); }
  
  public boolean columnUpdated(int paramInt) throws SQLException { return this.crsInternal.columnUpdated(paramInt); }
  
  public boolean rowInserted() { return this.crsInternal.rowInserted(); }
  
  public boolean rowDeleted() { return this.crsInternal.rowDeleted(); }
  
  public void updateNull(int paramInt) throws SQLException { this.crsInternal.updateNull(paramInt); }
  
  public void updateBoolean(int paramInt, boolean paramBoolean) throws SQLException { this.crsInternal.updateBoolean(paramInt, paramBoolean); }
  
  public void updateByte(int paramInt, byte paramByte) throws SQLException { this.crsInternal.updateByte(paramInt, paramByte); }
  
  public void updateShort(int paramInt, short paramShort) throws SQLException { this.crsInternal.updateShort(paramInt, paramShort); }
  
  public void updateInt(int paramInt1, int paramInt2) throws SQLException { this.crsInternal.updateInt(paramInt1, paramInt2); }
  
  public void updateLong(int paramInt, long paramLong) throws SQLException { this.crsInternal.updateLong(paramInt, paramLong); }
  
  public void updateFloat(int paramInt, float paramFloat) throws SQLException { this.crsInternal.updateFloat(paramInt, paramFloat); }
  
  public void updateDouble(int paramInt, double paramDouble) throws SQLException { this.crsInternal.updateDouble(paramInt, paramDouble); }
  
  public void updateBigDecimal(int paramInt, BigDecimal paramBigDecimal) throws SQLException { this.crsInternal.updateBigDecimal(paramInt, paramBigDecimal); }
  
  public void updateString(int paramInt, String paramString) throws SQLException { this.crsInternal.updateString(paramInt, paramString); }
  
  public void updateBytes(int paramInt, byte[] paramArrayOfByte) throws SQLException { this.crsInternal.updateBytes(paramInt, paramArrayOfByte); }
  
  public void updateDate(int paramInt, Date paramDate) throws SQLException { this.crsInternal.updateDate(paramInt, paramDate); }
  
  public void updateTime(int paramInt, Time paramTime) throws SQLException { this.crsInternal.updateTime(paramInt, paramTime); }
  
  public void updateTimestamp(int paramInt, Timestamp paramTimestamp) throws SQLException { this.crsInternal.updateTimestamp(paramInt, paramTimestamp); }
  
  public void updateAsciiStream(int paramInt1, InputStream paramInputStream, int paramInt2) throws SQLException { this.crsInternal.updateAsciiStream(paramInt1, paramInputStream, paramInt2); }
  
  public void updateBinaryStream(int paramInt1, InputStream paramInputStream, int paramInt2) throws SQLException { this.crsInternal.updateBinaryStream(paramInt1, paramInputStream, paramInt2); }
  
  public void updateCharacterStream(int paramInt1, Reader paramReader, int paramInt2) throws SQLException { this.crsInternal.updateCharacterStream(paramInt1, paramReader, paramInt2); }
  
  public void updateObject(int paramInt1, Object paramObject, int paramInt2) throws SQLException { this.crsInternal.updateObject(paramInt1, paramObject, paramInt2); }
  
  public void updateObject(int paramInt, Object paramObject) throws SQLException { this.crsInternal.updateObject(paramInt, paramObject); }
  
  public void updateNull(String paramString) throws SQLException { this.crsInternal.updateNull(paramString); }
  
  public void updateBoolean(String paramString, boolean paramBoolean) throws SQLException { this.crsInternal.updateBoolean(paramString, paramBoolean); }
  
  public void updateByte(String paramString, byte paramByte) throws SQLException { this.crsInternal.updateByte(paramString, paramByte); }
  
  public void updateShort(String paramString, short paramShort) throws SQLException { this.crsInternal.updateShort(paramString, paramShort); }
  
  public void updateInt(String paramString, int paramInt) throws SQLException { this.crsInternal.updateInt(paramString, paramInt); }
  
  public void updateLong(String paramString, long paramLong) throws SQLException { this.crsInternal.updateLong(paramString, paramLong); }
  
  public void updateFloat(String paramString, float paramFloat) throws SQLException { this.crsInternal.updateFloat(paramString, paramFloat); }
  
  public void updateDouble(String paramString, double paramDouble) throws SQLException { this.crsInternal.updateDouble(paramString, paramDouble); }
  
  public void updateBigDecimal(String paramString, BigDecimal paramBigDecimal) throws SQLException { this.crsInternal.updateBigDecimal(paramString, paramBigDecimal); }
  
  public void updateString(String paramString1, String paramString2) throws SQLException { this.crsInternal.updateString(paramString1, paramString2); }
  
  public void updateBytes(String paramString, byte[] paramArrayOfByte) throws SQLException { this.crsInternal.updateBytes(paramString, paramArrayOfByte); }
  
  public void updateDate(String paramString, Date paramDate) throws SQLException { this.crsInternal.updateDate(paramString, paramDate); }
  
  public void updateTime(String paramString, Time paramTime) throws SQLException { this.crsInternal.updateTime(paramString, paramTime); }
  
  public void updateTimestamp(String paramString, Timestamp paramTimestamp) throws SQLException { this.crsInternal.updateTimestamp(paramString, paramTimestamp); }
  
  public void updateAsciiStream(String paramString, InputStream paramInputStream, int paramInt) throws SQLException { this.crsInternal.updateAsciiStream(paramString, paramInputStream, paramInt); }
  
  public void updateBinaryStream(String paramString, InputStream paramInputStream, int paramInt) throws SQLException { this.crsInternal.updateBinaryStream(paramString, paramInputStream, paramInt); }
  
  public void updateCharacterStream(String paramString, Reader paramReader, int paramInt) throws SQLException { this.crsInternal.updateCharacterStream(paramString, paramReader, paramInt); }
  
  public void updateObject(String paramString, Object paramObject, int paramInt) throws SQLException { this.crsInternal.updateObject(paramString, paramObject, paramInt); }
  
  public void updateObject(String paramString, Object paramObject) throws SQLException { this.crsInternal.updateObject(paramString, paramObject); }
  
  public void insertRow() throws SQLException { this.crsInternal.insertRow(); }
  
  public void updateRow() throws SQLException { this.crsInternal.updateRow(); }
  
  public void deleteRow() throws SQLException { this.crsInternal.deleteRow(); }
  
  public void refreshRow() throws SQLException { this.crsInternal.refreshRow(); }
  
  public void cancelRowUpdates() throws SQLException { this.crsInternal.cancelRowUpdates(); }
  
  public void moveToInsertRow() throws SQLException { this.crsInternal.moveToInsertRow(); }
  
  public void moveToCurrentRow() throws SQLException { this.crsInternal.moveToCurrentRow(); }
  
  public Statement getStatement() throws SQLException { return this.crsInternal.getStatement(); }
  
  public Ref getRef(int paramInt) throws SQLException { return this.crsInternal.getRef(paramInt); }
  
  public Blob getBlob(int paramInt) throws SQLException { return this.crsInternal.getBlob(paramInt); }
  
  public Clob getClob(int paramInt) throws SQLException { return this.crsInternal.getClob(paramInt); }
  
  public Array getArray(int paramInt) throws SQLException { return this.crsInternal.getArray(paramInt); }
  
  public Ref getRef(String paramString) throws SQLException { return this.crsInternal.getRef(paramString); }
  
  public Blob getBlob(String paramString) throws SQLException { return this.crsInternal.getBlob(paramString); }
  
  public Clob getClob(String paramString) throws SQLException { return this.crsInternal.getClob(paramString); }
  
  public Array getArray(String paramString) throws SQLException { return this.crsInternal.getArray(paramString); }
  
  public Date getDate(int paramInt, Calendar paramCalendar) throws SQLException { return this.crsInternal.getDate(paramInt, paramCalendar); }
  
  public Date getDate(String paramString, Calendar paramCalendar) throws SQLException { return this.crsInternal.getDate(paramString, paramCalendar); }
  
  public Time getTime(int paramInt, Calendar paramCalendar) throws SQLException { return this.crsInternal.getTime(paramInt, paramCalendar); }
  
  public Time getTime(String paramString, Calendar paramCalendar) throws SQLException { return this.crsInternal.getTime(paramString, paramCalendar); }
  
  public Timestamp getTimestamp(int paramInt, Calendar paramCalendar) throws SQLException { return this.crsInternal.getTimestamp(paramInt, paramCalendar); }
  
  public Timestamp getTimestamp(String paramString, Calendar paramCalendar) throws SQLException { return this.crsInternal.getTimestamp(paramString, paramCalendar); }
  
  public void setMetaData(RowSetMetaData paramRowSetMetaData) throws SQLException { this.crsInternal.setMetaData(paramRowSetMetaData); }
  
  public ResultSet getOriginal() throws SQLException { return this.crsInternal.getOriginal(); }
  
  public ResultSet getOriginalRow() throws SQLException { return this.crsInternal.getOriginalRow(); }
  
  public void setOriginalRow() throws SQLException { this.crsInternal.setOriginalRow(); }
  
  public int[] getKeyColumns() throws SQLException { return this.crsInternal.getKeyColumns(); }
  
  public void setKeyColumns(int[] paramArrayOfInt) throws SQLException { this.crsInternal.setKeyColumns(paramArrayOfInt); }
  
  public void updateRef(int paramInt, Ref paramRef) throws SQLException { this.crsInternal.updateRef(paramInt, paramRef); }
  
  public void updateRef(String paramString, Ref paramRef) throws SQLException { this.crsInternal.updateRef(paramString, paramRef); }
  
  public void updateClob(int paramInt, Clob paramClob) throws SQLException { this.crsInternal.updateClob(paramInt, paramClob); }
  
  public void updateClob(String paramString, Clob paramClob) throws SQLException { this.crsInternal.updateClob(paramString, paramClob); }
  
  public void updateBlob(int paramInt, Blob paramBlob) throws SQLException { this.crsInternal.updateBlob(paramInt, paramBlob); }
  
  public void updateBlob(String paramString, Blob paramBlob) throws SQLException { this.crsInternal.updateBlob(paramString, paramBlob); }
  
  public void updateArray(int paramInt, Array paramArray) throws SQLException { this.crsInternal.updateArray(paramInt, paramArray); }
  
  public void updateArray(String paramString, Array paramArray) throws SQLException { this.crsInternal.updateArray(paramString, paramArray); }
  
  public void execute() throws SQLException { this.crsInternal.execute(); }
  
  public void execute(Connection paramConnection) throws SQLException { this.crsInternal.execute(paramConnection); }
  
  public URL getURL(int paramInt) throws SQLException { return this.crsInternal.getURL(paramInt); }
  
  public URL getURL(String paramString) throws SQLException { return this.crsInternal.getURL(paramString); }
  
  public void writeXml(ResultSet paramResultSet, Writer paramWriter) throws SQLException {
    this.wrs = new WebRowSetImpl();
    this.wrs.populate(paramResultSet);
    this.wrs.writeXml(paramWriter);
  }
  
  public void writeXml(Writer paramWriter) throws SQLException { createWebRowSet().writeXml(paramWriter); }
  
  public void readXml(Reader paramReader) throws SQLException {
    this.wrs = new WebRowSetImpl();
    this.wrs.readXml(paramReader);
    this.crsInternal = (CachedRowSetImpl)this.wrs;
  }
  
  public void readXml(InputStream paramInputStream) throws SQLException, IOException {
    this.wrs = new WebRowSetImpl();
    this.wrs.readXml(paramInputStream);
    this.crsInternal = (CachedRowSetImpl)this.wrs;
  }
  
  public void writeXml(OutputStream paramOutputStream) throws SQLException, IOException { createWebRowSet().writeXml(paramOutputStream); }
  
  public void writeXml(ResultSet paramResultSet, OutputStream paramOutputStream) throws SQLException, IOException {
    this.wrs = new WebRowSetImpl();
    this.wrs.populate(paramResultSet);
    this.wrs.writeXml(paramOutputStream);
  }
  
  private WebRowSet createWebRowSet() throws SQLException {
    if (this.wrs != null)
      return this.wrs; 
    this.wrs = new WebRowSetImpl();
    this.crsInternal.beforeFirst();
    this.wrs.populate(this.crsInternal);
    return this.wrs;
  }
  
  public int getJoinType() {
    if (this.vecJoinType == null)
      setJoinType(1); 
    Integer integer = (Integer)this.vecJoinType.get(this.vecJoinType.size() - 1);
    return integer.intValue();
  }
  
  public void addRowSetListener(RowSetListener paramRowSetListener) { this.crsInternal.addRowSetListener(paramRowSetListener); }
  
  public void removeRowSetListener(RowSetListener paramRowSetListener) { this.crsInternal.removeRowSetListener(paramRowSetListener); }
  
  public Collection<?> toCollection() throws SQLException { return this.crsInternal.toCollection(); }
  
  public Collection<?> toCollection(int paramInt) throws SQLException { return this.crsInternal.toCollection(paramInt); }
  
  public Collection<?> toCollection(String paramString) throws SQLException { return this.crsInternal.toCollection(paramString); }
  
  public CachedRowSet createCopySchema() throws SQLException { return this.crsInternal.createCopySchema(); }
  
  public void setSyncProvider(String paramString) throws SQLException { this.crsInternal.setSyncProvider(paramString); }
  
  public void acceptChanges() throws SQLException { this.crsInternal.acceptChanges(); }
  
  public SyncProvider getSyncProvider() throws SQLException { return this.crsInternal.getSyncProvider(); }
  
  private void readObject(ObjectInputStream paramObjectInputStream) throws IOException, ClassNotFoundException {
    paramObjectInputStream.defaultReadObject();
    try {
      this.resBundle = JdbcRowSetResourceBundle.getJdbcRowSetResourceBundle();
    } catch (IOException iOException) {
      throw new RuntimeException(iOException);
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\rowset\JoinRowSetImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */