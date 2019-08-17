package com.sun.org.apache.xml.internal.utils;

import java.text.CollationElementIterator;
import java.text.Collator;
import java.text.RuleBasedCollator;
import java.util.Locale;

public class StringComparable implements Comparable {
  public static final int UNKNOWN_CASE = -1;
  
  public static final int UPPER_CASE = 1;
  
  public static final int LOWER_CASE = 2;
  
  private String m_text;
  
  private Locale m_locale;
  
  private RuleBasedCollator m_collator;
  
  private String m_caseOrder;
  
  private int m_mask = -1;
  
  public StringComparable(String paramString1, Locale paramLocale, Collator paramCollator, String paramString2) {
    this.m_text = paramString1;
    this.m_locale = paramLocale;
    this.m_collator = (RuleBasedCollator)paramCollator;
    this.m_caseOrder = paramString2;
    this.m_mask = getMask(this.m_collator.getStrength());
  }
  
  public static final Comparable getComparator(String paramString1, Locale paramLocale, Collator paramCollator, String paramString2) { return (paramString2 == null || paramString2.length() == 0) ? ((RuleBasedCollator)paramCollator).getCollationKey(paramString1) : new StringComparable(paramString1, paramLocale, paramCollator, paramString2); }
  
  public final String toString() { return this.m_text; }
  
  public int compareTo(Object paramObject) {
    String str = ((StringComparable)paramObject).toString();
    if (this.m_text.equals(str))
      return 0; 
    int i = this.m_collator.getStrength();
    int j = 0;
    if (i == 0 || i == 1) {
      j = this.m_collator.compare(this.m_text, str);
    } else {
      this.m_collator.setStrength(1);
      j = this.m_collator.compare(this.m_text, str);
      this.m_collator.setStrength(i);
    } 
    if (j != 0)
      return j; 
    j = getCaseDiff(this.m_text, str);
    return (j != 0) ? j : this.m_collator.compare(this.m_text, str);
  }
  
  private final int getCaseDiff(String paramString1, String paramString2) {
    int i = this.m_collator.getStrength();
    int j = this.m_collator.getDecomposition();
    this.m_collator.setStrength(2);
    this.m_collator.setDecomposition(1);
    int[] arrayOfInt = getFirstCaseDiff(paramString1, paramString2, this.m_locale);
    this.m_collator.setStrength(i);
    this.m_collator.setDecomposition(j);
    return (arrayOfInt != null) ? (this.m_caseOrder.equals("upper-first") ? ((arrayOfInt[0] == 1) ? -1 : 1) : ((arrayOfInt[0] == 2) ? -1 : 1)) : 0;
  }
  
  private final int[] getFirstCaseDiff(String paramString1, String paramString2, Locale paramLocale) {
    int[] arrayOfInt;
    CollationElementIterator collationElementIterator1 = this.m_collator.getCollationElementIterator(paramString1);
    CollationElementIterator collationElementIterator2 = this.m_collator.getCollationElementIterator(paramString2);
    int i = -1;
    int j = -1;
    int k = -1;
    int m = -1;
    int n = getElement(-1);
    int i1 = 0;
    int i2 = 0;
    boolean bool1 = true;
    boolean bool2 = true;
    while (true) {
      if (bool1) {
        k = collationElementIterator2.getOffset();
        i1 = getElement(collationElementIterator2.next());
        m = collationElementIterator2.getOffset();
      } 
      if (bool2) {
        i = collationElementIterator1.getOffset();
        i2 = getElement(collationElementIterator1.next());
        j = collationElementIterator1.getOffset();
      } 
      bool2 = bool1 = true;
      if (i1 == n || i2 == n)
        return null; 
      if (i2 == 0) {
        bool1 = false;
        continue;
      } 
      if (i1 == 0) {
        bool2 = false;
        continue;
      } 
      if (i2 != i1 && k < m && i < j) {
        String str1 = paramString1.substring(i, j);
        String str2 = paramString2.substring(k, m);
        String str3 = str1.toUpperCase(paramLocale);
        String str4 = str2.toUpperCase(paramLocale);
        if (this.m_collator.compare(str3, str4) != 0)
          continue; 
        arrayOfInt = new int[] { -1, -1 };
        if (this.m_collator.compare(str1, str3) == 0) {
          arrayOfInt[0] = 1;
        } else if (this.m_collator.compare(str1, str1.toLowerCase(paramLocale)) == 0) {
          arrayOfInt[0] = 2;
        } 
        if (this.m_collator.compare(str2, str4) == 0) {
          arrayOfInt[1] = 1;
        } else if (this.m_collator.compare(str2, str2.toLowerCase(paramLocale)) == 0) {
          arrayOfInt[1] = 2;
        } 
        if ((arrayOfInt[0] == 1 && arrayOfInt[1] == 2) || (arrayOfInt[0] == 2 && arrayOfInt[1] == 1))
          break; 
      } 
    } 
    return arrayOfInt;
  }
  
  private static final int getMask(int paramInt) {
    switch (paramInt) {
      case 0:
        return -65536;
      case 1:
        return -256;
    } 
    return -1;
  }
  
  private final int getElement(int paramInt) { return paramInt & this.m_mask; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xml\interna\\utils\StringComparable.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */