package org.example;

import org.example.logger.AsyncLogger;
import org.example.model.Worker;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.ObjectOutputStream;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.logging.Level;

public class Payload {

    private static final AsyncLogger logger = AsyncLogger.get("client");
    private static final int PAYLOAD_SIZE = 4000;
    private static final Map<String, Integer> commandBit;
    private final String command;
    private final String[] args;
    private byte[] payload;


    static {
        commandBit = new HashMap<>();
        commandBit.put("help", 10);
        commandBit.put("info", 20);
        commandBit.put("show", 30);
        commandBit.put("add", 11);
        commandBit.put("update_id", 21);
        commandBit.put("remove_by_id", 32);
        commandBit.put("clear", 40);
        commandBit.put("remove_first", 60);
        commandBit.put("add_if_max", 41);
        commandBit.put("add_if_min", 51);
        commandBit.put("average_of_salary", 70);
        commandBit.put("print_field_ascending_status", 80);
        commandBit.put("print_field_descending_organization", 90);
    }


    Payload(String command, String... args) {
        this.command = command;
        this.args = args;
        loadData();
    }

    private void loadData() {

        int bit = commandBit.getOrDefault(command, 0);
        payload = new byte[PAYLOAD_SIZE];
        payload[0] = (byte) bit;
        byte[] data = switch (bit) {
            // 11 - add, 21 - update_id , 41 - add_if_max , 51 - add_if_min
            case 11, 21, 41, 51 -> {
                int id = 0;
                if (bit == 21)
                    id = ConsoleHelper.getId();
                Worker worker = ConsoleHelper.getWorker();
                worker.setId(id);
                byte[] model = new byte[0]; 
                try (var bis = new ByteArrayOutputStream();
                     var ois = new ObjectOutputStream(bis);) {
                    ois.writeObject(worker);
                    model =  bis.toByteArray();
                    System.out.println(model.length);

                } catch (IOException e) {
                    logger.log(Level.INFO, "Ошибка при соединении");
                }
                yield model; 

            }
            // remove_by_id
            case 32 -> {
                // [com_bit , byte1, byte2, byte3, byte4]
                ByteBuffer buffer = ByteBuffer.allocate(4);
                buffer.putInt(ConsoleHelper.getId());
                yield buffer.array();
            }

            default -> 
                ByteBuffer.allocate(0).array();
        };
        System.arraycopy(data, 0, payload, 1, data.length);

    }

    public byte[] getData() {
        return payload;
    }

    public boolean isEmptyPayload() {
        return payload[0] == 0;
    }


}