package app.sagen.mysupersecretmapapp.util;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class Util {

    public static final SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);

    public static Date parseJsonDate(String jsonDate) {
        try {
            return sdf.parse(jsonDate);
        } catch (ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

}
