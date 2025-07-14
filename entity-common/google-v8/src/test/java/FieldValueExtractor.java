import java.util.*;
import java.lang.reflect.Field;

public class FieldValueExtractor {

    /**
     * 递归属性列表，获取对象中指定字段的值，如有多个字段，则返回一个list
     * @param obj
     * @param fieldName
     * @return
     */
    public static List<Object> extractVal(Object obj, String fieldName){
        List<Object> result = new ArrayList<>();
        extractValHelper(obj, fieldName, result, new HashSet<>());
        return result;
    }
    
    private static void extractValHelper(Object obj, String fieldName, 
                                         List<Object> result, Set<Object> visited) {
        if (obj == null || visited.contains(obj)) {
            return;
        }
        visited.add(obj);
        Class<?> clazz = obj.getClass();
        for (Field field : clazz.getFields()) {
            if (field.getName().equals(fieldName)) {
                field.setAccessible(true);
                try {
                    Object value = field.get(obj);
                    // 递归处理非简单类型对象
                    if (value != null) {
                        if(value instanceof Optional){
                            if(((Optional<?>) value).isPresent()){
                                value=((Optional<?>) value).get();
                            }else{
                                continue;
                            }
                        }
                        if (!isSimpleType(value.getClass())) {
                            extractValHelper(value, fieldName, result, visited);
                        } else {
                            result.add(value);
                        }
                    }
                } catch (IllegalAccessException ignored) {
                }
            }
        }
    }
    
    private static boolean isSimpleType(Class<?> clazz) {
        return clazz.isPrimitive() || 
               clazz == String.class || 
               clazz.getPackage().getName().startsWith("java.");
    }
}
