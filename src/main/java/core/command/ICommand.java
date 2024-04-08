package core.command;

/**
 * Интерфейс команды, содержащий метод выполнения команды -execute()
 */
public interface ICommand {
    /**
     * Execute string.
     *
     * @param args the args
     * @return the string
     */
    String execute(String... args);
}
