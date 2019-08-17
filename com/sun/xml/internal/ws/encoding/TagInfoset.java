package com.sun.xml.internal.ws.encoding;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import java.util.ArrayList;
import java.util.List;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.AttributesImpl;

public final class TagInfoset {
  @NotNull
  public final String[] ns;
  
  @NotNull
  public final AttributesImpl atts;
  
  @Nullable
  public final String prefix;
  
  @Nullable
  public final String nsUri;
  
  @NotNull
  public final String localName;
  
  @Nullable
  private String qname;
  
  private static final String[] EMPTY_ARRAY = new String[0];
  
  private static final AttributesImpl EMPTY_ATTRIBUTES = new AttributesImpl();
  
  public TagInfoset(String paramString1, String paramString2, String paramString3, AttributesImpl paramAttributesImpl, String... paramVarArgs) {
    this.nsUri = paramString1;
    this.prefix = paramString3;
    this.localName = paramString2;
    this.atts = paramAttributesImpl;
    this.ns = paramVarArgs;
  }
  
  public TagInfoset(XMLStreamReader paramXMLStreamReader) {
    this.prefix = paramXMLStreamReader.getPrefix();
    this.nsUri = paramXMLStreamReader.getNamespaceURI();
    this.localName = paramXMLStreamReader.getLocalName();
    int i = paramXMLStreamReader.getNamespaceCount();
    if (i > 0) {
      this.ns = new String[i * 2];
      for (byte b = 0; b < i; b++) {
        this.ns[b * 2] = fixNull(paramXMLStreamReader.getNamespacePrefix(b));
        this.ns[b * 2 + 1] = fixNull(paramXMLStreamReader.getNamespaceURI(b));
      } 
    } else {
      this.ns = EMPTY_ARRAY;
    } 
    int j = paramXMLStreamReader.getAttributeCount();
    if (j > 0) {
      this.atts = new AttributesImpl();
      StringBuilder stringBuilder = new StringBuilder();
      for (byte b = 0; b < j; b++) {
        String str3;
        stringBuilder.setLength(0);
        String str1 = paramXMLStreamReader.getAttributePrefix(b);
        String str2 = paramXMLStreamReader.getAttributeLocalName(b);
        if (str1 != null && str1.length() != 0) {
          stringBuilder.append(str1);
          stringBuilder.append(":");
          stringBuilder.append(str2);
          str3 = stringBuilder.toString();
        } else {
          str3 = str2;
        } 
        this.atts.addAttribute(fixNull(paramXMLStreamReader.getAttributeNamespace(b)), str2, str3, paramXMLStreamReader.getAttributeType(b), paramXMLStreamReader.getAttributeValue(b));
      } 
    } else {
      this.atts = EMPTY_ATTRIBUTES;
    } 
  }
  
  public void writeStart(ContentHandler paramContentHandler) throws SAXException {
    for (boolean bool = false; bool < this.ns.length; bool += true)
      paramContentHandler.startPrefixMapping(fixNull(this.ns[bool]), fixNull(this.ns[bool + true])); 
    paramContentHandler.startElement(fixNull(this.nsUri), this.localName, getQName(), this.atts);
  }
  
  public void writeEnd(ContentHandler paramContentHandler) throws SAXException {
    paramContentHandler.endElement(fixNull(this.nsUri), this.localName, getQName());
    for (int i = this.ns.length - 2; i >= 0; i -= 2)
      paramContentHandler.endPrefixMapping(fixNull(this.ns[i])); 
  }
  
  public void writeStart(XMLStreamWriter paramXMLStreamWriter) throws XMLStreamException {
    if (this.prefix == null) {
      if (this.nsUri == null) {
        paramXMLStreamWriter.writeStartElement(this.localName);
      } else {
        paramXMLStreamWriter.writeStartElement("", this.localName, this.nsUri);
      } 
    } else {
      paramXMLStreamWriter.writeStartElement(this.prefix, this.localName, this.nsUri);
    } 
    byte b;
    for (b = 0; b < this.ns.length; b += true)
      paramXMLStreamWriter.writeNamespace(this.ns[b], this.ns[b + true]); 
    for (b = 0; b < this.atts.getLength(); b++) {
      String str = this.atts.getURI(b);
      if (str == null || str.length() == 0) {
        paramXMLStreamWriter.writeAttribute(this.atts.getLocalName(b), this.atts.getValue(b));
      } else {
        String str1 = this.atts.getQName(b);
        String str2 = str1.substring(0, str1.indexOf(':'));
        paramXMLStreamWriter.writeAttribute(str2, str, this.atts.getLocalName(b), this.atts.getValue(b));
      } 
    } 
  }
  
  private String getQName() {
    if (this.qname != null)
      return this.qname; 
    StringBuilder stringBuilder = new StringBuilder();
    if (this.prefix != null) {
      stringBuilder.append(this.prefix);
      stringBuilder.append(':');
      stringBuilder.append(this.localName);
      this.qname = stringBuilder.toString();
    } else {
      this.qname = this.localName;
    } 
    return this.qname;
  }
  
  private static String fixNull(String paramString) { return (paramString == null) ? "" : paramString; }
  
  public String getNamespaceURI(String paramString) {
    int i = this.ns.length / 2;
    for (byte b = 0; b < i; b++) {
      String str1 = this.ns[b * 2];
      String str2 = this.ns[b * 2 + 1];
      if (paramString.equals(str1))
        return str2; 
    } 
    return null;
  }
  
  public String getPrefix(String paramString) {
    int i = this.ns.length / 2;
    for (byte b = 0; b < i; b++) {
      String str1 = this.ns[b * 2];
      String str2 = this.ns[b * 2 + 1];
      if (paramString.equals(str2))
        return str1; 
    } 
    return null;
  }
  
  public List<String> allPrefixes(String paramString) {
    int i = this.ns.length / 2;
    ArrayList arrayList = new ArrayList();
    for (byte b = 0; b < i; b++) {
      String str1 = this.ns[b * 2];
      String str2 = this.ns[b * 2 + 1];
      if (paramString.equals(str2))
        arrayList.add(str1); 
    } 
    return arrayList;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\encoding\TagInfoset.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */