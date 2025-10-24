package JavaProjects.TodoApp.src;

public class Task {

  private String title;
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

  @Override
  public String toString() {
    return (isDone ? "[X]" : "[ ]") + title;
  }
}
