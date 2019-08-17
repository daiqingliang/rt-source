package com.sun.xml.internal.txw2.output;

import java.io.Writer;
import java.util.Stack;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;

public class DataWriter extends XMLWriter {
  private static final Object SEEN_NOTHING = new Object();
  
  private static final Object SEEN_ELEMENT = new Object();
  
  private static final Object SEEN_DATA = new Object();
  
  private Object state = SEEN_NOTHING;
  
  private Stack stateStack = new Stack();
  
  private String indentStep = "";
  
  private int depth = 0;
  
  public DataWriter(Writer paramWriter, String paramString, CharacterEscapeHandler paramCharacterEscapeHandler) { super(paramWriter, paramString, paramCharacterEscapeHandler); }
  
  public DataWriter(Writer paramWriter, String paramString) { this(paramWriter, paramString, DumbEscapeHandler.theInstance); }
  
  public DataWriter(Writer paramWriter) { this(paramWriter, null, DumbEscapeHandler.theInstance); }
  
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
  
  public void reset() {
    this.depth = 0;
    this.state = SEEN_NOTHING;
    this.stateStack = new Stack();
    super.reset();
  }
  
  public void startElement(String paramString1, String paramString2, String paramString3, Attributes paramAttributes) throws SAXException {
    this.stateStack.push(SEEN_ELEMENT);
    this.state = SEEN_NOTHING;
    if (this.depth > 0)
      characters("\n"); 
    doIndent();
    super.startElement(paramString1, paramString2, paramString3, paramAttributes);
    this.depth++;
  }
  
  public void endElement(String paramString1, String paramString2, String paramString3) throws SAXException {
    this.depth--;
    if (this.state == SEEN_ELEMENT) {
      characters("\n");
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
      characters("\n"); 
    doIndent();
    super.comment(paramArrayOfChar, paramInt1, paramInt2);
  }
  
  private void doIndent() {
    if (this.depth > 0) {
      char[] arrayOfChar = this.indentStep.toCharArray();
      for (byte b = 0; b < this.depth; b++)
        characters(arrayOfChar, 0, arrayOfChar.length); 
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\txw2\output\DataWriter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */