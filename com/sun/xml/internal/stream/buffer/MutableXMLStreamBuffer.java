package com.sun.xml.internal.stream.buffer;

import com.sun.xml.internal.stream.buffer.sax.SAXBufferCreator;
import com.sun.xml.internal.stream.buffer.stax.StreamReaderBufferCreator;
import com.sun.xml.internal.stream.buffer.stax.StreamWriterBufferCreator;
import java.io.IOException;
import java.io.InputStream;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;

public class MutableXMLStreamBuffer extends XMLStreamBuffer {
  public static final int DEFAULT_ARRAY_SIZE = 512;
  
  public MutableXMLStreamBuffer() { this(512); }
  
  public void setSystemId(String paramString) { this.systemId = paramString; }
  
  public MutableXMLStreamBuffer(int paramInt) {
    this._structure = new FragmentedArray(new byte[paramInt]);
    this._structureStrings = new FragmentedArray(new String[paramInt]);
    this._contentCharactersBuffer = new FragmentedArray(new char[4096]);
    this._contentObjects = new FragmentedArray(new Object[paramInt]);
    (byte[])this._structure.getArray()[0] = -112;
  }
  
  public void createFromXMLStreamReader(XMLStreamReader paramXMLStreamReader) throws XMLStreamException {
    reset();
    StreamReaderBufferCreator streamReaderBufferCreator = new StreamReaderBufferCreator(this);
    streamReaderBufferCreator.create(paramXMLStreamReader);
  }
  
  public XMLStreamWriter createFromXMLStreamWriter() {
    reset();
    return new StreamWriterBufferCreator(this);
  }
  
  public SAXBufferCreator createFromSAXBufferCreator() {
    reset();
    SAXBufferCreator sAXBufferCreator = new SAXBufferCreator();
    sAXBufferCreator.setBuffer(this);
    return sAXBufferCreator;
  }
  
  public void createFromXMLReader(XMLReader paramXMLReader, InputStream paramInputStream) throws SAXException, IOException { createFromXMLReader(paramXMLReader, paramInputStream, null); }
  
  public void createFromXMLReader(XMLReader paramXMLReader, InputStream paramInputStream, String paramString) throws SAXException, IOException {
    reset();
    SAXBufferCreator sAXBufferCreator = new SAXBufferCreator(this);
    paramXMLReader.setContentHandler(sAXBufferCreator);
    paramXMLReader.setDTDHandler(sAXBufferCreator);
    paramXMLReader.setProperty("http://xml.org/sax/properties/lexical-handler", sAXBufferCreator);
    sAXBufferCreator.create(paramXMLReader, paramInputStream, paramString);
  }
  
  public void reset() {
    this._structurePtr = this._structureStringsPtr = this._contentCharactersBufferPtr = this._contentObjectsPtr = 0;
    (byte[])this._structure.getArray()[0] = -112;
    this._contentObjects.setNext(null);
    Object[] arrayOfObject = (Object[])this._contentObjects.getArray();
    for (byte b = 0; b < arrayOfObject.length && arrayOfObject[b] != null; b++)
      arrayOfObject[b] = null; 
    this.treeCount = 0;
  }
  
  protected void setHasInternedStrings(boolean paramBoolean) { this._hasInternedStrings = paramBoolean; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\stream\buffer\MutableXMLStreamBuffer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */