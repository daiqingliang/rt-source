package javax.print.attribute;

import java.io.Serializable;

public class HashDocAttributeSet extends HashAttributeSet implements DocAttributeSet, Serializable {
  private static final long serialVersionUID = -1128534486061432528L;
  
  public HashDocAttributeSet() { super(DocAttribute.class); }
  
  public HashDocAttributeSet(DocAttribute paramDocAttribute) { super(paramDocAttribute, DocAttribute.class); }
  
  public HashDocAttributeSet(DocAttribute[] paramArrayOfDocAttribute) { super(paramArrayOfDocAttribute, DocAttribute.class); }
  
  public HashDocAttributeSet(DocAttributeSet paramDocAttributeSet) { super(paramDocAttributeSet, DocAttribute.class); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\print\attribute\HashDocAttributeSet.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */