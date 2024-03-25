package core.command;

/**
 * Интерфейс команды, содержащий метод выполнения команды -execute()
 */

public interface ICommand {
        String execute(String... args);
}
