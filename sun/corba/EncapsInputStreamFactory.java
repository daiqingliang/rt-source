package sun.corba;

import com.sun.corba.se.impl.encoding.EncapsInputStream;
import com.sun.corba.se.impl.encoding.TypeCodeInputStream;
import com.sun.corba.se.spi.ior.iiop.GIOPVersion;
import com.sun.org.omg.SendingContext.CodeBase;
import java.nio.ByteBuffer;
import java.security.AccessController;
import java.security.PrivilegedAction;
import org.omg.CORBA.ORB;

public class EncapsInputStreamFactory {
  public static EncapsInputStream newEncapsInputStream(final ORB orb, final byte[] buf, final int size, final boolean littleEndian, final GIOPVersion version) { return (EncapsInputStream)AccessController.doPrivileged(new PrivilegedAction<EncapsInputStream>() {
          public EncapsInputStream run() { return new EncapsInputStream(orb, buf, size, littleEndian, version); }
        }); }
  
  public static EncapsInputStream newEncapsInputStream(final ORB orb, final ByteBuffer byteBuffer, final int size, final boolean littleEndian, final GIOPVersion version) { return (EncapsInputStream)AccessController.doPrivileged(new PrivilegedAction<EncapsInputStream>() {
          public EncapsInputStream run() { return new EncapsInputStream(orb, byteBuffer, size, littleEndian, version); }
        }); }
  
  public static EncapsInputStream newEncapsInputStream(final ORB orb, final byte[] data, final int size) { return (EncapsInputStream)AccessController.doPrivileged(new PrivilegedAction<EncapsInputStream>() {
          public EncapsInputStream run() { return new EncapsInputStream(orb, data, size); }
        }); }
  
  public static EncapsInputStream newEncapsInputStream(final EncapsInputStream eis) { return (EncapsInputStream)AccessController.doPrivileged(new PrivilegedAction<EncapsInputStream>() {
          public EncapsInputStream run() { return new EncapsInputStream(eis); }
        }); }
  
  public static EncapsInputStream newEncapsInputStream(final ORB orb, final byte[] data, final int size, final GIOPVersion version) { return (EncapsInputStream)AccessController.doPrivileged(new PrivilegedAction<EncapsInputStream>() {
          public EncapsInputStream run() { return new EncapsInputStream(orb, data, size, version); }
        }); }
  
  public static EncapsInputStream newEncapsInputStream(final ORB orb, final byte[] data, final int size, final GIOPVersion version, final CodeBase codeBase) { return (EncapsInputStream)AccessController.doPrivileged(new PrivilegedAction<EncapsInputStream>() {
          public EncapsInputStream run() { return new EncapsInputStream(orb, data, size, version, codeBase); }
        }); }
  
  public static TypeCodeInputStream newTypeCodeInputStream(final ORB orb, final byte[] buf, final int size, final boolean littleEndian, final GIOPVersion version) { return (TypeCodeInputStream)AccessController.doPrivileged(new PrivilegedAction<TypeCodeInputStream>() {
          public TypeCodeInputStream run() { return new TypeCodeInputStream(orb, buf, size, littleEndian, version); }
        }); }
  
  public static TypeCodeInputStream newTypeCodeInputStream(final ORB orb, final ByteBuffer byteBuffer, final int size, final boolean littleEndian, final GIOPVersion version) { return (TypeCodeInputStream)AccessController.doPrivileged(new PrivilegedAction<TypeCodeInputStream>() {
          public TypeCodeInputStream run() { return new TypeCodeInputStream(orb, byteBuffer, size, littleEndian, version); }
        }); }
  
  public static TypeCodeInputStream newTypeCodeInputStream(final ORB orb, final byte[] data, final int size) { return (TypeCodeInputStream)AccessController.doPrivileged(new PrivilegedAction<TypeCodeInputStream>() {
          public TypeCodeInputStream run() { return new TypeCodeInputStream(orb, data, size); }
        }); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\corba\EncapsInputStreamFactory.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */