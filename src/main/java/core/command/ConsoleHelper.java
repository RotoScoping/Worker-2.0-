package core.command;

import core.model.*;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Date;
import java.util.Scanner;
import java.util.stream.Stream;

import static java.time.LocalDate.parse;
import static java.time.format.DateTimeFormatter.ofPattern;

/**
 * Класс, содержащий полезные функции интерактива с клиентом и считывания данных с консоли
 */
public final class ConsoleHelper {
    public static final String DATE_FORMAT = "dd.MM.yyyy";

    private ConsoleHelper() {
    }

    /**
     * Метод который позволяет создать Worker в интерактивном консольном режиме
     * @return worker
     */
    public static Worker getWorker() {
        Scanner sc = new Scanner(System.in);
        Worker.Builder worker = Worker.builder();
        // имя воркера
        print("Введите имя воркера: ");
        worker.name(sc.nextLine()
                .trim());
        // зарплата воркера
        print("Введите зарплату воркера: ");
        while (!sc.hasNextFloat()) {
            print("Введите число с точкой: ");
            sc.next();
        }
        worker.salary(sc.nextFloat());
        sc.nextLine();
        // startDate - начало работы воркера
        System.out.printf("Введите когда воркер начал работу %s : ", DATE_FORMAT);
        String dateString = sc.nextLine()
                .trim();
        while (!validateDate(dateString)) {
            print("\nНекорректная дата, попробуйте еще раз: ");
            dateString = sc.nextLine();
        }

        Instant instant = parse(dateString, ofPattern(DATE_FORMAT)).atStartOfDay()
                .atZone(ZoneId.systemDefault())
                .toInstant();

        worker.startDate(Date.from(instant));

        // локация воркера
        worker.coordinates(getCoordinates(sc));

        // вакансия воркера
        System.out.printf("Введите на какой вакансии находится воркер (%s): ", String.join(", ", Stream.of(Position.values())
                .map(Enum::name)
                .toArray(String[]::new)));
        String position = sc.nextLine()
                .toUpperCase();
        while (validatePosition(position) == null) {
            print("\n Введите вакансию из списка: ");
            position = sc.nextLine()
                    .toUpperCase();
        }
        worker.position(Position.valueOf(position));

        // статус воркера

        System.out.printf("Введите статус воркера (%s): ", String.join(", ", Stream.of(Status.values())
                .map(Enum::name)
                .toArray(String[]::new)));
        String status = sc.nextLine()
                .toUpperCase();
        while (validateStatus(status) == null) {
            print("\n Введите статус из списка: ");
            status = sc.nextLine()
                    .toUpperCase();
        }
        worker.status(Status.valueOf(status));

        // Организация воркера
        worker.organization(getOrganization(sc));

        return worker.build();

    }
    /**
     * Метод который позволяет создать Coordinates в интерактивном консольном режиме
     * @return coordinates
     */
    public static Coordinates getCoordinates(Scanner sc) {
        print("Введите местоположение воркера:\n ");
        Coordinates.Builder coordinates = Coordinates.builder();
        print("Координата x: ");
        // x координата локации воркера
        while (!sc.hasNextLong()) {
            print("Введите целочисленный x: ");
            sc.next();
        }
        coordinates.x(sc.nextLong());
        print("Координата y: ");
        // y координата локации воркера
        while (!sc.hasNextDouble()) {
            print("Введите вещественное число с точкой: ");
            sc.next();
        }
        return coordinates.y(sc.nextDouble()).build();

    }
    /**
     * Метод который позволяет создать Organization в интерактивном консольном режиме
     * @return Organization
     */
    public static Organization getOrganization(Scanner sc) {
        Organization.Builder organization = Organization.builder();
        // имя организации
        print("Введите имя организации воркера: ");
        organization.fullName(sc.nextLine()
                .trim());
        // тип организации
        System.out.printf("Введите тип организации (%s)", String.join(", ", Stream.of(OrganizationType.values())
                .map(Enum::name)
                .toArray(String[]::new)));
        String type = sc.nextLine()
                .toUpperCase();
        while (validateOrganizationType(type) == null) {
            type = sc.nextLine()
                    .toUpperCase();
        }
        // адрес организации
        return organization.type(OrganizationType.valueOf(type))
                .postalAddress(getAddress(sc))
                .build();


    }
    /**
     * Метод который позволяет создать Address в интерактивном консольном режиме
     * @return Address
     */
    public static Address getAddress(Scanner sc) {
        Address.Builder address = Address.builder();
        // почтовый индекс
        print("Введите почтовый индекс организации: ");
        return address.zipCode(sc.nextLine()
                        .trim())
                .town(getLocation(sc))
                .build();

    }
    /**
     * Метод который позволяет создать Location в интерактивном консольном режиме
     * @return Location
     */
    public static Location getLocation(Scanner sc) {
        Location.Builder location = Location.builder();
        // x координата локации организации
        print("Введите координату x организации: ");
        while (!sc.hasNextDouble()) {
            print("Введите вещественное число с точкой: ");
            sc.next();
        }
        location.x(sc.nextDouble());

        print("Введите координату y организации: ");
        // y координата локации организации
        while (!sc.hasNextInt()) {
            print("Введите целое число: ");
            sc.next();
        }
        location.y(sc.nextInt());

        print("Введите координату z организации: ");
        while (!sc.hasNextDouble()) {
            print("Введите вещественное число с точкой: ");
            sc.next();
        }
        location.z(sc.nextDouble());
        sc.nextLine();
        print("Введите название локации: ");
        location.name(sc.nextLine()
                .trim());
        return location.build();

    }
    /**
     * Метод который который пытается превратить строку в Position
     * @return Position
     */
    public static Position validatePosition(String position) {
        try {
            return Position.valueOf(position);
        } catch (IllegalArgumentException e) {
            return null;
        }

    }
    /**
     * Метод который который пытается превратить строку в Status
     * @return Status
     */
    private static Status validateStatus(String status) {
        try {
            return Status.valueOf(status);
        } catch (IllegalArgumentException e) {
            return null;
        }

    }
    /**
     * Метод который который пытается превратить строку в OrganizationType
     * @return OrganizationType
     */
    private static OrganizationType validateOrganizationType(String type) {
        try {
            return OrganizationType.valueOf(type);
        } catch (IllegalArgumentException e) {
            return null;
        }

    }


    public static void print(String text) {
        System.out.print(text);
    }
    /**
     * Метод который валидирует дату по шаблону
     * @return boolean
     */
    private static boolean validateDate(String date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat(DATE_FORMAT);
        dateFormat.setLenient(false);
        try {
            dateFormat.parse(date);
            return true;
        } catch (ParseException e) {
            return false;
        }
    }
    /**
     * Метод который проверяет, что строка корректно переводится в цел.число
     * @return boolean
     */
    public static boolean isNumeric(String str) {
        try {
            Integer.parseInt(str);
            return true;
        } catch(NumberFormatException e) {
            return false;
        }
    }

}
