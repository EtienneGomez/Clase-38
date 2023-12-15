import networking.WebClient;

import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutionException;

public class Aggregator {
  private WebClient webClient;

  public Aggregator() {
    this.webClient = new WebClient();
  }

  public Map<String, String> sendTasksToWorkers(List<String> workersAddresses, List<String> tasks) {
    CompletableFuture<String>[] futures = new CompletableFuture[workersAddresses.size()];
    Map<String, String> taskAssignments = new LinkedHashMap<>();

    // Asegurarse de que hay tareas disponibles
    if (tasks.size() < workersAddresses.size()) {
      throw new IllegalArgumentException("No hay suficientes tareas para los trabajadores.");
    }

    for (int i = 0; i < workersAddresses.size(); i++) {
      String workerAddress = workersAddresses.get(i);

      // Verificar que hay tareas restantes
      if (i < tasks.size()) {
        String task = tasks.get(i);
        byte[] requestPayload = task.getBytes();
        futures[i] = webClient.sendTask(workerAddress, requestPayload);
        taskAssignments.put("Tarea asignada a " + workerAddress, task);
      } else {
        // No hay más tareas, salir del bucle
        break;
      }
    }

    CompletableFuture<?> firstFinished = CompletableFuture.anyOf(futures);
    try {
      firstFinished.get(); // Esperar a que alguno termine
    } catch (InterruptedException | ExecutionException e) {
      e.printStackTrace();
    }

    // Recopilar resultados de las tareas
    for (int i = 0; i < futures.length; i++) {
      try {
        // Verificar que hay resultados disponibles
        if (i < tasks.size()) {
          String result = futures[i].get();
          taskAssignments.put("Resultado de la tarea " + tasks.get(i), result);
        } else {
          // No hay más resultados, salir del bucle
          break;
        }
      } catch (InterruptedException | ExecutionException e) {
        e.printStackTrace();
      }
    }

    return taskAssignments;
  }
}
