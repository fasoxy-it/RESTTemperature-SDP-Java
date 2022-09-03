package MQTT;

import org.eclipse.paho.client.mqttv3.*;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Scanner;

public class PublisherSubscriberSensorController {

    public static void main(String[] args) {

        MqttClient client;
        String broker = "tcp://localhost:1883";
        String clientId = MqttClient.generateClientId();
        String subtopic = "home/sensors/temp";
        String pubTopic = "home/controllers/temp";
        int qos = 2;

        ArrayList<String> lastMeasurements = new ArrayList<String>();

        try {
            client = new MqttClient(broker, clientId);
            MqttConnectOptions connectOptions = new MqttConnectOptions();
            connectOptions.setCleanSession(true);

            System.out.println(clientId + "Connecting Broker" + broker);
            client.connect(connectOptions);
            System.out.println(clientId + " Connected - Thread PID: " + Thread.currentThread().getId());

            client.setCallback(new MqttCallback() {

                public void messageArrived(String topic, MqttMessage message) throws MqttException {
                    // Called when a message arrives from the server that matches any subscription made by the client
                    String time = new Timestamp(System.currentTimeMillis()).toString();
                    String receivedMessage = new String(message.getPayload());
                    System.out.println(clientId +" Received a Message! - Callback - Thread PID: " + Thread.currentThread().getId() +
                            "\n\tTime:    " + time +
                            "\n\tTopic:   " + topic +
                            "\n\tMessage: " + receivedMessage +
                            "\n\tQoS:     " + message.getQos() + "\n");

                    lastMeasurements.add(receivedMessage);

                    if (lastMeasurements.size() == 5) {

                        float total = 0;
                        float average = 0;

                        for (int i=0; i<lastMeasurements.size(); i++) {
                            total = total + Float.parseFloat(lastMeasurements.get(i));
                            average = total / 5;
                        }

                        System.out.println("Average: " + average + "\n");

                        String payload = "";

                        if (average < 20.0) {
                            payload = "TURN ON";
                        } else {
                            payload = "TURN OFF";
                        }

                        MqttMessage sendMessage = new MqttMessage(payload.getBytes());

                        sendMessage.setQos(qos);
                        System.out.println(clientId + " Publishing message: " + payload + " ...");
                        client.publish(pubTopic, sendMessage);
                        System.out.println(clientId + " Message published");

                        lastMeasurements.remove(0);
                    }

                    System.out.println("\n ***  Press a random key to exit *** \n");

                }

                public void connectionLost(Throwable cause) {
                    System.out.println(clientId + " Connectionlost! cause:" + cause.getMessage()+ "-  Thread PID: " + Thread.currentThread().getId());
                }

                public void deliveryComplete(IMqttDeliveryToken token) {
                    // Not used here
                }

            });

            System.out.println(clientId + " Subscribing ... - Thread PID: " + Thread.currentThread().getId());
            client.subscribe(subtopic,qos);
            System.out.println(clientId + " Subscribed to topics : " + subtopic);

            System.out.println("\n ***  Press a random key to exit *** \n");
            Scanner command = new Scanner(System.in);
            command.nextLine();
            client.disconnect();

        } catch (MqttException mqttException) {
            System.out.println("reason " + mqttException.getReasonCode());
            System.out.println("msg " + mqttException.getMessage());
            System.out.println("loc " + mqttException.getLocalizedMessage());
            System.out.println("cause " + mqttException.getCause());
            System.out.println("excep " + mqttException);
            mqttException.printStackTrace();
        }
    }

}
