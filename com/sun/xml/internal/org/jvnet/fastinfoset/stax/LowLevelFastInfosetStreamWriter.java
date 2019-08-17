package com.sun.xml.internal.org.jvnet.fastinfoset.stax;

import java.io.IOException;
import javax.xml.stream.XMLStreamException;

public interface LowLevelFastInfosetStreamWriter {
  void initiateLowLevelWriting() throws XMLStreamException;
  
  int getNextElementIndex();
  
  int getNextAttributeIndex();
  
  int getLocalNameIndex();
  
  int getNextLocalNameIndex();
  
  void writeLowLevelTerminationAndMark() throws XMLStreamException;
  
  void writeLowLevelStartElementIndexed(int paramInt1, int paramInt2) throws IOException;
  
  boolean writeLowLevelStartElement(int paramInt, String paramString1, String paramString2, String paramString3) throws IOException;
  
  void writeLowLevelStartNamespaces() throws XMLStreamException;
  
  void writeLowLevelNamespace(String paramString1, String paramString2) throws IOException;
  
  void writeLowLevelEndNamespaces() throws XMLStreamException;
  
  void writeLowLevelStartAttributes() throws XMLStreamException;
  
  void writeLowLevelAttributeIndexed(int paramInt) throws IOException;
  
  boolean writeLowLevelAttribute(String paramString1, String paramString2, String paramString3) throws IOException;
  
  void writeLowLevelAttributeValue(String paramString) throws IOException;
  
  void writeLowLevelStartNameLiteral(int paramInt, String paramString1, byte[] paramArrayOfByte, String paramString2) throws IOException;
  
  void writeLowLevelStartNameLiteral(int paramInt1, String paramString1, int paramInt2, String paramString2) throws IOException;
  
  void writeLowLevelEndStartElement() throws XMLStreamException;
  
  void writeLowLevelEndElement() throws XMLStreamException;
  
  void writeLowLevelText(char[] paramArrayOfChar, int paramInt) throws IOException;
  
  void writeLowLevelText(String paramString) throws IOException;
  
  void writeLowLevelOctets(byte[] paramArrayOfByte, int paramInt) throws IOException;
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\org\jvnet\fastinfoset\stax\LowLevelFastInfosetStreamWriter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */