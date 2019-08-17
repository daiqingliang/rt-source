package com.sun.xml.internal.ws.api.pipe;

import com.sun.istack.internal.NotNull;

public interface TubelineAssembler {
  @NotNull
  Tube createClient(@NotNull ClientTubeAssemblerContext paramClientTubeAssemblerContext);
  
  @NotNull
  Tube createServer(@NotNull ServerTubeAssemblerContext paramServerTubeAssemblerContext);
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\api\pipe\TubelineAssembler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */