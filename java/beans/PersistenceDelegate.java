package java.beans;

public abstract class PersistenceDelegate {
  public void writeObject(Object paramObject, Encoder paramEncoder) {
    Object object = paramEncoder.get(paramObject);
    if (!mutatesTo(paramObject, object)) {
      paramEncoder.remove(paramObject);
      paramEncoder.writeExpression(instantiate(paramObject, paramEncoder));
    } else {
      initialize(paramObject.getClass(), paramObject, object, paramEncoder);
    } 
  }
  
  protected boolean mutatesTo(Object paramObject1, Object paramObject2) { return (paramObject2 != null && paramObject1 != null && paramObject1.getClass() == paramObject2.getClass()); }
  
  protected abstract Expression instantiate(Object paramObject, Encoder paramEncoder);
  
  protected void initialize(Class<?> paramClass, Object paramObject1, Object paramObject2, Encoder paramEncoder) {
    Class clazz = paramClass.getSuperclass();
    PersistenceDelegate persistenceDelegate = paramEncoder.getPersistenceDelegate(clazz);
    persistenceDelegate.initialize(clazz, paramObject1, paramObject2, paramEncoder);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\beans\PersistenceDelegate.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */