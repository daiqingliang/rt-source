package com.sun.rowset.internal;

import com.sun.rowset.JdbcRowSetResourceBundle;
import com.sun.rowset.WebRowSetImpl;
import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Date;
import java.sql.SQLException;
import java.sql.Time;
import java.sql.Timestamp;
import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Vector;
import javax.sql.RowSet;
import javax.sql.RowSetMetaData;
import javax.sql.rowset.RowSetMetaDataImpl;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.SAXParseException;
import org.xml.sax.helpers.DefaultHandler;
import sun.reflect.misc.ReflectUtil;

public class XmlReaderContentHandler extends DefaultHandler {
  private HashMap<String, Integer> propMap;
  
  private HashMap<String, Integer> colDefMap;
  
  private HashMap<String, Integer> dataMap;
  
  private HashMap<String, Class<?>> typeMap;
  
  private Vector<Object[]> updates;
  
  private Vector<String> keyCols;
  
  private String columnValue;
  
  private String propertyValue;
  
  private String metaDataValue;
  
  private int tag;
  
  private int state;
  
  private WebRowSetImpl rs;
  
  private boolean nullVal;
  
  private boolean emptyStringVal;
  
  private RowSetMetaData md;
  
  private int idx;
  
  private String lastval;
  
  private String Key_map;
  
  private String Value_map;
  
  private String tempStr;
  
  private String tempUpdate;
  
  private String tempCommand;
  
  private Object[] upd;
  
  private String[] properties = { 
      "command", "concurrency", "datasource", "escape-processing", "fetch-direction", "fetch-size", "isolation-level", "key-columns", "map", "max-field-size", 
      "max-rows", "query-timeout", "read-only", "rowset-type", "show-deleted", "table-name", "url", "null", "column", "type", 
      "class", "sync-provider", "sync-provider-name", "sync-provider-vendor", "sync-provider-version", "sync-provider-grade", "data-source-lock" };
  
  private static final int CommandTag = 0;
  
  private static final int ConcurrencyTag = 1;
  
  private static final int DatasourceTag = 2;
  
  private static final int EscapeProcessingTag = 3;
  
  private static final int FetchDirectionTag = 4;
  
  private static final int FetchSizeTag = 5;
  
  private static final int IsolationLevelTag = 6;
  
  private static final int KeycolsTag = 7;
  
  private static final int MapTag = 8;
  
  private static final int MaxFieldSizeTag = 9;
  
  private static final int MaxRowsTag = 10;
  
  private static final int QueryTimeoutTag = 11;
  
  private static final int ReadOnlyTag = 12;
  
  private static final int RowsetTypeTag = 13;
  
  private static final int ShowDeletedTag = 14;
  
  private static final int TableNameTag = 15;
  
  private static final int UrlTag = 16;
  
  private static final int PropNullTag = 17;
  
  private static final int PropColumnTag = 18;
  
  private static final int PropTypeTag = 19;
  
  private static final int PropClassTag = 20;
  
  private static final int SyncProviderTag = 21;
  
  private static final int SyncProviderNameTag = 22;
  
  private static final int SyncProviderVendorTag = 23;
  
  private static final int SyncProviderVersionTag = 24;
  
  private static final int SyncProviderGradeTag = 25;
  
  private static final int DataSourceLock = 26;
  
  private String[] colDef = { 
      "column-count", "column-definition", "column-index", "auto-increment", "case-sensitive", "currency", "nullable", "signed", "searchable", "column-display-size", 
      "column-label", "column-name", "schema-name", "column-precision", "column-scale", "table-name", "catalog-name", "column-type", "column-type-name", "null" };
  
  private static final int ColumnCountTag = 0;
  
  private static final int ColumnDefinitionTag = 1;
  
  private static final int ColumnIndexTag = 2;
  
  private static final int AutoIncrementTag = 3;
  
  private static final int CaseSensitiveTag = 4;
  
  private static final int CurrencyTag = 5;
  
  private static final int NullableTag = 6;
  
  private static final int SignedTag = 7;
  
  private static final int SearchableTag = 8;
  
  private static final int ColumnDisplaySizeTag = 9;
  
  private static final int ColumnLabelTag = 10;
  
  private static final int ColumnNameTag = 11;
  
  private static final int SchemaNameTag = 12;
  
  private static final int ColumnPrecisionTag = 13;
  
  private static final int ColumnScaleTag = 14;
  
  private static final int MetaTableNameTag = 15;
  
  private static final int CatalogNameTag = 16;
  
  private static final int ColumnTypeTag = 17;
  
  private static final int ColumnTypeNameTag = 18;
  
  private static final int MetaNullTag = 19;
  
  private String[] data = { "currentRow", "columnValue", "insertRow", "deleteRow", "insdel", "updateRow", "null", "emptyString" };
  
  private static final int RowTag = 0;
  
  private static final int ColTag = 1;
  
  private static final int InsTag = 2;
  
  private static final int DelTag = 3;
  
  private static final int InsDelTag = 4;
  
  private static final int UpdTag = 5;
  
  private static final int NullTag = 6;
  
  private static final int EmptyStringTag = 7;
  
  private static final int INITIAL = 0;
  
  private static final int PROPERTIES = 1;
  
  private static final int METADATA = 2;
  
  private static final int DATA = 3;
  
  private JdbcRowSetResourceBundle resBundle;
  
  public XmlReaderContentHandler(RowSet paramRowSet) {
    this.rs = (WebRowSetImpl)paramRowSet;
    initMaps();
    this.updates = new Vector();
    this.columnValue = "";
    this.propertyValue = "";
    this.metaDataValue = "";
    this.nullVal = false;
    this.idx = 0;
    this.tempStr = "";
    this.tempUpdate = "";
    this.tempCommand = "";
    try {
      this.resBundle = JdbcRowSetResourceBundle.getJdbcRowSetResourceBundle();
    } catch (IOException iOException) {
      throw new RuntimeException(iOException);
    } 
  }
  
  private void initMaps() {
    this.propMap = new HashMap();
    int i = this.properties.length;
    byte b;
    for (b = 0; b < i; b++)
      this.propMap.put(this.properties[b], Integer.valueOf(b)); 
    this.colDefMap = new HashMap();
    i = this.colDef.length;
    for (b = 0; b < i; b++)
      this.colDefMap.put(this.colDef[b], Integer.valueOf(b)); 
    this.dataMap = new HashMap();
    i = this.data.length;
    for (b = 0; b < i; b++)
      this.dataMap.put(this.data[b], Integer.valueOf(b)); 
    this.typeMap = new HashMap();
  }
  
  public void startDocument() {}
  
  public void endDocument() {}
  
  public void startElement(String paramString1, String paramString2, String paramString3, Attributes paramAttributes) throws SAXException {
    int i;
    String str = "";
    str = paramString2;
    switch (getState()) {
      case 1:
        this.tempCommand = "";
        i = ((Integer)this.propMap.get(str)).intValue();
        if (i == 17) {
          setNullValue(true);
        } else {
          setTag(i);
        } 
        return;
      case 2:
        i = ((Integer)this.colDefMap.get(str)).intValue();
        if (i == 19) {
          setNullValue(true);
        } else {
          setTag(i);
        } 
        return;
      case 3:
        this.tempStr = "";
        this.tempUpdate = "";
        if (this.dataMap.get(str) == null) {
          i = 6;
        } else if (((Integer)this.dataMap.get(str)).intValue() == 7) {
          i = 7;
        } else {
          i = ((Integer)this.dataMap.get(str)).intValue();
        } 
        if (i == 6) {
          setNullValue(true);
        } else if (i == 7) {
          setEmptyStringValue(true);
        } else {
          setTag(i);
          if (i == 0 || i == 3 || i == 2) {
            this.idx = 0;
            try {
              this.rs.moveToInsertRow();
            } catch (SQLException sQLException) {}
          } 
        } 
        return;
    } 
    setState(str);
  }
  
  public void endElement(String paramString1, String paramString2, String paramString3) throws SAXException {
    int i;
    String str = "";
    str = paramString2;
    switch (getState()) {
      case 1:
        if (str.equals("properties")) {
          this.state = 0;
          break;
        } 
        try {
          i = ((Integer)this.propMap.get(str)).intValue();
          switch (i) {
            case 7:
              if (this.keyCols != null) {
                int[] arrayOfInt = new int[this.keyCols.size()];
                for (byte b = 0; b < arrayOfInt.length; b++)
                  arrayOfInt[b] = Integer.parseInt((String)this.keyCols.elementAt(b)); 
                this.rs.setKeyColumns(arrayOfInt);
              } 
              break;
            case 20:
              try {
                this.typeMap.put(this.Key_map, ReflectUtil.forName(this.Value_map));
              } catch (ClassNotFoundException classNotFoundException) {
                throw new SAXException(MessageFormat.format(this.resBundle.handleGetObject("xmlrch.errmap").toString(), new Object[] { classNotFoundException.getMessage() }));
              } 
              break;
            case 8:
              this.rs.setTypeMap(this.typeMap);
              break;
          } 
          if (getNullValue()) {
            setPropertyValue(null);
            setNullValue(false);
          } else {
            setPropertyValue(this.propertyValue);
          } 
        } catch (SQLException sQLException) {
          throw new SAXException(sQLException.getMessage());
        } 
        this.propertyValue = "";
        setTag(-1);
        break;
      case 2:
        if (str.equals("metadata")) {
          try {
            this.rs.setMetaData(this.md);
            this.state = 0;
          } catch (SQLException sQLException) {
            throw new SAXException(MessageFormat.format(this.resBundle.handleGetObject("xmlrch.errmetadata").toString(), new Object[] { sQLException.getMessage() }));
          } 
        } else {
          try {
            if (getNullValue()) {
              setMetaDataValue(null);
              setNullValue(false);
            } else {
              setMetaDataValue(this.metaDataValue);
            } 
          } catch (SQLException sQLException) {
            throw new SAXException(MessageFormat.format(this.resBundle.handleGetObject("xmlrch.errmetadata").toString(), new Object[] { sQLException.getMessage() }));
          } 
          this.metaDataValue = "";
        } 
        setTag(-1);
        break;
      case 3:
        if (str.equals("data")) {
          this.state = 0;
          return;
        } 
        if (this.dataMap.get(str) == null) {
          i = 6;
        } else {
          i = ((Integer)this.dataMap.get(str)).intValue();
        } 
        switch (i) {
          case 1:
            try {
              this.idx++;
              if (getNullValue()) {
                insertValue(null);
                setNullValue(false);
              } else {
                insertValue(this.tempStr);
              } 
              this.columnValue = "";
            } catch (SQLException sQLException) {
              throw new SAXException(MessageFormat.format(this.resBundle.handleGetObject("xmlrch.errinsertval").toString(), new Object[] { sQLException.getMessage() }));
            } 
            break;
          case 0:
            try {
              this.rs.insertRow();
              this.rs.moveToCurrentRow();
              this.rs.next();
              this.rs.setOriginalRow();
              applyUpdates();
            } catch (SQLException sQLException) {
              throw new SAXException(MessageFormat.format(this.resBundle.handleGetObject("xmlrch.errconstr").toString(), new Object[] { sQLException.getMessage() }));
            } 
            break;
          case 3:
            try {
              this.rs.insertRow();
              this.rs.moveToCurrentRow();
              this.rs.next();
              this.rs.setOriginalRow();
              applyUpdates();
              this.rs.deleteRow();
            } catch (SQLException sQLException) {
              throw new SAXException(MessageFormat.format(this.resBundle.handleGetObject("xmlrch.errdel").toString(), new Object[] { sQLException.getMessage() }));
            } 
            break;
          case 2:
            try {
              this.rs.insertRow();
              this.rs.moveToCurrentRow();
              this.rs.next();
              applyUpdates();
            } catch (SQLException sQLException) {
              throw new SAXException(MessageFormat.format(this.resBundle.handleGetObject("xmlrch.errinsert").toString(), new Object[] { sQLException.getMessage() }));
            } 
            break;
          case 4:
            try {
              this.rs.insertRow();
              this.rs.moveToCurrentRow();
              this.rs.next();
              this.rs.setOriginalRow();
              applyUpdates();
            } catch (SQLException sQLException) {
              throw new SAXException(MessageFormat.format(this.resBundle.handleGetObject("xmlrch.errinsdel").toString(), new Object[] { sQLException.getMessage() }));
            } 
            break;
          case 5:
            try {
              if (getNullValue()) {
                insertValue(null);
                setNullValue(false);
                break;
              } 
              if (getEmptyStringValue()) {
                insertValue("");
                setEmptyStringValue(false);
                break;
              } 
              this.updates.add(this.upd);
            } catch (SQLException sQLException) {
              throw new SAXException(MessageFormat.format(this.resBundle.handleGetObject("xmlrch.errupdate").toString(), new Object[] { sQLException.getMessage() }));
            } 
            break;
        } 
        break;
    } 
  }
  
  private void applyUpdates() {
    if (this.updates.size() > 0) {
      try {
        for (Object[] arrayOfObject : this.updates) {
          this.idx = ((Integer)arrayOfObject[0]).intValue();
          if (!this.lastval.equals(arrayOfObject[1]))
            insertValue((String)arrayOfObject[1]); 
        } 
        this.rs.updateRow();
      } catch (SQLException sQLException) {
        throw new SAXException(MessageFormat.format(this.resBundle.handleGetObject("xmlrch.errupdrow").toString(), new Object[] { sQLException.getMessage() }));
      } 
      this.updates.removeAllElements();
    } 
  }
  
  public void characters(char[] paramArrayOfChar, int paramInt1, int paramInt2) throws SAXException {
    try {
      switch (getState()) {
        case 1:
          this.propertyValue = new String(paramArrayOfChar, paramInt1, paramInt2);
          this.tempCommand = this.tempCommand.concat(this.propertyValue);
          this.propertyValue = this.tempCommand;
          if (this.tag == 19) {
            this.Key_map = this.propertyValue;
            break;
          } 
          if (this.tag == 20)
            this.Value_map = this.propertyValue; 
          break;
        case 2:
          if (this.tag == -1)
            break; 
          this.metaDataValue = new String(paramArrayOfChar, paramInt1, paramInt2);
          break;
        case 3:
          setDataValue(paramArrayOfChar, paramInt1, paramInt2);
          break;
      } 
    } catch (SQLException sQLException) {
      throw new SAXException(this.resBundle.handleGetObject("xmlrch.chars").toString() + sQLException.getMessage());
    } 
  }
  
  private void setState(String paramString) throws SAXException {
    if (paramString.equals("webRowSet")) {
      this.state = 0;
    } else if (paramString.equals("properties")) {
      if (this.state != 1) {
        this.state = 1;
      } else {
        this.state = 0;
      } 
    } else if (paramString.equals("metadata")) {
      if (this.state != 2) {
        this.state = 2;
      } else {
        this.state = 0;
      } 
    } else if (paramString.equals("data")) {
      if (this.state != 3) {
        this.state = 3;
      } else {
        this.state = 0;
      } 
    } 
  }
  
  private int getState() { return this.state; }
  
  private void setTag(int paramInt) { this.tag = paramInt; }
  
  private int getTag() { return this.tag; }
  
  private void setNullValue(boolean paramBoolean) { this.nullVal = paramBoolean; }
  
  private boolean getNullValue() { return this.nullVal; }
  
  private void setEmptyStringValue(boolean paramBoolean) { this.emptyStringVal = paramBoolean; }
  
  private boolean getEmptyStringValue() { return this.emptyStringVal; }
  
  private String getStringValue(String paramString) { return paramString; }
  
  private int getIntegerValue(String paramString) { return Integer.parseInt(paramString); }
  
  private boolean getBooleanValue(String paramString) { return Boolean.valueOf(paramString).booleanValue(); }
  
  private BigDecimal getBigDecimalValue(String paramString) { return new BigDecimal(paramString); }
  
  private byte getByteValue(String paramString) { return Byte.parseByte(paramString); }
  
  private short getShortValue(String paramString) { return Short.parseShort(paramString); }
  
  private long getLongValue(String paramString) { return Long.parseLong(paramString); }
  
  private float getFloatValue(String paramString) { return Float.parseFloat(paramString); }
  
  private double getDoubleValue(String paramString) { return Double.parseDouble(paramString); }
  
  private byte[] getBinaryValue(String paramString) { return paramString.getBytes(); }
  
  private Date getDateValue(String paramString) { return new Date(getLongValue(paramString)); }
  
  private Time getTimeValue(String paramString) { return new Time(getLongValue(paramString)); }
  
  private Timestamp getTimestampValue(String paramString) { return new Timestamp(getLongValue(paramString)); }
  
  private void setPropertyValue(String paramString) throws SAXException {
    char c;
    String str;
    boolean bool = getNullValue();
    switch (getTag()) {
      case 0:
        if (bool)
          break; 
        this.rs.setCommand(paramString);
        break;
      case 1:
        if (bool)
          throw new SQLException(this.resBundle.handleGetObject("xmlrch.badvalue").toString()); 
        this.rs.setConcurrency(getIntegerValue(paramString));
        break;
      case 2:
        if (bool) {
          this.rs.setDataSourceName(null);
          break;
        } 
        this.rs.setDataSourceName(paramString);
        break;
      case 3:
        if (bool)
          throw new SQLException(this.resBundle.handleGetObject("xmlrch.badvalue").toString()); 
        this.rs.setEscapeProcessing(getBooleanValue(paramString));
        break;
      case 4:
        if (bool)
          throw new SQLException(this.resBundle.handleGetObject("xmlrch.badvalue").toString()); 
        this.rs.setFetchDirection(getIntegerValue(paramString));
        break;
      case 5:
        if (bool)
          throw new SQLException(this.resBundle.handleGetObject("xmlrch.badvalue").toString()); 
        this.rs.setFetchSize(getIntegerValue(paramString));
        break;
      case 6:
        if (bool)
          throw new SQLException(this.resBundle.handleGetObject("xmlrch.badvalue").toString()); 
        this.rs.setTransactionIsolation(getIntegerValue(paramString));
        break;
      case 18:
        if (this.keyCols == null)
          this.keyCols = new Vector(); 
        this.keyCols.add(paramString);
        break;
      case 9:
        if (bool)
          throw new SQLException(this.resBundle.handleGetObject("xmlrch.badvalue").toString()); 
        this.rs.setMaxFieldSize(getIntegerValue(paramString));
        break;
      case 10:
        if (bool)
          throw new SQLException(this.resBundle.handleGetObject("xmlrch.badvalue").toString()); 
        this.rs.setMaxRows(getIntegerValue(paramString));
        break;
      case 11:
        if (bool)
          throw new SQLException(this.resBundle.handleGetObject("xmlrch.badvalue").toString()); 
        this.rs.setQueryTimeout(getIntegerValue(paramString));
        break;
      case 12:
        if (bool)
          throw new SQLException(this.resBundle.handleGetObject("xmlrch.badvalue").toString()); 
        this.rs.setReadOnly(getBooleanValue(paramString));
        break;
      case 13:
        if (bool)
          throw new SQLException(this.resBundle.handleGetObject("xmlrch.badvalue").toString()); 
        str = getStringValue(paramString);
        c = Character.MIN_VALUE;
        if (str.trim().equals("ResultSet.TYPE_SCROLL_INSENSITIVE")) {
          c = 'Ϭ';
        } else if (str.trim().equals("ResultSet.TYPE_SCROLL_SENSITIVE")) {
          c = 'ϭ';
        } else if (str.trim().equals("ResultSet.TYPE_FORWARD_ONLY")) {
          c = 'ϫ';
        } 
        this.rs.setType(c);
        break;
      case 14:
        if (bool)
          throw new SQLException(this.resBundle.handleGetObject("xmlrch.badvalue").toString()); 
        this.rs.setShowDeleted(getBooleanValue(paramString));
        break;
      case 15:
        if (bool)
          break; 
        this.rs.setTableName(paramString);
        break;
      case 16:
        if (bool) {
          this.rs.setUrl(null);
          break;
        } 
        this.rs.setUrl(paramString);
        break;
      case 22:
        if (bool) {
          this.rs.setSyncProvider(null);
          break;
        } 
        str = paramString.substring(0, paramString.indexOf("@") + 1);
        this.rs.setSyncProvider(str);
        break;
    } 
  }
  
  private void setMetaDataValue(String paramString) throws SAXException {
    boolean bool = getNullValue();
    switch (getTag()) {
      case 0:
        this.md = new RowSetMetaDataImpl();
        this.idx = 0;
        if (bool)
          throw new SQLException(this.resBundle.handleGetObject("xmlrch.badvalue1").toString()); 
        this.md.setColumnCount(getIntegerValue(paramString));
        break;
      case 2:
        this.idx++;
        break;
      case 3:
        if (bool)
          throw new SQLException(this.resBundle.handleGetObject("xmlrch.badvalue1").toString()); 
        this.md.setAutoIncrement(this.idx, getBooleanValue(paramString));
        break;
      case 4:
        if (bool)
          throw new SQLException(this.resBundle.handleGetObject("xmlrch.badvalue1").toString()); 
        this.md.setCaseSensitive(this.idx, getBooleanValue(paramString));
        break;
      case 5:
        if (bool)
          throw new SQLException(this.resBundle.handleGetObject("xmlrch.badvalue1").toString()); 
        this.md.setCurrency(this.idx, getBooleanValue(paramString));
        break;
      case 6:
        if (bool)
          throw new SQLException(this.resBundle.handleGetObject("xmlrch.badvalue1").toString()); 
        this.md.setNullable(this.idx, getIntegerValue(paramString));
        break;
      case 7:
        if (bool)
          throw new SQLException(this.resBundle.handleGetObject("xmlrch.badvalue1").toString()); 
        this.md.setSigned(this.idx, getBooleanValue(paramString));
        break;
      case 8:
        if (bool)
          throw new SQLException(this.resBundle.handleGetObject("xmlrch.badvalue1").toString()); 
        this.md.setSearchable(this.idx, getBooleanValue(paramString));
        break;
      case 9:
        if (bool)
          throw new SQLException(this.resBundle.handleGetObject("xmlrch.badvalue1").toString()); 
        this.md.setColumnDisplaySize(this.idx, getIntegerValue(paramString));
        break;
      case 10:
        if (bool) {
          this.md.setColumnLabel(this.idx, null);
          break;
        } 
        this.md.setColumnLabel(this.idx, paramString);
        break;
      case 11:
        if (bool) {
          this.md.setColumnName(this.idx, null);
          break;
        } 
        this.md.setColumnName(this.idx, paramString);
        break;
      case 12:
        if (bool) {
          this.md.setSchemaName(this.idx, null);
          break;
        } 
        this.md.setSchemaName(this.idx, paramString);
        break;
      case 13:
        if (bool)
          throw new SQLException(this.resBundle.handleGetObject("xmlrch.badvalue1").toString()); 
        this.md.setPrecision(this.idx, getIntegerValue(paramString));
        break;
      case 14:
        if (bool)
          throw new SQLException(this.resBundle.handleGetObject("xmlrch.badvalue1").toString()); 
        this.md.setScale(this.idx, getIntegerValue(paramString));
        break;
      case 15:
        if (bool) {
          this.md.setTableName(this.idx, null);
          break;
        } 
        this.md.setTableName(this.idx, paramString);
        break;
      case 16:
        if (bool) {
          this.md.setCatalogName(this.idx, null);
          break;
        } 
        this.md.setCatalogName(this.idx, paramString);
        break;
      case 17:
        if (bool)
          throw new SQLException(this.resBundle.handleGetObject("xmlrch.badvalue1").toString()); 
        this.md.setColumnType(this.idx, getIntegerValue(paramString));
        break;
      case 18:
        if (bool) {
          this.md.setColumnTypeName(this.idx, null);
          break;
        } 
        this.md.setColumnTypeName(this.idx, paramString);
        break;
    } 
  }
  
  private void setDataValue(char[] paramArrayOfChar, int paramInt1, int paramInt2) throws SAXException {
    switch (getTag()) {
      case 1:
        this.columnValue = new String(paramArrayOfChar, paramInt1, paramInt2);
        this.tempStr = this.tempStr.concat(this.columnValue);
        break;
      case 5:
        this.upd = new Object[2];
        this.tempUpdate = this.tempUpdate.concat(new String(paramArrayOfChar, paramInt1, paramInt2));
        this.upd[0] = Integer.valueOf(this.idx);
        this.upd[1] = this.tempUpdate;
        this.lastval = (String)this.upd[1];
        break;
    } 
  }
  
  private void insertValue(String paramString) throws SAXException {
    if (getNullValue()) {
      this.rs.updateNull(this.idx);
      return;
    } 
    int i = this.rs.getMetaData().getColumnType(this.idx);
    switch (i) {
      case -7:
        this.rs.updateBoolean(this.idx, getBooleanValue(paramString));
        break;
      case 16:
        this.rs.updateBoolean(this.idx, getBooleanValue(paramString));
        break;
      case -6:
      case 5:
        this.rs.updateShort(this.idx, getShortValue(paramString));
        break;
      case 4:
        this.rs.updateInt(this.idx, getIntegerValue(paramString));
        break;
      case -5:
        this.rs.updateLong(this.idx, getLongValue(paramString));
        break;
      case 6:
      case 7:
        this.rs.updateFloat(this.idx, getFloatValue(paramString));
        break;
      case 8:
        this.rs.updateDouble(this.idx, getDoubleValue(paramString));
        break;
      case 2:
      case 3:
        this.rs.updateObject(this.idx, getBigDecimalValue(paramString));
        break;
      case -4:
      case -3:
      case -2:
        this.rs.updateBytes(this.idx, getBinaryValue(paramString));
        break;
      case 91:
        this.rs.updateDate(this.idx, getDateValue(paramString));
        break;
      case 92:
        this.rs.updateTime(this.idx, getTimeValue(paramString));
        break;
      case 93:
        this.rs.updateTimestamp(this.idx, getTimestampValue(paramString));
        break;
      case -1:
      case 1:
      case 12:
        this.rs.updateString(this.idx, getStringValue(paramString));
        break;
    } 
  }
  
  public void error(SAXParseException paramSAXParseException) throws SAXParseException { throw paramSAXParseException; }
  
  public void warning(SAXParseException paramSAXParseException) throws SAXParseException { System.out.println(MessageFormat.format(this.resBundle.handleGetObject("xmlrch.warning").toString(), new Object[] { paramSAXParseException.getMessage(), Integer.valueOf(paramSAXParseException.getLineNumber()), paramSAXParseException.getSystemId() })); }
  
  public void notationDecl(String paramString1, String paramString2, String paramString3) throws SAXException {}
  
  public void unparsedEntityDecl(String paramString1, String paramString2, String paramString3, String paramString4) {}
  
  private Row getPresentRow(WebRowSetImpl paramWebRowSetImpl) throws SQLException { return null; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\rowset\internal\XmlReaderContentHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */