package JavaProjects.TodoApp.src;

import java.util.ArrayList;
import java.util.Scanner;

public class Main {

  public static void main(String[] args) {
    ArrayList<Task> tasks = new ArrayList<>();
    Scanner scanner = new Scanner(System.in);
    boolean running = true;

    while (running) {
      System.out.println("\n=== To-Do Liste ===");
      for (int i = 0; i < tasks.size(); i++) {
        System.out.println((i + 1) + ". " + tasks.get(i));
      }

      System.out.println("\nOptionen:");
      System.out.println("1 - add a new task");
      System.out.println("2 - set the task as done");
      System.out.println("3 - end");
      System.out.println("Choose an option please!");
      int option = scanner.nextInt();
      scanner.nextLine();

      switch (option) {
        case 1:
          System.out.println("New Task: ");
          String title = scanner.nextLine();
          tasks.add(new Task(title));
          break;
        case 2:
          System.out.println("Number of the task: ");
          int num = scanner.nextInt();
          if (num > 0 && num <= tasks.size()) {
            tasks.get(num - 1).markDone();
          } else {
            System.out.println("Unvalid number!");
          }
          break;
        case 3:
          running = false;
          System.out.println("Program is closed");
          break;
        default:
          System.out.println("Unvalid option");
      }
    }
    scanner.close();
  }
}
