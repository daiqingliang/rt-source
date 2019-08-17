package java.text.spi;

import java.text.DecimalFormatSymbols;
import java.util.Locale;
import java.util.spi.LocaleServiceProvider;

public abstract class DecimalFormatSymbolsProvider extends LocaleServiceProvider {
  public abstract DecimalFormatSymbols getInstance(Locale paramLocale);
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\text\spi\DecimalFormatSymbolsProvider.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */