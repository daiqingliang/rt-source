package java.awt;

import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.io.ObjectStreamField;
import java.io.Serializable;
import java.util.Enumeration;
import java.util.Hashtable;
import java.util.Vector;

public class CardLayout implements LayoutManager2, Serializable {
  private static final long serialVersionUID = -4328196481005934313L;
  
  Vector<Card> vector = new Vector();
  
  int currentCard = 0;
  
  int hgap;
  
  int vgap;
  
  private static final ObjectStreamField[] serialPersistentFields = { new ObjectStreamField("tab", Hashtable.class), new ObjectStreamField("hgap", int.class), new ObjectStreamField("vgap", int.class), new ObjectStreamField("vector", Vector.class), new ObjectStreamField("currentCard", int.class) };
  
  public CardLayout() { this(0, 0); }
  
  public CardLayout(int paramInt1, int paramInt2) {
    this.hgap = paramInt1;
    this.vgap = paramInt2;
  }
  
  public int getHgap() { return this.hgap; }
  
  public void setHgap(int paramInt) { this.hgap = paramInt; }
  
  public int getVgap() { return this.vgap; }
  
  public void setVgap(int paramInt) { this.vgap = paramInt; }
  
  public void addLayoutComponent(Component paramComponent, Object paramObject) {
    synchronized (paramComponent.getTreeLock()) {
      if (paramObject == null)
        paramObject = ""; 
      if (paramObject instanceof String) {
        addLayoutComponent((String)paramObject, paramComponent);
      } else {
        throw new IllegalArgumentException("cannot add to layout: constraint must be a string");
      } 
    } 
  }
  
  @Deprecated
  public void addLayoutComponent(String paramString, Component paramComponent) {
    synchronized (paramComponent.getTreeLock()) {
      if (!this.vector.isEmpty())
        paramComponent.setVisible(false); 
      for (byte b = 0; b < this.vector.size(); b++) {
        if (((Card)this.vector.get(b)).name.equals(paramString)) {
          ((Card)this.vector.get(b)).comp = paramComponent;
          return;
        } 
      } 
      this.vector.add(new Card(paramString, paramComponent));
    } 
  }
  
  public void removeLayoutComponent(Component paramComponent) {
    synchronized (paramComponent.getTreeLock()) {
      for (byte b = 0; b < this.vector.size(); b++) {
        if (((Card)this.vector.get(b)).comp == paramComponent) {
          if (paramComponent.isVisible() && paramComponent.getParent() != null)
            next(paramComponent.getParent()); 
          this.vector.remove(b);
          if (this.currentCard > b)
            this.currentCard--; 
          break;
        } 
      } 
    } 
  }
  
  public Dimension preferredLayoutSize(Container paramContainer) {
    synchronized (paramContainer.getTreeLock()) {
      Insets insets = paramContainer.getInsets();
      int i = paramContainer.getComponentCount();
      int j = 0;
      int k = 0;
      for (byte b = 0; b < i; b++) {
        Component component = paramContainer.getComponent(b);
        Dimension dimension = component.getPreferredSize();
        if (dimension.width > j)
          j = dimension.width; 
        if (dimension.height > k)
          k = dimension.height; 
      } 
      return new Dimension(insets.left + insets.right + j + this.hgap * 2, insets.top + insets.bottom + k + this.vgap * 2);
    } 
  }
  
  public Dimension minimumLayoutSize(Container paramContainer) {
    synchronized (paramContainer.getTreeLock()) {
      Insets insets = paramContainer.getInsets();
      int i = paramContainer.getComponentCount();
      int j = 0;
      int k = 0;
      for (byte b = 0; b < i; b++) {
        Component component = paramContainer.getComponent(b);
        Dimension dimension = component.getMinimumSize();
        if (dimension.width > j)
          j = dimension.width; 
        if (dimension.height > k)
          k = dimension.height; 
      } 
      return new Dimension(insets.left + insets.right + j + this.hgap * 2, insets.top + insets.bottom + k + this.vgap * 2);
    } 
  }
  
  public Dimension maximumLayoutSize(Container paramContainer) { return new Dimension(2147483647, 2147483647); }
  
  public float getLayoutAlignmentX(Container paramContainer) { return 0.5F; }
  
  public float getLayoutAlignmentY(Container paramContainer) { return 0.5F; }
  
  public void invalidateLayout(Container paramContainer) {}
  
  public void layoutContainer(Container paramContainer) {
    synchronized (paramContainer.getTreeLock()) {
      Insets insets = paramContainer.getInsets();
      int i = paramContainer.getComponentCount();
      Component component = null;
      boolean bool = false;
      for (byte b = 0; b < i; b++) {
        component = paramContainer.getComponent(b);
        component.setBounds(this.hgap + insets.left, this.vgap + insets.top, paramContainer.width - this.hgap * 2 + insets.left + insets.right, paramContainer.height - this.vgap * 2 + insets.top + insets.bottom);
        if (component.isVisible())
          bool = true; 
      } 
      if (!bool && i > 0)
        paramContainer.getComponent(0).setVisible(true); 
    } 
  }
  
  void checkLayout(Container paramContainer) {
    if (paramContainer.getLayout() != this)
      throw new IllegalArgumentException("wrong parent for CardLayout"); 
  }
  
  public void first(Container paramContainer) {
    synchronized (paramContainer.getTreeLock()) {
      checkLayout(paramContainer);
      int i = paramContainer.getComponentCount();
      for (byte b = 0; b < i; b++) {
        Component component = paramContainer.getComponent(b);
        if (component.isVisible()) {
          component.setVisible(false);
          break;
        } 
      } 
      if (i > 0) {
        this.currentCard = 0;
        paramContainer.getComponent(0).setVisible(true);
        paramContainer.validate();
      } 
    } 
  }
  
  public void next(Container paramContainer) {
    synchronized (paramContainer.getTreeLock()) {
      checkLayout(paramContainer);
      int i = paramContainer.getComponentCount();
      for (byte b = 0; b < i; b++) {
        Component component = paramContainer.getComponent(b);
        if (component.isVisible()) {
          component.setVisible(false);
          this.currentCard = (b + 1) % i;
          component = paramContainer.getComponent(this.currentCard);
          component.setVisible(true);
          paramContainer.validate();
          return;
        } 
      } 
      showDefaultComponent(paramContainer);
    } 
  }
  
  public void previous(Container paramContainer) {
    synchronized (paramContainer.getTreeLock()) {
      checkLayout(paramContainer);
      int i = paramContainer.getComponentCount();
      for (byte b = 0; b < i; b++) {
        Component component = paramContainer.getComponent(b);
        if (component.isVisible()) {
          component.setVisible(false);
          this.currentCard = (b > 0) ? (b - 1) : (i - 1);
          component = paramContainer.getComponent(this.currentCard);
          component.setVisible(true);
          paramContainer.validate();
          return;
        } 
      } 
      showDefaultComponent(paramContainer);
    } 
  }
  
  void showDefaultComponent(Container paramContainer) {
    if (paramContainer.getComponentCount() > 0) {
      this.currentCard = 0;
      paramContainer.getComponent(0).setVisible(true);
      paramContainer.validate();
    } 
  }
  
  public void last(Container paramContainer) {
    synchronized (paramContainer.getTreeLock()) {
      checkLayout(paramContainer);
      int i = paramContainer.getComponentCount();
      for (byte b = 0; b < i; b++) {
        Component component = paramContainer.getComponent(b);
        if (component.isVisible()) {
          component.setVisible(false);
          break;
        } 
      } 
      if (i > 0) {
        this.currentCard = i - 1;
        paramContainer.getComponent(this.currentCard).setVisible(true);
        paramContainer.validate();
      } 
    } 
  }
  
  public void show(Container paramContainer, String paramString) {
    synchronized (paramContainer.getTreeLock()) {
      checkLayout(paramContainer);
      Component component = null;
      int i = this.vector.size();
      byte b;
      for (b = 0; b < i; b++) {
        Card card = (Card)this.vector.get(b);
        if (card.name.equals(paramString)) {
          component = card.comp;
          this.currentCard = b;
          break;
        } 
      } 
      if (component != null && !component.isVisible()) {
        i = paramContainer.getComponentCount();
        for (b = 0; b < i; b++) {
          Component component1 = paramContainer.getComponent(b);
          if (component1.isVisible()) {
            component1.setVisible(false);
            break;
          } 
        } 
        component.setVisible(true);
        paramContainer.validate();
      } 
    } 
  }
  
  public String toString() { return getClass().getName() + "[hgap=" + this.hgap + ",vgap=" + this.vgap + "]"; }
  
  private void readObject(ObjectInputStream paramObjectInputStream) throws ClassNotFoundException, IOException {
    ObjectInputStream.GetField getField = paramObjectInputStream.readFields();
    this.hgap = getField.get("hgap", 0);
    this.vgap = getField.get("vgap", 0);
    if (getField.defaulted("vector")) {
      Hashtable hashtable = (Hashtable)getField.get("tab", null);
      this.vector = new Vector();
      if (hashtable != null && !hashtable.isEmpty()) {
        Enumeration enumeration = hashtable.keys();
        while (enumeration.hasMoreElements()) {
          String str = (String)enumeration.nextElement();
          Component component = (Component)hashtable.get(str);
          this.vector.add(new Card(str, component));
          if (component.isVisible())
            this.currentCard = this.vector.size() - 1; 
        } 
      } 
    } else {
      this.vector = (Vector)getField.get("vector", null);
      this.currentCard = getField.get("currentCard", 0);
    } 
  }
  
  private void writeObject(ObjectOutputStream paramObjectOutputStream) throws IOException {
    Hashtable hashtable = new Hashtable();
    int i = this.vector.size();
    for (byte b = 0; b < i; b++) {
      Card card = (Card)this.vector.get(b);
      hashtable.put(card.name, card.comp);
    } 
    ObjectOutputStream.PutField putField = paramObjectOutputStream.putFields();
    putField.put("hgap", this.hgap);
    putField.put("vgap", this.vgap);
    putField.put("vector", this.vector);
    putField.put("currentCard", this.currentCard);
    putField.put("tab", hashtable);
    paramObjectOutputStream.writeFields();
  }
  
  class Card implements Serializable {
    static final long serialVersionUID = 6640330810709497518L;
    
    public String name;
    
    public Component comp;
    
    public Card(String param1String, Component param1Component) {
      this.name = param1String;
      this.comp = param1Component;
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\awt\CardLayout.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */