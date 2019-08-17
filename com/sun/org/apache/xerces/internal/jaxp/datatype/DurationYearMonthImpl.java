package com.sun.org.apache.xerces.internal.jaxp.datatype;

import java.math.BigInteger;

class DurationYearMonthImpl extends DurationImpl {
  public DurationYearMonthImpl(boolean paramBoolean, BigInteger paramBigInteger1, BigInteger paramBigInteger2) {
    super(paramBoolean, paramBigInteger1, paramBigInteger2, null, null, null, null);
    convertToCanonicalYearMonth();
  }
  
  protected DurationYearMonthImpl(boolean paramBoolean, int paramInt1, int paramInt2) { this(paramBoolean, wrap(paramInt1), wrap(paramInt2)); }
  
  protected DurationYearMonthImpl(long paramLong) {
    super(paramLong);
    convertToCanonicalYearMonth();
    this.days = null;
    this.hours = null;
    this.minutes = null;
    this.seconds = null;
    this.signum = calcSignum(!(this.signum < 0));
  }
  
  protected DurationYearMonthImpl(String paramString) {
    super(paramString);
    if (getDays() > 0 || getHours() > 0 || getMinutes() > 0 || getSeconds() > 0)
      throw new IllegalArgumentException("Trying to create an xdt:yearMonthDuration with an invalid lexical representation of \"" + paramString + "\", data model requires PnYnM."); 
    convertToCanonicalYearMonth();
  }
  
  public int getValue() { return getYears() * 12 + getMonths(); }
  
  private void convertToCanonicalYearMonth() {
    while (getMonths() >= 12)
      this.years = (this.months = this.months.subtract(BigInteger.valueOf(12L))).valueOf(getYears()).add(BigInteger.ONE); 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xerces\internal\jaxp\datatype\DurationYearMonthImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */