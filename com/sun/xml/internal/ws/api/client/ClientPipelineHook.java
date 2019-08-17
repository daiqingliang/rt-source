package com.sun.xml.internal.ws.api.client;

import com.sun.istack.internal.NotNull;
import com.sun.xml.internal.ws.api.pipe.ClientPipeAssemblerContext;
import com.sun.xml.internal.ws.api.pipe.Pipe;

public abstract class ClientPipelineHook {
  @NotNull
  public Pipe createSecurityPipe(ClientPipeAssemblerContext paramClientPipeAssemblerContext, @NotNull Pipe paramPipe) { return paramPipe; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\api\client\ClientPipelineHook.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */