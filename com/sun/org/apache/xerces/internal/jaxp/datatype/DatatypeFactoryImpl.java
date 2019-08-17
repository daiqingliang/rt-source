package com.sun.org.apache.xerces.internal.jaxp.datatype;

import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.GregorianCalendar;
import javax.xml.datatype.DatatypeFactory;
import javax.xml.datatype.Duration;
import javax.xml.datatype.XMLGregorianCalendar;

public class DatatypeFactoryImpl extends DatatypeFactory {
  public Duration newDuration(String paramString) { return new DurationImpl(paramString); }
  
  public Duration newDuration(long paramLong) { return new DurationImpl(paramLong); }
  
  public Duration newDuration(boolean paramBoolean, BigInteger paramBigInteger1, BigInteger paramBigInteger2, BigInteger paramBigInteger3, BigInteger paramBigInteger4, BigInteger paramBigInteger5, BigDecimal paramBigDecimal) { return new DurationImpl(paramBoolean, paramBigInteger1, paramBigInteger2, paramBigInteger3, paramBigInteger4, paramBigInteger5, paramBigDecimal); }
  
  public Duration newDurationYearMonth(boolean paramBoolean, BigInteger paramBigInteger1, BigInteger paramBigInteger2) { return new DurationYearMonthImpl(paramBoolean, paramBigInteger1, paramBigInteger2); }
  
  public Duration newDurationYearMonth(boolean paramBoolean, int paramInt1, int paramInt2) { return new DurationYearMonthImpl(paramBoolean, paramInt1, paramInt2); }
  
  public Duration newDurationYearMonth(String paramString) { return new DurationYearMonthImpl(paramString); }
  
  public Duration newDurationYearMonth(long paramLong) { return new DurationYearMonthImpl(paramLong); }
  
  public Duration newDurationDayTime(String paramString) {
    if (paramString == null)
      throw new NullPointerException("Trying to create an xdt:dayTimeDuration with an invalid lexical representation of \"null\""); 
    return new DurationDayTimeImpl(paramString);
  }
  
  public Duration newDurationDayTime(long paramLong) { return new DurationDayTimeImpl(paramLong); }
  
  public Duration newDurationDayTime(boolean paramBoolean, BigInteger paramBigInteger1, BigInteger paramBigInteger2, BigInteger paramBigInteger3, BigInteger paramBigInteger4) { return new DurationDayTimeImpl(paramBoolean, paramBigInteger1, paramBigInteger2, paramBigInteger3, (paramBigInteger4 != null) ? new BigDecimal(paramBigInteger4) : null); }
  
  public Duration newDurationDayTime(boolean paramBoolean, int paramInt1, int paramInt2, int paramInt3, int paramInt4) { return new DurationDayTimeImpl(paramBoolean, paramInt1, paramInt2, paramInt3, paramInt4); }
  
  public XMLGregorianCalendar newXMLGregorianCalendar() { return new XMLGregorianCalendarImpl(); }
  
  public XMLGregorianCalendar newXMLGregorianCalendar(String paramString) { return new XMLGregorianCalendarImpl(paramString); }
  
  public XMLGregorianCalendar newXMLGregorianCalendar(GregorianCalendar paramGregorianCalendar) { return new XMLGregorianCalendarImpl(paramGregorianCalendar); }
  
  public XMLGregorianCalendar newXMLGregorianCalendar(BigInteger paramBigInteger, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5, BigDecimal paramBigDecimal, int paramInt6) { return new XMLGregorianCalendarImpl(paramBigInteger, paramInt1, paramInt2, paramInt3, paramInt4, paramInt5, paramBigDecimal, paramInt6); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xerces\internal\jaxp\datatype\DatatypeFactoryImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */