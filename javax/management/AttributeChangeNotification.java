package javax.management;

public class AttributeChangeNotification extends Notification {
  private static final long serialVersionUID = 535176054565814134L;
  
  public static final String ATTRIBUTE_CHANGE = "jmx.attribute.change";
  
  private String attributeName = null;
  
  private String attributeType = null;
  
  private Object oldValue = null;
  
  private Object newValue = null;
  
  public AttributeChangeNotification(Object paramObject1, long paramLong1, long paramLong2, String paramString1, String paramString2, String paramString3, Object paramObject2, Object paramObject3) {
    super("jmx.attribute.change", paramObject1, paramLong1, paramLong2, paramString1);
    this.attributeName = paramString2;
    this.attributeType = paramString3;
    this.oldValue = paramObject2;
    this.newValue = paramObject3;
  }
  
  public String getAttributeName() { return this.attributeName; }
  
  public String getAttributeType() { return this.attributeType; }
  
  public Object getOldValue() { return this.oldValue; }
  
  public Object getNewValue() { return this.newValue; }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\management\AttributeChangeNotification.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */