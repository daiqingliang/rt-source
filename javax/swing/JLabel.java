package javax.swing;

import java.awt.Component;
import java.awt.Image;
import java.awt.Insets;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.event.KeyEvent;
import java.awt.geom.Rectangle2D;
import java.beans.Transient;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.text.BreakIterator;
import javax.accessibility.Accessible;
import javax.accessibility.AccessibleContext;
import javax.accessibility.AccessibleExtendedComponent;
import javax.accessibility.AccessibleIcon;
import javax.accessibility.AccessibleKeyBinding;
import javax.accessibility.AccessibleRelation;
import javax.accessibility.AccessibleRelationSet;
import javax.accessibility.AccessibleRole;
import javax.accessibility.AccessibleText;
import javax.swing.plaf.LabelUI;
import javax.swing.text.AttributeSet;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.Element;
import javax.swing.text.Position;
import javax.swing.text.StyledDocument;
import javax.swing.text.View;

public class JLabel extends JComponent implements SwingConstants, Accessible {
  private static final String uiClassID = "LabelUI";
  
  private int mnemonic = 0;
  
  private int mnemonicIndex = -1;
  
  private String text = "";
  
  private Icon defaultIcon = null;
  
  private Icon disabledIcon = null;
  
  private boolean disabledIconSet = false;
  
  private int verticalAlignment = 0;
  
  private int horizontalAlignment = 10;
  
  private int verticalTextPosition = 0;
  
  private int horizontalTextPosition = 11;
  
  private int iconTextGap = 4;
  
  protected Component labelFor = null;
  
  static final String LABELED_BY_PROPERTY = "labeledBy";
  
  public JLabel(String paramString, Icon paramIcon, int paramInt) {
    setText(paramString);
    setIcon(paramIcon);
    setHorizontalAlignment(paramInt);
    updateUI();
    setAlignmentX(0.0F);
  }
  
  public JLabel(String paramString, int paramInt) { this(paramString, null, paramInt); }
  
  public JLabel(String paramString) { this(paramString, null, 10); }
  
  public JLabel(Icon paramIcon, int paramInt) { this(null, paramIcon, paramInt); }
  
  public JLabel(Icon paramIcon) { this(null, paramIcon, 0); }
  
  public JLabel() { this("", null, 10); }
  
  public LabelUI getUI() { return (LabelUI)this.ui; }
  
  public void setUI(LabelUI paramLabelUI) {
    setUI(paramLabelUI);
    if (!this.disabledIconSet && this.disabledIcon != null)
      setDisabledIcon(null); 
  }
  
  public void updateUI() { setUI((LabelUI)UIManager.getUI(this)); }
  
  public String getUIClassID() { return "LabelUI"; }
  
  public String getText() { return this.text; }
  
  public void setText(String paramString) {
    String str1 = null;
    if (this.accessibleContext != null)
      str1 = this.accessibleContext.getAccessibleName(); 
    String str2 = this.text;
    this.text = paramString;
    firePropertyChange("text", str2, paramString);
    setDisplayedMnemonicIndex(SwingUtilities.findDisplayedMnemonicIndex(paramString, getDisplayedMnemonic()));
    if (this.accessibleContext != null && this.accessibleContext.getAccessibleName() != str1)
      this.accessibleContext.firePropertyChange("AccessibleVisibleData", str1, this.accessibleContext.getAccessibleName()); 
    if (paramString == null || str2 == null || !paramString.equals(str2)) {
      revalidate();
      repaint();
    } 
  }
  
  public Icon getIcon() { return this.defaultIcon; }
  
  public void setIcon(Icon paramIcon) {
    Icon icon = this.defaultIcon;
    this.defaultIcon = paramIcon;
    if (this.defaultIcon != icon && !this.disabledIconSet)
      this.disabledIcon = null; 
    firePropertyChange("icon", icon, this.defaultIcon);
    if (this.accessibleContext != null && icon != this.defaultIcon)
      this.accessibleContext.firePropertyChange("AccessibleVisibleData", icon, this.defaultIcon); 
    if (this.defaultIcon != icon) {
      if (this.defaultIcon == null || icon == null || this.defaultIcon.getIconWidth() != icon.getIconWidth() || this.defaultIcon.getIconHeight() != icon.getIconHeight())
        revalidate(); 
      repaint();
    } 
  }
  
  @Transient
  public Icon getDisabledIcon() {
    if (!this.disabledIconSet && this.disabledIcon == null && this.defaultIcon != null) {
      this.disabledIcon = UIManager.getLookAndFeel().getDisabledIcon(this, this.defaultIcon);
      if (this.disabledIcon != null)
        firePropertyChange("disabledIcon", null, this.disabledIcon); 
    } 
    return this.disabledIcon;
  }
  
  public void setDisabledIcon(Icon paramIcon) {
    Icon icon = this.disabledIcon;
    this.disabledIcon = paramIcon;
    this.disabledIconSet = (paramIcon != null);
    firePropertyChange("disabledIcon", icon, paramIcon);
    if (paramIcon != icon) {
      if (paramIcon == null || icon == null || paramIcon.getIconWidth() != icon.getIconWidth() || paramIcon.getIconHeight() != icon.getIconHeight())
        revalidate(); 
      if (!isEnabled())
        repaint(); 
    } 
  }
  
  public void setDisplayedMnemonic(int paramInt) {
    int i = this.mnemonic;
    this.mnemonic = paramInt;
    firePropertyChange("displayedMnemonic", i, this.mnemonic);
    setDisplayedMnemonicIndex(SwingUtilities.findDisplayedMnemonicIndex(getText(), this.mnemonic));
    if (paramInt != i) {
      revalidate();
      repaint();
    } 
  }
  
  public void setDisplayedMnemonic(char paramChar) {
    int i = KeyEvent.getExtendedKeyCodeForChar(paramChar);
    if (i != 0)
      setDisplayedMnemonic(i); 
  }
  
  public int getDisplayedMnemonic() { return this.mnemonic; }
  
  public void setDisplayedMnemonicIndex(int paramInt) {
    int i = this.mnemonicIndex;
    if (paramInt == -1) {
      this.mnemonicIndex = -1;
    } else {
      String str = getText();
      boolean bool = (str == null) ? 0 : str.length();
      if (paramInt < -1 || paramInt >= bool)
        throw new IllegalArgumentException("index == " + paramInt); 
    } 
    this.mnemonicIndex = paramInt;
    firePropertyChange("displayedMnemonicIndex", i, paramInt);
    if (paramInt != i) {
      revalidate();
      repaint();
    } 
  }
  
  public int getDisplayedMnemonicIndex() { return this.mnemonicIndex; }
  
  protected int checkHorizontalKey(int paramInt, String paramString) {
    if (paramInt == 2 || paramInt == 0 || paramInt == 4 || paramInt == 10 || paramInt == 11)
      return paramInt; 
    throw new IllegalArgumentException(paramString);
  }
  
  protected int checkVerticalKey(int paramInt, String paramString) {
    if (paramInt == 1 || paramInt == 0 || paramInt == 3)
      return paramInt; 
    throw new IllegalArgumentException(paramString);
  }
  
  public int getIconTextGap() { return this.iconTextGap; }
  
  public void setIconTextGap(int paramInt) {
    int i = this.iconTextGap;
    this.iconTextGap = paramInt;
    firePropertyChange("iconTextGap", i, paramInt);
    if (paramInt != i) {
      revalidate();
      repaint();
    } 
  }
  
  public int getVerticalAlignment() { return this.verticalAlignment; }
  
  public void setVerticalAlignment(int paramInt) {
    if (paramInt == this.verticalAlignment)
      return; 
    int i = this.verticalAlignment;
    this.verticalAlignment = checkVerticalKey(paramInt, "verticalAlignment");
    firePropertyChange("verticalAlignment", i, this.verticalAlignment);
    repaint();
  }
  
  public int getHorizontalAlignment() { return this.horizontalAlignment; }
  
  public void setHorizontalAlignment(int paramInt) {
    if (paramInt == this.horizontalAlignment)
      return; 
    int i = this.horizontalAlignment;
    this.horizontalAlignment = checkHorizontalKey(paramInt, "horizontalAlignment");
    firePropertyChange("horizontalAlignment", i, this.horizontalAlignment);
    repaint();
  }
  
  public int getVerticalTextPosition() { return this.verticalTextPosition; }
  
  public void setVerticalTextPosition(int paramInt) {
    if (paramInt == this.verticalTextPosition)
      return; 
    int i = this.verticalTextPosition;
    this.verticalTextPosition = checkVerticalKey(paramInt, "verticalTextPosition");
    firePropertyChange("verticalTextPosition", i, this.verticalTextPosition);
    revalidate();
    repaint();
  }
  
  public int getHorizontalTextPosition() { return this.horizontalTextPosition; }
  
  public void setHorizontalTextPosition(int paramInt) {
    int i = this.horizontalTextPosition;
    this.horizontalTextPosition = checkHorizontalKey(paramInt, "horizontalTextPosition");
    firePropertyChange("horizontalTextPosition", i, this.horizontalTextPosition);
    revalidate();
    repaint();
  }
  
  public boolean imageUpdate(Image paramImage, int paramInt1, int paramInt2, int paramInt3, int paramInt4, int paramInt5) { return (!isShowing() || (!SwingUtilities.doesIconReferenceImage(getIcon(), paramImage) && !SwingUtilities.doesIconReferenceImage(this.disabledIcon, paramImage))) ? false : super.imageUpdate(paramImage, paramInt1, paramInt2, paramInt3, paramInt4, paramInt5); }
  
  private void writeObject(ObjectOutputStream paramObjectOutputStream) throws IOException {
    paramObjectOutputStream.defaultWriteObject();
    if (getUIClassID().equals("LabelUI")) {
      byte b = JComponent.getWriteObjCounter(this);
      b = (byte)(b - 1);
      JComponent.setWriteObjCounter(this, b);
      if (b == 0 && this.ui != null)
        this.ui.installUI(this); 
    } 
  }
  
  protected String paramString() {
    String str8;
    String str7;
    String str6;
    String str5;
    String str1 = (this.text != null) ? this.text : "";
    String str2 = (this.defaultIcon != null && this.defaultIcon != this) ? this.defaultIcon.toString() : "";
    String str3 = (this.disabledIcon != null && this.disabledIcon != this) ? this.disabledIcon.toString() : "";
    String str4 = (this.labelFor != null) ? this.labelFor.toString() : "";
    if (this.verticalAlignment == 1) {
      str5 = "TOP";
    } else if (this.verticalAlignment == 0) {
      str5 = "CENTER";
    } else if (this.verticalAlignment == 3) {
      str5 = "BOTTOM";
    } else {
      str5 = "";
    } 
    if (this.horizontalAlignment == 2) {
      str6 = "LEFT";
    } else if (this.horizontalAlignment == 0) {
      str6 = "CENTER";
    } else if (this.horizontalAlignment == 4) {
      str6 = "RIGHT";
    } else if (this.horizontalAlignment == 10) {
      str6 = "LEADING";
    } else if (this.horizontalAlignment == 11) {
      str6 = "TRAILING";
    } else {
      str6 = "";
    } 
    if (this.verticalTextPosition == 1) {
      str7 = "TOP";
    } else if (this.verticalTextPosition == 0) {
      str7 = "CENTER";
    } else if (this.verticalTextPosition == 3) {
      str7 = "BOTTOM";
    } else {
      str7 = "";
    } 
    if (this.horizontalTextPosition == 2) {
      str8 = "LEFT";
    } else if (this.horizontalTextPosition == 0) {
      str8 = "CENTER";
    } else if (this.horizontalTextPosition == 4) {
      str8 = "RIGHT";
    } else if (this.horizontalTextPosition == 10) {
      str8 = "LEADING";
    } else if (this.horizontalTextPosition == 11) {
      str8 = "TRAILING";
    } else {
      str8 = "";
    } 
    return super.paramString() + ",defaultIcon=" + str2 + ",disabledIcon=" + str3 + ",horizontalAlignment=" + str6 + ",horizontalTextPosition=" + str8 + ",iconTextGap=" + this.iconTextGap + ",labelFor=" + str4 + ",text=" + str1 + ",verticalAlignment=" + str5 + ",verticalTextPosition=" + str7;
  }
  
  public Component getLabelFor() { return this.labelFor; }
  
  public void setLabelFor(Component paramComponent) {
    Component component = this.labelFor;
    this.labelFor = paramComponent;
    firePropertyChange("labelFor", component, paramComponent);
    if (component instanceof JComponent)
      ((JComponent)component).putClientProperty("labeledBy", null); 
    if (paramComponent instanceof JComponent)
      ((JComponent)paramComponent).putClientProperty("labeledBy", this); 
  }
  
  public AccessibleContext getAccessibleContext() {
    if (this.accessibleContext == null)
      this.accessibleContext = new AccessibleJLabel(); 
    return this.accessibleContext;
  }
  
  protected class AccessibleJLabel extends JComponent.AccessibleJComponent implements AccessibleText, AccessibleExtendedComponent {
    protected AccessibleJLabel() { super(JLabel.this); }
    
    public String getAccessibleName() {
      String str = this.accessibleName;
      if (str == null)
        str = (String)JLabel.this.getClientProperty("AccessibleName"); 
      if (str == null)
        str = JLabel.this.getText(); 
      if (str == null)
        str = super.getAccessibleName(); 
      return str;
    }
    
    public AccessibleRole getAccessibleRole() { return AccessibleRole.LABEL; }
    
    public AccessibleIcon[] getAccessibleIcon() {
      Icon icon = JLabel.this.getIcon();
      if (icon instanceof Accessible) {
        AccessibleContext accessibleContext = ((Accessible)icon).getAccessibleContext();
        if (accessibleContext != null && accessibleContext instanceof AccessibleIcon)
          return new AccessibleIcon[] { (AccessibleIcon)accessibleContext }; 
      } 
      return null;
    }
    
    public AccessibleRelationSet getAccessibleRelationSet() {
      AccessibleRelationSet accessibleRelationSet = super.getAccessibleRelationSet();
      if (!accessibleRelationSet.contains(AccessibleRelation.LABEL_FOR)) {
        Component component = JLabel.this.getLabelFor();
        if (component != null) {
          AccessibleRelation accessibleRelation = new AccessibleRelation(AccessibleRelation.LABEL_FOR);
          accessibleRelation.setTarget(component);
          accessibleRelationSet.add(accessibleRelation);
        } 
      } 
      return accessibleRelationSet;
    }
    
    public AccessibleText getAccessibleText() {
      View view = (View)JLabel.this.getClientProperty("html");
      return (view != null) ? this : null;
    }
    
    public int getIndexAtPoint(Point param1Point) {
      View view = (View)JLabel.this.getClientProperty("html");
      if (view != null) {
        Rectangle rectangle = getTextRectangle();
        if (rectangle == null)
          return -1; 
        Rectangle2D.Float float = new Rectangle2D.Float(rectangle.x, rectangle.y, rectangle.width, rectangle.height);
        Position.Bias[] arrayOfBias = new Position.Bias[1];
        return view.viewToModel(param1Point.x, param1Point.y, float, arrayOfBias);
      } 
      return -1;
    }
    
    public Rectangle getCharacterBounds(int param1Int) {
      View view = (View)JLabel.this.getClientProperty("html");
      if (view != null) {
        Rectangle rectangle = getTextRectangle();
        if (rectangle == null)
          return null; 
        Rectangle2D.Float float = new Rectangle2D.Float(rectangle.x, rectangle.y, rectangle.width, rectangle.height);
        try {
          Shape shape = view.modelToView(param1Int, float, Position.Bias.Forward);
          return shape.getBounds();
        } catch (BadLocationException badLocationException) {
          return null;
        } 
      } 
      return null;
    }
    
    public int getCharCount() {
      View view = (View)JLabel.this.getClientProperty("html");
      if (view != null) {
        Document document = view.getDocument();
        if (document instanceof StyledDocument) {
          StyledDocument styledDocument = (StyledDocument)document;
          return styledDocument.getLength();
        } 
      } 
      return JLabel.this.accessibleContext.getAccessibleName().length();
    }
    
    public int getCaretPosition() { return -1; }
    
    public String getAtIndex(int param1Int1, int param1Int2) {
      if (param1Int2 < 0 || param1Int2 >= getCharCount())
        return null; 
      switch (param1Int1) {
        case 1:
          try {
            return getText(param1Int2, 1);
          } catch (BadLocationException badLocationException) {
            return null;
          } 
        case 2:
          try {
            String str = getText(0, getCharCount());
            BreakIterator breakIterator = BreakIterator.getWordInstance(getLocale());
            breakIterator.setText(str);
            int i = breakIterator.following(param1Int2);
            return str.substring(breakIterator.previous(), i);
          } catch (BadLocationException badLocationException) {
            return null;
          } 
        case 3:
          try {
            String str = getText(0, getCharCount());
            BreakIterator breakIterator = BreakIterator.getSentenceInstance(getLocale());
            breakIterator.setText(str);
            int i = breakIterator.following(param1Int2);
            return str.substring(breakIterator.previous(), i);
          } catch (BadLocationException badLocationException) {
            return null;
          } 
      } 
      return null;
    }
    
    public String getAfterIndex(int param1Int1, int param1Int2) {
      if (param1Int2 < 0 || param1Int2 >= getCharCount())
        return null; 
      switch (param1Int1) {
        case 1:
          if (param1Int2 + 1 >= getCharCount())
            return null; 
          try {
            return getText(param1Int2 + 1, 1);
          } catch (BadLocationException badLocationException) {
            return null;
          } 
        case 2:
          try {
            String str = getText(0, getCharCount());
            BreakIterator breakIterator = BreakIterator.getWordInstance(getLocale());
            breakIterator.setText(str);
            int i = breakIterator.following(param1Int2);
            if (i == -1 || i >= str.length())
              return null; 
            int j = breakIterator.following(i);
            return (j == -1 || j >= str.length()) ? null : str.substring(i, j);
          } catch (BadLocationException badLocationException) {
            return null;
          } 
        case 3:
          try {
            String str = getText(0, getCharCount());
            BreakIterator breakIterator = BreakIterator.getSentenceInstance(getLocale());
            breakIterator.setText(str);
            int i = breakIterator.following(param1Int2);
            if (i == -1 || i > str.length())
              return null; 
            int j = breakIterator.following(i);
            return (j == -1 || j > str.length()) ? null : str.substring(i, j);
          } catch (BadLocationException badLocationException) {
            return null;
          } 
      } 
      return null;
    }
    
    public String getBeforeIndex(int param1Int1, int param1Int2) {
      if (param1Int2 < 0 || param1Int2 > getCharCount() - 1)
        return null; 
      switch (param1Int1) {
        case 1:
          if (param1Int2 == 0)
            return null; 
          try {
            return getText(param1Int2 - 1, 1);
          } catch (BadLocationException badLocationException) {
            return null;
          } 
        case 2:
          try {
            String str = getText(0, getCharCount());
            BreakIterator breakIterator = BreakIterator.getWordInstance(getLocale());
            breakIterator.setText(str);
            int i = breakIterator.following(param1Int2);
            i = breakIterator.previous();
            int j = breakIterator.previous();
            return (j == -1) ? null : str.substring(j, i);
          } catch (BadLocationException badLocationException) {
            return null;
          } 
        case 3:
          try {
            String str = getText(0, getCharCount());
            BreakIterator breakIterator = BreakIterator.getSentenceInstance(getLocale());
            breakIterator.setText(str);
            int i = breakIterator.following(param1Int2);
            i = breakIterator.previous();
            int j = breakIterator.previous();
            return (j == -1) ? null : str.substring(j, i);
          } catch (BadLocationException badLocationException) {
            return null;
          } 
      } 
      return null;
    }
    
    public AttributeSet getCharacterAttribute(int param1Int) {
      View view = (View)JLabel.this.getClientProperty("html");
      if (view != null) {
        Document document = view.getDocument();
        if (document instanceof StyledDocument) {
          StyledDocument styledDocument = (StyledDocument)document;
          Element element = styledDocument.getCharacterElement(param1Int);
          if (element != null)
            return element.getAttributes(); 
        } 
      } 
      return null;
    }
    
    public int getSelectionStart() { return -1; }
    
    public int getSelectionEnd() { return -1; }
    
    public String getSelectedText() { return null; }
    
    private String getText(int param1Int1, int param1Int2) {
      View view = (View)JLabel.this.getClientProperty("html");
      if (view != null) {
        Document document = view.getDocument();
        if (document instanceof StyledDocument) {
          StyledDocument styledDocument = (StyledDocument)document;
          return styledDocument.getText(param1Int1, param1Int2);
        } 
      } 
      return null;
    }
    
    private Rectangle getTextRectangle() {
      String str1 = JLabel.this.getText();
      Icon icon = JLabel.this.isEnabled() ? JLabel.this.getIcon() : JLabel.this.getDisabledIcon();
      if (icon == null && str1 == null)
        return null; 
      Rectangle rectangle1 = new Rectangle();
      Rectangle rectangle2 = new Rectangle();
      Rectangle rectangle3 = new Rectangle();
      Insets insets = new Insets(0, 0, 0, 0);
      insets = JLabel.this.getInsets(insets);
      rectangle3.x = insets.left;
      rectangle3.y = insets.top;
      rectangle3.width = JLabel.this.getWidth() - insets.left + insets.right;
      rectangle3.height = JLabel.this.getHeight() - insets.top + insets.bottom;
      String str2 = SwingUtilities.layoutCompoundLabel(JLabel.this, getFontMetrics(getFont()), str1, icon, JLabel.this.getVerticalAlignment(), JLabel.this.getHorizontalAlignment(), JLabel.this.getVerticalTextPosition(), JLabel.this.getHorizontalTextPosition(), rectangle3, rectangle1, rectangle2, JLabel.this.getIconTextGap());
      return rectangle2;
    }
    
    AccessibleExtendedComponent getAccessibleExtendedComponent() { return this; }
    
    public String getToolTipText() { return JLabel.this.getToolTipText(); }
    
    public String getTitledBorderText() { return super.getTitledBorderText(); }
    
    public AccessibleKeyBinding getAccessibleKeyBinding() {
      int i = JLabel.this.getDisplayedMnemonic();
      return (i == 0) ? null : new LabelKeyBinding(i);
    }
    
    class LabelKeyBinding implements AccessibleKeyBinding {
      int mnemonic;
      
      LabelKeyBinding(int param2Int) { this.mnemonic = param2Int; }
      
      public int getAccessibleKeyBindingCount() { return 1; }
      
      public Object getAccessibleKeyBinding(int param2Int) {
        if (param2Int != 0)
          throw new IllegalArgumentException(); 
        return KeyStroke.getKeyStroke(this.mnemonic, 0);
      }
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\JLabel.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */