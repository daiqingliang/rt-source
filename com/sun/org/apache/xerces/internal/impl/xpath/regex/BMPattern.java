package com.sun.org.apache.xerces.internal.impl.xpath.regex;

import java.text.CharacterIterator;

public class BMPattern {
  char[] pattern;
  
  int[] shiftTable;
  
  boolean ignoreCase;
  
  public BMPattern(String paramString, boolean paramBoolean) { this(paramString, 256, paramBoolean); }
  
  public BMPattern(String paramString, int paramInt, boolean paramBoolean) {
    this.pattern = paramString.toCharArray();
    this.shiftTable = new int[paramInt];
    this.ignoreCase = paramBoolean;
    int i = this.pattern.length;
    int j;
    for (j = 0; j < this.shiftTable.length; j++)
      this.shiftTable[j] = i; 
    for (j = 0; j < i; j++) {
      int k = this.pattern[j];
      int m = i - j - 1;
      char c = k % this.shiftTable.length;
      if (m < this.shiftTable[c])
        this.shiftTable[c] = m; 
      if (this.ignoreCase) {
        int i1 = Character.toUpperCase(k);
        c = i1 % this.shiftTable.length;
        if (m < this.shiftTable[c])
          this.shiftTable[c] = m; 
        int n = Character.toLowerCase(i1);
        c = n % this.shiftTable.length;
        if (m < this.shiftTable[c])
          this.shiftTable[c] = m; 
      } 
    } 
  }
  
  public int matches(CharacterIterator paramCharacterIterator, int paramInt1, int paramInt2) {
    if (this.ignoreCase)
      return matchesIgnoreCase(paramCharacterIterator, paramInt1, paramInt2); 
    int i = this.pattern.length;
    if (i == 0)
      return paramInt1; 
    int j = paramInt1 + i;
    while (j <= paramInt2) {
      int k = i;
      int m = j + 1;
      int n;
      while ((n = paramCharacterIterator.setIndex(--j)) == this.pattern[--k]) {
        if (k == 0)
          return j; 
        if (k <= 0)
          break; 
      } 
      j += this.shiftTable[n % this.shiftTable.length] + 1;
      if (j < m)
        j = m; 
    } 
    return -1;
  }
  
  public int matches(String paramString, int paramInt1, int paramInt2) {
    if (this.ignoreCase)
      return matchesIgnoreCase(paramString, paramInt1, paramInt2); 
    int i = this.pattern.length;
    if (i == 0)
      return paramInt1; 
    int j = paramInt1 + i;
    while (j <= paramInt2) {
      int k = i;
      int m = j + 1;
      int n;
      while ((n = paramString.charAt(--j)) == this.pattern[--k]) {
        if (k == 0)
          return j; 
        if (k <= 0)
          break; 
      } 
      j += this.shiftTable[n % this.shiftTable.length] + 1;
      if (j < m)
        j = m; 
    } 
    return -1;
  }
  
  public int matches(char[] paramArrayOfChar, int paramInt1, int paramInt2) {
    if (this.ignoreCase)
      return matchesIgnoreCase(paramArrayOfChar, paramInt1, paramInt2); 
    int i = this.pattern.length;
    if (i == 0)
      return paramInt1; 
    int j = paramInt1 + i;
    while (j <= paramInt2) {
      int k = i;
      int m = j + 1;
      int n;
      while ((n = paramArrayOfChar[--j]) == this.pattern[--k]) {
        if (k == 0)
          return j; 
        if (k <= 0)
          break; 
      } 
      j += this.shiftTable[n % this.shiftTable.length] + 1;
      if (j < m)
        j = m; 
    } 
    return -1;
  }
  
  int matchesIgnoreCase(CharacterIterator paramCharacterIterator, int paramInt1, int paramInt2) {
    int i = this.pattern.length;
    if (i == 0)
      return paramInt1; 
    int j = paramInt1 + i;
    while (j <= paramInt2) {
      int n;
      int k = i;
      int m = j + 1;
      do {
        char c1 = n = paramCharacterIterator.setIndex(--j);
        char c2 = this.pattern[--k];
        if (c1 != c2) {
          c1 = Character.toUpperCase(c1);
          c2 = Character.toUpperCase(c2);
          if (c1 != c2 && Character.toLowerCase(c1) != Character.toLowerCase(c2))
            break; 
        } 
        if (k == 0)
          return j; 
      } while (k > 0);
      j += this.shiftTable[n % this.shiftTable.length] + 1;
      if (j < m)
        j = m; 
    } 
    return -1;
  }
  
  int matchesIgnoreCase(String paramString, int paramInt1, int paramInt2) {
    int i = this.pattern.length;
    if (i == 0)
      return paramInt1; 
    int j = paramInt1 + i;
    while (j <= paramInt2) {
      int n;
      int k = i;
      int m = j + 1;
      do {
        char c1 = n = paramString.charAt(--j);
        char c2 = this.pattern[--k];
        if (c1 != c2) {
          c1 = Character.toUpperCase(c1);
          c2 = Character.toUpperCase(c2);
          if (c1 != c2 && Character.toLowerCase(c1) != Character.toLowerCase(c2))
            break; 
        } 
        if (k == 0)
          return j; 
      } while (k > 0);
      j += this.shiftTable[n % this.shiftTable.length] + 1;
      if (j < m)
        j = m; 
    } 
    return -1;
  }
  
  int matchesIgnoreCase(char[] paramArrayOfChar, int paramInt1, int paramInt2) {
    int i = this.pattern.length;
    if (i == 0)
      return paramInt1; 
    int j = paramInt1 + i;
    while (j <= paramInt2) {
      int n;
      int k = i;
      int m = j + 1;
      do {
        char c1 = n = paramArrayOfChar[--j];
        char c2 = this.pattern[--k];
        if (c1 != c2) {
          c1 = Character.toUpperCase(c1);
          c2 = Character.toUpperCase(c2);
          if (c1 != c2 && Character.toLowerCase(c1) != Character.toLowerCase(c2))
            break; 
        } 
        if (k == 0)
          return j; 
      } while (k > 0);
      j += this.shiftTable[n % this.shiftTable.length] + 1;
      if (j < m)
        j = m; 
    } 
    return -1;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xerces\internal\impl\xpath\regex\BMPattern.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */