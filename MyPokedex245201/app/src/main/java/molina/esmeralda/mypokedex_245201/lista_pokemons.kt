package molina.esmeralda.mypokedex_245201

import android.content.Intent
import android.os.Bundle
import android.widget.Button
import android.widget.ListView
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.google.firebase.firestore.FirebaseFirestore

class lista_pokemons : AppCompatActivity() {
    private lateinit var listView: ListView
    private lateinit var adapter: PokemonAdapter
    private val pokemonList = mutableListOf<Pokemon>()
    private val db = Fireba
    seFirestore.getInstance()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(R.layout.activity_main)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        listView = findViewById(R.id.listView)
        adapter = PokemonAdapter(this, pokemonList)
        listView.adapter = adapter

        val btnRegistrar: Button = findViewById(R.id.btnContent)
        btnRegistrar.setOnClickListener {
            val intent = Intent(this, RegistrarPokemon::class.java)
            startActivity(intent)
        }

        loadPokemonFromFirestore()
    }

    private fun loadPokemonFromFirestore() {
        db.collection("pokemons")
            .orderBy("number")
            .addSnapshotListener { snapshots, e ->
                if (e != null) {
                    Toast.makeText(this, "Error al cargar: ${e.message}", Toast.LENGTH_SHORT).show()
                    return@addSnapshotListener
                }

                pokemonList.clear()

                for (document in snapshots!!) {
                    val pokemon = document.toObject(Pokemon::class.java)
                    pokemonList.add(pokemon)
                }

                adapter.notifyDataSetChanged()
            }
    }
}
