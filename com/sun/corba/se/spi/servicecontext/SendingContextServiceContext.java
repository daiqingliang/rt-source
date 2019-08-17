package com.sun.corba.se.spi.servicecontext;

import com.sun.corba.se.impl.ior.IORImpl;
import com.sun.corba.se.spi.ior.IOR;
import com.sun.corba.se.spi.ior.iiop.GIOPVersion;
import org.omg.CORBA.SystemException;
import org.omg.CORBA_2_3.portable.InputStream;
import org.omg.CORBA_2_3.portable.OutputStream;

public class SendingContextServiceContext extends ServiceContext {
  public static final int SERVICE_CONTEXT_ID = 6;
  
  private IOR ior = null;
  
  public SendingContextServiceContext(IOR paramIOR) { this.ior = paramIOR; }
  
  public SendingContextServiceContext(InputStream paramInputStream, GIOPVersion paramGIOPVersion) {
    super(paramInputStream, paramGIOPVersion);
    this.ior = new IORImpl(this.in);
  }
  
  public int getId() { return 6; }
  
  public void writeData(OutputStream paramOutputStream) throws SystemException { this.ior.write(paramOutputStream); }
  
  public IOR getIOR() { return this.ior; }
  
  public String toString() { return "SendingContexServiceContext[ ior=" + this.ior + " ]"; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\spi\servicecontext\SendingContextServiceContext.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */