package java.beans;

import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.ImageProducer;
import java.net.URL;

public class SimpleBeanInfo implements BeanInfo {
  public BeanDescriptor getBeanDescriptor() { return null; }
  
  public PropertyDescriptor[] getPropertyDescriptors() { return null; }
  
  public int getDefaultPropertyIndex() { return -1; }
  
  public EventSetDescriptor[] getEventSetDescriptors() { return null; }
  
  public int getDefaultEventIndex() { return -1; }
  
  public MethodDescriptor[] getMethodDescriptors() { return null; }
  
  public BeanInfo[] getAdditionalBeanInfo() { return null; }
  
  public Image getIcon(int paramInt) { return null; }
  
  public Image loadImage(String paramString) {
    try {
      URL uRL = getClass().getResource(paramString);
      if (uRL != null) {
        ImageProducer imageProducer = (ImageProducer)uRL.getContent();
        if (imageProducer != null)
          return Toolkit.getDefaultToolkit().createImage(imageProducer); 
      } 
    } catch (Exception exception) {}
    return null;
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\java\beans\SimpleBeanInfo.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */