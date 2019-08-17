package jdk.internal.org.objectweb.asm.commons;

import java.util.Collections;
import java.util.Comparator;
import jdk.internal.org.objectweb.asm.MethodVisitor;
import jdk.internal.org.objectweb.asm.tree.MethodNode;
import jdk.internal.org.objectweb.asm.tree.TryCatchBlockNode;

public class TryCatchBlockSorter extends MethodNode {
  public TryCatchBlockSorter(MethodVisitor paramMethodVisitor, int paramInt, String paramString1, String paramString2, String paramString3, String[] paramArrayOfString) { this(327680, paramMethodVisitor, paramInt, paramString1, paramString2, paramString3, paramArrayOfString); }
  
  protected TryCatchBlockSorter(int paramInt1, MethodVisitor paramMethodVisitor, int paramInt2, String paramString1, String paramString2, String paramString3, String[] paramArrayOfString) {
    super(paramInt1, paramInt2, paramString1, paramString2, paramString3, paramArrayOfString);
    this.mv = paramMethodVisitor;
  }
  
  public void visitEnd() {
    Comparator<TryCatchBlockNode> comparator = new Comparator<TryCatchBlockNode>() {
        public int compare(TryCatchBlockNode param1TryCatchBlockNode1, TryCatchBlockNode param1TryCatchBlockNode2) {
          int i = blockLength(param1TryCatchBlockNode1);
          int j = blockLength(param1TryCatchBlockNode2);
          return i - j;
        }
        
        private int blockLength(TryCatchBlockNode param1TryCatchBlockNode) {
          int i = TryCatchBlockSorter.this.instructions.indexOf(param1TryCatchBlockNode.start);
          int j = TryCatchBlockSorter.this.instructions.indexOf(param1TryCatchBlockNode.end);
          return j - i;
        }
      };
    Collections.sort(this.tryCatchBlocks, comparator);
    for (byte b = 0; b < this.tryCatchBlocks.size(); b++)
      ((TryCatchBlockNode)this.tryCatchBlocks.get(b)).updateIndex(b); 
    if (this.mv != null)
      accept(this.mv); 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\jdk\internal\org\objectweb\asm\commons\TryCatchBlockSorter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */