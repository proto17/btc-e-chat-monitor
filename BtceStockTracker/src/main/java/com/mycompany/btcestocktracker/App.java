package com.mycompany.btcestocktracker;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.security.SecureRandom;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;
import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.SingleClientConnManager;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.protocol.HTTP;

public class App {

    public static void main(String[] args) throws Exception {
        final SSLContext sslContext = SSLContext.getInstance("SSL");

        sslContext.init(null, new TrustManager[]{new X509TrustManager() {
                public X509Certificate[] getAcceptedIssuers() {
                    return null;
                }

                public void checkClientTrusted(final X509Certificate[] certs, final String authType) {
                }

                public void checkServerTrusted(final X509Certificate[] certs, final String authType) {
                }
            }}, new SecureRandom());

        final SSLSocketFactory sf = new SSLSocketFactory(sslContext);
        final Scheme httpsScheme = new Scheme("https", 443, sf);
        final SchemeRegistry schemeRegistry = new SchemeRegistry();
        schemeRegistry.register(httpsScheme);

        final ClientConnectionManager cm = new SingleClientConnManager(schemeRegistry);
        final HttpClient httpClient = new DefaultHttpClient(cm);
        final HttpPost post = new HttpPost("https://btc-e.com/ajax/order_.php");
        final List<NameValuePair> nvp = new ArrayList<NameValuePair>();
        nvp.add(new BasicNameValuePair("act", "orders_update"));
        nvp.add(new BasicNameValuePair("pair", "14"));

        post.setEntity(new UrlEncodedFormEntity(nvp, HTTP.UTF_8));
        
        final BufferedWriter writer = new BufferedWriter(new FileWriter(new File("c:\\users\\main-local\\prices.txt")));
        
        while(true){
            final HttpResponse response = httpClient.execute(post);

            final BufferedReader reader = new BufferedReader(new InputStreamReader(response.getEntity().getContent()));

            String data;
            while ((data = reader.readLine()) != null) {
                System.out.println(data);
                writer.write(data);
            }
            
            writer.flush();
            
            Thread.sleep(1000 * 60 * 30);
        }
    }
}
