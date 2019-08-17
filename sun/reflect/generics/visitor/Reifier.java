package sun.reflect.generics.visitor;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Iterator;
import java.util.List;
import sun.reflect.generics.factory.GenericsFactory;
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
import sun.reflect.generics.tree.TypeArgument;
import sun.reflect.generics.tree.TypeVariableSignature;
import sun.reflect.generics.tree.VoidDescriptor;
import sun.reflect.generics.tree.Wildcard;

public class Reifier extends Object implements TypeTreeVisitor<Type> {
  private Type resultType;
  
  private GenericsFactory factory;
  
  private Reifier(GenericsFactory paramGenericsFactory) { this.factory = paramGenericsFactory; }
  
  private GenericsFactory getFactory() { return this.factory; }
  
  public static Reifier make(GenericsFactory paramGenericsFactory) { return new Reifier(paramGenericsFactory); }
  
  private Type[] reifyTypeArguments(TypeArgument[] paramArrayOfTypeArgument) {
    Type[] arrayOfType = new Type[paramArrayOfTypeArgument.length];
    for (byte b = 0; b < paramArrayOfTypeArgument.length; b++) {
      paramArrayOfTypeArgument[b].accept(this);
      arrayOfType[b] = this.resultType;
    } 
    return arrayOfType;
  }
  
  public Type getResult() {
    assert this.resultType != null;
    return this.resultType;
  }
  
  public void visitFormalTypeParameter(FormalTypeParameter paramFormalTypeParameter) { this.resultType = getFactory().makeTypeVariable(paramFormalTypeParameter.getName(), paramFormalTypeParameter.getBounds()); }
  
  public void visitClassTypeSignature(ClassTypeSignature paramClassTypeSignature) {
    List list = paramClassTypeSignature.getPath();
    assert !list.isEmpty();
    Iterator iterator = list.iterator();
    SimpleClassTypeSignature simpleClassTypeSignature = (SimpleClassTypeSignature)iterator.next();
    StringBuilder stringBuilder = new StringBuilder(simpleClassTypeSignature.getName());
    boolean bool = simpleClassTypeSignature.getDollar();
    while (iterator.hasNext() && simpleClassTypeSignature.getTypeArguments().length == 0) {
      simpleClassTypeSignature = (SimpleClassTypeSignature)iterator.next();
      bool = simpleClassTypeSignature.getDollar();
      stringBuilder.append(bool ? "$" : ".").append(simpleClassTypeSignature.getName());
    } 
    assert !iterator.hasNext() || simpleClassTypeSignature.getTypeArguments().length > 0;
    Type type = getFactory().makeNamedType(stringBuilder.toString());
    if (simpleClassTypeSignature.getTypeArguments().length == 0) {
      assert !iterator.hasNext();
      this.resultType = type;
    } else {
      assert simpleClassTypeSignature.getTypeArguments().length > 0;
      Type[] arrayOfType = reifyTypeArguments(simpleClassTypeSignature.getTypeArguments());
      ParameterizedType parameterizedType = getFactory().makeParameterizedType(type, arrayOfType, null);
      bool = false;
      while (iterator.hasNext()) {
        simpleClassTypeSignature = (SimpleClassTypeSignature)iterator.next();
        bool = simpleClassTypeSignature.getDollar();
        stringBuilder.append(bool ? "$" : ".").append(simpleClassTypeSignature.getName());
        type = getFactory().makeNamedType(stringBuilder.toString());
        arrayOfType = reifyTypeArguments(simpleClassTypeSignature.getTypeArguments());
        parameterizedType = getFactory().makeParameterizedType(type, arrayOfType, parameterizedType);
      } 
      this.resultType = parameterizedType;
    } 
  }
  
  public void visitArrayTypeSignature(ArrayTypeSignature paramArrayTypeSignature) {
    paramArrayTypeSignature.getComponentType().accept(this);
    Type type = this.resultType;
    this.resultType = getFactory().makeArrayType(type);
  }
  
  public void visitTypeVariableSignature(TypeVariableSignature paramTypeVariableSignature) { this.resultType = getFactory().findTypeVariable(paramTypeVariableSignature.getIdentifier()); }
  
  public void visitWildcard(Wildcard paramWildcard) { this.resultType = getFactory().makeWildcard(paramWildcard.getUpperBounds(), paramWildcard.getLowerBounds()); }
  
  public void visitSimpleClassTypeSignature(SimpleClassTypeSignature paramSimpleClassTypeSignature) { this.resultType = getFactory().makeNamedType(paramSimpleClassTypeSignature.getName()); }
  
  public void visitBottomSignature(BottomSignature paramBottomSignature) {}
  
  public void visitByteSignature(ByteSignature paramByteSignature) { this.resultType = getFactory().makeByte(); }
  
  public void visitBooleanSignature(BooleanSignature paramBooleanSignature) { this.resultType = getFactory().makeBool(); }
  
  public void visitShortSignature(ShortSignature paramShortSignature) { this.resultType = getFactory().makeShort(); }
  
  public void visitCharSignature(CharSignature paramCharSignature) { this.resultType = getFactory().makeChar(); }
  
  public void visitIntSignature(IntSignature paramIntSignature) { this.resultType = getFactory().makeInt(); }
  
  public void visitLongSignature(LongSignature paramLongSignature) { this.resultType = getFactory().makeLong(); }
  
  public void visitFloatSignature(FloatSignature paramFloatSignature) { this.resultType = getFactory().makeFloat(); }
  
  public void visitDoubleSignature(DoubleSignature paramDoubleSignature) { this.resultType = getFactory().makeDouble(); }
  
  public void visitVoidDescriptor(VoidDescriptor paramVoidDescriptor) { this.resultType = getFactory().makeVoid(); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\reflect\generics\visitor\Reifier.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */