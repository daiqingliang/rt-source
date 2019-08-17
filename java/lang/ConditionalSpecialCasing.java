package java.lang;

import java.text.BreakIterator;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.Locale;
import sun.text.Normalizer;

final class ConditionalSpecialCasing {
  static final int FINAL_CASED = 1;
  
  static final int AFTER_SOFT_DOTTED = 2;
  
  static final int MORE_ABOVE = 3;
  
  static final int AFTER_I = 4;
  
  static final int NOT_BEFORE_DOT = 5;
  
  static final int COMBINING_CLASS_ABOVE = 230;
  
  static Entry[] entry = { 
      new Entry(931, new char[] { 'ς' }, new char[] { 'Σ' }, null, 1), new Entry(304, new char[] { 'i', '̇' }, new char[] { 'İ' }, null, 0), new Entry(775, new char[] { '̇' }, new char[0], "lt", 2), new Entry(73, new char[] { 'i', '̇' }, new char[] { 'I' }, "lt", 3), new Entry(74, new char[] { 'j', '̇' }, new char[] { 'J' }, "lt", 3), new Entry(302, new char[] { 'į', '̇' }, new char[] { 'Į' }, "lt", 3), new Entry(204, new char[] { 'i', '̇', '̀' }, new char[] { 'Ì' }, "lt", 0), new Entry(205, new char[] { 'i', '̇', '́' }, new char[] { 'Í' }, "lt", 0), new Entry(296, new char[] { 'i', '̇', '̃' }, new char[] { 'Ĩ' }, "lt", 0), new Entry(304, new char[] { 'i' }, new char[] { 'İ' }, "tr", 0), 
      new Entry(304, new char[] { 'i' }, new char[] { 'İ' }, "az", 0), new Entry(775, new char[0], new char[] { '̇' }, "tr", 4), new Entry(775, new char[0], new char[] { '̇' }, "az", 4), new Entry(73, new char[] { 'ı' }, new char[] { 'I' }, "tr", 5), new Entry(73, new char[] { 'ı' }, new char[] { 'I' }, "az", 5), new Entry(105, new char[] { 'i' }, new char[] { 'İ' }, "tr", 0), new Entry(105, new char[] { 'i' }, new char[] { 'İ' }, "az", 0) };
  
  static Hashtable<Integer, HashSet<Entry>> entryTable = new Hashtable();
  
  static int toLowerCaseEx(String paramString, int paramInt, Locale paramLocale) {
    char[] arrayOfChar = lookUpTable(paramString, paramInt, paramLocale, true);
    return (arrayOfChar != null) ? ((arrayOfChar.length == 1) ? arrayOfChar[0] : -1) : Character.toLowerCase(paramString.codePointAt(paramInt));
  }
  
  static int toUpperCaseEx(String paramString, int paramInt, Locale paramLocale) {
    char[] arrayOfChar = lookUpTable(paramString, paramInt, paramLocale, false);
    return (arrayOfChar != null) ? ((arrayOfChar.length == 1) ? arrayOfChar[0] : -1) : Character.toUpperCaseEx(paramString.codePointAt(paramInt));
  }
  
  static char[] toLowerCaseCharArray(String paramString, int paramInt, Locale paramLocale) { return lookUpTable(paramString, paramInt, paramLocale, true); }
  
  static char[] toUpperCaseCharArray(String paramString, int paramInt, Locale paramLocale) {
    char[] arrayOfChar = lookUpTable(paramString, paramInt, paramLocale, false);
    return (arrayOfChar != null) ? arrayOfChar : Character.toUpperCaseCharArray(paramString.codePointAt(paramInt));
  }
  
  private static char[] lookUpTable(String paramString, int paramInt, Locale paramLocale, boolean paramBoolean) {
    HashSet hashSet = (HashSet)entryTable.get(new Integer(paramString.codePointAt(paramInt)));
    char[] arrayOfChar = null;
    if (hashSet != null) {
      Iterator iterator = hashSet.iterator();
      String str = paramLocale.getLanguage();
      while (iterator.hasNext()) {
        Entry entry1 = (Entry)iterator.next();
        String str1 = entry1.getLanguage();
        if ((str1 == null || str1.equals(str)) && isConditionMet(paramString, paramInt, paramLocale, entry1.getCondition())) {
          arrayOfChar = paramBoolean ? entry1.getLowerCase() : entry1.getUpperCase();
          if (str1 != null)
            break; 
        } 
      } 
    } 
    return arrayOfChar;
  }
  
  private static boolean isConditionMet(String paramString, int paramInt1, Locale paramLocale, int paramInt2) {
    switch (paramInt2) {
      case 1:
        return isFinalCased(paramString, paramInt1, paramLocale);
      case 2:
        return isAfterSoftDotted(paramString, paramInt1);
      case 3:
        return isMoreAbove(paramString, paramInt1);
      case 4:
        return isAfterI(paramString, paramInt1);
      case 5:
        return !isBeforeDot(paramString, paramInt1);
    } 
    return true;
  }
  
  private static boolean isFinalCased(String paramString, int paramInt, Locale paramLocale) {
    BreakIterator breakIterator = BreakIterator.getWordInstance(paramLocale);
    breakIterator.setText(paramString);
    int i;
    for (i = paramInt; i >= 0 && !breakIterator.isBoundary(i); i -= Character.charCount(j)) {
      int j = paramString.codePointBefore(i);
      if (isCased(j)) {
        int k = paramString.length();
        for (i = paramInt + Character.charCount(paramString.codePointAt(paramInt)); i < k && !breakIterator.isBoundary(i); i += Character.charCount(j)) {
          j = paramString.codePointAt(i);
          if (isCased(j))
            return false; 
        } 
        return true;
      } 
    } 
    return false;
  }
  
  private static boolean isAfterI(String paramString, int paramInt) {
    int i;
    for (i = paramInt; i > 0; i -= Character.charCount(j)) {
      int j = paramString.codePointBefore(i);
      if (j == 73)
        return true; 
      int k = Normalizer.getCombiningClass(j);
      if (k == 0 || k == 230)
        return false; 
    } 
    return false;
  }
  
  private static boolean isAfterSoftDotted(String paramString, int paramInt) {
    int i;
    for (i = paramInt; i > 0; i -= Character.charCount(j)) {
      int j = paramString.codePointBefore(i);
      if (isSoftDotted(j))
        return true; 
      int k = Normalizer.getCombiningClass(j);
      if (k == 0 || k == 230)
        return false; 
    } 
    return false;
  }
  
  private static boolean isMoreAbove(String paramString, int paramInt) {
    int i = paramString.length();
    int j;
    for (j = paramInt + Character.charCount(paramString.codePointAt(paramInt)); j < i; j += Character.charCount(k)) {
      int k = paramString.codePointAt(j);
      int m = Normalizer.getCombiningClass(k);
      if (m == 230)
        return true; 
      if (m == 0)
        return false; 
    } 
    return false;
  }
  
  private static boolean isBeforeDot(String paramString, int paramInt) {
    int i = paramString.length();
    int j;
    for (j = paramInt + Character.charCount(paramString.codePointAt(paramInt)); j < i; j += Character.charCount(k)) {
      int k = paramString.codePointAt(j);
      if (k == 775)
        return true; 
      int m = Normalizer.getCombiningClass(k);
      if (m == 0 || m == 230)
        return false; 
    } 
    return false;
  }
  
  private static boolean isCased(int paramInt) {
    int i = Character.getType(paramInt);
    return (i == 2 || i == 1 || i == 3) ? true : ((paramInt >= 688 && paramInt <= 696) ? true : ((paramInt >= 704 && paramInt <= 705) ? true : ((paramInt >= 736 && paramInt <= 740) ? true : ((paramInt == 837) ? true : ((paramInt == 890) ? true : ((paramInt >= 7468 && paramInt <= 7521) ? true : ((paramInt >= 8544 && paramInt <= 8575) ? true : ((paramInt >= 9398 && paramInt <= 9449)))))))));
  }
  
  private static boolean isSoftDotted(int paramInt) {
    switch (paramInt) {
      case 105:
      case 106:
      case 303:
      case 616:
      case 1110:
      case 1112:
      case 7522:
      case 7725:
      case 7883:
      case 8305:
        return true;
    } 
    return false;
  }
  
  static  {
    for (byte b = 0; b < entry.length; b++) {
      Entry entry1 = entry[b];
      Integer integer = new Integer(entry1.getCodePoint());
      HashSet hashSet = (HashSet)entryTable.get(integer);
      if (hashSet == null)
        hashSet = new HashSet(); 
      hashSet.add(entry1);
      entryTable.put(integer, hashSet);
    } 
  }
  
  static class Entry {
    int ch;
    
    char[] lower;
    
    char[] upper;
    
    String lang;
    
    int condition;
    
    Entry(int param1Int1, char[] param1ArrayOfChar1, char[] param1ArrayOfChar2, String param1String, int param1Int2) {
      this.ch = param1Int1;
      this.lower = param1ArrayOfChar1;
      this.upper = param1ArrayOfChar2;
      this.lang = param1String;
      this.condition = param1Int2;
    }
    
    int getCodePoint() { return this.ch; }
    
    char[] getLowerCase() { return this.lower; }
    
    char[] getUpperCase() { return this.upper; }
    
    String getLanguage() { return this.lang; }
    
    int getCondition() { return this.condition; }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\lang\ConditionalSpecialCasing.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */