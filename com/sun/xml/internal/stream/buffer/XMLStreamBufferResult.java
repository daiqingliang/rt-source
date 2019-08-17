package com.sun.xml.internal.stream.buffer;

import com.sun.xml.internal.stream.buffer.sax.SAXBufferCreator;
import javax.xml.transform.sax.SAXResult;
import org.xml.sax.ContentHandler;
import org.xml.sax.ext.LexicalHandler;

public class XMLStreamBufferResult extends SAXResult {
  protected MutableXMLStreamBuffer _buffer;
  
  protected SAXBufferCreator _bufferCreator;
  
  public XMLStreamBufferResult() { setXMLStreamBuffer(new MutableXMLStreamBuffer()); }
  
  public XMLStreamBufferResult(MutableXMLStreamBuffer paramMutableXMLStreamBuffer) { setXMLStreamBuffer(paramMutableXMLStreamBuffer); }
  
  public MutableXMLStreamBuffer getXMLStreamBuffer() { return this._buffer; }
  
  public void setXMLStreamBuffer(MutableXMLStreamBuffer paramMutableXMLStreamBuffer) {
    if (paramMutableXMLStreamBuffer == null)
      throw new NullPointerException("buffer cannot be null"); 
    this._buffer = paramMutableXMLStreamBuffer;
    setSystemId(this._buffer.getSystemId());
    if (this._bufferCreator != null)
      this._bufferCreator.setXMLStreamBuffer(this._buffer); 
  }
  
  public ContentHandler getHandler() {
    if (this._bufferCreator == null) {
      this._bufferCreator = new SAXBufferCreator(this._buffer);
      setHandler(this._bufferCreator);
    } else if (super.getHandler() == null) {
      setHandler(this._bufferCreator);
    } 
    return this._bufferCreator;
  }
  
  public LexicalHandler getLexicalHandler() { return (LexicalHandler)getHandler(); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\stream\buffer\XMLStreamBufferResult.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */