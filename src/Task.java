package JavaProjects.TodoApp.src;

import java.io.Serializable;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class Task implements Serializable {

  private static final long serialVersionUID = 1L;

  public enum Priority {
    HIGH,
    MEDIUM,
    LOW
  }

  private final String title;
  private boolean isDone;
  private boolean isArchived;
  private final LocalDateTime createdDate;
  private LocalDate deadline;
  private Priority priority;

  private static final DateTimeFormatter DATE_FORMATTER = DateTimeFormatter.ofPattern("dd.MM.yyyy");

  public Task(String title, LocalDate deadline, Priority priority) {
    this.title = title;
    this.isDone = false;
    this.isArchived = false;
    this.createdDate = LocalDateTime.now();
    this.deadline = deadline;
    this.priority = priority;
  }

  public String getTitle() {
    return title;
  }

  public boolean isDone() {
    return isDone;
  }

  public boolean isArchived() {
    return isArchived;
  }

  public void setArchived(boolean archived) {
    isArchived = archived;
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

  public void setDeadline(LocalDate newDeadline) {
    this.deadline = newDeadline;
  }

  public Priority getPriority() {
    return priority;
  }

  public void setPriority(Priority newPriority) {
    this.priority = newPriority;
  }

  public LocalDateTime getCreatedDate() {
    return createdDate;
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
