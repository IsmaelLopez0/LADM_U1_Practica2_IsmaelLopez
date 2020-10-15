package com.example.ladm_u1_practica2

import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.core.app.ActivityCompat
import kotlinx.android.synthetic.main.activity_main.*
import java.io.*

class MainActivity : AppCompatActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        abrir.setOnClickListener {
            var archivo = nombreArchivo.text.toString()
            if(btnInterna.isChecked){
                if(!abrirInterna(archivo))
                    AlertDialog.Builder(this)
                        .setTitle("ATENCIÓN")
                        .setMessage("No se pudo abrir el archivo. Compruebe el nombre o la ubicación.")
                        .setPositiveButton("Bien"){ d, i -> d.dismiss() }
                        .show()
            } else {
                if(!abrirExterna(archivo))
                    AlertDialog.Builder(this)
                        .setTitle("ATENCIÓN")
                        .setMessage("No se pudo abrir el archivo. Compruebe el nombre, la ubicación" +
                                "o si tiene insertada una memoria externa.")
                        .setPositiveButton("Bien"){ d, i -> d.dismiss() }
                        .show()
            }
        }

        guardar.setOnClickListener {
            var archivo = nombreArchivo.text.toString()
            if(btnInterna.isChecked){
                if(!guardarInterna(archivo))
                    AlertDialog.Builder(this)
                        .setTitle("ATENCIÓN")
                        .setMessage("No se pudo guardar el archivo. Posible memoria llena.")
                        .setPositiveButton("Bien"){ d, i -> d.dismiss() }
                        .show()
                else
                    Toast.makeText(this, "Se guardó", Toast.LENGTH_LONG).show()
            } else {
                if(!guardarExterna(archivo))
                    AlertDialog.Builder(this)
                        .setTitle("ATENCIÓN")
                        .setMessage("No se pudo guardar el archivo. Posible memoria llena o no existe un memoria secundaria insertada.")
                        .setPositiveButton("Bien"){ d, i -> d.dismiss() }
                        .show()
                else
                    Toast.makeText(this, "Se guardó", Toast.LENGTH_LONG).show()
            }
        }
    }

    private fun concedido(): Boolean {
        return (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
                == PackageManager.PERMISSION_GRANTED)
    }

    private fun permiso(){
        if(ActivityCompat.checkSelfPermission(this, android.Manifest.permission.WRITE_EXTERNAL_STORAGE)
            != PackageManager.PERMISSION_GRANTED){
            ActivityCompat.requestPermissions(this,
                arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE),
                0)
        }
        Toast.makeText(this, "Entró a permiso", Toast.LENGTH_LONG).show()
    }

    private fun abrirInterna(nombre: String): Boolean {
        var contenido = ""
        try {
            var flujoEntrada = BufferedReader(InputStreamReader(openFileInput(nombre)))
            contenido = flujoEntrada.use(BufferedReader::readText)
            texto.setText(contenido)
            flujoEntrada.close()
        }catch (io: IOException){ return false }
        return true
    }

    private fun abrirExterna(nombre: String): Boolean {
        if (concedido()){
            try {
                if(Environment.getExternalStorageState() != Environment.MEDIA_MOUNTED){
                    return false
                }
                var rutaSD = Environment.getExternalStorageDirectory()
                var archivoEnSD = File(rutaSD.absolutePath, nombre)
                var flujoEntrada = BufferedReader(InputStreamReader(FileInputStream(archivoEnSD)))
                var data = flujoEntrada.use(BufferedReader::readText)
                texto.setText(data)
                flujoEntrada.close()
            }catch ( error : IOException ){ return false }
            return true
        } else {
            AlertDialog.Builder(this)
                .setMessage("Necesitas dar permisos para abrir desde almacenamiento externo")
                .setPositiveButton("Dar"){ d, i ->
                    permiso()
                }
                .setNegativeButton("Cancelar"){ d, i -> d.dismiss() }
                .show()
            return false
        }
    }

    private fun guardarInterna(nombre: String): Boolean {
        try {
            var flujoSalida = OutputStreamWriter( openFileOutput(nombre, MODE_PRIVATE) )
            var data = texto.text.toString()
            flujoSalida.write(data)
            flujoSalida.flush()
            flujoSalida.close()
        }catch (io: IOException){ return false }
        return true
    }

    private fun guardarExterna(nombre: String): Boolean {
        if (concedido()){//Se tiene el permiso
            try {
                if (Environment.getExternalStorageState() != Environment.MEDIA_MOUNTED) {
                    return false
                }
                var rutaSD = Environment.getExternalStorageDirectory()
                var archivoEnSD = File(rutaSD.absolutePath, nombre)
                var flujoSalida = OutputStreamWriter( FileOutputStream(archivoEnSD) )
                flujoSalida.write(texto.text.toString())
                flujoSalida.flush()
                flujoSalida.close()
            } catch (io: Exception){ return false }
            return true
        } else {
            AlertDialog.Builder(this)
                .setMessage("Necesitas dar permisos para guardar en el almacenamiento externo")
                .setPositiveButton("Dar"){ d, i ->
                    permiso()
                }
                .setNegativeButton("Cancelar"){ d, i -> d.dismiss() }
                .show()
            return false
        }
    }
}