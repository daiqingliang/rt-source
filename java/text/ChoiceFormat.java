package java.text;

import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.util.Arrays;

public class ChoiceFormat extends NumberFormat {
  private static final long serialVersionUID = 1795184449645032964L;
  
  private double[] choiceLimits;
  
  private String[] choiceFormats;
  
  static final long SIGN = -9223372036854775808L;
  
  static final long EXPONENT = 9218868437227405312L;
  
  static final long POSITIVEINFINITY = 9218868437227405312L;
  
  public void applyPattern(String paramString) {
    StringBuffer[] arrayOfStringBuffer = new StringBuffer[2];
    for (byte b1 = 0; b1 < arrayOfStringBuffer.length; b1++)
      arrayOfStringBuffer[b1] = new StringBuffer(); 
    double[] arrayOfDouble = new double[30];
    String[] arrayOfString = new String[30];
    byte b2 = 0;
    boolean bool1 = false;
    double d1 = 0.0D;
    double d2 = NaND;
    boolean bool2 = false;
    for (byte b3 = 0; b3 < paramString.length(); b3++) {
      char c = paramString.charAt(b3);
      if (c == '\'') {
        if (b3 + 1 < paramString.length() && paramString.charAt(b3 + 1) == c) {
          arrayOfStringBuffer[bool1].append(c);
          b3++;
        } else {
          bool2 = !bool2 ? 1 : 0;
        } 
      } else if (bool2) {
        arrayOfStringBuffer[bool1].append(c);
      } else if (c == '<' || c == '#' || c == '≤') {
        if (arrayOfStringBuffer[0].length() == 0)
          throw new IllegalArgumentException(); 
        try {
          String str = arrayOfStringBuffer[0].toString();
          if (str.equals("∞")) {
            d1 = Double.POSITIVE_INFINITY;
          } else if (str.equals("-∞")) {
            d1 = Double.NEGATIVE_INFINITY;
          } else {
            d1 = Double.valueOf(arrayOfStringBuffer[0].toString()).doubleValue();
          } 
        } catch (Exception exception) {
          throw new IllegalArgumentException();
        } 
        if (c == '<' && d1 != Double.POSITIVE_INFINITY && d1 != Double.NEGATIVE_INFINITY)
          d1 = nextDouble(d1); 
        if (d1 <= d2)
          throw new IllegalArgumentException(); 
        arrayOfStringBuffer[0].setLength(0);
        bool1 = true;
      } else if (c == '|') {
        if (b2 == arrayOfDouble.length) {
          arrayOfDouble = doubleArraySize(arrayOfDouble);
          arrayOfString = doubleArraySize(arrayOfString);
        } 
        arrayOfDouble[b2] = d1;
        arrayOfString[b2] = arrayOfStringBuffer[1].toString();
        b2++;
        d2 = d1;
        arrayOfStringBuffer[1].setLength(0);
        bool1 = false;
      } else {
        arrayOfStringBuffer[bool1].append(c);
      } 
    } 
    if (bool1 == true) {
      if (b2 == arrayOfDouble.length) {
        arrayOfDouble = doubleArraySize(arrayOfDouble);
        arrayOfString = doubleArraySize(arrayOfString);
      } 
      arrayOfDouble[b2] = d1;
      arrayOfString[b2] = arrayOfStringBuffer[1].toString();
      b2++;
    } 
    this.choiceLimits = new double[b2];
    System.arraycopy(arrayOfDouble, 0, this.choiceLimits, 0, b2);
    this.choiceFormats = new String[b2];
    System.arraycopy(arrayOfString, 0, this.choiceFormats, 0, b2);
  }
  
  public String toPattern() {
    StringBuffer stringBuffer = new StringBuffer();
    for (byte b = 0; b < this.choiceLimits.length; b++) {
      if (b)
        stringBuffer.append('|'); 
      double d1 = previousDouble(this.choiceLimits[b]);
      double d2 = Math.abs(Math.IEEEremainder(this.choiceLimits[b], 1.0D));
      double d3 = Math.abs(Math.IEEEremainder(d1, 1.0D));
      if (d2 < d3) {
        stringBuffer.append("" + this.choiceLimits[b]);
        stringBuffer.append('#');
      } else {
        if (this.choiceLimits[b] == Double.POSITIVE_INFINITY) {
          stringBuffer.append("∞");
        } else if (this.choiceLimits[b] == Double.NEGATIVE_INFINITY) {
          stringBuffer.append("-∞");
        } else {
          stringBuffer.append("" + d1);
        } 
        stringBuffer.append('<');
      } 
      String str = this.choiceFormats[b];
      boolean bool = (str.indexOf('<') >= 0 || str.indexOf('#') >= 0 || str.indexOf('≤') >= 0 || str.indexOf('|') >= 0) ? 1 : 0;
      if (bool)
        stringBuffer.append('\''); 
      if (str.indexOf('\'') < 0) {
        stringBuffer.append(str);
      } else {
        for (byte b1 = 0; b1 < str.length(); b1++) {
          char c = str.charAt(b1);
          stringBuffer.append(c);
          if (c == '\'')
            stringBuffer.append(c); 
        } 
      } 
      if (bool)
        stringBuffer.append('\''); 
    } 
    return stringBuffer.toString();
  }
  
  public ChoiceFormat(String paramString) { applyPattern(paramString); }
  
  public ChoiceFormat(double[] paramArrayOfDouble, String[] paramArrayOfString) { setChoices(paramArrayOfDouble, paramArrayOfString); }
  
  public void setChoices(double[] paramArrayOfDouble, String[] paramArrayOfString) {
    if (paramArrayOfDouble.length != paramArrayOfString.length)
      throw new IllegalArgumentException("Array and limit arrays must be of the same length."); 
    this.choiceLimits = Arrays.copyOf(paramArrayOfDouble, paramArrayOfDouble.length);
    this.choiceFormats = (String[])Arrays.copyOf(paramArrayOfString, paramArrayOfString.length);
  }
  
  public double[] getLimits() { return Arrays.copyOf(this.choiceLimits, this.choiceLimits.length); }
  
  public Object[] getFormats() { return Arrays.copyOf(this.choiceFormats, this.choiceFormats.length); }
  
  public StringBuffer format(long paramLong, StringBuffer paramStringBuffer, FieldPosition paramFieldPosition) { return format(paramLong, paramStringBuffer, paramFieldPosition); }
  
  public StringBuffer format(double paramDouble, StringBuffer paramStringBuffer, FieldPosition paramFieldPosition) {
    byte b;
    for (b = 0; b < this.choiceLimits.length && paramDouble >= this.choiceLimits[b]; b++);
    if (--b < 0)
      b = 0; 
    return paramStringBuffer.append(this.choiceFormats[b]);
  }
  
  public Number parse(String paramString, ParsePosition paramParsePosition) {
    int i = paramParsePosition.index;
    int j = i;
    double d1 = NaND;
    double d2 = 0.0D;
    for (byte b = 0; b < this.choiceFormats.length; b++) {
      String str = this.choiceFormats[b];
      if (paramString.regionMatches(i, str, 0, str.length())) {
        paramParsePosition.index = i + str.length();
        d2 = this.choiceLimits[b];
        if (paramParsePosition.index > j) {
          j = paramParsePosition.index;
          d1 = d2;
          if (j == paramString.length())
            break; 
        } 
      } 
    } 
    paramParsePosition.index = j;
    if (paramParsePosition.index == i)
      paramParsePosition.errorIndex = j; 
    return new Double(d1);
  }
  
  public static final double nextDouble(double paramDouble) { return nextDouble(paramDouble, true); }
  
  public static final double previousDouble(double paramDouble) { return nextDouble(paramDouble, false); }
  
  public Object clone() {
    ChoiceFormat choiceFormat = (ChoiceFormat)super.clone();
    choiceFormat.choiceLimits = (double[])this.choiceLimits.clone();
    choiceFormat.choiceFormats = (String[])this.choiceFormats.clone();
    return choiceFormat;
  }
  
  public int hashCode() {
    int i = this.choiceLimits.length;
    if (this.choiceFormats.length > 0)
      i ^= this.choiceFormats[this.choiceFormats.length - 1].hashCode(); 
    return i;
  }
  
  public boolean equals(Object paramObject) {
    if (paramObject == null)
      return false; 
    if (this == paramObject)
      return true; 
    if (getClass() != paramObject.getClass())
      return false; 
    ChoiceFormat choiceFormat = (ChoiceFormat)paramObject;
    return (Arrays.equals(this.choiceLimits, choiceFormat.choiceLimits) && Arrays.equals(this.choiceFormats, choiceFormat.choiceFormats));
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream) throws IOException, ClassNotFoundException {
    paramObjectInputStream.defaultReadObject();
    if (this.choiceLimits.length != this.choiceFormats.length)
      throw new InvalidObjectException("limits and format arrays of different length."); 
  }
  
  public static double nextDouble(double paramDouble, boolean paramBoolean) {
    if (Double.isNaN(paramDouble))
      return paramDouble; 
    if (paramDouble == 0.0D) {
      double d = Double.longBitsToDouble(1L);
      return paramBoolean ? d : -d;
    } 
    long l1 = Double.doubleToLongBits(paramDouble);
    long l2 = l1 & Float.MAX_VALUE;
    if (((l1 > 0L)) == paramBoolean) {
      if (l2 != 9218868437227405312L)
        l2++; 
    } else {
      l2--;
    } 
    long l3 = l1 & Float.MIN_VALUE;
    return Double.longBitsToDouble(l2 | l3);
  }
  
  private static double[] doubleArraySize(double[] paramArrayOfDouble) {
    int i = paramArrayOfDouble.length;
    double[] arrayOfDouble = new double[i * 2];
    System.arraycopy(paramArrayOfDouble, 0, arrayOfDouble, 0, i);
    return arrayOfDouble;
  }
  
  private String[] doubleArraySize(String[] paramArrayOfString) {
    int i = paramArrayOfString.length;
    String[] arrayOfString = new String[i * 2];
    System.arraycopy(paramArrayOfString, 0, arrayOfString, 0, i);
    return arrayOfString;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\text\ChoiceFormat.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */