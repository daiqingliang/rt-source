package com.sun.rowset.internal;

import com.sun.rowset.JdbcRowSetResourceBundle;
import java.io.IOException;
import java.io.InputStream;
import java.io.ObjectInputStream;
import java.io.Reader;
import java.io.Serializable;
import java.sql.Connection;
import java.sql.Date;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.Calendar;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;
import javax.sql.RowSet;
import javax.sql.RowSetInternal;
import javax.sql.RowSetReader;
import javax.sql.rowset.CachedRowSet;

public class CachedRowSetReader implements RowSetReader, Serializable {
  private int writerCalls = 0;
  
  private boolean userCon = false;
  
  private int startPosition;
  
  private JdbcRowSetResourceBundle resBundle;
  
  static final long serialVersionUID = 5049738185801363801L;
  
  public CachedRowSetReader() {
    try {
      this.resBundle = JdbcRowSetResourceBundle.getJdbcRowSetResourceBundle();
    } catch (IOException iOException) {
      throw new RuntimeException(iOException);
    } 
  }
  
  public void readData(RowSetInternal paramRowSetInternal) throws SQLException {
    connection = null;
    try {
      cachedRowSet = (CachedRowSet)paramRowSetInternal;
      if (cachedRowSet.getPageSize() == 0 && cachedRowSet.size() > 0)
        cachedRowSet.close(); 
      this.writerCalls = 0;
      this.userCon = false;
      connection = connect(paramRowSetInternal);
      if (connection == null || cachedRowSet.getCommand() == null)
        throw new SQLException(this.resBundle.handleGetObject("crsreader.connecterr").toString()); 
      try {
        connection.setTransactionIsolation(cachedRowSet.getTransactionIsolation());
      } catch (Exception exception) {}
      PreparedStatement preparedStatement = connection.prepareStatement(cachedRowSet.getCommand());
      decodeParams(paramRowSetInternal.getParams(), preparedStatement);
      try {
        preparedStatement.setMaxRows(cachedRowSet.getMaxRows());
        preparedStatement.setMaxFieldSize(cachedRowSet.getMaxFieldSize());
        preparedStatement.setEscapeProcessing(cachedRowSet.getEscapeProcessing());
        preparedStatement.setQueryTimeout(cachedRowSet.getQueryTimeout());
      } catch (Exception exception) {
        throw new SQLException(exception.getMessage());
      } 
      if (cachedRowSet.getCommand().toLowerCase().indexOf("select") != -1) {
        ResultSet resultSet = preparedStatement.executeQuery();
        if (cachedRowSet.getPageSize() == 0) {
          cachedRowSet.populate(resultSet);
        } else {
          preparedStatement = connection.prepareStatement(cachedRowSet.getCommand(), 1004, 1008);
          decodeParams(paramRowSetInternal.getParams(), preparedStatement);
          try {
            preparedStatement.setMaxRows(cachedRowSet.getMaxRows());
            preparedStatement.setMaxFieldSize(cachedRowSet.getMaxFieldSize());
            preparedStatement.setEscapeProcessing(cachedRowSet.getEscapeProcessing());
            preparedStatement.setQueryTimeout(cachedRowSet.getQueryTimeout());
          } catch (Exception exception) {
            throw new SQLException(exception.getMessage());
          } 
          resultSet = preparedStatement.executeQuery();
          cachedRowSet.populate(resultSet, this.startPosition);
        } 
        resultSet.close();
      } else {
        preparedStatement.executeUpdate();
      } 
      preparedStatement.close();
      try {
        connection.commit();
      } catch (SQLException sQLException) {}
      if (getCloseConnection() == true)
        connection.close(); 
    } catch (SQLException sQLException) {
      throw sQLException;
    } finally {
      try {
        if (connection != null && getCloseConnection() == true) {
          try {
            if (!connection.getAutoCommit())
              connection.rollback(); 
          } catch (Exception exception) {}
          connection.close();
          connection = null;
        } 
      } catch (SQLException sQLException) {}
    } 
  }
  
  public boolean reset() throws SQLException {
    this.writerCalls++;
    return (this.writerCalls == 1);
  }
  
  public Connection connect(RowSetInternal paramRowSetInternal) throws SQLException {
    if (paramRowSetInternal.getConnection() != null) {
      this.userCon = true;
      return paramRowSetInternal.getConnection();
    } 
    if (((RowSet)paramRowSetInternal).getDataSourceName() != null)
      try {
        InitialContext initialContext = new InitialContext();
        DataSource dataSource = (DataSource)initialContext.lookup(((RowSet)paramRowSetInternal).getDataSourceName());
        return (((RowSet)paramRowSetInternal).getUsername() != null) ? dataSource.getConnection(((RowSet)paramRowSetInternal).getUsername(), ((RowSet)paramRowSetInternal).getPassword()) : dataSource.getConnection();
      } catch (NamingException namingException) {
        SQLException sQLException = new SQLException(this.resBundle.handleGetObject("crsreader.connect").toString());
        sQLException.initCause(namingException);
        throw sQLException;
      }  
    return (((RowSet)paramRowSetInternal).getUrl() != null) ? DriverManager.getConnection(((RowSet)paramRowSetInternal).getUrl(), ((RowSet)paramRowSetInternal).getUsername(), ((RowSet)paramRowSetInternal).getPassword()) : null;
  }
  
  private void decodeParams(Object[] paramArrayOfObject, PreparedStatement paramPreparedStatement) throws SQLException {
    Object[] arrayOfObject = null;
    for (byte b = 0; b < paramArrayOfObject.length; b++) {
      if (paramArrayOfObject[b] instanceof Object[]) {
        arrayOfObject = (Object[])paramArrayOfObject[b];
        if (arrayOfObject.length == 2) {
          if (arrayOfObject[false] == null) {
            paramPreparedStatement.setNull(b + true, ((Integer)arrayOfObject[1]).intValue());
          } else if (arrayOfObject[0] instanceof Date || arrayOfObject[0] instanceof java.sql.Time || arrayOfObject[0] instanceof java.sql.Timestamp) {
            System.err.println(this.resBundle.handleGetObject("crsreader.datedetected").toString());
            if (arrayOfObject[1] instanceof Calendar) {
              System.err.println(this.resBundle.handleGetObject("crsreader.caldetected").toString());
              paramPreparedStatement.setDate(b + true, (Date)arrayOfObject[0], (Calendar)arrayOfObject[1]);
            } else {
              throw new SQLException(this.resBundle.handleGetObject("crsreader.paramtype").toString());
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
                  throw new SQLException(this.resBundle.handleGetObject("crsreader.paramtype").toString());
              }  
            if (arrayOfObject[1] instanceof Integer && arrayOfObject[2] instanceof Integer) {
              paramPreparedStatement.setObject(b + true, arrayOfObject[0], ((Integer)arrayOfObject[1]).intValue(), ((Integer)arrayOfObject[2]).intValue());
            } else {
              throw new SQLException(this.resBundle.handleGetObject("crsreader.paramtype").toString());
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
  
  protected boolean getCloseConnection() throws SQLException { return !(this.userCon == true); }
  
  public void setStartPosition(int paramInt) { this.startPosition = paramInt; }
  
  private void readObject(ObjectInputStream paramObjectInputStream) throws IOException, ClassNotFoundException {
    paramObjectInputStream.defaultReadObject();
    try {
      this.resBundle = JdbcRowSetResourceBundle.getJdbcRowSetResourceBundle();
    } catch (IOException iOException) {
      throw new RuntimeException(iOException);
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\rowset\internal\CachedRowSetReader.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */