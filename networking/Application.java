package networking;

import java.util.Arrays;
import java.util.Map;

public class Application {
    private static final String WORKER_ADDRESS_1 = "http://35.226.252.238:8080/searchToken";

    public static void main(String[] args) {
        Aggregator aggregator = new Aggregator();
        String task1 = "175760,IPN";

        Map<String, String> results = aggregator.sendTasksToWorkers(Arrays.asList(WORKER_ADDRESS_1),
            Arrays.asList(task1));

        for (Map.Entry<String, String> entry : results.entrySet()) {
            System.out.println(entry.getKey() + ": " + entry.getValue());
        }
    }
}
