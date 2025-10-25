package JavaProjects.TodoApp.src;

import java.util.ArrayList;

public class TodoPrinter {

    private static final int NUM_WIDTH = 3;
    private static final int TASK_WIDTH = 40;

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
            String title = t.getTitle();

            int titleMaxLength = TASK_WIDTH - 4;
            if (title.length() > titleMaxLength) {
                title = title.substring(0, titleMaxLength - 3) + "...";
            }

            System.out.printf(lineColor + "║%-" + NUM_WIDTH + "d║" + reset + "%-3s %-" + (TASK_WIDTH - 4) + "s"
                    + lineColor + "║%n" + reset, i + 1, coloredStatus, title);
        }

        System.out.println(lowLine);
        printButtons(lineColor, reset);
    }

    private static String repeat(String s, int times) {
        return s.repeat(times);
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
