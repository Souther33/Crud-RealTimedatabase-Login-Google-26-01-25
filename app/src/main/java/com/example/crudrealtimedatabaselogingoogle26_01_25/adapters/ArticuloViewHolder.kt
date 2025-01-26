package com.example.crudrealtimedatabaselogingoogle26_01_25.adapters

import android.view.View
import androidx.recyclerview.widget.RecyclerView
import com.example.crudrealtimedatabaselogingoogle26_01_25.databinding.ArticuloLayoutBinding
import com.example.crudrealtimedatabaselogingoogle26_01_25.models.Articulo

class ArticuloViewHolder(v: View): RecyclerView.ViewHolder(v) {
    private val binding = ArticuloLayoutBinding.bind(v)

    fun render(item: Articulo, onBorrar: (Articulo) -> Unit, onEdit: (Articulo) -> Unit) {
        binding.tvNombre.text = item.nombre
        binding.tvDescripcion.text = item.descripcion
        binding.tvPrecio.text = item.precio.toString()

        binding.btEditar.setOnClickListener {
            onEdit(item)
        }

        binding.btBorrar.setOnClickListener {
            onBorrar(item)
        }
    }
}