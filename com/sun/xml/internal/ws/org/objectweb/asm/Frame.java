package com.sun.xml.internal.ws.org.objectweb.asm;

final class Frame {
  static final int DIM = -268435456;
  
  static final int ARRAY_OF = 268435456;
  
  static final int ELEMENT_OF = -268435456;
  
  static final int KIND = 251658240;
  
  static final int VALUE = 16777215;
  
  static final int BASE_KIND = 267386880;
  
  static final int BASE_VALUE = 1048575;
  
  static final int BASE = 16777216;
  
  static final int OBJECT = 24117248;
  
  static final int UNINITIALIZED = 25165824;
  
  private static final int LOCAL = 33554432;
  
  private static final int STACK = 50331648;
  
  static final int TOP = 16777216;
  
  static final int BOOLEAN = 16777225;
  
  static final int BYTE = 16777226;
  
  static final int CHAR = 16777227;
  
  static final int SHORT = 16777228;
  
  static final int INTEGER = 16777217;
  
  static final int FLOAT = 16777218;
  
  static final int DOUBLE = 16777219;
  
  static final int LONG = 16777220;
  
  static final int NULL = 16777221;
  
  static final int UNINITIALIZED_THIS = 16777222;
  
  static final int[] SIZE;
  
  Label owner;
  
  int[] inputLocals;
  
  int[] inputStack;
  
  private int[] outputLocals;
  
  private int[] outputStack;
  
  private int outputStackTop;
  
  private int initializationCount;
  
  private int[] initializations;
  
  private int get(int paramInt) {
    if (this.outputLocals == null || paramInt >= this.outputLocals.length)
      return 0x2000000 | paramInt; 
    int i = this.outputLocals[paramInt];
    if (i == 0)
      i = this.outputLocals[paramInt] = 0x2000000 | paramInt; 
    return i;
  }
  
  private void set(int paramInt1, int paramInt2) {
    if (this.outputLocals == null)
      this.outputLocals = new int[10]; 
    int i = this.outputLocals.length;
    if (paramInt1 >= i) {
      int[] arrayOfInt = new int[Math.max(paramInt1 + 1, 2 * i)];
      System.arraycopy(this.outputLocals, 0, arrayOfInt, 0, i);
      this.outputLocals = arrayOfInt;
    } 
    this.outputLocals[paramInt1] = paramInt2;
  }
  
  private void push(int paramInt) {
    if (this.outputStack == null)
      this.outputStack = new int[10]; 
    int i = this.outputStack.length;
    if (this.outputStackTop >= i) {
      int[] arrayOfInt = new int[Math.max(this.outputStackTop + 1, 2 * i)];
      System.arraycopy(this.outputStack, 0, arrayOfInt, 0, i);
      this.outputStack = arrayOfInt;
    } 
    this.outputStack[this.outputStackTop++] = paramInt;
    int j = this.owner.inputStackTop + this.outputStackTop;
    if (j > this.owner.outputStackMax)
      this.owner.outputStackMax = j; 
  }
  
  private void push(ClassWriter paramClassWriter, String paramString) {
    int i = type(paramClassWriter, paramString);
    if (i != 0) {
      push(i);
      if (i == 16777220 || i == 16777219)
        push(16777216); 
    } 
  }
  
  private static int type(ClassWriter paramClassWriter, String paramString) {
    int i = (paramString.charAt(0) == '(') ? (paramString.indexOf(')') + 1) : 0;
    switch (paramString.charAt(i)) {
      case 'V':
        return 0;
      case 'B':
      case 'C':
      case 'I':
      case 'S':
      case 'Z':
        return 16777217;
      case 'F':
        return 16777218;
      case 'J':
        return 16777220;
      case 'D':
        return 16777219;
      case 'L':
        str = paramString.substring(i + 1, paramString.length() - 1);
        return 0x1700000 | paramClassWriter.addType(str);
    } 
    int k;
    for (k = i + 1; paramString.charAt(k) == '['; k++);
    switch (paramString.charAt(k)) {
      case 'Z':
        j = 16777225;
        return k - i << 28 | j;
      case 'C':
        j = 16777227;
        return k - i << 28 | j;
      case 'B':
        j = 16777226;
        return k - i << 28 | j;
      case 'S':
        j = 16777228;
        return k - i << 28 | j;
      case 'I':
        j = 16777217;
        return k - i << 28 | j;
      case 'F':
        j = 16777218;
        return k - i << 28 | j;
      case 'J':
        j = 16777220;
        return k - i << 28 | j;
      case 'D':
        j = 16777219;
        return k - i << 28 | j;
    } 
    String str = paramString.substring(k + 1, paramString.length() - 1);
    int j = 0x1700000 | paramClassWriter.addType(str);
    return k - i << 28 | j;
  }
  
  private int pop() { return (this.outputStackTop > 0) ? this.outputStack[--this.outputStackTop] : (0x3000000 | ---this.owner.inputStackTop); }
  
  private void pop(int paramInt) {
    if (this.outputStackTop >= paramInt) {
      this.outputStackTop -= paramInt;
    } else {
      this.owner.inputStackTop -= paramInt - this.outputStackTop;
      this.outputStackTop = 0;
    } 
  }
  
  private void pop(String paramString) {
    char c = paramString.charAt(0);
    if (c == '(') {
      pop((MethodWriter.getArgumentsAndReturnSizes(paramString) >> 2) - 1);
    } else if (c == 'J' || c == 'D') {
      pop(2);
    } else {
      pop(1);
    } 
  }
  
  private void init(int paramInt) {
    if (this.initializations == null)
      this.initializations = new int[2]; 
    int i = this.initializations.length;
    if (this.initializationCount >= i) {
      int[] arrayOfInt = new int[Math.max(this.initializationCount + 1, 2 * i)];
      System.arraycopy(this.initializations, 0, arrayOfInt, 0, i);
      this.initializations = arrayOfInt;
    } 
    this.initializations[this.initializationCount++] = paramInt;
  }
  
  private int init(ClassWriter paramClassWriter, int paramInt) {
    int i;
    if (paramInt == 16777222) {
      i = 0x1700000 | paramClassWriter.addType(paramClassWriter.thisName);
    } else if ((paramInt & 0xFFF00000) == 25165824) {
      String str = (paramClassWriter.typeTable[paramInt & 0xFFFFF]).strVal1;
      i = 0x1700000 | paramClassWriter.addType(str);
    } else {
      return paramInt;
    } 
    for (byte b = 0; b < this.initializationCount; b++) {
      int j = this.initializations[b];
      int k = j & 0xF0000000;
      int m = j & 0xF000000;
      if (m == 33554432) {
        j = k + this.inputLocals[j & 0xFFFFFF];
      } else if (m == 50331648) {
        j = k + this.inputStack[this.inputStack.length - (j & 0xFFFFFF)];
      } 
      if (paramInt == j)
        return i; 
    } 
    return paramInt;
  }
  
  void initInputFrame(ClassWriter paramClassWriter, int paramInt1, Type[] paramArrayOfType, int paramInt2) {
    this.inputLocals = new int[paramInt2];
    this.inputStack = new int[0];
    byte b1 = 0;
    if ((paramInt1 & 0x8) == 0)
      if ((paramInt1 & 0x40000) == 0) {
        this.inputLocals[b1++] = 0x1700000 | paramClassWriter.addType(paramClassWriter.thisName);
      } else {
        this.inputLocals[b1++] = 16777222;
      }  
    for (byte b2 = 0; b2 < paramArrayOfType.length; b2++) {
      int i = type(paramClassWriter, paramArrayOfType[b2].getDescriptor());
      this.inputLocals[b1++] = i;
      if (i == 16777220 || i == 16777219)
        this.inputLocals[b1++] = 16777216; 
    } 
    while (b1 < paramInt2)
      this.inputLocals[b1++] = 16777216; 
  }
  
  void execute(int paramInt1, int paramInt2, ClassWriter paramClassWriter, Item paramItem) {
    String str;
    int m;
    int k;
    int j;
    int i;
    switch (paramInt1) {
      case 0:
      case 116:
      case 117:
      case 118:
      case 119:
      case 145:
      case 146:
      case 147:
      case 167:
      case 177:
        return;
      case 1:
        push(16777221);
      case 2:
      case 3:
      case 4:
      case 5:
      case 6:
      case 7:
      case 8:
      case 16:
      case 17:
      case 21:
        push(16777217);
      case 9:
      case 10:
      case 22:
        push(16777220);
        push(16777216);
      case 11:
      case 12:
      case 13:
      case 23:
        push(16777218);
      case 14:
      case 15:
      case 24:
        push(16777219);
        push(16777216);
      case 18:
        switch (paramItem.type) {
          case 3:
            push(16777217);
          case 5:
            push(16777220);
            push(16777216);
          case 4:
            push(16777218);
          case 6:
            push(16777219);
            push(16777216);
          case 7:
            push(0x1700000 | paramClassWriter.addType("java/lang/Class"));
        } 
        push(0x1700000 | paramClassWriter.addType("java/lang/String"));
      case 25:
        push(get(paramInt2));
      case 46:
      case 51:
      case 52:
      case 53:
        pop(2);
        push(16777217);
      case 47:
      case 143:
        pop(2);
        push(16777220);
        push(16777216);
      case 48:
        pop(2);
        push(16777218);
      case 49:
      case 138:
        pop(2);
        push(16777219);
        push(16777216);
      case 50:
        pop(1);
        i = pop();
        push(-268435456 + i);
      case 54:
      case 56:
      case 58:
        i = pop();
        set(paramInt2, i);
        if (paramInt2 > 0) {
          int n = get(paramInt2 - 1);
          if (n == 16777220 || n == 16777219)
            set(paramInt2 - 1, 16777216); 
        } 
      case 55:
      case 57:
        pop(1);
        i = pop();
        set(paramInt2, i);
        set(paramInt2 + 1, 16777216);
        if (paramInt2 > 0) {
          int n = get(paramInt2 - 1);
          if (n == 16777220 || n == 16777219)
            set(paramInt2 - 1, 16777216); 
        } 
      case 79:
      case 81:
      case 83:
      case 84:
      case 85:
      case 86:
        pop(3);
      case 80:
      case 82:
        pop(4);
      case 87:
      case 153:
      case 154:
      case 155:
      case 156:
      case 157:
      case 158:
      case 170:
      case 171:
      case 172:
      case 174:
      case 176:
      case 191:
      case 194:
      case 195:
      case 198:
      case 199:
        pop(1);
      case 88:
      case 159:
      case 160:
      case 161:
      case 162:
      case 163:
      case 164:
      case 165:
      case 166:
      case 173:
      case 175:
        pop(2);
      case 89:
        i = pop();
        push(i);
        push(i);
      case 90:
        i = pop();
        j = pop();
        push(i);
        push(j);
        push(i);
      case 91:
        i = pop();
        j = pop();
        k = pop();
        push(i);
        push(k);
        push(j);
        push(i);
      case 92:
        i = pop();
        j = pop();
        push(j);
        push(i);
        push(j);
        push(i);
      case 93:
        i = pop();
        j = pop();
        k = pop();
        push(j);
        push(i);
        push(k);
        push(j);
        push(i);
      case 94:
        i = pop();
        j = pop();
        k = pop();
        m = pop();
        push(j);
        push(i);
        push(m);
        push(k);
        push(j);
        push(i);
      case 95:
        i = pop();
        j = pop();
        push(i);
        push(j);
      case 96:
      case 100:
      case 104:
      case 108:
      case 112:
      case 120:
      case 122:
      case 124:
      case 126:
      case 128:
      case 130:
      case 136:
      case 142:
      case 149:
      case 150:
        pop(2);
        push(16777217);
      case 97:
      case 101:
      case 105:
      case 109:
      case 113:
      case 127:
      case 129:
      case 131:
        pop(4);
        push(16777220);
        push(16777216);
      case 98:
      case 102:
      case 106:
      case 110:
      case 114:
      case 137:
      case 144:
        pop(2);
        push(16777218);
      case 99:
      case 103:
      case 107:
      case 111:
      case 115:
        pop(4);
        push(16777219);
        push(16777216);
      case 121:
      case 123:
      case 125:
        pop(3);
        push(16777220);
        push(16777216);
      case 132:
        set(paramInt2, 16777217);
      case 133:
      case 140:
        pop(1);
        push(16777220);
        push(16777216);
      case 134:
        pop(1);
        push(16777218);
      case 135:
      case 141:
        pop(1);
        push(16777219);
        push(16777216);
      case 139:
      case 190:
      case 193:
        pop(1);
        push(16777217);
      case 148:
      case 151:
      case 152:
        pop(4);
        push(16777217);
      case 168:
      case 169:
        throw new RuntimeException("JSR/RET are not supported with computeFrames option");
      case 178:
        push(paramClassWriter, paramItem.strVal3);
      case 179:
        pop(paramItem.strVal3);
      case 180:
        pop(1);
        push(paramClassWriter, paramItem.strVal3);
      case 181:
        pop(paramItem.strVal3);
        pop();
      case 182:
      case 183:
      case 184:
      case 185:
        pop(paramItem.strVal3);
        if (paramInt1 != 184) {
          i = pop();
          if (paramInt1 == 183 && paramItem.strVal2.charAt(0) == '<')
            init(i); 
        } 
        push(paramClassWriter, paramItem.strVal3);
      case 187:
        push(0x1800000 | paramClassWriter.addUninitializedType(paramItem.strVal1, paramInt2));
      case 188:
        pop();
        switch (paramInt2) {
          case 4:
            push(285212681);
          case 5:
            push(285212683);
          case 8:
            push(285212682);
          case 9:
            push(285212684);
          case 10:
            push(285212673);
          case 6:
            push(285212674);
          case 7:
            push(285212675);
        } 
        push(285212676);
      case 189:
        str = paramItem.strVal1;
        pop();
        if (str.charAt(0) == '[') {
          push(paramClassWriter, '[' + str);
        } else {
          push(0x11700000 | paramClassWriter.addType(str));
        } 
      case 192:
        str = paramItem.strVal1;
        pop();
        if (str.charAt(0) == '[') {
          push(paramClassWriter, str);
        } else {
          push(0x1700000 | paramClassWriter.addType(str));
        } 
    } 
    pop(paramInt2);
    push(paramClassWriter, paramItem.strVal1);
  }
  
  boolean merge(ClassWriter paramClassWriter, Frame paramFrame, int paramInt) {
    null = false;
    int j = this.inputLocals.length;
    int k = this.inputStack.length;
    if (paramFrame.inputLocals == null) {
      paramFrame.inputLocals = new int[j];
      null = true;
    } 
    int i;
    for (i = 0; i < j; i++) {
      int n;
      if (this.outputLocals != null && i < this.outputLocals.length) {
        int i1 = this.outputLocals[i];
        if (i1 == 0) {
          n = this.inputLocals[i];
        } else {
          int i2 = i1 & 0xF0000000;
          int i3 = i1 & 0xF000000;
          if (i3 == 33554432) {
            n = i2 + this.inputLocals[i1 & 0xFFFFFF];
          } else if (i3 == 50331648) {
            n = i2 + this.inputStack[k - (i1 & 0xFFFFFF)];
          } else {
            n = i1;
          } 
        } 
      } else {
        n = this.inputLocals[i];
      } 
      if (this.initializations != null)
        n = init(paramClassWriter, n); 
      null |= merge(paramClassWriter, n, paramFrame.inputLocals, i);
    } 
    if (paramInt > 0) {
      for (i = 0; i < j; i++) {
        int n = this.inputLocals[i];
        null |= merge(paramClassWriter, n, paramFrame.inputLocals, i);
      } 
      if (paramFrame.inputStack == null) {
        paramFrame.inputStack = new int[1];
        null = true;
      } 
      return merge(paramClassWriter, paramInt, paramFrame.inputStack, 0);
    } 
    int m = this.inputStack.length + this.owner.inputStackTop;
    if (paramFrame.inputStack == null) {
      paramFrame.inputStack = new int[m + this.outputStackTop];
      null = true;
    } 
    for (i = 0; i < m; i++) {
      int n = this.inputStack[i];
      if (this.initializations != null)
        n = init(paramClassWriter, n); 
      null |= merge(paramClassWriter, n, paramFrame.inputStack, i);
    } 
    for (i = 0; i < this.outputStackTop; i++) {
      int i3;
      int n = this.outputStack[i];
      int i1 = n & 0xF0000000;
      int i2 = n & 0xF000000;
      if (i2 == 33554432) {
        i3 = i1 + this.inputLocals[n & 0xFFFFFF];
      } else if (i2 == 50331648) {
        i3 = i1 + this.inputStack[k - (n & 0xFFFFFF)];
      } else {
        i3 = n;
      } 
      if (this.initializations != null)
        i3 = init(paramClassWriter, i3); 
      null |= merge(paramClassWriter, i3, paramFrame.inputStack, m + i);
    } 
    return null;
  }
  
  private static boolean merge(ClassWriter paramClassWriter, int paramInt1, int[] paramArrayOfInt, int paramInt2) {
    int j;
    int i = paramArrayOfInt[paramInt2];
    if (i == paramInt1)
      return false; 
    if ((paramInt1 & 0xFFFFFFF) == 16777221) {
      if (i == 16777221)
        return false; 
      paramInt1 = 16777221;
    } 
    if (i == 0) {
      paramArrayOfInt[paramInt2] = paramInt1;
      return true;
    } 
    if ((i & 0xFF00000) == 24117248 || (i & 0xF0000000) != 0) {
      if (paramInt1 == 16777221)
        return false; 
      if ((paramInt1 & 0xFFF00000) == (i & 0xFFF00000)) {
        if ((i & 0xFF00000) == 24117248) {
          j = paramInt1 & 0xF0000000 | 0x1700000 | paramClassWriter.getMergedType(paramInt1 & 0xFFFFF, i & 0xFFFFF);
        } else {
          j = 0x1700000 | paramClassWriter.addType("java/lang/Object");
        } 
      } else if ((paramInt1 & 0xFF00000) == 24117248 || (paramInt1 & 0xF0000000) != 0) {
        j = 0x1700000 | paramClassWriter.addType("java/lang/Object");
      } else {
        j = 16777216;
      } 
    } else if (i == 16777221) {
      j = ((paramInt1 & 0xFF00000) == 24117248 || (paramInt1 & 0xF0000000) != 0) ? paramInt1 : 16777216;
    } else {
      j = 16777216;
    } 
    if (i != j) {
      paramArrayOfInt[paramInt2] = j;
      return true;
    } 
    return false;
  }
  
  static  {
    int[] arrayOfInt = new int[202];
    String str = "EFFFFFFFFGGFFFGGFFFEEFGFGFEEEEEEEEEEEEEEEEEEEEDEDEDDDDDCDCDEEEEEEEEEEEEEEEEEEEEBABABBBBDCFFFGGGEDCDCDCDCDCDCDCDCDCDCEEEEDDDDDDDCDCDCEFEFDDEEFFDEDEEEBDDBBDDDDDDCCCCCCCCEFEDDDCDCDEEEEEEEEEEFEEEEEEDDEEDDEE";
    for (byte b = 0; b < arrayOfInt.length; b++)
      arrayOfInt[b] = str.charAt(b) - 'E'; 
    SIZE = arrayOfInt;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\com\sun\xml\internal\ws\org\objectweb\asm\Frame.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */