package JavaProjects.PlanIT.src;

import java.io.*;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Scanner;

public class Main {

  private static final String TASK_FILE = System.getProperty("user.home") + "/Documents/tasks.dat";
  private static final String ARCHIVE_FILE = System.getProperty("user.home") + "/Documents/archive.dat";

  public static void main(String[] args) {

    UIHelper.Language lang = UIHelper.loadLanguageFromFile();
    UIHelper.setLanguage(lang);

    ArrayList<Task> tasks = loadTasks();
    try (Scanner scanner = new Scanner(System.in)) {
      boolean running = true;

      while (running) {
        TodoPrinter.printTodoList(tasks);

        int choice = getValidNumber(scanner, UIHelper.t(""), 1, 7);

        switch (choice) {
          case 1 -> addTask(tasks, scanner);
          case 2 -> TaskManager.editTask(tasks, scanner);
          case 3 -> markTaskDone(tasks, scanner);
          case 4 -> deleteTask(tasks, scanner);
          case 5 -> TaskManager.archiveTask(tasks, scanner);
          case 6 -> TaskManager.viewArchive(scanner);
          case 7 -> TaskManager.clearCompletedTasks(tasks);
          case 8 -> sortTasks(tasks, scanner);
          case 9 -> settingsMenu(scanner);
          case 10 -> {
            System.out.println(UIHelper.PASTEL_GREEN + UIHelper.t("goodbye") + UIHelper.RESET);
            saveTasks(tasks);
            running = false;
          }
          default -> System.out.println(
              String.format(UIHelper.t("invalid_choice"), 1, 7));
        }
      }

      scanner.close();
    }
  }

  public static String getTaskFilePath() {
    return TASK_FILE;
  }

  public static String getArchiveFilePath() {
    return ARCHIVE_FILE;
  }

  private static void addTask(ArrayList<Task> tasks, Scanner scanner) {
    System.out.print(UIHelper.t("enter_new"));
    String title = scanner.nextLine().trim();
    if (title.isEmpty()) {
      System.out.println(
          UIHelper.PASTEL_RED_URGENT + UIHelper.t("empty_task") + UIHelper.RESET);
      return;
    }

    // Deadline
    System.out.print("Enter deadline (dd.MM.yyyy) or leave empty: ");
    String deadlineInput = scanner.nextLine().trim();
    LocalDate deadline = null;
    if (!deadlineInput.isEmpty()) {
      try {
        deadline = LocalDate.parse(deadlineInput, DateTimeFormatter.ofPattern("dd.MM.yyyy"));
      } catch (Exception e) {
        System.out.println(UIHelper.PASTEL_YELLOW + "Invalid date format, ignored." + UIHelper.RESET);
      }
    }

    // Priority
    System.out.print(UIHelper.t("add_priority"));
    String prioInput = scanner.nextLine().trim();
    Task.Priority priority = null;
    if (!prioInput.isEmpty()) {
      try {
        int num = Integer.parseInt(prioInput);
        switch (num) {
          case 1 -> priority = Task.Priority.HIGH;
          case 2 -> priority = Task.Priority.MEDIUM;
          case 3 -> priority = Task.Priority.LOW;
          default -> {
            System.out.println(UIHelper.PASTEL_RED + UIHelper.t("invalid_priority") + UIHelper.RESET);
          }
        }
      } catch (NumberFormatException e) {
        System.out.println(UIHelper.PASTEL_RED + UIHelper.t("please_number") + UIHelper.RESET);
      }
    }

    tasks.add(new Task(title, deadline, priority));
    saveTasks(tasks);
    System.out.println(
        UIHelper.PASTEL_GREEN + UIHelper.t("task_added") + UIHelper.RESET);
  }

  private static void markTaskDone(ArrayList<Task> tasks, Scanner scanner) {
    if (tasks.isEmpty()) {
      System.out.println(
          UIHelper.PASTEL_YELLOW + UIHelper.t("no_to_mark") + UIHelper.RESET);
      return;
    }

    while (true) {
      int num = getValidNumber(
          scanner,
          UIHelper.t("enter_num_mark"),
          0,
          tasks.size());
      if (num == 0) {
        System.out.println(
            UIHelper.PASTEL_YELLOW +
                UIHelper.t("deletion_cancel") +
                UIHelper.RESET);
        break;
      } else if (num > 0 && num <= tasks.size()) {
        Task t = tasks.get(num - 1);
        if (!t.isDone()) {
          t.markDone();
          System.out.println(
              UIHelper.PASTEL_GREEN + UIHelper.t("marked_done") + UIHelper.RESET);
        } else {
          t.markUndone();
          System.out.println(
              UIHelper.PASTEL_GREEN + UIHelper.t("marked_undone") + UIHelper.RESET);
        }
        saveTasks(tasks);
        break;
      } else {
        System.out.println(
            UIHelper.PASTEL_RED_URGENT + UIHelper.t("please_number") + UIHelper.RESET);
      }
    }
  }

  private static void deleteTask(ArrayList<Task> tasks, Scanner scanner) {
    if (tasks.isEmpty()) {
      System.out.println(
          UIHelper.PASTEL_YELLOW + UIHelper.t("no_to_delete") + UIHelper.RESET);
      return;
    }
    while (true) {
      int num = getValidNumber(
          scanner,
          UIHelper.t("enter_num_delete"),
          0,
          tasks.size());

      if (num == 0) {
        System.out.println(
            UIHelper.PASTEL_YELLOW +
                UIHelper.t("deletion_cancel") +
                UIHelper.RESET);
        break;
      } else if (num > 0 && num <= tasks.size()) {
        Task removed = tasks.remove(num - 1);
        System.out.println(
            UIHelper.PASTEL_GREEN +
                UIHelper.t("task_deleted") +
                removed.getTitle() +
                UIHelper.RESET);
        saveTasks(tasks);
        break;
      } else {
        System.out.println(
            UIHelper.PASTEL_RED_URGENT + UIHelper.t("please_number") + UIHelper.RESET);
      }
    }
  }

  public static void saveTasks(ArrayList<Task> tasks) {
    try (
        ObjectOutputStream oos = new ObjectOutputStream(
            new FileOutputStream(TASK_FILE))) {
      oos.writeObject(tasks);
    } catch (IOException e) {
      System.out.println(
          UIHelper.PASTEL_RED_URGENT +
              UIHelper.t("saving_error") +
              e.getMessage() +
              UIHelper.RESET);
    }
  }

  @SuppressWarnings("unchecked")
  public static ArrayList<Task> loadTasks() {
    File f = new File(TASK_FILE);
    if (!f.exists())
      return new ArrayList<>();
    try (ObjectInputStream ois = new ObjectInputStream(new FileInputStream(TASK_FILE))) {
      ArrayList<Task> list = (ArrayList<Task>) ois.readObject();

      // Fix for older tasks missing createdDate
      for (Task t : list) {
        if (t.getCreatedDate() == null) {
          // "now" as fallback timestamp
          try {
            java.lang.reflect.Field field = Task.class.getDeclaredField("createdDate");
            field.setAccessible(true);
            field.set(t, java.time.LocalDateTime.now());
          } catch (IllegalAccessException | IllegalArgumentException | NoSuchFieldException ignored) {
          }
        }
      }

      return list;
    } catch (IOException | ClassNotFoundException e) {
      return new ArrayList<>();
    }
  }

  private static void settingsMenu(Scanner scanner) {
    while (true) {
      UIHelper.printHeader(UIHelper.t("settings_title"));
      System.out.println(UIHelper.t("settings_lang"));
      System.out.println(UIHelper.t("settings_back"));

      int opt = getValidNumber(scanner, "Choose an option (1-2): ", 1, 2);
      if (opt == 1) {
        System.out.print(UIHelper.t("choose_lang"));
        int langChoice = getValidNumber(scanner, "", 0, 2);
        switch (langChoice) {
          case 0 -> System.out.println(
              UIHelper.PASTEL_YELLOW +
                  UIHelper.t("deletion_cancel") +
                  UIHelper.RESET);
          case 1 -> {
            UIHelper.setLanguage(UIHelper.Language.EN);
            UIHelper.saveLanguageToFile(UIHelper.Language.EN);
            System.out.println(
                UIHelper.PASTEL_GREEN +
                    "Language set to English." +
                    UIHelper.RESET);
            break;
          }
          case 2 -> {
            UIHelper.setLanguage(UIHelper.Language.DE);
            UIHelper.saveLanguageToFile(UIHelper.Language.DE);
            System.out.println(
                UIHelper.PASTEL_GREEN +
                    "Sprache auf Deutsch gesetzt." +
                    UIHelper.RESET);
            break;
          }
        }
      } else {
        break;
      }
    }
  }

  private static void sortTasks(ArrayList<Task> tasks, Scanner scanner) {
    if (tasks.isEmpty()) {
      System.out.println(UIHelper.PASTEL_YELLOW + UIHelper.t("no_tasks") + UIHelper.RESET);
      return;
    }

    while (true) {
      UIHelper.printHeader(UIHelper.t("sort_menu_title"));
      System.out.println(UIHelper.t("sort_choose"));
      System.out.println(UIHelper.t("sort_priority"));
      System.out.println(UIHelper.t("sort_deadline"));
      System.out.println(UIHelper.t("sort_created"));
      System.out.println(UIHelper.t("sort_alpha"));
      System.out.println(UIHelper.t("sort_back"));

      int opt = getValidNumber(scanner, "→ ", 1, 5);
      switch (opt) {
        case 1 -> {
          // Sort by Priority (HIGH -> MEDIUM -> LOW -> null)
          tasks.sort((a, b) -> {
            if (a.getPriority() == null && b.getPriority() == null)
              return 0;
            if (a.getPriority() == null)
              return 1;
            if (b.getPriority() == null)
              return -1;
            return a.getPriority().compareTo(b.getPriority());
          });
        }
        case 2 -> {
          // Sort by Deadline (earliest first, nulls last)
          tasks.sort((a, b) -> {
            if (a.getDeadline() == null && b.getDeadline() == null)
              return 0;
            if (a.getDeadline() == null)
              return 1;
            if (b.getDeadline() == null)
              return -1;
            return a.getDeadline().compareTo(b.getDeadline());
          });
        }
        case 3 -> {
          // Sort by Created Date (oldest first, nulls last)
          tasks.sort((a, b) -> {
            if (a.getCreatedDate() == null && b.getCreatedDate() == null)
              return 0;
            if (a.getCreatedDate() == null)
              return 1;
            if (b.getCreatedDate() == null)
              return -1;
            return a.getCreatedDate().compareTo(b.getCreatedDate());
          });
        }
        case 4 -> {
          // Sort alphabetically (A → Z)
          tasks.sort((a, b) -> a.getTitle().compareToIgnoreCase(b.getTitle()));
        }
        case 5 -> {
          return;
        }
        default -> System.out.println(UIHelper.PASTEL_RED + UIHelper.t("please_number") + UIHelper.RESET);
      }

      System.out.println(UIHelper.PASTEL_GREEN + UIHelper.t("sort_done") + UIHelper.RESET);
      saveTasks(tasks);
      break;
    }
  }

  private static int getValidNumber(
      Scanner scanner,
      String prompt,
      int min,
      int max) {
    while (true) {
      if (prompt != null && !prompt.isEmpty())
        System.out.print(prompt);
      String input = scanner.nextLine().trim();
      if (input.isEmpty()) {
        System.out.println(
            UIHelper.PASTEL_RED_URGENT + UIHelper.t("please_number") + UIHelper.RESET);
        continue;
      }
      try {
        int n = Integer.parseInt(input);
        if (n < min || n > max) {
          System.out.println(
              UIHelper.PASTEL_RED_URGENT +
                  String.format(UIHelper.t("invalid_choice"), min, max) +
                  UIHelper.RESET);
          continue;
        }
        return n;
      } catch (NumberFormatException e) {
        System.out.println(
            UIHelper.PASTEL_RED_URGENT + UIHelper.t("please_number") + UIHelper.RESET);
      }
    }
  }
}
