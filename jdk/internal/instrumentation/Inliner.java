package jdk.internal.instrumentation;

import java.lang.reflect.Method;
import java.util.List;
import jdk.internal.org.objectweb.asm.ClassReader;
import jdk.internal.org.objectweb.asm.ClassVisitor;
import jdk.internal.org.objectweb.asm.MethodVisitor;
import jdk.internal.org.objectweb.asm.Type;
import jdk.internal.org.objectweb.asm.tree.ClassNode;
import jdk.internal.org.objectweb.asm.tree.MethodNode;

final class Inliner extends ClassVisitor {
  private final String instrumentationClassName;
  
  private final Logger logger;
  
  private final ClassNode targetClassNode;
  
  private final List<Method> instrumentationMethods;
  
  private final MaxLocalsTracker maxLocalsTracker;
  
  Inliner(int paramInt, ClassVisitor paramClassVisitor, String paramString, ClassReader paramClassReader, List<Method> paramList, MaxLocalsTracker paramMaxLocalsTracker, Logger paramLogger) {
    super(paramInt, paramClassVisitor);
    this.instrumentationClassName = paramString;
    this.instrumentationMethods = paramList;
    this.maxLocalsTracker = paramMaxLocalsTracker;
    this.logger = paramLogger;
    ClassNode classNode = new ClassNode(327680);
    paramClassReader.accept(classNode, 8);
    this.targetClassNode = classNode;
  }
  
  public MethodVisitor visitMethod(int paramInt, String paramString1, String paramString2, String paramString3, String[] paramArrayOfString) {
    MethodVisitor methodVisitor = super.visitMethod(paramInt, paramString1, paramString2, paramString3, paramArrayOfString);
    if (isInstrumentationMethod(paramString1, paramString2)) {
      MethodNode methodNode = findTargetMethodNode(paramString1, paramString2);
      if (methodNode == null)
        throw new IllegalArgumentException("Could not find the method to instrument in the target class"); 
      if ((methodNode.access & 0x100) == 1)
        throw new IllegalArgumentException("Cannot instrument native methods: " + this.targetClassNode.name + "." + methodNode.name + methodNode.desc); 
      this.logger.trace("Inliner processing method " + paramString1 + paramString2);
      return new MethodCallInliner(paramInt, paramString2, methodVisitor, methodNode, this.instrumentationClassName, this.maxLocalsTracker.getMaxLocals(paramString1, paramString2), this.logger);
    } 
    return methodVisitor;
  }
  
  private boolean isInstrumentationMethod(String paramString1, String paramString2) {
    for (Method method : this.instrumentationMethods) {
      if (method.getName().equals(paramString1) && Type.getMethodDescriptor(method).equals(paramString2))
        return true; 
    } 
    return false;
  }
  
  private MethodNode findTargetMethodNode(String paramString1, String paramString2) {
    for (MethodNode methodNode : this.targetClassNode.methods) {
      if (methodNode.desc.equals(paramString2) && methodNode.name.equals(paramString1))
        return methodNode; 
    } 
    throw new IllegalArgumentException("could not find MethodNode for " + paramString1 + paramString2);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\jdk\internal\instrumentation\Inliner.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */