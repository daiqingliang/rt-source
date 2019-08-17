package javax.naming.ldap;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.io.StreamCorruptedException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Locale;
import javax.naming.InvalidNameException;
import javax.naming.NamingEnumeration;
import javax.naming.NamingException;
import javax.naming.directory.Attribute;
import javax.naming.directory.Attributes;
import javax.naming.directory.BasicAttributes;

public class Rdn extends Object implements Serializable, Comparable<Object> {
  private ArrayList<RdnEntry> entries;
  
  private static final int DEFAULT_SIZE = 1;
  
  private static final long serialVersionUID = -5994465067210009656L;
  
  private static final String escapees = ",=+<>#;\"\\";
  
  public Rdn(Attributes paramAttributes) throws InvalidNameException {
    if (paramAttributes.size() == 0)
      throw new InvalidNameException("Attributes cannot be empty"); 
    this.entries = new ArrayList(paramAttributes.size());
    NamingEnumeration namingEnumeration = paramAttributes.getAll();
    try {
      for (byte b = 0; namingEnumeration.hasMore(); b++) {
        RdnEntry rdnEntry = new RdnEntry(null);
        Attribute attribute = (Attribute)namingEnumeration.next();
        rdnEntry.type = attribute.getID();
        rdnEntry.value = attribute.get();
        this.entries.add(b, rdnEntry);
      } 
    } catch (NamingException namingException) {
      InvalidNameException invalidNameException = new InvalidNameException(namingException.getMessage());
      invalidNameException.initCause(namingException);
      throw invalidNameException;
    } 
    sort();
  }
  
  public Rdn(String paramString) throws InvalidNameException {
    this.entries = new ArrayList(1);
    (new Rfc2253Parser(paramString)).parseRdn(this);
  }
  
  public Rdn(Rdn paramRdn) {
    this.entries = new ArrayList(paramRdn.entries.size());
    this.entries.addAll(paramRdn.entries);
  }
  
  public Rdn(String paramString, Object paramObject) throws InvalidNameException {
    if (paramObject == null)
      throw new NullPointerException("Cannot set value to null"); 
    if (paramString.equals("") || isEmptyValue(paramObject))
      throw new InvalidNameException("type or value cannot be empty, type:" + paramString + " value:" + paramObject); 
    this.entries = new ArrayList(1);
    put(paramString, paramObject);
  }
  
  private boolean isEmptyValue(Object paramObject) { return ((paramObject instanceof String && paramObject.equals("")) || (paramObject instanceof byte[] && (byte[])paramObject.length == 0)); }
  
  Rdn() { this.entries = new ArrayList(1); }
  
  Rdn put(String paramString, Object paramObject) {
    RdnEntry rdnEntry;
    rdnEntry.type = paramString;
    if (paramObject instanceof byte[]) {
      rdnEntry.value = ((byte[])paramObject).clone();
    } else {
      rdnEntry.value = paramObject;
    } 
    this.entries.add(rdnEntry);
    return this;
  }
  
  void sort() {
    if (this.entries.size() > 1)
      Collections.sort(this.entries); 
  }
  
  public Object getValue() { return ((RdnEntry)this.entries.get(0)).getValue(); }
  
  public String getType() { return ((RdnEntry)this.entries.get(0)).getType(); }
  
  public String toString() {
    StringBuilder stringBuilder = new StringBuilder();
    int i = this.entries.size();
    if (i > 0)
      stringBuilder.append(this.entries.get(0)); 
    for (byte b = 1; b < i; b++) {
      stringBuilder.append('+');
      stringBuilder.append(this.entries.get(b));
    } 
    return stringBuilder.toString();
  }
  
  public int compareTo(Object paramObject) {
    if (!(paramObject instanceof Rdn))
      throw new ClassCastException("The obj is not a Rdn"); 
    if (paramObject == this)
      return 0; 
    Rdn rdn = (Rdn)paramObject;
    int i = Math.min(this.entries.size(), rdn.entries.size());
    for (byte b = 0; b < i; b++) {
      int j = ((RdnEntry)this.entries.get(b)).compareTo((RdnEntry)rdn.entries.get(b));
      if (j != 0)
        return j; 
    } 
    return this.entries.size() - rdn.entries.size();
  }
  
  public boolean equals(Object paramObject) {
    if (paramObject == this)
      return true; 
    if (!(paramObject instanceof Rdn))
      return false; 
    Rdn rdn = (Rdn)paramObject;
    if (this.entries.size() != rdn.size())
      return false; 
    for (byte b = 0; b < this.entries.size(); b++) {
      if (!((RdnEntry)this.entries.get(b)).equals(rdn.entries.get(b)))
        return false; 
    } 
    return true;
  }
  
  public int hashCode() {
    int i = 0;
    for (byte b = 0; b < this.entries.size(); b++)
      i += ((RdnEntry)this.entries.get(b)).hashCode(); 
    return i;
  }
  
  public Attributes toAttributes() {
    BasicAttributes basicAttributes = new BasicAttributes(true);
    for (byte b = 0; b < this.entries.size(); b++) {
      RdnEntry rdnEntry = (RdnEntry)this.entries.get(b);
      Attribute attribute = basicAttributes.put(rdnEntry.getType(), rdnEntry.getValue());
      if (attribute != null) {
        attribute.add(rdnEntry.getValue());
        basicAttributes.put(attribute);
      } 
    } 
    return basicAttributes;
  }
  
  public int size() { return this.entries.size(); }
  
  public static String escapeValue(Object paramObject) { return (paramObject instanceof byte[]) ? escapeBinaryValue((byte[])paramObject) : escapeStringValue((String)paramObject); }
  
  private static String escapeStringValue(String paramString) {
    char[] arrayOfChar = paramString.toCharArray();
    StringBuilder stringBuilder = new StringBuilder(2 * paramString.length());
    byte b1;
    for (b1 = 0; b1 < arrayOfChar.length && isWhitespace(arrayOfChar[b1]); b1++);
    int i;
    for (i = arrayOfChar.length - 1; i >= 0 && isWhitespace(arrayOfChar[i]); i--);
    for (byte b2 = 0; b2 < arrayOfChar.length; b2++) {
      char c = arrayOfChar[b2];
      if (b2 < b1 || b2 > i || ",=+<>#;\"\\".indexOf(c) >= 0)
        stringBuilder.append('\\'); 
      stringBuilder.append(c);
    } 
    return stringBuilder.toString();
  }
  
  private static String escapeBinaryValue(byte[] paramArrayOfByte) {
    StringBuilder stringBuilder = new StringBuilder(1 + 2 * paramArrayOfByte.length);
    stringBuilder.append("#");
    for (byte b = 0; b < paramArrayOfByte.length; b++) {
      byte b1 = paramArrayOfByte[b];
      stringBuilder.append(Character.forDigit(0xF & b1 >>> 4, 16));
      stringBuilder.append(Character.forDigit(0xF & b1, 16));
    } 
    return stringBuilder.toString();
  }
  
  public static Object unescapeValue(String paramString) {
    char[] arrayOfChar = paramString.toCharArray();
    int i = 0;
    int j = arrayOfChar.length;
    while (i < j && isWhitespace(arrayOfChar[i]))
      i++; 
    while (i < j && isWhitespace(arrayOfChar[j - 1]))
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
    StringBuilder stringBuilder = new StringBuilder(j - i);
    int k = -1;
    int m;
    for (m = i; m < j; m++) {
      if (arrayOfChar[m] == '\\' && m + 1 < j) {
        if (!Character.isLetterOrDigit(arrayOfChar[m + 1])) {
          stringBuilder.append(arrayOfChar[++m]);
          k = m;
        } else {
          byte[] arrayOfByte = getUtf8Octets(arrayOfChar, m, j);
          if (arrayOfByte.length > 0) {
            try {
              stringBuilder.append(new String(arrayOfByte, "UTF8"));
            } catch (UnsupportedEncodingException unsupportedEncodingException) {}
            m += arrayOfByte.length * 3 - 1;
          } else {
            throw new IllegalArgumentException("Not a valid attribute string value:" + paramString + ",improper usage of backslash");
          } 
        } 
      } else {
        stringBuilder.append(arrayOfChar[m]);
      } 
    } 
    m = stringBuilder.length();
    if (isWhitespace(stringBuilder.charAt(m - 1)) && k != j - 1)
      stringBuilder.setLength(m - 1); 
    return stringBuilder.toString();
  }
  
  private static byte[] decodeHexPairs(char[] paramArrayOfChar, int paramInt1, int paramInt2) {
    byte[] arrayOfByte = new byte[(paramInt2 - paramInt1) / 2];
    for (byte b = 0; paramInt1 + 1 < paramInt2; b++) {
      int i = Character.digit(paramArrayOfChar[paramInt1], 16);
      int j = Character.digit(paramArrayOfChar[paramInt1 + 1], 16);
      if (i < 0 || j < 0)
        break; 
      arrayOfByte[b] = (byte)((i << 4) + j);
      paramInt1 += 2;
    } 
    if (paramInt1 != paramInt2)
      throw new IllegalArgumentException("Illegal attribute value: " + new String(paramArrayOfChar)); 
    return arrayOfByte;
  }
  
  private static byte[] getUtf8Octets(char[] paramArrayOfChar, int paramInt1, int paramInt2) {
    byte[] arrayOfByte1 = new byte[(paramInt2 - paramInt1) / 3];
    byte b = 0;
    while (paramInt1 + 2 < paramInt2 && paramArrayOfChar[paramInt1++] == '\\') {
      int i = Character.digit(paramArrayOfChar[paramInt1++], 16);
      int j = Character.digit(paramArrayOfChar[paramInt1++], 16);
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
  
  private static boolean isWhitespace(char paramChar) { return (paramChar == ' ' || paramChar == '\r'); }
  
  private void writeObject(ObjectOutputStream paramObjectOutputStream) throws IOException {
    paramObjectOutputStream.defaultWriteObject();
    paramObjectOutputStream.writeObject(toString());
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream) throws IOException, ClassNotFoundException {
    paramObjectInputStream.defaultReadObject();
    this.entries = new ArrayList(1);
    String str = (String)paramObjectInputStream.readObject();
    try {
      (new Rfc2253Parser(str)).parseRdn(this);
    } catch (InvalidNameException invalidNameException) {
      throw new StreamCorruptedException("Invalid name: " + str);
    } 
  }
  
  private static class RdnEntry extends Object implements Comparable<RdnEntry> {
    private String type;
    
    private Object value;
    
    private String comparable = null;
    
    private RdnEntry() {}
    
    String getType() { return this.type; }
    
    Object getValue() { return this.value; }
    
    public int compareTo(RdnEntry param1RdnEntry) {
      int i = this.type.compareToIgnoreCase(param1RdnEntry.type);
      return (i != 0) ? i : (this.value.equals(param1RdnEntry.value) ? 0 : getValueComparable().compareTo(param1RdnEntry.getValueComparable()));
    }
    
    public boolean equals(Object param1Object) {
      if (param1Object == this)
        return true; 
      if (!(param1Object instanceof RdnEntry))
        return false; 
      RdnEntry rdnEntry = (RdnEntry)param1Object;
      return (this.type.equalsIgnoreCase(rdnEntry.type) && getValueComparable().equals(rdnEntry.getValueComparable()));
    }
    
    public int hashCode() { return this.type.toUpperCase(Locale.ENGLISH).hashCode() + getValueComparable().hashCode(); }
    
    public String toString() { return this.type + "=" + Rdn.escapeValue(this.value); }
    
    private String getValueComparable() {
      if (this.comparable != null)
        return this.comparable; 
      if (this.value instanceof byte[]) {
        this.comparable = Rdn.escapeBinaryValue((byte[])this.value);
      } else {
        this.comparable = ((String)this.value).toUpperCase(Locale.ENGLISH);
      } 
      return this.comparable;
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\naming\ldap\Rdn.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */