package com.sun.xml.internal.bind.v2.runtime.unmarshaller;

import com.sun.xml.internal.bind.WhiteSpaceProcessor;
import com.sun.xml.internal.fastinfoset.stax.StAXDocumentParser;
import javax.xml.stream.Location;
import javax.xml.stream.XMLStreamException;
import org.xml.sax.SAXException;

final class FastInfosetConnector extends StAXConnector {
  private final StAXDocumentParser fastInfosetStreamReader;
  
  private boolean textReported;
  
  private final Base64Data base64Data = new Base64Data();
  
  private final StringBuilder buffer = new StringBuilder();
  
  private final CharSequenceImpl charArray = new CharSequenceImpl();
  
  public FastInfosetConnector(StAXDocumentParser paramStAXDocumentParser, XmlVisitor paramXmlVisitor) {
    super(paramXmlVisitor);
    paramStAXDocumentParser.setStringInterning(true);
    this.fastInfosetStreamReader = paramStAXDocumentParser;
  }
  
  public void bridge() throws XMLStreamException {
    try {
      byte b = 0;
      int i = this.fastInfosetStreamReader.getEventType();
      if (i == 7)
        while (!this.fastInfosetStreamReader.isStartElement())
          i = this.fastInfosetStreamReader.next();  
      if (i != 1)
        throw new IllegalStateException("The current event is not START_ELEMENT\n but " + i); 
      handleStartDocument(this.fastInfosetStreamReader.getNamespaceContext());
      while (true) {
        switch (i) {
          case 1:
            handleStartElement();
            b++;
            break;
          case 2:
            b--;
            handleEndElement();
            if (b == 0)
              break; 
            break;
          case 4:
          case 6:
          case 12:
            if (this.predictor.expectText()) {
              i = this.fastInfosetStreamReader.peekNext();
              if (i == 2) {
                processNonIgnorableText();
                break;
              } 
              if (i == 1) {
                processIgnorableText();
                break;
              } 
              handleFragmentedCharacters();
            } 
            break;
        } 
        i = this.fastInfosetStreamReader.next();
      } 
      this.fastInfosetStreamReader.next();
      handleEndDocument();
    } catch (SAXException sAXException) {
      throw new XMLStreamException(sAXException);
    } 
  }
  
  protected Location getCurrentLocation() { return this.fastInfosetStreamReader.getLocation(); }
  
  protected String getCurrentQName() { return this.fastInfosetStreamReader.getNameString(); }
  
  private void handleStartElement() throws XMLStreamException {
    processUnreportedText();
    for (byte b = 0; b < this.fastInfosetStreamReader.accessNamespaceCount(); b++)
      this.visitor.startPrefixMapping(this.fastInfosetStreamReader.getNamespacePrefix(b), this.fastInfosetStreamReader.getNamespaceURI(b)); 
    this.tagName.uri = this.fastInfosetStreamReader.accessNamespaceURI();
    this.tagName.local = this.fastInfosetStreamReader.accessLocalName();
    this.tagName.atts = this.fastInfosetStreamReader.getAttributesHolder();
    this.visitor.startElement(this.tagName);
  }
  
  private void handleFragmentedCharacters() throws XMLStreamException {
    this.buffer.setLength(0);
    this.buffer.append(this.fastInfosetStreamReader.getTextCharacters(), this.fastInfosetStreamReader.getTextStart(), this.fastInfosetStreamReader.getTextLength());
    while (true) {
      switch (this.fastInfosetStreamReader.peekNext()) {
        case 1:
          processBufferedText(true);
          return;
        case 2:
          processBufferedText(false);
          return;
        case 4:
        case 6:
        case 12:
          this.fastInfosetStreamReader.next();
          this.buffer.append(this.fastInfosetStreamReader.getTextCharacters(), this.fastInfosetStreamReader.getTextStart(), this.fastInfosetStreamReader.getTextLength());
          continue;
      } 
      this.fastInfosetStreamReader.next();
    } 
  }
  
  private void handleEndElement() throws XMLStreamException {
    processUnreportedText();
    this.tagName.uri = this.fastInfosetStreamReader.accessNamespaceURI();
    this.tagName.local = this.fastInfosetStreamReader.accessLocalName();
    this.visitor.endElement(this.tagName);
    for (int i = this.fastInfosetStreamReader.accessNamespaceCount() - 1; i >= 0; i--)
      this.visitor.endPrefixMapping(this.fastInfosetStreamReader.getNamespacePrefix(i)); 
  }
  
  private void processNonIgnorableText() throws XMLStreamException {
    this.textReported = true;
    boolean bool = (this.fastInfosetStreamReader.getTextAlgorithmBytes() != null) ? 1 : 0;
    if (bool && this.fastInfosetStreamReader.getTextAlgorithmIndex() == 1) {
      this.base64Data.set(this.fastInfosetStreamReader.getTextAlgorithmBytesClone(), null);
      this.visitor.text(this.base64Data);
    } else {
      if (bool)
        this.fastInfosetStreamReader.getText(); 
      this.charArray.set();
      this.visitor.text(this.charArray);
    } 
  }
  
  private void processIgnorableText() throws XMLStreamException {
    boolean bool = (this.fastInfosetStreamReader.getTextAlgorithmBytes() != null) ? 1 : 0;
    if (bool && this.fastInfosetStreamReader.getTextAlgorithmIndex() == 1) {
      this.base64Data.set(this.fastInfosetStreamReader.getTextAlgorithmBytesClone(), null);
      this.visitor.text(this.base64Data);
      this.textReported = true;
    } else {
      if (bool)
        this.fastInfosetStreamReader.getText(); 
      this.charArray.set();
      if (!WhiteSpaceProcessor.isWhiteSpace(this.charArray)) {
        this.visitor.text(this.charArray);
        this.textReported = true;
      } 
    } 
  }
  
  private void processBufferedText(boolean paramBoolean) throws SAXException {
    if (!paramBoolean || !WhiteSpaceProcessor.isWhiteSpace(this.buffer)) {
      this.visitor.text(this.buffer);
      this.textReported = true;
    } 
  }
  
  private void processUnreportedText() throws XMLStreamException {
    if (!this.textReported && this.predictor.expectText())
      this.visitor.text(""); 
    this.textReported = false;
  }
  
  private final class CharSequenceImpl implements CharSequence {
    char[] ch;
    
    int start;
    
    int length;
    
    CharSequenceImpl() {}
    
    CharSequenceImpl(char[] param1ArrayOfChar, int param1Int1, int param1Int2) {
      this.ch = param1ArrayOfChar;
      this.start = param1Int1;
      this.length = param1Int2;
    }
    
    public void set() throws XMLStreamException {
      this.ch = FastInfosetConnector.this.fastInfosetStreamReader.getTextCharacters();
      this.start = FastInfosetConnector.this.fastInfosetStreamReader.getTextStart();
      this.length = FastInfosetConnector.this.fastInfosetStreamReader.getTextLength();
    }
    
    public final int length() { return this.length; }
    
    public final char charAt(int param1Int) { return this.ch[this.start + param1Int]; }
    
    public final CharSequence subSequence(int param1Int1, int param1Int2) { return new CharSequenceImpl(FastInfosetConnector.this, this.ch, this.start + param1Int1, param1Int2 - param1Int1); }
    
    public String toString() { return new String(this.ch, this.start, this.length); }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\bind\v2\runtim\\unmarshaller\FastInfosetConnector.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */