package com.sun.corba.se.impl.interceptors;

import com.sun.corba.se.impl.corba.AnyImpl;
import com.sun.corba.se.impl.encoding.EncapsInputStream;
import com.sun.corba.se.impl.encoding.EncapsOutputStream;
import com.sun.corba.se.impl.logging.ORBUtilSystemException;
import com.sun.corba.se.spi.ior.iiop.GIOPVersion;
import com.sun.corba.se.spi.orb.ORB;
import org.omg.CORBA.Any;
import org.omg.CORBA.LocalObject;
import org.omg.CORBA.ORB;
import org.omg.CORBA.TypeCode;
import org.omg.IOP.Codec;
import org.omg.IOP.CodecPackage.FormatMismatch;
import org.omg.IOP.CodecPackage.InvalidTypeForEncoding;
import org.omg.IOP.CodecPackage.TypeMismatch;
import sun.corba.EncapsInputStreamFactory;
import sun.corba.OutputStreamFactory;

public final class CDREncapsCodec extends LocalObject implements Codec {
  private ORB orb;
  
  ORBUtilSystemException wrapper;
  
  private GIOPVersion giopVersion;
  
  public CDREncapsCodec(ORB paramORB, int paramInt1, int paramInt2) {
    this.orb = paramORB;
    this.wrapper = ORBUtilSystemException.get((ORB)paramORB, "rpc.protocol");
    this.giopVersion = GIOPVersion.getInstance((byte)paramInt1, (byte)paramInt2);
  }
  
  public byte[] encode(Any paramAny) throws InvalidTypeForEncoding {
    if (paramAny == null)
      throw this.wrapper.nullParam(); 
    return encodeImpl(paramAny, true);
  }
  
  public Any decode(byte[] paramArrayOfByte) throws FormatMismatch {
    if (paramArrayOfByte == null)
      throw this.wrapper.nullParam(); 
    return decodeImpl(paramArrayOfByte, null);
  }
  
  public byte[] encode_value(Any paramAny) throws InvalidTypeForEncoding {
    if (paramAny == null)
      throw this.wrapper.nullParam(); 
    return encodeImpl(paramAny, false);
  }
  
  public Any decode_value(byte[] paramArrayOfByte, TypeCode paramTypeCode) throws FormatMismatch, TypeMismatch {
    if (paramArrayOfByte == null)
      throw this.wrapper.nullParam(); 
    if (paramTypeCode == null)
      throw this.wrapper.nullParam(); 
    return decodeImpl(paramArrayOfByte, paramTypeCode);
  }
  
  private byte[] encodeImpl(Any paramAny, boolean paramBoolean) throws InvalidTypeForEncoding {
    if (paramAny == null)
      throw this.wrapper.nullParam(); 
    EncapsOutputStream encapsOutputStream = OutputStreamFactory.newEncapsOutputStream((ORB)this.orb, this.giopVersion);
    encapsOutputStream.putEndian();
    if (paramBoolean)
      encapsOutputStream.write_TypeCode(paramAny.type()); 
    paramAny.write_value(encapsOutputStream);
    return encapsOutputStream.toByteArray();
  }
  
  private Any decodeImpl(byte[] paramArrayOfByte, TypeCode paramTypeCode) throws FormatMismatch, TypeMismatch {
    if (paramArrayOfByte == null)
      throw this.wrapper.nullParam(); 
    AnyImpl anyImpl = null;
    try {
      EncapsInputStream encapsInputStream = EncapsInputStreamFactory.newEncapsInputStream(this.orb, paramArrayOfByte, paramArrayOfByte.length, this.giopVersion);
      encapsInputStream.consumeEndian();
      if (paramTypeCode == null)
        paramTypeCode = encapsInputStream.read_TypeCode(); 
      anyImpl = new AnyImpl((ORB)this.orb);
      anyImpl.read_value(encapsInputStream, paramTypeCode);
    } catch (RuntimeException runtimeException) {
      throw new FormatMismatch();
    } 
    return anyImpl;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\impl\interceptors\CDREncapsCodec.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */