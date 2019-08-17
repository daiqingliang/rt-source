package java.sql;

public static enum JDBCType implements SQLType {
  BIT(Integer.valueOf(-7)),
  TINYINT(Integer.valueOf(-6)),
  SMALLINT(Integer.valueOf(5)),
  INTEGER(Integer.valueOf(4)),
  BIGINT(Integer.valueOf(-5)),
  FLOAT(Integer.valueOf(6)),
  REAL(Integer.valueOf(7)),
  DOUBLE(Integer.valueOf(8)),
  NUMERIC(Integer.valueOf(2)),
  DECIMAL(Integer.valueOf(3)),
  CHAR(Integer.valueOf(1)),
  VARCHAR(Integer.valueOf(12)),
  LONGVARCHAR(Integer.valueOf(-1)),
  DATE(Integer.valueOf(91)),
  TIME(Integer.valueOf(92)),
  TIMESTAMP(Integer.valueOf(93)),
  BINARY(Integer.valueOf(-2)),
  VARBINARY(Integer.valueOf(-3)),
  LONGVARBINARY(Integer.valueOf(-4)),
  NULL(Integer.valueOf(0)),
  OTHER(Integer.valueOf(1111)),
  JAVA_OBJECT(Integer.valueOf(2000)),
  DISTINCT(Integer.valueOf(2001)),
  STRUCT(Integer.valueOf(2002)),
  ARRAY(Integer.valueOf(2003)),
  BLOB(Integer.valueOf(2004)),
  CLOB(Integer.valueOf(2005)),
  REF(Integer.valueOf(2006)),
  DATALINK(Integer.valueOf(70)),
  BOOLEAN(Integer.valueOf(16)),
  ROWID(Integer.valueOf(-8)),
  NCHAR(Integer.valueOf(-15)),
  NVARCHAR(Integer.valueOf(-9)),
  LONGNVARCHAR(Integer.valueOf(-16)),
  NCLOB(Integer.valueOf(2011)),
  SQLXML(Integer.valueOf(2009)),
  REF_CURSOR(Integer.valueOf(2012)),
  TIME_WITH_TIMEZONE(Integer.valueOf(2013)),
  TIMESTAMP_WITH_TIMEZONE(Integer.valueOf(2014));
  
  private Integer type;
  
  JDBCType(Integer paramInteger1) { this.type = paramInteger1; }
  
  public String getName() { return name(); }
  
  public String getVendor() { return "java.sql"; }
  
  public Integer getVendorTypeNumber() { return this.type; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\sql\JDBCType.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */