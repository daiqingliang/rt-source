package com.sun.xml.internal.org.jvnet.mimepull;

import java.io.IOException;
import java.util.List;
import java.util.NoSuchElementException;

final class InternetHeaders {
  private final FinalArrayList<Hdr> headers = new FinalArrayList();
  
  InternetHeaders(MIMEParser.LineInputStream paramLineInputStream) {
    String str = null;
    StringBuilder stringBuilder = new StringBuilder();
    try {
      String str1;
      do {
        str1 = paramLineInputStream.readLine();
        if (str1 != null && (str1.startsWith(" ") || str1.startsWith("\t"))) {
          if (str != null) {
            stringBuilder.append(str);
            str = null;
          } 
          stringBuilder.append("\r\n");
          stringBuilder.append(str1);
        } else {
          if (str != null) {
            addHeaderLine(str);
          } else if (stringBuilder.length() > 0) {
            addHeaderLine(stringBuilder.toString());
            stringBuilder.setLength(0);
          } 
          str = str1;
        } 
      } while (str1 != null && str1.length() > 0);
    } catch (IOException iOException) {
      throw new MIMEParsingException("Error in input stream", iOException);
    } 
  }
  
  List<String> getHeader(String paramString) {
    FinalArrayList finalArrayList = new FinalArrayList();
    int i = this.headers.size();
    for (byte b = 0; b < i; b++) {
      Hdr hdr = (Hdr)this.headers.get(b);
      if (paramString.equalsIgnoreCase(hdr.name))
        finalArrayList.add(hdr.getValue()); 
    } 
    return (finalArrayList.size() == 0) ? null : finalArrayList;
  }
  
  FinalArrayList<? extends Header> getAllHeaders() { return this.headers; }
  
  void addHeaderLine(String paramString) {
    try {
      char c = paramString.charAt(0);
      if (c == ' ' || c == '\t') {
        Hdr hdr = (Hdr)this.headers.get(this.headers.size() - 1);
        hdr.line += "\r\n" + paramString;
      } else {
        this.headers.add(new Hdr(paramString));
      } 
    } catch (StringIndexOutOfBoundsException stringIndexOutOfBoundsException) {
    
    } catch (NoSuchElementException noSuchElementException) {}
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\org\jvnet\mimepull\InternetHeaders.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */