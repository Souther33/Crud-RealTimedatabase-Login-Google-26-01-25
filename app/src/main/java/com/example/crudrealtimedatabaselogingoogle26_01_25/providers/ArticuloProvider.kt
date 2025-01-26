package com.example.crudrealtimedatabaselogingoogle26_01_25.providers

import com.example.crudrealtimedatabaselogingoogle26_01_25.models.Articulo
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener

class ArticuloProvider {
    private val database = FirebaseDatabase.getInstance().getReference("articulo")

    fun getDatos(datosArticulo: (MutableList<Articulo>) -> Unit) {
        database.addListenerForSingleValueEvent(object: ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                val listado = mutableListOf<Articulo>()

                for(item in snapshot.children) {
                    val valor = item.getValue(Articulo::class.java)
                    if(valor != null) {
                        listado.add(valor)
                    }
                }

                listado.sortBy { it.nombre }
                datosArticulo(listado)
            }

            override fun onCancelled(error: DatabaseError) {
                println("Error al obtener los datos: ${error.message}")
            }

        })
    }
}