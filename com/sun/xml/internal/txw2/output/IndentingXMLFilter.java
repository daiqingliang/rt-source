package com.sun.xml.internal.txw2.output;

import java.util.Stack;
import org.xml.sax.Attributes;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import org.xml.sax.ext.LexicalHandler;
import org.xml.sax.helpers.XMLFilterImpl;

public class IndentingXMLFilter extends XMLFilterImpl implements LexicalHandler {
  private LexicalHandler lexical;
  
  private static final char[] NEWLINE = { '\n' };
  
  private static final Object SEEN_NOTHING = new Object();
  
  private static final Object SEEN_ELEMENT = new Object();
  
  private static final Object SEEN_DATA = new Object();
  
  private Object state = SEEN_NOTHING;
  
  private Stack<Object> stateStack = new Stack();
  
  private String indentStep = "";
  
  private int depth = 0;
  
  public IndentingXMLFilter() {}
  
  public IndentingXMLFilter(ContentHandler paramContentHandler) { setContentHandler(paramContentHandler); }
  
  public IndentingXMLFilter(ContentHandler paramContentHandler, LexicalHandler paramLexicalHandler) {
    setContentHandler(paramContentHandler);
    setLexicalHandler(paramLexicalHandler);
  }
  
  public LexicalHandler getLexicalHandler() { return this.lexical; }
  
  public void setLexicalHandler(LexicalHandler paramLexicalHandler) { this.lexical = paramLexicalHandler; }
  
  public int getIndentStep() { return this.indentStep.length(); }
  
  public void setIndentStep(int paramInt) {
    StringBuilder stringBuilder = new StringBuilder();
    while (paramInt > 0) {
      stringBuilder.append(' ');
      paramInt--;
    } 
    setIndentStep(stringBuilder.toString());
  }
  
  public void setIndentStep(String paramString) { this.indentStep = paramString; }
  
  public void startElement(String paramString1, String paramString2, String paramString3, Attributes paramAttributes) throws SAXException {
    this.stateStack.push(SEEN_ELEMENT);
    this.state = SEEN_NOTHING;
    if (this.depth > 0)
      writeNewLine(); 
    doIndent();
    super.startElement(paramString1, paramString2, paramString3, paramAttributes);
    this.depth++;
  }
  
  private void writeNewLine() { super.characters(NEWLINE, 0, NEWLINE.length); }
  
  public void endElement(String paramString1, String paramString2, String paramString3) throws SAXException {
    this.depth--;
    if (this.state == SEEN_ELEMENT) {
      writeNewLine();
      doIndent();
    } 
    super.endElement(paramString1, paramString2, paramString3);
    this.state = this.stateStack.pop();
  }
  
  public void characters(char[] paramArrayOfChar, int paramInt1, int paramInt2) throws SAXException {
    this.state = SEEN_DATA;
    super.characters(paramArrayOfChar, paramInt1, paramInt2);
  }
  
  public void comment(char[] paramArrayOfChar, int paramInt1, int paramInt2) throws SAXException {
    if (this.depth > 0)
      writeNewLine(); 
    doIndent();
    if (this.lexical != null)
      this.lexical.comment(paramArrayOfChar, paramInt1, paramInt2); 
  }
  
  public void startDTD(String paramString1, String paramString2, String paramString3) throws SAXException {
    if (this.lexical != null)
      this.lexical.startDTD(paramString1, paramString2, paramString3); 
  }
  
  public void endDTD() {
    if (this.lexical != null)
      this.lexical.endDTD(); 
  }
  
  public void startEntity(String paramString) {
    if (this.lexical != null)
      this.lexical.startEntity(paramString); 
  }
  
  public void endEntity(String paramString) {
    if (this.lexical != null)
      this.lexical.endEntity(paramString); 
  }
  
  public void startCDATA() {
    if (this.lexical != null)
      this.lexical.startCDATA(); 
  }
  
  public void endCDATA() {
    if (this.lexical != null)
      this.lexical.endCDATA(); 
  }
  
  private void doIndent() {
    if (this.depth > 0) {
      char[] arrayOfChar = this.indentStep.toCharArray();
      for (byte b = 0; b < this.depth; b++)
        characters(arrayOfChar, 0, arrayOfChar.length); 
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\txw2\output\IndentingXMLFilter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */