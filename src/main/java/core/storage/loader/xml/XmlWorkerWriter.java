package core.storage.loader.xml;

import core.model.Worker;

import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import java.util.Collection;

public class XmlWorkerWriter {

    private XMLStreamWriter writer;
    private Collection<Worker> workers;

    public XmlWorkerWriter(XMLStreamWriter writer, Collection<Worker> workers) {
        this.writer = writer;
        this.workers = workers;
    }

    public void map() {
        try {
            writer.writeStartDocument("UTF-8", "1.0");
            writer.writeStartElement("workers");

            for (Worker worker : workers) {
                writer.writeStartElement("worker");
                writer.writeStartElement("name");
                writer.writeCharacters(worker.getName());
                writer.writeEndElement();
                writer.writeStartElement("coordinates");
                writer.writeStartElement("x");
                writer.writeCharacters(Long.toString(worker.getCoordinates()
                        .getX()));
                writer.writeEndElement();
                writer.writeStartElement("y");
                writer.writeCharacters(Double.toString(worker.getCoordinates()
                        .getY()));
                writer.writeEndElement();
                writer.writeEndElement();

                writer.writeStartElement("salary");
                writer.writeCharacters(Float.toString(worker.getSalary()));
                writer.writeEndElement();

                writer.writeStartElement("startDate");
                writer.writeCharacters(worker.getStartDate()
                        .toString());
                writer.writeEndElement();

                writer.writeStartElement("position");
                writer.writeCharacters(worker.getPosition()
                        .toString());
                writer.writeEndElement();

                writer.writeStartElement("status");
                writer.writeCharacters(worker.getStatus()
                        .toString());
                writer.writeEndElement();

                if (worker.getOrganization() != null) {
                    writer.writeStartElement("organization");
                    writer.writeStartElement("fullName");
                    writer.writeCharacters(worker.getOrganization()
                            .getFullName());
                    writer.writeEndElement();

                    if (worker.getOrganization()
                                .getType() != null) {
                        writer.writeStartElement("type");
                        writer.writeCharacters(worker.getOrganization()
                                .getType()
                                .toString());
                        writer.writeEndElement();
                    }

                    if (worker.getOrganization()
                                .getPostalAddress() != null) {
                        writer.writeStartElement("postalAddress");

                        writer.writeStartElement("zipCode");
                        writer.writeCharacters(worker.getOrganization()
                                .getPostalAddress()
                                .getZipCode());
                        writer.writeEndElement();

                        writer.writeStartElement("town");
                        writer.writeStartElement("x");
                        writer.writeCharacters(Double.toString(worker.getOrganization()
                                .getPostalAddress()
                                .getTown()
                                .getX()));
                        writer.writeEndElement();
                        writer.writeStartElement("y");
                        writer.writeCharacters(Integer.toString(worker.getOrganization()
                                .getPostalAddress()
                                .getTown()
                                .getY()));
                        writer.writeEndElement();
                        writer.writeStartElement("z");
                        writer.writeCharacters(Double.toString(worker.getOrganization()
                                .getPostalAddress()
                                .getTown()
                                .getZ()));
                        writer.writeEndElement();
                        writer.writeStartElement("name");
                        writer.writeCharacters(worker.getOrganization()
                                .getPostalAddress()
                                .getTown()
                                .getName());
                        writer.writeEndElement();
                        writer.writeEndElement();

                        writer.writeEndElement();
                    }

                    writer.writeEndElement();
                }

                writer.writeEndElement();
            }

            writer.writeEndElement(); // End
        } catch (XMLStreamException e) {
            throw new RuntimeException(e);
        }

    }
}
