package javax.swing.text;

import java.util.Enumeration;

public interface AttributeSet {
  public static final Object NameAttribute = StyleConstants.NameAttribute;
  
  public static final Object ResolveAttribute = StyleConstants.ResolveAttribute;
  
  int getAttributeCount();
  
  boolean isDefined(Object paramObject);
  
  boolean isEqual(AttributeSet paramAttributeSet);
  
  AttributeSet copyAttributes();
  
  Object getAttribute(Object paramObject);
  
  Enumeration<?> getAttributeNames();
  
  boolean containsAttribute(Object paramObject1, Object paramObject2);
  
  boolean containsAttributes(AttributeSet paramAttributeSet);
  
  AttributeSet getResolveParent();
  
  public static interface CharacterAttribute {}
  
  public static interface ColorAttribute {}
  
  public static interface FontAttribute {}
  
  public static interface ParagraphAttribute {}
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\text\AttributeSet.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */