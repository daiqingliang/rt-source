package com.oracle.xmlns.internal.webservices.jaxws_databinding;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;

@XmlType(name = "soap-binding-style")
@XmlEnum
public static enum SoapBindingStyle {
  DOCUMENT, RPC;
  
  public String value() { return name(); }
  
  public static SoapBindingStyle fromValue(String paramString) { return valueOf(paramString); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\oracle\xmlns\internal\webservices\jaxws_databinding\SoapBindingStyle.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */