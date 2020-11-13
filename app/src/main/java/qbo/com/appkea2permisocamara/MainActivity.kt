package qbo.com.appkea2permisocamara

import android.app.Activity
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.BitmapFactory
import android.media.ExifInterface
import android.net.Uri
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.content.FileProvider
import kotlinx.android.synthetic.main.activity_main.*
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import kotlin.math.min

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
        val imagen: File =  File.createTempFile(fotoNombre, ".jpg",
            almacenamientoDirectorio)
        mRutaFoto = imagen.absolutePath
        return imagen
    }

    private fun iniciarCamaraFoto(){
        val intentCamara = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        //validar que el dispositivo cuente con la aplicación de la cámara
        if(intentCamara.resolveActivity(this?.packageManager!!) != null){
            val archivofoto = crearArchivoImagen()
            if(archivofoto != null){
                val urlfoto = FileProvider.getUriForFile(
                        applicationContext,
                        "qbo.com.appkea2permisocamara.provider",
                        archivofoto
                )
                intentCamara.putExtra(MediaStore.EXTRA_OUTPUT, urlfoto)
                startActivityForResult(intentCamara, CAMERA_REQUEST)
            }
        }
    }

    private fun grabarFoto(){
        val intentFoto = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        val nuevoarchivo = File(mRutaFoto)
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.N){
            val contenturl = FileProvider.getUriForFile(
                    applicationContext,
                    "qbo.com.appkea2permisocamara.provider",
                    nuevoarchivo
            )
            intentFoto.data = contenturl
        }else{
            val contenturl = Uri.fromFile(nuevoarchivo)
            intentFoto.data = contenturl
        }
        this?.sendBroadcast(intentFoto)
    }

    private fun mostrarFoto(){
        val ei = ExifInterface(mRutaFoto)
        val orientacion: Int = ei.getAttributeInt(
                ExifInterface.TAG_ORIENTATION,
                ExifInterface.ORIENTATION_UNDEFINED
        )
        if(orientacion == ExifInterface.ORIENTATION_ROTATE_90){
            ivfoto.rotation = 90.0F
        }else{
            ivfoto.rotation = 0.0F
        }
        val anchoiv: Int = ivfoto.width
        val altoiv: Int = ivfoto.height
        val bitmapOpciones = BitmapFactory.Options()
        bitmapOpciones.inJustDecodeBounds = true
        BitmapFactory.decodeFile(mRutaFoto, bitmapOpciones)
        val anchofoto: Int = bitmapOpciones.outWidth
        val altofoto: Int = bitmapOpciones.outHeight
        val escala: Int = min(anchoiv / anchofoto, altoiv / altofoto)
        bitmapOpciones.inSampleSize = escala
        bitmapOpciones.inJustDecodeBounds = false
        val imagenbitmap = BitmapFactory.decodeFile(mRutaFoto, bitmapOpciones)
        ivfoto.setImageBitmap(imagenbitmap)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if(requestCode == CAMERA_REQUEST){
            if(resultCode == Activity.RESULT_OK){
                grabarFoto()
                mostrarFoto()
            }
        }
        super.onActivityResult(requestCode, resultCode, data)
    }


}