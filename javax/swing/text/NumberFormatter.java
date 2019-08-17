package javax.swing.text;

import java.lang.reflect.Constructor;
import java.text.AttributedCharacterIterator;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.Format;
import java.text.NumberFormat;
import java.text.ParseException;
import java.util.Map;
import sun.reflect.misc.ReflectUtil;
import sun.swing.SwingUtilities2;

public class NumberFormatter extends InternationalFormatter {
  private String specialChars;
  
  public NumberFormatter() { this(NumberFormat.getNumberInstance()); }
  
  public NumberFormatter(NumberFormat paramNumberFormat) {
    super(paramNumberFormat);
    setFormat(paramNumberFormat);
    setAllowsInvalid(true);
    setCommitsOnValidEdit(false);
    setOverwriteMode(false);
  }
  
  public void setFormat(Format paramFormat) {
    super.setFormat(paramFormat);
    DecimalFormatSymbols decimalFormatSymbols = getDecimalFormatSymbols();
    if (decimalFormatSymbols != null) {
      StringBuilder stringBuilder = new StringBuilder();
      stringBuilder.append(decimalFormatSymbols.getCurrencySymbol());
      stringBuilder.append(decimalFormatSymbols.getDecimalSeparator());
      stringBuilder.append(decimalFormatSymbols.getGroupingSeparator());
      stringBuilder.append(decimalFormatSymbols.getInfinity());
      stringBuilder.append(decimalFormatSymbols.getInternationalCurrencySymbol());
      stringBuilder.append(decimalFormatSymbols.getMinusSign());
      stringBuilder.append(decimalFormatSymbols.getMonetaryDecimalSeparator());
      stringBuilder.append(decimalFormatSymbols.getNaN());
      stringBuilder.append(decimalFormatSymbols.getPercent());
      stringBuilder.append('+');
      this.specialChars = stringBuilder.toString();
    } else {
      this.specialChars = "";
    } 
  }
  
  Object stringToValue(String paramString, Format paramFormat) throws ParseException {
    if (paramFormat == null)
      return paramString; 
    Object object = paramFormat.parseObject(paramString);
    return convertValueToValueClass(object, getValueClass());
  }
  
  private Object convertValueToValueClass(Object paramObject, Class paramClass) {
    if (paramClass != null && paramObject instanceof Number) {
      Number number = (Number)paramObject;
      if (paramClass == Integer.class)
        return Integer.valueOf(number.intValue()); 
      if (paramClass == Long.class)
        return Long.valueOf(number.longValue()); 
      if (paramClass == Float.class)
        return Float.valueOf(number.floatValue()); 
      if (paramClass == Double.class)
        return Double.valueOf(number.doubleValue()); 
      if (paramClass == Byte.class)
        return Byte.valueOf(number.byteValue()); 
      if (paramClass == Short.class)
        return Short.valueOf(number.shortValue()); 
    } 
    return paramObject;
  }
  
  private char getPositiveSign() { return '+'; }
  
  private char getMinusSign() {
    DecimalFormatSymbols decimalFormatSymbols = getDecimalFormatSymbols();
    return (decimalFormatSymbols != null) ? decimalFormatSymbols.getMinusSign() : 45;
  }
  
  private char getDecimalSeparator() {
    DecimalFormatSymbols decimalFormatSymbols = getDecimalFormatSymbols();
    return (decimalFormatSymbols != null) ? decimalFormatSymbols.getDecimalSeparator() : 46;
  }
  
  private DecimalFormatSymbols getDecimalFormatSymbols() {
    Format format = getFormat();
    return (format instanceof DecimalFormat) ? ((DecimalFormat)format).getDecimalFormatSymbols() : null;
  }
  
  boolean isLegalInsertText(String paramString) {
    if (getAllowsInvalid())
      return true; 
    for (int i = paramString.length() - 1; i >= 0; i--) {
      char c = paramString.charAt(i);
      if (!Character.isDigit(c) && this.specialChars.indexOf(c) == -1)
        return false; 
    } 
    return true;
  }
  
  boolean isLiteral(Map paramMap) {
    if (!super.isLiteral(paramMap)) {
      if (paramMap == null)
        return false; 
      int i = paramMap.size();
      if (paramMap.get(NumberFormat.Field.GROUPING_SEPARATOR) != null) {
        i--;
        if (paramMap.get(NumberFormat.Field.INTEGER) != null)
          i--; 
      } 
      if (paramMap.get(NumberFormat.Field.EXPONENT_SYMBOL) != null)
        i--; 
      if (paramMap.get(NumberFormat.Field.PERCENT) != null)
        i--; 
      if (paramMap.get(NumberFormat.Field.PERMILLE) != null)
        i--; 
      if (paramMap.get(NumberFormat.Field.CURRENCY) != null)
        i--; 
      if (paramMap.get(NumberFormat.Field.SIGN) != null)
        i--; 
      return (i == 0);
    } 
    return true;
  }
  
  boolean isNavigatable(int paramInt) { return !super.isNavigatable(paramInt) ? ((getBufferedChar(paramInt) == getDecimalSeparator())) : true; }
  
  private NumberFormat.Field getFieldFrom(int paramInt1, int paramInt2) {
    if (isValidMask()) {
      int i = getFormattedTextField().getDocument().getLength();
      AttributedCharacterIterator attributedCharacterIterator = getIterator();
      if (paramInt1 >= i)
        paramInt1 += paramInt2; 
      while (paramInt1 >= 0 && paramInt1 < i) {
        attributedCharacterIterator.setIndex(paramInt1);
        Map map = attributedCharacterIterator.getAttributes();
        if (map != null && map.size() > 0)
          for (Object object : map.keySet()) {
            if (object instanceof NumberFormat.Field)
              return (NumberFormat.Field)object; 
          }  
        paramInt1 += paramInt2;
      } 
    } 
    return null;
  }
  
  void replace(DocumentFilter.FilterBypass paramFilterBypass, int paramInt1, int paramInt2, String paramString, AttributeSet paramAttributeSet) throws BadLocationException {
    if (!getAllowsInvalid() && paramInt2 == 0 && paramString != null && paramString.length() == 1 && toggleSignIfNecessary(paramFilterBypass, paramInt1, paramString.charAt(0)))
      return; 
    super.replace(paramFilterBypass, paramInt1, paramInt2, paramString, paramAttributeSet);
  }
  
  private boolean toggleSignIfNecessary(DocumentFilter.FilterBypass paramFilterBypass, int paramInt, char paramChar) throws BadLocationException {
    if (paramChar == getMinusSign() || paramChar == getPositiveSign()) {
      NumberFormat.Field field = getFieldFrom(paramInt, -1);
      try {
        Object object;
        if (field == null || (field != NumberFormat.Field.EXPONENT && field != NumberFormat.Field.EXPONENT_SYMBOL && field != NumberFormat.Field.EXPONENT_SIGN)) {
          object = toggleSign((paramChar == getPositiveSign()));
        } else {
          object = toggleExponentSign(paramInt, paramChar);
        } 
        if (object != null && isValidValue(object, false)) {
          int i = getLiteralCountTo(paramInt);
          String str = valueToString(object);
          paramFilterBypass.remove(0, paramFilterBypass.getDocument().getLength());
          paramFilterBypass.insertString(0, str, null);
          updateValue(object);
          repositionCursor(getLiteralCountTo(paramInt) - i + paramInt, 1);
          return true;
        } 
      } catch (ParseException parseException) {
        invalidEdit();
      } 
    } 
    return false;
  }
  
  private Object toggleSign(boolean paramBoolean) throws ParseException {
    Object object = stringToValue(getFormattedTextField().getText());
    if (object != null) {
      String str = object.toString();
      if (str != null && str.length() > 0) {
        if (paramBoolean) {
          if (str.charAt(0) == '-')
            str = str.substring(1); 
        } else {
          if (str.charAt(0) == '+')
            str = str.substring(1); 
          if (str.length() > 0 && str.charAt(0) != '-')
            str = "-" + str; 
        } 
        if (str != null) {
          Class clazz = getValueClass();
          if (clazz == null)
            clazz = object.getClass(); 
          try {
            ReflectUtil.checkPackageAccess(clazz);
            SwingUtilities2.checkAccess(clazz.getModifiers());
            Constructor constructor = clazz.getConstructor(new Class[] { String.class });
            if (constructor != null) {
              SwingUtilities2.checkAccess(constructor.getModifiers());
              return constructor.newInstance(new Object[] { str });
            } 
          } catch (Throwable throwable) {}
        } 
      } 
    } 
    return null;
  }
  
  private Object toggleExponentSign(int paramInt, char paramChar) throws BadLocationException, ParseException {
    String str = getFormattedTextField().getText();
    byte b = 0;
    int i = getAttributeStart(NumberFormat.Field.EXPONENT_SIGN);
    if (i >= 0) {
      b = 1;
      paramInt = i;
    } 
    if (paramChar == getPositiveSign()) {
      str = getReplaceString(paramInt, b, null);
    } else {
      str = getReplaceString(paramInt, b, new String(new char[] { paramChar }));
    } 
    return stringToValue(str);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\text\NumberFormatter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */