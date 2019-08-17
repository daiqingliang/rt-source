package com.sun.xml.internal.bind.v2.runtime.output;

import com.sun.xml.internal.bind.marshaller.NoEscapeHandler;
import com.sun.xml.internal.bind.v2.runtime.JAXBContextImpl;
import com.sun.xml.internal.bind.v2.runtime.Name;
import com.sun.xml.internal.bind.v2.runtime.XMLSerializer;
import com.sun.xml.internal.bind.v2.runtime.unmarshaller.Base64Data;
import com.sun.xml.internal.fastinfoset.stax.StAXDocumentSerializer;
import com.sun.xml.internal.org.jvnet.fastinfoset.VocabularyApplicationData;
import java.io.IOException;
import java.util.Collection;
import java.util.Map;
import java.util.WeakHashMap;
import javax.xml.bind.JAXBContext;
import javax.xml.stream.XMLStreamException;
import org.xml.sax.SAXException;

public final class FastInfosetStreamWriterOutput extends XMLStreamWriterOutput {
  private final StAXDocumentSerializer fiout;
  
  private final Encoded[] localNames;
  
  private final TablesPerJAXBContext tables;
  
  public FastInfosetStreamWriterOutput(StAXDocumentSerializer paramStAXDocumentSerializer, JAXBContextImpl paramJAXBContextImpl) {
    super(paramStAXDocumentSerializer, NoEscapeHandler.theInstance);
    this.fiout = paramStAXDocumentSerializer;
    this.localNames = paramJAXBContextImpl.getUTF8NameTable();
    VocabularyApplicationData vocabularyApplicationData = this.fiout.getVocabularyApplicationData();
    AppData appData = null;
    if (vocabularyApplicationData == null || !(vocabularyApplicationData instanceof AppData)) {
      appData = new AppData();
      this.fiout.setVocabularyApplicationData(appData);
    } else {
      appData = (AppData)vocabularyApplicationData;
    } 
    TablesPerJAXBContext tablesPerJAXBContext = (TablesPerJAXBContext)appData.contexts.get(paramJAXBContextImpl);
    if (tablesPerJAXBContext != null) {
      this.tables = tablesPerJAXBContext;
      this.tables.clearOrResetTables(paramStAXDocumentSerializer.getLocalNameIndex());
    } else {
      this.tables = new TablesPerJAXBContext(paramJAXBContextImpl, paramStAXDocumentSerializer.getLocalNameIndex());
      appData.contexts.put(paramJAXBContextImpl, this.tables);
    } 
  }
  
  public void startDocument(XMLSerializer paramXMLSerializer, boolean paramBoolean, int[] paramArrayOfInt, NamespaceContextImpl paramNamespaceContextImpl) throws IOException, SAXException, XMLStreamException {
    super.startDocument(paramXMLSerializer, paramBoolean, paramArrayOfInt, paramNamespaceContextImpl);
    if (paramBoolean)
      this.fiout.initiateLowLevelWriting(); 
  }
  
  public void endDocument(boolean paramBoolean) throws IOException, SAXException, XMLStreamException { super.endDocument(paramBoolean); }
  
  public void beginStartTag(Name paramName) throws IOException {
    this.fiout.writeLowLevelTerminationAndMark();
    if (this.nsContext.getCurrent().count() == 0) {
      int i = this.tables.elementIndexes[paramName.qNameIndex] - this.tables.indexOffset;
      int j = this.nsUriIndex2prefixIndex[paramName.nsUriIndex];
      if (i >= 0 && this.tables.elementIndexPrefixes[paramName.qNameIndex] == j) {
        this.fiout.writeLowLevelStartElementIndexed(0, i);
      } else {
        this.tables.elementIndexes[paramName.qNameIndex] = this.fiout.getNextElementIndex() + this.tables.indexOffset;
        this.tables.elementIndexPrefixes[paramName.qNameIndex] = j;
        writeLiteral(60, paramName, this.nsContext.getPrefix(j), this.nsContext.getNamespaceURI(j));
      } 
    } else {
      beginStartTagWithNamespaces(paramName);
    } 
  }
  
  public void beginStartTagWithNamespaces(Name paramName) throws IOException {
    NamespaceContextImpl.Element element = this.nsContext.getCurrent();
    this.fiout.writeLowLevelStartNamespaces();
    int i;
    for (i = element.count() - 1; i >= 0; i--) {
      String str = element.getNsUri(i);
      if (str.length() != 0 || element.getBase() != 1)
        this.fiout.writeLowLevelNamespace(element.getPrefix(i), str); 
    } 
    this.fiout.writeLowLevelEndNamespaces();
    i = this.tables.elementIndexes[paramName.qNameIndex] - this.tables.indexOffset;
    int j = this.nsUriIndex2prefixIndex[paramName.nsUriIndex];
    if (i >= 0 && this.tables.elementIndexPrefixes[paramName.qNameIndex] == j) {
      this.fiout.writeLowLevelStartElementIndexed(0, i);
    } else {
      this.tables.elementIndexes[paramName.qNameIndex] = this.fiout.getNextElementIndex() + this.tables.indexOffset;
      this.tables.elementIndexPrefixes[paramName.qNameIndex] = j;
      writeLiteral(60, paramName, this.nsContext.getPrefix(j), this.nsContext.getNamespaceURI(j));
    } 
  }
  
  public void attribute(Name paramName, String paramString) throws IOException {
    this.fiout.writeLowLevelStartAttributes();
    int i = this.tables.attributeIndexes[paramName.qNameIndex] - this.tables.indexOffset;
    if (i >= 0) {
      this.fiout.writeLowLevelAttributeIndexed(i);
    } else {
      this.tables.attributeIndexes[paramName.qNameIndex] = this.fiout.getNextAttributeIndex() + this.tables.indexOffset;
      short s = paramName.nsUriIndex;
      if (s == -1) {
        writeLiteral(120, paramName, "", "");
      } else {
        int j = this.nsUriIndex2prefixIndex[s];
        writeLiteral(120, paramName, this.nsContext.getPrefix(j), this.nsContext.getNamespaceURI(j));
      } 
    } 
    this.fiout.writeLowLevelAttributeValue(paramString);
  }
  
  private void writeLiteral(int paramInt, Name paramName, String paramString1, String paramString2) throws IOException {
    int i = this.tables.localNameIndexes[paramName.localNameIndex] - this.tables.indexOffset;
    if (i < 0) {
      this.tables.localNameIndexes[paramName.localNameIndex] = this.fiout.getNextLocalNameIndex() + this.tables.indexOffset;
      this.fiout.writeLowLevelStartNameLiteral(paramInt, paramString1, (this.localNames[paramName.localNameIndex]).buf, paramString2);
    } else {
      this.fiout.writeLowLevelStartNameLiteral(paramInt, paramString1, i, paramString2);
    } 
  }
  
  public void endStartTag() throws IOException { this.fiout.writeLowLevelEndStartElement(); }
  
  public void endTag(Name paramName) throws IOException { this.fiout.writeLowLevelEndElement(); }
  
  public void endTag(int paramInt, String paramString) throws IOException { this.fiout.writeLowLevelEndElement(); }
  
  public void text(Pcdata paramPcdata, boolean paramBoolean) throws IOException {
    if (paramBoolean)
      this.fiout.writeLowLevelText(" "); 
    if (!(paramPcdata instanceof Base64Data)) {
      int i = paramPcdata.length();
      if (i < this.buf.length) {
        paramPcdata.writeTo(this.buf, 0);
        this.fiout.writeLowLevelText(this.buf, i);
      } else {
        this.fiout.writeLowLevelText(paramPcdata.toString());
      } 
    } else {
      Base64Data base64Data = (Base64Data)paramPcdata;
      this.fiout.writeLowLevelOctets(base64Data.get(), base64Data.getDataLen());
    } 
  }
  
  public void text(String paramString, boolean paramBoolean) throws IOException {
    if (paramBoolean)
      this.fiout.writeLowLevelText(" "); 
    this.fiout.writeLowLevelText(paramString);
  }
  
  public void beginStartTag(int paramInt, String paramString) throws IOException {
    this.fiout.writeLowLevelTerminationAndMark();
    byte b = 0;
    if (this.nsContext.getCurrent().count() > 0) {
      NamespaceContextImpl.Element element = this.nsContext.getCurrent();
      this.fiout.writeLowLevelStartNamespaces();
      for (int i = element.count() - 1; i >= 0; i--) {
        String str = element.getNsUri(i);
        if (str.length() != 0 || element.getBase() != 1)
          this.fiout.writeLowLevelNamespace(element.getPrefix(i), str); 
      } 
      this.fiout.writeLowLevelEndNamespaces();
      b = 0;
    } 
    boolean bool = this.fiout.writeLowLevelStartElement(b, this.nsContext.getPrefix(paramInt), paramString, this.nsContext.getNamespaceURI(paramInt));
    if (!bool)
      this.tables.incrementMaxIndexValue(); 
  }
  
  public void attribute(int paramInt, String paramString1, String paramString2) throws IOException {
    boolean bool;
    this.fiout.writeLowLevelStartAttributes();
    if (paramInt == -1) {
      bool = this.fiout.writeLowLevelAttribute("", "", paramString1);
    } else {
      bool = this.fiout.writeLowLevelAttribute(this.nsContext.getPrefix(paramInt), this.nsContext.getNamespaceURI(paramInt), paramString1);
    } 
    if (!bool)
      this.tables.incrementMaxIndexValue(); 
    this.fiout.writeLowLevelAttributeValue(paramString2);
  }
  
  static final class AppData implements VocabularyApplicationData {
    final Map<JAXBContext, FastInfosetStreamWriterOutput.TablesPerJAXBContext> contexts = new WeakHashMap();
    
    final Collection<FastInfosetStreamWriterOutput.TablesPerJAXBContext> collectionOfContexts = this.contexts.values();
    
    public void clear() throws IOException {
      for (FastInfosetStreamWriterOutput.TablesPerJAXBContext tablesPerJAXBContext : this.collectionOfContexts)
        tablesPerJAXBContext.requireClearTables(); 
    }
  }
  
  static final class TablesPerJAXBContext {
    final int[] elementIndexes;
    
    final int[] elementIndexPrefixes;
    
    final int[] attributeIndexes;
    
    final int[] localNameIndexes;
    
    int indexOffset;
    
    int maxIndex;
    
    boolean requiresClear;
    
    TablesPerJAXBContext(JAXBContextImpl param1JAXBContextImpl, int param1Int) {
      this.elementIndexes = new int[param1JAXBContextImpl.getNumberOfElementNames()];
      this.elementIndexPrefixes = new int[param1JAXBContextImpl.getNumberOfElementNames()];
      this.attributeIndexes = new int[param1JAXBContextImpl.getNumberOfAttributeNames()];
      this.localNameIndexes = new int[param1JAXBContextImpl.getNumberOfLocalNames()];
      this.indexOffset = 1;
      this.maxIndex = param1Int + this.elementIndexes.length + this.attributeIndexes.length;
    }
    
    public void requireClearTables() throws IOException { this.requiresClear = true; }
    
    public void clearOrResetTables(int param1Int) {
      if (this.requiresClear) {
        this.requiresClear = false;
        this.indexOffset += this.maxIndex;
        this.maxIndex = param1Int + this.elementIndexes.length + this.attributeIndexes.length;
        if (this.indexOffset + this.maxIndex < 0)
          clearAll(); 
      } else {
        this.maxIndex = param1Int + this.elementIndexes.length + this.attributeIndexes.length;
        if (this.indexOffset + this.maxIndex < 0)
          resetAll(); 
      } 
    }
    
    private void clearAll() throws IOException {
      clear(this.elementIndexes);
      clear(this.attributeIndexes);
      clear(this.localNameIndexes);
      this.indexOffset = 1;
    }
    
    private void clear(int[] param1ArrayOfInt) {
      for (byte b = 0; b < param1ArrayOfInt.length; b++)
        param1ArrayOfInt[b] = 0; 
    }
    
    public void incrementMaxIndexValue() throws IOException {
      this.maxIndex++;
      if (this.indexOffset + this.maxIndex < 0)
        resetAll(); 
    }
    
    private void resetAll() throws IOException {
      clear(this.elementIndexes);
      clear(this.attributeIndexes);
      clear(this.localNameIndexes);
      this.indexOffset = 1;
    }
    
    private void reset(int[] param1ArrayOfInt) {
      for (byte b = 0; b < param1ArrayOfInt.length; b++) {
        if (param1ArrayOfInt[b] > this.indexOffset) {
          param1ArrayOfInt[b] = param1ArrayOfInt[b] - this.indexOffset + 1;
        } else {
          param1ArrayOfInt[b] = 0;
        } 
      } 
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\bind\v2\runtime\output\FastInfosetStreamWriterOutput.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */