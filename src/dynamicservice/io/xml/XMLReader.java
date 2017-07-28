/*
 */
package dynamicservice.io.xml;

import java.io.IOException;
import java.io.StringReader;
import java.io.StringWriter;
import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

/**
 *
 * @author migo
 */
public class XMLReader {

    /**
     *
     * @param <T>
     * @param model
     * @param xml
     * @return
     */
    @SuppressWarnings("unchecked")
    public static <T> T getModelFromXML(Class<T> model, String xml) {
        try {
            T instance = model.newInstance();
            List children = new LinkedList();
            XMLValidator xmlv = new XMLValidator(xml);

            if (!xmlv.isVerified()) {
                return null;
            }

            DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = factory.newDocumentBuilder();
            InputSource inputSource = new InputSource(new StringReader(xmlv.getXML()));
            Document document = builder.parse(inputSource);
            document.getDocumentElement().normalize();

            NodeList nodeList = document.getDocumentElement().getChildNodes();
            NamedNodeMap attributes = document.getDocumentElement().getAttributes();

            for (int ai = 0; ai < attributes.getLength(); ai++) {
                String fieldName = getFieldName(attributes.item(ai).getNodeName());
                String fieldValue = attributes.item(ai).getNodeValue();
                Field field = getField(model, fieldName);

                if (field != null) {
                    field.setAccessible(true);
                    field.set(instance, fieldValue);
                }

            }

            Field field = getField(model, "_nodeXml", true);
            if (field != null) {
                field.setAccessible(true);
                field.set(instance, xml);
            }

            for (int index = 0; index < nodeList.getLength(); index++) {
                Node node = nodeList.item(index);
                if (node.getNodeName().equals("#text")) {
                    continue;
                }
                field = getField(model, getFieldName(node.getNodeName()));
                if (field != null) {
                    field.setAccessible(true);
                    Class<?> fieldType = field.getType();

                    if (fieldType.getPackage().getName().equals(model.getPackage().getName())) {
                        try {
                            Object childInstance = getClassFromNode(model.getPackage().getName(), node.getNodeName()).newInstance();
                            childInstance = getModelFromXML(childInstance.getClass(), nodeToXML(node));
                            field.set(instance, childInstance);
                        } catch (ClassNotFoundException ex) {
                            Logger.getLogger(XMLReader.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    } else if (node.hasChildNodes() && fieldType.getName().equals("java.util.List")) {
                        try {
                            Object childInstance = getClassFromNode(model.getPackage().getName(), node.getNodeName()).newInstance();
                            childInstance = getModelFromXML(childInstance.getClass(), nodeToXML(node));
                            children.add(childInstance);
                            field.set(instance, children);
                        } catch (ClassNotFoundException ex) {
                            Logger.getLogger(XMLReader.class.getName()).log(Level.SEVERE, null, ex);
                        }
                    } else {
                        String value;
                        if (node.hasChildNodes()) {
                            value = node.getChildNodes().item(0).getNodeValue();
                        } else {
                            value = node.getNodeValue();
                        }
                        field.set(instance, value);
                    }
                }
            }
            return instance;
        } catch (ParserConfigurationException | IOException | SAXException | InstantiationException | IllegalAccessException exception) {
            exception.printStackTrace();

            return null;
        }
    }

    /**
     *
     * @param nodeName
     * @return
     * @throws ClassNotFoundException
     */
    private static Class getClassFromNode(String packageName, String nodeName) throws ClassNotFoundException {
        String className = nodeName;
        if (className.contains("_")) {
            className = toCamelCase(className, "_");
        } else if (className.contains("-")) {
            className = toCamelCase(className, "-");
        } else {
            className = toProperCase(className);
        }
        return Class.forName(packageName + "." + className);
    }

    /**
     *
     * @param s
     * @param delimitor
     * @return
     */
    private static String toCamelCase(String s, String delimitor) {
        String[] parts = s.split(delimitor);
        String camelCaseString = "";
        for (String part : parts) {
            camelCaseString = camelCaseString + toProperCase(part);
        }
        return camelCaseString;
    }

    /**
     *
     * @param s
     * @return
     */
    private static String toProperCase(String s) {
        return s.substring(0, 1).toUpperCase() + s.substring(1).toLowerCase();
    }

    /**
     *
     * @param obj
     * @param name
     * @return
     */
    private static Field getField(Class obj, String name) {
        return getField(obj, name, false);
    }

    /**
     *
     * @param obj
     * @param name
     * @return
     */
    private static Field getField(Class obj, String name, boolean checkSuper) {
        try {
            for (Field field : obj.getDeclaredFields()) {
                if (field.getName().equals(name)) {
                    return field;
                }
            }
            if (checkSuper) {
                for (Field field : obj.getSuperclass().getDeclaredFields()) {
                    if (field.getName().equals(name)) {
                        return field;
                    }
                }
            }
        } catch (SecurityException ex) {
        }
        return null;
    }

    /**
     *
     * @param node
     * @return
     */
    private static String nodeToXML(Node node) {
        StringWriter sw = new StringWriter();
        try {
            Transformer t = TransformerFactory.newInstance().newTransformer();
            //t.setOutputProperty(OutputKeys.OMIT_XML_DECLARATION, "yes");
            t.transform(new DOMSource(node), new StreamResult(sw));
        } catch (TransformerException te) {
            System.out.println("nodeToString Transformer Exception");
            te.printStackTrace();
        }
        return sw.toString();
    }

    /**
     *
     * @param name
     * @return
     */
    private static String getFieldName(String name) {
        String tmpName;

        if (name.contains("_")) {
            tmpName = toCamelCase(name, "_");
        } else if (name.contains("-")) {
            tmpName = toCamelCase(name, "-");
        } else {
            tmpName = name;
        }

        return tmpName.substring(0, 1).toLowerCase() + tmpName.substring(1);
    }

    public static Map<String, String> readSet(Node dataNode, String... paths) {
        //System.out.println("paths  : " + Arrays.toString(paths));
        Map<String, String> dataItems = new HashMap<>();
        int pathsFound = 0;
        if (dataNode.getChildNodes().getLength() > 0) {
            for (int index = 0; index < paths.length; index++) {
                String currentPath = paths[index];
                for (int childIndex = 1; childIndex < dataNode.getChildNodes().getLength(); childIndex++) {
                    if (pathsFound < paths.length && dataNode.getChildNodes().item(childIndex).getNodeName().equals(currentPath)) {
                        pathsFound++;
          //              System.out.println(pathsFound + " : path : " + currentPath + " " + dataNode.getChildNodes().item(childIndex).getNodeName());
                        dataNode = dataNode.getChildNodes().item(childIndex);
                        if (pathsFound == paths.length) {
                            return readSet(dataNode);
                        }
                        break;
                    }
                }
            }
        }
        return dataItems;
    }

    public static Map<String, String> readSet(Node dataNode) {
        Map<String, String> dataItems = new HashMap<>();
        if (dataNode.getChildNodes().getLength() > 0) {
            for (int childIndex = 1; childIndex < dataNode.getChildNodes().getLength(); childIndex++) {
                dataItems.put(dataNode.getChildNodes().item(childIndex).getNodeName(), dataNode.getChildNodes().item(childIndex).getTextContent());
            }
        }
        return dataItems;
    }

    /**
     *
     */
    public XMLReader() {
    }
}
