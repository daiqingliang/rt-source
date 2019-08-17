package com.sun.xml.internal.txw2.output;

import java.util.Stack;
import javax.xml.namespace.NamespaceContext;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;

public class IndentingXMLStreamWriter extends DelegatingXMLStreamWriter {
  private static final Object SEEN_NOTHING = new Object();
  
  private static final Object SEEN_ELEMENT = new Object();
  
  private static final Object SEEN_DATA = new Object();
  
  private Object state = SEEN_NOTHING;
  
  private Stack<Object> stateStack = new Stack();
  
  private String indentStep = "  ";
  
  private int depth = 0;
  
  public IndentingXMLStreamWriter(XMLStreamWriter paramXMLStreamWriter) { super(paramXMLStreamWriter); }
  
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
  
  private void onStartElement() throws XMLStreamException {
    this.stateStack.push(SEEN_ELEMENT);
    this.state = SEEN_NOTHING;
    if (this.depth > 0)
      super.writeCharacters("\n"); 
    doIndent();
    this.depth++;
  }
  
  private void onEndElement() throws XMLStreamException {
    this.depth--;
    if (this.state == SEEN_ELEMENT) {
      super.writeCharacters("\n");
      doIndent();
    } 
    this.state = this.stateStack.pop();
  }
  
  private void onEmptyElement() throws XMLStreamException {
    this.state = SEEN_ELEMENT;
    if (this.depth > 0)
      super.writeCharacters("\n"); 
    doIndent();
  }
  
  private void doIndent() throws XMLStreamException {
    if (this.depth > 0)
      for (byte b = 0; b < this.depth; b++)
        super.writeCharacters(this.indentStep);  
  }
  
  public void writeStartDocument() throws XMLStreamException {
    super.writeStartDocument();
    super.writeCharacters("\n");
  }
  
  public void writeStartDocument(String paramString) {
    super.writeStartDocument(paramString);
    super.writeCharacters("\n");
  }
  
  public void writeStartDocument(String paramString1, String paramString2) throws XMLStreamException {
    super.writeStartDocument(paramString1, paramString2);
    super.writeCharacters("\n");
  }
  
  public void writeStartElement(String paramString) {
    onStartElement();
    super.writeStartElement(paramString);
  }
  
  public void writeStartElement(String paramString1, String paramString2) throws XMLStreamException {
    onStartElement();
    super.writeStartElement(paramString1, paramString2);
  }
  
  public void writeStartElement(String paramString1, String paramString2, String paramString3) throws XMLStreamException {
    onStartElement();
    super.writeStartElement(paramString1, paramString2, paramString3);
  }
  
  public void writeEmptyElement(String paramString1, String paramString2) throws XMLStreamException {
    onEmptyElement();
    super.writeEmptyElement(paramString1, paramString2);
  }
  
  public void writeEmptyElement(String paramString1, String paramString2, String paramString3) throws XMLStreamException {
    onEmptyElement();
    super.writeEmptyElement(paramString1, paramString2, paramString3);
  }
  
  public void writeEmptyElement(String paramString) {
    onEmptyElement();
    super.writeEmptyElement(paramString);
  }
  
  public void writeEndElement() throws XMLStreamException {
    onEndElement();
    super.writeEndElement();
  }
  
  public void writeCharacters(String paramString) {
    this.state = SEEN_DATA;
    super.writeCharacters(paramString);
  }
  
  public void writeCharacters(char[] paramArrayOfChar, int paramInt1, int paramInt2) throws XMLStreamException {
    this.state = SEEN_DATA;
    super.writeCharacters(paramArrayOfChar, paramInt1, paramInt2);
  }
  
  public void writeCData(String paramString) {
    this.state = SEEN_DATA;
    super.writeCData(paramString);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\txw2\output\IndentingXMLStreamWriter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */