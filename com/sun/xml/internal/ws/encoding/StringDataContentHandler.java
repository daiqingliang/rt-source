package com.sun.xml.internal.ws.encoding;

import java.awt.datatransfer.DataFlavor;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.nio.charset.Charset;
import javax.activation.ActivationDataFlavor;
import javax.activation.DataContentHandler;
import javax.activation.DataSource;

public class StringDataContentHandler implements DataContentHandler {
  private static final ActivationDataFlavor myDF = new ActivationDataFlavor(String.class, "text/plain", "Text String");
  
  protected ActivationDataFlavor getDF() { return myDF; }
  
  public DataFlavor[] getTransferDataFlavors() { return new DataFlavor[] { getDF() }; }
  
  public Object getTransferData(DataFlavor paramDataFlavor, DataSource paramDataSource) throws IOException { return getDF().equals(paramDataFlavor) ? getContent(paramDataSource) : null; }
  
  public Object getContent(DataSource paramDataSource) throws IOException {
    String str = null;
    try {
      str = getCharset(paramDataSource.getContentType());
      inputStreamReader = new InputStreamReader(paramDataSource.getInputStream(), str);
    } catch (IllegalArgumentException illegalArgumentException) {
      throw new UnsupportedEncodingException(str);
    } 
    try {
      int i = 0;
      char[] arrayOfChar = new char[1024];
      int j;
      while ((j = inputStreamReader.read(arrayOfChar, i, arrayOfChar.length - i)) != -1) {
        i += j;
        if (i >= arrayOfChar.length) {
          int k = arrayOfChar.length;
          if (k < 262144) {
            k += k;
          } else {
            k += 262144;
          } 
          char[] arrayOfChar1 = new char[k];
          System.arraycopy(arrayOfChar, 0, arrayOfChar1, 0, i);
          arrayOfChar = arrayOfChar1;
        } 
      } 
      return new String(arrayOfChar, 0, i);
    } finally {
      try {
        inputStreamReader.close();
      } catch (IOException iOException) {}
    } 
  }
  
  public void writeTo(Object paramObject, String paramString, OutputStream paramOutputStream) throws IOException {
    OutputStreamWriter outputStreamWriter;
    if (!(paramObject instanceof String))
      throw new IOException("\"" + getDF().getMimeType() + "\" DataContentHandler requires String object, was given object of type " + paramObject.getClass().toString()); 
    String str1 = null;
    try {
      str1 = getCharset(paramString);
      outputStreamWriter = new OutputStreamWriter(paramOutputStream, str1);
    } catch (IllegalArgumentException illegalArgumentException) {
      throw new UnsupportedEncodingException(str1);
    } 
    String str2 = (String)paramObject;
    outputStreamWriter.write(str2, 0, str2.length());
    outputStreamWriter.flush();
  }
  
  private String getCharset(String paramString) {
    try {
      ContentType contentType = new ContentType(paramString);
      String str = contentType.getParameter("charset");
      if (str == null)
        str = "us-ascii"; 
      return Charset.forName(str).name();
    } catch (Exception exception) {
      return null;
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\encoding\StringDataContentHandler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */