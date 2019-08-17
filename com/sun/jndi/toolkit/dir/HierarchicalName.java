package com.sun.jndi.toolkit.dir;

import java.util.Enumeration;
import java.util.Locale;
import java.util.NoSuchElementException;
import java.util.Properties;
import javax.naming.CompoundName;
import javax.naming.InvalidNameException;
import javax.naming.Name;

final class HierarchicalName extends CompoundName {
  private int hashValue = -1;
  
  private static final long serialVersionUID = -6717336834584573168L;
  
  HierarchicalName() { super(new Enumeration<String>() {
          public boolean hasMoreElements() { return false; }
          
          public String nextElement() { throw new NoSuchElementException(); }
        },  HierarchicalNameParser.mySyntax); }
  
  HierarchicalName(Enumeration<String> paramEnumeration, Properties paramProperties) { super(paramEnumeration, paramProperties); }
  
  HierarchicalName(String paramString, Properties paramProperties) throws InvalidNameException { super(paramString, paramProperties); }
  
  public int hashCode() {
    if (this.hashValue == -1) {
      String str = toString().toUpperCase(Locale.ENGLISH);
      int i = str.length();
      byte b = 0;
      char[] arrayOfChar = new char[i];
      str.getChars(0, i, arrayOfChar, 0);
      for (int j = i; j > 0; j--)
        this.hashValue = this.hashValue * 37 + arrayOfChar[b++]; 
    } 
    return this.hashValue;
  }
  
  public Name getPrefix(int paramInt) {
    Enumeration enumeration = super.getPrefix(paramInt).getAll();
    return new HierarchicalName(enumeration, this.mySyntax);
  }
  
  public Name getSuffix(int paramInt) {
    Enumeration enumeration = super.getSuffix(paramInt).getAll();
    return new HierarchicalName(enumeration, this.mySyntax);
  }
  
  public Object clone() { return new HierarchicalName(getAll(), this.mySyntax); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\jndi\toolkit\dir\HierarchicalName.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */