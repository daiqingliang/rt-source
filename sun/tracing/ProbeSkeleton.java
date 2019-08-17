package sun.tracing;

import com.sun.tracing.Probe;
import java.lang.reflect.Field;

public abstract class ProbeSkeleton implements Probe {
  protected Class<?>[] parameters;
  
  protected ProbeSkeleton(Class<?>[] paramArrayOfClass) { this.parameters = paramArrayOfClass; }
  
  public abstract boolean isEnabled();
  
  public abstract void uncheckedTrigger(Object[] paramArrayOfObject);
  
  private static boolean isAssignable(Object paramObject, Class<?> paramClass) {
    if (paramObject != null && !paramClass.isInstance(paramObject)) {
      if (paramClass.isPrimitive())
        try {
          Field field = paramObject.getClass().getField("TYPE");
          return paramClass.isAssignableFrom((Class)field.get(null));
        } catch (Exception exception) {} 
      return false;
    } 
    return true;
  }
  
  public void trigger(Object... paramVarArgs) {
    if (paramVarArgs.length != this.parameters.length)
      throw new IllegalArgumentException("Wrong number of arguments"); 
    for (byte b = 0; b < this.parameters.length; b++) {
      if (!isAssignable(paramVarArgs[b], this.parameters[b]))
        throw new IllegalArgumentException("Wrong type of argument at position " + b); 
    } 
    uncheckedTrigger(paramVarArgs);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\tracing\ProbeSkeleton.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */