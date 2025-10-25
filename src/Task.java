package JavaProjects.TodoApp.src;

import java.io.Serializable;

public class Task implements Serializable {

  private static final long serialVersionUID = 1L;

  private final String title;
  private boolean isDone;

  public Task(String title) {
    this.title = title;
    this.isDone = false;
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

  @Override
  public String toString() {
    return (isDone ? "[X]" : "[ ]") + title;
  }
}
