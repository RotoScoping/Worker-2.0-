package org.example.client;

import org.example.client.ConsoleHelper;
import org.example.client.Crypto;
import org.example.gui.event.*;
import org.example.logger.AsyncLogger;
import org.example.model.Form;
import org.example.model.Worker;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;

public class Payload {

    private static final AsyncLogger logger = AsyncLogger.get("client");
    private static final int PAYLOAD_SIZE = 4000;
    private static final Map<EventType, Integer> commandBit;
    private final Event event;
    private byte[] payload;

    private static String login;


    static {
        commandBit = new HashMap<>();
        commandBit.put(EventType.HELP, 10);
        commandBit.put(EventType.REGISTER, 101);
        commandBit.put(EventType.LOGIN, 111);
        commandBit.put(EventType.INFO, 20);
        commandBit.put(EventType.SHOW, 30);
        commandBit.put(EventType.ADD, 11);
        commandBit.put(EventType.UPDATE_ID, 21);
        commandBit.put(EventType.REMOVE_BY_ID, 32);
        commandBit.put(EventType.CLEAR, 40);
        commandBit.put(EventType.REMOVE_FIRST, 60);
        commandBit.put(EventType.ADD_IF_MAX, 41);
        commandBit.put(EventType.ADD_IF_MIN, 51);
        commandBit.put(EventType.AVERAGE_OF_SALARY, 70);
        commandBit.put(EventType.PRINT_FIELD_ASCENDING_STATUS, 80);
        commandBit.put(EventType.PRINT_FIELD_DESCENDING_ORGANIZATION, 90);
    }


    Payload(UUID token , Event event) {
        this.event = event;
        loadData(token);
    }

    private void loadData(UUID token) {

        int bit = commandBit.getOrDefault(event.getType(), 0);
        byte[] tokenBytes = getTokenBytes(token);
        payload = new byte[PAYLOAD_SIZE];
        payload[0] = (byte) bit;
        System.arraycopy(tokenBytes, 0, payload, 1, tokenBytes.length);
        byte[] data = switch (event.getType()) {
            case ADD, UPDATE_ID, ADD_IF_MAX, ADD_IF_MIN -> {
                AddEvent addEvent = (AddEvent) event;

                byte[] model = new byte[0]; 
                try (var bis = new ByteArrayOutputStream();
                     var ois = new ObjectOutputStream(bis);) {
                    ois.writeObject(addEvent.getWorker());
                    model =  bis.toByteArray();
                    System.out.println(model.length);

                } catch (IOException e) {
                    logger.log(Level.INFO, "Ошибка при соединении");
                }
                yield model; 

            }
            // remove_by_id
            case REMOVE_BY_ID -> {
                RemoveByIdEvent removeEvent = (RemoveByIdEvent) event;
                ByteBuffer buffer = ByteBuffer.allocate(4);
                buffer.putInt(removeEvent.getId());
                yield buffer.array();
            }

            case REGISTER, LOGIN-> {
                AuthEvent authEvent = (AuthEvent) event;
                login = authEvent.getLogin();
                byte[] model = new byte[0];
                try (var bis = new ByteArrayOutputStream();
                     var ois = new ObjectOutputStream(bis);) {
                    ois.writeObject(Crypto.getEncryptedForm(authEvent.getForm()));
                    model =  bis.toByteArray();
                } catch (IOException e) {
                    logger.log(Level.INFO, "Ошибка при соединении");
                }
                yield model;
            }

            default -> 
                ByteBuffer.allocate(0).array();
        };
        System.arraycopy(data, 0, payload, 37, data.length);
    }

    private byte[] getTokenBytes(UUID token) {
        return token == null ? new byte[36] : token.toString().getBytes();
    }

    public byte[] getData() {
        return payload;
    }

    public boolean isEmptyPayload() {
        return payload[0] == 0;
    }

    public static String getLogin() {
        return login;
    }


}
