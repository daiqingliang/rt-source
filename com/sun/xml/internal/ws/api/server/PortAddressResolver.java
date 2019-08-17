package com.sun.xml.internal.ws.api.server;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import javax.xml.namespace.QName;

public abstract class PortAddressResolver {
  @Nullable
  public abstract String getAddressFor(@NotNull QName paramQName, @NotNull String paramString);
  
  @Nullable
  public String getAddressFor(@NotNull QName paramQName, @NotNull String paramString1, String paramString2) { return getAddressFor(paramQName, paramString1); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\api\server\PortAddressResolver.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */