package com.sun.org.apache.xerces.internal.impl.dv.xs;

import com.sun.org.apache.xerces.internal.impl.dv.InvalidDatatypeValueException;
import com.sun.org.apache.xerces.internal.impl.dv.ValidationContext;
import com.sun.org.apache.xerces.internal.util.URI;
import java.io.UnsupportedEncodingException;

public class AnyURIDV extends TypeValidator {
  private static final URI BASE_URI;
  
  private static boolean[] gNeedEscaping;
  
  private static char[] gAfterEscaping1;
  
  private static char[] gAfterEscaping2;
  
  private static char[] gHexChs;
  
  public short getAllowedFacets() { return 2079; }
  
  public Object getActualValue(String paramString, ValidationContext paramValidationContext) throws InvalidDatatypeValueException {
    try {
      if (paramString.length() != 0) {
        String str = encode(paramString);
        new URI(BASE_URI, str);
      } 
    } catch (com.sun.org.apache.xerces.internal.util.URI.MalformedURIException malformedURIException) {
      throw new InvalidDatatypeValueException("cvc-datatype-valid.1.2.1", new Object[] { paramString, "anyURI" });
    } 
    return paramString;
  }
  
  private static String encode(String paramString) {
    int i = paramString.length();
    StringBuffer stringBuffer = new StringBuffer(i * 3);
    byte b;
    for (b = 0; b < i; b++) {
      char c = paramString.charAt(b);
      if (c >= '')
        break; 
      if (gNeedEscaping[c]) {
        stringBuffer.append('%');
        stringBuffer.append(gAfterEscaping1[c]);
        stringBuffer.append(gAfterEscaping2[c]);
      } else {
        stringBuffer.append((char)c);
      } 
    } 
    if (b < i) {
      byte[] arrayOfByte = null;
      try {
        arrayOfByte = paramString.substring(b).getBytes("UTF-8");
      } catch (UnsupportedEncodingException unsupportedEncodingException) {
        return paramString;
      } 
      i = arrayOfByte.length;
      for (b = 0; b < i; b++) {
        byte b1 = arrayOfByte[b];
        if (b1 < 0) {
          byte b2 = b1 + 256;
          stringBuffer.append('%');
          stringBuffer.append(gHexChs[b2 >> 4]);
          stringBuffer.append(gHexChs[b2 & 0xF]);
        } else if (gNeedEscaping[b1]) {
          stringBuffer.append('%');
          stringBuffer.append(gAfterEscaping1[b1]);
          stringBuffer.append(gAfterEscaping2[b1]);
        } else {
          stringBuffer.append((char)b1);
        } 
      } 
    } 
    return (stringBuffer.length() != i) ? stringBuffer.toString() : paramString;
  }
  
  static  {
    URI uRI = null;
    try {
      uRI = new URI("abc://def.ghi.jkl");
    } catch (com.sun.org.apache.xerces.internal.util.URI.MalformedURIException malformedURIException) {}
    BASE_URI = uRI;
    gNeedEscaping = new boolean[128];
    gAfterEscaping1 = new char[128];
    gAfterEscaping2 = new char[128];
    gHexChs = new char[] { 
        '0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 
        'A', 'B', 'C', 'D', 'E', 'F' };
    for (byte b = 0; b <= 31; b++) {
      gNeedEscaping[b] = true;
      gAfterEscaping1[b] = gHexChs[b >> 4];
      gAfterEscaping2[b] = gHexChs[b & 0xF];
    } 
    gNeedEscaping[127] = true;
    gAfterEscaping1[127] = '7';
    gAfterEscaping2[127] = 'F';
    for (char c : new char[] { 
        ' ', '<', '>', '"', '{', '}', '|', '\\', '^', '~', 
        '`' }) {
      gNeedEscaping[c] = true;
      gAfterEscaping1[c] = gHexChs[c >> '\004'];
      gAfterEscaping2[c] = gHexChs[c & 0xF];
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xerces\internal\impl\dv\xs\AnyURIDV.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */