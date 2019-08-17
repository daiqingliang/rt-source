package com.sun.xml.internal.ws.assembler.dev;

import com.sun.istack.internal.NotNull;
import com.sun.istack.internal.Nullable;
import com.sun.xml.internal.ws.api.model.SEIModel;
import com.sun.xml.internal.ws.api.model.wsdl.WSDLPort;
import com.sun.xml.internal.ws.api.pipe.Codec;
import com.sun.xml.internal.ws.api.pipe.ServerTubeAssemblerContext;
import com.sun.xml.internal.ws.api.pipe.Tube;
import com.sun.xml.internal.ws.api.server.WSEndpoint;
import com.sun.xml.internal.ws.policy.PolicyMap;

public interface ServerTubelineAssemblyContext extends TubelineAssemblyContext {
  @NotNull
  Codec getCodec();
  
  @NotNull
  WSEndpoint getEndpoint();
  
  PolicyMap getPolicyMap();
  
  @Nullable
  SEIModel getSEIModel();
  
  @NotNull
  Tube getTerminalTube();
  
  ServerTubeAssemblerContext getWrappedContext();
  
  @Nullable
  WSDLPort getWsdlPort();
  
  boolean isPolicyAvailable();
  
  boolean isSynchronous();
  
  void setCodec(@NotNull Codec paramCodec);
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\assembler\dev\ServerTubelineAssemblyContext.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */