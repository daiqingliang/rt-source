package javax.print;

import java.awt.Component;
import java.awt.Dialog;
import java.awt.Frame;
import java.awt.GraphicsConfiguration;
import java.awt.GraphicsDevice;
import java.awt.GraphicsEnvironment;
import java.awt.HeadlessException;
import java.awt.Rectangle;
import javax.print.attribute.Attribute;
import javax.print.attribute.AttributeSet;
import javax.print.attribute.PrintRequestAttributeSet;
import javax.print.attribute.standard.Fidelity;
import sun.print.ServiceDialog;

public class ServiceUI {
  public static PrintService printDialog(GraphicsConfiguration paramGraphicsConfiguration, int paramInt1, int paramInt2, PrintService[] paramArrayOfPrintService, PrintService paramPrintService, DocFlavor paramDocFlavor, PrintRequestAttributeSet paramPrintRequestAttributeSet) throws HeadlessException {
    ServiceDialog serviceDialog;
    byte b = -1;
    if (GraphicsEnvironment.isHeadless())
      throw new HeadlessException(); 
    if (paramArrayOfPrintService == null || paramArrayOfPrintService.length == 0)
      throw new IllegalArgumentException("services must be non-null and non-empty"); 
    if (paramPrintRequestAttributeSet == null)
      throw new IllegalArgumentException("attributes must be non-null"); 
    if (paramPrintService != null) {
      for (byte b2 = 0; b2 < paramArrayOfPrintService.length; b2++) {
        if (paramArrayOfPrintService[b2].equals(paramPrintService)) {
          b = b2;
          break;
        } 
      } 
      if (b < 0)
        throw new IllegalArgumentException("services must contain defaultService"); 
    } else {
      b = 0;
    } 
    Component component = null;
    Rectangle rectangle1 = (paramGraphicsConfiguration == null) ? GraphicsEnvironment.getLocalGraphicsEnvironment().getDefaultScreenDevice().getDefaultConfiguration().getBounds() : paramGraphicsConfiguration.getBounds();
    if (component instanceof Frame) {
      serviceDialog = new ServiceDialog(paramGraphicsConfiguration, paramInt1 + rectangle1.x, paramInt2 + rectangle1.y, paramArrayOfPrintService, b, paramDocFlavor, paramPrintRequestAttributeSet, (Frame)component);
    } else {
      serviceDialog = new ServiceDialog(paramGraphicsConfiguration, paramInt1 + rectangle1.x, paramInt2 + rectangle1.y, paramArrayOfPrintService, b, paramDocFlavor, paramPrintRequestAttributeSet, (Dialog)component);
    } 
    Rectangle rectangle2 = serviceDialog.getBounds();
    GraphicsEnvironment graphicsEnvironment = GraphicsEnvironment.getLocalGraphicsEnvironment();
    GraphicsDevice[] arrayOfGraphicsDevice = graphicsEnvironment.getScreenDevices();
    for (byte b1 = 0; b1 < arrayOfGraphicsDevice.length; b1++)
      rectangle1 = rectangle1.union(arrayOfGraphicsDevice[b1].getDefaultConfiguration().getBounds()); 
    if (!rectangle1.contains(rectangle2))
      serviceDialog.setLocationRelativeTo(component); 
    serviceDialog.show();
    if (serviceDialog.getStatus() == 1) {
      PrintRequestAttributeSet printRequestAttributeSet = serviceDialog.getAttributes();
      Class clazz1 = javax.print.attribute.standard.Destination.class;
      Class clazz2 = sun.print.SunAlternateMedia.class;
      Class clazz3 = Fidelity.class;
      if (paramPrintRequestAttributeSet.containsKey(clazz1) && !printRequestAttributeSet.containsKey(clazz1))
        paramPrintRequestAttributeSet.remove(clazz1); 
      if (paramPrintRequestAttributeSet.containsKey(clazz2) && !printRequestAttributeSet.containsKey(clazz2))
        paramPrintRequestAttributeSet.remove(clazz2); 
      paramPrintRequestAttributeSet.addAll(printRequestAttributeSet);
      Fidelity fidelity = (Fidelity)paramPrintRequestAttributeSet.get(clazz3);
      if (fidelity != null && fidelity == Fidelity.FIDELITY_TRUE)
        removeUnsupportedAttributes(serviceDialog.getPrintService(), paramDocFlavor, paramPrintRequestAttributeSet); 
    } 
    return serviceDialog.getPrintService();
  }
  
  private static void removeUnsupportedAttributes(PrintService paramPrintService, DocFlavor paramDocFlavor, AttributeSet paramAttributeSet) {
    AttributeSet attributeSet = paramPrintService.getUnsupportedAttributes(paramDocFlavor, paramAttributeSet);
    if (attributeSet != null) {
      Attribute[] arrayOfAttribute = attributeSet.toArray();
      for (byte b = 0; b < arrayOfAttribute.length; b++) {
        Class clazz = arrayOfAttribute[b].getCategory();
        if (paramPrintService.isAttributeCategorySupported(clazz)) {
          Attribute attribute = (Attribute)paramPrintService.getDefaultAttributeValue(clazz);
          if (attribute != null) {
            paramAttributeSet.add(attribute);
          } else {
            paramAttributeSet.remove(clazz);
          } 
        } else {
          paramAttributeSet.remove(clazz);
        } 
      } 
    } 
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\javax\print\ServiceUI.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */