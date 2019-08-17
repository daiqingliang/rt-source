package com.sun.xml.internal.bind.v2.runtime.output;

import com.sun.xml.internal.bind.marshaller.CharacterEscapeHandler;
import com.sun.xml.internal.bind.v2.runtime.Name;
import java.io.IOException;
import java.io.OutputStream;
import javax.xml.stream.XMLStreamException;
import org.xml.sax.SAXException;

public final class IndentingUTF8XmlOutput extends UTF8XmlOutput {
  private final Encoded indent8;
  
  private final int unitLen;
  
  private int depth = 0;
  
  private boolean seenText = false;
  
  public IndentingUTF8XmlOutput(OutputStream paramOutputStream, String paramString, Encoded[] paramArrayOfEncoded, CharacterEscapeHandler paramCharacterEscapeHandler) {
    super(paramOutputStream, paramArrayOfEncoded, paramCharacterEscapeHandler);
    if (paramString != null) {
      Encoded encoded = new Encoded(paramString);
      this.indent8 = new Encoded();
      this.indent8.ensureSize(encoded.len * 8);
      this.unitLen = encoded.len;
      for (int i = 0; i < 8; i++)
        System.arraycopy(encoded.buf, 0, this.indent8.buf, this.unitLen * i, this.unitLen); 
    } else {
      this.indent8 = null;
      this.unitLen = 0;
    } 
  }
  
  public void beginStartTag(int paramInt, String paramString) throws IOException {
    indentStartTag();
    super.beginStartTag(paramInt, paramString);
  }
  
  public void beginStartTag(Name paramName) throws IOException {
    indentStartTag();
    super.beginStartTag(paramName);
  }
  
  private void indentStartTag() throws IOException {
    closeStartTag();
    if (!this.seenText)
      printIndent(); 
    this.depth++;
    this.seenText = false;
  }
  
  public void endTag(Name paramName) throws IOException {
    indentEndTag();
    super.endTag(paramName);
  }
  
  public void endTag(int paramInt, String paramString) throws IOException {
    indentEndTag();
    super.endTag(paramInt, paramString);
  }
  
  private void indentEndTag() throws IOException {
    this.depth--;
    if (!this.closeStartTagPending && !this.seenText)
      printIndent(); 
    this.seenText = false;
  }
  
  private void printIndent() throws IOException {
    write(10);
    int i = this.depth % 8;
    write(this.indent8.buf, 0, i * this.unitLen);
    for (i >>= 3; i > 0; i--)
      this.indent8.write(this); 
  }
  
  public void text(String paramString, boolean paramBoolean) throws IOException {
    this.seenText = true;
    super.text(paramString, paramBoolean);
  }
  
  public void text(Pcdata paramPcdata, boolean paramBoolean) throws IOException {
    this.seenText = true;
    super.text(paramPcdata, paramBoolean);
  }
  
  public void endDocument(boolean paramBoolean) throws IOException, SAXException, XMLStreamException {
    write(10);
    super.endDocument(paramBoolean);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\bind\v2\runtime\output\IndentingUTF8XmlOutput.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */