Login

Desarrollar una aplicación Android que permita la validación de un acceso mediante usuario/contraseña:
•	Diseño: el diseño es libre, pero tiene que ser responsive y ocupar un porcentaje de la pantalla usando GuideLines.
•	Base de datos: para poder corregir la práctica 
o	el nombre de la base de datos puede ser de libre elección
o	El nombre de la tabla ha de ser “usuarios”
o	Los campos ha de ser:
“usuario” varchar de 50
“contrasena” (poner una “n” en lugar de una “ñ”). varchar de 50
“fecha_nacimiento” formato 
•	PHP (conector entre vuestra app Android y la BBDD). Para que funcionen se tiene que poner los PHP en un XAMP y ser llamados con el localhost. 
o	Conector que se le pasa valores por POST para validar si el usuario y contraseña es válido. Podéis usar el mismo que hay en el material del tema. Se ha de llamar “validacuenta.php”
o	Conector que devuelve todos los valores de la tabla de usuarios. Podéis usar el mismo que hay en el material del tema. Se ha de llamar “consultausuarios.php”
![image](https://github.com/user-attachments/assets/6dbd332e-7f26-4e8d-82c1-33857f69047b)

•	Funcionalidad
o	La pantalla principal ha de permitir al usuario introducir el nombre de la cuenta y su contraseña. Para validar la contraseña se usará el PHP validacuenta.php
o	Si no pasa la validación 
	Se ha de guardar el nombre de la cuenta, la contraseña y la fecha y hora en una base de datos SQLite interna a la APK
	Se ha de mostrar una nueva actividad en donde aparecerá la lista (listView) de los todos intentos fallidos que se han guardado en el SQLite, además de un botón para volver a la pantalla inicial.
o	Si pasa la validación tiene que abrir una nueva actividad en donde se mostrará la lista (en un listView) de valores de la tabla de usuarios de la tabla externa. Para poder consultarla se usará el PHP consultausuarios.php. También debe tener el botón para volver a la pantalla principal 
•	Controles
o	Los campos de usuario y contraseña no pueden estar vacíos
o	El nombre de la cuenta sólo puede permitir letras y números
o	La contraseña ha de tener una longitud de entre 4 y 8 caracteres inclusive.
o	Si no hay conexión a la BBDD externa tiene que sacar un mensaje


