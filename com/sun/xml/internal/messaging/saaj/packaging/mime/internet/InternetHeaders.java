package com.sun.xml.internal.messaging.saaj.packaging.mime.internet;

import com.sun.xml.internal.messaging.saaj.packaging.mime.MessagingException;
import com.sun.xml.internal.messaging.saaj.packaging.mime.util.LineInputStream;
import com.sun.xml.internal.messaging.saaj.util.FinalArrayList;
import java.io.IOException;
import java.io.InputStream;
import java.util.AbstractList;
import java.util.List;
import java.util.NoSuchElementException;

public final class InternetHeaders {
  private final FinalArrayList headers = new FinalArrayList();
  
  private List headerValueView;
  
  public InternetHeaders() {}
  
  public InternetHeaders(InputStream paramInputStream) throws MessagingException { load(paramInputStream); }
  
  public void load(InputStream paramInputStream) throws MessagingException {
    LineInputStream lineInputStream = new LineInputStream(paramInputStream);
    String str = null;
    StringBuffer stringBuffer = new StringBuffer();
    try {
      String str1;
      do {
        str1 = lineInputStream.readLine();
        if (str1 != null && (str1.startsWith(" ") || str1.startsWith("\t"))) {
          if (str != null) {
            stringBuffer.append(str);
            str = null;
          } 
          stringBuffer.append("\r\n");
          stringBuffer.append(str1);
        } else {
          if (str != null) {
            addHeaderLine(str);
          } else if (stringBuffer.length() > 0) {
            addHeaderLine(stringBuffer.toString());
            stringBuffer.setLength(0);
          } 
          str = str1;
        } 
      } while (str1 != null && str1.length() > 0);
    } catch (IOException iOException) {
      throw new MessagingException("Error in input stream", iOException);
    } 
  }
  
  public String[] getHeader(String paramString) {
    FinalArrayList finalArrayList = new FinalArrayList();
    int i = this.headers.size();
    for (byte b = 0; b < i; b++) {
      hdr hdr = (hdr)this.headers.get(b);
      if (paramString.equalsIgnoreCase(hdr.name))
        finalArrayList.add(hdr.getValue()); 
    } 
    return (finalArrayList.size() == 0) ? null : (String[])finalArrayList.toArray(new String[finalArrayList.size()]);
  }
  
  public String getHeader(String paramString1, String paramString2) {
    String[] arrayOfString = getHeader(paramString1);
    if (arrayOfString == null)
      return null; 
    if (arrayOfString.length == 1 || paramString2 == null)
      return arrayOfString[0]; 
    StringBuffer stringBuffer = new StringBuffer(arrayOfString[0]);
    for (byte b = 1; b < arrayOfString.length; b++) {
      stringBuffer.append(paramString2);
      stringBuffer.append(arrayOfString[b]);
    } 
    return stringBuffer.toString();
  }
  
  public void setHeader(String paramString1, String paramString2) {
    boolean bool = false;
    for (byte b = 0; b < this.headers.size(); b++) {
      hdr hdr = (hdr)this.headers.get(b);
      if (paramString1.equalsIgnoreCase(hdr.name))
        if (!bool) {
          int i;
          if (hdr.line != null && (i = hdr.line.indexOf(':')) >= 0) {
            hdr.line = hdr.line.substring(0, i + 1) + " " + paramString2;
          } else {
            hdr.line = paramString1 + ": " + paramString2;
          } 
          bool = true;
        } else {
          this.headers.remove(b);
          b--;
        }  
    } 
    if (!bool)
      addHeader(paramString1, paramString2); 
  }
  
  public void addHeader(String paramString1, String paramString2) {
    int i = this.headers.size();
    for (int j = this.headers.size() - 1; j >= 0; j--) {
      hdr hdr = (hdr)this.headers.get(j);
      if (paramString1.equalsIgnoreCase(hdr.name)) {
        this.headers.add(j + 1, new hdr(paramString1, paramString2));
        return;
      } 
      if (hdr.name.equals(":"))
        i = j; 
    } 
    this.headers.add(i, new hdr(paramString1, paramString2));
  }
  
  public void removeHeader(String paramString) {
    for (byte b = 0; b < this.headers.size(); b++) {
      hdr hdr = (hdr)this.headers.get(b);
      if (paramString.equalsIgnoreCase(hdr.name)) {
        this.headers.remove(b);
        b--;
      } 
    } 
  }
  
  public FinalArrayList getAllHeaders() { return this.headers; }
  
  public void addHeaderLine(String paramString) {
    try {
      char c = paramString.charAt(0);
      if (c == ' ' || c == '\t') {
        hdr hdr = (hdr)this.headers.get(this.headers.size() - 1);
        hdr.line += "\r\n" + paramString;
      } else {
        this.headers.add(new hdr(paramString));
      } 
    } catch (StringIndexOutOfBoundsException stringIndexOutOfBoundsException) {
      return;
    } catch (NoSuchElementException noSuchElementException) {}
  }
  
  public List getAllHeaderLines() {
    if (this.headerValueView == null)
      this.headerValueView = new AbstractList() {
          public Object get(int param1Int) { return ((hdr)this.this$0.headers.get(param1Int)).line; }
          
          public int size() { return InternetHeaders.this.headers.size(); }
        }; 
    return this.headerValueView;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\messaging\saaj\packaging\mime\internet\InternetHeaders.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */