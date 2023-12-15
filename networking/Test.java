package networking;
import java.util.concurrent.ExecutionException;

public class Test {
    public static void main(String[] args) {
        WebClient client = new WebClient();
        Demo demoObject = new Demo(2022, "Prueba serializacion y deserializacion");

        System.out.println("Enviando objeto Demo: a = " + demoObject.a + ", b = " + demoObject.b);
        try {
            byte[] serializedDemo = SerializationUtils.serialize(demoObject);
            String response = client.sendTask("http://localhost:8080/task", serializedDemo).get();
            System.out.println(response);
        } catch (InterruptedException | ExecutionException e) {
            e.printStackTrace();
        }
    }
}
