package com.sun.xml.internal.ws.api.wsdl.parser;

import com.sun.istack.internal.NotNull;
import java.util.List;
import javax.xml.transform.Source;

public abstract class ServiceDescriptor {
  @NotNull
  public abstract List<? extends Source> getWSDLs();
  
  @NotNull
  public abstract List<? extends Source> getSchemas();
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\api\wsdl\parser\ServiceDescriptor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */