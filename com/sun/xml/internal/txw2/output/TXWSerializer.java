package com.sun.xml.internal.txw2.output;

import com.sun.xml.internal.txw2.TypedXmlWriter;

public final class TXWSerializer implements XmlSerializer {
  public final TypedXmlWriter txw;
  
  public TXWSerializer(TypedXmlWriter paramTypedXmlWriter) { this.txw = paramTypedXmlWriter; }
  
  public void startDocument() { throw new UnsupportedOperationException(); }
  
  public void endDocument() { throw new UnsupportedOperationException(); }
  
  public void beginStartTag(String paramString1, String paramString2, String paramString3) { throw new UnsupportedOperationException(); }
  
  public void writeAttribute(String paramString1, String paramString2, String paramString3, StringBuilder paramStringBuilder) { throw new UnsupportedOperationException(); }
  
  public void writeXmlns(String paramString1, String paramString2) { throw new UnsupportedOperationException(); }
  
  public void endStartTag(String paramString1, String paramString2, String paramString3) { throw new UnsupportedOperationException(); }
  
  public void endTag() { throw new UnsupportedOperationException(); }
  
  public void text(StringBuilder paramStringBuilder) { throw new UnsupportedOperationException(); }
  
  public void cdata(StringBuilder paramStringBuilder) { throw new UnsupportedOperationException(); }
  
  public void comment(StringBuilder paramStringBuilder) { throw new UnsupportedOperationException(); }
  
  public void flush() { throw new UnsupportedOperationException(); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\txw2\output\TXWSerializer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */