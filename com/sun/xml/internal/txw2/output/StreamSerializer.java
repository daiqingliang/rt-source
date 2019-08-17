package com.sun.xml.internal.txw2.output;

import com.sun.xml.internal.txw2.TxwException;
import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import javax.xml.transform.stream.StreamResult;
import org.xml.sax.ContentHandler;
import org.xml.sax.ext.LexicalHandler;

public class StreamSerializer implements XmlSerializer {
  private final SaxSerializer serializer;
  
  private final XMLWriter writer;
  
  public StreamSerializer(OutputStream paramOutputStream) { this(createWriter(paramOutputStream)); }
  
  public StreamSerializer(OutputStream paramOutputStream, String paramString) throws UnsupportedEncodingException { this(createWriter(paramOutputStream, paramString)); }
  
  public StreamSerializer(Writer paramWriter) { this(new StreamResult(paramWriter)); }
  
  public StreamSerializer(StreamResult paramStreamResult) {
    final OutputStream[] autoClose = new OutputStream[1];
    if (paramStreamResult.getWriter() != null) {
      this.writer = createWriter(paramStreamResult.getWriter());
    } else if (paramStreamResult.getOutputStream() != null) {
      this.writer = createWriter(paramStreamResult.getOutputStream());
    } else if (paramStreamResult.getSystemId() != null) {
      String str = paramStreamResult.getSystemId();
      str = convertURL(str);
      try {
        FileOutputStream fileOutputStream = new FileOutputStream(str);
        arrayOfOutputStream[0] = fileOutputStream;
        this.writer = createWriter(fileOutputStream);
      } catch (IOException iOException) {
        throw new TxwException(iOException);
      } 
    } else {
      throw new IllegalArgumentException();
    } 
    this.serializer = new SaxSerializer(this.writer, this.writer, false) {
        public void endDocument() {
          super.endDocument();
          if (autoClose[false] != null) {
            try {
              autoClose[0].close();
            } catch (IOException iOException) {
              throw new TxwException(iOException);
            } 
            autoClose[0] = null;
          } 
        }
      };
  }
  
  private StreamSerializer(XMLWriter paramXMLWriter) {
    this.writer = paramXMLWriter;
    this.serializer = new SaxSerializer(paramXMLWriter, paramXMLWriter, false);
  }
  
  private String convertURL(String paramString) {
    paramString = paramString.replace('\\', '/');
    paramString = paramString.replaceAll("//", "/");
    paramString = paramString.replaceAll("//", "/");
    if (paramString.startsWith("file:/"))
      if (paramString.substring(6).indexOf(":") > 0) {
        paramString = paramString.substring(6);
      } else {
        paramString = paramString.substring(5);
      }  
    return paramString;
  }
  
  public void startDocument() { this.serializer.startDocument(); }
  
  public void beginStartTag(String paramString1, String paramString2, String paramString3) { this.serializer.beginStartTag(paramString1, paramString2, paramString3); }
  
  public void writeAttribute(String paramString1, String paramString2, String paramString3, StringBuilder paramStringBuilder) { this.serializer.writeAttribute(paramString1, paramString2, paramString3, paramStringBuilder); }
  
  public void writeXmlns(String paramString1, String paramString2) { this.serializer.writeXmlns(paramString1, paramString2); }
  
  public void endStartTag(String paramString1, String paramString2, String paramString3) { this.serializer.endStartTag(paramString1, paramString2, paramString3); }
  
  public void endTag() { this.serializer.endTag(); }
  
  public void text(StringBuilder paramStringBuilder) { this.serializer.text(paramStringBuilder); }
  
  public void cdata(StringBuilder paramStringBuilder) { this.serializer.cdata(paramStringBuilder); }
  
  public void comment(StringBuilder paramStringBuilder) { this.serializer.comment(paramStringBuilder); }
  
  public void endDocument() { this.serializer.endDocument(); }
  
  public void flush() {
    this.serializer.flush();
    try {
      this.writer.flush();
    } catch (IOException iOException) {
      throw new TxwException(iOException);
    } 
  }
  
  private static XMLWriter createWriter(Writer paramWriter) {
    DataWriter dataWriter = new DataWriter(new BufferedWriter(paramWriter));
    dataWriter.setIndentStep("  ");
    return dataWriter;
  }
  
  private static XMLWriter createWriter(OutputStream paramOutputStream, String paramString) throws UnsupportedEncodingException {
    XMLWriter xMLWriter = createWriter(new OutputStreamWriter(paramOutputStream, paramString));
    xMLWriter.setEncoding(paramString);
    return xMLWriter;
  }
  
  private static XMLWriter createWriter(OutputStream paramOutputStream) {
    try {
      return createWriter(paramOutputStream, "UTF-8");
    } catch (UnsupportedEncodingException unsupportedEncodingException) {
      throw new Error(unsupportedEncodingException);
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\txw2\output\StreamSerializer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */