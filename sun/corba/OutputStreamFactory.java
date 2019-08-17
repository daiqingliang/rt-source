package sun.corba;

import com.sun.corba.se.impl.encoding.CDROutputObject;
import com.sun.corba.se.impl.encoding.EncapsOutputStream;
import com.sun.corba.se.impl.encoding.TypeCodeOutputStream;
import com.sun.corba.se.impl.protocol.giopmsgheaders.Message;
import com.sun.corba.se.pept.protocol.MessageMediator;
import com.sun.corba.se.spi.ior.iiop.GIOPVersion;
import com.sun.corba.se.spi.orb.ORB;
import com.sun.corba.se.spi.protocol.CorbaMessageMediator;
import com.sun.corba.se.spi.transport.CorbaConnection;
import java.security.AccessController;
import java.security.PrivilegedAction;

public final class OutputStreamFactory {
  public static TypeCodeOutputStream newTypeCodeOutputStream(final ORB orb) { return (TypeCodeOutputStream)AccessController.doPrivileged(new PrivilegedAction<TypeCodeOutputStream>() {
          public TypeCodeOutputStream run() { return new TypeCodeOutputStream(orb); }
        }); }
  
  public static TypeCodeOutputStream newTypeCodeOutputStream(final ORB orb, final boolean littleEndian) { return (TypeCodeOutputStream)AccessController.doPrivileged(new PrivilegedAction<TypeCodeOutputStream>() {
          public TypeCodeOutputStream run() { return new TypeCodeOutputStream(orb, littleEndian); }
        }); }
  
  public static EncapsOutputStream newEncapsOutputStream(final ORB orb) { return (EncapsOutputStream)AccessController.doPrivileged(new PrivilegedAction<EncapsOutputStream>() {
          public EncapsOutputStream run() { return new EncapsOutputStream(orb); }
        }); }
  
  public static EncapsOutputStream newEncapsOutputStream(final ORB orb, final GIOPVersion giopVersion) { return (EncapsOutputStream)AccessController.doPrivileged(new PrivilegedAction<EncapsOutputStream>() {
          public EncapsOutputStream run() { return new EncapsOutputStream(orb, giopVersion); }
        }); }
  
  public static EncapsOutputStream newEncapsOutputStream(final ORB orb, final boolean isLittleEndian) { return (EncapsOutputStream)AccessController.doPrivileged(new PrivilegedAction<EncapsOutputStream>() {
          public EncapsOutputStream run() { return new EncapsOutputStream(orb, isLittleEndian); }
        }); }
  
  public static CDROutputObject newCDROutputObject(final ORB orb, final MessageMediator messageMediator, final Message header, final byte streamFormatVersion) { return (CDROutputObject)AccessController.doPrivileged(new PrivilegedAction<CDROutputObject>() {
          public CDROutputObject run() { return new CDROutputObject(orb, messageMediator, header, streamFormatVersion); }
        }); }
  
  public static CDROutputObject newCDROutputObject(final ORB orb, final MessageMediator messageMediator, final Message header, final byte streamFormatVersion, final int strategy) { return (CDROutputObject)AccessController.doPrivileged(new PrivilegedAction<CDROutputObject>() {
          public CDROutputObject run() { return new CDROutputObject(orb, messageMediator, header, streamFormatVersion, strategy); }
        }); }
  
  public static CDROutputObject newCDROutputObject(final ORB orb, final CorbaMessageMediator mediator, final GIOPVersion giopVersion, final CorbaConnection connection, final Message header, final byte streamFormatVersion) { return (CDROutputObject)AccessController.doPrivileged(new PrivilegedAction<CDROutputObject>() {
          public CDROutputObject run() { return new CDROutputObject(orb, mediator, giopVersion, connection, header, streamFormatVersion); }
        }); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\corba\OutputStreamFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */