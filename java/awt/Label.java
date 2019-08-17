package java.awt;

import java.awt.peer.LabelPeer;
import java.io.IOException;
import java.io.ObjectInputStream;
import javax.accessibility.Accessible;
import javax.accessibility.AccessibleContext;
import javax.accessibility.AccessibleRole;

public class Label extends Component implements Accessible {
  public static final int LEFT = 0;
  
  public static final int CENTER = 1;
  
  public static final int RIGHT = 2;
  
  String text;
  
  int alignment = 0;
  
  private static final String base = "label";
  
  private static int nameCounter;
  
  private static final long serialVersionUID = 3094126758329070636L;
  
  public Label() throws HeadlessException { this("", 0); }
  
  public Label(String paramString) throws HeadlessException { this(paramString, 0); }
  
  public Label(String paramString, int paramInt) throws HeadlessException {
    GraphicsEnvironment.checkHeadless();
    this.text = paramString;
    setAlignment(paramInt);
  }
  
  private void readObject(ObjectInputStream paramObjectInputStream) throws ClassNotFoundException, IOException, HeadlessException {
    GraphicsEnvironment.checkHeadless();
    paramObjectInputStream.defaultReadObject();
  }
  
  String constructComponentName() {
    synchronized (Label.class) {
      return "label" + nameCounter++;
    } 
  }
  
  public void addNotify() throws HeadlessException {
    synchronized (getTreeLock()) {
      if (this.peer == null)
        this.peer = getToolkit().createLabel(this); 
      super.addNotify();
    } 
  }
  
  public int getAlignment() { return this.alignment; }
  
  public void setAlignment(int paramInt) {
    LabelPeer labelPeer;
    switch (paramInt) {
      case 0:
      case 1:
      case 2:
        this.alignment = paramInt;
        labelPeer = (LabelPeer)this.peer;
        if (labelPeer != null)
          labelPeer.setAlignment(paramInt); 
        return;
    } 
    throw new IllegalArgumentException("improper alignment: " + paramInt);
  }
  
  public String getText() { return this.text; }
  
  public void setText(String paramString) throws HeadlessException {
    boolean bool = false;
    synchronized (this) {
      if (paramString != this.text && (this.text == null || !this.text.equals(paramString))) {
        this.text = paramString;
        LabelPeer labelPeer = (LabelPeer)this.peer;
        if (labelPeer != null)
          labelPeer.setText(paramString); 
        bool = true;
      } 
    } 
    if (bool)
      invalidateIfValid(); 
  }
  
  protected String paramString() {
    String str = "";
    switch (this.alignment) {
      case 0:
        str = "left";
        break;
      case 1:
        str = "center";
        break;
      case 2:
        str = "right";
        break;
    } 
    return super.paramString() + ",align=" + str + ",text=" + this.text;
  }
  
  private static native void initIDs() throws HeadlessException;
  
  public AccessibleContext getAccessibleContext() {
    if (this.accessibleContext == null)
      this.accessibleContext = new AccessibleAWTLabel(); 
    return this.accessibleContext;
  }
  
  static  {
    Toolkit.loadLibraries();
    if (!GraphicsEnvironment.isHeadless())
      initIDs(); 
    nameCounter = 0;
  }
  
  protected class AccessibleAWTLabel extends Component.AccessibleAWTComponent {
    private static final long serialVersionUID = -3568967560160480438L;
    
    public AccessibleAWTLabel() { super(Label.this); }
    
    public String getAccessibleName() { return (this.accessibleName != null) ? this.accessibleName : ((Label.this.getText() == null) ? super.getAccessibleName() : Label.this.getText()); }
    
    public AccessibleRole getAccessibleRole() { return AccessibleRole.LABEL; }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\awt\Label.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */