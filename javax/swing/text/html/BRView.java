package javax.swing.text.html;

import javax.swing.text.Element;

class BRView extends InlineView {
  public BRView(Element paramElement) { super(paramElement); }
  
  public int getBreakWeight(int paramInt, float paramFloat1, float paramFloat2) { return (paramInt == 0) ? 3000 : super.getBreakWeight(paramInt, paramFloat1, paramFloat2); }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\swing\text\html\BRView.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */