package sun.misc;

import java.util.Arrays;

public class FormattedFloatingDecimal {
  private int decExponentRounded;
  
  private char[] mantissa;
  
  private char[] exponent;
  
  private static final ThreadLocal<Object> threadLocalCharBuffer = new ThreadLocal<Object>() {
      protected Object initialValue() { return new char[20]; }
    };
  
  public static FormattedFloatingDecimal valueOf(double paramDouble, int paramInt, Form paramForm) {
    FloatingDecimal.BinaryToASCIIConverter binaryToASCIIConverter = FloatingDecimal.getBinaryToASCIIConverter(paramDouble, (paramForm == Form.COMPATIBLE));
    return new FormattedFloatingDecimal(paramInt, paramForm, binaryToASCIIConverter);
  }
  
  private static char[] getBuffer() { return (char[])threadLocalCharBuffer.get(); }
  
  private FormattedFloatingDecimal(int paramInt, Form paramForm, FloatingDecimal.BinaryToASCIIConverter paramBinaryToASCIIConverter) {
    if (paramBinaryToASCIIConverter.isExceptional()) {
      this.mantissa = paramBinaryToASCIIConverter.toJavaFormatString().toCharArray();
      this.exponent = null;
      return;
    } 
    char[] arrayOfChar = getBuffer();
    int i = paramBinaryToASCIIConverter.getDigits(arrayOfChar);
    int j = paramBinaryToASCIIConverter.getDecimalExponent();
    boolean bool = paramBinaryToASCIIConverter.isNegative();
    switch (paramForm) {
      case COMPATIBLE:
        k = j;
        this.decExponentRounded = k;
        fillCompatible(paramInt, arrayOfChar, i, k, bool);
        return;
      case DECIMAL_FLOAT:
        k = applyPrecision(j, arrayOfChar, i, j + paramInt);
        fillDecimal(paramInt, arrayOfChar, i, k, bool);
        this.decExponentRounded = k;
        return;
      case SCIENTIFIC:
        k = applyPrecision(j, arrayOfChar, i, paramInt + 1);
        fillScientific(paramInt, arrayOfChar, i, k, bool);
        this.decExponentRounded = k;
        return;
      case GENERAL:
        k = applyPrecision(j, arrayOfChar, i, paramInt);
        if (k - 1 < -4 || k - 1 >= paramInt) {
          fillScientific(--paramInt, arrayOfChar, i, k, bool);
        } else {
          paramInt -= k;
          fillDecimal(paramInt, arrayOfChar, i, k, bool);
        } 
        this.decExponentRounded = k;
        return;
    } 
    assert false;
  }
  
  public int getExponentRounded() { return this.decExponentRounded - 1; }
  
  public char[] getMantissa() { return this.mantissa; }
  
  public char[] getExponent() { return this.exponent; }
  
  private static int applyPrecision(int paramInt1, char[] paramArrayOfChar, int paramInt2, int paramInt3) {
    if (paramInt3 >= paramInt2 || paramInt3 < 0)
      return paramInt1; 
    if (paramInt3 == 0) {
      if (paramArrayOfChar[0] >= '5') {
        paramArrayOfChar[0] = '1';
        Arrays.fill(paramArrayOfChar, 1, paramInt2, '0');
        return paramInt1 + 1;
      } 
      Arrays.fill(paramArrayOfChar, 0, paramInt2, '0');
      return paramInt1;
    } 
    char c = paramArrayOfChar[paramInt3];
    if (c >= '5') {
      int i = paramInt3;
      c = paramArrayOfChar[--i];
      if (c == '9') {
        while (c == '9' && i > 0)
          c = paramArrayOfChar[--i]; 
        if (c == '9') {
          paramArrayOfChar[0] = '1';
          Arrays.fill(paramArrayOfChar, 1, paramInt2, '0');
          return paramInt1 + 1;
        } 
      } 
      paramArrayOfChar[i] = (char)(c + '\001');
      Arrays.fill(paramArrayOfChar, i + 1, paramInt2, '0');
    } else {
      Arrays.fill(paramArrayOfChar, paramInt3, paramInt2, '0');
    } 
    return paramInt1;
  }
  
  private void fillCompatible(int paramInt1, char[] paramArrayOfChar, int paramInt2, int paramInt3, boolean paramBoolean) {
    int i = paramBoolean ? 1 : 0;
    if (paramInt3 > 0 && paramInt3 < 8) {
      if (paramInt2 < paramInt3) {
        int j = paramInt3 - paramInt2;
        this.mantissa = create(paramBoolean, paramInt2 + j + 2);
        System.arraycopy(paramArrayOfChar, 0, this.mantissa, i, paramInt2);
        Arrays.fill(this.mantissa, i + paramInt2, i + paramInt2 + j, '0');
        this.mantissa[i + paramInt2 + j] = '.';
        this.mantissa[i + paramInt2 + j + 1] = '0';
      } else if (paramInt3 < paramInt2) {
        int j = Math.min(paramInt2 - paramInt3, paramInt1);
        this.mantissa = create(paramBoolean, paramInt3 + 1 + j);
        System.arraycopy(paramArrayOfChar, 0, this.mantissa, i, paramInt3);
        this.mantissa[i + paramInt3] = '.';
        System.arraycopy(paramArrayOfChar, paramInt3, this.mantissa, i + paramInt3 + 1, j);
      } else {
        this.mantissa = create(paramBoolean, paramInt2 + 2);
        System.arraycopy(paramArrayOfChar, 0, this.mantissa, i, paramInt2);
        this.mantissa[i + paramInt2] = '.';
        this.mantissa[i + paramInt2 + 1] = '0';
      } 
    } else if (paramInt3 <= 0 && paramInt3 > -3) {
      int j = Math.max(0, Math.min(-paramInt3, paramInt1));
      int k = Math.max(0, Math.min(paramInt2, paramInt1 + paramInt3));
      if (j > 0) {
        this.mantissa = create(paramBoolean, j + 2 + k);
        this.mantissa[i] = '0';
        this.mantissa[i + 1] = '.';
        Arrays.fill(this.mantissa, i + 2, i + 2 + j, '0');
        if (k > 0)
          System.arraycopy(paramArrayOfChar, 0, this.mantissa, i + 2 + j, k); 
      } else if (k > 0) {
        this.mantissa = create(paramBoolean, j + 2 + k);
        this.mantissa[i] = '0';
        this.mantissa[i + 1] = '.';
        System.arraycopy(paramArrayOfChar, 0, this.mantissa, i + 2, k);
      } else {
        this.mantissa = create(paramBoolean, 1);
        this.mantissa[i] = '0';
      } 
    } else {
      boolean bool;
      int j;
      if (paramInt2 > 1) {
        this.mantissa = create(paramBoolean, paramInt2 + 1);
        this.mantissa[i] = paramArrayOfChar[0];
        this.mantissa[i + 1] = '.';
        System.arraycopy(paramArrayOfChar, 1, this.mantissa, i + 2, paramInt2 - 1);
      } else {
        this.mantissa = create(paramBoolean, 3);
        this.mantissa[i] = paramArrayOfChar[0];
        this.mantissa[i + 1] = '.';
        this.mantissa[i + 2] = '0';
      } 
      boolean bool1 = (paramInt3 <= 0);
      if (bool1) {
        j = -paramInt3 + 1;
        bool = true;
      } else {
        j = paramInt3 - 1;
        bool = false;
      } 
      if (j <= 9) {
        this.exponent = create(bool1, 1);
        this.exponent[bool] = (char)(j + 48);
      } else if (j <= 99) {
        this.exponent = create(bool1, 2);
        this.exponent[bool] = (char)(j / 10 + 48);
        this.exponent[bool + true] = (char)(j % 10 + 48);
      } else {
        this.exponent = create(bool1, 3);
        this.exponent[bool] = (char)(j / 100 + 48);
        j %= 100;
        this.exponent[bool + true] = (char)(j / 10 + 48);
        this.exponent[bool + 2] = (char)(j % 10 + 48);
      } 
    } 
  }
  
  private static char[] create(boolean paramBoolean, int paramInt) {
    if (paramBoolean) {
      char[] arrayOfChar = new char[paramInt + 1];
      arrayOfChar[0] = '-';
      return arrayOfChar;
    } 
    return new char[paramInt];
  }
  
  private void fillDecimal(int paramInt1, char[] paramArrayOfChar, int paramInt2, int paramInt3, boolean paramBoolean) {
    int i = paramBoolean ? 1 : 0;
    if (paramInt3 > 0) {
      if (paramInt2 < paramInt3) {
        this.mantissa = create(paramBoolean, paramInt3);
        System.arraycopy(paramArrayOfChar, 0, this.mantissa, i, paramInt2);
        Arrays.fill(this.mantissa, i + paramInt2, i + paramInt3, '0');
      } else {
        int j = Math.min(paramInt2 - paramInt3, paramInt1);
        this.mantissa = create(paramBoolean, paramInt3 + ((j > 0) ? (j + 1) : 0));
        System.arraycopy(paramArrayOfChar, 0, this.mantissa, i, paramInt3);
        if (j > 0) {
          this.mantissa[i + paramInt3] = '.';
          System.arraycopy(paramArrayOfChar, paramInt3, this.mantissa, i + paramInt3 + 1, j);
        } 
      } 
    } else if (paramInt3 <= 0) {
      int j = Math.max(0, Math.min(-paramInt3, paramInt1));
      int k = Math.max(0, Math.min(paramInt2, paramInt1 + paramInt3));
      if (j > 0) {
        this.mantissa = create(paramBoolean, j + 2 + k);
        this.mantissa[i] = '0';
        this.mantissa[i + 1] = '.';
        Arrays.fill(this.mantissa, i + 2, i + 2 + j, '0');
        if (k > 0)
          System.arraycopy(paramArrayOfChar, 0, this.mantissa, i + 2 + j, k); 
      } else if (k > 0) {
        this.mantissa = create(paramBoolean, j + 2 + k);
        this.mantissa[i] = '0';
        this.mantissa[i + 1] = '.';
        System.arraycopy(paramArrayOfChar, 0, this.mantissa, i + 2, k);
      } else {
        this.mantissa = create(paramBoolean, 1);
        this.mantissa[i] = '0';
      } 
    } 
  }
  
  private void fillScientific(int paramInt1, char[] paramArrayOfChar, int paramInt2, int paramInt3, boolean paramBoolean) {
    int j;
    byte b;
    boolean bool = paramBoolean ? 1 : 0;
    int i = Math.max(0, Math.min(paramInt2 - 1, paramInt1));
    if (i > 0) {
      this.mantissa = create(paramBoolean, i + 2);
      this.mantissa[bool] = paramArrayOfChar[0];
      this.mantissa[bool + true] = '.';
      System.arraycopy(paramArrayOfChar, 1, this.mantissa, bool + 2, i);
    } else {
      this.mantissa = create(paramBoolean, 1);
      this.mantissa[bool] = paramArrayOfChar[0];
    } 
    if (paramInt3 <= 0) {
      b = 45;
      j = -paramInt3 + 1;
    } else {
      b = 43;
      j = paramInt3 - 1;
    } 
    if (j <= 9) {
      this.exponent = new char[] { b, '0', (char)(j + 48) };
    } else if (j <= 99) {
      this.exponent = new char[] { b, (char)(j / 10 + 48), (char)(j % 10 + 48) };
    } else {
      char c = (char)(j / 100 + 48);
      j %= 100;
      this.exponent = new char[] { b, c, (char)(j / 10 + 48), (char)(j % 10 + 48) };
    } 
  }
  
  public enum Form {
    SCIENTIFIC, COMPATIBLE, DECIMAL_FLOAT, GENERAL;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\misc\FormattedFloatingDecimal.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */