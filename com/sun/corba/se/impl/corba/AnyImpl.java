package com.sun.corba.se.impl.corba;

import com.sun.corba.se.impl.encoding.CDRInputStream;
import com.sun.corba.se.impl.encoding.EncapsInputStream;
import com.sun.corba.se.impl.encoding.EncapsOutputStream;
import com.sun.corba.se.impl.io.ValueUtility;
import com.sun.corba.se.impl.logging.ORBUtilSystemException;
import com.sun.corba.se.impl.orbutil.ORBUtility;
import com.sun.corba.se.impl.orbutil.RepositoryIdFactory;
import com.sun.corba.se.impl.orbutil.RepositoryIdStrings;
import com.sun.corba.se.spi.orb.ORB;
import com.sun.corba.se.spi.orb.ORBVersionFactory;
import com.sun.corba.se.spi.presentation.rmi.StubAdapter;
import java.io.Serializable;
import java.math.BigDecimal;
import java.security.AccessController;
import java.security.PrivilegedAction;
import java.util.ArrayList;
import org.omg.CORBA.Any;
import org.omg.CORBA.CompletionStatus;
import org.omg.CORBA.Object;
import org.omg.CORBA.Principal;
import org.omg.CORBA.TCKind;
import org.omg.CORBA.TypeCode;
import org.omg.CORBA.TypeCodePackage.BadKind;
import org.omg.CORBA.TypeCodePackage.Bounds;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA.portable.Streamable;
import org.omg.CORBA_2_3.portable.InputStream;
import org.omg.CORBA_2_3.portable.OutputStream;

public class AnyImpl extends Any {
  private TypeCodeImpl typeCode;
  
  protected ORB orb;
  
  private ORBUtilSystemException wrapper;
  
  private CDRInputStream stream;
  
  private long value;
  
  private Object object;
  
  private boolean isInitialized = false;
  
  private static final int DEFAULT_BUFFER_SIZE = 32;
  
  static boolean[] isStreamed = { 
      false, false, false, false, false, false, false, false, false, false, 
      false, false, false, true, false, true, true, false, false, true, 
      true, true, true, false, false, false, false, false, false, false, 
      false, false, false };
  
  static AnyImpl convertToNative(ORB paramORB, Any paramAny) {
    if (paramAny instanceof AnyImpl)
      return (AnyImpl)paramAny; 
    AnyImpl anyImpl = new AnyImpl(paramORB, paramAny);
    anyImpl.typeCode = TypeCodeImpl.convertToNative(paramORB, anyImpl.typeCode);
    return anyImpl;
  }
  
  public AnyImpl(ORB paramORB) {
    this.orb = paramORB;
    this.wrapper = ORBUtilSystemException.get(paramORB, "rpc.presentation");
    this.typeCode = paramORB.get_primitive_tc(0);
    this.stream = null;
    this.object = null;
    this.value = 0L;
    this.isInitialized = true;
  }
  
  public AnyImpl(ORB paramORB, Any paramAny) {
    this(paramORB);
    if (paramAny instanceof AnyImpl) {
      AnyImpl anyImpl = (AnyImpl)paramAny;
      this.typeCode = anyImpl.typeCode;
      this.value = anyImpl.value;
      this.object = anyImpl.object;
      this.isInitialized = anyImpl.isInitialized;
      if (anyImpl.stream != null)
        this.stream = anyImpl.stream.dup(); 
    } else {
      read_value(paramAny.create_input_stream(), paramAny.type());
    } 
  }
  
  public TypeCode type() { return this.typeCode; }
  
  private TypeCode realType() { return realType(this.typeCode); }
  
  private TypeCode realType(TypeCode paramTypeCode) {
    TypeCode typeCode1 = paramTypeCode;
    try {
      while (typeCode1.kind().value() == 21)
        typeCode1 = typeCode1.content_type(); 
    } catch (BadKind badKind) {
      throw this.wrapper.badkindCannotOccur(badKind);
    } 
    return typeCode1;
  }
  
  public void type(TypeCode paramTypeCode) {
    this.typeCode = TypeCodeImpl.convertToNative(this.orb, paramTypeCode);
    this.stream = null;
    this.value = 0L;
    this.object = null;
    this.isInitialized = (paramTypeCode.kind().value() == 0);
  }
  
  public boolean equal(Any paramAny) {
    InputStream inputStream2;
    InputStream inputStream1;
    if (paramAny == this)
      return true; 
    if (!this.typeCode.equal(paramAny.type()))
      return false; 
    TypeCode typeCode1 = realType();
    switch (typeCode1.kind().value()) {
      case 0:
      case 1:
        return true;
      case 2:
        return (extract_short() == paramAny.extract_short());
      case 3:
        return (extract_long() == paramAny.extract_long());
      case 4:
        return (extract_ushort() == paramAny.extract_ushort());
      case 5:
        return (extract_ulong() == paramAny.extract_ulong());
      case 6:
        return (extract_float() == paramAny.extract_float());
      case 7:
        return (extract_double() == paramAny.extract_double());
      case 8:
        return (extract_boolean() == paramAny.extract_boolean());
      case 9:
        return (extract_char() == paramAny.extract_char());
      case 26:
        return (extract_wchar() == paramAny.extract_wchar());
      case 10:
        return (extract_octet() == paramAny.extract_octet());
      case 11:
        return extract_any().equal(paramAny.extract_any());
      case 12:
        return extract_TypeCode().equal(paramAny.extract_TypeCode());
      case 18:
        return extract_string().equals(paramAny.extract_string());
      case 27:
        return extract_wstring().equals(paramAny.extract_wstring());
      case 23:
        return (extract_longlong() == paramAny.extract_longlong());
      case 24:
        return (extract_ulonglong() == paramAny.extract_ulonglong());
      case 14:
        return extract_Object().equals(paramAny.extract_Object());
      case 13:
        return extract_Principal().equals(paramAny.extract_Principal());
      case 17:
        return (extract_long() == paramAny.extract_long());
      case 28:
        return (extract_fixed().compareTo(paramAny.extract_fixed()) == 0);
      case 15:
      case 16:
      case 19:
      case 20:
      case 22:
        inputStream1 = create_input_stream();
        inputStream2 = paramAny.create_input_stream();
        return equalMember(typeCode1, inputStream1, inputStream2);
      case 29:
      case 30:
        return extract_Value().equals(paramAny.extract_Value());
      case 21:
        throw this.wrapper.errorResolvingAlias();
      case 25:
        throw this.wrapper.tkLongDoubleNotSupported();
    } 
    throw this.wrapper.typecodeNotSupported();
  }
  
  private boolean equalMember(TypeCode paramTypeCode, InputStream paramInputStream1, InputStream paramInputStream2) {
    TypeCode typeCode1 = realType(paramTypeCode);
    try {
      int k;
      TypeCodeImpl typeCodeImpl;
      Any any2;
      byte b2;
      InputStream inputStream2;
      byte b1;
      Any any1;
      int j;
      InputStream inputStream1;
      int i;
      switch (typeCode1.kind().value()) {
        case 0:
        case 1:
          return true;
        case 2:
          return (paramInputStream1.read_short() == paramInputStream2.read_short());
        case 3:
          return (paramInputStream1.read_long() == paramInputStream2.read_long());
        case 4:
          return (paramInputStream1.read_ushort() == paramInputStream2.read_ushort());
        case 5:
          return (paramInputStream1.read_ulong() == paramInputStream2.read_ulong());
        case 6:
          return (paramInputStream1.read_float() == paramInputStream2.read_float());
        case 7:
          return (paramInputStream1.read_double() == paramInputStream2.read_double());
        case 8:
          return (paramInputStream1.read_boolean() == paramInputStream2.read_boolean());
        case 9:
          return (paramInputStream1.read_char() == paramInputStream2.read_char());
        case 26:
          return (paramInputStream1.read_wchar() == paramInputStream2.read_wchar());
        case 10:
          return (paramInputStream1.read_octet() == paramInputStream2.read_octet());
        case 11:
          return paramInputStream1.read_any().equal(paramInputStream2.read_any());
        case 12:
          return paramInputStream1.read_TypeCode().equal(paramInputStream2.read_TypeCode());
        case 18:
          return paramInputStream1.read_string().equals(paramInputStream2.read_string());
        case 27:
          return paramInputStream1.read_wstring().equals(paramInputStream2.read_wstring());
        case 23:
          return (paramInputStream1.read_longlong() == paramInputStream2.read_longlong());
        case 24:
          return (paramInputStream1.read_ulonglong() == paramInputStream2.read_ulonglong());
        case 14:
          return paramInputStream1.read_Object().equals(paramInputStream2.read_Object());
        case 13:
          return paramInputStream1.read_Principal().equals(paramInputStream2.read_Principal());
        case 17:
          return (paramInputStream1.read_long() == paramInputStream2.read_long());
        case 28:
          return (paramInputStream1.read_fixed().compareTo(paramInputStream2.read_fixed()) == 0);
        case 15:
        case 22:
          j = typeCode1.member_count();
          for (b2 = 0; b2 < j; b2++) {
            if (!equalMember(typeCode1.member_type(b2), paramInputStream1, paramInputStream2))
              return false; 
          } 
          return true;
        case 16:
          any1 = this.orb.create_any();
          any2 = this.orb.create_any();
          any1.read_value(paramInputStream1, typeCode1.discriminator_type());
          any2.read_value(paramInputStream2, typeCode1.discriminator_type());
          if (!any1.equal(any2))
            return false; 
          typeCodeImpl = TypeCodeImpl.convertToNative(this.orb, typeCode1);
          k = typeCodeImpl.currentUnionMemberIndex(any1);
          if (k == -1)
            throw this.wrapper.unionDiscriminatorError(); 
          return !!equalMember(typeCode1.member_type(k), paramInputStream1, paramInputStream2);
        case 19:
          i = paramInputStream1.read_long();
          paramInputStream2.read_long();
          for (b1 = 0; b1 < i; b1++) {
            if (!equalMember(typeCode1.content_type(), paramInputStream1, paramInputStream2))
              return false; 
          } 
          return true;
        case 20:
          i = typeCode1.member_count();
          for (b1 = 0; b1 < i; b1++) {
            if (!equalMember(typeCode1.content_type(), paramInputStream1, paramInputStream2))
              return false; 
          } 
          return true;
        case 29:
        case 30:
          inputStream1 = (InputStream)paramInputStream1;
          inputStream2 = (InputStream)paramInputStream2;
          return inputStream1.read_value().equals(inputStream2.read_value());
        case 21:
          throw this.wrapper.errorResolvingAlias();
        case 25:
          throw this.wrapper.tkLongDoubleNotSupported();
      } 
      throw this.wrapper.typecodeNotSupported();
    } catch (BadKind badKind) {
      throw this.wrapper.badkindCannotOccur();
    } catch (Bounds bounds) {
      throw this.wrapper.boundsCannotOccur();
    } 
  }
  
  public OutputStream create_output_stream() {
    final ORB finalorb = this.orb;
    return (OutputStream)AccessController.doPrivileged(new PrivilegedAction<AnyOutputStream>() {
          public AnyImpl.AnyOutputStream run() { return new AnyImpl.AnyOutputStream(finalorb); }
        });
  }
  
  public InputStream create_input_stream() {
    if (isStreamed[realType().kind().value()])
      return this.stream.dup(); 
    OutputStream outputStream = this.orb.create_output_stream();
    TCUtility.marshalIn(outputStream, realType(), this.value, this.object);
    return outputStream.create_input_stream();
  }
  
  public void read_value(InputStream paramInputStream, TypeCode paramTypeCode) {
    this.typeCode = TypeCodeImpl.convertToNative(this.orb, paramTypeCode);
    int i = realType().kind().value();
    if (i >= isStreamed.length)
      throw this.wrapper.invalidIsstreamedTckind(CompletionStatus.COMPLETED_MAYBE, new Integer(i)); 
    if (isStreamed[i]) {
      if (paramInputStream instanceof AnyInputStream) {
        this.stream = (CDRInputStream)paramInputStream;
      } else {
        OutputStream outputStream = (OutputStream)this.orb.create_output_stream();
        this.typeCode.copy((InputStream)paramInputStream, outputStream);
        this.stream = (CDRInputStream)outputStream.create_input_stream();
      } 
    } else {
      Object[] arrayOfObject = new Object[1];
      arrayOfObject[0] = this.object;
      long[] arrayOfLong = new long[1];
      TCUtility.unmarshalIn(paramInputStream, realType(), arrayOfLong, arrayOfObject);
      this.value = arrayOfLong[0];
      this.object = arrayOfObject[0];
      this.stream = null;
    } 
    this.isInitialized = true;
  }
  
  public void write_value(OutputStream paramOutputStream) {
    if (isStreamed[realType().kind().value()]) {
      this.typeCode.copy(this.stream.dup(), paramOutputStream);
    } else {
      TCUtility.marshalIn(paramOutputStream, realType(), this.value, this.object);
    } 
  }
  
  public void insert_Streamable(Streamable paramStreamable) {
    this.typeCode = TypeCodeImpl.convertToNative(this.orb, paramStreamable._type());
    this.object = paramStreamable;
    this.isInitialized = true;
  }
  
  public Streamable extract_Streamable() { return (Streamable)this.object; }
  
  public void insert_short(short paramShort) {
    this.typeCode = this.orb.get_primitive_tc(2);
    this.value = paramShort;
    this.isInitialized = true;
  }
  
  private String getTCKindName(int paramInt) { return (paramInt >= 0 && paramInt < TypeCodeImpl.kindNames.length) ? TypeCodeImpl.kindNames[paramInt] : ("UNKNOWN(" + paramInt + ")"); }
  
  private void checkExtractBadOperation(int paramInt) {
    if (!this.isInitialized)
      throw this.wrapper.extractNotInitialized(); 
    int i = realType().kind().value();
    if (i != paramInt) {
      String str1 = getTCKindName(i);
      String str2 = getTCKindName(paramInt);
      throw this.wrapper.extractWrongType(str2, str1);
    } 
  }
  
  private void checkExtractBadOperationList(int[] paramArrayOfInt) {
    if (!this.isInitialized)
      throw this.wrapper.extractNotInitialized(); 
    int i = realType().kind().value();
    for (byte b1 = 0; b1 < paramArrayOfInt.length; b1++) {
      if (i == paramArrayOfInt[b1])
        return; 
    } 
    ArrayList arrayList = new ArrayList();
    for (byte b2 = 0; b2 < paramArrayOfInt.length; b2++)
      arrayList.add(getTCKindName(paramArrayOfInt[b2])); 
    String str = getTCKindName(i);
    throw this.wrapper.extractWrongTypeList(arrayList, str);
  }
  
  public short extract_short() {
    checkExtractBadOperation(2);
    return (short)(int)this.value;
  }
  
  public void insert_long(int paramInt) {
    int i = realType().kind().value();
    if (i != 3 && i != 17)
      this.typeCode = this.orb.get_primitive_tc(3); 
    this.value = paramInt;
    this.isInitialized = true;
  }
  
  public int extract_long() {
    checkExtractBadOperationList(new int[] { 3, 17 });
    return (int)this.value;
  }
  
  public void insert_ushort(short paramShort) {
    this.typeCode = this.orb.get_primitive_tc(4);
    this.value = paramShort;
    this.isInitialized = true;
  }
  
  public short extract_ushort() {
    checkExtractBadOperation(4);
    return (short)(int)this.value;
  }
  
  public void insert_ulong(int paramInt) {
    this.typeCode = this.orb.get_primitive_tc(5);
    this.value = paramInt;
    this.isInitialized = true;
  }
  
  public int extract_ulong() {
    checkExtractBadOperation(5);
    return (int)this.value;
  }
  
  public void insert_float(float paramFloat) {
    this.typeCode = this.orb.get_primitive_tc(6);
    this.value = Float.floatToIntBits(paramFloat);
    this.isInitialized = true;
  }
  
  public float extract_float() {
    checkExtractBadOperation(6);
    return Float.intBitsToFloat((int)this.value);
  }
  
  public void insert_double(double paramDouble) {
    this.typeCode = this.orb.get_primitive_tc(7);
    this.value = Double.doubleToLongBits(paramDouble);
    this.isInitialized = true;
  }
  
  public double extract_double() {
    checkExtractBadOperation(7);
    return Double.longBitsToDouble(this.value);
  }
  
  public void insert_longlong(long paramLong) {
    this.typeCode = this.orb.get_primitive_tc(23);
    this.value = paramLong;
    this.isInitialized = true;
  }
  
  public long extract_longlong() {
    checkExtractBadOperation(23);
    return this.value;
  }
  
  public void insert_ulonglong(long paramLong) {
    this.typeCode = this.orb.get_primitive_tc(24);
    this.value = paramLong;
    this.isInitialized = true;
  }
  
  public long extract_ulonglong() {
    checkExtractBadOperation(24);
    return this.value;
  }
  
  public void insert_boolean(boolean paramBoolean) {
    this.typeCode = this.orb.get_primitive_tc(8);
    this.value = paramBoolean ? 1L : 0L;
    this.isInitialized = true;
  }
  
  public boolean extract_boolean() {
    checkExtractBadOperation(8);
    return !(this.value == 0L);
  }
  
  public void insert_char(char paramChar) {
    this.typeCode = this.orb.get_primitive_tc(9);
    this.value = paramChar;
    this.isInitialized = true;
  }
  
  public char extract_char() {
    checkExtractBadOperation(9);
    return (char)(int)this.value;
  }
  
  public void insert_wchar(char paramChar) {
    this.typeCode = this.orb.get_primitive_tc(26);
    this.value = paramChar;
    this.isInitialized = true;
  }
  
  public char extract_wchar() {
    checkExtractBadOperation(26);
    return (char)(int)this.value;
  }
  
  public void insert_octet(byte paramByte) {
    this.typeCode = this.orb.get_primitive_tc(10);
    this.value = paramByte;
    this.isInitialized = true;
  }
  
  public byte extract_octet() {
    checkExtractBadOperation(10);
    return (byte)(int)this.value;
  }
  
  public void insert_string(String paramString) {
    if (this.typeCode.kind() == TCKind.tk_string) {
      int i = 0;
      try {
        i = this.typeCode.length();
      } catch (BadKind badKind) {
        throw this.wrapper.badkindCannotOccur();
      } 
      if (i != 0 && paramString != null && paramString.length() > i)
        throw this.wrapper.badStringBounds(new Integer(paramString.length()), new Integer(i)); 
    } else {
      this.typeCode = this.orb.get_primitive_tc(18);
    } 
    this.object = paramString;
    this.isInitialized = true;
  }
  
  public String extract_string() {
    checkExtractBadOperation(18);
    return (String)this.object;
  }
  
  public void insert_wstring(String paramString) {
    if (this.typeCode.kind() == TCKind.tk_wstring) {
      int i = 0;
      try {
        i = this.typeCode.length();
      } catch (BadKind badKind) {
        throw this.wrapper.badkindCannotOccur();
      } 
      if (i != 0 && paramString != null && paramString.length() > i)
        throw this.wrapper.badStringBounds(new Integer(paramString.length()), new Integer(i)); 
    } else {
      this.typeCode = this.orb.get_primitive_tc(27);
    } 
    this.object = paramString;
    this.isInitialized = true;
  }
  
  public String extract_wstring() {
    checkExtractBadOperation(27);
    return (String)this.object;
  }
  
  public void insert_any(Any paramAny) {
    this.typeCode = this.orb.get_primitive_tc(11);
    this.object = paramAny;
    this.stream = null;
    this.isInitialized = true;
  }
  
  public Any extract_any() {
    checkExtractBadOperation(11);
    return (Any)this.object;
  }
  
  public void insert_Object(Object paramObject) {
    if (paramObject == null) {
      this.typeCode = this.orb.get_primitive_tc(14);
    } else if (StubAdapter.isStub(paramObject)) {
      String[] arrayOfString = StubAdapter.getTypeIds(paramObject);
      this.typeCode = new TypeCodeImpl(this.orb, 14, arrayOfString[0], "");
    } else {
      throw this.wrapper.badInsertobjParam(CompletionStatus.COMPLETED_MAYBE, paramObject.getClass().getName());
    } 
    this.object = paramObject;
    this.isInitialized = true;
  }
  
  public void insert_Object(Object paramObject, TypeCode paramTypeCode) {
    try {
      if (paramTypeCode.id().equals("IDL:omg.org/CORBA/Object:1.0") || paramObject._is_a(paramTypeCode.id())) {
        this.typeCode = TypeCodeImpl.convertToNative(this.orb, paramTypeCode);
        this.object = paramObject;
      } else {
        throw this.wrapper.insertObjectIncompatible();
      } 
    } catch (Exception exception) {
      throw this.wrapper.insertObjectFailed(exception);
    } 
    this.isInitialized = true;
  }
  
  public Object extract_Object() {
    if (!this.isInitialized)
      throw this.wrapper.extractNotInitialized(); 
    Object object1 = null;
    try {
      object1 = (Object)this.object;
      if (this.typeCode.id().equals("IDL:omg.org/CORBA/Object:1.0") || object1._is_a(this.typeCode.id()))
        return object1; 
      throw this.wrapper.extractObjectIncompatible();
    } catch (Exception exception) {
      throw this.wrapper.extractObjectFailed(exception);
    } 
  }
  
  public void insert_TypeCode(TypeCode paramTypeCode) {
    this.typeCode = this.orb.get_primitive_tc(12);
    this.object = paramTypeCode;
    this.isInitialized = true;
  }
  
  public TypeCode extract_TypeCode() {
    checkExtractBadOperation(12);
    return (TypeCode)this.object;
  }
  
  @Deprecated
  public void insert_Principal(Principal paramPrincipal) {
    this.typeCode = this.orb.get_primitive_tc(13);
    this.object = paramPrincipal;
    this.isInitialized = true;
  }
  
  @Deprecated
  public Principal extract_Principal() {
    checkExtractBadOperation(13);
    return (Principal)this.object;
  }
  
  public Serializable extract_Value() {
    checkExtractBadOperationList(new int[] { 29, 30, 32 });
    return (Serializable)this.object;
  }
  
  public void insert_Value(Serializable paramSerializable) {
    TypeCode typeCode1;
    this.object = paramSerializable;
    if (paramSerializable == null) {
      typeCode1 = this.orb.get_primitive_tc(TCKind.tk_value);
    } else {
      typeCode1 = createTypeCodeForClass(paramSerializable.getClass(), (ORB)ORB.init());
    } 
    this.typeCode = TypeCodeImpl.convertToNative(this.orb, typeCode1);
    this.isInitialized = true;
  }
  
  public void insert_Value(Serializable paramSerializable, TypeCode paramTypeCode) {
    this.object = paramSerializable;
    this.typeCode = TypeCodeImpl.convertToNative(this.orb, paramTypeCode);
    this.isInitialized = true;
  }
  
  public void insert_fixed(BigDecimal paramBigDecimal) {
    this.typeCode = TypeCodeImpl.convertToNative(this.orb, this.orb.create_fixed_tc(TypeCodeImpl.digits(paramBigDecimal), TypeCodeImpl.scale(paramBigDecimal)));
    this.object = paramBigDecimal;
    this.isInitialized = true;
  }
  
  public void insert_fixed(BigDecimal paramBigDecimal, TypeCode paramTypeCode) {
    try {
      if (TypeCodeImpl.digits(paramBigDecimal) > paramTypeCode.fixed_digits() || TypeCodeImpl.scale(paramBigDecimal) > paramTypeCode.fixed_scale())
        throw this.wrapper.fixedNotMatch(); 
    } catch (BadKind badKind) {
      throw this.wrapper.fixedBadTypecode(badKind);
    } 
    this.typeCode = TypeCodeImpl.convertToNative(this.orb, paramTypeCode);
    this.object = paramBigDecimal;
    this.isInitialized = true;
  }
  
  public BigDecimal extract_fixed() {
    checkExtractBadOperation(28);
    return (BigDecimal)this.object;
  }
  
  public TypeCode createTypeCodeForClass(Class paramClass, ORB paramORB) {
    TypeCodeImpl typeCodeImpl = paramORB.getTypeCodeForClass(paramClass);
    if (typeCodeImpl != null)
      return typeCodeImpl; 
    RepositoryIdStrings repositoryIdStrings = RepositoryIdFactory.getRepIdStringsFactory();
    if (paramClass.isArray()) {
      TypeCode typeCode1;
      Class clazz = paramClass.getComponentType();
      if (clazz.isPrimitive()) {
        typeCode1 = getPrimitiveTypeCodeForClass(clazz, paramORB);
      } else {
        typeCode1 = createTypeCodeForClass(clazz, paramORB);
      } 
      TypeCode typeCode2 = paramORB.create_sequence_tc(0, typeCode1);
      String str = repositoryIdStrings.createForJavaType(paramClass);
      return paramORB.create_value_box_tc(str, "Sequence", typeCode2);
    } 
    if (paramClass == String.class) {
      TypeCode typeCode1 = paramORB.create_string_tc(0);
      String str = repositoryIdStrings.createForJavaType(paramClass);
      return paramORB.create_value_box_tc(str, "StringValue", typeCode1);
    } 
    typeCodeImpl = (TypeCodeImpl)ValueUtility.createTypeCodeForClass(paramORB, paramClass, ORBUtility.createValueHandler());
    typeCodeImpl.setCaching(true);
    paramORB.setTypeCodeForClass(paramClass, typeCodeImpl);
    return typeCodeImpl;
  }
  
  private TypeCode getPrimitiveTypeCodeForClass(Class paramClass, ORB paramORB) { return (paramClass == int.class) ? paramORB.get_primitive_tc(TCKind.tk_long) : ((paramClass == byte.class) ? paramORB.get_primitive_tc(TCKind.tk_octet) : ((paramClass == long.class) ? paramORB.get_primitive_tc(TCKind.tk_longlong) : ((paramClass == float.class) ? paramORB.get_primitive_tc(TCKind.tk_float) : ((paramClass == double.class) ? paramORB.get_primitive_tc(TCKind.tk_double) : ((paramClass == short.class) ? paramORB.get_primitive_tc(TCKind.tk_short) : ((paramClass == char.class) ? ((ORBVersionFactory.getFOREIGN().compareTo(paramORB.getORBVersion()) == 0 || ORBVersionFactory.getNEWER().compareTo(paramORB.getORBVersion()) <= 0) ? paramORB.get_primitive_tc(TCKind.tk_wchar) : paramORB.get_primitive_tc(TCKind.tk_char)) : ((paramClass == boolean.class) ? paramORB.get_primitive_tc(TCKind.tk_boolean) : paramORB.get_primitive_tc(TCKind.tk_any)))))))); }
  
  public Any extractAny(TypeCode paramTypeCode, ORB paramORB) {
    Any any = paramORB.create_any();
    OutputStream outputStream = any.create_output_stream();
    TypeCodeImpl.convertToNative(paramORB, paramTypeCode).copy(this.stream, outputStream);
    any.read_value(outputStream.create_input_stream(), paramTypeCode);
    return any;
  }
  
  public static Any extractAnyFromStream(TypeCode paramTypeCode, InputStream paramInputStream, ORB paramORB) {
    Any any = paramORB.create_any();
    OutputStream outputStream = any.create_output_stream();
    TypeCodeImpl.convertToNative(paramORB, paramTypeCode).copy(paramInputStream, outputStream);
    any.read_value(outputStream.create_input_stream(), paramTypeCode);
    return any;
  }
  
  public boolean isInitialized() { return this.isInitialized; }
  
  private static final class AnyInputStream extends EncapsInputStream {
    public AnyInputStream(EncapsInputStream param1EncapsInputStream) { super(param1EncapsInputStream); }
  }
  
  private static final class AnyOutputStream extends EncapsOutputStream {
    public AnyOutputStream(ORB param1ORB) { super(param1ORB); }
    
    public InputStream create_input_stream() {
      final InputStream is = super.create_input_stream();
      return (AnyImpl.AnyInputStream)AccessController.doPrivileged(new PrivilegedAction<AnyImpl.AnyInputStream>() {
            public AnyImpl.AnyInputStream run() { return new AnyImpl.AnyInputStream((EncapsInputStream)is); }
          });
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\impl\corba\AnyImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */