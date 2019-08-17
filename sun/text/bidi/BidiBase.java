package sun.text.bidi;

import java.io.IOException;
import java.lang.reflect.Array;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.text.AttributedCharacterIterator;
import java.text.Bidi;
import java.util.Arrays;
import java.util.MissingResourceException;
import sun.text.normalizer.UBiDiProps;
import sun.text.normalizer.UCharacter;
import sun.text.normalizer.UTF16;

public class BidiBase {
  public static final byte INTERNAL_LEVEL_DEFAULT_LTR = 126;
  
  public static final byte INTERNAL_LEVEL_DEFAULT_RTL = 127;
  
  public static final byte MAX_EXPLICIT_LEVEL = 61;
  
  public static final byte INTERNAL_LEVEL_OVERRIDE = -128;
  
  public static final int MAP_NOWHERE = -1;
  
  public static final byte MIXED = 2;
  
  public static final short DO_MIRRORING = 2;
  
  private static final short REORDER_DEFAULT = 0;
  
  private static final short REORDER_NUMBERS_SPECIAL = 1;
  
  private static final short REORDER_GROUP_NUMBERS_WITH_R = 2;
  
  private static final short REORDER_RUNS_ONLY = 3;
  
  private static final short REORDER_INVERSE_NUMBERS_AS_L = 4;
  
  private static final short REORDER_INVERSE_LIKE_DIRECT = 5;
  
  private static final short REORDER_INVERSE_FOR_NUMBERS_SPECIAL = 6;
  
  private static final short REORDER_LAST_LOGICAL_TO_VISUAL = 1;
  
  private static final int OPTION_INSERT_MARKS = 1;
  
  private static final int OPTION_REMOVE_CONTROLS = 2;
  
  private static final int OPTION_STREAMING = 4;
  
  private static final byte L = 0;
  
  private static final byte R = 1;
  
  private static final byte EN = 2;
  
  private static final byte ES = 3;
  
  private static final byte ET = 4;
  
  private static final byte AN = 5;
  
  private static final byte CS = 6;
  
  static final byte B = 7;
  
  private static final byte S = 8;
  
  private static final byte WS = 9;
  
  private static final byte ON = 10;
  
  private static final byte LRE = 11;
  
  private static final byte LRO = 12;
  
  private static final byte AL = 13;
  
  private static final byte RLE = 14;
  
  private static final byte RLO = 15;
  
  private static final byte PDF = 16;
  
  private static final byte NSM = 17;
  
  private static final byte BN = 18;
  
  private static final int MASK_R_AL = 8194;
  
  private static final char CR = '\r';
  
  private static final char LF = '\n';
  
  static final int LRM_BEFORE = 1;
  
  static final int LRM_AFTER = 2;
  
  static final int RLM_BEFORE = 4;
  
  static final int RLM_AFTER = 8;
  
  BidiBase paraBidi;
  
  final UBiDiProps bdp;
  
  char[] text;
  
  int originalLength;
  
  public int length;
  
  int resultLength;
  
  boolean mayAllocateText;
  
  boolean mayAllocateRuns;
  
  byte[] dirPropsMemory = new byte[1];
  
  byte[] levelsMemory = new byte[1];
  
  byte[] dirProps;
  
  byte[] levels;
  
  boolean orderParagraphsLTR;
  
  byte paraLevel;
  
  byte defaultParaLevel;
  
  ImpTabPair impTabPair;
  
  byte direction;
  
  int flags;
  
  int lastArabicPos;
  
  int trailingWSStart;
  
  int paraCount;
  
  int[] parasMemory = new int[1];
  
  int[] paras;
  
  int[] simpleParas = { 0 };
  
  int runCount;
  
  BidiRun[] runsMemory = new BidiRun[0];
  
  BidiRun[] runs;
  
  BidiRun[] simpleRuns = { new BidiRun() };
  
  int[] logicalToVisualRunsMap;
  
  boolean isGoodLogicalToVisualRunsMap;
  
  InsertPoints insertPoints = new InsertPoints();
  
  int controlCount;
  
  static final byte CONTEXT_RTL_SHIFT = 6;
  
  static final byte CONTEXT_RTL = 64;
  
  static final int DirPropFlagMultiRuns = DirPropFlag((byte)31);
  
  static final int[] DirPropFlagLR = { DirPropFlag((byte)0), DirPropFlag((byte)1) };
  
  static final int[] DirPropFlagE = { DirPropFlag((byte)11), DirPropFlag((byte)14) };
  
  static final int[] DirPropFlagO = { DirPropFlag((byte)12), DirPropFlag((byte)15) };
  
  static final int MASK_LTR = DirPropFlag((byte)0) | DirPropFlag((byte)2) | DirPropFlag((byte)5) | DirPropFlag((byte)11) | DirPropFlag((byte)12);
  
  static final int MASK_RTL = DirPropFlag((byte)1) | DirPropFlag((byte)13) | DirPropFlag((byte)14) | DirPropFlag((byte)15);
  
  private static final int MASK_LRX = DirPropFlag((byte)11) | DirPropFlag((byte)12);
  
  private static final int MASK_RLX = DirPropFlag((byte)14) | DirPropFlag((byte)15);
  
  private static final int MASK_EXPLICIT = MASK_LRX | MASK_RLX | DirPropFlag((byte)16);
  
  private static final int MASK_BN_EXPLICIT = DirPropFlag((byte)18) | MASK_EXPLICIT;
  
  private static final int MASK_B_S = DirPropFlag((byte)7) | DirPropFlag((byte)8);
  
  static final int MASK_WS = MASK_B_S | DirPropFlag((byte)9) | MASK_BN_EXPLICIT;
  
  private static final int MASK_N = DirPropFlag((byte)10) | MASK_WS;
  
  private static final int MASK_POSSIBLE_N = DirPropFlag((byte)6) | DirPropFlag((byte)3) | DirPropFlag((byte)4) | MASK_N;
  
  static final int MASK_EMBEDDING = DirPropFlag((byte)17) | MASK_POSSIBLE_N;
  
  private static final int IMPTABPROPS_COLUMNS = 14;
  
  private static final int IMPTABPROPS_RES = 13;
  
  private static final short[] groupProp = { 
      0, 1, 2, 7, 8, 3, 9, 6, 5, 4, 
      4, 10, 10, 12, 10, 10, 10, 11, 10 };
  
  private static final short _L = 0;
  
  private static final short _R = 1;
  
  private static final short _EN = 2;
  
  private static final short _AN = 3;
  
  private static final short _ON = 4;
  
  private static final short _S = 5;
  
  private static final short _B = 6;
  
  private static final short[][] impTabProps = { 
      { 
        1, 2, 4, 5, 7, 15, 17, 7, 9, 7, 
        0, 7, 3, 4 }, { 
        1, 34, 36, 37, 39, 47, 49, 39, 41, 39, 
        1, 1, 35, 0 }, { 
        33, 2, 36, 37, 39, 47, 49, 39, 41, 39, 
        2, 2, 35, 1 }, { 
        33, 34, 38, 38, 40, 48, 49, 40, 40, 40, 
        3, 3, 3, 1 }, { 
        33, 34, 4, 37, 39, 47, 49, 74, 11, 74, 
        4, 4, 35, 2 }, { 
        33, 34, 36, 5, 39, 47, 49, 39, 41, 76, 
        5, 5, 35, 3 }, { 
        33, 34, 6, 6, 40, 48, 49, 40, 40, 77, 
        6, 6, 35, 3 }, { 
        33, 34, 36, 37, 7, 47, 49, 7, 78, 7, 
        7, 7, 35, 4 }, { 
        33, 34, 38, 38, 8, 48, 49, 8, 8, 8, 
        8, 8, 35, 4 }, { 
        33, 34, 4, 37, 7, 47, 49, 7, 9, 7, 
        9, 9, 35, 4 }, 
      { 
        97, 98, 4, 101, 135, 111, 113, 135, 142, 135, 
        10, 135, 99, 2 }, { 
        33, 34, 4, 37, 39, 47, 49, 39, 11, 39, 
        11, 11, 35, 2 }, { 
        97, 98, 100, 5, 135, 111, 113, 135, 142, 135, 
        12, 135, 99, 3 }, { 
        97, 98, 6, 6, 136, 112, 113, 136, 136, 136, 
        13, 136, 99, 3 }, { 
        33, 34, 132, 37, 7, 47, 49, 7, 14, 7, 
        14, 14, 35, 4 }, { 
        33, 34, 36, 37, 39, 15, 49, 39, 41, 39, 
        15, 39, 35, 5 }, { 
        33, 34, 38, 38, 40, 16, 49, 40, 40, 40, 
        16, 40, 35, 5 }, { 
        33, 34, 36, 37, 39, 47, 17, 39, 41, 39, 
        17, 39, 35, 6 } };
  
  private static final int IMPTABLEVELS_COLUMNS = 8;
  
  private static final int IMPTABLEVELS_RES = 7;
  
  private static final byte[][] impTabL_DEFAULT = { { 0, 1, 0, 2, 0, 0, 0, 0 }, { 0, 1, 3, 3, 20, 20, 0, 1 }, { 0, 1, 0, 2, 21, 21, 0, 2 }, { 0, 1, 3, 3, 20, 20, 0, 2 }, { 32, 1, 3, 3, 4, 4, 32, 1 }, { 32, 1, 32, 2, 5, 5, 32, 1 } };
  
  private static final byte[][] impTabR_DEFAULT = { { 1, 0, 2, 2, 0, 0, 0, 0 }, { 1, 0, 1, 3, 20, 20, 0, 1 }, { 1, 0, 2, 2, 0, 0, 0, 1 }, { 1, 0, 1, 3, 5, 5, 0, 1 }, { 33, 0, 33, 3, 4, 4, 0, 0 }, { 1, 0, 1, 3, 5, 5, 0, 0 } };
  
  private static final short[] impAct0 = { 0, 1, 2, 3, 4, 5, 6 };
  
  private static final ImpTabPair impTab_DEFAULT = new ImpTabPair(impTabL_DEFAULT, impTabR_DEFAULT, impAct0, impAct0);
  
  private static final byte[][] impTabL_NUMBERS_SPECIAL = { { 0, 2, 1, 1, 0, 0, 0, 0 }, { 0, 2, 1, 1, 0, 0, 0, 2 }, { 0, 2, 4, 4, 19, 0, 0, 1 }, { 32, 2, 4, 4, 3, 3, 32, 1 }, { 0, 2, 4, 4, 19, 19, 0, 2 } };
  
  private static final ImpTabPair impTab_NUMBERS_SPECIAL = new ImpTabPair(impTabL_NUMBERS_SPECIAL, impTabR_DEFAULT, impAct0, impAct0);
  
  private static final byte[][] impTabL_GROUP_NUMBERS_WITH_R = { { 0, 3, 17, 17, 0, 0, 0, 0 }, { 32, 3, 1, 1, 2, 32, 32, 2 }, { 32, 3, 1, 1, 2, 32, 32, 1 }, { 0, 3, 5, 5, 20, 0, 0, 1 }, { 32, 3, 5, 5, 4, 32, 32, 1 }, { 0, 3, 5, 5, 20, 0, 0, 2 } };
  
  private static final byte[][] impTabR_GROUP_NUMBERS_WITH_R = { { 2, 0, 1, 1, 0, 0, 0, 0 }, { 2, 0, 1, 1, 0, 0, 0, 1 }, { 2, 0, 20, 20, 19, 0, 0, 1 }, { 34, 0, 4, 4, 3, 0, 0, 0 }, { 34, 0, 4, 4, 3, 0, 0, 1 } };
  
  private static final ImpTabPair impTab_GROUP_NUMBERS_WITH_R = new ImpTabPair(impTabL_GROUP_NUMBERS_WITH_R, impTabR_GROUP_NUMBERS_WITH_R, impAct0, impAct0);
  
  private static final byte[][] impTabL_INVERSE_NUMBERS_AS_L = { { 0, 1, 0, 0, 0, 0, 0, 0 }, { 0, 1, 0, 0, 20, 20, 0, 1 }, { 0, 1, 0, 0, 21, 21, 0, 2 }, { 0, 1, 0, 0, 20, 20, 0, 2 }, { 32, 1, 32, 32, 4, 4, 32, 1 }, { 32, 1, 32, 32, 5, 5, 32, 1 } };
  
  private static final byte[][] impTabR_INVERSE_NUMBERS_AS_L = { { 1, 0, 1, 1, 0, 0, 0, 0 }, { 1, 0, 1, 1, 20, 20, 0, 1 }, { 1, 0, 1, 1, 0, 0, 0, 1 }, { 1, 0, 1, 1, 5, 5, 0, 1 }, { 33, 0, 33, 33, 4, 4, 0, 0 }, { 1, 0, 1, 1, 5, 5, 0, 0 } };
  
  private static final ImpTabPair impTab_INVERSE_NUMBERS_AS_L = new ImpTabPair(impTabL_INVERSE_NUMBERS_AS_L, impTabR_INVERSE_NUMBERS_AS_L, impAct0, impAct0);
  
  private static final byte[][] impTabR_INVERSE_LIKE_DIRECT = { { 1, 0, 2, 2, 0, 0, 0, 0 }, { 1, 0, 1, 2, 19, 19, 0, 1 }, { 1, 0, 2, 2, 0, 0, 0, 1 }, { 33, 48, 6, 4, 3, 3, 48, 0 }, { 33, 48, 6, 4, 5, 5, 48, 3 }, { 33, 48, 6, 4, 5, 5, 48, 2 }, { 33, 48, 6, 4, 3, 3, 48, 1 } };
  
  private static final short[] impAct1 = { 0, 1, 11, 12 };
  
  private static final ImpTabPair impTab_INVERSE_LIKE_DIRECT = new ImpTabPair(impTabL_DEFAULT, impTabR_INVERSE_LIKE_DIRECT, impAct0, impAct1);
  
  private static final byte[][] impTabL_INVERSE_LIKE_DIRECT_WITH_MARKS = { { 0, 99, 0, 1, 0, 0, 0, 0 }, { 0, 99, 0, 1, 18, 48, 0, 4 }, { 32, 99, 32, 1, 2, 48, 32, 3 }, { 0, 99, 85, 86, 20, 48, 0, 3 }, { 48, 67, 85, 86, 4, 48, 48, 3 }, { 48, 67, 5, 86, 20, 48, 48, 4 }, { 48, 67, 85, 6, 20, 48, 48, 4 } };
  
  private static final byte[][] impTabR_INVERSE_LIKE_DIRECT_WITH_MARKS = { { 19, 0, 1, 1, 0, 0, 0, 0 }, { 35, 0, 1, 1, 2, 64, 0, 1 }, { 35, 0, 1, 1, 2, 64, 0, 0 }, { 3, 0, 3, 54, 20, 64, 0, 1 }, { 83, 64, 5, 54, 4, 64, 64, 0 }, { 83, 64, 5, 54, 4, 64, 64, 1 }, { 83, 64, 6, 6, 4, 64, 64, 3 } };
  
  private static final short[] impAct2 = { 0, 1, 7, 8, 9, 10 };
  
  private static final ImpTabPair impTab_INVERSE_LIKE_DIRECT_WITH_MARKS = new ImpTabPair(impTabL_INVERSE_LIKE_DIRECT_WITH_MARKS, impTabR_INVERSE_LIKE_DIRECT_WITH_MARKS, impAct0, impAct2);
  
  private static final ImpTabPair impTab_INVERSE_FOR_NUMBERS_SPECIAL = new ImpTabPair(impTabL_NUMBERS_SPECIAL, impTabR_INVERSE_LIKE_DIRECT, impAct0, impAct1);
  
  private static final byte[][] impTabL_INVERSE_FOR_NUMBERS_SPECIAL_WITH_MARKS = { { 0, 98, 1, 1, 0, 0, 0, 0 }, { 0, 98, 1, 1, 0, 48, 0, 4 }, { 0, 98, 84, 84, 19, 48, 0, 3 }, { 48, 66, 84, 84, 3, 48, 48, 3 }, { 48, 66, 4, 4, 19, 48, 48, 4 } };
  
  private static final ImpTabPair impTab_INVERSE_FOR_NUMBERS_SPECIAL_WITH_MARKS = new ImpTabPair(impTabL_INVERSE_FOR_NUMBERS_SPECIAL_WITH_MARKS, impTabR_INVERSE_LIKE_DIRECT_WITH_MARKS, impAct0, impAct2);
  
  static final int FIRSTALLOC = 10;
  
  private static final int INTERNAL_DIRECTION_DEFAULT_LEFT_TO_RIGHT = 126;
  
  private static final int INTERMAL_DIRECTION_DEFAULT_RIGHT_TO_LEFT = 127;
  
  static int DirPropFlag(byte paramByte) { return 1 << paramByte; }
  
  static byte NoContextRTL(byte paramByte) { return (byte)(paramByte & 0xFFFFFFBF); }
  
  static int DirPropFlagNC(byte paramByte) { return 1 << (paramByte & 0xFFFFFFBF); }
  
  static final int DirPropFlagLR(byte paramByte) { return DirPropFlagLR[paramByte & true]; }
  
  static final int DirPropFlagE(byte paramByte) { return DirPropFlagE[paramByte & true]; }
  
  static final int DirPropFlagO(byte paramByte) { return DirPropFlagO[paramByte & true]; }
  
  private static byte GetLRFromLevel(byte paramByte) { return (byte)(paramByte & true); }
  
  private static boolean IsDefaultLevel(byte paramByte) { return ((paramByte & 0x7E) == 126); }
  
  byte GetParaLevelAt(int paramInt) { return (this.defaultParaLevel != 0) ? (byte)(this.dirProps[paramInt] >> 6) : this.paraLevel; }
  
  static boolean IsBidiControlChar(int paramInt) { return ((paramInt & 0xFFFFFFFC) == 8204 || (paramInt >= 8234 && paramInt <= 8238)); }
  
  public void verifyValidPara() {
    if (this != this.paraBidi)
      throw new IllegalStateException(""); 
  }
  
  public void verifyValidParaOrLine() {
    BidiBase bidiBase = this.paraBidi;
    if (this == bidiBase)
      return; 
    if (bidiBase == null || bidiBase != bidiBase.paraBidi)
      throw new IllegalStateException(); 
  }
  
  public void verifyRange(int paramInt1, int paramInt2, int paramInt3) {
    if (paramInt1 < paramInt2 || paramInt1 >= paramInt3)
      throw new IllegalArgumentException("Value " + paramInt1 + " is out of range " + paramInt2 + " to " + paramInt3); 
  }
  
  public void verifyIndex(int paramInt1, int paramInt2, int paramInt3) {
    if (paramInt1 < paramInt2 || paramInt1 >= paramInt3)
      throw new ArrayIndexOutOfBoundsException("Index " + paramInt1 + " is out of range " + paramInt2 + " to " + paramInt3); 
  }
  
  public BidiBase(int paramInt1, int paramInt2) {
    if (paramInt1 < 0 || paramInt2 < 0)
      throw new IllegalArgumentException(); 
    try {
      this.bdp = UBiDiProps.getSingleton();
    } catch (IOException iOException) {
      throw new MissingResourceException(iOException.getMessage(), "(BidiProps)", "");
    } 
    if (paramInt1 > 0) {
      getInitialDirPropsMemory(paramInt1);
      getInitialLevelsMemory(paramInt1);
    } else {
      this.mayAllocateText = true;
    } 
    if (paramInt2 > 0) {
      if (paramInt2 > 1)
        getInitialRunsMemory(paramInt2); 
    } else {
      this.mayAllocateRuns = true;
    } 
  }
  
  private Object getMemory(String paramString, Object paramObject, Class<?> paramClass, boolean paramBoolean, int paramInt) {
    int i = Array.getLength(paramObject);
    if (paramInt == i)
      return paramObject; 
    if (!paramBoolean) {
      if (paramInt <= i)
        return paramObject; 
      throw new OutOfMemoryError("Failed to allocate memory for " + paramString);
    } 
    try {
      return Array.newInstance(paramClass, paramInt);
    } catch (Exception exception) {
      throw new OutOfMemoryError("Failed to allocate memory for " + paramString);
    } 
  }
  
  private void getDirPropsMemory(boolean paramBoolean, int paramInt) {
    Object object = getMemory("DirProps", this.dirPropsMemory, byte.class, paramBoolean, paramInt);
    this.dirPropsMemory = (byte[])object;
  }
  
  void getDirPropsMemory(int paramInt) { getDirPropsMemory(this.mayAllocateText, paramInt); }
  
  private void getLevelsMemory(boolean paramBoolean, int paramInt) {
    Object object = getMemory("Levels", this.levelsMemory, byte.class, paramBoolean, paramInt);
    this.levelsMemory = (byte[])object;
  }
  
  void getLevelsMemory(int paramInt) { getLevelsMemory(this.mayAllocateText, paramInt); }
  
  private void getRunsMemory(boolean paramBoolean, int paramInt) {
    Object object = getMemory("Runs", this.runsMemory, BidiRun.class, paramBoolean, paramInt);
    this.runsMemory = (BidiRun[])object;
  }
  
  void getRunsMemory(int paramInt) { getRunsMemory(this.mayAllocateRuns, paramInt); }
  
  private void getInitialDirPropsMemory(int paramInt) { getDirPropsMemory(true, paramInt); }
  
  private void getInitialLevelsMemory(int paramInt) { getLevelsMemory(true, paramInt); }
  
  private void getInitialParasMemory(int paramInt) {
    Object object = getMemory("Paras", this.parasMemory, int.class, true, paramInt);
    this.parasMemory = (int[])object;
  }
  
  private void getInitialRunsMemory(int paramInt) { getRunsMemory(true, paramInt); }
  
  private void getDirProps() {
    byte b2;
    byte b;
    int i = 0;
    this.flags = 0;
    byte b1 = 0;
    boolean bool = IsDefaultLevel(this.paraLevel);
    this.lastArabicPos = -1;
    this.controlCount = 0;
    int j = 0;
    byte b3 = 0;
    int k = 0;
    if (bool) {
      b1 = ((this.paraLevel & true) != 0) ? 64 : 0;
      b2 = b1;
      b3 = b1;
      b = 1;
    } else {
      b = 0;
      b2 = 0;
    } 
    i = 0;
    while (i < this.originalLength) {
      boolean bool1 = i;
      int n = UTF16.charAt(this.text, 0, this.originalLength, i);
      i += Character.charCount(n);
      int m = i - 1;
      byte b4 = (byte)this.bdp.getClass(n);
      this.flags |= DirPropFlag(b4);
      this.dirProps[m] = (byte)(b4 | b2);
      if (m > bool1) {
        this.flags |= DirPropFlag((byte)18);
        do {
          this.dirProps[--m] = (byte)(0x12 | b2);
        } while (m > bool1);
      } 
      if (b == 1) {
        if (b4 == 0) {
          b = 2;
          if (b2 != 0) {
            b2 = 0;
            for (m = j; m < i; m++)
              this.dirProps[m] = (byte)(this.dirProps[m] & 0xFFFFFFBF); 
          } 
          continue;
        } 
        if (b4 == 1 || b4 == 13) {
          b = 2;
          if (b2 == 0) {
            b2 = 64;
            for (m = j; m < i; m++)
              this.dirProps[m] = (byte)(this.dirProps[m] | 0x40); 
          } 
          continue;
        } 
      } 
      if (b4 == 0) {
        b3 = 0;
        k = i;
        continue;
      } 
      if (b4 == 1) {
        b3 = 64;
        continue;
      } 
      if (b4 == 13) {
        b3 = 64;
        this.lastArabicPos = i - 1;
        continue;
      } 
      if (b4 == 7 && i < this.originalLength) {
        if (n != 13 || this.text[i] != '\n')
          this.paraCount++; 
        if (bool) {
          b = 1;
          j = i;
          b2 = b1;
          b3 = b1;
        } 
      } 
    } 
    if (bool)
      this.paraLevel = GetParaLevelAt(0); 
    this.flags |= DirPropFlagLR(this.paraLevel);
    if (this.orderParagraphsLTR && (this.flags & DirPropFlag((byte)7)) != 0)
      this.flags |= DirPropFlag((byte)0); 
  }
  
  private byte directionFromFlags() { return ((this.flags & MASK_RTL) == 0 && ((this.flags & DirPropFlag((byte)5)) == 0 || (this.flags & MASK_POSSIBLE_N) == 0)) ? 0 : (((this.flags & MASK_LTR) == 0) ? 1 : 2); }
  
  private byte resolveExplicitLevels() {
    byte b1 = 0;
    byte b2 = GetParaLevelAt(0);
    byte b4 = 0;
    byte b3 = directionFromFlags();
    if (b3 == 2 || this.paraCount != 1)
      if (this.paraCount == 1 && (this.flags & MASK_EXPLICIT) == 0) {
        for (b1 = 0; b1 < this.length; b1++)
          this.levels[b1] = b2; 
      } else {
        byte b = b2;
        byte b5 = 0;
        byte[] arrayOfByte = new byte[61];
        byte b6 = 0;
        byte b7 = 0;
        this.flags = 0;
        for (b1 = 0; b1 < this.length; b1++) {
          byte b9;
          byte b8 = NoContextRTL(this.dirProps[b1]);
          switch (b8) {
            case 11:
            case 12:
              b9 = (byte)(b + 2 & 0x7E);
              if (b9 <= 61) {
                arrayOfByte[b5] = b;
                b5 = (byte)(b5 + true);
                b = b9;
                if (b8 == 12)
                  b = (byte)(b | 0xFFFFFF80); 
              } else if ((b & 0x7F) == 61) {
                b7++;
              } else {
                b6++;
              } 
              this.flags |= DirPropFlag((byte)18);
              break;
            case 14:
            case 15:
              b9 = (byte)((b & 0x7F) + 1 | true);
              if (b9 <= 61) {
                arrayOfByte[b5] = b;
                b5 = (byte)(b5 + 1);
                b = b9;
                if (b8 == 15)
                  b = (byte)(b | 0xFFFFFF80); 
              } else {
                b7++;
              } 
              this.flags |= DirPropFlag((byte)18);
              break;
            case 16:
              if (b7 > 0) {
                b7--;
              } else if (b6 > 0 && (b & 0x7F) != 61) {
                b6--;
              } else if (b5 > 0) {
                b5 = (byte)(b5 - 1);
                b = arrayOfByte[b5];
              } 
              this.flags |= DirPropFlag((byte)18);
              break;
            case 7:
              b5 = 0;
              b6 = 0;
              b7 = 0;
              b2 = GetParaLevelAt(b1);
              if (b1 + 1 < this.length) {
                b = GetParaLevelAt(b1 + 1);
                if (this.text[b1] != '\r' || this.text[b1 + 1] != '\n')
                  this.paras[b4++] = b1 + 1; 
              } 
              this.flags |= DirPropFlag((byte)7);
              break;
            case 18:
              this.flags |= DirPropFlag((byte)18);
              break;
            default:
              if (b2 != b) {
                b2 = b;
                if ((b2 & 0xFFFFFF80) != 0) {
                  this.flags |= DirPropFlagO(b2) | DirPropFlagMultiRuns;
                } else {
                  this.flags |= DirPropFlagE(b2) | DirPropFlagMultiRuns;
                } 
              } 
              if ((b2 & 0xFFFFFF80) == 0)
                this.flags |= DirPropFlag(b8); 
              break;
          } 
          this.levels[b1] = b2;
        } 
        if ((this.flags & MASK_EMBEDDING) != 0)
          this.flags |= DirPropFlagLR(this.paraLevel); 
        if (this.orderParagraphsLTR && (this.flags & DirPropFlag((byte)7)) != 0)
          this.flags |= DirPropFlag((byte)0); 
        b3 = directionFromFlags();
      }  
    return b3;
  }
  
  private byte checkExplicitLevels() {
    this.flags = 0;
    byte b2 = 0;
    for (byte b1 = 0; b1 < this.length; b1++) {
      if (this.levels[b1] == 0)
        this.levels[b1] = this.paraLevel; 
      if (61 < (this.levels[b1] & 0x7F))
        if ((this.levels[b1] & 0xFFFFFF80) != 0) {
          this.levels[b1] = (byte)(this.paraLevel | 0xFFFFFF80);
        } else {
          this.levels[b1] = this.paraLevel;
        }  
      byte b4 = this.levels[b1];
      byte b3 = NoContextRTL(this.dirProps[b1]);
      if ((b4 & 0xFFFFFF80) != 0) {
        b4 = (byte)(b4 & 0x7F);
        this.flags |= DirPropFlagO(b4);
      } else {
        this.flags |= DirPropFlagE(b4) | DirPropFlag(b3);
      } 
      if ((b4 < GetParaLevelAt(b1) && (0 != b4 || b3 != 7)) || 61 < b4)
        throw new IllegalArgumentException("level " + b4 + " out of bounds at index " + b1); 
      if (b3 == 7 && b1 + 1 < this.length && (this.text[b1] != '\r' || this.text[b1 + 1] != '\n'))
        this.paras[b2++] = b1 + 1; 
    } 
    if ((this.flags & MASK_EMBEDDING) != 0)
      this.flags |= DirPropFlagLR(this.paraLevel); 
    return directionFromFlags();
  }
  
  private static short GetStateProps(short paramShort) { return (short)(paramShort & 0x1F); }
  
  private static short GetActionProps(short paramShort) { return (short)(paramShort >> 5); }
  
  private static short GetState(byte paramByte) { return (short)(paramByte & 0xF); }
  
  private static short GetAction(byte paramByte) { return (short)(paramByte >> 4); }
  
  private void addPoint(int paramInt1, int paramInt2) {
    Point point = new Point();
    int i = this.insertPoints.points.length;
    if (i == 0) {
      this.insertPoints.points = new Point[10];
      i = 10;
    } 
    if (this.insertPoints.size >= i) {
      Point[] arrayOfPoint = this.insertPoints.points;
      this.insertPoints.points = new Point[i * 2];
      System.arraycopy(arrayOfPoint, 0, this.insertPoints.points, 0, i);
    } 
    point.pos = paramInt1;
    point.flag = paramInt2;
    this.insertPoints.points[this.insertPoints.size] = point;
    this.insertPoints.size++;
  }
  
  private void processPropertySeq(LevState paramLevState, short paramShort, int paramInt1, int paramInt2) {
    byte[][] arrayOfByte = paramLevState.impTab;
    short[] arrayOfShort = paramLevState.impAct;
    int i = paramInt1;
    short s1 = paramLevState.state;
    byte b1 = arrayOfByte[s1][paramShort];
    paramLevState.state = GetState(b1);
    short s2 = arrayOfShort[GetAction(b1)];
    byte b2 = arrayOfByte[paramLevState.state][7];
    if (s2 != 0) {
      int j;
      byte b;
      switch (s2) {
        case 1:
          paramLevState.startON = i;
          break;
        case 2:
          paramInt1 = paramLevState.startON;
          break;
        case 3:
          if (paramLevState.startL2EN >= 0)
            addPoint(paramLevState.startL2EN, 1); 
          paramLevState.startL2EN = -1;
          if (this.insertPoints.points.length == 0 || this.insertPoints.size <= this.insertPoints.confirmed) {
            paramLevState.lastStrongRTL = -1;
            byte b3 = arrayOfByte[s1][7];
            if ((b3 & true) != 0 && paramLevState.startON > 0)
              paramInt1 = paramLevState.startON; 
            if (paramShort == 5) {
              addPoint(i, 1);
              this.insertPoints.confirmed = this.insertPoints.size;
            } 
            break;
          } 
          for (j = paramLevState.lastStrongRTL + 1; j < i; j++)
            this.levels[j] = (byte)(this.levels[j] - 2 & 0xFFFFFFFE); 
          this.insertPoints.confirmed = this.insertPoints.size;
          paramLevState.lastStrongRTL = -1;
          if (paramShort == 5) {
            addPoint(i, 1);
            this.insertPoints.confirmed = this.insertPoints.size;
          } 
          break;
        case 4:
          if (this.insertPoints.points.length > 0)
            this.insertPoints.size = this.insertPoints.confirmed; 
          paramLevState.startON = -1;
          paramLevState.startL2EN = -1;
          paramLevState.lastStrongRTL = paramInt2 - 1;
          break;
        case 5:
          if (paramShort == 3 && NoContextRTL(this.dirProps[i]) == 5) {
            if (paramLevState.startL2EN == -1) {
              paramLevState.lastStrongRTL = paramInt2 - 1;
              break;
            } 
            if (paramLevState.startL2EN >= 0) {
              addPoint(paramLevState.startL2EN, 1);
              paramLevState.startL2EN = -2;
            } 
            addPoint(i, 1);
            break;
          } 
          if (paramLevState.startL2EN == -1)
            paramLevState.startL2EN = i; 
          break;
        case 6:
          paramLevState.lastStrongRTL = paramInt2 - 1;
          paramLevState.startON = -1;
          break;
        case 7:
          for (j = i - 1; j >= 0 && (this.levels[j] & true) == 0; j--);
          if (j >= 0) {
            addPoint(j, 4);
            this.insertPoints.confirmed = this.insertPoints.size;
          } 
          paramLevState.startON = i;
          break;
        case 8:
          addPoint(i, 1);
          addPoint(i, 2);
          break;
        case 9:
          this.insertPoints.size = this.insertPoints.confirmed;
          if (paramShort == 5) {
            addPoint(i, 4);
            this.insertPoints.confirmed = this.insertPoints.size;
          } 
          break;
        case 10:
          b = (byte)(paramLevState.runLevel + b2);
          for (j = paramLevState.startON; j < i; j++) {
            if (this.levels[j] < b)
              this.levels[j] = b; 
          } 
          this.insertPoints.confirmed = this.insertPoints.size;
          paramLevState.startON = i;
          break;
        case 11:
          b = paramLevState.runLevel;
          for (j = i - 1; j >= paramLevState.startON; j--) {
            if (this.levels[j] == b + 3) {
              while (this.levels[j] == b + 3)
                this.levels[j--] = (byte)(this.levels[j--] - 2); 
              while (this.levels[j] == b)
                j--; 
            } 
            if (this.levels[j] == b + 2) {
              this.levels[j] = b;
            } else {
              this.levels[j] = (byte)(b + 1);
            } 
          } 
          break;
        case 12:
          b = (byte)(paramLevState.runLevel + 1);
          for (j = i - 1; j >= paramLevState.startON; j--) {
            if (this.levels[j] > b)
              this.levels[j] = (byte)(this.levels[j] - 2); 
          } 
          break;
        default:
          throw new IllegalStateException("Internal ICU error in processPropertySeq");
      } 
    } 
    if (b2 != 0 || paramInt1 < i) {
      byte b = (byte)(paramLevState.runLevel + b2);
      for (int j = paramInt1; j < paramInt2; j++)
        this.levels[j] = b; 
    } 
  }
  
  private void resolveImplicitLevels(int paramInt1, int paramInt2, short paramShort1, short paramShort2) {
    short s;
    LevState levState = new LevState(null);
    boolean bool = true;
    byte b = -1;
    levState.startL2EN = -1;
    levState.lastStrongRTL = -1;
    levState.state = 0;
    levState.runLevel = this.levels[paramInt1];
    levState.impTab = this.impTabPair.imptab[levState.runLevel & true];
    levState.impAct = this.impTabPair.impact[levState.runLevel & true];
    processPropertySeq(levState, paramShort1, paramInt1, paramInt1);
    if (this.dirProps[paramInt1] == 17) {
      s = (short)(1 + paramShort1);
    } else {
      s = 0;
    } 
    int j = paramInt1;
    int k = 0;
    for (int i = paramInt1; i <= paramInt2; i++) {
      short s2;
      if (i >= paramInt2) {
        s2 = paramShort2;
      } else {
        short s4 = (short)NoContextRTL(this.dirProps[i]);
        s2 = groupProp[s4];
      } 
      boolean bool1 = s;
      short s3 = impTabProps[bool1][s2];
      s = GetStateProps(s3);
      short s1 = GetActionProps(s3);
      if (i == paramInt2 && s1 == 0)
        s1 = 1; 
      if (s1 != 0) {
        short s4 = impTabProps[bool1][13];
        switch (s1) {
          case 1:
            processPropertySeq(levState, s4, j, i);
            j = i;
            break;
          case 2:
            k = i;
            break;
          case 3:
            processPropertySeq(levState, s4, j, k);
            processPropertySeq(levState, (short)4, k, i);
            j = i;
            break;
          case 4:
            processPropertySeq(levState, s4, j, k);
            j = k;
            k = i;
            break;
          default:
            throw new IllegalStateException("Internal ICU error in resolveImplicitLevels");
        } 
      } 
    } 
    processPropertySeq(levState, paramShort2, paramInt2, paramInt2);
  }
  
  private void adjustWSLevels() {
    if ((this.flags & MASK_WS) != 0) {
      int i = this.trailingWSStart;
      while (i > 0) {
        int j;
        while (i > 0 && ((j = DirPropFlagNC(this.dirProps[--i])) & MASK_WS) != 0) {
          if (this.orderParagraphsLTR && (j & DirPropFlag((byte)7)) != 0) {
            this.levels[i] = 0;
            continue;
          } 
          this.levels[i] = GetParaLevelAt(i);
        } 
        while (i > 0) {
          j = DirPropFlagNC(this.dirProps[--i]);
          if ((j & MASK_BN_EXPLICIT) != 0) {
            this.levels[i] = this.levels[i + 1];
            continue;
          } 
          if (this.orderParagraphsLTR && (j & DirPropFlag((byte)7)) != 0) {
            this.levels[i] = 0;
            break;
          } 
          if ((j & MASK_B_S) != 0)
            this.levels[i] = GetParaLevelAt(i); 
        } 
      } 
    } 
  }
  
  private int Bidi_Min(int paramInt1, int paramInt2) { return (paramInt1 < paramInt2) ? paramInt1 : paramInt2; }
  
  private int Bidi_Abs(int paramInt) { return (paramInt >= 0) ? paramInt : -paramInt; }
  
  void setPara(String paramString, byte paramByte, byte[] paramArrayOfByte) {
    if (paramString == null) {
      setPara(new char[0], paramByte, paramArrayOfByte);
    } else {
      setPara(paramString.toCharArray(), paramByte, paramArrayOfByte);
    } 
  }
  
  public void setPara(char[] paramArrayOfChar, byte paramByte, byte[] paramArrayOfByte) {
    if (paramByte < 126)
      verifyRange(paramByte, 0, 62); 
    if (paramArrayOfChar == null)
      paramArrayOfChar = new char[0]; 
    this.paraBidi = null;
    this.text = paramArrayOfChar;
    this.length = this.originalLength = this.resultLength = this.text.length;
    this.paraLevel = paramByte;
    this.direction = 0;
    this.paraCount = 1;
    this.dirProps = new byte[0];
    this.levels = new byte[0];
    this.runs = new BidiRun[0];
    this.isGoodLogicalToVisualRunsMap = false;
    this.insertPoints.size = 0;
    this.insertPoints.confirmed = 0;
    if (IsDefaultLevel(paramByte)) {
      this.defaultParaLevel = paramByte;
    } else {
      this.defaultParaLevel = 0;
    } 
    if (this.length == 0) {
      if (IsDefaultLevel(paramByte)) {
        this.paraLevel = (byte)(this.paraLevel & true);
        this.defaultParaLevel = 0;
      } 
      if ((this.paraLevel & true) != 0) {
        this.flags = DirPropFlag((byte)1);
        this.direction = 1;
      } else {
        this.flags = DirPropFlag((byte)0);
        this.direction = 0;
      } 
      this.runCount = 0;
      this.paraCount = 0;
      this.paraBidi = this;
      return;
    } 
    this.runCount = -1;
    getDirPropsMemory(this.length);
    this.dirProps = this.dirPropsMemory;
    getDirProps();
    this.trailingWSStart = this.length;
    if (this.paraCount > 1) {
      getInitialParasMemory(this.paraCount);
      this.paras = this.parasMemory;
      this.paras[this.paraCount - 1] = this.length;
    } else {
      this.paras = this.simpleParas;
      this.simpleParas[0] = this.length;
    } 
    if (paramArrayOfByte == null) {
      getLevelsMemory(this.length);
      this.levels = this.levelsMemory;
      this.direction = resolveExplicitLevels();
    } else {
      this.levels = paramArrayOfByte;
      this.direction = checkExplicitLevels();
    } 
    switch (this.direction) {
      case 0:
        paramByte = (byte)(paramByte + 1 & 0xFFFFFFFE);
        this.trailingWSStart = 0;
        break;
      case 1:
        paramByte = (byte)(paramByte | true);
        this.trailingWSStart = 0;
        break;
      default:
        this.impTabPair = impTab_DEFAULT;
        if (paramArrayOfByte == null && this.paraCount <= 1 && (this.flags & DirPropFlagMultiRuns) == 0) {
          resolveImplicitLevels(0, this.length, (short)GetLRFromLevel(GetParaLevelAt(0)), (short)GetLRFromLevel(GetParaLevelAt(this.length - 1)));
        } else {
          short s;
          byte b = 0;
          byte b1 = GetParaLevelAt(0);
          byte b2 = this.levels[0];
          if (b1 < b2) {
            s = (short)GetLRFromLevel(b2);
          } else {
            s = (short)GetLRFromLevel(b1);
          } 
          do {
            short s1;
            byte b3 = b;
            b1 = b2;
            if (b3 && NoContextRTL(this.dirProps[b3 - true]) == 7) {
              s1 = (short)GetLRFromLevel(GetParaLevelAt(b3));
            } else {
              s1 = s;
            } 
            while (++b < this.length && this.levels[b] == b1);
            if (b < this.length) {
              b2 = this.levels[b];
            } else {
              b2 = GetParaLevelAt(this.length - 1);
            } 
            if ((b1 & 0x7F) < (b2 & 0x7F)) {
              s = (short)GetLRFromLevel(b2);
            } else {
              s = (short)GetLRFromLevel(b1);
            } 
            if ((b1 & 0xFFFFFF80) == 0) {
              resolveImplicitLevels(b3, b, s1, s);
            } else {
              do {
                this.levels[b3++] = (byte)(this.levels[b3++] & 0x7F);
              } while (b3 < b);
            } 
          } while (b < this.length);
        } 
        adjustWSLevels();
        break;
    } 
    this.resultLength += this.insertPoints.size;
    this.paraBidi = this;
  }
  
  public void setPara(AttributedCharacterIterator paramAttributedCharacterIterator) {
    byte b;
    char c = paramAttributedCharacterIterator.first();
    Boolean bool = (Boolean)paramAttributedCharacterIterator.getAttribute(TextAttributeConstants.RUN_DIRECTION);
    Object object = paramAttributedCharacterIterator.getAttribute(TextAttributeConstants.NUMERIC_SHAPING);
    if (bool == null) {
      b = 126;
    } else {
      b = bool.equals(TextAttributeConstants.RUN_DIRECTION_LTR) ? 0 : 1;
    } 
    byte[] arrayOfByte1 = null;
    int i = paramAttributedCharacterIterator.getEndIndex() - paramAttributedCharacterIterator.getBeginIndex();
    byte[] arrayOfByte2 = new byte[i];
    char[] arrayOfChar = new char[i];
    for (byte b1 = 0; c != Character.MAX_VALUE; b1++) {
      arrayOfChar[b1] = c;
      Integer integer = (Integer)paramAttributedCharacterIterator.getAttribute(TextAttributeConstants.BIDI_EMBEDDING);
      if (integer != null) {
        byte b2 = integer.byteValue();
        if (b2 != 0)
          if (b2 < 0) {
            arrayOfByte1 = arrayOfByte2;
            arrayOfByte2[b1] = (byte)(0 - b2 | 0xFFFFFF80);
          } else {
            arrayOfByte1 = arrayOfByte2;
            arrayOfByte2[b1] = b2;
          }  
      } 
      c = paramAttributedCharacterIterator.next();
    } 
    if (object != null)
      NumericShapings.shape(object, arrayOfChar, 0, i); 
    setPara(arrayOfChar, b, arrayOfByte1);
  }
  
  private void orderParagraphsLTR(boolean paramBoolean) { this.orderParagraphsLTR = paramBoolean; }
  
  private byte getDirection() {
    verifyValidParaOrLine();
    return this.direction;
  }
  
  public int getLength() {
    verifyValidParaOrLine();
    return this.originalLength;
  }
  
  public byte getParaLevel() {
    verifyValidParaOrLine();
    return this.paraLevel;
  }
  
  public int getParagraphIndex(int paramInt) {
    verifyValidParaOrLine();
    BidiBase bidiBase = this.paraBidi;
    verifyRange(paramInt, 0, bidiBase.length);
    byte b;
    for (b = 0; paramInt >= bidiBase.paras[b]; b++);
    return b;
  }
  
  public Bidi setLine(Bidi paramBidi1, BidiBase paramBidiBase1, Bidi paramBidi2, BidiBase paramBidiBase2, int paramInt1, int paramInt2) {
    verifyValidPara();
    verifyRange(paramInt1, 0, paramInt2);
    verifyRange(paramInt2, 0, this.length + 1);
    return BidiLine.setLine(paramBidi1, this, paramBidi2, paramBidiBase2, paramInt1, paramInt2);
  }
  
  public byte getLevelAt(int paramInt) {
    if (paramInt < 0 || paramInt >= this.length)
      return (byte)getBaseLevel(); 
    verifyValidParaOrLine();
    verifyRange(paramInt, 0, this.length);
    return BidiLine.getLevelAt(this, paramInt);
  }
  
  private byte[] getLevels() {
    verifyValidParaOrLine();
    return (this.length <= 0) ? new byte[0] : BidiLine.getLevels(this);
  }
  
  public int countRuns() {
    verifyValidParaOrLine();
    BidiLine.getRuns(this);
    return this.runCount;
  }
  
  private int[] getVisualMap() {
    countRuns();
    return (this.resultLength <= 0) ? new int[0] : BidiLine.getVisualMap(this);
  }
  
  private static int[] reorderVisual(byte[] paramArrayOfByte) { return BidiLine.reorderVisual(paramArrayOfByte); }
  
  public BidiBase(char[] paramArrayOfChar, int paramInt1, byte[] paramArrayOfByte, int paramInt2, int paramInt3, int paramInt4) {
    this(0, 0);
    switch (paramInt4) {
      default:
        b = 0;
        break;
      case 1:
        b = 1;
        break;
      case -2:
        b = 126;
        break;
      case -1:
        b = Byte.MAX_VALUE;
        break;
    } 
    if (paramArrayOfByte == null) {
      arrayOfByte = null;
    } else {
      arrayOfByte = new byte[paramInt3];
      for (int i = 0; i < paramInt3; i++) {
        byte b1 = paramArrayOfByte[i + paramInt2];
        if (b1 < 0) {
          b1 = (byte)(-b1 | 0xFFFFFF80);
        } else if (b1 == 0) {
          b1 = b;
          if (b > 61)
            b1 = (byte)(b1 & true); 
        } 
        arrayOfByte[i] = b1;
      } 
    } 
    if (paramInt1 == 0 && paramInt2 == 0 && paramInt3 == paramArrayOfChar.length) {
      setPara(paramArrayOfChar, b, arrayOfByte);
    } else {
      char[] arrayOfChar = new char[paramInt3];
      System.arraycopy(paramArrayOfChar, paramInt1, arrayOfChar, 0, paramInt3);
      setPara(arrayOfChar, b, arrayOfByte);
    } 
  }
  
  public boolean isMixed() { return (!isLeftToRight() && !isRightToLeft()); }
  
  public boolean isLeftToRight() { return (getDirection() == 0 && (this.paraLevel & true) == 0); }
  
  public boolean isRightToLeft() { return (getDirection() == 1 && (this.paraLevel & true) == 1); }
  
  public boolean baseIsLeftToRight() { return (getParaLevel() == 0); }
  
  public int getBaseLevel() { return getParaLevel(); }
  
  private void getLogicalToVisualRunsMap() {
    if (this.isGoodLogicalToVisualRunsMap)
      return; 
    int i = countRuns();
    if (this.logicalToVisualRunsMap == null || this.logicalToVisualRunsMap.length < i)
      this.logicalToVisualRunsMap = new int[i]; 
    long[] arrayOfLong = new long[i];
    byte b;
    for (b = 0; b < i; b++)
      arrayOfLong[b] = ((this.runs[b]).start << 32) + b; 
    Arrays.sort(arrayOfLong);
    for (b = 0; b < i; b++)
      this.logicalToVisualRunsMap[b] = (int)(arrayOfLong[b] & 0xFFFFFFFFFFFFFFFFL); 
    arrayOfLong = null;
    this.isGoodLogicalToVisualRunsMap = true;
  }
  
  public int getRunLevel(int paramInt) {
    verifyValidParaOrLine();
    BidiLine.getRuns(this);
    if (paramInt < 0 || paramInt >= this.runCount)
      return getParaLevel(); 
    getLogicalToVisualRunsMap();
    return (this.runs[this.logicalToVisualRunsMap[paramInt]]).level;
  }
  
  public int getRunStart(int paramInt) {
    verifyValidParaOrLine();
    BidiLine.getRuns(this);
    if (this.runCount == 1)
      return 0; 
    if (paramInt == this.runCount)
      return this.length; 
    verifyIndex(paramInt, 0, this.runCount);
    getLogicalToVisualRunsMap();
    return (this.runs[this.logicalToVisualRunsMap[paramInt]]).start;
  }
  
  public int getRunLimit(int paramInt) {
    verifyValidParaOrLine();
    BidiLine.getRuns(this);
    if (this.runCount == 1)
      return this.length; 
    verifyIndex(paramInt, 0, this.runCount);
    getLogicalToVisualRunsMap();
    int i = this.logicalToVisualRunsMap[paramInt];
    int j = (i == 0) ? (this.runs[i]).limit : ((this.runs[i]).limit - (this.runs[i - 1]).limit);
    return (this.runs[i]).start + j;
  }
  
  public static boolean requiresBidi(char[] paramArrayOfChar, int paramInt1, int paramInt2) {
    if (0 > paramInt1 || paramInt1 > paramInt2 || paramInt2 > paramArrayOfChar.length)
      throw new IllegalArgumentException("Value start " + paramInt1 + " is out of range 0 to " + paramInt2); 
    for (int i = paramInt1; i < paramInt2; i++) {
      if (Character.isHighSurrogate(paramArrayOfChar[i]) && i < paramInt2 - 1 && Character.isLowSurrogate(paramArrayOfChar[i + 1])) {
        if ((1 << UCharacter.getDirection(Character.codePointAt(paramArrayOfChar, i)) & 0xE022) != 0)
          return true; 
      } else if ((1 << UCharacter.getDirection(paramArrayOfChar[i]) & 0xE022) != 0) {
        return true;
      } 
    } 
    return false;
  }
  
  public static void reorderVisually(byte[] paramArrayOfByte, int paramInt1, Object[] paramArrayOfObject, int paramInt2, int paramInt3) {
    if (0 > paramInt1 || paramArrayOfByte.length <= paramInt1)
      throw new IllegalArgumentException("Value levelStart " + paramInt1 + " is out of range 0 to " + (paramArrayOfByte.length - 1)); 
    if (0 > paramInt2 || paramArrayOfObject.length <= paramInt2)
      throw new IllegalArgumentException("Value objectStart " + paramInt1 + " is out of range 0 to " + (paramArrayOfObject.length - 1)); 
    if (0 > paramInt3 || paramArrayOfObject.length < paramInt2 + paramInt3)
      throw new IllegalArgumentException("Value count " + paramInt1 + " is out of range 0 to " + (paramArrayOfObject.length - paramInt2)); 
    byte[] arrayOfByte = new byte[paramInt3];
    System.arraycopy(paramArrayOfByte, paramInt1, arrayOfByte, 0, paramInt3);
    int[] arrayOfInt = reorderVisual(arrayOfByte);
    Object[] arrayOfObject = new Object[paramInt3];
    System.arraycopy(paramArrayOfObject, paramInt2, arrayOfObject, 0, paramInt3);
    for (int i = 0; i < paramInt3; i++)
      paramArrayOfObject[paramInt2 + i] = arrayOfObject[arrayOfInt[i]]; 
  }
  
  public String toString() {
    StringBuilder stringBuilder = new StringBuilder(getClass().getName());
    stringBuilder.append("[dir: ");
    stringBuilder.append(this.direction);
    stringBuilder.append(" baselevel: ");
    stringBuilder.append(this.paraLevel);
    stringBuilder.append(" length: ");
    stringBuilder.append(this.length);
    stringBuilder.append(" runs: ");
    if (this.levels == null) {
      stringBuilder.append("none");
    } else {
      stringBuilder.append('[');
      stringBuilder.append(this.levels[0]);
      for (byte b1 = 1; b1 < this.levels.length; b1++) {
        stringBuilder.append(' ');
        stringBuilder.append(this.levels[b1]);
      } 
      stringBuilder.append(']');
    } 
    stringBuilder.append(" text: [0x");
    stringBuilder.append(Integer.toHexString(this.text[0]));
    for (byte b = 1; b < this.text.length; b++) {
      stringBuilder.append(" 0x");
      stringBuilder.append(Integer.toHexString(this.text[b]));
    } 
    stringBuilder.append("]]");
    return stringBuilder.toString();
  }
  
  private static class ImpTabPair {
    byte[][][] imptab;
    
    short[][] impact;
    
    ImpTabPair(byte[][] param1ArrayOfByte1, byte[][] param1ArrayOfByte2, short[] param1ArrayOfShort1, short[] param1ArrayOfShort2) {
      this.imptab = new byte[][][] { param1ArrayOfByte1, param1ArrayOfByte2 };
      this.impact = new short[][] { param1ArrayOfShort1, param1ArrayOfShort2 };
    }
  }
  
  class InsertPoints {
    int size;
    
    int confirmed;
    
    BidiBase.Point[] points = new BidiBase.Point[0];
  }
  
  private class LevState {
    byte[][] impTab;
    
    short[] impAct;
    
    int startON;
    
    int startL2EN;
    
    int lastStrongRTL;
    
    short state;
    
    byte runLevel;
    
    private LevState() {}
  }
  
  private static class NumericShapings {
    private static final Class<?> clazz = getClass("java.awt.font.NumericShaper");
    
    private static final Method shapeMethod = getMethod(clazz, "shape", new Class[] { char[].class, int.class, int.class });
    
    private static Class<?> getClass(String param1String) {
      try {
        return Class.forName(param1String, true, null);
      } catch (ClassNotFoundException classNotFoundException) {
        return null;
      } 
    }
    
    private static Method getMethod(Class<?> param1Class, String param1String, Class<?>... param1VarArgs) {
      if (param1Class != null)
        try {
          return param1Class.getMethod(param1String, param1VarArgs);
        } catch (NoSuchMethodException noSuchMethodException) {
          throw new AssertionError(noSuchMethodException);
        }  
      return null;
    }
    
    static void shape(Object param1Object, char[] param1ArrayOfChar, int param1Int1, int param1Int2) {
      if (shapeMethod == null)
        throw new AssertionError("Should not get here"); 
      try {
        shapeMethod.invoke(param1Object, new Object[] { param1ArrayOfChar, Integer.valueOf(param1Int1), Integer.valueOf(param1Int2) });
      } catch (InvocationTargetException invocationTargetException) {
        Throwable throwable = invocationTargetException.getCause();
        if (throwable instanceof RuntimeException)
          throw (RuntimeException)throwable; 
        throw new AssertionError(invocationTargetException);
      } catch (IllegalAccessException illegalAccessException) {
        throw new AssertionError(illegalAccessException);
      } 
    }
  }
  
  class Point {
    int pos;
    
    int flag;
  }
  
  private static class TextAttributeConstants {
    private static final Class<?> clazz = getClass("java.awt.font.TextAttribute");
    
    static final AttributedCharacterIterator.Attribute RUN_DIRECTION = getTextAttribute("RUN_DIRECTION");
    
    static final AttributedCharacterIterator.Attribute NUMERIC_SHAPING = getTextAttribute("NUMERIC_SHAPING");
    
    static final AttributedCharacterIterator.Attribute BIDI_EMBEDDING = getTextAttribute("BIDI_EMBEDDING");
    
    static final Boolean RUN_DIRECTION_LTR = (clazz == null) ? Boolean.FALSE : (Boolean)getStaticField(clazz, "RUN_DIRECTION_LTR");
    
    private static Class<?> getClass(String param1String) {
      try {
        return Class.forName(param1String, true, null);
      } catch (ClassNotFoundException classNotFoundException) {
        return null;
      } 
    }
    
    private static Object getStaticField(Class<?> param1Class, String param1String) {
      try {
        Field field = param1Class.getField(param1String);
        return field.get(null);
      } catch (NoSuchFieldException|IllegalAccessException noSuchFieldException) {
        throw new AssertionError(noSuchFieldException);
      } 
    }
    
    private static AttributedCharacterIterator.Attribute getTextAttribute(String param1String) { return (clazz == null) ? new AttributedCharacterIterator.Attribute(param1String) {
        
        } : (AttributedCharacterIterator.Attribute)getStaticField(clazz, param1String); }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\text\bidi\BidiBase.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */