package store;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import model.annotation.Id;
public class Repository<T> {
    private final HashMap<String,T> items=new HashMap<>();
    public synchronized T save(T entity){if(entity==null)throw new IllegalArgumentException("Entity cannot be null.");String id=extractId(entity);items.put(id,entity);return entity;}
    public synchronized T findById(String id){return items.get(id);}
    public synchronized ArrayList<T> findAll(){return new ArrayList<>(items.values());}
    public synchronized boolean delete(String id){return items.remove(id)!=null;}
    @SafeVarargs public final synchronized void saveAll(T... entities){for(T entity:entities)save(entity);}
    private String extractId(T entity){for(Class<?> c=entity.getClass();c!=null&&c!=Object.class;c=c.getSuperclass()){for(Field f:c.getDeclaredFields()){if(f.isAnnotationPresent(Id.class)){try{f.setAccessible(true);Object value=f.get(entity);if(value==null)throw new IllegalArgumentException("@Id field is null.");return value.toString();}catch(IllegalAccessException e){throw new IllegalArgumentException("Cannot read @Id field.",e);}}}}throw new IllegalArgumentException("No field annotated with @Id found on "+entity.getClass().getName());}
    public static <N extends Number> double average(N[] values){if(values==null||values.length==0) return 0.0;double sum=0;for(N n:values)sum+=n.doubleValue();return sum/values.length;}
}
