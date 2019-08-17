package javax.swing.text;

import java.awt.Color;
import java.awt.event.ActionEvent;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.io.Serializable;
import javax.swing.Action;
import javax.swing.JEditorPane;
import javax.swing.UIManager;
import javax.swing.event.CaretEvent;
import javax.swing.event.CaretListener;

public class StyledEditorKit extends DefaultEditorKit {
  private static final ViewFactory defaultFactory = new StyledViewFactory();
  
  Element currentRun;
  
  Element currentParagraph;
  
  MutableAttributeSet inputAttributes;
  
  private AttributeTracker inputAttributeUpdater;
  
  private static final Action[] defaultActions = { 
      new FontFamilyAction("font-family-SansSerif", "SansSerif"), new FontFamilyAction("font-family-Monospaced", "Monospaced"), new FontFamilyAction("font-family-Serif", "Serif"), new FontSizeAction("font-size-8", 8), new FontSizeAction("font-size-10", 10), new FontSizeAction("font-size-12", 12), new FontSizeAction("font-size-14", 14), new FontSizeAction("font-size-16", 16), new FontSizeAction("font-size-18", 18), new FontSizeAction("font-size-24", 24), 
      new FontSizeAction("font-size-36", 36), new FontSizeAction("font-size-48", 48), new AlignmentAction("left-justify", 0), new AlignmentAction("center-justify", 1), new AlignmentAction("right-justify", 2), new BoldAction(), new ItalicAction(), new StyledInsertBreakAction(), new UnderlineAction() };
  
  public StyledEditorKit() {
    createInputAttributeUpdated();
    createInputAttributes();
  }
  
  public MutableAttributeSet getInputAttributes() { return this.inputAttributes; }
  
  public Element getCharacterAttributeRun() { return this.currentRun; }
  
  public Action[] getActions() {
    this;
    return TextAction.augmentList(super.getActions(), defaultActions);
  }
  
  public Document createDefaultDocument() { return new DefaultStyledDocument(); }
  
  public void install(JEditorPane paramJEditorPane) {
    paramJEditorPane.addCaretListener(this.inputAttributeUpdater);
    paramJEditorPane.addPropertyChangeListener(this.inputAttributeUpdater);
    Caret caret = paramJEditorPane.getCaret();
    if (caret != null)
      this.inputAttributeUpdater.updateInputAttributes(caret.getDot(), caret.getMark(), paramJEditorPane); 
  }
  
  public void deinstall(JEditorPane paramJEditorPane) {
    paramJEditorPane.removeCaretListener(this.inputAttributeUpdater);
    paramJEditorPane.removePropertyChangeListener(this.inputAttributeUpdater);
    this.currentRun = null;
    this.currentParagraph = null;
  }
  
  public ViewFactory getViewFactory() { return defaultFactory; }
  
  public Object clone() {
    StyledEditorKit styledEditorKit = (StyledEditorKit)super.clone();
    styledEditorKit.currentRun = styledEditorKit.currentParagraph = null;
    styledEditorKit.createInputAttributeUpdated();
    styledEditorKit.createInputAttributes();
    return styledEditorKit;
  }
  
  private void createInputAttributes() { this.inputAttributes = new SimpleAttributeSet() {
        public AttributeSet getResolveParent() { return (StyledEditorKit.this.currentParagraph != null) ? StyledEditorKit.this.currentParagraph.getAttributes() : null; }
        
        public Object clone() { return new SimpleAttributeSet(this); }
      }; }
  
  private void createInputAttributeUpdated() { this.inputAttributeUpdater = new AttributeTracker(); }
  
  protected void createInputAttributes(Element paramElement, MutableAttributeSet paramMutableAttributeSet) {
    if (paramElement.getAttributes().getAttributeCount() > 0 || paramElement.getEndOffset() - paramElement.getStartOffset() > 1 || paramElement.getEndOffset() < paramElement.getDocument().getLength()) {
      paramMutableAttributeSet.removeAttributes(paramMutableAttributeSet);
      paramMutableAttributeSet.addAttributes(paramElement.getAttributes());
      paramMutableAttributeSet.removeAttribute(StyleConstants.ComponentAttribute);
      paramMutableAttributeSet.removeAttribute(StyleConstants.IconAttribute);
      paramMutableAttributeSet.removeAttribute("$ename");
      paramMutableAttributeSet.removeAttribute(StyleConstants.ComposedTextAttribute);
    } 
  }
  
  public static class AlignmentAction extends StyledTextAction {
    private int a;
    
    public AlignmentAction(String param1String, int param1Int) {
      super(param1String);
      this.a = param1Int;
    }
    
    public void actionPerformed(ActionEvent param1ActionEvent) {
      JEditorPane jEditorPane = getEditor(param1ActionEvent);
      if (jEditorPane != null) {
        int i = this.a;
        if (param1ActionEvent != null && param1ActionEvent.getSource() == jEditorPane) {
          String str = param1ActionEvent.getActionCommand();
          try {
            i = Integer.parseInt(str, 10);
          } catch (NumberFormatException numberFormatException) {}
        } 
        SimpleAttributeSet simpleAttributeSet = new SimpleAttributeSet();
        StyleConstants.setAlignment(simpleAttributeSet, i);
        setParagraphAttributes(jEditorPane, simpleAttributeSet, false);
      } 
    }
  }
  
  class AttributeTracker implements CaretListener, PropertyChangeListener, Serializable {
    void updateInputAttributes(int param1Int1, int param1Int2, JTextComponent param1JTextComponent) {
      Element element;
      Document document = param1JTextComponent.getDocument();
      if (!(document instanceof StyledDocument))
        return; 
      int i = Math.min(param1Int1, param1Int2);
      StyledDocument styledDocument = (StyledDocument)document;
      StyledEditorKit.this.currentParagraph = styledDocument.getParagraphElement(i);
      if (StyledEditorKit.this.currentParagraph.getStartOffset() == i || param1Int1 != param1Int2) {
        element = styledDocument.getCharacterElement(i);
      } else {
        element = styledDocument.getCharacterElement(Math.max(i - 1, 0));
      } 
      if (element != StyledEditorKit.this.currentRun) {
        StyledEditorKit.this.currentRun = element;
        StyledEditorKit.this.createInputAttributes(StyledEditorKit.this.currentRun, StyledEditorKit.this.getInputAttributes());
      } 
    }
    
    public void propertyChange(PropertyChangeEvent param1PropertyChangeEvent) {
      Object object1 = param1PropertyChangeEvent.getNewValue();
      Object object2 = param1PropertyChangeEvent.getSource();
      if (object2 instanceof JTextComponent && object1 instanceof Document)
        updateInputAttributes(0, 0, (JTextComponent)object2); 
    }
    
    public void caretUpdate(CaretEvent param1CaretEvent) { updateInputAttributes(param1CaretEvent.getDot(), param1CaretEvent.getMark(), (JTextComponent)param1CaretEvent.getSource()); }
  }
  
  public static class BoldAction extends StyledTextAction {
    public BoldAction() { super("font-bold"); }
    
    public void actionPerformed(ActionEvent param1ActionEvent) {
      JEditorPane jEditorPane = getEditor(param1ActionEvent);
      if (jEditorPane != null) {
        StyledEditorKit styledEditorKit = getStyledEditorKit(jEditorPane);
        MutableAttributeSet mutableAttributeSet = styledEditorKit.getInputAttributes();
        boolean bool = !StyleConstants.isBold(mutableAttributeSet);
        SimpleAttributeSet simpleAttributeSet = new SimpleAttributeSet();
        StyleConstants.setBold(simpleAttributeSet, bool);
        setCharacterAttributes(jEditorPane, simpleAttributeSet, false);
      } 
    }
  }
  
  public static class FontFamilyAction extends StyledTextAction {
    private String family;
    
    public FontFamilyAction(String param1String1, String param1String2) {
      super(param1String1);
      this.family = param1String2;
    }
    
    public void actionPerformed(ActionEvent param1ActionEvent) {
      JEditorPane jEditorPane = getEditor(param1ActionEvent);
      if (jEditorPane != null) {
        String str = this.family;
        if (param1ActionEvent != null && param1ActionEvent.getSource() == jEditorPane) {
          String str1 = param1ActionEvent.getActionCommand();
          if (str1 != null)
            str = str1; 
        } 
        if (str != null) {
          SimpleAttributeSet simpleAttributeSet = new SimpleAttributeSet();
          StyleConstants.setFontFamily(simpleAttributeSet, str);
          setCharacterAttributes(jEditorPane, simpleAttributeSet, false);
        } else {
          UIManager.getLookAndFeel().provideErrorFeedback(jEditorPane);
        } 
      } 
    }
  }
  
  public static class FontSizeAction extends StyledTextAction {
    private int size;
    
    public FontSizeAction(String param1String, int param1Int) {
      super(param1String);
      this.size = param1Int;
    }
    
    public void actionPerformed(ActionEvent param1ActionEvent) {
      JEditorPane jEditorPane = getEditor(param1ActionEvent);
      if (jEditorPane != null) {
        int i = this.size;
        if (param1ActionEvent != null && param1ActionEvent.getSource() == jEditorPane) {
          String str = param1ActionEvent.getActionCommand();
          try {
            i = Integer.parseInt(str, 10);
          } catch (NumberFormatException numberFormatException) {}
        } 
        if (i != 0) {
          SimpleAttributeSet simpleAttributeSet = new SimpleAttributeSet();
          StyleConstants.setFontSize(simpleAttributeSet, i);
          setCharacterAttributes(jEditorPane, simpleAttributeSet, false);
        } else {
          UIManager.getLookAndFeel().provideErrorFeedback(jEditorPane);
        } 
      } 
    }
  }
  
  public static class ForegroundAction extends StyledTextAction {
    private Color fg;
    
    public ForegroundAction(String param1String, Color param1Color) {
      super(param1String);
      this.fg = param1Color;
    }
    
    public void actionPerformed(ActionEvent param1ActionEvent) {
      JEditorPane jEditorPane = getEditor(param1ActionEvent);
      if (jEditorPane != null) {
        Color color = this.fg;
        if (param1ActionEvent != null && param1ActionEvent.getSource() == jEditorPane) {
          String str = param1ActionEvent.getActionCommand();
          try {
            color = Color.decode(str);
          } catch (NumberFormatException numberFormatException) {}
        } 
        if (color != null) {
          SimpleAttributeSet simpleAttributeSet = new SimpleAttributeSet();
          StyleConstants.setForeground(simpleAttributeSet, color);
          setCharacterAttributes(jEditorPane, simpleAttributeSet, false);
        } else {
          UIManager.getLookAndFeel().provideErrorFeedback(jEditorPane);
        } 
      } 
    }
  }
  
  public static class ItalicAction extends StyledTextAction {
    public ItalicAction() { super("font-italic"); }
    
    public void actionPerformed(ActionEvent param1ActionEvent) {
      JEditorPane jEditorPane = getEditor(param1ActionEvent);
      if (jEditorPane != null) {
        StyledEditorKit styledEditorKit = getStyledEditorKit(jEditorPane);
        MutableAttributeSet mutableAttributeSet = styledEditorKit.getInputAttributes();
        boolean bool = !StyleConstants.isItalic(mutableAttributeSet);
        SimpleAttributeSet simpleAttributeSet = new SimpleAttributeSet();
        StyleConstants.setItalic(simpleAttributeSet, bool);
        setCharacterAttributes(jEditorPane, simpleAttributeSet, false);
      } 
    }
  }
  
  static class StyledInsertBreakAction extends StyledTextAction {
    private SimpleAttributeSet tempSet;
    
    StyledInsertBreakAction() { super("insert-break"); }
    
    public void actionPerformed(ActionEvent param1ActionEvent) {
      JEditorPane jEditorPane = getEditor(param1ActionEvent);
      if (jEditorPane != null) {
        if (!jEditorPane.isEditable() || !jEditorPane.isEnabled()) {
          UIManager.getLookAndFeel().provideErrorFeedback(jEditorPane);
          return;
        } 
        StyledEditorKit styledEditorKit = getStyledEditorKit(jEditorPane);
        if (this.tempSet != null) {
          this.tempSet.removeAttributes(this.tempSet);
        } else {
          this.tempSet = new SimpleAttributeSet();
        } 
        this.tempSet.addAttributes(styledEditorKit.getInputAttributes());
        jEditorPane.replaceSelection("\n");
        MutableAttributeSet mutableAttributeSet = styledEditorKit.getInputAttributes();
        mutableAttributeSet.removeAttributes(mutableAttributeSet);
        mutableAttributeSet.addAttributes(this.tempSet);
        this.tempSet.removeAttributes(this.tempSet);
      } else {
        JTextComponent jTextComponent = getTextComponent(param1ActionEvent);
        if (jTextComponent != null) {
          if (!jTextComponent.isEditable() || !jTextComponent.isEnabled()) {
            UIManager.getLookAndFeel().provideErrorFeedback(jEditorPane);
            return;
          } 
          jTextComponent.replaceSelection("\n");
        } 
      } 
    }
  }
  
  public static abstract class StyledTextAction extends TextAction {
    public StyledTextAction(String param1String) { super(param1String); }
    
    protected final JEditorPane getEditor(ActionEvent param1ActionEvent) {
      JTextComponent jTextComponent = getTextComponent(param1ActionEvent);
      return (jTextComponent instanceof JEditorPane) ? (JEditorPane)jTextComponent : null;
    }
    
    protected final StyledDocument getStyledDocument(JEditorPane param1JEditorPane) {
      Document document = param1JEditorPane.getDocument();
      if (document instanceof StyledDocument)
        return (StyledDocument)document; 
      throw new IllegalArgumentException("document must be StyledDocument");
    }
    
    protected final StyledEditorKit getStyledEditorKit(JEditorPane param1JEditorPane) {
      EditorKit editorKit = param1JEditorPane.getEditorKit();
      if (editorKit instanceof StyledEditorKit)
        return (StyledEditorKit)editorKit; 
      throw new IllegalArgumentException("EditorKit must be StyledEditorKit");
    }
    
    protected final void setCharacterAttributes(JEditorPane param1JEditorPane, AttributeSet param1AttributeSet, boolean param1Boolean) {
      int i = param1JEditorPane.getSelectionStart();
      int j = param1JEditorPane.getSelectionEnd();
      if (i != j) {
        StyledDocument styledDocument = getStyledDocument(param1JEditorPane);
        styledDocument.setCharacterAttributes(i, j - i, param1AttributeSet, param1Boolean);
      } 
      StyledEditorKit styledEditorKit = getStyledEditorKit(param1JEditorPane);
      MutableAttributeSet mutableAttributeSet = styledEditorKit.getInputAttributes();
      if (param1Boolean)
        mutableAttributeSet.removeAttributes(mutableAttributeSet); 
      mutableAttributeSet.addAttributes(param1AttributeSet);
    }
    
    protected final void setParagraphAttributes(JEditorPane param1JEditorPane, AttributeSet param1AttributeSet, boolean param1Boolean) {
      int i = param1JEditorPane.getSelectionStart();
      int j = param1JEditorPane.getSelectionEnd();
      StyledDocument styledDocument = getStyledDocument(param1JEditorPane);
      styledDocument.setParagraphAttributes(i, j - i, param1AttributeSet, param1Boolean);
    }
  }
  
  static class StyledViewFactory implements ViewFactory {
    public View create(Element param1Element) {
      String str = param1Element.getName();
      if (str != null) {
        if (str.equals("content"))
          return new LabelView(param1Element); 
        if (str.equals("paragraph"))
          return new ParagraphView(param1Element); 
        if (str.equals("section"))
          return new BoxView(param1Element, 1); 
        if (str.equals("component"))
          return new ComponentView(param1Element); 
        if (str.equals("icon"))
          return new IconView(param1Element); 
      } 
      return new LabelView(param1Element);
    }
  }
  
  public static class UnderlineAction extends StyledTextAction {
    public UnderlineAction() { super("font-underline"); }
    
    public void actionPerformed(ActionEvent param1ActionEvent) {
      JEditorPane jEditorPane = getEditor(param1ActionEvent);
      if (jEditorPane != null) {
        StyledEditorKit styledEditorKit = getStyledEditorKit(jEditorPane);
        MutableAttributeSet mutableAttributeSet = styledEditorKit.getInputAttributes();
        boolean bool = !StyleConstants.isUnderline(mutableAttributeSet);
        SimpleAttributeSet simpleAttributeSet = new SimpleAttributeSet();
        StyleConstants.setUnderline(simpleAttributeSet, bool);
        setCharacterAttributes(jEditorPane, simpleAttributeSet, false);
      } 
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\text\StyledEditorKit.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */