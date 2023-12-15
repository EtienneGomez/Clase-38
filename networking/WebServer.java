package networking;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpServer;
import java.io.IOException;
import java.io.OutputStream;
import java.math.BigInteger;
import java.net.InetSocketAddress;
import java.util.Arrays;
import java.util.Random; // Importación añadida
import java.util.concurrent.Executors;

public class WebServer {
    private static final String TASK_ENDPOINT = "/task";
    private static final String STATUS_ENDPOINT = "/status";
    private static final String SEARCH_TOKEN_ENDPOINT = "/searchToken";
    private final int port;
    private HttpServer server;

    public static void main(String[] args) {
        int serverPort = 8080;
        if (args.length == 1) {
            serverPort = Integer.parseInt(args[0]);
        }
        WebServer webServer = new WebServer(serverPort);
        webServer.startServer();
        System.out.println("Servidor escuchando en el puerto " + serverPort);
    }

    public WebServer(int port) {
        this.port = port;
    }

    public void startServer() {
        try {
            this.server = HttpServer.create(new InetSocketAddress(port), 0);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }
        HttpContext statusContext = server.createContext(STATUS_ENDPOINT);
        HttpContext taskContext = server.createContext(TASK_ENDPOINT);
        HttpContext searchTokenContext = server.createContext(SEARCH_TOKEN_ENDPOINT);
        statusContext.setHandler(this::handleStatusCheckRequest);
        taskContext.setHandler(this::handleTaskRequest);
        searchTokenContext.setHandler(this::handlerSearchToken);
        server.setExecutor(Executors.newFixedThreadPool(8));
        server.start();
    }

    private void handleTaskRequest(HttpExchange exchange) throws IOException {
        if (!exchange.getRequestMethod().equalsIgnoreCase("post")) {
            exchange.close();
            return;
        }

        byte[] requestBytes = exchange.getRequestBody().readAllBytes();
        // Actualiza esta línea para usar directamente Demo en lugar de Test.Demo
        Demo receivedObject = (Demo) SerializationUtils.deserialize(requestBytes);
        System.out.println("Objeto Demo recibido: a = " + receivedObject.a + ", b = " + receivedObject.b);

        String responseMessage = "Objeto Demo recibido correctamente\n";
        sendResponse(responseMessage.getBytes(), exchange);
    }
    private byte[] calculateResponse(byte[] requestBytes) {
        String bodyString = new String(requestBytes);
        String[] stringNumbers = bodyString.split(",");
        BigInteger result = BigInteger.ONE;
        for (String number : stringNumbers) {
            BigInteger bigInteger = new BigInteger(number);
            result = result.multiply(bigInteger);
        }
        return String.format("El resultado de la multiplicación es %s\n", result).getBytes();
    }
    private void handleStatusCheckRequest(HttpExchange exchange) throws IOException {
        if (!exchange.getRequestMethod().equalsIgnoreCase("get")) {
            exchange.close();
            return;
        }
        String responseMessage = "El servidor está vivo\n";
        sendResponse(responseMessage.getBytes(), exchange);
    }
    private void sendResponse(byte[] responseBytes, HttpExchange exchange) throws IOException {
        exchange.sendResponseHeaders(200, responseBytes.length);
        OutputStream outputStream = exchange.getResponseBody();
        outputStream.write(responseBytes);
        outputStream.flush();
        outputStream.close();
        exchange.close();
    }
    private void handlerSearchToken(HttpExchange exchange) throws IOException {
        if (!exchange.getRequestMethod().equalsIgnoreCase("post")) {
            exchange.close();
            return;
        }
        Headers headers = exchange.getRequestHeaders();
        boolean isDebugMode = false;
        if (headers.containsKey("X-Debug") && headers.get("X-Debug").get(0).equalsIgnoreCase("true")) {
            isDebugMode = true;
        }
        long startTime = System.nanoTime();
        byte[] requestBytes = exchange.getRequestBody().readAllBytes();
        byte[] responseBytes = searchToken(requestBytes);
        long finishTime = System.nanoTime();
        if (isDebugMode) {
            String debugMessage = String.format("La operación tomó %d nanosegundos", finishTime - startTime);
            exchange.getResponseHeaders().put("X-Debug-Info", Arrays.asList(debugMessage));
        }
        sendResponse(responseBytes, exchange);
    }
    private byte[] searchToken(byte[] requestBytes) {
        String bodyString = new String(requestBytes);
        String[] stringInput = bodyString.split(",");
        int cantidadPalabras = Integer.parseInt(stringInput[0]);
        char[] cadenota = generarCadenota(cantidadPalabras);
        int contador = contarApariciones(cadenota, stringInput[1]);
        String result = String.format("Número de veces que aparece la cadena %s es: %s\n", stringInput[1], contador);
        return result.getBytes();
    }
    public static char[] generarCadenota(int cantidadPalabras) {
        Random random = new Random();
        char[] cadenota = new char[cantidadPalabras * 4];
        for (int i = 0; i < cantidadPalabras; i++) {
            for (int j = 0; j < 3; j++) {
                char letra = (char) (random.nextInt(26) + 'A');
                cadenota[i * 4 + j] = letra;
            }
            cadenota[i * 4 + 3] = ' ';
        }
        return cadenota;
    }
    private int contarApariciones(char[] cadenota, String token) {
        int contadorApariciones = 0;
        int tokenLength = token.length();
    
        for (int i = 0; i <= cadenota.length - tokenLength; i++) {
            boolean coincide = true;
    
            for (int j = 0; j < tokenLength; j++) {
                if (cadenota[i + j] != token.charAt(j)) {
                    coincide = false;
                    break;
                }
            }
    
            if (coincide) {
                contadorApariciones++;
            }
        }
    
        return contadorApariciones;
    }
}