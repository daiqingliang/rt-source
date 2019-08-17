package java.util.stream;

import java.util.EnumMap;
import java.util.Map;
import java.util.Spliterator;

static enum StreamOpFlag {
  DISTINCT,
  SORTED,
  ORDERED,
  SIZED,
  SHORT_CIRCUIT(12, (SIZED = new StreamOpFlag("SIZED", 3, 3, (ORDERED = new StreamOpFlag("ORDERED", 2, 2, (SORTED = new StreamOpFlag("SORTED", 1, 1, (DISTINCT = new StreamOpFlag("DISTINCT", 0, 0, set(Type.SPLITERATOR).set(Type.STREAM).setAndClear(Type.OP))).set(Type.SPLITERATOR).set(Type.STREAM).setAndClear(Type.OP))).set(Type.SPLITERATOR).set(Type.STREAM).setAndClear(Type.OP).clear(Type.TERMINAL_OP).clear(Type.UPSTREAM_TERMINAL_OP))).set(Type.SPLITERATOR).set(Type.STREAM).clear(Type.OP))).set(Type.OP).set(Type.TERMINAL_OP));
  
  private static final int SET_BITS = 1;
  
  private static final int CLEAR_BITS = 2;
  
  private static final int PRESERVE_BITS = 3;
  
  private final Map<Type, Integer> maskTable;
  
  private final int bitPosition;
  
  private final int set;
  
  private final int clear;
  
  private final int preserve;
  
  static final int SPLITERATOR_CHARACTERISTICS_MASK;
  
  static final int STREAM_MASK;
  
  static final int OP_MASK;
  
  static final int TERMINAL_OP_MASK;
  
  static final int UPSTREAM_TERMINAL_OP_MASK;
  
  private static final int FLAG_MASK;
  
  private static final int FLAG_MASK_IS;
  
  private static final int FLAG_MASK_NOT;
  
  static final int INITIAL_OPS_VALUE;
  
  static final int IS_DISTINCT;
  
  static final int NOT_DISTINCT;
  
  static final int IS_SORTED;
  
  static final int NOT_SORTED;
  
  static final int IS_ORDERED;
  
  static final int NOT_ORDERED;
  
  static final int IS_SIZED;
  
  static final int NOT_SIZED;
  
  static final int IS_SHORT_CIRCUIT;
  
  private static MaskBuilder set(Type paramType) { return (new MaskBuilder(new EnumMap(Type.class))).set(paramType); }
  
  int set() { return this.set; }
  
  int clear() { return this.clear; }
  
  boolean isStreamFlag() { return (((Integer)this.maskTable.get(Type.STREAM)).intValue() > 0); }
  
  boolean isKnown(int paramInt) { return ((paramInt & this.preserve) == this.set); }
  
  boolean isCleared(int paramInt) { return ((paramInt & this.preserve) == this.clear); }
  
  boolean isPreserved(int paramInt) { return ((paramInt & this.preserve) == this.preserve); }
  
  boolean canSet(Type paramType) { return ((((Integer)this.maskTable.get(paramType)).intValue() & true) > 0); }
  
  private static int createMask(Type paramType) {
    int i = 0;
    for (StreamOpFlag streamOpFlag : values())
      i |= ((Integer)streamOpFlag.maskTable.get(paramType)).intValue() << streamOpFlag.bitPosition; 
    return i;
  }
  
  private static int createFlagMask() {
    int i = 0;
    for (StreamOpFlag streamOpFlag : values())
      i |= streamOpFlag.preserve; 
    return i;
  }
  
  private static int getMask(int paramInt) { return (paramInt == 0) ? FLAG_MASK : ((paramInt | (FLAG_MASK_IS & paramInt) << 1 | (FLAG_MASK_NOT & paramInt) >> 1) ^ 0xFFFFFFFF); }
  
  static int combineOpFlags(int paramInt1, int paramInt2) { return paramInt2 & getMask(paramInt1) | paramInt1; }
  
  static int toStreamFlags(int paramInt) { return (paramInt ^ 0xFFFFFFFF) >> 1 & FLAG_MASK_IS & paramInt; }
  
  static int toCharacteristics(int paramInt) { return paramInt & SPLITERATOR_CHARACTERISTICS_MASK; }
  
  static int fromCharacteristics(Spliterator<?> paramSpliterator) {
    int i = paramSpliterator.characteristics();
    return ((i & 0x4) != 0 && paramSpliterator.getComparator() != null) ? (i & SPLITERATOR_CHARACTERISTICS_MASK & 0xFFFFFFFB) : (i & SPLITERATOR_CHARACTERISTICS_MASK);
  }
  
  static int fromCharacteristics(int paramInt) { return paramInt & SPLITERATOR_CHARACTERISTICS_MASK; }
  
  static  {
    SPLITERATOR_CHARACTERISTICS_MASK = createMask(Type.SPLITERATOR);
    STREAM_MASK = createMask(Type.STREAM);
    OP_MASK = createMask(Type.OP);
    TERMINAL_OP_MASK = createMask(Type.TERMINAL_OP);
    UPSTREAM_TERMINAL_OP_MASK = createMask(Type.UPSTREAM_TERMINAL_OP);
    FLAG_MASK = createFlagMask();
    FLAG_MASK_IS = STREAM_MASK;
    FLAG_MASK_NOT = STREAM_MASK << 1;
    INITIAL_OPS_VALUE = FLAG_MASK_IS | FLAG_MASK_NOT;
    IS_DISTINCT = DISTINCT.set;
    NOT_DISTINCT = DISTINCT.clear;
    IS_SORTED = SORTED.set;
    NOT_SORTED = SORTED.clear;
    IS_ORDERED = ORDERED.set;
    NOT_ORDERED = ORDERED.clear;
    IS_SIZED = SIZED.set;
    NOT_SIZED = SIZED.clear;
    IS_SHORT_CIRCUIT = SHORT_CIRCUIT.set;
  }
  
  private static class MaskBuilder {
    final Map<StreamOpFlag.Type, Integer> map;
    
    MaskBuilder(Map<StreamOpFlag.Type, Integer> param1Map) { this.map = param1Map; }
    
    MaskBuilder mask(StreamOpFlag.Type param1Type, Integer param1Integer) {
      this.map.put(param1Type, param1Integer);
      return this;
    }
    
    MaskBuilder set(StreamOpFlag.Type param1Type) { return mask(param1Type, Integer.valueOf(1)); }
    
    MaskBuilder clear(StreamOpFlag.Type param1Type) { return mask(param1Type, Integer.valueOf(2)); }
    
    MaskBuilder setAndClear(StreamOpFlag.Type param1Type) { return mask(param1Type, Integer.valueOf(3)); }
    
    Map<StreamOpFlag.Type, Integer> build() {
      for (StreamOpFlag.Type type : StreamOpFlag.Type.values())
        this.map.putIfAbsent(type, Integer.valueOf(0)); 
      return this.map;
    }
  }
  
  enum Type {
    SPLITERATOR, STREAM, OP, TERMINAL_OP, UPSTREAM_TERMINAL_OP;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\jav\\util\stream\StreamOpFlag.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */