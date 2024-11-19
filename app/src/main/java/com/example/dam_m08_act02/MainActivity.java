
/**
 * Nombre del Proyecto: Aplicación de Gestión de Login
 * Archivo: MainActivity.java
 * Creado por: Iuliia Shevchenko
 * Fecha de creación: el 6 de noviembre de 2024
 * Propósito: Esta clase maneja la actividad principal del inicio de sesión de la aplicación,
 * donde el usuario ingresa sus credenciales y se validan con un servidor externo.
 * También se registra cualquier intento fallido en la base de datos local SQLite.
 */
package com.example.dam_m08_act02;

import android.annotation.SuppressLint;
import android.content.ContentValues;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

/**
 * MainActivity
 * Actividad principal donde el usuario realiza el inicio de sesión.
 * Clase para gestionar el login y verificar credenciales.
 */
public class MainActivity extends AppCompatActivity {

    // Variables de interfaz de usuario para los campos de entrada de nombre de usuario y contraseña
    private EditText loginUsername, loginPassword;
    private Button loginButton;

    // Variables para la gestión de la base de datos SQLite
    private DatabaseHelper dbHelper;
    private SQLiteDatabase db;

    /**
     * Método llamado cuando la actividad se crea por primera vez.
     * Se encarga de inicializar las vistas, la base de datos y configurar los eventos de la interfaz de usuario.
     *
     * @param savedInstanceState Estado previamente guardado de la actividad, si existe.
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // Inicializa la base de datos interna
        dbHelper = new DatabaseHelper(this);
        db = dbHelper.getWritableDatabase();

        // Conecta los elementos de la interfaz de usuario definidos en el archivo XML con las variables Java
        loginUsername = findViewById(R.id.login_user);
        loginPassword = findViewById(R.id.login_password);
        loginButton = findViewById(R.id.login_button);

        // Configura la acción del botón de inicio de sesión para validar la entrada del usuario
        loginButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (validarCampos()) { // Solo procede si los campos son válidos
                    validarUsuario();
                }
            }
        });
        // Mostrar el mensaje de aviso si existe
        Intent intent = getIntent();
        if (intent != null && intent.hasExtra("show_message")) {
            String message = intent.getStringExtra("show_message");
            if (message != null) {
                Toast.makeText(MainActivity.this, message, Toast.LENGTH_LONG).show();
            }
        }
    }

    /**
     * Método para validar los campos de entrada de usuario y contraseña.
     * Realiza diversas validaciones:
     * - No deben estar vacíos.
     * - El nombre de usuario solo puede contener letras y números.
     * - La contraseña debe tener entre 4 y 8 caracteres.
     *
     * @return true si los campos cumplen todas las condiciones; false en caso contrario.
     */
    private boolean validarCampos() {
        String username = loginUsername.getText().toString().trim();
        String password = loginPassword.getText().toString().trim();

        // Validar si los campos están vacíos
        if (username.isEmpty() || password.isEmpty()) {
            Toast.makeText(MainActivity.this, "Los campos no pueden estar vacíos", Toast.LENGTH_SHORT).show();
            return false;
        }

        // Validar que el nombre de usuario solo contiene letras y números
        if (!username.matches("[a-zA-Z0-9]+")) {
            Toast.makeText(MainActivity.this, "El nombre de usuario solo puede contener letras y números", Toast.LENGTH_SHORT).show();
            return false;
        }

        // Validar la longitud de la contraseña (debe estar entre 4 y 8 caracteres)
        if (password.length() < 4 || password.length() > 8) {
            Toast.makeText(MainActivity.this, "La contraseña debe tener entre 4 y 8 caracteres", Toast.LENGTH_SHORT).show();
            return false;
        }

        return true; // Si todas las validaciones se cumplen
    }

    /**
     * Método para validar el usuario mediante una solicitud a un servidor externo.
     * Utiliza una solicitud HTTP POST para enviar el nombre de usuario y la contraseña.
     * Este método se ejecuta en segundo plano para evitar bloquear la interfaz de usuario.
     */
    @SuppressLint("StaticFieldLeak")
    private void validarUsuario() {
        final String usernameIs = loginUsername.getText().toString().trim();
        final String passwordIs = loginPassword.getText().toString().trim();

        new AsyncTask<Void, Void, String>() {
            @Override
            protected String doInBackground(Void... voids) {
                try {
                    // URL del servidor al que se hará la petición
                    URL urlIs = new URL("http://192.168.1.136/loginApp/validacuenta.php");
                    HttpURLConnection conexionIs = (HttpURLConnection) urlIs.openConnection();
                    conexionIs.setRequestMethod("POST");
                    conexionIs.setRequestProperty("Content-Type", "application/json; utf-8");
                    conexionIs.setRequestProperty("Accept", "application/json");
                    conexionIs.setDoOutput(true);

                    // Crear el JSON con el nombre de usuario y contraseña
                    JSONObject jsonBody = new JSONObject();
                    jsonBody.put("Usuario", usernameIs);
                    jsonBody.put("Contraseña", passwordIs);
                    Log.d("ValidacionUsuario", jsonBody.toString());

                    // Enviar la petición al servidor
                    try (OutputStream os = conexionIs.getOutputStream()) {
                        byte[] input = jsonBody.toString().getBytes("utf-8");
                        os.write(input, 0, input.length);
                    }

                    // Leer la respuesta del servidor
                    BufferedReader br = new BufferedReader(new InputStreamReader(conexionIs.getInputStream(), "utf-8"));
                    StringBuilder responseIs = new StringBuilder();
                    String responseLine;
                    while ((responseLine = br.readLine()) != null) {
                        responseIs.append(responseLine.trim());
                    }

                    // Imprimir la respuesta del servidor en el log para depuración
                    Log.d("ServerResponse", responseIs.toString());

                    return responseIs.toString(); // Retornar la respuesta del servidor
                } catch (Exception e) {
                    // Captura cualquier excepción relacionada con la conexión y registra el error
                    Log.e("ConexionError", "Error en la conexión con el servidor: " + e.getMessage(), e);
                    return null; // Retornar null en caso de error
                }
            }

            @Override
            protected void onPostExecute(String resultIs) {
                // Si resultIs es null, significa que ocurrió un error de conexión
                if (resultIs == null) {
                    // Mostrar mensaje de error de conexión
                    Toast.makeText(MainActivity.this, "Error de conexión con el servidor. Por favor, inténtelo más tarde.", Toast.LENGTH_LONG).show();
                    return; // Salir del método
                }

                // Manejar la respuesta del servidor
                try {
                    JSONObject jsonResponse = new JSONObject(resultIs);
                    String status = jsonResponse.optString("status", "error");

                    if (status.equals("valido")) {
                        // Usuario validado exitosamente
                        Toast.makeText(MainActivity.this, "Validación exitosa", Toast.LENGTH_SHORT).show();
                        // Redirigir al usuario a SuccessfulLoginActivity
                        Intent intent = new Intent(MainActivity.this, SuccessfulLoginActivity.class);
                        startActivity(intent);
                    } else if (status.equals("invalido")) {
                        // Guardar intento fallido en la base de datos
                        saveFailedAttempt(usernameIs, passwordIs);
                        Toast.makeText(MainActivity.this, "Usuario inválido. Intento fallido registrado.", Toast.LENGTH_SHORT).show();
                        // Redirigir a FailedLoginActivity
                        Intent intent = new Intent(MainActivity.this, FailedLoginActivity.class);
                        startActivity(intent);
                    }
                } catch (Exception e) {
                    // Manejar errores de procesamiento de la respuesta del servidor
                    Log.e("ValidacionUsuario", "Error al procesar la respuesta del servidor: " + e.getMessage(), e);
                    Toast.makeText(MainActivity.this, "Error al procesar la respuesta del servidor.", Toast.LENGTH_SHORT).show();
                }
            }
        }.execute();
    }


    /**
     * Método para guardar un intento fallido de inicio de sesión en la base de datos SQLite interna.
     * Los datos almacenados incluyen:
     * - Nombre de usuario
     * - Contraseña
     * - Fecha y hora del intento
     *
     * @param username Nombre de usuario introducido.
     * @param password Contraseña introducida.
     */
    private void saveFailedAttempt(String username, String password) {
        // Crear un objeto ContentValues para almacenar los datos
        ContentValues values = new ContentValues();
        values.put("usuario", username);
        values.put("contrasena", password);
        values.put("fecha_hora", System.currentTimeMillis()); // Almacena el timestamp actual

        // Insertar los valores en la base de datos SQLite
        long result = db.insert("intentos_fallidos", null, values);
        if (result == -1) {
            // Imprimir un mensaje de error en el log si la inserción falla
            Log.e("MainActivity", "Error al insertar el intento fallido en la base de datos");
        } else {
            // Imprimir un mensaje de éxito en el log si la inserción fue correcta
            Log.d("MainActivity", "Intento fallido guardado correctamente");
        }
    }
}
