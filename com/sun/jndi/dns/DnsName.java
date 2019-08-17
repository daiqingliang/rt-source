package com.sun.jndi.dns;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.StreamCorruptedException;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.NoSuchElementException;
import javax.naming.InvalidNameException;
import javax.naming.Name;

public final class DnsName implements Name {
  private String domain = "";
  
  private ArrayList<String> labels = new ArrayList();
  
  private short octets = 1;
  
  private static final long serialVersionUID = 7040187611324710271L;
  
  public DnsName() {}
  
  public DnsName(String paramString) throws InvalidNameException { parse(paramString); }
  
  private DnsName(DnsName paramDnsName, int paramInt1, int paramInt2) {
    int i = paramDnsName.size() - paramInt2;
    int j = paramDnsName.size() - paramInt1;
    this.labels.addAll(paramDnsName.labels.subList(i, j));
    if (size() == paramDnsName.size()) {
      this.domain = paramDnsName.domain;
      this.octets = paramDnsName.octets;
    } else {
      for (String str : this.labels) {
        if (str.length() > 0)
          this.octets = (short)(this.octets + (short)(str.length() + 1)); 
      } 
    } 
  }
  
  public String toString() {
    if (this.domain == null) {
      StringBuilder stringBuilder = new StringBuilder();
      for (String str : this.labels) {
        if (stringBuilder.length() > 0 || str.length() == 0)
          stringBuilder.append('.'); 
        escape(stringBuilder, str);
      } 
      this.domain = stringBuilder.toString();
    } 
    return this.domain;
  }
  
  public boolean isHostName() {
    for (String str : this.labels) {
      if (!isHostNameLabel(str))
        return false; 
    } 
    return true;
  }
  
  public short getOctets() { return this.octets; }
  
  public int size() { return this.labels.size(); }
  
  public boolean isEmpty() { return (size() == 0); }
  
  public int hashCode() {
    int i = 0;
    for (byte b = 0; b < size(); b++)
      i = 31 * i + getKey(b).hashCode(); 
    return i;
  }
  
  public boolean equals(Object paramObject) {
    if (!(paramObject instanceof Name) || paramObject instanceof javax.naming.CompositeName)
      return false; 
    Name name = (Name)paramObject;
    return (size() == name.size() && compareTo(paramObject) == 0);
  }
  
  public int compareTo(Object paramObject) {
    Name name = (Name)paramObject;
    return compareRange(0, size(), name);
  }
  
  public boolean startsWith(Name paramName) { return (size() >= paramName.size() && compareRange(0, paramName.size(), paramName) == 0); }
  
  public boolean endsWith(Name paramName) { return (size() >= paramName.size() && compareRange(size() - paramName.size(), size(), paramName) == 0); }
  
  public String get(int paramInt) {
    if (paramInt < 0 || paramInt >= size())
      throw new ArrayIndexOutOfBoundsException(); 
    int i = size() - paramInt - 1;
    return (String)this.labels.get(i);
  }
  
  public Enumeration<String> getAll() { return new Enumeration<String>() {
        int pos = 0;
        
        public boolean hasMoreElements() { return (this.pos < DnsName.this.size()); }
        
        public String nextElement() {
          if (this.pos < DnsName.this.size())
            return DnsName.this.get(this.pos++); 
          throw new NoSuchElementException();
        }
      }; }
  
  public Name getPrefix(int paramInt) { return new DnsName(this, 0, paramInt); }
  
  public Name getSuffix(int paramInt) { return new DnsName(this, paramInt, size()); }
  
  public Object clone() { return new DnsName(this, 0, size()); }
  
  public Object remove(int paramInt) {
    if (paramInt < 0 || paramInt >= size())
      throw new ArrayIndexOutOfBoundsException(); 
    int i = size() - paramInt - 1;
    String str = (String)this.labels.remove(i);
    int j = str.length();
    if (j > 0)
      this.octets = (short)(this.octets - (short)(j + 1)); 
    this.domain = null;
    return str;
  }
  
  public Name add(String paramString) throws InvalidNameException { return add(size(), paramString); }
  
  public Name add(int paramInt, String paramString) throws InvalidNameException {
    if (paramInt < 0 || paramInt > size())
      throw new ArrayIndexOutOfBoundsException(); 
    int i = paramString.length();
    if ((paramInt > 0 && i == 0) || (paramInt == 0 && hasRootLabel()))
      throw new InvalidNameException("Empty label must be the last label in a domain name"); 
    if (i > 0) {
      if (this.octets + i + 1 >= 256)
        throw new InvalidNameException("Name too long"); 
      this.octets = (short)(this.octets + (short)(i + 1));
    } 
    int j = size() - paramInt;
    verifyLabel(paramString);
    this.labels.add(j, paramString);
    this.domain = null;
    return this;
  }
  
  public Name addAll(Name paramName) throws InvalidNameException { return addAll(size(), paramName); }
  
  public Name addAll(int paramInt, Name paramName) throws InvalidNameException {
    if (paramName instanceof DnsName) {
      DnsName dnsName = (DnsName)paramName;
      if (dnsName.isEmpty())
        return this; 
      if ((paramInt > 0 && dnsName.hasRootLabel()) || (paramInt == 0 && hasRootLabel()))
        throw new InvalidNameException("Empty label must be the last label in a domain name"); 
      short s = (short)(this.octets + dnsName.octets - 1);
      if (s > 255)
        throw new InvalidNameException("Name too long"); 
      this.octets = s;
      int i = size() - paramInt;
      this.labels.addAll(i, dnsName.labels);
      if (isEmpty()) {
        this.domain = dnsName.domain;
      } else if (this.domain == null || dnsName.domain == null) {
        this.domain = null;
      } else if (paramInt == 0) {
        this.domain += (dnsName.domain.equals(".") ? "" : ".") + dnsName.domain;
      } else if (paramInt == size()) {
        dnsName.domain += (this.domain.equals(".") ? "" : ".") + this.domain;
      } else {
        this.domain = null;
      } 
    } else if (paramName instanceof javax.naming.CompositeName) {
      paramName = (DnsName)paramName;
    } else {
      for (int i = paramName.size() - 1; i >= 0; i--)
        add(paramInt, paramName.get(i)); 
    } 
    return this;
  }
  
  boolean hasRootLabel() { return (!isEmpty() && get(0).equals("")); }
  
  private int compareRange(int paramInt1, int paramInt2, Name paramName) {
    if (paramName instanceof javax.naming.CompositeName)
      paramName = (DnsName)paramName; 
    int i = Math.min(paramInt2 - paramInt1, paramName.size());
    for (int j = 0; j < i; j++) {
      String str1 = get(j + paramInt1);
      String str2 = paramName.get(j);
      int k = size() - j + paramInt1 - 1;
      int m = compareLabels(str1, str2);
      if (m != 0)
        return m; 
    } 
    return paramInt2 - paramInt1 - paramName.size();
  }
  
  String getKey(int paramInt) { return keyForLabel(get(paramInt)); }
  
  private void parse(String paramString) throws InvalidNameException {
    StringBuffer stringBuffer = new StringBuffer();
    for (byte b = 0; b < paramString.length(); b++) {
      char c = paramString.charAt(b);
      if (c == '\\') {
        c = getEscapedOctet(paramString, b++);
        if (isDigit(paramString.charAt(b)))
          b += 2; 
        stringBuffer.append(c);
      } else if (c != '.') {
        stringBuffer.append(c);
      } else {
        add(0, stringBuffer.toString());
        stringBuffer.delete(0, b);
      } 
    } 
    if (!paramString.equals("") && !paramString.equals("."))
      add(0, stringBuffer.toString()); 
    this.domain = paramString;
  }
  
  private static char getEscapedOctet(String paramString, int paramInt) throws InvalidNameException {
    try {
      char c = paramString.charAt(++paramInt);
      if (isDigit(c)) {
        char c1 = paramString.charAt(++paramInt);
        char c2 = paramString.charAt(++paramInt);
        if (isDigit(c1) && isDigit(c2))
          return (char)((c - '0') * 'd' + (c1 - '0') * '\n' + c2 - '0'); 
        throw new InvalidNameException("Invalid escape sequence in " + paramString);
      } 
      return c;
    } catch (IndexOutOfBoundsException indexOutOfBoundsException) {
      throw new InvalidNameException("Invalid escape sequence in " + paramString);
    } 
  }
  
  private static void verifyLabel(String paramString) throws InvalidNameException {
    if (paramString.length() > 63)
      throw new InvalidNameException("Label exceeds 63 octets: " + paramString); 
    for (byte b = 0; b < paramString.length(); b++) {
      char c = paramString.charAt(b);
      if ((c & 0xFF00) != '\000')
        throw new InvalidNameException("Label has two-byte char: " + paramString); 
    } 
  }
  
  private static boolean isHostNameLabel(String paramString) {
    for (byte b = 0; b < paramString.length(); b++) {
      char c = paramString.charAt(b);
      if (!isHostNameChar(c))
        return false; 
    } 
    return (!paramString.startsWith("-") && !paramString.endsWith("-"));
  }
  
  private static boolean isHostNameChar(char paramChar) { return (paramChar == '-' || (paramChar >= 'a' && paramChar <= 'z') || (paramChar >= 'A' && paramChar <= 'Z') || (paramChar >= '0' && paramChar <= '9')); }
  
  private static boolean isDigit(char paramChar) { return (paramChar >= '0' && paramChar <= '9'); }
  
  private static void escape(StringBuilder paramStringBuilder, String paramString) {
    for (byte b = 0; b < paramString.length(); b++) {
      char c = paramString.charAt(b);
      if (c == '.' || c == '\\')
        paramStringBuilder.append('\\'); 
      paramStringBuilder.append(c);
    } 
  }
  
  private static int compareLabels(String paramString1, String paramString2) {
    int i = Math.min(paramString1.length(), paramString2.length());
    for (byte b = 0; b < i; b++) {
      char c1 = paramString1.charAt(b);
      char c2 = paramString2.charAt(b);
      if (c1 >= 'A' && c1 <= 'Z')
        c1 = (char)(c1 + ' '); 
      if (c2 >= 'A' && c2 <= 'Z')
        c2 = (char)(c2 + ' '); 
      if (c1 != c2)
        return c1 - c2; 
    } 
    return paramString1.length() - paramString2.length();
  }
  
  private static String keyForLabel(String paramString) {
    StringBuffer stringBuffer = new StringBuffer(paramString.length());
    for (byte b = 0; b < paramString.length(); b++) {
      char c = paramString.charAt(b);
      if (c >= 'A' && c <= 'Z')
        c = (char)(c + ' '); 
      stringBuffer.append(c);
    } 
    return stringBuffer.toString();
  }
  
  private void writeObject(ObjectOutputStream paramObjectOutputStream) throws IOException { paramObjectOutputStream.writeObject(toString()); }
  
  private void readObject(ObjectInputStream paramObjectInputStream) throws IOException, ClassNotFoundException {
    try {
      parse((String)paramObjectInputStream.readObject());
    } catch (InvalidNameException invalidNameException) {
      throw new StreamCorruptedException("Invalid name: " + this.domain);
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\jndi\dns\DnsName.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */