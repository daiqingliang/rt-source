package java.text;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.Serializable;
import java.text.spi.DecimalFormatSymbolsProvider;
import java.util.Currency;
import java.util.Locale;
import sun.util.locale.provider.LocaleProviderAdapter;
import sun.util.locale.provider.LocaleServiceProviderPool;

public class DecimalFormatSymbols implements Cloneable, Serializable {
  private char zeroDigit;
  
  private char groupingSeparator;
  
  private char decimalSeparator;
  
  private char perMill;
  
  private char percent;
  
  private char digit;
  
  private char patternSeparator;
  
  private String infinity;
  
  private String NaN;
  
  private char minusSign;
  
  private String currencySymbol;
  
  private String intlCurrencySymbol;
  
  private char monetarySeparator;
  
  private char exponential;
  
  private String exponentialSeparator;
  
  private Locale locale;
  
  private Currency currency;
  
  static final long serialVersionUID = 5772796243397350300L;
  
  private static final int currentSerialVersion = 3;
  
  private int serialVersionOnStream = 3;
  
  public DecimalFormatSymbols() { initialize(Locale.getDefault(Locale.Category.FORMAT)); }
  
  public DecimalFormatSymbols(Locale paramLocale) { initialize(paramLocale); }
  
  public static Locale[] getAvailableLocales() {
    LocaleServiceProviderPool localeServiceProviderPool = LocaleServiceProviderPool.getPool(DecimalFormatSymbolsProvider.class);
    return localeServiceProviderPool.getAvailableLocales();
  }
  
  public static final DecimalFormatSymbols getInstance() { return getInstance(Locale.getDefault(Locale.Category.FORMAT)); }
  
  public static final DecimalFormatSymbols getInstance(Locale paramLocale) {
    LocaleProviderAdapter localeProviderAdapter = LocaleProviderAdapter.getAdapter(DecimalFormatSymbolsProvider.class, paramLocale);
    DecimalFormatSymbolsProvider decimalFormatSymbolsProvider = localeProviderAdapter.getDecimalFormatSymbolsProvider();
    DecimalFormatSymbols decimalFormatSymbols = decimalFormatSymbolsProvider.getInstance(paramLocale);
    if (decimalFormatSymbols == null) {
      decimalFormatSymbolsProvider = LocaleProviderAdapter.forJRE().getDecimalFormatSymbolsProvider();
      decimalFormatSymbols = decimalFormatSymbolsProvider.getInstance(paramLocale);
    } 
    return decimalFormatSymbols;
  }
  
  public char getZeroDigit() { return this.zeroDigit; }
  
  public void setZeroDigit(char paramChar) { this.zeroDigit = paramChar; }
  
  public char getGroupingSeparator() { return this.groupingSeparator; }
  
  public void setGroupingSeparator(char paramChar) { this.groupingSeparator = paramChar; }
  
  public char getDecimalSeparator() { return this.decimalSeparator; }
  
  public void setDecimalSeparator(char paramChar) { this.decimalSeparator = paramChar; }
  
  public char getPerMill() { return this.perMill; }
  
  public void setPerMill(char paramChar) { this.perMill = paramChar; }
  
  public char getPercent() { return this.percent; }
  
  public void setPercent(char paramChar) { this.percent = paramChar; }
  
  public char getDigit() { return this.digit; }
  
  public void setDigit(char paramChar) { this.digit = paramChar; }
  
  public char getPatternSeparator() { return this.patternSeparator; }
  
  public void setPatternSeparator(char paramChar) { this.patternSeparator = paramChar; }
  
  public String getInfinity() { return this.infinity; }
  
  public void setInfinity(String paramString) { this.infinity = paramString; }
  
  public String getNaN() { return this.NaN; }
  
  public void setNaN(String paramString) { this.NaN = paramString; }
  
  public char getMinusSign() { return this.minusSign; }
  
  public void setMinusSign(char paramChar) { this.minusSign = paramChar; }
  
  public String getCurrencySymbol() { return this.currencySymbol; }
  
  public void setCurrencySymbol(String paramString) { this.currencySymbol = paramString; }
  
  public String getInternationalCurrencySymbol() { return this.intlCurrencySymbol; }
  
  public void setInternationalCurrencySymbol(String paramString) {
    this.intlCurrencySymbol = paramString;
    this.currency = null;
    if (paramString != null)
      try {
        this.currency = Currency.getInstance(paramString);
        this.currencySymbol = this.currency.getSymbol();
      } catch (IllegalArgumentException illegalArgumentException) {} 
  }
  
  public Currency getCurrency() { return this.currency; }
  
  public void setCurrency(Currency paramCurrency) {
    if (paramCurrency == null)
      throw new NullPointerException(); 
    this.currency = paramCurrency;
    this.intlCurrencySymbol = paramCurrency.getCurrencyCode();
    this.currencySymbol = paramCurrency.getSymbol(this.locale);
  }
  
  public char getMonetaryDecimalSeparator() { return this.monetarySeparator; }
  
  public void setMonetaryDecimalSeparator(char paramChar) { this.monetarySeparator = paramChar; }
  
  char getExponentialSymbol() { return this.exponential; }
  
  public String getExponentSeparator() { return this.exponentialSeparator; }
  
  void setExponentialSymbol(char paramChar) { this.exponential = paramChar; }
  
  public void setExponentSeparator(String paramString) {
    if (paramString == null)
      throw new NullPointerException(); 
    this.exponentialSeparator = paramString;
  }
  
  public Object clone() {
    try {
      return (DecimalFormatSymbols)super.clone();
    } catch (CloneNotSupportedException cloneNotSupportedException) {
      throw new InternalError(cloneNotSupportedException);
    } 
  }
  
  public boolean equals(Object paramObject) {
    if (paramObject == null)
      return false; 
    if (this == paramObject)
      return true; 
    if (getClass() != paramObject.getClass())
      return false; 
    DecimalFormatSymbols decimalFormatSymbols = (DecimalFormatSymbols)paramObject;
    return (this.zeroDigit == decimalFormatSymbols.zeroDigit && this.groupingSeparator == decimalFormatSymbols.groupingSeparator && this.decimalSeparator == decimalFormatSymbols.decimalSeparator && this.percent == decimalFormatSymbols.percent && this.perMill == decimalFormatSymbols.perMill && this.digit == decimalFormatSymbols.digit && this.minusSign == decimalFormatSymbols.minusSign && this.patternSeparator == decimalFormatSymbols.patternSeparator && this.infinity.equals(decimalFormatSymbols.infinity) && this.NaN.equals(decimalFormatSymbols.NaN) && this.currencySymbol.equals(decimalFormatSymbols.currencySymbol) && this.intlCurrencySymbol.equals(decimalFormatSymbols.intlCurrencySymbol) && this.currency == decimalFormatSymbols.currency && this.monetarySeparator == decimalFormatSymbols.monetarySeparator && this.exponentialSeparator.equals(decimalFormatSymbols.exponentialSeparator) && this.locale.equals(decimalFormatSymbols.locale));
  }
  
  public int hashCode() {
    null = this.zeroDigit;
    null = null * '%' + this.groupingSeparator;
    return null * '%' + this.decimalSeparator;
  }
  
  private void initialize(Locale paramLocale) {
    this.locale = paramLocale;
    LocaleProviderAdapter localeProviderAdapter = LocaleProviderAdapter.getAdapter(DecimalFormatSymbolsProvider.class, paramLocale);
    if (!(localeProviderAdapter instanceof sun.util.locale.provider.ResourceBundleBasedAdapter))
      localeProviderAdapter = LocaleProviderAdapter.getResourceBundleBased(); 
    Object[] arrayOfObject = localeProviderAdapter.getLocaleResources(paramLocale).getDecimalFormatSymbolsData();
    String[] arrayOfString = (String[])arrayOfObject[0];
    this.decimalSeparator = arrayOfString[0].charAt(0);
    this.groupingSeparator = arrayOfString[1].charAt(0);
    this.patternSeparator = arrayOfString[2].charAt(0);
    this.percent = arrayOfString[3].charAt(0);
    this.zeroDigit = arrayOfString[4].charAt(0);
    this.digit = arrayOfString[5].charAt(0);
    this.minusSign = arrayOfString[6].charAt(0);
    this.exponential = arrayOfString[7].charAt(0);
    this.exponentialSeparator = arrayOfString[7];
    this.perMill = arrayOfString[8].charAt(0);
    this.infinity = arrayOfString[9];
    this.NaN = arrayOfString[10];
    if (paramLocale.getCountry().length() > 0)
      try {
        this.currency = Currency.getInstance(paramLocale);
      } catch (IllegalArgumentException illegalArgumentException) {} 
    if (this.currency != null) {
      this.intlCurrencySymbol = this.currency.getCurrencyCode();
      if (arrayOfObject[true] != null && arrayOfObject[true] == this.intlCurrencySymbol) {
        this.currencySymbol = (String)arrayOfObject[2];
      } else {
        this.currencySymbol = this.currency.getSymbol(paramLocale);
        arrayOfObject[1] = this.intlCurrencySymbol;
        arrayOfObject[2] = this.currencySymbol;
      } 
    } else {
      this.intlCurrencySymbol = "XXX";
      try {
        this.currency = Currency.getInstance(this.intlCurrencySymbol);
      } catch (IllegalArgumentException illegalArgumentException) {}
      this.currencySymbol = "¤";
    } 
    this.monetarySeparator = this.decimalSeparator;
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream) throws IOException, ClassNotFoundException {
    paramObjectInputStream.defaultReadObject();
    if (this.serialVersionOnStream < 1) {
      this.monetarySeparator = this.decimalSeparator;
      this.exponential = 'E';
    } 
    if (this.serialVersionOnStream < 2)
      this.locale = Locale.ROOT; 
    if (this.serialVersionOnStream < 3)
      this.exponentialSeparator = Character.toString(this.exponential); 
    this.serialVersionOnStream = 3;
    if (this.intlCurrencySymbol != null)
      try {
        this.currency = Currency.getInstance(this.intlCurrencySymbol);
      } catch (IllegalArgumentException illegalArgumentException) {} 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\text\DecimalFormatSymbols.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */