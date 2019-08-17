package java.text;

import java.lang.ref.SoftReference;
import java.text.spi.CollatorProvider;
import java.util.Comparator;
import java.util.Locale;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import sun.util.locale.provider.LocaleProviderAdapter;
import sun.util.locale.provider.LocaleServiceProviderPool;

public abstract class Collator extends Object implements Comparator<Object>, Cloneable {
  public static final int PRIMARY = 0;
  
  public static final int SECONDARY = 1;
  
  public static final int TERTIARY = 2;
  
  public static final int IDENTICAL = 3;
  
  public static final int NO_DECOMPOSITION = 0;
  
  public static final int CANONICAL_DECOMPOSITION = 1;
  
  public static final int FULL_DECOMPOSITION = 2;
  
  private int strength = 0;
  
  private int decmp = 0;
  
  private static final ConcurrentMap<Locale, SoftReference<Collator>> cache = new ConcurrentHashMap();
  
  static final int LESS = -1;
  
  static final int EQUAL = 0;
  
  static final int GREATER = 1;
  
  public static Collator getInstance() { return getInstance(Locale.getDefault()); }
  
  public static Collator getInstance(Locale paramLocale) {
    SoftReference softReference = (SoftReference)cache.get(paramLocale);
    Collator collator = (softReference != null) ? (Collator)softReference.get() : null;
    if (collator == null) {
      LocaleProviderAdapter localeProviderAdapter = LocaleProviderAdapter.getAdapter(CollatorProvider.class, paramLocale);
      CollatorProvider collatorProvider = localeProviderAdapter.getCollatorProvider();
      collator = collatorProvider.getInstance(paramLocale);
      if (collator == null)
        collator = LocaleProviderAdapter.forJRE().getCollatorProvider().getInstance(paramLocale); 
      while (true) {
        if (softReference != null)
          cache.remove(paramLocale, softReference); 
        softReference = (SoftReference)cache.putIfAbsent(paramLocale, new SoftReference(collator));
        if (softReference == null)
          break; 
        Collator collator1 = (Collator)softReference.get();
        if (collator1 != null) {
          collator = collator1;
          break;
        } 
      } 
    } 
    return (Collator)collator.clone();
  }
  
  public abstract int compare(String paramString1, String paramString2);
  
  public int compare(Object paramObject1, Object paramObject2) { return compare((String)paramObject1, (String)paramObject2); }
  
  public abstract CollationKey getCollationKey(String paramString);
  
  public boolean equals(String paramString1, String paramString2) { return (compare(paramString1, paramString2) == 0); }
  
  public int getStrength() { return this.strength; }
  
  public void setStrength(int paramInt) {
    if (paramInt != 0 && paramInt != 1 && paramInt != 2 && paramInt != 3)
      throw new IllegalArgumentException("Incorrect comparison level."); 
    this.strength = paramInt;
  }
  
  public int getDecomposition() { return this.decmp; }
  
  public void setDecomposition(int paramInt) {
    if (paramInt != 0 && paramInt != 1 && paramInt != 2)
      throw new IllegalArgumentException("Wrong decomposition mode."); 
    this.decmp = paramInt;
  }
  
  public static Locale[] getAvailableLocales() {
    LocaleServiceProviderPool localeServiceProviderPool = LocaleServiceProviderPool.getPool(CollatorProvider.class);
    return localeServiceProviderPool.getAvailableLocales();
  }
  
  public Object clone() {
    try {
      return (Collator)super.clone();
    } catch (CloneNotSupportedException cloneNotSupportedException) {
      throw new InternalError(cloneNotSupportedException);
    } 
  }
  
  public boolean equals(Object paramObject) {
    if (this == paramObject)
      return true; 
    if (paramObject == null)
      return false; 
    if (getClass() != paramObject.getClass())
      return false; 
    Collator collator = (Collator)paramObject;
    return (this.strength == collator.strength && this.decmp == collator.decmp);
  }
  
  public abstract int hashCode();
  
  protected Collator() {
    this.strength = 2;
    this.decmp = 1;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\text\Collator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */