package com.sun.jndi.dns;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import javax.naming.CommunicationException;

public class ResourceRecord {
  static final int TYPE_A = 1;
  
  static final int TYPE_NS = 2;
  
  static final int TYPE_CNAME = 5;
  
  static final int TYPE_SOA = 6;
  
  static final int TYPE_PTR = 12;
  
  static final int TYPE_HINFO = 13;
  
  static final int TYPE_MX = 15;
  
  static final int TYPE_TXT = 16;
  
  static final int TYPE_AAAA = 28;
  
  static final int TYPE_SRV = 33;
  
  static final int TYPE_NAPTR = 35;
  
  static final int QTYPE_AXFR = 252;
  
  static final int QTYPE_STAR = 255;
  
  static final String[] rrTypeNames = { 
      null, "A", "NS", null, null, "CNAME", "SOA", null, null, null, 
      null, null, "PTR", "HINFO", null, "MX", "TXT", null, null, null, 
      null, null, null, null, null, null, null, null, "AAAA", null, 
      null, null, null, "SRV", null, "NAPTR" };
  
  static final int CLASS_INTERNET = 1;
  
  static final int CLASS_HESIOD = 2;
  
  static final int QCLASS_STAR = 255;
  
  static final String[] rrClassNames = { null, "IN", null, null, "HS" };
  
  private static final int MAXIMUM_COMPRESSION_REFERENCES = 16;
  
  byte[] msg;
  
  int msgLen;
  
  boolean qSection;
  
  int offset;
  
  int rrlen;
  
  DnsName name;
  
  int rrtype;
  
  String rrtypeName;
  
  int rrclass;
  
  String rrclassName;
  
  int ttl = 0;
  
  int rdlen = 0;
  
  Object rdata = null;
  
  private static final boolean debug = false;
  
  ResourceRecord(byte[] paramArrayOfByte, int paramInt1, int paramInt2, boolean paramBoolean1, boolean paramBoolean2) throws CommunicationException {
    this.msg = paramArrayOfByte;
    this.msgLen = paramInt1;
    this.offset = paramInt2;
    this.qSection = paramBoolean1;
    decode(paramBoolean2);
  }
  
  public String toString() {
    String str = this.name + " " + this.rrclassName + " " + this.rrtypeName;
    if (!this.qSection)
      str = str + " " + this.ttl + " " + ((this.rdata != null) ? this.rdata : "[n/a]"); 
    return str;
  }
  
  public DnsName getName() { return this.name; }
  
  public int size() { return this.rrlen; }
  
  public int getType() { return this.rrtype; }
  
  public int getRrclass() { return this.rrclass; }
  
  public Object getRdata() { return this.rdata; }
  
  public static String getTypeName(int paramInt) { return valueToName(paramInt, rrTypeNames); }
  
  public static int getType(String paramString) { return nameToValue(paramString, rrTypeNames); }
  
  public static String getRrclassName(int paramInt) { return valueToName(paramInt, rrClassNames); }
  
  public static int getRrclass(String paramString) { return nameToValue(paramString, rrClassNames); }
  
  private static String valueToName(int paramInt, String[] paramArrayOfString) {
    String str = null;
    if (paramInt > 0 && paramInt < paramArrayOfString.length) {
      str = paramArrayOfString[paramInt];
    } else if (paramInt == 255) {
      str = "*";
    } 
    if (str == null)
      str = Integer.toString(paramInt); 
    return str;
  }
  
  private static int nameToValue(String paramString, String[] paramArrayOfString) {
    if (paramString.equals(""))
      return -1; 
    if (paramString.equals("*"))
      return 255; 
    if (Character.isDigit(paramString.charAt(0)))
      try {
        return Integer.parseInt(paramString);
      } catch (NumberFormatException numberFormatException) {} 
    for (byte b = 1; b < paramArrayOfString.length; b++) {
      if (paramArrayOfString[b] != null && paramString.equalsIgnoreCase(paramArrayOfString[b]))
        return b; 
    } 
    return -1;
  }
  
  public static int compareSerialNumbers(long paramLong1, long paramLong2) {
    long l = paramLong2 - paramLong1;
    return (l == 0L) ? 0 : (((l > 0L && l <= 2147483647L) || (l < 0L && -l > 2147483647L)) ? -1 : 1);
  }
  
  private void decode(boolean paramBoolean) throws CommunicationException {
    int i = this.offset;
    this.name = new DnsName();
    i = decodeName(i, this.name);
    this.rrtype = getUShort(i);
    this.rrtypeName = (this.rrtype < rrTypeNames.length) ? rrTypeNames[this.rrtype] : null;
    if (this.rrtypeName == null)
      this.rrtypeName = Integer.toString(this.rrtype); 
    i += 2;
    this.rrclass = getUShort(i);
    this.rrclassName = (this.rrclass < rrClassNames.length) ? rrClassNames[this.rrclass] : null;
    if (this.rrclassName == null)
      this.rrclassName = Integer.toString(this.rrclass); 
    i += 2;
    if (!this.qSection) {
      this.ttl = getInt(i);
      i += 4;
      this.rdlen = getUShort(i);
      i += 2;
      this.rdata = (paramBoolean || this.rrtype == 6) ? decodeRdata(i) : null;
      if (this.rdata instanceof DnsName)
        this.rdata = this.rdata.toString(); 
      i += this.rdlen;
    } 
    this.rrlen = i - this.offset;
    this.msg = null;
  }
  
  private int getUByte(int paramInt) { return this.msg[paramInt] & 0xFF; }
  
  private int getUShort(int paramInt) { return (this.msg[paramInt] & 0xFF) << 8 | this.msg[paramInt + 1] & 0xFF; }
  
  private int getInt(int paramInt) { return getUShort(paramInt) << 16 | getUShort(paramInt + 2); }
  
  private long getUInt(int paramInt) { return getInt(paramInt) & 0xFFFFFFFFL; }
  
  private DnsName decodeName(int paramInt) throws CommunicationException {
    DnsName dnsName = new DnsName();
    decodeName(paramInt, dnsName);
    return dnsName;
  }
  
  private int decodeName(int paramInt, DnsName paramDnsName) throws CommunicationException {
    int i = -1;
    byte b = 0;
    try {
      while (true) {
        if (b > 16)
          throw new IOException("Too many compression references"); 
        byte b1 = this.msg[paramInt] & 0xFF;
        if (b1 == 0) {
          paramInt++;
          paramDnsName.add(0, "");
          break;
        } 
        if (b1 <= 63) {
          paramDnsName.add(0, new String(this.msg, ++paramInt, b1, StandardCharsets.ISO_8859_1));
          paramInt += b1;
          continue;
        } 
        if ((b1 & 0xC0) == 192) {
          b++;
          int j = paramInt;
          if (i == -1)
            i = paramInt + 2; 
          paramInt = getUShort(paramInt) & 0x3FFF;
          continue;
        } 
        throw new IOException("Invalid label type: " + b1);
      } 
    } catch (IOException|javax.naming.InvalidNameException iOException) {
      CommunicationException communicationException = new CommunicationException("DNS error: malformed packet");
      communicationException.initCause(iOException);
      throw communicationException;
    } 
    if (i == -1)
      i = paramInt; 
    return i;
  }
  
  private Object decodeRdata(int paramInt) throws CommunicationException {
    if (this.rrclass == 1)
      switch (this.rrtype) {
        case 1:
          return decodeA(paramInt);
        case 28:
          return decodeAAAA(paramInt);
        case 2:
        case 5:
        case 12:
          return decodeName(paramInt);
        case 15:
          return decodeMx(paramInt);
        case 6:
          return decodeSoa(paramInt);
        case 33:
          return decodeSrv(paramInt);
        case 35:
          return decodeNaptr(paramInt);
        case 16:
          return decodeTxt(paramInt);
        case 13:
          return decodeHinfo(paramInt);
      }  
    byte[] arrayOfByte = new byte[this.rdlen];
    System.arraycopy(this.msg, paramInt, arrayOfByte, 0, this.rdlen);
    return arrayOfByte;
  }
  
  private String decodeMx(int paramInt) {
    int i = getUShort(paramInt);
    paramInt += 2;
    DnsName dnsName = decodeName(paramInt);
    return i + " " + dnsName;
  }
  
  private String decodeSoa(int paramInt) {
    DnsName dnsName1 = new DnsName();
    paramInt = decodeName(paramInt, dnsName1);
    DnsName dnsName2 = new DnsName();
    paramInt = decodeName(paramInt, dnsName2);
    long l1 = getUInt(paramInt);
    paramInt += 4;
    long l2 = getUInt(paramInt);
    paramInt += 4;
    long l3 = getUInt(paramInt);
    paramInt += 4;
    long l4 = getUInt(paramInt);
    paramInt += 4;
    long l5 = getUInt(paramInt);
    paramInt += 4;
    return dnsName1 + " " + dnsName2 + " " + l1 + " " + l2 + " " + l3 + " " + l4 + " " + l5;
  }
  
  private String decodeSrv(int paramInt) {
    int i = getUShort(paramInt);
    paramInt += 2;
    int j = getUShort(paramInt);
    paramInt += 2;
    int k = getUShort(paramInt);
    paramInt += 2;
    DnsName dnsName = decodeName(paramInt);
    return i + " " + j + " " + k + " " + dnsName;
  }
  
  private String decodeNaptr(int paramInt) {
    int i = getUShort(paramInt);
    paramInt += 2;
    int j = getUShort(paramInt);
    paramInt += 2;
    StringBuffer stringBuffer1 = new StringBuffer();
    paramInt += decodeCharString(paramInt, stringBuffer1);
    StringBuffer stringBuffer2 = new StringBuffer();
    paramInt += decodeCharString(paramInt, stringBuffer2);
    StringBuffer stringBuffer3 = new StringBuffer(this.rdlen);
    paramInt += decodeCharString(paramInt, stringBuffer3);
    DnsName dnsName = decodeName(paramInt);
    return i + " " + j + " " + stringBuffer1 + " " + stringBuffer2 + " " + stringBuffer3 + " " + dnsName;
  }
  
  private String decodeTxt(int paramInt) {
    StringBuffer stringBuffer = new StringBuffer(this.rdlen);
    int i = paramInt + this.rdlen;
    while (paramInt < i) {
      paramInt += decodeCharString(paramInt, stringBuffer);
      if (paramInt < i)
        stringBuffer.append(' '); 
    } 
    return stringBuffer.toString();
  }
  
  private String decodeHinfo(int paramInt) {
    StringBuffer stringBuffer = new StringBuffer(this.rdlen);
    paramInt += decodeCharString(paramInt, stringBuffer);
    stringBuffer.append(' ');
    paramInt += decodeCharString(paramInt, stringBuffer);
    return stringBuffer.toString();
  }
  
  private int decodeCharString(int paramInt, StringBuffer paramStringBuffer) {
    int i = paramStringBuffer.length();
    int j = getUByte(paramInt++);
    boolean bool = (j == 0) ? 1 : 0;
    for (byte b = 0; b < j; b++) {
      int k = getUByte(paramInt++);
      bool |= ((k == 32) ? 1 : 0);
      if (k == 92 || k == 34) {
        bool = true;
        paramStringBuffer.append('\\');
      } 
      paramStringBuffer.append((char)k);
    } 
    if (bool) {
      paramStringBuffer.insert(i, '"');
      paramStringBuffer.append('"');
    } 
    return j + 1;
  }
  
  private String decodeA(int paramInt) { return (this.msg[paramInt] & 0xFF) + "." + (this.msg[paramInt + 1] & 0xFF) + "." + (this.msg[paramInt + 2] & 0xFF) + "." + (this.msg[paramInt + 3] & 0xFF); }
  
  private String decodeAAAA(int paramInt) {
    int[] arrayOfInt = new int[8];
    byte b1;
    for (b1 = 0; b1 < 8; b1++) {
      arrayOfInt[b1] = getUShort(paramInt);
      paramInt += 2;
    } 
    b1 = -1;
    byte b2 = 0;
    byte b3 = -1;
    byte b4 = 0;
    byte b5;
    for (b5 = 0; b5 < 8; b5++) {
      if (arrayOfInt[b5] == 0) {
        if (b1 == -1) {
          b1 = b5;
          b2 = 1;
        } else if (++b2 >= 2 && b2 > b4) {
          b3 = b1;
          b4 = b2;
        } 
      } else {
        b1 = -1;
      } 
    } 
    if (b3 == 0) {
      if (b4 == 6 || (b4 == 7 && arrayOfInt[7] > 1))
        return "::" + decodeA(paramInt - 4); 
      if (b4 == 5 && arrayOfInt[5] == 65535)
        return "::ffff:" + decodeA(paramInt - 4); 
    } 
    b5 = (b3 != -1) ? 1 : 0;
    StringBuffer stringBuffer = new StringBuffer(40);
    if (b3 == 0)
      stringBuffer.append(':'); 
    for (byte b6 = 0; b6 < 8; b6++) {
      if (b5 == 0 || b6 < b3 || b6 >= b3 + b4) {
        stringBuffer.append(Integer.toHexString(arrayOfInt[b6]));
        if (b6 < 7)
          stringBuffer.append(':'); 
      } else if (b5 != 0 && b6 == b3) {
        stringBuffer.append(':');
      } 
    } 
    return stringBuffer.toString();
  }
  
  private static void dprint(String paramString) {}
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\jndi\dns\ResourceRecord.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */