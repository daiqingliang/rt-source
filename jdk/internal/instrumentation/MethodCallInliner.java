package jdk.internal.instrumentation;

import java.util.ArrayList;
import java.util.List;
import jdk.internal.org.objectweb.asm.Label;
import jdk.internal.org.objectweb.asm.MethodVisitor;
import jdk.internal.org.objectweb.asm.tree.MethodNode;

final class MethodCallInliner extends MethodVisitor {
  private final String newClass;
  
  private final MethodNode inlineTarget;
  
  private final List<CatchBlock> blocks = new ArrayList();
  
  private boolean inlining;
  
  private final Logger logger;
  
  private final int maxLocals;
  
  public MethodCallInliner(int paramInt1, String paramString1, MethodVisitor paramMethodVisitor, MethodNode paramMethodNode, String paramString2, int paramInt2, Logger paramLogger) {
    super(327680, paramMethodVisitor);
    this.newClass = paramString2;
    this.inlineTarget = paramMethodNode;
    this.logger = paramLogger;
    this.maxLocals = paramInt2;
    paramLogger.trace("MethodCallInliner: targetMethod=" + paramString2 + "." + paramMethodNode.name + paramMethodNode.desc);
  }
  
  public void visitMethodInsn(int paramInt, String paramString1, String paramString2, String paramString3, boolean paramBoolean) {
    if (!shouldBeInlined(paramString1, paramString2, paramString3)) {
      this.mv.visitMethodInsn(paramInt, paramString1, paramString2, paramString3, paramBoolean);
      return;
    } 
    this.logger.trace("Inlining call to " + paramString2 + paramString3);
    Label label = new Label();
    this.inlining = true;
    this.inlineTarget.instructions.resetLabels();
    MethodInliningAdapter methodInliningAdapter = new MethodInliningAdapter(this, label, (paramInt == 184) ? 8 : 0, paramString3, this.maxLocals);
    this.inlineTarget.accept(methodInliningAdapter);
    this.logger.trace("Inlining done");
    this.inlining = false;
    visitLabel(label);
  }
  
  private boolean shouldBeInlined(String paramString1, String paramString2, String paramString3) { return (this.inlineTarget.desc.equals(paramString3) && this.inlineTarget.name.equals(paramString2) && paramString1.equals(this.newClass.replace('.', '/'))); }
  
  public void visitTryCatchBlock(Label paramLabel1, Label paramLabel2, Label paramLabel3, String paramString) {
    if (!this.inlining) {
      this.blocks.add(new CatchBlock(paramLabel1, paramLabel2, paramLabel3, paramString));
    } else {
      super.visitTryCatchBlock(paramLabel1, paramLabel2, paramLabel3, paramString);
    } 
  }
  
  public void visitMaxs(int paramInt1, int paramInt2) {
    for (CatchBlock catchBlock : this.blocks)
      super.visitTryCatchBlock(catchBlock.start, catchBlock.end, catchBlock.handler, catchBlock.type); 
    super.visitMaxs(paramInt1, paramInt2);
  }
  
  static final class CatchBlock {
    final Label start;
    
    final Label end;
    
    final Label handler;
    
    final String type;
    
    CatchBlock(Label param1Label1, Label param1Label2, Label param1Label3, String param1String) {
      this.start = param1Label1;
      this.end = param1Label2;
      this.handler = param1Label3;
      this.type = param1String;
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\jdk\internal\instrumentation\MethodCallInliner.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */