package com.sun.java.util.jar.pack;

import java.util.Arrays;
import java.util.List;

class Constants {
  public static final int JAVA_MAGIC = -889275714;
  
  public static final Package.Version JAVA_MIN_CLASS_VERSION;
  
  public static final Package.Version JAVA5_MAX_CLASS_VERSION;
  
  public static final Package.Version JAVA6_MAX_CLASS_VERSION;
  
  public static final Package.Version JAVA7_MAX_CLASS_VERSION;
  
  public static final Package.Version JAVA8_MAX_CLASS_VERSION;
  
  public static final int JAVA_PACKAGE_MAGIC = -889270259;
  
  public static final Package.Version JAVA5_PACKAGE_VERSION;
  
  public static final Package.Version JAVA6_PACKAGE_VERSION;
  
  public static final Package.Version JAVA7_PACKAGE_VERSION;
  
  public static final Package.Version JAVA8_PACKAGE_VERSION = (JAVA7_PACKAGE_VERSION = (JAVA6_PACKAGE_VERSION = (JAVA5_PACKAGE_VERSION = (JAVA8_MAX_CLASS_VERSION = (JAVA7_MAX_CLASS_VERSION = (JAVA6_MAX_CLASS_VERSION = (JAVA5_MAX_CLASS_VERSION = (JAVA_MIN_CLASS_VERSION = Package.Version.of(45, 3)).of(49, 0)).of(50, 0)).of(51, 0)).of(52, 0)).of(150, 7)).of(160, 1)).of(170, 1)).of(171, 0);
  
  public static final Package.Version JAVA_MAX_CLASS_VERSION = JAVA8_MAX_CLASS_VERSION;
  
  public static final Package.Version MAX_PACKAGE_VERSION = JAVA7_PACKAGE_VERSION;
  
  public static final int CONSTANT_POOL_INDEX_LIMIT = 65536;
  
  public static final int CONSTANT_POOL_NARROW_LIMIT = 256;
  
  public static final String JAVA_SIGNATURE_CHARS = "BSCIJFDZLV([";
  
  public static final byte CONSTANT_Utf8 = 1;
  
  public static final byte CONSTANT_unused2 = 2;
  
  public static final byte CONSTANT_Integer = 3;
  
  public static final byte CONSTANT_Float = 4;
  
  public static final byte CONSTANT_Long = 5;
  
  public static final byte CONSTANT_Double = 6;
  
  public static final byte CONSTANT_Class = 7;
  
  public static final byte CONSTANT_String = 8;
  
  public static final byte CONSTANT_Fieldref = 9;
  
  public static final byte CONSTANT_Methodref = 10;
  
  public static final byte CONSTANT_InterfaceMethodref = 11;
  
  public static final byte CONSTANT_NameandType = 12;
  
  public static final byte CONSTANT_unused13 = 13;
  
  public static final byte CONSTANT_unused14 = 14;
  
  public static final byte CONSTANT_MethodHandle = 15;
  
  public static final byte CONSTANT_MethodType = 16;
  
  public static final byte CONSTANT_unused17 = 17;
  
  public static final byte CONSTANT_InvokeDynamic = 18;
  
  public static final byte CONSTANT_None = 0;
  
  public static final byte CONSTANT_Signature = 13;
  
  public static final byte CONSTANT_BootstrapMethod = 17;
  
  public static final byte CONSTANT_Limit = 19;
  
  public static final byte CONSTANT_All = 50;
  
  public static final byte CONSTANT_LoadableValue = 51;
  
  public static final byte CONSTANT_AnyMember = 52;
  
  public static final byte CONSTANT_FieldSpecific = 53;
  
  public static final byte CONSTANT_GroupFirst = 50;
  
  public static final byte CONSTANT_GroupLimit = 54;
  
  public static final byte REF_getField = 1;
  
  public static final byte REF_getStatic = 2;
  
  public static final byte REF_putField = 3;
  
  public static final byte REF_putStatic = 4;
  
  public static final byte REF_invokeVirtual = 5;
  
  public static final byte REF_invokeStatic = 6;
  
  public static final byte REF_invokeSpecial = 7;
  
  public static final byte REF_newInvokeSpecial = 8;
  
  public static final byte REF_invokeInterface = 9;
  
  public static final int ACC_IC_LONG_FORM = 65536;
  
  public static final int ATTR_CONTEXT_CLASS = 0;
  
  public static final int ATTR_CONTEXT_FIELD = 1;
  
  public static final int ATTR_CONTEXT_METHOD = 2;
  
  public static final int ATTR_CONTEXT_CODE = 3;
  
  public static final int ATTR_CONTEXT_LIMIT = 4;
  
  public static final String[] ATTR_CONTEXT_NAME = { "class", "field", "method", "code" };
  
  public static final int X_ATTR_OVERFLOW = 16;
  
  public static final int CLASS_ATTR_SourceFile = 17;
  
  public static final int METHOD_ATTR_Code = 17;
  
  public static final int FIELD_ATTR_ConstantValue = 17;
  
  public static final int CLASS_ATTR_EnclosingMethod = 18;
  
  public static final int METHOD_ATTR_Exceptions = 18;
  
  public static final int X_ATTR_Signature = 19;
  
  public static final int X_ATTR_Deprecated = 20;
  
  public static final int X_ATTR_RuntimeVisibleAnnotations = 21;
  
  public static final int X_ATTR_RuntimeInvisibleAnnotations = 22;
  
  public static final int METHOD_ATTR_RuntimeVisibleParameterAnnotations = 23;
  
  public static final int CLASS_ATTR_InnerClasses = 23;
  
  public static final int METHOD_ATTR_RuntimeInvisibleParameterAnnotations = 24;
  
  public static final int CLASS_ATTR_ClassFile_version = 24;
  
  public static final int METHOD_ATTR_AnnotationDefault = 25;
  
  public static final int METHOD_ATTR_MethodParameters = 26;
  
  public static final int X_ATTR_RuntimeVisibleTypeAnnotations = 27;
  
  public static final int X_ATTR_RuntimeInvisibleTypeAnnotations = 28;
  
  public static final int CODE_ATTR_StackMapTable = 0;
  
  public static final int CODE_ATTR_LineNumberTable = 1;
  
  public static final int CODE_ATTR_LocalVariableTable = 2;
  
  public static final int CODE_ATTR_LocalVariableTypeTable = 3;
  
  public static final int FO_DEFLATE_HINT = 1;
  
  public static final int FO_IS_CLASS_STUB = 2;
  
  public static final int AO_HAVE_SPECIAL_FORMATS = 1;
  
  public static final int AO_HAVE_CP_NUMBERS = 2;
  
  public static final int AO_HAVE_ALL_CODE_FLAGS = 4;
  
  public static final int AO_HAVE_CP_EXTRAS = 8;
  
  public static final int AO_HAVE_FILE_HEADERS = 16;
  
  public static final int AO_DEFLATE_HINT = 32;
  
  public static final int AO_HAVE_FILE_MODTIME = 64;
  
  public static final int AO_HAVE_FILE_OPTIONS = 128;
  
  public static final int AO_HAVE_FILE_SIZE_HI = 256;
  
  public static final int AO_HAVE_CLASS_FLAGS_HI = 512;
  
  public static final int AO_HAVE_FIELD_FLAGS_HI = 1024;
  
  public static final int AO_HAVE_METHOD_FLAGS_HI = 2048;
  
  public static final int AO_HAVE_CODE_FLAGS_HI = 4096;
  
  public static final int AO_UNUSED_MBZ = -8192;
  
  public static final int LG_AO_HAVE_XXX_FLAGS_HI = 9;
  
  static final int VRM_CLASSIC = 0;
  
  static final int VRM_PACKAGE = 1;
  
  public static final int NO_MODTIME = 0;
  
  public static final int[] noInts = new int[0];
  
  public static final byte[] noBytes = new byte[0];
  
  public static final Object[] noValues = new Object[0];
  
  public static final String[] noStrings = new String[0];
  
  public static final List<Object> emptyList = Arrays.asList(noValues);
  
  public static final int _meta_default = 0;
  
  public static final int _meta_canon_min = 1;
  
  public static final int _meta_canon_max = 115;
  
  public static final int _meta_arb = 116;
  
  public static final int _meta_run = 117;
  
  public static final int _meta_pop = 141;
  
  public static final int _meta_limit = 189;
  
  public static final int _nop = 0;
  
  public static final int _aconst_null = 1;
  
  public static final int _iconst_m1 = 2;
  
  public static final int _iconst_0 = 3;
  
  public static final int _iconst_1 = 4;
  
  public static final int _iconst_2 = 5;
  
  public static final int _iconst_3 = 6;
  
  public static final int _iconst_4 = 7;
  
  public static final int _iconst_5 = 8;
  
  public static final int _lconst_0 = 9;
  
  public static final int _lconst_1 = 10;
  
  public static final int _fconst_0 = 11;
  
  public static final int _fconst_1 = 12;
  
  public static final int _fconst_2 = 13;
  
  public static final int _dconst_0 = 14;
  
  public static final int _dconst_1 = 15;
  
  public static final int _bipush = 16;
  
  public static final int _sipush = 17;
  
  public static final int _ldc = 18;
  
  public static final int _ldc_w = 19;
  
  public static final int _ldc2_w = 20;
  
  public static final int _iload = 21;
  
  public static final int _lload = 22;
  
  public static final int _fload = 23;
  
  public static final int _dload = 24;
  
  public static final int _aload = 25;
  
  public static final int _iload_0 = 26;
  
  public static final int _iload_1 = 27;
  
  public static final int _iload_2 = 28;
  
  public static final int _iload_3 = 29;
  
  public static final int _lload_0 = 30;
  
  public static final int _lload_1 = 31;
  
  public static final int _lload_2 = 32;
  
  public static final int _lload_3 = 33;
  
  public static final int _fload_0 = 34;
  
  public static final int _fload_1 = 35;
  
  public static final int _fload_2 = 36;
  
  public static final int _fload_3 = 37;
  
  public static final int _dload_0 = 38;
  
  public static final int _dload_1 = 39;
  
  public static final int _dload_2 = 40;
  
  public static final int _dload_3 = 41;
  
  public static final int _aload_0 = 42;
  
  public static final int _aload_1 = 43;
  
  public static final int _aload_2 = 44;
  
  public static final int _aload_3 = 45;
  
  public static final int _iaload = 46;
  
  public static final int _laload = 47;
  
  public static final int _faload = 48;
  
  public static final int _daload = 49;
  
  public static final int _aaload = 50;
  
  public static final int _baload = 51;
  
  public static final int _caload = 52;
  
  public static final int _saload = 53;
  
  public static final int _istore = 54;
  
  public static final int _lstore = 55;
  
  public static final int _fstore = 56;
  
  public static final int _dstore = 57;
  
  public static final int _astore = 58;
  
  public static final int _istore_0 = 59;
  
  public static final int _istore_1 = 60;
  
  public static final int _istore_2 = 61;
  
  public static final int _istore_3 = 62;
  
  public static final int _lstore_0 = 63;
  
  public static final int _lstore_1 = 64;
  
  public static final int _lstore_2 = 65;
  
  public static final int _lstore_3 = 66;
  
  public static final int _fstore_0 = 67;
  
  public static final int _fstore_1 = 68;
  
  public static final int _fstore_2 = 69;
  
  public static final int _fstore_3 = 70;
  
  public static final int _dstore_0 = 71;
  
  public static final int _dstore_1 = 72;
  
  public static final int _dstore_2 = 73;
  
  public static final int _dstore_3 = 74;
  
  public static final int _astore_0 = 75;
  
  public static final int _astore_1 = 76;
  
  public static final int _astore_2 = 77;
  
  public static final int _astore_3 = 78;
  
  public static final int _iastore = 79;
  
  public static final int _lastore = 80;
  
  public static final int _fastore = 81;
  
  public static final int _dastore = 82;
  
  public static final int _aastore = 83;
  
  public static final int _bastore = 84;
  
  public static final int _castore = 85;
  
  public static final int _sastore = 86;
  
  public static final int _pop = 87;
  
  public static final int _pop2 = 88;
  
  public static final int _dup = 89;
  
  public static final int _dup_x1 = 90;
  
  public static final int _dup_x2 = 91;
  
  public static final int _dup2 = 92;
  
  public static final int _dup2_x1 = 93;
  
  public static final int _dup2_x2 = 94;
  
  public static final int _swap = 95;
  
  public static final int _iadd = 96;
  
  public static final int _ladd = 97;
  
  public static final int _fadd = 98;
  
  public static final int _dadd = 99;
  
  public static final int _isub = 100;
  
  public static final int _lsub = 101;
  
  public static final int _fsub = 102;
  
  public static final int _dsub = 103;
  
  public static final int _imul = 104;
  
  public static final int _lmul = 105;
  
  public static final int _fmul = 106;
  
  public static final int _dmul = 107;
  
  public static final int _idiv = 108;
  
  public static final int _ldiv = 109;
  
  public static final int _fdiv = 110;
  
  public static final int _ddiv = 111;
  
  public static final int _irem = 112;
  
  public static final int _lrem = 113;
  
  public static final int _frem = 114;
  
  public static final int _drem = 115;
  
  public static final int _ineg = 116;
  
  public static final int _lneg = 117;
  
  public static final int _fneg = 118;
  
  public static final int _dneg = 119;
  
  public static final int _ishl = 120;
  
  public static final int _lshl = 121;
  
  public static final int _ishr = 122;
  
  public static final int _lshr = 123;
  
  public static final int _iushr = 124;
  
  public static final int _lushr = 125;
  
  public static final int _iand = 126;
  
  public static final int _land = 127;
  
  public static final int _ior = 128;
  
  public static final int _lor = 129;
  
  public static final int _ixor = 130;
  
  public static final int _lxor = 131;
  
  public static final int _iinc = 132;
  
  public static final int _i2l = 133;
  
  public static final int _i2f = 134;
  
  public static final int _i2d = 135;
  
  public static final int _l2i = 136;
  
  public static final int _l2f = 137;
  
  public static final int _l2d = 138;
  
  public static final int _f2i = 139;
  
  public static final int _f2l = 140;
  
  public static final int _f2d = 141;
  
  public static final int _d2i = 142;
  
  public static final int _d2l = 143;
  
  public static final int _d2f = 144;
  
  public static final int _i2b = 145;
  
  public static final int _i2c = 146;
  
  public static final int _i2s = 147;
  
  public static final int _lcmp = 148;
  
  public static final int _fcmpl = 149;
  
  public static final int _fcmpg = 150;
  
  public static final int _dcmpl = 151;
  
  public static final int _dcmpg = 152;
  
  public static final int _ifeq = 153;
  
  public static final int _ifne = 154;
  
  public static final int _iflt = 155;
  
  public static final int _ifge = 156;
  
  public static final int _ifgt = 157;
  
  public static final int _ifle = 158;
  
  public static final int _if_icmpeq = 159;
  
  public static final int _if_icmpne = 160;
  
  public static final int _if_icmplt = 161;
  
  public static final int _if_icmpge = 162;
  
  public static final int _if_icmpgt = 163;
  
  public static final int _if_icmple = 164;
  
  public static final int _if_acmpeq = 165;
  
  public static final int _if_acmpne = 166;
  
  public static final int _goto = 167;
  
  public static final int _jsr = 168;
  
  public static final int _ret = 169;
  
  public static final int _tableswitch = 170;
  
  public static final int _lookupswitch = 171;
  
  public static final int _ireturn = 172;
  
  public static final int _lreturn = 173;
  
  public static final int _freturn = 174;
  
  public static final int _dreturn = 175;
  
  public static final int _areturn = 176;
  
  public static final int _return = 177;
  
  public static final int _getstatic = 178;
  
  public static final int _putstatic = 179;
  
  public static final int _getfield = 180;
  
  public static final int _putfield = 181;
  
  public static final int _invokevirtual = 182;
  
  public static final int _invokespecial = 183;
  
  public static final int _invokestatic = 184;
  
  public static final int _invokeinterface = 185;
  
  public static final int _invokedynamic = 186;
  
  public static final int _new = 187;
  
  public static final int _newarray = 188;
  
  public static final int _anewarray = 189;
  
  public static final int _arraylength = 190;
  
  public static final int _athrow = 191;
  
  public static final int _checkcast = 192;
  
  public static final int _instanceof = 193;
  
  public static final int _monitorenter = 194;
  
  public static final int _monitorexit = 195;
  
  public static final int _wide = 196;
  
  public static final int _multianewarray = 197;
  
  public static final int _ifnull = 198;
  
  public static final int _ifnonnull = 199;
  
  public static final int _goto_w = 200;
  
  public static final int _jsr_w = 201;
  
  public static final int _bytecode_limit = 202;
  
  public static final int _end_marker = 255;
  
  public static final int _byte_escape = 254;
  
  public static final int _ref_escape = 253;
  
  public static final int _first_linker_op = 178;
  
  public static final int _last_linker_op = 184;
  
  public static final int _num_linker_ops = 7;
  
  public static final int _self_linker_op = 202;
  
  public static final int _self_linker_aload_flag = 7;
  
  public static final int _self_linker_super_flag = 14;
  
  public static final int _self_linker_limit = 230;
  
  public static final int _invokeinit_op = 230;
  
  public static final int _invokeinit_self_option = 0;
  
  public static final int _invokeinit_super_option = 1;
  
  public static final int _invokeinit_new_option = 2;
  
  public static final int _invokeinit_limit = 233;
  
  public static final int _pseudo_instruction_limit = 233;
  
  public static final int _xldc_op = 233;
  
  public static final int _sldc = 18;
  
  public static final int _cldc = 233;
  
  public static final int _ildc = 234;
  
  public static final int _fldc = 235;
  
  public static final int _sldc_w = 19;
  
  public static final int _cldc_w = 236;
  
  public static final int _ildc_w = 237;
  
  public static final int _fldc_w = 238;
  
  public static final int _lldc2_w = 20;
  
  public static final int _dldc2_w = 239;
  
  public static final int _qldc = 240;
  
  public static final int _qldc_w = 241;
  
  public static final int _xldc_limit = 242;
  
  public static final int _invoke_int_op = 242;
  
  public static final int _invokespecial_int = 242;
  
  public static final int _invokestatic_int = 243;
  
  public static final int _invoke_int_limit = 244;
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\jav\\util\jar\pack\Constants.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */