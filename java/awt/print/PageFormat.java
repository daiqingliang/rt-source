package java.awt.print;

public class PageFormat implements Cloneable {
  public static final int LANDSCAPE = 0;
  
  public static final int PORTRAIT = 1;
  
  public static final int REVERSE_LANDSCAPE = 2;
  
  private Paper mPaper = new Paper();
  
  private int mOrientation = 1;
  
  public Object clone() {
    Object object;
    try {
      object = (PageFormat)super.clone();
      object.mPaper = (Paper)this.mPaper.clone();
    } catch (CloneNotSupportedException cloneNotSupportedException) {
      cloneNotSupportedException.printStackTrace();
      object = null;
    } 
    return object;
  }
  
  public double getWidth() {
    double d;
    int i = getOrientation();
    if (i == 1) {
      d = this.mPaper.getWidth();
    } else {
      d = this.mPaper.getHeight();
    } 
    return d;
  }
  
  public double getHeight() {
    double d;
    int i = getOrientation();
    if (i == 1) {
      d = this.mPaper.getHeight();
    } else {
      d = this.mPaper.getWidth();
    } 
    return d;
  }
  
  public double getImageableX() {
    switch (getOrientation()) {
      case 0:
        return this.mPaper.getHeight() - this.mPaper.getImageableY() + this.mPaper.getImageableHeight();
      case 1:
        return this.mPaper.getImageableX();
      case 2:
        return this.mPaper.getImageableY();
    } 
    throw new InternalError("unrecognized orientation");
  }
  
  public double getImageableY() {
    switch (getOrientation()) {
      case 0:
        return this.mPaper.getImageableX();
      case 1:
        return this.mPaper.getImageableY();
      case 2:
        return this.mPaper.getWidth() - this.mPaper.getImageableX() + this.mPaper.getImageableWidth();
    } 
    throw new InternalError("unrecognized orientation");
  }
  
  public double getImageableWidth() {
    double d;
    if (getOrientation() == 1) {
      d = this.mPaper.getImageableWidth();
    } else {
      d = this.mPaper.getImageableHeight();
    } 
    return d;
  }
  
  public double getImageableHeight() {
    double d;
    if (getOrientation() == 1) {
      d = this.mPaper.getImageableHeight();
    } else {
      d = this.mPaper.getImageableWidth();
    } 
    return d;
  }
  
  public Paper getPaper() { return (Paper)this.mPaper.clone(); }
  
  public void setPaper(Paper paramPaper) { this.mPaper = (Paper)paramPaper.clone(); }
  
  public void setOrientation(int paramInt) throws IllegalArgumentException {
    if (0 <= paramInt && paramInt <= 2) {
      this.mOrientation = paramInt;
    } else {
      throw new IllegalArgumentException();
    } 
  }
  
  public int getOrientation() { return this.mOrientation; }
  
  public double[] getMatrix() {
    double[] arrayOfDouble = new double[6];
    switch (this.mOrientation) {
      case 0:
        arrayOfDouble[0] = 0.0D;
        arrayOfDouble[1] = -1.0D;
        arrayOfDouble[2] = 1.0D;
        arrayOfDouble[3] = 0.0D;
        arrayOfDouble[4] = 0.0D;
        arrayOfDouble[5] = this.mPaper.getHeight();
        return arrayOfDouble;
      case 1:
        arrayOfDouble[0] = 1.0D;
        arrayOfDouble[1] = 0.0D;
        arrayOfDouble[2] = 0.0D;
        arrayOfDouble[3] = 1.0D;
        arrayOfDouble[4] = 0.0D;
        arrayOfDouble[5] = 0.0D;
        return arrayOfDouble;
      case 2:
        arrayOfDouble[0] = 0.0D;
        arrayOfDouble[1] = 1.0D;
        arrayOfDouble[2] = -1.0D;
        arrayOfDouble[3] = 0.0D;
        arrayOfDouble[4] = this.mPaper.getWidth();
        arrayOfDouble[5] = 0.0D;
        return arrayOfDouble;
    } 
    throw new IllegalArgumentException();
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\awt\print\PageFormat.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */