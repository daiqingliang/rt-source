package sun.swing.text;

import java.awt.Component;
import java.awt.ComponentOrientation;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Insets;
import java.awt.Rectangle;
import java.awt.font.FontRenderContext;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.lang.reflect.InvocationTargetException;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.FutureTask;
import java.util.concurrent.atomic.AtomicReference;
import javax.swing.BorderFactory;
import javax.swing.CellRendererPane;
import javax.swing.JEditorPane;
import javax.swing.JPasswordField;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.JViewport;
import javax.swing.SwingUtilities;
import javax.swing.border.Border;
import javax.swing.border.TitledBorder;
import javax.swing.text.AbstractDocument;
import javax.swing.text.BadLocationException;
import javax.swing.text.Document;
import javax.swing.text.EditorKit;
import javax.swing.text.JTextComponent;
import javax.swing.text.html.HTML;
import javax.swing.text.html.HTMLDocument;
import sun.font.FontDesignMetrics;

public class TextComponentPrintable implements CountingPrintable {
  private static final int LIST_SIZE = 1000;
  
  private boolean isLayouted = false;
  
  private final JTextComponent textComponentToPrint;
  
  private final AtomicReference<FontRenderContext> frc = new AtomicReference(null);
  
  private final JTextComponent printShell;
  
  private final MessageFormat headerFormat;
  
  private final MessageFormat footerFormat;
  
  private static final float HEADER_FONT_SIZE = 18.0F;
  
  private static final float FOOTER_FONT_SIZE = 12.0F;
  
  private final Font headerFont;
  
  private final Font footerFont;
  
  private final List<IntegerSegment> rowsMetrics;
  
  private final List<IntegerSegment> pagesMetrics;
  
  private boolean needReadLock = false;
  
  public static Printable getPrintable(JTextComponent paramJTextComponent, MessageFormat paramMessageFormat1, MessageFormat paramMessageFormat2) {
    if (paramJTextComponent instanceof JEditorPane && isFrameSetDocument(paramJTextComponent.getDocument())) {
      List list = getFrames((JEditorPane)paramJTextComponent);
      ArrayList arrayList = new ArrayList();
      for (JEditorPane jEditorPane : list)
        arrayList.add((CountingPrintable)getPrintable(jEditorPane, paramMessageFormat1, paramMessageFormat2)); 
      return new CompoundPrintable(arrayList);
    } 
    return new TextComponentPrintable(paramJTextComponent, paramMessageFormat1, paramMessageFormat2);
  }
  
  private static boolean isFrameSetDocument(Document paramDocument) {
    boolean bool = false;
    if (paramDocument instanceof HTMLDocument) {
      HTMLDocument hTMLDocument = (HTMLDocument)paramDocument;
      if (hTMLDocument.getIterator(HTML.Tag.FRAME).isValid())
        bool = true; 
    } 
    return bool;
  }
  
  private static List<JEditorPane> getFrames(JEditorPane paramJEditorPane) {
    ArrayList arrayList = new ArrayList();
    getFrames(paramJEditorPane, arrayList);
    if (arrayList.size() == 0) {
      createFrames(paramJEditorPane);
      getFrames(paramJEditorPane, arrayList);
    } 
    return arrayList;
  }
  
  private static void getFrames(Container paramContainer, List<JEditorPane> paramList) {
    for (Component component : paramContainer.getComponents()) {
      if (component instanceof sun.swing.text.html.FrameEditorPaneTag && component instanceof JEditorPane) {
        paramList.add((JEditorPane)component);
      } else if (component instanceof Container) {
        getFrames((Container)component, paramList);
      } 
    } 
  }
  
  private static void createFrames(final JEditorPane editor) {
    Runnable runnable = new Runnable() {
        public void run() {
          CellRendererPane cellRendererPane = new CellRendererPane();
          cellRendererPane.add(editor);
          cellRendererPane.setSize(500, 500);
        }
      };
    if (SwingUtilities.isEventDispatchThread()) {
      runnable.run();
    } else {
      try {
        SwingUtilities.invokeAndWait(runnable);
      } catch (Exception exception) {
        if (exception instanceof RuntimeException)
          throw (RuntimeException)exception; 
        throw new RuntimeException(exception);
      } 
    } 
  }
  
  private TextComponentPrintable(JTextComponent paramJTextComponent, MessageFormat paramMessageFormat1, MessageFormat paramMessageFormat2) {
    this.textComponentToPrint = paramJTextComponent;
    this.headerFormat = paramMessageFormat1;
    this.footerFormat = paramMessageFormat2;
    this.headerFont = paramJTextComponent.getFont().deriveFont(1, 18.0F);
    this.footerFont = paramJTextComponent.getFont().deriveFont(0, 12.0F);
    this.pagesMetrics = Collections.synchronizedList(new ArrayList());
    this.rowsMetrics = new ArrayList(1000);
    this.printShell = createPrintShell(paramJTextComponent);
  }
  
  private JTextComponent createPrintShell(JTextComponent paramJTextComponent) {
    if (SwingUtilities.isEventDispatchThread())
      return createPrintShellOnEDT(paramJTextComponent); 
    FutureTask futureTask = new FutureTask(new Callable<JTextComponent>(this, paramJTextComponent) {
          public JTextComponent call() throws Exception { return TextComponentPrintable.this.createPrintShellOnEDT(textComponent); }
        });
    SwingUtilities.invokeLater(futureTask);
    try {
      return (JTextComponent)futureTask.get();
    } catch (InterruptedException interruptedException) {
      throw new RuntimeException(interruptedException);
    } catch (ExecutionException executionException) {
      Throwable throwable = executionException.getCause();
      if (throwable instanceof Error)
        throw (Error)throwable; 
      if (throwable instanceof RuntimeException)
        throw (RuntimeException)throwable; 
      throw new AssertionError(throwable);
    } 
  }
  
  private JTextComponent createPrintShellOnEDT(final JTextComponent textComponent) {
    assert SwingUtilities.isEventDispatchThread();
    JEditorPane jEditorPane = null;
    if (paramJTextComponent instanceof JPasswordField) {
      jEditorPane = new JPasswordField() {
          public FontMetrics getFontMetrics(Font param1Font) { return (TextComponentPrintable.this.frc.get() == null) ? super.getFontMetrics(param1Font) : FontDesignMetrics.getMetrics(param1Font, (FontRenderContext)TextComponentPrintable.this.frc.get()); }
        };
    } else if (paramJTextComponent instanceof JTextField) {
      JTextField jTextField = new JTextField() {
          public FontMetrics getFontMetrics(Font param1Font) { return (TextComponentPrintable.this.frc.get() == null) ? super.getFontMetrics(param1Font) : FontDesignMetrics.getMetrics(param1Font, (FontRenderContext)TextComponentPrintable.this.frc.get()); }
        };
    } else if (paramJTextComponent instanceof JTextArea) {
      JTextArea jTextArea = new JTextArea() {
          public FontMetrics getFontMetrics(Font param1Font) { return (TextComponentPrintable.this.frc.get() == null) ? super.getFontMetrics(param1Font) : FontDesignMetrics.getMetrics(param1Font, (FontRenderContext)TextComponentPrintable.this.frc.get()); }
        };
    } else if (paramJTextComponent instanceof JTextPane) {
      JTextPane jTextPane = new JTextPane() {
          public FontMetrics getFontMetrics(Font param1Font) { return (TextComponentPrintable.this.frc.get() == null) ? super.getFontMetrics(param1Font) : FontDesignMetrics.getMetrics(param1Font, (FontRenderContext)TextComponentPrintable.this.frc.get()); }
          
          public EditorKit getEditorKit() { return (getDocument() == textComponent.getDocument()) ? ((JTextPane)textComponent).getEditorKit() : super.getEditorKit(); }
        };
    } else if (paramJTextComponent instanceof JEditorPane) {
      jEditorPane = new JEditorPane() {
          public FontMetrics getFontMetrics(Font param1Font) { return (TextComponentPrintable.this.frc.get() == null) ? super.getFontMetrics(param1Font) : FontDesignMetrics.getMetrics(param1Font, (FontRenderContext)TextComponentPrintable.this.frc.get()); }
          
          public EditorKit getEditorKit() { return (getDocument() == textComponent.getDocument()) ? ((JEditorPane)textComponent).getEditorKit() : super.getEditorKit(); }
        };
    } 
    jEditorPane.setBorder(null);
    jEditorPane.setOpaque(paramJTextComponent.isOpaque());
    jEditorPane.setEditable(paramJTextComponent.isEditable());
    jEditorPane.setEnabled(paramJTextComponent.isEnabled());
    jEditorPane.setFont(paramJTextComponent.getFont());
    jEditorPane.setBackground(paramJTextComponent.getBackground());
    jEditorPane.setForeground(paramJTextComponent.getForeground());
    jEditorPane.setComponentOrientation(paramJTextComponent.getComponentOrientation());
    if (jEditorPane instanceof JEditorPane) {
      jEditorPane.putClientProperty("JEditorPane.honorDisplayProperties", paramJTextComponent.getClientProperty("JEditorPane.honorDisplayProperties"));
      jEditorPane.putClientProperty("JEditorPane.w3cLengthUnits", paramJTextComponent.getClientProperty("JEditorPane.w3cLengthUnits"));
      jEditorPane.putClientProperty("charset", paramJTextComponent.getClientProperty("charset"));
    } 
    jEditorPane.setDocument(paramJTextComponent.getDocument());
    return jEditorPane;
  }
  
  public int getNumberOfPages() { return this.pagesMetrics.size(); }
  
  public int print(final Graphics graphics, final PageFormat pf, final int pageIndex) throws PrinterException {
    int i;
    if (!this.isLayouted) {
      if (paramGraphics instanceof Graphics2D)
        this.frc.set(((Graphics2D)paramGraphics).getFontRenderContext()); 
      layout((int)Math.floor(paramPageFormat.getImageableWidth()));
      calculateRowsMetrics();
    } 
    if (!SwingUtilities.isEventDispatchThread()) {
      Callable<Integer> callable = new Callable<Integer>() {
          public Integer call() throws Exception { return Integer.valueOf(TextComponentPrintable.this.printOnEDT(graphics, pf, pageIndex)); }
        };
      FutureTask futureTask = new FutureTask(callable);
      SwingUtilities.invokeLater(futureTask);
      try {
        i = ((Integer)futureTask.get()).intValue();
      } catch (InterruptedException interruptedException) {
        throw new RuntimeException(interruptedException);
      } catch (ExecutionException executionException) {
        Throwable throwable = executionException.getCause();
        if (throwable instanceof PrinterException)
          throw (PrinterException)throwable; 
        if (throwable instanceof RuntimeException)
          throw (RuntimeException)throwable; 
        if (throwable instanceof Error)
          throw (Error)throwable; 
        throw new RuntimeException(throwable);
      } 
    } else {
      i = printOnEDT(paramGraphics, paramPageFormat, paramInt);
    } 
    return i;
  }
  
  private int printOnEDT(Graphics paramGraphics, PageFormat paramPageFormat, int paramInt) throws PrinterException {
    assert SwingUtilities.isEventDispatchThread();
    Border border = BorderFactory.createEmptyBorder();
    if (this.headerFormat != null || this.footerFormat != null) {
      Object[] arrayOfObject = { Integer.valueOf(paramInt + 1) };
      if (this.headerFormat != null)
        border = new TitledBorder(border, this.headerFormat.format(arrayOfObject), 2, 1, this.headerFont, this.printShell.getForeground()); 
      if (this.footerFormat != null)
        border = new TitledBorder(border, this.footerFormat.format(arrayOfObject), 2, 6, this.footerFont, this.printShell.getForeground()); 
    } 
    Insets insets = border.getBorderInsets(this.printShell);
    updatePagesMetrics(paramInt, (int)Math.floor(paramPageFormat.getImageableHeight()) - insets.top - insets.bottom);
    if (this.pagesMetrics.size() <= paramInt)
      return 1; 
    Graphics2D graphics2D = (Graphics2D)paramGraphics.create();
    graphics2D.translate(paramPageFormat.getImageableX(), paramPageFormat.getImageableY());
    border.paintBorder(this.printShell, graphics2D, 0, 0, (int)Math.floor(paramPageFormat.getImageableWidth()), (int)Math.floor(paramPageFormat.getImageableHeight()));
    graphics2D.translate(0, insets.top);
    Rectangle rectangle = new Rectangle(0, 0, (int)paramPageFormat.getWidth(), ((IntegerSegment)this.pagesMetrics.get(paramInt)).end - ((IntegerSegment)this.pagesMetrics.get(paramInt)).start + 1);
    graphics2D.clip(rectangle);
    int i = 0;
    if (ComponentOrientation.RIGHT_TO_LEFT == this.printShell.getComponentOrientation())
      i = (int)paramPageFormat.getImageableWidth() - this.printShell.getWidth(); 
    graphics2D.translate(i, -((IntegerSegment)this.pagesMetrics.get(paramInt)).start);
    this.printShell.print(graphics2D);
    graphics2D.dispose();
    return 0;
  }
  
  private void releaseReadLock() {
    assert !SwingUtilities.isEventDispatchThread();
    Document document = this.textComponentToPrint.getDocument();
    if (document instanceof AbstractDocument)
      try {
        ((AbstractDocument)document).readUnlock();
        this.needReadLock = true;
      } catch (Error error) {} 
  }
  
  private void acquireReadLock() {
    assert !SwingUtilities.isEventDispatchThread();
    if (this.needReadLock) {
      try {
        SwingUtilities.invokeAndWait(new Runnable() {
              public void run() {}
            });
      } catch (InterruptedException interruptedException) {
      
      } catch (InvocationTargetException invocationTargetException) {}
      Document document = this.textComponentToPrint.getDocument();
      ((AbstractDocument)document).readLock();
      this.needReadLock = false;
    } 
  }
  
  private void layout(final int width) {
    if (!SwingUtilities.isEventDispatchThread()) {
      Callable<Object> callable = new Callable<Object>() {
          public Object call() throws Exception {
            TextComponentPrintable.this.layoutOnEDT(width);
            return null;
          }
        };
      FutureTask futureTask = new FutureTask(callable);
      releaseReadLock();
      SwingUtilities.invokeLater(futureTask);
      try {
        futureTask.get();
      } catch (InterruptedException interruptedException) {
        throw new RuntimeException(interruptedException);
      } catch (ExecutionException executionException) {
        Throwable throwable = executionException.getCause();
        if (throwable instanceof RuntimeException)
          throw (RuntimeException)throwable; 
        if (throwable instanceof Error)
          throw (Error)throwable; 
        throw new RuntimeException(throwable);
      } finally {
        acquireReadLock();
      } 
    } else {
      layoutOnEDT(paramInt);
    } 
    this.isLayouted = true;
  }
  
  private void layoutOnEDT(int paramInt) {
    assert SwingUtilities.isEventDispatchThread();
    CellRendererPane cellRendererPane = new CellRendererPane();
    JViewport jViewport = new JViewport();
    jViewport.setBorder(null);
    Dimension dimension = new Dimension(paramInt, 2147482647);
    if (this.printShell instanceof JTextField)
      dimension = new Dimension(dimension.width, (this.printShell.getPreferredSize()).height); 
    this.printShell.setSize(dimension);
    jViewport.setComponentOrientation(this.printShell.getComponentOrientation());
    jViewport.setSize(dimension);
    jViewport.add(this.printShell);
    cellRendererPane.add(jViewport);
  }
  
  private void updatePagesMetrics(int paramInt1, int paramInt2) {
    while (paramInt1 >= this.pagesMetrics.size() && !this.rowsMetrics.isEmpty()) {
      int i = this.pagesMetrics.size() - 1;
      int j = (i >= 0) ? (((IntegerSegment)this.pagesMetrics.get(i)).end + 1) : 0;
      byte b1;
      for (b1 = 0; b1 < this.rowsMetrics.size() && ((IntegerSegment)this.rowsMetrics.get(b1)).end - j + 1 <= paramInt2; b1++);
      if (b1 == 0) {
        this.pagesMetrics.add(new IntegerSegment(j, j + paramInt2 - 1));
        continue;
      } 
      this.pagesMetrics.add(new IntegerSegment(j, ((IntegerSegment)this.rowsMetrics.get(--b1)).end));
      for (byte b2 = 0; b2 <= b1; b2++)
        this.rowsMetrics.remove(0); 
    } 
  }
  
  private void calculateRowsMetrics() {
    int i = this.printShell.getDocument().getLength();
    ArrayList arrayList = new ArrayList(1000);
    int j = 0;
    int k = -1;
    int m = -1;
    while (j < i) {
      try {
        Rectangle rectangle = this.printShell.modelToView(j);
        if (rectangle != null) {
          int n = (int)rectangle.getY();
          int i1 = (int)rectangle.getHeight();
          if (i1 != 0 && (n != k || i1 != m)) {
            k = n;
            m = i1;
            arrayList.add(new IntegerSegment(n, n + i1 - 1));
          } 
        } 
      } catch (BadLocationException badLocationException) {
        assert false;
      } 
      j++;
    } 
    Collections.sort(arrayList);
    j = Integer.MIN_VALUE;
    k = Integer.MIN_VALUE;
    for (IntegerSegment integerSegment : arrayList) {
      if (k < integerSegment.start) {
        if (k != Integer.MIN_VALUE)
          this.rowsMetrics.add(new IntegerSegment(j, k)); 
        j = integerSegment.start;
        k = integerSegment.end;
        continue;
      } 
      k = integerSegment.end;
    } 
    if (k != Integer.MIN_VALUE)
      this.rowsMetrics.add(new IntegerSegment(j, k)); 
  }
  
  private static class IntegerSegment extends Object implements Comparable<IntegerSegment> {
    final int start;
    
    final int end;
    
    IntegerSegment(int param1Int1, int param1Int2) {
      this.start = param1Int1;
      this.end = param1Int2;
    }
    
    public int compareTo(IntegerSegment param1IntegerSegment) {
      int i = this.start - param1IntegerSegment.start;
      return (i != 0) ? i : (this.end - param1IntegerSegment.end);
    }
    
    public boolean equals(Object param1Object) { return (param1Object instanceof IntegerSegment) ? ((compareTo((IntegerSegment)param1Object) == 0)) : false; }
    
    public int hashCode() {
      null = 17;
      null = 37 * null + this.start;
      return 37 * null + this.end;
    }
    
    public String toString() { return "IntegerSegment [" + this.start + ", " + this.end + "]"; }
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\swing\text\TextComponentPrintable.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */