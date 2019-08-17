package com.sun.java.util.jar.pack;

import java.io.IOException;
import java.util.Arrays;

class Instruction {
  protected byte[] bytes;
  
  protected int pc;
  
  protected int bc;
  
  protected int w;
  
  protected int length;
  
  protected boolean special;
  
  private static final byte[][] BC_LENGTH = new byte[2][256];
  
  private static final byte[][] BC_INDEX = new byte[2][256];
  
  private static final byte[][] BC_TAG = new byte[2][256];
  
  private static final byte[][] BC_BRANCH = new byte[2][256];
  
  private static final byte[][] BC_SLOT = new byte[2][256];
  
  private static final byte[][] BC_CON = new byte[2][256];
  
  private static final String[] BC_NAME = new String[256];
  
  private static final String[][] BC_FORMAT = new String[2][202];
  
  private static int BW;
  
  protected Instruction(byte[] paramArrayOfByte, int paramInt1, int paramInt2, int paramInt3, int paramInt4) { reset(paramArrayOfByte, paramInt1, paramInt2, paramInt3, paramInt4); }
  
  private void reset(byte[] paramArrayOfByte, int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
    this.bytes = paramArrayOfByte;
    this.pc = paramInt1;
    this.bc = paramInt2;
    this.w = paramInt3;
    this.length = paramInt4;
  }
  
  public int getBC() { return this.bc; }
  
  public boolean isWide() { return (this.w != 0); }
  
  public byte[] getBytes() { return this.bytes; }
  
  public int getPC() { return this.pc; }
  
  public int getLength() { return this.length; }
  
  public int getNextPC() { return this.pc + this.length; }
  
  public Instruction next() {
    int i = this.pc + this.length;
    return (i == this.bytes.length) ? null : at(this.bytes, i, this);
  }
  
  public boolean isNonstandard() { return isNonstandard(this.bc); }
  
  public void setNonstandardLength(int paramInt) {
    assert isNonstandard();
    this.length = paramInt;
  }
  
  public Instruction forceNextPC(int paramInt) {
    int i = paramInt - this.pc;
    return new Instruction(this.bytes, this.pc, -1, -1, i);
  }
  
  public static Instruction at(byte[] paramArrayOfByte, int paramInt) { return at(paramArrayOfByte, paramInt, null); }
  
  public static Instruction at(byte[] paramArrayOfByte, int paramInt, Instruction paramInstruction) {
    int i = getByte(paramArrayOfByte, paramInt);
    byte b = -1;
    byte b1 = 0;
    byte b2 = BC_LENGTH[b1][i];
    if (b2 == 0)
      switch (i) {
        case 196:
          i = getByte(paramArrayOfByte, paramInt + 1);
          b1 = 1;
          b2 = BC_LENGTH[b1][i];
          if (b2 == 0)
            b2 = 1; 
          break;
        case 170:
          return new TableSwitch(paramArrayOfByte, paramInt);
        case 171:
          return new LookupSwitch(paramArrayOfByte, paramInt);
        default:
          b2 = 1;
          break;
      }  
    assert b2 > 0;
    assert paramInt + b2 <= paramArrayOfByte.length;
    if (paramInstruction != null && !paramInstruction.special) {
      paramInstruction.reset(paramArrayOfByte, paramInt, i, b1, b2);
      return paramInstruction;
    } 
    return new Instruction(paramArrayOfByte, paramInt, i, b1, b2);
  }
  
  public byte getCPTag() { return BC_TAG[this.w][this.bc]; }
  
  public int getCPIndex() {
    byte b = BC_INDEX[this.w][this.bc];
    if (b == 0)
      return -1; 
    assert this.w == 0;
    return (this.length == 2) ? getByte(this.bytes, this.pc + b) : getShort(this.bytes, this.pc + b);
  }
  
  public void setCPIndex(int paramInt) {
    byte b = BC_INDEX[this.w][this.bc];
    assert b != 0;
    if (this.length == 2) {
      setByte(this.bytes, this.pc + b, paramInt);
    } else {
      setShort(this.bytes, this.pc + b, paramInt);
    } 
    assert getCPIndex() == paramInt;
  }
  
  public ConstantPool.Entry getCPRef(ConstantPool.Entry[] paramArrayOfEntry) {
    int i = getCPIndex();
    return (i < 0) ? null : paramArrayOfEntry[i];
  }
  
  public int getLocalSlot() {
    byte b = BC_SLOT[this.w][this.bc];
    return (b == 0) ? -1 : ((this.w == 0) ? getByte(this.bytes, this.pc + b) : getShort(this.bytes, this.pc + b));
  }
  
  public int getBranchLabel() {
    int i;
    byte b = BC_BRANCH[this.w][this.bc];
    if (b == 0)
      return -1; 
    assert this.w == 0;
    assert this.length == 3 || this.length == 5;
    if (this.length == 3) {
      i = (short)getShort(this.bytes, this.pc + b);
    } else {
      i = getInt(this.bytes, this.pc + b);
    } 
    assert i + this.pc >= 0;
    assert i + this.pc <= this.bytes.length;
    return i + this.pc;
  }
  
  public void setBranchLabel(int paramInt) {
    byte b = BC_BRANCH[this.w][this.bc];
    assert b != 0;
    if (this.length == 3) {
      setShort(this.bytes, this.pc + b, paramInt - this.pc);
    } else {
      setInt(this.bytes, this.pc + b, paramInt - this.pc);
    } 
    assert paramInt == getBranchLabel();
  }
  
  public int getConstant() {
    byte b = BC_CON[this.w][this.bc];
    if (b == 0)
      return 0; 
    switch (this.length - b) {
      case 1:
        return (byte)getByte(this.bytes, this.pc + b);
      case 2:
        return (short)getShort(this.bytes, this.pc + b);
    } 
    assert false;
    return 0;
  }
  
  public void setConstant(int paramInt) {
    byte b = BC_CON[this.w][this.bc];
    assert b != 0;
    switch (this.length - b) {
      case 1:
        setByte(this.bytes, this.pc + b, paramInt);
        break;
      case 2:
        setShort(this.bytes, this.pc + b, paramInt);
        break;
    } 
    assert paramInt == getConstant();
  }
  
  public boolean equals(Object paramObject) { return (paramObject != null && paramObject.getClass() == Instruction.class && equals((Instruction)paramObject)); }
  
  public int hashCode() {
    null = 3;
    null = 11 * null + Arrays.hashCode(this.bytes);
    null = 11 * null + this.pc;
    null = 11 * null + this.bc;
    null = 11 * null + this.w;
    return 11 * null + this.length;
  }
  
  public boolean equals(Instruction paramInstruction) {
    if (this.pc != paramInstruction.pc)
      return false; 
    if (this.bc != paramInstruction.bc)
      return false; 
    if (this.w != paramInstruction.w)
      return false; 
    if (this.length != paramInstruction.length)
      return false; 
    for (int i = 1; i < this.length; i++) {
      if (this.bytes[this.pc + i] != paramInstruction.bytes[paramInstruction.pc + i])
        return false; 
    } 
    return true;
  }
  
  static String labstr(int paramInt) { return (paramInt >= 0 && paramInt < 100000) ? ((100000 + paramInt) + "").substring(1) : (paramInt + ""); }
  
  public String toString() { return toString(null); }
  
  public String toString(ConstantPool.Entry[] paramArrayOfEntry) {
    String str1 = labstr(this.pc) + ": ";
    if (this.bc >= 202)
      return str1 + Integer.toHexString(this.bc); 
    if (this.w == 1)
      str1 = str1 + "wide "; 
    String str2 = (this.bc < BC_NAME.length) ? BC_NAME[this.bc] : null;
    if (str2 == null)
      return str1 + "opcode#" + this.bc; 
    str1 = str1 + str2;
    byte b = getCPTag();
    if (b != 0)
      str1 = str1 + " " + ConstantPool.tagName(b) + ":"; 
    int i = getCPIndex();
    if (i >= 0)
      str1 = str1 + ((paramArrayOfEntry == null) ? ("" + i) : ("=" + paramArrayOfEntry[i].stringValue())); 
    int j = getLocalSlot();
    if (j >= 0)
      str1 = str1 + " Local:" + j; 
    int k = getBranchLabel();
    if (k >= 0)
      str1 = str1 + " To:" + labstr(k); 
    int m = getConstant();
    if (m != 0)
      str1 = str1 + " Con:" + m; 
    return str1;
  }
  
  public int getIntAt(int paramInt) { return getInt(this.bytes, this.pc + paramInt); }
  
  public int getShortAt(int paramInt) { return getShort(this.bytes, this.pc + paramInt); }
  
  public int getByteAt(int paramInt) { return getByte(this.bytes, this.pc + paramInt); }
  
  public static int getInt(byte[] paramArrayOfByte, int paramInt) { return (getShort(paramArrayOfByte, paramInt + 0) << 16) + (getShort(paramArrayOfByte, paramInt + 2) << 0); }
  
  public static int getShort(byte[] paramArrayOfByte, int paramInt) { return (getByte(paramArrayOfByte, paramInt + 0) << 8) + (getByte(paramArrayOfByte, paramInt + 1) << 0); }
  
  public static int getByte(byte[] paramArrayOfByte, int paramInt) { return paramArrayOfByte[paramInt] & 0xFF; }
  
  public static void setInt(byte[] paramArrayOfByte, int paramInt1, int paramInt2) {
    setShort(paramArrayOfByte, paramInt1 + 0, paramInt2 >> 16);
    setShort(paramArrayOfByte, paramInt1 + 2, paramInt2 >> 0);
  }
  
  public static void setShort(byte[] paramArrayOfByte, int paramInt1, int paramInt2) {
    setByte(paramArrayOfByte, paramInt1 + 0, paramInt2 >> 8);
    setByte(paramArrayOfByte, paramInt1 + 1, paramInt2 >> 0);
  }
  
  public static void setByte(byte[] paramArrayOfByte, int paramInt1, int paramInt2) { paramArrayOfByte[paramInt1] = (byte)paramInt2; }
  
  public static boolean isNonstandard(int paramInt) { return (BC_LENGTH[0][paramInt] < 0); }
  
  public static int opLength(int paramInt) {
    byte b = BC_LENGTH[0][paramInt];
    assert b > 0;
    return b;
  }
  
  public static int opWideLength(int paramInt) {
    byte b = BC_LENGTH[1][paramInt];
    assert b > 0;
    return b;
  }
  
  public static boolean isLocalSlotOp(int paramInt) { return (paramInt < BC_SLOT[0].length && BC_SLOT[0][paramInt] > 0); }
  
  public static boolean isBranchOp(int paramInt) { return (paramInt < BC_BRANCH[0].length && BC_BRANCH[0][paramInt] > 0); }
  
  public static boolean isCPRefOp(int paramInt) { return (paramInt < BC_INDEX[0].length && BC_INDEX[0][paramInt] > 0) ? true : ((paramInt >= 233 && paramInt < 242) ? true : ((paramInt == 242 || paramInt == 243))); }
  
  public static byte getCPRefOpTag(int paramInt) { return (paramInt < BC_INDEX[0].length && BC_INDEX[0][paramInt] > 0) ? BC_TAG[0][paramInt] : ((paramInt >= 233 && paramInt < 242) ? 51 : ((paramInt == 243 || paramInt == 242) ? 11 : 0)); }
  
  public static boolean isFieldOp(int paramInt) { return (paramInt >= 178 && paramInt <= 181); }
  
  public static boolean isInvokeInitOp(int paramInt) { return (paramInt >= 230 && paramInt < 233); }
  
  public static boolean isSelfLinkerOp(int paramInt) { return (paramInt >= 202 && paramInt < 230); }
  
  public static String byteName(int paramInt) {
    String str;
    if (paramInt < BC_NAME.length && BC_NAME[paramInt] != null) {
      str = BC_NAME[paramInt];
    } else if (isSelfLinkerOp(paramInt)) {
      int i = paramInt - 202;
      boolean bool1 = (i >= 14) ? 1 : 0;
      if (bool1)
        i -= 14; 
      boolean bool2 = (i >= 7) ? 1 : 0;
      if (bool2)
        i -= 7; 
      int j = 178 + i;
      assert j >= 178 && j <= 184;
      str = BC_NAME[j];
      str = str + (bool1 ? "_super" : "_this");
      if (bool2)
        str = "aload_0&" + str; 
      str = "*" + str;
    } else if (isInvokeInitOp(paramInt)) {
      int i = paramInt - 230;
      switch (i) {
        case 0:
          return "*invokespecial_init_this";
        case 1:
          return "*invokespecial_init_super";
      } 
      assert i == 2;
      str = "*invokespecial_init_new";
    } else {
      switch (paramInt) {
        case 234:
          return "*ildc";
        case 235:
          return "*fldc";
        case 237:
          return "*ildc_w";
        case 238:
          return "*fldc_w";
        case 239:
          return "*dldc2_w";
        case 233:
          return "*cldc";
        case 236:
          return "*cldc_w";
        case 240:
          return "*qldc";
        case 241:
          return "*qldc_w";
        case 254:
          return "*byte_escape";
        case 253:
          return "*ref_escape";
        case 255:
          return "*end";
      } 
      str = "*bc#" + paramInt;
    } 
    return str;
  }
  
  private static void def(String paramString, int paramInt) { def(paramString, paramInt, paramInt); }
  
  private static void def(String paramString, int paramInt1, int paramInt2) {
    String[] arrayOfString = { paramString, null };
    if (paramString.indexOf('w') > 0) {
      arrayOfString[1] = paramString.substring(paramString.indexOf('w'));
      arrayOfString[0] = paramString.substring(0, paramString.indexOf('w'));
    } 
    for (byte b = 0; b <= 1; b++) {
      paramString = arrayOfString[b];
      if (paramString != null) {
        int i = paramString.length();
        int j = Math.max(0, paramString.indexOf('k'));
        byte b1 = 0;
        int k = Math.max(0, paramString.indexOf('o'));
        int m = Math.max(0, paramString.indexOf('l'));
        int n = Math.max(0, paramString.indexOf('x'));
        if (j > 0 && j + 1 < i) {
          switch (paramString.charAt(j + 1)) {
            case 'c':
              b1 = 7;
              break;
            case 'k':
              b1 = 51;
              break;
            case 'f':
              b1 = 9;
              break;
            case 'm':
              b1 = 10;
              break;
            case 'i':
              b1 = 11;
              break;
            case 'y':
              b1 = 18;
              break;
          } 
          assert b1 != 0;
        } else if (j > 0 && i == 2) {
          assert paramInt1 == 18;
          b1 = 51;
        } 
        for (int i1 = paramInt1; i1 <= paramInt2; i1++) {
          BC_FORMAT[b][i1] = paramString;
          assert BC_LENGTH[b][i1] == -1;
          BC_LENGTH[b][i1] = (byte)i;
          BC_INDEX[b][i1] = (byte)j;
          BC_TAG[b][i1] = (byte)b1;
          assert j != 0 || b1 == 0;
          BC_BRANCH[b][i1] = (byte)k;
          BC_SLOT[b][i1] = (byte)m;
          assert k == 0 || m == 0;
          assert k == 0 || j == 0;
          assert m == 0 || j == 0;
          BC_CON[b][i1] = (byte)n;
        } 
      } 
    } 
  }
  
  public static void opcodeChecker(byte[] paramArrayOfByte, ConstantPool.Entry[] paramArrayOfEntry, Package.Version paramVersion) throws FormatException {
    for (Instruction instruction = at(paramArrayOfByte, 0); instruction != null; instruction = instruction.next()) {
      int i = instruction.getBC();
      if (i < 0 || i > 201) {
        String str = "illegal opcode: " + i + " " + instruction;
        throw new FormatException(str);
      } 
      ConstantPool.Entry entry = instruction.getCPRef(paramArrayOfEntry);
      if (entry != null) {
        byte b = instruction.getCPTag();
        boolean bool = entry.tagMatches(b);
        if (!bool && (instruction.bc == 183 || instruction.bc == 184) && entry.tagMatches(11) && paramVersion.greaterThan(Constants.JAVA7_MAX_CLASS_VERSION))
          bool = true; 
        if (!bool) {
          String str = "illegal reference, expected type=" + ConstantPool.tagName(b) + ": " + instruction.toString(paramArrayOfEntry);
          throw new FormatException(str);
        } 
      } 
    } 
  }
  
  static  {
    byte b1;
    for (b1 = 0; b1 < 'Ê'; b1++) {
      BC_LENGTH[0][b1] = -1;
      BC_LENGTH[1][b1] = -1;
    } 
    def("b", 0, 15);
    def("bx", 16);
    def("bxx", 17);
    def("bk", 18);
    def("bkk", 19, 20);
    def("blwbll", 21, 25);
    def("b", 26, 53);
    def("blwbll", 54, 58);
    def("b", 59, 131);
    def("blxwbllxx", 132);
    def("b", 133, 152);
    def("boo", 153, 168);
    def("blwbll", 169);
    def("", 170, 171);
    def("b", 172, 177);
    def("bkf", 178, 181);
    def("bkm", 182, 184);
    def("bkixx", 185);
    def("bkyxx", 186);
    def("bkc", 187);
    def("bx", 188);
    def("bkc", 189);
    def("b", 190, 191);
    def("bkc", 192, 193);
    def("b", 194, 195);
    def("", 196);
    def("bkcx", 197);
    def("boo", 198, 199);
    def("boooo", 200, 201);
    for (b1 = 0; b1 < 'Ê'; b1++) {
      if (BC_LENGTH[0][b1] != -1 && BC_LENGTH[1][b1] == -1)
        BC_LENGTH[1][b1] = (byte)(1 + BC_LENGTH[0][b1]); 
    } 
    String str = "nop aconst_null iconst_m1 iconst_0 iconst_1 iconst_2 iconst_3 iconst_4 iconst_5 lconst_0 lconst_1 fconst_0 fconst_1 fconst_2 dconst_0 dconst_1 bipush sipush ldc ldc_w ldc2_w iload lload fload dload aload iload_0 iload_1 iload_2 iload_3 lload_0 lload_1 lload_2 lload_3 fload_0 fload_1 fload_2 fload_3 dload_0 dload_1 dload_2 dload_3 aload_0 aload_1 aload_2 aload_3 iaload laload faload daload aaload baload caload saload istore lstore fstore dstore astore istore_0 istore_1 istore_2 istore_3 lstore_0 lstore_1 lstore_2 lstore_3 fstore_0 fstore_1 fstore_2 fstore_3 dstore_0 dstore_1 dstore_2 dstore_3 astore_0 astore_1 astore_2 astore_3 iastore lastore fastore dastore aastore bastore castore sastore pop pop2 dup dup_x1 dup_x2 dup2 dup2_x1 dup2_x2 swap iadd ladd fadd dadd isub lsub fsub dsub imul lmul fmul dmul idiv ldiv fdiv ddiv irem lrem frem drem ineg lneg fneg dneg ishl lshl ishr lshr iushr lushr iand land ior lor ixor lxor iinc i2l i2f i2d l2i l2f l2d f2i f2l f2d d2i d2l d2f i2b i2c i2s lcmp fcmpl fcmpg dcmpl dcmpg ifeq ifne iflt ifge ifgt ifle if_icmpeq if_icmpne if_icmplt if_icmpge if_icmpgt if_icmple if_acmpeq if_acmpne goto jsr ret tableswitch lookupswitch ireturn lreturn freturn dreturn areturn return getstatic putstatic getfield putfield invokevirtual invokespecial invokestatic invokeinterface invokedynamic new newarray anewarray arraylength athrow checkcast instanceof monitorenter monitorexit wide multianewarray ifnull ifnonnull goto_w jsr_w ";
    for (byte b2 = 0; str.length() > 0; b2++) {
      int i = str.indexOf(' ');
      BC_NAME[b2] = str.substring(0, i);
      str = str.substring(i + 1);
    } 
    BW = 4;
  }
  
  static class FormatException extends IOException {
    private static final long serialVersionUID = 3175572275651367015L;
    
    FormatException(String param1String) { super(param1String); }
  }
  
  public static class LookupSwitch extends Switch {
    public int getCaseCount() { return intAt(1); }
    
    public int getCaseValue(int param1Int) { return intAt(2 + param1Int * 2 + 0); }
    
    public int getCaseLabel(int param1Int) { return intAt(2 + param1Int * 2 + 1) + this.pc; }
    
    public void setCaseCount(int param1Int) {
      setIntAt(1, param1Int);
      this.length = getLength(param1Int);
    }
    
    public void setCaseValue(int param1Int1, int param1Int2) { setIntAt(2 + param1Int1 * 2 + 0, param1Int2); }
    
    public void setCaseLabel(int param1Int1, int param1Int2) { setIntAt(2 + param1Int1 * 2 + 1, param1Int2 - this.pc); }
    
    LookupSwitch(byte[] param1ArrayOfByte, int param1Int) { super(param1ArrayOfByte, param1Int, 171); }
    
    protected int getLength(int param1Int) { return this.apc - this.pc + (2 + param1Int * 2) * 4; }
  }
  
  public static abstract class Switch extends Instruction {
    protected int apc;
    
    public abstract int getCaseCount();
    
    public abstract int getCaseValue(int param1Int);
    
    public abstract int getCaseLabel(int param1Int);
    
    public abstract void setCaseCount(int param1Int);
    
    public abstract void setCaseValue(int param1Int1, int param1Int2);
    
    public abstract void setCaseLabel(int param1Int1, int param1Int2);
    
    protected abstract int getLength(int param1Int);
    
    public int getDefaultLabel() { return intAt(0) + this.pc; }
    
    public void setDefaultLabel(int param1Int) { setIntAt(0, param1Int - this.pc); }
    
    protected int intAt(int param1Int) { return getInt(this.bytes, this.apc + param1Int * 4); }
    
    protected void setIntAt(int param1Int1, int param1Int2) { setInt(this.bytes, this.apc + param1Int1 * 4, param1Int2); }
    
    protected Switch(byte[] param1ArrayOfByte, int param1Int1, int param1Int2) {
      super(param1ArrayOfByte, param1Int1, param1Int2, 0, 0);
      this.apc = alignPC(param1Int1 + 1);
      this.special = true;
      this.length = getLength(getCaseCount());
    }
    
    public int getAlignedPC() { return this.apc; }
    
    public String toString() {
      String str = super.toString();
      str = str + " Default:" + labstr(getDefaultLabel());
      int i = getCaseCount();
      for (byte b = 0; b < i; b++)
        str = str + "\n\tCase " + getCaseValue(b) + ":" + labstr(getCaseLabel(b)); 
      return str;
    }
    
    public static int alignPC(int param1Int) {
      while (param1Int % 4 != 0)
        param1Int++; 
      return param1Int;
    }
  }
  
  public static class TableSwitch extends Switch {
    public int getLowCase() { return intAt(1); }
    
    public int getHighCase() { return intAt(2); }
    
    public int getCaseCount() { return intAt(2) - intAt(1) + 1; }
    
    public int getCaseValue(int param1Int) { return getLowCase() + param1Int; }
    
    public int getCaseLabel(int param1Int) { return intAt(3 + param1Int) + this.pc; }
    
    public void setLowCase(int param1Int) { setIntAt(1, param1Int); }
    
    public void setHighCase(int param1Int) { setIntAt(2, param1Int); }
    
    public void setCaseLabel(int param1Int1, int param1Int2) { setIntAt(3 + param1Int1, param1Int2 - this.pc); }
    
    public void setCaseCount(int param1Int) {
      setHighCase(getLowCase() + param1Int - 1);
      this.length = getLength(param1Int);
    }
    
    public void setCaseValue(int param1Int1, int param1Int2) {
      if (param1Int1 != 0)
        throw new UnsupportedOperationException(); 
      int i = getCaseCount();
      setLowCase(param1Int2);
      setCaseCount(i);
    }
    
    TableSwitch(byte[] param1ArrayOfByte, int param1Int) { super(param1ArrayOfByte, param1Int, 170); }
    
    protected int getLength(int param1Int) { return this.apc - this.pc + (3 + param1Int) * 4; }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\jav\\util\jar\pack\Instruction.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */