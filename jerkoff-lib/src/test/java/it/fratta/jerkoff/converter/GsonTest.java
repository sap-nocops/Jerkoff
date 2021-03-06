/**
 * 
 */
package it.fratta.jerkoff.converter;

import java.io.StringWriter;
import java.io.Writer;
import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Arrays;
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
    public void testCollection() {
        try {
            Gson gson = getGson();

            Integer[] arre = new Integer[] { 1, 2, 3, 4, 5 };
            Collection<Integer> arrColl = Arrays.asList(arre);
            String json = gson.toJson(arrColl);
            LOG.info("json: " + json);
            Type collectionType = new TypeToken<Collection<Integer>>() {
            }.getType();
            Collection<Integer> ints2 = gson.fromJson(json, collectionType);
            LOG.info("ints2: " + ints2);

            Collection collection = new ArrayList();
            collection.add("hello");
            collection.add(5);
            collection.add(new Event("GREETINGS", "guest"));

            json = gson.toJson(collection.toArray());
            LOG.info("json: " + json);

            Object[] arrayObj = gson.fromJson(json, Object[].class);
            LOG.info("arrayObj: " + arrayObj);

            if (Iterable.class.isAssignableFrom(ints2.getClass())) {
                Object[] arr = (Object[]) ints2.toArray();
                LOG.info("coll " + arr);
            }
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
            LOG.error("", e);
        }
    }

    @Test
    public void testObject() {
        try {
            Gson gson = getGson();

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

            BagOfPrimitives obj33 = new BagOfPrimitives();
            json = gson.toJson(obj33);
            BagOfPrimitives obj3 = gson.fromJson(json, BagOfPrimitives.class);
            LOG.info("obj3: " + obj3);

            Comparable comp = new Long(1L);
            json = gson.toJson(comp);
            LOG.info("json: " + json);
            Comparable comp2 = gson.fromJson(json, Comparable.class);
            LOG.info("comp2: " + comp2);

        } catch (Exception e) {
            LOG.error("", e);
        }
    }

    @Test
    public void testPrimitivi() {
        try {
            Gson gson = getGson();
            int one = 1;
            String json = gson.toJson(one);
            LOG.info("json: " + json);
            int oneInt = gson.fromJson(json, int.class);
            LOG.info("oneInt: " + oneInt);
            Integer oneInteg = 1;
            json = gson.toJson(oneInteg);
            LOG.info("json: " + json);
            Integer oneInteger = gson.fromJson(json, Integer.class);
            LOG.info("oneInteger: " + oneInteger);
            long oneL = 1L;
            json = gson.toJson(oneL);
            LOG.info("json: " + json);
            long oneLong = gson.fromJson(json, long.class);
            Long oneLo = 1L;
            json = gson.toJson(oneLo);
            LOG.info("json: " + json);
            Long onelong = gson.fromJson(json, Long.class);
            LOG.info("oneLong: " + oneLong);
            Boolean falseB = Boolean.FALSE;
            json = gson.toJson(falseB);
            LOG.info("json: " + json);
            Boolean falseBoolean = gson.fromJson(json, Boolean.class);
            LOG.info("falseBoolean: " + falseBoolean);
            String stabc = "abc";
            json = gson.toJson(stabc);
            LOG.info("json: " + json);
            String str = gson.fromJson(json, String.class);
            LOG.info("str: " + str);
            String[] stArr = { "abc", "bcb" };
            json = gson.toJson(stArr);
            LOG.info("json: " + json);
            String[] arrayStr = gson.fromJson(json, String[].class);
            LOG.info("arrayStr: " + arrayStr);
            if (arrayStr.getClass().isArray()) {
                Object[] arr = (Object[]) arrayStr;
                LOG.info("arrStr " + arr);
            }
        } catch (Exception e) {
            LOG.error("", e);
        }
    }

    private Gson getGson() {

        // GsonBuilder gsonBuilder = new GsonBuilder();
        // // register custom adapter from configuration
        // gsonBuilder.registerTypeHierarchyAdapter(Object.class,
        // new ObjectSerializer(gsonBuilder.create()));
        // gsonBuilder.registerTypeHierarchyAdapter(Object.class,
        // new ObjectDeserializer(gsonBuilder.create()));
        // Gson gson = gsonBuilder.create();
        GsonBuilder gsonBuilder = new GsonBuilder();
        ObjectTypeAdapterFactory del = new ObjectTypeAdapterFactory();
        gsonBuilder.registerTypeAdapterFactory(del);
        // register custom adapter from configuration
        new GraphAdapterBuilder().addType(A.class).addType(B.class).registerOn(gsonBuilder);

        Gson gson = gsonBuilder.create();
        return gson;
    }

    @Test
    public void testGeneric() {
        try {
            Gson gson = getGson();

            TypeToken<Foo<Integer>> fooIntTok = new TypeToken<Foo<Integer>>() {
            };
            Foo<Integer> foo = new Foo<Integer>();
            foo.value = 1;
            String json = gson.toJson(foo);
            LOG.info("json: " + json);
            Foo<Integer> fooDes = gson.fromJson(json, fooIntTok.getType());
            LOG.info("fooDes: " + fooDes);

            Foo<Integer> fooDesG = gson.fromJson(json, foo.getClass());
            LOG.info("fooDes: " + fooDesG);
            LOG.info(foo.getClass().getTypeParameters().length);
            LOG.info(foo.value.getClass().getTypeParameters().length);
            for (Method method : foo.getClass().getDeclaredMethods()) {
                for (Type parameterGeneric : method.getGenericParameterTypes()) {
                    if (parameterGeneric instanceof ParameterizedType) {
                        LOG.info(toStringPT((ParameterizedType) parameterGeneric));
                        Type[] ata = ((ParameterizedType) parameterGeneric)
                                .getActualTypeArguments();
                        LOG.info(ata);
                    } else {
                        LOG.info(parameterGeneric);
                    }
                }
            }
            new TypeToken<it.fratta.jerkoff.converter.GsonTest.Foo<java.lang.Integer>>() {};
        } catch (Exception e) {
            LOG.error(e);
        }
    }

    static String toStringPT(ParameterizedType pt) {
        StringBuilder sb = new StringBuilder();
        Type ownerType = pt.getOwnerType();
        Class<?> rawType = (Class<?>) pt.getRawType();
        Type[] actualTypeArguments = pt.getActualTypeArguments();
        sb.append(rawType.getName().replace('$', '.'));

        if (actualTypeArguments != null && actualTypeArguments.length > 0) {
            sb.append("<");
            boolean first = true;
            for (Type t : actualTypeArguments) {
                if (!first)
                    sb.append(", ");
                sb.append(((Class<?>) t).getName());
                first = false;
            }
            sb.append(">");
        }

        return sb.toString();
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

        void test(Foo<Integer> prova, String pippo) {

        }
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
