package com.sun.xml.internal.stream.events;

import com.sun.xml.internal.stream.dtd.nonvalidating.XMLNotationDecl;
import java.io.IOException;
import java.io.Writer;
import javax.xml.stream.events.NotationDeclaration;

public class NotationDeclarationImpl extends DummyEvent implements NotationDeclaration {
  String fName = null;
  
  String fPublicId = null;
  
  String fSystemId = null;
  
  public NotationDeclarationImpl() { setEventType(14); }
  
  public NotationDeclarationImpl(String paramString1, String paramString2, String paramString3) {
    this.fName = paramString1;
    this.fPublicId = paramString2;
    this.fSystemId = paramString3;
    setEventType(14);
  }
  
  public NotationDeclarationImpl(XMLNotationDecl paramXMLNotationDecl) {
    this.fName = paramXMLNotationDecl.name;
    this.fPublicId = paramXMLNotationDecl.publicId;
    this.fSystemId = paramXMLNotationDecl.systemId;
    setEventType(14);
  }
  
  public String getName() { return this.fName; }
  
  public String getPublicId() { return this.fPublicId; }
  
  public String getSystemId() { return this.fSystemId; }
  
  void setPublicId(String paramString) { this.fPublicId = paramString; }
  
  void setSystemId(String paramString) { this.fSystemId = paramString; }
  
  void setName(String paramString) { this.fName = paramString; }
  
  protected void writeAsEncodedUnicodeEx(Writer paramWriter) throws IOException {
    paramWriter.write("<!NOTATION ");
    paramWriter.write(getName());
    if (this.fPublicId != null) {
      paramWriter.write(" PUBLIC \"");
      paramWriter.write(this.fPublicId);
      paramWriter.write("\"");
    } else if (this.fSystemId != null) {
      paramWriter.write(" SYSTEM");
      paramWriter.write(" \"");
      paramWriter.write(this.fSystemId);
      paramWriter.write("\"");
    } 
    paramWriter.write(62);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\stream\events\NotationDeclarationImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */