package com.sun.xml.internal.fastinfoset.tools;

import com.sun.xml.internal.fastinfoset.CommonResourceBundle;
import com.sun.xml.internal.fastinfoset.QualifiedName;
import com.sun.xml.internal.fastinfoset.util.CharArray;
import com.sun.xml.internal.fastinfoset.util.LocalNameQualifiedNamesMap;
import com.sun.xml.internal.fastinfoset.util.PrefixArray;
import com.sun.xml.internal.fastinfoset.util.QualifiedNameArray;
import com.sun.xml.internal.fastinfoset.util.StringArray;
import com.sun.xml.internal.fastinfoset.util.StringIntMap;
import com.sun.xml.internal.fastinfoset.vocab.ParserVocabulary;
import com.sun.xml.internal.fastinfoset.vocab.SerializerVocabulary;
import com.sun.xml.internal.org.jvnet.fastinfoset.Vocabulary;
import java.util.Set;
import org.xml.sax.Attributes;
import org.xml.sax.Locator;
import org.xml.sax.SAXException;
import org.xml.sax.ext.LexicalHandler;
import org.xml.sax.helpers.DefaultHandler;

public class VocabularyGenerator extends DefaultHandler implements LexicalHandler {
  protected SerializerVocabulary _serializerVocabulary = new SerializerVocabulary();
  
  protected ParserVocabulary _parserVocabulary = new ParserVocabulary();
  
  protected Vocabulary _v = new Vocabulary();
  
  protected int attributeValueSizeConstraint = 32;
  
  protected int characterContentChunkSizeContraint = 32;
  
  public VocabularyGenerator() {}
  
  public VocabularyGenerator(SerializerVocabulary paramSerializerVocabulary) {}
  
  public VocabularyGenerator(ParserVocabulary paramParserVocabulary) {}
  
  public VocabularyGenerator(SerializerVocabulary paramSerializerVocabulary, ParserVocabulary paramParserVocabulary) {}
  
  public Vocabulary getVocabulary() { return this._v; }
  
  public void setCharacterContentChunkSizeLimit(int paramInt) {
    if (paramInt < 0)
      paramInt = 0; 
    this.characterContentChunkSizeContraint = paramInt;
  }
  
  public int getCharacterContentChunkSizeLimit() { return this.characterContentChunkSizeContraint; }
  
  public void setAttributeValueSizeLimit(int paramInt) {
    if (paramInt < 0)
      paramInt = 0; 
    this.attributeValueSizeConstraint = paramInt;
  }
  
  public int getAttributeValueSizeLimit() { return this.attributeValueSizeConstraint; }
  
  public void startDocument() {}
  
  public void endDocument() {}
  
  public void startPrefixMapping(String paramString1, String paramString2) throws SAXException {
    addToTable(paramString1, this._v.prefixes, this._serializerVocabulary.prefix, this._parserVocabulary.prefix);
    addToTable(paramString2, this._v.namespaceNames, this._serializerVocabulary.namespaceName, this._parserVocabulary.namespaceName);
  }
  
  public void endPrefixMapping(String paramString) throws SAXException {}
  
  public void startElement(String paramString1, String paramString2, String paramString3, Attributes paramAttributes) throws SAXException {
    addToNameTable(paramString1, paramString3, paramString2, this._v.elements, this._serializerVocabulary.elementName, this._parserVocabulary.elementName, false);
    for (byte b = 0; b < paramAttributes.getLength(); b++) {
      addToNameTable(paramAttributes.getURI(b), paramAttributes.getQName(b), paramAttributes.getLocalName(b), this._v.attributes, this._serializerVocabulary.attributeName, this._parserVocabulary.attributeName, true);
      String str = paramAttributes.getValue(b);
      if (str.length() < this.attributeValueSizeConstraint)
        addToTable(str, this._v.attributeValues, this._serializerVocabulary.attributeValue, this._parserVocabulary.attributeValue); 
    } 
  }
  
  public void endElement(String paramString1, String paramString2, String paramString3) throws SAXException {}
  
  public void characters(char[] paramArrayOfChar, int paramInt1, int paramInt2) throws SAXException {
    if (paramInt2 < this.characterContentChunkSizeContraint)
      addToCharArrayTable(new CharArray(paramArrayOfChar, paramInt1, paramInt2, true)); 
  }
  
  public void ignorableWhitespace(char[] paramArrayOfChar, int paramInt1, int paramInt2) throws SAXException {}
  
  public void processingInstruction(String paramString1, String paramString2) throws SAXException {}
  
  public void setDocumentLocator(Locator paramLocator) {}
  
  public void skippedEntity(String paramString) throws SAXException {}
  
  public void comment(char[] paramArrayOfChar, int paramInt1, int paramInt2) throws SAXException {}
  
  public void startCDATA() {}
  
  public void endCDATA() {}
  
  public void startDTD(String paramString1, String paramString2, String paramString3) throws SAXException {}
  
  public void endDTD() {}
  
  public void startEntity(String paramString) throws SAXException {}
  
  public void endEntity(String paramString) throws SAXException {}
  
  public void addToTable(String paramString, Set paramSet, StringIntMap paramStringIntMap, StringArray paramStringArray) {
    if (paramString.length() == 0)
      return; 
    if (paramStringIntMap.obtainIndex(paramString) == -1)
      paramStringArray.add(paramString); 
    paramSet.add(paramString);
  }
  
  public void addToTable(String paramString, Set paramSet, StringIntMap paramStringIntMap, PrefixArray paramPrefixArray) {
    if (paramString.length() == 0)
      return; 
    if (paramStringIntMap.obtainIndex(paramString) == -1)
      paramPrefixArray.add(paramString); 
    paramSet.add(paramString);
  }
  
  public void addToCharArrayTable(CharArray paramCharArray) {
    if (this._serializerVocabulary.characterContentChunk.obtainIndex(paramCharArray.ch, paramCharArray.start, paramCharArray.length, false) == -1)
      this._parserVocabulary.characterContentChunk.add(paramCharArray.ch, paramCharArray.length); 
    this._v.characterContentChunks.add(paramCharArray.toString());
  }
  
  public void addToNameTable(String paramString1, String paramString2, String paramString3, Set paramSet, LocalNameQualifiedNamesMap paramLocalNameQualifiedNamesMap, QualifiedNameArray paramQualifiedNameArray, boolean paramBoolean) throws SAXException {
    LocalNameQualifiedNamesMap.Entry entry = paramLocalNameQualifiedNamesMap.obtainEntry(paramString2);
    if (entry._valueIndex > 0) {
      QualifiedName[] arrayOfQualifiedName = entry._value;
      for (byte b = 0; b < entry._valueIndex; b++) {
        if (paramString1 == (arrayOfQualifiedName[b]).namespaceName || paramString1.equals((arrayOfQualifiedName[b]).namespaceName))
          return; 
      } 
    } 
    String str = getPrefixFromQualifiedName(paramString2);
    int i = -1;
    int j = -1;
    int k = -1;
    if (paramString1.length() > 0) {
      i = this._serializerVocabulary.namespaceName.get(paramString1);
      if (i == -1)
        throw new SAXException(CommonResourceBundle.getInstance().getString("message.namespaceURINotIndexed", new Object[] { Integer.valueOf(i) })); 
      if (str.length() > 0) {
        j = this._serializerVocabulary.prefix.get(str);
        if (j == -1)
          throw new SAXException(CommonResourceBundle.getInstance().getString("message.prefixNotIndexed", new Object[] { Integer.valueOf(j) })); 
      } 
    } 
    k = this._serializerVocabulary.localName.obtainIndex(paramString3);
    if (k == -1) {
      this._parserVocabulary.localName.add(paramString3);
      k = this._parserVocabulary.localName.getSize() - 1;
    } 
    QualifiedName qualifiedName = new QualifiedName(str, paramString1, paramString3, paramLocalNameQualifiedNamesMap.getNextIndex(), j, i, k);
    if (paramBoolean)
      qualifiedName.createAttributeValues(256); 
    entry.addQualifiedName(qualifiedName);
    paramQualifiedNameArray.add(qualifiedName);
    paramSet.add(qualifiedName.getQName());
  }
  
  public static String getPrefixFromQualifiedName(String paramString) {
    int i = paramString.indexOf(':');
    String str = "";
    if (i != -1)
      str = paramString.substring(0, i); 
    return str;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\fastinfoset\tools\VocabularyGenerator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */