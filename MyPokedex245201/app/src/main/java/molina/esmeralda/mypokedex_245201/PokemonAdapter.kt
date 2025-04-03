package molina.esmeralda.mypokedex_245201

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.ImageView
import android.widget.TextView

class PokemonAdapter(context: Context, private val pokemonList: List<Pokemon>) :
    ArrayAdapter<Pokemon>(context, 0, pokemonList) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        val view = convertView ?: LayoutInflater.from(context).inflate(R.layout.item_pokemon, parent, false)

        val pokemon = pokemonList[position]
        val imgPokemon = view.findViewById<ImageView>(R.id.imgPokemon)
        val txtNumber = view.findViewById<TextView>(R.id.txtPokemonNumber)
        val txtName = view.findViewById<TextView>(R.id.txtPokemonName)

        txtNumber.text = "No. ${pokemon.number}"
        txtName.text = pokemon.name

        Glide.with(context)
            .load(pokemon.imageUrl)
            .into(imgPokemon)

        return view
    }
}
