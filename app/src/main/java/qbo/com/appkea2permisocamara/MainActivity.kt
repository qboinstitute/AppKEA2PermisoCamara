package qbo.com.appkea2permisocamara

import android.content.Intent
import android.content.pm.PackageManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*

class MainActivity : AppCompatActivity() {
    private val CAMERA_REQUEST = 1888
    private val PERMISO_ESCRITURA_REQUEST = 1889
    var mRutaFoto = ""

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        btntomarfoto.setOnClickListener {
            if(permisoEscritura()){
                try {
                    iniciarCamaraFoto()
                }catch (e: IOException){

                }
            }else {
                solicitarPermisoEscritura()
            }
        }
        btncompartir.setOnClickListener {

        }
    }

    fun permisoEscritura(): Boolean {
        val resultado = ContextCompat.checkSelfPermission(
            applicationContext, android.Manifest.permission.WRITE_EXTERNAL_STORAGE
        )
        return resultado == PackageManager.PERMISSION_GRANTED
    }

    fun solicitarPermisoEscritura(){
        ActivityCompat.requestPermissions(
            this,
            arrayOf(android.Manifest.permission.WRITE_EXTERNAL_STORAGE),
            PERMISO_ESCRITURA_REQUEST)
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        if(requestCode == PERMISO_ESCRITURA_REQUEST){
            if(grantResults.isNotEmpty()
                && grantResults[0] == PackageManager.PERMISSION_GRANTED){
                iniciarCamaraFoto()
            }else{
                Toast.makeText(applicationContext,
                "Permiso Denegado", Toast.LENGTH_LONG).show()
            }
        }
    }

    @Throws(IOException::class)
    private fun crearArchivoImagen() : File?{
        val fechaHoraFoto = SimpleDateFormat("yyyyMMdd_HHmmss")
            .format(Date())
        val fotoNombre = "JPG_$fechaHoraFoto"
        val almacenamientoDirectorio : File =
            this?.getExternalFilesDir(Environment.DIRECTORY_PICTURES)!!
        return File.createTempFile(fotoNombre, ".jpg",
            almacenamientoDirectorio)
    }

    private fun iniciarCamaraFoto(){
        val intentCamara = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        //validar que el dispositivo cuente con la aplicación de la cámara
        if(intentCamara.resolveActivity(this?.packageManager!!) != null){

        }
    }


}