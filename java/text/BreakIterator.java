package java.text;

import java.lang.ref.SoftReference;
import java.text.spi.BreakIteratorProvider;
import java.util.Locale;
import sun.util.locale.provider.LocaleProviderAdapter;
import sun.util.locale.provider.LocaleServiceProviderPool;

public abstract class BreakIterator implements Cloneable {
  public static final int DONE = -1;
  
  private static final int CHARACTER_INDEX = 0;
  
  private static final int WORD_INDEX = 1;
  
  private static final int LINE_INDEX = 2;
  
  private static final int SENTENCE_INDEX = 3;
  
  private static final SoftReference<BreakIteratorCache>[] iterCache = (SoftReference[])new SoftReference[4];
  
  public Object clone() {
    try {
      return super.clone();
    } catch (CloneNotSupportedException cloneNotSupportedException) {
      throw new InternalError(cloneNotSupportedException);
    } 
  }
  
  public abstract int first();
  
  public abstract int last();
  
  public abstract int next(int paramInt);
  
  public abstract int next();
  
  public abstract int previous();
  
  public abstract int following(int paramInt);
  
  public int preceding(int paramInt) {
    int i;
    for (i = following(paramInt); i >= paramInt && i != -1; i = previous());
    return i;
  }
  
  public boolean isBoundary(int paramInt) {
    if (paramInt == 0)
      return true; 
    int i = following(paramInt - 1);
    if (i == -1)
      throw new IllegalArgumentException(); 
    return (i == paramInt);
  }
  
  public abstract int current();
  
  public abstract CharacterIterator getText();
  
  public void setText(String paramString) { setText(new StringCharacterIterator(paramString)); }
  
  public abstract void setText(CharacterIterator paramCharacterIterator);
  
  public static BreakIterator getWordInstance() { return getWordInstance(Locale.getDefault()); }
  
  public static BreakIterator getWordInstance(Locale paramLocale) { return getBreakInstance(paramLocale, 1); }
  
  public static BreakIterator getLineInstance() { return getLineInstance(Locale.getDefault()); }
  
  public static BreakIterator getLineInstance(Locale paramLocale) { return getBreakInstance(paramLocale, 2); }
  
  public static BreakIterator getCharacterInstance() { return getCharacterInstance(Locale.getDefault()); }
  
  public static BreakIterator getCharacterInstance(Locale paramLocale) { return getBreakInstance(paramLocale, 0); }
  
  public static BreakIterator getSentenceInstance() { return getSentenceInstance(Locale.getDefault()); }
  
  public static BreakIterator getSentenceInstance(Locale paramLocale) { return getBreakInstance(paramLocale, 3); }
  
  private static BreakIterator getBreakInstance(Locale paramLocale, int paramInt) {
    if (iterCache[paramInt] != null) {
      BreakIteratorCache breakIteratorCache1 = (BreakIteratorCache)iterCache[paramInt].get();
      if (breakIteratorCache1 != null && breakIteratorCache1.getLocale().equals(paramLocale))
        return breakIteratorCache1.createBreakInstance(); 
    } 
    BreakIterator breakIterator = createBreakInstance(paramLocale, paramInt);
    BreakIteratorCache breakIteratorCache = new BreakIteratorCache(paramLocale, breakIterator);
    iterCache[paramInt] = new SoftReference(breakIteratorCache);
    return breakIterator;
  }
  
  private static BreakIterator createBreakInstance(Locale paramLocale, int paramInt) {
    LocaleProviderAdapter localeProviderAdapter = LocaleProviderAdapter.getAdapter(BreakIteratorProvider.class, paramLocale);
    BreakIterator breakIterator = createBreakInstance(localeProviderAdapter, paramLocale, paramInt);
    if (breakIterator == null)
      breakIterator = createBreakInstance(LocaleProviderAdapter.forJRE(), paramLocale, paramInt); 
    return breakIterator;
  }
  
  private static BreakIterator createBreakInstance(LocaleProviderAdapter paramLocaleProviderAdapter, Locale paramLocale, int paramInt) {
    BreakIteratorProvider breakIteratorProvider = paramLocaleProviderAdapter.getBreakIteratorProvider();
    BreakIterator breakIterator = null;
    switch (paramInt) {
      case 0:
        breakIterator = breakIteratorProvider.getCharacterInstance(paramLocale);
        break;
      case 1:
        breakIterator = breakIteratorProvider.getWordInstance(paramLocale);
        break;
      case 2:
        breakIterator = breakIteratorProvider.getLineInstance(paramLocale);
        break;
      case 3:
        breakIterator = breakIteratorProvider.getSentenceInstance(paramLocale);
        break;
    } 
    return breakIterator;
  }
  
  public static Locale[] getAvailableLocales() {
    LocaleServiceProviderPool localeServiceProviderPool = LocaleServiceProviderPool.getPool(BreakIteratorProvider.class);
    return localeServiceProviderPool.getAvailableLocales();
  }
  
  private static final class BreakIteratorCache {
    private BreakIterator iter;
    
    private Locale locale;
    
    BreakIteratorCache(Locale param1Locale, BreakIterator param1BreakIterator) {
      this.locale = param1Locale;
      this.iter = (BreakIterator)param1BreakIterator.clone();
    }
    
    Locale getLocale() { return this.locale; }
    
    BreakIterator createBreakInstance() { return (BreakIterator)this.iter.clone(); }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\text\BreakIterator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */