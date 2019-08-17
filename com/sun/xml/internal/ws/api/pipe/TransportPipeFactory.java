package com.sun.xml.internal.ws.api.pipe;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import com.sun.xml.internal.ws.api.pipe.helper.PipeAdapter;

public abstract class TransportPipeFactory {
  public abstract Pipe doCreate(@NotNull ClientPipeAssemblerContext paramClientPipeAssemblerContext);
  
  public static Pipe create(@Nullable ClassLoader paramClassLoader, @NotNull ClientPipeAssemblerContext paramClientPipeAssemblerContext) { return PipeAdapter.adapt(TransportTubeFactory.create(paramClassLoader, paramClientPipeAssemblerContext)); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\api\pipe\TransportPipeFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */