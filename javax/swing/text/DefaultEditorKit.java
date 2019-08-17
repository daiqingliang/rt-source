package javax.swing.text;

import java.awt.ComponentOrientation;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Toolkit;
import java.awt.event.ActionEvent;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import javax.swing.Action;
import javax.swing.UIManager;
import sun.awt.SunToolkit;

public class DefaultEditorKit extends EditorKit {
  public static final String EndOfLineStringProperty = "__EndOfLine__";
  
  public static final String insertContentAction = "insert-content";
  
  public static final String insertBreakAction = "insert-break";
  
  public static final String insertTabAction = "insert-tab";
  
  public static final String deletePrevCharAction = "delete-previous";
  
  public static final String deleteNextCharAction = "delete-next";
  
  public static final String deleteNextWordAction = "delete-next-word";
  
  public static final String deletePrevWordAction = "delete-previous-word";
  
  public static final String readOnlyAction = "set-read-only";
  
  public static final String writableAction = "set-writable";
  
  public static final String cutAction = "cut-to-clipboard";
  
  public static final String copyAction = "copy-to-clipboard";
  
  public static final String pasteAction = "paste-from-clipboard";
  
  public static final String beepAction = "beep";
  
  public static final String pageUpAction = "page-up";
  
  public static final String pageDownAction = "page-down";
  
  static final String selectionPageUpAction = "selection-page-up";
  
  static final String selectionPageDownAction = "selection-page-down";
  
  static final String selectionPageLeftAction = "selection-page-left";
  
  static final String selectionPageRightAction = "selection-page-right";
  
  public static final String forwardAction = "caret-forward";
  
  public static final String backwardAction = "caret-backward";
  
  public static final String selectionForwardAction = "selection-forward";
  
  public static final String selectionBackwardAction = "selection-backward";
  
  public static final String upAction = "caret-up";
  
  public static final String downAction = "caret-down";
  
  public static final String selectionUpAction = "selection-up";
  
  public static final String selectionDownAction = "selection-down";
  
  public static final String beginWordAction = "caret-begin-word";
  
  public static final String endWordAction = "caret-end-word";
  
  public static final String selectionBeginWordAction = "selection-begin-word";
  
  public static final String selectionEndWordAction = "selection-end-word";
  
  public static final String previousWordAction = "caret-previous-word";
  
  public static final String nextWordAction = "caret-next-word";
  
  public static final String selectionPreviousWordAction = "selection-previous-word";
  
  public static final String selectionNextWordAction = "selection-next-word";
  
  public static final String beginLineAction = "caret-begin-line";
  
  public static final String endLineAction = "caret-end-line";
  
  public static final String selectionBeginLineAction = "selection-begin-line";
  
  public static final String selectionEndLineAction = "selection-end-line";
  
  public static final String beginParagraphAction = "caret-begin-paragraph";
  
  public static final String endParagraphAction = "caret-end-paragraph";
  
  public static final String selectionBeginParagraphAction = "selection-begin-paragraph";
  
  public static final String selectionEndParagraphAction = "selection-end-paragraph";
  
  public static final String beginAction = "caret-begin";
  
  public static final String endAction = "caret-end";
  
  public static final String selectionBeginAction = "selection-begin";
  
  public static final String selectionEndAction = "selection-end";
  
  public static final String selectWordAction = "select-word";
  
  public static final String selectLineAction = "select-line";
  
  public static final String selectParagraphAction = "select-paragraph";
  
  public static final String selectAllAction = "select-all";
  
  static final String unselectAction = "unselect";
  
  static final String toggleComponentOrientationAction = "toggle-componentOrientation";
  
  public static final String defaultKeyTypedAction = "default-typed";
  
  private static final Action[] defaultActions = { 
      new InsertContentAction(), new DeletePrevCharAction(), new DeleteNextCharAction(), new ReadOnlyAction(), new DeleteWordAction("delete-previous-word"), new DeleteWordAction("delete-next-word"), new WritableAction(), new CutAction(), new CopyAction(), new PasteAction(), 
      new VerticalPageAction("page-up", -1, false), new VerticalPageAction("page-down", 1, false), new VerticalPageAction("selection-page-up", -1, true), new VerticalPageAction("selection-page-down", 1, true), new PageAction("selection-page-left", true, true), new PageAction("selection-page-right", false, true), new InsertBreakAction(), new BeepAction(), new NextVisualPositionAction("caret-forward", false, 3), new NextVisualPositionAction("caret-backward", false, 7), 
      new NextVisualPositionAction("selection-forward", true, 3), new NextVisualPositionAction("selection-backward", true, 7), new NextVisualPositionAction("caret-up", false, 1), new NextVisualPositionAction("caret-down", false, 5), new NextVisualPositionAction("selection-up", true, 1), new NextVisualPositionAction("selection-down", true, 5), new BeginWordAction("caret-begin-word", false), new EndWordAction("caret-end-word", false), new BeginWordAction("selection-begin-word", true), new EndWordAction("selection-end-word", true), 
      new PreviousWordAction("caret-previous-word", false), new NextWordAction("caret-next-word", false), new PreviousWordAction("selection-previous-word", true), new NextWordAction("selection-next-word", true), new BeginLineAction("caret-begin-line", false), new EndLineAction("caret-end-line", false), new BeginLineAction("selection-begin-line", true), new EndLineAction("selection-end-line", true), new BeginParagraphAction("caret-begin-paragraph", false), new EndParagraphAction("caret-end-paragraph", false), 
      new BeginParagraphAction("selection-begin-paragraph", true), new EndParagraphAction("selection-end-paragraph", true), new BeginAction("caret-begin", false), new EndAction("caret-end", false), new BeginAction("selection-begin", true), new EndAction("selection-end", true), new DefaultKeyTypedAction(), new InsertTabAction(), new SelectWordAction(), new SelectLineAction(), 
      new SelectParagraphAction(), new SelectAllAction(), new UnselectAction(), new ToggleComponentOrientationAction(), new DumpModelAction() };
  
  public String getContentType() { return "text/plain"; }
  
  public ViewFactory getViewFactory() { return null; }
  
  public Action[] getActions() { return (Action[])defaultActions.clone(); }
  
  public Caret createCaret() { return null; }
  
  public Document createDefaultDocument() { return new PlainDocument(); }
  
  public void read(InputStream paramInputStream, Document paramDocument, int paramInt) throws IOException, BadLocationException { read(new InputStreamReader(paramInputStream), paramDocument, paramInt); }
  
  public void write(OutputStream paramOutputStream, Document paramDocument, int paramInt1, int paramInt2) throws IOException, BadLocationException {
    OutputStreamWriter outputStreamWriter = new OutputStreamWriter(paramOutputStream);
    write(outputStreamWriter, paramDocument, paramInt1, paramInt2);
    outputStreamWriter.flush();
  }
  
  MutableAttributeSet getInputAttributes() { return null; }
  
  public void read(Reader paramReader, Document paramDocument, int paramInt) throws IOException, BadLocationException {
    char[] arrayOfChar = new char[4096];
    boolean bool1 = false;
    boolean bool2 = false;
    boolean bool3 = false;
    boolean bool4 = (paramDocument.getLength() == 0) ? 1 : 0;
    MutableAttributeSet mutableAttributeSet = getInputAttributes();
    int i;
    while ((i = paramReader.read(arrayOfChar, 0, arrayOfChar.length)) != -1) {
      int j = 0;
      for (byte b = 0; b < i; b++) {
        switch (arrayOfChar[b]) {
          case '\r':
            if (bool1) {
              bool3 = true;
              if (!b) {
                paramDocument.insertString(paramInt, "\n", mutableAttributeSet);
                paramInt++;
                break;
              } 
              arrayOfChar[b - true] = '\n';
              break;
            } 
            bool1 = true;
            break;
          case '\n':
            if (bool1) {
              if (b > j + true) {
                paramDocument.insertString(paramInt, new String(arrayOfChar, j, b - j - true), mutableAttributeSet);
                paramInt += b - j - 1;
              } 
              bool1 = false;
              j = b;
              bool2 = true;
            } 
            break;
          default:
            if (bool1) {
              bool3 = true;
              if (b == 0) {
                paramDocument.insertString(paramInt, "\n", mutableAttributeSet);
                paramInt++;
              } else {
                arrayOfChar[b - 1] = '\n';
              } 
              bool1 = false;
            } 
            break;
        } 
      } 
      if (j < i) {
        if (bool1) {
          if (j < i - 1) {
            paramDocument.insertString(paramInt, new String(arrayOfChar, j, i - j - 1), mutableAttributeSet);
            paramInt += i - j - 1;
          } 
          continue;
        } 
        paramDocument.insertString(paramInt, new String(arrayOfChar, j, i - j), mutableAttributeSet);
        paramInt += i - j;
      } 
    } 
    if (bool1) {
      paramDocument.insertString(paramInt, "\n", mutableAttributeSet);
      bool3 = true;
    } 
    if (bool4)
      if (bool2) {
        paramDocument.putProperty("__EndOfLine__", "\r\n");
      } else if (bool3) {
        paramDocument.putProperty("__EndOfLine__", "\r");
      } else {
        paramDocument.putProperty("__EndOfLine__", "\n");
      }  
  }
  
  public void write(Writer paramWriter, Document paramDocument, int paramInt1, int paramInt2) throws IOException, BadLocationException {
    String str;
    if (paramInt1 < 0 || paramInt1 + paramInt2 > paramDocument.getLength())
      throw new BadLocationException("DefaultEditorKit.write", paramInt1); 
    Segment segment = new Segment();
    int i = paramInt2;
    int j = paramInt1;
    Object object = paramDocument.getProperty("__EndOfLine__");
    if (object == null)
      try {
        object = System.getProperty("line.separator");
      } catch (SecurityException null) {} 
    if (object instanceof String) {
      str = (String)object;
    } else {
      str = null;
    } 
    if (object != null && !str.equals("\n")) {
      while (i > 0) {
        int k = Math.min(i, 4096);
        paramDocument.getText(j, k, segment);
        int m = segment.offset;
        char[] arrayOfChar = segment.array;
        int n = m + segment.count;
        for (int i1 = m; i1 < n; i1++) {
          if (arrayOfChar[i1] == '\n') {
            if (i1 > m)
              paramWriter.write(arrayOfChar, m, i1 - m); 
            paramWriter.write(str);
            m = i1 + 1;
          } 
        } 
        if (n > m)
          paramWriter.write(arrayOfChar, m, n - m); 
        j += k;
        i -= k;
      } 
    } else {
      while (i > 0) {
        int k = Math.min(i, 4096);
        paramDocument.getText(j, k, segment);
        paramWriter.write(segment.array, segment.offset, segment.count);
        j += k;
        i -= k;
      } 
    } 
    paramWriter.flush();
  }
  
  public static class BeepAction extends TextAction {
    public BeepAction() { super("beep"); }
    
    public void actionPerformed(ActionEvent param1ActionEvent) {
      JTextComponent jTextComponent = getTextComponent(param1ActionEvent);
      UIManager.getLookAndFeel().provideErrorFeedback(jTextComponent);
    }
  }
  
  static class BeginAction extends TextAction {
    private boolean select;
    
    BeginAction(String param1String, boolean param1Boolean) {
      super(param1String);
      this.select = param1Boolean;
    }
    
    public void actionPerformed(ActionEvent param1ActionEvent) {
      JTextComponent jTextComponent = getTextComponent(param1ActionEvent);
      if (jTextComponent != null)
        if (this.select) {
          jTextComponent.moveCaretPosition(0);
        } else {
          jTextComponent.setCaretPosition(0);
        }  
    }
  }
  
  static class BeginLineAction extends TextAction {
    private boolean select;
    
    BeginLineAction(String param1String, boolean param1Boolean) {
      super(param1String);
      this.select = param1Boolean;
    }
    
    public void actionPerformed(ActionEvent param1ActionEvent) {
      JTextComponent jTextComponent = getTextComponent(param1ActionEvent);
      if (jTextComponent != null)
        try {
          int i = jTextComponent.getCaretPosition();
          int j = Utilities.getRowStart(jTextComponent, i);
          if (this.select) {
            jTextComponent.moveCaretPosition(j);
          } else {
            jTextComponent.setCaretPosition(j);
          } 
        } catch (BadLocationException badLocationException) {
          UIManager.getLookAndFeel().provideErrorFeedback(jTextComponent);
        }  
    }
  }
  
  static class BeginParagraphAction extends TextAction {
    private boolean select;
    
    BeginParagraphAction(String param1String, boolean param1Boolean) {
      super(param1String);
      this.select = param1Boolean;
    }
    
    public void actionPerformed(ActionEvent param1ActionEvent) {
      JTextComponent jTextComponent = getTextComponent(param1ActionEvent);
      if (jTextComponent != null) {
        int i = jTextComponent.getCaretPosition();
        Element element = Utilities.getParagraphElement(jTextComponent, i);
        i = element.getStartOffset();
        if (this.select) {
          jTextComponent.moveCaretPosition(i);
        } else {
          jTextComponent.setCaretPosition(i);
        } 
      } 
    }
  }
  
  static class BeginWordAction extends TextAction {
    private boolean select;
    
    BeginWordAction(String param1String, boolean param1Boolean) {
      super(param1String);
      this.select = param1Boolean;
    }
    
    public void actionPerformed(ActionEvent param1ActionEvent) {
      JTextComponent jTextComponent = getTextComponent(param1ActionEvent);
      if (jTextComponent != null)
        try {
          int i = jTextComponent.getCaretPosition();
          int j = Utilities.getWordStart(jTextComponent, i);
          if (this.select) {
            jTextComponent.moveCaretPosition(j);
          } else {
            jTextComponent.setCaretPosition(j);
          } 
        } catch (BadLocationException badLocationException) {
          UIManager.getLookAndFeel().provideErrorFeedback(jTextComponent);
        }  
    }
  }
  
  public static class CopyAction extends TextAction {
    public CopyAction() { super("copy-to-clipboard"); }
    
    public void actionPerformed(ActionEvent param1ActionEvent) {
      JTextComponent jTextComponent = getTextComponent(param1ActionEvent);
      if (jTextComponent != null)
        jTextComponent.copy(); 
    }
  }
  
  public static class CutAction extends TextAction {
    public CutAction() { super("cut-to-clipboard"); }
    
    public void actionPerformed(ActionEvent param1ActionEvent) {
      JTextComponent jTextComponent = getTextComponent(param1ActionEvent);
      if (jTextComponent != null)
        jTextComponent.cut(); 
    }
  }
  
  public static class DefaultKeyTypedAction extends TextAction {
    public DefaultKeyTypedAction() { super("default-typed"); }
    
    public void actionPerformed(ActionEvent param1ActionEvent) {
      JTextComponent jTextComponent = getTextComponent(param1ActionEvent);
      if (jTextComponent != null && param1ActionEvent != null) {
        if (!jTextComponent.isEditable() || !jTextComponent.isEnabled())
          return; 
        String str = param1ActionEvent.getActionCommand();
        int i = param1ActionEvent.getModifiers();
        if (str != null && str.length() > 0) {
          boolean bool = true;
          Toolkit toolkit = Toolkit.getDefaultToolkit();
          if (toolkit instanceof SunToolkit)
            bool = ((SunToolkit)toolkit).isPrintableCharacterModifiersMask(i); 
          if (bool) {
            char c = str.charAt(0);
            if (c >= ' ' && c != '')
              jTextComponent.replaceSelection(str); 
          } 
        } 
      } 
    }
  }
  
  static class DeleteNextCharAction extends TextAction {
    DeleteNextCharAction() { super("delete-next"); }
    
    public void actionPerformed(ActionEvent param1ActionEvent) {
      JTextComponent jTextComponent = getTextComponent(param1ActionEvent);
      boolean bool = true;
      if (jTextComponent != null && jTextComponent.isEditable())
        try {
          Document document = jTextComponent.getDocument();
          Caret caret = jTextComponent.getCaret();
          int i = caret.getDot();
          int j = caret.getMark();
          if (i != j) {
            document.remove(Math.min(i, j), Math.abs(i - j));
            bool = false;
          } else if (i < document.getLength()) {
            byte b = 1;
            if (i < document.getLength() - 1) {
              String str = document.getText(i, 2);
              char c1 = str.charAt(0);
              char c2 = str.charAt(1);
              if (c1 >= '?' && c1 <= '?' && c2 >= '?' && c2 <= '?')
                b = 2; 
            } 
            document.remove(i, b);
            bool = false;
          } 
        } catch (BadLocationException badLocationException) {} 
      if (bool)
        UIManager.getLookAndFeel().provideErrorFeedback(jTextComponent); 
    }
  }
  
  static class DeletePrevCharAction extends TextAction {
    DeletePrevCharAction() { super("delete-previous"); }
    
    public void actionPerformed(ActionEvent param1ActionEvent) {
      JTextComponent jTextComponent = getTextComponent(param1ActionEvent);
      boolean bool = true;
      if (jTextComponent != null && jTextComponent.isEditable())
        try {
          Document document = jTextComponent.getDocument();
          Caret caret = jTextComponent.getCaret();
          int i = caret.getDot();
          int j = caret.getMark();
          if (i != j) {
            document.remove(Math.min(i, j), Math.abs(i - j));
            bool = false;
          } else if (i > 0) {
            int k = 1;
            if (i > 1) {
              String str = document.getText(i - 2, 2);
              char c1 = str.charAt(0);
              char c2 = str.charAt(1);
              if (c1 >= '?' && c1 <= '?' && c2 >= '?' && c2 <= '?')
                k = 2; 
            } 
            document.remove(i - k, k);
            bool = false;
          } 
        } catch (BadLocationException badLocationException) {} 
      if (bool)
        UIManager.getLookAndFeel().provideErrorFeedback(jTextComponent); 
    }
  }
  
  static class DeleteWordAction extends TextAction {
    DeleteWordAction(String param1String) {
      super(param1String);
      assert param1String == "delete-previous-word" || param1String == "delete-next-word";
    }
    
    public void actionPerformed(ActionEvent param1ActionEvent) {
      JTextComponent jTextComponent = getTextComponent(param1ActionEvent);
      if (jTextComponent != null && param1ActionEvent != null) {
        if (!jTextComponent.isEditable() || !jTextComponent.isEnabled()) {
          UIManager.getLookAndFeel().provideErrorFeedback(jTextComponent);
          return;
        } 
        boolean bool = true;
        try {
          int j;
          int i = jTextComponent.getSelectionStart();
          Element element = Utilities.getParagraphElement(jTextComponent, i);
          if ("delete-next-word" == getValue("Name")) {
            j = Utilities.getNextWordInParagraph(jTextComponent, element, i, false);
            if (j == -1) {
              int n = element.getEndOffset();
              if (i == n - 1) {
                j = n;
              } else {
                j = n - 1;
              } 
            } 
          } else {
            j = Utilities.getPrevWordInParagraph(jTextComponent, element, i);
            if (j == -1) {
              int n = element.getStartOffset();
              if (i == n) {
                j = n - 1;
              } else {
                j = n;
              } 
            } 
          } 
          int k = Math.min(i, j);
          int m = Math.abs(j - i);
          if (k >= 0) {
            jTextComponent.getDocument().remove(k, m);
            bool = false;
          } 
        } catch (BadLocationException badLocationException) {}
        if (bool)
          UIManager.getLookAndFeel().provideErrorFeedback(jTextComponent); 
      } 
    }
  }
  
  static class DumpModelAction extends TextAction {
    DumpModelAction() { super("dump-model"); }
    
    public void actionPerformed(ActionEvent param1ActionEvent) {
      JTextComponent jTextComponent = getTextComponent(param1ActionEvent);
      if (jTextComponent != null) {
        Document document = jTextComponent.getDocument();
        if (document instanceof AbstractDocument)
          ((AbstractDocument)document).dump(System.err); 
      } 
    }
  }
  
  static class EndAction extends TextAction {
    private boolean select;
    
    EndAction(String param1String, boolean param1Boolean) {
      super(param1String);
      this.select = param1Boolean;
    }
    
    public void actionPerformed(ActionEvent param1ActionEvent) {
      JTextComponent jTextComponent = getTextComponent(param1ActionEvent);
      if (jTextComponent != null) {
        Document document = jTextComponent.getDocument();
        int i = document.getLength();
        if (this.select) {
          jTextComponent.moveCaretPosition(i);
        } else {
          jTextComponent.setCaretPosition(i);
        } 
      } 
    }
  }
  
  static class EndLineAction extends TextAction {
    private boolean select;
    
    EndLineAction(String param1String, boolean param1Boolean) {
      super(param1String);
      this.select = param1Boolean;
    }
    
    public void actionPerformed(ActionEvent param1ActionEvent) {
      JTextComponent jTextComponent = getTextComponent(param1ActionEvent);
      if (jTextComponent != null)
        try {
          int i = jTextComponent.getCaretPosition();
          int j = Utilities.getRowEnd(jTextComponent, i);
          if (this.select) {
            jTextComponent.moveCaretPosition(j);
          } else {
            jTextComponent.setCaretPosition(j);
          } 
        } catch (BadLocationException badLocationException) {
          UIManager.getLookAndFeel().provideErrorFeedback(jTextComponent);
        }  
    }
  }
  
  static class EndParagraphAction extends TextAction {
    private boolean select;
    
    EndParagraphAction(String param1String, boolean param1Boolean) {
      super(param1String);
      this.select = param1Boolean;
    }
    
    public void actionPerformed(ActionEvent param1ActionEvent) {
      JTextComponent jTextComponent = getTextComponent(param1ActionEvent);
      if (jTextComponent != null) {
        int i = jTextComponent.getCaretPosition();
        Element element = Utilities.getParagraphElement(jTextComponent, i);
        i = Math.min(jTextComponent.getDocument().getLength(), element.getEndOffset());
        if (this.select) {
          jTextComponent.moveCaretPosition(i);
        } else {
          jTextComponent.setCaretPosition(i);
        } 
      } 
    }
  }
  
  static class EndWordAction extends TextAction {
    private boolean select;
    
    EndWordAction(String param1String, boolean param1Boolean) {
      super(param1String);
      this.select = param1Boolean;
    }
    
    public void actionPerformed(ActionEvent param1ActionEvent) {
      JTextComponent jTextComponent = getTextComponent(param1ActionEvent);
      if (jTextComponent != null)
        try {
          int i = jTextComponent.getCaretPosition();
          int j = Utilities.getWordEnd(jTextComponent, i);
          if (this.select) {
            jTextComponent.moveCaretPosition(j);
          } else {
            jTextComponent.setCaretPosition(j);
          } 
        } catch (BadLocationException badLocationException) {
          UIManager.getLookAndFeel().provideErrorFeedback(jTextComponent);
        }  
    }
  }
  
  public static class InsertBreakAction extends TextAction {
    public InsertBreakAction() { super("insert-break"); }
    
    public void actionPerformed(ActionEvent param1ActionEvent) {
      JTextComponent jTextComponent = getTextComponent(param1ActionEvent);
      if (jTextComponent != null) {
        if (!jTextComponent.isEditable() || !jTextComponent.isEnabled()) {
          UIManager.getLookAndFeel().provideErrorFeedback(jTextComponent);
          return;
        } 
        jTextComponent.replaceSelection("\n");
      } 
    }
  }
  
  public static class InsertContentAction extends TextAction {
    public InsertContentAction() { super("insert-content"); }
    
    public void actionPerformed(ActionEvent param1ActionEvent) {
      JTextComponent jTextComponent = getTextComponent(param1ActionEvent);
      if (jTextComponent != null && param1ActionEvent != null) {
        if (!jTextComponent.isEditable() || !jTextComponent.isEnabled()) {
          UIManager.getLookAndFeel().provideErrorFeedback(jTextComponent);
          return;
        } 
        String str = param1ActionEvent.getActionCommand();
        if (str != null) {
          jTextComponent.replaceSelection(str);
        } else {
          UIManager.getLookAndFeel().provideErrorFeedback(jTextComponent);
        } 
      } 
    }
  }
  
  public static class InsertTabAction extends TextAction {
    public InsertTabAction() { super("insert-tab"); }
    
    public void actionPerformed(ActionEvent param1ActionEvent) {
      JTextComponent jTextComponent = getTextComponent(param1ActionEvent);
      if (jTextComponent != null) {
        if (!jTextComponent.isEditable() || !jTextComponent.isEnabled()) {
          UIManager.getLookAndFeel().provideErrorFeedback(jTextComponent);
          return;
        } 
        jTextComponent.replaceSelection("\t");
      } 
    }
  }
  
  static class NextVisualPositionAction extends TextAction {
    private boolean select;
    
    private int direction;
    
    NextVisualPositionAction(String param1String, boolean param1Boolean, int param1Int) {
      super(param1String);
      this.select = param1Boolean;
      this.direction = param1Int;
    }
    
    public void actionPerformed(ActionEvent param1ActionEvent) {
      JTextComponent jTextComponent = getTextComponent(param1ActionEvent);
      if (jTextComponent != null) {
        Caret caret = jTextComponent.getCaret();
        DefaultCaret defaultCaret = (caret instanceof DefaultCaret) ? (DefaultCaret)caret : null;
        int i = caret.getDot();
        Position.Bias[] arrayOfBias = new Position.Bias[1];
        Point point = caret.getMagicCaretPosition();
        try {
          if (point == null && (this.direction == 1 || this.direction == 5)) {
            Rectangle rectangle = (defaultCaret != null) ? jTextComponent.getUI().modelToView(jTextComponent, i, defaultCaret.getDotBias()) : jTextComponent.modelToView(i);
            point = new Point(rectangle.x, rectangle.y);
          } 
          NavigationFilter navigationFilter = jTextComponent.getNavigationFilter();
          if (navigationFilter != null) {
            i = navigationFilter.getNextVisualPositionFrom(jTextComponent, i, (defaultCaret != null) ? defaultCaret.getDotBias() : Position.Bias.Forward, this.direction, arrayOfBias);
          } else {
            i = jTextComponent.getUI().getNextVisualPositionFrom(jTextComponent, i, (defaultCaret != null) ? defaultCaret.getDotBias() : Position.Bias.Forward, this.direction, arrayOfBias);
          } 
          if (arrayOfBias[false] == null)
            arrayOfBias[0] = Position.Bias.Forward; 
          if (defaultCaret != null) {
            if (this.select) {
              defaultCaret.moveDot(i, arrayOfBias[0]);
            } else {
              defaultCaret.setDot(i, arrayOfBias[0]);
            } 
          } else if (this.select) {
            caret.moveDot(i);
          } else {
            caret.setDot(i);
          } 
          if (point != null && (this.direction == 1 || this.direction == 5))
            jTextComponent.getCaret().setMagicCaretPosition(point); 
        } catch (BadLocationException badLocationException) {}
      } 
    }
  }
  
  static class NextWordAction extends TextAction {
    private boolean select;
    
    NextWordAction(String param1String, boolean param1Boolean) {
      super(param1String);
      this.select = param1Boolean;
    }
    
    public void actionPerformed(ActionEvent param1ActionEvent) {
      JTextComponent jTextComponent = getTextComponent(param1ActionEvent);
      if (jTextComponent != null) {
        int i = jTextComponent.getCaretPosition();
        boolean bool = false;
        int j = i;
        Element element = Utilities.getParagraphElement(jTextComponent, i);
        try {
          i = Utilities.getNextWord(jTextComponent, i);
          if (i >= element.getEndOffset() && j != element.getEndOffset() - 1)
            i = element.getEndOffset() - 1; 
        } catch (BadLocationException badLocationException) {
          int k = jTextComponent.getDocument().getLength();
          if (i != k) {
            if (j != element.getEndOffset() - 1) {
              i = element.getEndOffset() - 1;
            } else {
              i = k;
            } 
          } else {
            bool = true;
          } 
        } 
        if (!bool) {
          if (this.select) {
            jTextComponent.moveCaretPosition(i);
          } else {
            jTextComponent.setCaretPosition(i);
          } 
        } else {
          UIManager.getLookAndFeel().provideErrorFeedback(jTextComponent);
        } 
      } 
    }
  }
  
  static class PageAction extends TextAction {
    private boolean select;
    
    private boolean left;
    
    public PageAction(String param1String, boolean param1Boolean1, boolean param1Boolean2) {
      super(param1String);
      this.select = param1Boolean2;
      this.left = param1Boolean1;
    }
    
    public void actionPerformed(ActionEvent param1ActionEvent) {
      JTextComponent jTextComponent = getTextComponent(param1ActionEvent);
      if (jTextComponent != null) {
        Rectangle rectangle = new Rectangle();
        jTextComponent.computeVisibleRect(rectangle);
        if (this.left) {
          rectangle.x = Math.max(0, rectangle.x - rectangle.width);
        } else {
          rectangle.x += rectangle.width;
        } 
        int i = jTextComponent.getCaretPosition();
        if (i != -1) {
          if (this.left) {
            i = jTextComponent.viewToModel(new Point(rectangle.x, rectangle.y));
          } else {
            i = jTextComponent.viewToModel(new Point(rectangle.x + rectangle.width - 1, rectangle.y + rectangle.height - 1));
          } 
          Document document = jTextComponent.getDocument();
          if (i != 0 && i > document.getLength() - 1) {
            i = document.getLength() - 1;
          } else if (i < 0) {
            i = 0;
          } 
          if (this.select) {
            jTextComponent.moveCaretPosition(i);
          } else {
            jTextComponent.setCaretPosition(i);
          } 
        } 
      } 
    }
  }
  
  public static class PasteAction extends TextAction {
    public PasteAction() { super("paste-from-clipboard"); }
    
    public void actionPerformed(ActionEvent param1ActionEvent) {
      JTextComponent jTextComponent = getTextComponent(param1ActionEvent);
      if (jTextComponent != null)
        jTextComponent.paste(); 
    }
  }
  
  static class PreviousWordAction extends TextAction {
    private boolean select;
    
    PreviousWordAction(String param1String, boolean param1Boolean) {
      super(param1String);
      this.select = param1Boolean;
    }
    
    public void actionPerformed(ActionEvent param1ActionEvent) {
      JTextComponent jTextComponent = getTextComponent(param1ActionEvent);
      if (jTextComponent != null) {
        int i = jTextComponent.getCaretPosition();
        boolean bool = false;
        try {
          Element element = Utilities.getParagraphElement(jTextComponent, i);
          i = Utilities.getPreviousWord(jTextComponent, i);
          if (i < element.getStartOffset())
            i = Utilities.getParagraphElement(jTextComponent, i).getEndOffset() - 1; 
        } catch (BadLocationException badLocationException) {
          if (i != 0) {
            i = 0;
          } else {
            bool = true;
          } 
        } 
        if (!bool) {
          if (this.select) {
            jTextComponent.moveCaretPosition(i);
          } else {
            jTextComponent.setCaretPosition(i);
          } 
        } else {
          UIManager.getLookAndFeel().provideErrorFeedback(jTextComponent);
        } 
      } 
    }
  }
  
  static class ReadOnlyAction extends TextAction {
    ReadOnlyAction() { super("set-read-only"); }
    
    public void actionPerformed(ActionEvent param1ActionEvent) {
      JTextComponent jTextComponent = getTextComponent(param1ActionEvent);
      if (jTextComponent != null)
        jTextComponent.setEditable(false); 
    }
  }
  
  static class SelectAllAction extends TextAction {
    SelectAllAction() { super("select-all"); }
    
    public void actionPerformed(ActionEvent param1ActionEvent) {
      JTextComponent jTextComponent = getTextComponent(param1ActionEvent);
      if (jTextComponent != null) {
        Document document = jTextComponent.getDocument();
        jTextComponent.setCaretPosition(0);
        jTextComponent.moveCaretPosition(document.getLength());
      } 
    }
  }
  
  static class SelectLineAction extends TextAction {
    private Action start = new DefaultEditorKit.BeginLineAction("pigdog", false);
    
    private Action end = new DefaultEditorKit.EndLineAction("pigdog", true);
    
    SelectLineAction() { super("select-line"); }
    
    public void actionPerformed(ActionEvent param1ActionEvent) {
      this.start.actionPerformed(param1ActionEvent);
      this.end.actionPerformed(param1ActionEvent);
    }
  }
  
  static class SelectParagraphAction extends TextAction {
    private Action start = new DefaultEditorKit.BeginParagraphAction("pigdog", false);
    
    private Action end = new DefaultEditorKit.EndParagraphAction("pigdog", true);
    
    SelectParagraphAction() { super("select-paragraph"); }
    
    public void actionPerformed(ActionEvent param1ActionEvent) {
      this.start.actionPerformed(param1ActionEvent);
      this.end.actionPerformed(param1ActionEvent);
    }
  }
  
  static class SelectWordAction extends TextAction {
    private Action start = new DefaultEditorKit.BeginWordAction("pigdog", false);
    
    private Action end = new DefaultEditorKit.EndWordAction("pigdog", true);
    
    SelectWordAction() { super("select-word"); }
    
    public void actionPerformed(ActionEvent param1ActionEvent) {
      this.start.actionPerformed(param1ActionEvent);
      this.end.actionPerformed(param1ActionEvent);
    }
  }
  
  static class ToggleComponentOrientationAction extends TextAction {
    ToggleComponentOrientationAction() { super("toggle-componentOrientation"); }
    
    public void actionPerformed(ActionEvent param1ActionEvent) {
      JTextComponent jTextComponent = getTextComponent(param1ActionEvent);
      if (jTextComponent != null) {
        ComponentOrientation componentOrientation2;
        ComponentOrientation componentOrientation1 = jTextComponent.getComponentOrientation();
        if (componentOrientation1 == ComponentOrientation.RIGHT_TO_LEFT) {
          componentOrientation2 = ComponentOrientation.LEFT_TO_RIGHT;
        } else {
          componentOrientation2 = ComponentOrientation.RIGHT_TO_LEFT;
        } 
        jTextComponent.setComponentOrientation(componentOrientation2);
        jTextComponent.repaint();
      } 
    }
  }
  
  static class UnselectAction extends TextAction {
    UnselectAction() { super("unselect"); }
    
    public void actionPerformed(ActionEvent param1ActionEvent) {
      JTextComponent jTextComponent = getTextComponent(param1ActionEvent);
      if (jTextComponent != null)
        jTextComponent.setCaretPosition(jTextComponent.getCaretPosition()); 
    }
  }
  
  static class VerticalPageAction extends TextAction {
    private boolean select;
    
    private int direction;
    
    public VerticalPageAction(String param1String, int param1Int, boolean param1Boolean) {
      super(param1String);
      this.select = param1Boolean;
      this.direction = param1Int;
    }
    
    public void actionPerformed(ActionEvent param1ActionEvent) {
      JTextComponent jTextComponent = getTextComponent(param1ActionEvent);
      if (jTextComponent != null) {
        Rectangle rectangle1 = jTextComponent.getVisibleRect();
        Rectangle rectangle2 = new Rectangle(rectangle1);
        int i = jTextComponent.getCaretPosition();
        int j = this.direction * jTextComponent.getScrollableBlockIncrement(rectangle1, 1, this.direction);
        int k = rectangle1.y;
        Caret caret = jTextComponent.getCaret();
        Point point = caret.getMagicCaretPosition();
        if (i != -1) {
          try {
            Rectangle rectangle = jTextComponent.modelToView(i);
            int m = (point != null) ? point.x : rectangle.x;
            int n = rectangle.height;
            if (n > 0)
              j = j / n * n; 
            rectangle2.y = constrainY(jTextComponent, k + j, rectangle1.height);
            if (rectangle1.contains(rectangle.x, rectangle.y)) {
              i1 = jTextComponent.viewToModel(new Point(m, constrainY(jTextComponent, rectangle.y + j, 0)));
            } else if (this.direction == -1) {
              i1 = jTextComponent.viewToModel(new Point(m, rectangle2.y));
            } else {
              i1 = jTextComponent.viewToModel(new Point(m, rectangle2.y + rectangle1.height));
            } 
            int i1 = constrainOffset(jTextComponent, i1);
            if (i1 != i) {
              int i2 = getAdjustedY(jTextComponent, rectangle2, i1);
              if ((this.direction == -1 && i2 <= k) || (this.direction == 1 && i2 >= k)) {
                rectangle2.y = i2;
                if (this.select) {
                  jTextComponent.moveCaretPosition(i1);
                } else {
                  jTextComponent.setCaretPosition(i1);
                } 
              } 
            } 
          } catch (BadLocationException badLocationException) {}
        } else {
          rectangle2.y = constrainY(jTextComponent, k + j, rectangle1.height);
        } 
        if (point != null)
          caret.setMagicCaretPosition(point); 
        jTextComponent.scrollRectToVisible(rectangle2);
      } 
    }
    
    private int constrainY(JTextComponent param1JTextComponent, int param1Int1, int param1Int2) {
      if (param1Int1 < 0) {
        param1Int1 = 0;
      } else if (param1Int1 + param1Int2 > param1JTextComponent.getHeight()) {
        param1Int1 = Math.max(0, param1JTextComponent.getHeight() - param1Int2);
      } 
      return param1Int1;
    }
    
    private int constrainOffset(JTextComponent param1JTextComponent, int param1Int) {
      Document document = param1JTextComponent.getDocument();
      if (param1Int != 0 && param1Int > document.getLength())
        param1Int = document.getLength(); 
      if (param1Int < 0)
        param1Int = 0; 
      return param1Int;
    }
    
    private int getAdjustedY(JTextComponent param1JTextComponent, Rectangle param1Rectangle, int param1Int) {
      int i = param1Rectangle.y;
      try {
        Rectangle rectangle = param1JTextComponent.modelToView(param1Int);
        if (rectangle.y < param1Rectangle.y) {
          i = rectangle.y;
        } else if (rectangle.y > param1Rectangle.y + param1Rectangle.height || rectangle.y + rectangle.height > param1Rectangle.y + param1Rectangle.height) {
          i = rectangle.y + rectangle.height - param1Rectangle.height;
        } 
      } catch (BadLocationException badLocationException) {}
      return i;
    }
  }
  
  static class WritableAction extends TextAction {
    WritableAction() { super("set-writable"); }
    
    public void actionPerformed(ActionEvent param1ActionEvent) {
      JTextComponent jTextComponent = getTextComponent(param1ActionEvent);
      if (jTextComponent != null)
        jTextComponent.setEditable(true); 
    }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\text\DefaultEditorKit.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */