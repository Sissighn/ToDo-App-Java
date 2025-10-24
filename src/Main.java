package JavaProjects.TodoApp.src;

import java.io.*;
import java.util.ArrayList;
import java.util.Scanner;

public class Main {

  private static final String FILE_NAME =
    System.getProperty("user.home") + "/Documents/tasks.dat";

  public static void main(String[] args) {
    System.out.println("Welcome to your To-Do List!");
    ArrayList<Task> tasks = loadTasks();
    try (Scanner scanner = new Scanner(System.in)) {
      boolean running = true;
      while (running) {
        System.out.println("\n=== Your To-Do List ===");
        if (tasks.isEmpty()) {
          System.out.println("No tasks found.");
        } else {
          for (int i = 0; i < tasks.size(); i++) {
            System.out.println((i + 1) + ". " + tasks.get(i));
          }
        }
        System.out.println("\nOptions:");
        System.out.println("1 - Add a new task");
        System.out.println("2 - Mark a task as done");
        System.out.println("3 - Delete a task");
        System.out.println("4 - Exit");
        int option = getValidNumber(scanner, "Please choose an option (1–4): ");
        switch (option) {
          case 1 -> addTask(tasks, scanner);
          case 2 -> markTaskDone(tasks, scanner);
          case 3 -> deleteTask(tasks, scanner);
          case 4 -> {
            running = false;
            System.out.println("Goodbye!");
            saveTasks(tasks);
          }
          default -> System.out.println("Invalid choice! Please choose 1–4.");
        }
      }
    }
  }

  private static void addTask(ArrayList<Task> tasks, Scanner scanner) {
    System.out.print("Enter the new task: ");
    String title = scanner.nextLine().trim();
    if (title.isEmpty()) {
      System.out.println("Task cannot be empty!");
      return;
    }
    tasks.add(new Task(title));
    saveTasks(tasks);
    System.out.println("Task added successfully!");
  }

  private static void markTaskDone(ArrayList<Task> tasks, Scanner scanner) {
    if (tasks.isEmpty()) {
      System.out.println("No tasks to mark as done.");
      return;
    }
    int num = getValidNumber(
      scanner,
      "Enter the number of the task to mark as done: "
    );
    if (num > 0 && num <= tasks.size()) {
      tasks.get(num - 1).markDone();
      saveTasks(tasks);
      System.out.println("Task marked as done!");
    } else {
      System.out.println("Invalid task number!");
    }
  }

  private static void deleteTask(ArrayList<Task> tasks, Scanner scanner) {
    if (tasks.isEmpty()) {
      System.out.println("No tasks to delete.");
      return;
    }
    while (true) {
      int num = getValidNumber(
        scanner,
        "Enter the number of the task to delete (or 0 to cancel): "
      );
      if (num == 0) {
        System.out.println("Deletion cancelled.");
        break;
      } else if (num > 0 && num <= tasks.size()) {
        Task removed = tasks.remove(num - 1);
        System.out.println("Task deleted: " + removed.getTitle());
        saveTasks(tasks);
        break;
      } else {
        System.out.println("Invalid task number! Please try again.");
      }
    }
  }

  private static void saveTasks(ArrayList<Task> tasks) {
    try (
      ObjectOutputStream oos = new ObjectOutputStream(
        new FileOutputStream(FILE_NAME)
      )
    ) {
      oos.writeObject(tasks);
    } catch (IOException e) {
      System.out.println("Error saving tasks: " + e.getMessage());
    }
  }

  @SuppressWarnings("unchecked")
  private static ArrayList<Task> loadTasks() {
    File file = new File(FILE_NAME);
    if (!file.exists()) {
      return new ArrayList<>();
    }
    try (
      ObjectInputStream ois = new ObjectInputStream(
        new FileInputStream(FILE_NAME)
      )
    ) {
      return (ArrayList<Task>) ois.readObject();
    } catch (IOException | ClassNotFoundException e) {
      System.out.println("Error loading tasks: " + e.getMessage());
      return new ArrayList<>();
    }
  }

  private static int getValidNumber(Scanner scanner, String message) {
    while (true) {
      System.out.print(message);
      String input = scanner.nextLine().trim();
      try {
        return Integer.parseInt(input);
      } catch (NumberFormatException e) {
        System.out.println("Please enter a valid number!");
      }
    }
  }
}
