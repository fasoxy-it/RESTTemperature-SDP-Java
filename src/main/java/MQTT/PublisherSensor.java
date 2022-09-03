package MQTT;

import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import static java.lang.Thread.sleep;

public class PublisherSensor {

    public static void main(String[] args) {

        MqttClient client;
        String broker = "tcp://localhost:1883";
        String clientId = MqttClient.generateClientId();
        String topic = "home/sensors/temp";
        int qos = 2;

        try {

            client = new MqttClient(broker, clientId);
            MqttConnectOptions connectOptions = new MqttConnectOptions();
            connectOptions.setCleanSession(true);

            System.out.println(clientId + "Connecting Broker " + broker);
            client.connect(connectOptions);
            System.out.println(clientId + "Connected");

            while (true) {
                String payload = String.valueOf(18 + (Math.random()) * (22 - 18));
                MqttMessage message = new MqttMessage(payload.getBytes());

                message.setQos(qos);
                System.out.println(clientId + " Publishing message: " + payload + " ...");
                client.publish(topic, message);
                System.out.println(clientId + " Message published");

                sleep(5000);
            }

        } catch (MqttException mqttException) {
            mqttException.printStackTrace();
            System.out.println("reason " + mqttException.getReasonCode());
            System.out.println("msg " + mqttException.getMessage());
            System.out.println("loc " + mqttException.getLocalizedMessage());
            System.out.println("cause " + mqttException.getCause());
            System.out.println("excep " + mqttException);
            mqttException.printStackTrace();
        } catch (InterruptedException interruptedException) {
            interruptedException.printStackTrace();
        }
    }

}
