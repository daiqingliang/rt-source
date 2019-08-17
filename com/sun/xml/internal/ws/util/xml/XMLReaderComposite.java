package com.sun.xml.internal.ws.util.xml;

import com.sun.xml.internal.org.jvnet.staxex.NamespaceContextEx;
import com.sun.xml.internal.org.jvnet.staxex.XMLStreamReaderEx;
import com.sun.xml.internal.ws.encoding.TagInfoset;
import java.util.Iterator;
import java.util.List;
import javax.xml.namespace.NamespaceContext;
import javax.xml.namespace.QName;
import javax.xml.stream.Location;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;

public class XMLReaderComposite implements XMLStreamReaderEx {
  protected State state = State.StartTag;
  
  protected ElemInfo elemInfo;
  
  protected TagInfoset tagInfo;
  
  protected XMLStreamReader[] children;
  
  protected int payloadIndex = -1;
  
  protected XMLStreamReader payloadReader;
  
  public XMLReaderComposite(ElemInfo paramElemInfo, XMLStreamReader[] paramArrayOfXMLStreamReader) {
    this.elemInfo = paramElemInfo;
    this.tagInfo = paramElemInfo.tagInfo;
    this.children = paramArrayOfXMLStreamReader;
    if (this.children != null && this.children.length > 0) {
      this.payloadIndex = 0;
      this.payloadReader = this.children[this.payloadIndex];
    } 
  }
  
  public int next() throws XMLStreamException {
    switch (this.state) {
      case StartTag:
        if (this.payloadReader != null) {
          this.state = State.Payload;
          return this.payloadReader.getEventType();
        } 
        this.state = State.EndTag;
        return 2;
      case EndTag:
        return 8;
    } 
    int i = 8;
    if (this.payloadReader != null && this.payloadReader.hasNext())
      i = this.payloadReader.next(); 
    if (i != 8)
      return i; 
    if (this.payloadIndex + 1 < this.children.length) {
      this.payloadIndex++;
      this.payloadReader = this.children[this.payloadIndex];
      return this.payloadReader.getEventType();
    } 
    this.state = State.EndTag;
    return 2;
  }
  
  public boolean hasNext() throws XMLStreamException {
    switch (this.state) {
      case EndTag:
        return false;
    } 
    return true;
  }
  
  public String getElementText() throws XMLStreamException {
    switch (this.state) {
      case StartTag:
        return this.payloadReader.isCharacters() ? this.payloadReader.getText() : "";
    } 
    return this.payloadReader.getElementText();
  }
  
  public int nextTag() throws XMLStreamException {
    int i = next();
    if (i == 8)
      return i; 
    while (i != 8) {
      if (i == 1)
        return i; 
      if (i == 2)
        return i; 
      i = next();
    } 
    return i;
  }
  
  public Object getProperty(String paramString) throws IllegalArgumentException { return (this.payloadReader != null) ? this.payloadReader.getProperty(paramString) : null; }
  
  public void require(int paramInt, String paramString1, String paramString2) throws XMLStreamException {
    if (this.payloadReader != null)
      this.payloadReader.require(paramInt, paramString1, paramString2); 
  }
  
  public void close() throws XMLStreamException {
    if (this.payloadReader != null)
      this.payloadReader.close(); 
  }
  
  public String getNamespaceURI(String paramString) {
    switch (this.state) {
      case StartTag:
      case EndTag:
        return this.elemInfo.getNamespaceURI(paramString);
    } 
    return this.payloadReader.getNamespaceURI(paramString);
  }
  
  public boolean isStartElement() throws XMLStreamException {
    switch (this.state) {
      case StartTag:
        return true;
      case EndTag:
        return false;
    } 
    return this.payloadReader.isStartElement();
  }
  
  public boolean isEndElement() throws XMLStreamException {
    switch (this.state) {
      case StartTag:
        return false;
      case EndTag:
        return true;
    } 
    return this.payloadReader.isEndElement();
  }
  
  public boolean isCharacters() throws XMLStreamException {
    switch (this.state) {
      case StartTag:
      case EndTag:
        return false;
    } 
    return this.payloadReader.isCharacters();
  }
  
  public boolean isWhiteSpace() throws XMLStreamException {
    switch (this.state) {
      case StartTag:
      case EndTag:
        return false;
    } 
    return this.payloadReader.isWhiteSpace();
  }
  
  public String getAttributeValue(String paramString1, String paramString2) {
    switch (this.state) {
      case StartTag:
      case EndTag:
        return this.tagInfo.atts.getValue(paramString1, paramString2);
    } 
    return this.payloadReader.getAttributeValue(paramString1, paramString2);
  }
  
  public int getAttributeCount() throws XMLStreamException {
    switch (this.state) {
      case StartTag:
      case EndTag:
        return this.tagInfo.atts.getLength();
    } 
    return this.payloadReader.getAttributeCount();
  }
  
  public QName getAttributeName(int paramInt) {
    switch (this.state) {
      case StartTag:
      case EndTag:
        return new QName(this.tagInfo.atts.getURI(paramInt), this.tagInfo.atts.getLocalName(paramInt), getPrfix(this.tagInfo.atts.getQName(paramInt)));
    } 
    return this.payloadReader.getAttributeName(paramInt);
  }
  
  public String getAttributeNamespace(int paramInt) {
    switch (this.state) {
      case StartTag:
      case EndTag:
        return this.tagInfo.atts.getURI(paramInt);
    } 
    return this.payloadReader.getAttributeNamespace(paramInt);
  }
  
  public String getAttributeLocalName(int paramInt) {
    switch (this.state) {
      case StartTag:
      case EndTag:
        return this.tagInfo.atts.getLocalName(paramInt);
    } 
    return this.payloadReader.getAttributeLocalName(paramInt);
  }
  
  public String getAttributePrefix(int paramInt) {
    switch (this.state) {
      case StartTag:
      case EndTag:
        return getPrfix(this.tagInfo.atts.getQName(paramInt));
    } 
    return this.payloadReader.getAttributePrefix(paramInt);
  }
  
  private static String getPrfix(String paramString) {
    if (paramString == null)
      return null; 
    int i = paramString.indexOf(":");
    return (i > 0) ? paramString.substring(0, i) : "";
  }
  
  public String getAttributeType(int paramInt) {
    switch (this.state) {
      case StartTag:
      case EndTag:
        return this.tagInfo.atts.getType(paramInt);
    } 
    return this.payloadReader.getAttributeType(paramInt);
  }
  
  public String getAttributeValue(int paramInt) {
    switch (this.state) {
      case StartTag:
      case EndTag:
        return this.tagInfo.atts.getValue(paramInt);
    } 
    return this.payloadReader.getAttributeValue(paramInt);
  }
  
  public boolean isAttributeSpecified(int paramInt) {
    switch (this.state) {
      case StartTag:
      case EndTag:
        return (paramInt < this.tagInfo.atts.getLength()) ? ((this.tagInfo.atts.getLocalName(paramInt) != null)) : false;
    } 
    return this.payloadReader.isAttributeSpecified(paramInt);
  }
  
  public int getNamespaceCount() throws XMLStreamException {
    switch (this.state) {
      case StartTag:
      case EndTag:
        return this.tagInfo.ns.length / 2;
    } 
    return this.payloadReader.getNamespaceCount();
  }
  
  public String getNamespacePrefix(int paramInt) {
    switch (this.state) {
      case StartTag:
      case EndTag:
        return this.tagInfo.ns[2 * paramInt];
    } 
    return this.payloadReader.getNamespacePrefix(paramInt);
  }
  
  public String getNamespaceURI(int paramInt) {
    switch (this.state) {
      case StartTag:
      case EndTag:
        return this.tagInfo.ns[2 * paramInt + 1];
    } 
    return this.payloadReader.getNamespaceURI(paramInt);
  }
  
  public NamespaceContextEx getNamespaceContext() {
    switch (this.state) {
      case StartTag:
      case EndTag:
        return new NamespaceContextExAdaper(this.elemInfo);
    } 
    return isPayloadReaderEx() ? payloadReaderEx().getNamespaceContext() : new NamespaceContextExAdaper(this.payloadReader.getNamespaceContext());
  }
  
  private boolean isPayloadReaderEx() throws XMLStreamException { return this.payloadReader instanceof XMLStreamReaderEx; }
  
  private XMLStreamReaderEx payloadReaderEx() { return (XMLStreamReaderEx)this.payloadReader; }
  
  public int getEventType() throws XMLStreamException {
    switch (this.state) {
      case StartTag:
        return 1;
      case EndTag:
        return 2;
    } 
    return this.payloadReader.getEventType();
  }
  
  public String getText() throws XMLStreamException {
    switch (this.state) {
      case StartTag:
      case EndTag:
        return null;
    } 
    return this.payloadReader.getText();
  }
  
  public char[] getTextCharacters() {
    switch (this.state) {
      case StartTag:
      case EndTag:
        return null;
    } 
    return this.payloadReader.getTextCharacters();
  }
  
  public int getTextCharacters(int paramInt1, char[] paramArrayOfChar, int paramInt2, int paramInt3) throws XMLStreamException {
    switch (this.state) {
      case StartTag:
      case EndTag:
        return -1;
    } 
    return this.payloadReader.getTextCharacters(paramInt1, paramArrayOfChar, paramInt2, paramInt3);
  }
  
  public int getTextStart() throws XMLStreamException {
    switch (this.state) {
      case StartTag:
      case EndTag:
        return 0;
    } 
    return this.payloadReader.getTextStart();
  }
  
  public int getTextLength() throws XMLStreamException {
    switch (this.state) {
      case StartTag:
      case EndTag:
        return 0;
    } 
    return this.payloadReader.getTextLength();
  }
  
  public String getEncoding() throws XMLStreamException {
    switch (this.state) {
      case StartTag:
      case EndTag:
        return null;
    } 
    return this.payloadReader.getEncoding();
  }
  
  public boolean hasText() throws XMLStreamException {
    switch (this.state) {
      case StartTag:
      case EndTag:
        return false;
    } 
    return this.payloadReader.hasText();
  }
  
  public Location getLocation() {
    switch (this.state) {
      case StartTag:
      case EndTag:
        return new Location() {
            public int getLineNumber() throws XMLStreamException { return 0; }
            
            public int getColumnNumber() throws XMLStreamException { return 0; }
            
            public int getCharacterOffset() throws XMLStreamException { return 0; }
            
            public String getPublicId() throws XMLStreamException { return null; }
            
            public String getSystemId() throws XMLStreamException { return null; }
          };
    } 
    return this.payloadReader.getLocation();
  }
  
  public QName getName() {
    switch (this.state) {
      case StartTag:
      case EndTag:
        return new QName(this.tagInfo.nsUri, this.tagInfo.localName, this.tagInfo.prefix);
    } 
    return this.payloadReader.getName();
  }
  
  public String getLocalName() throws XMLStreamException {
    switch (this.state) {
      case StartTag:
      case EndTag:
        return this.tagInfo.localName;
    } 
    return this.payloadReader.getLocalName();
  }
  
  public boolean hasName() throws XMLStreamException {
    switch (this.state) {
      case StartTag:
      case EndTag:
        return true;
    } 
    return this.payloadReader.hasName();
  }
  
  public String getNamespaceURI() throws XMLStreamException {
    switch (this.state) {
      case StartTag:
      case EndTag:
        return this.tagInfo.nsUri;
    } 
    return this.payloadReader.getNamespaceURI();
  }
  
  public String getPrefix() throws XMLStreamException {
    switch (this.state) {
      case StartTag:
      case EndTag:
        return this.tagInfo.prefix;
    } 
    return this.payloadReader.getPrefix();
  }
  
  public String getVersion() throws XMLStreamException {
    switch (this.state) {
      case StartTag:
      case EndTag:
        return null;
    } 
    return this.payloadReader.getVersion();
  }
  
  public boolean isStandalone() throws XMLStreamException {
    switch (this.state) {
      case StartTag:
      case EndTag:
        return true;
    } 
    return this.payloadReader.isStandalone();
  }
  
  public boolean standaloneSet() throws XMLStreamException {
    switch (this.state) {
      case StartTag:
      case EndTag:
        return true;
    } 
    return this.payloadReader.standaloneSet();
  }
  
  public String getCharacterEncodingScheme() throws XMLStreamException {
    switch (this.state) {
      case StartTag:
      case EndTag:
        return null;
    } 
    return this.payloadReader.getCharacterEncodingScheme();
  }
  
  public String getPITarget() throws XMLStreamException {
    switch (this.state) {
      case StartTag:
      case EndTag:
        return null;
    } 
    return this.payloadReader.getPITarget();
  }
  
  public String getPIData() throws XMLStreamException {
    switch (this.state) {
      case StartTag:
      case EndTag:
        return null;
    } 
    return this.payloadReader.getPIData();
  }
  
  public String getElementTextTrim() throws XMLStreamException {
    switch (this.state) {
      case StartTag:
      case EndTag:
        return null;
    } 
    return isPayloadReaderEx() ? payloadReaderEx().getElementTextTrim() : this.payloadReader.getElementText().trim();
  }
  
  public CharSequence getPCDATA() throws XMLStreamException {
    switch (this.state) {
      case StartTag:
      case EndTag:
        return null;
    } 
    return isPayloadReaderEx() ? payloadReaderEx().getPCDATA() : this.payloadReader.getElementText();
  }
  
  public static class ElemInfo implements NamespaceContext {
    ElemInfo ancestor;
    
    TagInfoset tagInfo;
    
    public ElemInfo(TagInfoset param1TagInfoset, ElemInfo param1ElemInfo) {
      this.tagInfo = param1TagInfoset;
      this.ancestor = param1ElemInfo;
    }
    
    public String getNamespaceURI(String param1String) {
      String str = this.tagInfo.getNamespaceURI(param1String);
      return (str != null) ? str : ((this.ancestor != null) ? this.ancestor.getNamespaceURI(param1String) : null);
    }
    
    public String getPrefix(String param1String) {
      String str = this.tagInfo.getPrefix(param1String);
      return (str != null) ? str : ((this.ancestor != null) ? this.ancestor.getPrefix(param1String) : null);
    }
    
    public List<String> allPrefixes(String param1String) {
      List list = this.tagInfo.allPrefixes(param1String);
      if (this.ancestor != null) {
        List list1 = this.ancestor.allPrefixes(param1String);
        list1.addAll(list);
        return list1;
      } 
      return list;
    }
    
    public Iterator<String> getPrefixes(String param1String) { return allPrefixes(param1String).iterator(); }
  }
  
  public enum State {
    StartTag, Payload, EndTag;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\w\\util\xml\XMLReaderComposite.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */