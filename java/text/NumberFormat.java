package java.text;

import java.io.IOException;
import java.io.InvalidObjectException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.math.BigInteger;
import java.math.RoundingMode;
import java.text.spi.NumberFormatProvider;
import java.util.Currency;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import sun.util.locale.provider.LocaleProviderAdapter;
import sun.util.locale.provider.LocaleServiceProviderPool;

public abstract class NumberFormat extends Format {
  public static final int INTEGER_FIELD = 0;
  
  public static final int FRACTION_FIELD = 1;
  
  private static final int NUMBERSTYLE = 0;
  
  private static final int CURRENCYSTYLE = 1;
  
  private static final int PERCENTSTYLE = 2;
  
  private static final int SCIENTIFICSTYLE = 3;
  
  private static final int INTEGERSTYLE = 4;
  
  private boolean groupingUsed = true;
  
  private byte maxIntegerDigits = 40;
  
  private byte minIntegerDigits = 1;
  
  private byte maxFractionDigits = 3;
  
  private byte minFractionDigits = 0;
  
  private boolean parseIntegerOnly = false;
  
  private int maximumIntegerDigits = 40;
  
  private int minimumIntegerDigits = 1;
  
  private int maximumFractionDigits = 3;
  
  private int minimumFractionDigits = 0;
  
  static final int currentSerialVersion = 1;
  
  private int serialVersionOnStream = 1;
  
  static final long serialVersionUID = -2308460125733713944L;
  
  public StringBuffer format(Object paramObject, StringBuffer paramStringBuffer, FieldPosition paramFieldPosition) {
    if (paramObject instanceof Long || paramObject instanceof Integer || paramObject instanceof Short || paramObject instanceof Byte || paramObject instanceof java.util.concurrent.atomic.AtomicInteger || paramObject instanceof java.util.concurrent.atomic.AtomicLong || (paramObject instanceof BigInteger && ((BigInteger)paramObject).bitLength() < 64))
      return format(((Number)paramObject).longValue(), paramStringBuffer, paramFieldPosition); 
    if (paramObject instanceof Number)
      return format(((Number)paramObject).doubleValue(), paramStringBuffer, paramFieldPosition); 
    throw new IllegalArgumentException("Cannot format given Object as a Number");
  }
  
  public final Object parseObject(String paramString, ParsePosition paramParsePosition) { return parse(paramString, paramParsePosition); }
  
  public final String format(double paramDouble) {
    String str = fastFormat(paramDouble);
    return (str != null) ? str : format(paramDouble, new StringBuffer(), DontCareFieldPosition.INSTANCE).toString();
  }
  
  String fastFormat(double paramDouble) { return null; }
  
  public final String format(long paramLong) { return format(paramLong, new StringBuffer(), DontCareFieldPosition.INSTANCE).toString(); }
  
  public abstract StringBuffer format(double paramDouble, StringBuffer paramStringBuffer, FieldPosition paramFieldPosition);
  
  public abstract StringBuffer format(long paramLong, StringBuffer paramStringBuffer, FieldPosition paramFieldPosition);
  
  public abstract Number parse(String paramString, ParsePosition paramParsePosition);
  
  public Number parse(String paramString) throws ParseException {
    ParsePosition parsePosition = new ParsePosition(0);
    Number number = parse(paramString, parsePosition);
    if (parsePosition.index == 0)
      throw new ParseException("Unparseable number: \"" + paramString + "\"", parsePosition.errorIndex); 
    return number;
  }
  
  public boolean isParseIntegerOnly() { return this.parseIntegerOnly; }
  
  public void setParseIntegerOnly(boolean paramBoolean) { this.parseIntegerOnly = paramBoolean; }
  
  public static final NumberFormat getInstance() { return getInstance(Locale.getDefault(Locale.Category.FORMAT), 0); }
  
  public static NumberFormat getInstance(Locale paramLocale) { return getInstance(paramLocale, 0); }
  
  public static final NumberFormat getNumberInstance() { return getInstance(Locale.getDefault(Locale.Category.FORMAT), 0); }
  
  public static NumberFormat getNumberInstance(Locale paramLocale) { return getInstance(paramLocale, 0); }
  
  public static final NumberFormat getIntegerInstance() { return getInstance(Locale.getDefault(Locale.Category.FORMAT), 4); }
  
  public static NumberFormat getIntegerInstance(Locale paramLocale) { return getInstance(paramLocale, 4); }
  
  public static final NumberFormat getCurrencyInstance() { return getInstance(Locale.getDefault(Locale.Category.FORMAT), 1); }
  
  public static NumberFormat getCurrencyInstance(Locale paramLocale) { return getInstance(paramLocale, 1); }
  
  public static final NumberFormat getPercentInstance() { return getInstance(Locale.getDefault(Locale.Category.FORMAT), 2); }
  
  public static NumberFormat getPercentInstance(Locale paramLocale) { return getInstance(paramLocale, 2); }
  
  static final NumberFormat getScientificInstance() { return getInstance(Locale.getDefault(Locale.Category.FORMAT), 3); }
  
  static NumberFormat getScientificInstance(Locale paramLocale) { return getInstance(paramLocale, 3); }
  
  public static Locale[] getAvailableLocales() {
    LocaleServiceProviderPool localeServiceProviderPool = LocaleServiceProviderPool.getPool(NumberFormatProvider.class);
    return localeServiceProviderPool.getAvailableLocales();
  }
  
  public int hashCode() { return this.maximumIntegerDigits * 37 + this.maxFractionDigits; }
  
  public boolean equals(Object paramObject) {
    if (paramObject == null)
      return false; 
    if (this == paramObject)
      return true; 
    if (getClass() != paramObject.getClass())
      return false; 
    NumberFormat numberFormat = (NumberFormat)paramObject;
    return (this.maximumIntegerDigits == numberFormat.maximumIntegerDigits && this.minimumIntegerDigits == numberFormat.minimumIntegerDigits && this.maximumFractionDigits == numberFormat.maximumFractionDigits && this.minimumFractionDigits == numberFormat.minimumFractionDigits && this.groupingUsed == numberFormat.groupingUsed && this.parseIntegerOnly == numberFormat.parseIntegerOnly);
  }
  
  public Object clone() { return (NumberFormat)super.clone(); }
  
  public boolean isGroupingUsed() { return this.groupingUsed; }
  
  public void setGroupingUsed(boolean paramBoolean) { this.groupingUsed = paramBoolean; }
  
  public int getMaximumIntegerDigits() { return this.maximumIntegerDigits; }
  
  public void setMaximumIntegerDigits(int paramInt) {
    this.maximumIntegerDigits = Math.max(0, paramInt);
    if (this.minimumIntegerDigits > this.maximumIntegerDigits)
      this.minimumIntegerDigits = this.maximumIntegerDigits; 
  }
  
  public int getMinimumIntegerDigits() { return this.minimumIntegerDigits; }
  
  public void setMinimumIntegerDigits(int paramInt) {
    this.minimumIntegerDigits = Math.max(0, paramInt);
    if (this.minimumIntegerDigits > this.maximumIntegerDigits)
      this.maximumIntegerDigits = this.minimumIntegerDigits; 
  }
  
  public int getMaximumFractionDigits() { return this.maximumFractionDigits; }
  
  public void setMaximumFractionDigits(int paramInt) {
    this.maximumFractionDigits = Math.max(0, paramInt);
    if (this.maximumFractionDigits < this.minimumFractionDigits)
      this.minimumFractionDigits = this.maximumFractionDigits; 
  }
  
  public int getMinimumFractionDigits() { return this.minimumFractionDigits; }
  
  public void setMinimumFractionDigits(int paramInt) {
    this.minimumFractionDigits = Math.max(0, paramInt);
    if (this.maximumFractionDigits < this.minimumFractionDigits)
      this.maximumFractionDigits = this.minimumFractionDigits; 
  }
  
  public Currency getCurrency() { throw new UnsupportedOperationException(); }
  
  public void setCurrency(Currency paramCurrency) { throw new UnsupportedOperationException(); }
  
  public RoundingMode getRoundingMode() { throw new UnsupportedOperationException(); }
  
  public void setRoundingMode(RoundingMode paramRoundingMode) { throw new UnsupportedOperationException(); }
  
  private static NumberFormat getInstance(Locale paramLocale, int paramInt) {
    LocaleProviderAdapter localeProviderAdapter = LocaleProviderAdapter.getAdapter(NumberFormatProvider.class, paramLocale);
    NumberFormat numberFormat = getInstance(localeProviderAdapter, paramLocale, paramInt);
    if (numberFormat == null)
      numberFormat = getInstance(LocaleProviderAdapter.forJRE(), paramLocale, paramInt); 
    return numberFormat;
  }
  
  private static NumberFormat getInstance(LocaleProviderAdapter paramLocaleProviderAdapter, Locale paramLocale, int paramInt) {
    NumberFormatProvider numberFormatProvider = paramLocaleProviderAdapter.getNumberFormatProvider();
    NumberFormat numberFormat = null;
    switch (paramInt) {
      case 0:
        numberFormat = numberFormatProvider.getNumberInstance(paramLocale);
        break;
      case 2:
        numberFormat = numberFormatProvider.getPercentInstance(paramLocale);
        break;
      case 1:
        numberFormat = numberFormatProvider.getCurrencyInstance(paramLocale);
        break;
      case 4:
        numberFormat = numberFormatProvider.getIntegerInstance(paramLocale);
        break;
    } 
    return numberFormat;
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream) throws IOException, ClassNotFoundException {
    paramObjectInputStream.defaultReadObject();
    if (this.serialVersionOnStream < 1) {
      this.maximumIntegerDigits = this.maxIntegerDigits;
      this.minimumIntegerDigits = this.minIntegerDigits;
      this.maximumFractionDigits = this.maxFractionDigits;
      this.minimumFractionDigits = this.minFractionDigits;
    } 
    if (this.minimumIntegerDigits > this.maximumIntegerDigits || this.minimumFractionDigits > this.maximumFractionDigits || this.minimumIntegerDigits < 0 || this.minimumFractionDigits < 0)
      throw new InvalidObjectException("Digit count range invalid"); 
    this.serialVersionOnStream = 1;
  }
  
  private void writeObject(ObjectOutputStream paramObjectOutputStream) throws IOException {
    this.maxIntegerDigits = (this.maximumIntegerDigits > 127) ? Byte.MAX_VALUE : (byte)this.maximumIntegerDigits;
    this.minIntegerDigits = (this.minimumIntegerDigits > 127) ? Byte.MAX_VALUE : (byte)this.minimumIntegerDigits;
    this.maxFractionDigits = (this.maximumFractionDigits > 127) ? Byte.MAX_VALUE : (byte)this.maximumFractionDigits;
    this.minFractionDigits = (this.minimumFractionDigits > 127) ? Byte.MAX_VALUE : (byte)this.minimumFractionDigits;
    paramObjectOutputStream.defaultWriteObject();
  }
  
  public static class Field extends Format.Field {
    private static final long serialVersionUID = 7494728892700160890L;
    
    private static final Map<String, Field> instanceMap = new HashMap(11);
    
    public static final Field INTEGER = new Field("integer");
    
    public static final Field FRACTION = new Field("fraction");
    
    public static final Field EXPONENT = new Field("exponent");
    
    public static final Field DECIMAL_SEPARATOR = new Field("decimal separator");
    
    public static final Field SIGN = new Field("sign");
    
    public static final Field GROUPING_SEPARATOR = new Field("grouping separator");
    
    public static final Field EXPONENT_SYMBOL = new Field("exponent symbol");
    
    public static final Field PERCENT = new Field("percent");
    
    public static final Field PERMILLE = new Field("per mille");
    
    public static final Field CURRENCY = new Field("currency");
    
    public static final Field EXPONENT_SIGN = new Field("exponent sign");
    
    protected Field(String param1String) {
      super(param1String);
      if (getClass() == Field.class)
        instanceMap.put(param1String, this); 
    }
    
    protected Object readResolve() {
      if (getClass() != Field.class)
        throw new InvalidObjectException("subclass didn't correctly implement readResolve"); 
      Object object = instanceMap.get(getName());
      if (object != null)
        return object; 
      throw new InvalidObjectException("unknown attribute name");
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\text\NumberFormat.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */