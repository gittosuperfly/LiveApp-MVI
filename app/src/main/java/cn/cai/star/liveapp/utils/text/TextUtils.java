package cn.cai.star.liveapp.utils.text;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.net.URLEncoder;
import java.text.DateFormatSymbols;
import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.style.AbsoluteSizeSpan;
import android.text.style.ForegroundColorSpan;
import android.text.style.StyleSpan;
import android.text.style.URLSpan;
import android.util.Log;
import android.webkit.MimeTypeMap;
import android.widget.EditText;
import android.widget.TextView;

public class TextUtils {

    public static final String EMPTY_STRING = "";

    public static final String ELLIPSIS = "...";

    public static StyleSpan BOLD_STYLE = new StyleSpan(Typeface.BOLD);

    static int[][] CJKCodeRanges = {
            {0x2e80, 0x2eff}, // CJK Radicals Supplement
            {0x2f00, 0x2fdf}, // Kangxi Radicals
            {0x3000, 0x303f}, // CJK Symbols and Punctuation
            {0x3040, 0x309f}, // Hiragana
            {0x30a0, 0x30ff}, // Katakana
            {0x3100, 0x312f}, // Bopomofo
            {0x3130, 0x318f}, // Hangul Compatibility Jamo
            {0x3190, 0x319f}, // Kanbun
            {0x31a0, 0x31bf}, // Bopomofo Extended
            {0x31f0, 0x31ff}, // Katakana Phonetic Extensions
            {0x3200, 0x32ff}, // Enclosed CJK Letters and Months, ㈀ - ㋾
            {0x3300, 0x3ff}, // CJK Compatibility, ㌀ - ㏾
            {0x3400, 0x4dbf}, // CJK Unified Ideographs Extension A, 㐀 - 䶵
            {0x4e00, 0x9fff}, // CJK Unified Ideographs，一
            {0xac00, 0xd7af}, // Hangul Syllables
            {0xf900, 0xfaff}, // CJK Compatibility Ideographs
            {0xfe30, 0xfe4f}, // CJK Compatibility Forms
            {0x20000, 0x2a6df}, // CJK Unified Ideographs Extension B
            {0x2A700, 0x2B73F}, // CJK Unified Ideographs Extension C
            {0x2B740, 0x2B81F}, // CJK Unified Ideographs Extension D
            {0x2B820, 0x2CEAF}, // CJK Unified Ideographs Extension E
            {0x2f800, 0x2fa1f}, // CJK Compatibility Ideographs Supplement
    };

    private TextUtils() {
    }

    /**
     * SpannableString中 link的字段的下划线去除.
     */
    public static Spannable removeUnderlines(Spannable text) {
        if (text == null) {
            return null;
        }

        URLSpan[] spans = text.getSpans(0, text.length(), URLSpan.class);
        for (URLSpan span : spans) {
            int start = text.getSpanStart(span);
            int end = text.getSpanEnd(span);
            text.removeSpan(span);
            span = new URLSpanNoUnderline(span.getURL());
            text.setSpan(span, start, end, 0);
        }
        return text;
    }

    public static boolean isEmpty(@Nullable CharSequence str) {
        return str == null || str.length() == 0;
    }

    /**
     * 返回完整的字符个数（一个emoji算一个字符）
     */
    public static int codePointsCount(String text) {
        if (isEmpty(text)) {
            return 0;
        }
        return text.codePointCount(0, text.length());
    }

    /**
     * 如果文字中包括emoji等utf-16表示的字符时，要保证完整字符
     */
    public static String limitTextByMaxLength(String text, int maxLength) {
        int codePointsCount = codePointsCount(text);
        if (maxLength < 0) {
            return "";
        }
        if (maxLength >= codePointsCount) {
            return text;
        }
        int endIndex = text.offsetByCodePoints(0, maxLength);
        return text.substring(0, endIndex);
    }

    /**
     * 有一个为空就返回true 替代 ：
     * if(TextUtil.isEmpTy(a)||TextUtil.isEmpTy(b)){
     * code...
     * }
     */
    public static boolean isOrEmpty(CharSequence... str) {

        CharSequence[] s = str;

        if (s == null || s.length == 0) return true;
        boolean result = false;
        for (CharSequence sequence : s) {
            result = result || isEmpty(sequence);
        }
        return result;
    }

    public static boolean equals(CharSequence a, CharSequence b) {
        if (a == b) return true;
        int length;
        if (a != null && b != null && (length = a.length()) == b.length()) {
            if (a instanceof String && b instanceof String) {
                return a.equals(b);
            } else {
                for (int i = 0; i < length; i++) {
                    if (a.charAt(i) != b.charAt(i)) return false;
                }
                return true;
            }
        }
        return false;
    }

    public static String defaultIfEmpty(String text, String defaultText) {
        if (isEmpty(text)) {
            return defaultText;
        }
        return text;
    }

    /**
     * 将字符串中 .0 移除. 例如 7.0 返回 7
     */
    public static String removeTrailingZeros(String s) {
        return !s.contains(".") ? s : s.replaceAll("0*$", "")
                .replaceAll("\\.$", "");
    }

    public static SpannableString getSpecialSizeText(Context context, int resId, int size) {
        SpannableString string = new SpannableString(context.getString(resId));
        AbsoluteSizeSpan sizeSpan = new AbsoluteSizeSpan(size, true);
        string.setSpan(sizeSpan, 0, string.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        return string;
    }

    public static boolean isCJK(int code) {
        for (int[] range : CJKCodeRanges) {
            if (range[0] <= code && code <= range[1]) {
                return true;
            }
        }
        return false;
    }

    public static CharSequence getBold(CharSequence s) {
        SpannableString ss = new SpannableString(s);
        ss.setSpan(BOLD_STYLE, 0, ss.length(), 0);
        return ss;
    }

    public static int string2color(String color, int fallback) {
        try {
            return Color.parseColor(color);
        } catch (Throwable e) {
            Log.e("@", "fail to parse color", e);
            return fallback;
        }
    }

    public static String color2string(int color) {
        return String.format(Locale.US, "%08X", color & 0xFFFFFFFF);
    }

    public static CharSequence getColor(int color, String s) {
        SpannableString ss = new SpannableString(s);
        ss.setSpan(new ForegroundColorSpan(color), 0, ss.length(), 0);
        return ss;
    }

    public static String valueOf(long n) {
        return valueOf(Locale.getDefault(), n);
    }

    /**
     * 根据数字返回相应的字符串，如1000返回1k；此重载用于单元测试
     */
    public static String valueOf(@NonNull Locale language, long n) {
        if (toLowerCase(language.getLanguage()).equals("zh")) {
            if (n <= 9999) {
                return String.valueOf(n);
            }
            float w = n / 10000F;
            BigDecimal b = new BigDecimal(w + "");
            double wWith1Dot = b.setScale(1, BigDecimal.ROUND_HALF_UP).doubleValue();
            int s = ((int) (wWith1Dot * 10)) % 10;// 如果第一位小数为0，则不显示小数位数
            return String.format(s == 0 ? "%.0fw" : "%.1fw", wWith1Dot);
        } else {
            if (n <= 999) {
                return String.valueOf(n);
            }
            float k = n / 1000F;
            BigDecimal b = new BigDecimal(k + "");
            double kWith1Dot = b.setScale(1, BigDecimal.ROUND_HALF_UP).doubleValue();
            if (kWith1Dot <= 999) {
                int s = ((int) (kWith1Dot * 10)) % 10;
                return String.format(s == 0 ? "%.0fk" : "%.1fk", kWith1Dot);
            }
            b = new BigDecimal(kWith1Dot / 1000F + "");
            double mWith1Dot = b.setScale(1, BigDecimal.ROUND_HALF_UP).doubleValue();
            int s = ((int) (mWith1Dot * 10)) % 10;
            return String.format(s == 0 ? "%.0fm" : "%.1fm", mWith1Dot);
        }
    }

    public static String msToSecond(long ms) {
        float second = ((float) ms) / 1000f;
        return String.format(Locale.US, "%.3f", second);
    }


    public static String timeSpreadFormat(long time) {
        long h = time / (60 * 60 * 1000);
        long m = time / (60 * 1000) - h * 60;
        long s = time / 1000 - m * 60 - h * 3600;
        if (h > 0) {
            return String.format(Locale.US, "%02d:%02d:%02d", h, m, s);
        } else {
            return String.format(Locale.US, "%02d:%02d", m, s);
        }
    }

    public static String timeSpreadFormatNew(long time) {
        long h = time / (60 * 60 * 1000);
        long m = time / (60 * 1000) - h * 60;
        long s = time / 1000 - m * 60 - h * 3600;
        return String.format(Locale.US, "%02d:%02d:%02d", h, m, s);
    }

    @SuppressWarnings("deprecation")
    public static String encodeUrl(String url) {
        if (url == null) {
            return "";
        }
        return URLEncoder.encode(url);
    }

    public static String appendUrlParams(@NonNull String url, Map<String, String> params) {
        if (params != null) {
            StringBuilder sb = new StringBuilder();
            boolean first = true;
            for (String s : params.keySet()) {
                try {
                    String value = params.get(s);
                    if (!android.text.TextUtils.isEmpty(s)
                            && !android.text.TextUtils.isEmpty(value)) {
                        if (first) {
                            first = false;
                        } else {
                            sb.append('&');
                        }
                        String val = URLEncoder.encode(value, "utf-8");
                        sb.append(s).append('=').append(val);
                    }
                } catch (Throwable e) {
                    e.printStackTrace();
                }
            }
            return appendUrlParams(url, sb);
        }
        return url;
    }

    /**
     * append query to url like 'scheme://authority/path?query#hash'
     *
     * @param formedQuery like key=value&key1=value1&key2=value2
     */
    public static String appendUrlParams(String url, CharSequence formedQuery) {
        if (TextUtils.isEmpty(formedQuery)) {
            return url;
        }
        int hashPosition = url.indexOf("#");
        String hashPart = hashPosition == -1 ? "" : url.substring(hashPosition);

        String queryAppendPart =
                hashPosition < 0 ? url : hashPosition == 0 ? "" : url.substring(0, hashPosition);
        StringBuilder sb = new StringBuilder();
        sb.append(queryAppendPart);
        if (!queryAppendPart.contains("?")) {
            sb.append("?");
        } else if (!queryAppendPart.endsWith("?")
                && !queryAppendPart.endsWith("&")) {
            sb.append("&");
        }
        if (formedQuery.length() > 1
                && (formedQuery.charAt(0) == '?' || formedQuery.charAt(0) == '&')) {
            sb.append(formedQuery.subSequence(1, formedQuery.length()));
        } else {
            sb.append(formedQuery);
        }
        sb.append(hashPart);
        return sb.toString();
    }

    public static String addOrReplaceParamKey(String url, @NonNull String key,
                                              @NonNull String value) {
        if (TextUtils.isEmpty(url)) {
            return url;
        }
        Uri uri = Uri.parse(url);
        if (uri != null && uri.getQueryParameterNames().contains(key)) {
            return replaceParamKey(url, key, value);
        } else {
            Map<String, String> addQuery = new HashMap<>();
            addQuery.put(key, value);
            return appendUrlParams(url, addQuery);
        }
    }

    public static String replaceParamKey(@NonNull String url, @NonNull String key,
                                         @NonNull String value) {
        if (!android.text.TextUtils.isEmpty(url) && !android.text.TextUtils.isEmpty(key)) {
            url = url.replaceAll("(" + key + "=[^&]*)", key + "=" + value);
        }
        return url;
    }

    public static String appendPageParams(String url, CharSequence formedQuery) {
        if (TextUtils.isEmpty(formedQuery)) {
            return url;
        }
        StringBuilder sb = new StringBuilder();
        if (!TextUtils.isEmpty(url)) {
            sb.append("&");
            sb.append(formedQuery);
        } else {
            sb.append(formedQuery);
        }
        return sb.toString();
    }

    public static Editable safeGetText(EditText editText) {
        // 某些机型getText会返回null
        return (editText == null || editText.getText() == null)
                ? new SpannableStringBuilder("")
                : editText.getText();
    }

    public static CharSequence safeGetText(@NonNull TextView tv) {
        if (tv == null) {
            return EMPTY_STRING;
        }
        return android.text.TextUtils.isEmpty(tv.getText()) ? EMPTY_STRING : tv.getText();
    }

    public static String getNonScientificNumber(double number) {
        final BigDecimal df = new BigDecimal(number);
        return removeTrailingZeros(df.toPlainString());
    }

    public static boolean isChinese(char c) {
        Character.UnicodeBlock ub = Character.UnicodeBlock.of(c);
        return ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS
                || ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS
                || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A
                || ub == Character.UnicodeBlock.GENERAL_PUNCTUATION
                || ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION
                || ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS;
    }

    public static boolean containChineseChar(String s) {
        if (!TextUtils.isEmpty(s)) {
            for (char c : s.toCharArray()) {
                if (isChinese(c)) {
                    return true;
                }
            }
            return false;
        } else {
            return false;
        }
    }

    public static String formatSimpleDate(long timestamp) {
        SimpleDateFormat simpleDateFormat = TextUtils
                .newSimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return simpleDateFormat.format(new Date(timestamp));
    }

    public static String removeAngleBrackets(String s) {
        return s.replaceAll("[《》]", "");
    }

    public static String sanityCheckNull(String str) {
        return str == null ? EMPTY_STRING : str;
    }

    public static String nullIfEmpty(@Nullable String str) {
        return android.text.TextUtils.isEmpty(str) ? null : str;
    }

    public static String emptyIfNull(@Nullable String str) {
        return android.text.TextUtils.isEmpty(str) ? "" : str;
    }

    public static CharSequence nullIfEmpty(@Nullable CharSequence str) {
        return android.text.TextUtils.isEmpty(str) ? null : str;
    }

    public static CharSequence emptyIfNull(@Nullable CharSequence str) {
        return android.text.TextUtils.isEmpty(str) ? "" : str;
    }

    public static String toUpperCase(@Nullable String str) {
        return android.text.TextUtils.isEmpty(str) ? str
                : Objects.requireNonNull(str).toUpperCase(Locale.US);
    }

    public static String toLowerCase(@Nullable String str) {
        return android.text.TextUtils.isEmpty(str) ? str
                : Objects.requireNonNull(str).toLowerCase(Locale.US);
    }

    public static String getExtension(String name) {
        if (android.text.TextUtils.isEmpty(name)) {
            return "";
        }

        int dotPos = name.lastIndexOf('.');
        return dotPos >= 0 ? name.substring(dotPos + 1) : "";
    }

    public static String getMimeType(String name) {
        return MimeTypeMap.getSingleton().getMimeTypeFromExtension(getExtension(name));
    }

    public static boolean matchFileType(File file, String... ext) {
        return file != null && matchFileType(file.getName(), ext);
    }

    public static boolean matchFileType(String filename, String... suffix) {
        if (android.text.TextUtils.isEmpty(filename)) {
            return false;
        }
        filename = TextUtils.toLowerCase(filename);
        for (String s : suffix) {
            if (filename.endsWith(TextUtils.toLowerCase(s))) {
                return true;
            }
        }
        return false;
    }

    public static String fileTypeByUrl(String url) {
        if (!TextUtils.isEmpty(url)) {
            try {
                return fileTypeByPath(Uri.parse(url).getPath());
            } catch (Throwable e) {
                Log.e("@", "fail to parse ext from url: " + url, e);
            }
        }
        return ".xxx";
    }

    public static String fileTypeByPath(String path) {
        if (path == null) {
            return ".xxx";
        }
        int extPos = path.lastIndexOf('.');
        if (extPos >= 0 && extPos < path.length() - 2) {
            return TextUtils.toLowerCase(path.substring(extPos));
        }
        return ".xxx";
    }

    /**
     * 关键字高亮变色
     *
     * @param color   变化的色值
     * @param text    文字
     * @param keyword 文字中的关键字
     * @return 结果SpannableString
     */
    public static SpannableString matcherSearchTitle(int color, String text, String keyword) {
        SpannableString s = new SpannableString(text);
        keyword = escapeExprSpecialWord(keyword);
        text = escapeExprSpecialWord(text);
        if (text.contains(keyword) && !TextUtils.isEmpty(keyword)) {
            try {
                Pattern p = Pattern.compile(keyword);
                Matcher m = p.matcher(s);
                while (m.find()) {
                    int start = m.start();
                    int end = m.end();
                    s.setSpan(new ForegroundColorSpan(color), start, end,
                            Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return s;
    }

    /**
     * 转义正则特殊字符 （$()*+.[]?\^{},|）
     */
    public static String escapeExprSpecialWord(String keyword) {
        if (!TextUtils.isEmpty(keyword)) {
            String[] fbsArr = {"\\", "$", "(", ")", "*", "+", ".", "[", "]",
                    "?", "^", "{", "}", "|"};
            for (String key : fbsArr) {
                if (keyword.contains(key)) {
                    keyword = keyword.replace(key, "\\" + key);
                }
            }
        }
        return keyword;
    }

    public static String leftPad(String originalString, int length, char padCharacter) {
        StringBuilder sb = new StringBuilder();
        while (sb.length() + originalString.length() < length) {
            sb.append(padCharacter);
        }
        sb.append(originalString);
        return sb.toString();
    }

    static class URLSpanNoUnderline extends URLSpan {

        public URLSpanNoUnderline(String url) {
            super(url);
        }

        public void updateDrawState(TextPaint drawState) {
            super.updateDrawState(drawState);
            drawState.setUnderlineText(false);
        }
    }

    public static int getLengthContainChineseChar(String str) {
        int stringLength = 0;
        try {
            if (!isEmpty(str)) {
                stringLength = str.getBytes("GB18030").length;
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return stringLength;
    }

    public static boolean endsWith(String str, String suffix) {
        return !(TextUtils.isEmpty(str) || TextUtils.isEmpty(suffix)) && str.endsWith(suffix);
    }

    /**
     * 有 emoji 的截断字符，最大 n 个字符，如果大于这个长度则返回 n-1 + ellips.
     *
     * @param str      字符
     * @param maxWidth 最大长度
     * @param ellips   超过最大长度的时候添加的尾巴
     * @return 截断后的字符
     */
    public static String abbrByCodePoints(String str, int maxWidth, String ellips) {
        if (isEmpty(str) || maxWidth <= 0 || codePointsCount(str) <= maxWidth) {
            return str;
        }
        if (isEmpty(ellips)) {
            return limitTextByMaxLength(str, maxWidth);
        } else {
            return limitTextByMaxLength(str, maxWidth - 1) + ellips;
        }
    }

    /**
     * 截断字符，最大 n 个字符，如果大于这个长度则返回 n-1 + ellips.
     *
     * @param str      字符
     * @param maxWidth 最大长度
     * @param ellips   超过最大长度的时候添加的尾巴
     * @return 截断后的字符
     */
    public static String abbreviate(String str, int maxWidth, String ellips) {
        if (isEmpty(str) || maxWidth <= 0 || str.length() <= maxWidth) {
            return str;
        }
        if (isEmpty(ellips)) {
            return str.substring(0, maxWidth);
        } else {
            return str.substring(0, maxWidth - 1) + ellips;
        }
    }

    public static DecimalFormat newDecimalFormat(String pattern) {
        return new DecimalFormat(pattern, new DecimalFormatSymbols(Locale.US));
    }

    public static SimpleDateFormat newSimpleDateFormat(String pattern) {
        return new SimpleDateFormat(pattern, new DateFormatSymbols(Locale.US));
    }

    public static String format(String format, Object... args) {
        return String.format(Locale.US, format, args);
    }
}
