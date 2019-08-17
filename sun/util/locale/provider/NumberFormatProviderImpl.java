package sun.util.locale.provider;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.NumberFormat;
import java.text.spi.NumberFormatProvider;
import java.util.Currency;
import java.util.Locale;
import java.util.Set;

public class NumberFormatProviderImpl extends NumberFormatProvider implements AvailableLanguageTags {
  private static final int NUMBERSTYLE = 0;
  
  private static final int CURRENCYSTYLE = 1;
  
  private static final int PERCENTSTYLE = 2;
  
  private static final int SCIENTIFICSTYLE = 3;
  
  private static final int INTEGERSTYLE = 4;
  
  private final LocaleProviderAdapter.Type type;
  
  private final Set<String> langtags;
  
  public NumberFormatProviderImpl(LocaleProviderAdapter.Type paramType, Set<String> paramSet) {
    this.type = paramType;
    this.langtags = paramSet;
  }
  
  public Locale[] getAvailableLocales() { return LocaleProviderAdapter.forType(this.type).getAvailableLocales(); }
  
  public boolean isSupportedLocale(Locale paramLocale) { return LocaleProviderAdapter.isSupportedLocale(paramLocale, this.type, this.langtags); }
  
  public NumberFormat getCurrencyInstance(Locale paramLocale) { return getInstance(paramLocale, 1); }
  
  public NumberFormat getIntegerInstance(Locale paramLocale) { return getInstance(paramLocale, 4); }
  
  public NumberFormat getNumberInstance(Locale paramLocale) { return getInstance(paramLocale, 0); }
  
  public NumberFormat getPercentInstance(Locale paramLocale) { return getInstance(paramLocale, 2); }
  
  private NumberFormat getInstance(Locale paramLocale, int paramInt) {
    if (paramLocale == null)
      throw new NullPointerException(); 
    LocaleProviderAdapter localeProviderAdapter = LocaleProviderAdapter.forType(this.type);
    String[] arrayOfString = localeProviderAdapter.getLocaleResources(paramLocale).getNumberPatterns();
    DecimalFormatSymbols decimalFormatSymbols = DecimalFormatSymbols.getInstance(paramLocale);
    boolean bool = (paramInt == 4) ? 0 : paramInt;
    DecimalFormat decimalFormat = new DecimalFormat(arrayOfString[bool], decimalFormatSymbols);
    if (paramInt == 4) {
      decimalFormat.setMaximumFractionDigits(0);
      decimalFormat.setDecimalSeparatorAlwaysShown(false);
      decimalFormat.setParseIntegerOnly(true);
    } else if (paramInt == 1) {
      adjustForCurrencyDefaultFractionDigits(decimalFormat, decimalFormatSymbols);
    } 
    return decimalFormat;
  }
  
  private static void adjustForCurrencyDefaultFractionDigits(DecimalFormat paramDecimalFormat, DecimalFormatSymbols paramDecimalFormatSymbols) {
    Currency currency = paramDecimalFormatSymbols.getCurrency();
    if (currency == null)
      try {
        currency = Currency.getInstance(paramDecimalFormatSymbols.getInternationalCurrencySymbol());
      } catch (IllegalArgumentException illegalArgumentException) {} 
    if (currency != null) {
      int i = currency.getDefaultFractionDigits();
      if (i != -1) {
        int j = paramDecimalFormat.getMinimumFractionDigits();
        if (j == paramDecimalFormat.getMaximumFractionDigits()) {
          paramDecimalFormat.setMinimumFractionDigits(i);
          paramDecimalFormat.setMaximumFractionDigits(i);
        } else {
          paramDecimalFormat.setMinimumFractionDigits(Math.min(i, j));
          paramDecimalFormat.setMaximumFractionDigits(i);
        } 
      } 
    } 
  }
  
  public Set<String> getAvailableLanguageTags() { return this.langtags; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\su\\util\locale\provider\NumberFormatProviderImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */