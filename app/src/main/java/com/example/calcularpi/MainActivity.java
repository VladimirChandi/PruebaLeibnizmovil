package com.example.calcularpi;

import android.os.AsyncTask;
import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;

import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity {

    private EditText termsInput;
    private Button startButton;
    private TextView piValue, seriesValue;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        termsInput = findViewById(R.id.termsInput);
        startButton = findViewById(R.id.startButton);
        piValue = findViewById(R.id.piValue);
        seriesValue = findViewById(R.id.seriesValue);

        startButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String terms = termsInput.getText().toString();
                new CalculatePiTask().execute(terms);
            }
        });
    }

    private class CalculatePiTask extends AsyncTask<String, Void, JSONObject> {
        @Override
        protected JSONObject doInBackground(String... strings) {
            try {
                String terms = strings[0];
                URL url = new URL("http://10.10.30.205:3000/calculatePi/" + terms);
                HttpURLConnection connection = (HttpURLConnection) url.openConnection();
                connection.setRequestMethod("GET");

                BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
                StringBuilder response = new StringBuilder();
                String line;
                while ((line = reader.readLine()) != null) {
                    response.append(line);
                }
                reader.close();

                return new JSONObject(response.toString());
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(JSONObject result) {
            super.onPostExecute(result);
            if (result != null) {
                try {
                    double pi = result.getDouble("pi");
                    String series = result.getString("series");

                    piValue.setText("Valor aproximado de pi: " + pi);
                    seriesValue.setText("Serie de Leibniz: " + series);
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else {
                // Manejar el caso en que result es nulo
                piValue.setText("Error: No se pudo obtener el valor de pi");
                seriesValue.setText("Error: No se pudo obtener la serie de Leibniz");
            }
        }

    }
}
