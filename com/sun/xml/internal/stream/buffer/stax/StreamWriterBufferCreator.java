package com.sun.xml.internal.stream.buffer.stax;

import com.sun.xml.internal.org.jvnet.staxex.Base64Data;
import com.sun.xml.internal.org.jvnet.staxex.NamespaceContextEx;
import com.sun.xml.internal.org.jvnet.staxex.XMLStreamWriterEx;
import com.sun.xml.internal.stream.buffer.MutableXMLStreamBuffer;
import java.io.OutputStream;
import javax.activation.DataHandler;
import javax.xml.namespace.NamespaceContext;
import javax.xml.stream.XMLStreamException;

public class StreamWriterBufferCreator extends StreamBufferCreator implements XMLStreamWriterEx {
  private final NamespaceContexHelper namespaceContext = new NamespaceContexHelper();
  
  private int depth = 0;
  
  public StreamWriterBufferCreator() { setXMLStreamBuffer(new MutableXMLStreamBuffer()); }
  
  public StreamWriterBufferCreator(MutableXMLStreamBuffer paramMutableXMLStreamBuffer) { setXMLStreamBuffer(paramMutableXMLStreamBuffer); }
  
  public Object getProperty(String paramString) throws IllegalArgumentException { return null; }
  
  public void close() {}
  
  public void flush() {}
  
  public NamespaceContextEx getNamespaceContext() { return this.namespaceContext; }
  
  public void setNamespaceContext(NamespaceContext paramNamespaceContext) throws XMLStreamException { throw new UnsupportedOperationException(); }
  
  public void setDefaultNamespace(String paramString) throws XMLStreamException { setPrefix("", paramString); }
  
  public void setPrefix(String paramString1, String paramString2) throws XMLStreamException { this.namespaceContext.declareNamespace(paramString1, paramString2); }
  
  public String getPrefix(String paramString) throws XMLStreamException { return this.namespaceContext.getPrefix(paramString); }
  
  public void writeStartDocument() { writeStartDocument("", ""); }
  
  public void writeStartDocument(String paramString) throws XMLStreamException { writeStartDocument("", ""); }
  
  public void writeStartDocument(String paramString1, String paramString2) throws XMLStreamException {
    this.namespaceContext.resetContexts();
    storeStructure(16);
  }
  
  public void writeEndDocument() { storeStructure(144); }
  
  public void writeStartElement(String paramString) throws XMLStreamException {
    this.namespaceContext.pushContext();
    this.depth++;
    String str = this.namespaceContext.getNamespaceURI("");
    if (str == null) {
      storeQualifiedName(32, null, null, paramString);
    } else {
      storeQualifiedName(32, null, str, paramString);
    } 
  }
  
  public void writeStartElement(String paramString1, String paramString2) throws XMLStreamException {
    this.namespaceContext.pushContext();
    this.depth++;
    String str = this.namespaceContext.getPrefix(paramString1);
    if (str == null)
      throw new XMLStreamException(); 
    this.namespaceContext.pushContext();
    storeQualifiedName(32, str, paramString1, paramString2);
  }
  
  public void writeStartElement(String paramString1, String paramString2, String paramString3) throws XMLStreamException {
    this.namespaceContext.pushContext();
    this.depth++;
    storeQualifiedName(32, paramString1, paramString3, paramString2);
  }
  
  public void writeEmptyElement(String paramString) throws XMLStreamException {
    writeStartElement(paramString);
    writeEndElement();
  }
  
  public void writeEmptyElement(String paramString1, String paramString2) throws XMLStreamException {
    writeStartElement(paramString1, paramString2);
    writeEndElement();
  }
  
  public void writeEmptyElement(String paramString1, String paramString2, String paramString3) throws XMLStreamException {
    writeStartElement(paramString1, paramString2, paramString3);
    writeEndElement();
  }
  
  public void writeEndElement() {
    this.namespaceContext.popContext();
    storeStructure(144);
    if (--this.depth == 0)
      increaseTreeCount(); 
  }
  
  public void writeDefaultNamespace(String paramString) throws XMLStreamException { storeNamespaceAttribute(null, paramString); }
  
  public void writeNamespace(String paramString1, String paramString2) throws XMLStreamException {
    if ("xmlns".equals(paramString1))
      paramString1 = null; 
    storeNamespaceAttribute(paramString1, paramString2);
  }
  
  public void writeAttribute(String paramString1, String paramString2) throws XMLStreamException { storeAttribute(null, null, paramString1, "CDATA", paramString2); }
  
  public void writeAttribute(String paramString1, String paramString2, String paramString3) throws XMLStreamException {
    String str = this.namespaceContext.getPrefix(paramString1);
    if (str == null)
      throw new XMLStreamException(); 
    writeAttribute(str, paramString1, paramString2, paramString3);
  }
  
  public void writeAttribute(String paramString1, String paramString2, String paramString3, String paramString4) throws XMLStreamException { storeAttribute(paramString1, paramString2, paramString3, "CDATA", paramString4); }
  
  public void writeCData(String paramString) throws XMLStreamException {
    storeStructure(88);
    storeContentString(paramString);
  }
  
  public void writeCharacters(String paramString) throws XMLStreamException {
    storeStructure(88);
    storeContentString(paramString);
  }
  
  public void writeCharacters(char[] paramArrayOfChar, int paramInt1, int paramInt2) throws XMLStreamException { storeContentCharacters(80, paramArrayOfChar, paramInt1, paramInt2); }
  
  public void writeComment(String paramString) throws XMLStreamException {
    storeStructure(104);
    storeContentString(paramString);
  }
  
  public void writeDTD(String paramString) throws XMLStreamException {}
  
  public void writeEntityRef(String paramString) throws XMLStreamException {
    storeStructure(128);
    storeContentString(paramString);
  }
  
  public void writeProcessingInstruction(String paramString) throws XMLStreamException { writeProcessingInstruction(paramString, ""); }
  
  public void writeProcessingInstruction(String paramString1, String paramString2) throws XMLStreamException { storeProcessingInstruction(paramString1, paramString2); }
  
  public void writePCDATA(CharSequence paramCharSequence) throws XMLStreamException {
    if (paramCharSequence instanceof Base64Data) {
      storeStructure(92);
      storeContentObject(((Base64Data)paramCharSequence).clone());
    } else {
      writeCharacters(paramCharSequence.toString());
    } 
  }
  
  public void writeBinary(byte[] paramArrayOfByte, int paramInt1, int paramInt2, String paramString) throws XMLStreamException {
    Base64Data base64Data = new Base64Data();
    byte[] arrayOfByte = new byte[paramInt2];
    System.arraycopy(paramArrayOfByte, paramInt1, arrayOfByte, 0, paramInt2);
    base64Data.set(arrayOfByte, paramInt2, null, true);
    storeStructure(92);
    storeContentObject(base64Data);
  }
  
  public void writeBinary(DataHandler paramDataHandler) throws XMLStreamException {
    Base64Data base64Data = new Base64Data();
    base64Data.set(paramDataHandler);
    storeStructure(92);
    storeContentObject(base64Data);
  }
  
  public OutputStream writeBinary(String paramString) throws XMLStreamException { throw new UnsupportedOperationException(); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\stream\buffer\stax\StreamWriterBufferCreator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */