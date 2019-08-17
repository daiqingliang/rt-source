package org.omg.IOP;

import org.omg.CORBA.Any;
import org.omg.CORBA.TypeCode;
import org.omg.IOP.CodecPackage.FormatMismatch;
import org.omg.IOP.CodecPackage.InvalidTypeForEncoding;
import org.omg.IOP.CodecPackage.TypeMismatch;

public interface CodecOperations {
  byte[] encode(Any paramAny) throws InvalidTypeForEncoding;
  
  Any decode(byte[] paramArrayOfByte) throws FormatMismatch;
  
  byte[] encode_value(Any paramAny) throws InvalidTypeForEncoding;
  
  Any decode_value(byte[] paramArrayOfByte, TypeCode paramTypeCode) throws FormatMismatch, TypeMismatch;
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\org\omg\IOP\CodecOperations.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */