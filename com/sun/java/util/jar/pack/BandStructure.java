package com.sun.java.util.jar.pack;

import java.io.BufferedOutputStream;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.EOFException;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FilterInputStream;
import java.io.FilterOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.PrintStream;
import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

abstract class BandStructure {
  static final int MAX_EFFORT = 9;
  
  static final int MIN_EFFORT = 1;
  
  static final int DEFAULT_EFFORT = 5;
  
  PropMap p200 = Utils.currentPropMap();
  
  int verbose = this.p200.getInteger("com.sun.java.util.jar.pack.verbose");
  
  int effort = this.p200.getInteger("pack.effort");
  
  boolean optDumpBands;
  
  boolean optDebugBands;
  
  boolean optVaryCodings;
  
  boolean optBigStrings;
  
  private Package.Version highestClassVersion;
  
  private final boolean isReader;
  
  static final Coding BYTE1;
  
  static final Coding CHAR3;
  
  static final Coding BCI5;
  
  static final Coding BRANCH5;
  
  static final Coding UNSIGNED5 = (BRANCH5 = (BCI5 = (CHAR3 = (BYTE1 = Coding.of(1, 256)).of(3, 128)).of(5, 4)).of(5, 4, 2)).of(5, 64);
  
  static final Coding UDELTA5;
  
  static final Coding SIGNED5 = (UDELTA5 = UNSIGNED5.getDeltaCoding()).of(5, 64, 1);
  
  static final Coding DELTA5;
  
  static final Coding MDELTA5 = (DELTA5 = SIGNED5.getDeltaCoding()).of(5, 64, 2).getDeltaCoding();
  
  private static final Coding[] basicCodings = { 
      null, null, null, null, null, null, null, null, null, null, 
      null, null, null, null, null, null, null, null, null, null, 
      null, null, null, null, null, null, null, null, null, null, 
      null, null, null, null, null, null, null, null, null, null, 
      null, null, null, null, null, null, null, null, null, null, 
      null, null, null, null, null, null, null, null, null, null, 
      null, null, null, null, null, null, null, null, null, null, 
      null, null, null, null, null, null, null, null, null, null, 
      null, null, null, null, null, null, null, null, null, null, 
      null, null, null, null, null, null, null, null, null, null, 
      null, null, null, null, null, null, null, null, null, null, 
      null, null, null, null, null, (new Coding[117][114] = (new Coding[117][113] = (new Coding[117][112] = (new Coding[117][111] = (new Coding[117][110] = (new Coding[117][109] = (new Coding[117][108] = (new Coding[117][107] = (new Coding[117][106] = (new Coding[117][105] = (new Coding[117][104] = (new Coding[117][103] = (new Coding[117][102] = (new Coding[117][101] = (new Coding[117][100] = (new Coding[117][99] = (new Coding[117][98] = (new Coding[117][97] = (new Coding[117][96] = (new Coding[117][95] = (new Coding[117][94] = (new Coding[117][93] = (new Coding[117][92] = (new Coding[117][91] = (new Coding[117][90] = (new Coding[117][89] = (new Coding[117][88] = (new Coding[117][87] = (new Coding[117][86] = (new Coding[117][85] = (new Coding[117][84] = (new Coding[117][83] = (new Coding[117][82] = (new Coding[117][81] = (new Coding[117][80] = (new Coding[117][79] = (new Coding[117][78] = (new Coding[117][77] = (new Coding[117][76] = (new Coding[117][75] = (new Coding[117][74] = (new Coding[117][73] = (new Coding[117][72] = (new Coding[117][71] = (new Coding[117][70] = (new Coding[117][69] = (new Coding[117][68] = (new Coding[117][67] = (new Coding[117][66] = (new Coding[117][65] = (new Coding[117][64] = (new Coding[117][63] = (new Coding[117][62] = (new Coding[117][61] = (new Coding[117][60] = (new Coding[117][59] = (new Coding[117][58] = (new Coding[117][57] = (new Coding[117][56] = (new Coding[117][55] = (new Coding[117][54] = (new Coding[117][53] = (new Coding[117][52] = (new Coding[117][51] = (new Coding[117][50] = (new Coding[117][49] = (new Coding[117][48] = (new Coding[117][47] = (new Coding[117][46] = (new Coding[117][45] = (new Coding[117][44] = (new Coding[117][43] = (new Coding[117][42] = (new Coding[117][41] = (new Coding[117][40] = (new Coding[117][39] = (new Coding[117][38] = (new Coding[117][37] = (new Coding[117][36] = (new Coding[117][35] = (new Coding[117][34] = (new Coding[117][33] = (new Coding[117][32] = (new Coding[117][31] = (new Coding[117][30] = (new Coding[117][29] = (new Coding[117][28] = (new Coding[117][27] = (new Coding[117][26] = (new Coding[117][25] = (new Coding[117][24] = (new Coding[117][23] = (new Coding[117][22] = (new Coding[117][21] = (new Coding[117][20] = (new Coding[117][19] = (new Coding[117][18] = (new Coding[117][17] = (new Coding[117][16] = (new Coding[117][15] = (new Coding[117][14] = (new Coding[117][13] = (new Coding[117][12] = (new Coding[117][11] = (new Coding[117][10] = (new Coding[117][9] = (new Coding[117][8] = (new Coding[117][7] = (new Coding[117][6] = (new Coding[117][5] = (new Coding[117][4] = (new Coding[117][3] = (new Coding[117][2] = (new Coding[117][1] = (new Coding[117][0] = null).of(1, 256, 0)).of(1, 256, 1)).of(1, 256, 0).getDeltaCoding()).of(1, 256, 1).getDeltaCoding()).of(2, 256, 0)).of(2, 256, 1)).of(2, 256, 0).getDeltaCoding()).of(2, 256, 1).getDeltaCoding()).of(3, 256, 0)).of(3, 256, 1)).of(3, 256, 0).getDeltaCoding()).of(3, 256, 1).getDeltaCoding()).of(4, 256, 0)).of(4, 256, 1)).of(4, 256, 0).getDeltaCoding()).of(4, 256, 1).getDeltaCoding()).of(5, 4, 0)).of(5, 4, 1)).of(5, 4, 2)).of(5, 16, 0)).of(5, 16, 1)).of(5, 16, 2)).of(5, 32, 0)).of(5, 32, 1)).of(5, 32, 2)).of(5, 64, 0)).of(5, 64, 1)).of(5, 64, 2)).of(5, 128, 0)).of(5, 128, 1)).of(5, 128, 2)).of(5, 4, 0).getDeltaCoding()).of(5, 4, 1).getDeltaCoding()).of(5, 4, 2).getDeltaCoding()).of(5, 16, 0).getDeltaCoding()).of(5, 16, 1).getDeltaCoding()).of(5, 16, 2).getDeltaCoding()).of(5, 32, 0).getDeltaCoding()).of(5, 32, 1).getDeltaCoding()).of(5, 32, 2).getDeltaCoding()).of(5, 64, 0).getDeltaCoding()).of(5, 64, 1).getDeltaCoding()).of(5, 64, 2).getDeltaCoding()).of(5, 128, 0).getDeltaCoding()).of(5, 128, 1).getDeltaCoding()).of(5, 128, 2).getDeltaCoding()).of(2, 192, 0)).of(2, 224, 0)).of(2, 240, 0)).of(2, 248, 0)).of(2, 252, 0)).of(2, 8, 0).getDeltaCoding()).of(2, 8, 1).getDeltaCoding()).of(2, 16, 0).getDeltaCoding()).of(2, 16, 1).getDeltaCoding()).of(2, 32, 0).getDeltaCoding()).of(2, 32, 1).getDeltaCoding()).of(2, 64, 0).getDeltaCoding()).of(2, 64, 1).getDeltaCoding()).of(2, 128, 0).getDeltaCoding()).of(2, 128, 1).getDeltaCoding()).of(2, 192, 0).getDeltaCoding()).of(2, 192, 1).getDeltaCoding()).of(2, 224, 0).getDeltaCoding()).of(2, 224, 1).getDeltaCoding()).of(2, 240, 0).getDeltaCoding()).of(2, 240, 1).getDeltaCoding()).of(2, 248, 0).getDeltaCoding()).of(2, 248, 1).getDeltaCoding()).of(3, 192, 0)).of(3, 224, 0)).of(3, 240, 0)).of(3, 248, 0)).of(3, 252, 0)).of(3, 8, 0).getDeltaCoding()).of(3, 8, 1).getDeltaCoding()).of(3, 16, 0).getDeltaCoding()).of(3, 16, 1).getDeltaCoding()).of(3, 32, 0).getDeltaCoding()).of(3, 32, 1).getDeltaCoding()).of(3, 64, 0).getDeltaCoding()).of(3, 64, 1).getDeltaCoding()).of(3, 128, 0).getDeltaCoding()).of(3, 128, 1).getDeltaCoding()).of(3, 192, 0).getDeltaCoding()).of(3, 192, 1).getDeltaCoding()).of(3, 224, 0).getDeltaCoding()).of(3, 224, 1).getDeltaCoding()).of(3, 240, 0).getDeltaCoding()).of(3, 240, 1).getDeltaCoding()).of(3, 248, 0).getDeltaCoding()).of(3, 248, 1).getDeltaCoding()).of(4, 192, 0)).of(4, 224, 0)).of(4, 240, 0)).of(4, 248, 0)).of(4, 252, 0)).of(4, 8, 0).getDeltaCoding()).of(4, 8, 1).getDeltaCoding()).of(4, 16, 0).getDeltaCoding()).of(4, 16, 1).getDeltaCoding()).of(4, 32, 0).getDeltaCoding()).of(4, 32, 1).getDeltaCoding()).of(4, 64, 0).getDeltaCoding()).of(4, 64, 1).getDeltaCoding()).of(4, 128, 0).getDeltaCoding()).of(4, 128, 1).getDeltaCoding()).of(4, 192, 0).getDeltaCoding()).of(4, 192, 1).getDeltaCoding()).of(4, 224, 0).getDeltaCoding()).of(4, 224, 1).getDeltaCoding()).of(4, 240, 0).getDeltaCoding()).of(4, 240, 1).getDeltaCoding()).of(4, 248, 0).getDeltaCoding()).of(4, 248, 1).getDeltaCoding(), null };
  
  private static final Map<Coding, Integer> basicCodingIndexes;
  
  protected byte[] bandHeaderBytes;
  
  protected int bandHeaderBytePos;
  
  protected int bandHeaderBytePos0;
  
  static final int SHORT_BAND_HEURISTIC = 100;
  
  public static final int NO_PHASE = 0;
  
  public static final int COLLECT_PHASE = 1;
  
  public static final int FROZEN_PHASE = 3;
  
  public static final int WRITE_PHASE = 5;
  
  public static final int EXPECT_PHASE = 2;
  
  public static final int READ_PHASE = 4;
  
  public static final int DISBURSE_PHASE = 6;
  
  public static final int DONE_PHASE = 8;
  
  private final List<CPRefBand> allKQBands;
  
  private List<Object[]> needPredefIndex;
  
  private CodingChooser codingChooser;
  
  static final byte[] defaultMetaCoding;
  
  static final byte[] noMetaCoding;
  
  ByteCounter outputCounter;
  
  protected int archiveOptions;
  
  protected long archiveSize0;
  
  protected long archiveSize1;
  
  protected int archiveNextCount;
  
  static final int AH_LENGTH_0 = 3;
  
  static final int AH_LENGTH_MIN = 15;
  
  static final int AH_LENGTH_S = 2;
  
  static final int AH_ARCHIVE_SIZE_HI = 0;
  
  static final int AH_ARCHIVE_SIZE_LO = 1;
  
  static final int AH_FILE_HEADER_LEN = 5;
  
  static final int AH_SPECIAL_FORMAT_LEN = 2;
  
  static final int AH_CP_NUMBER_LEN = 4;
  
  static final int AH_CP_EXTRA_LEN = 4;
  
  static final int AB_FLAGS_HI = 0;
  
  static final int AB_FLAGS_LO = 1;
  
  static final int AB_ATTR_COUNT = 2;
  
  static final int AB_ATTR_INDEXES = 3;
  
  static final int AB_ATTR_CALLS = 4;
  
  private static final boolean NULL_IS_OK = true;
  
  MultiBand all_bands;
  
  ByteBand archive_magic;
  
  IntBand archive_header_0;
  
  IntBand archive_header_S;
  
  IntBand archive_header_1;
  
  ByteBand band_headers;
  
  MultiBand cp_bands;
  
  IntBand cp_Utf8_prefix;
  
  IntBand cp_Utf8_suffix;
  
  IntBand cp_Utf8_chars;
  
  IntBand cp_Utf8_big_suffix;
  
  MultiBand cp_Utf8_big_chars;
  
  IntBand cp_Int;
  
  IntBand cp_Float;
  
  IntBand cp_Long_hi;
  
  IntBand cp_Long_lo;
  
  IntBand cp_Double_hi;
  
  IntBand cp_Double_lo;
  
  CPRefBand cp_String;
  
  CPRefBand cp_Class;
  
  CPRefBand cp_Signature_form;
  
  CPRefBand cp_Signature_classes;
  
  CPRefBand cp_Descr_name;
  
  CPRefBand cp_Descr_type;
  
  CPRefBand cp_Field_class;
  
  CPRefBand cp_Field_desc;
  
  CPRefBand cp_Method_class;
  
  CPRefBand cp_Method_desc;
  
  CPRefBand cp_Imethod_class;
  
  CPRefBand cp_Imethod_desc;
  
  IntBand cp_MethodHandle_refkind;
  
  CPRefBand cp_MethodHandle_member;
  
  CPRefBand cp_MethodType;
  
  CPRefBand cp_BootstrapMethod_ref;
  
  IntBand cp_BootstrapMethod_arg_count;
  
  CPRefBand cp_BootstrapMethod_arg;
  
  CPRefBand cp_InvokeDynamic_spec;
  
  CPRefBand cp_InvokeDynamic_desc;
  
  MultiBand attr_definition_bands;
  
  ByteBand attr_definition_headers;
  
  CPRefBand attr_definition_name;
  
  CPRefBand attr_definition_layout;
  
  MultiBand ic_bands;
  
  CPRefBand ic_this_class;
  
  IntBand ic_flags;
  
  CPRefBand ic_outer_class;
  
  CPRefBand ic_name;
  
  MultiBand class_bands;
  
  CPRefBand class_this;
  
  CPRefBand class_super;
  
  IntBand class_interface_count;
  
  CPRefBand class_interface;
  
  IntBand class_field_count;
  
  IntBand class_method_count;
  
  CPRefBand field_descr;
  
  MultiBand field_attr_bands;
  
  IntBand field_flags_hi;
  
  IntBand field_flags_lo;
  
  IntBand field_attr_count;
  
  IntBand field_attr_indexes;
  
  IntBand field_attr_calls;
  
  CPRefBand field_ConstantValue_KQ;
  
  CPRefBand field_Signature_RS;
  
  MultiBand field_metadata_bands;
  
  MultiBand field_type_metadata_bands;
  
  CPRefBand method_descr;
  
  MultiBand method_attr_bands;
  
  IntBand method_flags_hi;
  
  IntBand method_flags_lo;
  
  IntBand method_attr_count;
  
  IntBand method_attr_indexes;
  
  IntBand method_attr_calls;
  
  IntBand method_Exceptions_N;
  
  CPRefBand method_Exceptions_RC;
  
  CPRefBand method_Signature_RS;
  
  MultiBand method_metadata_bands;
  
  IntBand method_MethodParameters_NB;
  
  CPRefBand method_MethodParameters_name_RUN;
  
  IntBand method_MethodParameters_flag_FH;
  
  MultiBand method_type_metadata_bands;
  
  MultiBand class_attr_bands;
  
  IntBand class_flags_hi;
  
  IntBand class_flags_lo;
  
  IntBand class_attr_count;
  
  IntBand class_attr_indexes;
  
  IntBand class_attr_calls;
  
  CPRefBand class_SourceFile_RUN;
  
  CPRefBand class_EnclosingMethod_RC;
  
  CPRefBand class_EnclosingMethod_RDN;
  
  CPRefBand class_Signature_RS;
  
  MultiBand class_metadata_bands;
  
  IntBand class_InnerClasses_N;
  
  CPRefBand class_InnerClasses_RC;
  
  IntBand class_InnerClasses_F;
  
  CPRefBand class_InnerClasses_outer_RCN;
  
  CPRefBand class_InnerClasses_name_RUN;
  
  IntBand class_ClassFile_version_minor_H;
  
  IntBand class_ClassFile_version_major_H;
  
  MultiBand class_type_metadata_bands;
  
  MultiBand code_bands;
  
  ByteBand code_headers;
  
  IntBand code_max_stack;
  
  IntBand code_max_na_locals;
  
  IntBand code_handler_count;
  
  IntBand code_handler_start_P;
  
  IntBand code_handler_end_PO;
  
  IntBand code_handler_catch_PO;
  
  CPRefBand code_handler_class_RCN;
  
  MultiBand code_attr_bands;
  
  IntBand code_flags_hi;
  
  IntBand code_flags_lo;
  
  IntBand code_attr_count;
  
  IntBand code_attr_indexes;
  
  IntBand code_attr_calls;
  
  MultiBand stackmap_bands;
  
  IntBand code_StackMapTable_N;
  
  IntBand code_StackMapTable_frame_T;
  
  IntBand code_StackMapTable_local_N;
  
  IntBand code_StackMapTable_stack_N;
  
  IntBand code_StackMapTable_offset;
  
  IntBand code_StackMapTable_T;
  
  CPRefBand code_StackMapTable_RC;
  
  IntBand code_StackMapTable_P;
  
  IntBand code_LineNumberTable_N;
  
  IntBand code_LineNumberTable_bci_P;
  
  IntBand code_LineNumberTable_line;
  
  IntBand code_LocalVariableTable_N;
  
  IntBand code_LocalVariableTable_bci_P;
  
  IntBand code_LocalVariableTable_span_O;
  
  CPRefBand code_LocalVariableTable_name_RU;
  
  CPRefBand code_LocalVariableTable_type_RS;
  
  IntBand code_LocalVariableTable_slot;
  
  IntBand code_LocalVariableTypeTable_N;
  
  IntBand code_LocalVariableTypeTable_bci_P;
  
  IntBand code_LocalVariableTypeTable_span_O;
  
  CPRefBand code_LocalVariableTypeTable_name_RU;
  
  CPRefBand code_LocalVariableTypeTable_type_RS;
  
  IntBand code_LocalVariableTypeTable_slot;
  
  MultiBand code_type_metadata_bands;
  
  MultiBand bc_bands;
  
  ByteBand bc_codes;
  
  IntBand bc_case_count;
  
  IntBand bc_case_value;
  
  ByteBand bc_byte;
  
  IntBand bc_short;
  
  IntBand bc_local;
  
  IntBand bc_label;
  
  CPRefBand bc_intref;
  
  CPRefBand bc_floatref;
  
  CPRefBand bc_longref;
  
  CPRefBand bc_doubleref;
  
  CPRefBand bc_stringref;
  
  CPRefBand bc_loadablevalueref;
  
  CPRefBand bc_classref;
  
  CPRefBand bc_fieldref;
  
  CPRefBand bc_methodref;
  
  CPRefBand bc_imethodref;
  
  CPRefBand bc_indyref;
  
  CPRefBand bc_thisfield;
  
  CPRefBand bc_superfield;
  
  CPRefBand bc_thismethod;
  
  CPRefBand bc_supermethod;
  
  IntBand bc_initref;
  
  CPRefBand bc_escref;
  
  IntBand bc_escrefsize;
  
  IntBand bc_escsize;
  
  ByteBand bc_escbyte;
  
  MultiBand file_bands;
  
  CPRefBand file_name;
  
  IntBand file_size_hi;
  
  IntBand file_size_lo;
  
  IntBand file_modtime;
  
  IntBand file_options;
  
  ByteBand file_bits;
  
  protected MultiBand[] metadataBands;
  
  protected MultiBand[] typeMetadataBands;
  
  public static final int ADH_CONTEXT_MASK = 3;
  
  public static final int ADH_BIT_SHIFT = 2;
  
  public static final int ADH_BIT_IS_LSB = 1;
  
  public static final int ATTR_INDEX_OVERFLOW = -1;
  
  public int[] attrIndexLimit;
  
  protected long[] attrFlagMask;
  
  protected long[] attrDefSeen;
  
  protected int[] attrOverflowMask;
  
  protected int attrClassFileVersionMask;
  
  protected Map<Attribute.Layout, Band[]> attrBandTable;
  
  protected final Attribute.Layout attrCodeEmpty;
  
  protected final Attribute.Layout attrInnerClassesEmpty;
  
  protected final Attribute.Layout attrClassFileVersion;
  
  protected final Attribute.Layout attrConstantValue;
  
  Map<Attribute.Layout, Integer> attrIndexTable;
  
  protected List<List<Attribute.Layout>> attrDefs;
  
  protected MultiBand[] attrBands;
  
  private static final int[][] shortCodeLimits;
  
  public final int shortCodeHeader_h_limit;
  
  static final int LONG_CODE_HEADER = 0;
  
  static int nextSeqForDebug;
  
  static File dumpDir;
  
  private Map<Band, Band> prevForAssertMap;
  
  static LinkedList<String> bandSequenceList;
  
  protected abstract ConstantPool.Index getCPIndex(byte paramByte);
  
  public void initHighestClassVersion(Package.Version paramVersion) throws IOException {
    if (this.highestClassVersion != null)
      throw new IOException("Highest class major version is already initialized to " + this.highestClassVersion + "; new setting is " + paramVersion); 
    this.highestClassVersion = paramVersion;
    adjustToClassVersion();
  }
  
  public Package.Version getHighestClassVersion() { return this.highestClassVersion; }
  
  protected BandStructure() {
    if (this.effort == 0)
      this.effort = 5; 
    this.optDumpBands = this.p200.getBoolean("com.sun.java.util.jar.pack.dump.bands");
    this.optDebugBands = this.p200.getBoolean("com.sun.java.util.jar.pack.debug.bands");
    this.optVaryCodings = !this.p200.getBoolean("com.sun.java.util.jar.pack.no.vary.codings");
    this.optBigStrings = !this.p200.getBoolean("com.sun.java.util.jar.pack.no.big.strings");
    this.highestClassVersion = null;
    this.isReader = this instanceof PackageReader;
    this.allKQBands = new ArrayList();
    this.needPredefIndex = new ArrayList();
    this.all_bands = (MultiBand)(new MultiBand("(package)", UNSIGNED5)).init();
    this.archive_magic = this.all_bands.newByteBand("archive_magic");
    this.archive_header_0 = this.all_bands.newIntBand("archive_header_0", UNSIGNED5);
    this.archive_header_S = this.all_bands.newIntBand("archive_header_S", UNSIGNED5);
    this.archive_header_1 = this.all_bands.newIntBand("archive_header_1", UNSIGNED5);
    this.band_headers = this.all_bands.newByteBand("band_headers");
    this.cp_bands = this.all_bands.newMultiBand("(constant_pool)", DELTA5);
    this.cp_Utf8_prefix = this.cp_bands.newIntBand("cp_Utf8_prefix");
    this.cp_Utf8_suffix = this.cp_bands.newIntBand("cp_Utf8_suffix", UNSIGNED5);
    this.cp_Utf8_chars = this.cp_bands.newIntBand("cp_Utf8_chars", CHAR3);
    this.cp_Utf8_big_suffix = this.cp_bands.newIntBand("cp_Utf8_big_suffix");
    this.cp_Utf8_big_chars = this.cp_bands.newMultiBand("(cp_Utf8_big_chars)", DELTA5);
    this.cp_Int = this.cp_bands.newIntBand("cp_Int", UDELTA5);
    this.cp_Float = this.cp_bands.newIntBand("cp_Float", UDELTA5);
    this.cp_Long_hi = this.cp_bands.newIntBand("cp_Long_hi", UDELTA5);
    this.cp_Long_lo = this.cp_bands.newIntBand("cp_Long_lo");
    this.cp_Double_hi = this.cp_bands.newIntBand("cp_Double_hi", UDELTA5);
    this.cp_Double_lo = this.cp_bands.newIntBand("cp_Double_lo");
    this.cp_String = this.cp_bands.newCPRefBand("cp_String", UDELTA5, (byte)1);
    this.cp_Class = this.cp_bands.newCPRefBand("cp_Class", UDELTA5, (byte)1);
    this.cp_Signature_form = this.cp_bands.newCPRefBand("cp_Signature_form", (byte)1);
    this.cp_Signature_classes = this.cp_bands.newCPRefBand("cp_Signature_classes", UDELTA5, (byte)7);
    this.cp_Descr_name = this.cp_bands.newCPRefBand("cp_Descr_name", (byte)1);
    this.cp_Descr_type = this.cp_bands.newCPRefBand("cp_Descr_type", UDELTA5, (byte)13);
    this.cp_Field_class = this.cp_bands.newCPRefBand("cp_Field_class", (byte)7);
    this.cp_Field_desc = this.cp_bands.newCPRefBand("cp_Field_desc", UDELTA5, (byte)12);
    this.cp_Method_class = this.cp_bands.newCPRefBand("cp_Method_class", (byte)7);
    this.cp_Method_desc = this.cp_bands.newCPRefBand("cp_Method_desc", UDELTA5, (byte)12);
    this.cp_Imethod_class = this.cp_bands.newCPRefBand("cp_Imethod_class", (byte)7);
    this.cp_Imethod_desc = this.cp_bands.newCPRefBand("cp_Imethod_desc", UDELTA5, (byte)12);
    this.cp_MethodHandle_refkind = this.cp_bands.newIntBand("cp_MethodHandle_refkind", DELTA5);
    this.cp_MethodHandle_member = this.cp_bands.newCPRefBand("cp_MethodHandle_member", UDELTA5, (byte)52);
    this.cp_MethodType = this.cp_bands.newCPRefBand("cp_MethodType", UDELTA5, (byte)13);
    this.cp_BootstrapMethod_ref = this.cp_bands.newCPRefBand("cp_BootstrapMethod_ref", DELTA5, (byte)15);
    this.cp_BootstrapMethod_arg_count = this.cp_bands.newIntBand("cp_BootstrapMethod_arg_count", UDELTA5);
    this.cp_BootstrapMethod_arg = this.cp_bands.newCPRefBand("cp_BootstrapMethod_arg", DELTA5, (byte)51);
    this.cp_InvokeDynamic_spec = this.cp_bands.newCPRefBand("cp_InvokeDynamic_spec", DELTA5, (byte)17);
    this.cp_InvokeDynamic_desc = this.cp_bands.newCPRefBand("cp_InvokeDynamic_desc", UDELTA5, (byte)12);
    this.attr_definition_bands = this.all_bands.newMultiBand("(attr_definition_bands)", UNSIGNED5);
    this.attr_definition_headers = this.attr_definition_bands.newByteBand("attr_definition_headers");
    this.attr_definition_name = this.attr_definition_bands.newCPRefBand("attr_definition_name", (byte)1);
    this.attr_definition_layout = this.attr_definition_bands.newCPRefBand("attr_definition_layout", (byte)1);
    this.ic_bands = this.all_bands.newMultiBand("(ic_bands)", DELTA5);
    this.ic_this_class = this.ic_bands.newCPRefBand("ic_this_class", UDELTA5, (byte)7);
    this.ic_flags = this.ic_bands.newIntBand("ic_flags", UNSIGNED5);
    this.ic_outer_class = this.ic_bands.newCPRefBand("ic_outer_class", DELTA5, (byte)7, true);
    this.ic_name = this.ic_bands.newCPRefBand("ic_name", DELTA5, (byte)1, true);
    this.class_bands = this.all_bands.newMultiBand("(class_bands)", DELTA5);
    this.class_this = this.class_bands.newCPRefBand("class_this", (byte)7);
    this.class_super = this.class_bands.newCPRefBand("class_super", (byte)7);
    this.class_interface_count = this.class_bands.newIntBand("class_interface_count");
    this.class_interface = this.class_bands.newCPRefBand("class_interface", (byte)7);
    this.class_field_count = this.class_bands.newIntBand("class_field_count");
    this.class_method_count = this.class_bands.newIntBand("class_method_count");
    this.field_descr = this.class_bands.newCPRefBand("field_descr", (byte)12);
    this.field_attr_bands = this.class_bands.newMultiBand("(field_attr_bands)", UNSIGNED5);
    this.field_flags_hi = this.field_attr_bands.newIntBand("field_flags_hi");
    this.field_flags_lo = this.field_attr_bands.newIntBand("field_flags_lo");
    this.field_attr_count = this.field_attr_bands.newIntBand("field_attr_count");
    this.field_attr_indexes = this.field_attr_bands.newIntBand("field_attr_indexes");
    this.field_attr_calls = this.field_attr_bands.newIntBand("field_attr_calls");
    this.field_ConstantValue_KQ = this.field_attr_bands.newCPRefBand("field_ConstantValue_KQ", (byte)53);
    this.field_Signature_RS = this.field_attr_bands.newCPRefBand("field_Signature_RS", (byte)13);
    this.field_metadata_bands = this.field_attr_bands.newMultiBand("(field_metadata_bands)", UNSIGNED5);
    this.field_type_metadata_bands = this.field_attr_bands.newMultiBand("(field_type_metadata_bands)", UNSIGNED5);
    this.method_descr = this.class_bands.newCPRefBand("method_descr", MDELTA5, (byte)12);
    this.method_attr_bands = this.class_bands.newMultiBand("(method_attr_bands)", UNSIGNED5);
    this.method_flags_hi = this.method_attr_bands.newIntBand("method_flags_hi");
    this.method_flags_lo = this.method_attr_bands.newIntBand("method_flags_lo");
    this.method_attr_count = this.method_attr_bands.newIntBand("method_attr_count");
    this.method_attr_indexes = this.method_attr_bands.newIntBand("method_attr_indexes");
    this.method_attr_calls = this.method_attr_bands.newIntBand("method_attr_calls");
    this.method_Exceptions_N = this.method_attr_bands.newIntBand("method_Exceptions_N");
    this.method_Exceptions_RC = this.method_attr_bands.newCPRefBand("method_Exceptions_RC", (byte)7);
    this.method_Signature_RS = this.method_attr_bands.newCPRefBand("method_Signature_RS", (byte)13);
    this.method_metadata_bands = this.method_attr_bands.newMultiBand("(method_metadata_bands)", UNSIGNED5);
    this.method_MethodParameters_NB = this.method_attr_bands.newIntBand("method_MethodParameters_NB", BYTE1);
    this.method_MethodParameters_name_RUN = this.method_attr_bands.newCPRefBand("method_MethodParameters_name_RUN", UNSIGNED5, (byte)1, true);
    this.method_MethodParameters_flag_FH = this.method_attr_bands.newIntBand("method_MethodParameters_flag_FH");
    this.method_type_metadata_bands = this.method_attr_bands.newMultiBand("(method_type_metadata_bands)", UNSIGNED5);
    this.class_attr_bands = this.class_bands.newMultiBand("(class_attr_bands)", UNSIGNED5);
    this.class_flags_hi = this.class_attr_bands.newIntBand("class_flags_hi");
    this.class_flags_lo = this.class_attr_bands.newIntBand("class_flags_lo");
    this.class_attr_count = this.class_attr_bands.newIntBand("class_attr_count");
    this.class_attr_indexes = this.class_attr_bands.newIntBand("class_attr_indexes");
    this.class_attr_calls = this.class_attr_bands.newIntBand("class_attr_calls");
    this.class_SourceFile_RUN = this.class_attr_bands.newCPRefBand("class_SourceFile_RUN", UNSIGNED5, (byte)1, true);
    this.class_EnclosingMethod_RC = this.class_attr_bands.newCPRefBand("class_EnclosingMethod_RC", (byte)7);
    this.class_EnclosingMethod_RDN = this.class_attr_bands.newCPRefBand("class_EnclosingMethod_RDN", UNSIGNED5, (byte)12, true);
    this.class_Signature_RS = this.class_attr_bands.newCPRefBand("class_Signature_RS", (byte)13);
    this.class_metadata_bands = this.class_attr_bands.newMultiBand("(class_metadata_bands)", UNSIGNED5);
    this.class_InnerClasses_N = this.class_attr_bands.newIntBand("class_InnerClasses_N");
    this.class_InnerClasses_RC = this.class_attr_bands.newCPRefBand("class_InnerClasses_RC", (byte)7);
    this.class_InnerClasses_F = this.class_attr_bands.newIntBand("class_InnerClasses_F");
    this.class_InnerClasses_outer_RCN = this.class_attr_bands.newCPRefBand("class_InnerClasses_outer_RCN", UNSIGNED5, (byte)7, true);
    this.class_InnerClasses_name_RUN = this.class_attr_bands.newCPRefBand("class_InnerClasses_name_RUN", UNSIGNED5, (byte)1, true);
    this.class_ClassFile_version_minor_H = this.class_attr_bands.newIntBand("class_ClassFile_version_minor_H");
    this.class_ClassFile_version_major_H = this.class_attr_bands.newIntBand("class_ClassFile_version_major_H");
    this.class_type_metadata_bands = this.class_attr_bands.newMultiBand("(class_type_metadata_bands)", UNSIGNED5);
    this.code_bands = this.class_bands.newMultiBand("(code_bands)", UNSIGNED5);
    this.code_headers = this.code_bands.newByteBand("code_headers");
    this.code_max_stack = this.code_bands.newIntBand("code_max_stack", UNSIGNED5);
    this.code_max_na_locals = this.code_bands.newIntBand("code_max_na_locals", UNSIGNED5);
    this.code_handler_count = this.code_bands.newIntBand("code_handler_count", UNSIGNED5);
    this.code_handler_start_P = this.code_bands.newIntBand("code_handler_start_P", BCI5);
    this.code_handler_end_PO = this.code_bands.newIntBand("code_handler_end_PO", BRANCH5);
    this.code_handler_catch_PO = this.code_bands.newIntBand("code_handler_catch_PO", BRANCH5);
    this.code_handler_class_RCN = this.code_bands.newCPRefBand("code_handler_class_RCN", UNSIGNED5, (byte)7, true);
    this.code_attr_bands = this.class_bands.newMultiBand("(code_attr_bands)", UNSIGNED5);
    this.code_flags_hi = this.code_attr_bands.newIntBand("code_flags_hi");
    this.code_flags_lo = this.code_attr_bands.newIntBand("code_flags_lo");
    this.code_attr_count = this.code_attr_bands.newIntBand("code_attr_count");
    this.code_attr_indexes = this.code_attr_bands.newIntBand("code_attr_indexes");
    this.code_attr_calls = this.code_attr_bands.newIntBand("code_attr_calls");
    this.stackmap_bands = this.code_attr_bands.newMultiBand("(StackMapTable_bands)", UNSIGNED5);
    this.code_StackMapTable_N = this.stackmap_bands.newIntBand("code_StackMapTable_N");
    this.code_StackMapTable_frame_T = this.stackmap_bands.newIntBand("code_StackMapTable_frame_T", BYTE1);
    this.code_StackMapTable_local_N = this.stackmap_bands.newIntBand("code_StackMapTable_local_N");
    this.code_StackMapTable_stack_N = this.stackmap_bands.newIntBand("code_StackMapTable_stack_N");
    this.code_StackMapTable_offset = this.stackmap_bands.newIntBand("code_StackMapTable_offset", UNSIGNED5);
    this.code_StackMapTable_T = this.stackmap_bands.newIntBand("code_StackMapTable_T", BYTE1);
    this.code_StackMapTable_RC = this.stackmap_bands.newCPRefBand("code_StackMapTable_RC", (byte)7);
    this.code_StackMapTable_P = this.stackmap_bands.newIntBand("code_StackMapTable_P", BCI5);
    this.code_LineNumberTable_N = this.code_attr_bands.newIntBand("code_LineNumberTable_N");
    this.code_LineNumberTable_bci_P = this.code_attr_bands.newIntBand("code_LineNumberTable_bci_P", BCI5);
    this.code_LineNumberTable_line = this.code_attr_bands.newIntBand("code_LineNumberTable_line");
    this.code_LocalVariableTable_N = this.code_attr_bands.newIntBand("code_LocalVariableTable_N");
    this.code_LocalVariableTable_bci_P = this.code_attr_bands.newIntBand("code_LocalVariableTable_bci_P", BCI5);
    this.code_LocalVariableTable_span_O = this.code_attr_bands.newIntBand("code_LocalVariableTable_span_O", BRANCH5);
    this.code_LocalVariableTable_name_RU = this.code_attr_bands.newCPRefBand("code_LocalVariableTable_name_RU", (byte)1);
    this.code_LocalVariableTable_type_RS = this.code_attr_bands.newCPRefBand("code_LocalVariableTable_type_RS", (byte)13);
    this.code_LocalVariableTable_slot = this.code_attr_bands.newIntBand("code_LocalVariableTable_slot");
    this.code_LocalVariableTypeTable_N = this.code_attr_bands.newIntBand("code_LocalVariableTypeTable_N");
    this.code_LocalVariableTypeTable_bci_P = this.code_attr_bands.newIntBand("code_LocalVariableTypeTable_bci_P", BCI5);
    this.code_LocalVariableTypeTable_span_O = this.code_attr_bands.newIntBand("code_LocalVariableTypeTable_span_O", BRANCH5);
    this.code_LocalVariableTypeTable_name_RU = this.code_attr_bands.newCPRefBand("code_LocalVariableTypeTable_name_RU", (byte)1);
    this.code_LocalVariableTypeTable_type_RS = this.code_attr_bands.newCPRefBand("code_LocalVariableTypeTable_type_RS", (byte)13);
    this.code_LocalVariableTypeTable_slot = this.code_attr_bands.newIntBand("code_LocalVariableTypeTable_slot");
    this.code_type_metadata_bands = this.code_attr_bands.newMultiBand("(code_type_metadata_bands)", UNSIGNED5);
    this.bc_bands = this.all_bands.newMultiBand("(byte_codes)", UNSIGNED5);
    this.bc_codes = this.bc_bands.newByteBand("bc_codes");
    this.bc_case_count = this.bc_bands.newIntBand("bc_case_count");
    this.bc_case_value = this.bc_bands.newIntBand("bc_case_value", DELTA5);
    this.bc_byte = this.bc_bands.newByteBand("bc_byte");
    this.bc_short = this.bc_bands.newIntBand("bc_short", DELTA5);
    this.bc_local = this.bc_bands.newIntBand("bc_local");
    this.bc_label = this.bc_bands.newIntBand("bc_label", BRANCH5);
    this.bc_intref = this.bc_bands.newCPRefBand("bc_intref", DELTA5, (byte)3);
    this.bc_floatref = this.bc_bands.newCPRefBand("bc_floatref", DELTA5, (byte)4);
    this.bc_longref = this.bc_bands.newCPRefBand("bc_longref", DELTA5, (byte)5);
    this.bc_doubleref = this.bc_bands.newCPRefBand("bc_doubleref", DELTA5, (byte)6);
    this.bc_stringref = this.bc_bands.newCPRefBand("bc_stringref", DELTA5, (byte)8);
    this.bc_loadablevalueref = this.bc_bands.newCPRefBand("bc_loadablevalueref", DELTA5, (byte)51);
    this.bc_classref = this.bc_bands.newCPRefBand("bc_classref", UNSIGNED5, (byte)7, true);
    this.bc_fieldref = this.bc_bands.newCPRefBand("bc_fieldref", DELTA5, (byte)9);
    this.bc_methodref = this.bc_bands.newCPRefBand("bc_methodref", (byte)10);
    this.bc_imethodref = this.bc_bands.newCPRefBand("bc_imethodref", DELTA5, (byte)11);
    this.bc_indyref = this.bc_bands.newCPRefBand("bc_indyref", DELTA5, (byte)18);
    this.bc_thisfield = this.bc_bands.newCPRefBand("bc_thisfield", (byte)0);
    this.bc_superfield = this.bc_bands.newCPRefBand("bc_superfield", (byte)0);
    this.bc_thismethod = this.bc_bands.newCPRefBand("bc_thismethod", (byte)0);
    this.bc_supermethod = this.bc_bands.newCPRefBand("bc_supermethod", (byte)0);
    this.bc_initref = this.bc_bands.newIntBand("bc_initref");
    this.bc_escref = this.bc_bands.newCPRefBand("bc_escref", (byte)50);
    this.bc_escrefsize = this.bc_bands.newIntBand("bc_escrefsize");
    this.bc_escsize = this.bc_bands.newIntBand("bc_escsize");
    this.bc_escbyte = this.bc_bands.newByteBand("bc_escbyte");
    this.file_bands = this.all_bands.newMultiBand("(file_bands)", UNSIGNED5);
    this.file_name = this.file_bands.newCPRefBand("file_name", (byte)1);
    this.file_size_hi = this.file_bands.newIntBand("file_size_hi");
    this.file_size_lo = this.file_bands.newIntBand("file_size_lo");
    this.file_modtime = this.file_bands.newIntBand("file_modtime", DELTA5);
    this.file_options = this.file_bands.newIntBand("file_options");
    this.file_bits = this.file_bands.newByteBand("file_bits");
    this.metadataBands = new MultiBand[4];
    this.metadataBands[0] = this.class_metadata_bands;
    this.metadataBands[1] = this.field_metadata_bands;
    this.metadataBands[2] = this.method_metadata_bands;
    this.typeMetadataBands = new MultiBand[4];
    this.typeMetadataBands[0] = this.class_type_metadata_bands;
    this.typeMetadataBands[1] = this.field_type_metadata_bands;
    this.typeMetadataBands[2] = this.method_type_metadata_bands;
    this.typeMetadataBands[3] = this.code_type_metadata_bands;
    this.attrIndexLimit = new int[4];
    this.attrFlagMask = new long[4];
    this.attrDefSeen = new long[4];
    this.attrOverflowMask = new int[4];
    this.attrBandTable = new HashMap();
    this.attrIndexTable = new HashMap();
    this.attrDefs = new FixedList(4);
    byte b1;
    for (b1 = 0; b1 < 4; b1++) {
      assert this.attrIndexLimit[b1] == 0;
      this.attrIndexLimit[b1] = 32;
      this.attrDefs.set(b1, new ArrayList(Collections.nCopies(this.attrIndexLimit[b1], (Attribute.Layout)null)));
    } 
    this.attrInnerClassesEmpty = predefineAttribute(23, 0, null, "InnerClasses", "");
    assert this.attrInnerClassesEmpty == Package.attrInnerClassesEmpty;
    predefineAttribute(17, 0, new Band[] { this.class_SourceFile_RUN }, "SourceFile", "RUNH");
    predefineAttribute(18, 0, new Band[] { this.class_EnclosingMethod_RC, this.class_EnclosingMethod_RDN }, "EnclosingMethod", "RCHRDNH");
    this.attrClassFileVersion = predefineAttribute(24, 0, new Band[] { this.class_ClassFile_version_minor_H, this.class_ClassFile_version_major_H }, ".ClassFile.version", "HH");
    predefineAttribute(19, 0, new Band[] { this.class_Signature_RS }, "Signature", "RSH");
    predefineAttribute(20, 0, null, "Deprecated", "");
    predefineAttribute(16, 0, null, ".Overflow", "");
    this.attrConstantValue = predefineAttribute(17, 1, new Band[] { this.field_ConstantValue_KQ }, "ConstantValue", "KQH");
    predefineAttribute(19, 1, new Band[] { this.field_Signature_RS }, "Signature", "RSH");
    predefineAttribute(20, 1, null, "Deprecated", "");
    predefineAttribute(16, 1, null, ".Overflow", "");
    this.attrCodeEmpty = predefineAttribute(17, 2, null, "Code", "");
    predefineAttribute(18, 2, new Band[] { this.method_Exceptions_N, this.method_Exceptions_RC }, "Exceptions", "NH[RCH]");
    predefineAttribute(26, 2, new Band[] { this.method_MethodParameters_NB, this.method_MethodParameters_name_RUN, this.method_MethodParameters_flag_FH }, "MethodParameters", "NB[RUNHFH]");
    assert this.attrCodeEmpty == Package.attrCodeEmpty;
    predefineAttribute(19, 2, new Band[] { this.method_Signature_RS }, "Signature", "RSH");
    predefineAttribute(20, 2, null, "Deprecated", "");
    predefineAttribute(16, 2, null, ".Overflow", "");
    for (b1 = 0; b1 < 4; b1++) {
      MultiBand multiBand1 = this.metadataBands[b1];
      if (b1 != 3) {
        predefineAttribute(21, Constants.ATTR_CONTEXT_NAME[b1] + "_RVA_", multiBand1, Attribute.lookup(null, b1, "RuntimeVisibleAnnotations"));
        predefineAttribute(22, Constants.ATTR_CONTEXT_NAME[b1] + "_RIA_", multiBand1, Attribute.lookup(null, b1, "RuntimeInvisibleAnnotations"));
        if (b1 == 2) {
          predefineAttribute(23, "method_RVPA_", multiBand1, Attribute.lookup(null, b1, "RuntimeVisibleParameterAnnotations"));
          predefineAttribute(24, "method_RIPA_", multiBand1, Attribute.lookup(null, b1, "RuntimeInvisibleParameterAnnotations"));
          predefineAttribute(25, "method_AD_", multiBand1, Attribute.lookup(null, b1, "AnnotationDefault"));
        } 
      } 
      MultiBand multiBand2 = this.typeMetadataBands[b1];
      predefineAttribute(27, Constants.ATTR_CONTEXT_NAME[b1] + "_RVTA_", multiBand2, Attribute.lookup(null, b1, "RuntimeVisibleTypeAnnotations"));
      predefineAttribute(28, Constants.ATTR_CONTEXT_NAME[b1] + "_RITA_", multiBand2, Attribute.lookup(null, b1, "RuntimeInvisibleTypeAnnotations"));
    } 
    Attribute.Layout layout = Attribute.lookup(null, 3, "StackMapTable").layout();
    predefineAttribute(0, 3, this.stackmap_bands.toArray(), layout.name(), layout.layout());
    predefineAttribute(1, 3, new Band[] { this.code_LineNumberTable_N, this.code_LineNumberTable_bci_P, this.code_LineNumberTable_line }, "LineNumberTable", "NH[PHH]");
    predefineAttribute(2, 3, new Band[] { this.code_LocalVariableTable_N, this.code_LocalVariableTable_bci_P, this.code_LocalVariableTable_span_O, this.code_LocalVariableTable_name_RU, this.code_LocalVariableTable_type_RS, this.code_LocalVariableTable_slot }, "LocalVariableTable", "NH[PHOHRUHRSHH]");
    predefineAttribute(3, 3, new Band[] { this.code_LocalVariableTypeTable_N, this.code_LocalVariableTypeTable_bci_P, this.code_LocalVariableTypeTable_span_O, this.code_LocalVariableTypeTable_name_RU, this.code_LocalVariableTypeTable_type_RS, this.code_LocalVariableTypeTable_slot }, "LocalVariableTypeTable", "NH[PHOHRUHRSHH]");
    predefineAttribute(16, 3, null, ".Overflow", "");
    byte b2;
    for (b2 = 0; b2 < 4; b2++)
      this.attrDefSeen[b2] = 0L; 
    for (b2 = 0; b2 < 4; b2++) {
      this.attrOverflowMask[b2] = 65536;
      this.attrIndexLimit[b2] = 0;
    } 
    this.attrClassFileVersionMask = 16777216;
    this.attrBands = new MultiBand[4];
    this.attrBands[0] = this.class_attr_bands;
    this.attrBands[1] = this.field_attr_bands;
    this.attrBands[2] = this.method_attr_bands;
    this.attrBands[3] = this.code_attr_bands;
    this.shortCodeHeader_h_limit = shortCodeLimits.length;
  }
  
  public static Coding codingForIndex(int paramInt) { return (paramInt < basicCodings.length) ? basicCodings[paramInt] : null; }
  
  public static int indexOf(Coding paramCoding) {
    Integer integer = (Integer)basicCodingIndexes.get(paramCoding);
    return (integer == null) ? 0 : integer.intValue();
  }
  
  public static Coding[] getBasicCodings() { return (Coding[])basicCodings.clone(); }
  
  protected CodingMethod getBandHeader(int paramInt, Coding paramCoding) {
    CodingMethod[] arrayOfCodingMethod = { null };
    this.bandHeaderBytes[--this.bandHeaderBytePos] = (byte)paramInt;
    this.bandHeaderBytePos0 = this.bandHeaderBytePos;
    this.bandHeaderBytePos = parseMetaCoding(this.bandHeaderBytes, this.bandHeaderBytePos, paramCoding, arrayOfCodingMethod);
    return arrayOfCodingMethod[0];
  }
  
  public static int parseMetaCoding(byte[] paramArrayOfByte, int paramInt, Coding paramCoding, CodingMethod[] paramArrayOfCodingMethod) {
    if ((paramArrayOfByte[paramInt] & 0xFF) == 0) {
      paramArrayOfCodingMethod[0] = paramCoding;
      return paramInt + 1;
    } 
    int i = Coding.parseMetaCoding(paramArrayOfByte, paramInt, paramCoding, paramArrayOfCodingMethod);
    if (i > paramInt)
      return i; 
    i = PopulationCoding.parseMetaCoding(paramArrayOfByte, paramInt, paramCoding, paramArrayOfCodingMethod);
    if (i > paramInt)
      return i; 
    i = AdaptiveCoding.parseMetaCoding(paramArrayOfByte, paramInt, paramCoding, paramArrayOfCodingMethod);
    if (i > paramInt)
      return i; 
    throw new RuntimeException("Bad meta-coding op " + (paramArrayOfByte[paramInt] & 0xFF));
  }
  
  static boolean phaseIsRead(int paramInt) { return (paramInt % 2 == 0); }
  
  static int phaseCmp(int paramInt1, int paramInt2) {
    assert paramInt1 % 2 == paramInt2 % 2 || paramInt1 % 8 == 0 || paramInt2 % 8 == 0;
    return paramInt1 - paramInt2;
  }
  
  static int getIntTotal(int[] paramArrayOfInt) {
    int i = 0;
    for (byte b = 0; b < paramArrayOfInt.length; b++)
      i += paramArrayOfInt[b]; 
    return i;
  }
  
  int encodeRef(ConstantPool.Entry paramEntry, ConstantPool.Index paramIndex) {
    if (paramIndex == null)
      throw new RuntimeException("null index for " + paramEntry.stringValue()); 
    int i = paramIndex.indexOf(paramEntry);
    if (this.verbose > 2)
      Utils.log.fine("putRef " + i + " => " + paramEntry); 
    return i;
  }
  
  ConstantPool.Entry decodeRef(int paramInt, ConstantPool.Index paramIndex) {
    if (paramInt < 0 || paramInt >= paramIndex.size())
      Utils.log.warning("decoding bad ref " + paramInt + " in " + paramIndex); 
    ConstantPool.Entry entry = paramIndex.getEntry(paramInt);
    if (this.verbose > 2)
      Utils.log.fine("getRef " + paramInt + " => " + entry); 
    return entry;
  }
  
  protected CodingChooser getCodingChooser() {
    if (this.codingChooser == null) {
      this.codingChooser = new CodingChooser(this.effort, basicCodings);
      if (this.codingChooser.stress != null && this instanceof PackageWriter) {
        ArrayList arrayList = ((PackageWriter)this).pkg.classes;
        if (!arrayList.isEmpty()) {
          Package.Class clazz = (Package.Class)arrayList.get(0);
          this.codingChooser.addStressSeed(clazz.getName().hashCode());
        } 
      } 
    } 
    return this.codingChooser;
  }
  
  public CodingMethod chooseCoding(int[] paramArrayOfInt1, int paramInt1, int paramInt2, Coding paramCoding, String paramString, int[] paramArrayOfInt2) {
    assert this.optVaryCodings;
    if (this.effort <= 1)
      return paramCoding; 
    CodingChooser codingChooser1 = getCodingChooser();
    if (this.verbose > 1 || codingChooser1.verbose > 1)
      Utils.log.fine("--- chooseCoding " + paramString); 
    return codingChooser1.choose(paramArrayOfInt1, paramInt1, paramInt2, paramCoding, paramArrayOfInt2);
  }
  
  protected static int decodeEscapeValue(int paramInt, Coding paramCoding) {
    if (paramCoding.B() == 1 || paramCoding.L() == 0)
      return -1; 
    if (paramCoding.S() != 0) {
      if (-256 <= paramInt && paramInt <= -1 && paramCoding.min() <= -256) {
        int i = -1 - paramInt;
        assert i >= 0 && i < 256;
        return i;
      } 
    } else {
      int i = paramCoding.L();
      if (i <= paramInt && paramInt <= i + 255 && paramCoding.max() >= i + 255) {
        int j = paramInt - i;
        assert j >= 0 && j < 256;
        return j;
      } 
    } 
    return -1;
  }
  
  protected static int encodeEscapeValue(int paramInt, Coding paramCoding) {
    int i;
    assert paramInt >= 0 && paramInt < 256;
    assert paramCoding.B() > 1 && paramCoding.L() > 0;
    if (paramCoding.S() != 0) {
      assert paramCoding.min() <= -256;
      i = -1 - paramInt;
    } else {
      int j = paramCoding.L();
      assert paramCoding.max() >= j + 255;
      i = paramInt + j;
    } 
    assert decodeEscapeValue(i, paramCoding) == paramInt : paramCoding + " XB=" + paramInt + " X=" + i;
    return i;
  }
  
  void writeAllBandsTo(OutputStream paramOutputStream) throws IOException {
    this.outputCounter = new ByteCounter(paramOutputStream);
    paramOutputStream = this.outputCounter;
    this.all_bands.writeTo(paramOutputStream);
    if (this.verbose > 0) {
      long l = this.outputCounter.getCount();
      Utils.log.info("Wrote total of " + l + " bytes.");
      assert l == this.archiveSize0 + this.archiveSize1;
    } 
    this.outputCounter = null;
  }
  
  static IntBand getAttrBand(MultiBand paramMultiBand, int paramInt) {
    IntBand intBand = (IntBand)paramMultiBand.get(paramInt);
    switch (paramInt) {
      case 0:
        assert intBand.name().endsWith("_flags_hi");
        return intBand;
      case 1:
        assert intBand.name().endsWith("_flags_lo");
        return intBand;
      case 2:
        assert intBand.name().endsWith("_attr_count");
        return intBand;
      case 3:
        assert intBand.name().endsWith("_attr_indexes");
        return intBand;
      case 4:
        assert intBand.name().endsWith("_attr_calls");
        return intBand;
    } 
    assert false;
    return intBand;
  }
  
  protected void setBandIndexes() {
    for (Object[] arrayOfObject : this.needPredefIndex) {
      CPRefBand cPRefBand = (CPRefBand)arrayOfObject[0];
      Byte byte = (Byte)arrayOfObject[1];
      cPRefBand.setIndex(getCPIndex(byte.byteValue()));
    } 
    this.needPredefIndex = null;
    if (this.verbose > 3)
      printCDecl(this.all_bands); 
  }
  
  protected void setBandIndex(CPRefBand paramCPRefBand, byte paramByte) {
    Object[] arrayOfObject = { paramCPRefBand, Byte.valueOf(paramByte) };
    if (paramByte == 53) {
      this.allKQBands.add(paramCPRefBand);
    } else if (this.needPredefIndex != null) {
      this.needPredefIndex.add(arrayOfObject);
    } else {
      paramCPRefBand.setIndex(getCPIndex(paramByte));
    } 
  }
  
  protected void setConstantValueIndex(Package.Class.Field paramField) {
    ConstantPool.Index index = null;
    if (paramField != null) {
      byte b = paramField.getLiteralTag();
      index = getCPIndex(b);
      if (this.verbose > 2)
        Utils.log.fine("setConstantValueIndex " + paramField + " " + ConstantPool.tagName(b) + " => " + index); 
      assert index != null;
    } 
    for (CPRefBand cPRefBand : this.allKQBands)
      cPRefBand.setIndex(index); 
  }
  
  private void adjustToClassVersion() {
    if (getHighestClassVersion().lessThan(Constants.JAVA6_MAX_CLASS_VERSION)) {
      if (this.verbose > 0)
        Utils.log.fine("Legacy package version"); 
      undefineAttribute(0, 3);
    } 
  }
  
  protected void initAttrIndexLimit() {
    for (byte b = 0; b < 4; b++) {
      assert this.attrIndexLimit[b] == 0;
      this.attrIndexLimit[b] = haveFlagsHi(b) ? 63 : 32;
      List list = (List)this.attrDefs.get(b);
      assert list.size() == 32;
      int i = this.attrIndexLimit[b] - list.size();
      list.addAll(Collections.nCopies(i, (Attribute.Layout)null));
    } 
  }
  
  protected boolean haveFlagsHi(int paramInt) {
    int i = 1 << 9 + paramInt;
    switch (paramInt) {
      case 0:
        assert i == 512;
        return testBit(this.archiveOptions, i);
      case 1:
        assert i == 1024;
        return testBit(this.archiveOptions, i);
      case 2:
        assert i == 2048;
        return testBit(this.archiveOptions, i);
      case 3:
        assert i == 4096;
        return testBit(this.archiveOptions, i);
    } 
    assert false;
    return testBit(this.archiveOptions, i);
  }
  
  protected List<Attribute.Layout> getPredefinedAttrs(int paramInt) {
    assert this.attrIndexLimit[paramInt] != 0;
    ArrayList arrayList = new ArrayList(this.attrIndexLimit[paramInt]);
    for (byte b = 0; b < this.attrIndexLimit[paramInt]; b++) {
      if (!testBit(this.attrDefSeen[paramInt], 1L << b)) {
        Attribute.Layout layout = (Attribute.Layout)((List)this.attrDefs.get(paramInt)).get(b);
        if (layout != null) {
          assert isPredefinedAttr(paramInt, b);
          arrayList.add(layout);
        } 
      } 
    } 
    return arrayList;
  }
  
  protected boolean isPredefinedAttr(int paramInt1, int paramInt2) {
    assert this.attrIndexLimit[paramInt1] != 0;
    return (paramInt2 >= this.attrIndexLimit[paramInt1]) ? false : (testBit(this.attrDefSeen[paramInt1], 1L << paramInt2) ? false : ((((List)this.attrDefs.get(paramInt1)).get(paramInt2) != null)));
  }
  
  protected void adjustSpecialAttrMasks() {
    this.attrClassFileVersionMask = (int)(this.attrClassFileVersionMask & (this.attrDefSeen[0] ^ 0xFFFFFFFFFFFFFFFFL));
    for (byte b = 0; b < 4; b++)
      this.attrOverflowMask[b] = (int)(this.attrOverflowMask[b] & (this.attrDefSeen[b] ^ 0xFFFFFFFFFFFFFFFFL)); 
  }
  
  protected Attribute makeClassFileVersionAttr(Package.Version paramVersion) { return this.attrClassFileVersion.addContent(paramVersion.asBytes()); }
  
  protected Package.Version parseClassFileVersionAttr(Attribute paramAttribute) {
    assert paramAttribute.layout() == this.attrClassFileVersion;
    assert paramAttribute.size() == 4;
    return Package.Version.of(paramAttribute.bytes());
  }
  
  private boolean assertBandOKForElems(Band[] paramArrayOfBand, Attribute.Layout.Element[] paramArrayOfElement) {
    for (byte b = 0; b < paramArrayOfElement.length; b++)
      assert assertBandOKForElem(paramArrayOfBand, paramArrayOfElement[b]); 
    return true;
  }
  
  private boolean assertBandOKForElem(Band[] paramArrayOfBand, Attribute.Layout.Element paramElement) {
    Band band = null;
    if (paramElement.bandIndex != -1)
      band = paramArrayOfBand[paramElement.bandIndex]; 
    Coding coding = UNSIGNED5;
    boolean bool = true;
    switch (paramElement.kind) {
      case 1:
        if (paramElement.flagTest((byte)1)) {
          coding = SIGNED5;
          break;
        } 
        if (paramElement.len == 1)
          coding = BYTE1; 
        break;
      case 2:
        if (!paramElement.flagTest((byte)2)) {
          coding = BCI5;
          break;
        } 
        coding = BRANCH5;
        break;
      case 3:
        coding = BRANCH5;
        break;
      case 4:
        if (paramElement.len == 1)
          coding = BYTE1; 
        break;
      case 5:
        if (paramElement.len == 1)
          coding = BYTE1; 
        assertBandOKForElems(paramArrayOfBand, paramElement.body);
        break;
      case 7:
        if (paramElement.flagTest((byte)1)) {
          coding = SIGNED5;
        } else if (paramElement.len == 1) {
          coding = BYTE1;
        } 
        assertBandOKForElems(paramArrayOfBand, paramElement.body);
        break;
      case 8:
        assert band == null;
        assertBandOKForElems(paramArrayOfBand, paramElement.body);
        return true;
      case 9:
        assert band == null;
        return true;
      case 10:
        assert band == null;
        assertBandOKForElems(paramArrayOfBand, paramElement.body);
        return true;
      case 6:
        bool = false;
        assert band instanceof CPRefBand;
        assert ((CPRefBand)band).nullOK == paramElement.flagTest((byte)4);
        break;
      default:
        assert false;
        break;
    } 
    assert band.regularCoding == coding : paramElement + " // " + band;
    if (bool && !$assertionsDisabled && !(band instanceof IntBand))
      throw new AssertionError(); 
    return true;
  }
  
  private Attribute.Layout predefineAttribute(int paramInt1, int paramInt2, Band[] paramArrayOfBand, String paramString1, String paramString2) {
    Attribute.Layout layout = Attribute.find(paramInt2, paramString1, paramString2).layout();
    if (paramInt1 >= 0)
      setAttributeLayoutIndex(layout, paramInt1); 
    if (paramArrayOfBand == null)
      paramArrayOfBand = new Band[0]; 
    assert this.attrBandTable.get(layout) == null;
    this.attrBandTable.put(layout, paramArrayOfBand);
    assert layout.bandCount == paramArrayOfBand.length : layout + " // " + Arrays.asList(paramArrayOfBand);
    assert assertBandOKForElems(paramArrayOfBand, layout.elems);
    return layout;
  }
  
  private Attribute.Layout predefineAttribute(int paramInt, String paramString, MultiBand paramMultiBand, Attribute paramAttribute) {
    Attribute.Layout layout = paramAttribute.layout();
    int i = layout.ctype();
    return predefineAttribute(paramInt, i, makeNewAttributeBands(paramString, layout, paramMultiBand), layout.name(), layout.layout());
  }
  
  private void undefineAttribute(int paramInt1, int paramInt2) {
    if (this.verbose > 1)
      System.out.println("Removing predefined " + Constants.ATTR_CONTEXT_NAME[paramInt2] + " attribute on bit " + paramInt1); 
    List list = (List)this.attrDefs.get(paramInt2);
    Attribute.Layout layout = (Attribute.Layout)list.get(paramInt1);
    assert layout != null;
    list.set(paramInt1, null);
    this.attrIndexTable.put(layout, null);
    assert paramInt1 < 64;
    this.attrDefSeen[paramInt2] = this.attrDefSeen[paramInt2] & (1L << paramInt1 ^ 0xFFFFFFFFFFFFFFFFL);
    this.attrFlagMask[paramInt2] = this.attrFlagMask[paramInt2] & (1L << paramInt1 ^ 0xFFFFFFFFFFFFFFFFL);
    Band[] arrayOfBand = (Band[])this.attrBandTable.get(layout);
    for (byte b = 0; b < arrayOfBand.length; b++)
      arrayOfBand[b].doneWithUnusedBand(); 
  }
  
  void makeNewAttributeBands() {
    adjustSpecialAttrMasks();
    for (byte b = 0; b < 4; b++) {
      String str = Constants.ATTR_CONTEXT_NAME[b];
      MultiBand multiBand = this.attrBands[b];
      long l = this.attrDefSeen[b];
      assert (l & (this.attrFlagMask[b] ^ 0xFFFFFFFFFFFFFFFFL)) == 0L;
      for (byte b1 = 0; b1 < ((List)this.attrDefs.get(b)).size(); b1++) {
        Attribute.Layout layout = (Attribute.Layout)((List)this.attrDefs.get(b)).get(b1);
        if (layout != null && layout.bandCount != 0)
          if (b1 < this.attrIndexLimit[b] && !testBit(l, 1L << b1)) {
            assert this.attrBandTable.get(layout) != null;
          } else {
            int i = multiBand.size();
            String str1 = str + "_" + layout.name() + "_";
            if (this.verbose > 1)
              Utils.log.fine("Making new bands for " + layout); 
            Band[] arrayOfBand = makeNewAttributeBands(str1, layout, multiBand);
            assert arrayOfBand.length == layout.bandCount;
            Band[] arrayOfBand1 = (Band[])this.attrBandTable.put(layout, arrayOfBand);
            if (arrayOfBand1 != null)
              for (byte b2 = 0; b2 < arrayOfBand1.length; b2++)
                arrayOfBand1[b2].doneWithUnusedBand();  
          }  
      } 
    } 
  }
  
  private Band[] makeNewAttributeBands(String paramString, Attribute.Layout paramLayout, MultiBand paramMultiBand) {
    int i = paramMultiBand.size();
    makeNewAttributeBands(paramString, paramLayout.elems, paramMultiBand);
    int j = paramMultiBand.size() - i;
    Band[] arrayOfBand = new Band[j];
    for (int k = 0; k < j; k++)
      arrayOfBand[k] = paramMultiBand.get(i + k); 
    return arrayOfBand;
  }
  
  private void makeNewAttributeBands(String paramString, Attribute.Layout.Element[] paramArrayOfElement, MultiBand paramMultiBand) {
    for (byte b = 0;; b++) {
      if (b < paramArrayOfElement.length) {
        boolean bool;
        byte b1;
        Band band;
        Attribute.Layout.Element element = paramArrayOfElement[b];
        String str = paramString + paramMultiBand.size() + "_" + element.layout;
        int i;
        if ((i = str.indexOf('[')) > 0)
          str = str.substring(0, i); 
        if ((i = str.indexOf('(')) > 0)
          str = str.substring(0, i); 
        if (str.endsWith("H"))
          str = str.substring(0, str.length() - 1); 
        switch (element.kind) {
          case 1:
            band = newElemBand(element, str, paramMultiBand);
            if (this.verbose > 1)
              Utils.log.fine("New attribute band " + band); 
            b++;
            continue;
          case 2:
            if (!element.flagTest((byte)2)) {
              band = paramMultiBand.newIntBand(str, BCI5);
            } else {
              band = paramMultiBand.newIntBand(str, BRANCH5);
            } 
            if (this.verbose > 1)
              Utils.log.fine("New attribute band " + band); 
            b++;
            continue;
          case 3:
            band = paramMultiBand.newIntBand(str, BRANCH5);
            if (this.verbose > 1)
              Utils.log.fine("New attribute band " + band); 
            b++;
            continue;
          case 4:
            assert !element.flagTest((byte)1);
            band = newElemBand(element, str, paramMultiBand);
            if (this.verbose > 1)
              Utils.log.fine("New attribute band " + band); 
            b++;
            continue;
          case 5:
            assert !element.flagTest((byte)1);
            band = newElemBand(element, str, paramMultiBand);
            makeNewAttributeBands(paramString, element.body, paramMultiBand);
            if (this.verbose > 1)
              Utils.log.fine("New attribute band " + band); 
            b++;
            continue;
          case 7:
            band = newElemBand(element, str, paramMultiBand);
            makeNewAttributeBands(paramString, element.body, paramMultiBand);
            if (this.verbose > 1)
              Utils.log.fine("New attribute band " + band); 
            b++;
            continue;
          case 8:
            if (!element.flagTest((byte)8))
              makeNewAttributeBands(paramString, element.body, paramMultiBand); 
            break;
          case 6:
            b1 = element.refKind;
            bool = element.flagTest((byte)4);
            band = paramMultiBand.newCPRefBand(str, UNSIGNED5, b1, bool);
            if (this.verbose > 1)
              Utils.log.fine("New attribute band " + band); 
            b++;
            continue;
          case 9:
            break;
          case 10:
            makeNewAttributeBands(paramString, element.body, paramMultiBand);
            break;
          default:
            assert false;
            break;
        } 
      } else {
        break;
      } 
    } 
  }
  
  private Band newElemBand(Attribute.Layout.Element paramElement, String paramString, MultiBand paramMultiBand) { return paramElement.flagTest((byte)1) ? paramMultiBand.newIntBand(paramString, SIGNED5) : ((paramElement.len == 1) ? paramMultiBand.newIntBand(paramString, BYTE1) : paramMultiBand.newIntBand(paramString, UNSIGNED5)); }
  
  protected int setAttributeLayoutIndex(Attribute.Layout paramLayout, int paramInt) {
    int i = paramLayout.ctype;
    assert -1 <= paramInt && paramInt < this.attrIndexLimit[i];
    List list = (List)this.attrDefs.get(i);
    if (paramInt == -1) {
      paramInt = list.size();
      list.add(paramLayout);
      if (this.verbose > 0)
        Utils.log.info("Adding new attribute at " + paramLayout + ": " + paramInt); 
      this.attrIndexTable.put(paramLayout, Integer.valueOf(paramInt));
      return paramInt;
    } 
    if (testBit(this.attrDefSeen[i], 1L << paramInt))
      throw new RuntimeException("Multiple explicit definition at " + paramInt + ": " + paramLayout); 
    this.attrDefSeen[i] = this.attrDefSeen[i] | 1L << paramInt;
    assert 0 <= paramInt && paramInt < this.attrIndexLimit[i];
    if (this.verbose > ((this.attrClassFileVersionMask == 0) ? 2 : 0))
      Utils.log.fine("Fixing new attribute at " + paramInt + ": " + paramLayout + ((list.get(paramInt) == null) ? "" : ("; replacing " + list.get(paramInt)))); 
    this.attrFlagMask[i] = this.attrFlagMask[i] | 1L << paramInt;
    this.attrIndexTable.put(list.get(paramInt), null);
    list.set(paramInt, paramLayout);
    this.attrIndexTable.put(paramLayout, Integer.valueOf(paramInt));
    return paramInt;
  }
  
  static int shortCodeHeader(Code paramCode) {
    int i = paramCode.max_stack;
    int j = paramCode.max_locals;
    int k = paramCode.handler_class.length;
    if (k >= shortCodeLimits.length)
      return 0; 
    int m = paramCode.getMethod().getArgumentSize();
    assert j >= m;
    if (j < m)
      return 0; 
    int n = j - m;
    int i1 = shortCodeLimits[k][0];
    int i2 = shortCodeLimits[k][1];
    if (i >= i1 || n >= i2)
      return 0; 
    int i3 = shortCodeHeader_h_base(k);
    i3 += i + i1 * n;
    if (i3 > 255)
      return 0; 
    assert shortCodeHeader_max_stack(i3) == i;
    assert shortCodeHeader_max_na_locals(i3) == n;
    assert shortCodeHeader_handler_count(i3) == k;
    return i3;
  }
  
  static int shortCodeHeader_handler_count(int paramInt) {
    assert paramInt > 0 && paramInt <= 255;
    for (byte b = 0;; b++) {
      if (paramInt < shortCodeHeader_h_base(b + true))
        return b; 
    } 
  }
  
  static int shortCodeHeader_max_stack(int paramInt) {
    int i = shortCodeHeader_handler_count(paramInt);
    int j = shortCodeLimits[i][0];
    return (paramInt - shortCodeHeader_h_base(i)) % j;
  }
  
  static int shortCodeHeader_max_na_locals(int paramInt) {
    int i = shortCodeHeader_handler_count(paramInt);
    int j = shortCodeLimits[i][0];
    return (paramInt - shortCodeHeader_h_base(i)) / j;
  }
  
  private static int shortCodeHeader_h_base(int paramInt) {
    assert paramInt <= shortCodeLimits.length;
    int i = 1;
    for (byte b = 0; b < paramInt; b++) {
      int j = shortCodeLimits[b][0];
      int k = shortCodeLimits[b][1];
      i += j * k;
    } 
    return i;
  }
  
  protected void putLabel(IntBand paramIntBand, Code paramCode, int paramInt1, int paramInt2) { paramIntBand.putInt(paramCode.encodeBCI(paramInt2) - paramCode.encodeBCI(paramInt1)); }
  
  protected int getLabel(IntBand paramIntBand, Code paramCode, int paramInt) { return paramCode.decodeBCI(paramIntBand.getInt() + paramCode.encodeBCI(paramInt)); }
  
  protected CPRefBand getCPRefOpBand(int paramInt) {
    switch (Instruction.getCPRefOpTag(paramInt)) {
      case 7:
        return this.bc_classref;
      case 9:
        return this.bc_fieldref;
      case 10:
        return this.bc_methodref;
      case 11:
        return this.bc_imethodref;
      case 18:
        return this.bc_indyref;
      case 51:
        switch (paramInt) {
          case 234:
          case 237:
            return this.bc_intref;
          case 235:
          case 238:
            return this.bc_floatref;
          case 20:
            return this.bc_longref;
          case 239:
            return this.bc_doubleref;
          case 18:
          case 19:
            return this.bc_stringref;
          case 233:
          case 236:
            return this.bc_classref;
          case 240:
          case 241:
            return this.bc_loadablevalueref;
        } 
        break;
    } 
    assert false;
    return null;
  }
  
  protected CPRefBand selfOpRefBand(int paramInt) {
    assert Instruction.isSelfLinkerOp(paramInt);
    int i = paramInt - 202;
    boolean bool1 = (i >= 14) ? 1 : 0;
    if (bool1)
      i -= 14; 
    boolean bool2 = (i >= 7) ? 1 : 0;
    if (bool2)
      i -= 7; 
    int j = 178 + i;
    boolean bool = Instruction.isFieldOp(j);
    return !bool1 ? (bool ? this.bc_thisfield : this.bc_thismethod) : (bool ? this.bc_superfield : this.bc_supermethod);
  }
  
  static OutputStream getDumpStream(Band paramBand, String paramString) throws IOException { return getDumpStream(paramBand.name, paramBand.seqForDebug, paramString, paramBand); }
  
  static OutputStream getDumpStream(ConstantPool.Index paramIndex, String paramString) throws IOException {
    if (paramIndex.size() == 0)
      return new ByteArrayOutputStream(); 
    byte b = ConstantPool.TAG_ORDER[(paramIndex.cpMap[0]).tag];
    return getDumpStream(paramIndex.debugName, b, paramString, paramIndex);
  }
  
  static OutputStream getDumpStream(String paramString1, int paramInt, String paramString2, Object paramObject) throws IOException {
    if (dumpDir == null) {
      dumpDir = File.createTempFile("BD_", "", new File("."));
      dumpDir.delete();
      if (dumpDir.mkdir())
        Utils.log.info("Dumping bands to " + dumpDir); 
    } 
    paramString1 = paramString1.replace('(', ' ').replace(')', ' ');
    paramString1 = paramString1.replace('/', ' ');
    paramString1 = paramString1.replace('*', ' ');
    paramString1 = paramString1.trim().replace(' ', '_');
    paramString1 = ((10000 + paramInt) + "_" + paramString1).substring(1);
    File file = new File(dumpDir, paramString1 + paramString2);
    Utils.log.info("Dumping " + paramObject + " to " + file);
    return new BufferedOutputStream(new FileOutputStream(file));
  }
  
  static boolean assertCanChangeLength(Band paramBand) {
    switch (paramBand.phase) {
      case 1:
      case 4:
        return true;
    } 
    return false;
  }
  
  static boolean assertPhase(Band paramBand, int paramInt) {
    if (paramBand.phase() != paramInt) {
      Utils.log.warning("phase expected " + paramInt + " was " + paramBand.phase() + " in " + paramBand);
      return false;
    } 
    return true;
  }
  
  static int verbose() { return Utils.currentPropMap().getInteger("com.sun.java.util.jar.pack.verbose"); }
  
  static boolean assertPhaseChangeOK(Band paramBand, int paramInt1, int paramInt2) {
    switch (paramInt1 * 10 + paramInt2) {
      case 1:
        assert !paramBand.isReader();
        assert paramBand.capacity() >= 0;
        assert paramBand.length() == 0;
        return true;
      case 13:
      case 33:
        assert paramBand.length() == 0;
        return true;
      case 15:
      case 35:
        return true;
      case 58:
        return true;
      case 2:
        assert paramBand.isReader();
        assert paramBand.capacity() < 0;
        return true;
      case 24:
        assert Math.max(0, paramBand.capacity()) >= paramBand.valuesExpected();
        assert paramBand.length() <= 0;
        return true;
      case 46:
        assert paramBand.valuesRemainingForDebug() == paramBand.length();
        return true;
      case 68:
        assert assertDoneDisbursing(paramBand);
        return true;
    } 
    if (paramInt1 == paramInt2) {
      Utils.log.warning("Already in phase " + paramInt1);
    } else {
      Utils.log.warning("Unexpected phase " + paramInt1 + " -> " + paramInt2);
    } 
    return false;
  }
  
  private static boolean assertDoneDisbursing(Band paramBand) {
    if (paramBand.phase != 6) {
      Utils.log.warning("assertDoneDisbursing: still in phase " + paramBand.phase + ": " + paramBand);
      if (verbose() <= 1)
        return false; 
    } 
    int i = paramBand.valuesRemainingForDebug();
    if (i > 0) {
      Utils.log.warning("assertDoneDisbursing: " + i + " values left in " + paramBand);
      if (verbose() <= 1)
        return false; 
    } 
    if (paramBand instanceof MultiBand) {
      MultiBand multiBand = (MultiBand)paramBand;
      for (byte b = 0; b < multiBand.bandCount; b++) {
        Band band;
        if (band.phase != 8) {
          Utils.log.warning("assertDoneDisbursing: sub-band still in phase " + band.phase + ": " + band);
          if (verbose() <= 1)
            return false; 
        } 
      } 
    } 
    return true;
  }
  
  private static void printCDecl(Band paramBand) {
    String str2;
    if (paramBand instanceof MultiBand) {
      MultiBand multiBand = (MultiBand)paramBand;
      for (byte b = 0; b < multiBand.bandCount; b++)
        printCDecl(multiBand.bands[b]); 
      return;
    } 
    String str1 = "NULL";
    if (paramBand instanceof CPRefBand) {
      ConstantPool.Index index = ((CPRefBand)paramBand).index;
      if (index != null)
        str1 = "INDEX(" + index.debugName + ")"; 
    } 
    Coding[] arrayOfCoding = { BYTE1, CHAR3, BCI5, BRANCH5, UNSIGNED5, UDELTA5, SIGNED5, DELTA5, MDELTA5 };
    String[] arrayOfString = { "BYTE1", "CHAR3", "BCI5", "BRANCH5", "UNSIGNED5", "UDELTA5", "SIGNED5", "DELTA5", "MDELTA5" };
    Coding coding = paramBand.regularCoding;
    int i = Arrays.asList(arrayOfCoding).indexOf(coding);
    if (i >= 0) {
      str2 = arrayOfString[i];
    } else {
      str2 = "CODING" + coding.keyString();
    } 
    System.out.println("  BAND_INIT(\"" + paramBand.name() + "\", " + str2 + ", " + str1 + "),");
  }
  
  boolean notePrevForAssert(Band paramBand1, Band paramBand2) {
    if (this.prevForAssertMap == null)
      this.prevForAssertMap = new HashMap(); 
    this.prevForAssertMap.put(paramBand1, paramBand2);
    return true;
  }
  
  private boolean assertReadyToReadFrom(Band paramBand, InputStream paramInputStream) throws IOException {
    Band band = (Band)this.prevForAssertMap.get(paramBand);
    if (band != null && phaseCmp(band.phase(), 6) < 0) {
      Utils.log.warning("Previous band not done reading.");
      Utils.log.info("    Previous band: " + band);
      Utils.log.info("        Next band: " + paramBand);
      assert this.verbose > 0;
    } 
    String str = paramBand.name;
    if (this.optDebugBands && !str.startsWith("(")) {
      assert bandSequenceList != null;
      String str1 = (String)bandSequenceList.removeFirst();
      if (!str1.equals(str)) {
        Utils.log.warning("Expected " + str + " but read: " + str1);
        return false;
      } 
      Utils.log.info("Read band in sequence: " + str);
    } 
    return true;
  }
  
  private boolean assertValidCPRefs(CPRefBand paramCPRefBand) {
    if (paramCPRefBand.index == null)
      return true; 
    int i = paramCPRefBand.index.size() + 1;
    for (byte b = 0; b < paramCPRefBand.length(); b++) {
      int j = paramCPRefBand.valueAtForDebug(b);
      if (j < 0 || j >= i) {
        Utils.log.warning("CP ref out of range [" + b + "] = " + j + " in " + paramCPRefBand);
        return false;
      } 
    } 
    return true;
  }
  
  private boolean assertReadyToWriteTo(Band paramBand, OutputStream paramOutputStream) throws IOException {
    Band band = (Band)this.prevForAssertMap.get(paramBand);
    if (band != null && phaseCmp(band.phase(), 8) < 0) {
      Utils.log.warning("Previous band not done writing.");
      Utils.log.info("    Previous band: " + band);
      Utils.log.info("        Next band: " + paramBand);
      assert this.verbose > 0;
    } 
    String str = paramBand.name;
    if (this.optDebugBands && !str.startsWith("(")) {
      if (bandSequenceList == null)
        bandSequenceList = new LinkedList(); 
      bandSequenceList.add(str);
    } 
    return true;
  }
  
  protected static boolean testBit(int paramInt1, int paramInt2) { return ((paramInt1 & paramInt2) != 0); }
  
  protected static int setBit(int paramInt1, int paramInt2, boolean paramBoolean) { return paramBoolean ? (paramInt1 | paramInt2) : (paramInt1 & (paramInt2 ^ 0xFFFFFFFF)); }
  
  protected static boolean testBit(long paramLong1, long paramLong2) { return ((paramLong1 & paramLong2) != 0L); }
  
  protected static long setBit(long paramLong1, long paramLong2, boolean paramBoolean) { return paramBoolean ? (paramLong1 | paramLong2) : (paramLong1 & (paramLong2 ^ 0xFFFFFFFFFFFFFFFFL)); }
  
  static void printArrayTo(PrintStream paramPrintStream, int[] paramArrayOfInt, int paramInt1, int paramInt2) {
    int i = paramInt2 - paramInt1;
    for (int j = 0; j < i; j++) {
      if (j % 10 == 0) {
        paramPrintStream.println();
      } else {
        paramPrintStream.print(" ");
      } 
      paramPrintStream.print(paramArrayOfInt[paramInt1 + j]);
    } 
    paramPrintStream.println();
  }
  
  static void printArrayTo(PrintStream paramPrintStream, ConstantPool.Entry[] paramArrayOfEntry, int paramInt1, int paramInt2) { printArrayTo(paramPrintStream, paramArrayOfEntry, paramInt1, paramInt2, false); }
  
  static void printArrayTo(PrintStream paramPrintStream, ConstantPool.Entry[] paramArrayOfEntry, int paramInt1, int paramInt2, boolean paramBoolean) {
    StringBuffer stringBuffer = new StringBuffer();
    int i = paramInt2 - paramInt1;
    for (int j = 0; j < i; j++) {
      ConstantPool.Entry entry = paramArrayOfEntry[paramInt1 + j];
      paramPrintStream.print(paramInt1 + j);
      paramPrintStream.print("=");
      if (paramBoolean) {
        paramPrintStream.print(entry.tag);
        paramPrintStream.print(":");
      } 
      String str = entry.stringValue();
      stringBuffer.setLength(0);
      for (byte b = 0; b < str.length(); b++) {
        char c = str.charAt(b);
        if (c >= ' ' && c <= '~' && c != '\\') {
          stringBuffer.append(c);
        } else if (c == '\\') {
          stringBuffer.append("\\\\");
        } else if (c == '\n') {
          stringBuffer.append("\\n");
        } else if (c == '\t') {
          stringBuffer.append("\\t");
        } else if (c == '\r') {
          stringBuffer.append("\\r");
        } else {
          String str1 = "000" + Integer.toHexString(c);
          stringBuffer.append("\\u").append(str1.substring(str1.length() - 4));
        } 
      } 
      paramPrintStream.println(stringBuffer);
    } 
  }
  
  protected static Object[] realloc(Object[] paramArrayOfObject, int paramInt) {
    Class clazz = paramArrayOfObject.getClass().getComponentType();
    Object[] arrayOfObject = (Object[])Array.newInstance(clazz, paramInt);
    System.arraycopy(paramArrayOfObject, 0, arrayOfObject, 0, Math.min(paramArrayOfObject.length, paramInt));
    return arrayOfObject;
  }
  
  protected static Object[] realloc(Object[] paramArrayOfObject) { return realloc(paramArrayOfObject, Math.max(10, paramArrayOfObject.length * 2)); }
  
  protected static int[] realloc(int[] paramArrayOfInt, int paramInt) {
    if (paramInt == 0)
      return Constants.noInts; 
    if (paramArrayOfInt == null)
      return new int[paramInt]; 
    int[] arrayOfInt = new int[paramInt];
    System.arraycopy(paramArrayOfInt, 0, arrayOfInt, 0, Math.min(paramArrayOfInt.length, paramInt));
    return arrayOfInt;
  }
  
  protected static int[] realloc(int[] paramArrayOfInt) { return realloc(paramArrayOfInt, Math.max(10, paramArrayOfInt.length * 2)); }
  
  protected static byte[] realloc(byte[] paramArrayOfByte, int paramInt) {
    if (paramInt == 0)
      return Constants.noBytes; 
    if (paramArrayOfByte == null)
      return new byte[paramInt]; 
    byte[] arrayOfByte = new byte[paramInt];
    System.arraycopy(paramArrayOfByte, 0, arrayOfByte, 0, Math.min(paramArrayOfByte.length, paramInt));
    return arrayOfByte;
  }
  
  protected static byte[] realloc(byte[] paramArrayOfByte) { return realloc(paramArrayOfByte, Math.max(10, paramArrayOfByte.length * 2)); }
  
  static  {
    assert basicCodings[false] == null;
    assert basicCodings[true] != null;
    assert basicCodings[115] != null;
    HashMap hashMap = new HashMap();
    byte b;
    for (b = 0; b < basicCodings.length; b++) {
      Coding coding = basicCodings[b];
      if (coding != null) {
        assert b >= 1;
        assert b <= 115;
        hashMap.put(coding, Integer.valueOf(b));
      } 
    } 
    basicCodingIndexes = hashMap;
    defaultMetaCoding = new byte[] { 0 };
    noMetaCoding = new byte[0];
    boolean bool = false;
    assert bool = true;
    if (bool)
      for (b = 0; b < basicCodings.length; b++) {
        Coding coding = basicCodings[b];
        if (coding != null && coding.B() != 1 && coding.L() != 0)
          for (byte b1 = 0; b1 <= 'ÿ'; b1++)
            encodeEscapeValue(b1, coding);  
      }  
    shortCodeLimits = new int[][] { { 12, 12 }, { 8, 8 }, { 7, 7 } };
    dumpDir = null;
    bandSequenceList = null;
  }
  
  abstract class Band {
    private int phase = 0;
    
    private final String name;
    
    private int valuesExpected;
    
    protected long outputSize = -1L;
    
    public final Coding regularCoding;
    
    public final int seqForDebug;
    
    public int elementCountForDebug;
    
    protected int lengthForDebug = -1;
    
    protected Band(String param1String, Coding param1Coding) {
      this.name = param1String;
      this.regularCoding = param1Coding;
      this.seqForDebug = ++BandStructure.nextSeqForDebug;
      if (BandStructure.this.verbose > 2)
        Utils.log.fine("Band " + this.seqForDebug + " is " + param1String); 
    }
    
    public Band init() {
      if (BandStructure.this.isReader) {
        readyToExpect();
      } else {
        readyToCollect();
      } 
      return this;
    }
    
    boolean isReader() { return BandStructure.this.isReader; }
    
    int phase() { return this.phase; }
    
    String name() { return this.name; }
    
    public abstract int capacity();
    
    protected abstract void setCapacity(int param1Int);
    
    public abstract int length();
    
    protected abstract int valuesRemainingForDebug();
    
    public final int valuesExpected() { return this.valuesExpected; }
    
    public final void writeTo(OutputStream param1OutputStream) throws IOException {
      assert BandStructure.this.assertReadyToWriteTo(this, param1OutputStream);
      setPhase(5);
      writeDataTo(param1OutputStream);
      doneWriting();
    }
    
    abstract void chooseBandCodings();
    
    public final long outputSize() {
      if (this.outputSize >= 0L) {
        long l = this.outputSize;
        assert l == computeOutputSize();
        return l;
      } 
      return computeOutputSize();
    }
    
    protected abstract long computeOutputSize();
    
    protected abstract void writeDataTo(OutputStream param1OutputStream) throws IOException;
    
    void expectLength(int param1Int) {
      assert BandStructure.assertPhase(this, 2);
      assert this.valuesExpected == 0;
      assert param1Int >= 0;
      this.valuesExpected = param1Int;
    }
    
    void expectMoreLength(int param1Int) {
      assert BandStructure.assertPhase(this, 2);
      this.valuesExpected += param1Int;
    }
    
    private void readyToCollect() {
      setCapacity(1);
      setPhase(1);
    }
    
    protected void doneWriting() {
      assert BandStructure.assertPhase(this, 5);
      setPhase(8);
    }
    
    private void readyToExpect() { setPhase(2); }
    
    public final void readFrom(InputStream param1InputStream) throws IOException {
      assert BandStructure.this.assertReadyToReadFrom(this, param1InputStream);
      setCapacity(valuesExpected());
      setPhase(4);
      readDataFrom(param1InputStream);
      readyToDisburse();
    }
    
    protected abstract void readDataFrom(InputStream param1InputStream) throws IOException;
    
    protected void readyToDisburse() {
      if (BandStructure.this.verbose > 1)
        Utils.log.fine("readyToDisburse " + this); 
      setPhase(6);
    }
    
    public void doneDisbursing() {
      assert BandStructure.assertPhase(this, 6);
      setPhase(8);
    }
    
    public final void doneWithUnusedBand() {
      if (BandStructure.this.isReader) {
        assert BandStructure.assertPhase(this, 2);
        assert valuesExpected() == 0;
        setPhase(4);
        setPhase(6);
        setPhase(8);
      } else {
        setPhase(3);
      } 
    }
    
    protected void setPhase(int param1Int) {
      assert BandStructure.assertPhaseChangeOK(this, this.phase, param1Int);
      this.phase = param1Int;
    }
    
    public String toString() {
      int i = (this.lengthForDebug != -1) ? this.lengthForDebug : length();
      String str = this.name;
      if (i != 0)
        str = str + "[" + i + "]"; 
      if (this.elementCountForDebug != 0)
        str = str + "(" + this.elementCountForDebug + ")"; 
      return str;
    }
  }
  
  class ByteBand extends Band {
    private ByteArrayOutputStream bytes;
    
    private ByteArrayOutputStream bytesForDump;
    
    private InputStream in;
    
    public ByteBand(String param1String) { super(BandStructure.this, param1String, BandStructure.BYTE1); }
    
    public int capacity() { return (this.bytes == null) ? -1 : Integer.MAX_VALUE; }
    
    protected void setCapacity(int param1Int) {
      assert this.bytes == null;
      this.bytes = new ByteArrayOutputStream(param1Int);
    }
    
    public void destroy() {
      this.lengthForDebug = length();
      this.bytes = null;
    }
    
    public int length() { return (this.bytes == null) ? -1 : this.bytes.size(); }
    
    public void reset() { this.bytes.reset(); }
    
    protected int valuesRemainingForDebug() { return (this.bytes == null) ? -1 : ((ByteArrayInputStream)this.in).available(); }
    
    protected void chooseBandCodings() {
      assert BandStructure.decodeEscapeValue(this.regularCoding.min(), this.regularCoding) < 0;
      assert BandStructure.decodeEscapeValue(this.regularCoding.max(), this.regularCoding) < 0;
    }
    
    protected long computeOutputSize() { return this.bytes.size(); }
    
    public void writeDataTo(OutputStream param1OutputStream) throws IOException {
      if (length() == 0)
        return; 
      this.bytes.writeTo(param1OutputStream);
      if (BandStructure.this.optDumpBands)
        dumpBand(); 
      destroy();
    }
    
    private void dumpBand() {
      assert BandStructure.this.optDumpBands;
      try (OutputStream null = BandStructure.getDumpStream(this, ".bnd")) {
        if (this.bytesForDump != null) {
          this.bytesForDump.writeTo(outputStream);
        } else {
          this.bytes.writeTo(outputStream);
        } 
      } 
    }
    
    public void readDataFrom(InputStream param1InputStream) throws IOException {
      int i = valuesExpected();
      if (i == 0)
        return; 
      if (BandStructure.this.verbose > 1) {
        this.lengthForDebug = i;
        Utils.log.fine("Reading band " + this);
        this.lengthForDebug = -1;
      } 
      byte[] arrayOfByte = new byte[Math.min(i, 16384)];
      while (i > 0) {
        int j = param1InputStream.read(arrayOfByte, 0, Math.min(i, arrayOfByte.length));
        if (j < 0)
          throw new EOFException(); 
        this.bytes.write(arrayOfByte, 0, j);
        i -= j;
      } 
      if (BandStructure.this.optDumpBands)
        dumpBand(); 
    }
    
    public void readyToDisburse() {
      this.in = new ByteArrayInputStream(this.bytes.toByteArray());
      super.readyToDisburse();
    }
    
    public void doneDisbursing() {
      super.doneDisbursing();
      if (BandStructure.this.optDumpBands && this.bytesForDump != null && this.bytesForDump.size() > 0)
        try {
          dumpBand();
        } catch (IOException iOException) {
          throw new RuntimeException(iOException);
        }  
      this.in = null;
      this.bytes = null;
      this.bytesForDump = null;
    }
    
    public void setInputStreamFrom(InputStream param1InputStream) throws IOException {
      assert this.bytes == null;
      assert BandStructure.this.assertReadyToReadFrom(this, param1InputStream);
      setPhase(4);
      this.in = param1InputStream;
      if (BandStructure.this.optDumpBands) {
        this.bytesForDump = new ByteArrayOutputStream();
        this.in = new FilterInputStream(param1InputStream) {
            public int read() {
              int i = this.in.read();
              if (i >= 0)
                BandStructure.ByteBand.this.bytesForDump.write(i); 
              return i;
            }
            
            public int read(byte[] param2ArrayOfByte, int param2Int1, int param2Int2) throws IOException {
              int i = this.in.read(param2ArrayOfByte, param2Int1, param2Int2);
              if (i >= 0)
                BandStructure.ByteBand.this.bytesForDump.write(param2ArrayOfByte, param2Int1, i); 
              return i;
            }
          };
      } 
      super.readyToDisburse();
    }
    
    public OutputStream collectorStream() {
      assert phase() == 1;
      assert this.bytes != null;
      return this.bytes;
    }
    
    public InputStream getInputStream() {
      assert phase() == 6;
      assert this.in != null;
      return this.in;
    }
    
    public int getByte() {
      int i = getInputStream().read();
      if (i < 0)
        throw new EOFException(); 
      return i;
    }
    
    public void putByte(int param1Int) {
      assert param1Int == (param1Int & 0xFF);
      collectorStream().write(param1Int);
    }
    
    public String toString() { return "byte " + super.toString(); }
  }
  
  private static class ByteCounter extends FilterOutputStream {
    private long count;
    
    public ByteCounter(OutputStream param1OutputStream) throws IOException { super(param1OutputStream); }
    
    public long getCount() { return this.count; }
    
    public void setCount(long param1Long) { this.count = param1Long; }
    
    public void write(int param1Int) {
      this.count++;
      if (this.out != null)
        this.out.write(param1Int); 
    }
    
    public void write(byte[] param1ArrayOfByte, int param1Int1, int param1Int2) throws IOException {
      this.count += param1Int2;
      if (this.out != null)
        this.out.write(param1ArrayOfByte, param1Int1, param1Int2); 
    }
    
    public String toString() { return String.valueOf(getCount()); }
  }
  
  class CPRefBand extends ValueBand {
    ConstantPool.Index index;
    
    boolean nullOK;
    
    public CPRefBand(String param1String, Coding param1Coding, byte param1Byte, boolean param1Boolean) {
      super(BandStructure.this, param1String, param1Coding);
      this.nullOK = param1Boolean;
      if (param1Byte != 0)
        this$0.setBandIndex(this, param1Byte); 
    }
    
    public CPRefBand(String param1String, Coding param1Coding, byte param1Byte) { this(param1String, param1Coding, param1Byte, false); }
    
    public CPRefBand(String param1String, Coding param1Coding, Object param1Object) { this(param1String, param1Coding, (byte)0, false); }
    
    public void setIndex(ConstantPool.Index param1Index) { this.index = param1Index; }
    
    protected void readDataFrom(InputStream param1InputStream) throws IOException {
      super.readDataFrom(param1InputStream);
      assert BandStructure.this.assertValidCPRefs(this);
    }
    
    public void putRef(ConstantPool.Entry param1Entry) { addValue(encodeRefOrNull(param1Entry, this.index)); }
    
    public void putRef(ConstantPool.Entry param1Entry, ConstantPool.Index param1Index) {
      assert this.index == null;
      addValue(encodeRefOrNull(param1Entry, param1Index));
    }
    
    public void putRef(ConstantPool.Entry param1Entry, byte param1Byte) { putRef(param1Entry, BandStructure.this.getCPIndex(param1Byte)); }
    
    public ConstantPool.Entry getRef() {
      if (this.index == null)
        Utils.log.warning("No index for " + this); 
      assert this.index != null;
      return decodeRefOrNull(getValue(), this.index);
    }
    
    public ConstantPool.Entry getRef(ConstantPool.Index param1Index) {
      assert this.index == null;
      return decodeRefOrNull(getValue(), param1Index);
    }
    
    public ConstantPool.Entry getRef(byte param1Byte) { return getRef(BandStructure.this.getCPIndex(param1Byte)); }
    
    private int encodeRefOrNull(ConstantPool.Entry param1Entry, ConstantPool.Index param1Index) {
      int i;
      if (param1Entry == null) {
        i = -1;
      } else {
        i = BandStructure.this.encodeRef(param1Entry, param1Index);
      } 
      return (this.nullOK ? 1 : 0) + i;
    }
    
    private ConstantPool.Entry decodeRefOrNull(int param1Int, ConstantPool.Index param1Index) {
      int i = param1Int - (this.nullOK ? 1 : 0);
      return (i == -1) ? null : BandStructure.this.decodeRef(i, param1Index);
    }
  }
  
  class IntBand extends ValueBand {
    public IntBand(String param1String, Coding param1Coding) { super(BandStructure.this, param1String, param1Coding); }
    
    public void putInt(int param1Int) {
      assert phase() == 1;
      addValue(param1Int);
    }
    
    public int getInt() { return getValue(); }
    
    public int getIntTotal() {
      assert phase() == 6;
      assert valuesRemainingForDebug() == length();
      int i = 0;
      for (int j = length(); j > 0; j--)
        i += getInt(); 
      resetForSecondPass();
      return i;
    }
    
    public int getIntCount(int param1Int) {
      assert phase() == 6;
      assert valuesRemainingForDebug() == length();
      byte b = 0;
      for (int i = length(); i > 0; i--) {
        if (getInt() == param1Int)
          b++; 
      } 
      resetForSecondPass();
      return b;
    }
  }
  
  class MultiBand extends Band {
    BandStructure.Band[] bands = new BandStructure.Band[10];
    
    int bandCount = 0;
    
    private int cap = -1;
    
    MultiBand(String param1String, Coding param1Coding) { super(BandStructure.this, param1String, param1Coding); }
    
    public BandStructure.Band init() {
      super.init();
      setCapacity(0);
      if (phase() == 2) {
        setPhase(4);
        setPhase(6);
      } 
      return this;
    }
    
    int size() { return this.bandCount; }
    
    BandStructure.Band get(int param1Int) {
      assert param1Int < this.bandCount;
      return this.bands[param1Int];
    }
    
    BandStructure.Band[] toArray() { return (Band[])BandStructure.realloc(this.bands, this.bandCount); }
    
    void add(BandStructure.Band param1Band) {
      assert this.bandCount == 0 || BandStructure.this.notePrevForAssert(param1Band, this.bands[this.bandCount - 1]);
      if (this.bandCount == this.bands.length)
        this.bands = (Band[])BandStructure.realloc(this.bands); 
      this.bands[this.bandCount++] = param1Band;
    }
    
    BandStructure.ByteBand newByteBand(String param1String) {
      BandStructure.ByteBand byteBand = new BandStructure.ByteBand(BandStructure.this, param1String);
      byteBand.init();
      add(byteBand);
      return byteBand;
    }
    
    BandStructure.IntBand newIntBand(String param1String) {
      BandStructure.IntBand intBand = new BandStructure.IntBand(BandStructure.this, param1String, this.regularCoding);
      intBand.init();
      add(intBand);
      return intBand;
    }
    
    BandStructure.IntBand newIntBand(String param1String, Coding param1Coding) {
      BandStructure.IntBand intBand = new BandStructure.IntBand(BandStructure.this, param1String, param1Coding);
      intBand.init();
      add(intBand);
      return intBand;
    }
    
    MultiBand newMultiBand(String param1String, Coding param1Coding) {
      MultiBand multiBand = new MultiBand(BandStructure.this, param1String, param1Coding);
      multiBand.init();
      add(multiBand);
      return multiBand;
    }
    
    BandStructure.CPRefBand newCPRefBand(String param1String, byte param1Byte) {
      BandStructure.CPRefBand cPRefBand = new BandStructure.CPRefBand(BandStructure.this, param1String, this.regularCoding, param1Byte);
      cPRefBand.init();
      add(cPRefBand);
      return cPRefBand;
    }
    
    BandStructure.CPRefBand newCPRefBand(String param1String, Coding param1Coding, byte param1Byte) {
      BandStructure.CPRefBand cPRefBand = new BandStructure.CPRefBand(BandStructure.this, param1String, param1Coding, param1Byte);
      cPRefBand.init();
      add(cPRefBand);
      return cPRefBand;
    }
    
    BandStructure.CPRefBand newCPRefBand(String param1String, Coding param1Coding, byte param1Byte, boolean param1Boolean) {
      BandStructure.CPRefBand cPRefBand = new BandStructure.CPRefBand(BandStructure.this, param1String, param1Coding, param1Byte, param1Boolean);
      cPRefBand.init();
      add(cPRefBand);
      return cPRefBand;
    }
    
    int bandCount() { return this.bandCount; }
    
    public int capacity() { return this.cap; }
    
    public void setCapacity(int param1Int) { this.cap = param1Int; }
    
    public int length() { return 0; }
    
    public int valuesRemainingForDebug() { return 0; }
    
    protected void chooseBandCodings() {
      for (byte b = 0; b < this.bandCount; b++) {
        BandStructure.Band band = this.bands[b];
        band.chooseBandCodings();
      } 
    }
    
    protected long computeOutputSize() {
      long l = 0L;
      for (byte b = 0; b < this.bandCount; b++) {
        BandStructure.Band band = this.bands[b];
        long l1 = band.outputSize();
        assert l1 >= 0L : band;
        l += l1;
      } 
      return l;
    }
    
    protected void writeDataTo(OutputStream param1OutputStream) throws IOException {
      long l = 0L;
      if (BandStructure.this.outputCounter != null)
        l = BandStructure.this.outputCounter.getCount(); 
      for (byte b = 0; b < this.bandCount; b++) {
        BandStructure.Band band = this.bands[b];
        band.writeTo(param1OutputStream);
        if (BandStructure.this.outputCounter != null) {
          long l1 = BandStructure.this.outputCounter.getCount();
          long l2 = l1 - l;
          l = l1;
          if ((BandStructure.this.verbose > 0 && l2 > 0L) || BandStructure.this.verbose > 1)
            Utils.log.info("  ...wrote " + l2 + " bytes from " + band); 
        } 
      } 
    }
    
    protected void readDataFrom(InputStream param1InputStream) throws IOException {
      assert false;
      for (byte b = 0; b < this.bandCount; b++) {
        BandStructure.Band band = this.bands[b];
        band.readFrom(param1InputStream);
        if ((BandStructure.this.verbose > 0 && band.length() > 0) || BandStructure.this.verbose > 1)
          Utils.log.info("  ...read " + band); 
      } 
    }
    
    public String toString() { return "{" + bandCount() + " bands: " + super.toString() + "}"; }
  }
  
  class ValueBand extends Band {
    private int[] values;
    
    private int length;
    
    private int valuesDisbursed;
    
    private CodingMethod bandCoding;
    
    private byte[] metaCoding;
    
    protected ValueBand(String param1String, Coding param1Coding) { super(BandStructure.this, param1String, param1Coding); }
    
    public int capacity() { return (this.values == null) ? -1 : this.values.length; }
    
    protected void setCapacity(int param1Int) {
      assert this.length <= param1Int;
      if (param1Int == -1) {
        this.values = null;
        return;
      } 
      this.values = BandStructure.realloc(this.values, param1Int);
    }
    
    public int length() { return this.length; }
    
    protected int valuesRemainingForDebug() { return this.length - this.valuesDisbursed; }
    
    protected int valueAtForDebug(int param1Int) { return this.values[param1Int]; }
    
    void patchValue(int param1Int1, int param1Int2) {
      assert this == BandStructure.this.archive_header_S;
      assert param1Int1 == 0 || param1Int1 == 1;
      assert param1Int1 < this.length;
      this.values[param1Int1] = param1Int2;
      this.outputSize = -1L;
    }
    
    protected void initializeValues(int[] param1ArrayOfInt) {
      assert BandStructure.assertCanChangeLength(this);
      assert this.length == 0;
      this.values = param1ArrayOfInt;
      this.length = param1ArrayOfInt.length;
    }
    
    protected void addValue(int param1Int) {
      assert BandStructure.assertCanChangeLength(this);
      if (this.length == this.values.length)
        setCapacity((this.length < 1000) ? (this.length * 10) : (this.length * 2)); 
      this.values[this.length++] = param1Int;
    }
    
    private boolean canVaryCoding() { return !BandStructure.this.optVaryCodings ? false : ((this.length == 0) ? false : ((this == BandStructure.this.archive_header_0) ? false : ((this == BandStructure.this.archive_header_S) ? false : ((this == BandStructure.this.archive_header_1) ? false : ((this.regularCoding.min() <= -256 || this.regularCoding.max() >= 256)))))); }
    
    private boolean shouldVaryCoding() {
      assert canVaryCoding();
      return !(BandStructure.this.effort < 9 && this.length < 100);
    }
    
    protected void chooseBandCodings() {
      boolean bool = canVaryCoding();
      if (!bool || !shouldVaryCoding()) {
        if (this.regularCoding.canRepresent(this.values, 0, this.length)) {
          this.bandCoding = this.regularCoding;
        } else {
          assert bool;
          if (BandStructure.this.verbose > 1)
            Utils.log.fine("regular coding fails in band " + name()); 
          this.bandCoding = BandStructure.UNSIGNED5;
        } 
        this.outputSize = -1L;
      } else {
        int[] arrayOfInt = { 0, 0 };
        this.bandCoding = BandStructure.this.chooseCoding(this.values, 0, this.length, this.regularCoding, name(), arrayOfInt);
        this.outputSize = arrayOfInt[0];
        if (this.outputSize == 0L)
          this.outputSize = -1L; 
      } 
      if (this.bandCoding != this.regularCoding) {
        this.metaCoding = this.bandCoding.getMetaCoding(this.regularCoding);
        if (BandStructure.this.verbose > 1)
          Utils.log.fine("alternate coding " + this + " " + this.bandCoding); 
      } else if (bool && BandStructure.decodeEscapeValue(this.values[0], this.regularCoding) >= 0) {
        this.metaCoding = BandStructure.defaultMetaCoding;
      } else {
        this.metaCoding = BandStructure.noMetaCoding;
      } 
      if (this.metaCoding.length > 0 && (BandStructure.this.verbose > 2 || (BandStructure.this.verbose > 1 && this.metaCoding.length > 1))) {
        StringBuffer stringBuffer = new StringBuffer();
        for (byte b = 0; b < this.metaCoding.length; b++) {
          if (b == 1)
            stringBuffer.append(" /"); 
          stringBuffer.append(" ").append(this.metaCoding[b] & 0xFF);
        } 
        Utils.log.fine("   meta-coding " + stringBuffer);
      } 
      assert this.outputSize < 0L || !(this.bandCoding instanceof Coding) || this.outputSize == ((Coding)this.bandCoding).getLength(this.values, 0, this.length) : this.bandCoding + " : " + this.outputSize + " != " + ((Coding)this.bandCoding).getLength(this.values, false, this.length) + " ?= " + BandStructure.this.getCodingChooser().computeByteSize(this.bandCoding, this.values, false, this.length);
      if (this.metaCoding.length > 0) {
        if (this.outputSize >= 0L)
          this.outputSize += computeEscapeSize(); 
        for (byte b = 1; b < this.metaCoding.length; b++)
          BandStructure.this.band_headers.putByte(this.metaCoding[b] & 0xFF); 
      } 
    }
    
    protected long computeOutputSize() {
      this.outputSize = BandStructure.this.getCodingChooser().computeByteSize(this.bandCoding, this.values, 0, this.length);
      assert this.outputSize < 2147483647L;
      this.outputSize += computeEscapeSize();
      return this.outputSize;
    }
    
    protected int computeEscapeSize() {
      if (this.metaCoding.length == 0)
        return 0; 
      byte b = this.metaCoding[0] & 0xFF;
      int i = BandStructure.encodeEscapeValue(b, this.regularCoding);
      return this.regularCoding.setD(0).getLength(i);
    }
    
    protected void writeDataTo(OutputStream param1OutputStream) throws IOException {
      if (this.length == 0)
        return; 
      long l = 0L;
      if (param1OutputStream == BandStructure.this.outputCounter)
        l = BandStructure.this.outputCounter.getCount(); 
      if (this.metaCoding.length > 0) {
        byte b = this.metaCoding[0] & 0xFF;
        int i = BandStructure.encodeEscapeValue(b, this.regularCoding);
        this.regularCoding.setD(0).writeTo(param1OutputStream, i);
      } 
      this.bandCoding.writeArrayTo(param1OutputStream, this.values, 0, this.length);
      if (param1OutputStream == BandStructure.this.outputCounter && !$assertionsDisabled && this.outputSize != BandStructure.this.outputCounter.getCount() - l)
        throw new AssertionError(this.outputSize + " != " + BandStructure.this.outputCounter.getCount() + "-" + l); 
      if (BandStructure.this.optDumpBands)
        dumpBand(); 
    }
    
    protected void readDataFrom(InputStream param1InputStream) throws IOException {
      this.length = valuesExpected();
      if (this.length == 0)
        return; 
      if (BandStructure.this.verbose > 1)
        Utils.log.fine("Reading band " + this); 
      if (!canVaryCoding()) {
        this.bandCoding = this.regularCoding;
        this.metaCoding = BandStructure.noMetaCoding;
      } else {
        assert param1InputStream.markSupported();
        param1InputStream.mark(5);
        int i = this.regularCoding.setD(0).readFrom(param1InputStream);
        int j = BandStructure.decodeEscapeValue(i, this.regularCoding);
        if (j < 0) {
          param1InputStream.reset();
          this.bandCoding = this.regularCoding;
          this.metaCoding = BandStructure.noMetaCoding;
        } else if (j == 0) {
          this.bandCoding = this.regularCoding;
          this.metaCoding = BandStructure.defaultMetaCoding;
        } else {
          if (BandStructure.this.verbose > 2)
            Utils.log.fine("found X=" + i + " => XB=" + j); 
          this.bandCoding = BandStructure.this.getBandHeader(j, this.regularCoding);
          int k = BandStructure.this.bandHeaderBytePos0;
          int m = BandStructure.this.bandHeaderBytePos;
          this.metaCoding = new byte[m - k];
          System.arraycopy(BandStructure.this.bandHeaderBytes, k, this.metaCoding, 0, this.metaCoding.length);
        } 
      } 
      if (this.bandCoding != this.regularCoding && BandStructure.this.verbose > 1)
        Utils.log.fine(name() + ": irregular coding " + this.bandCoding); 
      this.bandCoding.readArrayFrom(param1InputStream, this.values, 0, this.length);
      if (BandStructure.this.optDumpBands)
        dumpBand(); 
    }
    
    public void doneDisbursing() {
      super.doneDisbursing();
      this.values = null;
    }
    
    private void dumpBand() {
      assert BandStructure.this.optDumpBands;
      try (PrintStream null = new PrintStream(BandStructure.getDumpStream(this, ".txt"))) {
        str = (this.bandCoding == this.regularCoding) ? "" : " irregular";
        printStream.print("# length=" + this.length + " size=" + outputSize() + str + " coding=" + this.bandCoding);
        if (this.metaCoding != BandStructure.noMetaCoding) {
          StringBuffer stringBuffer = new StringBuffer();
          for (byte b = 0; b < this.metaCoding.length; b++) {
            if (b == 1)
              stringBuffer.append(" /"); 
            stringBuffer.append(" ").append(this.metaCoding[b] & 0xFF);
          } 
          printStream.print(" //header: " + stringBuffer);
        } 
        BandStructure.printArrayTo(printStream, this.values, 0, this.length);
      } 
      try (OutputStream null = BandStructure.getDumpStream(this, ".bnd")) {
        this.bandCoding.writeArrayTo(outputStream, this.values, 0, this.length);
      } 
    }
    
    protected int getValue() {
      assert phase() == 6;
      if (BandStructure.this.optDebugBands && this.length == 0 && this.valuesDisbursed == this.length)
        return 0; 
      assert this.valuesDisbursed <= this.length;
      return this.values[this.valuesDisbursed++];
    }
    
    public void resetForSecondPass() {
      assert phase() == 6;
      assert this.valuesDisbursed == length();
      this.valuesDisbursed = 0;
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\jav\\util\jar\pack\BandStructure.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */