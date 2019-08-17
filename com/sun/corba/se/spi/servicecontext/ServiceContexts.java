package com.sun.corba.se.spi.servicecontext;

import com.sun.corba.se.impl.encoding.CDRInputStream;
import com.sun.corba.se.impl.encoding.EncapsInputStream;
import com.sun.corba.se.impl.logging.ORBUtilSystemException;
import com.sun.corba.se.impl.orbutil.ORBUtility;
import com.sun.corba.se.impl.util.Utility;
import com.sun.corba.se.spi.ior.iiop.GIOPVersion;
import com.sun.corba.se.spi.orb.ORB;
import com.sun.org.omg.SendingContext.CodeBase;
import java.util.HashMap;
import java.util.Map;
import org.omg.CORBA.CompletionStatus;
import org.omg.CORBA.OctetSeqHelper;
import org.omg.CORBA_2_3.portable.InputStream;
import org.omg.CORBA_2_3.portable.OutputStream;
import sun.corba.EncapsInputStreamFactory;

public class ServiceContexts {
  private static final int JAVAIDL_ALIGN_SERVICE_ID = -1106033203;
  
  private ORB orb;
  
  private Map scMap;
  
  private boolean addAlignmentOnWrite;
  
  private CodeBase codeBase;
  
  private GIOPVersion giopVersion;
  
  private ORBUtilSystemException wrapper;
  
  private static boolean isDebugging(OutputStream paramOutputStream) {
    ORB oRB = (ORB)paramOutputStream.orb();
    return (oRB == null) ? false : oRB.serviceContextDebugFlag;
  }
  
  private static boolean isDebugging(InputStream paramInputStream) {
    ORB oRB = (ORB)paramInputStream.orb();
    return (oRB == null) ? false : oRB.serviceContextDebugFlag;
  }
  
  private void dprint(String paramString) { ORBUtility.dprint(this, paramString); }
  
  public static void writeNullServiceContext(OutputStream paramOutputStream) {
    if (isDebugging(paramOutputStream))
      ORBUtility.dprint("ServiceContexts", "Writing null service context"); 
    paramOutputStream.write_long(0);
  }
  
  private void createMapFromInputStream(InputStream paramInputStream) {
    this.orb = (ORB)paramInputStream.orb();
    if (this.orb.serviceContextDebugFlag)
      dprint("Constructing ServiceContexts from input stream"); 
    int i = paramInputStream.read_long();
    if (this.orb.serviceContextDebugFlag)
      dprint("Number of service contexts = " + i); 
    for (byte b = 0; b < i; b++) {
      int j = paramInputStream.read_long();
      if (this.orb.serviceContextDebugFlag)
        dprint("Reading service context id " + j); 
      byte[] arrayOfByte = OctetSeqHelper.read(paramInputStream);
      if (this.orb.serviceContextDebugFlag)
        dprint("Service context" + j + " length: " + arrayOfByte.length); 
      this.scMap.put(new Integer(j), arrayOfByte);
    } 
  }
  
  public ServiceContexts(ORB paramORB) {
    this.orb = paramORB;
    this.wrapper = ORBUtilSystemException.get(paramORB, "rpc.protocol");
    this.addAlignmentOnWrite = false;
    this.scMap = new HashMap();
    this.giopVersion = paramORB.getORBData().getGIOPVersion();
    this.codeBase = null;
  }
  
  public ServiceContexts(InputStream paramInputStream) {
    this((ORB)paramInputStream.orb());
    this.codeBase = ((CDRInputStream)paramInputStream).getCodeBase();
    createMapFromInputStream(paramInputStream);
    this.giopVersion = ((CDRInputStream)paramInputStream).getGIOPVersion();
  }
  
  private ServiceContext unmarshal(Integer paramInteger, byte[] paramArrayOfByte) {
    ServiceContextRegistry serviceContextRegistry = this.orb.getServiceContextRegistry();
    ServiceContextData serviceContextData = serviceContextRegistry.findServiceContextData(paramInteger.intValue());
    ServiceContext serviceContext = null;
    if (serviceContextData == null) {
      if (this.orb.serviceContextDebugFlag)
        dprint("Could not find ServiceContextData for " + paramInteger + " using UnknownServiceContext"); 
      serviceContext = new UnknownServiceContext(paramInteger.intValue(), paramArrayOfByte);
    } else {
      if (this.orb.serviceContextDebugFlag)
        dprint("Found " + serviceContextData); 
      EncapsInputStream encapsInputStream = EncapsInputStreamFactory.newEncapsInputStream(this.orb, paramArrayOfByte, paramArrayOfByte.length, this.giopVersion, this.codeBase);
      encapsInputStream.consumeEndian();
      serviceContext = serviceContextData.makeServiceContext(encapsInputStream, this.giopVersion);
      if (serviceContext == null)
        throw this.wrapper.svcctxUnmarshalError(CompletionStatus.COMPLETED_MAYBE); 
    } 
    return serviceContext;
  }
  
  public void addAlignmentPadding() { this.addAlignmentOnWrite = true; }
  
  public void write(OutputStream paramOutputStream, GIOPVersion paramGIOPVersion) {
    if (isDebugging(paramOutputStream)) {
      dprint("Writing service contexts to output stream");
      Utility.printStackTrace();
    } 
    int i = this.scMap.size();
    if (this.addAlignmentOnWrite) {
      if (isDebugging(paramOutputStream))
        dprint("Adding alignment padding"); 
      i++;
    } 
    if (isDebugging(paramOutputStream))
      dprint("Service context has " + i + " components"); 
    paramOutputStream.write_long(i);
    writeServiceContextsInOrder(paramOutputStream, paramGIOPVersion);
    if (this.addAlignmentOnWrite) {
      if (isDebugging(paramOutputStream))
        dprint("Writing alignment padding"); 
      paramOutputStream.write_long(-1106033203);
      paramOutputStream.write_long(4);
      paramOutputStream.write_octet((byte)0);
      paramOutputStream.write_octet((byte)0);
      paramOutputStream.write_octet((byte)0);
      paramOutputStream.write_octet((byte)0);
    } 
    if (isDebugging(paramOutputStream))
      dprint("Service context writing complete"); 
  }
  
  private void writeServiceContextsInOrder(OutputStream paramOutputStream, GIOPVersion paramGIOPVersion) {
    Integer integer = new Integer(9);
    Object object = this.scMap.remove(integer);
    for (Integer integer1 : this.scMap.keySet())
      writeMapEntry(paramOutputStream, integer1, this.scMap.get(integer1), paramGIOPVersion); 
    if (object != null) {
      writeMapEntry(paramOutputStream, integer, object, paramGIOPVersion);
      this.scMap.put(integer, object);
    } 
  }
  
  private void writeMapEntry(OutputStream paramOutputStream, Integer paramInteger, Object paramObject, GIOPVersion paramGIOPVersion) {
    if (paramObject instanceof byte[]) {
      if (isDebugging(paramOutputStream))
        dprint("Writing service context bytes for id " + paramInteger); 
      OctetSeqHelper.write(paramOutputStream, (byte[])paramObject);
    } else {
      ServiceContext serviceContext = (ServiceContext)paramObject;
      if (isDebugging(paramOutputStream))
        dprint("Writing service context " + serviceContext); 
      serviceContext.write(paramOutputStream, paramGIOPVersion);
    } 
  }
  
  public void put(ServiceContext paramServiceContext) {
    Integer integer = new Integer(paramServiceContext.getId());
    this.scMap.put(integer, paramServiceContext);
  }
  
  public void delete(int paramInt) { delete(new Integer(paramInt)); }
  
  public void delete(Integer paramInteger) { this.scMap.remove(paramInteger); }
  
  public ServiceContext get(int paramInt) { return get(new Integer(paramInt)); }
  
  public ServiceContext get(Integer paramInteger) {
    Object object = this.scMap.get(paramInteger);
    if (object == null)
      return null; 
    if (object instanceof byte[]) {
      ServiceContext serviceContext = unmarshal(paramInteger, (byte[])object);
      this.scMap.put(paramInteger, serviceContext);
      return serviceContext;
    } 
    return (ServiceContext)object;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\spi\servicecontext\ServiceContexts.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */