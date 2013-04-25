package com.justinschultz.examples;

/*	
 *  Copyright (C) 2012 Justin Schultz
 *  JavaPusherClient, a Pusher (http://pusherapp.com) client for Java
 *  
 *  http://justinschultz.com/
 *  http://publicstaticdroidmain.com/
 * 
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *  
 *  	http://www.apache.org/licenses/LICENSE-2.0
 *  
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License. 
 */
import org.json.JSONObject;

import com.justinschultz.pusherclient.ChannelListener;
import com.justinschultz.pusherclient.Pusher;
import com.justinschultz.pusherclient.PusherListener;
import com.justinschultz.pusherclient.Pusher.Channel;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import org.json.JSONException;

public class PusherTest {

    private static final String PUSHER_API_KEY = "4e0ebd7a8b66fa3554a4";
    private static final String PUSHER_CHANNEL = "chat_en";
    private static Pusher pusher;
    
    private static boolean needsRestarting = true;

    public static void main(String[] args) throws IOException {
        final BufferedWriter writer = new BufferedWriter(new FileWriter(new File("/tmp/chat.txt")));
        PusherListener eventListener = new PusherListener() {
            Channel channel;

            @Override
            public void onConnect(String socketId) {
                System.out.println("Pusher connected. Socket Id is: " + socketId);
                channel = pusher.subscribe(PUSHER_CHANNEL);
                System.out.println("Subscribed to channel: " + channel);
                channel.send("client-event-test", new JSONObject());

                channel.bind("price-updated", new ChannelListener() {
                    @Override
                    public void onMessage(String message) {
                        System.out.println("Received bound channel message: " + message);
                    }
                });
            }

            @Override
            public void onMessage(String message) {
                System.out.println("Received message from Pusher: " + message);
                try {
                    JSONObject obj = new JSONObject(message);
                    final String data = new JSONObject(obj.get("data")).get("bytes").toString();
                    final String dataObj = new JSONObject(obj.toString().replace("\\\\", "")).get("data").toString();

                    final JSONObject finalObject = new JSONObject(dataObj.substring(1, dataObj.length() - 1));
                    
                    writer.write(finalObject.get("uid")+"\t"+finalObject.get("login")+"\t"+finalObject.get("msg")+"\t"+finalObject.get("msg_id")+"\t"+finalObject.get("date")+"\n");
                    writer.flush();
                    //System.out.println(finalObject.get("login"));
                } catch (JSONException e) {
                    e.printStackTrace();
                } catch(IOException e){
                    e.printStackTrace();
                }
            }

            @Override
            public void onDisconnect() {
                System.out.println("Pusher disconnected.");
                needsRestarting = true;
            }
        };

        while(true){
            if(needsRestarting == true){
                pusher = new Pusher(PUSHER_API_KEY);
                pusher.setPusherListener(eventListener);
                pusher.connect();
                
                needsRestarting = false;
            }else{
                try { Thread.sleep(60000); }catch(Exception e){}
            }
        }
        
    }
}
