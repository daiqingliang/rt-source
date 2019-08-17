package com.sun.xml.internal.ws.api;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import javax.xml.ws.WebServiceException;

public abstract class BindingIDFactory {
  @Nullable
  public abstract BindingID parse(@NotNull String paramString) throws WebServiceException;
  
  @Nullable
  public BindingID create(@NotNull String paramString, @NotNull SOAPVersion paramSOAPVersion) throws WebServiceException { return null; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\api\BindingIDFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */