package com.sun.corba.se.impl.encoding;

import com.sun.corba.se.impl.corba.CORBAObjectImpl;
import com.sun.corba.se.impl.corba.PrincipalImpl;
import com.sun.corba.se.impl.corba.TypeCodeImpl;
import com.sun.corba.se.impl.logging.OMGSystemException;
import com.sun.corba.se.impl.logging.ORBUtilSystemException;
import com.sun.corba.se.impl.orbutil.CacheTable;
import com.sun.corba.se.impl.orbutil.ORBUtility;
import com.sun.corba.se.impl.orbutil.RepositoryIdFactory;
import com.sun.corba.se.impl.orbutil.RepositoryIdInterface;
import com.sun.corba.se.impl.orbutil.RepositoryIdStrings;
import com.sun.corba.se.impl.orbutil.RepositoryIdUtility;
import com.sun.corba.se.impl.util.RepositoryId;
import com.sun.corba.se.impl.util.Utility;
import com.sun.corba.se.pept.protocol.MessageMediator;
import com.sun.corba.se.pept.transport.ByteBufferPool;
import com.sun.corba.se.spi.ior.IOR;
import com.sun.corba.se.spi.ior.IORFactories;
import com.sun.corba.se.spi.ior.iiop.GIOPVersion;
import com.sun.corba.se.spi.orb.ORB;
import com.sun.corba.se.spi.orb.ORBVersionFactory;
import com.sun.corba.se.spi.presentation.rmi.PresentationDefaults;
import com.sun.corba.se.spi.presentation.rmi.PresentationManager;
import com.sun.corba.se.spi.presentation.rmi.StubAdapter;
import com.sun.corba.se.spi.protocol.CorbaClientDelegate;
import com.sun.org.omg.CORBA.portable.ValueHelper;
import com.sun.org.omg.SendingContext.CodeBase;
import java.io.Serializable;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.math.BigDecimal;
import java.net.MalformedURLException;
import java.nio.ByteBuffer;
import java.security.AccessController;
import java.security.PrivilegedActionException;
import java.security.PrivilegedExceptionAction;
import javax.rmi.CORBA.Tie;
import javax.rmi.CORBA.ValueHandler;
import org.omg.CORBA.Any;
import org.omg.CORBA.AnySeqHolder;
import org.omg.CORBA.BooleanSeqHolder;
import org.omg.CORBA.CharSeqHolder;
import org.omg.CORBA.CompletionStatus;
import org.omg.CORBA.Context;
import org.omg.CORBA.CustomMarshal;
import org.omg.CORBA.DoubleSeqHolder;
import org.omg.CORBA.FloatSeqHolder;
import org.omg.CORBA.LongLongSeqHolder;
import org.omg.CORBA.LongSeqHolder;
import org.omg.CORBA.MARSHAL;
import org.omg.CORBA.ORB;
import org.omg.CORBA.Object;
import org.omg.CORBA.OctetSeqHolder;
import org.omg.CORBA.Principal;
import org.omg.CORBA.ShortSeqHolder;
import org.omg.CORBA.SystemException;
import org.omg.CORBA.TypeCode;
import org.omg.CORBA.TypeCodePackage.BadKind;
import org.omg.CORBA.ULongLongSeqHolder;
import org.omg.CORBA.ULongSeqHolder;
import org.omg.CORBA.UShortSeqHolder;
import org.omg.CORBA.WCharSeqHolder;
import org.omg.CORBA.portable.BoxedValueHelper;
import org.omg.CORBA.portable.CustomValue;
import org.omg.CORBA.portable.IndirectionException;
import org.omg.CORBA.portable.StreamableValue;
import org.omg.CORBA.portable.ValueFactory;

public class CDRInputStream_1_0 extends CDRInputStreamBase implements RestorableInputStream {
  private static final String kReadMethod = "read";
  
  private static final int maxBlockLength = 2147483392;
  
  protected BufferManagerRead bufferManagerRead;
  
  protected ByteBufferWithInfo bbwi;
  
  private boolean debug = false;
  
  protected boolean littleEndian;
  
  protected ORB orb;
  
  protected ORBUtilSystemException wrapper;
  
  protected OMGSystemException omgWrapper;
  
  protected ValueHandler valueHandler = null;
  
  private CacheTable valueCache = null;
  
  private CacheTable repositoryIdCache = null;
  
  private CacheTable codebaseCache = null;
  
  protected int blockLength = 2147483392;
  
  protected int end_flag = 0;
  
  private int chunkedValueNestingLevel = 0;
  
  protected int valueIndirection = 0;
  
  protected int stringIndirection = 0;
  
  protected boolean isChunked = false;
  
  private RepositoryIdUtility repIdUtil;
  
  private RepositoryIdStrings repIdStrs;
  
  private CodeSetConversion.BTCConverter charConverter;
  
  private CodeSetConversion.BTCConverter wcharConverter;
  
  private boolean specialNoOptionalDataState = false;
  
  private static final String _id = "IDL:omg.org/CORBA/DataInputStream:1.0";
  
  private static final String[] _ids = { "IDL:omg.org/CORBA/DataInputStream:1.0" };
  
  protected MarkAndResetHandler markAndResetHandler = null;
  
  public CDRInputStreamBase dup() {
    CDRInputStreamBase cDRInputStreamBase = null;
    try {
      cDRInputStreamBase = (CDRInputStreamBase)getClass().newInstance();
    } catch (Exception exception) {
      throw this.wrapper.couldNotDuplicateCdrInputStream(exception);
    } 
    cDRInputStreamBase.init(this.orb, this.bbwi.byteBuffer, this.bbwi.buflen, this.littleEndian, this.bufferManagerRead);
    ((CDRInputStream_1_0)cDRInputStreamBase).bbwi.position(this.bbwi.position());
    ((CDRInputStream_1_0)cDRInputStreamBase).bbwi.byteBuffer.limit(this.bbwi.buflen);
    return cDRInputStreamBase;
  }
  
  public void init(ORB paramORB, ByteBuffer paramByteBuffer, int paramInt, boolean paramBoolean, BufferManagerRead paramBufferManagerRead) {
    this.orb = (ORB)paramORB;
    this.wrapper = ORBUtilSystemException.get((ORB)paramORB, "rpc.encoding");
    this.omgWrapper = OMGSystemException.get((ORB)paramORB, "rpc.encoding");
    this.littleEndian = paramBoolean;
    this.bufferManagerRead = paramBufferManagerRead;
    this.bbwi = new ByteBufferWithInfo(paramORB, paramByteBuffer, 0);
    this.bbwi.buflen = paramInt;
    this.bbwi.byteBuffer.limit(this.bbwi.buflen);
    this.markAndResetHandler = this.bufferManagerRead.getMarkAndResetHandler();
    this.debug = ((ORB)paramORB).transportDebugFlag;
  }
  
  void performORBVersionSpecificInit() { createRepositoryIdHandlers(); }
  
  private final void createRepositoryIdHandlers() {
    this.repIdUtil = RepositoryIdFactory.getRepIdUtility();
    this.repIdStrs = RepositoryIdFactory.getRepIdStringsFactory();
  }
  
  public GIOPVersion getGIOPVersion() { return GIOPVersion.V1_0; }
  
  void setHeaderPadding(boolean paramBoolean) { throw this.wrapper.giopVersionError(); }
  
  protected final int computeAlignment(int paramInt1, int paramInt2) {
    if (paramInt2 > 1) {
      int i = paramInt1 & paramInt2 - 1;
      if (i != 0)
        return paramInt2 - i; 
    } 
    return 0;
  }
  
  public int getSize() { return this.bbwi.position(); }
  
  protected void checkBlockLength(int paramInt1, int paramInt2) {
    if (!this.isChunked)
      return; 
    if (this.specialNoOptionalDataState)
      throw this.omgWrapper.rmiiiopOptionalDataIncompatible1(); 
    boolean bool = false;
    if (this.blockLength == get_offset()) {
      this.blockLength = 2147483392;
      start_block();
      if (this.blockLength == 2147483392)
        bool = true; 
    } else if (this.blockLength < get_offset()) {
      throw this.wrapper.chunkOverflow();
    } 
    int i = computeAlignment(this.bbwi.position(), paramInt1) + paramInt2;
    if (this.blockLength != 2147483392 && this.blockLength < get_offset() + i)
      throw this.omgWrapper.rmiiiopOptionalDataIncompatible2(); 
    if (bool) {
      int j = read_long();
      this.bbwi.position(this.bbwi.position() - 4);
      if (j < 0)
        throw this.omgWrapper.rmiiiopOptionalDataIncompatible3(); 
    } 
  }
  
  protected void alignAndCheck(int paramInt1, int paramInt2) {
    checkBlockLength(paramInt1, paramInt2);
    int i = computeAlignment(this.bbwi.position(), paramInt1);
    this.bbwi.position(this.bbwi.position() + i);
    if (this.bbwi.position() + paramInt2 > this.bbwi.buflen)
      grow(paramInt1, paramInt2); 
  }
  
  protected void grow(int paramInt1, int paramInt2) {
    this.bbwi.needed = paramInt2;
    this.bbwi = this.bufferManagerRead.underflow(this.bbwi);
  }
  
  public final void consumeEndian() { this.littleEndian = read_boolean(); }
  
  public final double read_longdouble() { throw this.wrapper.longDoubleNotImplemented(CompletionStatus.COMPLETED_MAYBE); }
  
  public final boolean read_boolean() { return (read_octet() != 0); }
  
  public final char read_char() {
    alignAndCheck(1, 1);
    return getConvertedChars(1, getCharConverter())[0];
  }
  
  public char read_wchar() {
    byte b2;
    byte b1;
    if (ORBUtility.isForeignORB(this.orb))
      throw this.wrapper.wcharDataInGiop10(CompletionStatus.COMPLETED_MAYBE); 
    alignAndCheck(2, 2);
    if (this.littleEndian) {
      b2 = this.bbwi.byteBuffer.get(this.bbwi.position()) & 0xFF;
      this.bbwi.position(this.bbwi.position() + 1);
      b1 = this.bbwi.byteBuffer.get(this.bbwi.position()) & 0xFF;
      this.bbwi.position(this.bbwi.position() + 1);
    } else {
      b1 = this.bbwi.byteBuffer.get(this.bbwi.position()) & 0xFF;
      this.bbwi.position(this.bbwi.position() + 1);
      b2 = this.bbwi.byteBuffer.get(this.bbwi.position()) & 0xFF;
      this.bbwi.position(this.bbwi.position() + 1);
    } 
    return (char)((b1 << 8) + (b2 << 0));
  }
  
  public final byte read_octet() {
    alignAndCheck(1, 1);
    byte b = this.bbwi.byteBuffer.get(this.bbwi.position());
    this.bbwi.position(this.bbwi.position() + 1);
    return b;
  }
  
  public final short read_short() {
    byte b2;
    byte b1;
    alignAndCheck(2, 2);
    if (this.littleEndian) {
      b2 = this.bbwi.byteBuffer.get(this.bbwi.position()) << 0 & 0xFF;
      this.bbwi.position(this.bbwi.position() + 1);
      b1 = this.bbwi.byteBuffer.get(this.bbwi.position()) << 8 & 0xFF00;
      this.bbwi.position(this.bbwi.position() + 1);
    } else {
      b1 = this.bbwi.byteBuffer.get(this.bbwi.position()) << 8 & 0xFF00;
      this.bbwi.position(this.bbwi.position() + 1);
      b2 = this.bbwi.byteBuffer.get(this.bbwi.position()) << 0 & 0xFF;
      this.bbwi.position(this.bbwi.position() + 1);
    } 
    return (short)(b1 | b2);
  }
  
  public final short read_ushort() { return read_short(); }
  
  public final int read_long() {
    byte b4;
    byte b3;
    byte b2;
    byte b1;
    alignAndCheck(4, 4);
    int i = this.bbwi.position();
    if (this.littleEndian) {
      b4 = this.bbwi.byteBuffer.get(i++) & 0xFF;
      b3 = this.bbwi.byteBuffer.get(i++) & 0xFF;
      b2 = this.bbwi.byteBuffer.get(i++) & 0xFF;
      b1 = this.bbwi.byteBuffer.get(i++) & 0xFF;
    } else {
      b1 = this.bbwi.byteBuffer.get(i++) & 0xFF;
      b2 = this.bbwi.byteBuffer.get(i++) & 0xFF;
      b3 = this.bbwi.byteBuffer.get(i++) & 0xFF;
      b4 = this.bbwi.byteBuffer.get(i++) & 0xFF;
    } 
    this.bbwi.position(i);
    return b1 << 24 | b2 << 16 | b3 << 8 | b4;
  }
  
  public final int read_ulong() { return read_long(); }
  
  public final long read_longlong() {
    long l2;
    long l1;
    alignAndCheck(8, 8);
    if (this.littleEndian) {
      l2 = read_long() & 0xFFFFFFFFL;
      l1 = read_long() << 32;
    } else {
      l1 = read_long() << 32;
      l2 = read_long() & 0xFFFFFFFFL;
    } 
    return l1 | l2;
  }
  
  public final long read_ulonglong() { return read_longlong(); }
  
  public final float read_float() { return Float.intBitsToFloat(read_long()); }
  
  public final double read_double() { return Double.longBitsToDouble(read_longlong()); }
  
  protected final void checkForNegativeLength(int paramInt) {
    if (paramInt < 0)
      throw this.wrapper.negativeStringLength(CompletionStatus.COMPLETED_MAYBE, new Integer(paramInt)); 
  }
  
  protected final String readStringOrIndirection(boolean paramBoolean) {
    int i = read_long();
    if (paramBoolean) {
      if (i == -1)
        return null; 
      this.stringIndirection = get_offset() - 4;
    } 
    checkForNegativeLength(i);
    return internalReadString(i);
  }
  
  private final String internalReadString(int paramInt) {
    if (paramInt == 0)
      return new String(""); 
    char[] arrayOfChar = getConvertedChars(paramInt - 1, getCharConverter());
    read_octet();
    return new String(arrayOfChar, 0, getCharConverter().getNumChars());
  }
  
  public final String read_string() { return readStringOrIndirection(false); }
  
  public String read_wstring() {
    if (ORBUtility.isForeignORB(this.orb))
      throw this.wrapper.wcharDataInGiop10(CompletionStatus.COMPLETED_MAYBE); 
    int i = read_long();
    if (i == 0)
      return new String(""); 
    checkForNegativeLength(i);
    char[] arrayOfChar = new char[--i];
    for (byte b = 0; b < i; b++)
      arrayOfChar[b] = read_wchar(); 
    read_wchar();
    return new String(arrayOfChar);
  }
  
  public final void read_octet_array(byte[] paramArrayOfByte, int paramInt1, int paramInt2) {
    if (paramArrayOfByte == null)
      throw this.wrapper.nullParam(); 
    if (paramInt2 == 0)
      return; 
    alignAndCheck(1, 1);
    int i;
    for (i = paramInt1; i < paramInt2 + paramInt1; i += k) {
      int j = this.bbwi.buflen - this.bbwi.position();
      if (j <= 0) {
        grow(1, 1);
        j = this.bbwi.buflen - this.bbwi.position();
      } 
      int m = paramInt2 + paramInt1 - i;
      int k = (m < j) ? m : j;
      for (int n = 0; n < k; n++)
        paramArrayOfByte[i + n] = this.bbwi.byteBuffer.get(this.bbwi.position() + n); 
      this.bbwi.position(this.bbwi.position() + k);
    } 
  }
  
  public Principal read_Principal() {
    int i = read_long();
    byte[] arrayOfByte = new byte[i];
    read_octet_array(arrayOfByte, 0, i);
    PrincipalImpl principalImpl = new PrincipalImpl();
    principalImpl.name(arrayOfByte);
    return principalImpl;
  }
  
  public TypeCode read_TypeCode() {
    TypeCodeImpl typeCodeImpl = new TypeCodeImpl(this.orb);
    typeCodeImpl.read_value(this.parent);
    return typeCodeImpl;
  }
  
  public Any read_any() {
    Any any = this.orb.create_any();
    TypeCodeImpl typeCodeImpl = new TypeCodeImpl(this.orb);
    try {
      typeCodeImpl.read_value(this.parent);
    } catch (MARSHAL mARSHAL) {
      if (typeCodeImpl.kind().value() != 29)
        throw mARSHAL; 
      dprintThrowable(mARSHAL);
    } 
    any.read_value(this.parent, typeCodeImpl);
    return any;
  }
  
  public Object read_Object() { return read_Object(null); }
  
  public Object read_Object(Class paramClass) {
    IOR iOR = IORFactories.makeIOR(this.parent);
    if (iOR.isNil())
      return null; 
    PresentationManager.StubFactoryFactory stubFactoryFactory = ORB.getStubFactoryFactory();
    String str = iOR.getProfile().getCodebase();
    PresentationManager.StubFactory stubFactory = null;
    if (paramClass == null) {
      RepositoryId repositoryId = RepositoryId.cache.getId(iOR.getTypeId());
      String str1 = repositoryId.getClassName();
      this.orb.validateIORClass(str1);
      boolean bool = repositoryId.isIDLType();
      if (str1 == null || str1.equals("")) {
        stubFactory = null;
      } else {
        try {
          stubFactory = stubFactoryFactory.createStubFactory(str1, bool, str, (Class)null, (ClassLoader)null);
        } catch (Exception exception) {
          stubFactory = null;
        } 
      } 
    } else if (StubAdapter.isStubClass(paramClass)) {
      stubFactory = PresentationDefaults.makeStaticStubFactory(paramClass);
    } else {
      boolean bool = org.omg.CORBA.portable.IDLEntity.class.isAssignableFrom(paramClass);
      stubFactory = stubFactoryFactory.createStubFactory(paramClass.getName(), bool, str, paramClass, paramClass.getClassLoader());
    } 
    return internalIORToObject(iOR, stubFactory, this.orb);
  }
  
  public static Object internalIORToObject(IOR paramIOR, PresentationManager.StubFactory paramStubFactory, ORB paramORB) {
    ORBUtilSystemException oRBUtilSystemException = ORBUtilSystemException.get(paramORB, "rpc.encoding");
    Object object = paramIOR.getProfile().getServant();
    if (object != null) {
      if (object instanceof Tie) {
        String str = paramIOR.getProfile().getCodebase();
        Object object2 = (Object)Utility.loadStub((Tie)object, paramStubFactory, str, false);
        if (object2 != null)
          return object2; 
        throw oRBUtilSystemException.readObjectException();
      } 
      if (object instanceof Object) {
        if (!(object instanceof org.omg.CORBA.portable.InvokeHandler))
          return (Object)object; 
      } else {
        throw oRBUtilSystemException.badServantReadObject();
      } 
    } 
    CorbaClientDelegate corbaClientDelegate = ORBUtility.makeClientDelegate(paramIOR);
    Object object1 = null;
    try {
      object1 = paramStubFactory.makeStub();
    } catch (Throwable throwable) {
      oRBUtilSystemException.stubCreateError(throwable);
      if (throwable instanceof ThreadDeath)
        throw (ThreadDeath)throwable; 
      object1 = new CORBAObjectImpl();
    } 
    StubAdapter.setDelegate(object1, corbaClientDelegate);
    return object1;
  }
  
  public Object read_abstract_interface() { return read_abstract_interface(null); }
  
  public Object read_abstract_interface(Class paramClass) {
    boolean bool = read_boolean();
    return bool ? read_Object(paramClass) : read_value();
  }
  
  public Serializable read_value() { return read_value((Class)null); }
  
  private Serializable handleIndirection() {
    int i = read_long() + get_offset() - 4;
    if (this.valueCache != null && this.valueCache.containsVal(i))
      return (Serializable)this.valueCache.getKey(i); 
    throw new IndirectionException(i);
  }
  
  private String readRepositoryIds(int paramInt, Class paramClass, String paramString) { return readRepositoryIds(paramInt, paramClass, paramString, null); }
  
  private String readRepositoryIds(int paramInt, Class paramClass, String paramString, BoxedValueHelper paramBoxedValueHelper) {
    switch (this.repIdUtil.getTypeInfo(paramInt)) {
      case 0:
        if (paramClass == null) {
          if (paramString != null)
            return paramString; 
          if (paramBoxedValueHelper != null)
            return paramBoxedValueHelper.get_id(); 
          throw this.wrapper.expectedTypeNullAndNoRepId(CompletionStatus.COMPLETED_MAYBE);
        } 
        return this.repIdStrs.createForAnyType(paramClass);
      case 2:
        return read_repositoryId();
      case 6:
        return read_repositoryIds();
    } 
    throw this.wrapper.badValueTag(CompletionStatus.COMPLETED_MAYBE, Integer.toHexString(paramInt));
  }
  
  public Serializable read_value(Class paramClass) {
    int i = readValueTag();
    if (i == 0)
      return null; 
    if (i == -1)
      return handleIndirection(); 
    int j = get_offset() - 4;
    boolean bool = this.isChunked;
    this.isChunked = this.repIdUtil.isChunkedEncoding(i);
    Serializable serializable = null;
    String str1 = null;
    if (this.repIdUtil.isCodeBasePresent(i))
      str1 = read_codebase_URL(); 
    String str2 = readRepositoryIds(i, paramClass, null);
    start_block();
    this.end_flag--;
    if (this.isChunked)
      this.chunkedValueNestingLevel--; 
    if (str2.equals(this.repIdStrs.getWStringValueRepId())) {
      serializable = read_wstring();
    } else if (str2.equals(this.repIdStrs.getClassDescValueRepId())) {
      Class clazz = readClass();
    } else {
      Class clazz = paramClass;
      if (paramClass == null || !str2.equals(this.repIdStrs.createForAnyType(paramClass)))
        clazz = getClassFromString(str2, str1, paramClass); 
      if (clazz == null)
        throw this.wrapper.couldNotFindClass(CompletionStatus.COMPLETED_MAYBE, new ClassNotFoundException()); 
      if (clazz != null && org.omg.CORBA.portable.IDLEntity.class.isAssignableFrom(clazz)) {
        Object object = readIDLValue(j, str2, clazz, str1);
      } else {
        try {
          if (this.valueHandler == null)
            this.valueHandler = ORBUtility.createValueHandler(); 
          serializable = this.valueHandler.readValue(this.parent, j, clazz, str2, getCodeBase());
        } catch (SystemException systemException) {
          throw systemException;
        } catch (Exception exception) {
          throw this.wrapper.valuehandlerReadException(CompletionStatus.COMPLETED_MAYBE, exception);
        } catch (Error error) {
          throw this.wrapper.valuehandlerReadError(CompletionStatus.COMPLETED_MAYBE, error);
        } 
      } 
    } 
    handleEndOfValue();
    readEndTag();
    if (this.valueCache == null)
      this.valueCache = new CacheTable(this.orb, false); 
    this.valueCache.put(serializable, j);
    this.isChunked = bool;
    start_block();
    return (Serializable)serializable;
  }
  
  public Serializable read_value(BoxedValueHelper paramBoxedValueHelper) {
    int i = readValueTag();
    if (i == 0)
      return null; 
    if (i == -1) {
      int k = read_long() + get_offset() - 4;
      if (this.valueCache != null && this.valueCache.containsVal(k))
        return (Serializable)this.valueCache.getKey(k); 
      throw new IndirectionException(k);
    } 
    int j = get_offset() - 4;
    boolean bool = this.isChunked;
    this.isChunked = this.repIdUtil.isChunkedEncoding(i);
    Object object = null;
    String str1 = null;
    if (this.repIdUtil.isCodeBasePresent(i))
      str1 = read_codebase_URL(); 
    String str2 = readRepositoryIds(i, null, null, paramBoxedValueHelper);
    if (!str2.equals(paramBoxedValueHelper.get_id()))
      paramBoxedValueHelper = Utility.getHelper(null, str1, str2); 
    start_block();
    this.end_flag--;
    if (this.isChunked)
      this.chunkedValueNestingLevel--; 
    if (paramBoxedValueHelper instanceof ValueHelper) {
      object = readIDLValueWithHelper((ValueHelper)paramBoxedValueHelper, j);
    } else {
      this.valueIndirection = j;
      object = paramBoxedValueHelper.read_value(this.parent);
    } 
    handleEndOfValue();
    readEndTag();
    if (this.valueCache == null)
      this.valueCache = new CacheTable(this.orb, false); 
    this.valueCache.put(object, j);
    this.isChunked = bool;
    start_block();
    return (Serializable)object;
  }
  
  private boolean isCustomType(ValueHelper paramValueHelper) {
    try {
      TypeCode typeCode = paramValueHelper.get_type();
      int i = typeCode.kind().value();
      if (i == 29)
        return (typeCode.type_modifier() == 1); 
    } catch (BadKind badKind) {
      throw this.wrapper.badKind(badKind);
    } 
    return false;
  }
  
  public Serializable read_value(Serializable paramSerializable) {
    if (this.valueCache == null)
      this.valueCache = new CacheTable(this.orb, false); 
    this.valueCache.put(paramSerializable, this.valueIndirection);
    if (paramSerializable instanceof StreamableValue) {
      ((StreamableValue)paramSerializable)._read(this.parent);
    } else if (paramSerializable instanceof CustomValue) {
      ((CustomValue)paramSerializable).unmarshal(this.parent);
    } 
    return paramSerializable;
  }
  
  public Serializable read_value(String paramString) {
    int i = readValueTag();
    if (i == 0)
      return null; 
    if (i == -1) {
      int k = read_long() + get_offset() - 4;
      if (this.valueCache != null && this.valueCache.containsVal(k))
        return (Serializable)this.valueCache.getKey(k); 
      throw new IndirectionException(k);
    } 
    int j = get_offset() - 4;
    boolean bool = this.isChunked;
    this.isChunked = this.repIdUtil.isChunkedEncoding(i);
    Serializable serializable = null;
    String str1 = null;
    if (this.repIdUtil.isCodeBasePresent(i))
      str1 = read_codebase_URL(); 
    String str2 = readRepositoryIds(i, null, paramString);
    ValueFactory valueFactory = Utility.getFactory(null, str1, this.orb, str2);
    start_block();
    this.end_flag--;
    if (this.isChunked)
      this.chunkedValueNestingLevel--; 
    this.valueIndirection = j;
    serializable = valueFactory.read_value(this.parent);
    handleEndOfValue();
    readEndTag();
    if (this.valueCache == null)
      this.valueCache = new CacheTable(this.orb, false); 
    this.valueCache.put(serializable, j);
    this.isChunked = bool;
    start_block();
    return (Serializable)serializable;
  }
  
  private Class readClass() {
    String str1 = null;
    String str2 = null;
    if (this.orb == null || ORBVersionFactory.getFOREIGN().equals(this.orb.getORBVersion()) || ORBVersionFactory.getNEWER().compareTo(this.orb.getORBVersion()) <= 0) {
      str1 = (String)read_value(String.class);
      str2 = (String)read_value(String.class);
    } else {
      str2 = (String)read_value(String.class);
      str1 = (String)read_value(String.class);
    } 
    if (this.debug)
      dprint("readClass codebases: " + str1 + " rep Id: " + str2); 
    Class clazz = null;
    RepositoryIdInterface repositoryIdInterface = this.repIdStrs.getFromString(str2);
    try {
      clazz = repositoryIdInterface.getClassFromType(str1);
    } catch (ClassNotFoundException classNotFoundException) {
      throw this.wrapper.cnfeReadClass(CompletionStatus.COMPLETED_MAYBE, classNotFoundException, repositoryIdInterface.getClassName());
    } catch (MalformedURLException malformedURLException) {
      throw this.wrapper.malformedUrl(CompletionStatus.COMPLETED_MAYBE, malformedURLException, repositoryIdInterface.getClassName(), str1);
    } 
    return clazz;
  }
  
  private Object readIDLValueWithHelper(ValueHelper paramValueHelper, int paramInt) {
    Method method;
    try {
      Class[] arrayOfClass = { org.omg.CORBA.portable.InputStream.class, paramValueHelper.get_class() };
      method = paramValueHelper.getClass().getDeclaredMethod("read", arrayOfClass);
    } catch (NoSuchMethodException noSuchMethodException) {
      return paramValueHelper.read_value(this.parent);
    } 
    Object object = null;
    try {
      object = paramValueHelper.get_class().newInstance();
    } catch (InstantiationException instantiationException) {
      throw this.wrapper.couldNotInstantiateHelper(instantiationException, paramValueHelper.get_class());
    } catch (IllegalAccessException illegalAccessException) {
      return paramValueHelper.read_value(this.parent);
    } 
    if (this.valueCache == null)
      this.valueCache = new CacheTable(this.orb, false); 
    this.valueCache.put(object, paramInt);
    if (object instanceof CustomMarshal && isCustomType(paramValueHelper)) {
      ((CustomMarshal)object).unmarshal(this.parent);
      return object;
    } 
    try {
      Object[] arrayOfObject = { this.parent, object };
      method.invoke(paramValueHelper, arrayOfObject);
      return object;
    } catch (IllegalAccessException illegalAccessException) {
      throw this.wrapper.couldNotInvokeHelperReadMethod(illegalAccessException, paramValueHelper.get_class());
    } catch (InvocationTargetException invocationTargetException) {
      throw this.wrapper.couldNotInvokeHelperReadMethod(invocationTargetException, paramValueHelper.get_class());
    } 
  }
  
  private Object readBoxedIDLEntity(Class paramClass, String paramString) {
    Class clazz = null;
    try {
      ClassLoader classLoader = (paramClass == null) ? null : paramClass.getClassLoader();
      clazz = Utility.loadClassForClass(paramClass.getName() + "Helper", paramString, classLoader, paramClass, classLoader);
      final Class helperClass = clazz;
      final Class[] argTypes = { org.omg.CORBA.portable.InputStream.class };
      Method method = null;
      try {
        method = (Method)AccessController.doPrivileged(new PrivilegedExceptionAction() {
              public Object run() { return helperClass.getDeclaredMethod("read", argTypes); }
            });
      } catch (PrivilegedActionException privilegedActionException) {
        throw (NoSuchMethodException)privilegedActionException.getException();
      } 
      Object[] arrayOfObject = { this.parent };
      return method.invoke(null, arrayOfObject);
    } catch (ClassNotFoundException classNotFoundException) {
      throw this.wrapper.couldNotInvokeHelperReadMethod(classNotFoundException, clazz);
    } catch (NoSuchMethodException noSuchMethodException) {
      throw this.wrapper.couldNotInvokeHelperReadMethod(noSuchMethodException, clazz);
    } catch (IllegalAccessException illegalAccessException) {
      throw this.wrapper.couldNotInvokeHelperReadMethod(illegalAccessException, clazz);
    } catch (InvocationTargetException invocationTargetException) {
      throw this.wrapper.couldNotInvokeHelperReadMethod(invocationTargetException, clazz);
    } 
  }
  
  private Object readIDLValue(int paramInt, String paramString1, Class paramClass, String paramString2) {
    ValueFactory valueFactory;
    try {
      valueFactory = Utility.getFactory(paramClass, paramString2, this.orb, paramString1);
    } catch (MARSHAL mARSHAL) {
      if (!StreamableValue.class.isAssignableFrom(paramClass) && !CustomValue.class.isAssignableFrom(paramClass) && org.omg.CORBA.portable.ValueBase.class.isAssignableFrom(paramClass)) {
        BoxedValueHelper boxedValueHelper = Utility.getHelper(paramClass, paramString2, paramString1);
        return (boxedValueHelper instanceof ValueHelper) ? readIDLValueWithHelper((ValueHelper)boxedValueHelper, paramInt) : boxedValueHelper.read_value(this.parent);
      } 
      return readBoxedIDLEntity(paramClass, paramString2);
    } 
    this.valueIndirection = paramInt;
    return valueFactory.read_value(this.parent);
  }
  
  private void readEndTag() {
    if (this.isChunked) {
      int i = read_long();
      if (i >= 0)
        throw this.wrapper.positiveEndTag(CompletionStatus.COMPLETED_MAYBE, new Integer(i), new Integer(get_offset() - 4)); 
      if (this.orb == null || ORBVersionFactory.getFOREIGN().equals(this.orb.getORBVersion()) || ORBVersionFactory.getNEWER().compareTo(this.orb.getORBVersion()) <= 0) {
        if (i < this.chunkedValueNestingLevel)
          throw this.wrapper.unexpectedEnclosingValuetype(CompletionStatus.COMPLETED_MAYBE, new Integer(i), new Integer(this.chunkedValueNestingLevel)); 
        if (i != this.chunkedValueNestingLevel)
          this.bbwi.position(this.bbwi.position() - 4); 
      } else if (i != this.end_flag) {
        this.bbwi.position(this.bbwi.position() - 4);
      } 
      this.chunkedValueNestingLevel++;
    } 
    this.end_flag++;
  }
  
  protected int get_offset() { return this.bbwi.position(); }
  
  private void start_block() {
    if (!this.isChunked)
      return; 
    this.blockLength = 2147483392;
    this.blockLength = read_long();
    if (this.blockLength > 0 && this.blockLength < 2147483392) {
      this.blockLength += get_offset();
    } else {
      this.blockLength = 2147483392;
      this.bbwi.position(this.bbwi.position() - 4);
    } 
  }
  
  private void handleEndOfValue() {
    if (!this.isChunked)
      return; 
    while (this.blockLength != 2147483392) {
      end_block();
      start_block();
    } 
    int i = read_long();
    this.bbwi.position(this.bbwi.position() - 4);
    if (i < 0)
      return; 
    if (i == 0 || i >= 2147483392) {
      read_value();
      handleEndOfValue();
    } else {
      throw this.wrapper.couldNotSkipBytes(CompletionStatus.COMPLETED_MAYBE, new Integer(i), new Integer(get_offset()));
    } 
  }
  
  private void end_block() {
    if (this.blockLength != 2147483392)
      if (this.blockLength == get_offset()) {
        this.blockLength = 2147483392;
      } else if (this.blockLength > get_offset()) {
        skipToOffset(this.blockLength);
      } else {
        throw this.wrapper.badChunkLength(new Integer(this.blockLength), new Integer(get_offset()));
      }  
  }
  
  private int readValueTag() { return read_long(); }
  
  public ORB orb() { return this.orb; }
  
  public final void read_boolean_array(boolean[] paramArrayOfBoolean, int paramInt1, int paramInt2) {
    for (int i = 0; i < paramInt2; i++)
      paramArrayOfBoolean[i + paramInt1] = read_boolean(); 
  }
  
  public final void read_char_array(char[] paramArrayOfChar, int paramInt1, int paramInt2) {
    for (int i = 0; i < paramInt2; i++)
      paramArrayOfChar[i + paramInt1] = read_char(); 
  }
  
  public final void read_wchar_array(char[] paramArrayOfChar, int paramInt1, int paramInt2) {
    for (int i = 0; i < paramInt2; i++)
      paramArrayOfChar[i + paramInt1] = read_wchar(); 
  }
  
  public final void read_short_array(short[] paramArrayOfShort, int paramInt1, int paramInt2) {
    for (int i = 0; i < paramInt2; i++)
      paramArrayOfShort[i + paramInt1] = read_short(); 
  }
  
  public final void read_ushort_array(short[] paramArrayOfShort, int paramInt1, int paramInt2) { read_short_array(paramArrayOfShort, paramInt1, paramInt2); }
  
  public final void read_long_array(int[] paramArrayOfInt, int paramInt1, int paramInt2) {
    for (int i = 0; i < paramInt2; i++)
      paramArrayOfInt[i + paramInt1] = read_long(); 
  }
  
  public final void read_ulong_array(int[] paramArrayOfInt, int paramInt1, int paramInt2) { read_long_array(paramArrayOfInt, paramInt1, paramInt2); }
  
  public final void read_longlong_array(long[] paramArrayOfLong, int paramInt1, int paramInt2) {
    for (int i = 0; i < paramInt2; i++)
      paramArrayOfLong[i + paramInt1] = read_longlong(); 
  }
  
  public final void read_ulonglong_array(long[] paramArrayOfLong, int paramInt1, int paramInt2) { read_longlong_array(paramArrayOfLong, paramInt1, paramInt2); }
  
  public final void read_float_array(float[] paramArrayOfFloat, int paramInt1, int paramInt2) {
    for (int i = 0; i < paramInt2; i++)
      paramArrayOfFloat[i + paramInt1] = read_float(); 
  }
  
  public final void read_double_array(double[] paramArrayOfDouble, int paramInt1, int paramInt2) {
    for (int i = 0; i < paramInt2; i++)
      paramArrayOfDouble[i + paramInt1] = read_double(); 
  }
  
  public final void read_any_array(Any[] paramArrayOfAny, int paramInt1, int paramInt2) {
    for (int i = 0; i < paramInt2; i++)
      paramArrayOfAny[i + paramInt1] = read_any(); 
  }
  
  private String read_repositoryIds() {
    int i = read_long();
    if (i == -1) {
      int k = read_long() + get_offset() - 4;
      if (this.repositoryIdCache != null && this.repositoryIdCache.containsOrderedVal(k))
        return (String)this.repositoryIdCache.getKey(k); 
      throw this.wrapper.unableToLocateRepIdArray(new Integer(k));
    } 
    int j = get_offset();
    String str = read_repositoryId();
    if (this.repositoryIdCache == null)
      this.repositoryIdCache = new CacheTable(this.orb, false); 
    this.repositoryIdCache.put(str, j);
    for (byte b = 1; b < i; b++)
      read_repositoryId(); 
    return str;
  }
  
  private final String read_repositoryId() {
    String str = readStringOrIndirection(true);
    if (str == null) {
      int i = read_long() + get_offset() - 4;
      if (this.repositoryIdCache != null && this.repositoryIdCache.containsOrderedVal(i))
        return (String)this.repositoryIdCache.getKey(i); 
      throw this.wrapper.badRepIdIndirection(CompletionStatus.COMPLETED_MAYBE, new Integer(this.bbwi.position()));
    } 
    if (this.repositoryIdCache == null)
      this.repositoryIdCache = new CacheTable(this.orb, false); 
    this.repositoryIdCache.put(str, this.stringIndirection);
    return str;
  }
  
  private final String read_codebase_URL() {
    String str = readStringOrIndirection(true);
    if (str == null) {
      int i = read_long() + get_offset() - 4;
      if (this.codebaseCache != null && this.codebaseCache.containsVal(i))
        return (String)this.codebaseCache.getKey(i); 
      throw this.wrapper.badCodebaseIndirection(CompletionStatus.COMPLETED_MAYBE, new Integer(this.bbwi.position()));
    } 
    if (this.codebaseCache == null)
      this.codebaseCache = new CacheTable(this.orb, false); 
    this.codebaseCache.put(str, this.stringIndirection);
    return str;
  }
  
  public Object read_Abstract() { return read_abstract_interface(); }
  
  public Serializable read_Value() { return read_value(); }
  
  public void read_any_array(AnySeqHolder paramAnySeqHolder, int paramInt1, int paramInt2) { read_any_array(paramAnySeqHolder.value, paramInt1, paramInt2); }
  
  public void read_boolean_array(BooleanSeqHolder paramBooleanSeqHolder, int paramInt1, int paramInt2) { read_boolean_array(paramBooleanSeqHolder.value, paramInt1, paramInt2); }
  
  public void read_char_array(CharSeqHolder paramCharSeqHolder, int paramInt1, int paramInt2) { read_char_array(paramCharSeqHolder.value, paramInt1, paramInt2); }
  
  public void read_wchar_array(WCharSeqHolder paramWCharSeqHolder, int paramInt1, int paramInt2) { read_wchar_array(paramWCharSeqHolder.value, paramInt1, paramInt2); }
  
  public void read_octet_array(OctetSeqHolder paramOctetSeqHolder, int paramInt1, int paramInt2) { read_octet_array(paramOctetSeqHolder.value, paramInt1, paramInt2); }
  
  public void read_short_array(ShortSeqHolder paramShortSeqHolder, int paramInt1, int paramInt2) { read_short_array(paramShortSeqHolder.value, paramInt1, paramInt2); }
  
  public void read_ushort_array(UShortSeqHolder paramUShortSeqHolder, int paramInt1, int paramInt2) { read_ushort_array(paramUShortSeqHolder.value, paramInt1, paramInt2); }
  
  public void read_long_array(LongSeqHolder paramLongSeqHolder, int paramInt1, int paramInt2) { read_long_array(paramLongSeqHolder.value, paramInt1, paramInt2); }
  
  public void read_ulong_array(ULongSeqHolder paramULongSeqHolder, int paramInt1, int paramInt2) { read_ulong_array(paramULongSeqHolder.value, paramInt1, paramInt2); }
  
  public void read_ulonglong_array(ULongLongSeqHolder paramULongLongSeqHolder, int paramInt1, int paramInt2) { read_ulonglong_array(paramULongLongSeqHolder.value, paramInt1, paramInt2); }
  
  public void read_longlong_array(LongLongSeqHolder paramLongLongSeqHolder, int paramInt1, int paramInt2) { read_longlong_array(paramLongLongSeqHolder.value, paramInt1, paramInt2); }
  
  public void read_float_array(FloatSeqHolder paramFloatSeqHolder, int paramInt1, int paramInt2) { read_float_array(paramFloatSeqHolder.value, paramInt1, paramInt2); }
  
  public void read_double_array(DoubleSeqHolder paramDoubleSeqHolder, int paramInt1, int paramInt2) { read_double_array(paramDoubleSeqHolder.value, paramInt1, paramInt2); }
  
  public BigDecimal read_fixed(short paramShort1, short paramShort2) {
    StringBuffer stringBuffer = read_fixed_buffer();
    if (paramShort1 != stringBuffer.length())
      throw this.wrapper.badFixed(new Integer(paramShort1), new Integer(stringBuffer.length())); 
    stringBuffer.insert(paramShort1 - paramShort2, '.');
    return new BigDecimal(stringBuffer.toString());
  }
  
  public BigDecimal read_fixed() { return new BigDecimal(read_fixed_buffer().toString()); }
  
  private StringBuffer read_fixed_buffer() {
    StringBuffer stringBuffer = new StringBuffer(64);
    boolean bool1 = false;
    boolean bool2 = true;
    while (bool2) {
      byte b1 = read_octet();
      byte b2 = (b1 & 0xF0) >> 4;
      byte b3 = b1 & 0xF;
      if (bool1 || b2 != 0) {
        stringBuffer.append(Character.forDigit(b2, 10));
        bool1 = true;
      } 
      if (b3 == 12) {
        if (!bool1)
          return new StringBuffer("0.0"); 
        bool2 = false;
        continue;
      } 
      if (b3 == 13) {
        stringBuffer.insert(0, '-');
        bool2 = false;
        continue;
      } 
      stringBuffer.append(Character.forDigit(b3, 10));
      bool1 = true;
    } 
    return stringBuffer;
  }
  
  public String[] _truncatable_ids() { return (_ids == null) ? null : (String[])_ids.clone(); }
  
  public void printBuffer() { printBuffer(this.bbwi); }
  
  public static void printBuffer(ByteBufferWithInfo paramByteBufferWithInfo) {
    System.out.println("----- Input Buffer -----");
    System.out.println();
    System.out.println("Current position: " + paramByteBufferWithInfo.position());
    System.out.println("Total length : " + paramByteBufferWithInfo.buflen);
    System.out.println();
    try {
      char[] arrayOfChar = new char[16];
      for (byte b = 0; b < paramByteBufferWithInfo.buflen; b += 16) {
        byte b1;
        for (b1 = 0; b1 < 16 && b1 + b < paramByteBufferWithInfo.buflen; b1++) {
          char c = paramByteBufferWithInfo.byteBuffer.get(b + b1);
          if (c < 0)
            c = 'Ā' + c; 
          String str = Integer.toHexString(c);
          if (str.length() == 1)
            str = "0" + str; 
          System.out.print(str + " ");
        } 
        while (b1 < 16) {
          System.out.print("   ");
          b1++;
        } 
        byte b2;
        for (b2 = 0; b2 < 16 && b2 + b < paramByteBufferWithInfo.buflen; b2++) {
          if (ORBUtility.isPrintable((char)paramByteBufferWithInfo.byteBuffer.get(b + b2))) {
            arrayOfChar[b2] = (char)paramByteBufferWithInfo.byteBuffer.get(b + b2);
          } else {
            arrayOfChar[b2] = '.';
          } 
        } 
        System.out.println(new String(arrayOfChar, 0, b2));
      } 
    } catch (Throwable throwable) {
      throwable.printStackTrace();
    } 
    System.out.println("------------------------");
  }
  
  public ByteBuffer getByteBuffer() {
    ByteBuffer byteBuffer = null;
    if (this.bbwi != null)
      byteBuffer = this.bbwi.byteBuffer; 
    return byteBuffer;
  }
  
  public int getBufferLength() { return this.bbwi.buflen; }
  
  public void setBufferLength(int paramInt) {
    this.bbwi.buflen = paramInt;
    this.bbwi.byteBuffer.limit(this.bbwi.buflen);
  }
  
  public void setByteBufferWithInfo(ByteBufferWithInfo paramByteBufferWithInfo) { this.bbwi = paramByteBufferWithInfo; }
  
  public void setByteBuffer(ByteBuffer paramByteBuffer) { this.bbwi.byteBuffer = paramByteBuffer; }
  
  public int getIndex() { return this.bbwi.position(); }
  
  public void setIndex(int paramInt) { this.bbwi.position(paramInt); }
  
  public boolean isLittleEndian() { return this.littleEndian; }
  
  public void orb(ORB paramORB) { this.orb = (ORB)paramORB; }
  
  public BufferManagerRead getBufferManager() { return this.bufferManagerRead; }
  
  private void skipToOffset(int paramInt) {
    int i = paramInt - get_offset();
    for (int j = 0; j < i; j += m) {
      int k = this.bbwi.buflen - this.bbwi.position();
      if (k <= 0) {
        grow(1, 1);
        k = this.bbwi.buflen - this.bbwi.position();
      } 
      int n = i - j;
      int m = (n < k) ? n : k;
      this.bbwi.position(this.bbwi.position() + m);
    } 
  }
  
  public Object createStreamMemento() { return new StreamMemento(); }
  
  public void restoreInternalState(Object paramObject) {
    StreamMemento streamMemento;
    this.blockLength = streamMemento.blockLength_;
    this.end_flag = streamMemento.end_flag_;
    this.chunkedValueNestingLevel = streamMemento.chunkedValueNestingLevel_;
    this.valueIndirection = streamMemento.valueIndirection_;
    this.stringIndirection = streamMemento.stringIndirection_;
    this.isChunked = streamMemento.isChunked_;
    this.valueHandler = streamMemento.valueHandler_;
    this.specialNoOptionalDataState = streamMemento.specialNoOptionalDataState_;
    this.bbwi = streamMemento.bbwi_;
  }
  
  public int getPosition() { return get_offset(); }
  
  public void mark(int paramInt) { this.markAndResetHandler.mark(this); }
  
  public void reset() { this.markAndResetHandler.reset(); }
  
  CodeBase getCodeBase() { return this.parent.getCodeBase(); }
  
  private Class getClassFromString(String paramString1, String paramString2, Class paramClass) {
    RepositoryIdInterface repositoryIdInterface = this.repIdStrs.getFromString(paramString1);
    try {
      return repositoryIdInterface.getClassFromType(paramClass, paramString2);
    } catch (ClassNotFoundException classNotFoundException) {
      try {
        if (getCodeBase() == null)
          return null; 
        paramString2 = getCodeBase().implementation(paramString1);
        return (paramString2 == null) ? null : repositoryIdInterface.getClassFromType(paramClass, paramString2);
      } catch (ClassNotFoundException classNotFoundException1) {
        dprintThrowable(classNotFoundException1);
        return null;
      } 
    } catch (MalformedURLException malformedURLException) {
      throw this.wrapper.malformedUrl(CompletionStatus.COMPLETED_MAYBE, malformedURLException, paramString1, paramString2);
    } 
  }
  
  private Class getClassFromString(String paramString1, String paramString2) {
    RepositoryIdInterface repositoryIdInterface = this.repIdStrs.getFromString(paramString1);
    for (byte b = 0; b < 3; b++) {
      try {
        switch (b) {
          case false:
            return repositoryIdInterface.getClassFromType();
          case true:
            paramString2 = getCodeBase().implementation(paramString1);
            break;
        } 
        if (paramString2 != null)
          return repositoryIdInterface.getClassFromType(paramString2); 
      } catch (ClassNotFoundException classNotFoundException) {
      
      } catch (MalformedURLException malformedURLException) {
        throw this.wrapper.malformedUrl(CompletionStatus.COMPLETED_MAYBE, malformedURLException, paramString1, paramString2);
      } 
    } 
    dprint("getClassFromString failed with rep id " + paramString1 + " and codebase " + paramString2);
    return null;
  }
  
  char[] getConvertedChars(int paramInt, CodeSetConversion.BTCConverter paramBTCConverter) {
    if (this.bbwi.buflen - this.bbwi.position() >= paramInt) {
      byte[] arrayOfByte1;
      if (this.bbwi.byteBuffer.hasArray()) {
        arrayOfByte1 = this.bbwi.byteBuffer.array();
      } else {
        arrayOfByte1 = new byte[this.bbwi.buflen];
        for (byte b = 0; b < this.bbwi.buflen; b++)
          arrayOfByte1[b] = this.bbwi.byteBuffer.get(b); 
      } 
      char[] arrayOfChar = paramBTCConverter.getChars(arrayOfByte1, this.bbwi.position(), paramInt);
      this.bbwi.position(this.bbwi.position() + paramInt);
      return arrayOfChar;
    } 
    byte[] arrayOfByte = new byte[paramInt];
    read_octet_array(arrayOfByte, 0, arrayOfByte.length);
    return paramBTCConverter.getChars(arrayOfByte, 0, paramInt);
  }
  
  protected CodeSetConversion.BTCConverter getCharConverter() {
    if (this.charConverter == null)
      this.charConverter = this.parent.createCharBTCConverter(); 
    return this.charConverter;
  }
  
  protected CodeSetConversion.BTCConverter getWCharConverter() {
    if (this.wcharConverter == null)
      this.wcharConverter = this.parent.createWCharBTCConverter(); 
    return this.wcharConverter;
  }
  
  protected void dprintThrowable(Throwable paramThrowable) {
    if (this.debug && paramThrowable != null)
      paramThrowable.printStackTrace(); 
  }
  
  protected void dprint(String paramString) {
    if (this.debug)
      ORBUtility.dprint(this, paramString); 
  }
  
  void alignOnBoundary(int paramInt) {
    int i = computeAlignment(this.bbwi.position(), paramInt);
    if (this.bbwi.position() + i <= this.bbwi.buflen)
      this.bbwi.position(this.bbwi.position() + i); 
  }
  
  public void resetCodeSetConverters() {
    this.charConverter = null;
    this.wcharConverter = null;
  }
  
  public void start_value() {
    int i = readValueTag();
    if (i == 0) {
      this.specialNoOptionalDataState = true;
      return;
    } 
    if (i == -1)
      throw this.wrapper.customWrapperIndirection(CompletionStatus.COMPLETED_MAYBE); 
    if (this.repIdUtil.isCodeBasePresent(i))
      throw this.wrapper.customWrapperWithCodebase(CompletionStatus.COMPLETED_MAYBE); 
    if (this.repIdUtil.getTypeInfo(i) != 2)
      throw this.wrapper.customWrapperNotSingleRepid(CompletionStatus.COMPLETED_MAYBE); 
    read_repositoryId();
    start_block();
    this.end_flag--;
    this.chunkedValueNestingLevel--;
  }
  
  public void end_value() {
    if (this.specialNoOptionalDataState) {
      this.specialNoOptionalDataState = false;
      return;
    } 
    handleEndOfValue();
    readEndTag();
    start_block();
  }
  
  public void close() {
    getBufferManager().close(this.bbwi);
    if (this.bbwi != null && getByteBuffer() != null) {
      MessageMediator messageMediator = this.parent.getMessageMediator();
      if (messageMediator != null) {
        CDROutputObject cDROutputObject = (CDROutputObject)messageMediator.getOutputObject();
        if (cDROutputObject != null && cDROutputObject.isSharing(getByteBuffer())) {
          cDROutputObject.setByteBuffer(null);
          cDROutputObject.setByteBufferWithInfo(null);
        } 
      } 
      ByteBufferPool byteBufferPool = this.orb.getByteBufferPool();
      if (this.debug) {
        int i = System.identityHashCode(this.bbwi.byteBuffer);
        StringBuffer stringBuffer = new StringBuffer(80);
        stringBuffer.append(".close - releasing ByteBuffer id (");
        stringBuffer.append(i).append(") to ByteBufferPool.");
        String str = stringBuffer.toString();
        dprint(str);
      } 
      byteBufferPool.releaseByteBuffer(this.bbwi.byteBuffer);
      this.bbwi.byteBuffer = null;
      this.bbwi = null;
    } 
  }
  
  protected class StreamMemento {
    private int blockLength_ = CDRInputStream_1_0.this.blockLength;
    
    private int end_flag_ = CDRInputStream_1_0.this.end_flag;
    
    private int chunkedValueNestingLevel_;
    
    private int valueIndirection_;
    
    private int stringIndirection_;
    
    private boolean isChunked_;
    
    private ValueHandler valueHandler_;
    
    private ByteBufferWithInfo bbwi_;
    
    private boolean specialNoOptionalDataState_;
    
    public StreamMemento() {
      this.chunkedValueNestingLevel_ = this$0.chunkedValueNestingLevel;
      this.valueIndirection_ = CDRInputStream_1_0.this.valueIndirection;
      this.stringIndirection_ = CDRInputStream_1_0.this.stringIndirection;
      this.isChunked_ = CDRInputStream_1_0.this.isChunked;
      this.valueHandler_ = CDRInputStream_1_0.this.valueHandler;
      this.specialNoOptionalDataState_ = this$0.specialNoOptionalDataState;
      this.bbwi_ = new ByteBufferWithInfo(CDRInputStream_1_0.this.bbwi);
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\impl\encoding\CDRInputStream_1_0.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */