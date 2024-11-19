package com.example.dam_m08_act02;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Clase DatabaseHelper para gestionar la creación y actualización de la base de datos SQLite.
 * Esta clase permite el almacenamiento y manejo de los intentos fallidos de inicio de sesión.
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    // Nombre de la base de datos
    private static final String DATABASE_NAME = "login.db";
    // Versión de la base de datos, debe incrementarse cuando haya cambios estructurales en la base de datos
    private static final int DATABASE_VERSION = 2;

    /**
     * Constructor de la clase DatabaseHelper.
     * @param context El contexto de la aplicación que se utilizará para abrir o crear la base de datos.
     */
    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    /**
     * Método llamado cuando la base de datos se crea por primera vez.
     * Este método se encarga de crear la estructura de la base de datos, incluyendo las tablas necesarias.
     *
     * @param db Instancia de la base de datos proporcionada por el framework Android.
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        // Crear la tabla "intentos_fallidos" si no existe
        String createTable = "CREATE TABLE IF NOT EXISTS intentos_fallidos (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " + // Identificador único de cada intento fallido
                "usuario TEXT, " + // Nombre de usuario del intento de inicio de sesión fallido
                "contrasena TEXT, " + // Contraseña del intento de inicio de sesión fallido
                "fecha_hora TEXT)"; // Fecha y hora del intento fallido en formato timestamp
        db.execSQL(createTable); // Ejecutar la sentencia SQL para crear la tabla
    }

    /**
     * Método llamado cuando se requiere una actualización de la base de datos.
     * Este método se invoca automáticamente cuando la versión de la base de datos cambia, y permite
     * realizar modificaciones en la estructura de la base de datos.
     *
     * @param db Instancia de la base de datos proporcionada por el framework Android.
     * @param oldVersion Versión antigua de la base de datos.
     * @param newVersion Nueva versión de la base de datos.
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Eliminar la tabla "intentos_fallidos" si existe
        db.execSQL("DROP TABLE IF EXISTS intentos_fallidos");
        // Crear nuevamente la tabla "intentos_fallidos"
        onCreate(db);
    }
}
