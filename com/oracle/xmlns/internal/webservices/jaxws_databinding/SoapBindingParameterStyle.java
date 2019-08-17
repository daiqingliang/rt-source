package com.oracle.xmlns.internal.webservices.jaxws_databinding;

import javax.xml.bind.annotation.XmlEnum;
import javax.xml.bind.annotation.XmlType;

@XmlType(name = "soap-binding-parameter-style")
@XmlEnum
public static enum SoapBindingParameterStyle {
  BARE, WRAPPED;
  
  public String value() { return name(); }
  
  public static SoapBindingParameterStyle fromValue(String paramString) { return valueOf(paramString); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\oracle\xmlns\internal\webservices\jaxws_databinding\SoapBindingParameterStyle.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */