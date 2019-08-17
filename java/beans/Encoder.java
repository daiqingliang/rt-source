package java.beans;

import com.sun.beans.finder.PersistenceDelegateFinder;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.Map;

public class Encoder {
  private final PersistenceDelegateFinder finder = new PersistenceDelegateFinder();
  
  private Map<Object, Expression> bindings = new IdentityHashMap();
  
  private ExceptionListener exceptionListener;
  
  boolean executeStatements = true;
  
  private Map<Object, Object> attributes;
  
  protected void writeObject(Object paramObject) {
    if (paramObject == this)
      return; 
    PersistenceDelegate persistenceDelegate = getPersistenceDelegate((paramObject == null) ? null : paramObject.getClass());
    persistenceDelegate.writeObject(paramObject, this);
  }
  
  public void setExceptionListener(ExceptionListener paramExceptionListener) { this.exceptionListener = paramExceptionListener; }
  
  public ExceptionListener getExceptionListener() { return (this.exceptionListener != null) ? this.exceptionListener : Statement.defaultExceptionListener; }
  
  Object getValue(Expression paramExpression) {
    try {
      return (paramExpression == null) ? null : paramExpression.getValue();
    } catch (Exception exception) {
      getExceptionListener().exceptionThrown(exception);
      throw new RuntimeException("failed to evaluate: " + paramExpression.toString());
    } 
  }
  
  public PersistenceDelegate getPersistenceDelegate(Class<?> paramClass) {
    PersistenceDelegate persistenceDelegate = this.finder.find(paramClass);
    if (persistenceDelegate == null) {
      persistenceDelegate = MetaData.getPersistenceDelegate(paramClass);
      if (persistenceDelegate != null)
        this.finder.register(paramClass, persistenceDelegate); 
    } 
    return persistenceDelegate;
  }
  
  public void setPersistenceDelegate(Class<?> paramClass, PersistenceDelegate paramPersistenceDelegate) { this.finder.register(paramClass, paramPersistenceDelegate); }
  
  public Object remove(Object paramObject) {
    Expression expression = (Expression)this.bindings.remove(paramObject);
    return getValue(expression);
  }
  
  public Object get(Object paramObject) {
    if (paramObject == null || paramObject == this || paramObject.getClass() == String.class)
      return paramObject; 
    Expression expression = (Expression)this.bindings.get(paramObject);
    return getValue(expression);
  }
  
  private Object writeObject1(Object paramObject) {
    Object object = get(paramObject);
    if (object == null) {
      writeObject(paramObject);
      object = get(paramObject);
    } 
    return object;
  }
  
  private Statement cloneStatement(Statement paramStatement) {
    Object object1 = paramStatement.getTarget();
    Object object2 = writeObject1(object1);
    Object[] arrayOfObject1 = paramStatement.getArguments();
    Object[] arrayOfObject2 = new Object[arrayOfObject1.length];
    for (byte b = 0; b < arrayOfObject1.length; b++)
      arrayOfObject2[b] = writeObject1(arrayOfObject1[b]); 
    Statement statement = Statement.class.equals(paramStatement.getClass()) ? new Statement(object2, paramStatement.getMethodName(), arrayOfObject2) : new Expression(object2, paramStatement.getMethodName(), arrayOfObject2);
    statement.loader = paramStatement.loader;
    return statement;
  }
  
  public void writeStatement(Statement paramStatement) {
    Statement statement = cloneStatement(paramStatement);
    if (paramStatement.getTarget() != this && this.executeStatements)
      try {
        statement.execute();
      } catch (Exception exception) {
        getExceptionListener().exceptionThrown(new Exception("Encoder: discarding statement " + statement, exception));
      }  
  }
  
  public void writeExpression(Expression paramExpression) {
    Object object = getValue(paramExpression);
    if (get(object) != null)
      return; 
    this.bindings.put(object, (Expression)cloneStatement(paramExpression));
    writeObject(object);
  }
  
  void clear() { this.bindings.clear(); }
  
  void setAttribute(Object paramObject1, Object paramObject2) {
    if (this.attributes == null)
      this.attributes = new HashMap(); 
    this.attributes.put(paramObject1, paramObject2);
  }
  
  Object getAttribute(Object paramObject) { return (this.attributes == null) ? null : this.attributes.get(paramObject); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\beans\Encoder.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */