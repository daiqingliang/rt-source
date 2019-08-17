package com.sun.org.apache.xerces.internal.impl.dv.xs;

import com.sun.org.apache.xerces.internal.impl.dv.InvalidDatatypeValueException;
import com.sun.org.apache.xerces.internal.impl.dv.ValidationContext;
import com.sun.org.apache.xerces.internal.xs.datatypes.XSFloat;

public class FloatDV extends TypeValidator {
  public short getAllowedFacets() { return 2552; }
  
  public Object getActualValue(String paramString, ValidationContext paramValidationContext) throws InvalidDatatypeValueException {
    try {
      return new XFloat(paramString);
    } catch (NumberFormatException numberFormatException) {
      throw new InvalidDatatypeValueException("cvc-datatype-valid.1.2.1", new Object[] { paramString, "float" });
    } 
  }
  
  public int compare(Object paramObject1, Object paramObject2) { return ((XFloat)paramObject1).compareTo((XFloat)paramObject2); }
  
  public boolean isIdentical(Object paramObject1, Object paramObject2) { return (paramObject2 instanceof XFloat) ? ((XFloat)paramObject1).isIdentical((XFloat)paramObject2) : 0; }
  
  private static final class XFloat implements XSFloat {
    private final float value;
    
    private String canonical;
    
    public XFloat(String param1String) throws NumberFormatException {
      if (DoubleDV.isPossibleFP(param1String)) {
        this.value = Float.parseFloat(param1String);
      } else if (param1String.equals("INF")) {
        this.value = Float.POSITIVE_INFINITY;
      } else if (param1String.equals("-INF")) {
        this.value = Float.NEGATIVE_INFINITY;
      } else if (param1String.equals("NaN")) {
        this.value = NaNF;
      } else {
        throw new NumberFormatException(param1String);
      } 
    }
    
    public boolean equals(Object param1Object) {
      if (param1Object == this)
        return true; 
      if (!(param1Object instanceof XFloat))
        return false; 
      XFloat xFloat = (XFloat)param1Object;
      return (this.value == xFloat.value) ? true : ((this.value != this.value && xFloat.value != xFloat.value));
    }
    
    public int hashCode() { return (this.value == 0.0F) ? 0 : Float.floatToIntBits(this.value); }
    
    public boolean isIdentical(XFloat param1XFloat) { return (param1XFloat == this) ? true : ((this.value == param1XFloat.value) ? ((this.value != 0.0F || Float.floatToIntBits(this.value) == Float.floatToIntBits(param1XFloat.value))) : ((this.value != this.value && param1XFloat.value != param1XFloat.value))); }
    
    private int compareTo(XFloat param1XFloat) {
      float f = param1XFloat.value;
      return (this.value < f) ? -1 : ((this.value > f) ? 1 : ((this.value == f) ? 0 : ((this.value != this.value) ? ((f != f) ? 0 : 2) : 2)));
    }
    
    public String toString() {
      if (this.canonical == null)
        if (this.value == Float.POSITIVE_INFINITY) {
          this.canonical = "INF";
        } else if (this.value == Float.NEGATIVE_INFINITY) {
          this.canonical = "-INF";
        } else if (this.value != this.value) {
          this.canonical = "NaN";
        } else if (this.value == 0.0F) {
          this.canonical = "0.0E1";
        } else {
          this.canonical = Float.toString(this.value);
          if (this.canonical.indexOf('E') == -1) {
            int i = this.canonical.length();
            char[] arrayOfChar = new char[i + 3];
            this.canonical.getChars(0, i, arrayOfChar, 0);
            int j = (arrayOfChar[0] == '-') ? 2 : 1;
            if (this.value >= 1.0F || this.value <= -1.0F) {
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
    
    public float getValue() { return this.value; }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xerces\internal\impl\dv\xs\FloatDV.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */