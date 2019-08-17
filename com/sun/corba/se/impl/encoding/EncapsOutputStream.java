package com.sun.corba.se.impl.encoding;

import com.sun.corba.se.spi.ior.iiop.GIOPVersion;
import com.sun.corba.se.spi.orb.ORB;
import org.omg.CORBA.CompletionStatus;
import org.omg.CORBA.portable.InputStream;
import sun.corba.EncapsInputStreamFactory;

public class EncapsOutputStream extends CDROutputStream {
  static final boolean usePooledByteBuffers = false;
  
  public EncapsOutputStream(ORB paramORB) { this(paramORB, GIOPVersion.V1_2); }
  
  public EncapsOutputStream(ORB paramORB, GIOPVersion paramGIOPVersion) { this(paramORB, paramGIOPVersion, false); }
  
  public EncapsOutputStream(ORB paramORB, boolean paramBoolean) { this(paramORB, GIOPVersion.V1_2, paramBoolean); }
  
  public EncapsOutputStream(ORB paramORB, GIOPVersion paramGIOPVersion, boolean paramBoolean) { super(paramORB, paramGIOPVersion, (byte)0, paramBoolean, BufferManagerFactory.newBufferManagerWrite(0, (byte)0, paramORB), (byte)1, false); }
  
  public InputStream create_input_stream() {
    freeInternalCaches();
    return EncapsInputStreamFactory.newEncapsInputStream(orb(), getByteBuffer(), getSize(), isLittleEndian(), getGIOPVersion());
  }
  
  protected CodeSetConversion.CTBConverter createCharCTBConverter() { return CodeSetConversion.impl().getCTBConverter(OSFCodeSetRegistry.ISO_8859_1); }
  
  protected CodeSetConversion.CTBConverter createWCharCTBConverter() {
    if (getGIOPVersion().equals(GIOPVersion.V1_0))
      throw this.wrapper.wcharDataInGiop10(CompletionStatus.COMPLETED_MAYBE); 
    if (getGIOPVersion().equals(GIOPVersion.V1_1))
      return CodeSetConversion.impl().getCTBConverter(OSFCodeSetRegistry.UTF_16, isLittleEndian(), false); 
    boolean bool = ((ORB)orb()).getORBData().useByteOrderMarkersInEncapsulations();
    return CodeSetConversion.impl().getCTBConverter(OSFCodeSetRegistry.UTF_16, false, bool);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\impl\encoding\EncapsOutputStream.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */