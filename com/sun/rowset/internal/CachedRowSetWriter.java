package com.sun.rowset.internal;

import com.sun.rowset.CachedRowSetImpl;
import com.sun.rowset.JdbcRowSetResourceBundle;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.sql.Array;
import java.sql.Blob;
import java.sql.Clob;
import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLData;
import java.sql.SQLException;
import java.sql.Savepoint;
import java.sql.Struct;
import java.util.ArrayList;
import java.util.Map;
import java.util.Vector;
import javax.sql.RowSetInternal;
import javax.sql.rowset.CachedRowSet;
import javax.sql.rowset.RowSetMetaDataImpl;
import javax.sql.rowset.serial.SQLInputImpl;
import javax.sql.rowset.serial.SerialArray;
import javax.sql.rowset.serial.SerialBlob;
import javax.sql.rowset.serial.SerialClob;
import javax.sql.rowset.serial.SerialStruct;
import javax.sql.rowset.spi.SyncProviderException;
import javax.sql.rowset.spi.TransactionalWriter;
import sun.reflect.misc.ReflectUtil;

public class CachedRowSetWriter implements TransactionalWriter, Serializable {
  private Connection con;
  
  private String selectCmd;
  
  private String updateCmd;
  
  private String updateWhere;
  
  private String deleteCmd;
  
  private String deleteWhere;
  
  private String insertCmd;
  
  private int[] keyCols;
  
  private Object[] params;
  
  private CachedRowSetReader reader;
  
  private ResultSetMetaData callerMd;
  
  private int callerColumnCount;
  
  private CachedRowSetImpl crsResolve;
  
  private ArrayList<Integer> status;
  
  private int iChangedValsInDbAndCRS;
  
  private int iChangedValsinDbOnly;
  
  private JdbcRowSetResourceBundle resBundle;
  
  static final long serialVersionUID = -8506030970299413976L;
  
  public CachedRowSetWriter() {
    try {
      this.resBundle = JdbcRowSetResourceBundle.getJdbcRowSetResourceBundle();
    } catch (IOException iOException) {
      throw new RuntimeException(iOException);
    } 
  }
  
  public boolean writeData(RowSetInternal paramRowSetInternal) throws SQLException {
    long l = 0L;
    boolean bool = false;
    PreparedStatement preparedStatement = null;
    this.iChangedValsInDbAndCRS = 0;
    this.iChangedValsinDbOnly = 0;
    CachedRowSetImpl cachedRowSetImpl = (CachedRowSetImpl)paramRowSetInternal;
    this.crsResolve = new CachedRowSetImpl();
    this.con = this.reader.connect(paramRowSetInternal);
    if (this.con == null)
      throw new SQLException(this.resBundle.handleGetObject("crswriter.connect").toString()); 
    initSQLStatements(cachedRowSetImpl);
    RowSetMetaDataImpl rowSetMetaDataImpl1 = (RowSetMetaDataImpl)cachedRowSetImpl.getMetaData();
    RowSetMetaDataImpl rowSetMetaDataImpl2 = new RowSetMetaDataImpl();
    int i = rowSetMetaDataImpl1.getColumnCount();
    int j = cachedRowSetImpl.size() + 1;
    this.status = new ArrayList(j);
    this.status.add(0, null);
    rowSetMetaDataImpl2.setColumnCount(i);
    byte b;
    for (b = 1; b <= i; b++) {
      rowSetMetaDataImpl2.setColumnType(b, rowSetMetaDataImpl1.getColumnType(b));
      rowSetMetaDataImpl2.setColumnName(b, rowSetMetaDataImpl1.getColumnName(b));
      rowSetMetaDataImpl2.setNullable(b, 2);
    } 
    this.crsResolve.setMetaData(rowSetMetaDataImpl2);
    if (this.callerColumnCount < 1) {
      if (this.reader.getCloseConnection() == true)
        this.con.close(); 
      return true;
    } 
    bool = cachedRowSetImpl.getShowDeleted();
    cachedRowSetImpl.setShowDeleted(true);
    cachedRowSetImpl.beforeFirst();
    for (b = 1; cachedRowSetImpl.next(); b++) {
      if (cachedRowSetImpl.rowDeleted()) {
        if (deleteOriginalRow(cachedRowSetImpl, this.crsResolve)) {
          this.status.add(b, Integer.valueOf(1));
          l++;
        } else {
          this.status.add(b, Integer.valueOf(3));
        } 
      } else if (cachedRowSetImpl.rowInserted()) {
        preparedStatement = this.con.prepareStatement(this.insertCmd);
        if (insertNewRow(cachedRowSetImpl, preparedStatement, this.crsResolve)) {
          this.status.add(b, Integer.valueOf(2));
          l++;
        } else {
          this.status.add(b, Integer.valueOf(3));
        } 
      } else if (cachedRowSetImpl.rowUpdated()) {
        if (updateOriginalRow(cachedRowSetImpl)) {
          this.status.add(b, Integer.valueOf(0));
          l++;
        } else {
          this.status.add(b, Integer.valueOf(3));
        } 
      } else {
        int k = cachedRowSetImpl.getMetaData().getColumnCount();
        this.status.add(b, Integer.valueOf(3));
        this.crsResolve.moveToInsertRow();
        for (byte b1 = 0; b1 < i; b1++)
          this.crsResolve.updateNull(b1 + true); 
        this.crsResolve.insertRow();
        this.crsResolve.moveToCurrentRow();
      } 
    } 
    if (preparedStatement != null)
      preparedStatement.close(); 
    cachedRowSetImpl.setShowDeleted(bool);
    cachedRowSetImpl.beforeFirst();
    this.crsResolve.beforeFirst();
    if (l != 0L) {
      SyncProviderException syncProviderException = new SyncProviderException(l + " " + this.resBundle.handleGetObject("crswriter.conflictsno").toString());
      SyncResolverImpl syncResolverImpl = (SyncResolverImpl)syncProviderException.getSyncResolver();
      syncResolverImpl.setCachedRowSet(cachedRowSetImpl);
      syncResolverImpl.setCachedRowSetResolver(this.crsResolve);
      syncResolverImpl.setStatus(this.status);
      syncResolverImpl.setCachedRowSetWriter(this);
      throw syncProviderException;
    } 
    return true;
  }
  
  private boolean updateOriginalRow(CachedRowSet paramCachedRowSet) throws SQLException {
    int i = 0;
    byte b = 0;
    ResultSet resultSet = paramCachedRowSet.getOriginalRow();
    resultSet.next();
    try {
      this.updateWhere = buildWhereClause(this.updateWhere, resultSet);
      String str = this.selectCmd.toLowerCase();
      int j = str.indexOf("where");
      if (j != -1) {
        String str1 = this.selectCmd.substring(0, j);
        this.selectCmd = str1;
      } 
      PreparedStatement preparedStatement = this.con.prepareStatement(this.selectCmd + this.updateWhere, 1005, 1007);
      for (i = 0; i < this.keyCols.length; i++) {
        if (this.params[i] != null)
          preparedStatement.setObject(++b, this.params[i]); 
      } 
      try {
        preparedStatement.setMaxRows(paramCachedRowSet.getMaxRows());
        preparedStatement.setMaxFieldSize(paramCachedRowSet.getMaxFieldSize());
        preparedStatement.setEscapeProcessing(paramCachedRowSet.getEscapeProcessing());
        preparedStatement.setQueryTimeout(paramCachedRowSet.getQueryTimeout());
      } catch (Exception exception) {}
      ResultSet resultSet1 = null;
      resultSet1 = preparedStatement.executeQuery();
      ResultSetMetaData resultSetMetaData = resultSet1.getMetaData();
      if (resultSet1.next()) {
        if (resultSet1.next())
          return true; 
        resultSet1.first();
        byte b1 = 0;
        Vector vector = new Vector();
        String str1 = this.updateCmd;
        boolean bool1 = true;
        Object object = null;
        boolean bool2 = true;
        boolean bool3 = true;
        this.crsResolve.moveToInsertRow();
        for (i = 1; i <= this.callerColumnCount; i++) {
          Object object1 = resultSet.getObject(i);
          Object object2 = paramCachedRowSet.getObject(i);
          Object object3 = resultSet1.getObject(i);
          Map map = (paramCachedRowSet.getTypeMap() == null) ? this.con.getTypeMap() : paramCachedRowSet.getTypeMap();
          if (object3 instanceof Struct) {
            Struct struct = (Struct)object3;
            Class clazz = null;
            clazz = (Class)map.get(struct.getSQLTypeName());
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
              object3 = sQLData;
            } 
          } else if (object3 instanceof SQLData) {
            object3 = new SerialStruct((SQLData)object3, map);
          } else if (object3 instanceof Blob) {
            object3 = new SerialBlob((Blob)object3);
          } else if (object3 instanceof Clob) {
            object3 = new SerialClob((Clob)object3);
          } else if (object3 instanceof Array) {
            object3 = new SerialArray((Array)object3, map);
          } 
          bool1 = true;
          if (object3 == null && object1 != null) {
            this.iChangedValsinDbOnly++;
            bool1 = false;
            object = object3;
          } else if (object3 != null && !object3.equals(object1)) {
            this.iChangedValsinDbOnly++;
            bool1 = false;
            object = object3;
          } else if (object1 == null || object2 == null) {
            if (!bool2 || !bool3)
              str1 = str1 + ", "; 
            str1 = str1 + paramCachedRowSet.getMetaData().getColumnName(i);
            vector.add(Integer.valueOf(i));
            str1 = str1 + " = ? ";
            bool2 = false;
          } else if (object1.equals(object2)) {
            b1++;
          } else if (!object1.equals(object2) && paramCachedRowSet.columnUpdated(i)) {
            if (object3.equals(object1)) {
              if (!bool3 || !bool2)
                str1 = str1 + ", "; 
              str1 = str1 + paramCachedRowSet.getMetaData().getColumnName(i);
              vector.add(Integer.valueOf(i));
              str1 = str1 + " = ? ";
              bool3 = false;
            } else {
              bool1 = false;
              object = object3;
              this.iChangedValsInDbAndCRS++;
            } 
          } 
          if (!bool1) {
            this.crsResolve.updateObject(i, object);
          } else {
            this.crsResolve.updateNull(i);
          } 
        } 
        resultSet1.close();
        preparedStatement.close();
        this.crsResolve.insertRow();
        this.crsResolve.moveToCurrentRow();
        if ((!bool2 && vector.size() == 0) || b1 == this.callerColumnCount)
          return false; 
        if (this.iChangedValsInDbAndCRS != 0 || this.iChangedValsinDbOnly != 0)
          return true; 
        str1 = str1 + this.updateWhere;
        preparedStatement = this.con.prepareStatement(str1);
        for (i = 0; i < vector.size(); i++) {
          Object object1 = paramCachedRowSet.getObject(((Integer)vector.get(i)).intValue());
          if (object1 != null) {
            preparedStatement.setObject(i + 1, object1);
          } else {
            preparedStatement.setNull(i + 1, paramCachedRowSet.getMetaData().getColumnType(i + 1));
          } 
        } 
        b = i;
        for (i = 0; i < this.keyCols.length; i++) {
          if (this.params[i] != null)
            preparedStatement.setObject(++b, this.params[i]); 
        } 
        i = preparedStatement.executeUpdate();
        return false;
      } 
      return true;
    } catch (SQLException sQLException) {
      sQLException.printStackTrace();
      this.crsResolve.moveToInsertRow();
      for (i = 1; i <= this.callerColumnCount; i++)
        this.crsResolve.updateNull(i); 
      this.crsResolve.insertRow();
      this.crsResolve.moveToCurrentRow();
      return true;
    } 
  }
  
  private boolean insertNewRow(CachedRowSet paramCachedRowSet, PreparedStatement paramPreparedStatement, CachedRowSetImpl paramCachedRowSetImpl) throws SQLException {
    boolean bool = false;
    try(PreparedStatement null = this.con.prepareStatement(this.selectCmd, 1005, 1007); ResultSet null = preparedStatement.executeQuery(); ResultSet null = this.con.getMetaData().getPrimaryKeys(null, null, paramCachedRowSet.getTableName())) {
      ResultSetMetaData resultSetMetaData = paramCachedRowSet.getMetaData();
      i = resultSetMetaData.getColumnCount();
      String[] arrayOfString = new String[i];
      for (byte b = 0; resultSet1.next(); b++)
        arrayOfString[b] = resultSet1.getString("COLUMN_NAME"); 
      if (resultSet.next())
        for (String str : arrayOfString) {
          if (isPKNameValid(str, resultSetMetaData)) {
            Object object = paramCachedRowSet.getObject(str);
            if (object == null)
              break; 
            String str1 = resultSet.getObject(str).toString();
            if (object.toString().equals(str1)) {
              bool = true;
              this.crsResolve.moveToInsertRow();
              for (byte b1 = 1; b1 <= i; b1++) {
                String str2 = resultSet.getMetaData().getColumnName(b1);
                if (str2.equals(str)) {
                  this.crsResolve.updateObject(b1, str1);
                } else {
                  this.crsResolve.updateNull(b1);
                } 
              } 
              this.crsResolve.insertRow();
              this.crsResolve.moveToCurrentRow();
            } 
          } 
        }  
      if (bool)
        return bool; 
    } 
  }
  
  private boolean deleteOriginalRow(CachedRowSet paramCachedRowSet, CachedRowSetImpl paramCachedRowSetImpl) throws SQLException {
    byte b2 = 0;
    ResultSet resultSet1 = paramCachedRowSet.getOriginalRow();
    resultSet1.next();
    this.deleteWhere = buildWhereClause(this.deleteWhere, resultSet1);
    PreparedStatement preparedStatement = this.con.prepareStatement(this.selectCmd + this.deleteWhere, 1005, 1007);
    byte b1;
    for (b1 = 0; b1 < this.keyCols.length; b1++) {
      if (this.params[b1] != null)
        preparedStatement.setObject(++b2, this.params[b1]); 
    } 
    try {
      preparedStatement.setMaxRows(paramCachedRowSet.getMaxRows());
      preparedStatement.setMaxFieldSize(paramCachedRowSet.getMaxFieldSize());
      preparedStatement.setEscapeProcessing(paramCachedRowSet.getEscapeProcessing());
      preparedStatement.setQueryTimeout(paramCachedRowSet.getQueryTimeout());
    } catch (Exception exception) {}
    ResultSet resultSet2 = preparedStatement.executeQuery();
    if (resultSet2.next() == true) {
      if (resultSet2.next())
        return true; 
      resultSet2.first();
      boolean bool = false;
      paramCachedRowSetImpl.moveToInsertRow();
      for (b1 = 1; b1 <= paramCachedRowSet.getMetaData().getColumnCount(); b1++) {
        Object object1 = resultSet1.getObject(b1);
        Object object2 = resultSet2.getObject(b1);
        if (object1 != null && object2 != null) {
          if (!object1.toString().equals(object2.toString())) {
            bool = true;
            paramCachedRowSetImpl.updateObject(b1, resultSet1.getObject(b1));
          } 
        } else {
          paramCachedRowSetImpl.updateNull(b1);
        } 
      } 
      paramCachedRowSetImpl.insertRow();
      paramCachedRowSetImpl.moveToCurrentRow();
      if (bool)
        return true; 
      String str = this.deleteCmd + this.deleteWhere;
      preparedStatement = this.con.prepareStatement(str);
      b2 = 0;
      for (b1 = 0; b1 < this.keyCols.length; b1++) {
        if (this.params[b1] != null)
          preparedStatement.setObject(++b2, this.params[b1]); 
      } 
      if (preparedStatement.executeUpdate() != 1)
        return true; 
      preparedStatement.close();
    } else {
      return true;
    } 
    return false;
  }
  
  public void setReader(CachedRowSetReader paramCachedRowSetReader) throws SQLException { this.reader = paramCachedRowSetReader; }
  
  public CachedRowSetReader getReader() throws SQLException { return this.reader; }
  
  private void initSQLStatements(CachedRowSet paramCachedRowSet) throws SQLException {
    this.callerMd = paramCachedRowSet.getMetaData();
    this.callerColumnCount = this.callerMd.getColumnCount();
    if (this.callerColumnCount < 1)
      return; 
    String str1 = paramCachedRowSet.getTableName();
    if (str1 == null) {
      str1 = this.callerMd.getTableName(1);
      if (str1 == null || str1.length() == 0)
        throw new SQLException(this.resBundle.handleGetObject("crswriter.tname").toString()); 
    } 
    String str2 = this.callerMd.getCatalogName(1);
    String str3 = this.callerMd.getSchemaName(1);
    DatabaseMetaData databaseMetaData = this.con.getMetaData();
    this.selectCmd = "SELECT ";
    byte b;
    for (b = 1; b <= this.callerColumnCount; b++) {
      this.selectCmd += this.callerMd.getColumnName(b);
      if (b < this.callerMd.getColumnCount()) {
        this.selectCmd += ", ";
      } else {
        this.selectCmd += " ";
      } 
    } 
    this.selectCmd += "FROM " + buildTableName(databaseMetaData, str2, str3, str1);
    this.updateCmd = "UPDATE " + buildTableName(databaseMetaData, str2, str3, str1);
    String str4 = this.updateCmd.toLowerCase();
    int i = str4.indexOf("where");
    if (i != -1)
      this.updateCmd = this.updateCmd.substring(0, i); 
    this.updateCmd += "SET ";
    this.insertCmd = "INSERT INTO " + buildTableName(databaseMetaData, str2, str3, str1);
    this.insertCmd += "(";
    for (b = 1; b <= this.callerColumnCount; b++) {
      this.insertCmd += this.callerMd.getColumnName(b);
      if (b < this.callerMd.getColumnCount()) {
        this.insertCmd += ", ";
      } else {
        this.insertCmd += ") VALUES (";
      } 
    } 
    for (b = 1; b <= this.callerColumnCount; b++) {
      this.insertCmd += "?";
      if (b < this.callerColumnCount) {
        this.insertCmd += ", ";
      } else {
        this.insertCmd += ")";
      } 
    } 
    this.deleteCmd = "DELETE FROM " + buildTableName(databaseMetaData, str2, str3, str1);
    buildKeyDesc(paramCachedRowSet);
  }
  
  private String buildTableName(DatabaseMetaData paramDatabaseMetaData, String paramString1, String paramString2, String paramString3) throws SQLException {
    null = "";
    paramString1 = paramString1.trim();
    paramString2 = paramString2.trim();
    paramString3 = paramString3.trim();
    if (paramDatabaseMetaData.isCatalogAtStart() == true) {
      if (paramString1 != null && paramString1.length() > 0)
        null = null + paramString1 + paramDatabaseMetaData.getCatalogSeparator(); 
      if (paramString2 != null && paramString2.length() > 0)
        null = null + paramString2 + "."; 
      null = null + paramString3;
    } else {
      if (paramString2 != null && paramString2.length() > 0)
        null = null + paramString2 + "."; 
      null = null + paramString3;
      if (paramString1 != null && paramString1.length() > 0)
        null = null + paramDatabaseMetaData.getCatalogSeparator() + paramString1; 
    } 
    return null + " ";
  }
  
  private void buildKeyDesc(CachedRowSet paramCachedRowSet) throws SQLException {
    this.keyCols = paramCachedRowSet.getKeyColumns();
    ResultSetMetaData resultSetMetaData = paramCachedRowSet.getMetaData();
    if (this.keyCols == null || this.keyCols.length == 0) {
      ArrayList arrayList = new ArrayList();
      byte b;
      for (b = 0; b < this.callerColumnCount; b++) {
        if (resultSetMetaData.getColumnType(b + true) != 2005 && resultSetMetaData.getColumnType(b + true) != 2002 && resultSetMetaData.getColumnType(b + true) != 2009 && resultSetMetaData.getColumnType(b + true) != 2004 && resultSetMetaData.getColumnType(b + true) != 2003 && resultSetMetaData.getColumnType(b + true) != 1111)
          arrayList.add(Integer.valueOf(b + true)); 
      } 
      this.keyCols = new int[arrayList.size()];
      for (b = 0; b < arrayList.size(); b++)
        this.keyCols[b] = ((Integer)arrayList.get(b)).intValue(); 
    } 
    this.params = new Object[this.keyCols.length];
  }
  
  private String buildWhereClause(String paramString, ResultSet paramResultSet) throws SQLException {
    paramString = "WHERE ";
    for (byte b = 0; b < this.keyCols.length; b++) {
      if (b)
        paramString = paramString + "AND "; 
      paramString = paramString + this.callerMd.getColumnName(this.keyCols[b]);
      this.params[b] = paramResultSet.getObject(this.keyCols[b]);
      if (paramResultSet.wasNull() == true) {
        paramString = paramString + " IS NULL ";
      } else {
        paramString = paramString + " = ? ";
      } 
    } 
    return paramString;
  }
  
  void updateResolvedConflictToDB(CachedRowSet paramCachedRowSet, Connection paramConnection) throws SQLException {
    String str1 = "WHERE ";
    String str2 = " ";
    String str3 = "UPDATE ";
    int i = paramCachedRowSet.getMetaData().getColumnCount();
    int[] arrayOfInt = paramCachedRowSet.getKeyColumns();
    String str4 = "";
    str1 = buildWhereClause(str1, paramCachedRowSet);
    if (arrayOfInt == null || arrayOfInt.length == 0) {
      arrayOfInt = new int[i];
      byte b1 = 0;
      while (b1 < arrayOfInt.length)
        arrayOfInt[b1++] = b1; 
    } 
    Object[] arrayOfObject = new Object[arrayOfInt.length];
    str3 = "UPDATE " + buildTableName(paramConnection.getMetaData(), paramCachedRowSet.getMetaData().getCatalogName(1), paramCachedRowSet.getMetaData().getSchemaName(1), paramCachedRowSet.getTableName());
    str3 = str3 + "SET ";
    boolean bool = true;
    byte b;
    for (b = 1; b <= i; b++) {
      if (paramCachedRowSet.columnUpdated(b)) {
        if (!bool)
          str4 = str4 + ", "; 
        str4 = str4 + paramCachedRowSet.getMetaData().getColumnName(b);
        str4 = str4 + " = ? ";
        bool = false;
      } 
    } 
    str3 = str3 + str4;
    str1 = "WHERE ";
    for (b = 0; b < arrayOfInt.length; b++) {
      if (b > 0)
        str1 = str1 + "AND "; 
      str1 = str1 + paramCachedRowSet.getMetaData().getColumnName(arrayOfInt[b]);
      arrayOfObject[b] = paramCachedRowSet.getObject(arrayOfInt[b]);
      if (paramCachedRowSet.wasNull() == true) {
        str1 = str1 + " IS NULL ";
      } else {
        str1 = str1 + " = ? ";
      } 
    } 
    str3 = str3 + str1;
    PreparedStatement preparedStatement = paramConnection.prepareStatement(str3);
    b = 0;
    int j;
    for (j = 0; j < i; j++) {
      if (paramCachedRowSet.columnUpdated(j + true)) {
        Object object = paramCachedRowSet.getObject(j + true);
        if (object != null) {
          preparedStatement.setObject(++b, object);
        } else {
          preparedStatement.setNull(j + true, paramCachedRowSet.getMetaData().getColumnType(j + true));
        } 
      } 
    } 
    for (j = 0; j < arrayOfInt.length; j++) {
      if (arrayOfObject[j] != null)
        preparedStatement.setObject(++b, arrayOfObject[j]); 
    } 
    j = preparedStatement.executeUpdate();
  }
  
  public void commit() {
    this.con.commit();
    if (this.reader.getCloseConnection() == true)
      this.con.close(); 
  }
  
  public void commit(CachedRowSetImpl paramCachedRowSetImpl, boolean paramBoolean) throws SQLException {
    this.con.commit();
    if (paramBoolean && paramCachedRowSetImpl.getCommand() != null)
      paramCachedRowSetImpl.execute(this.con); 
    if (this.reader.getCloseConnection() == true)
      this.con.close(); 
  }
  
  public void rollback() {
    this.con.rollback();
    if (this.reader.getCloseConnection() == true)
      this.con.close(); 
  }
  
  public void rollback(Savepoint paramSavepoint) throws SQLException {
    this.con.rollback(paramSavepoint);
    if (this.reader.getCloseConnection() == true)
      this.con.close(); 
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream) throws IOException, ClassNotFoundException {
    paramObjectInputStream.defaultReadObject();
    try {
      this.resBundle = JdbcRowSetResourceBundle.getJdbcRowSetResourceBundle();
    } catch (IOException iOException) {
      throw new RuntimeException(iOException);
    } 
  }
  
  private boolean isPKNameValid(String paramString, ResultSetMetaData paramResultSetMetaData) throws SQLException {
    boolean bool = false;
    int i = paramResultSetMetaData.getColumnCount();
    for (byte b = 1; b <= i; b++) {
      String str = paramResultSetMetaData.getColumnClassName(b);
      if (str.equalsIgnoreCase(paramString)) {
        bool = true;
        break;
      } 
    } 
    return bool;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\rowset\internal\CachedRowSetWriter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */