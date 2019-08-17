package com.oracle.webservices.internal.api.databinding;

import java.io.File;

public interface WSDLGenerator {
  WSDLGenerator inlineSchema(boolean paramBoolean);
  
  WSDLGenerator property(String paramString, Object paramObject);
  
  void generate(WSDLResolver paramWSDLResolver);
  
  void generate(File paramFile, String paramString);
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\oracle\webservices\internal\api\databinding\WSDLGenerator.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */