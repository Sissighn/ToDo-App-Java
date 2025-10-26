package JavaProjects.TodoApp.src;

import java.io.*;
import java.util.HashMap;
import java.util.Map;

public class UIHelper {

  public enum Language {
    EN,
    DE,
  }

  public static final String RESET = "\u001B[0m";
  public static final String BOLD = "\u001B[1m";

  public static final String PASTEL_PINK = "\u001B[38;5;175m"; // #ffafff
  public static final String PASTEL_PURPLE = "\u001B[38;5;183m"; // #afffff
  public static final String PASTEL_SALMON_PINK = "\u001B[38;5;205m"; // #ff5fff
  public static final String PASTEL_YELLOW = "\u001B[38;5;229m"; // #FFFF87
  public static final String PASTEL_CYAN = "\u001B[38;5;159m"; // #AFFFFF
  public static final String PASTEL_BROWN = "\u001B[38;5;180m"; // #AF8787
  public static final String PASTEL_RED = "\u001B[38;5;131m"; // #875F5F
  public static final String PASTEL_RED_URGENT = "\u001b[38;2;210;58;58m"; // #d23a3aff
  public static final String PASTEL_GREEN = "\u001B[38;5;120m"; // #5fff5f

  private static final String SETTINGS_FILE = "settings.cfg";
  private static Language language = Language.EN;

  // Translation map (key -> [en, de])
  private static final Map<String, String[]> texts = new HashMap<>();

  static {
    texts.put(
        "welcome",
        new String[] {
            "    Welcome to your To-Do List!",
            "    Willkommen zu deiner To-Do-Liste!",
        });
    texts.put(
        "no_tasks",
        new String[] { "No tasks found.", "Keine Aufgaben gefunden." });
    texts.put("options", new String[] { "Options:", "Optionen:" });
    texts.put(
        "opt_add",
        new String[] { "1 - Add a new task", "1 - Neue Aufgabe hinzufügen" });
    texts.put(
        "opt_done",
        new String[] {
            "2 - Mark a task as done/undone",
            "2 - Aufgabe als erledigt/offen markieren",
        });
    texts.put(
        "opt_delete",
        new String[] { "3 - Delete a task", "3 - Aufgabe löschen" });
    texts.put(
        "opt_settings",
        new String[] { "4 - Settings", "4 - Einstellungen" });
    texts.put(
        "enter_new",
        new String[] { "Enter the new task: ", "Neue Aufgabe eingeben: " });
    texts.put(
        "empty_task",
        new String[] { "Task cannot be empty!", "Aufgabe darf nicht leer sein!" });
    texts.put(
        "task_added",
        new String[] {
            "Task added successfully!",
            "Aufgabe erfolgreich hinzugefügt!",
        });
    texts.put(
        "no_to_mark",
        new String[] {
            "No tasks to mark as done.",
            "Keine Aufgaben zum Markieren vorhanden.",
        });
    texts.put(
        "enter_num_mark",
        new String[] {
            "Enter the number of the task to mark/unmark (or 0 to cancel): ",
            "Gib die Nummer der Aufgabe ein, um sie als erledigt oder offen zu markieren (oder 0 zum Abbrechen): ",
        });
    texts.put(
        "marked_done",
        new String[] { "Task marked as done!", "Aufgabe als erledigt markiert!" });
    texts.put(
        "marked_undone",
        new String[] { "Task marked as not done!", "Aufgabe als offen markiert!" });
    texts.put(
        "no_to_delete",
        new String[] { "No tasks to delete.", "Keine Aufgaben zum Löschen." });
    texts.put(
        "enter_num_delete",
        new String[] {
            "Enter the number of the task to delete (or 0 to cancel): ",
            "Gib die Nummer der Aufgabe zum Löschen ein (oder 0 zum Abbrechen): ",
        });
    texts.put(
        "task_deleted",
        new String[] { "Task deleted: ", "Aufgabe gelöscht: " });
    texts.put(
        "deletion_cancel",
        new String[] { "Deletion cancelled.", "Löschen abgebrochen." });
    texts.put("goodbye", new String[] { "Goodbye!", "Auf Wiedersehen!" });
    texts.put(
        "invalid_choice",
        new String[] {
            "Invalid choice! Please choose a number between %d and %d.",
            "Ungültige Auswahl! Bitte wähle eine Zahl zwischen %d und %d.",
        });
    texts.put(
        "please_number",
        new String[] {
            "Please enter a valid number!",
            "Bitte gib eine gültige Zahl ein!",
        });
    texts.put(
        "settings_title",
        new String[] { "=== Settings ===", "=== Einstellungen ===" });
    texts.put("settings_lang", new String[] { "1 - Language", "1 - Sprache" });
    texts.put("settings_back", new String[] { "2 - Back", "2 - Zurück" });
    texts.put(
        "choose_lang",
        new String[] {
            "Choose a language: 1 - English, 2 - Deutsch (or 0 to cancel): ",
            "Wähle eine Sprache: 1 - English, 2 - Deutsch (oder 0 zum Abbrechen): ",
        });
    texts.put(
        "lang_set",
        new String[] {
            "Language set to English.",
            "Sprache auf Deutsch gesetzt.",
        });
    texts.put(
        "saving_error",
        new String[] {
            "An error occurred while saving your tasks: ",
            "Beim Speichern der Aufgaben ist ein Fehler aufgetreten: ",
        });
  }

  public static void setLanguage(Language lang) {
    language = lang;
  }

  public static Language getLanguage() {
    return language;
  }

  public static String t(String key) {
    String[] arr = texts.get(key);
    if (arr == null)
      return key;
    return arr[language == Language.EN ? 0 : 1];
  }

  public static void printHeader(String title) {
    clearScrean();
    String line = "══════════════════════════════════════════════";
    System.out.println(PASTEL_PURPLE + line + RESET);
    System.out.println(BOLD + PASTEL_PINK + "  " + title + RESET);
    System.out.println(PASTEL_PURPLE + line + RESET);
  }

  public static void clearScrean() {
    System.out.print("\033[H\033[2J");
    System.out.flush();
  }

  public static Language loadLanguageFromFile() {
    File f = new File(SETTINGS_FILE);
    if (!f.exists())
      return Language.EN;
    try (BufferedReader br = new BufferedReader(new FileReader(f))) {
      String line;
      while ((line = br.readLine()) != null) {
        line = line.trim();
        if (line.startsWith("lang=")) {
          String v = line.substring(5).trim().toUpperCase();
          if (v.equals("DE"))
            return Language.DE;
          return Language.EN;
        }
      }
    } catch (IOException ignored) {
    }
    return Language.EN;
  }

  public static void saveLanguageToFile(Language lang) {
    try (PrintWriter pw = new PrintWriter(new FileWriter(SETTINGS_FILE))) {
      pw.println("lang=" + (lang == Language.DE ? "DE" : "EN"));
    } catch (IOException e) {
      System.out.println(
          PASTEL_RED_URGENT + "Could not save settings: " + e.getMessage() + RESET);
    }
  }
}
