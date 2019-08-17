package com.sun.jndi.cosnaming;

import java.util.Enumeration;
import java.util.Properties;
import java.util.Vector;
import javax.naming.CompositeName;
import javax.naming.CompoundName;
import javax.naming.InvalidNameException;
import javax.naming.Name;
import javax.naming.NameParser;
import javax.naming.NamingException;
import org.omg.CosNaming.NameComponent;

public final class CNNameParser implements NameParser {
  private static final Properties mySyntax = new Properties();
  
  private static final char kindSeparator = '.';
  
  private static final char compSeparator = '/';
  
  private static final char escapeChar = '\\';
  
  public Name parse(String paramString) throws NamingException {
    Vector vector = insStringToStringifiedComps(paramString);
    return new CNCompoundName(vector.elements());
  }
  
  static NameComponent[] nameToCosName(Name paramName) throws InvalidNameException {
    int i = paramName.size();
    if (i == 0)
      return new NameComponent[0]; 
    NameComponent[] arrayOfNameComponent = new NameComponent[i];
    for (byte b = 0; b < i; b++)
      arrayOfNameComponent[b] = parseComponent(paramName.get(b)); 
    return arrayOfNameComponent;
  }
  
  static String cosNameToInsString(NameComponent[] paramArrayOfNameComponent) {
    StringBuffer stringBuffer = new StringBuffer();
    for (byte b = 0; b < paramArrayOfNameComponent.length; b++) {
      if (b)
        stringBuffer.append('/'); 
      stringBuffer.append(stringifyComponent(paramArrayOfNameComponent[b]));
    } 
    return stringBuffer.toString();
  }
  
  static Name cosNameToName(NameComponent[] paramArrayOfNameComponent) {
    CompositeName compositeName = new CompositeName();
    for (byte b = 0; paramArrayOfNameComponent != null && b < paramArrayOfNameComponent.length; b++) {
      try {
        compositeName.add(stringifyComponent(paramArrayOfNameComponent[b]));
      } catch (InvalidNameException invalidNameException) {}
    } 
    return compositeName;
  }
  
  private static Vector<String> insStringToStringifiedComps(String paramString) throws InvalidNameException {
    int i = paramString.length();
    Vector vector = new Vector(10);
    char[] arrayOfChar1 = new char[i];
    char[] arrayOfChar2 = new char[i];
    byte b = 0;
    while (b < i) {
      byte b2 = 0;
      byte b1 = b2;
      boolean bool = true;
      while (b < i && paramString.charAt(b) != '/') {
        if (paramString.charAt(b) == '\\') {
          if (b + 1 >= i)
            throw new InvalidNameException(paramString + ": unescaped \\ at end of component"); 
          if (isMeta(paramString.charAt(b + 1))) {
            b++;
            if (bool) {
              arrayOfChar1[b1++] = paramString.charAt(b++);
              continue;
            } 
            arrayOfChar2[b2++] = paramString.charAt(b++);
            continue;
          } 
          throw new InvalidNameException(paramString + ": invalid character being escaped");
        } 
        if (bool && paramString.charAt(b) == '.') {
          b++;
          bool = false;
          continue;
        } 
        if (bool) {
          arrayOfChar1[b1++] = paramString.charAt(b++);
          continue;
        } 
        arrayOfChar2[b2++] = paramString.charAt(b++);
      } 
      vector.addElement(stringifyComponent(new NameComponent(new String(arrayOfChar1, 0, b1), new String(arrayOfChar2, 0, b2))));
      if (b < i)
        b++; 
    } 
    return vector;
  }
  
  private static NameComponent parseComponent(String paramString) throws InvalidNameException {
    NameComponent nameComponent = new NameComponent();
    byte b1 = -1;
    int i = paramString.length();
    byte b = 0;
    char[] arrayOfChar = new char[i];
    boolean bool = false;
    byte b2;
    for (b2 = 0; b2 < i && b1 < 0; b2++) {
      if (bool) {
        arrayOfChar[b++] = paramString.charAt(b2);
        bool = false;
      } else if (paramString.charAt(b2) == '\\') {
        if (b2 + 1 >= i)
          throw new InvalidNameException(paramString + ": unescaped \\ at end of component"); 
        if (isMeta(paramString.charAt(b2 + 1))) {
          bool = true;
        } else {
          throw new InvalidNameException(paramString + ": invalid character being escaped");
        } 
      } else if (paramString.charAt(b2) == '.') {
        b1 = b2;
      } else {
        arrayOfChar[b++] = paramString.charAt(b2);
      } 
    } 
    nameComponent.id = new String(arrayOfChar, 0, b);
    if (b1 < 0) {
      nameComponent.kind = "";
    } else {
      b = 0;
      bool = false;
      for (b2 = b1 + 1; b2 < i; b2++) {
        if (bool) {
          arrayOfChar[b++] = paramString.charAt(b2);
          bool = false;
        } else if (paramString.charAt(b2) == '\\') {
          if (b2 + 1 >= i)
            throw new InvalidNameException(paramString + ": unescaped \\ at end of component"); 
          if (isMeta(paramString.charAt(b2 + 1))) {
            bool = true;
          } else {
            throw new InvalidNameException(paramString + ": invalid character being escaped");
          } 
        } else {
          arrayOfChar[b++] = paramString.charAt(b2);
        } 
      } 
      nameComponent.kind = new String(arrayOfChar, 0, b);
    } 
    return nameComponent;
  }
  
  private static String stringifyComponent(NameComponent paramNameComponent) {
    StringBuffer stringBuffer = new StringBuffer(escape(paramNameComponent.id));
    if (paramNameComponent.kind != null && !paramNameComponent.kind.equals(""))
      stringBuffer.append('.' + escape(paramNameComponent.kind)); 
    return (stringBuffer.length() == 0) ? "." : stringBuffer.toString();
  }
  
  private static String escape(String paramString) {
    if (paramString.indexOf('.') < 0 && paramString.indexOf('/') < 0 && paramString.indexOf('\\') < 0)
      return paramString; 
    int i = paramString.length();
    byte b1 = 0;
    char[] arrayOfChar = new char[i + i];
    for (byte b2 = 0; b2 < i; b2++) {
      if (isMeta(paramString.charAt(b2)))
        arrayOfChar[b1++] = '\\'; 
      arrayOfChar[b1++] = paramString.charAt(b2);
    } 
    return new String(arrayOfChar, 0, b1);
  }
  
  private static boolean isMeta(char paramChar) {
    switch (paramChar) {
      case '.':
      case '/':
      case '\\':
        return true;
    } 
    return false;
  }
  
  static  {
    mySyntax.put("jndi.syntax.direction", "left_to_right");
    mySyntax.put("jndi.syntax.separator", "/");
    mySyntax.put("jndi.syntax.escape", "\\");
  }
  
  static final class CNCompoundName extends CompoundName {
    private static final long serialVersionUID = -6599252802678482317L;
    
    CNCompoundName(Enumeration<String> param1Enumeration) { super(param1Enumeration, mySyntax); }
    
    public Object clone() { return new CNCompoundName(getAll()); }
    
    public Name getPrefix(int param1Int) {
      Enumeration enumeration = super.getPrefix(param1Int).getAll();
      return new CNCompoundName(enumeration);
    }
    
    public Name getSuffix(int param1Int) {
      Enumeration enumeration = super.getSuffix(param1Int).getAll();
      return new CNCompoundName(enumeration);
    }
    
    public String toString() {
      try {
        return CNNameParser.cosNameToInsString(CNNameParser.nameToCosName(this));
      } catch (InvalidNameException invalidNameException) {
        return super.toString();
      } 
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\jndi\cosnaming\CNNameParser.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */