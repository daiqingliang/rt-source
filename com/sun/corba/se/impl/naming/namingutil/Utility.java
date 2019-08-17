package com.sun.corba.se.impl.naming.namingutil;

import com.sun.corba.se.impl.logging.NamingSystemException;
import java.io.StringWriter;
import org.omg.CORBA.DATA_CONVERSION;

class Utility {
  private static NamingSystemException wrapper = NamingSystemException.get("naming");
  
  static String cleanEscapes(String paramString) {
    StringWriter stringWriter = new StringWriter();
    for (byte b = 0; b < paramString.length(); b++) {
      char c = paramString.charAt(b);
      if (c != '%') {
        stringWriter.write(c);
      } else {
        int i = hexOf(paramString.charAt(++b));
        int j = hexOf(paramString.charAt(++b));
        int k = i * 16 + j;
        stringWriter.write((char)k);
      } 
    } 
    return stringWriter.toString();
  }
  
  static int hexOf(char paramChar) {
    char c = paramChar - '0';
    if (c >= '\000' && c <= '\t')
      return c; 
    c = paramChar - 'a' + '\n';
    if (c >= '\n' && c <= '\017')
      return c; 
    c = paramChar - 'A' + '\n';
    if (c >= '\n' && c <= '\017')
      return c; 
    throw new DATA_CONVERSION();
  }
  
  static void validateGIOPVersion(IIOPEndpointInfo paramIIOPEndpointInfo) {
    if (paramIIOPEndpointInfo.getMajor() > 1 || paramIIOPEndpointInfo.getMinor() > 2)
      throw wrapper.insBadAddress(); 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\impl\naming\namingutil\Utility.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */