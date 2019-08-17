package com.sun.org.apache.xml.internal.serialize;

import java.io.OutputStream;
import java.io.Writer;

public class XHTMLSerializer extends HTMLSerializer {
  public XHTMLSerializer() { super(true, new OutputFormat("xhtml", null, false)); }
  
  public XHTMLSerializer(OutputFormat paramOutputFormat) { super(true, (paramOutputFormat != null) ? paramOutputFormat : new OutputFormat("xhtml", null, false)); }
  
  public XHTMLSerializer(Writer paramWriter, OutputFormat paramOutputFormat) {
    super(true, (paramOutputFormat != null) ? paramOutputFormat : new OutputFormat("xhtml", null, false));
    setOutputCharStream(paramWriter);
  }
  
  public XHTMLSerializer(OutputStream paramOutputStream, OutputFormat paramOutputFormat) {
    super(true, (paramOutputFormat != null) ? paramOutputFormat : new OutputFormat("xhtml", null, false));
    setOutputByteStream(paramOutputStream);
  }
  
  public void setOutputFormat(OutputFormat paramOutputFormat) { super.setOutputFormat((paramOutputFormat != null) ? paramOutputFormat : new OutputFormat("xhtml", null, false)); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xml\internal\serialize\XHTMLSerializer.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */