package com.sun.org.apache.xerces.internal.util;

import com.sun.org.apache.xerces.internal.xni.parser.XMLInputSource;
import java.io.InputStream;
import java.io.Reader;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

public final class SAXInputSource extends XMLInputSource {
  private XMLReader fXMLReader;
  
  private InputSource fInputSource;
  
  public SAXInputSource() { this(null); }
  
  public SAXInputSource(InputSource paramInputSource) { this(null, paramInputSource); }
  
  public SAXInputSource(XMLReader paramXMLReader, InputSource paramInputSource) {
    super((paramInputSource != null) ? paramInputSource.getPublicId() : null, (paramInputSource != null) ? paramInputSource.getSystemId() : null, null);
    if (paramInputSource != null) {
      setByteStream(paramInputSource.getByteStream());
      setCharacterStream(paramInputSource.getCharacterStream());
      setEncoding(paramInputSource.getEncoding());
    } 
    this.fInputSource = paramInputSource;
    this.fXMLReader = paramXMLReader;
  }
  
  public void setXMLReader(XMLReader paramXMLReader) { this.fXMLReader = paramXMLReader; }
  
  public XMLReader getXMLReader() { return this.fXMLReader; }
  
  public void setInputSource(InputSource paramInputSource) {
    if (paramInputSource != null) {
      setPublicId(paramInputSource.getPublicId());
      setSystemId(paramInputSource.getSystemId());
      setByteStream(paramInputSource.getByteStream());
      setCharacterStream(paramInputSource.getCharacterStream());
      setEncoding(paramInputSource.getEncoding());
    } else {
      setPublicId(null);
      setSystemId(null);
      setByteStream(null);
      setCharacterStream(null);
      setEncoding(null);
    } 
    this.fInputSource = paramInputSource;
  }
  
  public InputSource getInputSource() { return this.fInputSource; }
  
  public void setPublicId(String paramString) {
    super.setPublicId(paramString);
    if (this.fInputSource == null)
      this.fInputSource = new InputSource(); 
    this.fInputSource.setPublicId(paramString);
  }
  
  public void setSystemId(String paramString) {
    super.setSystemId(paramString);
    if (this.fInputSource == null)
      this.fInputSource = new InputSource(); 
    this.fInputSource.setSystemId(paramString);
  }
  
  public void setByteStream(InputStream paramInputStream) {
    super.setByteStream(paramInputStream);
    if (this.fInputSource == null)
      this.fInputSource = new InputSource(); 
    this.fInputSource.setByteStream(paramInputStream);
  }
  
  public void setCharacterStream(Reader paramReader) {
    super.setCharacterStream(paramReader);
    if (this.fInputSource == null)
      this.fInputSource = new InputSource(); 
    this.fInputSource.setCharacterStream(paramReader);
  }
  
  public void setEncoding(String paramString) {
    super.setEncoding(paramString);
    if (this.fInputSource == null)
      this.fInputSource = new InputSource(); 
    this.fInputSource.setEncoding(paramString);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xerces\interna\\util\SAXInputSource.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */