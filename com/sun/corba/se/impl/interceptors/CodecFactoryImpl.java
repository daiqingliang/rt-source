package com.sun.corba.se.impl.interceptors;

import com.sun.corba.se.impl.logging.ORBUtilSystemException;
import com.sun.corba.se.spi.orb.ORB;
import org.omg.CORBA.LocalObject;
import org.omg.CORBA.ORB;
import org.omg.IOP.Codec;
import org.omg.IOP.CodecFactory;
import org.omg.IOP.CodecFactoryPackage.UnknownEncoding;
import org.omg.IOP.Encoding;

public final class CodecFactoryImpl extends LocalObject implements CodecFactory {
  private ORB orb;
  
  private ORBUtilSystemException wrapper;
  
  private static final int MAX_MINOR_VERSION_SUPPORTED = 2;
  
  private Codec[] codecs = new Codec[3];
  
  public CodecFactoryImpl(ORB paramORB) {
    this.orb = paramORB;
    this.wrapper = ORBUtilSystemException.get((ORB)paramORB, "rpc.protocol");
    for (byte b = 0; b <= 2; b++)
      this.codecs[b] = new CDREncapsCodec(paramORB, 1, b); 
  }
  
  public Codec create_codec(Encoding paramEncoding) throws UnknownEncoding {
    if (paramEncoding == null)
      nullParam(); 
    Codec codec = null;
    if (paramEncoding.format == 0 && paramEncoding.major_version == 1 && paramEncoding.minor_version >= 0 && paramEncoding.minor_version <= 2)
      codec = this.codecs[paramEncoding.minor_version]; 
    if (codec == null)
      throw new UnknownEncoding(); 
    return codec;
  }
  
  private void nullParam() { throw this.wrapper.nullParam(); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\impl\interceptors\CodecFactoryImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */