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
            int responseCode = client.getResponseCode();
            //System.out.println("GET Response Code :: " + responseCode);
            //System.out.println(client.getRequestProperty("Accept"));
            risposta = new BufferedInputStream(client.getInputStream());
        }catch (Exception e) {
            e.printStackTrace();
        }

         loadRates(risposta);
        return "";
    }

    private void loadRates(InputStream in) {
        String[] rates=null; //risultati
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(in))) {
            /*while((s=reader.readLine())!=null)
            {
                sb.append(s);
                System.out.println("Stringa s "+s);
            }*/
            JSONObject object = new JSONObject(reader.readLine());
            JSONArray array=object.getJSONArray("latestRates");
            rates=new String[array.length()];
            Double eurRate;

            for(int i=0;i<array.length();i++)
            {
                String currency=array.getJSONObject(i).getString("currency");
                String nazione = array.getJSONObject(i).getString("country");
                //System.out.println(i+" "+currency);
                // EurRate : quantita di valuta estera per un euro
                // es ValDollaro = valEuro * eurRate
                if(!(array.getJSONObject(i).getString("eurRate").equals("N.D.")))
                    eurRate = array.getJSONObject(i).getDouble("eurRate");
                else
                    eurRate = 0.0;
                if (Arrays.asList(supportedCurrencies).contains(currency)){
                    MainActivity.countryRates.add(new Valuta(nazione,currency,eurRate));
                    //rates[i]=""+currency + " --> "+eurRate;

                }
            }
        } catch (IOException | JSONException e) {
            System.out.println("FINE1");
            e.printStackTrace();
        }
        //System.out.println("FINE");
        return;
    }



}
