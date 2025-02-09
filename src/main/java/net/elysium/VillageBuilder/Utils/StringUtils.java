package net.elysium.VillageBuilder.Utils;

public final class StringUtils {

    private StringUtils() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated.");
    }

    public static String formatResourceName(String resource) {
        String[] words = resource.split("_");

        StringBuilder formatted = new StringBuilder();
        for (String word : words) {
            if (!formatted.isEmpty()) {
                formatted.append(" ");
            }
            formatted.append(word.substring(0, 1).toUpperCase())
                    .append(word.substring(1).toLowerCase());
        }

        return formatted.toString();
    }
}