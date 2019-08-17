package com.sun.org.apache.xerces.internal.impl.dv.xs;

import com.sun.org.apache.xerces.internal.impl.dv.InvalidDatatypeValueException;
import com.sun.org.apache.xerces.internal.impl.dv.ValidationContext;
import com.sun.org.apache.xerces.internal.xs.datatypes.XSDecimal;
import java.math.BigDecimal;
import java.math.BigInteger;
import java.util.Objects;

public class DecimalDV extends TypeValidator {
  public final short getAllowedFacets() { return 4088; }
  
  public Object getActualValue(String paramString, ValidationContext paramValidationContext) throws InvalidDatatypeValueException {
    try {
      return new XDecimal(paramString);
    } catch (NumberFormatException numberFormatException) {
      throw new InvalidDatatypeValueException("cvc-datatype-valid.1.2.1", new Object[] { paramString, "decimal" });
    } 
  }
  
  public final int compare(Object paramObject1, Object paramObject2) { return ((XDecimal)paramObject1).compareTo((XDecimal)paramObject2); }
  
  public final int getTotalDigits(Object paramObject) { return ((XDecimal)paramObject).totalDigits; }
  
  public final int getFractionDigits(Object paramObject) { return ((XDecimal)paramObject).fracDigits; }
  
  static final class XDecimal implements XSDecimal {
    int sign = 1;
    
    int totalDigits = 0;
    
    int intDigits = 0;
    
    int fracDigits = 0;
    
    String ivalue = "";
    
    String fvalue = "";
    
    boolean integer = false;
    
    private String canonical;
    
    XDecimal(String param1String) throws NumberFormatException { initD(param1String); }
    
    XDecimal(String param1String, boolean param1Boolean) throws NumberFormatException {
      if (param1Boolean) {
        initI(param1String);
      } else {
        initD(param1String);
      } 
    }
    
    void initD(String param1String) throws NumberFormatException {
      int i = param1String.length();
      if (i == 0)
        throw new NumberFormatException(); 
      boolean bool = false;
      byte b1 = 0;
      int j = 0;
      int k = 0;
      if (param1String.charAt(0) == '+') {
        bool = true;
      } else if (param1String.charAt(0) == '-') {
        bool = true;
        this.sign = -1;
      } 
      byte b2;
      for (b2 = bool; b2 < i && param1String.charAt(b2) == '0'; b2++);
      for (b1 = b2; b1 < i && TypeValidator.isDigit(param1String.charAt(b1)); b1++);
      if (b1 < i) {
        if (param1String.charAt(b1) != '.')
          throw new NumberFormatException(); 
        j = b1 + 1;
        k = i;
      } 
      if (bool == b1 && j == k)
        throw new NumberFormatException(); 
      while (k > j && param1String.charAt(k - 1) == '0')
        k--; 
      for (byte b3 = j; b3 < k; b3++) {
        if (!TypeValidator.isDigit(param1String.charAt(b3)))
          throw new NumberFormatException(); 
      } 
      this.intDigits = b1 - b2;
      this.fracDigits = k - j;
      this.totalDigits = this.intDigits + this.fracDigits;
      if (this.intDigits > 0) {
        this.ivalue = param1String.substring(b2, b1);
        if (this.fracDigits > 0)
          this.fvalue = param1String.substring(j, k); 
      } else if (this.fracDigits > 0) {
        this.fvalue = param1String.substring(j, k);
      } else {
        this.sign = 0;
      } 
    }
    
    void initI(String param1String) throws NumberFormatException {
      int i = param1String.length();
      if (i == 0)
        throw new NumberFormatException(); 
      boolean bool = false;
      byte b1 = 0;
      if (param1String.charAt(0) == '+') {
        bool = true;
      } else if (param1String.charAt(0) == '-') {
        bool = true;
        this.sign = -1;
      } 
      byte b2;
      for (b2 = bool; b2 < i && param1String.charAt(b2) == '0'; b2++);
      for (b1 = b2; b1 < i && TypeValidator.isDigit(param1String.charAt(b1)); b1++);
      if (b1 < i)
        throw new NumberFormatException(); 
      if (bool == b1)
        throw new NumberFormatException(); 
      this.intDigits = b1 - b2;
      this.fracDigits = 0;
      this.totalDigits = this.intDigits;
      if (this.intDigits > 0) {
        this.ivalue = param1String.substring(b2, b1);
      } else {
        this.sign = 0;
      } 
      this.integer = true;
    }
    
    public boolean equals(Object param1Object) {
      if (param1Object == this)
        return true; 
      if (!(param1Object instanceof XDecimal))
        return false; 
      XDecimal xDecimal = (XDecimal)param1Object;
      return (this.sign != xDecimal.sign) ? false : ((this.sign == 0) ? true : ((this.intDigits == xDecimal.intDigits && this.fracDigits == xDecimal.fracDigits && this.ivalue.equals(xDecimal.ivalue) && this.fvalue.equals(xDecimal.fvalue))));
    }
    
    public int hashCode() {
      null = 7;
      null = 17 * null + this.sign;
      if (this.sign == 0)
        return null; 
      null = 17 * null + this.intDigits;
      null = 17 * null + this.fracDigits;
      null = 17 * null + Objects.hashCode(this.ivalue);
      return 17 * null + Objects.hashCode(this.fvalue);
    }
    
    public int compareTo(XDecimal param1XDecimal) { return (this.sign != param1XDecimal.sign) ? ((this.sign > param1XDecimal.sign) ? 1 : -1) : ((this.sign == 0) ? 0 : (this.sign * intComp(param1XDecimal))); }
    
    private int intComp(XDecimal param1XDecimal) {
      if (this.intDigits != param1XDecimal.intDigits)
        return (this.intDigits > param1XDecimal.intDigits) ? 1 : -1; 
      int i = this.ivalue.compareTo(param1XDecimal.ivalue);
      if (i != 0)
        return (i > 0) ? 1 : -1; 
      i = this.fvalue.compareTo(param1XDecimal.fvalue);
      return (i == 0) ? 0 : ((i > 0) ? 1 : -1);
    }
    
    public String toString() {
      if (this.canonical == null)
        makeCanonical(); 
      return this.canonical;
    }
    
    private void makeCanonical() {
      if (this.sign == 0) {
        if (this.integer) {
          this.canonical = "0";
        } else {
          this.canonical = "0.0";
        } 
        return;
      } 
      if (this.integer && this.sign > 0) {
        this.canonical = this.ivalue;
        return;
      } 
      StringBuilder stringBuilder = new StringBuilder(this.totalDigits + 3);
      if (this.sign == -1)
        stringBuilder.append('-'); 
      if (this.intDigits != 0) {
        stringBuilder.append(this.ivalue);
      } else {
        stringBuilder.append('0');
      } 
      if (!this.integer) {
        stringBuilder.append('.');
        if (this.fracDigits != 0) {
          stringBuilder.append(this.fvalue);
        } else {
          stringBuilder.append('0');
        } 
      } 
      this.canonical = stringBuilder.toString();
    }
    
    public BigDecimal getBigDecimal() { return (this.sign == 0) ? new BigDecimal(BigInteger.ZERO) : new BigDecimal(toString()); }
    
    public BigInteger getBigInteger() throws NumberFormatException {
      if (this.fracDigits != 0)
        throw new NumberFormatException(); 
      return (this.sign == 0) ? BigInteger.ZERO : ((this.sign == 1) ? new BigInteger(this.ivalue) : new BigInteger("-" + this.ivalue));
    }
    
    public long getLong() throws NumberFormatException {
      if (this.fracDigits != 0)
        throw new NumberFormatException(); 
      return (this.sign == 0) ? 0L : ((this.sign == 1) ? Long.parseLong(this.ivalue) : Long.parseLong("-" + this.ivalue));
    }
    
    public int getInt() {
      if (this.fracDigits != 0)
        throw new NumberFormatException(); 
      return (this.sign == 0) ? 0 : ((this.sign == 1) ? Integer.parseInt(this.ivalue) : Integer.parseInt("-" + this.ivalue));
    }
    
    public short getShort() {
      if (this.fracDigits != 0)
        throw new NumberFormatException(); 
      return (this.sign == 0) ? 0 : ((this.sign == 1) ? Short.parseShort(this.ivalue) : Short.parseShort("-" + this.ivalue));
    }
    
    public byte getByte() throws NumberFormatException {
      if (this.fracDigits != 0)
        throw new NumberFormatException(); 
      return (this.sign == 0) ? 0 : ((this.sign == 1) ? Byte.parseByte(this.ivalue) : Byte.parseByte("-" + this.ivalue));
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xerces\internal\impl\dv\xs\DecimalDV.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */