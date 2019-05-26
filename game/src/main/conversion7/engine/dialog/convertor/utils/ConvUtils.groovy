package conversion7.engine.dialog.convertor.utils

import com.google.common.base.CaseFormat
import conversion7.engine.dialog.convertor.ConverterError
import org.apache.commons.lang3.StringUtils
import org.jsoup.Jsoup

class ConvUtils {

    static String getSafeFileName(String name) {
        return name.replaceAll('[^A-Za-z0-9_.]', '_')
    }
    static String getResName(String keyName) {
        return getConstName(keyName)
    }
    static String getConstName(String s) {
        def val = removeHtml(
                CaseFormat.LOWER_CAMEL.to(
                        CaseFormat.UPPER_UNDERSCORE, toCamelCase(s)
                )
        )
        if (val == 'NULL') {
            throw new ConverterError("Invalid key! Maybe encoding broken! Initial text: " + s);
        }
        return val
    }

    static String removeHtml(String s) {
        return Jsoup.parse(s).text()
    }

    public static String toCamelCase(final String inText) {
        String text = inText;
        if (text == null) {
            return "null";
        }
        // convert bad symbols to spaces
        text = text.replaceAll("[^a-zA-Z0-9]", " ");
        // no numbers on start
        text = text.replaceAll("^\\d*", "");
        // split by upper-case symbols to do not break existing camel-case
        text = text.replaceAll(String.format("%s|%s|%s", "(?<=[A-Z])(?=[A-Z][a-z])", "(?<=[^A-Z])(?=[A-Z])",
                "(?<=[A-Za-z])(?=[^A-Za-z])"), " ");

        String validatedText = text.trim();
        if (validatedText.isEmpty()) {
            return "null";
        }
        String[] parts = validatedText.split(" ");
        StringBuilder camelCaseBuilder = new StringBuilder();
        for (String part : parts) {
            if (!part.isEmpty()) {
                String proceedPart = StringUtils.lowerCase(part);
                if (camelCaseBuilder.length() > 0) {
                    proceedPart = StringUtils.capitalize(proceedPart);
                }
                camelCaseBuilder.append(proceedPart);
            }
        }
        return camelCaseBuilder.toString();
    }
}
