package com.sun.xml.internal.ws.dump;

import com.sun.xml.internal.ws.api.message.Packet;
import com.sun.xml.internal.ws.api.pipe.Fiber;
import com.sun.xml.internal.ws.api.pipe.NextAction;
import com.sun.xml.internal.ws.api.pipe.Tube;
import com.sun.xml.internal.ws.api.pipe.TubeCloner;
import com.sun.xml.internal.ws.api.pipe.helper.AbstractFilterTubeImpl;
import com.sun.xml.internal.ws.api.pipe.helper.AbstractTubeImpl;
import com.sun.xml.internal.ws.commons.xmlutil.Converter;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.logging.Logger;

final class MessageDumpingTube extends AbstractFilterTubeImpl {
  static final String DEFAULT_MSGDUMP_LOGGING_ROOT = "com.sun.xml.internal.ws.messagedump";
  
  private static final AtomicInteger ID_GENERATOR = new AtomicInteger(0);
  
  private final MessageDumper messageDumper;
  
  private final int tubeId;
  
  private final MessageDumpingFeature messageDumpingFeature;
  
  MessageDumpingTube(Tube paramTube, MessageDumpingFeature paramMessageDumpingFeature) {
    super(paramTube);
    this.messageDumpingFeature = paramMessageDumpingFeature;
    this.tubeId = ID_GENERATOR.incrementAndGet();
    this.messageDumper = new MessageDumper("MesageDumpingTube", Logger.getLogger(paramMessageDumpingFeature.getMessageLoggingRoot()), paramMessageDumpingFeature.getMessageLoggingLevel());
  }
  
  MessageDumpingTube(MessageDumpingTube paramMessageDumpingTube, TubeCloner paramTubeCloner) {
    super(paramMessageDumpingTube, paramTubeCloner);
    this.messageDumpingFeature = paramMessageDumpingTube.messageDumpingFeature;
    this.tubeId = ID_GENERATOR.incrementAndGet();
    this.messageDumper = paramMessageDumpingTube.messageDumper;
  }
  
  public MessageDumpingTube copy(TubeCloner paramTubeCloner) { return new MessageDumpingTube(this, paramTubeCloner); }
  
  public NextAction processRequest(Packet paramPacket) {
    dump(MessageDumper.MessageType.Request, Converter.toString(paramPacket), (Fiber.current()).owner.id);
    return super.processRequest(paramPacket);
  }
  
  public NextAction processResponse(Packet paramPacket) {
    dump(MessageDumper.MessageType.Response, Converter.toString(paramPacket), (Fiber.current()).owner.id);
    return super.processResponse(paramPacket);
  }
  
  public NextAction processException(Throwable paramThrowable) {
    dump(MessageDumper.MessageType.Exception, Converter.toString(paramThrowable), (Fiber.current()).owner.id);
    return super.processException(paramThrowable);
  }
  
  protected final void dump(MessageDumper.MessageType paramMessageType, String paramString1, String paramString2) {
    String str;
    if (this.messageDumpingFeature.getMessageLoggingStatus()) {
      this.messageDumper.setLoggingLevel(this.messageDumpingFeature.getMessageLoggingLevel());
      str = this.messageDumper.dump(paramMessageType, MessageDumper.ProcessingState.Received, paramString1, this.tubeId, paramString2);
    } else {
      str = this.messageDumper.createLogMessage(paramMessageType, MessageDumper.ProcessingState.Received, this.tubeId, paramString2, paramString1);
    } 
    this.messageDumpingFeature.offerMessage(str);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\dump\MessageDumpingTube.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */