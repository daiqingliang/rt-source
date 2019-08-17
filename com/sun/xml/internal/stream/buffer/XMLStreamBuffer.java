package com.sun.xml.internal.stream.buffer;

import com.sun.xml.internal.stream.buffer.sax.SAXBufferProcessor;
import com.sun.xml.internal.stream.buffer.stax.StreamReaderBufferProcessor;
import com.sun.xml.internal.stream.buffer.stax.StreamWriterBufferProcessor;
import java.io.IOException;
import java.io.InputStream;
import java.util.Collections;
import java.util.Map;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMResult;
import org.w3c.dom.Node;
import org.xml.sax.ContentHandler;
import org.xml.sax.DTDHandler;
import org.xml.sax.ErrorHandler;
import org.xml.sax.SAXException;
import org.xml.sax.XMLReader;
import org.xml.sax.ext.LexicalHandler;

public abstract class XMLStreamBuffer {
  protected Map<String, String> _inscopeNamespaces = Collections.emptyMap();
  
  protected boolean _hasInternedStrings;
  
  protected FragmentedArray<byte[]> _structure;
  
  protected int _structurePtr;
  
  protected FragmentedArray<String[]> _structureStrings;
  
  protected int _structureStringsPtr;
  
  protected FragmentedArray<char[]> _contentCharactersBuffer;
  
  protected int _contentCharactersBufferPtr;
  
  protected FragmentedArray<Object[]> _contentObjects;
  
  protected int _contentObjectsPtr;
  
  protected int treeCount;
  
  protected String systemId;
  
  private static final ContextClassloaderLocal<TransformerFactory> trnsformerFactory = new ContextClassloaderLocal<TransformerFactory>() {
      protected TransformerFactory initialValue() throws Exception { return TransformerFactory.newInstance(); }
    };
  
  public final boolean isCreated() { return ((byte[])this._structure.getArray()[0] != 144); }
  
  public final boolean isFragment() { return (isCreated() && ((byte[])this._structure.getArray()[this._structurePtr] & 0xF0) != 16); }
  
  public final boolean isElementFragment() { return (isCreated() && ((byte[])this._structure.getArray()[this._structurePtr] & 0xF0) == 32); }
  
  public final boolean isForest() { return (isCreated() && this.treeCount > 1); }
  
  public final String getSystemId() { return this.systemId; }
  
  public final Map<String, String> getInscopeNamespaces() { return this._inscopeNamespaces; }
  
  public final boolean hasInternedStrings() { return this._hasInternedStrings; }
  
  public final StreamReaderBufferProcessor readAsXMLStreamReader() throws XMLStreamException { return new StreamReaderBufferProcessor(this); }
  
  public final void writeToXMLStreamWriter(XMLStreamWriter paramXMLStreamWriter, boolean paramBoolean) throws XMLStreamException {
    StreamWriterBufferProcessor streamWriterBufferProcessor = new StreamWriterBufferProcessor(this, paramBoolean);
    streamWriterBufferProcessor.process(paramXMLStreamWriter);
  }
  
  public final void writeToXMLStreamWriter(XMLStreamWriter paramXMLStreamWriter) throws XMLStreamException { writeToXMLStreamWriter(paramXMLStreamWriter, isFragment()); }
  
  public final SAXBufferProcessor readAsXMLReader() { return new SAXBufferProcessor(this, isFragment()); }
  
  public final SAXBufferProcessor readAsXMLReader(boolean paramBoolean) { return new SAXBufferProcessor(this, paramBoolean); }
  
  public final void writeTo(ContentHandler paramContentHandler, boolean paramBoolean) throws SAXException {
    SAXBufferProcessor sAXBufferProcessor = readAsXMLReader(paramBoolean);
    sAXBufferProcessor.setContentHandler(paramContentHandler);
    if (sAXBufferProcessor instanceof LexicalHandler)
      sAXBufferProcessor.setLexicalHandler((LexicalHandler)paramContentHandler); 
    if (sAXBufferProcessor instanceof DTDHandler)
      sAXBufferProcessor.setDTDHandler((DTDHandler)paramContentHandler); 
    if (sAXBufferProcessor instanceof ErrorHandler)
      sAXBufferProcessor.setErrorHandler((ErrorHandler)paramContentHandler); 
    sAXBufferProcessor.process();
  }
  
  public final void writeTo(ContentHandler paramContentHandler) throws SAXException { writeTo(paramContentHandler, isFragment()); }
  
  public final void writeTo(ContentHandler paramContentHandler, ErrorHandler paramErrorHandler, boolean paramBoolean) throws SAXException {
    SAXBufferProcessor sAXBufferProcessor = readAsXMLReader(paramBoolean);
    sAXBufferProcessor.setContentHandler(paramContentHandler);
    if (sAXBufferProcessor instanceof LexicalHandler)
      sAXBufferProcessor.setLexicalHandler((LexicalHandler)paramContentHandler); 
    if (sAXBufferProcessor instanceof DTDHandler)
      sAXBufferProcessor.setDTDHandler((DTDHandler)paramContentHandler); 
    sAXBufferProcessor.setErrorHandler(paramErrorHandler);
    sAXBufferProcessor.process();
  }
  
  public final void writeTo(ContentHandler paramContentHandler, ErrorHandler paramErrorHandler) throws SAXException { writeTo(paramContentHandler, paramErrorHandler, isFragment()); }
  
  public final Node writeTo(Node paramNode) throws XMLStreamBufferException {
    try {
      Transformer transformer = ((TransformerFactory)trnsformerFactory.get()).newTransformer();
      transformer.transform(new XMLStreamBufferSource(this), new DOMResult(paramNode));
      return paramNode.getLastChild();
    } catch (TransformerException transformerException) {
      throw new XMLStreamBufferException(transformerException);
    } 
  }
  
  public static XMLStreamBuffer createNewBufferFromXMLStreamReader(XMLStreamReader paramXMLStreamReader) throws XMLStreamException {
    MutableXMLStreamBuffer mutableXMLStreamBuffer = new MutableXMLStreamBuffer();
    mutableXMLStreamBuffer.createFromXMLStreamReader(paramXMLStreamReader);
    return mutableXMLStreamBuffer;
  }
  
  public static XMLStreamBuffer createNewBufferFromXMLReader(XMLReader paramXMLReader, InputStream paramInputStream) throws SAXException, IOException {
    MutableXMLStreamBuffer mutableXMLStreamBuffer = new MutableXMLStreamBuffer();
    mutableXMLStreamBuffer.createFromXMLReader(paramXMLReader, paramInputStream);
    return mutableXMLStreamBuffer;
  }
  
  public static XMLStreamBuffer createNewBufferFromXMLReader(XMLReader paramXMLReader, InputStream paramInputStream, String paramString) throws SAXException, IOException {
    MutableXMLStreamBuffer mutableXMLStreamBuffer = new MutableXMLStreamBuffer();
    mutableXMLStreamBuffer.createFromXMLReader(paramXMLReader, paramInputStream, paramString);
    return mutableXMLStreamBuffer;
  }
  
  protected final FragmentedArray<byte[]> getStructure() { return this._structure; }
  
  protected final int getStructurePtr() { return this._structurePtr; }
  
  protected final FragmentedArray<String[]> getStructureStrings() { return this._structureStrings; }
  
  protected final int getStructureStringsPtr() { return this._structureStringsPtr; }
  
  protected final FragmentedArray<char[]> getContentCharactersBuffer() { return this._contentCharactersBuffer; }
  
  protected final int getContentCharactersBufferPtr() { return this._contentCharactersBufferPtr; }
  
  protected final FragmentedArray<Object[]> getContentObjects() { return this._contentObjects; }
  
  protected final int getContentObjectsPtr() { return this._contentObjectsPtr; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\stream\buffer\XMLStreamBuffer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */