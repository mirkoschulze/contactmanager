package de.mcdb.contactmanagerdesktop;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamException;
import javax.xml.stream.XMLStreamWriter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Class to write a valid persistence.xml for JDBC/Hibernate usage.
 * <p>
 * Expects user name and password to enable access to the MySQL server.
 *
 * @author Mirko
 */
public class PersistenceWriter {

    private static final Logger L = LoggerFactory.getLogger(PersistenceWriter.class);
    private final String FILE_NAME_WITH_PATH = ".\\src\\main\\resources\\META-INF\\persistence.xml";

    /**
     * Writes a persistence.xml with the submitted values for
     *javax.persistence.jdbc.user and javax.persistence.jdbc.password to the
     * following location:
     * projectFolder/src/main/resources/META-INF/persistence.xml.
     *
     * @param user value for javax.persistence.jdbc.user parameter
     * @param password value for javax.persistence.jdbc.password parameter
     */
    public void writePersistenceXML(String user, String password) {
        XMLOutputFactory factory = XMLOutputFactory.newInstance();
        try {
            XMLStreamWriter writer = factory.createXMLStreamWriter(new OutputStreamWriter(new FileOutputStream(new File(FILE_NAME_WITH_PATH)), "UTF-8"));

            writer.writeStartDocument("UTF-8", "1.0");

            writer.writeStartElement("persistence");
            writer.writeAttribute("version", "2.1");
            writer.writeAttribute("xmlns", "http://xmlns.jcp.org/xml/ns/persistence");
            writer.writeAttribute("xmlns:xsi", "http://www.w3.org/2001/XMLSchema-instance");
            writer.writeAttribute("xsi:schemaLocation", "http://xmlns.jcp.org/xml/ns/persistence http://xmlns.jcp.org/xml/ns/persistence/persistence_2_1.xsd");

            writer.writeStartElement("persistence-unit");
            writer.writeAttribute("name", "ContactManagerDesktopPU");

            writer.writeStartElement("provider");
            writer.writeCharacters("org.hibernate.ejb.HibernatePersistence");
            writer.writeEndElement();

            writer.writeStartElement("class");
            writer.writeCharacters("de.mcdb.contactmanagerapi.datamodel.Company");
            writer.writeEndElement();

            writer.writeStartElement("class");
            writer.writeCharacters("de.mcdb.contactmanagerapi.datamodel.Division");
            writer.writeEndElement();

            writer.writeStartElement("class");
            writer.writeCharacters("de.mcdb.contactmanagerapi.datamodel.Staffer");
            writer.writeEndElement();

            writer.writeStartElement("properties");

            writer.writeStartElement("property");
            writer.writeAttribute("name", "javax.persistence.jdbc.driver");
            writer.writeAttribute("value", "com.mysql.cj.jdbc.Driver");
            writer.writeEndElement();

            writer.writeStartElement("property");
            writer.writeAttribute("name", "javax.persistence.jdbc.url");
            writer.writeAttribute("value", "jdbc:mysql://localhost:3306/contact_db?serverTimezone=UTC");
            writer.writeEndElement();

            writer.writeStartElement("property");
            writer.writeAttribute("name", "javax.persistence.jdbc.user");
            writer.writeAttribute("value", user);
            writer.writeEndElement();

            writer.writeStartElement("property");
            writer.writeAttribute("name", "javax.persistence.jdbc.password");
            writer.writeAttribute("value", password);
            writer.writeEndElement();

            writer.writeStartElement("property");
            writer.writeAttribute("name", "hibernate.dialect");
            writer.writeAttribute("value", "org.hibernate.dialect.MySQL8Dialect");
            writer.writeEndElement();

            writer.writeStartElement("property");
            writer.writeAttribute("name", "hibernate.current_session_context_class");
            writer.writeAttribute("value", "thread");
            writer.writeEndElement();

            writer.writeStartElement("property");
            writer.writeAttribute("name", "hibernate.hbm2ddl.auto");
            writer.writeAttribute("value", "update");
            writer.writeEndElement();

            writer.writeStartElement("property");
            writer.writeAttribute("name", "hibernate.show_sql");
            writer.writeAttribute("value", "false");
            writer.writeEndElement();

            writer.writeStartElement("property");
            writer.writeAttribute("name", "hibernate.format_sql");
            writer.writeAttribute("value", "false");
            writer.writeEndElement();

            writer.writeEndElement();
            writer.writeEndElement();
            writer.writeEndElement();

            writer.writeEndDocument();

            writer.flush();
            writer.close();
        } catch (XMLStreamException | IOException e) {
            L.info("Exception in [{}] : {}", PersistenceWriter.class.getSimpleName(), e);
        }
    }

}
