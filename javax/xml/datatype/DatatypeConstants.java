package javax.xml.datatype;

import javax.xml.namespace.QName;

public final class DatatypeConstants {
  public static final int JANUARY = 1;
  
  public static final int FEBRUARY = 2;
  
  public static final int MARCH = 3;
  
  public static final int APRIL = 4;
  
  public static final int MAY = 5;
  
  public static final int JUNE = 6;
  
  public static final int JULY = 7;
  
  public static final int AUGUST = 8;
  
  public static final int SEPTEMBER = 9;
  
  public static final int OCTOBER = 10;
  
  public static final int NOVEMBER = 11;
  
  public static final int DECEMBER = 12;
  
  public static final int LESSER = -1;
  
  public static final int EQUAL = 0;
  
  public static final int GREATER = 1;
  
  public static final int INDETERMINATE = 2;
  
  public static final int FIELD_UNDEFINED = -2147483648;
  
  public static final Field YEARS = new Field("YEARS", 0, null);
  
  public static final Field MONTHS = new Field("MONTHS", 1, null);
  
  public static final Field DAYS = new Field("DAYS", 2, null);
  
  public static final Field HOURS = new Field("HOURS", 3, null);
  
  public static final Field MINUTES = new Field("MINUTES", 4, null);
  
  public static final Field SECONDS = new Field("SECONDS", 5, null);
  
  public static final QName DATETIME = new QName("http://www.w3.org/2001/XMLSchema", "dateTime");
  
  public static final QName TIME = new QName("http://www.w3.org/2001/XMLSchema", "time");
  
  public static final QName DATE = new QName("http://www.w3.org/2001/XMLSchema", "date");
  
  public static final QName GYEARMONTH = new QName("http://www.w3.org/2001/XMLSchema", "gYearMonth");
  
  public static final QName GMONTHDAY = new QName("http://www.w3.org/2001/XMLSchema", "gMonthDay");
  
  public static final QName GYEAR = new QName("http://www.w3.org/2001/XMLSchema", "gYear");
  
  public static final QName GMONTH = new QName("http://www.w3.org/2001/XMLSchema", "gMonth");
  
  public static final QName GDAY = new QName("http://www.w3.org/2001/XMLSchema", "gDay");
  
  public static final QName DURATION = new QName("http://www.w3.org/2001/XMLSchema", "duration");
  
  public static final QName DURATION_DAYTIME = new QName("http://www.w3.org/2003/11/xpath-datatypes", "dayTimeDuration");
  
  public static final QName DURATION_YEARMONTH = new QName("http://www.w3.org/2003/11/xpath-datatypes", "yearMonthDuration");
  
  public static final int MAX_TIMEZONE_OFFSET = -840;
  
  public static final int MIN_TIMEZONE_OFFSET = 840;
  
  public static final class Field {
    private final String str;
    
    private final int id;
    
    private Field(String param1String, int param1Int) {
      this.str = param1String;
      this.id = param1Int;
    }
    
    public String toString() { return this.str; }
    
    public int getId() { return this.id; }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\xml\datatype\DatatypeConstants.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */