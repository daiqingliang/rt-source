package javax.print.attribute;

import java.io.Serializable;

public final class AttributeSetUtilities {
  public static AttributeSet unmodifiableView(AttributeSet paramAttributeSet) {
    if (paramAttributeSet == null)
      throw new NullPointerException(); 
    return new UnmodifiableAttributeSet(paramAttributeSet);
  }
  
  public static DocAttributeSet unmodifiableView(DocAttributeSet paramDocAttributeSet) {
    if (paramDocAttributeSet == null)
      throw new NullPointerException(); 
    return new UnmodifiableDocAttributeSet(paramDocAttributeSet);
  }
  
  public static PrintRequestAttributeSet unmodifiableView(PrintRequestAttributeSet paramPrintRequestAttributeSet) {
    if (paramPrintRequestAttributeSet == null)
      throw new NullPointerException(); 
    return new UnmodifiablePrintRequestAttributeSet(paramPrintRequestAttributeSet);
  }
  
  public static PrintJobAttributeSet unmodifiableView(PrintJobAttributeSet paramPrintJobAttributeSet) {
    if (paramPrintJobAttributeSet == null)
      throw new NullPointerException(); 
    return new UnmodifiablePrintJobAttributeSet(paramPrintJobAttributeSet);
  }
  
  public static PrintServiceAttributeSet unmodifiableView(PrintServiceAttributeSet paramPrintServiceAttributeSet) {
    if (paramPrintServiceAttributeSet == null)
      throw new NullPointerException(); 
    return new UnmodifiablePrintServiceAttributeSet(paramPrintServiceAttributeSet);
  }
  
  public static AttributeSet synchronizedView(AttributeSet paramAttributeSet) {
    if (paramAttributeSet == null)
      throw new NullPointerException(); 
    return new SynchronizedAttributeSet(paramAttributeSet);
  }
  
  public static DocAttributeSet synchronizedView(DocAttributeSet paramDocAttributeSet) {
    if (paramDocAttributeSet == null)
      throw new NullPointerException(); 
    return new SynchronizedDocAttributeSet(paramDocAttributeSet);
  }
  
  public static PrintRequestAttributeSet synchronizedView(PrintRequestAttributeSet paramPrintRequestAttributeSet) {
    if (paramPrintRequestAttributeSet == null)
      throw new NullPointerException(); 
    return new SynchronizedPrintRequestAttributeSet(paramPrintRequestAttributeSet);
  }
  
  public static PrintJobAttributeSet synchronizedView(PrintJobAttributeSet paramPrintJobAttributeSet) {
    if (paramPrintJobAttributeSet == null)
      throw new NullPointerException(); 
    return new SynchronizedPrintJobAttributeSet(paramPrintJobAttributeSet);
  }
  
  public static PrintServiceAttributeSet synchronizedView(PrintServiceAttributeSet paramPrintServiceAttributeSet) {
    if (paramPrintServiceAttributeSet == null)
      throw new NullPointerException(); 
    return new SynchronizedPrintServiceAttributeSet(paramPrintServiceAttributeSet);
  }
  
  public static Class<?> verifyAttributeCategory(Object paramObject, Class<?> paramClass) {
    Class clazz = (Class)paramObject;
    if (paramClass.isAssignableFrom(clazz))
      return clazz; 
    throw new ClassCastException();
  }
  
  public static Attribute verifyAttributeValue(Object paramObject, Class<?> paramClass) {
    if (paramObject == null)
      throw new NullPointerException(); 
    if (paramClass.isInstance(paramObject))
      return (Attribute)paramObject; 
    throw new ClassCastException();
  }
  
  public static void verifyCategoryForValue(Class<?> paramClass, Attribute paramAttribute) {
    if (!paramClass.equals(paramAttribute.getCategory()))
      throw new IllegalArgumentException(); 
  }
  
  private static class SynchronizedAttributeSet implements AttributeSet, Serializable {
    private AttributeSet attrset;
    
    public SynchronizedAttributeSet(AttributeSet param1AttributeSet) { this.attrset = param1AttributeSet; }
    
    public Attribute get(Class<?> param1Class) { return this.attrset.get(param1Class); }
    
    public boolean add(Attribute param1Attribute) { return this.attrset.add(param1Attribute); }
    
    public boolean remove(Class<?> param1Class) { return this.attrset.remove(param1Class); }
    
    public boolean remove(Attribute param1Attribute) { return this.attrset.remove(param1Attribute); }
    
    public boolean containsKey(Class<?> param1Class) { return this.attrset.containsKey(param1Class); }
    
    public boolean containsValue(Attribute param1Attribute) { return this.attrset.containsValue(param1Attribute); }
    
    public boolean addAll(AttributeSet param1AttributeSet) { return this.attrset.addAll(param1AttributeSet); }
    
    public int size() { return this.attrset.size(); }
    
    public Attribute[] toArray() { return this.attrset.toArray(); }
    
    public void clear() { this.attrset.clear(); }
    
    public boolean isEmpty() { return this.attrset.isEmpty(); }
    
    public boolean equals(Object param1Object) { return this.attrset.equals(param1Object); }
    
    public int hashCode() { return this.attrset.hashCode(); }
  }
  
  private static class SynchronizedDocAttributeSet extends SynchronizedAttributeSet implements DocAttributeSet, Serializable {
    public SynchronizedDocAttributeSet(DocAttributeSet param1DocAttributeSet) { super(param1DocAttributeSet); }
  }
  
  private static class SynchronizedPrintJobAttributeSet extends SynchronizedAttributeSet implements PrintJobAttributeSet, Serializable {
    public SynchronizedPrintJobAttributeSet(PrintJobAttributeSet param1PrintJobAttributeSet) { super(param1PrintJobAttributeSet); }
  }
  
  private static class SynchronizedPrintRequestAttributeSet extends SynchronizedAttributeSet implements PrintRequestAttributeSet, Serializable {
    public SynchronizedPrintRequestAttributeSet(PrintRequestAttributeSet param1PrintRequestAttributeSet) { super(param1PrintRequestAttributeSet); }
  }
  
  private static class SynchronizedPrintServiceAttributeSet extends SynchronizedAttributeSet implements PrintServiceAttributeSet, Serializable {
    public SynchronizedPrintServiceAttributeSet(PrintServiceAttributeSet param1PrintServiceAttributeSet) { super(param1PrintServiceAttributeSet); }
  }
  
  private static class UnmodifiableAttributeSet implements AttributeSet, Serializable {
    private AttributeSet attrset;
    
    public UnmodifiableAttributeSet(AttributeSet param1AttributeSet) { this.attrset = param1AttributeSet; }
    
    public Attribute get(Class<?> param1Class) { return this.attrset.get(param1Class); }
    
    public boolean add(Attribute param1Attribute) { throw new UnmodifiableSetException(); }
    
    public boolean remove(Class<?> param1Class) { throw new UnmodifiableSetException(); }
    
    public boolean remove(Attribute param1Attribute) { throw new UnmodifiableSetException(); }
    
    public boolean containsKey(Class<?> param1Class) { return this.attrset.containsKey(param1Class); }
    
    public boolean containsValue(Attribute param1Attribute) { return this.attrset.containsValue(param1Attribute); }
    
    public boolean addAll(AttributeSet param1AttributeSet) { throw new UnmodifiableSetException(); }
    
    public int size() { return this.attrset.size(); }
    
    public Attribute[] toArray() { return this.attrset.toArray(); }
    
    public void clear() { throw new UnmodifiableSetException(); }
    
    public boolean isEmpty() { return this.attrset.isEmpty(); }
    
    public boolean equals(Object param1Object) { return this.attrset.equals(param1Object); }
    
    public int hashCode() { return this.attrset.hashCode(); }
  }
  
  private static class UnmodifiableDocAttributeSet extends UnmodifiableAttributeSet implements DocAttributeSet, Serializable {
    public UnmodifiableDocAttributeSet(DocAttributeSet param1DocAttributeSet) { super(param1DocAttributeSet); }
  }
  
  private static class UnmodifiablePrintJobAttributeSet extends UnmodifiableAttributeSet implements PrintJobAttributeSet, Serializable {
    public UnmodifiablePrintJobAttributeSet(PrintJobAttributeSet param1PrintJobAttributeSet) { super(param1PrintJobAttributeSet); }
  }
  
  private static class UnmodifiablePrintRequestAttributeSet extends UnmodifiableAttributeSet implements PrintRequestAttributeSet, Serializable {
    public UnmodifiablePrintRequestAttributeSet(PrintRequestAttributeSet param1PrintRequestAttributeSet) { super(param1PrintRequestAttributeSet); }
  }
  
  private static class UnmodifiablePrintServiceAttributeSet extends UnmodifiableAttributeSet implements PrintServiceAttributeSet, Serializable {
    public UnmodifiablePrintServiceAttributeSet(PrintServiceAttributeSet param1PrintServiceAttributeSet) { super(param1PrintServiceAttributeSet); }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\print\attribute\AttributeSetUtilities.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */