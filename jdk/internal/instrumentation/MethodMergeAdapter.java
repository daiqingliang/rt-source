package jdk.internal.instrumentation;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import jdk.internal.org.objectweb.asm.ClassVisitor;
import jdk.internal.org.objectweb.asm.Handle;
import jdk.internal.org.objectweb.asm.MethodVisitor;
import jdk.internal.org.objectweb.asm.Type;
import jdk.internal.org.objectweb.asm.commons.RemappingMethodAdapter;
import jdk.internal.org.objectweb.asm.commons.SimpleRemapper;
import jdk.internal.org.objectweb.asm.tree.ClassNode;
import jdk.internal.org.objectweb.asm.tree.MethodNode;

final class MethodMergeAdapter extends ClassVisitor {
  private final ClassNode cn;
  
  private final List<Method> methodFilter;
  
  private final Map<String, String> typeMap;
  
  private final Logger logger;
  
  public MethodMergeAdapter(ClassVisitor paramClassVisitor, ClassNode paramClassNode, List<Method> paramList, TypeMapping[] paramArrayOfTypeMapping, Logger paramLogger) {
    super(327680, paramClassVisitor);
    this.cn = paramClassNode;
    this.methodFilter = paramList;
    this.logger = paramLogger;
    this.typeMap = new HashMap();
    for (TypeMapping typeMapping : paramArrayOfTypeMapping)
      this.typeMap.put(typeMapping.from().replace('.', '/'), typeMapping.to().replace('.', '/')); 
  }
  
  public void visit(int paramInt1, int paramInt2, String paramString1, String paramString2, String paramString3, String[] paramArrayOfString) {
    super.visit(paramInt1, paramInt2, paramString1, paramString2, paramString3, paramArrayOfString);
    this.typeMap.put(this.cn.name, paramString1);
  }
  
  public MethodVisitor visitMethod(int paramInt, String paramString1, String paramString2, String paramString3, String[] paramArrayOfString) {
    if (methodInFilter(paramString1, paramString2)) {
      this.logger.trace("Deleting " + paramString1 + paramString2);
      return null;
    } 
    return super.visitMethod(paramInt, paramString1, paramString2, paramString3, paramArrayOfString);
  }
  
  public void visitEnd() {
    SimpleRemapper simpleRemapper = new SimpleRemapper(this.typeMap);
    LinkedList linkedList = new LinkedList();
    for (MethodNode methodNode : this.cn.methods) {
      if (methodInFilter(methodNode.name, methodNode.desc))
        linkedList.add(methodNode); 
    } 
    while (!linkedList.isEmpty()) {
      MethodNode methodNode = (MethodNode)linkedList.remove(0);
      this.logger.trace("Copying method: " + methodNode.name + methodNode.desc);
      this.logger.trace("   with mapper: " + this.typeMap);
      String[] arrayOfString = (String[])methodNode.exceptions.toArray(new String[0]);
      MethodVisitor methodVisitor = this.cv.visitMethod(methodNode.access, methodNode.name, methodNode.desc, methodNode.signature, arrayOfString);
      methodNode.instructions.resetLabels();
      methodNode.accept(new RemappingMethodAdapter(methodNode.access, methodNode.desc, methodVisitor, simpleRemapper));
      findMethodsReferencedByInvokeDynamic(methodNode, linkedList);
    } 
    super.visitEnd();
  }
  
  private void findMethodsReferencedByInvokeDynamic(final MethodNode mn, final List<MethodNode> toCopy) { paramMethodNode.accept(new MethodVisitor(327680) {
          public void visitInvokeDynamicInsn(String param1String1, String param1String2, Handle param1Handle, Object... param1VarArgs) {
            for (Object object : param1VarArgs) {
              if (object instanceof Handle) {
                Handle handle = (Handle)object;
                MethodNode methodNode = MethodMergeAdapter.findMethod(MethodMergeAdapter.this.cn, handle);
                if (methodNode == null)
                  MethodMergeAdapter.this.logger.error("Could not find method " + handle.getName() + handle.getDesc() + " referenced from an invokedynamic in " + this.val$mn.name + this.val$mn.desc + " while processing class " + this.this$0.cn.name); 
                MethodMergeAdapter.this.logger.trace("Adding method referenced from invokedynamic " + methodNode.name + methodNode.desc + " to the list of methods to be copied from " + this.this$0.cn.name);
                toCopy.add(methodNode);
              } 
            } 
          }
        }); }
  
  private static MethodNode findMethod(ClassNode paramClassNode, Handle paramHandle) {
    for (MethodNode methodNode : paramClassNode.methods) {
      if (methodNode.name.equals(paramHandle.getName()) && methodNode.desc.equals(paramHandle.getDesc()))
        return methodNode; 
    } 
    return null;
  }
  
  private boolean methodInFilter(String paramString1, String paramString2) {
    for (Method method : this.methodFilter) {
      if (method.getName().equals(paramString1) && Type.getMethodDescriptor(method).equals(paramString2))
        return true; 
    } 
    return false;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\jdk\internal\instrumentation\MethodMergeAdapter.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */