package core.storage.loader.xml;

import core.model.*;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

/**
 * Класс обработчик для маппинга xml -> List (Worker)
 */
public class WorkerHandler extends DefaultHandler {
    private List<Worker> workers = new ArrayList<>();
    private Worker.Builder currentWorker;
    private Coordinates.Builder currentCoordinates;
    private Organization.Builder currentOrganization;
    private Address.Builder currentAddress;
    private Location.Builder currentLocation;
    private StringBuilder elementValue = new StringBuilder();
    private SimpleDateFormat dateFormatter = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss'Z'", Locale.ENGLISH);

    /**
     * Gets workers.
     *
     * @return the workers
     */
    public List<Worker> getWorkers() {
        return workers;
    }

    @Override
    public void startElement(String uri, String localName, String qName, Attributes attributes) {
        switch (qName) {
            case "worker" -> currentWorker = Worker.builder();
            case "coordinates" -> currentCoordinates = Coordinates.builder();
            case "organization" -> currentOrganization = Organization.builder();
            case "postalAddress" -> currentAddress = Address.builder();
            case "town" -> currentLocation = Location.builder();
        }
        elementValue.setLength(0);
    }

    @Override
    public void characters(char[] ch, int start, int length) {
        elementValue.append(ch, start, length);
    }

    @Override
    public void endElement(String uri, String localName, String qName) throws SAXException {
        switch (qName) {
            case "worker" -> workers.add(currentWorker.build());
            case "name" -> {
               if (currentLocation != null) {
                    currentLocation.name(elementValue.toString());
               } else {
                    currentWorker.name(elementValue.toString());
               }

            }
            case "x" -> {
                if (currentCoordinates != null) {
                    currentCoordinates.x(Long.parseLong(elementValue.toString()));
                } else if (currentLocation != null) {
                    currentLocation.x(Double.parseDouble(elementValue.toString()));
                }
            }
            case "y" -> {
                if (currentCoordinates != null) {
                    currentCoordinates.y(Double.parseDouble(elementValue.toString()));
                } else if (currentLocation != null) {
                    currentLocation.y(Integer.parseInt(elementValue.toString()));
                }
            }
            case "z" -> {
                if (currentLocation != null) {
                    currentLocation.z(Double.parseDouble(elementValue.toString()));
                }
            }
            case "salary" -> currentWorker.salary(Float.parseFloat(elementValue.toString()));
            case "startDate" -> {
                try {
                    currentWorker.startDate(dateFormatter.parse(elementValue.toString()));
                } catch (Exception e) {
                    throw new SAXException("Error parsing date", e);
                }
            }
            case "position" -> currentWorker.position(Position.valueOf(elementValue.toString()));
            case "status" -> currentWorker.status(Status.valueOf(elementValue.toString()));
            case "fullName" -> currentOrganization.fullName(elementValue.toString());
            case "type" -> currentOrganization.type(OrganizationType.valueOf(elementValue.toString()));
            case "zipCode" -> currentAddress.zipCode(elementValue.toString());
            case "town" -> {
                currentAddress.town(currentLocation.build());
                currentLocation = null;
            }
            case "coordinates" -> {
                currentWorker.coordinates(currentCoordinates.build());
                currentCoordinates = null;
            }
            case "organization" -> {
                currentWorker.organization(currentOrganization.build());
                currentOrganization = null;
            }

            case "postalAddress" -> {
                currentOrganization.postalAddress(currentAddress.build());
                currentAddress = null;
            }

        }
    }
}
