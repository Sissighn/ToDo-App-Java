package JavaProjects.PlanIT.src;

import java.util.regex.Pattern;

/**
 * Utility class for handling ANSI escape sequences correctly when measuring or
 * clipping text.
 * Prevents layout distortion in colored console UIs.
 */
public class AnsiUtils {

  // Regex pattern for ANSI color codes (e.g. "\u001B[31m")
  private static final Pattern ANSI_PATTERN = Pattern.compile("\\u001B\\[[;\\d]*m");

  // Removes all ANSI escape codes from a string.
  public static String strip(String s) {
    if (s == null)
      return "";
    return ANSI_PATTERN.matcher(s).replaceAll("");
  }

  // Returns visible character length of a string (without counting ANSI codes).
  public static int visibleLength(String s) {
    return strip(s).length();
  }

  /**
   * Clips a string to a given visible width (ignoring ANSI codes).
   * Keeps ANSI sequences intact and never cuts them in half.
   */
  public static String clipVisible(String s, int maxVisible) {
    if (s == null)
      return "";
    StringBuilder out = new StringBuilder();
    int visibleCount = 0;
    for (int i = 0; i < s.length() && visibleCount < maxVisible; i++) {
      char c = s.charAt(i);
      if (c == 0x1B) { // ESC sequence
        int j = i + 1;
        if (j < s.length() && s.charAt(j) == '[') {
          j++;
          while (j < s.length() && s.charAt(j) != 'm')
            j++;
          if (j < s.length())
            j++; // include the 'm'
        }
        out.append(s, i, j);
        i = j - 1;
      } else {
        out.append(c);
        visibleCount++;
      }
    }
    return out.toString();
  }

  /** Pads a string with spaces on the right to reach the target visible width. */
  public static String padRight(String s, int width) {
    if (s == null)
      s = "";
    int pad = width - visibleLength(s);
    return s + " ".repeat(Math.max(0, pad));
  }
}
