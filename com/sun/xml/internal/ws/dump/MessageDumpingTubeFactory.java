package com.sun.xml.internal.ws.dump;

import com.sun.xml.internal.ws.api.pipe.Tube;
import com.sun.xml.internal.ws.assembler.dev.ClientTubelineAssemblyContext;
import com.sun.xml.internal.ws.assembler.dev.ServerTubelineAssemblyContext;
import com.sun.xml.internal.ws.assembler.dev.TubeFactory;
import javax.xml.ws.WebServiceException;

public final class MessageDumpingTubeFactory implements TubeFactory {
  public Tube createTube(ClientTubelineAssemblyContext paramClientTubelineAssemblyContext) throws WebServiceException {
    MessageDumpingFeature messageDumpingFeature = (MessageDumpingFeature)paramClientTubelineAssemblyContext.getBinding().getFeature(MessageDumpingFeature.class);
    return (messageDumpingFeature != null) ? new MessageDumpingTube(paramClientTubelineAssemblyContext.getTubelineHead(), messageDumpingFeature) : paramClientTubelineAssemblyContext.getTubelineHead();
  }
  
  public Tube createTube(ServerTubelineAssemblyContext paramServerTubelineAssemblyContext) throws WebServiceException {
    MessageDumpingFeature messageDumpingFeature = (MessageDumpingFeature)paramServerTubelineAssemblyContext.getEndpoint().getBinding().getFeature(MessageDumpingFeature.class);
    return (messageDumpingFeature != null) ? new MessageDumpingTube(paramServerTubelineAssemblyContext.getTubelineHead(), messageDumpingFeature) : paramServerTubelineAssemblyContext.getTubelineHead();
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\dump\MessageDumpingTubeFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */