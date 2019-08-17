package sun.text.resources;

import sun.util.resources.ParallelListResourceBundle;

public class FormatData extends ParallelListResourceBundle {
  protected final Object[][] getContents() {
    String[] arrayOfString1 = { "BC", "AD" };
    String[] arrayOfString2 = { "BC", "B.E." };
    String[] arrayOfString3 = { "", "M", "T", "S", "H" };
    String[] arrayOfString4 = { "", "Meiji", "Taisho", "Showa", "Heisei" };
    return new Object[][] { 
        { "MonthNames", { 
            "January", "February", "March", "April", "May", "June", "July", "August", "September", "October", 
            "November", "December", "" } }, { "MonthAbbreviations", { 
            "Jan", "Feb", "Mar", "Apr", "May", "Jun", "Jul", "Aug", "Sep", "Oct", 
            "Nov", "Dec", "" } }, { "MonthNarrows", { 
            "1", "2", "3", "4", "5", "6", "7", "8", "9", "10", 
            "11", "12", "" } }, { "DayNames", { "Sunday", "Monday", "Tuesday", "Wednesday", "Thursday", "Friday", "Saturday" } }, { "DayAbbreviations", { "Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat" } }, { "DayNarrows", { "S", "M", "T", "W", "T", "F", "S" } }, { "AmPmMarkers", { "AM", "PM" } }, { "narrow.AmPmMarkers", { "a", "p" } }, { "Eras", arrayOfString1 }, { "short.Eras", arrayOfString1 }, 
        { "narrow.Eras", { "B", "A" } }, { "buddhist.Eras", arrayOfString2 }, { "buddhist.short.Eras", arrayOfString2 }, { "buddhist.narrow.Eras", arrayOfString2 }, { "japanese.Eras", arrayOfString4 }, { "japanese.short.Eras", arrayOfString3 }, { "japanese.narrow.Eras", arrayOfString3 }, { "japanese.FirstYear", new String[0] }, { "NumberPatterns", { "#,##0.###;-#,##0.###", "¤ #,##0.00;-¤ #,##0.00", "#,##0%" } }, { "DefaultNumberingSystem", "" }, 
        { "NumberElements", { 
            ".", ",", ";", "%", "0", "#", "-", "E", "‰", "∞", 
            "�" } }, { "arab.NumberElements", { 
            "٫", "٬", "؛", "٪", "٠", "#", "-", "اس", "؉", "∞", 
            "NaN" } }, { "arabext.NumberElements", { 
            "٫", "٬", "؛", "٪", "۰", "#", "-", "×۱۰^", "؉", "∞", 
            "NaN" } }, { "bali.NumberElements", { 
            ".", ",", ";", "%", "᭐", "#", "-", "E", "‰", "∞", 
            "NaN" } }, { "beng.NumberElements", { 
            ".", ",", ";", "%", "০", "#", "-", "E", "‰", "∞", 
            "NaN" } }, { "cham.NumberElements", { 
            ".", ",", ";", "%", "꩐", "#", "-", "E", "‰", "∞", 
            "NaN" } }, { "deva.NumberElements", { 
            ".", ",", ";", "%", "०", "#", "-", "E", "‰", "∞", 
            "NaN" } }, { "fullwide.NumberElements", { 
            ".", ",", ";", "%", "０", "#", "-", "E", "‰", "∞", 
            "NaN" } }, { "gujr.NumberElements", { 
            ".", ",", ";", "%", "૦", "#", "-", "E", "‰", "∞", 
            "NaN" } }, { "guru.NumberElements", { 
            ".", ",", ";", "%", "੦", "#", "-", "E", "‰", "∞", 
            "NaN" } }, 
        { "java.NumberElements", { 
            ".", ",", ";", "%", "꧐", "#", "-", "E", "‰", "∞", 
            "NaN" } }, { "kali.NumberElements", { 
            ".", ",", ";", "%", "꤀", "#", "-", "E", "‰", "∞", 
            "NaN" } }, { "khmr.NumberElements", { 
            ".", ",", ";", "%", "០", "#", "-", "E", "‰", "∞", 
            "NaN" } }, { "knda.NumberElements", { 
            ".", ",", ";", "%", "೦", "#", "-", "E", "‰", "∞", 
            "NaN" } }, { "laoo.NumberElements", { 
            ".", ",", ";", "%", "໐", "#", "-", "E", "‰", "∞", 
            "NaN" } }, { "lana.NumberElements", { 
            ".", ",", ";", "%", "᪀", "#", "-", "E", "‰", "∞", 
            "NaN" } }, { "lanatham.NumberElements", { 
            ".", ",", ";", "%", "᪐", "#", "-", "E", "‰", "∞", 
            "NaN" } }, { "latn.NumberElements", { 
            ".", ",", ";", "%", "0", "#", "-", "E", "‰", "∞", 
            "�" } }, { "lepc.NumberElements", { 
            ".", ",", ";", "%", "᱀", "#", "-", "E", "‰", "∞", 
            "NaN" } }, { "limb.NumberElements", { 
            ".", ",", ";", "%", "᥆", "#", "-", "E", "‰", "∞", 
            "NaN" } }, 
        { "mlym.NumberElements", { 
            ".", ",", ";", "%", "൦", "#", "-", "E", "‰", "∞", 
            "NaN" } }, { "mong.NumberElements", { 
            ".", ",", ";", "%", "᠐", "#", "-", "E", "‰", "∞", 
            "NaN" } }, { "mtei.NumberElements", { 
            ".", ",", ";", "%", "꯰", "#", "-", "E", "‰", "∞", 
            "NaN" } }, { "mymr.NumberElements", { 
            ".", ",", ";", "%", "၀", "#", "-", "E", "‰", "∞", 
            "NaN" } }, { "mymrshan.NumberElements", { 
            ".", ",", ";", "%", "႐", "#", "-", "E", "‰", "∞", 
            "NaN" } }, { "nkoo.NumberElements", { 
            ".", ",", ";", "%", "߀", "#", "-", "E", "‰", "∞", 
            "NaN" } }, { "olck.NumberElements", { 
            ".", ",", ";", "%", "᱐", "#", "-", "E", "‰", "∞", 
            "NaN" } }, { "orya.NumberElements", { 
            ".", ",", ";", "%", "୦", "#", "-", "E", "‰", "∞", 
            "NaN" } }, { "saur.NumberElements", { 
            ".", ",", ";", "%", "꣐", "#", "-", "E", "‰", "∞", 
            "NaN" } }, { "sund.NumberElements", { 
            ".", ",", ";", "%", "᮰", "#", "-", "E", "‰", "∞", 
            "NaN" } }, 
        { "talu.NumberElements", { 
            ".", ",", ";", "%", "᧐", "#", "-", "E", "‰", "∞", 
            "NaN" } }, { "tamldec.NumberElements", { 
            ".", ",", ";", "%", "௦", "#", "-", "E", "‰", "∞", 
            "NaN" } }, { "telu.NumberElements", { 
            ".", ",", ";", "%", "౦", "#", "-", "E", "‰", "∞", 
            "NaN" } }, { "thai.NumberElements", { 
            ".", ",", ";", "%", "๐", "#", "-", "E", "‰", "∞", 
            "�" } }, { "tibt.NumberElements", { 
            ".", ",", ";", "%", "༠", "#", "-", "E", "‰", "∞", 
            "NaN" } }, { "vaii.NumberElements", { 
            ".", ",", ";", "%", "꘠", "#", "-", "E", "‰", "∞", 
            "NaN" } }, { "TimePatterns", { "h:mm:ss a z", "h:mm:ss a z", "h:mm:ss a", "h:mm a" } }, { "DatePatterns", { "EEEE, MMMM d, yyyy", "MMMM d, yyyy", "MMM d, yyyy", "M/d/yy" } }, { "DateTimePatterns", { "{1} {0}" } }, { "buddhist.TimePatterns", { "H:mm:ss z", "H:mm:ss z", "H:mm:ss", "H:mm" } }, 
        { "buddhist.DatePatterns", { "EEEE d MMMM G yyyy", "d MMMM yyyy", "d MMM yyyy", "d/M/yyyy" } }, { "buddhist.DateTimePatterns", { "{1}, {0}" } }, { "japanese.TimePatterns", { "h:mm:ss a z", "h:mm:ss a z", "h:mm:ss a", "h:mm a" } }, { "japanese.DatePatterns", { "GGGG yyyy MMMM d (EEEE)", "GGGG yyyy MMMM d", "GGGG yyyy MMM d", "Gy.MM.dd" } }, { "japanese.DateTimePatterns", { "{1} {0}" } }, { "DateTimePatternChars", "GyMdkHmsSEDFwWahKzZ" }, { "calendarname.islamic-umalqura", "Islamic Umm al-Qura Calendar" } };
  }
}


/* Location:              D:\software\jd-gui\jd-gui-windows-1.6.3\rt.jar!\sun\text\resources\FormatData.class
 * Java compiler version: 8 (52.0)
 * JD-Core Version:       1.0.7
 */