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
import java.util.logging.Level;
import java.util.logging.Logger;

public class LoggingDumpTube extends AbstractFilterTubeImpl {
  private static final AtomicInteger ID_GENERATOR = new AtomicInteger(0);
  
  private MessageDumper messageDumper;
  
  private final Level loggingLevel;
  
  private final Position position;
  
  private final int tubeId;
  
  public LoggingDumpTube(Level paramLevel, Position paramPosition, Tube paramTube) {
    super(paramTube);
    this.position = paramPosition;
    this.loggingLevel = paramLevel;
    this.tubeId = ID_GENERATOR.incrementAndGet();
  }
  
  public void setLoggedTubeName(String paramString) {
    assert this.messageDumper == null;
    this.messageDumper = new MessageDumper(paramString, Logger.getLogger(paramString), this.loggingLevel);
  }
  
  private LoggingDumpTube(LoggingDumpTube paramLoggingDumpTube, TubeCloner paramTubeCloner) {
    super(paramLoggingDumpTube, paramTubeCloner);
    this.messageDumper = paramLoggingDumpTube.messageDumper;
    this.loggingLevel = paramLoggingDumpTube.loggingLevel;
    this.position = paramLoggingDumpTube.position;
    this.tubeId = ID_GENERATOR.incrementAndGet();
  }
  
  public LoggingDumpTube copy(TubeCloner paramTubeCloner) { return new LoggingDumpTube(this, paramTubeCloner); }
  
  public NextAction processRequest(Packet paramPacket) {
    if (this.messageDumper.isLoggable()) {
      Packet packet = (paramPacket != null) ? paramPacket.copy(true) : null;
      this.messageDumper.dump(MessageDumper.MessageType.Request, this.position.requestState, Converter.toString(packet), this.tubeId, (Fiber.current()).owner.id);
    } 
    return super.processRequest(paramPacket);
  }
  
  public NextAction processResponse(Packet paramPacket) {
    if (this.messageDumper.isLoggable()) {
      Packet packet = (paramPacket != null) ? paramPacket.copy(true) : null;
      this.messageDumper.dump(MessageDumper.MessageType.Response, this.position.responseState, Converter.toString(packet), this.tubeId, (Fiber.current()).owner.id);
    } 
    return super.processResponse(paramPacket);
  }
  
  public NextAction processException(Throwable paramThrowable) {
    if (this.messageDumper.isLoggable())
      this.messageDumper.dump(MessageDumper.MessageType.Exception, this.position.responseState, Converter.toString(paramThrowable), this.tubeId, (Fiber.current()).owner.id); 
    return super.processException(paramThrowable);
  }
  
  public void preDestroy() { super.preDestroy(); }
  
  public enum Position {
    Before(MessageDumper.ProcessingState.Received, MessageDumper.ProcessingState.Processed),
    After(MessageDumper.ProcessingState.Processed, MessageDumper.ProcessingState.Received);
    
    private final MessageDumper.ProcessingState requestState;
    
    private final MessageDumper.ProcessingState responseState;
    
    Position(MessageDumper.ProcessingState param1ProcessingState1, MessageDumper.ProcessingState param1ProcessingState2) {
      this.requestState = param1ProcessingState1;
      this.responseState = param1ProcessingState2;
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\dump\LoggingDumpTube.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */