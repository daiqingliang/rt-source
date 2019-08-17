package com.oracle.xmlns.internal.webservices.jaxws_databinding;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;

@XmlType(name = "soap-binding-use")
@XmlEnum
public static enum SoapBindingUse {
  LITERAL, ENCODED;
  
  public String value() { return name(); }
  
  public static SoapBindingUse fromValue(String paramString) { return valueOf(paramString); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\oracle\xmlns\internal\webservices\jaxws_databinding\SoapBindingUse.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */