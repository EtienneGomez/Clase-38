import java.util.Arrays;
// import java.util.List;
import java.util.Map;

public class Application {
  private static final String WORKER_ADDRESS_1 = "http://35.226.252.238:8080/searchToken";
  // private static final String WORKER_ADDRESS_2 =
  // "http://localhost:8081/searchToken";

  public static void main(String[] args) {
    Aggregator aggregator = new Aggregator();
    String task1 = "175760,IPN";
    // String task2 = "1757600,SAL";
    // String task3 = "700000,MAS";

    Map<String, String> results = aggregator.sendTasksToWorkers(Arrays.asList(WORKER_ADDRESS_1),
        Arrays.asList(task1));

    // Imprimir las asignaciones y resultados
    for (Map.Entry<String, String> entry : results.entrySet()) {
      System.out.println(entry.getKey() + ": " + entry.getValue());
    }
  }
}
