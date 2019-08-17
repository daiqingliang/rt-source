package sun.util.locale.provider;

import java.io.IOException;
import java.text.BreakIterator;
import java.text.spi.BreakIteratorProvider;
import java.util.Locale;
import java.util.Set;

public class BreakIteratorProviderImpl extends BreakIteratorProvider implements AvailableLanguageTags {
  private static final int CHARACTER_INDEX = 0;
  
  private static final int WORD_INDEX = 1;
  
  private static final int LINE_INDEX = 2;
  
  private static final int SENTENCE_INDEX = 3;
  
  private final LocaleProviderAdapter.Type type;
  
  private final Set<String> langtags;
  
  public BreakIteratorProviderImpl(LocaleProviderAdapter.Type paramType, Set<String> paramSet) {
    this.type = paramType;
    this.langtags = paramSet;
  }
  
  public Locale[] getAvailableLocales() { return LocaleProviderAdapter.toLocaleArray(this.langtags); }
  
  public BreakIterator getWordInstance(Locale paramLocale) { return getBreakInstance(paramLocale, 1, "WordData", "WordDictionary"); }
  
  public BreakIterator getLineInstance(Locale paramLocale) { return getBreakInstance(paramLocale, 2, "LineData", "LineDictionary"); }
  
  public BreakIterator getCharacterInstance(Locale paramLocale) { return getBreakInstance(paramLocale, 0, "CharacterData", "CharacterDictionary"); }
  
  public BreakIterator getSentenceInstance(Locale paramLocale) { return getBreakInstance(paramLocale, 3, "SentenceData", "SentenceDictionary"); }
  
  private BreakIterator getBreakInstance(Locale paramLocale, int paramInt, String paramString1, String paramString2) {
    if (paramLocale == null)
      throw new NullPointerException(); 
    LocaleResources localeResources = LocaleProviderAdapter.forJRE().getLocaleResources(paramLocale);
    String[] arrayOfString = (String[])localeResources.getBreakIteratorInfo("BreakIteratorClasses");
    String str = (String)localeResources.getBreakIteratorInfo(paramString1);
    try {
      String str1;
      switch (arrayOfString[paramInt]) {
        case "RuleBasedBreakIterator":
          return new RuleBasedBreakIterator(str);
        case "DictionaryBasedBreakIterator":
          str1 = (String)localeResources.getBreakIteratorInfo(paramString2);
          return new DictionaryBasedBreakIterator(str, str1);
      } 
      throw new IllegalArgumentException("Invalid break iterator class \"" + arrayOfString[paramInt] + "\"");
    } catch (IOException|java.util.MissingResourceException|IllegalArgumentException iOException) {
      throw new InternalError(iOException.toString(), iOException);
    } 
  }
  
  public Set<String> getAvailableLanguageTags() { return this.langtags; }
  
  public boolean isSupportedLocale(Locale paramLocale) { return LocaleProviderAdapter.isSupportedLocale(paramLocale, this.type, this.langtags); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\su\\util\locale\provider\BreakIteratorProviderImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */