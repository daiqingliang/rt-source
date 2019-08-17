package com.sun.xml.internal.txw2.output;

import java.io.PrintStream;

public class DumpSerializer implements XmlSerializer {
  private final PrintStream out;
  
  public DumpSerializer(PrintStream paramPrintStream) { this.out = paramPrintStream; }
  
  public void beginStartTag(String paramString1, String paramString2, String paramString3) { this.out.println('<' + paramString3 + ':' + paramString2); }
  
  public void writeAttribute(String paramString1, String paramString2, String paramString3, StringBuilder paramStringBuilder) { this.out.println('@' + paramString3 + ':' + paramString2 + '=' + paramStringBuilder); }
  
  public void writeXmlns(String paramString1, String paramString2) { this.out.println("xmlns:" + paramString1 + '=' + paramString2); }
  
  public void endStartTag(String paramString1, String paramString2, String paramString3) { this.out.println('>'); }
  
  public void endTag() { this.out.println("</  >"); }
  
  public void text(StringBuilder paramStringBuilder) { this.out.println(paramStringBuilder); }
  
  public void cdata(StringBuilder paramStringBuilder) {
    this.out.println("<![CDATA[");
    this.out.println(paramStringBuilder);
    this.out.println("]]>");
  }
  
  public void comment(StringBuilder paramStringBuilder) {
    this.out.println("<!--");
    this.out.println(paramStringBuilder);
    this.out.println("-->");
  }
  
  public void startDocument() { this.out.println("<?xml?>"); }
  
  public void endDocument() { this.out.println("done"); }
  
  public void flush() { this.out.println("flush"); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\txw2\output\DumpSerializer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */