package com.sun.xml.internal.stream.buffer.stax;

import com.sun.xml.internal.org.jvnet.staxex.Base64Data;
import com.sun.xml.internal.org.jvnet.staxex.XMLStreamWriterEx;
import com.sun.xml.internal.stream.buffer.AbstractProcessor;
import com.sun.xml.internal.stream.buffer.XMLStreamBuffer;
import java.io.IOException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

public class StreamWriterBufferProcessor extends AbstractProcessor {
  public StreamWriterBufferProcessor() {}
  
  public StreamWriterBufferProcessor(XMLStreamBuffer paramXMLStreamBuffer) { setXMLStreamBuffer(paramXMLStreamBuffer, paramXMLStreamBuffer.isFragment()); }
  
  public StreamWriterBufferProcessor(XMLStreamBuffer paramXMLStreamBuffer, boolean paramBoolean) { setXMLStreamBuffer(paramXMLStreamBuffer, paramBoolean); }
  
  public final void process(XMLStreamBuffer paramXMLStreamBuffer, XMLStreamWriter paramXMLStreamWriter) throws XMLStreamException {
    setXMLStreamBuffer(paramXMLStreamBuffer, paramXMLStreamBuffer.isFragment());
    process(paramXMLStreamWriter);
  }
  
  public void process(XMLStreamWriter paramXMLStreamWriter) throws XMLStreamException {
    if (this._fragmentMode) {
      writeFragment(paramXMLStreamWriter);
    } else {
      write(paramXMLStreamWriter);
    } 
  }
  
  public void setXMLStreamBuffer(XMLStreamBuffer paramXMLStreamBuffer) { setBuffer(paramXMLStreamBuffer); }
  
  public void setXMLStreamBuffer(XMLStreamBuffer paramXMLStreamBuffer, boolean paramBoolean) { setBuffer(paramXMLStreamBuffer, paramBoolean); }
  
  public void write(XMLStreamWriter paramXMLStreamWriter) throws XMLStreamException {
    int i;
    if (!this._fragmentMode) {
      if (this._treeCount > 1)
        throw new IllegalStateException("forest cannot be written as a full infoset"); 
      paramXMLStreamWriter.writeStartDocument();
    } 
    while (true) {
      String str;
      int k;
      char[] arrayOfChar;
      int j;
      i = getEIIState(peekStructure());
      paramXMLStreamWriter.flush();
      switch (i) {
        case 1:
          readStructure();
          continue;
        case 3:
        case 4:
        case 5:
        case 6:
          writeFragment(paramXMLStreamWriter);
          continue;
        case 12:
          readStructure();
          j = readStructure();
          k = readContentCharactersBuffer(j);
          str = new String(this._contentCharactersBuffer, k, j);
          paramXMLStreamWriter.writeComment(str);
          continue;
        case 13:
          readStructure();
          j = readStructure16();
          k = readContentCharactersBuffer(j);
          str = new String(this._contentCharactersBuffer, k, j);
          paramXMLStreamWriter.writeComment(str);
          continue;
        case 14:
          readStructure();
          arrayOfChar = readContentCharactersCopy();
          paramXMLStreamWriter.writeComment(new String(arrayOfChar));
          continue;
        case 16:
          readStructure();
          paramXMLStreamWriter.writeProcessingInstruction(readStructureString(), readStructureString());
          continue;
        case 17:
          readStructure();
          paramXMLStreamWriter.writeEndDocument();
          return;
      } 
      break;
    } 
    throw new XMLStreamException("Invalid State " + i);
  }
  
  public void writeFragment(XMLStreamWriter paramXMLStreamWriter) throws XMLStreamException {
    if (paramXMLStreamWriter instanceof XMLStreamWriterEx) {
      writeFragmentEx((XMLStreamWriterEx)paramXMLStreamWriter);
    } else {
      writeFragmentNoEx(paramXMLStreamWriter);
    } 
  }
  
  public void writeFragmentEx(XMLStreamWriterEx paramXMLStreamWriterEx) throws XMLStreamException {
    byte b = 0;
    int i = getEIIState(peekStructure());
    if (i == 1)
      readStructure(); 
    do {
      String str4;
      String str3;
      int m;
      int j;
      int k;
      String str2;
      String str1;
      CharSequence charSequence;
      char[] arrayOfChar2;
      char[] arrayOfChar1;
      i = readEiiState();
      switch (i) {
        case 1:
          throw new AssertionError();
        case 3:
          b++;
          str2 = readStructureString();
          str3 = readStructureString();
          str4 = getPrefixFromQName(readStructureString());
          paramXMLStreamWriterEx.writeStartElement(str4, str3, str2);
          writeAttributes(paramXMLStreamWriterEx, isInscope(b));
          break;
        case 4:
          b++;
          str2 = readStructureString();
          str3 = readStructureString();
          str4 = readStructureString();
          paramXMLStreamWriterEx.writeStartElement(str2, str4, str3);
          writeAttributes(paramXMLStreamWriterEx, isInscope(b));
          break;
        case 5:
          b++;
          str2 = readStructureString();
          str3 = readStructureString();
          paramXMLStreamWriterEx.writeStartElement("", str3, str2);
          writeAttributes(paramXMLStreamWriterEx, isInscope(b));
          break;
        case 6:
          b++;
          str2 = readStructureString();
          paramXMLStreamWriterEx.writeStartElement(str2);
          writeAttributes(paramXMLStreamWriterEx, isInscope(b));
          break;
        case 7:
          k = readStructure();
          m = readContentCharactersBuffer(k);
          paramXMLStreamWriterEx.writeCharacters(this._contentCharactersBuffer, m, k);
          break;
        case 8:
          k = readStructure16();
          m = readContentCharactersBuffer(k);
          paramXMLStreamWriterEx.writeCharacters(this._contentCharactersBuffer, m, k);
          break;
        case 9:
          arrayOfChar2 = readContentCharactersCopy();
          paramXMLStreamWriterEx.writeCharacters(arrayOfChar2, 0, arrayOfChar2.length);
          break;
        case 10:
          str1 = readContentString();
          paramXMLStreamWriterEx.writeCharacters(str1);
          break;
        case 11:
          charSequence = (CharSequence)readContentObject();
          paramXMLStreamWriterEx.writePCDATA(charSequence);
          break;
        case 12:
          j = readStructure();
          m = readContentCharactersBuffer(j);
          str4 = new String(this._contentCharactersBuffer, m, j);
          paramXMLStreamWriterEx.writeComment(str4);
          break;
        case 13:
          j = readStructure16();
          m = readContentCharactersBuffer(j);
          str4 = new String(this._contentCharactersBuffer, m, j);
          paramXMLStreamWriterEx.writeComment(str4);
          break;
        case 14:
          arrayOfChar1 = readContentCharactersCopy();
          paramXMLStreamWriterEx.writeComment(new String(arrayOfChar1));
          break;
        case 16:
          paramXMLStreamWriterEx.writeProcessingInstruction(readStructureString(), readStructureString());
          break;
        case 17:
          paramXMLStreamWriterEx.writeEndElement();
          if (--b == 0)
            this._treeCount--; 
          break;
        default:
          throw new XMLStreamException("Invalid State " + i);
      } 
    } while (b > 0 || this._treeCount > 0);
  }
  
  public void writeFragmentNoEx(XMLStreamWriter paramXMLStreamWriter) throws XMLStreamException {
    byte b = 0;
    int i = getEIIState(peekStructure());
    if (i == 1)
      readStructure(); 
    do {
      String str4;
      String str3;
      String str2;
      String str1;
      int j;
      CharSequence charSequence;
      char[] arrayOfChar1;
      int k;
      char[] arrayOfChar2;
      i = readEiiState();
      switch (i) {
        case 1:
          throw new AssertionError();
        case 3:
          b++;
          str2 = readStructureString();
          str3 = readStructureString();
          str4 = getPrefixFromQName(readStructureString());
          paramXMLStreamWriter.writeStartElement(str4, str3, str2);
          writeAttributes(paramXMLStreamWriter, isInscope(b));
          break;
        case 4:
          b++;
          str2 = readStructureString();
          str3 = readStructureString();
          str4 = readStructureString();
          paramXMLStreamWriter.writeStartElement(str2, str4, str3);
          writeAttributes(paramXMLStreamWriter, isInscope(b));
          break;
        case 5:
          b++;
          str2 = readStructureString();
          str3 = readStructureString();
          paramXMLStreamWriter.writeStartElement("", str3, str2);
          writeAttributes(paramXMLStreamWriter, isInscope(b));
          break;
        case 6:
          b++;
          str2 = readStructureString();
          paramXMLStreamWriter.writeStartElement(str2);
          writeAttributes(paramXMLStreamWriter, isInscope(b));
          break;
        case 7:
          k = readStructure();
          m = readContentCharactersBuffer(k);
          paramXMLStreamWriter.writeCharacters(this._contentCharactersBuffer, m, k);
          break;
        case 8:
          k = readStructure16();
          m = readContentCharactersBuffer(k);
          paramXMLStreamWriter.writeCharacters(this._contentCharactersBuffer, m, k);
          break;
        case 9:
          arrayOfChar2 = readContentCharactersCopy();
          paramXMLStreamWriter.writeCharacters(arrayOfChar2, 0, arrayOfChar2.length);
          break;
        case 10:
          str1 = readContentString();
          paramXMLStreamWriter.writeCharacters(str1);
          break;
        case 11:
          charSequence = (CharSequence)readContentObject();
          if (charSequence instanceof Base64Data) {
            try {
              Base64Data base64Data = (Base64Data)charSequence;
              base64Data.writeTo(paramXMLStreamWriter);
            } catch (IOException m) {
              IOException iOException;
              throw new XMLStreamException(iOException);
            } 
            break;
          } 
          paramXMLStreamWriter.writeCharacters(charSequence.toString());
          break;
        case 12:
          j = readStructure();
          m = readContentCharactersBuffer(j);
          str4 = new String(this._contentCharactersBuffer, m, j);
          paramXMLStreamWriter.writeComment(str4);
          break;
        case 13:
          j = readStructure16();
          m = readContentCharactersBuffer(j);
          str4 = new String(this._contentCharactersBuffer, m, j);
          paramXMLStreamWriter.writeComment(str4);
          break;
        case 14:
          arrayOfChar1 = readContentCharactersCopy();
          paramXMLStreamWriter.writeComment(new String(arrayOfChar1));
          break;
        case 16:
          paramXMLStreamWriter.writeProcessingInstruction(readStructureString(), readStructureString());
          break;
        case 17:
          paramXMLStreamWriter.writeEndElement();
          if (--b == 0)
            this._treeCount--; 
          break;
        default:
          throw new XMLStreamException("Invalid State " + i);
      } 
    } while (b > 0 || this._treeCount > 0);
  }
  
  private boolean isInscope(int paramInt) { return (this._buffer.getInscopeNamespaces().size() > 0 && paramInt == 1); }
  
  private void writeAttributes(XMLStreamWriter paramXMLStreamWriter, boolean paramBoolean) throws XMLStreamException {
    HashSet hashSet = paramBoolean ? new HashSet() : Collections.emptySet();
    int i = peekStructure();
    if ((i & 0xF0) == 64)
      i = writeNamespaceAttributes(i, paramXMLStreamWriter, paramBoolean, hashSet); 
    if (paramBoolean)
      writeInscopeNamespaces(paramXMLStreamWriter, hashSet); 
    if ((i & 0xF0) == 48)
      writeAttributes(i, paramXMLStreamWriter); 
  }
  
  private static String fixNull(String paramString) { return (paramString == null) ? "" : paramString; }
  
  private void writeInscopeNamespaces(XMLStreamWriter paramXMLStreamWriter, Set<String> paramSet) throws XMLStreamException {
    for (Map.Entry entry : this._buffer.getInscopeNamespaces().entrySet()) {
      String str = fixNull((String)entry.getKey());
      if (!paramSet.contains(str))
        paramXMLStreamWriter.writeNamespace(str, (String)entry.getValue()); 
    } 
  }
  
  private int writeNamespaceAttributes(int paramInt, XMLStreamWriter paramXMLStreamWriter, boolean paramBoolean, Set<String> paramSet) throws XMLStreamException {
    do {
      String str;
      switch (getNIIState(paramInt)) {
        case 1:
          paramXMLStreamWriter.writeDefaultNamespace("");
          if (paramBoolean)
            paramSet.add(""); 
          break;
        case 2:
          str = readStructureString();
          paramXMLStreamWriter.writeNamespace(str, "");
          if (paramBoolean)
            paramSet.add(str); 
          break;
        case 3:
          str = readStructureString();
          paramXMLStreamWriter.writeNamespace(str, readStructureString());
          if (paramBoolean)
            paramSet.add(str); 
          break;
        case 4:
          paramXMLStreamWriter.writeDefaultNamespace(readStructureString());
          if (paramBoolean)
            paramSet.add(""); 
          break;
      } 
      readStructure();
      paramInt = peekStructure();
    } while ((paramInt & 0xF0) == 64);
    return paramInt;
  }
  
  private void writeAttributes(int paramInt, XMLStreamWriter paramXMLStreamWriter) throws XMLStreamException {
    do {
      String str3;
      String str2;
      String str1;
      switch (getAIIState(paramInt)) {
        case 1:
          str1 = readStructureString();
          str2 = readStructureString();
          str3 = getPrefixFromQName(readStructureString());
          paramXMLStreamWriter.writeAttribute(str3, str1, str2, readContentString());
          break;
        case 2:
          paramXMLStreamWriter.writeAttribute(readStructureString(), readStructureString(), readStructureString(), readContentString());
          break;
        case 3:
          paramXMLStreamWriter.writeAttribute(readStructureString(), readStructureString(), readContentString());
          break;
        case 4:
          paramXMLStreamWriter.writeAttribute(readStructureString(), readContentString());
          break;
      } 
      readStructureString();
      readStructure();
      paramInt = peekStructure();
    } while ((paramInt & 0xF0) == 48);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\stream\buffer\stax\StreamWriterBufferProcessor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */