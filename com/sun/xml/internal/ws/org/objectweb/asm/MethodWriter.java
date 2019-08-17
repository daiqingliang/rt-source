package com.sun.xml.internal.ws.org.objectweb.asm;

class MethodWriter implements MethodVisitor {
  static final int ACC_CONSTRUCTOR = 262144;
  
  static final int SAME_FRAME = 0;
  
  static final int SAME_LOCALS_1_STACK_ITEM_FRAME = 64;
  
  static final int RESERVED = 128;
  
  static final int SAME_LOCALS_1_STACK_ITEM_FRAME_EXTENDED = 247;
  
  static final int CHOP_FRAME = 248;
  
  static final int SAME_FRAME_EXTENDED = 251;
  
  static final int APPEND_FRAME = 252;
  
  static final int FULL_FRAME = 255;
  
  private static final int FRAMES = 0;
  
  private static final int MAXS = 1;
  
  private static final int NOTHING = 2;
  
  MethodWriter next;
  
  final ClassWriter cw;
  
  private int access;
  
  private final int name;
  
  private final int desc;
  
  private final String descriptor;
  
  String signature;
  
  int classReaderOffset;
  
  int classReaderLength;
  
  int exceptionCount;
  
  int[] exceptions;
  
  private ByteVector annd;
  
  private AnnotationWriter anns;
  
  private AnnotationWriter ianns;
  
  private AnnotationWriter[] panns;
  
  private AnnotationWriter[] ipanns;
  
  private int synthetics;
  
  private Attribute attrs;
  
  private ByteVector code = new ByteVector();
  
  private int maxStack;
  
  private int maxLocals;
  
  private int frameCount;
  
  private ByteVector stackMap;
  
  private int previousFrameOffset;
  
  private int[] previousFrame;
  
  private int frameIndex;
  
  private int[] frame;
  
  private int handlerCount;
  
  private Handler firstHandler;
  
  private Handler lastHandler;
  
  private int localVarCount;
  
  private ByteVector localVar;
  
  private int localVarTypeCount;
  
  private ByteVector localVarType;
  
  private int lineNumberCount;
  
  private ByteVector lineNumber;
  
  private Attribute cattrs;
  
  private boolean resize;
  
  private int subroutines;
  
  private final int compute;
  
  private Label labels;
  
  private Label previousBlock;
  
  private Label currentBlock;
  
  private int stackSize;
  
  private int maxStackSize;
  
  MethodWriter(ClassWriter paramClassWriter, int paramInt, String paramString1, String paramString2, String paramString3, String[] paramArrayOfString, boolean paramBoolean1, boolean paramBoolean2) {
    if (paramClassWriter.firstMethod == null) {
      paramClassWriter.firstMethod = this;
    } else {
      paramClassWriter.lastMethod.next = this;
    } 
    paramClassWriter.lastMethod = this;
    this.cw = paramClassWriter;
    this.access = paramInt;
    this.name = paramClassWriter.newUTF8(paramString1);
    this.desc = paramClassWriter.newUTF8(paramString2);
    this.descriptor = paramString2;
    this.signature = paramString3;
    if (paramArrayOfString != null && paramArrayOfString.length > 0) {
      this.exceptionCount = paramArrayOfString.length;
      this.exceptions = new int[this.exceptionCount];
      for (byte b = 0; b < this.exceptionCount; b++)
        this.exceptions[b] = paramClassWriter.newClass(paramArrayOfString[b]); 
    } 
    this.compute = paramBoolean2 ? 0 : (paramBoolean1 ? 1 : 2);
    if (paramBoolean1 || paramBoolean2) {
      if (paramBoolean2 && "<init>".equals(paramString1))
        this.access |= 0x40000; 
      int i = getArgumentsAndReturnSizes(this.descriptor) >> 2;
      if ((paramInt & 0x8) != 0)
        i--; 
      this.maxLocals = i;
      this.labels = new Label();
      this.labels.status |= 0x8;
      visitLabel(this.labels);
    } 
  }
  
  public AnnotationVisitor visitAnnotationDefault() {
    this.annd = new ByteVector();
    return new AnnotationWriter(this.cw, false, this.annd, null, 0);
  }
  
  public AnnotationVisitor visitAnnotation(String paramString, boolean paramBoolean) {
    ByteVector byteVector = new ByteVector();
    byteVector.putShort(this.cw.newUTF8(paramString)).putShort(0);
    AnnotationWriter annotationWriter = new AnnotationWriter(this.cw, true, byteVector, byteVector, 2);
    if (paramBoolean) {
      annotationWriter.next = this.anns;
      this.anns = annotationWriter;
    } else {
      annotationWriter.next = this.ianns;
      this.ianns = annotationWriter;
    } 
    return annotationWriter;
  }
  
  public AnnotationVisitor visitParameterAnnotation(int paramInt, String paramString, boolean paramBoolean) {
    ByteVector byteVector = new ByteVector();
    if ("Ljava/lang/Synthetic;".equals(paramString)) {
      this.synthetics = Math.max(this.synthetics, paramInt + 1);
      return new AnnotationWriter(this.cw, false, byteVector, null, 0);
    } 
    byteVector.putShort(this.cw.newUTF8(paramString)).putShort(0);
    AnnotationWriter annotationWriter = new AnnotationWriter(this.cw, true, byteVector, byteVector, 2);
    if (paramBoolean) {
      if (this.panns == null)
        this.panns = new AnnotationWriter[Type.getArgumentTypes(this.descriptor).length]; 
      annotationWriter.next = this.panns[paramInt];
      this.panns[paramInt] = annotationWriter;
    } else {
      if (this.ipanns == null)
        this.ipanns = new AnnotationWriter[Type.getArgumentTypes(this.descriptor).length]; 
      annotationWriter.next = this.ipanns[paramInt];
      this.ipanns[paramInt] = annotationWriter;
    } 
    return annotationWriter;
  }
  
  public void visitAttribute(Attribute paramAttribute) {
    if (paramAttribute.isCodeAttribute()) {
      paramAttribute.next = this.cattrs;
      this.cattrs = paramAttribute;
    } else {
      paramAttribute.next = this.attrs;
      this.attrs = paramAttribute;
    } 
  }
  
  public void visitCode() {}
  
  public void visitFrame(int paramInt1, int paramInt2, Object[] paramArrayOfObject1, int paramInt3, Object[] paramArrayOfObject2) {
    if (this.compute == 0)
      return; 
    if (paramInt1 == -1) {
      startFrame(this.code.length, paramInt2, paramInt3);
      byte b;
      for (b = 0; b < paramInt2; b++) {
        if (paramArrayOfObject1[b] instanceof String) {
          this.frame[this.frameIndex++] = 0x1700000 | this.cw.addType((String)paramArrayOfObject1[b]);
        } else if (paramArrayOfObject1[b] instanceof Integer) {
          this.frame[this.frameIndex++] = ((Integer)paramArrayOfObject1[b]).intValue();
        } else {
          this.frame[this.frameIndex++] = 0x1800000 | this.cw.addUninitializedType("", ((Label)paramArrayOfObject1[b]).position);
        } 
      } 
      for (b = 0; b < paramInt3; b++) {
        if (paramArrayOfObject2[b] instanceof String) {
          this.frame[this.frameIndex++] = 0x1700000 | this.cw.addType((String)paramArrayOfObject2[b]);
        } else if (paramArrayOfObject2[b] instanceof Integer) {
          this.frame[this.frameIndex++] = ((Integer)paramArrayOfObject2[b]).intValue();
        } else {
          this.frame[this.frameIndex++] = 0x1800000 | this.cw.addUninitializedType("", ((Label)paramArrayOfObject2[b]).position);
        } 
      } 
      endFrame();
    } else {
      byte b;
      int i;
      if (this.stackMap == null) {
        this.stackMap = new ByteVector();
        i = this.code.length;
      } else {
        i = this.code.length - this.previousFrameOffset - 1;
      } 
      switch (paramInt1) {
        case 0:
          this.stackMap.putByte(255).putShort(i).putShort(paramInt2);
          for (b = 0; b < paramInt2; b++)
            writeFrameType(paramArrayOfObject1[b]); 
          this.stackMap.putShort(paramInt3);
          for (b = 0; b < paramInt3; b++)
            writeFrameType(paramArrayOfObject2[b]); 
          break;
        case 1:
          this.stackMap.putByte(251 + paramInt2).putShort(i);
          for (b = 0; b < paramInt2; b++)
            writeFrameType(paramArrayOfObject1[b]); 
          break;
        case 2:
          this.stackMap.putByte(251 - paramInt2).putShort(i);
          break;
        case 3:
          if (i < 64) {
            this.stackMap.putByte(i);
            break;
          } 
          this.stackMap.putByte(251).putShort(i);
          break;
        case 4:
          if (i < 64) {
            this.stackMap.putByte(64 + i);
          } else {
            this.stackMap.putByte(247).putShort(i);
          } 
          writeFrameType(paramArrayOfObject2[0]);
          break;
      } 
      this.previousFrameOffset = this.code.length;
      this.frameCount++;
    } 
  }
  
  public void visitInsn(int paramInt) {
    this.code.putByte(paramInt);
    if (this.currentBlock != null) {
      if (this.compute == 0) {
        this.currentBlock.frame.execute(paramInt, 0, null, null);
      } else {
        int i = this.stackSize + Frame.SIZE[paramInt];
        if (i > this.maxStackSize)
          this.maxStackSize = i; 
        this.stackSize = i;
      } 
      if ((paramInt >= 172 && paramInt <= 177) || paramInt == 191)
        noSuccessor(); 
    } 
  }
  
  public void visitIntInsn(int paramInt1, int paramInt2) {
    if (this.currentBlock != null)
      if (this.compute == 0) {
        this.currentBlock.frame.execute(paramInt1, paramInt2, null, null);
      } else if (paramInt1 != 188) {
        int i = this.stackSize + 1;
        if (i > this.maxStackSize)
          this.maxStackSize = i; 
        this.stackSize = i;
      }  
    if (paramInt1 == 17) {
      this.code.put12(paramInt1, paramInt2);
    } else {
      this.code.put11(paramInt1, paramInt2);
    } 
  }
  
  public void visitVarInsn(int paramInt1, int paramInt2) {
    if (this.currentBlock != null)
      if (this.compute == 0) {
        this.currentBlock.frame.execute(paramInt1, paramInt2, null, null);
      } else if (paramInt1 == 169) {
        this.currentBlock.status |= 0x100;
        this.currentBlock.inputStackTop = this.stackSize;
        noSuccessor();
      } else {
        int i = this.stackSize + Frame.SIZE[paramInt1];
        if (i > this.maxStackSize)
          this.maxStackSize = i; 
        this.stackSize = i;
      }  
    if (this.compute != 2) {
      int i;
      if (paramInt1 == 22 || paramInt1 == 24 || paramInt1 == 55 || paramInt1 == 57) {
        i = paramInt2 + 2;
      } else {
        i = paramInt2 + 1;
      } 
      if (i > this.maxLocals)
        this.maxLocals = i; 
    } 
    if (paramInt2 < 4 && paramInt1 != 169) {
      int i;
      if (paramInt1 < 54) {
        i = 26 + (paramInt1 - 21 << 2) + paramInt2;
      } else {
        i = 59 + (paramInt1 - 54 << 2) + paramInt2;
      } 
      this.code.putByte(i);
    } else if (paramInt2 >= 256) {
      this.code.putByte(196).put12(paramInt1, paramInt2);
    } else {
      this.code.put11(paramInt1, paramInt2);
    } 
    if (paramInt1 >= 54 && this.compute == 0 && this.handlerCount > 0)
      visitLabel(new Label()); 
  }
  
  public void visitTypeInsn(int paramInt, String paramString) {
    Item item = this.cw.newClassItem(paramString);
    if (this.currentBlock != null)
      if (this.compute == 0) {
        this.currentBlock.frame.execute(paramInt, this.code.length, this.cw, item);
      } else if (paramInt == 187) {
        int i = this.stackSize + 1;
        if (i > this.maxStackSize)
          this.maxStackSize = i; 
        this.stackSize = i;
      }  
    this.code.put12(paramInt, item.index);
  }
  
  public void visitFieldInsn(int paramInt, String paramString1, String paramString2, String paramString3) {
    Item item = this.cw.newFieldItem(paramString1, paramString2, paramString3);
    if (this.currentBlock != null)
      if (this.compute == 0) {
        this.currentBlock.frame.execute(paramInt, 0, this.cw, item);
      } else {
        int i;
        char c = paramString3.charAt(0);
        switch (paramInt) {
          case 178:
            i = this.stackSize + ((c == 'D' || c == 'J') ? 2 : 1);
            break;
          case 179:
            i = this.stackSize + ((c == 'D' || c == 'J') ? -2 : -1);
            break;
          case 180:
            i = this.stackSize + ((c == 'D' || c == 'J') ? 1 : 0);
            break;
          default:
            i = this.stackSize + ((c == 'D' || c == 'J') ? -3 : -2);
            break;
        } 
        if (i > this.maxStackSize)
          this.maxStackSize = i; 
        this.stackSize = i;
      }  
    this.code.put12(paramInt, item.index);
  }
  
  public void visitMethodInsn(int paramInt, String paramString1, String paramString2, String paramString3) {
    boolean bool = (paramInt == 185);
    Item item = this.cw.newMethodItem(paramString1, paramString2, paramString3, bool);
    int i = item.intVal;
    if (this.currentBlock != null)
      if (this.compute == 0) {
        this.currentBlock.frame.execute(paramInt, 0, this.cw, item);
      } else {
        int j;
        if (i == 0) {
          i = getArgumentsAndReturnSizes(paramString3);
          item.intVal = i;
        } 
        if (paramInt == 184) {
          j = this.stackSize - (i >> 2) + (i & 0x3) + 1;
        } else {
          j = this.stackSize - (i >> 2) + (i & 0x3);
        } 
        if (j > this.maxStackSize)
          this.maxStackSize = j; 
        this.stackSize = j;
      }  
    if (bool) {
      if (i == 0) {
        i = getArgumentsAndReturnSizes(paramString3);
        item.intVal = i;
      } 
      this.code.put12(185, item.index).put11(i >> 2, 0);
    } else {
      this.code.put12(paramInt, item.index);
    } 
  }
  
  public void visitJumpInsn(int paramInt, Label paramLabel) {
    Label label = null;
    if (this.currentBlock != null)
      if (this.compute == 0) {
        this.currentBlock.frame.execute(paramInt, 0, null, null);
        (paramLabel.getFirst()).status |= 0x10;
        addSuccessor(0, paramLabel);
        if (paramInt != 167)
          label = new Label(); 
      } else if (paramInt == 168) {
        if ((paramLabel.status & 0x200) == 0) {
          paramLabel.status |= 0x200;
          this.subroutines++;
        } 
        this.currentBlock.status |= 0x80;
        addSuccessor(this.stackSize + 1, paramLabel);
        label = new Label();
      } else {
        this.stackSize += Frame.SIZE[paramInt];
        addSuccessor(this.stackSize, paramLabel);
      }  
    if ((paramLabel.status & 0x2) != 0 && paramLabel.position - this.code.length < -32768) {
      if (paramInt == 167) {
        this.code.putByte(200);
      } else if (paramInt == 168) {
        this.code.putByte(201);
      } else {
        if (label != null)
          label.status |= 0x10; 
        this.code.putByte((paramInt <= 166) ? ((paramInt + 1 ^ true) - 1) : (paramInt ^ true));
        this.code.putShort(8);
        this.code.putByte(200);
      } 
      paramLabel.put(this, this.code, this.code.length - 1, true);
    } else {
      this.code.putByte(paramInt);
      paramLabel.put(this, this.code, this.code.length - 1, false);
    } 
    if (this.currentBlock != null) {
      if (label != null)
        visitLabel(label); 
      if (paramInt == 167)
        noSuccessor(); 
    } 
  }
  
  public void visitLabel(Label paramLabel) {
    this.resize |= paramLabel.resolve(this, this.code.length, this.code.data);
    if ((paramLabel.status & true) != 0)
      return; 
    if (this.compute == 0) {
      if (this.currentBlock != null) {
        if (paramLabel.position == this.currentBlock.position) {
          this.currentBlock.status |= paramLabel.status & 0x10;
          paramLabel.frame = this.currentBlock.frame;
          return;
        } 
        addSuccessor(0, paramLabel);
      } 
      this.currentBlock = paramLabel;
      if (paramLabel.frame == null) {
        paramLabel.frame = new Frame();
        paramLabel.frame.owner = paramLabel;
      } 
      if (this.previousBlock != null) {
        if (paramLabel.position == this.previousBlock.position) {
          this.previousBlock.status |= paramLabel.status & 0x10;
          paramLabel.frame = this.previousBlock.frame;
          this.currentBlock = this.previousBlock;
          return;
        } 
        this.previousBlock.successor = paramLabel;
      } 
      this.previousBlock = paramLabel;
    } else if (this.compute == 1) {
      if (this.currentBlock != null) {
        this.currentBlock.outputStackMax = this.maxStackSize;
        addSuccessor(this.stackSize, paramLabel);
      } 
      this.currentBlock = paramLabel;
      this.stackSize = 0;
      this.maxStackSize = 0;
      if (this.previousBlock != null)
        this.previousBlock.successor = paramLabel; 
      this.previousBlock = paramLabel;
    } 
  }
  
  public void visitLdcInsn(Object paramObject) {
    Item item = this.cw.newConstItem(paramObject);
    if (this.currentBlock != null)
      if (this.compute == 0) {
        this.currentBlock.frame.execute(18, 0, this.cw, item);
      } else {
        int j;
        if (item.type == 5 || item.type == 6) {
          j = this.stackSize + 2;
        } else {
          j = this.stackSize + 1;
        } 
        if (j > this.maxStackSize)
          this.maxStackSize = j; 
        this.stackSize = j;
      }  
    int i = item.index;
    if (item.type == 5 || item.type == 6) {
      this.code.put12(20, i);
    } else if (i >= 256) {
      this.code.put12(19, i);
    } else {
      this.code.put11(18, i);
    } 
  }
  
  public void visitIincInsn(int paramInt1, int paramInt2) {
    if (this.currentBlock != null && this.compute == 0)
      this.currentBlock.frame.execute(132, paramInt1, null, null); 
    if (this.compute != 2) {
      int i = paramInt1 + 1;
      if (i > this.maxLocals)
        this.maxLocals = i; 
    } 
    if (paramInt1 > 255 || paramInt2 > 127 || paramInt2 < -128) {
      this.code.putByte(196).put12(132, paramInt1).putShort(paramInt2);
    } else {
      this.code.putByte(132).put11(paramInt1, paramInt2);
    } 
  }
  
  public void visitTableSwitchInsn(int paramInt1, int paramInt2, Label paramLabel, Label[] paramArrayOfLabel) {
    int i = this.code.length;
    this.code.putByte(170);
    this.code.length += (4 - this.code.length % 4) % 4;
    paramLabel.put(this, this.code, i, true);
    this.code.putInt(paramInt1).putInt(paramInt2);
    for (byte b = 0; b < paramArrayOfLabel.length; b++)
      paramArrayOfLabel[b].put(this, this.code, i, true); 
    visitSwitchInsn(paramLabel, paramArrayOfLabel);
  }
  
  public void visitLookupSwitchInsn(Label paramLabel, int[] paramArrayOfInt, Label[] paramArrayOfLabel) {
    int i = this.code.length;
    this.code.putByte(171);
    this.code.length += (4 - this.code.length % 4) % 4;
    paramLabel.put(this, this.code, i, true);
    this.code.putInt(paramArrayOfLabel.length);
    for (byte b = 0; b < paramArrayOfLabel.length; b++) {
      this.code.putInt(paramArrayOfInt[b]);
      paramArrayOfLabel[b].put(this, this.code, i, true);
    } 
    visitSwitchInsn(paramLabel, paramArrayOfLabel);
  }
  
  private void visitSwitchInsn(Label paramLabel, Label[] paramArrayOfLabel) {
    if (this.currentBlock != null) {
      if (this.compute == 0) {
        this.currentBlock.frame.execute(171, 0, null, null);
        addSuccessor(0, paramLabel);
        (paramLabel.getFirst()).status |= 0x10;
        for (byte b = 0; b < paramArrayOfLabel.length; b++) {
          addSuccessor(0, paramArrayOfLabel[b]);
          (paramArrayOfLabel[b].getFirst()).status |= 0x10;
        } 
      } else {
        this.stackSize--;
        addSuccessor(this.stackSize, paramLabel);
        for (byte b = 0; b < paramArrayOfLabel.length; b++)
          addSuccessor(this.stackSize, paramArrayOfLabel[b]); 
      } 
      noSuccessor();
    } 
  }
  
  public void visitMultiANewArrayInsn(String paramString, int paramInt) {
    Item item = this.cw.newClassItem(paramString);
    if (this.currentBlock != null)
      if (this.compute == 0) {
        this.currentBlock.frame.execute(197, paramInt, this.cw, item);
      } else {
        this.stackSize += 1 - paramInt;
      }  
    this.code.put12(197, item.index).putByte(paramInt);
  }
  
  public void visitTryCatchBlock(Label paramLabel1, Label paramLabel2, Label paramLabel3, String paramString) {
    this.handlerCount++;
    Handler handler = new Handler();
    handler.start = paramLabel1;
    handler.end = paramLabel2;
    handler.handler = paramLabel3;
    handler.desc = paramString;
    handler.type = (paramString != null) ? this.cw.newClass(paramString) : 0;
    if (this.lastHandler == null) {
      this.firstHandler = handler;
    } else {
      this.lastHandler.next = handler;
    } 
    this.lastHandler = handler;
  }
  
  public void visitLocalVariable(String paramString1, String paramString2, String paramString3, Label paramLabel1, Label paramLabel2, int paramInt) {
    if (paramString3 != null) {
      if (this.localVarType == null)
        this.localVarType = new ByteVector(); 
      this.localVarTypeCount++;
      this.localVarType.putShort(paramLabel1.position).putShort(paramLabel2.position - paramLabel1.position).putShort(this.cw.newUTF8(paramString1)).putShort(this.cw.newUTF8(paramString3)).putShort(paramInt);
    } 
    if (this.localVar == null)
      this.localVar = new ByteVector(); 
    this.localVarCount++;
    this.localVar.putShort(paramLabel1.position).putShort(paramLabel2.position - paramLabel1.position).putShort(this.cw.newUTF8(paramString1)).putShort(this.cw.newUTF8(paramString2)).putShort(paramInt);
    if (this.compute != 2) {
      char c = paramString2.charAt(0);
      int i = paramInt + ((c == 'J' || c == 'D') ? 2 : 1);
      if (i > this.maxLocals)
        this.maxLocals = i; 
    } 
  }
  
  public void visitLineNumber(int paramInt, Label paramLabel) {
    if (this.lineNumber == null)
      this.lineNumber = new ByteVector(); 
    this.lineNumberCount++;
    this.lineNumber.putShort(paramLabel.position);
    this.lineNumber.putShort(paramInt);
  }
  
  public void visitMaxs(int paramInt1, int paramInt2) {
    if (this.compute == 0) {
      for (Handler handler = this.firstHandler; handler != null; handler = handler.next) {
        Label label3 = handler.start.getFirst();
        Label label4 = handler.handler.getFirst();
        Label label5 = handler.end.getFirst();
        String str = (handler.desc == null) ? "java/lang/Throwable" : handler.desc;
        int j = 0x1700000 | this.cw.addType(str);
        label4.status |= 0x10;
        while (label3 != label5) {
          Edge edge = new Edge();
          edge.info = j;
          edge.successor = label4;
          edge.next = label3.successors;
          label3.successors = edge;
          label3 = label3.successor;
        } 
      } 
      Frame frame1 = this.labels.frame;
      Type[] arrayOfType = Type.getArgumentTypes(this.descriptor);
      frame1.initInputFrame(this.cw, this.access, arrayOfType, this.maxLocals);
      visitFrame(frame1);
      int i = 0;
      Label label1 = this.labels;
      while (label1 != null) {
        Label label = label1;
        label1 = label1.next;
        label.next = null;
        frame1 = label.frame;
        if ((label.status & 0x10) != 0)
          label.status |= 0x20; 
        label.status |= 0x40;
        int j = frame1.inputStack.length + label.outputStackMax;
        if (j > i)
          i = j; 
        for (Edge edge = label.successors; edge != null; edge = edge.next) {
          Label label3 = edge.successor.getFirst();
          boolean bool = frame1.merge(this.cw, label3.frame, edge.info);
          if (bool && label3.next == null) {
            label3.next = label1;
            label1 = label3;
          } 
        } 
      } 
      this.maxStack = i;
      for (Label label2 = this.labels; label2 != null; label2 = label2.successor) {
        frame1 = label2.frame;
        if ((label2.status & 0x20) != 0)
          visitFrame(frame1); 
        if ((label2.status & 0x40) == 0) {
          Label label = label2.successor;
          int j = label2.position;
          int k = ((label == null) ? this.code.length : label.position) - 1;
          if (k >= j) {
            for (int m = j; m < k; m++)
              this.code.data[m] = 0; 
            this.code.data[k] = -65;
            startFrame(j, 0, 1);
            this.frame[this.frameIndex++] = 0x1700000 | this.cw.addType("java/lang/Throwable");
            endFrame();
          } 
        } 
      } 
    } else if (this.compute == 1) {
      for (Handler handler = this.firstHandler; handler != null; handler = handler.next) {
        Label label1 = handler.start;
        Label label2 = handler.handler;
        Label label3 = handler.end;
        while (label1 != label3) {
          Edge edge = new Edge();
          edge.info = Integer.MAX_VALUE;
          edge.successor = label2;
          if ((label1.status & 0x80) == 0) {
            edge.next = label1.successors;
            label1.successors = edge;
          } else {
            edge.next = label1.successors.next.next;
            label1.successors.next.next = edge;
          } 
          label1 = label1.successor;
        } 
      } 
      if (this.subroutines > 0) {
        byte b = 0;
        this.labels.visitSubroutine(null, 1L, this.subroutines);
        Label label1;
        for (label1 = this.labels; label1 != null; label1 = label1.successor) {
          if ((label1.status & 0x80) != 0) {
            Label label2 = label1.successors.next.successor;
            if ((label2.status & 0x400) == 0)
              label2.visitSubroutine(null, ++b / 32L << 32 | 1L << b % 32, this.subroutines); 
          } 
        } 
        for (label1 = this.labels; label1 != null; label1 = label1.successor) {
          if ((label1.status & 0x80) != 0) {
            for (Label label2 = this.labels; label2 != null; label2 = label2.successor)
              label2.status &= 0xFFFFFBFF; 
            Label label3 = label1.successors.next.successor;
            label3.visitSubroutine(label1, 0L, this.subroutines);
          } 
        } 
      } 
      int i = 0;
      Label label = this.labels;
      while (label != null) {
        Label label1 = label;
        label = label.next;
        int j = label1.inputStackTop;
        int k = j + label1.outputStackMax;
        if (k > i)
          i = k; 
        Edge edge = label1.successors;
        if ((label1.status & 0x80) != 0)
          edge = edge.next; 
        while (edge != null) {
          label1 = edge.successor;
          if ((label1.status & 0x8) == 0) {
            label1.inputStackTop = (edge.info == Integer.MAX_VALUE) ? 1 : (j + edge.info);
            label1.status |= 0x8;
            label1.next = label;
            label = label1;
          } 
          edge = edge.next;
        } 
      } 
      this.maxStack = i;
    } else {
      this.maxStack = paramInt1;
      this.maxLocals = paramInt2;
    } 
  }
  
  public void visitEnd() {}
  
  static int getArgumentsAndReturnSizes(String paramString) {
    byte b1 = 1;
    byte b2 = 1;
    while (true) {
      char c = paramString.charAt(b2++);
      if (c == ')') {
        c = paramString.charAt(b2);
        return b1 << 2 | ((c == 'V') ? 0 : ((c == 'D' || c == 'J') ? 2 : 1));
      } 
      if (c == 'L') {
        while (paramString.charAt(b2++) != ';');
        b1++;
        continue;
      } 
      if (c == '[') {
        while ((c = paramString.charAt(b2)) == '[')
          b2++; 
        if (c == 'D' || c == 'J')
          b1--; 
        continue;
      } 
      if (c == 'D' || c == 'J') {
        b1 += 2;
        continue;
      } 
      b1++;
    } 
  }
  
  private void addSuccessor(int paramInt, Label paramLabel) {
    Edge edge = new Edge();
    edge.info = paramInt;
    edge.successor = paramLabel;
    edge.next = this.currentBlock.successors;
    this.currentBlock.successors = edge;
  }
  
  private void noSuccessor() {
    if (this.compute == 0) {
      Label label = new Label();
      label.frame = new Frame();
      label.frame.owner = label;
      label.resolve(this, this.code.length, this.code.data);
      this.previousBlock.successor = label;
      this.previousBlock = label;
    } else {
      this.currentBlock.outputStackMax = this.maxStackSize;
    } 
    this.currentBlock = null;
  }
  
  private void visitFrame(Frame paramFrame) {
    byte b2 = 0;
    byte b3 = 0;
    byte b4 = 0;
    int[] arrayOfInt1 = paramFrame.inputLocals;
    int[] arrayOfInt2 = paramFrame.inputStack;
    byte b1;
    for (b1 = 0; b1 < arrayOfInt1.length; b1++) {
      int i = arrayOfInt1[b1];
      if (i == 16777216) {
        b2++;
      } else {
        b3 += b2 + 1;
        b2 = 0;
      } 
      if (i == 16777220 || i == 16777219)
        b1++; 
    } 
    for (b1 = 0; b1 < arrayOfInt2.length; b1++) {
      int i = arrayOfInt2[b1];
      b4++;
      if (i == 16777220 || i == 16777219)
        b1++; 
    } 
    startFrame(paramFrame.owner.position, b3, b4);
    b1 = 0;
    while (b3 > 0) {
      int i = arrayOfInt1[b1];
      this.frame[this.frameIndex++] = i;
      if (i == 16777220 || i == 16777219)
        b1++; 
      b1++;
      b3--;
    } 
    for (b1 = 0; b1 < arrayOfInt2.length; b1++) {
      int i = arrayOfInt2[b1];
      this.frame[this.frameIndex++] = i;
      if (i == 16777220 || i == 16777219)
        b1++; 
    } 
    endFrame();
  }
  
  private void startFrame(int paramInt1, int paramInt2, int paramInt3) {
    int i = 3 + paramInt2 + paramInt3;
    if (this.frame == null || this.frame.length < i)
      this.frame = new int[i]; 
    this.frame[0] = paramInt1;
    this.frame[1] = paramInt2;
    this.frame[2] = paramInt3;
    this.frameIndex = 3;
  }
  
  private void endFrame() {
    if (this.previousFrame != null) {
      if (this.stackMap == null)
        this.stackMap = new ByteVector(); 
      writeFrame();
      this.frameCount++;
    } 
    this.previousFrame = this.frame;
    this.frame = null;
  }
  
  private void writeFrame() {
    int n;
    int i = this.frame[1];
    int j = this.frame[2];
    if ((this.cw.version & 0xFFFF) < 50) {
      this.stackMap.putShort(this.frame[0]).putShort(i);
      writeFrameTypes(3, 3 + i);
      this.stackMap.putShort(j);
      writeFrameTypes(3 + i, 3 + i + j);
      return;
    } 
    int k = this.previousFrame[1];
    char c = 'ÿ';
    int m = 0;
    if (this.frameCount == 0) {
      n = this.frame[0];
    } else {
      n = this.frame[0] - this.previousFrame[0] - 1;
    } 
    if (j == 0) {
      m = i - k;
      switch (m) {
        case -3:
        case -2:
        case -1:
          c = 'ø';
          k = i;
          break;
        case 0:
          c = (n < 64) ? Character.MIN_VALUE : 'û';
          break;
        case 1:
        case 2:
        case 3:
          c = 'ü';
          break;
      } 
    } else if (i == k && j == 1) {
      c = (n < 63) ? '@' : '÷';
    } 
    if (c != 'ÿ') {
      byte b1 = 3;
      for (byte b2 = 0; b2 < k; b2++) {
        if (this.frame[b1] != this.previousFrame[b1]) {
          c = 'ÿ';
          break;
        } 
        b1++;
      } 
    } 
    switch (c) {
      case '\000':
        this.stackMap.putByte(n);
        return;
      case '@':
        this.stackMap.putByte(64 + n);
        writeFrameTypes(3 + i, 4 + i);
        return;
      case '÷':
        this.stackMap.putByte(247).putShort(n);
        writeFrameTypes(3 + i, 4 + i);
        return;
      case 'û':
        this.stackMap.putByte(251).putShort(n);
        return;
      case 'ø':
        this.stackMap.putByte(251 + m).putShort(n);
        return;
      case 'ü':
        this.stackMap.putByte(251 + m).putShort(n);
        writeFrameTypes(3 + k, 3 + i);
        return;
    } 
    this.stackMap.putByte(255).putShort(n).putShort(i);
    writeFrameTypes(3, 3 + i);
    this.stackMap.putShort(j);
    writeFrameTypes(3 + i, 3 + i + j);
  }
  
  private void writeFrameTypes(int paramInt1, int paramInt2) {
    for (int i = paramInt1; i < paramInt2; i++) {
      int j = this.frame[i];
      int k = j & 0xF0000000;
      if (k == 0) {
        int m = j & 0xFFFFF;
        switch (j & 0xFF00000) {
          case 24117248:
            this.stackMap.putByte(7).putShort(this.cw.newClass((this.cw.typeTable[m]).strVal1));
            break;
          case 25165824:
            this.stackMap.putByte(8).putShort((this.cw.typeTable[m]).intVal);
            break;
          default:
            this.stackMap.putByte(m);
            break;
        } 
      } else {
        StringBuffer stringBuffer = new StringBuffer();
        k >>= 28;
        while (k-- > 0)
          stringBuffer.append('['); 
        if ((j & 0xFF00000) == 24117248) {
          stringBuffer.append('L');
          stringBuffer.append((this.cw.typeTable[j & 0xFFFFF]).strVal1);
          stringBuffer.append(';');
        } else {
          switch (j & 0xF) {
            case 1:
              stringBuffer.append('I');
              break;
            case 2:
              stringBuffer.append('F');
              break;
            case 3:
              stringBuffer.append('D');
              break;
            case 9:
              stringBuffer.append('Z');
              break;
            case 10:
              stringBuffer.append('B');
              break;
            case 11:
              stringBuffer.append('C');
              break;
            case 12:
              stringBuffer.append('S');
              break;
            default:
              stringBuffer.append('J');
              break;
          } 
        } 
        this.stackMap.putByte(7).putShort(this.cw.newClass(stringBuffer.toString()));
      } 
    } 
  }
  
  private void writeFrameType(Object paramObject) {
    if (paramObject instanceof String) {
      this.stackMap.putByte(7).putShort(this.cw.newClass((String)paramObject));
    } else if (paramObject instanceof Integer) {
      this.stackMap.putByte(((Integer)paramObject).intValue());
    } else {
      this.stackMap.putByte(8).putShort(((Label)paramObject).position);
    } 
  }
  
  final int getSize() {
    if (this.classReaderOffset != 0)
      return 6 + this.classReaderLength; 
    if (this.resize)
      resizeInstructions(); 
    int i = 8;
    if (this.code.length > 0) {
      this.cw.newUTF8("Code");
      i += 18 + this.code.length + 8 * this.handlerCount;
      if (this.localVar != null) {
        this.cw.newUTF8("LocalVariableTable");
        i += 8 + this.localVar.length;
      } 
      if (this.localVarType != null) {
        this.cw.newUTF8("LocalVariableTypeTable");
        i += 8 + this.localVarType.length;
      } 
      if (this.lineNumber != null) {
        this.cw.newUTF8("LineNumberTable");
        i += 8 + this.lineNumber.length;
      } 
      if (this.stackMap != null) {
        boolean bool = ((this.cw.version & 0xFFFF) >= 50) ? 1 : 0;
        this.cw.newUTF8(bool ? "StackMapTable" : "StackMap");
        i += 8 + this.stackMap.length;
      } 
      if (this.cattrs != null)
        i += this.cattrs.getSize(this.cw, this.code.data, this.code.length, this.maxStack, this.maxLocals); 
    } 
    if (this.exceptionCount > 0) {
      this.cw.newUTF8("Exceptions");
      i += 8 + 2 * this.exceptionCount;
    } 
    if ((this.access & 0x1000) != 0 && (this.cw.version & 0xFFFF) < 49) {
      this.cw.newUTF8("Synthetic");
      i += 6;
    } 
    if ((this.access & 0x20000) != 0) {
      this.cw.newUTF8("Deprecated");
      i += 6;
    } 
    if (this.signature != null) {
      this.cw.newUTF8("Signature");
      this.cw.newUTF8(this.signature);
      i += 8;
    } 
    if (this.annd != null) {
      this.cw.newUTF8("AnnotationDefault");
      i += 6 + this.annd.length;
    } 
    if (this.anns != null) {
      this.cw.newUTF8("RuntimeVisibleAnnotations");
      i += 8 + this.anns.getSize();
    } 
    if (this.ianns != null) {
      this.cw.newUTF8("RuntimeInvisibleAnnotations");
      i += 8 + this.ianns.getSize();
    } 
    if (this.panns != null) {
      this.cw.newUTF8("RuntimeVisibleParameterAnnotations");
      i += 7 + 2 * (this.panns.length - this.synthetics);
      for (int j = this.panns.length - 1; j >= this.synthetics; j--)
        i += ((this.panns[j] == null) ? 0 : this.panns[j].getSize()); 
    } 
    if (this.ipanns != null) {
      this.cw.newUTF8("RuntimeInvisibleParameterAnnotations");
      i += 7 + 2 * (this.ipanns.length - this.synthetics);
      for (int j = this.ipanns.length - 1; j >= this.synthetics; j--)
        i += ((this.ipanns[j] == null) ? 0 : this.ipanns[j].getSize()); 
    } 
    if (this.attrs != null)
      i += this.attrs.getSize(this.cw, null, 0, -1, -1); 
    return i;
  }
  
  final void put(ByteVector paramByteVector) {
    paramByteVector.putShort(this.access).putShort(this.name).putShort(this.desc);
    if (this.classReaderOffset != 0) {
      paramByteVector.putByteArray(this.cw.cr.b, this.classReaderOffset, this.classReaderLength);
      return;
    } 
    int i = 0;
    if (this.code.length > 0)
      i++; 
    if (this.exceptionCount > 0)
      i++; 
    if ((this.access & 0x1000) != 0 && (this.cw.version & 0xFFFF) < 49)
      i++; 
    if ((this.access & 0x20000) != 0)
      i++; 
    if (this.signature != null)
      i++; 
    if (this.annd != null)
      i++; 
    if (this.anns != null)
      i++; 
    if (this.ianns != null)
      i++; 
    if (this.panns != null)
      i++; 
    if (this.ipanns != null)
      i++; 
    if (this.attrs != null)
      i += this.attrs.getCount(); 
    paramByteVector.putShort(i);
    if (this.code.length > 0) {
      int j = 12 + this.code.length + 8 * this.handlerCount;
      if (this.localVar != null)
        j += 8 + this.localVar.length; 
      if (this.localVarType != null)
        j += 8 + this.localVarType.length; 
      if (this.lineNumber != null)
        j += 8 + this.lineNumber.length; 
      if (this.stackMap != null)
        j += 8 + this.stackMap.length; 
      if (this.cattrs != null)
        j += this.cattrs.getSize(this.cw, this.code.data, this.code.length, this.maxStack, this.maxLocals); 
      paramByteVector.putShort(this.cw.newUTF8("Code")).putInt(j);
      paramByteVector.putShort(this.maxStack).putShort(this.maxLocals);
      paramByteVector.putInt(this.code.length).putByteArray(this.code.data, 0, this.code.length);
      paramByteVector.putShort(this.handlerCount);
      if (this.handlerCount > 0)
        for (Handler handler = this.firstHandler; handler != null; handler = handler.next)
          paramByteVector.putShort(handler.start.position).putShort(handler.end.position).putShort(handler.handler.position).putShort(handler.type);  
      i = 0;
      if (this.localVar != null)
        i++; 
      if (this.localVarType != null)
        i++; 
      if (this.lineNumber != null)
        i++; 
      if (this.stackMap != null)
        i++; 
      if (this.cattrs != null)
        i += this.cattrs.getCount(); 
      paramByteVector.putShort(i);
      if (this.localVar != null) {
        paramByteVector.putShort(this.cw.newUTF8("LocalVariableTable"));
        paramByteVector.putInt(this.localVar.length + 2).putShort(this.localVarCount);
        paramByteVector.putByteArray(this.localVar.data, 0, this.localVar.length);
      } 
      if (this.localVarType != null) {
        paramByteVector.putShort(this.cw.newUTF8("LocalVariableTypeTable"));
        paramByteVector.putInt(this.localVarType.length + 2).putShort(this.localVarTypeCount);
        paramByteVector.putByteArray(this.localVarType.data, 0, this.localVarType.length);
      } 
      if (this.lineNumber != null) {
        paramByteVector.putShort(this.cw.newUTF8("LineNumberTable"));
        paramByteVector.putInt(this.lineNumber.length + 2).putShort(this.lineNumberCount);
        paramByteVector.putByteArray(this.lineNumber.data, 0, this.lineNumber.length);
      } 
      if (this.stackMap != null) {
        boolean bool = ((this.cw.version & 0xFFFF) >= 50) ? 1 : 0;
        paramByteVector.putShort(this.cw.newUTF8(bool ? "StackMapTable" : "StackMap"));
        paramByteVector.putInt(this.stackMap.length + 2).putShort(this.frameCount);
        paramByteVector.putByteArray(this.stackMap.data, 0, this.stackMap.length);
      } 
      if (this.cattrs != null)
        this.cattrs.put(this.cw, this.code.data, this.code.length, this.maxLocals, this.maxStack, paramByteVector); 
    } 
    if (this.exceptionCount > 0) {
      paramByteVector.putShort(this.cw.newUTF8("Exceptions")).putInt(2 * this.exceptionCount + 2);
      paramByteVector.putShort(this.exceptionCount);
      for (byte b = 0; b < this.exceptionCount; b++)
        paramByteVector.putShort(this.exceptions[b]); 
    } 
    if ((this.access & 0x1000) != 0 && (this.cw.version & 0xFFFF) < 49)
      paramByteVector.putShort(this.cw.newUTF8("Synthetic")).putInt(0); 
    if ((this.access & 0x20000) != 0)
      paramByteVector.putShort(this.cw.newUTF8("Deprecated")).putInt(0); 
    if (this.signature != null)
      paramByteVector.putShort(this.cw.newUTF8("Signature")).putInt(2).putShort(this.cw.newUTF8(this.signature)); 
    if (this.annd != null) {
      paramByteVector.putShort(this.cw.newUTF8("AnnotationDefault"));
      paramByteVector.putInt(this.annd.length);
      paramByteVector.putByteArray(this.annd.data, 0, this.annd.length);
    } 
    if (this.anns != null) {
      paramByteVector.putShort(this.cw.newUTF8("RuntimeVisibleAnnotations"));
      this.anns.put(paramByteVector);
    } 
    if (this.ianns != null) {
      paramByteVector.putShort(this.cw.newUTF8("RuntimeInvisibleAnnotations"));
      this.ianns.put(paramByteVector);
    } 
    if (this.panns != null) {
      paramByteVector.putShort(this.cw.newUTF8("RuntimeVisibleParameterAnnotations"));
      AnnotationWriter.put(this.panns, this.synthetics, paramByteVector);
    } 
    if (this.ipanns != null) {
      paramByteVector.putShort(this.cw.newUTF8("RuntimeInvisibleParameterAnnotations"));
      AnnotationWriter.put(this.ipanns, this.synthetics, paramByteVector);
    } 
    if (this.attrs != null)
      this.attrs.put(this.cw, null, 0, -1, -1, paramByteVector); 
  }
  
  private void resizeInstructions() {
    byte[] arrayOfByte = this.code.data;
    int[] arrayOfInt1 = new int[0];
    int[] arrayOfInt2 = new int[0];
    boolean[] arrayOfBoolean = new boolean[this.code.length];
    byte b = 3;
    do {
      if (b == 3)
        b = 2; 
      int k = 0;
      while (k < arrayOfByte.length) {
        int n;
        int m;
        byte b1 = arrayOfByte[k] & 0xFF;
        int i1 = 0;
        switch (ClassWriter.TYPE[b1]) {
          case 0:
          case 4:
            k++;
            break;
          case 8:
            if (b1 > 201) {
              b1 = (b1 < 218) ? (b1 - 49) : (b1 - 20);
              m = k + readUnsignedShort(arrayOfByte, k + 1);
            } else {
              m = k + readShort(arrayOfByte, k + 1);
            } 
            n = getNewOffset(arrayOfInt1, arrayOfInt2, k, m);
            if ((n < -32768 || n > 32767) && !arrayOfBoolean[k]) {
              if (b1 == 167 || b1 == 168) {
                i1 = 2;
              } else {
                i1 = 5;
              } 
              arrayOfBoolean[k] = true;
            } 
            k += 3;
            break;
          case 9:
            k += 5;
            break;
          case 13:
            if (b == 1) {
              n = getNewOffset(arrayOfInt1, arrayOfInt2, 0, k);
              i1 = -(n & 0x3);
            } else if (!arrayOfBoolean[k]) {
              i1 = k & 0x3;
              arrayOfBoolean[k] = true;
            } 
            k = k + 4 - (k & 0x3);
            k += 4 * (readInt(arrayOfByte, k + 8) - readInt(arrayOfByte, k + 4) + 1) + 12;
            break;
          case 14:
            if (b == 1) {
              n = getNewOffset(arrayOfInt1, arrayOfInt2, 0, k);
              i1 = -(n & 0x3);
            } else if (!arrayOfBoolean[k]) {
              i1 = k & 0x3;
              arrayOfBoolean[k] = true;
            } 
            k = k + 4 - (k & 0x3);
            k += 8 * readInt(arrayOfByte, k + 4) + 8;
            break;
          case 16:
            b1 = arrayOfByte[k + 1] & 0xFF;
            if (b1 == 132) {
              k += 6;
              break;
            } 
            k += 4;
            break;
          case 1:
          case 3:
          case 10:
            k += 2;
            break;
          case 2:
          case 5:
          case 6:
          case 11:
          case 12:
            k += 3;
            break;
          case 7:
            k += 5;
            break;
          default:
            k += 4;
            break;
        } 
        if (i1 != 0) {
          int[] arrayOfInt3 = new int[arrayOfInt1.length + 1];
          int[] arrayOfInt4 = new int[arrayOfInt2.length + 1];
          System.arraycopy(arrayOfInt1, 0, arrayOfInt3, 0, arrayOfInt1.length);
          System.arraycopy(arrayOfInt2, 0, arrayOfInt4, 0, arrayOfInt2.length);
          arrayOfInt3[arrayOfInt1.length] = k;
          arrayOfInt4[arrayOfInt2.length] = i1;
          arrayOfInt1 = arrayOfInt3;
          arrayOfInt2 = arrayOfInt4;
          if (i1 > 0)
            b = 3; 
        } 
      } 
      if (b >= 3)
        continue; 
      b--;
    } while (b != 0);
    ByteVector byteVector = new ByteVector(this.code.length);
    int i;
    for (i = 0; i < this.code.length; i += 4) {
      int i1;
      int n;
      int m;
      int k;
      byte b1 = arrayOfByte[i] & 0xFF;
      switch (ClassWriter.TYPE[b1]) {
        case 0:
        case 4:
          byteVector.putByte(b1);
          i++;
          continue;
        case 8:
          if (b1 > 201) {
            b1 = (b1 < 218) ? (b1 - 49) : (b1 - 20);
            m = i + readUnsignedShort(arrayOfByte, i + 1);
          } else {
            m = i + readShort(arrayOfByte, i + 1);
          } 
          i1 = getNewOffset(arrayOfInt1, arrayOfInt2, i, m);
          if (arrayOfBoolean[i]) {
            if (b1 == 167) {
              byteVector.putByte(200);
            } else if (b1 == 168) {
              byteVector.putByte(201);
            } else {
              byteVector.putByte((b1 <= 166) ? ((b1 + 1 ^ true) - 1) : (b1 ^ true));
              byteVector.putShort(8);
              byteVector.putByte(200);
              i1 -= 3;
            } 
            byteVector.putInt(i1);
          } else {
            byteVector.putByte(b1);
            byteVector.putShort(i1);
          } 
          i += 3;
          continue;
        case 9:
          m = i + readInt(arrayOfByte, i + 1);
          i1 = getNewOffset(arrayOfInt1, arrayOfInt2, i, m);
          byteVector.putByte(b1);
          byteVector.putInt(i1);
          i += 5;
          continue;
        case 13:
          k = i;
          i = i + 4 - (k & 0x3);
          byteVector.putByte(170);
          byteVector.length += (4 - byteVector.length % 4) % 4;
          m = k + readInt(arrayOfByte, i);
          i += 4;
          i1 = getNewOffset(arrayOfInt1, arrayOfInt2, k, m);
          byteVector.putInt(i1);
          n = readInt(arrayOfByte, i);
          i += 4;
          byteVector.putInt(n);
          n = readInt(arrayOfByte, i) - n + 1;
          i += 4;
          byteVector.putInt(readInt(arrayOfByte, i - 4));
          while (n > 0) {
            m = k + readInt(arrayOfByte, i);
            i += 4;
            i1 = getNewOffset(arrayOfInt1, arrayOfInt2, k, m);
            byteVector.putInt(i1);
            n--;
          } 
          continue;
        case 14:
          k = i;
          i = i + 4 - (k & 0x3);
          byteVector.putByte(171);
          byteVector.length += (4 - byteVector.length % 4) % 4;
          m = k + readInt(arrayOfByte, i);
          i += 4;
          i1 = getNewOffset(arrayOfInt1, arrayOfInt2, k, m);
          byteVector.putInt(i1);
          n = readInt(arrayOfByte, i);
          i += 4;
          byteVector.putInt(n);
          while (n > 0) {
            byteVector.putInt(readInt(arrayOfByte, i));
            i += 4;
            m = k + readInt(arrayOfByte, i);
            i += 4;
            i1 = getNewOffset(arrayOfInt1, arrayOfInt2, k, m);
            byteVector.putInt(i1);
            n--;
          } 
          continue;
        case 16:
          b1 = arrayOfByte[i + 1] & 0xFF;
          if (b1 == 132) {
            byteVector.putByteArray(arrayOfByte, i, 6);
            i += 6;
            continue;
          } 
          byteVector.putByteArray(arrayOfByte, i, 4);
          i += 4;
          continue;
        case 1:
        case 3:
        case 10:
          byteVector.putByteArray(arrayOfByte, i, 2);
          i += 2;
          continue;
        case 2:
        case 5:
        case 6:
        case 11:
        case 12:
          byteVector.putByteArray(arrayOfByte, i, 3);
          i += 3;
          continue;
        case 7:
          byteVector.putByteArray(arrayOfByte, i, 5);
          i += 5;
          continue;
      } 
      byteVector.putByteArray(arrayOfByte, i, 4);
    } 
    if (this.frameCount > 0)
      if (this.compute == 0) {
        this.frameCount = 0;
        this.stackMap = null;
        this.previousFrame = null;
        this.frame = null;
        Frame frame1 = new Frame();
        frame1.owner = this.labels;
        Type[] arrayOfType = Type.getArgumentTypes(this.descriptor);
        frame1.initInputFrame(this.cw, this.access, arrayOfType, this.maxLocals);
        visitFrame(frame1);
        for (Label label = this.labels; label != null; label = label.successor) {
          i = label.position - 3;
          if ((label.status & 0x20) != 0 || (i >= 0 && arrayOfBoolean[i])) {
            getNewOffset(arrayOfInt1, arrayOfInt2, label);
            visitFrame(label.frame);
          } 
        } 
      } else {
        this.cw.invalidFrames = true;
      }  
    for (Handler handler = this.firstHandler; handler != null; handler = handler.next) {
      getNewOffset(arrayOfInt1, arrayOfInt2, handler.start);
      getNewOffset(arrayOfInt1, arrayOfInt2, handler.end);
      getNewOffset(arrayOfInt1, arrayOfInt2, handler.handler);
    } 
    int j;
    for (j = 0; j < 2; j++) {
      ByteVector byteVector1 = !j ? this.localVar : this.localVarType;
      if (byteVector1 != null) {
        arrayOfByte = byteVector1.data;
        for (i = 0; i < byteVector1.length; i += 10) {
          int k = readUnsignedShort(arrayOfByte, i);
          int m = getNewOffset(arrayOfInt1, arrayOfInt2, 0, k);
          writeShort(arrayOfByte, i, m);
          k += readUnsignedShort(arrayOfByte, i + 2);
          m = getNewOffset(arrayOfInt1, arrayOfInt2, 0, k) - m;
          writeShort(arrayOfByte, i + 2, m);
        } 
      } 
    } 
    if (this.lineNumber != null) {
      arrayOfByte = this.lineNumber.data;
      for (i = 0; i < this.lineNumber.length; i += 4)
        writeShort(arrayOfByte, i, getNewOffset(arrayOfInt1, arrayOfInt2, 0, readUnsignedShort(arrayOfByte, i))); 
    } 
    for (Attribute attribute = this.cattrs; attribute != null; attribute = attribute.next) {
      Label[] arrayOfLabel = attribute.getLabels();
      if (arrayOfLabel != null)
        for (j = arrayOfLabel.length - 1; j >= 0; j--)
          getNewOffset(arrayOfInt1, arrayOfInt2, arrayOfLabel[j]);  
    } 
    this.code = byteVector;
  }
  
  static int readUnsignedShort(byte[] paramArrayOfByte, int paramInt) { return (paramArrayOfByte[paramInt] & 0xFF) << 8 | paramArrayOfByte[paramInt + 1] & 0xFF; }
  
  static short readShort(byte[] paramArrayOfByte, int paramInt) { return (short)((paramArrayOfByte[paramInt] & 0xFF) << 8 | paramArrayOfByte[paramInt + 1] & 0xFF); }
  
  static int readInt(byte[] paramArrayOfByte, int paramInt) { return (paramArrayOfByte[paramInt] & 0xFF) << 24 | (paramArrayOfByte[paramInt + 1] & 0xFF) << 16 | (paramArrayOfByte[paramInt + 2] & 0xFF) << 8 | paramArrayOfByte[paramInt + 3] & 0xFF; }
  
  static void writeShort(byte[] paramArrayOfByte, int paramInt1, int paramInt2) {
    paramArrayOfByte[paramInt1] = (byte)(paramInt2 >>> 8);
    paramArrayOfByte[paramInt1 + 1] = (byte)paramInt2;
  }
  
  static int getNewOffset(int[] paramArrayOfInt1, int[] paramArrayOfInt2, int paramInt1, int paramInt2) {
    int i = paramInt2 - paramInt1;
    for (byte b = 0; b < paramArrayOfInt1.length; b++) {
      if (paramInt1 < paramArrayOfInt1[b] && paramArrayOfInt1[b] <= paramInt2) {
        i += paramArrayOfInt2[b];
      } else if (paramInt2 < paramArrayOfInt1[b] && paramArrayOfInt1[b] <= paramInt1) {
        i -= paramArrayOfInt2[b];
      } 
    } 
    return i;
  }
  
  static void getNewOffset(int[] paramArrayOfInt1, int[] paramArrayOfInt2, Label paramLabel) {
    if ((paramLabel.status & 0x4) == 0) {
      paramLabel.position = getNewOffset(paramArrayOfInt1, paramArrayOfInt2, 0, paramLabel.position);
      paramLabel.status |= 0x4;
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\org\objectweb\asm\MethodWriter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */