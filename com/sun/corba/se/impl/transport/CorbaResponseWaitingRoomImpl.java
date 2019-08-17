package com.sun.corba.se.impl.transport;

import com.sun.corba.se.impl.encoding.BufferManagerReadStream;
import com.sun.corba.se.impl.encoding.CDRInputObject;
import com.sun.corba.se.impl.logging.ORBUtilSystemException;
import com.sun.corba.se.impl.orbutil.ORBUtility;
import com.sun.corba.se.impl.protocol.giopmsgheaders.LocateReplyOrReplyMessage;
import com.sun.corba.se.pept.encoding.InputObject;
import com.sun.corba.se.pept.protocol.MessageMediator;
import com.sun.corba.se.spi.orb.ORB;
import com.sun.corba.se.spi.protocol.CorbaMessageMediator;
import com.sun.corba.se.spi.transport.CorbaConnection;
import com.sun.corba.se.spi.transport.CorbaResponseWaitingRoom;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import org.omg.CORBA.CompletionStatus;
import org.omg.CORBA.SystemException;

public class CorbaResponseWaitingRoomImpl implements CorbaResponseWaitingRoom {
  private ORB orb;
  
  private ORBUtilSystemException wrapper;
  
  private CorbaConnection connection;
  
  private final Map<Integer, OutCallDesc> out_calls;
  
  public CorbaResponseWaitingRoomImpl(ORB paramORB, CorbaConnection paramCorbaConnection) {
    this.orb = paramORB;
    this.wrapper = ORBUtilSystemException.get(paramORB, "rpc.transport");
    this.connection = paramCorbaConnection;
    this.out_calls = Collections.synchronizedMap(new HashMap());
  }
  
  public void registerWaiter(MessageMediator paramMessageMediator) {
    CorbaMessageMediator corbaMessageMediator = (CorbaMessageMediator)paramMessageMediator;
    if (this.orb.transportDebugFlag)
      dprint(".registerWaiter: " + opAndId(corbaMessageMediator)); 
    Integer integer = corbaMessageMediator.getRequestIdInteger();
    OutCallDesc outCallDesc = new OutCallDesc();
    outCallDesc.thread = Thread.currentThread();
    outCallDesc.messageMediator = corbaMessageMediator;
    this.out_calls.put(integer, outCallDesc);
  }
  
  public void unregisterWaiter(MessageMediator paramMessageMediator) {
    CorbaMessageMediator corbaMessageMediator = (CorbaMessageMediator)paramMessageMediator;
    if (this.orb.transportDebugFlag)
      dprint(".unregisterWaiter: " + opAndId(corbaMessageMediator)); 
    Integer integer = corbaMessageMediator.getRequestIdInteger();
    this.out_calls.remove(integer);
  }
  
  public InputObject waitForResponse(MessageMediator paramMessageMediator) {
    corbaMessageMediator = (CorbaMessageMediator)paramMessageMediator;
    try {
      InputObject inputObject = null;
      if (this.orb.transportDebugFlag)
        dprint(".waitForResponse->: " + opAndId(corbaMessageMediator)); 
      Integer integer = corbaMessageMediator.getRequestIdInteger();
      if (corbaMessageMediator.isOneWay()) {
        if (this.orb.transportDebugFlag)
          dprint(".waitForResponse: one way - not waiting: " + opAndId(corbaMessageMediator)); 
        return null;
      } 
      OutCallDesc outCallDesc = (OutCallDesc)this.out_calls.get(integer);
      if (outCallDesc == null)
        throw this.wrapper.nullOutCall(CompletionStatus.COMPLETED_MAYBE); 
      synchronized (outCallDesc.done) {
        while (outCallDesc.inputObject == null && outCallDesc.exception == null) {
          try {
            if (this.orb.transportDebugFlag)
              dprint(".waitForResponse: waiting: " + opAndId(corbaMessageMediator)); 
            outCallDesc.done.wait();
          } catch (InterruptedException interruptedException) {}
        } 
        if (outCallDesc.exception != null) {
          if (this.orb.transportDebugFlag)
            dprint(".waitForResponse: exception: " + opAndId(corbaMessageMediator)); 
          throw outCallDesc.exception;
        } 
        inputObject = outCallDesc.inputObject;
      } 
      if (inputObject != null)
        ((CDRInputObject)inputObject).unmarshalHeader(); 
      return inputObject;
    } finally {
      if (this.orb.transportDebugFlag)
        dprint(".waitForResponse<-: " + opAndId(corbaMessageMediator)); 
    } 
  }
  
  public void responseReceived(InputObject paramInputObject) {
    CDRInputObject cDRInputObject = (CDRInputObject)paramInputObject;
    LocateReplyOrReplyMessage locateReplyOrReplyMessage = (LocateReplyOrReplyMessage)cDRInputObject.getMessageHeader();
    Integer integer = new Integer(locateReplyOrReplyMessage.getRequestId());
    OutCallDesc outCallDesc = (OutCallDesc)this.out_calls.get(integer);
    if (this.orb.transportDebugFlag)
      dprint(".responseReceived: id/" + integer + ": " + locateReplyOrReplyMessage); 
    if (outCallDesc == null) {
      if (this.orb.transportDebugFlag)
        dprint(".responseReceived: id/" + integer + ": no waiter: " + locateReplyOrReplyMessage); 
      return;
    } 
    synchronized (outCallDesc.done) {
      CorbaMessageMediator corbaMessageMediator = (CorbaMessageMediator)outCallDesc.messageMediator;
      if (this.orb.transportDebugFlag)
        dprint(".responseReceived: " + opAndId(corbaMessageMediator) + ": notifying waiters"); 
      corbaMessageMediator.setReplyHeader(locateReplyOrReplyMessage);
      corbaMessageMediator.setInputObject(paramInputObject);
      cDRInputObject.setMessageMediator(corbaMessageMediator);
      outCallDesc.inputObject = paramInputObject;
      outCallDesc.done.notify();
    } 
  }
  
  public int numberRegistered() { return this.out_calls.size(); }
  
  public void signalExceptionToAllWaiters(SystemException paramSystemException) {
    if (this.orb.transportDebugFlag)
      dprint(".signalExceptionToAllWaiters: " + paramSystemException); 
    synchronized (this.out_calls) {
      if (this.orb.transportDebugFlag)
        dprint(".signalExceptionToAllWaiters: out_calls size :" + this.out_calls.size()); 
      for (OutCallDesc outCallDesc : this.out_calls.values()) {
        if (this.orb.transportDebugFlag)
          dprint(".signalExceptionToAllWaiters: signaling " + outCallDesc); 
        synchronized (outCallDesc.done) {
          try {
            CorbaMessageMediator corbaMessageMediator = (CorbaMessageMediator)outCallDesc.messageMediator;
            CDRInputObject cDRInputObject = (CDRInputObject)corbaMessageMediator.getInputObject();
            if (cDRInputObject != null) {
              BufferManagerReadStream bufferManagerReadStream = (BufferManagerReadStream)cDRInputObject.getBufferManager();
              int i = corbaMessageMediator.getRequestId();
              bufferManagerReadStream.cancelProcessing(i);
            } 
          } catch (Exception exception) {
          
          } finally {
            outCallDesc.inputObject = null;
            outCallDesc.exception = paramSystemException;
            outCallDesc.done.notifyAll();
          } 
        } 
      } 
    } 
  }
  
  public MessageMediator getMessageMediator(int paramInt) {
    Integer integer = new Integer(paramInt);
    OutCallDesc outCallDesc = (OutCallDesc)this.out_calls.get(integer);
    return (outCallDesc == null) ? null : outCallDesc.messageMediator;
  }
  
  protected void dprint(String paramString) { ORBUtility.dprint("CorbaResponseWaitingRoomImpl", paramString); }
  
  protected String opAndId(CorbaMessageMediator paramCorbaMessageMediator) { return ORBUtility.operationNameAndRequestId(paramCorbaMessageMediator); }
  
  static final class OutCallDesc {
    Object done = new Object();
    
    Thread thread;
    
    MessageMediator messageMediator;
    
    SystemException exception;
    
    InputObject inputObject;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\impl\transport\CorbaResponseWaitingRoomImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */