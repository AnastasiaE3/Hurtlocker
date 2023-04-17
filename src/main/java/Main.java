import org.apache.commons.io.IOUtils;

import javax.sql.rowset.serial.SerialStruct;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

public class Main {

    public String readRawDataToString() throws Exception{
        ClassLoader classLoader = getClass().getClassLoader();
        String result = IOUtils.toString(classLoader.getResourceAsStream("RawData.txt"));
        result = result.replace("^", ";");
        return result;
    }

    public static void main(String[] args) throws Exception{
        String output = (new Main()).readRawDataToString();
        String [] items = output.split(";");
        Pattern pattern = Pattern.compile("(?i)name:([\\w\\s]+);price:(\\d+\\.\\d{2});type:(\\w+);expiration:(\\d{1,2}/\\d{1,2}/\\d{4})");
        Map<String, Map<String, Integer>> itemsMap = new HashMap<>(); // for storing results
        for (String item : items) {
            String [] components = item.split(":");
            if (components.length != 4) {
                continue;
            }
            String name = components [1].trim().toLowerCase();
            String price = components [3].trim();
            if (!itemsMap.containsKey(name)) {
                itemsMap.put(name, new HashMap<>());
            }
            Map<String, Integer> itemData = itemsMap.get(name);
            itemData.put(price, itemData.getOrDefault(price, 0) + 1);
        }
        for (Map.Entry<String, Map<String, Integer>> entry : itemsMap.entrySet()) {
            System.out.println("name:\t" + entry.getKey() + "\tseen: " + entry.getValue().values().stream().reduce(0, Integer::sum) + " times");
            System.out.println("============= \t =============");
            for (Map.Entry<String, Integer> priceEntry : entry.getValue().entrySet()) {
                System.out.println("Price: \t" + priceEntry.getKey() + "\tseen: " + priceEntry.getValue() + " times");
                System.out.println("-------------\t-------------");
            }
            System.out.println();
        }
        System.out.println("Errors \t\tseen: " + itemsMap.getOrDefault("", new HashMap<>()).getOrDefault("", 0) + " times");
    }
}
