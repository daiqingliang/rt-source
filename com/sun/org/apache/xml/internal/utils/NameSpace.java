package com.sun.org.apache.xml.internal.utils;

import java.io.Serializable;

public class NameSpace implements Serializable {
  static final long serialVersionUID = 1471232939184881839L;
  
  public NameSpace m_next = null;
  
  public String m_prefix;
  
  public String m_uri;
  
  public NameSpace(String paramString1, String paramString2) {
    this.m_prefix = paramString1;
    this.m_uri = paramString2;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\org\apache\xml\interna\\utils\NameSpace.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */