package com.mq.demo.avro;

import org.apache.avro.Protocol;
import org.apache.avro.Schema;
import org.apache.avro.file.DataFileReader;
import org.apache.avro.file.DataFileWriter;
import org.apache.avro.generic.GenericArray;
import org.apache.avro.generic.GenericData;
import org.apache.avro.generic.GenericDatumReader;
import org.apache.avro.generic.GenericRecord;
import org.apache.avro.io.DatumReader;
import org.apache.avro.io.DatumWriter;
import org.apache.avro.specific.SpecificDatumWriter;
import org.apache.avro.util.Utf8;
import org.apache.commons.io.FileUtils;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;
import org.w3c.dom.*;
import org.xml.sax.SAXException;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.*;
import java.util.ArrayList;
import java.util.List;

@Component
public class AvroTransformer {

    private static final String xml = "<?xml version='1.0' standalone='yes'?>\n" +
            "<movies>\n" +
            " <movie>\n" +
            "  <title>PHP: Behind the Parser</title>\n" +
            "  <characters>\n" +
            "   <character>\n" +
            "    <name>Ms. Coder</name>\n" +
            "    <actor>Onlivia Actora</actor>\n" +
            "   </character>\n" +
            "   <character>\n" +
            "    <name>Mr. Coder</name>\n" +
            "    <actor>El Act&#211;r</actor>\n" +
            "   </character>\n" +
            "  </characters>\n" +
            "  <plot>\n" +
            "   So, this language. It's like, a programming language. Or is it a\n" +
            "   scripting language? All is revealed in this thrilling horror spoof\n" +
            "   of a documentary.\n" +
            "  </plot>\n" +
            "  <great-lines>\n" +
            "   <line>PHP solves all my web problems</line>\n" +
            "  </great-lines>\n" +
            "  <rating type=\"thumbs\">7</rating>\n" +
            "  <rating type=\"stars\">5</rating>\n" +
            " </movie>\n" +
            "</movies>";

    private static Protocol protocol;

    static {
        try {
            InputStream stream = AvroTransformer.class.getClassLoader().getResourceAsStream("xml.avsc");
            if (stream == null) throw new IllegalStateException("xml.avsc not in classpath, please check...");

            protocol = Protocol.parse(stream);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public static void main(String[] args) throws Exception{
        System.out.println("Enter AvroTransformer.main()...");
        AvroTransformer at = new AvroTransformer();
        //System.out.println(at.xmlToByteArray(xml));
        at.avroFileToXmlFile(new File("e:/xml.avro"), new File("e:/xmlfromavro.xml"));

    }

    public void xmlFileToAvroFile(File xmlFile, File avroFile) throws IOException, SAXException {
        Schema schema = protocol.getType("Element");

        Document doc = parse(xmlFile);
        DatumWriter<GenericRecord> datumWriter = new SpecificDatumWriter<>(schema);

        try (DataFileWriter<GenericRecord> fileWriter = new DataFileWriter<>(datumWriter)) {
            fileWriter.create(schema, avroFile);
            fileWriter.append(wrapElement(doc.getDocumentElement()));
        }
    }

    //has problem send GenericRecord directly to kafka
    public GenericRecord xmlToGenericRecord(String xmlContent) throws IOException, SAXException {
        Schema schema = protocol.getType("Element");

        Document doc = parse(xmlContent);
        DatumWriter<GenericRecord> datumWriter = new SpecificDatumWriter<>(schema);

//        try (DataFileWriter<GenericRecord> fileWriter = new DataFileWriter<>(datumWriter)) {
//            fileWriter.create(schema, avroFile);
//            fileWriter.append(wrapElement(doc.getDocumentElement()));
//
//        }

        return wrapElement(doc.getDocumentElement());
    }

    public byte[] xmlToByteArray(String xmlContent) throws IOException, SAXException {
        String tempFile = "e:/xml.avro";
        Schema schema = protocol.getType("Element");

        Document doc = parse(xmlContent);
        DatumWriter<GenericRecord> datumWriter = new SpecificDatumWriter<>(schema);

        try (DataFileWriter<GenericRecord> fileWriter = new DataFileWriter<>(datumWriter)) {
            fileWriter.create(schema, new File(tempFile));
            fileWriter.append(wrapElement(doc.getDocumentElement()));
            fileWriter.flush();
        }

        return FileUtils.readFileToByteArray(new File(tempFile));
    }

    private Document parse(String xmlContent) throws IOException, SAXException {
        try {
            InputStream is = new ByteArrayInputStream(xmlContent.getBytes());
            DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            return builder.parse(is);
        } catch (ParserConfigurationException e) {
            throw new RuntimeException(e);
        }
    }

    private Document parse(File file) throws IOException, SAXException {
        try {
            DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            return builder.parse(file);
        } catch (ParserConfigurationException e) {
            throw new RuntimeException(e);
        }
    }

    private GenericData.Record wrapElement(Element el) {
        GenericData.Record record = new GenericData.Record(protocol.getType("Element"));
        record.put("name", el.getNodeName());

        NamedNodeMap attributeNodes = el.getAttributes();
        List<GenericData.Record> attrRecords = new ArrayList<>();
        for (int i = 0; i < attributeNodes.getLength(); i++) {
            Attr attr = (Attr) attributeNodes.item(i);
            attrRecords.add(wrapAttr(attr));
        }
        record.put("attributes", attrRecords);

        List<Object> childArray = new ArrayList<>();
        NodeList childNodes = el.getChildNodes();
        for (int i = 0; i < childNodes.getLength(); i++) {
            Node node = childNodes.item(i);
            if (node.getNodeType() == Node.ELEMENT_NODE)
                childArray.add(wrapElement((Element) node));

            if (node.getNodeType() == Node.TEXT_NODE)
                childArray.add(node.getTextContent());
        }
        record.put("children", childArray);

        return record;
    }

    private GenericData.Record wrapAttr(Attr attr) {
        GenericData.Record record = new GenericData.Record(protocol.getType("Attribute"));

        record.put("name", attr.getName());
        record.put("value", attr.getValue());

        return record;
    }

    public void avroFileToXmlFile(File avroFile, File xmlFile) throws IOException {
        DatumReader<GenericRecord> datumReader = new GenericDatumReader<>(protocol.getType("Element"));
        DataFileReader<GenericRecord> dataFileReader = new DataFileReader<>(avroFile, datumReader);

        GenericRecord record = dataFileReader.next();

        Document doc;
        try {
            doc = DocumentBuilderFactory.newInstance().newDocumentBuilder().newDocument();
        } catch (ParserConfigurationException e) {
            throw new RuntimeException(e);
        }

        Element el = unwrapElement(record, doc);
        doc.appendChild(el);

        saveDocument(doc, xmlFile);
    }

    private Element unwrapElement(GenericRecord record, Document doc) {
        String name = "" + record.get("name");
        Element el = doc.createElement(name);

        @SuppressWarnings("unchecked")
        GenericArray<GenericRecord> attrArray = (GenericArray<GenericRecord>) record.get("attributes");
        for (GenericRecord attrRecord : attrArray)
            el.setAttributeNode(unwrapAttr(attrRecord, doc));

        @SuppressWarnings("unchecked")
        GenericArray<Object> childArray = (GenericArray<Object>) record.get("children");
        for (Object childObj : childArray) {
            if (childObj instanceof GenericRecord)
                el.appendChild(unwrapElement((GenericRecord) childObj, doc));

            if (childObj instanceof Utf8)
                el.appendChild(doc.createTextNode("" + childObj));
        }

        return el;
    }

    private Attr unwrapAttr(GenericRecord record, Document doc) {
        Attr attr = doc.createAttribute("" + record.get("name"));
        attr.setValue("" + record.get("value"));
        return attr;
    }

    private void saveDocument(Document doc, File file) {
        try {
            Transformer transformer = TransformerFactory.newInstance().newTransformer();
            transformer.transform(new DOMSource(doc), new StreamResult(file));
        } catch (TransformerException e) {
            throw new RuntimeException(e);
        }
    }

}
