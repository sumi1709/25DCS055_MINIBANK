package model.annotation;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.List;
public class AnnotationValidator {
    public static String[] validate(Object obj) {
        List<String> errors = new ArrayList<>();
        if (obj == null) return new String[]{"Object cannot be null."};
        for (Class<?> type=obj.getClass(); type!=null && type!=Object.class; type=type.getSuperclass()) {
            for (Field field : type.getDeclaredFields()) {
                try {
                    field.setAccessible(true); Object value=field.get(obj);
                    if (field.isAnnotationPresent(Positive.class)) {
                        Positive a=field.getAnnotation(Positive.class);
                        if (!(value instanceof Number) || ((Number)value).doubleValue()<=0) errors.add(field.getName()+" "+a.message());
                    }
                    if (field.isAnnotationPresent(MaxLength.class) && value instanceof String s) {
                        int max=field.getAnnotation(MaxLength.class).value();
                        if (s.length()>max) errors.add(field.getName()+" length must be at most "+max+" characters");
                    }
                    if (field.isAnnotationPresent(Id.class) && (value==null || value.toString().isBlank())) errors.add(field.getName()+" must contain an ID");
                } catch (IllegalAccessException e) { errors.add("Could not validate field: "+field.getName()); }
            }
        }
        return errors.toArray(new String[0]);
    }
}
