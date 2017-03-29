/**
 * 
 */
package it.fratta.jerkoff.converter;

import java.io.IOException;

import org.apache.log4j.Logger;

import com.google.gson.Gson;
import com.google.gson.JsonParseException;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonToken;
import com.google.gson.stream.JsonWriter;

/**
 * @author ElGansoSnowhiteDurden
 *
 */
public class ObjectTypeAdapterFactory implements TypeAdapterFactory {

    private static final Logger LOG = Logger.getLogger(ObjectTypeAdapterFactory.class);

    /*
     * (non-Javadoc)
     * 
     * @see com.google.gson.TypeAdapterFactory#create(com.google.gson.Gson,
     * com.google.gson.reflect.TypeToken)
     */
    @Override
    public <T> TypeAdapter<T> create(final Gson gson, TypeToken<T> type) {
        final TypeAdapter<T> delegate = gson.getDelegateAdapter(this, type);
        return new MyTypeAdapter<T>(delegate, gson);
    }

    class MyTypeAdapter<T> extends TypeAdapter<T> {

        private TypeAdapter<T> delegate;
        private Gson gson;

        public MyTypeAdapter(TypeAdapter<T> delegate, Gson gson) {
            super();
            this.delegate = delegate;
            this.gson = gson;
        }

        public void write(JsonWriter out, T value) throws IOException {
            out.beginObject();
            out.name("class");
            out.value(value.getClass().getName());
            out.name("value");
            delegate.write(out, value);
            out.endObject();
        }

        @SuppressWarnings("unchecked")
        public T read(JsonReader in) throws IOException {
            Object res;
            in.beginObject();
            LOG.info(in.nextName());
            String clazzName = in.nextString();
            LOG.info(clazzName);
            LOG.info(in.nextName());
            try {
                Class<?> typeOfT = Class.forName(clazzName);
//                 TypeAdapter<?> del = gson.getDelegateAdapter(new ObjectTypeAdapterFactory(),
//                 );
                TypeAdapter<?> del = gson.getAdapter(TypeToken.get(typeOfT));
                LOG.info("ta: " + del + "taDel: " + delegate + " tt: " + TypeToken.get(typeOfT));
                if (typeOfT.isArray()) {
                    del = com.google.gson.internal.bind.ArrayTypeAdapter.FACTORY.create(gson, TypeToken.get(typeOfT));
                } else {
                    del = delegate; 
                }
                
                JsonToken peek = in.peek();
                switch (peek) {
                    case STRING:
                        res = in.nextString();
                        break;
                    case BOOLEAN:
                        res = in.nextBoolean();
                        break;
                    case NUMBER:
                        if (Long.class.equals(typeOfT)) {
                            res = in.nextLong();
                        } else if (Integer.class.equals(typeOfT)) {
                            res = in.nextInt();
                        } else {
                            res = in.nextDouble();
                        }
                        break;

                    default:
                        res = (T) del.read(in);
                }

            } catch (ClassNotFoundException e) {
                throw new JsonParseException(e);
            }
            // res = delegate.read(in);
            in.endObject();
            return (T) res;
        }

    }

}
