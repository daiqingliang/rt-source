package com.sun.jndi.ldap;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;
import java.io.UnsupportedEncodingException;
import java.util.Enumeration;
import java.util.Locale;
import java.util.Vector;
import javax.naming.InvalidNameException;
import javax.naming.Name;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttributes;

public final class LdapName implements Name {
  private String unparsed;
  
  private Vector<Rdn> rdns;
  
  private boolean valuesCaseSensitive = false;
  
  static final long serialVersionUID = -1595520034788997356L;
  
  public LdapName(String paramString) throws InvalidNameException {
    this.unparsed = paramString;
    parse();
  }
  
  private LdapName(String paramString, Vector<Rdn> paramVector) {
    this.unparsed = paramString;
    this.rdns = (Vector)paramVector.clone();
  }
  
  private LdapName(String paramString, Vector<Rdn> paramVector, int paramInt1, int paramInt2) {
    this.unparsed = paramString;
    this.rdns = new Vector();
    for (int i = paramInt1; i < paramInt2; i++)
      this.rdns.addElement(paramVector.elementAt(i)); 
  }
  
  public Object clone() { return new LdapName(this.unparsed, this.rdns); }
  
  public String toString() {
    if (this.unparsed != null)
      return this.unparsed; 
    StringBuffer stringBuffer = new StringBuffer();
    for (int i = this.rdns.size() - 1; i >= 0; i--) {
      if (i < this.rdns.size() - 1)
        stringBuffer.append(','); 
      Rdn rdn = (Rdn)this.rdns.elementAt(i);
      stringBuffer.append(rdn);
    } 
    this.unparsed = new String(stringBuffer);
    return this.unparsed;
  }
  
  public boolean equals(Object paramObject) { return (paramObject instanceof LdapName && compareTo(paramObject) == 0); }
  
  public int compareTo(Object paramObject) {
    LdapName ldapName = (LdapName)paramObject;
    if (paramObject == this || (this.unparsed != null && this.unparsed.equals(ldapName.unparsed)))
      return 0; 
    int i = Math.min(this.rdns.size(), ldapName.rdns.size());
    for (byte b = 0; b < i; b++) {
      Rdn rdn1 = (Rdn)this.rdns.elementAt(b);
      Rdn rdn2 = (Rdn)ldapName.rdns.elementAt(b);
      int j = rdn1.compareTo(rdn2);
      if (j != 0)
        return j; 
    } 
    return this.rdns.size() - ldapName.rdns.size();
  }
  
  public int hashCode() {
    int i = 0;
    for (byte b = 0; b < this.rdns.size(); b++) {
      Rdn rdn = (Rdn)this.rdns.elementAt(b);
      i += rdn.hashCode();
    } 
    return i;
  }
  
  public int size() { return this.rdns.size(); }
  
  public boolean isEmpty() { return this.rdns.isEmpty(); }
  
  public Enumeration<String> getAll() {
    final Enumeration enum_ = this.rdns.elements();
    return new Enumeration<String>() {
        public boolean hasMoreElements() { return enum_.hasMoreElements(); }
        
        public String nextElement() { return ((LdapName.Rdn)enum_.nextElement()).toString(); }
      };
  }
  
  public String get(int paramInt) { return ((Rdn)this.rdns.elementAt(paramInt)).toString(); }
  
  public Name getPrefix(int paramInt) { return new LdapName(null, this.rdns, 0, paramInt); }
  
  public Name getSuffix(int paramInt) { return new LdapName(null, this.rdns, paramInt, this.rdns.size()); }
  
  public boolean startsWith(Name paramName) {
    int i = this.rdns.size();
    int j = paramName.size();
    return (i >= j && matches(0, j, paramName));
  }
  
  public boolean endsWith(Name paramName) {
    int i = this.rdns.size();
    int j = paramName.size();
    return (i >= j && matches(i - j, i, paramName));
  }
  
  public void setValuesCaseSensitive(boolean paramBoolean) {
    toString();
    this.rdns = null;
    try {
      parse();
    } catch (InvalidNameException invalidNameException) {
      throw new IllegalStateException("Cannot parse name: " + this.unparsed);
    } 
    this.valuesCaseSensitive = paramBoolean;
  }
  
  private boolean matches(int paramInt1, int paramInt2, Name paramName) {
    for (int i = paramInt1; i < paramInt2; i++) {
      Rdn rdn;
      if (paramName instanceof LdapName) {
        LdapName ldapName = (LdapName)paramName;
        rdn = (Rdn)ldapName.rdns.elementAt(i - paramInt1);
      } else {
        String str = paramName.get(i - paramInt1);
        try {
          rdn = (new DnParser(str, this.valuesCaseSensitive)).getRdn();
        } catch (InvalidNameException invalidNameException) {
          return false;
        } 
      } 
      if (!rdn.equals(this.rdns.elementAt(i)))
        return false; 
    } 
    return true;
  }
  
  public Name addAll(Name paramName) throws InvalidNameException { return addAll(size(), paramName); }
  
  public Name addAll(int paramInt, Name paramName) throws InvalidNameException {
    if (paramName instanceof LdapName) {
      LdapName ldapName = (LdapName)paramName;
      for (byte b = 0; b < ldapName.rdns.size(); b++)
        this.rdns.insertElementAt(ldapName.rdns.elementAt(b), paramInt++); 
    } else {
      Enumeration enumeration = paramName.getAll();
      while (enumeration.hasMoreElements()) {
        DnParser dnParser = new DnParser((String)enumeration.nextElement(), this.valuesCaseSensitive);
        this.rdns.insertElementAt(dnParser.getRdn(), paramInt++);
      } 
    } 
    this.unparsed = null;
    return this;
  }
  
  public Name add(String paramString) throws InvalidNameException { return add(size(), paramString); }
  
  public Name add(int paramInt, String paramString) throws InvalidNameException {
    Rdn rdn = (new DnParser(paramString, this.valuesCaseSensitive)).getRdn();
    this.rdns.insertElementAt(rdn, paramInt);
    this.unparsed = null;
    return this;
  }
  
  public Object remove(int paramInt) throws InvalidNameException {
    String str = get(paramInt);
    this.rdns.removeElementAt(paramInt);
    this.unparsed = null;
    return str;
  }
  
  private void parse() throws InvalidNameException { this.rdns = (new DnParser(this.unparsed, this.valuesCaseSensitive)).getDn(); }
  
  private static boolean isWhitespace(char paramChar) { return (paramChar == ' ' || paramChar == '\r'); }
  
  public static String escapeAttributeValue(Object paramObject) { return TypeAndValue.escapeValue(paramObject); }
  
  public static Object unescapeAttributeValue(String paramString) { return TypeAndValue.unescapeValue(paramString); }
  
  private void writeObject(ObjectOutputStream paramObjectOutputStream) throws IOException {
    paramObjectOutputStream.writeObject(toString());
    paramObjectOutputStream.writeBoolean(this.valuesCaseSensitive);
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream) throws IOException, ClassNotFoundException {
    this.unparsed = (String)paramObjectInputStream.readObject();
    this.valuesCaseSensitive = paramObjectInputStream.readBoolean();
    try {
      parse();
    } catch (InvalidNameException invalidNameException) {
      throw new StreamCorruptedException("Invalid name: " + this.unparsed);
    } 
  }
  
  static class DnParser {
    private final String name;
    
    private final char[] chars;
    
    private final int len;
    
    private int cur = 0;
    
    private boolean valuesCaseSensitive;
    
    DnParser(String param1String, boolean param1Boolean) throws InvalidNameException {
      this.name = param1String;
      this.len = param1String.length();
      this.chars = param1String.toCharArray();
      this.valuesCaseSensitive = param1Boolean;
    }
    
    Vector<LdapName.Rdn> getDn() throws InvalidNameException {
      this.cur = 0;
      Vector vector = new Vector(this.len / 3 + 10);
      if (this.len == 0)
        return vector; 
      vector.addElement(parseRdn());
      while (this.cur < this.len) {
        if (this.chars[this.cur] == ',' || this.chars[this.cur] == ';') {
          this.cur++;
          vector.insertElementAt(parseRdn(), 0);
          continue;
        } 
        throw new InvalidNameException("Invalid name: " + this.name);
      } 
      return vector;
    }
    
    LdapName.Rdn getRdn() throws InvalidNameException {
      LdapName.Rdn rdn = parseRdn();
      if (this.cur < this.len)
        throw new InvalidNameException("Invalid RDN: " + this.name); 
      return rdn;
    }
    
    private LdapName.Rdn parseRdn() throws InvalidNameException {
      LdapName.Rdn rdn = new LdapName.Rdn();
      while (this.cur < this.len) {
        consumeWhitespace();
        String str1 = parseAttrType();
        consumeWhitespace();
        if (this.cur >= this.len || this.chars[this.cur] != '=')
          throw new InvalidNameException("Invalid name: " + this.name); 
        this.cur++;
        consumeWhitespace();
        String str2 = parseAttrValue();
        consumeWhitespace();
        rdn.add(new LdapName.TypeAndValue(str1, str2, this.valuesCaseSensitive));
        if (this.cur >= this.len || this.chars[this.cur] != '+')
          break; 
        this.cur++;
      } 
      return rdn;
    }
    
    private String parseAttrType() {
      int i = this.cur;
      while (this.cur < this.len) {
        char c = this.chars[this.cur];
        if (Character.isLetterOrDigit(c) || c == '.' || c == '-' || c == ' ')
          this.cur++; 
      } 
      while (this.cur > i && this.chars[this.cur - 1] == ' ')
        this.cur--; 
      if (i == this.cur)
        throw new InvalidNameException("Invalid name: " + this.name); 
      return new String(this.chars, i, this.cur - i);
    }
    
    private String parseAttrValue() { return (this.cur < this.len && this.chars[this.cur] == '#') ? parseBinaryAttrValue() : ((this.cur < this.len && this.chars[this.cur] == '"') ? parseQuotedAttrValue() : parseStringAttrValue()); }
    
    private String parseBinaryAttrValue() {
      int i = this.cur;
      this.cur++;
      while (this.cur < this.len && Character.isLetterOrDigit(this.chars[this.cur]))
        this.cur++; 
      return new String(this.chars, i, this.cur - i);
    }
    
    private String parseQuotedAttrValue() {
      int i = this.cur;
      this.cur++;
      while (this.cur < this.len && this.chars[this.cur] != '"') {
        if (this.chars[this.cur] == '\\')
          this.cur++; 
        this.cur++;
      } 
      if (this.cur >= this.len)
        throw new InvalidNameException("Invalid name: " + this.name); 
      this.cur++;
      return new String(this.chars, i, this.cur - i);
    }
    
    private String parseStringAttrValue() {
      int i = this.cur;
      int j = -1;
      while (this.cur < this.len && !atTerminator()) {
        if (this.chars[this.cur] == '\\')
          j = ++this.cur; 
        this.cur++;
      } 
      if (this.cur > this.len)
        throw new InvalidNameException("Invalid name: " + this.name); 
      int k;
      for (k = this.cur; k > i && LdapName.isWhitespace(this.chars[k - 1]) && j != k - 1; k--);
      return new String(this.chars, i, k - i);
    }
    
    private void consumeWhitespace() throws InvalidNameException {
      while (this.cur < this.len && LdapName.isWhitespace(this.chars[this.cur]))
        this.cur++; 
    }
    
    private boolean atTerminator() { return (this.cur < this.len && (this.chars[this.cur] == ',' || this.chars[this.cur] == ';' || this.chars[this.cur] == '+')); }
  }
  
  static class Rdn {
    private final Vector<LdapName.TypeAndValue> tvs = new Vector();
    
    void add(LdapName.TypeAndValue param1TypeAndValue) {
      byte b;
      for (b = 0; b < this.tvs.size(); b++) {
        int i = param1TypeAndValue.compareTo(this.tvs.elementAt(b));
        if (i == 0)
          return; 
        if (i < 0)
          break; 
      } 
      this.tvs.insertElementAt(param1TypeAndValue, b);
    }
    
    public String toString() {
      StringBuffer stringBuffer = new StringBuffer();
      for (byte b = 0; b < this.tvs.size(); b++) {
        if (b)
          stringBuffer.append('+'); 
        stringBuffer.append(this.tvs.elementAt(b));
      } 
      return new String(stringBuffer);
    }
    
    public boolean equals(Object param1Object) { return (param1Object instanceof Rdn && compareTo(param1Object) == 0); }
    
    public int compareTo(Object param1Object) {
      Rdn rdn = (Rdn)param1Object;
      int i = Math.min(this.tvs.size(), rdn.tvs.size());
      for (byte b = 0; b < i; b++) {
        LdapName.TypeAndValue typeAndValue = (LdapName.TypeAndValue)this.tvs.elementAt(b);
        int j = typeAndValue.compareTo(rdn.tvs.elementAt(b));
        if (j != 0)
          return j; 
      } 
      return this.tvs.size() - rdn.tvs.size();
    }
    
    public int hashCode() {
      int i = 0;
      for (byte b = 0; b < this.tvs.size(); b++)
        i += ((LdapName.TypeAndValue)this.tvs.elementAt(b)).hashCode(); 
      return i;
    }
    
    Attributes toAttributes() {
      BasicAttributes basicAttributes = new BasicAttributes(true);
      for (byte b = 0; b < this.tvs.size(); b++) {
        LdapName.TypeAndValue typeAndValue = (LdapName.TypeAndValue)this.tvs.elementAt(b);
        Attribute attribute;
        if ((attribute = basicAttributes.get(typeAndValue.getType())) == null) {
          basicAttributes.put(typeAndValue.getType(), typeAndValue.getUnescapedValue());
        } else {
          attribute.add(typeAndValue.getUnescapedValue());
        } 
      } 
      return basicAttributes;
    }
  }
  
  static class TypeAndValue {
    private final String type;
    
    private final String value;
    
    private final boolean binary;
    
    private final boolean valueCaseSensitive;
    
    private String comparable = null;
    
    TypeAndValue(String param1String1, String param1String2, boolean param1Boolean) {
      this.type = param1String1;
      this.value = param1String2;
      this.binary = param1String2.startsWith("#");
      this.valueCaseSensitive = param1Boolean;
    }
    
    public String toString() { return this.type + "=" + this.value; }
    
    public int compareTo(Object param1Object) {
      TypeAndValue typeAndValue = (TypeAndValue)param1Object;
      int i = this.type.compareToIgnoreCase(typeAndValue.type);
      return (i != 0) ? i : (this.value.equals(typeAndValue.value) ? 0 : getValueComparable().compareTo(typeAndValue.getValueComparable()));
    }
    
    public boolean equals(Object param1Object) {
      if (!(param1Object instanceof TypeAndValue))
        return false; 
      TypeAndValue typeAndValue = (TypeAndValue)param1Object;
      return (this.type.equalsIgnoreCase(typeAndValue.type) && (this.value.equals(typeAndValue.value) || getValueComparable().equals(typeAndValue.getValueComparable())));
    }
    
    public int hashCode() { return this.type.toUpperCase(Locale.ENGLISH).hashCode() + getValueComparable().hashCode(); }
    
    String getType() { return this.type; }
    
    Object getUnescapedValue() { return unescapeValue(this.value); }
    
    private String getValueComparable() {
      if (this.comparable != null)
        return this.comparable; 
      if (this.binary) {
        this.comparable = this.value.toUpperCase(Locale.ENGLISH);
      } else {
        this.comparable = (String)unescapeValue(this.value);
        if (!this.valueCaseSensitive)
          this.comparable = this.comparable.toUpperCase(Locale.ENGLISH); 
      } 
      return this.comparable;
    }
    
    static String escapeValue(Object param1Object) { return (param1Object instanceof byte[]) ? escapeBinaryValue((byte[])param1Object) : escapeStringValue((String)param1Object); }
    
    private static String escapeStringValue(String param1String) {
      char[] arrayOfChar = param1String.toCharArray();
      StringBuffer stringBuffer = new StringBuffer(2 * param1String.length());
      byte b1;
      for (b1 = 0; b1 < arrayOfChar.length && LdapName.isWhitespace(arrayOfChar[b1]); b1++);
      int i;
      for (i = arrayOfChar.length - 1; i >= 0 && LdapName.isWhitespace(arrayOfChar[i]); i--);
      for (byte b2 = 0; b2 < arrayOfChar.length; b2++) {
        char c = arrayOfChar[b2];
        if (b2 < b1 || b2 > i || ",=+<>#;\"\\".indexOf(c) >= 0)
          stringBuffer.append('\\'); 
        stringBuffer.append(c);
      } 
      return new String(stringBuffer);
    }
    
    private static String escapeBinaryValue(byte[] param1ArrayOfByte) {
      StringBuffer stringBuffer = new StringBuffer(1 + 2 * param1ArrayOfByte.length);
      stringBuffer.append("#");
      for (byte b = 0; b < param1ArrayOfByte.length; b++) {
        byte b1 = param1ArrayOfByte[b];
        stringBuffer.append(Character.forDigit(0xF & b1 >>> 4, 16));
        stringBuffer.append(Character.forDigit(0xF & b1, 16));
      } 
      return (new String(stringBuffer)).toUpperCase(Locale.ENGLISH);
    }
    
    static Object unescapeValue(String param1String) {
      char[] arrayOfChar = param1String.toCharArray();
      int i = 0;
      int j = arrayOfChar.length;
      while (i < j && LdapName.isWhitespace(arrayOfChar[i]))
        i++; 
      while (i < j && LdapName.isWhitespace(arrayOfChar[j - 1]))
        j--; 
      if (j != arrayOfChar.length && i < j && arrayOfChar[j - 1] == '\\')
        j++; 
      if (i >= j)
        return ""; 
      if (arrayOfChar[i] == '#')
        return decodeHexPairs(arrayOfChar, ++i, j); 
      if (arrayOfChar[i] == '"' && arrayOfChar[j - 1] == '"') {
        i++;
        j--;
      } 
      StringBuffer stringBuffer = new StringBuffer(j - i);
      int k = -1;
      int m;
      for (m = i; m < j; m++) {
        if (arrayOfChar[m] == '\\' && m + 1 < j) {
          if (!Character.isLetterOrDigit(arrayOfChar[m + 1])) {
            stringBuffer.append(arrayOfChar[++m]);
            k = m;
          } else {
            byte[] arrayOfByte = getUtf8Octets(arrayOfChar, m, j);
            if (arrayOfByte.length > 0) {
              try {
                stringBuffer.append(new String(arrayOfByte, "UTF8"));
              } catch (UnsupportedEncodingException unsupportedEncodingException) {}
              m += arrayOfByte.length * 3 - 1;
            } else {
              throw new IllegalArgumentException("Not a valid attribute string value:" + param1String + ", improper usage of backslash");
            } 
          } 
        } else {
          stringBuffer.append(arrayOfChar[m]);
        } 
      } 
      m = stringBuffer.length();
      if (LdapName.isWhitespace(stringBuffer.charAt(m - 1)) && k != j - 1)
        stringBuffer.setLength(m - 1); 
      return new String(stringBuffer);
    }
    
    private static byte[] decodeHexPairs(char[] param1ArrayOfChar, int param1Int1, int param1Int2) {
      byte[] arrayOfByte = new byte[(param1Int2 - param1Int1) / 2];
      for (byte b = 0; param1Int1 + 1 < param1Int2; b++) {
        int i = Character.digit(param1ArrayOfChar[param1Int1], 16);
        int j = Character.digit(param1ArrayOfChar[param1Int1 + 1], 16);
        if (i < 0 || j < 0)
          break; 
        arrayOfByte[b] = (byte)((i << 4) + j);
        param1Int1 += 2;
      } 
      if (param1Int1 != param1Int2)
        throw new IllegalArgumentException("Illegal attribute value: #" + new String(param1ArrayOfChar)); 
      return arrayOfByte;
    }
    
    private static byte[] getUtf8Octets(char[] param1ArrayOfChar, int param1Int1, int param1Int2) {
      byte[] arrayOfByte1 = new byte[(param1Int2 - param1Int1) / 3];
      byte b = 0;
      while (param1Int1 + 2 < param1Int2 && param1ArrayOfChar[param1Int1++] == '\\') {
        int i = Character.digit(param1ArrayOfChar[param1Int1++], 16);
        int j = Character.digit(param1ArrayOfChar[param1Int1++], 16);
        if (i < 0 || j < 0)
          break; 
        arrayOfByte1[b++] = (byte)((i << 4) + j);
      } 
      if (b == arrayOfByte1.length)
        return arrayOfByte1; 
      byte[] arrayOfByte2 = new byte[b];
      System.arraycopy(arrayOfByte1, 0, arrayOfByte2, 0, b);
      return arrayOfByte2;
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\jndi\ldap\LdapName.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */