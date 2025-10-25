package JavaProjects.TodoApp.src;

import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;

public class Main {

  private static final String TASK_FILE = System.getProperty("user.home") + "/Documents/tasks.dat";

  public static void main(String[] args) {
    // Load language from settings
    UIHelper.Language lang = UIHelper.loadLanguageFromFile();
    UIHelper.setLanguage(lang);

    ArrayList<Task> tasks = loadTasks();
    try (Scanner scanner = new Scanner(System.in)) {
      boolean running = true;

      while (running) {
        TodoPrinter.printTodoList(tasks);

        int choice = getValidNumber(scanner, UIHelper.t(""), 1, 5);

        switch (choice) {
          case 1 -> {
            addTask(tasks, scanner);
          }
          case 2 -> {
            markTaskDone(tasks, scanner);
          }
          case 3 -> {
            deleteTask(tasks, scanner);
          }
          case 4 -> {
            settingsMenu(scanner);
            // reload language after settings (UIHelper already updated and saved)
            UIHelper.setLanguage(UIHelper.getLanguage());
          }
          case 5 -> {
            System.out.println(
                UIHelper.PASTEL_GREEN + UIHelper.t("goodbye") + UIHelper.RESET);
            saveTasks(tasks);
            running = false;
          }
          default -> System.out.println(
              String.format(UIHelper.t("invalid_choice"), 1, 5));
        }
      }

      scanner.close();
    }
  }

  private static void addTask(ArrayList<Task> tasks, Scanner scanner) {
    System.out.print(UIHelper.t("enter_new"));
    String title = scanner.nextLine().trim();
    if (title.isEmpty()) {
      System.out.println(
          UIHelper.PASTEL_RED + UIHelper.t("empty_task") + UIHelper.RESET);
      return;
    }
    tasks.add(new Task(title));
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
            UIHelper.PASTEL_RED + UIHelper.t("please_number") + UIHelper.RESET);
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
            UIHelper.PASTEL_RED + UIHelper.t("please_number") + UIHelper.RESET);
      }
    }
  }

  private static void saveTasks(ArrayList<Task> tasks) {
    try (
        ObjectOutputStream oos = new ObjectOutputStream(
            new FileOutputStream(TASK_FILE))) {
      oos.writeObject(tasks);
    } catch (IOException e) {
      System.out.println(
          UIHelper.PASTEL_RED +
              UIHelper.t("saving_error") +
              e.getMessage() +
              UIHelper.RESET);
    }
  }

  @SuppressWarnings("unchecked")
  private static ArrayList<Task> loadTasks() {
    File f = new File(TASK_FILE);
    if (!f.exists())
      return new ArrayList<>();
    try (
        ObjectInputStream ois = new ObjectInputStream(
            new FileInputStream(TASK_FILE))) {
      return (ArrayList<Task>) ois.readObject();
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
            UIHelper.PASTEL_RED + UIHelper.t("please_number") + UIHelper.RESET);
        continue;
      }
      try {
        int n = Integer.parseInt(input);
        if (n < min || n > max) {
          System.out.println(
              UIHelper.PASTEL_RED +
                  String.format(UIHelper.t("invalid_choice"), min, max) +
                  UIHelper.RESET);
          continue;
        }
        return n;
      } catch (NumberFormatException e) {
        System.out.println(
            UIHelper.PASTEL_RED + UIHelper.t("please_number") + UIHelper.RESET);
      }
    }
  }
}
