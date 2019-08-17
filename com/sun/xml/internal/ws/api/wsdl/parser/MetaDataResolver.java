package com.sun.xml.internal.ws.api.wsdl.parser;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import java.net.URI;

public abstract class MetaDataResolver {
  @Nullable
  public abstract ServiceDescriptor resolve(@NotNull URI paramURI);
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\api\wsdl\parser\MetaDataResolver.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */