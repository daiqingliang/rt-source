package com.sun.corba.se.spi.ior.iiop;

import com.sun.corba.se.spi.ior.IOR;
import com.sun.corba.se.spi.orb.ORB;
import com.sun.corba.se.spi.orb.ORBVersion;
import com.sun.corba.se.spi.orb.ORBVersionFactory;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;

public class GIOPVersion {
  public static final GIOPVersion V1_0 = new GIOPVersion((byte)1, (byte)0);
  
  public static final GIOPVersion V1_1 = new GIOPVersion((byte)1, (byte)1);
  
  public static final GIOPVersion V1_2 = new GIOPVersion((byte)1, (byte)2);
  
  public static final GIOPVersion V1_3 = new GIOPVersion((byte)1, (byte)3);
  
  public static final GIOPVersion V13_XX = new GIOPVersion((byte)13, (byte)1);
  
  public static final GIOPVersion DEFAULT_VERSION = V1_2;
  
  public static final int VERSION_1_0 = 256;
  
  public static final int VERSION_1_1 = 257;
  
  public static final int VERSION_1_2 = 258;
  
  public static final int VERSION_1_3 = 259;
  
  public static final int VERSION_13_XX = 3329;
  
  private byte major = 0;
  
  private byte minor = 0;
  
  public GIOPVersion() {}
  
  public GIOPVersion(byte paramByte1, byte paramByte2) {
    this.major = paramByte1;
    this.minor = paramByte2;
  }
  
  public GIOPVersion(int paramInt1, int paramInt2) {
    this.major = (byte)paramInt1;
    this.minor = (byte)paramInt2;
  }
  
  public byte getMajor() { return this.major; }
  
  public byte getMinor() { return this.minor; }
  
  public boolean equals(GIOPVersion paramGIOPVersion) { return (paramGIOPVersion.major == this.major && paramGIOPVersion.minor == this.minor); }
  
  public boolean equals(Object paramObject) { return (paramObject != null && paramObject instanceof GIOPVersion) ? equals((GIOPVersion)paramObject) : 0; }
  
  public int hashCode() { return 37 * this.major + this.minor; }
  
  public boolean lessThan(GIOPVersion paramGIOPVersion) { return (this.major < paramGIOPVersion.major) ? true : ((this.major == paramGIOPVersion.major && this.minor < paramGIOPVersion.minor)); }
  
  public int intValue() { return this.major << 8 | this.minor; }
  
  public String toString() { return this.major + "." + this.minor; }
  
  public static GIOPVersion getInstance(byte paramByte1, byte paramByte2) {
    switch (paramByte1 << 8 | paramByte2) {
      case 256:
        return V1_0;
      case 257:
        return V1_1;
      case 258:
        return V1_2;
      case 259:
        return V1_3;
      case 3329:
        return V13_XX;
    } 
    return new GIOPVersion(paramByte1, paramByte2);
  }
  
  public static GIOPVersion parseVersion(String paramString) {
    int i = paramString.indexOf('.');
    if (i < 1 || i == paramString.length() - 1)
      throw new NumberFormatException("GIOP major, minor, and decimal point required: " + paramString); 
    int j = Integer.parseInt(paramString.substring(0, i));
    int k = Integer.parseInt(paramString.substring(i + 1, paramString.length()));
    return getInstance((byte)j, (byte)k);
  }
  
  public static GIOPVersion chooseRequestVersion(ORB paramORB, IOR paramIOR) {
    GIOPVersion gIOPVersion1 = paramORB.getORBData().getGIOPVersion();
    IIOPProfile iIOPProfile = paramIOR.getProfile();
    GIOPVersion gIOPVersion2 = iIOPProfile.getGIOPVersion();
    ORBVersion oRBVersion = iIOPProfile.getORBVersion();
    if (!oRBVersion.equals(ORBVersionFactory.getFOREIGN()) && oRBVersion.lessThan(ORBVersionFactory.getNEWER()))
      return V1_0; 
    byte b1 = gIOPVersion2.getMajor();
    byte b2 = gIOPVersion2.getMinor();
    byte b3 = gIOPVersion1.getMajor();
    byte b4 = gIOPVersion1.getMinor();
    return (b3 < b1) ? gIOPVersion1 : ((b3 > b1) ? gIOPVersion2 : ((b4 <= b2) ? gIOPVersion1 : gIOPVersion2));
  }
  
  public boolean supportsIORIIOPProfileComponents() { return (getMinor() > 0 || getMajor() > 1); }
  
  public void read(InputStream paramInputStream) {
    this.major = paramInputStream.read_octet();
    this.minor = paramInputStream.read_octet();
  }
  
  public void write(OutputStream paramOutputStream) {
    paramOutputStream.write_octet(this.major);
    paramOutputStream.write_octet(this.minor);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\spi\ior\iiop\GIOPVersion.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */