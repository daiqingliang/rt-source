package com.sun.org.apache.xerces.internal.impl.dv.xs;

import com.sun.org.apache.xerces.internal.impl.dv.InvalidDatatypeValueException;
import com.sun.org.apache.xerces.internal.impl.dv.ValidationContext;
import com.sun.org.apache.xerces.internal.xs.datatypes.XSDouble;

public class DoubleDV extends TypeValidator {
  public short getAllowedFacets() { return 2552; }
  
  public Object getActualValue(String paramString, ValidationContext paramValidationContext) throws InvalidDatatypeValueException {
    try {
      return new XDouble(paramString);
    } catch (NumberFormatException numberFormatException) {
      throw new InvalidDatatypeValueException("cvc-datatype-valid.1.2.1", new Object[] { paramString, "double" });
    } 
  }
  
  public int compare(Object paramObject1, Object paramObject2) { return ((XDouble)paramObject1).compareTo((XDouble)paramObject2); }
  
  public boolean isIdentical(Object paramObject1, Object paramObject2) { return (paramObject2 instanceof XDouble) ? ((XDouble)paramObject1).isIdentical((XDouble)paramObject2) : 0; }
  
  static boolean isPossibleFP(String paramString) {
    int i = paramString.length();
    for (byte b = 0; b < i; b++) {
      char c = paramString.charAt(b);
      if ((c < '0' || c > '9') && c != '.' && c != '-' && c != '+' && c != 'E' && c != 'e')
        return false; 
    } 
    return true;
  }
  
  private static final class XDouble implements XSDouble {
    private final double value;
    
    private String canonical;
    
    public XDouble(String param1String) throws NumberFormatException {
      if (DoubleDV.isPossibleFP(param1String)) {
        this.value = Double.parseDouble(param1String);
      } else if (param1String.equals("INF")) {
        this.value = Double.POSITIVE_INFINITY;
      } else if (param1String.equals("-INF")) {
        this.value = Double.NEGATIVE_INFINITY;
      } else if (param1String.equals("NaN")) {
        this.value = NaND;
      } else {
        throw new NumberFormatException(param1String);
      } 
    }
    
    public boolean equals(Object param1Object) {
      if (param1Object == this)
        return true; 
      if (!(param1Object instanceof XDouble))
        return false; 
      XDouble xDouble = (XDouble)param1Object;
      return (this.value == xDouble.value) ? true : ((this.value != this.value && xDouble.value != xDouble.value));
    }
    
    public int hashCode() {
      if (this.value == 0.0D)
        return 0; 
      long l = Double.doubleToLongBits(this.value);
      return (int)(l ^ l >>> 32);
    }
    
    public boolean isIdentical(XDouble param1XDouble) { return (param1XDouble == this) ? true : ((this.value == param1XDouble.value) ? ((this.value != 0.0D || Double.doubleToLongBits(this.value) == Double.doubleToLongBits(param1XDouble.value))) : ((this.value != this.value && param1XDouble.value != param1XDouble.value))); }
    
    private int compareTo(XDouble param1XDouble) {
      double d = param1XDouble.value;
      return (this.value < d) ? -1 : ((this.value > d) ? 1 : ((this.value == d) ? 0 : ((this.value != this.value) ? ((d != d) ? 0 : 2) : 2)));
    }
    
    public String toString() {
      if (this.canonical == null)
        if (this.value == Double.POSITIVE_INFINITY) {
          this.canonical = "INF";
        } else if (this.value == Double.NEGATIVE_INFINITY) {
          this.canonical = "-INF";
        } else if (this.value != this.value) {
          this.canonical = "NaN";
        } else if (this.value == 0.0D) {
          this.canonical = "0.0E1";
        } else {
          this.canonical = Double.toString(this.value);
          if (this.canonical.indexOf('E') == -1) {
            int i = this.canonical.length();
            char[] arrayOfChar = new char[i + 3];
            this.canonical.getChars(0, i, arrayOfChar, 0);
            int j = (arrayOfChar[0] == '-') ? 2 : 1;
            if (this.value >= 1.0D || this.value <= -1.0D) {
              int k = this.canonical.indexOf('.');
              int m;
              for (m = k; m > j; m--)
                arrayOfChar[m] = arrayOfChar[m - 1]; 
              arrayOfChar[j] = '.';
              while (arrayOfChar[i - 1] == '0')
                i--; 
              if (arrayOfChar[i - 1] == '.')
                i++; 
              arrayOfChar[i++] = 'E';
              m = k - j;
              arrayOfChar[i++] = (char)(m + 48);
            } else {
              int k;
              for (k = j + 1; arrayOfChar[k] == '0'; k++);
              arrayOfChar[j - 1] = arrayOfChar[k];
              arrayOfChar[j] = '.';
              int m = k + 1;
              for (int n = j + 1; m < i; n++) {
                arrayOfChar[n] = arrayOfChar[m];
                m++;
              } 
              i -= k - j;
              if (i == j + 1)
                arrayOfChar[i++] = '0'; 
              arrayOfChar[i++] = 'E';
              arrayOfChar[i++] = '-';
              m = k - j;
              arrayOfChar[i++] = (char)(m + 48);
            } 
            this.canonical = new String(arrayOfChar, 0, i);
          } 
        }  
      return this.canonical;
    }
    
    public double getValue() { return this.value; }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xerces\internal\impl\dv\xs\DoubleDV.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */