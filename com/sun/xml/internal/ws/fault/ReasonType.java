package com.sun.xml.internal.ws.fault;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlElements;

class ReasonType {
  @XmlElements({@XmlElement(name = "Text", namespace = "http://www.w3.org/2003/05/soap-envelope", type = TextType.class)})
  private final List<TextType> text = new ArrayList();
  
  ReasonType() {}
  
  ReasonType(String paramString) { this.text.add(new TextType(paramString)); }
  
  List<TextType> texts() { return this.text; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\fault\ReasonType.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */