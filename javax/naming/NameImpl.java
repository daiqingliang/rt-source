package javax.naming;

import java.util.Enumeration;
import java.util.Locale;
import java.util.NoSuchElementException;
import java.util.Properties;
import java.util.Vector;

class NameImpl {
  private static final byte LEFT_TO_RIGHT = 1;
  
  private static final byte RIGHT_TO_LEFT = 2;
  
  private static final byte FLAT = 0;
  
  private Vector<String> components;
  
  private byte syntaxDirection = 1;
  
  private String syntaxSeparator = "/";
  
  private String syntaxSeparator2 = null;
  
  private boolean syntaxCaseInsensitive = false;
  
  private boolean syntaxTrimBlanks = false;
  
  private String syntaxEscape = "\\";
  
  private String syntaxBeginQuote1 = "\"";
  
  private String syntaxEndQuote1 = "\"";
  
  private String syntaxBeginQuote2 = "'";
  
  private String syntaxEndQuote2 = "'";
  
  private String syntaxAvaSeparator = null;
  
  private String syntaxTypevalSeparator = null;
  
  private static final int STYLE_NONE = 0;
  
  private static final int STYLE_QUOTE1 = 1;
  
  private static final int STYLE_QUOTE2 = 2;
  
  private static final int STYLE_ESCAPE = 3;
  
  private int escapingStyle = 0;
  
  private final boolean isA(String paramString1, int paramInt, String paramString2) { return (paramString2 != null && paramString1.startsWith(paramString2, paramInt)); }
  
  private final boolean isMeta(String paramString, int paramInt) { return (isA(paramString, paramInt, this.syntaxEscape) || isA(paramString, paramInt, this.syntaxBeginQuote1) || isA(paramString, paramInt, this.syntaxBeginQuote2) || isSeparator(paramString, paramInt)); }
  
  private final boolean isSeparator(String paramString, int paramInt) { return (isA(paramString, paramInt, this.syntaxSeparator) || isA(paramString, paramInt, this.syntaxSeparator2)); }
  
  private final int skipSeparator(String paramString, int paramInt) {
    if (isA(paramString, paramInt, this.syntaxSeparator)) {
      paramInt += this.syntaxSeparator.length();
    } else if (isA(paramString, paramInt, this.syntaxSeparator2)) {
      paramInt += this.syntaxSeparator2.length();
    } 
    return paramInt;
  }
  
  private final int extractComp(String paramString, int paramInt1, int paramInt2, Vector<String> paramVector) throws InvalidNameException {
    boolean bool = true;
    boolean bool1 = false;
    StringBuffer stringBuffer = new StringBuffer(paramInt2);
    while (paramInt1 < paramInt2) {
      if (bool && ((bool1 = isA(paramString, paramInt1, this.syntaxBeginQuote1)) || isA(paramString, paramInt1, this.syntaxBeginQuote2))) {
        String str1 = bool1 ? this.syntaxBeginQuote1 : this.syntaxBeginQuote2;
        String str2 = bool1 ? this.syntaxEndQuote1 : this.syntaxEndQuote2;
        if (this.escapingStyle == 0)
          this.escapingStyle = bool1 ? 1 : 2; 
        for (paramInt1 += str1.length(); paramInt1 < paramInt2 && !paramString.startsWith(str2, paramInt1); paramInt1++) {
          if (isA(paramString, paramInt1, this.syntaxEscape) && isA(paramString, paramInt1 + this.syntaxEscape.length(), str2))
            paramInt1 += this.syntaxEscape.length(); 
          stringBuffer.append(paramString.charAt(paramInt1));
        } 
        if (paramInt1 >= paramInt2)
          throw new InvalidNameException(paramString + ": no close quote"); 
        paramInt1 += str2.length();
        if (paramInt1 == paramInt2 || isSeparator(paramString, paramInt1))
          break; 
        throw new InvalidNameException(paramString + ": close quote appears before end of component");
      } 
      if (isSeparator(paramString, paramInt1))
        break; 
      if (isA(paramString, paramInt1, this.syntaxEscape)) {
        if (isMeta(paramString, paramInt1 + this.syntaxEscape.length())) {
          paramInt1 += this.syntaxEscape.length();
          if (this.escapingStyle == 0)
            this.escapingStyle = 3; 
        } else if (paramInt1 + this.syntaxEscape.length() >= paramInt2) {
          throw new InvalidNameException(paramString + ": unescaped " + this.syntaxEscape + " at end of component");
        } 
      } else if (isA(paramString, paramInt1, this.syntaxTypevalSeparator) && ((bool1 = isA(paramString, paramInt1 + this.syntaxTypevalSeparator.length(), this.syntaxBeginQuote1)) || isA(paramString, paramInt1 + this.syntaxTypevalSeparator.length(), this.syntaxBeginQuote2))) {
        String str1 = bool1 ? this.syntaxBeginQuote1 : this.syntaxBeginQuote2;
        String str2 = bool1 ? this.syntaxEndQuote1 : this.syntaxEndQuote2;
        paramInt1 += this.syntaxTypevalSeparator.length();
        stringBuffer.append(this.syntaxTypevalSeparator + str1);
        for (paramInt1 += str1.length(); paramInt1 < paramInt2 && !paramString.startsWith(str2, paramInt1); paramInt1++) {
          if (isA(paramString, paramInt1, this.syntaxEscape) && isA(paramString, paramInt1 + this.syntaxEscape.length(), str2))
            paramInt1 += this.syntaxEscape.length(); 
          stringBuffer.append(paramString.charAt(paramInt1));
        } 
        if (paramInt1 >= paramInt2)
          throw new InvalidNameException(paramString + ": typeval no close quote"); 
        paramInt1 += str2.length();
        stringBuffer.append(str2);
        if (paramInt1 == paramInt2 || isSeparator(paramString, paramInt1))
          break; 
        throw new InvalidNameException(paramString.substring(paramInt1) + ": typeval close quote appears before end of component");
      } 
      stringBuffer.append(paramString.charAt(paramInt1++));
      bool = false;
    } 
    if (this.syntaxDirection == 2) {
      paramVector.insertElementAt(stringBuffer.toString(), 0);
    } else {
      paramVector.addElement(stringBuffer.toString());
    } 
    return paramInt1;
  }
  
  private static boolean getBoolean(Properties paramProperties, String paramString) { return toBoolean(paramProperties.getProperty(paramString)); }
  
  private static boolean toBoolean(String paramString) { return (paramString != null && paramString.toLowerCase(Locale.ENGLISH).equals("true")); }
  
  private final void recordNamingConvention(Properties paramProperties) {
    String str = paramProperties.getProperty("jndi.syntax.direction", "flat");
    if (str.equals("left_to_right")) {
      this.syntaxDirection = 1;
    } else if (str.equals("right_to_left")) {
      this.syntaxDirection = 2;
    } else if (str.equals("flat")) {
      this.syntaxDirection = 0;
    } else {
      throw new IllegalArgumentException(str + "is not a valid value for the jndi.syntax.direction property");
    } 
    if (this.syntaxDirection != 0) {
      this.syntaxSeparator = paramProperties.getProperty("jndi.syntax.separator");
      this.syntaxSeparator2 = paramProperties.getProperty("jndi.syntax.separator2");
      if (this.syntaxSeparator == null)
        throw new IllegalArgumentException("jndi.syntax.separator property required for non-flat syntax"); 
    } else {
      this.syntaxSeparator = null;
    } 
    this.syntaxEscape = paramProperties.getProperty("jndi.syntax.escape");
    this.syntaxCaseInsensitive = getBoolean(paramProperties, "jndi.syntax.ignorecase");
    this.syntaxTrimBlanks = getBoolean(paramProperties, "jndi.syntax.trimblanks");
    this.syntaxBeginQuote1 = paramProperties.getProperty("jndi.syntax.beginquote");
    this.syntaxEndQuote1 = paramProperties.getProperty("jndi.syntax.endquote");
    if (this.syntaxEndQuote1 == null && this.syntaxBeginQuote1 != null) {
      this.syntaxEndQuote1 = this.syntaxBeginQuote1;
    } else if (this.syntaxBeginQuote1 == null && this.syntaxEndQuote1 != null) {
      this.syntaxBeginQuote1 = this.syntaxEndQuote1;
    } 
    this.syntaxBeginQuote2 = paramProperties.getProperty("jndi.syntax.beginquote2");
    this.syntaxEndQuote2 = paramProperties.getProperty("jndi.syntax.endquote2");
    if (this.syntaxEndQuote2 == null && this.syntaxBeginQuote2 != null) {
      this.syntaxEndQuote2 = this.syntaxBeginQuote2;
    } else if (this.syntaxBeginQuote2 == null && this.syntaxEndQuote2 != null) {
      this.syntaxBeginQuote2 = this.syntaxEndQuote2;
    } 
    this.syntaxAvaSeparator = paramProperties.getProperty("jndi.syntax.separator.ava");
    this.syntaxTypevalSeparator = paramProperties.getProperty("jndi.syntax.separator.typeval");
  }
  
  NameImpl(Properties paramProperties) {
    if (paramProperties != null)
      recordNamingConvention(paramProperties); 
    this.components = new Vector();
  }
  
  NameImpl(Properties paramProperties, String paramString) throws InvalidNameException {
    this(paramProperties);
    boolean bool1 = (this.syntaxDirection == 2) ? 1 : 0;
    boolean bool2 = true;
    int i = paramString.length();
    int j = 0;
    while (j < i) {
      j = extractComp(paramString, j, i, this.components);
      String str = bool1 ? (String)this.components.firstElement() : (String)this.components.lastElement();
      if (str.length() >= 1)
        bool2 = false; 
      if (j < i) {
        j = skipSeparator(paramString, j);
        if (j == i && !bool2) {
          if (bool1) {
            this.components.insertElementAt("", 0);
            continue;
          } 
          this.components.addElement("");
        } 
      } 
    } 
  }
  
  NameImpl(Properties paramProperties, Enumeration<String> paramEnumeration) {
    this(paramProperties);
    while (paramEnumeration.hasMoreElements())
      this.components.addElement(paramEnumeration.nextElement()); 
  }
  
  private final String stringifyComp(String paramString) {
    int i = paramString.length();
    boolean bool1 = false;
    boolean bool2 = false;
    String str1 = null;
    String str2 = null;
    StringBuffer stringBuffer = new StringBuffer(i);
    if (this.syntaxSeparator != null && paramString.indexOf(this.syntaxSeparator) >= 0)
      if (this.syntaxBeginQuote1 != null) {
        str1 = this.syntaxBeginQuote1;
        str2 = this.syntaxEndQuote1;
      } else if (this.syntaxBeginQuote2 != null) {
        str1 = this.syntaxBeginQuote2;
        str2 = this.syntaxEndQuote2;
      } else if (this.syntaxEscape != null) {
        bool1 = true;
      }  
    if (this.syntaxSeparator2 != null && paramString.indexOf(this.syntaxSeparator2) >= 0)
      if (this.syntaxBeginQuote1 != null) {
        if (str1 == null) {
          str1 = this.syntaxBeginQuote1;
          str2 = this.syntaxEndQuote1;
        } 
      } else if (this.syntaxBeginQuote2 != null) {
        if (str1 == null) {
          str1 = this.syntaxBeginQuote2;
          str2 = this.syntaxEndQuote2;
        } 
      } else if (this.syntaxEscape != null) {
        bool2 = true;
      }  
    if (str1 != null) {
      stringBuffer = stringBuffer.append(str1);
      int j = 0;
      while (j < i) {
        if (paramString.startsWith(str2, j)) {
          stringBuffer.append(this.syntaxEscape).append(str2);
          j += str2.length();
          continue;
        } 
        stringBuffer.append(paramString.charAt(j++));
      } 
      stringBuffer.append(str2);
    } else {
      boolean bool = true;
      int j = 0;
      while (j < i) {
        if (bool && isA(paramString, j, this.syntaxBeginQuote1)) {
          stringBuffer.append(this.syntaxEscape).append(this.syntaxBeginQuote1);
          j += this.syntaxBeginQuote1.length();
        } else if (bool && isA(paramString, j, this.syntaxBeginQuote2)) {
          stringBuffer.append(this.syntaxEscape).append(this.syntaxBeginQuote2);
          j += this.syntaxBeginQuote2.length();
        } else if (isA(paramString, j, this.syntaxEscape)) {
          if (j + this.syntaxEscape.length() >= i) {
            stringBuffer.append(this.syntaxEscape);
          } else if (isMeta(paramString, j + this.syntaxEscape.length())) {
            stringBuffer.append(this.syntaxEscape);
          } 
          stringBuffer.append(this.syntaxEscape);
          j += this.syntaxEscape.length();
        } else if (bool1 && paramString.startsWith(this.syntaxSeparator, j)) {
          stringBuffer.append(this.syntaxEscape).append(this.syntaxSeparator);
          j += this.syntaxSeparator.length();
        } else if (bool2 && paramString.startsWith(this.syntaxSeparator2, j)) {
          stringBuffer.append(this.syntaxEscape).append(this.syntaxSeparator2);
          j += this.syntaxSeparator2.length();
        } else {
          stringBuffer.append(paramString.charAt(j++));
        } 
        bool = false;
      } 
    } 
    return stringBuffer.toString();
  }
  
  public String toString() {
    StringBuffer stringBuffer = new StringBuffer();
    boolean bool = true;
    int i = this.components.size();
    for (int j = 0; j < i; j++) {
      String str;
      if (this.syntaxDirection == 2) {
        str = stringifyComp((String)this.components.elementAt(i - 1 - j));
      } else {
        str = stringifyComp((String)this.components.elementAt(j));
      } 
      if (j != 0 && this.syntaxSeparator != null)
        stringBuffer.append(this.syntaxSeparator); 
      if (str.length() >= 1)
        bool = false; 
      stringBuffer = stringBuffer.append(str);
    } 
    if (bool && i >= 1 && this.syntaxSeparator != null)
      stringBuffer = stringBuffer.append(this.syntaxSeparator); 
    return stringBuffer.toString();
  }
  
  public boolean equals(Object paramObject) {
    if (paramObject != null && paramObject instanceof NameImpl) {
      NameImpl nameImpl = (NameImpl)paramObject;
      if (nameImpl.size() == size()) {
        Enumeration enumeration1 = getAll();
        Enumeration enumeration2 = nameImpl.getAll();
        while (enumeration1.hasMoreElements()) {
          String str1 = (String)enumeration1.nextElement();
          String str2 = (String)enumeration2.nextElement();
          if (this.syntaxTrimBlanks) {
            str1 = str1.trim();
            str2 = str2.trim();
          } 
          if (this.syntaxCaseInsensitive) {
            if (!str1.equalsIgnoreCase(str2))
              return false; 
            continue;
          } 
          if (!str1.equals(str2))
            return false; 
        } 
        return true;
      } 
    } 
    return false;
  }
  
  public int compareTo(NameImpl paramNameImpl) {
    if (this == paramNameImpl)
      return 0; 
    int i = size();
    int j = paramNameImpl.size();
    int k = Math.min(i, j);
    byte b1 = 0;
    byte b2 = 0;
    while (k-- != 0) {
      int m;
      String str1 = get(b1++);
      String str2 = paramNameImpl.get(b2++);
      if (this.syntaxTrimBlanks) {
        str1 = str1.trim();
        str2 = str2.trim();
      } 
      if (this.syntaxCaseInsensitive) {
        m = str1.compareToIgnoreCase(str2);
      } else {
        m = str1.compareTo(str2);
      } 
      if (m != 0)
        return m; 
    } 
    return i - j;
  }
  
  public int size() { return this.components.size(); }
  
  public Enumeration<String> getAll() { return this.components.elements(); }
  
  public String get(int paramInt) { return (String)this.components.elementAt(paramInt); }
  
  public Enumeration<String> getPrefix(int paramInt) {
    if (paramInt < 0 || paramInt > size())
      throw new ArrayIndexOutOfBoundsException(paramInt); 
    return new NameImplEnumerator(this.components, 0, paramInt);
  }
  
  public Enumeration<String> getSuffix(int paramInt) {
    int i = size();
    if (paramInt < 0 || paramInt > i)
      throw new ArrayIndexOutOfBoundsException(paramInt); 
    return new NameImplEnumerator(this.components, paramInt, i);
  }
  
  public boolean isEmpty() { return this.components.isEmpty(); }
  
  public boolean startsWith(int paramInt, Enumeration<String> paramEnumeration) {
    if (paramInt < 0 || paramInt > size())
      return false; 
    try {
      Enumeration enumeration = getPrefix(paramInt);
      while (enumeration.hasMoreElements()) {
        String str1 = (String)enumeration.nextElement();
        String str2 = (String)paramEnumeration.nextElement();
        if (this.syntaxTrimBlanks) {
          str1 = str1.trim();
          str2 = str2.trim();
        } 
        if (this.syntaxCaseInsensitive) {
          if (!str1.equalsIgnoreCase(str2))
            return false; 
          continue;
        } 
        if (!str1.equals(str2))
          return false; 
      } 
    } catch (NoSuchElementException noSuchElementException) {
      return false;
    } 
    return true;
  }
  
  public boolean endsWith(int paramInt, Enumeration<String> paramEnumeration) {
    int i = size() - paramInt;
    if (i < 0 || i > size())
      return false; 
    try {
      Enumeration enumeration = getSuffix(i);
      while (enumeration.hasMoreElements()) {
        String str1 = (String)enumeration.nextElement();
        String str2 = (String)paramEnumeration.nextElement();
        if (this.syntaxTrimBlanks) {
          str1 = str1.trim();
          str2 = str2.trim();
        } 
        if (this.syntaxCaseInsensitive) {
          if (!str1.equalsIgnoreCase(str2))
            return false; 
          continue;
        } 
        if (!str1.equals(str2))
          return false; 
      } 
    } catch (NoSuchElementException noSuchElementException) {
      return false;
    } 
    return true;
  }
  
  public boolean addAll(Enumeration<String> paramEnumeration) throws InvalidNameException {
    boolean bool = false;
    while (paramEnumeration.hasMoreElements()) {
      try {
        String str = (String)paramEnumeration.nextElement();
        if (size() > 0 && this.syntaxDirection == 0)
          throw new InvalidNameException("A flat name can only have a single component"); 
        this.components.addElement(str);
        bool = true;
      } catch (NoSuchElementException noSuchElementException) {
        break;
      } 
    } 
    return bool;
  }
  
  public boolean addAll(int paramInt, Enumeration<String> paramEnumeration) {
    boolean bool = false;
    for (int i = paramInt; paramEnumeration.hasMoreElements(); i++) {
      try {
        String str = (String)paramEnumeration.nextElement();
        if (size() > 0 && this.syntaxDirection == 0)
          throw new InvalidNameException("A flat name can only have a single component"); 
        this.components.insertElementAt(str, i);
        bool = true;
      } catch (NoSuchElementException noSuchElementException) {
        break;
      } 
    } 
    return bool;
  }
  
  public void add(String paramString) throws InvalidNameException {
    if (size() > 0 && this.syntaxDirection == 0)
      throw new InvalidNameException("A flat name can only have a single component"); 
    this.components.addElement(paramString);
  }
  
  public void add(int paramInt, String paramString) throws InvalidNameException {
    if (size() > 0 && this.syntaxDirection == 0)
      throw new InvalidNameException("A flat name can only zero or one component"); 
    this.components.insertElementAt(paramString, paramInt);
  }
  
  public Object remove(int paramInt) {
    Object object = this.components.elementAt(paramInt);
    this.components.removeElementAt(paramInt);
    return object;
  }
  
  public int hashCode() {
    int i = 0;
    Enumeration enumeration = getAll();
    while (enumeration.hasMoreElements()) {
      String str = (String)enumeration.nextElement();
      if (this.syntaxTrimBlanks)
        str = str.trim(); 
      if (this.syntaxCaseInsensitive)
        str = str.toLowerCase(Locale.ENGLISH); 
      i += str.hashCode();
    } 
    return i;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\naming\NameImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */