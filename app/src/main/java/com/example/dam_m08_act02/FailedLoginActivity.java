package com.example.dam_m08_act02;

import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.icu.text.SimpleDateFormat;
import android.os.Bundle;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import androidx.appcompat.app.AppCompatActivity;

import java.sql.Date;
import java.util.ArrayList;
import java.util.Locale;

import android.content.Intent;
import android.view.View;
import android.widget.Button;

/**
 * FailedLoginActivity muestra una lista de intentos fallidos de inicio de sesión guardados en la base de datos interna.
 * Además, permite al usuario volver a la pantalla de inicio de sesión.
 */
public class FailedLoginActivity extends AppCompatActivity {

    private ListView failedAttemptsListView;  // ListView para mostrar los intentos fallidos
    private ArrayList<String> failedAttemptsList;  // Lista que contiene la información de los intentos fallidos
    private ArrayAdapter<String> adapter;  // Adaptador que conecta la lista de intentos fallidos con el ListView
    private SQLiteDatabase db;  // Instancia de la base de datos SQLite
    private Button backButton;  // Botón para regresar a la pantalla principal

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_failed_login);

        // Enlazar las vistas del XML con los elementos de la interfaz de usuario
        failedAttemptsListView = findViewById(R.id.listView_failed_attempts);
        backButton = findViewById(R.id.button_back_to_main);

        // Inicializar la lista de intentos fallidos y configurar el adaptador
        failedAttemptsList = new ArrayList<>();
        adapter = new ArrayAdapter<>(this, android.R.layout.simple_list_item_1, failedAttemptsList);
        failedAttemptsListView.setAdapter(adapter);

        // Inicializar la base de datos SQLite para lectura
        DatabaseHelper dbHelper = new DatabaseHelper(this);
        db = dbHelper.getReadableDatabase();

        // Configurar el botón para volver a la pantalla principal de login
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                // Crear un intent para ir a la MainActivity (pantalla principal de inicio de sesión)
                Intent intent = new Intent(FailedLoginActivity.this, MainActivity.class);
                // Añadir un extra para indicar que se debe mostrar un mensaje de aviso
                intent.putExtra("show_message", "¡Bienvenido de nuevo desde la pantalla de intentos fallidos!");
                startActivity(intent);
                // Finalizar la actividad actual para evitar que el usuario pueda regresar a esta actividad usando el botón atrás
                finish();
            }
        });

        // Llamar al método para cargar los intentos fallidos desde la base de datos SQLite
        loadFailedAttempts();
    }

    /**
     * Método para cargar los intentos fallidos desde la base de datos SQLite.
     * Obtiene los intentos fallidos de la tabla intentos_fallidos y los muestra en el ListView.
     */
    private void loadFailedAttempts() {
        // Consultar todos los registros de la tabla intentos_fallidos
        Cursor cursor = db.query("intentos_fallidos", null, null, null, null, null, null);

        // Código de depuración para imprimir los nombres de las columnas en el registro
        String[] columnNames = cursor.getColumnNames();
        for (String columnName : columnNames) {
            Log.d("ColumnName", columnName);
        }

        // Procesar los resultados de la consulta
        if (cursor.moveToFirst()) {
            do {
                // Obtener los datos del intento fallido
                String username = cursor.getString(cursor.getColumnIndex("usuario"));
                String password = cursor.getString(cursor.getColumnIndex("contrasena"));
                String dateTime = cursor.getString(cursor.getColumnIndex("fecha_hora"));

                // Convertir el timestamp almacenado en la base de datos a un formato de fecha legible
                try {
                    long timestamp = Long.parseLong(dateTime);
                    Date date = new Date(timestamp);
                    SimpleDateFormat formatter = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss", Locale.getDefault());
                    String formattedDate = formatter.format(date);

                    // Agregar el intento fallido a la lista con los datos formateados
                    failedAttemptsList.add("Usuario: " + username + ", Contraseña: " + password + ", Fecha: " + formattedDate);
                } catch (NumberFormatException e) {
                    // Manejar el error de conversión del timestamp a una fecha legible
                    Log.e("FailedLoginActivity", "Error al convertir fecha_hora: " + dateTime, e);
                }

            } while (cursor.moveToNext());  // Iterar sobre todos los registros en el cursor
        }

        // Cerrar el cursor para liberar recursos
        cursor.close();

        // Notificar al adaptador que los datos han cambiado para actualizar el ListView
        adapter.notifyDataSetChanged();
    }
}
