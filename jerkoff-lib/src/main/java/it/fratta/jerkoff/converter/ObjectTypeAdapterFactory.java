/**
 * 
 */
package it.fratta.jerkoff.converter;

import java.io.IOException;

import com.google.gson.Gson;
import com.google.gson.TypeAdapter;
import com.google.gson.TypeAdapterFactory;
import com.google.gson.reflect.TypeToken;
import com.google.gson.stream.JsonReader;
import com.google.gson.stream.JsonWriter;

/**
 * @author ElGansoSnowhiteDurden
 *
 */
public class ObjectTypeAdapterFactory implements TypeAdapterFactory {

    /*
     * (non-Javadoc)
     * 
     * @see com.google.gson.TypeAdapterFactory#create(com.google.gson.Gson,
     * com.google.gson.reflect.TypeToken)
     */
    @Override
    public <T> TypeAdapter<T> create(Gson gson, TypeToken<T> type) {
        final TypeAdapter<T> delegate = gson.getDelegateAdapter(this, type);
        return new TypeAdapter<T>() {

            public void write(JsonWriter out, T value) throws IOException {
                out.beginObject();
                out.name("class");
                out.value(value.getClass().getName());
                out.name("value");
                delegate.write(out, value);
                out.endObject();
            }

            public T read(JsonReader in) throws IOException {
                in.beginObject();
                in.nextName();
                in.nextString();
                in.nextName();
                T res = delegate.read(in);
                in.endObject();
                return res;
            }
        };
    }

}
