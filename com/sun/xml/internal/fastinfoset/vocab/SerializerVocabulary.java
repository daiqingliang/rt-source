package com.sun.xml.internal.fastinfoset.vocab;

import com.sun.xml.internal.fastinfoset.QualifiedName;
import com.sun.xml.internal.fastinfoset.util.CharArrayIntMap;
import com.sun.xml.internal.fastinfoset.util.FixedEntryStringIntMap;
import com.sun.xml.internal.fastinfoset.util.KeyIntMap;
import com.sun.xml.internal.fastinfoset.util.LocalNameQualifiedNamesMap;
import com.sun.xml.internal.fastinfoset.util.StringIntMap;
import com.sun.xml.internal.org.jvnet.fastinfoset.Vocabulary;
import java.util.Iterator;
import javax.xml.namespace.QName;

public class SerializerVocabulary extends Vocabulary {
  public final StringIntMap restrictedAlphabet;
  
  public final StringIntMap encodingAlgorithm;
  
  public final StringIntMap namespaceName;
  
  public final StringIntMap prefix;
  
  public final StringIntMap localName;
  
  public final StringIntMap otherNCName;
  
  public final StringIntMap otherURI;
  
  public final StringIntMap attributeValue;
  
  public final CharArrayIntMap otherString;
  
  public final CharArrayIntMap characterContentChunk;
  
  public final LocalNameQualifiedNamesMap elementName;
  
  public final LocalNameQualifiedNamesMap attributeName;
  
  public final KeyIntMap[] tables = new KeyIntMap[12];
  
  protected boolean _useLocalNameAsKey;
  
  protected SerializerVocabulary _readOnlyVocabulary;
  
  public SerializerVocabulary() {
    this.tables[0] = this.restrictedAlphabet = new StringIntMap(4);
    this.tables[1] = this.encodingAlgorithm = new StringIntMap(4);
    this.tables[2] = this.prefix = new FixedEntryStringIntMap("xml", 8);
    this.tables[3] = this.namespaceName = new FixedEntryStringIntMap("http://www.w3.org/XML/1998/namespace", 8);
    this.tables[4] = this.localName = new StringIntMap();
    this.tables[5] = this.otherNCName = new StringIntMap(4);
    this.tables[6] = this.otherURI = new StringIntMap(4);
    this.tables[7] = this.attributeValue = new StringIntMap();
    this.tables[8] = this.otherString = new CharArrayIntMap(4);
    this.tables[9] = this.characterContentChunk = new CharArrayIntMap();
    this.tables[10] = this.elementName = new LocalNameQualifiedNamesMap();
    this.tables[11] = this.attributeName = new LocalNameQualifiedNamesMap();
  }
  
  public SerializerVocabulary(Vocabulary paramVocabulary, boolean paramBoolean) {
    this();
    this._useLocalNameAsKey = paramBoolean;
    convertVocabulary(paramVocabulary);
  }
  
  public SerializerVocabulary getReadOnlyVocabulary() { return this._readOnlyVocabulary; }
  
  protected void setReadOnlyVocabulary(SerializerVocabulary paramSerializerVocabulary, boolean paramBoolean) {
    for (byte b = 0; b < this.tables.length; b++)
      this.tables[b].setReadOnlyMap(paramSerializerVocabulary.tables[b], paramBoolean); 
  }
  
  public void setInitialVocabulary(SerializerVocabulary paramSerializerVocabulary, boolean paramBoolean) {
    setExternalVocabularyURI(null);
    setInitialReadOnlyVocabulary(true);
    setReadOnlyVocabulary(paramSerializerVocabulary, paramBoolean);
  }
  
  public void setExternalVocabulary(String paramString, SerializerVocabulary paramSerializerVocabulary, boolean paramBoolean) {
    setInitialReadOnlyVocabulary(false);
    setExternalVocabularyURI(paramString);
    setReadOnlyVocabulary(paramSerializerVocabulary, paramBoolean);
  }
  
  public void clear() {
    for (byte b = 0; b < this.tables.length; b++)
      this.tables[b].clear(); 
  }
  
  private void convertVocabulary(Vocabulary paramVocabulary) {
    addToTable(paramVocabulary.restrictedAlphabets.iterator(), this.restrictedAlphabet);
    addToTable(paramVocabulary.encodingAlgorithms.iterator(), this.encodingAlgorithm);
    addToTable(paramVocabulary.prefixes.iterator(), this.prefix);
    addToTable(paramVocabulary.namespaceNames.iterator(), this.namespaceName);
    addToTable(paramVocabulary.localNames.iterator(), this.localName);
    addToTable(paramVocabulary.otherNCNames.iterator(), this.otherNCName);
    addToTable(paramVocabulary.otherURIs.iterator(), this.otherURI);
    addToTable(paramVocabulary.attributeValues.iterator(), this.attributeValue);
    addToTable(paramVocabulary.otherStrings.iterator(), this.otherString);
    addToTable(paramVocabulary.characterContentChunks.iterator(), this.characterContentChunk);
    addToTable(paramVocabulary.elements.iterator(), this.elementName);
    addToTable(paramVocabulary.attributes.iterator(), this.attributeName);
  }
  
  private void addToTable(Iterator paramIterator, StringIntMap paramStringIntMap) {
    while (paramIterator.hasNext())
      addToTable((String)paramIterator.next(), paramStringIntMap); 
  }
  
  private void addToTable(String paramString, StringIntMap paramStringIntMap) {
    if (paramString.length() == 0)
      return; 
    paramStringIntMap.obtainIndex(paramString);
  }
  
  private void addToTable(Iterator paramIterator, CharArrayIntMap paramCharArrayIntMap) {
    while (paramIterator.hasNext())
      addToTable((String)paramIterator.next(), paramCharArrayIntMap); 
  }
  
  private void addToTable(String paramString, CharArrayIntMap paramCharArrayIntMap) {
    if (paramString.length() == 0)
      return; 
    char[] arrayOfChar = paramString.toCharArray();
    paramCharArrayIntMap.obtainIndex(arrayOfChar, 0, arrayOfChar.length, false);
  }
  
  private void addToTable(Iterator paramIterator, LocalNameQualifiedNamesMap paramLocalNameQualifiedNamesMap) {
    while (paramIterator.hasNext())
      addToNameTable((QName)paramIterator.next(), paramLocalNameQualifiedNamesMap); 
  }
  
  private void addToNameTable(QName paramQName, LocalNameQualifiedNamesMap paramLocalNameQualifiedNamesMap) {
    int i = -1;
    int j = -1;
    if (paramQName.getNamespaceURI().length() > 0) {
      i = this.namespaceName.obtainIndex(paramQName.getNamespaceURI());
      if (i == -1)
        i = this.namespaceName.get(paramQName.getNamespaceURI()); 
      if (paramQName.getPrefix().length() > 0) {
        j = this.prefix.obtainIndex(paramQName.getPrefix());
        if (j == -1)
          j = this.prefix.get(paramQName.getPrefix()); 
      } 
    } 
    int k = this.localName.obtainIndex(paramQName.getLocalPart());
    if (k == -1)
      k = this.localName.get(paramQName.getLocalPart()); 
    QualifiedName qualifiedName = new QualifiedName(paramQName.getPrefix(), paramQName.getNamespaceURI(), paramQName.getLocalPart(), paramLocalNameQualifiedNamesMap.getNextIndex(), j, i, k);
    LocalNameQualifiedNamesMap.Entry entry = null;
    if (this._useLocalNameAsKey) {
      entry = paramLocalNameQualifiedNamesMap.obtainEntry(paramQName.getLocalPart());
    } else {
      String str = (j == -1) ? paramQName.getLocalPart() : (paramQName.getPrefix() + ":" + paramQName.getLocalPart());
      entry = paramLocalNameQualifiedNamesMap.obtainEntry(str);
    } 
    entry.addQualifiedName(qualifiedName);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\fastinfoset\vocab\SerializerVocabulary.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */