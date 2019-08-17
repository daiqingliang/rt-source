package com.sun.org.apache.xerces.internal.dom;

import java.io.InputStream;
import java.io.Reader;
import org.w3c.dom.ls.LSInput;

public class DOMInputImpl implements LSInput {
  protected String fPublicId = null;
  
  protected String fSystemId = null;
  
  protected String fBaseSystemId = null;
  
  protected InputStream fByteStream = null;
  
  protected Reader fCharStream = null;
  
  protected String fData = null;
  
  protected String fEncoding = null;
  
  protected boolean fCertifiedText = false;
  
  public DOMInputImpl() {}
  
  public DOMInputImpl(String paramString1, String paramString2, String paramString3) {
    this.fPublicId = paramString1;
    this.fSystemId = paramString2;
    this.fBaseSystemId = paramString3;
  }
  
  public DOMInputImpl(String paramString1, String paramString2, String paramString3, InputStream paramInputStream, String paramString4) {
    this.fPublicId = paramString1;
    this.fSystemId = paramString2;
    this.fBaseSystemId = paramString3;
    this.fByteStream = paramInputStream;
    this.fEncoding = paramString4;
  }
  
  public DOMInputImpl(String paramString1, String paramString2, String paramString3, Reader paramReader, String paramString4) {
    this.fPublicId = paramString1;
    this.fSystemId = paramString2;
    this.fBaseSystemId = paramString3;
    this.fCharStream = paramReader;
    this.fEncoding = paramString4;
  }
  
  public DOMInputImpl(String paramString1, String paramString2, String paramString3, String paramString4, String paramString5) {
    this.fPublicId = paramString1;
    this.fSystemId = paramString2;
    this.fBaseSystemId = paramString3;
    this.fData = paramString4;
    this.fEncoding = paramString5;
  }
  
  public InputStream getByteStream() { return this.fByteStream; }
  
  public void setByteStream(InputStream paramInputStream) { this.fByteStream = paramInputStream; }
  
  public Reader getCharacterStream() { return this.fCharStream; }
  
  public void setCharacterStream(Reader paramReader) { this.fCharStream = paramReader; }
  
  public String getStringData() { return this.fData; }
  
  public void setStringData(String paramString) { this.fData = paramString; }
  
  public String getEncoding() { return this.fEncoding; }
  
  public void setEncoding(String paramString) { this.fEncoding = paramString; }
  
  public String getPublicId() { return this.fPublicId; }
  
  public void setPublicId(String paramString) { this.fPublicId = paramString; }
  
  public String getSystemId() { return this.fSystemId; }
  
  public void setSystemId(String paramString) { this.fSystemId = paramString; }
  
  public String getBaseURI() { return this.fBaseSystemId; }
  
  public void setBaseURI(String paramString) { this.fBaseSystemId = paramString; }
  
  public boolean getCertifiedText() { return this.fCertifiedText; }
  
  public void setCertifiedText(boolean paramBoolean) { this.fCertifiedText = paramBoolean; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xerces\internal\dom\DOMInputImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */