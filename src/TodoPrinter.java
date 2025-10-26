package JavaProjects.TodoApp.src;

import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.regex.Pattern;

public class TodoPrinter {

    private static final int NUM_WIDTH = 3;
    private static final int TASK_WIDTH = 40;
    private static final DateTimeFormatter DATE_FMT = DateTimeFormatter.ofPattern("dd.MM.yyyy");

    // ANSI-Handling
    private static final Pattern ANSI_PATTERN = Pattern.compile("\\u001B\\[[;\\d]*m");

    private static String stripAnsi(String s) {
        return ANSI_PATTERN.matcher(s).replaceAll("");
    }

    private static int visibleLen(String s) {
        return stripAnsi(s).length();
    }

    // schneidet einen String auf sichtbare Breite 'max', lässt ANSI-Sequenzen
    // intakt
    private static String clipVisible(String s, int max) {
        StringBuilder out = new StringBuilder();
        int vis = 0;
        for (int i = 0; i < s.length() && vis < max; i++) {
            char c = s.charAt(i);
            if (c == 0x1B) { // ESC
                int j = i + 1;
                if (j < s.length() && s.charAt(j) == '[') {
                    j++;
                    while (j < s.length() && s.charAt(j) != 'm')
                        j++;
                    if (j < s.length())
                        j++; // 'm' einschließen
                }
                out.append(s, i, j);
                i = j - 1;
            } else {
                out.append(c);
                vis++;
            }
        }
        return out.toString();
    }

    public static void printTodoList(ArrayList<Task> tasks) {
        UIHelper.printHeader(UIHelper.t("welcome"));

        String placeHolder = UIHelper.PASTEL_PINK;
        String lineColor = UIHelper.PASTEL_BROWN;
        String checkColor = UIHelper.PASTEL_GREEN;
        String reset = UIHelper.RESET;

        String topLine = lineColor + "╔" + repeat("═", NUM_WIDTH) + "╦" + repeat("═", TASK_WIDTH) + "╗" + reset;
        String midLine = lineColor + "╠" + repeat("═", NUM_WIDTH) + "╬" + repeat("═", TASK_WIDTH) + "╣" + reset;
        String lowLine = lineColor + "╚" + repeat("═", NUM_WIDTH) + "╩" + repeat("═", TASK_WIDTH) + "╝" + reset;

        System.out.println(topLine);
        System.out.printf(lineColor + "║%-" + NUM_WIDTH + "s║%-" + TASK_WIDTH + "s║%n" + reset, "", "Task");
        System.out.println(midLine);

        for (int i = 0; i < tasks.size(); i++) {

            Task t = tasks.get(i);
            String statusSymbol = t.isDone() ? "✔" : " ";
            String coloredStatus = (t.isDone() ? checkColor : placeHolder) + "[" + statusSymbol + "]" + UIHelper.RESET;
            String priorityColor;
            String priorityText;

            if (t.getPriority() == null) {
                priorityColor = UIHelper.PASTEL_BROWN;
                priorityText = "—";
            } else {
                switch (t.getPriority()) {
                    case HIGH -> {
                        priorityColor = UIHelper.PASTEL_RED_URGENT;
                        priorityText = "HIGH";
                    }
                    case MEDIUM -> {
                        priorityColor = UIHelper.PASTEL_YELLOW;
                        priorityText = "MEDIUM";
                    }
                    case LOW -> {
                        priorityColor = UIHelper.PASTEL_CYAN;
                        priorityText = "LOW";
                    }
                    default -> {
                        priorityColor = UIHelper.PASTEL_BROWN;
                        priorityText = "—";
                    }
                }
            }

            String deadlineStr = (t.getDeadline() != null) ? t.getDeadline().format(DATE_FMT) : "—";

            // Bausteine
            String left = coloredStatus + " "; // z.B. "[✔] "
            String right = " (" + priorityColor + priorityText + reset + ")  " + deadlineStr; // z.B. " (HIGH)
                                                                                              // 12.03.2099"
            String title = t.getTitle(); // hat keine ANSI – erleichtert Truncation

            // Verfügbare Sichtbreite für den Title: TASK_WIDTH - visibleLen(left) -
            // visibleLen(right)
            int spaceForTitle = Math.max(0, TASK_WIDTH - visibleLen(left) - visibleLen(right));

            // ggf. kürzen + Ellipsis
            String titleShown = title;
            if (visibleLen(titleShown) > spaceForTitle) {
                // mindestens Platz für "..." lassen
                int target = Math.max(0, spaceForTitle - 3);
                titleShown = title.substring(0, Math.min(title.length(), target)) + (spaceForTitle >= 3 ? "..." : "");
            }

            String cell = left + titleShown + right;

            // Wenn wegen Rundungsfehlern noch zu lang (sichtbar), hart auf Breite clippen
            if (visibleLen(cell) > TASK_WIDTH) {
                cell = clipVisible(cell, TASK_WIDTH);
            }
            // Falls kürzer: rechts mit Spaces auffüllen
            int pad = TASK_WIDTH - visibleLen(cell);
            if (pad > 0)
                cell = cell + " ".repeat(pad);

            System.out.printf(lineColor + "║%-" + NUM_WIDTH + "d║" + reset + "%s" + lineColor + "║%n" + reset, i + 1,
                    cell);
        }

        System.out.println(lowLine);
        printButtons(lineColor, reset);
    }

    private static String repeat(String s, int times) {
        return s.repeat(times);
    }

    private static String padToWidth(String s, int width) {
        String out = s;
        if (visibleLen(out) > width)
            out = clipVisible(out, width);
        int pad = width - visibleLen(out);
        return out + " ".repeat(Math.max(0, pad));
    }

    private static void printButtons(String lineColor, String reset) {
        String[] buttons = {
                "1 - Add",
                "2 - Done/Undone",
                "3 - Delete",
                "4 - Settings",
                "5 - Exit",
        };

        int buttonWidth = 0;
        for (String b : buttons)
            if (b.length() > buttonWidth)
                buttonWidth = b.length();
        buttonWidth += 4;

        for (String b : buttons)
            System.out.print(lineColor + "╔" + "═".repeat(buttonWidth) + "╗ " + reset);
        System.out.println();
        for (String b : buttons)
            System.out.print(lineColor + "║ " + b + " ".repeat(buttonWidth - b.length() - 1) + "║ " + reset);
        System.out.println();
        for (String b : buttons)
            System.out.print(lineColor + "╚" + "═".repeat(buttonWidth) + "╝ " + reset);
        System.out.println("\n");
    }
}
