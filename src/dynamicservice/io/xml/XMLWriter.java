/*
 */
package dynamicservice.io.xml;

import java.lang.reflect.Field;
import java.util.Map;

/**
 *
 * @author migo
 */
public class XMLWriter {

    /**
     * 
     * @param obj
     * @param name
     * @return 
     */
    @SuppressWarnings("unchecked")
    public static String ModelToXML(Object obj, String name) {
        StringBuilder builder = new StringBuilder();
        Class _class = obj.getClass();
        Map<String, String> _attributes;
        Field field = getField(_class, "_attributes");
        builder.append("<").append(name).append(" ");

        try {
            if (field != null) {
                _attributes = (Map<String, String>) field.get(obj);
                for (String key : _attributes.keySet()) {
                    builder.append(key).append("=\"").append(_attributes.get(key)).append("\" ");
                }
            }
            builder.append(">\n");

            Field[] fields = _class.getSuperclass().getDeclaredFields();
            writeValues(fields, obj, builder);

            fields = _class.getDeclaredFields();
            writeValues(fields, obj, builder);

            builder.append("</").append(name).append(">");

        } catch (Exception e) { e.printStackTrace();}

        return builder.toString();
    }

    /**
     * 
     * @param fields
     * @param obj
     * @param builder
     * @throws Exception 
     */
    @SuppressWarnings("unchecked")
    private static void writeValues(Field[] fields, Object obj, StringBuilder builder) throws Exception {
        if (fields.length == 0) {
            return;
        }
        for (Field f : fields) {
            if (f.getName().equals("_attributes") || f.getName().equals("_nodeXml")) {
                continue;
            }
            f.setAccessible(true);
            if (f.getType().getName().equals("java.lang.String")) {
                String value = (String) f.get(obj);
                builder.append("  <").append(f.getName());
                if (value == null) {
                    builder.append(" />\n");
                } else {
                    builder.append(">").append(value).append("</").append(f.getName()).append(">\n");
                }
            } else if (f.getType().getName().equals("java.util.Map")) {
                Map<String, String> values = (Map<String, String>) f.get(obj);
                builder.append("  <").append(f.getName()).append(">\n");
                for (String key : values.keySet()) {
                    String value = values.get(key);
                    builder.append("    <").append(key);
                    if (value == null) {
                        builder.append(" />\n");
                    } else {
                        builder.append(">").append(value).append("</").append(key).append(">\n");
                    }
                }
                builder.append("  </").append(f.getName()).append(">\n");
            }
        }

    }

    /**
     *
     * @param _class
     * @param name
     * @return
     */
    private static Field getField(Class _class, String name) {
        Field[] fields = _class.getDeclaredFields();
        for (Field f : fields) {
            if (f.getName().equals(name)) {
                f.setAccessible(true);
                return f;
            }
        }
        fields = _class.getSuperclass().getDeclaredFields();
        for (Field f : fields) {
            if (f.getName().equals(name)) {
                f.setAccessible(true);
                return f;
            }
        }
        return null;
    }
}
