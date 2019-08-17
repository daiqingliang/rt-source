package com.sun.xml.internal.stream.buffer;

import com.sun.xml.internal.stream.buffer.sax.SAXBufferProcessor;
import java.io.ByteArrayInputStream;
import javax.xml.transform.sax.SAXSource;
import org.xml.sax.InputSource;
import org.xml.sax.XMLReader;

public class XMLStreamBufferSource extends SAXSource {
  protected XMLStreamBuffer _buffer;
  
  protected SAXBufferProcessor _bufferProcessor;
  
  public XMLStreamBufferSource(XMLStreamBuffer paramXMLStreamBuffer) {
    super(new InputSource(new ByteArrayInputStream(new byte[0])));
    setXMLStreamBuffer(paramXMLStreamBuffer);
  }
  
  public XMLStreamBuffer getXMLStreamBuffer() { return this._buffer; }
  
  public void setXMLStreamBuffer(XMLStreamBuffer paramXMLStreamBuffer) {
    if (paramXMLStreamBuffer == null)
      throw new NullPointerException("buffer cannot be null"); 
    this._buffer = paramXMLStreamBuffer;
    if (this._bufferProcessor != null)
      this._bufferProcessor.setBuffer(this._buffer, false); 
  }
  
  public XMLReader getXMLReader() {
    if (this._bufferProcessor == null) {
      this._bufferProcessor = new SAXBufferProcessor(this._buffer, false);
      setXMLReader(this._bufferProcessor);
    } else if (super.getXMLReader() == null) {
      setXMLReader(this._bufferProcessor);
    } 
    return this._bufferProcessor;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\stream\buffer\XMLStreamBufferSource.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */