package com.sun.corba.se.impl.encoding;

import com.sun.corba.se.impl.logging.OMGSystemException;
import com.sun.corba.se.impl.logging.ORBUtilSystemException;
import com.sun.corba.se.impl.orbutil.ORBUtility;
import com.sun.corba.se.impl.protocol.giopmsgheaders.Message;
import com.sun.corba.se.pept.protocol.MessageMediator;
import com.sun.corba.se.spi.encoding.CorbaOutputObject;
import com.sun.corba.se.spi.ior.iiop.GIOPVersion;
import com.sun.corba.se.spi.orb.ORB;
import com.sun.corba.se.spi.protocol.CorbaMessageMediator;
import com.sun.corba.se.spi.transport.CorbaConnection;
import java.io.IOException;
import org.omg.CORBA.portable.InputStream;

public class CDROutputObject extends CorbaOutputObject {
  private Message header;
  
  private ORB orb;
  
  private ORBUtilSystemException wrapper;
  
  private OMGSystemException omgWrapper;
  
  private CorbaConnection connection;
  
  private CDROutputObject(ORB paramORB, GIOPVersion paramGIOPVersion, Message paramMessage, BufferManagerWrite paramBufferManagerWrite, byte paramByte, CorbaMessageMediator paramCorbaMessageMediator) {
    super(paramORB, paramGIOPVersion, paramMessage.getEncodingVersion(), false, paramBufferManagerWrite, paramByte, (paramCorbaMessageMediator != null && paramCorbaMessageMediator.getConnection() != null) ? ((CorbaConnection)paramCorbaMessageMediator.getConnection()).shouldUseDirectByteBuffers() : 0);
    this.header = paramMessage;
    this.orb = paramORB;
    this.wrapper = ORBUtilSystemException.get(paramORB, "rpc.encoding");
    this.omgWrapper = OMGSystemException.get(paramORB, "rpc.encoding");
    getBufferManager().setOutputObject(this);
    this.corbaMessageMediator = paramCorbaMessageMediator;
  }
  
  public CDROutputObject(ORB paramORB, MessageMediator paramMessageMediator, Message paramMessage, byte paramByte) { this(paramORB, ((CorbaMessageMediator)paramMessageMediator).getGIOPVersion(), paramMessage, BufferManagerFactory.newBufferManagerWrite(((CorbaMessageMediator)paramMessageMediator).getGIOPVersion(), paramMessage.getEncodingVersion(), paramORB), paramByte, (CorbaMessageMediator)paramMessageMediator); }
  
  public CDROutputObject(ORB paramORB, MessageMediator paramMessageMediator, Message paramMessage, byte paramByte, int paramInt) { this(paramORB, ((CorbaMessageMediator)paramMessageMediator).getGIOPVersion(), paramMessage, BufferManagerFactory.newBufferManagerWrite(paramInt, paramMessage.getEncodingVersion(), paramORB), paramByte, (CorbaMessageMediator)paramMessageMediator); }
  
  public CDROutputObject(ORB paramORB, CorbaMessageMediator paramCorbaMessageMediator, GIOPVersion paramGIOPVersion, CorbaConnection paramCorbaConnection, Message paramMessage, byte paramByte) {
    this(paramORB, paramGIOPVersion, paramMessage, BufferManagerFactory.newBufferManagerWrite(paramGIOPVersion, paramMessage.getEncodingVersion(), paramORB), paramByte, paramCorbaMessageMediator);
    this.connection = paramCorbaConnection;
  }
  
  public Message getMessageHeader() { return this.header; }
  
  public final void finishSendingMessage() { getBufferManager().sendMessage(); }
  
  public void writeTo(CorbaConnection paramCorbaConnection) throws IOException {
    ByteBufferWithInfo byteBufferWithInfo = getByteBufferWithInfo();
    getMessageHeader().setSize(byteBufferWithInfo.byteBuffer, byteBufferWithInfo.getSize());
    if (orb() != null) {
      if (((ORB)orb()).transportDebugFlag)
        dprint(".writeTo: " + paramCorbaConnection); 
      if (((ORB)orb()).giopDebugFlag)
        CDROutputStream_1_0.printBuffer(byteBufferWithInfo); 
    } 
    byteBufferWithInfo.byteBuffer.position(0).limit(byteBufferWithInfo.getSize());
    paramCorbaConnection.write(byteBufferWithInfo.byteBuffer);
  }
  
  public InputStream create_input_stream() { return null; }
  
  public CorbaConnection getConnection() { return (this.connection != null) ? this.connection : (CorbaConnection)this.corbaMessageMediator.getConnection(); }
  
  public final ByteBufferWithInfo getByteBufferWithInfo() { return super.getByteBufferWithInfo(); }
  
  public final void setByteBufferWithInfo(ByteBufferWithInfo paramByteBufferWithInfo) { super.setByteBufferWithInfo(paramByteBufferWithInfo); }
  
  protected CodeSetConversion.CTBConverter createCharCTBConverter() {
    CodeSetComponentInfo.CodeSetContext codeSetContext = getCodeSets();
    if (codeSetContext == null)
      return super.createCharCTBConverter(); 
    OSFCodeSetRegistry.Entry entry = OSFCodeSetRegistry.lookupEntry(codeSetContext.getCharCodeSet());
    if (entry == null)
      throw this.wrapper.unknownCodeset(entry); 
    return CodeSetConversion.impl().getCTBConverter(entry, isLittleEndian(), false);
  }
  
  protected CodeSetConversion.CTBConverter createWCharCTBConverter() {
    CodeSetComponentInfo.CodeSetContext codeSetContext = getCodeSets();
    if (codeSetContext == null) {
      if (getConnection().isServer())
        throw this.omgWrapper.noClientWcharCodesetCtx(); 
      throw this.omgWrapper.noServerWcharCodesetCmp();
    } 
    OSFCodeSetRegistry.Entry entry = OSFCodeSetRegistry.lookupEntry(codeSetContext.getWCharCodeSet());
    if (entry == null)
      throw this.wrapper.unknownCodeset(entry); 
    boolean bool = ((ORB)orb()).getORBData().useByteOrderMarkers();
    if (entry == OSFCodeSetRegistry.UTF_16) {
      if (getGIOPVersion().equals(GIOPVersion.V1_2))
        return CodeSetConversion.impl().getCTBConverter(entry, false, bool); 
      if (getGIOPVersion().equals(GIOPVersion.V1_1))
        return CodeSetConversion.impl().getCTBConverter(entry, isLittleEndian(), false); 
    } 
    return CodeSetConversion.impl().getCTBConverter(entry, isLittleEndian(), bool);
  }
  
  private CodeSetComponentInfo.CodeSetContext getCodeSets() { return (getConnection() == null) ? CodeSetComponentInfo.LOCAL_CODE_SETS : getConnection().getCodeSetContext(); }
  
  protected void dprint(String paramString) { ORBUtility.dprint("CDROutputObject", paramString); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\impl\encoding\CDROutputObject.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */