package com.hzleshare.common.utils;

import com.thoughtworks.xstream.XStream;
import com.thoughtworks.xstream.converters.Converter;
import com.thoughtworks.xstream.converters.MarshallingContext;
import com.thoughtworks.xstream.converters.UnmarshallingContext;
import com.thoughtworks.xstream.core.util.QuickWriter;
import com.thoughtworks.xstream.io.HierarchicalStreamReader;
import com.thoughtworks.xstream.io.HierarchicalStreamWriter;
import com.thoughtworks.xstream.io.naming.NoNameCoder;
import com.thoughtworks.xstream.io.xml.PrettyPrintWriter;
import com.thoughtworks.xstream.io.xml.XppDriver;

import java.io.InputStream;
import java.io.Writer;
import java.util.AbstractMap;
import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

/**
 *  2017/5/21.
 */
public class XStreamUtils {

    private XStreamUtils() {
    }

    /**
     * 创建 xstream 实例
     * @return
     */
    public static XStream getInstance() {
        XStream xstream = new XStream(new XppDriver(new NoNameCoder()) {
            @Override
            public HierarchicalStreamWriter createWriter(Writer out) {
                return new PrettyPrintWriter(out) {
                    // 对所有xml节点的转换都增加CDATA标记
                    boolean cdata = true;

                    @Override
                    @SuppressWarnings("rawtypes")
                    public void startNode(String name, Class clazz) {
                        super.startNode(name, clazz);
                    }

                    /**
                     * 解决 xstream 双下划线问题
                     * @param name
                     * @return
                     */
                    @Override
                    public String encodeNode(String name) {
                        return name;
                    }

                    /**
                     * 所有节点都用 CDATA 包装
                     * @param writer
                     * @param text
                     */
                    @Override
                    protected void writeText(QuickWriter writer, String text) {
                        if (cdata) {
                            writer.write("<![CDATA[");
                            writer.write(text);
                            writer.write("]]>");
                        } else {
                            writer.write(text);
                        }
                    }
                };
            }
        });

        //xstream.ignoreUnknownElements();
        return xstream;
    }

    /**
     * 从对象生成 xml
     * @param obj
     * @return
     */
    public static String toXml(Object obj) {
        XStream xstream = getInstance();
        xstream.processAnnotations(obj.getClass());
        return xstream.toXML(obj);
    }

    /**
     * 从 xml string 中序列化成对象
     * @param xml
     * @param clz
     * @param <T>
     * @return
     */
    public static <T> T fromXml(String xml, Class<T> clz) {
        XStream xstream = getInstance();
        xstream.processAnnotations(clz);
        return (T) xstream.fromXML(xml);
    }

    /**
     * 从 InputStream 中序列化成对象
     * @param inputStream
     * @param clz
     * @param <T>
     * @return
     */
    public static <T> T fromXml(InputStream inputStream, Class<T> clz) {
        XStream xstream = getInstance();
        xstream.processAnnotations(clz);
        return (T) xstream.fromXML(inputStream);
    }

    /**
     * 转成 map 对象
     * 验证收到的 sign 时使用此接口
     * @param xml
     * @return
     */
    public static SortedMap<String, String> fromXml(String xml) {
        XStream xstream = getInstance();
        xstream.registerConverter(new MapEntryConverter());
        xstream.alias("xml", Map.class);

        return (SortedMap<String, String>) xstream.fromXML(xml);
    }

    /**
     * 自定义 map 转换类
     */
    public static class MapEntryConverter implements Converter {

        @Override
        public boolean canConvert(Class clazz) {
            return AbstractMap.class.isAssignableFrom(clazz);
        }

        @Override
        public void marshal(Object value, HierarchicalStreamWriter writer, MarshallingContext context) {
            AbstractMap map = (AbstractMap) value;
            for (Object obj : map.entrySet()) {
                Map.Entry entry = (Map.Entry) obj;
                writer.startNode(entry.getKey().toString());
                Object val = entry.getValue();
                if (null != val) {
                    writer.setValue(val.toString());
                }
                writer.endNode();
            }
        }

        @Override
        public Object unmarshal(HierarchicalStreamReader reader, UnmarshallingContext context) {
            SortedMap<String, String> map = new TreeMap<>();

            while(reader.hasMoreChildren()) {
                reader.moveDown();

                String key = reader.getNodeName();
                // nodeName aka element's name
                String value = reader.getValue();
                map.put(key, value);

                reader.moveUp();
            }

            return map;
        }

    }

}
