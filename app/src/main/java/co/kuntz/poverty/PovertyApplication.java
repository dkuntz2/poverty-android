package co.kuntz.poverty;

import android.app.Application;
import android.graphics.Typeface;
import android.os.Build;
import android.util.Log;
import android.util.LongSparseArray;
import android.util.SparseArray;

import java.lang.reflect.Field;

public class PovertyApplication extends Application {
    private static final String DEFAULT_NORMAL_BOLD_FONT_FILENAME = "source-code-pro/SourceCodePro-Bold.otf";
    private static final String DEFAULT_NORMAL_BOLD_ITALIC_FONT_FILENAME = DEFAULT_NORMAL_BOLD_FONT_FILENAME;
    private static final String DEFAULT_NORMAL_NORMAL_FONT_FILENAME = "source-code-pro/SourceCodePro-Regular.otf";
    private static final String DEFAULT_NORMAL_ITALIC_FONT_FILENAME = DEFAULT_NORMAL_NORMAL_FONT_FILENAME;


    // Constants found in the Android documentation
    // http://developer.android.com/reference/android/widget/TextView.html#gattr_android:typeface
    private static final int normal_idx = 0;
    private static final int sans_idx = 1;
    private static final int serif_idx = 2;
    private static final int monospace_idx = 3;


    @Override
    public void onCreate() {
        super.onCreate();

        try {
            setDefaultFonts();

            // The following code is only necessary if you are using the android:typeface attribute
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.JELLY_BEAN) {
                setDefaultFontForTypeface();
            }
        } catch (NoSuchFieldException | IllegalAccessException e) {
            logFontError(e);
        }
    }

    private void setDefaultFonts() throws NoSuchFieldException, IllegalAccessException {
        final Typeface bold = Typeface.createFromAsset(getAssets(), DEFAULT_NORMAL_BOLD_FONT_FILENAME);
        final Typeface boldItalic = Typeface.createFromAsset(getAssets(), DEFAULT_NORMAL_BOLD_ITALIC_FONT_FILENAME);
        final Typeface italic = Typeface.createFromAsset(getAssets(), DEFAULT_NORMAL_ITALIC_FONT_FILENAME);
        final Typeface normal = Typeface.createFromAsset(getAssets(), DEFAULT_NORMAL_NORMAL_FONT_FILENAME);

        Field defaultField = Typeface.class.getDeclaredField("DEFAULT");
        defaultField.setAccessible(true);
        defaultField.set(null, normal);

        Field defaultBoldField = Typeface.class.getDeclaredField("DEFAULT_BOLD");
        defaultBoldField.setAccessible(true);
        defaultBoldField.set(null, bold);

        Field sDefaults = Typeface.class.getDeclaredField("sDefaults");
        sDefaults.setAccessible(true);
        sDefaults.set(null, new Typeface[]{normal, bold, italic, boldItalic});

        Field sansSerifDefault = Typeface.class.getDeclaredField("SANS_SERIF");
        sansSerifDefault.setAccessible(true);
        sansSerifDefault.set(null, normal);

        Field serifDefault = Typeface.class.getDeclaredField("SERIF");
        serifDefault.setAccessible(true);
        serifDefault.set(null, normal);

        Field monospaceDefault = Typeface.class.getDeclaredField("MONOSPACE");
        monospaceDefault.setAccessible(true);
        monospaceDefault.set(null, normal);
    }


    private void setDefaultFontForTypeface() throws NoSuchFieldException, IllegalAccessException {
        final Typeface bold = Typeface.createFromAsset(getAssets(), DEFAULT_NORMAL_BOLD_FONT_FILENAME);
        final Typeface italic = Typeface.createFromAsset(getAssets(), DEFAULT_NORMAL_ITALIC_FONT_FILENAME);
        final Typeface boldItalic = Typeface.createFromAsset(getAssets(), DEFAULT_NORMAL_BOLD_ITALIC_FONT_FILENAME);
        final Typeface normal = Typeface.createFromAsset(getAssets(), DEFAULT_NORMAL_NORMAL_FONT_FILENAME);

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            setTypeFaceDefaultsLollipop(normal, bold, italic, boldItalic, sans_idx);
            setTypeFaceDefaultsLollipop(normal, bold, italic, boldItalic, serif_idx);
            setTypeFaceDefaultsLollipop(normal, bold, italic, boldItalic, monospace_idx);
            setTypeFaceDefaultsLollipop(normal, bold, italic, boldItalic, normal_idx);
        } else {
            setTypeFaceDefaultsPreLollipop(normal, bold, italic, boldItalic, sans_idx);
            setTypeFaceDefaultsPreLollipop(normal, bold, italic, boldItalic, serif_idx);
            setTypeFaceDefaultsPreLollipop(normal, bold, italic, boldItalic, monospace_idx);
            setTypeFaceDefaultsPreLollipop(normal, bold, italic, boldItalic, normal_idx);
        }
    }

    private void setTypeFaceDefaultsPreLollipop(Typeface normal, Typeface bold, Typeface italic, Typeface boldItalic, int typefaceIndex) throws NoSuchFieldException, IllegalAccessException {
        Field typeFacesField = Typeface.class.getDeclaredField("sTypefaceCache");
        typeFacesField.setAccessible(true);

        SparseArray<SparseArray<Typeface>> sTypefaceCacheLocal = new SparseArray<SparseArray<Typeface>>(3);
        typeFacesField.get(sTypefaceCacheLocal);

        sTypefaceCacheLocal.put(typefaceIndex, getNewValues(normal, bold, italic, boldItalic));

        typeFacesField.set(null, sTypefaceCacheLocal);
    }

    private void setTypeFaceDefaultsLollipop(Typeface normal, Typeface bold, Typeface italic, Typeface boldItalic, int typefaceIndex) throws NoSuchFieldException, IllegalAccessException {
        Field typeFacesField = Typeface.class.getDeclaredField("sTypefaceCache");
        typeFacesField.setAccessible(true);

        LongSparseArray<SparseArray<Typeface>> sTypefaceCacheLocal = new LongSparseArray<SparseArray<Typeface>>(3);
        typeFacesField.get(sTypefaceCacheLocal);

        sTypefaceCacheLocal.put(typefaceIndex, getNewValues(normal, bold, italic, boldItalic));

        typeFacesField.set(null, sTypefaceCacheLocal);
    }

    private SparseArray<Typeface> getNewValues(Typeface normal, Typeface bold, Typeface italic, Typeface boldItalic) {
        SparseArray<Typeface> newValues = new SparseArray<Typeface>(4);
        newValues.put(Typeface.NORMAL, normal);
        newValues.put(Typeface.BOLD, bold);
        newValues.put(Typeface.ITALIC, italic);
        newValues.put(Typeface.BOLD_ITALIC, boldItalic);

        return newValues;
    }


    private void logFontError(Throwable e) {
        Log.e("font_override", "Error overriding font", e);
    }
}
