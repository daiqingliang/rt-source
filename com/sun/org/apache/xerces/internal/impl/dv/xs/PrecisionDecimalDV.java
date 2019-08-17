package com.sun.org.apache.xerces.internal.impl.dv.xs;

import com.sun.org.apache.xerces.internal.impl.dv.InvalidDatatypeValueException;
import com.sun.org.apache.xerces.internal.impl.dv.ValidationContext;

class PrecisionDecimalDV extends TypeValidator {
  public short getAllowedFacets() { return 4088; }
  
  public Object getActualValue(String paramString, ValidationContext paramValidationContext) throws InvalidDatatypeValueException {
    try {
      return new XPrecisionDecimal(paramString);
    } catch (NumberFormatException numberFormatException) {
      throw new InvalidDatatypeValueException("cvc-datatype-valid.1.2.1", new Object[] { paramString, "precisionDecimal" });
    } 
  }
  
  public int compare(Object paramObject1, Object paramObject2) { return ((XPrecisionDecimal)paramObject1).compareTo((XPrecisionDecimal)paramObject2); }
  
  public int getFractionDigits(Object paramObject) { return ((XPrecisionDecimal)paramObject).fracDigits; }
  
  public int getTotalDigits(Object paramObject) { return ((XPrecisionDecimal)paramObject).totalDigits; }
  
  public boolean isIdentical(Object paramObject1, Object paramObject2) { return (!(paramObject2 instanceof XPrecisionDecimal) || !(paramObject1 instanceof XPrecisionDecimal)) ? false : ((XPrecisionDecimal)paramObject1).isIdentical((XPrecisionDecimal)paramObject2); }
  
  static final class XPrecisionDecimal {
    int sign = 1;
    
    int totalDigits = 0;
    
    int intDigits = 0;
    
    int fracDigits = 0;
    
    String ivalue = "";
    
    String fvalue = "";
    
    int pvalue = 0;
    
    private String canonical;
    
    XPrecisionDecimal(String param1String) throws NumberFormatException {
      if (param1String.equals("NaN")) {
        this.ivalue = param1String;
        this.sign = 0;
      } 
      if (param1String.equals("+INF") || param1String.equals("INF") || param1String.equals("-INF")) {
        this.ivalue = (param1String.charAt(0) == '+') ? param1String.substring(1) : param1String;
        return;
      } 
      initD(param1String);
    }
    
    void initD(String param1String) throws NumberFormatException {
      int i = param1String.length();
      if (i == 0)
        throw new NumberFormatException(); 
      boolean bool = false;
      byte b1 = 0;
      byte b2 = 0;
      byte b3 = 0;
      if (param1String.charAt(0) == '+') {
        bool = true;
      } else if (param1String.charAt(0) == '-') {
        bool = true;
        this.sign = -1;
      } 
      byte b4;
      for (b4 = bool; b4 < i && param1String.charAt(b4) == '0'; b4++);
      for (b1 = b4; b1 < i && TypeValidator.isDigit(param1String.charAt(b1)); b1++);
      if (b1 < i) {
        if (param1String.charAt(b1) != '.' && param1String.charAt(b1) != 'E' && param1String.charAt(b1) != 'e')
          throw new NumberFormatException(); 
        if (param1String.charAt(b1) == '.') {
          b2 = b1 + 1;
          for (b3 = b2; b3 < i && TypeValidator.isDigit(param1String.charAt(b3)); b3++);
        } else {
          this.pvalue = Integer.parseInt(param1String.substring(b1 + 1, i));
        } 
      } 
      if (bool == b1 && b2 == b3)
        throw new NumberFormatException(); 
      for (byte b5 = b2; b5 < b3; b5++) {
        if (!TypeValidator.isDigit(param1String.charAt(b5)))
          throw new NumberFormatException(); 
      } 
      this.intDigits = b1 - b4;
      this.fracDigits = b3 - b2;
      if (this.intDigits > 0)
        this.ivalue = param1String.substring(b4, b1); 
      if (this.fracDigits > 0) {
        this.fvalue = param1String.substring(b2, b3);
        if (b3 < i)
          this.pvalue = Integer.parseInt(param1String.substring(b3 + 1, i)); 
      } 
      this.totalDigits = this.intDigits + this.fracDigits;
    }
    
    private static String canonicalToStringForHashCode(String param1String1, String param1String2, int param1Int1, int param1Int2) {
      if ("NaN".equals(param1String1))
        return "NaN"; 
      if ("INF".equals(param1String1))
        return (param1Int1 < 0) ? "-INF" : "INF"; 
      StringBuilder stringBuilder = new StringBuilder();
      int i = param1String1.length();
      int j = param1String2.length();
      int k;
      for (k = j; k > 0 && param1String2.charAt(k - 1) == '0'; k--);
      int m = k;
      int n = param1Int2;
      byte b1;
      for (b1 = 0; b1 < i && param1String1.charAt(b1) == '0'; b1++);
      byte b2 = 0;
      if (b1 < param1String1.length()) {
        stringBuilder.append((param1Int1 == -1) ? "-" : "");
        stringBuilder.append(param1String1.charAt(b1));
        b1++;
      } else if (m > 0) {
        for (b2 = 0; b2 < m && param1String2.charAt(b2) == '0'; b2++);
        if (b2 < m) {
          stringBuilder.append((param1Int1 == -1) ? "-" : "");
          stringBuilder.append(param1String2.charAt(b2));
          n -= ++b2;
        } else {
          return "0";
        } 
      } else {
        return "0";
      } 
      if (b1 < i || b2 < m)
        stringBuilder.append('.'); 
      while (b1 < i) {
        stringBuilder.append(param1String1.charAt(b1++));
        n++;
      } 
      while (b2 < m)
        stringBuilder.append(param1String2.charAt(b2++)); 
      if (n != 0)
        stringBuilder.append("E").append(n); 
      return stringBuilder.toString();
    }
    
    public boolean equals(Object param1Object) {
      if (param1Object == this)
        return true; 
      if (!(param1Object instanceof XPrecisionDecimal))
        return false; 
      XPrecisionDecimal xPrecisionDecimal = (XPrecisionDecimal)param1Object;
      return (compareTo(xPrecisionDecimal) == 0);
    }
    
    public int hashCode() { return canonicalToStringForHashCode(this.ivalue, this.fvalue, this.sign, this.pvalue).hashCode(); }
    
    private int compareFractionalPart(XPrecisionDecimal param1XPrecisionDecimal) {
      if (this.fvalue.equals(param1XPrecisionDecimal.fvalue))
        return 0; 
      StringBuffer stringBuffer1 = new StringBuffer(this.fvalue);
      StringBuffer stringBuffer2 = new StringBuffer(param1XPrecisionDecimal.fvalue);
      truncateTrailingZeros(stringBuffer1, stringBuffer2);
      return stringBuffer1.toString().compareTo(stringBuffer2.toString());
    }
    
    private void truncateTrailingZeros(StringBuffer param1StringBuffer1, StringBuffer param1StringBuffer2) {
      int i;
      for (i = param1StringBuffer1.length() - 1; i >= 0 && param1StringBuffer1.charAt(i) == '0'; i--)
        param1StringBuffer1.deleteCharAt(i); 
      for (i = param1StringBuffer2.length() - 1; i >= 0 && param1StringBuffer2.charAt(i) == '0'; i--)
        param1StringBuffer2.deleteCharAt(i); 
    }
    
    public int compareTo(XPrecisionDecimal param1XPrecisionDecimal) { return (this.sign == 0) ? 2 : ((this.ivalue.equals("INF") || param1XPrecisionDecimal.ivalue.equals("INF")) ? (this.ivalue.equals(param1XPrecisionDecimal.ivalue) ? 0 : (this.ivalue.equals("INF") ? 1 : -1)) : ((this.ivalue.equals("-INF") || param1XPrecisionDecimal.ivalue.equals("-INF")) ? (this.ivalue.equals(param1XPrecisionDecimal.ivalue) ? 0 : (this.ivalue.equals("-INF") ? -1 : 1)) : ((this.sign != param1XPrecisionDecimal.sign) ? ((this.sign > param1XPrecisionDecimal.sign) ? 1 : -1) : (this.sign * compare(param1XPrecisionDecimal))))); }
    
    private int compare(XPrecisionDecimal param1XPrecisionDecimal) {
      if (this.pvalue != 0 || param1XPrecisionDecimal.pvalue != 0) {
        if (this.pvalue == param1XPrecisionDecimal.pvalue)
          return intComp(param1XPrecisionDecimal); 
        if (this.intDigits + this.pvalue != param1XPrecisionDecimal.intDigits + param1XPrecisionDecimal.pvalue)
          return (this.intDigits + this.pvalue > param1XPrecisionDecimal.intDigits + param1XPrecisionDecimal.pvalue) ? 1 : -1; 
        if (this.pvalue > param1XPrecisionDecimal.pvalue) {
          int j = this.pvalue - param1XPrecisionDecimal.pvalue;
          StringBuffer stringBuffer3 = new StringBuffer(this.ivalue);
          StringBuffer stringBuffer4 = new StringBuffer(this.fvalue);
          for (byte b1 = 0; b1 < j; b1++) {
            if (b1 < this.fracDigits) {
              stringBuffer3.append(this.fvalue.charAt(b1));
              stringBuffer4.deleteCharAt(b1);
            } else {
              stringBuffer3.append('0');
            } 
          } 
          return compareDecimal(stringBuffer3.toString(), param1XPrecisionDecimal.ivalue, stringBuffer4.toString(), param1XPrecisionDecimal.fvalue);
        } 
        int i = param1XPrecisionDecimal.pvalue - this.pvalue;
        StringBuffer stringBuffer1 = new StringBuffer(param1XPrecisionDecimal.ivalue);
        StringBuffer stringBuffer2 = new StringBuffer(param1XPrecisionDecimal.fvalue);
        for (byte b = 0; b < i; b++) {
          if (b < param1XPrecisionDecimal.fracDigits) {
            stringBuffer1.append(param1XPrecisionDecimal.fvalue.charAt(b));
            stringBuffer2.deleteCharAt(b);
          } else {
            stringBuffer1.append('0');
          } 
        } 
        return compareDecimal(this.ivalue, stringBuffer1.toString(), this.fvalue, stringBuffer2.toString());
      } 
      return intComp(param1XPrecisionDecimal);
    }
    
    private int intComp(XPrecisionDecimal param1XPrecisionDecimal) { return (this.intDigits != param1XPrecisionDecimal.intDigits) ? ((this.intDigits > param1XPrecisionDecimal.intDigits) ? 1 : -1) : compareDecimal(this.ivalue, param1XPrecisionDecimal.ivalue, this.fvalue, param1XPrecisionDecimal.fvalue); }
    
    private int compareDecimal(String param1String1, String param1String2, String param1String3, String param1String4) {
      int i = param1String1.compareTo(param1String3);
      if (i != 0)
        return (i > 0) ? 1 : -1; 
      if (param1String2.equals(param1String4))
        return 0; 
      StringBuffer stringBuffer1 = new StringBuffer(param1String2);
      StringBuffer stringBuffer2 = new StringBuffer(param1String4);
      truncateTrailingZeros(stringBuffer1, stringBuffer2);
      i = stringBuffer1.toString().compareTo(stringBuffer2.toString());
      return (i == 0) ? 0 : ((i > 0) ? 1 : -1);
    }
    
    public String toString() {
      if (this.canonical == null)
        makeCanonical(); 
      return this.canonical;
    }
    
    private void makeCanonical() { this.canonical = "TBD by Working Group"; }
    
    public boolean isIdentical(XPrecisionDecimal param1XPrecisionDecimal) { return (this.ivalue.equals(param1XPrecisionDecimal.ivalue) && (this.ivalue.equals("INF") || this.ivalue.equals("-INF") || this.ivalue.equals("NaN"))) ? true : ((this.sign == param1XPrecisionDecimal.sign && this.intDigits == param1XPrecisionDecimal.intDigits && this.fracDigits == param1XPrecisionDecimal.fracDigits && this.pvalue == param1XPrecisionDecimal.pvalue && this.ivalue.equals(param1XPrecisionDecimal.ivalue) && this.fvalue.equals(param1XPrecisionDecimal.fvalue))); }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xerces\internal\impl\dv\xs\PrecisionDecimalDV.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */