package com.sun.corba.se.spi.servicecontext;

import com.sun.corba.se.impl.orbutil.ORBUtility;
import com.sun.corba.se.spi.ior.iiop.GIOPVersion;
import org.omg.CORBA.SystemException;
import org.omg.CORBA_2_3.portable.InputStream;
import org.omg.CORBA_2_3.portable.OutputStream;

public class MaxStreamFormatVersionServiceContext extends ServiceContext {
  private byte maxStreamFormatVersion = ORBUtility.getMaxStreamFormatVersion();
  
  public static final MaxStreamFormatVersionServiceContext singleton = new MaxStreamFormatVersionServiceContext();
  
  public static final int SERVICE_CONTEXT_ID = 17;
  
  public MaxStreamFormatVersionServiceContext() {}
  
  public MaxStreamFormatVersionServiceContext(byte paramByte) {}
  
  public MaxStreamFormatVersionServiceContext(InputStream paramInputStream, GIOPVersion paramGIOPVersion) { super(paramInputStream, paramGIOPVersion); }
  
  public int getId() { return 17; }
  
  public void writeData(OutputStream paramOutputStream) throws SystemException { paramOutputStream.write_octet(this.maxStreamFormatVersion); }
  
  public byte getMaximumStreamFormatVersion() { return this.maxStreamFormatVersion; }
  
  public String toString() { return "MaxStreamFormatVersionServiceContext[" + this.maxStreamFormatVersion + "]"; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\spi\servicecontext\MaxStreamFormatVersionServiceContext.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */