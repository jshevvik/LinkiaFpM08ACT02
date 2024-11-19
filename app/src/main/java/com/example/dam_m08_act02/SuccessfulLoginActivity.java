package com.example.dam_m08_act02;

import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Locale;

/**
 * SuccessfulLoginActivity es la actividad que se muestra cuando el usuario pasa la validación.
 * Muestra una lista de usuarios desde una fuente externa.
 */
public class SuccessfulLoginActivity extends AppCompatActivity {

    private ListView usersListView; // ListView para mostrar la lista de usuarios
    private ArrayList<String> usersList; // Lista que contiene la información de los usuarios
    private ArrayAdapter<String> adapter; // Adaptador para conectar la lista de usuarios con el ListView

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_succesful_login);

        // Enlazar las vistas del XML con los objetos del código
        usersListView = findViewById(R.id.listView_users);
        Button backButton = findViewById(R.id.button_back_to_login);

        // Inicializar la lista de usuarios y configurar el adaptador
        usersList = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, usersList);
        usersListView.setAdapter(adapter);

        // Configurar el botón para volver a la pantalla de login
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Crear un intent para ir a la pantalla de inicio de sesión
                Intent intent = new Intent(SuccessfulLoginActivity.this, MainActivity.class);
                // Añadir un extra para indicar que se debe mostrar un mensaje de aviso
                intent.putExtra("show_message", "¡Bienvenido de nuevo desde la pantalla de validación exitosa!");
                startActivity(intent);
                // Finalizar la actividad actual para evitar que el usuario regrese presionando el botón atrás
                finish();
            }
        });

        // Llamar al método para obtener la lista de usuarios desde el servidor externo
        obtenerUsuarios();
    }

    /**
     * Método para obtener la lista de usuarios desde el servidor.
     * Realiza una petición HTTP GET a un servidor externo para obtener los datos.
     */
    private void obtenerUsuarios() {
        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... voids) {
                try {
                    // URL del servidor al que se hará la petición
                    URL url = new URL("http://192.168.1.136/loginApp/consultausuarios.php");
                    // Establecer la conexión HTTP
                    HttpURLConnection conexion = (HttpURLConnection) url.openConnection();
                    conexion.setRequestMethod("GET"); // Usar el método GET para obtener datos
                    conexion.setRequestProperty("Accept", "application/json"); // Esperar respuesta en formato JSON

                    // Leer la respuesta del servidor
                    BufferedReader br = new BufferedReader(new InputStreamReader(conexion.getInputStream(), "utf-8"));
                    StringBuilder response = new StringBuilder();
                    String responseLine;
                    while ((responseLine = br.readLine()) != null) {
                        response.append(responseLine.trim()); // Agregar cada línea de respuesta
                    }
                    return response.toString(); // Retornar la respuesta del servidor como un string
                } catch (Exception e) {
                    e.printStackTrace();
                    return null; // En caso de error, retornar null
                }
            }

            @Override
            protected void onPostExecute(String result) {
                // Este método se ejecuta una vez que doInBackground() ha terminado y se ejecuta en el hilo principal
                if (result != null) {
                    try {
                        // Parsear la respuesta JSON
                        JSONObject jsonResponse = new JSONObject(result);
                        String status = jsonResponse.optString("status", "error");

                        if ("success".equals(status)) {
                            // Obtener el array de usuarios del objeto JSON
                            JSONArray usuariosArray = jsonResponse.getJSONArray("data");
                            for (int i = 0; i < usuariosArray.length(); i++) {
                                JSONObject usuario = usuariosArray.getJSONObject(i);
                                String nombre = usuario.getString("usuario");
                                String contrasena = usuario.getString("contrasena");
                                String fechaNacimiento = usuario.getString("fecha_nacimiento");

                                // Formatear la fecha de nacimiento de "yyyy-MM-dd" a "dd-MM-yyyy"
                                try {
                                    SimpleDateFormat inputFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                                    SimpleDateFormat outputFormat = new SimpleDateFormat("dd-MM-yyyy", Locale.getDefault());
                                    fechaNacimiento = outputFormat.format(inputFormat.parse(fechaNacimiento));
                                } catch (Exception e) {
                                    Log.e("SuccessfulLoginActivity", "Error al formatear la fecha de nacimiento", e);
                                }

                                // Formatear los datos del usuario en una cadena
                                String entry = "Usuario: " + nombre + ", Contraseña: " + contrasena + ", Fecha de nacimiento: " + fechaNacimiento;
                                // Agregar la entrada formateada a la lista de usuarios
                                usersList.add(entry);

                                // Añadir Log.d para verificar cada entrada agregada a la lista
                                Log.d("SuccessfulLoginActivity", "Usuario agregado: " + entry);
                            }
                            // Notificar al adaptador que los datos han cambiado para actualizar el ListView
                            adapter.notifyDataSetChanged();

                            // Añadir Log.d para verificar el tamaño final de la lista de usuarios
                            Log.d("SuccessfulLoginActivity", "Número total de usuarios agregados: " + usersList.size());
                        } else {
                            // Mostrar un mensaje si no se encontraron usuarios
                            Toast.makeText(SuccessfulLoginActivity.this, "No se encontraron usuarios", Toast.LENGTH_SHORT).show();
                        }
                    } catch (Exception e) {
                        Log.e("SuccessfulLoginActivity", "Error al procesar la respuesta del servidor", e);
                    }
                } else {
                    // Mostrar un mensaje si hubo un error de conexión con el servidor
                    Toast.makeText(SuccessfulLoginActivity.this, "Error de conexión", Toast.LENGTH_SHORT).show();
                }
            }

        }.execute();
    }
}
