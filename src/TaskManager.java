package JavaProjects.PlanIT.src;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Scanner;

public class TaskManager {

    private static String getArchivePath() {
        return Main.getArchiveFilePath();
    }

    /**
     * Allows the user to edit an existing task (title, deadline, priority).
     */
    public static void editTask(ArrayList<Task> tasks, Scanner scanner) {
        if (tasks.isEmpty()) {
            System.out.println(UIHelper.PASTEL_YELLOW + UIHelper.t("no_tasks") + UIHelper.RESET);
            return;
        }

        UIHelper.printPageHeader("edit");
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

    // archive
    public static void archiveTask(ArrayList<Task> tasks, Scanner scanner) {
        if (tasks.isEmpty()) {
            System.out.println(UIHelper.PASTEL_YELLOW + UIHelper.t("no_tasks") + UIHelper.RESET);
            return;
        }

        UIHelper.printPageHeader("archive");
        TodoPrinter.printTodoList(tasks);

        int num = getValidNumber(scanner, UIHelper.t("archive_enter_num"), 0, tasks.size());
        if (num == 0) {
            System.out.println(UIHelper.PASTEL_YELLOW + UIHelper.t("deletion_cancel") + UIHelper.RESET);
            return;
        }

        Task t = tasks.get(num - 1);
        t.setArchived(true);

        // Load archived list
        ArrayList<Task> archivedTasks = loadArchive();
        archivedTasks.add(t);

        // Remove from main list
        tasks.remove(t);

        // Save both lists
        saveArchive(archivedTasks);
        Main.saveTasks(tasks);

        System.out.println(UIHelper.PASTEL_GREEN + UIHelper.t("archived_success") + UIHelper.RESET);
    }

    public static void viewArchive(Scanner scanner) {
        ArrayList<Task> archived = loadArchive();
        UIHelper.printPageHeader("viewArchive");
        if (archived.isEmpty()) {
            System.out.println(UIHelper.PASTEL_YELLOW + UIHelper.t("archive_empty") + UIHelper.RESET);
            return;
        }
        TodoPrinter.printTodoList(archived);
        System.out.println(UIHelper.PASTEL_PURPLE + UIHelper.t("press_enter") + UIHelper.RESET);
        scanner.nextLine();
    }

    @SuppressWarnings("unchecked")
    public static ArrayList<Task> loadArchive() {
        File f = new File(getArchivePath());
        if (!f.exists())
            return new ArrayList<>();
        try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(f))) {
            return (ArrayList<Task>) ois.readObject();
        } catch (IOException | ClassNotFoundException e) {
            return new ArrayList<>();
        }
    }

    private static void saveArchive(ArrayList<Task> archive) {
        try (ObjectOutputStream oos = new ObjectOutputStream(new FileOutputStream(getArchivePath()))) {
            oos.writeObject(archive);
        } catch (IOException e) {
            System.out.println(UIHelper.PASTEL_RED + UIHelper.t("saving_error") + e.getMessage() + UIHelper.RESET);
        }
    }

    public static void clearCompletedTasks(ArrayList<Task> tasks) {
        long before = tasks.size();
        UIHelper.printPageHeader("clear");
        tasks.removeIf(t -> t.isDone() && !t.isArchived());
        long after = tasks.size();

        Main.saveTasks(tasks);
        System.out.println(UIHelper.PASTEL_GREEN +
                (before - after) + " " + UIHelper.t("tasks_cleared") + UIHelper.RESET);
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