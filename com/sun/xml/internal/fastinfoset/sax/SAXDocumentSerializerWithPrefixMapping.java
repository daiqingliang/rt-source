package com.sun.xml.internal.fastinfoset.sax;

import com.sun.xml.internal.fastinfoset.QualifiedName;
import com.sun.xml.internal.fastinfoset.util.LocalNameQualifiedNamesMap;
import com.sun.xml.internal.fastinfoset.util.StringIntMap;
import com.sun.xml.internal.org.jvnet.fastinfoset.FastInfosetException;
import com.sun.xml.internal.org.jvnet.fastinfoset.sax.EncodingAlgorithmAttributes;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public class SAXDocumentSerializerWithPrefixMapping extends SAXDocumentSerializer {
  protected Map _namespaceToPrefixMapping;
  
  protected Map _prefixToPrefixMapping;
  
  protected String _lastCheckedNamespace;
  
  protected String _lastCheckedPrefix;
  
  protected StringIntMap _declaredNamespaces;
  
  public SAXDocumentSerializerWithPrefixMapping(Map paramMap) {
    super(true);
    this._namespaceToPrefixMapping = new HashMap(paramMap);
    this._prefixToPrefixMapping = new HashMap();
    this._namespaceToPrefixMapping.put("", "");
    this._namespaceToPrefixMapping.put("http://www.w3.org/XML/1998/namespace", "xml");
    this._declaredNamespaces = new StringIntMap(4);
  }
  
  public final void startPrefixMapping(String paramString1, String paramString2) throws SAXException {
    try {
      if (!this._elementHasNamespaces) {
        encodeTermination();
        mark();
        this._elementHasNamespaces = true;
        write(56);
        this._declaredNamespaces.clear();
        this._declaredNamespaces.obtainIndex(paramString2);
      } else if (this._declaredNamespaces.obtainIndex(paramString2) != -1) {
        String str1 = getPrefix(paramString2);
        if (str1 != null)
          this._prefixToPrefixMapping.put(paramString1, str1); 
        return;
      } 
      String str = getPrefix(paramString2);
      if (str != null) {
        encodeNamespaceAttribute(str, paramString2);
        this._prefixToPrefixMapping.put(paramString1, str);
      } else {
        putPrefix(paramString2, paramString1);
        encodeNamespaceAttribute(paramString1, paramString2);
      } 
    } catch (IOException iOException) {
      throw new SAXException("startElement", iOException);
    } 
  }
  
  protected final void encodeElement(String paramString1, String paramString2, String paramString3) throws IOException {
    LocalNameQualifiedNamesMap.Entry entry = this._v.elementName.obtainEntry(paramString3);
    if (entry._valueIndex > 0) {
      if (encodeElementMapEntry(entry, paramString1))
        return; 
      if (this._v.elementName.isQNameFromReadOnlyMap(entry._value[0])) {
        entry = this._v.elementName.obtainDynamicEntry(paramString3);
        if (entry._valueIndex > 0 && encodeElementMapEntry(entry, paramString1))
          return; 
      } 
    } 
    encodeLiteralElementQualifiedNameOnThirdBit(paramString1, getPrefix(paramString1), paramString3, entry);
  }
  
  protected boolean encodeElementMapEntry(LocalNameQualifiedNamesMap.Entry paramEntry, String paramString) throws IOException {
    QualifiedName[] arrayOfQualifiedName = paramEntry._value;
    for (byte b = 0; b < paramEntry._valueIndex; b++) {
      if (paramString == (arrayOfQualifiedName[b]).namespaceName || paramString.equals((arrayOfQualifiedName[b]).namespaceName)) {
        encodeNonZeroIntegerOnThirdBit((arrayOfQualifiedName[b]).index);
        return true;
      } 
    } 
    return false;
  }
  
  protected final void encodeAttributes(Attributes paramAttributes) throws IOException, FastInfosetException {
    if (paramAttributes instanceof EncodingAlgorithmAttributes) {
      EncodingAlgorithmAttributes encodingAlgorithmAttributes = (EncodingAlgorithmAttributes)paramAttributes;
      for (byte b = 0; b < encodingAlgorithmAttributes.getLength(); b++) {
        String str = paramAttributes.getURI(b);
        if (encodeAttribute(str, paramAttributes.getQName(b), paramAttributes.getLocalName(b))) {
          Object object = encodingAlgorithmAttributes.getAlgorithmData(b);
          if (object == null) {
            String str1 = encodingAlgorithmAttributes.getValue(b);
            boolean bool1 = isAttributeValueLengthMatchesLimit(str1.length());
            boolean bool2 = encodingAlgorithmAttributes.getToIndex(b);
            String str2 = encodingAlgorithmAttributes.getAlpababet(b);
            if (str2 == null) {
              if (str == "http://www.w3.org/2001/XMLSchema-instance" || str.equals("http://www.w3.org/2001/XMLSchema-instance"))
                str1 = convertQName(str1); 
              encodeNonIdentifyingStringOnFirstBit(str1, this._v.attributeValue, bool1, bool2);
            } else if (str2 == "0123456789-:TZ ") {
              encodeDateTimeNonIdentifyingStringOnFirstBit(str1, bool1, bool2);
            } else if (str2 == "0123456789-+.E ") {
              encodeNumericNonIdentifyingStringOnFirstBit(str1, bool1, bool2);
            } else {
              encodeNonIdentifyingStringOnFirstBit(str1, this._v.attributeValue, bool1, bool2);
            } 
          } else {
            encodeNonIdentifyingStringOnFirstBit(encodingAlgorithmAttributes.getAlgorithmURI(b), encodingAlgorithmAttributes.getAlgorithmIndex(b), object);
          } 
        } 
      } 
    } else {
      for (byte b = 0; b < paramAttributes.getLength(); b++) {
        String str = paramAttributes.getURI(b);
        if (encodeAttribute(paramAttributes.getURI(b), paramAttributes.getQName(b), paramAttributes.getLocalName(b))) {
          String str1 = paramAttributes.getValue(b);
          boolean bool = isAttributeValueLengthMatchesLimit(str1.length());
          if (str == "http://www.w3.org/2001/XMLSchema-instance" || str.equals("http://www.w3.org/2001/XMLSchema-instance"))
            str1 = convertQName(str1); 
          encodeNonIdentifyingStringOnFirstBit(str1, this._v.attributeValue, bool, false);
        } 
      } 
    } 
    this._b = 240;
    this._terminate = true;
  }
  
  private String convertQName(String paramString) {
    int i = paramString.indexOf(':');
    String str1 = "";
    String str2 = paramString;
    if (i != -1) {
      str1 = paramString.substring(0, i);
      str2 = paramString.substring(i + 1);
    } 
    String str3 = (String)this._prefixToPrefixMapping.get(str1);
    return (str3 != null) ? ((str3.length() == 0) ? str2 : (str3 + ":" + str2)) : paramString;
  }
  
  protected final boolean encodeAttribute(String paramString1, String paramString2, String paramString3) throws IOException {
    LocalNameQualifiedNamesMap.Entry entry = this._v.attributeName.obtainEntry(paramString3);
    if (entry._valueIndex > 0) {
      if (encodeAttributeMapEntry(entry, paramString1))
        return true; 
      if (this._v.attributeName.isQNameFromReadOnlyMap(entry._value[0])) {
        entry = this._v.attributeName.obtainDynamicEntry(paramString3);
        if (entry._valueIndex > 0 && encodeAttributeMapEntry(entry, paramString1))
          return true; 
      } 
    } 
    return encodeLiteralAttributeQualifiedNameOnSecondBit(paramString1, getPrefix(paramString1), paramString3, entry);
  }
  
  protected boolean encodeAttributeMapEntry(LocalNameQualifiedNamesMap.Entry paramEntry, String paramString) throws IOException {
    QualifiedName[] arrayOfQualifiedName = paramEntry._value;
    for (byte b = 0; b < paramEntry._valueIndex; b++) {
      if (paramString == (arrayOfQualifiedName[b]).namespaceName || paramString.equals((arrayOfQualifiedName[b]).namespaceName)) {
        encodeNonZeroIntegerOnSecondBitFirstBitZero((arrayOfQualifiedName[b]).index);
        return true;
      } 
    } 
    return false;
  }
  
  protected final String getPrefix(String paramString) {
    if (this._lastCheckedNamespace == paramString)
      return this._lastCheckedPrefix; 
    this._lastCheckedNamespace = paramString;
    return this._lastCheckedPrefix = (String)this._namespaceToPrefixMapping.get(paramString);
  }
  
  protected final void putPrefix(String paramString1, String paramString2) throws SAXException {
    this._namespaceToPrefixMapping.put(paramString1, paramString2);
    this._lastCheckedNamespace = paramString1;
    this._lastCheckedPrefix = paramString2;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\fastinfoset\sax\SAXDocumentSerializerWithPrefixMapping.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */