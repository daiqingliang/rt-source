package java.awt.font;

import java.awt.geom.AffineTransform;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamException;
import java.io.Serializable;

public final class TransformAttribute implements Serializable {
  private AffineTransform transform;
  
  public static final TransformAttribute IDENTITY = new TransformAttribute(null);
  
  static final long serialVersionUID = 3356247357827709530L;
  
  public TransformAttribute(AffineTransform paramAffineTransform) {
    if (paramAffineTransform != null && !paramAffineTransform.isIdentity())
      this.transform = new AffineTransform(paramAffineTransform); 
  }
  
  public AffineTransform getTransform() {
    AffineTransform affineTransform = this.transform;
    return (affineTransform == null) ? new AffineTransform() : new AffineTransform(affineTransform);
  }
  
  public boolean isIdentity() { return (this.transform == null); }
  
  private void writeObject(ObjectOutputStream paramObjectOutputStream) throws ClassNotFoundException, IOException {
    if (this.transform == null)
      this.transform = new AffineTransform(); 
    paramObjectOutputStream.defaultWriteObject();
  }
  
  private Object readResolve() throws ObjectStreamException { return (this.transform == null || this.transform.isIdentity()) ? IDENTITY : this; }
  
  public int hashCode() { return (this.transform == null) ? 0 : this.transform.hashCode(); }
  
  public boolean equals(Object paramObject) {
    if (paramObject != null)
      try {
        TransformAttribute transformAttribute = (TransformAttribute)paramObject;
        return (this.transform == null) ? ((transformAttribute.transform == null)) : this.transform.equals(transformAttribute.transform);
      } catch (ClassCastException classCastException) {} 
    return false;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\awt\font\TransformAttribute.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */