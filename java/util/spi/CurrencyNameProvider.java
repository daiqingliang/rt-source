package java.util.spi;

import java.util.Locale;
import java.util.ResourceBundle;

public abstract class CurrencyNameProvider extends LocaleServiceProvider {
  public abstract String getSymbol(String paramString, Locale paramLocale);
  
  public String getDisplayName(String paramString, Locale paramLocale) {
    if (paramString == null || paramLocale == null)
      throw new NullPointerException(); 
    char[] arrayOfChar = paramString.toCharArray();
    if (arrayOfChar.length != 3)
      throw new IllegalArgumentException("The currencyCode is not in the form of three upper-case letters."); 
    for (char c : arrayOfChar) {
      if (c < 'A' || c > 'Z')
        throw new IllegalArgumentException("The currencyCode is not in the form of three upper-case letters."); 
    } 
    ResourceBundle.Control control = ResourceBundle.Control.getNoFallbackControl(ResourceBundle.Control.FORMAT_DEFAULT);
    for (Locale locale : getAvailableLocales()) {
      if (control.getCandidateLocales("", locale).contains(paramLocale))
        return null; 
    } 
    throw new IllegalArgumentException("The locale is not available");
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\jav\\util\spi\CurrencyNameProvider.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */