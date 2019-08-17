package jdk.internal.org.objectweb.asm.commons;

import java.util.AbstractMap;
import java.util.ArrayList;
import java.util.BitSet;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import jdk.internal.org.objectweb.asm.Label;
import jdk.internal.org.objectweb.asm.MethodVisitor;
import jdk.internal.org.objectweb.asm.Opcodes;
import jdk.internal.org.objectweb.asm.tree.AbstractInsnNode;
import jdk.internal.org.objectweb.asm.tree.InsnList;
import jdk.internal.org.objectweb.asm.tree.InsnNode;
import jdk.internal.org.objectweb.asm.tree.JumpInsnNode;
import jdk.internal.org.objectweb.asm.tree.LabelNode;
import jdk.internal.org.objectweb.asm.tree.LocalVariableNode;
import jdk.internal.org.objectweb.asm.tree.LookupSwitchInsnNode;
import jdk.internal.org.objectweb.asm.tree.MethodNode;
import jdk.internal.org.objectweb.asm.tree.TableSwitchInsnNode;
import jdk.internal.org.objectweb.asm.tree.TryCatchBlockNode;

public class JSRInlinerAdapter extends MethodNode implements Opcodes {
  private static final boolean LOGGING = false;
  
  private final Map<LabelNode, BitSet> subroutineHeads = new HashMap();
  
  private final BitSet mainSubroutine = new BitSet();
  
  final BitSet dualCitizens = new BitSet();
  
  public JSRInlinerAdapter(MethodVisitor paramMethodVisitor, int paramInt, String paramString1, String paramString2, String paramString3, String[] paramArrayOfString) {
    this(327680, paramMethodVisitor, paramInt, paramString1, paramString2, paramString3, paramArrayOfString);
    if (getClass() != JSRInlinerAdapter.class)
      throw new IllegalStateException(); 
  }
  
  protected JSRInlinerAdapter(int paramInt1, MethodVisitor paramMethodVisitor, int paramInt2, String paramString1, String paramString2, String paramString3, String[] paramArrayOfString) {
    super(paramInt1, paramInt2, paramString1, paramString2, paramString3, paramArrayOfString);
    this.mv = paramMethodVisitor;
  }
  
  public void visitJumpInsn(int paramInt, Label paramLabel) {
    super.visitJumpInsn(paramInt, paramLabel);
    LabelNode labelNode = ((JumpInsnNode)this.instructions.getLast()).label;
    if (paramInt == 168 && !this.subroutineHeads.containsKey(labelNode))
      this.subroutineHeads.put(labelNode, new BitSet()); 
  }
  
  public void visitEnd() {
    if (!this.subroutineHeads.isEmpty()) {
      markSubroutines();
      emitCode();
    } 
    if (this.mv != null)
      accept(this.mv); 
  }
  
  private void markSubroutines() {
    BitSet bitSet = new BitSet();
    markSubroutineWalk(this.mainSubroutine, 0, bitSet);
    for (Map.Entry entry : this.subroutineHeads.entrySet()) {
      LabelNode labelNode = (LabelNode)entry.getKey();
      BitSet bitSet1 = (BitSet)entry.getValue();
      int i = this.instructions.indexOf(labelNode);
      markSubroutineWalk(bitSet1, i, bitSet);
    } 
  }
  
  private void markSubroutineWalk(BitSet paramBitSet1, int paramInt, BitSet paramBitSet2) {
    markSubroutineWalkDFS(paramBitSet1, paramInt, paramBitSet2);
    boolean bool = true;
    while (bool) {
      bool = false;
      for (TryCatchBlockNode tryCatchBlockNode : this.tryCatchBlocks) {
        int i = this.instructions.indexOf(tryCatchBlockNode.handler);
        if (paramBitSet1.get(i))
          continue; 
        int j = this.instructions.indexOf(tryCatchBlockNode.start);
        int k = this.instructions.indexOf(tryCatchBlockNode.end);
        int m = paramBitSet1.nextSetBit(j);
        if (m != -1 && m < k) {
          markSubroutineWalkDFS(paramBitSet1, i, paramBitSet2);
          bool = true;
        } 
      } 
    } 
  }
  
  private void markSubroutineWalkDFS(BitSet paramBitSet1, int paramInt, BitSet paramBitSet2) {
    do {
      AbstractInsnNode abstractInsnNode = this.instructions.get(paramInt);
      if (paramBitSet1.get(paramInt))
        return; 
      paramBitSet1.set(paramInt);
      if (paramBitSet2.get(paramInt))
        this.dualCitizens.set(paramInt); 
      paramBitSet2.set(paramInt);
      if (abstractInsnNode.getType() == 7 && abstractInsnNode.getOpcode() != 168) {
        JumpInsnNode jumpInsnNode = (JumpInsnNode)abstractInsnNode;
        int i = this.instructions.indexOf(jumpInsnNode.label);
        markSubroutineWalkDFS(paramBitSet1, i, paramBitSet2);
      } 
      if (abstractInsnNode.getType() == 11) {
        TableSwitchInsnNode tableSwitchInsnNode = (TableSwitchInsnNode)abstractInsnNode;
        int i = this.instructions.indexOf(tableSwitchInsnNode.dflt);
        markSubroutineWalkDFS(paramBitSet1, i, paramBitSet2);
        for (int j = tableSwitchInsnNode.labels.size() - 1; j >= 0; j--) {
          LabelNode labelNode = (LabelNode)tableSwitchInsnNode.labels.get(j);
          i = this.instructions.indexOf(labelNode);
          markSubroutineWalkDFS(paramBitSet1, i, paramBitSet2);
        } 
      } 
      if (abstractInsnNode.getType() == 12) {
        LookupSwitchInsnNode lookupSwitchInsnNode = (LookupSwitchInsnNode)abstractInsnNode;
        int i = this.instructions.indexOf(lookupSwitchInsnNode.dflt);
        markSubroutineWalkDFS(paramBitSet1, i, paramBitSet2);
        for (int j = lookupSwitchInsnNode.labels.size() - 1; j >= 0; j--) {
          LabelNode labelNode = (LabelNode)lookupSwitchInsnNode.labels.get(j);
          i = this.instructions.indexOf(labelNode);
          markSubroutineWalkDFS(paramBitSet1, i, paramBitSet2);
        } 
      } 
      switch (this.instructions.get(paramInt).getOpcode()) {
        case 167:
        case 169:
        case 170:
        case 171:
        case 172:
        case 173:
        case 174:
        case 175:
        case 176:
        case 177:
        case 191:
          return;
      } 
    } while (++paramInt < this.instructions.size());
  }
  
  private void emitCode() {
    LinkedList linkedList = new LinkedList();
    linkedList.add(new Instantiation(null, this.mainSubroutine));
    InsnList insnList = new InsnList();
    ArrayList arrayList1 = new ArrayList();
    ArrayList arrayList2 = new ArrayList();
    while (!linkedList.isEmpty()) {
      Instantiation instantiation = (Instantiation)linkedList.removeFirst();
      emitSubroutine(instantiation, linkedList, insnList, arrayList1, arrayList2);
    } 
    this.instructions = insnList;
    this.tryCatchBlocks = arrayList1;
    this.localVariables = arrayList2;
  }
  
  private void emitSubroutine(Instantiation paramInstantiation, List<Instantiation> paramList1, InsnList paramInsnList, List<TryCatchBlockNode> paramList2, List<LocalVariableNode> paramList3) {
    LabelNode labelNode = null;
    byte b = 0;
    int i = this.instructions.size();
    while (b < i) {
      AbstractInsnNode abstractInsnNode = this.instructions.get(b);
      Instantiation instantiation = paramInstantiation.findOwner(b);
      if (abstractInsnNode.getType() == 8) {
        LabelNode labelNode1 = (LabelNode)abstractInsnNode;
        LabelNode labelNode2 = paramInstantiation.rangeLabel(labelNode1);
        if (labelNode2 != labelNode) {
          paramInsnList.add(labelNode2);
          labelNode = labelNode2;
        } 
      } else if (instantiation == paramInstantiation) {
        if (abstractInsnNode.getOpcode() == 169) {
          LabelNode labelNode1 = null;
          for (Instantiation instantiation1 = paramInstantiation; instantiation1 != null; instantiation1 = instantiation1.previous) {
            if (instantiation1.subroutine.get(b))
              labelNode1 = instantiation1.returnLabel; 
          } 
          if (labelNode1 == null)
            throw new RuntimeException("Instruction #" + b + " is a RET not owned by any subroutine"); 
          paramInsnList.add(new JumpInsnNode(167, labelNode1));
        } else if (abstractInsnNode.getOpcode() == 168) {
          LabelNode labelNode1 = ((JumpInsnNode)abstractInsnNode).label;
          BitSet bitSet = (BitSet)this.subroutineHeads.get(labelNode1);
          Instantiation instantiation1 = new Instantiation(paramInstantiation, bitSet);
          LabelNode labelNode2 = instantiation1.gotoLabel(labelNode1);
          paramInsnList.add(new InsnNode(1));
          paramInsnList.add(new JumpInsnNode(167, labelNode2));
          paramInsnList.add(instantiation1.returnLabel);
          paramList1.add(instantiation1);
        } else {
          paramInsnList.add(abstractInsnNode.clone(paramInstantiation));
        } 
      } 
      b++;
    } 
    for (TryCatchBlockNode tryCatchBlockNode : this.tryCatchBlocks) {
      LabelNode labelNode1 = paramInstantiation.rangeLabel(tryCatchBlockNode.start);
      LabelNode labelNode2 = paramInstantiation.rangeLabel(tryCatchBlockNode.end);
      if (labelNode1 == labelNode2)
        continue; 
      LabelNode labelNode3 = paramInstantiation.gotoLabel(tryCatchBlockNode.handler);
      if (labelNode1 == null || labelNode2 == null || labelNode3 == null)
        throw new RuntimeException("Internal error!"); 
      paramList2.add(new TryCatchBlockNode(labelNode1, labelNode2, labelNode3, tryCatchBlockNode.type));
    } 
    for (LocalVariableNode localVariableNode : this.localVariables) {
      LabelNode labelNode1 = paramInstantiation.rangeLabel(localVariableNode.start);
      LabelNode labelNode2 = paramInstantiation.rangeLabel(localVariableNode.end);
      if (labelNode1 == labelNode2)
        continue; 
      paramList3.add(new LocalVariableNode(localVariableNode.name, localVariableNode.desc, localVariableNode.signature, labelNode1, labelNode2, localVariableNode.index));
    } 
  }
  
  private static void log(String paramString) { System.err.println(paramString); }
  
  private class Instantiation extends AbstractMap<LabelNode, LabelNode> {
    final Instantiation previous;
    
    public final BitSet subroutine;
    
    public final Map<LabelNode, LabelNode> rangeTable = new HashMap();
    
    public final LabelNode returnLabel;
    
    Instantiation(Instantiation param1Instantiation, BitSet param1BitSet) {
      this.previous = param1Instantiation;
      this.subroutine = param1BitSet;
      Instantiation instantiation;
      for (instantiation = param1Instantiation; instantiation != null; instantiation = instantiation.previous) {
        if (instantiation.subroutine == param1BitSet)
          throw new RuntimeException("Recursive invocation of " + param1BitSet); 
      } 
      if (param1Instantiation != null) {
        this.returnLabel = new LabelNode();
      } else {
        this.returnLabel = null;
      } 
      instantiation = null;
      byte b = 0;
      int i = JSRInlinerAdapter.this.instructions.size();
      while (b < i) {
        AbstractInsnNode abstractInsnNode = JSRInlinerAdapter.this.instructions.get(b);
        if (abstractInsnNode.getType() == 8) {
          LabelNode labelNode1;
          LabelNode labelNode2 = (LabelNode)abstractInsnNode;
          if (instantiation == null)
            labelNode1 = new LabelNode(); 
          this.rangeTable.put(labelNode2, labelNode1);
        } else if (findOwner(b) == this) {
          instantiation = null;
        } 
        b++;
      } 
    }
    
    public Instantiation findOwner(int param1Int) {
      if (!this.subroutine.get(param1Int))
        return null; 
      if (!JSRInlinerAdapter.this.dualCitizens.get(param1Int))
        return this; 
      Instantiation instantiation1 = this;
      for (Instantiation instantiation2 = this.previous; instantiation2 != null; instantiation2 = instantiation2.previous) {
        if (instantiation2.subroutine.get(param1Int))
          instantiation1 = instantiation2; 
      } 
      return instantiation1;
    }
    
    public LabelNode gotoLabel(LabelNode param1LabelNode) {
      Instantiation instantiation = findOwner(JSRInlinerAdapter.this.instructions.indexOf(param1LabelNode));
      return (LabelNode)instantiation.rangeTable.get(param1LabelNode);
    }
    
    public LabelNode rangeLabel(LabelNode param1LabelNode) { return (LabelNode)this.rangeTable.get(param1LabelNode); }
    
    public Set<Map.Entry<LabelNode, LabelNode>> entrySet() { return null; }
    
    public LabelNode get(Object param1Object) { return gotoLabel((LabelNode)param1Object); }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\jdk\internal\org\objectweb\asm\commons\JSRInlinerAdapter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */