package javax.swing;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.geom.AffineTransform;
import java.awt.geom.Rectangle2D;
import java.awt.print.PageFormat;
import java.awt.print.Printable;
import java.awt.print.PrinterException;
import java.text.MessageFormat;
import javax.swing.table.JTableHeader;
import javax.swing.table.TableColumnModel;

class TablePrintable implements Printable {
  private JTable table;
  
  private JTableHeader header;
  
  private TableColumnModel colModel;
  
  private int totalColWidth;
  
  private JTable.PrintMode printMode;
  
  private MessageFormat headerFormat;
  
  private MessageFormat footerFormat;
  
  private int last = -1;
  
  private int row = 0;
  
  private int col = 0;
  
  private final Rectangle clip = new Rectangle(0, 0, 0, 0);
  
  private final Rectangle hclip = new Rectangle(0, 0, 0, 0);
  
  private final Rectangle tempRect = new Rectangle(0, 0, 0, 0);
  
  private static final int H_F_SPACE = 8;
  
  private static final float HEADER_FONT_SIZE = 18.0F;
  
  private static final float FOOTER_FONT_SIZE = 12.0F;
  
  private Font headerFont;
  
  private Font footerFont;
  
  public TablePrintable(JTable paramJTable, JTable.PrintMode paramPrintMode, MessageFormat paramMessageFormat1, MessageFormat paramMessageFormat2) {
    this.table = paramJTable;
    this.header = paramJTable.getTableHeader();
    this.colModel = paramJTable.getColumnModel();
    this.totalColWidth = this.colModel.getTotalColumnWidth();
    if (this.header != null)
      this.hclip.height = this.header.getHeight(); 
    this.printMode = paramPrintMode;
    this.headerFormat = paramMessageFormat1;
    this.footerFormat = paramMessageFormat2;
    this.headerFont = paramJTable.getFont().deriveFont(1, 18.0F);
    this.footerFont = paramJTable.getFont().deriveFont(0, 12.0F);
  }
  
  public int print(Graphics paramGraphics, PageFormat paramPageFormat, int paramInt) throws PrinterException {
    int i = (int)paramPageFormat.getImageableWidth();
    int j = (int)paramPageFormat.getImageableHeight();
    if (i <= 0)
      throw new PrinterException("Width of printable area is too small."); 
    Object[] arrayOfObject = { Integer.valueOf(paramInt + 1) };
    String str1 = null;
    if (this.headerFormat != null)
      str1 = this.headerFormat.format(arrayOfObject); 
    String str2 = null;
    if (this.footerFormat != null)
      str2 = this.footerFormat.format(arrayOfObject); 
    Rectangle2D rectangle2D1 = null;
    Rectangle2D rectangle2D2 = null;
    int k = 0;
    int m = 0;
    int n = j;
    if (str1 != null) {
      paramGraphics.setFont(this.headerFont);
      rectangle2D1 = paramGraphics.getFontMetrics().getStringBounds(str1, paramGraphics);
      k = (int)Math.ceil(rectangle2D1.getHeight());
      n -= k + 8;
    } 
    if (str2 != null) {
      paramGraphics.setFont(this.footerFont);
      rectangle2D2 = paramGraphics.getFontMetrics().getStringBounds(str2, paramGraphics);
      m = (int)Math.ceil(rectangle2D2.getHeight());
      n -= m + 8;
    } 
    if (n <= 0)
      throw new PrinterException("Height of printable area is too small."); 
    double d = 1.0D;
    if (this.printMode == JTable.PrintMode.FIT_WIDTH && this.totalColWidth > i) {
      assert i > 0;
      assert this.totalColWidth > 1;
      d = i / this.totalColWidth;
    } 
    assert d > 0.0D;
    while (this.last < paramInt) {
      if (this.row >= this.table.getRowCount() && this.col == 0)
        return 1; 
      int i1 = (int)(i / d);
      int i2 = (int)((n - this.hclip.height) / d);
      findNextClip(i1, i2);
      this.last++;
    } 
    Graphics2D graphics2D = (Graphics2D)paramGraphics.create();
    graphics2D.translate(paramPageFormat.getImageableX(), paramPageFormat.getImageableY());
    if (str2 != null) {
      AffineTransform affineTransform1 = graphics2D.getTransform();
      graphics2D.translate(0, j - m);
      printText(graphics2D, str2, rectangle2D2, this.footerFont, i);
      graphics2D.setTransform(affineTransform1);
    } 
    if (str1 != null) {
      printText(graphics2D, str1, rectangle2D1, this.headerFont, i);
      graphics2D.translate(0, k + 8);
    } 
    this.tempRect.x = 0;
    this.tempRect.y = 0;
    this.tempRect.width = i;
    this.tempRect.height = n;
    graphics2D.clip(this.tempRect);
    if (d != 1.0D) {
      graphics2D.scale(d, d);
    } else {
      int i1 = (i - this.clip.width) / 2;
      graphics2D.translate(i1, 0);
    } 
    AffineTransform affineTransform = graphics2D.getTransform();
    Shape shape = graphics2D.getClip();
    if (this.header != null) {
      this.hclip.x = this.clip.x;
      this.hclip.width = this.clip.width;
      graphics2D.translate(-this.hclip.x, 0);
      graphics2D.clip(this.hclip);
      this.header.print(graphics2D);
      graphics2D.setTransform(affineTransform);
      graphics2D.setClip(shape);
      graphics2D.translate(0, this.hclip.height);
    } 
    graphics2D.translate(-this.clip.x, -this.clip.y);
    graphics2D.clip(this.clip);
    this.table.print(graphics2D);
    graphics2D.setTransform(affineTransform);
    graphics2D.setClip(shape);
    graphics2D.setColor(Color.BLACK);
    graphics2D.drawRect(0, 0, this.clip.width, this.hclip.height + this.clip.height);
    graphics2D.dispose();
    return 0;
  }
  
  private void printText(Graphics2D paramGraphics2D, String paramString, Rectangle2D paramRectangle2D, Font paramFont, int paramInt) {
    int i;
    if (paramRectangle2D.getWidth() < paramInt) {
      i = (int)((paramInt - paramRectangle2D.getWidth()) / 2.0D);
    } else if (this.table.getComponentOrientation().isLeftToRight()) {
      i = 0;
    } else {
      i = -((int)(Math.ceil(paramRectangle2D.getWidth()) - paramInt));
    } 
    int j = (int)Math.ceil(Math.abs(paramRectangle2D.getY()));
    paramGraphics2D.setColor(Color.BLACK);
    paramGraphics2D.setFont(paramFont);
    paramGraphics2D.drawString(paramString, i, j);
  }
  
  private void findNextClip(int paramInt1, int paramInt2) {
    boolean bool = this.table.getComponentOrientation().isLeftToRight();
    if (this.col == 0) {
      if (bool) {
        this.clip.x = 0;
      } else {
        this.clip.x = this.totalColWidth;
      } 
      this.clip.y += this.clip.height;
      this.clip.width = 0;
      this.clip.height = 0;
      int k = this.table.getRowCount();
      int m = this.table.getRowHeight(this.row);
      this.clip.height += m;
      while (++this.row < k) {
        m = this.table.getRowHeight(this.row);
        if (this.clip.height + m > paramInt2)
          break; 
      } 
    } 
    if (this.printMode == JTable.PrintMode.FIT_WIDTH) {
      this.clip.x = 0;
      this.clip.width = this.totalColWidth;
      return;
    } 
    if (bool)
      this.clip.x += this.clip.width; 
    this.clip.width = 0;
    int i = this.table.getColumnCount();
    int j = this.colModel.getColumn(this.col).getWidth();
    do {
      this.clip.width += j;
      if (!bool)
        this.clip.x -= j; 
      if (++this.col >= i) {
        this.col = 0;
        break;
      } 
      j = this.colModel.getColumn(this.col).getWidth();
    } while (this.clip.width + j <= paramInt1);
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\TablePrintable.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */