package com.sun.corba.se.spi.servicecontext;

import com.sun.corba.se.impl.encoding.CodeSetComponentInfo;
import com.sun.corba.se.impl.encoding.MarshalInputStream;
import com.sun.corba.se.impl.encoding.MarshalOutputStream;
import com.sun.corba.se.spi.ior.iiop.GIOPVersion;
import org.omg.CORBA.SystemException;
import org.omg.CORBA_2_3.portable.InputStream;
import org.omg.CORBA_2_3.portable.OutputStream;

public class CodeSetServiceContext extends ServiceContext {
  public static final int SERVICE_CONTEXT_ID = 1;
  
  private CodeSetComponentInfo.CodeSetContext csc;
  
  public CodeSetServiceContext(CodeSetComponentInfo.CodeSetContext paramCodeSetContext) { this.csc = paramCodeSetContext; }
  
  public CodeSetServiceContext(InputStream paramInputStream, GIOPVersion paramGIOPVersion) {
    super(paramInputStream, paramGIOPVersion);
    this.csc = new CodeSetComponentInfo.CodeSetContext();
    this.csc.read((MarshalInputStream)this.in);
  }
  
  public int getId() { return 1; }
  
  public void writeData(OutputStream paramOutputStream) throws SystemException { this.csc.write((MarshalOutputStream)paramOutputStream); }
  
  public CodeSetComponentInfo.CodeSetContext getCodeSetContext() { return this.csc; }
  
  public String toString() { return "CodeSetServiceContext[ csc=" + this.csc + " ]"; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\spi\servicecontext\CodeSetServiceContext.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */