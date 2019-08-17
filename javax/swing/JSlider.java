package javax.swing;

import java.awt.Color;
import java.awt.Component;
import java.awt.Font;
import java.awt.Image;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.io.Serializable;
import java.util.Dictionary;
import java.util.Enumeration;
import java.util.Hashtable;
import javax.accessibility.Accessible;
import javax.accessibility.AccessibleContext;
import javax.accessibility.AccessibleRole;
import javax.accessibility.AccessibleState;
import javax.accessibility.AccessibleStateSet;
import javax.accessibility.AccessibleValue;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.plaf.SliderUI;
import javax.swing.plaf.UIResource;

public class JSlider extends JComponent implements SwingConstants, Accessible {
  private static final String uiClassID = "SliderUI";
  
  private boolean paintTicks = false;
  
  private boolean paintTrack = true;
  
  private boolean paintLabels = false;
  
  private boolean isInverted = false;
  
  protected BoundedRangeModel sliderModel;
  
  protected int majorTickSpacing;
  
  protected int minorTickSpacing;
  
  protected boolean snapToTicks = false;
  
  boolean snapToValue = true;
  
  protected int orientation;
  
  private Dictionary labelTable;
  
  protected ChangeListener changeListener = createChangeListener();
  
  protected ChangeEvent changeEvent = null;
  
  private void checkOrientation(int paramInt) {
    switch (paramInt) {
      case 0:
      case 1:
        return;
    } 
    throw new IllegalArgumentException("orientation must be one of: VERTICAL, HORIZONTAL");
  }
  
  public JSlider() { this(0, 0, 100, 50); }
  
  public JSlider(int paramInt) { this(paramInt, 0, 100, 50); }
  
  public JSlider(int paramInt1, int paramInt2) { this(0, paramInt1, paramInt2, (paramInt1 + paramInt2) / 2); }
  
  public JSlider(int paramInt1, int paramInt2, int paramInt3) { this(0, paramInt1, paramInt2, paramInt3); }
  
  public JSlider(int paramInt1, int paramInt2, int paramInt3, int paramInt4) {
    checkOrientation(paramInt1);
    this.orientation = paramInt1;
    setModel(new DefaultBoundedRangeModel(paramInt4, 0, paramInt2, paramInt3));
    updateUI();
  }
  
  public JSlider(BoundedRangeModel paramBoundedRangeModel) {
    this.orientation = 0;
    setModel(paramBoundedRangeModel);
    updateUI();
  }
  
  public SliderUI getUI() { return (SliderUI)this.ui; }
  
  public void setUI(SliderUI paramSliderUI) { setUI(paramSliderUI); }
  
  public void updateUI() {
    setUI((SliderUI)UIManager.getUI(this));
    updateLabelUIs();
  }
  
  public String getUIClassID() { return "SliderUI"; }
  
  protected ChangeListener createChangeListener() { return new ModelListener(null); }
  
  public void addChangeListener(ChangeListener paramChangeListener) { this.listenerList.add(ChangeListener.class, paramChangeListener); }
  
  public void removeChangeListener(ChangeListener paramChangeListener) { this.listenerList.remove(ChangeListener.class, paramChangeListener); }
  
  public ChangeListener[] getChangeListeners() { return (ChangeListener[])this.listenerList.getListeners(ChangeListener.class); }
  
  protected void fireStateChanged() {
    Object[] arrayOfObject = this.listenerList.getListenerList();
    for (int i = arrayOfObject.length - 2; i >= 0; i -= 2) {
      if (arrayOfObject[i] == ChangeListener.class) {
        if (this.changeEvent == null)
          this.changeEvent = new ChangeEvent(this); 
        ((ChangeListener)arrayOfObject[i + 1]).stateChanged(this.changeEvent);
      } 
    } 
  }
  
  public BoundedRangeModel getModel() { return this.sliderModel; }
  
  public void setModel(BoundedRangeModel paramBoundedRangeModel) {
    BoundedRangeModel boundedRangeModel = getModel();
    if (boundedRangeModel != null)
      boundedRangeModel.removeChangeListener(this.changeListener); 
    this.sliderModel = paramBoundedRangeModel;
    if (paramBoundedRangeModel != null)
      paramBoundedRangeModel.addChangeListener(this.changeListener); 
    if (this.accessibleContext != null)
      this.accessibleContext.firePropertyChange("AccessibleValue", (boundedRangeModel == null) ? null : Integer.valueOf(boundedRangeModel.getValue()), (paramBoundedRangeModel == null) ? null : Integer.valueOf(paramBoundedRangeModel.getValue())); 
    firePropertyChange("model", boundedRangeModel, this.sliderModel);
  }
  
  public int getValue() { return getModel().getValue(); }
  
  public void setValue(int paramInt) {
    BoundedRangeModel boundedRangeModel = getModel();
    int i = boundedRangeModel.getValue();
    if (i == paramInt)
      return; 
    boundedRangeModel.setValue(paramInt);
    if (this.accessibleContext != null)
      this.accessibleContext.firePropertyChange("AccessibleValue", Integer.valueOf(i), Integer.valueOf(boundedRangeModel.getValue())); 
  }
  
  public int getMinimum() { return getModel().getMinimum(); }
  
  public void setMinimum(int paramInt) {
    int i = getModel().getMinimum();
    getModel().setMinimum(paramInt);
    firePropertyChange("minimum", Integer.valueOf(i), Integer.valueOf(paramInt));
  }
  
  public int getMaximum() { return getModel().getMaximum(); }
  
  public void setMaximum(int paramInt) {
    int i = getModel().getMaximum();
    getModel().setMaximum(paramInt);
    firePropertyChange("maximum", Integer.valueOf(i), Integer.valueOf(paramInt));
  }
  
  public boolean getValueIsAdjusting() { return getModel().getValueIsAdjusting(); }
  
  public void setValueIsAdjusting(boolean paramBoolean) {
    BoundedRangeModel boundedRangeModel = getModel();
    boolean bool = boundedRangeModel.getValueIsAdjusting();
    boundedRangeModel.setValueIsAdjusting(paramBoolean);
    if (bool != paramBoolean && this.accessibleContext != null)
      this.accessibleContext.firePropertyChange("AccessibleState", bool ? AccessibleState.BUSY : null, paramBoolean ? AccessibleState.BUSY : null); 
  }
  
  public int getExtent() { return getModel().getExtent(); }
  
  public void setExtent(int paramInt) { getModel().setExtent(paramInt); }
  
  public int getOrientation() { return this.orientation; }
  
  public void setOrientation(int paramInt) {
    checkOrientation(paramInt);
    int i = this.orientation;
    this.orientation = paramInt;
    firePropertyChange("orientation", i, paramInt);
    if (i != paramInt && this.accessibleContext != null)
      this.accessibleContext.firePropertyChange("AccessibleState", (i == 1) ? AccessibleState.VERTICAL : AccessibleState.HORIZONTAL, (paramInt == 1) ? AccessibleState.VERTICAL : AccessibleState.HORIZONTAL); 
    if (paramInt != i)
      revalidate(); 
  }
  
  public void setFont(Font paramFont) {
    super.setFont(paramFont);
    updateLabelSizes();
  }
  
  public boolean imageUpdate(Image paramImage, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5) {
    if (!isShowing())
      return false; 
    Enumeration enumeration = this.labelTable.elements();
    while (enumeration.hasMoreElements()) {
      Component component = (Component)enumeration.nextElement();
      if (component instanceof JLabel) {
        JLabel jLabel = (JLabel)component;
        if (SwingUtilities.doesIconReferenceImage(jLabel.getIcon(), paramImage) || SwingUtilities.doesIconReferenceImage(jLabel.getDisabledIcon(), paramImage))
          return super.imageUpdate(paramImage, paramInt1, paramInt2, paramInt3, paramInt4, paramInt5); 
      } 
    } 
    return false;
  }
  
  public Dictionary getLabelTable() { return this.labelTable; }
  
  public void setLabelTable(Dictionary paramDictionary) {
    Dictionary dictionary = this.labelTable;
    this.labelTable = paramDictionary;
    updateLabelUIs();
    firePropertyChange("labelTable", dictionary, this.labelTable);
    if (paramDictionary != dictionary) {
      revalidate();
      repaint();
    } 
  }
  
  protected void updateLabelUIs() {
    Dictionary dictionary = getLabelTable();
    if (dictionary == null)
      return; 
    Enumeration enumeration = dictionary.keys();
    while (enumeration.hasMoreElements()) {
      JComponent jComponent = (JComponent)dictionary.get(enumeration.nextElement());
      jComponent.updateUI();
      jComponent.setSize(jComponent.getPreferredSize());
    } 
  }
  
  private void updateLabelSizes() {
    Dictionary dictionary = getLabelTable();
    if (dictionary != null) {
      Enumeration enumeration = dictionary.elements();
      while (enumeration.hasMoreElements()) {
        JComponent jComponent = (JComponent)enumeration.nextElement();
        jComponent.setSize(jComponent.getPreferredSize());
      } 
    } 
  }
  
  public Hashtable createStandardLabels(int paramInt) { return createStandardLabels(paramInt, getMinimum()); }
  
  public Hashtable createStandardLabels(int paramInt1, int paramInt2) {
    if (paramInt2 > getMaximum() || paramInt2 < getMinimum())
      throw new IllegalArgumentException("Slider label start point out of range."); 
    if (paramInt1 <= 0)
      throw new IllegalArgumentException("Label incremement must be > 0"); 
    class SmartHashtable extends Hashtable<Object, Object> implements PropertyChangeListener {
      int increment = 0;
      
      int start = 0;
      
      boolean startAtMin = false;
      
      public SmartHashtable(int param1Int1, int param1Int2) {
        this.increment = param1Int1;
        this.start = param1Int2;
        this.startAtMin = (param1Int2 == this$0.getMinimum());
        createLabels();
      }
      
      public void propertyChange(PropertyChangeEvent param1PropertyChangeEvent) {
        if (param1PropertyChangeEvent.getPropertyName().equals("minimum") && this.startAtMin)
          this.start = JSlider.this.getMinimum(); 
        if (param1PropertyChangeEvent.getPropertyName().equals("minimum") || param1PropertyChangeEvent.getPropertyName().equals("maximum")) {
          Enumeration enumeration = JSlider.this.getLabelTable().keys();
          Hashtable hashtable = new Hashtable();
          while (enumeration.hasMoreElements()) {
            Object object1 = enumeration.nextElement();
            Object object2 = JSlider.this.labelTable.get(object1);
            if (!(object2 instanceof LabelUIResource))
              hashtable.put(object1, object2); 
          } 
          clear();
          createLabels();
          enumeration = hashtable.keys();
          while (enumeration.hasMoreElements()) {
            Object object = enumeration.nextElement();
            put(object, hashtable.get(object));
          } 
          ((JSlider)param1PropertyChangeEvent.getSource()).setLabelTable(this);
        } 
      }
      
      void createLabels() {
        for (int i = this.start; i <= JSlider.this.getMaximum(); i += this.increment)
          put(Integer.valueOf(i), new LabelUIResource("" + i, 0)); 
      }
      
      class LabelUIResource extends JLabel implements UIResource {
        public LabelUIResource(String param2String, int param2Int) {
          super(param2String, param2Int);
          setName("Slider.label");
        }
        
        public Font getFont() {
          Font font = super.getFont();
          return (font != null && !(font instanceof UIResource)) ? font : JSlider.SmartHashtable.this.this$0.getFont();
        }
        
        public Color getForeground() {
          Color color = super.getForeground();
          return (color != null && !(color instanceof UIResource)) ? color : (!(JSlider.SmartHashtable.this.this$0.getForeground() instanceof UIResource) ? JSlider.SmartHashtable.this.this$0.getForeground() : color);
        }
      }
    };
    SmartHashtable smartHashtable = new SmartHashtable(paramInt1, paramInt2);
    Dictionary dictionary = getLabelTable();
    if (dictionary != null && dictionary instanceof PropertyChangeListener)
      removePropertyChangeListener((PropertyChangeListener)dictionary); 
    addPropertyChangeListener(smartHashtable);
    return smartHashtable;
  }
  
  public boolean getInverted() { return this.isInverted; }
  
  public void setInverted(boolean paramBoolean) {
    boolean bool = this.isInverted;
    this.isInverted = paramBoolean;
    firePropertyChange("inverted", bool, this.isInverted);
    if (paramBoolean != bool)
      repaint(); 
  }
  
  public int getMajorTickSpacing() { return this.majorTickSpacing; }
  
  public void setMajorTickSpacing(int paramInt) {
    int i = this.majorTickSpacing;
    this.majorTickSpacing = paramInt;
    if (this.labelTable == null && getMajorTickSpacing() > 0 && getPaintLabels())
      setLabelTable(createStandardLabels(getMajorTickSpacing())); 
    firePropertyChange("majorTickSpacing", i, this.majorTickSpacing);
    if (this.majorTickSpacing != i && getPaintTicks())
      repaint(); 
  }
  
  public int getMinorTickSpacing() { return this.minorTickSpacing; }
  
  public void setMinorTickSpacing(int paramInt) {
    int i = this.minorTickSpacing;
    this.minorTickSpacing = paramInt;
    firePropertyChange("minorTickSpacing", i, this.minorTickSpacing);
    if (this.minorTickSpacing != i && getPaintTicks())
      repaint(); 
  }
  
  public boolean getSnapToTicks() { return this.snapToTicks; }
  
  boolean getSnapToValue() { return this.snapToValue; }
  
  public void setSnapToTicks(boolean paramBoolean) {
    boolean bool = this.snapToTicks;
    this.snapToTicks = paramBoolean;
    firePropertyChange("snapToTicks", bool, this.snapToTicks);
  }
  
  void setSnapToValue(boolean paramBoolean) {
    boolean bool = this.snapToValue;
    this.snapToValue = paramBoolean;
    firePropertyChange("snapToValue", bool, this.snapToValue);
  }
  
  public boolean getPaintTicks() { return this.paintTicks; }
  
  public void setPaintTicks(boolean paramBoolean) {
    boolean bool = this.paintTicks;
    this.paintTicks = paramBoolean;
    firePropertyChange("paintTicks", bool, this.paintTicks);
    if (this.paintTicks != bool) {
      revalidate();
      repaint();
    } 
  }
  
  public boolean getPaintTrack() { return this.paintTrack; }
  
  public void setPaintTrack(boolean paramBoolean) {
    boolean bool = this.paintTrack;
    this.paintTrack = paramBoolean;
    firePropertyChange("paintTrack", bool, this.paintTrack);
    if (this.paintTrack != bool)
      repaint(); 
  }
  
  public boolean getPaintLabels() { return this.paintLabels; }
  
  public void setPaintLabels(boolean paramBoolean) {
    boolean bool = this.paintLabels;
    this.paintLabels = paramBoolean;
    if (this.labelTable == null && getMajorTickSpacing() > 0)
      setLabelTable(createStandardLabels(getMajorTickSpacing())); 
    firePropertyChange("paintLabels", bool, this.paintLabels);
    if (this.paintLabels != bool) {
      revalidate();
      repaint();
    } 
  }
  
  private void writeObject(ObjectOutputStream paramObjectOutputStream) throws IOException {
    paramObjectOutputStream.defaultWriteObject();
    if (getUIClassID().equals("SliderUI")) {
      byte b = JComponent.getWriteObjCounter(this);
      b = (byte)(b - 1);
      JComponent.setWriteObjCounter(this, b);
      if (b == 0 && this.ui != null)
        this.ui.installUI(this); 
    } 
  }
  
  protected String paramString() {
    String str1 = this.paintTicks ? "true" : "false";
    String str2 = this.paintTrack ? "true" : "false";
    String str3 = this.paintLabels ? "true" : "false";
    String str4 = this.isInverted ? "true" : "false";
    String str5 = this.snapToTicks ? "true" : "false";
    String str6 = this.snapToValue ? "true" : "false";
    String str7 = (this.orientation == 0) ? "HORIZONTAL" : "VERTICAL";
    return super.paramString() + ",isInverted=" + str4 + ",majorTickSpacing=" + this.majorTickSpacing + ",minorTickSpacing=" + this.minorTickSpacing + ",orientation=" + str7 + ",paintLabels=" + str3 + ",paintTicks=" + str1 + ",paintTrack=" + str2 + ",snapToTicks=" + str5 + ",snapToValue=" + str6;
  }
  
  public AccessibleContext getAccessibleContext() {
    if (this.accessibleContext == null)
      this.accessibleContext = new AccessibleJSlider(); 
    return this.accessibleContext;
  }
  
  protected class AccessibleJSlider extends JComponent.AccessibleJComponent implements AccessibleValue {
    protected AccessibleJSlider() { super(JSlider.this); }
    
    public AccessibleStateSet getAccessibleStateSet() {
      AccessibleStateSet accessibleStateSet = super.getAccessibleStateSet();
      if (JSlider.this.getValueIsAdjusting())
        accessibleStateSet.add(AccessibleState.BUSY); 
      if (JSlider.this.getOrientation() == 1) {
        accessibleStateSet.add(AccessibleState.VERTICAL);
      } else {
        accessibleStateSet.add(AccessibleState.HORIZONTAL);
      } 
      return accessibleStateSet;
    }
    
    public AccessibleRole getAccessibleRole() { return AccessibleRole.SLIDER; }
    
    public AccessibleValue getAccessibleValue() { return this; }
    
    public Number getCurrentAccessibleValue() { return Integer.valueOf(JSlider.this.getValue()); }
    
    public boolean setCurrentAccessibleValue(Number param1Number) {
      if (param1Number == null)
        return false; 
      JSlider.this.setValue(param1Number.intValue());
      return true;
    }
    
    public Number getMinimumAccessibleValue() { return Integer.valueOf(JSlider.this.getMinimum()); }
    
    public Number getMaximumAccessibleValue() {
      BoundedRangeModel boundedRangeModel = JSlider.this.getModel();
      return Integer.valueOf(boundedRangeModel.getMaximum() - boundedRangeModel.getExtent());
    }
  }
  
  private class ModelListener implements ChangeListener, Serializable {
    private ModelListener() {}
    
    public void stateChanged(ChangeEvent param1ChangeEvent) { JSlider.this.fireStateChanged(); }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\JSlider.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */