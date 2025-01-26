package com.example.crudrealtimedatabaselogingoogle26_01_25

import android.content.Intent
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.crudrealtimedatabaselogingoogle26_01_25.adapters.ArticuloAdapter
import com.example.crudrealtimedatabaselogingoogle26_01_25.databinding.ActivityPrincipalBinding
import com.example.crudrealtimedatabaselogingoogle26_01_25.models.Articulo
import com.example.crudrealtimedatabaselogingoogle26_01_25.providers.ArticuloProvider
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.ktx.Firebase

class PrincipalActivity : AppCompatActivity() {

    private lateinit var binding: ActivityPrincipalBinding
    private lateinit var auth: FirebaseAuth
    private lateinit var database: DatabaseReference
    var adapter = ArticuloAdapter(mutableListOf<Articulo>(), { articulo -> editarArticulo(articulo)}, { articulo -> borrarArticulo(articulo)})

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityPrincipalBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        auth = Firebase.auth
        database = FirebaseDatabase.getInstance().getReference("articulo")

        setTitle("Articulos de la Tienda")

        setRecycler()
        setListeners()
        setMenuLateral()
    }

    private fun setMenuLateral() {
        binding.navigationview.setNavigationItemSelectedListener {
            when(it.itemId) {
                R.id.item_cerrar_sesion -> {
                    auth.signOut()
                    finish()
                    true
                }
                R.id.item_salir -> {
                    finishAffinity()
                    true
                }
                R.id.item_borrar_todo -> {
                    confirmarBorrado()
                    true
                }
                else -> false
            }
        }
    }

    private fun confirmarBorrado() {
        val builder = AlertDialog.Builder(this)
            .setTitle("Borrar Articulos")
            .setMessage("¿Borrar todos los articulos?")
            .setNegativeButton("CANCELAR") {
                    dialog, _ -> dialog.dismiss()
            }
            .setPositiveButton("ACEPTAR") {
                    _, _ -> borrarTodo()
                setRecycler()
            }
            .create()
            .show()
    }

    private fun setListeners() {
        binding.floatingActionButton.setOnClickListener {
            irActivityAdd()
        }
    }

    private fun irActivityAdd(bundle: Bundle ?= null) {
        val i = Intent(this, AddActivity::class.java)
        if(bundle != null) {
            i.putExtras(bundle)
        }
        startActivity(i)
    }

    private fun setRecycler() {
        val layoutManager = LinearLayoutManager(this)
        binding.recyclerView.layoutManager = layoutManager

        binding.recyclerView.adapter = adapter
        recuperarArticulos()
    }

    private fun borrarTodo() {
        database.removeValue().addOnCompleteListener {
            if(it.isSuccessful) {
                Toast.makeText(this, "Articulos borrados", Toast.LENGTH_SHORT).show()
                recuperarArticulos()
            } else {
                Toast.makeText(this, "ERROR: No se pudieron borrar los articulos", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun recuperarArticulos() {
        val articuloProvider = ArticuloProvider()
        articuloProvider.getDatos { todosLosRegistros ->
            adapter.lista = todosLosRegistros
            adapter.notifyDataSetChanged()
        }
    }

    private fun editarArticulo(item: Articulo) {
        val b = Bundle().apply {
            putSerializable("ITEM", item)
        }
        irActivityAdd(b)
    }

    private fun borrarArticulo(item: Articulo) {
        database.child(item.nombre).removeValue()
            .addOnSuccessListener {
                val position = adapter.lista.indexOf(item)
                if(position != -1) {
                    adapter.lista.removeAt(position)
                    adapter.notifyItemRemoved(position)
                    Toast.makeText(this, "Articulo borrado", Toast.LENGTH_SHORT).show()
                }
            }
            .addOnFailureListener {
                Toast.makeText(this, "ERROR: No se pudo borrar el articulo", Toast.LENGTH_SHORT).show()
            }
    }

    override fun onResume() {
        super.onResume()
        recuperarArticulos()
    }
}