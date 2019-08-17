package com.sun.rowset.internal;

import com.sun.rowset.JdbcRowSetResourceBundle;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Serializable;
import java.io.Writer;
import java.math.BigDecimal;
import java.sql.Date;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.MessageFormat;
import java.util.Map;
import java.util.Stack;
import javax.sql.RowSet;
import javax.sql.RowSetInternal;
import javax.sql.rowset.WebRowSet;
import javax.sql.rowset.spi.XmlWriter;

public class WebRowSetXmlWriter implements XmlWriter, Serializable {
  private Writer writer;
  
  private Stack<String> stack;
  
  private JdbcRowSetResourceBundle resBundle;
  
  static final long serialVersionUID = 7163134986189677641L;
  
  public WebRowSetXmlWriter() {
    try {
      this.resBundle = JdbcRowSetResourceBundle.getJdbcRowSetResourceBundle();
    } catch (IOException iOException) {
      throw new RuntimeException(iOException);
    } 
  }
  
  public void writeXML(WebRowSet paramWebRowSet, Writer paramWriter) throws SQLException {
    this.stack = new Stack();
    this.writer = paramWriter;
    writeRowSet(paramWebRowSet);
  }
  
  public void writeXML(WebRowSet paramWebRowSet, OutputStream paramOutputStream) throws SQLException {
    this.stack = new Stack();
    this.writer = new OutputStreamWriter(paramOutputStream);
    writeRowSet(paramWebRowSet);
  }
  
  private void writeRowSet(WebRowSet paramWebRowSet) throws SQLException {
    try {
      startHeader();
      writeProperties(paramWebRowSet);
      writeMetaData(paramWebRowSet);
      writeData(paramWebRowSet);
      endHeader();
    } catch (IOException iOException) {
      throw new SQLException(MessageFormat.format(this.resBundle.handleGetObject("wrsxmlwriter.ioex").toString(), new Object[] { iOException.getMessage() }));
    } 
  }
  
  private void startHeader() {
    setTag("webRowSet");
    this.writer.write("<?xml version=\"1.0\"?>\n");
    this.writer.write("<webRowSet xmlns=\"http://java.sun.com/xml/ns/jdbc\" xmlns:xsi=\"http://www.w3.org/2001/XMLSchema-instance\"\n");
    this.writer.write("xsi:schemaLocation=\"http://java.sun.com/xml/ns/jdbc http://java.sun.com/xml/ns/jdbc/webrowset.xsd\">\n");
  }
  
  private void endHeader() { endTag("webRowSet"); }
  
  private void writeProperties(WebRowSet paramWebRowSet) throws SQLException {
    beginSection("properties");
    try {
      propString("command", processSpecialCharacters(paramWebRowSet.getCommand()));
      propInteger("concurrency", paramWebRowSet.getConcurrency());
      propString("datasource", paramWebRowSet.getDataSourceName());
      propBoolean("escape-processing", paramWebRowSet.getEscapeProcessing());
      try {
        propInteger("fetch-direction", paramWebRowSet.getFetchDirection());
      } catch (SQLException sQLException) {}
      propInteger("fetch-size", paramWebRowSet.getFetchSize());
      propInteger("isolation-level", paramWebRowSet.getTransactionIsolation());
      beginSection("key-columns");
      int[] arrayOfInt = paramWebRowSet.getKeyColumns();
      for (byte b = 0; arrayOfInt != null && b < arrayOfInt.length; b++)
        propInteger("column", arrayOfInt[b]); 
      endSection("key-columns");
      beginSection("map");
      Map map = paramWebRowSet.getTypeMap();
      if (map != null)
        for (Map.Entry entry : map.entrySet()) {
          propString("type", (String)entry.getKey());
          propString("class", ((Class)entry.getValue()).getName());
        }  
      endSection("map");
      propInteger("max-field-size", paramWebRowSet.getMaxFieldSize());
      propInteger("max-rows", paramWebRowSet.getMaxRows());
      propInteger("query-timeout", paramWebRowSet.getQueryTimeout());
      propBoolean("read-only", paramWebRowSet.isReadOnly());
      int i = paramWebRowSet.getType();
      String str1 = "";
      if (i == 1003) {
        str1 = "ResultSet.TYPE_FORWARD_ONLY";
      } else if (i == 1004) {
        str1 = "ResultSet.TYPE_SCROLL_INSENSITIVE";
      } else if (i == 1005) {
        str1 = "ResultSet.TYPE_SCROLL_SENSITIVE";
      } 
      propString("rowset-type", str1);
      propBoolean("show-deleted", paramWebRowSet.getShowDeleted());
      propString("table-name", paramWebRowSet.getTableName());
      propString("url", paramWebRowSet.getUrl());
      beginSection("sync-provider");
      String str2 = paramWebRowSet.getSyncProvider().toString();
      String str3 = str2.substring(0, paramWebRowSet.getSyncProvider().toString().indexOf("@"));
      propString("sync-provider-name", str3);
      propString("sync-provider-vendor", "Oracle Corporation");
      propString("sync-provider-version", "1.0");
      propInteger("sync-provider-grade", paramWebRowSet.getSyncProvider().getProviderGrade());
      propInteger("data-source-lock", paramWebRowSet.getSyncProvider().getDataSourceLock());
      endSection("sync-provider");
    } catch (SQLException sQLException) {
      throw new IOException(MessageFormat.format(this.resBundle.handleGetObject("wrsxmlwriter.sqlex").toString(), new Object[] { sQLException.getMessage() }));
    } 
    endSection("properties");
  }
  
  private void writeMetaData(WebRowSet paramWebRowSet) throws SQLException {
    beginSection("metadata");
    try {
      ResultSetMetaData resultSetMetaData = paramWebRowSet.getMetaData();
      int i = resultSetMetaData.getColumnCount();
      propInteger("column-count", i);
      for (byte b = 1; b <= i; b++) {
        beginSection("column-definition");
        propInteger("column-index", b);
        propBoolean("auto-increment", resultSetMetaData.isAutoIncrement(b));
        propBoolean("case-sensitive", resultSetMetaData.isCaseSensitive(b));
        propBoolean("currency", resultSetMetaData.isCurrency(b));
        propInteger("nullable", resultSetMetaData.isNullable(b));
        propBoolean("signed", resultSetMetaData.isSigned(b));
        propBoolean("searchable", resultSetMetaData.isSearchable(b));
        propInteger("column-display-size", resultSetMetaData.getColumnDisplaySize(b));
        propString("column-label", resultSetMetaData.getColumnLabel(b));
        propString("column-name", resultSetMetaData.getColumnName(b));
        propString("schema-name", resultSetMetaData.getSchemaName(b));
        propInteger("column-precision", resultSetMetaData.getPrecision(b));
        propInteger("column-scale", resultSetMetaData.getScale(b));
        propString("table-name", resultSetMetaData.getTableName(b));
        propString("catalog-name", resultSetMetaData.getCatalogName(b));
        propInteger("column-type", resultSetMetaData.getColumnType(b));
        propString("column-type-name", resultSetMetaData.getColumnTypeName(b));
        endSection("column-definition");
      } 
    } catch (SQLException sQLException) {
      throw new IOException(MessageFormat.format(this.resBundle.handleGetObject("wrsxmlwriter.sqlex").toString(), new Object[] { sQLException.getMessage() }));
    } 
    endSection("metadata");
  }
  
  private void writeData(WebRowSet paramWebRowSet) throws SQLException {
    try {
      ResultSetMetaData resultSetMetaData = paramWebRowSet.getMetaData();
      int i = resultSetMetaData.getColumnCount();
      beginSection("data");
      paramWebRowSet.beforeFirst();
      paramWebRowSet.setShowDeleted(true);
      while (paramWebRowSet.next()) {
        if (paramWebRowSet.rowDeleted() && paramWebRowSet.rowInserted()) {
          beginSection("modifyRow");
        } else if (paramWebRowSet.rowDeleted()) {
          beginSection("deleteRow");
        } else if (paramWebRowSet.rowInserted()) {
          beginSection("insertRow");
        } else {
          beginSection("currentRow");
        } 
        for (byte b = 1; b <= i; b++) {
          if (paramWebRowSet.columnUpdated(b)) {
            ResultSet resultSet = paramWebRowSet.getOriginalRow();
            resultSet.next();
            beginTag("columnValue");
            writeValue(b, (RowSet)resultSet);
            endTag("columnValue");
            beginTag("updateRow");
            writeValue(b, paramWebRowSet);
            endTag("updateRow");
          } else {
            beginTag("columnValue");
            writeValue(b, paramWebRowSet);
            endTag("columnValue");
          } 
        } 
        endSection();
      } 
      endSection("data");
    } catch (SQLException sQLException) {
      throw new IOException(MessageFormat.format(this.resBundle.handleGetObject("wrsxmlwriter.sqlex").toString(), new Object[] { sQLException.getMessage() }));
    } 
  }
  
  private void writeValue(int paramInt, RowSet paramRowSet) throws IOException {
    try {
      Timestamp timestamp;
      Time time;
      Date date;
      double d;
      float f;
      long l;
      int j;
      short s;
      boolean bool;
      int i = paramRowSet.getMetaData().getColumnType(paramInt);
      switch (i) {
        case -7:
        case 16:
          bool = paramRowSet.getBoolean(paramInt);
          if (paramRowSet.wasNull()) {
            writeNull();
          } else {
            writeBoolean(bool);
          } 
        case -6:
        case 5:
          s = paramRowSet.getShort(paramInt);
          if (paramRowSet.wasNull()) {
            writeNull();
          } else {
            writeShort(s);
          } 
        case 4:
          j = paramRowSet.getInt(paramInt);
          if (paramRowSet.wasNull()) {
            writeNull();
          } else {
            writeInteger(j);
          } 
        case -5:
          l = paramRowSet.getLong(paramInt);
          if (paramRowSet.wasNull()) {
            writeNull();
          } else {
            writeLong(l);
          } 
        case 6:
        case 7:
          f = paramRowSet.getFloat(paramInt);
          if (paramRowSet.wasNull()) {
            writeNull();
          } else {
            writeFloat(f);
          } 
        case 8:
          d = paramRowSet.getDouble(paramInt);
          if (paramRowSet.wasNull()) {
            writeNull();
          } else {
            writeDouble(d);
          } 
        case 2:
        case 3:
          writeBigDecimal(paramRowSet.getBigDecimal(paramInt));
        case -4:
        case -3:
        case -2:
          return;
        case 91:
          date = paramRowSet.getDate(paramInt);
          if (paramRowSet.wasNull()) {
            writeNull();
          } else {
            writeLong(date.getTime());
          } 
        case 92:
          time = paramRowSet.getTime(paramInt);
          if (paramRowSet.wasNull()) {
            writeNull();
          } else {
            writeLong(time.getTime());
          } 
        case 93:
          timestamp = paramRowSet.getTimestamp(paramInt);
          if (paramRowSet.wasNull()) {
            writeNull();
          } else {
            writeLong(timestamp.getTime());
          } 
        case -1:
        case 1:
        case 12:
          writeStringData(paramRowSet.getString(paramInt));
      } 
      System.out.println(this.resBundle.handleGetObject("wsrxmlwriter.notproper").toString());
    } catch (SQLException sQLException) {
      throw new IOException(this.resBundle.handleGetObject("wrsxmlwriter.failedwrite").toString() + sQLException.getMessage());
    } 
  }
  
  private void beginSection(String paramString) throws IOException {
    setTag(paramString);
    writeIndent(this.stack.size());
    this.writer.write("<" + paramString + ">\n");
  }
  
  private void endSection(String paramString) throws IOException {
    writeIndent(this.stack.size());
    String str = getTag();
    if (str.indexOf("webRowSet") != -1)
      str = "webRowSet"; 
    if (paramString.equals(str))
      this.writer.write("</" + str + ">\n"); 
    this.writer.flush();
  }
  
  private void endSection() {
    writeIndent(this.stack.size());
    String str = getTag();
    this.writer.write("</" + str + ">\n");
    this.writer.flush();
  }
  
  private void beginTag(String paramString) throws IOException {
    setTag(paramString);
    writeIndent(this.stack.size());
    this.writer.write("<" + paramString + ">");
  }
  
  private void endTag(String paramString) throws IOException {
    String str = getTag();
    if (paramString.equals(str))
      this.writer.write("</" + str + ">\n"); 
    this.writer.flush();
  }
  
  private void emptyTag(String paramString) throws IOException { this.writer.write("<" + paramString + "/>"); }
  
  private void setTag(String paramString) throws IOException { this.stack.push(paramString); }
  
  private String getTag() { return (String)this.stack.pop(); }
  
  private void writeNull() { emptyTag("null"); }
  
  private void writeStringData(String paramString) throws IOException {
    if (paramString == null) {
      writeNull();
    } else if (paramString.equals("")) {
      writeEmptyString();
    } else {
      paramString = processSpecialCharacters(paramString);
      this.writer.write(paramString);
    } 
  }
  
  private void writeString(String paramString) throws IOException {
    if (paramString != null) {
      this.writer.write(paramString);
    } else {
      writeNull();
    } 
  }
  
  private void writeShort(short paramShort) throws IOException { this.writer.write(Short.toString(paramShort)); }
  
  private void writeLong(long paramLong) throws IOException { this.writer.write(Long.toString(paramLong)); }
  
  private void writeInteger(int paramInt) throws IOException { this.writer.write(Integer.toString(paramInt)); }
  
  private void writeBoolean(boolean paramBoolean) throws IOException { this.writer.write(Boolean.valueOf(paramBoolean).toString()); }
  
  private void writeFloat(float paramFloat) throws IOException { this.writer.write(Float.toString(paramFloat)); }
  
  private void writeDouble(double paramDouble) throws IOException { this.writer.write(Double.toString(paramDouble)); }
  
  private void writeBigDecimal(BigDecimal paramBigDecimal) throws IOException {
    if (paramBigDecimal != null) {
      this.writer.write(paramBigDecimal.toString());
    } else {
      emptyTag("null");
    } 
  }
  
  private void writeIndent(int paramInt) throws IOException {
    for (byte b = 1; b < paramInt; b++)
      this.writer.write("  "); 
  }
  
  private void propString(String paramString1, String paramString2) throws IOException {
    beginTag(paramString1);
    writeString(paramString2);
    endTag(paramString1);
  }
  
  private void propInteger(String paramString, int paramInt) throws IOException {
    beginTag(paramString);
    writeInteger(paramInt);
    endTag(paramString);
  }
  
  private void propBoolean(String paramString, boolean paramBoolean) throws IOException {
    beginTag(paramString);
    writeBoolean(paramBoolean);
    endTag(paramString);
  }
  
  private void writeEmptyString() { emptyTag("emptyString"); }
  
  public boolean writeData(RowSetInternal paramRowSetInternal) { return false; }
  
  private String processSpecialCharacters(String paramString) {
    if (paramString == null)
      return null; 
    char[] arrayOfChar = paramString.toCharArray();
    String str = "";
    for (byte b = 0; b < arrayOfChar.length; b++) {
      if (arrayOfChar[b] == '&') {
        str = str.concat("&amp;");
      } else if (arrayOfChar[b] == '<') {
        str = str.concat("&lt;");
      } else if (arrayOfChar[b] == '>') {
        str = str.concat("&gt;");
      } else if (arrayOfChar[b] == '\'') {
        str = str.concat("&apos;");
      } else if (arrayOfChar[b] == '"') {
        str = str.concat("&quot;");
      } else {
        str = str.concat(String.valueOf(arrayOfChar[b]));
      } 
    } 
    return str;
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream) throws IOException, ClassNotFoundException {
    paramObjectInputStream.defaultReadObject();
    try {
      this.resBundle = JdbcRowSetResourceBundle.getJdbcRowSetResourceBundle();
    } catch (IOException iOException) {
      throw new RuntimeException(iOException);
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\rowset\internal\WebRowSetXmlWriter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */