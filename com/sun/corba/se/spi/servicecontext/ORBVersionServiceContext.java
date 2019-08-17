package com.sun.corba.se.spi.servicecontext;

import com.sun.corba.se.spi.ior.iiop.GIOPVersion;
import com.sun.corba.se.spi.orb.ORBVersion;
import com.sun.corba.se.spi.orb.ORBVersionFactory;
import org.omg.CORBA.SystemException;
import org.omg.CORBA_2_3.portable.InputStream;
import org.omg.CORBA_2_3.portable.OutputStream;

public class ORBVersionServiceContext extends ServiceContext {
  public static final int SERVICE_CONTEXT_ID = 1313165056;
  
  private ORBVersion version = ORBVersionFactory.getORBVersion();
  
  public ORBVersionServiceContext() { this.version = ORBVersionFactory.getORBVersion(); }
  
  public ORBVersionServiceContext(ORBVersion paramORBVersion) { this.version = paramORBVersion; }
  
  public ORBVersionServiceContext(InputStream paramInputStream, GIOPVersion paramGIOPVersion) {
    super(paramInputStream, paramGIOPVersion);
    this.version = ORBVersionFactory.create(this.in);
  }
  
  public int getId() { return 1313165056; }
  
  public void writeData(OutputStream paramOutputStream) throws SystemException { this.version.write(paramOutputStream); }
  
  public ORBVersion getVersion() { return this.version; }
  
  public String toString() { return "ORBVersionServiceContext[ version=" + this.version + " ]"; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\spi\servicecontext\ORBVersionServiceContext.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */