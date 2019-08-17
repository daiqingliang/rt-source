package com.sun.corba.se.impl.dynamicany;

import com.sun.corba.se.impl.corba.AnyImpl;
import com.sun.corba.se.impl.logging.ORBUtilSystemException;
import com.sun.corba.se.spi.orb.ORB;
import java.math.BigDecimal;
import org.omg.CORBA.Any;
import org.omg.CORBA.TypeCode;
import org.omg.CORBA.portable.InputStream;
import org.omg.DynamicAny.DynAny;
import org.omg.DynamicAny.DynAnyFactoryPackage.InconsistentTypeCode;
import org.omg.DynamicAny.DynAnyPackage.TypeMismatch;

public class DynAnyUtil {
  static boolean isConsistentType(TypeCode paramTypeCode) {
    int i = paramTypeCode.kind().value();
    return (i != 13 && i != 31 && i != 32);
  }
  
  static boolean isConstructedDynAny(DynAny paramDynAny) {
    int i = paramDynAny.type().kind().value();
    return (i == 19 || i == 15 || i == 20 || i == 16 || i == 17 || i == 28 || i == 29 || i == 30);
  }
  
  static DynAny createMostDerivedDynAny(Any paramAny, ORB paramORB, boolean paramBoolean) throws InconsistentTypeCode {
    if (paramAny == null || !isConsistentType(paramAny.type()))
      throw new InconsistentTypeCode(); 
    switch (paramAny.type().kind().value()) {
      case 19:
        return new DynSequenceImpl(paramORB, paramAny, paramBoolean);
      case 15:
        return new DynStructImpl(paramORB, paramAny, paramBoolean);
      case 20:
        return new DynArrayImpl(paramORB, paramAny, paramBoolean);
      case 16:
        return new DynUnionImpl(paramORB, paramAny, paramBoolean);
      case 17:
        return new DynEnumImpl(paramORB, paramAny, paramBoolean);
      case 28:
        return new DynFixedImpl(paramORB, paramAny, paramBoolean);
      case 29:
        return new DynValueImpl(paramORB, paramAny, paramBoolean);
      case 30:
        return new DynValueBoxImpl(paramORB, paramAny, paramBoolean);
    } 
    return new DynAnyBasicImpl(paramORB, paramAny, paramBoolean);
  }
  
  static DynAny createMostDerivedDynAny(TypeCode paramTypeCode, ORB paramORB) throws InconsistentTypeCode {
    if (paramTypeCode == null || !isConsistentType(paramTypeCode))
      throw new InconsistentTypeCode(); 
    switch (paramTypeCode.kind().value()) {
      case 19:
        return new DynSequenceImpl(paramORB, paramTypeCode);
      case 15:
        return new DynStructImpl(paramORB, paramTypeCode);
      case 20:
        return new DynArrayImpl(paramORB, paramTypeCode);
      case 16:
        return new DynUnionImpl(paramORB, paramTypeCode);
      case 17:
        return new DynEnumImpl(paramORB, paramTypeCode);
      case 28:
        return new DynFixedImpl(paramORB, paramTypeCode);
      case 29:
        return new DynValueImpl(paramORB, paramTypeCode);
      case 30:
        return new DynValueBoxImpl(paramORB, paramTypeCode);
    } 
    return new DynAnyBasicImpl(paramORB, paramTypeCode);
  }
  
  static Any extractAnyFromStream(TypeCode paramTypeCode, InputStream paramInputStream, ORB paramORB) { return AnyImpl.extractAnyFromStream(paramTypeCode, paramInputStream, paramORB); }
  
  static Any createDefaultAnyOfType(TypeCode paramTypeCode, ORB paramORB) {
    ORBUtilSystemException oRBUtilSystemException = ORBUtilSystemException.get(paramORB, "rpc.presentation");
    Any any = paramORB.create_any();
    switch (paramTypeCode.kind().value()) {
      case 8:
        any.insert_boolean(false);
      case 2:
        any.insert_short((short)0);
      case 4:
        any.insert_ushort((short)0);
      case 3:
        any.insert_long(0);
      case 5:
        any.insert_ulong(0);
      case 23:
        any.insert_longlong(0L);
      case 24:
        any.insert_ulonglong(0L);
      case 6:
        any.insert_float(0.0F);
      case 7:
        any.insert_double(0.0D);
      case 10:
        any.insert_octet((byte)0);
      case 9:
        any.insert_char(false);
      case 26:
        any.insert_wchar(false);
      case 18:
        any.type(paramTypeCode);
        any.insert_string("");
      case 27:
        any.type(paramTypeCode);
        any.insert_wstring("");
      case 14:
        any.insert_Object(null);
      case 12:
        any.insert_TypeCode(any.type());
      case 11:
        any.insert_any(paramORB.create_any());
      case 15:
      case 16:
      case 17:
      case 19:
      case 20:
      case 22:
      case 29:
      case 30:
        any.type(paramTypeCode);
      case 28:
        any.insert_fixed(new BigDecimal("0.0"), paramTypeCode);
      case 1:
      case 13:
      case 21:
      case 31:
      case 32:
        any.type(paramTypeCode);
      case 0:
        return any;
      case 25:
        throw oRBUtilSystemException.tkLongDoubleNotSupported();
    } 
    throw oRBUtilSystemException.typecodeNotSupported();
  }
  
  static Any copy(Any paramAny, ORB paramORB) { return new AnyImpl(paramORB, paramAny); }
  
  static DynAny convertToNative(DynAny paramDynAny, ORB paramORB) {
    if (paramDynAny instanceof DynAnyImpl)
      return paramDynAny; 
    try {
      return createMostDerivedDynAny(paramDynAny.to_any(), paramORB, true);
    } catch (InconsistentTypeCode inconsistentTypeCode) {
      return null;
    } 
  }
  
  static boolean isInitialized(Any paramAny) {
    boolean bool = ((AnyImpl)paramAny).isInitialized();
    switch (paramAny.type().kind().value()) {
      case 18:
        return (bool && paramAny.extract_string() != null);
      case 27:
        return (bool && paramAny.extract_wstring() != null);
    } 
    return bool;
  }
  
  static boolean set_current_component(DynAny paramDynAny1, DynAny paramDynAny2) {
    if (paramDynAny2 != null)
      try {
        paramDynAny1.rewind();
        do {
          if (paramDynAny1.current_component() == paramDynAny2)
            return true; 
        } while (paramDynAny1.next());
      } catch (TypeMismatch typeMismatch) {} 
    return false;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\impl\dynamicany\DynAnyUtil.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */