package JavaProjects.TodoApp.src;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class Task implements Serializable {

  private static final long serialVersionUID = 1L;

  public enum Priority {
    LOW,
    MEDIUM,
    HIGH
  }

  private final String title;
  private boolean isDone;
  private final LocalDate deadline;
  private final Priority priority;

  private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy");

  public Task(String title, LocalDate deadline, Priority priority) {
    this.title = title;
    this.isDone = false;
    this.deadline = deadline;
    this.priority = priority;
  }

  public String getTitle() {
    return title;
  }

  public boolean isDone() {
    return isDone;
  }

  public void markDone() {
    isDone = true;
  }

  public void markUndone() {
    this.isDone = false;
  }

  public LocalDate getDeadline() {
    return deadline;
  }

  public Priority getPriority() {
    return priority;
  }

  @Override
  public String toString() {
    String dateStr = (deadline != null) ? deadline.format(DATE_FORMATTER) : "—";
    String priorityStr = (priority != null) ? priority.name() : "—";
    return (isDone ? "[✔]" : "[ ]") + " " + title + " (" + priorityStr + ", " + dateStr + ")";
  }

  public static LocalDate parseDate(String input) {
    try {
      return LocalDate.parse(input, DATE_FORMATTER);
    } catch (Exception e) {
      return null;
    }
  }
}
