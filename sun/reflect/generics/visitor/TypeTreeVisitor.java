package sun.reflect.generics.visitor;

import sun.reflect.generics.tree.ArrayTypeSignature;
import sun.reflect.generics.tree.BooleanSignature;
import sun.reflect.generics.tree.BottomSignature;
import sun.reflect.generics.tree.ByteSignature;
import sun.reflect.generics.tree.CharSignature;
import sun.reflect.generics.tree.ClassTypeSignature;
import sun.reflect.generics.tree.DoubleSignature;
import sun.reflect.generics.tree.FloatSignature;
import sun.reflect.generics.tree.FormalTypeParameter;
import sun.reflect.generics.tree.IntSignature;
import sun.reflect.generics.tree.LongSignature;
import sun.reflect.generics.tree.ShortSignature;
import sun.reflect.generics.tree.SimpleClassTypeSignature;
import sun.reflect.generics.tree.TypeVariableSignature;
import sun.reflect.generics.tree.VoidDescriptor;
import sun.reflect.generics.tree.Wildcard;

public interface TypeTreeVisitor<T> {
  T getResult();
  
  void visitFormalTypeParameter(FormalTypeParameter paramFormalTypeParameter);
  
  void visitClassTypeSignature(ClassTypeSignature paramClassTypeSignature);
  
  void visitArrayTypeSignature(ArrayTypeSignature paramArrayTypeSignature);
  
  void visitTypeVariableSignature(TypeVariableSignature paramTypeVariableSignature);
  
  void visitWildcard(Wildcard paramWildcard);
  
  void visitSimpleClassTypeSignature(SimpleClassTypeSignature paramSimpleClassTypeSignature);
  
  void visitBottomSignature(BottomSignature paramBottomSignature);
  
  void visitByteSignature(ByteSignature paramByteSignature);
  
  void visitBooleanSignature(BooleanSignature paramBooleanSignature);
  
  void visitShortSignature(ShortSignature paramShortSignature);
  
  void visitCharSignature(CharSignature paramCharSignature);
  
  void visitIntSignature(IntSignature paramIntSignature);
  
  void visitLongSignature(LongSignature paramLongSignature);
  
  void visitFloatSignature(FloatSignature paramFloatSignature);
  
  void visitDoubleSignature(DoubleSignature paramDoubleSignature);
  
  void visitVoidDescriptor(VoidDescriptor paramVoidDescriptor);
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\reflect\generics\visitor\TypeTreeVisitor.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */