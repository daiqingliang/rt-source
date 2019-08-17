package com.sun.corba.se.impl.transport;

import com.sun.corba.se.impl.encoding.CDRInputObject;
import com.sun.corba.se.impl.encoding.CDROutputObject;
import com.sun.corba.se.impl.orbutil.ORBUtility;
import com.sun.corba.se.impl.protocol.CorbaMessageMediatorImpl;
import com.sun.corba.se.impl.protocol.giopmsgheaders.Message;
import com.sun.corba.se.impl.protocol.giopmsgheaders.MessageBase;
import com.sun.corba.se.pept.broker.Broker;
import com.sun.corba.se.pept.encoding.InputObject;
import com.sun.corba.se.pept.encoding.OutputObject;
import com.sun.corba.se.pept.protocol.ClientRequestDispatcher;
import com.sun.corba.se.pept.protocol.MessageMediator;
import com.sun.corba.se.pept.transport.Connection;
import com.sun.corba.se.pept.transport.ContactInfo;
import com.sun.corba.se.pept.transport.ContactInfoList;
import com.sun.corba.se.pept.transport.OutboundConnectionCache;
import com.sun.corba.se.spi.ior.IOR;
import com.sun.corba.se.spi.ior.iiop.GIOPVersion;
import com.sun.corba.se.spi.ior.iiop.IIOPProfile;
import com.sun.corba.se.spi.orb.ORB;
import com.sun.corba.se.spi.protocol.CorbaMessageMediator;
import com.sun.corba.se.spi.protocol.RequestDispatcherRegistry;
import com.sun.corba.se.spi.transport.CorbaConnection;
import com.sun.corba.se.spi.transport.CorbaContactInfo;
import com.sun.corba.se.spi.transport.CorbaContactInfoList;
import java.nio.ByteBuffer;
import sun.corba.OutputStreamFactory;

public abstract class CorbaContactInfoBase implements CorbaContactInfo {
  protected ORB orb;
  
  protected CorbaContactInfoList contactInfoList;
  
  protected IOR effectiveTargetIOR;
  
  protected short addressingDisposition;
  
  protected OutboundConnectionCache connectionCache;
  
  public Broker getBroker() { return this.orb; }
  
  public ContactInfoList getContactInfoList() { return this.contactInfoList; }
  
  public ClientRequestDispatcher getClientRequestDispatcher() {
    int i = getEffectiveProfile().getObjectKeyTemplate().getSubcontractId();
    RequestDispatcherRegistry requestDispatcherRegistry = this.orb.getRequestDispatcherRegistry();
    return requestDispatcherRegistry.getClientRequestDispatcher(i);
  }
  
  public void setConnectionCache(OutboundConnectionCache paramOutboundConnectionCache) { this.connectionCache = paramOutboundConnectionCache; }
  
  public OutboundConnectionCache getConnectionCache() { return this.connectionCache; }
  
  public MessageMediator createMessageMediator(Broker paramBroker, ContactInfo paramContactInfo, Connection paramConnection, String paramString, boolean paramBoolean) { return new CorbaMessageMediatorImpl((ORB)paramBroker, paramContactInfo, paramConnection, GIOPVersion.chooseRequestVersion((ORB)paramBroker, this.effectiveTargetIOR), this.effectiveTargetIOR, ((CorbaConnection)paramConnection).getNextRequestId(), getAddressingDisposition(), paramString, paramBoolean); }
  
  public MessageMediator createMessageMediator(Broker paramBroker, Connection paramConnection) {
    ORB oRB = (ORB)paramBroker;
    CorbaConnection corbaConnection = (CorbaConnection)paramConnection;
    if (oRB.transportDebugFlag)
      if (corbaConnection.shouldReadGiopHeaderOnly()) {
        dprint(".createMessageMediator: waiting for message header on connection: " + corbaConnection);
      } else {
        dprint(".createMessageMediator: waiting for message on connection: " + corbaConnection);
      }  
    MessageBase messageBase = null;
    if (corbaConnection.shouldReadGiopHeaderOnly()) {
      messageBase = MessageBase.readGIOPHeader(oRB, corbaConnection);
    } else {
      messageBase = MessageBase.readGIOPMessage(oRB, corbaConnection);
    } 
    ByteBuffer byteBuffer = messageBase.getByteBuffer();
    messageBase.setByteBuffer(null);
    return new CorbaMessageMediatorImpl(oRB, corbaConnection, messageBase, byteBuffer);
  }
  
  public MessageMediator finishCreatingMessageMediator(Broker paramBroker, Connection paramConnection, MessageMediator paramMessageMediator) {
    ORB oRB = (ORB)paramBroker;
    CorbaConnection corbaConnection = (CorbaConnection)paramConnection;
    CorbaMessageMediator corbaMessageMediator = (CorbaMessageMediator)paramMessageMediator;
    if (oRB.transportDebugFlag)
      dprint(".finishCreatingMessageMediator: waiting for message body on connection: " + corbaConnection); 
    Message message = corbaMessageMediator.getDispatchHeader();
    message.setByteBuffer(corbaMessageMediator.getDispatchBuffer());
    message = MessageBase.readGIOPBody(oRB, corbaConnection, message);
    ByteBuffer byteBuffer = message.getByteBuffer();
    message.setByteBuffer(null);
    corbaMessageMediator.setDispatchHeader(message);
    corbaMessageMediator.setDispatchBuffer(byteBuffer);
    return corbaMessageMediator;
  }
  
  public OutputObject createOutputObject(MessageMediator paramMessageMediator) {
    CorbaMessageMediator corbaMessageMediator = (CorbaMessageMediator)paramMessageMediator;
    CDROutputObject cDROutputObject = OutputStreamFactory.newCDROutputObject(this.orb, paramMessageMediator, corbaMessageMediator.getRequestHeader(), corbaMessageMediator.getStreamFormatVersion());
    paramMessageMediator.setOutputObject(cDROutputObject);
    return cDROutputObject;
  }
  
  public InputObject createInputObject(Broker paramBroker, MessageMediator paramMessageMediator) {
    CorbaMessageMediator corbaMessageMediator = (CorbaMessageMediator)paramMessageMediator;
    return new CDRInputObject((ORB)paramBroker, (CorbaConnection)paramMessageMediator.getConnection(), corbaMessageMediator.getDispatchBuffer(), corbaMessageMediator.getDispatchHeader());
  }
  
  public short getAddressingDisposition() { return this.addressingDisposition; }
  
  public void setAddressingDisposition(short paramShort) { this.addressingDisposition = paramShort; }
  
  public IOR getTargetIOR() { return this.contactInfoList.getTargetIOR(); }
  
  public IOR getEffectiveTargetIOR() { return this.effectiveTargetIOR; }
  
  public IIOPProfile getEffectiveProfile() { return this.effectiveTargetIOR.getProfile(); }
  
  public String toString() { return "CorbaContactInfoBase[]"; }
  
  protected void dprint(String paramString) { ORBUtility.dprint("CorbaContactInfoBase", paramString); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\impl\transport\CorbaContactInfoBase.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */