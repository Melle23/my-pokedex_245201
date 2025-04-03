package molina.esmeralda.mypokedex_245201

import android.annotation.SuppressLint
import android.app.Activity
import android.content.Intent
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.ImageView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.android.gms.cast.tv.media.MediaManager

class activity_registrar_pokemones : AppCompatActivity() {
    val REQUEST_IMAGE_GET = 1
    val CLOUD_NAME = "dfhiswgqi"
    val UPLOAD_PRESET = "pokemon-upload"
    var imageUri : Uri? = null
    var imagePublicUrl: String? = null
    lateinit var name : EditText
    lateinit var number : EditText
    private val db = FirebaseFirestore.getInstance()


    @SuppressLint("MissingInflatedId")
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_registrar_pokemon)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        initCloudinary()

        name = findViewById(R.id.etName)
        number = findViewById(R.id.etNumber)
        val btnUploadImagen : Button = findViewById(R.id.btnUploadImage)
        val btnSave : Button = findViewById(R.id.btnSavePokemon)
        val imageView : ImageView = findViewById(R.id.imageView) as ImageView

        btnUploadImagen.setOnClickListener{
            val intent : Intent = Intent(Intent.ACTION_GET_CONTENT)
            intent.type = "image/*"
            startActivityForResult(intent, REQUEST_IMAGE_GET)
        }

        btnSave.setOnClickListener{
            savePokemon()
            val intent = Intent(this, MainActivity::class.java)
            startActivity(intent)
        }

    }

    private fun savePokemonToFirestore() {
        val numberValue = number.text.toString().toIntOrNull()
        val nameValue = name.text.toString().trim()

        if (numberValue == null || nameValue.isEmpty()) {
            Toast.makeText(this, "Ingrese un número y nombre válidos", Toast.LENGTH_SHORT).show()
            return
        }

        val pokemon = hashMapOf(
            "number" to numberValue,
            "name" to nameValue,
            "imageUrl" to (imagePublicUrl ?: "")
        )

        db.collection("pokemons")
            .add(pokemon)
            .addOnSuccessListener {
                Toast.makeText(this, "Pokémon registrado correctamente", Toast.LENGTH_SHORT).show()
                number.text.clear()
                name.text.clear()
            }
            .addOnFailureListener { e ->
                Toast.makeText(this, "Error al registrar: ${e.message}", Toast.LENGTH_SHORT).show()
            }
    }

    fun initCloudinary() {
        try {
            val config: MutableMap<String, String> = HashMap<String, String>()
            config["cloud_name"] = CLOUD_NAME
            MediaManager.init(this, config)
        } catch (e: IllegalStateException) {
            Log.w("Cloudinary", "MediaManager ya estaba inicializado")
        }
    }



    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_GET && resultCode == Activity.RESULT_OK){
            val fullPhotoUrl: Uri? = data?.data
            if (fullPhotoUrl != null){
                changeImage(fullPhotoUrl)
                imageUri = fullPhotoUrl

            }
        }
    }

    fun changeImage(uri: Uri){
        val thumbnail: ImageView = findViewById(R.id.imageView)

        try {
            thumbnail.setImageURI(uri)
        } catch (e :Exception ){
            e.printStackTrace()
        }
    }

    fun savePokemon(): String {
        if (imageUri == null) {
            Toast.makeText(this, "Seleccione una imagen antes de registrar", Toast.LENGTH_SHORT).show()
            return ""
        }

        val requestId = MediaManager.get().upload(imageUri)
            .unsigned(UPLOAD_PRESET)
            .callback(object: UploadCallback{
                override fun onStart(requestId: String?) {
                    Log.i("OnStart", "onStart")
                }

                override fun onProgress(requestId: String?, bytes: Long, totalBytes: Long) {
                    Log.i("onProgress", "onProgress")
                }

                override fun onSuccess(
                    requestId: String?,
                    resultData: MutableMap<Any?, Any?>?
                ) {
                    imagePublicUrl = resultData?.get("url") as String?
                    Log.i("onSuccess", imagePublicUrl.toString())
                    savePokemonToFirestore()
                }

                override fun onError(requestId: String?, error: ErrorInfo?) {
                    Log.e("onError", error.toString())
                }

                override fun onReschedule(requestId: String?, error: ErrorInfo?) {
                    Log.i("onReschedule", "onReschedule")
                }
            })
            .dispatch()

        return requestId

    }
}