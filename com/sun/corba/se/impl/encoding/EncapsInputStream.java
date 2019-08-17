package com.sun.corba.se.impl.encoding;

import com.sun.corba.se.impl.logging.ORBUtilSystemException;
import com.sun.corba.se.spi.ior.iiop.GIOPVersion;
import com.sun.corba.se.spi.orb.ORB;
import com.sun.org.omg.SendingContext.CodeBase;
import java.nio.ByteBuffer;
import org.omg.CORBA.CompletionStatus;
import org.omg.CORBA.ORB;
import sun.corba.EncapsInputStreamFactory;

public class EncapsInputStream extends CDRInputStream {
  private ORBUtilSystemException wrapper;
  
  private CodeBase codeBase;
  
  public EncapsInputStream(ORB paramORB, byte[] paramArrayOfByte, int paramInt, boolean paramBoolean, GIOPVersion paramGIOPVersion) {
    super(paramORB, ByteBuffer.wrap(paramArrayOfByte), paramInt, paramBoolean, paramGIOPVersion, (byte)0, BufferManagerFactory.newBufferManagerRead(0, (byte)0, (ORB)paramORB));
    this.wrapper = ORBUtilSystemException.get((ORB)paramORB, "rpc.encoding");
    performORBVersionSpecificInit();
  }
  
  public EncapsInputStream(ORB paramORB, ByteBuffer paramByteBuffer, int paramInt, boolean paramBoolean, GIOPVersion paramGIOPVersion) {
    super(paramORB, paramByteBuffer, paramInt, paramBoolean, paramGIOPVersion, (byte)0, BufferManagerFactory.newBufferManagerRead(0, (byte)0, (ORB)paramORB));
    performORBVersionSpecificInit();
  }
  
  public EncapsInputStream(ORB paramORB, byte[] paramArrayOfByte, int paramInt) { this(paramORB, paramArrayOfByte, paramInt, GIOPVersion.V1_2); }
  
  public EncapsInputStream(EncapsInputStream paramEncapsInputStream) {
    super(paramEncapsInputStream);
    this.wrapper = ORBUtilSystemException.get((ORB)paramEncapsInputStream.orb(), "rpc.encoding");
    performORBVersionSpecificInit();
  }
  
  public EncapsInputStream(ORB paramORB, byte[] paramArrayOfByte, int paramInt, GIOPVersion paramGIOPVersion) { this(paramORB, paramArrayOfByte, paramInt, false, paramGIOPVersion); }
  
  public EncapsInputStream(ORB paramORB, byte[] paramArrayOfByte, int paramInt, GIOPVersion paramGIOPVersion, CodeBase paramCodeBase) {
    super(paramORB, ByteBuffer.wrap(paramArrayOfByte), paramInt, false, paramGIOPVersion, (byte)0, BufferManagerFactory.newBufferManagerRead(0, (byte)0, (ORB)paramORB));
    this.codeBase = paramCodeBase;
    performORBVersionSpecificInit();
  }
  
  public CDRInputStream dup() { return EncapsInputStreamFactory.newEncapsInputStream(this); }
  
  protected CodeSetConversion.BTCConverter createCharBTCConverter() { return CodeSetConversion.impl().getBTCConverter(OSFCodeSetRegistry.ISO_8859_1); }
  
  protected CodeSetConversion.BTCConverter createWCharBTCConverter() {
    if (getGIOPVersion().equals(GIOPVersion.V1_0))
      throw this.wrapper.wcharDataInGiop10(CompletionStatus.COMPLETED_MAYBE); 
    return getGIOPVersion().equals(GIOPVersion.V1_1) ? CodeSetConversion.impl().getBTCConverter(OSFCodeSetRegistry.UTF_16, isLittleEndian()) : CodeSetConversion.impl().getBTCConverter(OSFCodeSetRegistry.UTF_16, false);
  }
  
  public CodeBase getCodeBase() { return this.codeBase; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\impl\encoding\EncapsInputStream.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */