package com.sun.corba.se.impl.corba;

import com.sun.corba.se.impl.encoding.CDRInputStream;
import com.sun.corba.se.impl.encoding.CDROutputStream;
import com.sun.corba.se.impl.logging.ORBUtilSystemException;
import com.sun.corba.se.spi.orb.ORB;
import java.io.Serializable;
import java.math.BigDecimal;
import org.omg.CORBA.Any;
import org.omg.CORBA.Object;
import org.omg.CORBA.Principal;
import org.omg.CORBA.TypeCode;
import org.omg.CORBA.TypeCodePackage.BadKind;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.Streamable;
import org.omg.CORBA_2_3.portable.InputStream;
import org.omg.CORBA_2_3.portable.OutputStream;

public final class TCUtility {
  static void marshalIn(OutputStream paramOutputStream, TypeCode paramTypeCode, long paramLong, Object paramObject) {
    switch (paramTypeCode.kind().value()) {
      case 0:
      case 1:
      case 31:
        return;
      case 2:
        paramOutputStream.write_short((short)(int)(paramLong & 0xFFFFL));
      case 4:
        paramOutputStream.write_ushort((short)(int)(paramLong & 0xFFFFL));
      case 3:
      case 17:
        paramOutputStream.write_long((int)(paramLong & 0xFFFFFFFFL));
      case 5:
        paramOutputStream.write_ulong((int)(paramLong & 0xFFFFFFFFL));
      case 6:
        paramOutputStream.write_float(Float.intBitsToFloat((int)(paramLong & 0xFFFFFFFFL)));
      case 7:
        paramOutputStream.write_double(Double.longBitsToDouble(paramLong));
      case 8:
        if (paramLong == 0L) {
          paramOutputStream.write_boolean(false);
        } else {
          paramOutputStream.write_boolean(true);
        } 
      case 9:
        paramOutputStream.write_char((char)(int)(paramLong & 0xFFFFL));
      case 10:
        paramOutputStream.write_octet((byte)(int)(paramLong & 0xFFL));
      case 11:
        paramOutputStream.write_any((Any)paramObject);
      case 12:
        paramOutputStream.write_TypeCode((TypeCode)paramObject);
      case 13:
        paramOutputStream.write_Principal((Principal)paramObject);
      case 14:
        paramOutputStream.write_Object((Object)paramObject);
      case 23:
        paramOutputStream.write_longlong(paramLong);
      case 24:
        paramOutputStream.write_ulonglong(paramLong);
      case 26:
        paramOutputStream.write_wchar((char)(int)(paramLong & 0xFFFFL));
      case 18:
        paramOutputStream.write_string((String)paramObject);
      case 27:
        paramOutputStream.write_wstring((String)paramObject);
      case 29:
      case 30:
        ((OutputStream)paramOutputStream).write_value((Serializable)paramObject);
      case 28:
        if (paramOutputStream instanceof CDROutputStream) {
          try {
            ((CDROutputStream)paramOutputStream).write_fixed((BigDecimal)paramObject, paramTypeCode.fixed_digits(), paramTypeCode.fixed_scale());
          } catch (BadKind badKind) {}
        } else {
          paramOutputStream.write_fixed((BigDecimal)paramObject);
        } 
      case 15:
      case 16:
      case 19:
      case 20:
      case 21:
      case 22:
        ((Streamable)paramObject)._write(paramOutputStream);
      case 32:
        ((OutputStream)paramOutputStream).write_abstract_interface(paramObject);
    } 
    ORBUtilSystemException oRBUtilSystemException = ORBUtilSystemException.get((ORB)paramOutputStream.orb(), "rpc.presentation");
    throw oRBUtilSystemException.typecodeNotSupported();
  }
  
  static void unmarshalIn(InputStream paramInputStream, TypeCode paramTypeCode, long[] paramArrayOfLong, Object[] paramArrayOfObject) {
    ORBUtilSystemException oRBUtilSystemException;
    int i = paramTypeCode.kind().value();
    long l = 0L;
    Object object = paramArrayOfObject[0];
    switch (i) {
      case 0:
      case 1:
      case 31:
        break;
      case 2:
        l = paramInputStream.read_short() & 0xFFFFL;
        break;
      case 4:
        l = paramInputStream.read_ushort() & 0xFFFFL;
        break;
      case 3:
      case 17:
        l = paramInputStream.read_long() & 0xFFFFFFFFL;
        break;
      case 5:
        l = paramInputStream.read_ulong() & 0xFFFFFFFFL;
        break;
      case 6:
        l = Float.floatToIntBits(paramInputStream.read_float()) & 0xFFFFFFFFL;
        break;
      case 7:
        l = Double.doubleToLongBits(paramInputStream.read_double());
        break;
      case 9:
        l = paramInputStream.read_char() & 0xFFFFL;
        break;
      case 10:
        l = paramInputStream.read_octet() & 0xFFL;
        break;
      case 8:
        if (paramInputStream.read_boolean()) {
          l = 1L;
          break;
        } 
        l = 0L;
        break;
      case 11:
        object = paramInputStream.read_any();
        break;
      case 12:
        object = paramInputStream.read_TypeCode();
        break;
      case 13:
        object = paramInputStream.read_Principal();
        break;
      case 14:
        if (object instanceof Streamable) {
          ((Streamable)object)._read(paramInputStream);
          break;
        } 
        object = paramInputStream.read_Object();
        break;
      case 23:
        l = paramInputStream.read_longlong();
        break;
      case 24:
        l = paramInputStream.read_ulonglong();
        break;
      case 26:
        l = paramInputStream.read_wchar() & 0xFFFFL;
        break;
      case 18:
        object = paramInputStream.read_string();
        break;
      case 27:
        object = paramInputStream.read_wstring();
        break;
      case 29:
      case 30:
        object = ((InputStream)paramInputStream).read_value();
        break;
      case 28:
        try {
          if (paramInputStream instanceof CDRInputStream) {
            object = ((CDRInputStream)paramInputStream).read_fixed(paramTypeCode.fixed_digits(), paramTypeCode.fixed_scale());
            break;
          } 
          BigDecimal bigDecimal = paramInputStream.read_fixed();
          object = bigDecimal.movePointLeft(paramTypeCode.fixed_scale());
        } catch (BadKind badKind) {}
        break;
      case 15:
      case 16:
      case 19:
      case 20:
      case 21:
      case 22:
        ((Streamable)object)._read(paramInputStream);
        break;
      case 32:
        object = ((InputStream)paramInputStream).read_abstract_interface();
        break;
      default:
        oRBUtilSystemException = ORBUtilSystemException.get((ORB)paramInputStream.orb(), "rpc.presentation");
        throw oRBUtilSystemException.typecodeNotSupported();
    } 
    paramArrayOfObject[0] = object;
    paramArrayOfLong[0] = l;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\impl\corba\TCUtility.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */