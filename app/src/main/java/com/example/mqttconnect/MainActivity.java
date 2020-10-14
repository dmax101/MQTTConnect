package com.example.mqttconnect;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import org.eclipse.paho.android.service.MqttAndroidClient;

import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttClientPersistence;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.MqttSecurityException;
import org.eclipse.paho.client.mqttv3.internal.security.SSLSocketFactoryFactory;

import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.util.Properties;

public class MainActivity extends AppCompatActivity {

    private Button btn1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        btn1 = (Button) findViewById(R.id.btn1);
        btn1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                String clientId = MqttClient.generateClientId();

                String topic = "aslfdk/sfjhkl";
                String payload = clientId.toString();

                MqttConnectOptions options = new MqttConnectOptions();

                MqttAndroidClient client = new MqttAndroidClient(
                        MainActivity.this,
                        "ssl://csilab-broker.inatel.br:8883",
                        clientId
                );

                InputStream input = null;
                try {
                    input = MainActivity.this.getAssets().open("server.bks");
                } catch (IOException e) {
                    e.printStackTrace();
                }
                try {
                    options.setSocketFactory(client.getSSLSocketFactory(input,""));
                } catch (MqttSecurityException e) {
                    e.printStackTrace();
                }
                ;
                try {
                    input = MainActivity.this.getAssets().open("server.bks");
                } catch (IOException e) {
                    e.printStackTrace();
                }


                try {
                    options.setSocketFactory(client.getSSLSocketFactory(input, "WhoAmI#2020"));
                } catch (MqttSecurityException e) {
                    e.printStackTrace();
                }
                options.setUserName("csilab");
                    options.setPassword("WhoAmI#2020".toCharArray());
                    //options.setMqttVersion(3);
                    options.setCleanSession(true);
                    //Properties props = new Properties();
                    //options.setSSLProperties(props);


                try {
                    IMqttToken token = client.connect(options);
                    token.setActionCallback(new IMqttActionListener() {
                        @Override
                        public void onSuccess(IMqttToken asyncActionToken) {
                            // We are connected
                            System.out.println("onSuccess");

                            byte[] encodedPayload = new byte[0];

                            try {
                                encodedPayload = payload.getBytes("UTF-8");
                            } catch (UnsupportedEncodingException e) {
                                e.printStackTrace();
                            }
                            MqttMessage message = new MqttMessage(encodedPayload);
                            try {
                                client.publish(topic, message);
                            } catch (MqttException e) {
                                e.printStackTrace();
                            }
                            try {
                                client.disconnect();
                            } catch (MqttException e) {
                                e.printStackTrace();
                            }
                        }

                        @Override
                        public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                            // Something went wrong e.g. connection timeout or firewall problems
                            System.out.println("onFailure");

                        }
                    });
                } catch (MqttException e) {
                    e.printStackTrace();
                }
            }
        });
    }
}