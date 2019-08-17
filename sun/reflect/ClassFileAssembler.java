package sun.reflect;

class ClassFileAssembler implements ClassFileConstants {
  private ByteVector vec;
  
  private short cpIdx = 0;
  
  private int stack = 0;
  
  private int maxStack = 0;
  
  private int maxLocals = 0;
  
  public ClassFileAssembler() { this(ByteVectorFactory.create()); }
  
  public ClassFileAssembler(ByteVector paramByteVector) { this.vec = paramByteVector; }
  
  public ByteVector getData() { return this.vec; }
  
  public short getLength() { return (short)this.vec.getLength(); }
  
  public void emitMagicAndVersion() {
    emitInt(-889275714);
    emitShort((short)0);
    emitShort((short)49);
  }
  
  public void emitInt(int paramInt) {
    emitByte((byte)(paramInt >> 24));
    emitByte((byte)(paramInt >> 16 & 0xFF));
    emitByte((byte)(paramInt >> 8 & 0xFF));
    emitByte((byte)(paramInt & 0xFF));
  }
  
  public void emitShort(short paramShort) {
    emitByte((byte)(paramShort >> 8 & 0xFF));
    emitByte((byte)(paramShort & 0xFF));
  }
  
  void emitShort(short paramShort1, short paramShort2) {
    this.vec.put(paramShort1, (byte)(paramShort2 >> 8 & 0xFF));
    this.vec.put(paramShort1 + 1, (byte)(paramShort2 & 0xFF));
  }
  
  public void emitByte(byte paramByte) { this.vec.add(paramByte); }
  
  public void append(ClassFileAssembler paramClassFileAssembler) { append(paramClassFileAssembler.vec); }
  
  public void append(ByteVector paramByteVector) {
    for (byte b = 0; b < paramByteVector.getLength(); b++)
      emitByte(paramByteVector.get(b)); 
  }
  
  public short cpi() {
    if (this.cpIdx == 0)
      throw new RuntimeException("Illegal use of ClassFileAssembler"); 
    return this.cpIdx;
  }
  
  public void emitConstantPoolUTF8(String paramString) {
    byte[] arrayOfByte = UTF8.encode(paramString);
    emitByte((byte)1);
    emitShort((short)arrayOfByte.length);
    for (byte b = 0; b < arrayOfByte.length; b++)
      emitByte(arrayOfByte[b]); 
    this.cpIdx = (short)(this.cpIdx + 1);
  }
  
  public void emitConstantPoolClass(short paramShort) {
    emitByte((byte)7);
    emitShort(paramShort);
    this.cpIdx = (short)(this.cpIdx + 1);
  }
  
  public void emitConstantPoolNameAndType(short paramShort1, short paramShort2) {
    emitByte((byte)12);
    emitShort(paramShort1);
    emitShort(paramShort2);
    this.cpIdx = (short)(this.cpIdx + 1);
  }
  
  public void emitConstantPoolFieldref(short paramShort1, short paramShort2) {
    emitByte((byte)9);
    emitShort(paramShort1);
    emitShort(paramShort2);
    this.cpIdx = (short)(this.cpIdx + 1);
  }
  
  public void emitConstantPoolMethodref(short paramShort1, short paramShort2) {
    emitByte((byte)10);
    emitShort(paramShort1);
    emitShort(paramShort2);
    this.cpIdx = (short)(this.cpIdx + 1);
  }
  
  public void emitConstantPoolInterfaceMethodref(short paramShort1, short paramShort2) {
    emitByte((byte)11);
    emitShort(paramShort1);
    emitShort(paramShort2);
    this.cpIdx = (short)(this.cpIdx + 1);
  }
  
  public void emitConstantPoolString(short paramShort) {
    emitByte((byte)8);
    emitShort(paramShort);
    this.cpIdx = (short)(this.cpIdx + 1);
  }
  
  private void incStack() { setStack(this.stack + 1); }
  
  private void decStack() { this.stack--; }
  
  public short getMaxStack() { return (short)this.maxStack; }
  
  public short getMaxLocals() { return (short)this.maxLocals; }
  
  public void setMaxLocals(int paramInt) { this.maxLocals = paramInt; }
  
  public int getStack() { return this.stack; }
  
  public void setStack(int paramInt) {
    this.stack = paramInt;
    if (this.stack > this.maxStack)
      this.maxStack = this.stack; 
  }
  
  public void opc_aconst_null() {
    emitByte((byte)1);
    incStack();
  }
  
  public void opc_sipush(short paramShort) {
    emitByte((byte)17);
    emitShort(paramShort);
    incStack();
  }
  
  public void opc_ldc(byte paramByte) {
    emitByte((byte)18);
    emitByte(paramByte);
    incStack();
  }
  
  public void opc_iload_0() {
    emitByte((byte)26);
    if (this.maxLocals < 1)
      this.maxLocals = 1; 
    incStack();
  }
  
  public void opc_iload_1() {
    emitByte((byte)27);
    if (this.maxLocals < 2)
      this.maxLocals = 2; 
    incStack();
  }
  
  public void opc_iload_2() {
    emitByte((byte)28);
    if (this.maxLocals < 3)
      this.maxLocals = 3; 
    incStack();
  }
  
  public void opc_iload_3() {
    emitByte((byte)29);
    if (this.maxLocals < 4)
      this.maxLocals = 4; 
    incStack();
  }
  
  public void opc_lload_0() {
    emitByte((byte)30);
    if (this.maxLocals < 2)
      this.maxLocals = 2; 
    incStack();
    incStack();
  }
  
  public void opc_lload_1() {
    emitByte((byte)31);
    if (this.maxLocals < 3)
      this.maxLocals = 3; 
    incStack();
    incStack();
  }
  
  public void opc_lload_2() {
    emitByte((byte)32);
    if (this.maxLocals < 4)
      this.maxLocals = 4; 
    incStack();
    incStack();
  }
  
  public void opc_lload_3() {
    emitByte((byte)33);
    if (this.maxLocals < 5)
      this.maxLocals = 5; 
    incStack();
    incStack();
  }
  
  public void opc_fload_0() {
    emitByte((byte)34);
    if (this.maxLocals < 1)
      this.maxLocals = 1; 
    incStack();
  }
  
  public void opc_fload_1() {
    emitByte((byte)35);
    if (this.maxLocals < 2)
      this.maxLocals = 2; 
    incStack();
  }
  
  public void opc_fload_2() {
    emitByte((byte)36);
    if (this.maxLocals < 3)
      this.maxLocals = 3; 
    incStack();
  }
  
  public void opc_fload_3() {
    emitByte((byte)37);
    if (this.maxLocals < 4)
      this.maxLocals = 4; 
    incStack();
  }
  
  public void opc_dload_0() {
    emitByte((byte)38);
    if (this.maxLocals < 2)
      this.maxLocals = 2; 
    incStack();
    incStack();
  }
  
  public void opc_dload_1() {
    emitByte((byte)39);
    if (this.maxLocals < 3)
      this.maxLocals = 3; 
    incStack();
    incStack();
  }
  
  public void opc_dload_2() {
    emitByte((byte)40);
    if (this.maxLocals < 4)
      this.maxLocals = 4; 
    incStack();
    incStack();
  }
  
  public void opc_dload_3() {
    emitByte((byte)41);
    if (this.maxLocals < 5)
      this.maxLocals = 5; 
    incStack();
    incStack();
  }
  
  public void opc_aload_0() {
    emitByte((byte)42);
    if (this.maxLocals < 1)
      this.maxLocals = 1; 
    incStack();
  }
  
  public void opc_aload_1() {
    emitByte((byte)43);
    if (this.maxLocals < 2)
      this.maxLocals = 2; 
    incStack();
  }
  
  public void opc_aload_2() {
    emitByte((byte)44);
    if (this.maxLocals < 3)
      this.maxLocals = 3; 
    incStack();
  }
  
  public void opc_aload_3() {
    emitByte((byte)45);
    if (this.maxLocals < 4)
      this.maxLocals = 4; 
    incStack();
  }
  
  public void opc_aaload() {
    emitByte((byte)50);
    decStack();
  }
  
  public void opc_astore_0() {
    emitByte((byte)75);
    if (this.maxLocals < 1)
      this.maxLocals = 1; 
    decStack();
  }
  
  public void opc_astore_1() {
    emitByte((byte)76);
    if (this.maxLocals < 2)
      this.maxLocals = 2; 
    decStack();
  }
  
  public void opc_astore_2() {
    emitByte((byte)77);
    if (this.maxLocals < 3)
      this.maxLocals = 3; 
    decStack();
  }
  
  public void opc_astore_3() {
    emitByte((byte)78);
    if (this.maxLocals < 4)
      this.maxLocals = 4; 
    decStack();
  }
  
  public void opc_pop() {
    emitByte((byte)87);
    decStack();
  }
  
  public void opc_dup() {
    emitByte((byte)89);
    incStack();
  }
  
  public void opc_dup_x1() {
    emitByte((byte)90);
    incStack();
  }
  
  public void opc_swap() { emitByte((byte)95); }
  
  public void opc_i2l() { emitByte((byte)-123); }
  
  public void opc_i2f() { emitByte((byte)-122); }
  
  public void opc_i2d() { emitByte((byte)-121); }
  
  public void opc_l2f() { emitByte((byte)-119); }
  
  public void opc_l2d() { emitByte((byte)-118); }
  
  public void opc_f2d() { emitByte((byte)-115); }
  
  public void opc_ifeq(short paramShort) {
    emitByte((byte)-103);
    emitShort(paramShort);
    decStack();
  }
  
  public void opc_ifeq(Label paramLabel) {
    short s = getLength();
    emitByte((byte)-103);
    paramLabel.add(this, s, getLength(), getStack() - 1);
    emitShort((short)-1);
  }
  
  public void opc_if_icmpeq(short paramShort) {
    emitByte((byte)-97);
    emitShort(paramShort);
    setStack(getStack() - 2);
  }
  
  public void opc_if_icmpeq(Label paramLabel) {
    short s = getLength();
    emitByte((byte)-97);
    paramLabel.add(this, s, getLength(), getStack() - 2);
    emitShort((short)-1);
  }
  
  public void opc_goto(short paramShort) {
    emitByte((byte)-89);
    emitShort(paramShort);
  }
  
  public void opc_goto(Label paramLabel) {
    short s = getLength();
    emitByte((byte)-89);
    paramLabel.add(this, s, getLength(), getStack());
    emitShort((short)-1);
  }
  
  public void opc_ifnull(short paramShort) {
    emitByte((byte)-58);
    emitShort(paramShort);
    decStack();
  }
  
  public void opc_ifnull(Label paramLabel) {
    short s = getLength();
    emitByte((byte)-58);
    paramLabel.add(this, s, getLength(), getStack() - 1);
    emitShort((short)-1);
    decStack();
  }
  
  public void opc_ifnonnull(short paramShort) {
    emitByte((byte)-57);
    emitShort(paramShort);
    decStack();
  }
  
  public void opc_ifnonnull(Label paramLabel) {
    short s = getLength();
    emitByte((byte)-57);
    paramLabel.add(this, s, getLength(), getStack() - 1);
    emitShort((short)-1);
    decStack();
  }
  
  public void opc_ireturn() {
    emitByte((byte)-84);
    setStack(0);
  }
  
  public void opc_lreturn() {
    emitByte((byte)-83);
    setStack(0);
  }
  
  public void opc_freturn() {
    emitByte((byte)-82);
    setStack(0);
  }
  
  public void opc_dreturn() {
    emitByte((byte)-81);
    setStack(0);
  }
  
  public void opc_areturn() {
    emitByte((byte)-80);
    setStack(0);
  }
  
  public void opc_return() {
    emitByte((byte)-79);
    setStack(0);
  }
  
  public void opc_getstatic(short paramShort, int paramInt) {
    emitByte((byte)-78);
    emitShort(paramShort);
    setStack(getStack() + paramInt);
  }
  
  public void opc_putstatic(short paramShort, int paramInt) {
    emitByte((byte)-77);
    emitShort(paramShort);
    setStack(getStack() - paramInt);
  }
  
  public void opc_getfield(short paramShort, int paramInt) {
    emitByte((byte)-76);
    emitShort(paramShort);
    setStack(getStack() + paramInt - 1);
  }
  
  public void opc_putfield(short paramShort, int paramInt) {
    emitByte((byte)-75);
    emitShort(paramShort);
    setStack(getStack() - paramInt - 1);
  }
  
  public void opc_invokevirtual(short paramShort, int paramInt1, int paramInt2) {
    emitByte((byte)-74);
    emitShort(paramShort);
    setStack(getStack() - paramInt1 - 1 + paramInt2);
  }
  
  public void opc_invokespecial(short paramShort, int paramInt1, int paramInt2) {
    emitByte((byte)-73);
    emitShort(paramShort);
    setStack(getStack() - paramInt1 - 1 + paramInt2);
  }
  
  public void opc_invokestatic(short paramShort, int paramInt1, int paramInt2) {
    emitByte((byte)-72);
    emitShort(paramShort);
    setStack(getStack() - paramInt1 + paramInt2);
  }
  
  public void opc_invokeinterface(short paramShort, int paramInt1, byte paramByte, int paramInt2) {
    emitByte((byte)-71);
    emitShort(paramShort);
    emitByte(paramByte);
    emitByte((byte)0);
    setStack(getStack() - paramInt1 - 1 + paramInt2);
  }
  
  public void opc_arraylength() { emitByte((byte)-66); }
  
  public void opc_new(short paramShort) {
    emitByte((byte)-69);
    emitShort(paramShort);
    incStack();
  }
  
  public void opc_athrow() {
    emitByte((byte)-65);
    setStack(1);
  }
  
  public void opc_checkcast(short paramShort) {
    emitByte((byte)-64);
    emitShort(paramShort);
  }
  
  public void opc_instanceof(short paramShort) {
    emitByte((byte)-63);
    emitShort(paramShort);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\reflect\ClassFileAssembler.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */