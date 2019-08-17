package java.awt.dnd;

public class DragSourceDragEvent extends DragSourceEvent {
  private static final long serialVersionUID = 481346297933902471L;
  
  private static final int JDK_1_3_MODIFIERS = 63;
  
  private static final int JDK_1_4_MODIFIERS = 16320;
  
  private int targetActions = 0;
  
  private int dropAction = 0;
  
  private int gestureModifiers = 0;
  
  private boolean invalidModifiers;
  
  public DragSourceDragEvent(DragSourceContext paramDragSourceContext, int paramInt1, int paramInt2, int paramInt3) {
    super(paramDragSourceContext);
    this.targetActions = paramInt2;
    this.gestureModifiers = paramInt3;
    this.dropAction = paramInt1;
    if ((paramInt3 & 0xFFFFC000) != 0) {
      this.invalidModifiers = true;
    } else if (getGestureModifiers() != 0 && getGestureModifiersEx() == 0) {
      setNewModifiers();
    } else if (getGestureModifiers() == 0 && getGestureModifiersEx() != 0) {
      setOldModifiers();
    } else {
      this.invalidModifiers = true;
    } 
  }
  
  public DragSourceDragEvent(DragSourceContext paramDragSourceContext, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5) {
    super(paramDragSourceContext, paramInt4, paramInt5);
    this.targetActions = paramInt2;
    this.gestureModifiers = paramInt3;
    this.dropAction = paramInt1;
    if ((paramInt3 & 0xFFFFC000) != 0) {
      this.invalidModifiers = true;
    } else if (getGestureModifiers() != 0 && getGestureModifiersEx() == 0) {
      setNewModifiers();
    } else if (getGestureModifiers() == 0 && getGestureModifiersEx() != 0) {
      setOldModifiers();
    } else {
      this.invalidModifiers = true;
    } 
  }
  
  public int getTargetActions() { return this.targetActions; }
  
  public int getGestureModifiers() { return this.invalidModifiers ? this.gestureModifiers : (this.gestureModifiers & 0x3F); }
  
  public int getGestureModifiersEx() { return this.invalidModifiers ? this.gestureModifiers : (this.gestureModifiers & 0x3FC0); }
  
  public int getUserAction() { return this.dropAction; }
  
  public int getDropAction() { return this.targetActions & getDragSourceContext().getSourceActions(); }
  
  private void setNewModifiers() {
    if ((this.gestureModifiers & 0x10) != 0)
      this.gestureModifiers |= 0x400; 
    if ((this.gestureModifiers & 0x8) != 0)
      this.gestureModifiers |= 0x800; 
    if ((this.gestureModifiers & 0x4) != 0)
      this.gestureModifiers |= 0x1000; 
    if ((this.gestureModifiers & true) != 0)
      this.gestureModifiers |= 0x40; 
    if ((this.gestureModifiers & 0x2) != 0)
      this.gestureModifiers |= 0x80; 
    if ((this.gestureModifiers & 0x20) != 0)
      this.gestureModifiers |= 0x2000; 
  }
  
  private void setOldModifiers() {
    if ((this.gestureModifiers & 0x400) != 0)
      this.gestureModifiers |= 0x10; 
    if ((this.gestureModifiers & 0x800) != 0)
      this.gestureModifiers |= 0x8; 
    if ((this.gestureModifiers & 0x1000) != 0)
      this.gestureModifiers |= 0x4; 
    if ((this.gestureModifiers & 0x40) != 0)
      this.gestureModifiers |= 0x1; 
    if ((this.gestureModifiers & 0x80) != 0)
      this.gestureModifiers |= 0x2; 
    if ((this.gestureModifiers & 0x2000) != 0)
      this.gestureModifiers |= 0x20; 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\awt\dnd\DragSourceDragEvent.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */