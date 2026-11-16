package com.example.iapapp;
import android.widget.Toast;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.HashMap;
import java.util.Map;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class DatosActivity extends AppCompatActivity {

    private Spinner spinnerGenotipo, spinnerVyR, spinnerIM, spinnerIF;
    private EditText editTextDH, editTextDP;
    private TextView textViewPrediccion;
    private Button btnEnviar;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_datos);

        spinnerGenotipo = findViewById(R.id.spinnerGenotipo);
        editTextDH = findViewById(R.id.editTextDH);
        editTextDP = findViewById(R.id.editTextDP);
        spinnerVyR = findViewById(R.id.spinnerVyR);
        spinnerIM = findViewById(R.id.spinnerIM);
        spinnerIF = findViewById(R.id.spinnerIF);
        textViewPrediccion = findViewById(R.id.textViewPrediccion);
        btnEnviar = findViewById(R.id.btnEnviar);

        setupSpinners();

        btnEnviar.setOnClickListener(v -> enviarDatos());
    }

    private void setupSpinners() {
        ArrayAdapter<CharSequence> adapterGenotipo = ArrayAdapter.createFromResource(this, R.array.genotipo_options, android.R.layout.simple_spinner_item);
        adapterGenotipo.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerGenotipo.setAdapter(adapterGenotipo);

        ArrayAdapter<CharSequence> adapterVyR = ArrayAdapter.createFromResource(this, R.array.vyr_options, android.R.layout.simple_spinner_item);
        adapterVyR.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerVyR.setAdapter(adapterVyR);

        ArrayAdapter<CharSequence> adapterIM = ArrayAdapter.createFromResource(this, R.array.im_options, android.R.layout.simple_spinner_item);
        adapterIM.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerIM.setAdapter(adapterIM);

        ArrayAdapter<CharSequence> adapterIF = ArrayAdapter.createFromResource(this, R.array.if_options, android.R.layout.simple_spinner_item);
        adapterIF.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerIF.setAdapter(adapterIF);
    }

    private void enviarDatos() {
        String genotipo = spinnerGenotipo.getSelectedItem().toString();
        String dh = editTextDH.getText().toString();
        String dp = editTextDP.getText().toString();
        String vyr = spinnerVyR.getSelectedItem().toString();
        String im = spinnerIM.getSelectedItem().toString();
        String inf = spinnerIF.getSelectedItem().toString();

        // Mapeo de valores de los spinners
        Map<String, Integer> mappingGenotipo = new HashMap<>();
        mappingGenotipo.put("ADVANTA 9789", 0);
        mappingGenotipo.put("INIAP 543-QPM", 1);
        mappingGenotipo.put("INIAP H-551", 2);
        mappingGenotipo.put("INIAP H-554", 3);

        Map<String, Integer> mappingVyR = new HashMap<>();
        mappingVyR.put("R1", 0);
        mappingVyR.put("R2", 1);
        mappingVyR.put("V1", 2);

        Map<String, Integer> mappingIM = new HashMap<>();
        mappingIM.put("Pobre", 1);
        mappingIM.put("Promedio", 2);
        mappingIM.put("Bueno", 3);

        Map<String, Integer> mappingIF = new HashMap<>();
        mappingIF.put("Baja", 1);
        mappingIF.put("Media", 2);
        mappingIF.put("Alta", 3);

        // Convertir valores a números
        int genotipoValue = mappingGenotipo.getOrDefault(genotipo, 0);
        int vyrValue = mappingVyR.getOrDefault(vyr, 0);
        int imValue = mappingIM.getOrDefault(im, 0);
        int infValue = mappingIF.getOrDefault(inf, 0);

        // Mostrar valores para depuración
        String resultado = "Genotipo: " + genotipoValue + "\n" +
                "DH: " + dh + "\n" +
                "DP: " + dp + "\n" +
                "VyR: " + vyrValue + "\n" +
                "IM: " + imValue + "\n" +
                "IF: " + infValue;
        textViewPrediccion.setText(resultado);

        // Obtener los valores ingresados
        int genotipor = genotipoValue;
        String dhr = dh;
        String dpr = dp;
        int vyrr = vyrValue;
        int imr = imValue;
        int iffr = infValue;

        // Crear el JSON con los datos
        JSONObject postData = new JSONObject();
        try {
            postData.put("Genotipo", genotipor);
            postData.put("DH", dhr);
            postData.put("DP", dpr);
            postData.put("VyR", vyrr);
            postData.put("IM", imr);
            postData.put("IF", iffr);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        // URL de la API en PythonAnywhere (CÁMBIALA POR LA TUYA)
        String apiUrl = "https://deividii.pythonanywhere.com/predict";

        // Llamar a la tarea asincrónica
        new EnviarDatosTask(apiUrl, postData.toString()).execute();
    }

    private class EnviarDatosTask extends AsyncTask<Void, Void, String> {
        private String apiUrl;
        private String postData;

        public EnviarDatosTask(String apiUrl, String postData) {
            this.apiUrl = apiUrl;
            this.postData = postData;
        }

        @Override
        protected String doInBackground(Void... voids) {
            try {
                // Realizamos la conexión con la API
                URL url = new URL(apiUrl);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("POST");
                conn.setRequestProperty("Content-Type", "application/json");
                conn.setDoOutput(true);

                // Enviar datos a la API
                OutputStream os = conn.getOutputStream();
                os.write(postData.getBytes());
                os.flush();
                os.close();

                int responseCode = conn.getResponseCode();

                // Leer la respuesta de la API
                if (responseCode == HttpURLConnection.HTTP_OK) {
                    InputStreamReader reader = new InputStreamReader(conn.getInputStream());
                    BufferedReader in = new BufferedReader(reader);
                    String inputLine;
                    StringBuilder response = new StringBuilder();

                    while ((inputLine = in.readLine()) != null) {
                        response.append(inputLine);
                    }
                    in.close();

                    // Parsear la respuesta JSON para obtener la predicción
                    JSONObject jsonResponse = new JSONObject(response.toString());
                    int prediccion = jsonResponse.getInt("prediccion");

                    return "Los órganos sexuales aparecerán a los " + prediccion + " días después de la siembra";
                } else {
                    return "Error al enviar datos: " + responseCode;
                }

            } catch (Exception e) {
                e.printStackTrace();
                return "Error en la conexión";
            }
        }

        @Override
        protected void onPostExecute(String result) {


        }
    }
    }


