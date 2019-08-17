package com.sun.corba.se.impl.encoding;

import com.sun.corba.se.impl.logging.OMGSystemException;
import com.sun.corba.se.impl.logging.ORBUtilSystemException;
import com.sun.corba.se.impl.orbutil.ORBUtility;
import com.sun.corba.se.impl.protocol.giopmsgheaders.Message;
import com.sun.corba.se.pept.encoding.InputObject;
import com.sun.corba.se.spi.ior.iiop.GIOPVersion;
import com.sun.corba.se.spi.orb.ORB;
import com.sun.corba.se.spi.transport.CorbaConnection;
import com.sun.org.omg.SendingContext.CodeBase;
import java.nio.ByteBuffer;

public class CDRInputObject extends CDRInputStream implements InputObject {
  private CorbaConnection corbaConnection;
  
  private Message header;
  
  private boolean unmarshaledHeader;
  
  private ORB orb;
  
  private ORBUtilSystemException wrapper;
  
  private OMGSystemException omgWrapper;
  
  public CDRInputObject(ORB paramORB, CorbaConnection paramCorbaConnection, ByteBuffer paramByteBuffer, Message paramMessage) {
    super(paramORB, paramByteBuffer, paramMessage.getSize(), paramMessage.isLittleEndian(), paramMessage.getGIOPVersion(), paramMessage.getEncodingVersion(), BufferManagerFactory.newBufferManagerRead(paramMessage.getGIOPVersion(), paramMessage.getEncodingVersion(), paramORB));
    this.corbaConnection = paramCorbaConnection;
    this.orb = paramORB;
    this.wrapper = ORBUtilSystemException.get(paramORB, "rpc.encoding");
    this.omgWrapper = OMGSystemException.get(paramORB, "rpc.encoding");
    if (paramORB.transportDebugFlag)
      dprint(".CDRInputObject constructor:"); 
    getBufferManager().init(paramMessage);
    this.header = paramMessage;
    this.unmarshaledHeader = false;
    setIndex(12);
    setBufferLength(paramMessage.getSize());
  }
  
  public final CorbaConnection getConnection() { return this.corbaConnection; }
  
  public Message getMessageHeader() { return this.header; }
  
  public void unmarshalHeader() {
    if (!this.unmarshaledHeader)
      try {
        if (((ORB)orb()).transportDebugFlag)
          dprint(".unmarshalHeader->: " + getMessageHeader()); 
        getMessageHeader().read(this);
        this.unmarshaledHeader = true;
      } catch (RuntimeException runtimeException) {
        if (((ORB)orb()).transportDebugFlag)
          dprint(".unmarshalHeader: !!ERROR!!: " + getMessageHeader() + ": " + runtimeException); 
        throw runtimeException;
      } finally {
        if (((ORB)orb()).transportDebugFlag)
          dprint(".unmarshalHeader<-: " + getMessageHeader()); 
      }  
  }
  
  public final boolean unmarshaledHeader() { return this.unmarshaledHeader; }
  
  protected CodeSetConversion.BTCConverter createCharBTCConverter() {
    CodeSetComponentInfo.CodeSetContext codeSetContext = getCodeSets();
    if (codeSetContext == null)
      return super.createCharBTCConverter(); 
    OSFCodeSetRegistry.Entry entry = OSFCodeSetRegistry.lookupEntry(codeSetContext.getCharCodeSet());
    if (entry == null)
      throw this.wrapper.unknownCodeset(entry); 
    return CodeSetConversion.impl().getBTCConverter(entry, isLittleEndian());
  }
  
  protected CodeSetConversion.BTCConverter createWCharBTCConverter() {
    CodeSetComponentInfo.CodeSetContext codeSetContext = getCodeSets();
    if (codeSetContext == null) {
      if (getConnection().isServer())
        throw this.omgWrapper.noClientWcharCodesetCtx(); 
      throw this.omgWrapper.noServerWcharCodesetCmp();
    } 
    OSFCodeSetRegistry.Entry entry = OSFCodeSetRegistry.lookupEntry(codeSetContext.getWCharCodeSet());
    if (entry == null)
      throw this.wrapper.unknownCodeset(entry); 
    return (entry == OSFCodeSetRegistry.UTF_16 && getGIOPVersion().equals(GIOPVersion.V1_2)) ? CodeSetConversion.impl().getBTCConverter(entry, false) : CodeSetConversion.impl().getBTCConverter(entry, isLittleEndian());
  }
  
  private CodeSetComponentInfo.CodeSetContext getCodeSets() { return (getConnection() == null) ? CodeSetComponentInfo.LOCAL_CODE_SETS : getConnection().getCodeSetContext(); }
  
  public final CodeBase getCodeBase() { return (getConnection() == null) ? null : getConnection().getCodeBase(); }
  
  public CDRInputStream dup() { return null; }
  
  protected void dprint(String paramString) { ORBUtility.dprint("CDRInputObject", paramString); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\impl\encoding\CDRInputObject.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */