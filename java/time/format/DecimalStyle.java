package java.time.format;

import java.text.DecimalFormatSymbols;
import java.util.Collections;
import java.util.HashSet;
import java.util.Locale;
import java.util.Objects;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

public final class DecimalStyle {
  public static final DecimalStyle STANDARD = new DecimalStyle('0', '+', '-', '.');
  
  private static final ConcurrentMap<Locale, DecimalStyle> CACHE = new ConcurrentHashMap(16, 0.75F, 2);
  
  private final char zeroDigit;
  
  private final char positiveSign;
  
  private final char negativeSign;
  
  private final char decimalSeparator;
  
  public static Set<Locale> getAvailableLocales() {
    Locale[] arrayOfLocale = DecimalFormatSymbols.getAvailableLocales();
    HashSet hashSet = new HashSet(arrayOfLocale.length);
    Collections.addAll(hashSet, arrayOfLocale);
    return hashSet;
  }
  
  public static DecimalStyle ofDefaultLocale() { return of(Locale.getDefault(Locale.Category.FORMAT)); }
  
  public static DecimalStyle of(Locale paramLocale) {
    Objects.requireNonNull(paramLocale, "locale");
    DecimalStyle decimalStyle = (DecimalStyle)CACHE.get(paramLocale);
    if (decimalStyle == null) {
      decimalStyle = create(paramLocale);
      CACHE.putIfAbsent(paramLocale, decimalStyle);
      decimalStyle = (DecimalStyle)CACHE.get(paramLocale);
    } 
    return decimalStyle;
  }
  
  private static DecimalStyle create(Locale paramLocale) {
    DecimalFormatSymbols decimalFormatSymbols = DecimalFormatSymbols.getInstance(paramLocale);
    char c1 = decimalFormatSymbols.getZeroDigit();
    char c2 = '+';
    char c3 = decimalFormatSymbols.getMinusSign();
    char c4 = decimalFormatSymbols.getDecimalSeparator();
    return (c1 == '0' && c3 == '-' && c4 == '.') ? STANDARD : new DecimalStyle(c1, c2, c3, c4);
  }
  
  private DecimalStyle(char paramChar1, char paramChar2, char paramChar3, char paramChar4) {
    this.zeroDigit = paramChar1;
    this.positiveSign = paramChar2;
    this.negativeSign = paramChar3;
    this.decimalSeparator = paramChar4;
  }
  
  public char getZeroDigit() { return this.zeroDigit; }
  
  public DecimalStyle withZeroDigit(char paramChar) { return (paramChar == this.zeroDigit) ? this : new DecimalStyle(paramChar, this.positiveSign, this.negativeSign, this.decimalSeparator); }
  
  public char getPositiveSign() { return this.positiveSign; }
  
  public DecimalStyle withPositiveSign(char paramChar) { return (paramChar == this.positiveSign) ? this : new DecimalStyle(this.zeroDigit, paramChar, this.negativeSign, this.decimalSeparator); }
  
  public char getNegativeSign() { return this.negativeSign; }
  
  public DecimalStyle withNegativeSign(char paramChar) { return (paramChar == this.negativeSign) ? this : new DecimalStyle(this.zeroDigit, this.positiveSign, paramChar, this.decimalSeparator); }
  
  public char getDecimalSeparator() { return this.decimalSeparator; }
  
  public DecimalStyle withDecimalSeparator(char paramChar) { return (paramChar == this.decimalSeparator) ? this : new DecimalStyle(this.zeroDigit, this.positiveSign, this.negativeSign, paramChar); }
  
  int convertToDigit(char paramChar) {
    char c = paramChar - this.zeroDigit;
    return (c >= '\000' && c <= '\t') ? c : -1;
  }
  
  String convertNumberToI18N(String paramString) {
    if (this.zeroDigit == '0')
      return paramString; 
    char c = this.zeroDigit - '0';
    char[] arrayOfChar = paramString.toCharArray();
    for (byte b = 0; b < arrayOfChar.length; b++)
      arrayOfChar[b] = (char)(arrayOfChar[b] + c); 
    return new String(arrayOfChar);
  }
  
  public boolean equals(Object paramObject) {
    if (this == paramObject)
      return true; 
    if (paramObject instanceof DecimalStyle) {
      DecimalStyle decimalStyle = (DecimalStyle)paramObject;
      return (this.zeroDigit == decimalStyle.zeroDigit && this.positiveSign == decimalStyle.positiveSign && this.negativeSign == decimalStyle.negativeSign && this.decimalSeparator == decimalStyle.decimalSeparator);
    } 
    return false;
  }
  
  public int hashCode() { return this.zeroDigit + this.positiveSign + this.negativeSign + this.decimalSeparator; }
  
  public String toString() { return "DecimalStyle[" + this.zeroDigit + this.positiveSign + this.negativeSign + this.decimalSeparator + "]"; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\time\format\DecimalStyle.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */