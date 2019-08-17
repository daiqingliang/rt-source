package com.sun.xml.internal.txw2.output;

import com.sun.xml.internal.txw2.TxwException;
import java.util.Stack;
import javax.xml.transform.sax.SAXResult;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.ext.LexicalHandler;
import org.xml.sax.helpers.AttributesImpl;

public class SaxSerializer implements XmlSerializer {
  private final ContentHandler writer;
  
  private final LexicalHandler lexical;
  
  private final Stack<String> prefixBindings = new Stack();
  
  private final Stack<String> elementBindings = new Stack();
  
  private final AttributesImpl attrs = new AttributesImpl();
  
  public SaxSerializer(ContentHandler paramContentHandler) { this(paramContentHandler, null, true); }
  
  public SaxSerializer(ContentHandler paramContentHandler, LexicalHandler paramLexicalHandler) { this(paramContentHandler, paramLexicalHandler, true); }
  
  public SaxSerializer(ContentHandler paramContentHandler, LexicalHandler paramLexicalHandler, boolean paramBoolean) {
    if (!paramBoolean) {
      this.writer = paramContentHandler;
      this.lexical = paramLexicalHandler;
    } else {
      IndentingXMLFilter indentingXMLFilter = new IndentingXMLFilter(paramContentHandler, paramLexicalHandler);
      this.writer = indentingXMLFilter;
      this.lexical = indentingXMLFilter;
    } 
  }
  
  public SaxSerializer(SAXResult paramSAXResult) { this(paramSAXResult.getHandler(), paramSAXResult.getLexicalHandler()); }
  
  public void startDocument() {
    try {
      this.writer.startDocument();
    } catch (SAXException sAXException) {
      throw new TxwException(sAXException);
    } 
  }
  
  public void writeXmlns(String paramString1, String paramString2) {
    if (paramString1 == null)
      paramString1 = ""; 
    if (paramString1.equals("xml"))
      return; 
    this.prefixBindings.add(paramString2);
    this.prefixBindings.add(paramString1);
  }
  
  public void beginStartTag(String paramString1, String paramString2, String paramString3) {
    this.elementBindings.add(getQName(paramString3, paramString2));
    this.elementBindings.add(paramString2);
    this.elementBindings.add(paramString1);
  }
  
  public void writeAttribute(String paramString1, String paramString2, String paramString3, StringBuilder paramStringBuilder) { this.attrs.addAttribute(paramString1, paramString2, getQName(paramString3, paramString2), "CDATA", paramStringBuilder.toString()); }
  
  public void endStartTag(String paramString1, String paramString2, String paramString3) {
    try {
      while (this.prefixBindings.size() != 0)
        this.writer.startPrefixMapping((String)this.prefixBindings.pop(), (String)this.prefixBindings.pop()); 
      this.writer.startElement(paramString1, paramString2, getQName(paramString3, paramString2), this.attrs);
      this.attrs.clear();
    } catch (SAXException sAXException) {
      throw new TxwException(sAXException);
    } 
  }
  
  public void endTag() {
    try {
      this.writer.endElement((String)this.elementBindings.pop(), (String)this.elementBindings.pop(), (String)this.elementBindings.pop());
    } catch (SAXException sAXException) {
      throw new TxwException(sAXException);
    } 
  }
  
  public void text(StringBuilder paramStringBuilder) {
    try {
      this.writer.characters(paramStringBuilder.toString().toCharArray(), 0, paramStringBuilder.length());
    } catch (SAXException sAXException) {
      throw new TxwException(sAXException);
    } 
  }
  
  public void cdata(StringBuilder paramStringBuilder) {
    if (this.lexical == null)
      throw new UnsupportedOperationException("LexicalHandler is needed to write PCDATA"); 
    try {
      this.lexical.startCDATA();
      text(paramStringBuilder);
      this.lexical.endCDATA();
    } catch (SAXException sAXException) {
      throw new TxwException(sAXException);
    } 
  }
  
  public void comment(StringBuilder paramStringBuilder) {
    try {
      if (this.lexical == null)
        throw new UnsupportedOperationException("LexicalHandler is needed to write comments"); 
      this.lexical.comment(paramStringBuilder.toString().toCharArray(), 0, paramStringBuilder.length());
    } catch (SAXException sAXException) {
      throw new TxwException(sAXException);
    } 
  }
  
  public void endDocument() {
    try {
      this.writer.endDocument();
    } catch (SAXException sAXException) {
      throw new TxwException(sAXException);
    } 
  }
  
  public void flush() {}
  
  private static String getQName(String paramString1, String paramString2) {
    String str;
    if (paramString1 == null || paramString1.length() == 0) {
      str = paramString2;
    } else {
      str = paramString1 + ':' + paramString2;
    } 
    return str;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\txw2\output\SaxSerializer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */