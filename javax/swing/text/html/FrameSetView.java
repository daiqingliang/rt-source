package javax.swing.text.html;

import java.util.StringTokenizer;
import javax.swing.SizeRequirements;
import javax.swing.text.AttributeSet;
import javax.swing.text.BoxView;
import javax.swing.text.Element;
import javax.swing.text.View;

class FrameSetView extends BoxView {
  String[] children = null;
  
  int[] percentChildren;
  
  int[] absoluteChildren;
  
  int[] relativeChildren;
  
  int percentTotals;
  
  int absoluteTotals;
  
  int relativeTotals;
  
  public FrameSetView(Element paramElement, int paramInt) { super(paramElement, paramInt); }
  
  private String[] parseRowColSpec(HTML.Attribute paramAttribute) {
    AttributeSet attributeSet = getElement().getAttributes();
    String str = "*";
    if (attributeSet != null && attributeSet.getAttribute(paramAttribute) != null)
      str = (String)attributeSet.getAttribute(paramAttribute); 
    StringTokenizer stringTokenizer = new StringTokenizer(str, ",");
    int i = stringTokenizer.countTokens();
    int j = getViewCount();
    String[] arrayOfString = new String[Math.max(i, j)];
    byte b;
    for (b = 0; b < i; b++) {
      arrayOfString[b] = stringTokenizer.nextToken().trim();
      if (arrayOfString[b].equals("100%"))
        arrayOfString[b] = "*"; 
    } 
    while (b < arrayOfString.length) {
      arrayOfString[b] = "*";
      b++;
    } 
    return arrayOfString;
  }
  
  private void init() {
    if (getAxis() == 1) {
      this.children = parseRowColSpec(HTML.Attribute.ROWS);
    } else {
      this.children = parseRowColSpec(HTML.Attribute.COLS);
    } 
    this.percentChildren = new int[this.children.length];
    this.relativeChildren = new int[this.children.length];
    this.absoluteChildren = new int[this.children.length];
    byte b;
    for (b = 0; b < this.children.length; b++) {
      this.percentChildren[b] = -1;
      this.relativeChildren[b] = -1;
      this.absoluteChildren[b] = -1;
      if (this.children[b].endsWith("*")) {
        if (this.children[b].length() > 1) {
          this.relativeChildren[b] = Integer.parseInt(this.children[b].substring(0, this.children[b].length() - 1));
          this.relativeTotals += this.relativeChildren[b];
        } else {
          this.relativeChildren[b] = 1;
          this.relativeTotals++;
        } 
      } else if (this.children[b].indexOf('%') != -1) {
        this.percentChildren[b] = parseDigits(this.children[b]);
        this.percentTotals += this.percentChildren[b];
      } else {
        this.absoluteChildren[b] = Integer.parseInt(this.children[b]);
      } 
    } 
    if (this.percentTotals > 100) {
      for (b = 0; b < this.percentChildren.length; b++) {
        if (this.percentChildren[b] > 0)
          this.percentChildren[b] = this.percentChildren[b] * 100 / this.percentTotals; 
      } 
      this.percentTotals = 100;
    } 
  }
  
  protected void layoutMajorAxis(int paramInt1, int paramInt2, int[] paramArrayOfInt1, int[] paramArrayOfInt2) {
    if (this.children == null)
      init(); 
    SizeRequirements.calculateTiledPositions(paramInt1, null, getChildRequests(paramInt1, paramInt2), paramArrayOfInt1, paramArrayOfInt2);
  }
  
  protected SizeRequirements[] getChildRequests(int paramInt1, int paramInt2) {
    int[] arrayOfInt = new int[this.children.length];
    spread(paramInt1, arrayOfInt);
    int i = getViewCount();
    SizeRequirements[] arrayOfSizeRequirements = new SizeRequirements[i];
    byte b1 = 0;
    byte b2 = 0;
    while (b1 < i) {
      View view = getView(b1);
      if (view instanceof FrameView || view instanceof FrameSetView) {
        arrayOfSizeRequirements[b1] = new SizeRequirements((int)view.getMinimumSpan(paramInt2), arrayOfInt[b2], (int)view.getMaximumSpan(paramInt2), 0.5F);
        b2++;
      } else {
        int j = (int)view.getMinimumSpan(paramInt2);
        int k = (int)view.getPreferredSpan(paramInt2);
        int m = (int)view.getMaximumSpan(paramInt2);
        float f = view.getAlignment(paramInt2);
        arrayOfSizeRequirements[b1] = new SizeRequirements(j, k, m, f);
      } 
      b1++;
    } 
    return arrayOfSizeRequirements;
  }
  
  private void spread(int paramInt, int[] paramArrayOfInt) {
    if (paramInt == 0)
      return; 
    int i = 0;
    int j = paramInt;
    byte b;
    for (b = 0; b < paramArrayOfInt.length; b++) {
      if (this.absoluteChildren[b] > 0) {
        paramArrayOfInt[b] = this.absoluteChildren[b];
        j -= paramArrayOfInt[b];
      } 
    } 
    i = j;
    for (b = 0; b < paramArrayOfInt.length; b++) {
      if (this.percentChildren[b] > 0 && i > 0) {
        paramArrayOfInt[b] = this.percentChildren[b] * i / 100;
        j -= paramArrayOfInt[b];
      } else if (this.percentChildren[b] > 0 && i <= 0) {
        paramArrayOfInt[b] = paramInt / paramArrayOfInt.length;
        j -= paramArrayOfInt[b];
      } 
    } 
    if (j > 0 && this.relativeTotals > 0) {
      for (b = 0; b < paramArrayOfInt.length; b++) {
        if (this.relativeChildren[b] > 0)
          paramArrayOfInt[b] = j * this.relativeChildren[b] / this.relativeTotals; 
      } 
    } else if (j > 0) {
      float f = (paramInt - j);
      float[] arrayOfFloat = new float[paramArrayOfInt.length];
      j = paramInt;
      byte b1;
      for (b1 = 0; b1 < paramArrayOfInt.length; b1++) {
        arrayOfFloat[b1] = paramArrayOfInt[b1] / f * 100.0F;
        paramArrayOfInt[b1] = (int)(paramInt * arrayOfFloat[b1] / 100.0F);
        j -= paramArrayOfInt[b1];
      } 
      b1 = 0;
      while (j != 0) {
        if (j < 0) {
          paramArrayOfInt[b1++] = paramArrayOfInt[b1++] - 1;
          j++;
        } else {
          paramArrayOfInt[b1++] = paramArrayOfInt[b1++] + 1;
          j--;
        } 
        if (b1 == paramArrayOfInt.length)
          b1 = 0; 
      } 
    } 
  }
  
  private int parseDigits(String paramString) {
    int i = 0;
    for (byte b = 0; b < paramString.length(); b++) {
      char c = paramString.charAt(b);
      if (Character.isDigit(c))
        i = i * 10 + Character.digit(c, 10); 
    } 
    return i;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\text\html\FrameSetView.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */