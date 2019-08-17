package com.sun.org.apache.xerces.internal.jaxp.datatype;

import java.math.BigDecimal;
import java.math.BigInteger;

class DurationDayTimeImpl extends DurationImpl {
  public DurationDayTimeImpl(boolean paramBoolean, BigInteger paramBigInteger1, BigInteger paramBigInteger2, BigInteger paramBigInteger3, BigDecimal paramBigDecimal) {
    super(paramBoolean, null, null, paramBigInteger1, paramBigInteger2, paramBigInteger3, paramBigDecimal);
    convertToCanonicalDayTime();
  }
  
  public DurationDayTimeImpl(boolean paramBoolean, int paramInt1, int paramInt2, int paramInt3, int paramInt4) { this(paramBoolean, wrap(paramInt1), wrap(paramInt2), wrap(paramInt3), (paramInt4 != Integer.MIN_VALUE) ? new BigDecimal(String.valueOf(paramInt4)) : null); }
  
  protected DurationDayTimeImpl(String paramString) {
    super(paramString);
    if (getYears() > 0 || getMonths() > 0)
      throw new IllegalArgumentException("Trying to create an xdt:dayTimeDuration with an invalid lexical representation of \"" + paramString + "\", data model requires a format PnDTnHnMnS."); 
    convertToCanonicalDayTime();
  }
  
  protected DurationDayTimeImpl(long paramLong) {
    super(paramLong);
    convertToCanonicalDayTime();
    this.years = null;
    this.months = null;
  }
  
  public float getValue() {
    float f = (this.seconds == null) ? 0.0F : this.seconds.floatValue();
    return (((getDays() * 24 + getHours()) * 60 + getMinutes()) * 60) + f;
  }
  
  private void convertToCanonicalDayTime() {
    while (getSeconds() >= 60) {
      this.seconds = this.seconds.subtract(BigDecimal.valueOf(60L));
      this.minutes = BigInteger.valueOf(getMinutes()).add(BigInteger.ONE);
    } 
    while (getMinutes() >= 60)
      this.hours = (this.minutes = this.minutes.subtract(BigInteger.valueOf(60L))).valueOf(getHours()).add(BigInteger.ONE); 
    while (getHours() >= 24)
      this.days = (this.hours = this.hours.subtract(BigInteger.valueOf(24L))).valueOf(getDays()).add(BigInteger.ONE); 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xerces\internal\jaxp\datatype\DurationDayTimeImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */