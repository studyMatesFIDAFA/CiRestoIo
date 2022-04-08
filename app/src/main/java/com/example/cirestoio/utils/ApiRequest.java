package com.example.cirestoio.utils;

import android.os.AsyncTask;

import com.example.cirestoio.activity.MainActivity;
import com.example.cirestoio.model.Valuta;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Arrays;

public class ApiRequest extends AsyncTask<String, Void, String> {

    final String[] supportedCurrencies = {"Dollaro USA","Sterlina Gran Bretagna","Yen Giapponese","Renminbi(Yuan)","Rupia Indiana","Franco Svizzero"};

    @Override
    protected String doInBackground(String... url) {

        URL paginaURL;
        InputStream risposta = null;
        try {
            paginaURL = new URL(url[0]);
            HttpURLConnection client = (HttpURLConnection) paginaURL.openConnection();
            client.setRequestMethod("GET");
            client.setRequestProperty("Accept", "application/json");
            //int responseCode = client.getResponseCode();
            risposta = new BufferedInputStream(client.getInputStream());
        }catch (Exception e) {
            e.printStackTrace();
        }

         loadRates(risposta);
        return "";
    }

    private void loadRates(InputStream in) {
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(in))) {

            JSONObject object = new JSONObject(reader.readLine());
            JSONArray array=object.getJSONArray("latestRates");
            double eurRate;

            for(int i=0;i<array.length();i++)
            {
                String currency=array.getJSONObject(i).getString("currency");
                String nazione = array.getJSONObject(i).getString("country");
                // EurRate : quantita di valuta estera per un euro
                // es ValDollaro = valEuro * eurRate
                if(!(array.getJSONObject(i).getString("eurRate").equals("N.D.")))
                    eurRate = array.getJSONObject(i).getDouble("eurRate");
                else
                    eurRate = 0.0;
                if (Arrays.asList(supportedCurrencies).contains(currency)){
                    MainActivity.countryRates.add(new Valuta(nazione,currency,eurRate));

                }
            }
        } catch (IOException | JSONException e) {
            e.printStackTrace();
        }
        return;
    }



}
