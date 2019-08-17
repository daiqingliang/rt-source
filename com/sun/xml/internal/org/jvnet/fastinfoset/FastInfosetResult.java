package com.sun.xml.internal.org.jvnet.fastinfoset;

import com.sun.xml.internal.fastinfoset.sax.SAXDocumentSerializer;
import java.io.OutputStream;
import javax.xml.transform.sax.SAXResult;
import org.xml.sax.ContentHandler;
import org.xml.sax.ext.LexicalHandler;

public class FastInfosetResult extends SAXResult {
  OutputStream _outputStream;
  
  public FastInfosetResult(OutputStream paramOutputStream) { this._outputStream = paramOutputStream; }
  
  public ContentHandler getHandler() {
    ContentHandler contentHandler = super.getHandler();
    if (contentHandler == null) {
      contentHandler = new SAXDocumentSerializer();
      setHandler(contentHandler);
    } 
    ((SAXDocumentSerializer)contentHandler).setOutputStream(this._outputStream);
    return contentHandler;
  }
  
  public LexicalHandler getLexicalHandler() { return (LexicalHandler)getHandler(); }
  
  public OutputStream getOutputStream() { return this._outputStream; }
  
  public void setOutputStream(OutputStream paramOutputStream) { this._outputStream = paramOutputStream; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\org\jvnet\fastinfoset\FastInfosetResult.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */