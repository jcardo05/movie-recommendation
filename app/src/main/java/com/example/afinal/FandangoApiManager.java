package com.example.afinal;

import java.io.ByteArrayOutputStream;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import android.os.AsyncTask;

public class FandangoApiManager extends AsyncTask<String, Integer, String> {

    public FandangoApiResultDelgate delegate = null;

    protected String sha256Encode(String StringToEncode) throws NoSuchAlgorithmException {

        MessageDigest digest = MessageDigest.getInstance("SHA-256");
        byte[] buffUtf8Msg = digest.digest(StringToEncode.getBytes());
        String result = String.format("%0" + (buffUtf8Msg.length*2) + "X", new BigInteger(1, buffUtf8Msg));

        return result;
    }

    protected String buildAuthorizationParameters(String apiKey, String sharedSecret) throws NoSuchAlgorithmException {

        long seconds =  System.currentTimeMillis() / 1000;
        String paramsToEncode = apiKey + sharedSecret + seconds;
        String encodedParams = sha256Encode(paramsToEncode);

        String result = String.format("api_key=%s&sig=%s", apiKey, encodedParams);

        return result;
    }

    protected String getResponse(String parameters) {

        String baseUri = "http://api.fandango.com";
        String apiVersion = "1";

        // Use your account-specific values here
        String apiKey = "6svxefmy9ae8ewurqygdckkf";
        String sharedSecret = "W6r6QQ6FzK";

        String result = null;

        try {

            String authorizationParameters = buildAuthorizationParameters(apiKey, sharedSecret);
            String requestUri = String.format("%s/v%s/?%s&%s", baseUri, apiVersion, parameters, authorizationParameters);

            HttpClient httpclient = new DefaultHttpClient();
            HttpResponse response = httpclient.execute(new HttpGet(requestUri));
            ByteArrayOutputStream out = new ByteArrayOutputStream();
            response.getEntity().writeTo(out);
            out.close();
            result = out.toString();

        } catch (Exception e) {

            e.printStackTrace();
        }

        return result;
    }

    @Override
    protected String doInBackground(String... arg0) {

        return getResponse(arg0[0]);
    }

    @Override
    protected void onPostExecute(String result) {

        if (delegate != null) {

            delegate.gotResult(result);

        } else {

            System.out.println("Got a result, but sadly no one to give it to");
        }
    }
}