package com.sun.corba.se.impl.corba;

import com.sun.corba.se.impl.encoding.CDRInputStream;
import com.sun.corba.se.impl.encoding.CDROutputStream;
import com.sun.corba.se.impl.encoding.TypeCodeInputStream;
import com.sun.corba.se.impl.encoding.TypeCodeOutputStream;
import com.sun.corba.se.impl.encoding.TypeCodeReader;
import com.sun.corba.se.impl.encoding.WrapperInputStream;
import com.sun.corba.se.impl.logging.ORBUtilSystemException;
import com.sun.corba.se.spi.orb.ORB;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.math.BigDecimal;
import org.omg.CORBA.Any;
import org.omg.CORBA.StructMember;
import org.omg.CORBA.TCKind;
import org.omg.CORBA.TypeCode;
import org.omg.CORBA.TypeCodePackage.BadKind;
import org.omg.CORBA.TypeCodePackage.Bounds;
import org.omg.CORBA.UnionMember;
import org.omg.CORBA.ValueMember;
import org.omg.CORBA.portable.InputStream;
import org.omg.CORBA.portable.OutputStream;
import org.omg.CORBA_2_3.portable.InputStream;
import org.omg.CORBA_2_3.portable.OutputStream;
import sun.corba.OutputStreamFactory;

public final class TypeCodeImpl extends TypeCode {
  protected static final int tk_indirect = -1;
  
  private static final int EMPTY = 0;
  
  private static final int SIMPLE = 1;
  
  private static final int COMPLEX = 2;
  
  private static final int[] typeTable = { 
      0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 
      0, 0, 0, 0, 2, 2, 2, 2, 1, 2, 
      2, 2, 2, 0, 0, 0, 0, 1, 1, 2, 
      2, 2, 2 };
  
  static final String[] kindNames = { 
      "null", "void", "short", "long", "ushort", "ulong", "float", "double", "boolean", "char", 
      "octet", "any", "typecode", "principal", "objref", "struct", "union", "enum", "string", "sequence", 
      "array", "alias", "exception", "longlong", "ulonglong", "longdouble", "wchar", "wstring", "fixed", "value", 
      "valueBox", "native", "abstractInterface" };
  
  private int _kind = 0;
  
  private String _id = "";
  
  private String _name = "";
  
  private int _memberCount = 0;
  
  private String[] _memberNames = null;
  
  private TypeCodeImpl[] _memberTypes = null;
  
  private AnyImpl[] _unionLabels = null;
  
  private TypeCodeImpl _discriminator = null;
  
  private int _defaultIndex = -1;
  
  private int _length = 0;
  
  private TypeCodeImpl _contentType = null;
  
  private short _digits = 0;
  
  private short _scale = 0;
  
  private short _type_modifier = -1;
  
  private TypeCodeImpl _concrete_base = null;
  
  private short[] _memberAccess = null;
  
  private TypeCodeImpl _parent = null;
  
  private int _parentOffset = 0;
  
  private TypeCodeImpl _indirectType = null;
  
  private byte[] outBuffer = null;
  
  private boolean cachingEnabled = false;
  
  private ORB _orb;
  
  private ORBUtilSystemException wrapper;
  
  public TypeCodeImpl(ORB paramORB) {
    this._orb = paramORB;
    this.wrapper = ORBUtilSystemException.get(paramORB, "rpc.presentation");
  }
  
  public TypeCodeImpl(ORB paramORB, TypeCode paramTypeCode) {
    this(paramORB);
    if (paramTypeCode instanceof TypeCodeImpl) {
      TypeCodeImpl typeCodeImpl = (TypeCodeImpl)paramTypeCode;
      if (typeCodeImpl._kind == -1)
        throw this.wrapper.badRemoteTypecode(); 
      if (typeCodeImpl._kind == 19 && typeCodeImpl._contentType == null)
        throw this.wrapper.badRemoteTypecode(); 
    } 
    this._kind = paramTypeCode.kind().value();
    try {
      byte b2;
      TypeCode typeCode;
      byte b1;
      switch (this._kind) {
        case 29:
          this._type_modifier = paramTypeCode.type_modifier();
          typeCode = paramTypeCode.concrete_base_type();
          if (typeCode != null) {
            this._concrete_base = convertToNative(this._orb, typeCode);
          } else {
            this._concrete_base = null;
          } 
          this._memberAccess = new short[paramTypeCode.member_count()];
          for (b2 = 0; b2 < paramTypeCode.member_count(); b2++)
            this._memberAccess[b2] = paramTypeCode.member_visibility(b2); 
        case 15:
        case 16:
        case 22:
          this._memberTypes = new TypeCodeImpl[paramTypeCode.member_count()];
          for (b2 = 0; b2 < paramTypeCode.member_count(); b2++) {
            this._memberTypes[b2] = convertToNative(this._orb, paramTypeCode.member_type(b2));
            this._memberTypes[b2].setParent(this);
          } 
        case 17:
          this._memberNames = new String[paramTypeCode.member_count()];
          for (b2 = 0; b2 < paramTypeCode.member_count(); b2++)
            this._memberNames[b2] = paramTypeCode.member_name(b2); 
          this._memberCount = paramTypeCode.member_count();
        case 14:
        case 21:
        case 30:
        case 31:
        case 32:
          setId(paramTypeCode.id());
          this._name = paramTypeCode.name();
          break;
      } 
      switch (this._kind) {
        case 16:
          this._discriminator = convertToNative(this._orb, paramTypeCode.discriminator_type());
          this._defaultIndex = paramTypeCode.default_index();
          this._unionLabels = new AnyImpl[this._memberCount];
          for (b1 = 0; b1 < this._memberCount; b1++)
            this._unionLabels[b1] = new AnyImpl(this._orb, paramTypeCode.member_label(b1)); 
          break;
      } 
      switch (this._kind) {
        case 18:
        case 19:
        case 20:
        case 27:
          this._length = paramTypeCode.length();
          break;
      } 
      switch (this._kind) {
        case 19:
        case 20:
        case 21:
        case 30:
          this._contentType = convertToNative(this._orb, paramTypeCode.content_type());
          break;
      } 
    } catch (Bounds bounds) {
    
    } catch (BadKind badKind) {}
  }
  
  public TypeCodeImpl(ORB paramORB, int paramInt) {
    this(paramORB);
    this._kind = paramInt;
    switch (this._kind) {
      case 14:
        setId("IDL:omg.org/CORBA/Object:1.0");
        this._name = "Object";
        break;
      case 18:
      case 27:
        this._length = 0;
        break;
      case 29:
        this._concrete_base = null;
        break;
    } 
  }
  
  public TypeCodeImpl(ORB paramORB, int paramInt, String paramString1, String paramString2, StructMember[] paramArrayOfStructMember) {
    this(paramORB);
    if (paramInt == 15 || paramInt == 22) {
      this._kind = paramInt;
      setId(paramString1);
      this._name = paramString2;
      this._memberCount = paramArrayOfStructMember.length;
      this._memberNames = new String[this._memberCount];
      this._memberTypes = new TypeCodeImpl[this._memberCount];
      for (byte b = 0; b < this._memberCount; b++) {
        this._memberNames[b] = (paramArrayOfStructMember[b]).name;
        this._memberTypes[b] = convertToNative(this._orb, (paramArrayOfStructMember[b]).type);
        this._memberTypes[b].setParent(this);
      } 
    } 
  }
  
  public TypeCodeImpl(ORB paramORB, int paramInt, String paramString1, String paramString2, TypeCode paramTypeCode, UnionMember[] paramArrayOfUnionMember) {
    this(paramORB);
    if (paramInt == 16) {
      this._kind = paramInt;
      setId(paramString1);
      this._name = paramString2;
      this._memberCount = paramArrayOfUnionMember.length;
      this._discriminator = convertToNative(this._orb, paramTypeCode);
      this._memberNames = new String[this._memberCount];
      this._memberTypes = new TypeCodeImpl[this._memberCount];
      this._unionLabels = new AnyImpl[this._memberCount];
      for (byte b = 0; b < this._memberCount; b++) {
        this._memberNames[b] = (paramArrayOfUnionMember[b]).name;
        this._memberTypes[b] = convertToNative(this._orb, (paramArrayOfUnionMember[b]).type);
        this._memberTypes[b].setParent(this);
        this._unionLabels[b] = new AnyImpl(this._orb, (paramArrayOfUnionMember[b]).label);
        if (this._unionLabels[b].type().kind() == TCKind.tk_octet && this._unionLabels[b].extract_octet() == 0)
          this._defaultIndex = b; 
      } 
    } 
  }
  
  public TypeCodeImpl(ORB paramORB, int paramInt, String paramString1, String paramString2, short paramShort, TypeCode paramTypeCode, ValueMember[] paramArrayOfValueMember) {
    this(paramORB);
    if (paramInt == 29) {
      this._kind = paramInt;
      setId(paramString1);
      this._name = paramString2;
      this._type_modifier = paramShort;
      if (paramTypeCode != null)
        this._concrete_base = convertToNative(this._orb, paramTypeCode); 
      this._memberCount = paramArrayOfValueMember.length;
      this._memberNames = new String[this._memberCount];
      this._memberTypes = new TypeCodeImpl[this._memberCount];
      this._memberAccess = new short[this._memberCount];
      for (byte b = 0; b < this._memberCount; b++) {
        this._memberNames[b] = (paramArrayOfValueMember[b]).name;
        this._memberTypes[b] = convertToNative(this._orb, (paramArrayOfValueMember[b]).type);
        this._memberTypes[b].setParent(this);
        this._memberAccess[b] = (paramArrayOfValueMember[b]).access;
      } 
    } 
  }
  
  public TypeCodeImpl(ORB paramORB, int paramInt, String paramString1, String paramString2, String[] paramArrayOfString) {
    this(paramORB);
    if (paramInt == 17) {
      this._kind = paramInt;
      setId(paramString1);
      this._name = paramString2;
      this._memberCount = paramArrayOfString.length;
      this._memberNames = new String[this._memberCount];
      for (byte b = 0; b < this._memberCount; b++)
        this._memberNames[b] = paramArrayOfString[b]; 
    } 
  }
  
  public TypeCodeImpl(ORB paramORB, int paramInt, String paramString1, String paramString2, TypeCode paramTypeCode) {
    this(paramORB);
    if (paramInt == 21 || paramInt == 30) {
      this._kind = paramInt;
      setId(paramString1);
      this._name = paramString2;
      this._contentType = convertToNative(this._orb, paramTypeCode);
    } 
  }
  
  public TypeCodeImpl(ORB paramORB, int paramInt, String paramString1, String paramString2) {
    this(paramORB);
    if (paramInt == 14 || paramInt == 31 || paramInt == 32) {
      this._kind = paramInt;
      setId(paramString1);
      this._name = paramString2;
    } 
  }
  
  public TypeCodeImpl(ORB paramORB, int paramInt1, int paramInt2) {
    this(paramORB);
    if (paramInt2 < 0)
      throw this.wrapper.negativeBounds(); 
    if (paramInt1 == 18 || paramInt1 == 27) {
      this._kind = paramInt1;
      this._length = paramInt2;
    } 
  }
  
  public TypeCodeImpl(ORB paramORB, int paramInt1, int paramInt2, TypeCode paramTypeCode) {
    this(paramORB);
    if (paramInt1 == 19 || paramInt1 == 20) {
      this._kind = paramInt1;
      this._length = paramInt2;
      this._contentType = convertToNative(this._orb, paramTypeCode);
    } 
  }
  
  public TypeCodeImpl(ORB paramORB, int paramInt1, int paramInt2, int paramInt3) {
    this(paramORB);
    if (paramInt1 == 19) {
      this._kind = paramInt1;
      this._length = paramInt2;
      this._parentOffset = paramInt3;
    } 
  }
  
  public TypeCodeImpl(ORB paramORB, String paramString) {
    this(paramORB);
    this._kind = -1;
    this._id = paramString;
    tryIndirectType();
  }
  
  public TypeCodeImpl(ORB paramORB, int paramInt, short paramShort1, short paramShort2) {
    this(paramORB);
    if (paramInt == 28) {
      this._kind = paramInt;
      this._digits = paramShort1;
      this._scale = paramShort2;
    } 
  }
  
  protected static TypeCodeImpl convertToNative(ORB paramORB, TypeCode paramTypeCode) { return (paramTypeCode instanceof TypeCodeImpl) ? (TypeCodeImpl)paramTypeCode : new TypeCodeImpl(paramORB, paramTypeCode); }
  
  public static CDROutputStream newOutputStream(ORB paramORB) { return OutputStreamFactory.newTypeCodeOutputStream(paramORB); }
  
  private TypeCodeImpl indirectType() {
    this._indirectType = tryIndirectType();
    if (this._indirectType == null)
      throw this.wrapper.unresolvedRecursiveTypecode(); 
    return this._indirectType;
  }
  
  private TypeCodeImpl tryIndirectType() {
    if (this._indirectType != null)
      return this._indirectType; 
    setIndirectType(this._orb.getTypeCode(this._id));
    return this._indirectType;
  }
  
  private void setIndirectType(TypeCodeImpl paramTypeCodeImpl) {
    this._indirectType = paramTypeCodeImpl;
    if (this._indirectType != null)
      try {
        this._id = this._indirectType.id();
      } catch (BadKind badKind) {
        throw this.wrapper.badkindCannotOccur();
      }  
  }
  
  private void setId(String paramString) {
    this._id = paramString;
    if (this._orb instanceof TypeCodeFactory)
      this._orb.setTypeCode(this._id, this); 
  }
  
  private void setParent(TypeCodeImpl paramTypeCodeImpl) { this._parent = paramTypeCodeImpl; }
  
  private TypeCodeImpl getParentAtLevel(int paramInt) {
    if (paramInt == 0)
      return this; 
    if (this._parent == null)
      throw this.wrapper.unresolvedRecursiveTypecode(); 
    return this._parent.getParentAtLevel(paramInt - 1);
  }
  
  private TypeCodeImpl lazy_content_type() {
    if (this._contentType == null && this._kind == 19 && this._parentOffset > 0 && this._parent != null) {
      TypeCodeImpl typeCodeImpl = getParentAtLevel(this._parentOffset);
      if (typeCodeImpl != null && typeCodeImpl._id != null)
        this._contentType = new TypeCodeImpl(this._orb, typeCodeImpl._id); 
    } 
    return this._contentType;
  }
  
  private TypeCode realType(TypeCode paramTypeCode) {
    TypeCode typeCode = paramTypeCode;
    try {
      while (typeCode.kind().value() == 21)
        typeCode = typeCode.content_type(); 
    } catch (BadKind badKind) {
      throw this.wrapper.badkindCannotOccur();
    } 
    return typeCode;
  }
  
  public final boolean equal(TypeCode paramTypeCode) {
    if (paramTypeCode == this)
      return true; 
    try {
      TypeCode typeCode;
      byte b;
      if (this._kind == -1)
        return (this._id != null && paramTypeCode.id() != null) ? this._id.equals(paramTypeCode.id()) : ((this._id == null && paramTypeCode.id() == null) ? 1 : 0); 
      if (this._kind != paramTypeCode.kind().value())
        return false; 
      switch (typeTable[this._kind]) {
        case 0:
          return true;
        case 1:
          switch (this._kind) {
            case 18:
            case 27:
              return (this._length == paramTypeCode.length());
            case 28:
              return (this._digits == paramTypeCode.fixed_digits() && this._scale == paramTypeCode.fixed_scale());
          } 
          return false;
        case 2:
          switch (this._kind) {
            case 14:
              return (this._id.compareTo(paramTypeCode.id()) == 0) ? true : ((this._id.compareTo(this._orb.get_primitive_tc(this._kind).id()) == 0) ? true : ((paramTypeCode.id().compareTo(this._orb.get_primitive_tc(this._kind).id()) == 0)));
            case 31:
            case 32:
              return !(this._id.compareTo(paramTypeCode.id()) != 0);
            case 15:
            case 22:
              if (this._memberCount != paramTypeCode.member_count())
                return false; 
              if (this._id.compareTo(paramTypeCode.id()) != 0)
                return false; 
              for (b = 0; b < this._memberCount; b++) {
                if (!this._memberTypes[b].equal(paramTypeCode.member_type(b)))
                  return false; 
              } 
              return true;
            case 16:
              if (this._memberCount != paramTypeCode.member_count())
                return false; 
              if (this._id.compareTo(paramTypeCode.id()) != 0)
                return false; 
              if (this._defaultIndex != paramTypeCode.default_index())
                return false; 
              if (!this._discriminator.equal(paramTypeCode.discriminator_type()))
                return false; 
              for (b = 0; b < this._memberCount; b++) {
                if (!this._unionLabels[b].equal(paramTypeCode.member_label(b)))
                  return false; 
              } 
              for (b = 0; b < this._memberCount; b++) {
                if (!this._memberTypes[b].equal(paramTypeCode.member_type(b)))
                  return false; 
              } 
              return true;
            case 17:
              return (this._id.compareTo(paramTypeCode.id()) != 0) ? false : (!(this._memberCount != paramTypeCode.member_count()));
            case 19:
            case 20:
              return (this._length != paramTypeCode.length()) ? false : (!!lazy_content_type().equal(paramTypeCode.content_type()));
            case 29:
              if (this._memberCount != paramTypeCode.member_count())
                return false; 
              if (this._id.compareTo(paramTypeCode.id()) != 0)
                return false; 
              for (b = 0; b < this._memberCount; b++) {
                if (this._memberAccess[b] != paramTypeCode.member_visibility(b) || !this._memberTypes[b].equal(paramTypeCode.member_type(b)))
                  return false; 
              } 
              if (this._type_modifier == paramTypeCode.type_modifier())
                return false; 
              typeCode = paramTypeCode.concrete_base_type();
              return !((this._concrete_base == null && typeCode != null) || (this._concrete_base != null && typeCode == null) || !this._concrete_base.equal(typeCode));
            case 21:
            case 30:
              return (this._id.compareTo(paramTypeCode.id()) != 0) ? false : this._contentType.equal(paramTypeCode.content_type());
          } 
          break;
      } 
    } catch (Bounds bounds) {
    
    } catch (BadKind badKind) {}
    return false;
  }
  
  public boolean equivalent(TypeCode paramTypeCode) {
    if (paramTypeCode == this)
      return true; 
    TypeCodeImpl typeCodeImpl = (this._kind == -1) ? indirectType() : this;
    TypeCode typeCode1 = realType(typeCodeImpl);
    TypeCode typeCode2 = realType(paramTypeCode);
    if (typeCode1.kind().value() != typeCode2.kind().value())
      return false; 
    String str1 = null;
    String str2 = null;
    try {
      str1 = id();
      str2 = paramTypeCode.id();
      if (str1 != null && str2 != null)
        return str1.equals(str2); 
    } catch (BadKind badKind) {}
    int i = typeCode1.kind().value();
    try {
      if ((i == 15 || i == 16 || i == 17 || i == 22 || i == 29) && typeCode1.member_count() != typeCode2.member_count())
        return false; 
      if (i == 16 && typeCode1.default_index() != typeCode2.default_index())
        return false; 
      if ((i == 18 || i == 27 || i == 19 || i == 20) && typeCode1.length() != typeCode2.length())
        return false; 
      if (i == 28 && (typeCode1.fixed_digits() != typeCode2.fixed_digits() || typeCode1.fixed_scale() != typeCode2.fixed_scale()))
        return false; 
      if (i == 16) {
        for (byte b = 0; b < typeCode1.member_count(); b++) {
          if (typeCode1.member_label(b) != typeCode2.member_label(b))
            return false; 
        } 
        if (!typeCode1.discriminator_type().equivalent(typeCode2.discriminator_type()))
          return false; 
      } 
      if ((i == 21 || i == 30 || i == 19 || i == 20) && !typeCode1.content_type().equivalent(typeCode2.content_type()))
        return false; 
      if (i == 15 || i == 16 || i == 22 || i == 29)
        for (byte b = 0; b < typeCode1.member_count(); b++) {
          if (!typeCode1.member_type(b).equivalent(typeCode2.member_type(b)))
            return false; 
        }  
    } catch (BadKind badKind) {
      throw this.wrapper.badkindCannotOccur();
    } catch (Bounds bounds) {
      throw this.wrapper.boundsCannotOccur();
    } 
    return true;
  }
  
  public TypeCode get_compact_typecode() { return this; }
  
  public TCKind kind() { return (this._kind == -1) ? indirectType().kind() : TCKind.from_int(this._kind); }
  
  public boolean is_recursive() { return (this._kind == -1); }
  
  public String id() throws BadKind {
    switch (this._kind) {
      case -1:
      case 14:
      case 15:
      case 16:
      case 17:
      case 21:
      case 22:
      case 29:
      case 30:
      case 31:
      case 32:
        return this._id;
    } 
    throw new BadKind();
  }
  
  public String name() throws BadKind {
    switch (this._kind) {
      case -1:
        return indirectType().name();
      case 14:
      case 15:
      case 16:
      case 17:
      case 21:
      case 22:
      case 29:
      case 30:
      case 31:
      case 32:
        return this._name;
    } 
    throw new BadKind();
  }
  
  public int member_count() throws BadKind {
    switch (this._kind) {
      case -1:
        return indirectType().member_count();
      case 15:
      case 16:
      case 17:
      case 22:
      case 29:
        return this._memberCount;
    } 
    throw new BadKind();
  }
  
  public String member_name(int paramInt) throws BadKind, Bounds {
    switch (this._kind) {
      case -1:
        return indirectType().member_name(paramInt);
      case 15:
      case 16:
      case 17:
      case 22:
      case 29:
        try {
          return this._memberNames[paramInt];
        } catch (ArrayIndexOutOfBoundsException arrayIndexOutOfBoundsException) {
          throw new Bounds();
        } 
    } 
    throw new BadKind();
  }
  
  public TypeCode member_type(int paramInt) throws BadKind, Bounds {
    switch (this._kind) {
      case -1:
        return indirectType().member_type(paramInt);
      case 15:
      case 16:
      case 22:
      case 29:
        try {
          return this._memberTypes[paramInt];
        } catch (ArrayIndexOutOfBoundsException arrayIndexOutOfBoundsException) {
          throw new Bounds();
        } 
    } 
    throw new BadKind();
  }
  
  public Any member_label(int paramInt) throws BadKind, Bounds {
    switch (this._kind) {
      case -1:
        return indirectType().member_label(paramInt);
      case 16:
        try {
          return new AnyImpl(this._orb, this._unionLabels[paramInt]);
        } catch (ArrayIndexOutOfBoundsException arrayIndexOutOfBoundsException) {
          throw new Bounds();
        } 
    } 
    throw new BadKind();
  }
  
  public TypeCode discriminator_type() {
    switch (this._kind) {
      case -1:
        return indirectType().discriminator_type();
      case 16:
        return this._discriminator;
    } 
    throw new BadKind();
  }
  
  public int default_index() throws BadKind {
    switch (this._kind) {
      case -1:
        return indirectType().default_index();
      case 16:
        return this._defaultIndex;
    } 
    throw new BadKind();
  }
  
  public int length() throws BadKind {
    switch (this._kind) {
      case -1:
        return indirectType().length();
      case 18:
      case 19:
      case 20:
      case 27:
        return this._length;
    } 
    throw new BadKind();
  }
  
  public TypeCode content_type() {
    switch (this._kind) {
      case -1:
        return indirectType().content_type();
      case 19:
        return lazy_content_type();
      case 20:
      case 21:
      case 30:
        return this._contentType;
    } 
    throw new BadKind();
  }
  
  public short fixed_digits() throws BadKind {
    switch (this._kind) {
      case 28:
        return this._digits;
    } 
    throw new BadKind();
  }
  
  public short fixed_scale() throws BadKind {
    switch (this._kind) {
      case 28:
        return this._scale;
    } 
    throw new BadKind();
  }
  
  public short member_visibility(int paramInt) throws BadKind, Bounds {
    switch (this._kind) {
      case -1:
        return indirectType().member_visibility(paramInt);
      case 29:
        try {
          return this._memberAccess[paramInt];
        } catch (ArrayIndexOutOfBoundsException arrayIndexOutOfBoundsException) {
          throw new Bounds();
        } 
    } 
    throw new BadKind();
  }
  
  public short type_modifier() throws BadKind {
    switch (this._kind) {
      case -1:
        return indirectType().type_modifier();
      case 29:
        return this._type_modifier;
    } 
    throw new BadKind();
  }
  
  public TypeCode concrete_base_type() {
    switch (this._kind) {
      case -1:
        return indirectType().concrete_base_type();
      case 29:
        return this._concrete_base;
    } 
    throw new BadKind();
  }
  
  public void read_value(InputStream paramInputStream) {
    if (paramInputStream instanceof TypeCodeReader) {
      if (read_value_kind((TypeCodeReader)paramInputStream))
        read_value_body(paramInputStream); 
    } else if (paramInputStream instanceof CDRInputStream) {
      WrapperInputStream wrapperInputStream = new WrapperInputStream((CDRInputStream)paramInputStream);
      if (read_value_kind(wrapperInputStream))
        read_value_body(wrapperInputStream); 
    } else {
      read_value_kind(paramInputStream);
      read_value_body(paramInputStream);
    } 
  }
  
  private void read_value_recursive(TypeCodeInputStream paramTypeCodeInputStream) {
    if (paramTypeCodeInputStream instanceof TypeCodeReader) {
      if (read_value_kind(paramTypeCodeInputStream))
        read_value_body(paramTypeCodeInputStream); 
    } else {
      read_value_kind(paramTypeCodeInputStream);
      read_value_body(paramTypeCodeInputStream);
    } 
  }
  
  boolean read_value_kind(TypeCodeReader paramTypeCodeReader) {
    this._kind = paramTypeCodeReader.read_long();
    int i = paramTypeCodeReader.getTopLevelPosition() - 4;
    if ((this._kind < 0 || this._kind > typeTable.length) && this._kind != -1)
      throw this.wrapper.cannotMarshalBadTckind(); 
    if (this._kind == 31)
      throw this.wrapper.cannotMarshalNative(); 
    TypeCodeReader typeCodeReader = paramTypeCodeReader.getTopLevelStream();
    if (this._kind == -1) {
      int j = paramTypeCodeReader.read_long();
      if (j > -4)
        throw this.wrapper.invalidIndirection(new Integer(j)); 
      int k = paramTypeCodeReader.getTopLevelPosition();
      int m = k - 4 + j;
      TypeCodeImpl typeCodeImpl = typeCodeReader.getTypeCodeAtPosition(m);
      if (typeCodeImpl == null)
        throw this.wrapper.indirectionNotFound(new Integer(m)); 
      setIndirectType(typeCodeImpl);
      return false;
    } 
    typeCodeReader.addTypeCodeAtPosition(this, i);
    return true;
  }
  
  void read_value_kind(InputStream paramInputStream) {
    this._kind = paramInputStream.read_long();
    if ((this._kind < 0 || this._kind > typeTable.length) && this._kind != -1)
      throw this.wrapper.cannotMarshalBadTckind(); 
    if (this._kind == 31)
      throw this.wrapper.cannotMarshalNative(); 
    if (this._kind == -1)
      throw this.wrapper.recursiveTypecodeError(); 
  }
  
  void read_value_body(InputStream paramInputStream) {
    byte b;
    TypeCodeInputStream typeCodeInputStream;
    switch (typeTable[this._kind]) {
      case 1:
        switch (this._kind) {
          case 18:
          case 27:
            this._length = paramInputStream.read_long();
            break;
          case 28:
            this._digits = paramInputStream.read_ushort();
            this._scale = paramInputStream.read_short();
            break;
        } 
        throw this.wrapper.invalidSimpleTypecode();
      case 2:
        typeCodeInputStream = TypeCodeInputStream.readEncapsulation(paramInputStream, paramInputStream.orb());
        switch (this._kind) {
          case 14:
          case 32:
            setId(typeCodeInputStream.read_string());
            this._name = typeCodeInputStream.read_string();
            break;
          case 16:
            setId(typeCodeInputStream.read_string());
            this._name = typeCodeInputStream.read_string();
            this._discriminator = new TypeCodeImpl((ORB)paramInputStream.orb());
            this._discriminator.read_value_recursive(typeCodeInputStream);
            this._defaultIndex = typeCodeInputStream.read_long();
            this._memberCount = typeCodeInputStream.read_long();
            this._unionLabels = new AnyImpl[this._memberCount];
            this._memberNames = new String[this._memberCount];
            this._memberTypes = new TypeCodeImpl[this._memberCount];
            for (b = 0; b < this._memberCount; b++) {
              this._unionLabels[b] = new AnyImpl((ORB)paramInputStream.orb());
              if (b == this._defaultIndex) {
                this._unionLabels[b].insert_octet(typeCodeInputStream.read_octet());
              } else {
                switch (realType(this._discriminator).kind().value()) {
                  case 2:
                    this._unionLabels[b].insert_short(typeCodeInputStream.read_short());
                    break;
                  case 3:
                    this._unionLabels[b].insert_long(typeCodeInputStream.read_long());
                    break;
                  case 4:
                    this._unionLabels[b].insert_ushort(typeCodeInputStream.read_short());
                    break;
                  case 5:
                    this._unionLabels[b].insert_ulong(typeCodeInputStream.read_long());
                    break;
                  case 6:
                    this._unionLabels[b].insert_float(typeCodeInputStream.read_float());
                    break;
                  case 7:
                    this._unionLabels[b].insert_double(typeCodeInputStream.read_double());
                    break;
                  case 8:
                    this._unionLabels[b].insert_boolean(typeCodeInputStream.read_boolean());
                    break;
                  case 9:
                    this._unionLabels[b].insert_char(typeCodeInputStream.read_char());
                    break;
                  case 17:
                    this._unionLabels[b].type(this._discriminator);
                    this._unionLabels[b].insert_long(typeCodeInputStream.read_long());
                    break;
                  case 23:
                    this._unionLabels[b].insert_longlong(typeCodeInputStream.read_longlong());
                    break;
                  case 24:
                    this._unionLabels[b].insert_ulonglong(typeCodeInputStream.read_longlong());
                    break;
                  case 26:
                    this._unionLabels[b].insert_wchar(typeCodeInputStream.read_wchar());
                    break;
                  default:
                    throw this.wrapper.invalidComplexTypecode();
                } 
              } 
              this._memberNames[b] = typeCodeInputStream.read_string();
              this._memberTypes[b] = new TypeCodeImpl((ORB)paramInputStream.orb());
              this._memberTypes[b].read_value_recursive(typeCodeInputStream);
              this._memberTypes[b].setParent(this);
            } 
            break;
          case 17:
            setId(typeCodeInputStream.read_string());
            this._name = typeCodeInputStream.read_string();
            this._memberCount = typeCodeInputStream.read_long();
            this._memberNames = new String[this._memberCount];
            for (b = 0; b < this._memberCount; b++)
              this._memberNames[b] = typeCodeInputStream.read_string(); 
            break;
          case 19:
            this._contentType = new TypeCodeImpl((ORB)paramInputStream.orb());
            this._contentType.read_value_recursive(typeCodeInputStream);
            this._length = typeCodeInputStream.read_long();
            break;
          case 20:
            this._contentType = new TypeCodeImpl((ORB)paramInputStream.orb());
            this._contentType.read_value_recursive(typeCodeInputStream);
            this._length = typeCodeInputStream.read_long();
            break;
          case 21:
          case 30:
            setId(typeCodeInputStream.read_string());
            this._name = typeCodeInputStream.read_string();
            this._contentType = new TypeCodeImpl((ORB)paramInputStream.orb());
            this._contentType.read_value_recursive(typeCodeInputStream);
            break;
          case 15:
          case 22:
            setId(typeCodeInputStream.read_string());
            this._name = typeCodeInputStream.read_string();
            this._memberCount = typeCodeInputStream.read_long();
            this._memberNames = new String[this._memberCount];
            this._memberTypes = new TypeCodeImpl[this._memberCount];
            for (b = 0; b < this._memberCount; b++) {
              this._memberNames[b] = typeCodeInputStream.read_string();
              this._memberTypes[b] = new TypeCodeImpl((ORB)paramInputStream.orb());
              this._memberTypes[b].read_value_recursive(typeCodeInputStream);
              this._memberTypes[b].setParent(this);
            } 
            break;
          case 29:
            setId(typeCodeInputStream.read_string());
            this._name = typeCodeInputStream.read_string();
            this._type_modifier = typeCodeInputStream.read_short();
            this._concrete_base = new TypeCodeImpl((ORB)paramInputStream.orb());
            this._concrete_base.read_value_recursive(typeCodeInputStream);
            if (this._concrete_base.kind().value() == 0)
              this._concrete_base = null; 
            this._memberCount = typeCodeInputStream.read_long();
            this._memberNames = new String[this._memberCount];
            this._memberTypes = new TypeCodeImpl[this._memberCount];
            this._memberAccess = new short[this._memberCount];
            for (b = 0; b < this._memberCount; b++) {
              this._memberNames[b] = typeCodeInputStream.read_string();
              this._memberTypes[b] = new TypeCodeImpl((ORB)paramInputStream.orb());
              this._memberTypes[b].read_value_recursive(typeCodeInputStream);
              this._memberTypes[b].setParent(this);
              this._memberAccess[b] = typeCodeInputStream.read_short();
            } 
            break;
        } 
        throw this.wrapper.invalidTypecodeKindMarshal();
    } 
  }
  
  public void write_value(OutputStream paramOutputStream) {
    if (paramOutputStream instanceof TypeCodeOutputStream) {
      write_value((TypeCodeOutputStream)paramOutputStream);
    } else {
      TypeCodeOutputStream typeCodeOutputStream = null;
      if (this.outBuffer == null) {
        typeCodeOutputStream = TypeCodeOutputStream.wrapOutputStream(paramOutputStream);
        write_value(typeCodeOutputStream);
        if (this.cachingEnabled)
          this.outBuffer = typeCodeOutputStream.getTypeCodeBuffer(); 
      } 
      if (this.cachingEnabled && this.outBuffer != null) {
        paramOutputStream.write_long(this._kind);
        paramOutputStream.write_octet_array(this.outBuffer, 0, this.outBuffer.length);
      } else {
        typeCodeOutputStream.writeRawBuffer(paramOutputStream, this._kind);
      } 
    } 
  }
  
  public void write_value(TypeCodeOutputStream paramTypeCodeOutputStream) {
    byte b;
    TypeCodeOutputStream typeCodeOutputStream2;
    if (this._kind == 31)
      throw this.wrapper.cannotMarshalNative(); 
    TypeCodeOutputStream typeCodeOutputStream1 = paramTypeCodeOutputStream.getTopLevelStream();
    if (this._kind == -1) {
      int i = typeCodeOutputStream1.getPositionForID(this._id);
      int j = paramTypeCodeOutputStream.getTopLevelPosition();
      paramTypeCodeOutputStream.writeIndirection(-1, i);
      return;
    } 
    paramTypeCodeOutputStream.write_long(this._kind);
    typeCodeOutputStream1.addIDAtPosition(this._id, paramTypeCodeOutputStream.getTopLevelPosition() - 4);
    switch (typeTable[this._kind]) {
      case 1:
        switch (this._kind) {
          case 18:
          case 27:
            paramTypeCodeOutputStream.write_long(this._length);
            break;
          case 28:
            paramTypeCodeOutputStream.write_ushort(this._digits);
            paramTypeCodeOutputStream.write_short(this._scale);
            break;
        } 
        throw this.wrapper.invalidSimpleTypecode();
      case 2:
        typeCodeOutputStream2 = paramTypeCodeOutputStream.createEncapsulation(paramTypeCodeOutputStream.orb());
        switch (this._kind) {
          case 14:
          case 32:
            typeCodeOutputStream2.write_string(this._id);
            typeCodeOutputStream2.write_string(this._name);
            break;
          case 16:
            typeCodeOutputStream2.write_string(this._id);
            typeCodeOutputStream2.write_string(this._name);
            this._discriminator.write_value(typeCodeOutputStream2);
            typeCodeOutputStream2.write_long(this._defaultIndex);
            typeCodeOutputStream2.write_long(this._memberCount);
            for (b = 0; b < this._memberCount; b++) {
              if (b == this._defaultIndex) {
                typeCodeOutputStream2.write_octet(this._unionLabels[b].extract_octet());
              } else {
                switch (realType(this._discriminator).kind().value()) {
                  case 2:
                    typeCodeOutputStream2.write_short(this._unionLabels[b].extract_short());
                    break;
                  case 3:
                    typeCodeOutputStream2.write_long(this._unionLabels[b].extract_long());
                    break;
                  case 4:
                    typeCodeOutputStream2.write_short(this._unionLabels[b].extract_ushort());
                    break;
                  case 5:
                    typeCodeOutputStream2.write_long(this._unionLabels[b].extract_ulong());
                    break;
                  case 6:
                    typeCodeOutputStream2.write_float(this._unionLabels[b].extract_float());
                    break;
                  case 7:
                    typeCodeOutputStream2.write_double(this._unionLabels[b].extract_double());
                    break;
                  case 8:
                    typeCodeOutputStream2.write_boolean(this._unionLabels[b].extract_boolean());
                    break;
                  case 9:
                    typeCodeOutputStream2.write_char(this._unionLabels[b].extract_char());
                    break;
                  case 17:
                    typeCodeOutputStream2.write_long(this._unionLabels[b].extract_long());
                    break;
                  case 23:
                    typeCodeOutputStream2.write_longlong(this._unionLabels[b].extract_longlong());
                    break;
                  case 24:
                    typeCodeOutputStream2.write_longlong(this._unionLabels[b].extract_ulonglong());
                    break;
                  case 26:
                    typeCodeOutputStream2.write_wchar(this._unionLabels[b].extract_wchar());
                    break;
                  default:
                    throw this.wrapper.invalidComplexTypecode();
                } 
              } 
              typeCodeOutputStream2.write_string(this._memberNames[b]);
              this._memberTypes[b].write_value(typeCodeOutputStream2);
            } 
            break;
          case 17:
            typeCodeOutputStream2.write_string(this._id);
            typeCodeOutputStream2.write_string(this._name);
            typeCodeOutputStream2.write_long(this._memberCount);
            for (b = 0; b < this._memberCount; b++)
              typeCodeOutputStream2.write_string(this._memberNames[b]); 
            break;
          case 19:
            lazy_content_type().write_value(typeCodeOutputStream2);
            typeCodeOutputStream2.write_long(this._length);
            break;
          case 20:
            this._contentType.write_value(typeCodeOutputStream2);
            typeCodeOutputStream2.write_long(this._length);
            break;
          case 21:
          case 30:
            typeCodeOutputStream2.write_string(this._id);
            typeCodeOutputStream2.write_string(this._name);
            this._contentType.write_value(typeCodeOutputStream2);
            break;
          case 15:
          case 22:
            typeCodeOutputStream2.write_string(this._id);
            typeCodeOutputStream2.write_string(this._name);
            typeCodeOutputStream2.write_long(this._memberCount);
            for (b = 0; b < this._memberCount; b++) {
              typeCodeOutputStream2.write_string(this._memberNames[b]);
              this._memberTypes[b].write_value(typeCodeOutputStream2);
            } 
            break;
          case 29:
            typeCodeOutputStream2.write_string(this._id);
            typeCodeOutputStream2.write_string(this._name);
            typeCodeOutputStream2.write_short(this._type_modifier);
            if (this._concrete_base == null) {
              this._orb.get_primitive_tc(0).write_value(typeCodeOutputStream2);
            } else {
              this._concrete_base.write_value(typeCodeOutputStream2);
            } 
            typeCodeOutputStream2.write_long(this._memberCount);
            for (b = 0; b < this._memberCount; b++) {
              typeCodeOutputStream2.write_string(this._memberNames[b]);
              this._memberTypes[b].write_value(typeCodeOutputStream2);
              typeCodeOutputStream2.write_short(this._memberAccess[b]);
            } 
            break;
          default:
            throw this.wrapper.invalidTypecodeKindMarshal();
        } 
        typeCodeOutputStream2.writeOctetSequenceTo(paramTypeCodeOutputStream);
        break;
    } 
  }
  
  protected void copy(InputStream paramInputStream, OutputStream paramOutputStream) {
    char c1;
    float f;
    boolean bool;
    TypeCodeImpl typeCodeImpl;
    double d;
    int j;
    short s;
    int m;
    int k;
    char c2;
    long l;
    AnyImpl anyImpl;
    int i;
    Any any;
    String str;
    byte b;
    switch (this._kind) {
      case 0:
      case 1:
      case 31:
      case 32:
        return;
      case 2:
      case 4:
        paramOutputStream.write_short(paramInputStream.read_short());
      case 3:
      case 5:
        paramOutputStream.write_long(paramInputStream.read_long());
      case 6:
        paramOutputStream.write_float(paramInputStream.read_float());
      case 7:
        paramOutputStream.write_double(paramInputStream.read_double());
      case 23:
      case 24:
        paramOutputStream.write_longlong(paramInputStream.read_longlong());
      case 25:
        throw this.wrapper.tkLongDoubleNotSupported();
      case 8:
        paramOutputStream.write_boolean(paramInputStream.read_boolean());
      case 9:
        paramOutputStream.write_char(paramInputStream.read_char());
      case 26:
        paramOutputStream.write_wchar(paramInputStream.read_wchar());
      case 10:
        paramOutputStream.write_octet(paramInputStream.read_octet());
      case 18:
        str = paramInputStream.read_string();
        if (this._length != 0 && str.length() > this._length)
          throw this.wrapper.badStringBounds(new Integer(str.length()), new Integer(this._length)); 
        paramOutputStream.write_string(str);
      case 27:
        str = paramInputStream.read_wstring();
        if (this._length != 0 && str.length() > this._length)
          throw this.wrapper.badStringBounds(new Integer(str.length()), new Integer(this._length)); 
        paramOutputStream.write_wstring(str);
      case 28:
        paramOutputStream.write_ushort(paramInputStream.read_ushort());
        paramOutputStream.write_short(paramInputStream.read_short());
      case 11:
        any = ((CDRInputStream)paramInputStream).orb().create_any();
        typeCodeImpl = new TypeCodeImpl((ORB)paramOutputStream.orb());
        typeCodeImpl.read_value((InputStream)paramInputStream);
        typeCodeImpl.write_value((OutputStream)paramOutputStream);
        any.read_value(paramInputStream, typeCodeImpl);
        any.write_value(paramOutputStream);
      case 12:
        paramOutputStream.write_TypeCode(paramInputStream.read_TypeCode());
      case 13:
        paramOutputStream.write_Principal(paramInputStream.read_Principal());
      case 14:
        paramOutputStream.write_Object(paramInputStream.read_Object());
      case 22:
        paramOutputStream.write_string(paramInputStream.read_string());
      case 15:
      case 29:
        for (b = 0; b < this._memberTypes.length; b++)
          this._memberTypes[b].copy(paramInputStream, paramOutputStream); 
      case 16:
        anyImpl = new AnyImpl((ORB)paramInputStream.orb());
        switch (realType(this._discriminator).kind().value()) {
          case 2:
            s = paramInputStream.read_short();
            anyImpl.insert_short(s);
            paramOutputStream.write_short(s);
            break;
          case 3:
            m = paramInputStream.read_long();
            anyImpl.insert_long(m);
            paramOutputStream.write_long(m);
            break;
          case 4:
            m = paramInputStream.read_short();
            anyImpl.insert_ushort(m);
            paramOutputStream.write_short(m);
            break;
          case 5:
            k = paramInputStream.read_long();
            anyImpl.insert_ulong(k);
            paramOutputStream.write_long(k);
            break;
          case 6:
            f = paramInputStream.read_float();
            anyImpl.insert_float(f);
            paramOutputStream.write_float(f);
            break;
          case 7:
            d = paramInputStream.read_double();
            anyImpl.insert_double(d);
            paramOutputStream.write_double(d);
            break;
          case 8:
            bool = paramInputStream.read_boolean();
            anyImpl.insert_boolean(bool);
            paramOutputStream.write_boolean(bool);
            break;
          case 9:
            c2 = paramInputStream.read_char();
            anyImpl.insert_char(c2);
            paramOutputStream.write_char(c2);
            break;
          case 17:
            j = paramInputStream.read_long();
            anyImpl.type(this._discriminator);
            anyImpl.insert_long(j);
            paramOutputStream.write_long(j);
            break;
          case 23:
            l = paramInputStream.read_longlong();
            anyImpl.insert_longlong(l);
            paramOutputStream.write_longlong(l);
            break;
          case 24:
            l = paramInputStream.read_longlong();
            anyImpl.insert_ulonglong(l);
            paramOutputStream.write_longlong(l);
            break;
          case 26:
            c1 = paramInputStream.read_wchar();
            anyImpl.insert_wchar(c1);
            paramOutputStream.write_wchar(c1);
            break;
          default:
            throw this.wrapper.illegalUnionDiscriminatorType();
        } 
        for (c1 = Character.MIN_VALUE; c1 < this._unionLabels.length; c1++) {
          if (anyImpl.equal(this._unionLabels[c1])) {
            this._memberTypes[c1].copy(paramInputStream, paramOutputStream);
            break;
          } 
        } 
        if (c1 == this._unionLabels.length && this._defaultIndex != -1)
          this._memberTypes[this._defaultIndex].copy(paramInputStream, paramOutputStream); 
      case 17:
        paramOutputStream.write_long(paramInputStream.read_long());
      case 19:
        i = paramInputStream.read_long();
        if (this._length != 0 && i > this._length)
          throw this.wrapper.badSequenceBounds(new Integer(i), new Integer(this._length)); 
        paramOutputStream.write_long(i);
        lazy_content_type();
        for (c1 = Character.MIN_VALUE; c1 < i; c1++)
          this._contentType.copy(paramInputStream, paramOutputStream); 
      case 20:
        for (c1 = Character.MIN_VALUE; c1 < this._length; c1++)
          this._contentType.copy(paramInputStream, paramOutputStream); 
      case 21:
      case 30:
        this._contentType.copy(paramInputStream, paramOutputStream);
      case -1:
        indirectType().copy(paramInputStream, paramOutputStream);
    } 
    throw this.wrapper.invalidTypecodeKindMarshal();
  }
  
  protected static short digits(BigDecimal paramBigDecimal) {
    if (paramBigDecimal == null)
      return 0; 
    short s = (short)paramBigDecimal.unscaledValue().toString().length();
    if (paramBigDecimal.signum() == -1)
      s = (short)(s - 1); 
    return s;
  }
  
  protected static short scale(BigDecimal paramBigDecimal) { return (paramBigDecimal == null) ? 0 : (short)paramBigDecimal.scale(); }
  
  int currentUnionMemberIndex(Any paramAny) throws BadKind {
    if (this._kind != 16)
      throw new BadKind(); 
    try {
      for (byte b = 0; b < member_count(); b++) {
        if (member_label(b).equal(paramAny))
          return b; 
      } 
      if (this._defaultIndex != -1)
        return this._defaultIndex; 
    } catch (BadKind badKind) {
    
    } catch (Bounds bounds) {}
    return -1;
  }
  
  public String description() throws BadKind { return "TypeCodeImpl with kind " + this._kind + " and id " + this._id; }
  
  public String toString() throws BadKind {
    ByteArrayOutputStream byteArrayOutputStream = new ByteArrayOutputStream(1024);
    PrintStream printStream = new PrintStream(byteArrayOutputStream, true);
    printStream(printStream);
    return super.toString() + " =\n" + byteArrayOutputStream.toString();
  }
  
  public void printStream(PrintStream paramPrintStream) { printStream(paramPrintStream, 0); }
  
  private void printStream(PrintStream paramPrintStream, int paramInt) {
    byte b;
    if (this._kind == -1) {
      paramPrintStream.print("indirect " + this._id);
      return;
    } 
    switch (this._kind) {
      case 0:
      case 1:
      case 2:
      case 3:
      case 4:
      case 5:
      case 6:
      case 7:
      case 8:
      case 9:
      case 10:
      case 11:
      case 12:
      case 13:
      case 14:
      case 23:
      case 24:
      case 25:
      case 26:
      case 31:
        paramPrintStream.print(kindNames[this._kind] + " " + this._name);
        return;
      case 15:
      case 22:
      case 29:
        paramPrintStream.println(kindNames[this._kind] + " " + this._name + " = {");
        for (b = 0; b < this._memberCount; b++) {
          paramPrintStream.print(indent(paramInt + 1));
          if (this._memberTypes[b] != null) {
            this._memberTypes[b].printStream(paramPrintStream, paramInt + 1);
          } else {
            paramPrintStream.print("<unknown type>");
          } 
          paramPrintStream.println(" " + this._memberNames[b] + ";");
        } 
        paramPrintStream.print(indent(paramInt) + "}");
        return;
      case 16:
        paramPrintStream.print("union " + this._name + "...");
        return;
      case 17:
        paramPrintStream.print("enum " + this._name + "...");
        return;
      case 18:
        if (this._length == 0) {
          paramPrintStream.print("unbounded string " + this._name);
        } else {
          paramPrintStream.print("bounded string(" + this._length + ") " + this._name);
        } 
        return;
      case 19:
      case 20:
        paramPrintStream.println(kindNames[this._kind] + "[" + this._length + "] " + this._name + " = {");
        paramPrintStream.print(indent(paramInt + 1));
        if (lazy_content_type() != null)
          lazy_content_type().printStream(paramPrintStream, paramInt + 1); 
        paramPrintStream.println(indent(paramInt) + "}");
        return;
      case 21:
        paramPrintStream.print("alias " + this._name + " = " + ((this._contentType != null) ? this._contentType._name : "<unresolved>"));
        return;
      case 27:
        paramPrintStream.print("wstring[" + this._length + "] " + this._name);
        return;
      case 28:
        paramPrintStream.print("fixed(" + this._digits + ", " + this._scale + ") " + this._name);
        return;
      case 30:
        paramPrintStream.print("valueBox " + this._name + "...");
        return;
      case 32:
        paramPrintStream.print("abstractInterface " + this._name + "...");
        return;
    } 
    paramPrintStream.print("<unknown type>");
  }
  
  private String indent(int paramInt) throws BadKind, Bounds {
    String str = "";
    for (byte b = 0; b < paramInt; b++)
      str = str + "  "; 
    return str;
  }
  
  protected void setCaching(boolean paramBoolean) {
    this.cachingEnabled = paramBoolean;
    if (!paramBoolean)
      this.outBuffer = null; 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\corba\se\impl\corba\TypeCodeImpl.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */