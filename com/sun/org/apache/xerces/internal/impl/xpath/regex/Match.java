package com.sun.org.apache.xerces.internal.impl.xpath.regex;

import java.text.CharacterIterator;

public class Match implements Cloneable {
  int[] beginpos = null;
  
  int[] endpos = null;
  
  int nofgroups = 0;
  
  CharacterIterator ciSource = null;
  
  String strSource = null;
  
  char[] charSource = null;
  
  public Object clone() {
    Match match = new Match();
    if (this.nofgroups > 0) {
      match.setNumberOfGroups(this.nofgroups);
      if (this.ciSource != null)
        match.setSource(this.ciSource); 
      if (this.strSource != null)
        match.setSource(this.strSource); 
      for (byte b = 0; b < this.nofgroups; b++) {
        match.setBeginning(b, getBeginning(b));
        match.setEnd(b, getEnd(b));
      } 
    } 
    return match;
  }
  
  protected void setNumberOfGroups(int paramInt) {
    int i = this.nofgroups;
    this.nofgroups = paramInt;
    if (i <= 0 || i < paramInt || paramInt * 2 < i) {
      this.beginpos = new int[paramInt];
      this.endpos = new int[paramInt];
    } 
    for (byte b = 0; b < paramInt; b++) {
      this.beginpos[b] = -1;
      this.endpos[b] = -1;
    } 
  }
  
  protected void setSource(CharacterIterator paramCharacterIterator) {
    this.ciSource = paramCharacterIterator;
    this.strSource = null;
    this.charSource = null;
  }
  
  protected void setSource(String paramString) {
    this.ciSource = null;
    this.strSource = paramString;
    this.charSource = null;
  }
  
  protected void setSource(char[] paramArrayOfChar) {
    this.ciSource = null;
    this.strSource = null;
    this.charSource = paramArrayOfChar;
  }
  
  protected void setBeginning(int paramInt1, int paramInt2) { this.beginpos[paramInt1] = paramInt2; }
  
  protected void setEnd(int paramInt1, int paramInt2) { this.endpos[paramInt1] = paramInt2; }
  
  public int getNumberOfGroups() {
    if (this.nofgroups <= 0)
      throw new IllegalStateException("A result is not set."); 
    return this.nofgroups;
  }
  
  public int getBeginning(int paramInt) {
    if (this.beginpos == null)
      throw new IllegalStateException("A result is not set."); 
    if (paramInt < 0 || this.nofgroups <= paramInt)
      throw new IllegalArgumentException("The parameter must be less than " + this.nofgroups + ": " + paramInt); 
    return this.beginpos[paramInt];
  }
  
  public int getEnd(int paramInt) {
    if (this.endpos == null)
      throw new IllegalStateException("A result is not set."); 
    if (paramInt < 0 || this.nofgroups <= paramInt)
      throw new IllegalArgumentException("The parameter must be less than " + this.nofgroups + ": " + paramInt); 
    return this.endpos[paramInt];
  }
  
  public String getCapturedText(int paramInt) {
    String str;
    if (this.beginpos == null)
      throw new IllegalStateException("match() has never been called."); 
    if (paramInt < 0 || this.nofgroups <= paramInt)
      throw new IllegalArgumentException("The parameter must be less than " + this.nofgroups + ": " + paramInt); 
    int i = this.beginpos[paramInt];
    int j = this.endpos[paramInt];
    if (i < 0 || j < 0)
      return null; 
    if (this.ciSource != null) {
      str = REUtil.substring(this.ciSource, i, j);
    } else if (this.strSource != null) {
      str = this.strSource.substring(i, j);
    } else {
      str = new String(this.charSource, i, j - i);
    } 
    return str;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xerces\internal\impl\xpath\regex\Match.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */