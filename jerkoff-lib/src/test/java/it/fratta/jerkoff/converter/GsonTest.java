/**
 * 
 */
package it.fratta.jerkoff.converter;

import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;

import org.apache.log4j.Logger;
import org.junit.Test;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonDeserializationContext;
import com.google.gson.JsonDeserializer;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import com.google.gson.JsonParser;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import com.google.gson.graph.GraphAdapterBuilder;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonWriter;

/**
 * @author ElGansoSnowhiteDurden
 *
 */
@SuppressWarnings({ "rawtypes", "unchecked", "unused" })
public class GsonTest {

    private static final Logger LOG = Logger.getLogger(GsonTest.class);

    @Test
    public void test() {
        try {
            GsonBuilder gsonBuilder = new GsonBuilder();
            // register custom adapter from configuration
            gsonBuilder.registerTypeHierarchyAdapter(Object.class,
                    new ObjectSerializer(gsonBuilder.create()));
            gsonBuilder.registerTypeHierarchyAdapter(Object.class,
                    new ObjectDeserializer(gsonBuilder.create()));
            Gson gson = gsonBuilder.create();
            int oneInt = gson.fromJson("1", int.class);
            LOG.info("oneInt: " + oneInt);
            Integer oneInteger = gson.fromJson("1", Integer.class);
            LOG.info("oneInteger: " + oneInteger);
            Long oneLong = gson.fromJson("1", Long.class);
            LOG.info("oneLong: " + oneLong);
            Boolean falseBoolean = gson.fromJson("false", Boolean.class);
            LOG.info("falseBoolean: " + falseBoolean);
            String str = gson.fromJson("\"abc\"", String.class);
            LOG.info("str: " + str);
            String[] arrayStr = gson.fromJson("[\"abc\"]", String[].class);
            LOG.info("arrayStr: " + arrayStr);

            LOG.info("ex: " + new IllegalAccessError().getClass().getName());

            Object object = new Object();
            String json = gson.toJson(object);
            LOG.info("json: " + json);
            Object object2 = gson.fromJson(json, Object.class);
            LOG.info("object2: " + object2);

            BagOfPrimitives obj = new BagOfPrimitives();
            json = gson.toJson(obj);
            LOG.info("json: " + json);
            BagOfPrimitives obj2 = gson.fromJson(json, BagOfPrimitives.class);
            LOG.info("obj2: " + obj2);

            json = "[1,2,3,4,5]";
            Type collectionType = new TypeToken<Collection<Integer>>() {
            }.getType();
            Collection<Integer> ints2 = gson.fromJson(json, collectionType);
            LOG.info("ints2: " + ints2);

            Foo<Integer> foo = new Foo<Integer>();
            foo.value = 1;
            json = gson.toJson(foo);
            LOG.info("json: " + json);
            Foo<?> fooDes = gson.fromJson(json, foo.getClass());
            LOG.info("fooDes: " + fooDes);

            Collection collection = new ArrayList();
            collection.add("hello");
            collection.add(5);
            collection.add(new Event("GREETINGS", "guest"));

            json = gson.toJson(collection.toArray());
            LOG.info("json: " + json);
            JsonParser parser = new JsonParser();
            JsonArray array = parser.parse(json).getAsJsonArray();
            String message = gson.fromJson(array.get(0), String.class);
            Integer number = gson.fromJson(array.get(1), Integer.class);
            Event event = gson.fromJson(array.get(2), Event.class);
            LOG.info("message: " + message);
            LOG.info("number: " + number);
            LOG.info("event: " + event);

            Object[] arrayObj = gson.fromJson(json, Object[].class);
            LOG.info("arrayObj: " + arrayObj);

            json = "{\"value1\":1,\"value2\":\"abc\", \"class\":\"it.fratta.jerkoff.converter.GsonTest$BagOfPrimitives\"}";
            BagOfPrimitives obj3 = gson.fromJson(json, BagOfPrimitives.class);
            LOG.info("obj3: " + obj3);
            JsonElement elem = parser.parse(json);
            JsonElement elemClass = elem.getAsJsonObject().get("class");
            LOG.info("elemClass: " + elemClass.getAsString());

            Comparable comp = new Long(1L);
            json = gson.toJson(comp);
            LOG.info("json: " + json);
            Comparable comp2 = gson.fromJson(json, Comparable.class);
            LOG.info("comp2: " + comp2);

            if (Iterable.class.isAssignableFrom(ints2.getClass())) {
                Object[] arr = (Object[]) ints2.toArray();
                LOG.info("coll " + arr);
            }

            if (arrayStr.getClass().isArray()) {
                Object[] arr = (Object[]) arrayStr;
                LOG.info("arrStr " + arr);
            }

            BagOfCollections objC = new BagOfCollections();
            objC.collection = new ArrayList();
            objC.collection.add("hello");
            objC.collection.add(comp);
            objC.collection.add(new Event("GREETINGS", "guest"));
            json = gson.toJson(objC);
            LOG.info("json: " + json);
            BagOfCollections objC2 = gson.fromJson(json, BagOfCollections.class);
            LOG.info("objC2: " + objC2);

        } catch (Exception e) {
            LOG.error(e);
        }
    }

    @Test
    public void testDel() {
        try {
            GsonBuilder gsonBuilder = new GsonBuilder();
            // register custom adapter from configuration
            new GraphAdapterBuilder().addType(A.class).addType(B.class).registerOn(gsonBuilder);
            ObjectTypeAdapterFactory del = new ObjectTypeAdapterFactory();
            gsonBuilder.registerTypeAdapterFactory(del);
            Gson gson = gsonBuilder.create();

            BagOfPrimitives obj = new BagOfPrimitives(66);
            String json = gson.toJson(obj);
            LOG.info("json: " + json);
            BagOfPrimitives obj2 = gson.fromJson(json, BagOfPrimitives.class);
            LOG.info("obj2: " + obj2);
            TypeToken<Foo<Integer>> fooIntTok = new TypeToken<Foo<Integer>>() {
            };
            Foo<Integer> foo = new Foo<Integer>();
            foo.value = 1;
            json = gson.toJson(foo);
            LOG.info("json: " + json);
            Foo<Integer> fooDes = gson.fromJson(json, fooIntTok.getType());
            LOG.info("fooDes: " + fooDes);

            A a = new A();
            B b = new B();
            a.setName("puppa");
            a.setB(b);
            b.setA(a);

            json = gson.toJson(a);
            LOG.info("json: " + json);
            A a2 = gson.fromJson(json, a.getClass());
            LOG.info("a: " + a2);
            Comparable comp = new Long(1L);
            BagOfCollections objC = new BagOfCollections();
            objC.collection = new ArrayList();
            objC.collection.add("hello");
            objC.collection.add(comp);
            objC.collection.add(new Event("GREETINGS", "guest"));
            json = gson.toJson(objC);
            LOG.info("json: " + json);
            BagOfCollections objC2 = gson.fromJson(json, BagOfCollections.class);
            LOG.info("objC2: " + objC2);

        } catch (Exception e) {
            LOG.error(e);
        }
    }

    @Test
    public void testWr() {
        try {
            Writer wr = new StringWriter();
            JsonWriter out = new JsonWriter(wr);
            out.beginObject();
            out.name("class");
            out.value(this.getClass().getName());
            out.endObject();
            out.close();
            LOG.info("json: " + wr.toString());
        } catch (Exception e) {
            LOG.error(e);
        }
    }

    @Test
    public void testRec() {
        try {
            GsonBuilder gsonBuilder = new GsonBuilder();
            // register custom adapter from configuration
            new GraphAdapterBuilder().addType(A.class).addType(B.class).registerOn(gsonBuilder);
            // gsonBuilder.registerTypeHierarchyAdapter(Object.class,
            // new ObjectSerializer(gsonBuilder.create()));
            // gsonBuilder.registerTypeHierarchyAdapter(Object.class,
            // new ObjectDeserializer(gsonBuilder.create()));
            Gson gson = gsonBuilder.create();

            A a = new A();
            B b = new B();
            a.setB(b);
            b.setA(a);

            String json = gson.toJson(a);
            LOG.info("json: " + json);
            A a2 = gson.fromJson(json, a.getClass());
            LOG.info("a: " + a2);

        } catch (Exception e) {
            LOG.error(e);
        }
    }

    class A {

        private B b;
        private String name;

        public String getName() {
            return name;
        }

        public void setName(String name) {
            this.name = name;
        }

        public B getB() {
            return b;
        }

        public void setB(B b) {
            this.b = b;
        }
    }

    class B {

        private A a;

        public A getA() {
            return a;
        }

        public void setA(A a) {
            this.a = a;
        }
    }

    class BagOfPrimitives {

        private int value1 = 1;
        private String value2 = "abc";
        private transient int value3 = 3;

        BagOfPrimitives() {
            // no-args constructor
        }

        BagOfPrimitives(int val) {
            value1 = val;
        }
    }

    class BagOfCollections {

        private int value1 = 1;
        Collection collection;

        BagOfCollections() {

        }
    }

    class Foo<T> {

        T value;
    }

    class Event {

        private String name;
        private String source;

        public Event(String name, String source) {
            this.name = name;
            this.source = source;
        }

        @Override
        public String toString() {
            return String.format("(name=%s, source=%s)", name, source);
        }
    }

}
