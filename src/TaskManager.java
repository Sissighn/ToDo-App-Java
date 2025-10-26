package JavaProjects.PlanIT.src;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Scanner;

public class TaskManager {

    /**
     * Allows the user to edit an existing task (title, deadline, priority).
     */
    public static void editTask(ArrayList<Task> tasks, Scanner scanner) {
        if (tasks.isEmpty()) {
            System.out.println(UIHelper.PASTEL_YELLOW + UIHelper.t("no_tasks") + UIHelper.RESET);
            return;
        }

        UIHelper.printHeader(UIHelper.t("edit_title"));
        System.out.println(UIHelper.t("edit_choose"));
        TodoPrinter.printTodoList(tasks);

        int num = getValidNumber(scanner, UIHelper.t("edit_enter_num"), 1, tasks.size());
        Task t = tasks.get(num - 1);

        // Edit title
        System.out.print(UIHelper.t("edit_new_title"));
        String newTitle = scanner.nextLine().trim();
        if (!newTitle.isEmpty()) {
            try {
                java.lang.reflect.Field titleField = Task.class.getDeclaredField("title");
                titleField.setAccessible(true);
                titleField.set(t, newTitle);
            } catch (IllegalAccessException | IllegalArgumentException | NoSuchFieldException ignored) {
            }
        }

        // Edit deadline
        System.out.print(UIHelper.t("edit_new_deadline"));
        String deadlineStr = scanner.nextLine().trim();
        if (!deadlineStr.isEmpty()) {
            LocalDate newDeadline = Task.parseDate(deadlineStr);
            if (newDeadline != null)
                t.setDeadline(newDeadline);
            else
                System.out.println(UIHelper.PASTEL_RED + UIHelper.t("invalid_date") + UIHelper.RESET);
        }

        // Edit priority
        System.out.print(UIHelper.t("edit_new_priority"));
        String prioInput = scanner.nextLine().trim();

        if (!prioInput.isEmpty()) {
            try {
                int prioNum = Integer.parseInt(prioInput);
                switch (prioNum) {
                    case 1 -> t.setPriority(Task.Priority.HIGH);
                    case 2 -> t.setPriority(Task.Priority.MEDIUM);
                    case 3 -> t.setPriority(Task.Priority.LOW);
                    default -> {
                        System.out.println(UIHelper.PASTEL_RED + UIHelper.t("invalid_priority") + UIHelper.RESET);
                    }
                }
            } catch (NumberFormatException e) {
                System.out.println(UIHelper.PASTEL_RED + UIHelper.t("please_number") + UIHelper.RESET);
            }
        }

        System.out.println(UIHelper.PASTEL_GREEN + UIHelper.t("edit_success") + UIHelper.RESET);
    }

    /**
     * Utility method for safe numeric input.
     */
    private static int getValidNumber(Scanner scanner, String prompt, int min, int max) {
        while (true) {
            System.out.print(prompt);
            String input = scanner.nextLine().trim();
            try {
                int n = Integer.parseInt(input);
                if (n >= min && n <= max)
                    return n;
            } catch (NumberFormatException ignored) {
            }
            System.out.println(UIHelper.PASTEL_RED + UIHelper.t("please_number") + UIHelper.RESET);
        }
    }
}