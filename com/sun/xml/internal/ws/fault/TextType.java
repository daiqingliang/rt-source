package com.sun.xml.internal.ws.fault;

import java.util.Locale;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.XmlValue;

@XmlType(name = "TextType", namespace = "http://www.w3.org/2003/05/soap-envelope")
class TextType {
  @XmlValue
  private String text;
  
  @XmlAttribute(name = "lang", namespace = "http://www.w3.org/XML/1998/namespace", required = true)
  private String lang;
  
  TextType() {}
  
  TextType(String paramString) {
    this.text = paramString;
    this.lang = Locale.getDefault().getLanguage();
  }
  
  String getText() { return this.text; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\fault\TextType.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */