package com.example.crudrealtimedatabaselogingoogle26_01_25

import android.os.Bundle
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.example.crudrealtimedatabaselogingoogle26_01_25.databinding.ActivityAddBinding
import com.example.crudrealtimedatabaselogingoogle26_01_25.models.Articulo
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class AddActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAddBinding
    private var nombre = ""
    private var descripcion = ""
    private var precio = 0F
    private var editando = false
    private var articulo = Articulo()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        binding = ActivityAddBinding.inflate(layoutInflater)
        setContentView(binding.root)

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        setTitle("AÑADIR ARTICULO")

        binding.tvTitulo.text = "AÑADIR ARTICULO"
        setListeners()
        recogerDatos()
    }

    private fun setListeners() {
        binding.btAnadir.setOnClickListener {
            anadirArticulo()
        }

        binding.btCancelar.setOnClickListener {
            finish()
        }
    }

    private fun anadirArticulo() {
        if(!datosCorrectos()) return
        val database: DatabaseReference = FirebaseDatabase.getInstance().getReference("articulo")
        val item = Articulo(nombre, descripcion, precio)
        database.child(nombre).addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists() && !editando) {
                    Toast.makeText(this@AddActivity, "Ya está registrado un articulo con el mismo noombre", Toast.LENGTH_SHORT).show()
                } else {
                    database.child(nombre).setValue(item).addOnSuccessListener {
                        finish()
                    }
                        .addOnFailureListener {
                            Toast.makeText(this@AddActivity, "ERROR: No se ha podido guardar el articulo", Toast.LENGTH_SHORT).show()
                        }
                }
            }

            override fun onCancelled(error: DatabaseError) {

            }

        })
    }

    private fun datosCorrectos(): Boolean {
        nombre = binding.etNombre.text.toString().trim()
        if(nombre.length < 3) {
            binding.etNombre.error = "ERROR: El nombre del articulo debe tener al menos 2 caracteres"
            return false
        }

        descripcion = binding.etDescripcion.text.toString().trim()
        if(descripcion.length < 5) {
            binding.etDescripcion.error = "ERROR: La descripcion del articulo debe tener al menos 2 caracteres"
            return false
        }

        precio = binding.etPrecio.text.toString().toFloat()
        if(precio < 0.00 || precio > 999.99) {
            binding.etPrecio.error = "ERROR: El articulo no puede valer menos de 0.00 ni más de 999.99"
            return false
        }

        return true
    }

    private fun recogerDatos() {
        val datos = intent.extras
        if(datos != null) {
            articulo = datos.getSerializable("ITEM") as Articulo
            setTitle("EDITAR ARTICULO")
            binding.tvTitulo.text = "EDITAR ARTICULO"
            binding.btAnadir.text = "EDITAR"
            editando = true
            pintarValores()
        }
    }

    private fun pintarValores() {
        binding.etNombre.setText(articulo.nombre)
        binding.etDescripcion.setText(articulo.descripcion)
        binding.etPrecio.setText(articulo.precio.toString())
    }


}