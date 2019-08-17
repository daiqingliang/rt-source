package com.sun.org.apache.xerces.internal.xinclude;

import com.sun.org.apache.xerces.internal.impl.XMLEntityManager;
import com.sun.org.apache.xerces.internal.impl.XMLErrorReporter;
import com.sun.org.apache.xerces.internal.impl.io.ASCIIReader;
import com.sun.org.apache.xerces.internal.impl.io.UTF8Reader;
import com.sun.org.apache.xerces.internal.util.EncodingMap;
import com.sun.org.apache.xerces.internal.util.HTTPInputSource;
import com.sun.org.apache.xerces.internal.util.MessageFormatter;
import com.sun.org.apache.xerces.internal.util.XMLChar;
import com.sun.org.apache.xerces.internal.xni.XMLString;
import com.sun.org.apache.xerces.internal.xni.parser.XMLInputSource;
import java.io.BufferedInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.Reader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.Iterator;
import java.util.Locale;
import java.util.Map;

public class XIncludeTextReader {
  private Reader fReader;
  
  private XIncludeHandler fHandler;
  
  private XMLInputSource fSource;
  
  private XMLErrorReporter fErrorReporter;
  
  private XMLString fTempString = new XMLString();
  
  public XIncludeTextReader(XMLInputSource paramXMLInputSource, XIncludeHandler paramXIncludeHandler, int paramInt) throws IOException {
    this.fHandler = paramXIncludeHandler;
    this.fSource = paramXMLInputSource;
    this.fTempString = new XMLString(new char[paramInt + 1], 0, 0);
  }
  
  public void setErrorReporter(XMLErrorReporter paramXMLErrorReporter) { this.fErrorReporter = paramXMLErrorReporter; }
  
  protected Reader getReader(XMLInputSource paramXMLInputSource) throws IOException {
    if (paramXMLInputSource.getCharacterStream() != null)
      return paramXMLInputSource.getCharacterStream(); 
    InputStream inputStream = null;
    String str1 = paramXMLInputSource.getEncoding();
    if (str1 == null)
      str1 = "UTF-8"; 
    if (paramXMLInputSource.getByteStream() != null) {
      inputStream = paramXMLInputSource.getByteStream();
      if (!(inputStream instanceof BufferedInputStream))
        inputStream = new BufferedInputStream(inputStream, this.fTempString.ch.length); 
    } else {
      String str3 = XMLEntityManager.expandSystemId(paramXMLInputSource.getSystemId(), paramXMLInputSource.getBaseSystemId(), false);
      URL uRL = new URL(str3);
      URLConnection uRLConnection = uRL.openConnection();
      if (uRLConnection instanceof HttpURLConnection && paramXMLInputSource instanceof HTTPInputSource) {
        HttpURLConnection httpURLConnection = (HttpURLConnection)uRLConnection;
        HTTPInputSource hTTPInputSource = (HTTPInputSource)paramXMLInputSource;
        Iterator iterator = hTTPInputSource.getHTTPRequestProperties();
        while (iterator.hasNext()) {
          Map.Entry entry = (Map.Entry)iterator.next();
          httpURLConnection.setRequestProperty((String)entry.getKey(), (String)entry.getValue());
        } 
        boolean bool = hTTPInputSource.getFollowHTTPRedirects();
        if (!bool)
          XMLEntityManager.setInstanceFollowRedirects(httpURLConnection, bool); 
      } 
      inputStream = new BufferedInputStream(uRLConnection.getInputStream());
      String str4 = uRLConnection.getContentType();
      int i = (str4 != null) ? str4.indexOf(';') : -1;
      String str5 = null;
      String str6 = null;
      if (i != -1) {
        str5 = str4.substring(0, i).trim();
        str6 = str4.substring(i + 1).trim();
        if (str6.startsWith("charset=")) {
          str6 = str6.substring(8).trim();
          if ((str6.charAt(0) == '"' && str6.charAt(str6.length() - 1) == '"') || (str6.charAt(0) == '\'' && str6.charAt(str6.length() - 1) == '\''))
            str6 = str6.substring(1, str6.length() - 1); 
        } else {
          str6 = null;
        } 
      } else {
        str5 = str4.trim();
      } 
      String str7 = null;
      if (str5.equals("text/xml")) {
        if (str6 != null) {
          str7 = str6;
        } else {
          str7 = "US-ASCII";
        } 
      } else if (str5.equals("application/xml")) {
        if (str6 != null) {
          str7 = str6;
        } else {
          str7 = getEncodingName(inputStream);
        } 
      } else if (str5.endsWith("+xml")) {
        str7 = getEncodingName(inputStream);
      } 
      if (str7 != null)
        str1 = str7; 
    } 
    str1 = str1.toUpperCase(Locale.ENGLISH);
    str1 = consumeBOM(inputStream, str1);
    if (str1.equals("UTF-8"))
      return new UTF8Reader(inputStream, this.fTempString.ch.length, this.fErrorReporter.getMessageFormatter("http://www.w3.org/TR/1998/REC-xml-19980210"), this.fErrorReporter.getLocale()); 
    String str2 = EncodingMap.getIANA2JavaMapping(str1);
    if (str2 == null) {
      MessageFormatter messageFormatter = this.fErrorReporter.getMessageFormatter("http://www.w3.org/TR/1998/REC-xml-19980210");
      Locale locale = this.fErrorReporter.getLocale();
      throw new IOException(messageFormatter.formatMessage(locale, "EncodingDeclInvalid", new Object[] { str1 }));
    } 
    return str2.equals("ASCII") ? new ASCIIReader(inputStream, this.fTempString.ch.length, this.fErrorReporter.getMessageFormatter("http://www.w3.org/TR/1998/REC-xml-19980210"), this.fErrorReporter.getLocale()) : new InputStreamReader(inputStream, str2);
  }
  
  protected String getEncodingName(InputStream paramInputStream) throws IOException {
    byte[] arrayOfByte = new byte[4];
    String str = null;
    paramInputStream.mark(4);
    int i = paramInputStream.read(arrayOfByte, 0, 4);
    paramInputStream.reset();
    if (i == 4)
      str = getEncodingName(arrayOfByte); 
    return str;
  }
  
  protected String consumeBOM(InputStream paramInputStream, String paramString) throws IOException {
    byte[] arrayOfByte = new byte[3];
    int i = 0;
    paramInputStream.mark(3);
    if (paramString.equals("UTF-8")) {
      i = paramInputStream.read(arrayOfByte, 0, 3);
      if (i == 3) {
        byte b1 = arrayOfByte[0] & 0xFF;
        byte b2 = arrayOfByte[1] & 0xFF;
        byte b3 = arrayOfByte[2] & 0xFF;
        if (b1 != 239 || b2 != 187 || b3 != 191)
          paramInputStream.reset(); 
      } else {
        paramInputStream.reset();
      } 
    } else if (paramString.startsWith("UTF-16")) {
      i = paramInputStream.read(arrayOfByte, 0, 2);
      if (i == 2) {
        byte b1 = arrayOfByte[0] & 0xFF;
        byte b2 = arrayOfByte[1] & 0xFF;
        if (b1 == 254 && b2 == 255)
          return "UTF-16BE"; 
        if (b1 == 255 && b2 == 254)
          return "UTF-16LE"; 
      } 
      paramInputStream.reset();
    } 
    return paramString;
  }
  
  protected String getEncodingName(byte[] paramArrayOfByte) {
    byte b1 = paramArrayOfByte[0] & 0xFF;
    byte b2 = paramArrayOfByte[1] & 0xFF;
    if (b1 == 254 && b2 == 255)
      return "UTF-16BE"; 
    if (b1 == 255 && b2 == 254)
      return "UTF-16LE"; 
    byte b3 = paramArrayOfByte[2] & 0xFF;
    if (b1 == 239 && b2 == 187 && b3 == 191)
      return "UTF-8"; 
    byte b4 = paramArrayOfByte[3] & 0xFF;
    return (b1 == 0 && b2 == 0 && b3 == 0 && b4 == 60) ? "ISO-10646-UCS-4" : ((b1 == 60 && b2 == 0 && b3 == 0 && b4 == 0) ? "ISO-10646-UCS-4" : ((b1 == 0 && b2 == 0 && b3 == 60 && b4 == 0) ? "ISO-10646-UCS-4" : ((b1 == 0 && b2 == 60 && b3 == 0 && b4 == 0) ? "ISO-10646-UCS-4" : ((b1 == 0 && b2 == 60 && b3 == 0 && b4 == 63) ? "UTF-16BE" : ((b1 == 60 && b2 == 0 && b3 == 63 && b4 == 0) ? "UTF-16LE" : ((b1 == 76 && b2 == 111 && b3 == 167 && b4 == 148) ? "CP037" : null))))));
  }
  
  public void parse() throws IOException {
    this.fReader = getReader(this.fSource);
    this.fSource = null;
    for (int i = this.fReader.read(this.fTempString.ch, 0, this.fTempString.ch.length - 1); i != -1; i = this.fReader.read(this.fTempString.ch, 0, this.fTempString.ch.length - 1)) {
      for (byte b = 0; b < i; b++) {
        char c = this.fTempString.ch[b];
        if (!isValid(c))
          if (XMLChar.isHighSurrogate(c)) {
            int j;
            if (++b < i) {
              j = this.fTempString.ch[b];
            } else {
              j = this.fReader.read();
              if (j != -1)
                this.fTempString.ch[i++] = (char)j; 
            } 
            if (XMLChar.isLowSurrogate(j)) {
              int k = XMLChar.supplemental(c, (char)j);
              if (!isValid(k))
                this.fErrorReporter.reportError("http://www.w3.org/TR/1998/REC-xml-19980210", "InvalidCharInContent", new Object[] { Integer.toString(k, 16) }(short)2); 
            } else {
              this.fErrorReporter.reportError("http://www.w3.org/TR/1998/REC-xml-19980210", "InvalidCharInContent", new Object[] { Integer.toString(j, 16) }(short)2);
            } 
          } else {
            this.fErrorReporter.reportError("http://www.w3.org/TR/1998/REC-xml-19980210", "InvalidCharInContent", new Object[] { Integer.toString(c, 16) }(short)2);
          }  
      } 
      if (this.fHandler != null && i > 0) {
        this.fTempString.offset = 0;
        this.fTempString.length = i;
        this.fHandler.characters(this.fTempString, this.fHandler.modifyAugmentations(null, true));
      } 
    } 
  }
  
  public void setInputSource(XMLInputSource paramXMLInputSource) { this.fSource = paramXMLInputSource; }
  
  public void close() throws IOException {
    if (this.fReader != null) {
      this.fReader.close();
      this.fReader = null;
    } 
  }
  
  protected boolean isValid(int paramInt) { return XMLChar.isValid(paramInt); }
  
  protected void setBufferSize(int paramInt) {
    if (this.fTempString.ch.length != ++paramInt)
      this.fTempString.ch = new char[paramInt]; 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xerces\internal\xinclude\XIncludeTextReader.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */