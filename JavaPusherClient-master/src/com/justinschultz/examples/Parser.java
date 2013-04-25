/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

package com.justinschultz.examples;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.util.Arrays;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Map;
import java.util.StringTokenizer;

public class Parser {
    
    public static void main(final String[] args) throws Exception {
        final BufferedReader reader = new BufferedReader(new FileReader(new File("/tmp/chat.txt")));
        final Map<String, Integer> wordCloud = new HashMap<String, Integer>();
        while(reader.ready()){
            final String[] parts = reader.readLine().split("\t");
            if(parts.length != 5){
                return;
            }
            
            final Line line = new Line(parts);
            for(final String word : line.getMessage().split(" ")){
                //System.out.println(word);
                if(wordCloud.containsKey(word) == false){
                    wordCloud.put(word,1);
                }else{
                    wordCloud.put(word,wordCloud.get(word)+1);
                }
            }
        }
        
        final String[] keys = wordCloud.keySet().toArray(new String[]{});
        Arrays.sort(keys, new Comparator(){

            @Override
            public int compare(Object o1, Object o2) {
                int one = wordCloud.get((String)o1);
                int two = wordCloud.get((String)o2);
                if(one > two){
                    return -1;
                }else if(two > one){
                    return 1;
                }else{
                    return 0;
                }
            }
            
        });
        
        for(int a = 0; a < 25; a++){
            System.out.println(keys[a]+" => "+wordCloud.get(keys[a]));
        }
    }
    
    static class Line {
        private final String id;
        private final String username;
        private final String message;
        private final String messageId;
        private final String date;
        
        
        public Line(final String[] parts){
            if(parts.length != 5){
                throw new ArrayIndexOutOfBoundsException("Not enough elements in the parts arrray.");
            }
            id = parts[0];
            username = parts[1];
            message = parts[2];
            messageId = parts[3];
            date = parts[4];
        }

        public String getId() {
            return id;
        }

        public String getUsername() {
            return username;
        }

        public String getMessage() {
            return message;
        }

        public String getMessageId() {
            return messageId;
        }

        public String getDate() {
            return date;
        }
        
    }
}
