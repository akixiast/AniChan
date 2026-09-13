package com.aki.anichan.ui.common

import android.content.Context
import android.graphics.Color
import android.view.LayoutInflater
import android.view.ViewGroup
import com.aki.anichan.databinding.ListGenreBinding
import com.aki.anichan.data.response.Genre
import com.aki.anichan.data.response.getHexColor
import com.aki.anichan.helper.extensions.clicks
import com.aki.anichan.ui.base.BaseRecyclerViewAdapter

class GenreRvAdapter(
    private val context: Context,
    list: List<Genre>,
    private val listener: GenreListener? = null
) : BaseRecyclerViewAdapter<Genre, ListGenreBinding>(list) {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = ListGenreBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ItemViewHolder(view)
    }

    inner class ItemViewHolder(private val binding: ListGenreBinding) : ViewHolder(binding) {
        override fun bind(item: Genre, index: Int) {
            binding.genreText.text = item.name
            binding.genreCard.setCardBackgroundColor(Color.parseColor(item.getHexColor()))
            binding.genreCard.isEnabled = listener != null
            binding.genreCard.clicks {
                listener?.getGenre(item)
            }
        }
    }

    interface GenreListener {
        fun getGenre(genre: Genre)
    }
}