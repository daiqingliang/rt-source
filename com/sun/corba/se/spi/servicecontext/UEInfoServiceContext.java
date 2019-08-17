package com.sun.corba.se.spi.servicecontext;

import com.sun.corba.se.spi.ior.iiop.GIOPVersion;
import org.omg.CORBA.CompletionStatus;
import org.omg.CORBA.SystemException;
import org.omg.CORBA.UNKNOWN;
import org.omg.CORBA_2_3.portable.InputStream;
import org.omg.CORBA_2_3.portable.OutputStream;

public class UEInfoServiceContext extends ServiceContext {
  public static final int SERVICE_CONTEXT_ID = 9;
  
  private Throwable unknown = null;
  
  public UEInfoServiceContext(Throwable paramThrowable) { this.unknown = paramThrowable; }
  
  public UEInfoServiceContext(InputStream paramInputStream, GIOPVersion paramGIOPVersion) {
    super(paramInputStream, paramGIOPVersion);
    try {
      this.unknown = (Throwable)this.in.read_value();
    } catch (ThreadDeath threadDeath) {
      throw threadDeath;
    } catch (Throwable throwable) {
      this.unknown = new UNKNOWN(0, CompletionStatus.COMPLETED_MAYBE);
    } 
  }
  
  public int getId() { return 9; }
  
  public void writeData(OutputStream paramOutputStream) throws SystemException { paramOutputStream.write_value(this.unknown); }
  
  public Throwable getUE() { return this.unknown; }
  
  public String toString() { return "UEInfoServiceContext[ unknown=" + this.unknown.toString() + " ]"; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\spi\servicecontext\UEInfoServiceContext.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */