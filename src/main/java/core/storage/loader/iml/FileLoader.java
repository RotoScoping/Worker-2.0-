package core.storage.loader.iml;

import core.model.Worker;
import core.storage.loader.ILoader;
import core.storage.loader.xml.WorkerHandler;
import core.storage.loader.xml.XmlWorkerWriter;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import javax.xml.XMLConstants;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;
import java.io.*;
import java.util.Collection;
import java.util.List;
import java.util.PriorityQueue;
import java.util.Queue;

/**
 * Implementation @see core.storage.loader.ILoader .
 * Класс, реализующий загрузка данных о Worker из xml файла и обратно в файл.
 */
public class FileLoader implements ILoader<Collection<Worker>> {
    private static final String XML_FILE_PATH = System.getProperty("file_path");

    public static final String XSD_SCHEMA_PATH = "worker.xsd";

    /**
     * Запись данных в файл с использованием java.io.BufferedOutputStream и JAXP (Java API for XML Processing)
     * @return boolean
     */

    @Override
    public boolean save(Collection<Worker> workers) {
        XMLOutputFactory factory = XMLOutputFactory.newInstance();
        try (var out = new BufferedOutputStream(new FileOutputStream(new File("dump.xml")))) {
            XMLStreamWriter writer = factory.createXMLStreamWriter(out, "UTF-8");
            XmlWorkerWriter mapper = new XmlWorkerWriter(writer, workers);
            mapper.map();
        } catch (XMLStreamException | IOException e) {
            throw new RuntimeException(e);
        }
        return true;
    }


    /**
     * Метод, выполнящий загрузку данных из xml используя SAX Parser
     * @return the instance
     */
    @Override
    public Queue<Worker> load() {

        SAXParserFactory factory = SAXParserFactory.newInstance();
        WorkerHandler mapper = new WorkerHandler();

        try (var isr = new InputStreamReader(FileLoader.class.getClassLoader().getResourceAsStream(XML_FILE_PATH))){
            SchemaFactory schemaFactory = SchemaFactory.newInstance(XMLConstants.W3C_XML_SCHEMA_NS_URI);
            Schema schema = schemaFactory.newSchema(FileLoader.class.getClassLoader().getResource(XSD_SCHEMA_PATH));
            factory.setSchema(schema);
            factory.setValidating(false);
            SAXParser saxParser = factory.newSAXParser();
            saxParser.parse(new InputSource(isr), mapper);
        } catch (ParserConfigurationException | IOException | SAXException e) {
            System.err.println("Произошла ошибка при чтении файла!");
        }

        List<Worker> workers = mapper.getWorkers();
        return new PriorityQueue<>(workers);
    }

}

