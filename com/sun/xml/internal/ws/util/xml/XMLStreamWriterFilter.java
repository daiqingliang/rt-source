package com.sun.xml.internal.ws.util.xml;

import com.sun.xml.internal.ws.api.streaming.XMLStreamWriterFactory;
import javax.xml.namespace.NamespaceContext;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

public class XMLStreamWriterFilter implements XMLStreamWriter, XMLStreamWriterFactory.RecycleAware {
  protected XMLStreamWriter writer;
  
  public XMLStreamWriterFilter(XMLStreamWriter paramXMLStreamWriter) { this.writer = paramXMLStreamWriter; }
  
  public void close() throws XMLStreamException { this.writer.close(); }
  
  public void flush() throws XMLStreamException { this.writer.flush(); }
  
  public void writeEndDocument() throws XMLStreamException { this.writer.writeEndDocument(); }
  
  public void writeEndElement() throws XMLStreamException { this.writer.writeEndElement(); }
  
  public void writeStartDocument() throws XMLStreamException { this.writer.writeStartDocument(); }
  
  public void writeCharacters(char[] paramArrayOfChar, int paramInt1, int paramInt2) throws XMLStreamException { this.writer.writeCharacters(paramArrayOfChar, paramInt1, paramInt2); }
  
  public void setDefaultNamespace(String paramString) throws XMLStreamException { this.writer.setDefaultNamespace(paramString); }
  
  public void writeCData(String paramString) throws XMLStreamException { this.writer.writeCData(paramString); }
  
  public void writeCharacters(String paramString) throws XMLStreamException { this.writer.writeCharacters(paramString); }
  
  public void writeComment(String paramString) throws XMLStreamException { this.writer.writeComment(paramString); }
  
  public void writeDTD(String paramString) throws XMLStreamException { this.writer.writeDTD(paramString); }
  
  public void writeDefaultNamespace(String paramString) throws XMLStreamException { this.writer.writeDefaultNamespace(paramString); }
  
  public void writeEmptyElement(String paramString) throws XMLStreamException { this.writer.writeEmptyElement(paramString); }
  
  public void writeEntityRef(String paramString) throws XMLStreamException { this.writer.writeEntityRef(paramString); }
  
  public void writeProcessingInstruction(String paramString) throws XMLStreamException { this.writer.writeProcessingInstruction(paramString); }
  
  public void writeStartDocument(String paramString) throws XMLStreamException { this.writer.writeStartDocument(paramString); }
  
  public void writeStartElement(String paramString) throws XMLStreamException { this.writer.writeStartElement(paramString); }
  
  public NamespaceContext getNamespaceContext() { return this.writer.getNamespaceContext(); }
  
  public void setNamespaceContext(NamespaceContext paramNamespaceContext) throws XMLStreamException { this.writer.setNamespaceContext(paramNamespaceContext); }
  
  public Object getProperty(String paramString) throws IllegalArgumentException { return this.writer.getProperty(paramString); }
  
  public String getPrefix(String paramString) throws XMLStreamException { return this.writer.getPrefix(paramString); }
  
  public void setPrefix(String paramString1, String paramString2) throws XMLStreamException { this.writer.setPrefix(paramString1, paramString2); }
  
  public void writeAttribute(String paramString1, String paramString2) throws XMLStreamException { this.writer.writeAttribute(paramString1, paramString2); }
  
  public void writeEmptyElement(String paramString1, String paramString2) throws XMLStreamException { this.writer.writeEmptyElement(paramString1, paramString2); }
  
  public void writeNamespace(String paramString1, String paramString2) throws XMLStreamException { this.writer.writeNamespace(paramString1, paramString2); }
  
  public void writeProcessingInstruction(String paramString1, String paramString2) throws XMLStreamException { this.writer.writeProcessingInstruction(paramString1, paramString2); }
  
  public void writeStartDocument(String paramString1, String paramString2) throws XMLStreamException { this.writer.writeStartDocument(paramString1, paramString2); }
  
  public void writeStartElement(String paramString1, String paramString2) throws XMLStreamException { this.writer.writeStartElement(paramString1, paramString2); }
  
  public void writeAttribute(String paramString1, String paramString2, String paramString3) throws XMLStreamException { this.writer.writeAttribute(paramString1, paramString2, paramString3); }
  
  public void writeEmptyElement(String paramString1, String paramString2, String paramString3) throws XMLStreamException { this.writer.writeEmptyElement(paramString1, paramString2, paramString3); }
  
  public void writeStartElement(String paramString1, String paramString2, String paramString3) throws XMLStreamException { this.writer.writeStartElement(paramString1, paramString2, paramString3); }
  
  public void writeAttribute(String paramString1, String paramString2, String paramString3, String paramString4) throws XMLStreamException { this.writer.writeAttribute(paramString1, paramString2, paramString3, paramString4); }
  
  public void onRecycled() throws XMLStreamException {
    XMLStreamWriterFactory.recycle(this.writer);
    this.writer = null;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\w\\util\xml\XMLStreamWriterFilter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */