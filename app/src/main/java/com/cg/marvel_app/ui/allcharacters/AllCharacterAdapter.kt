package com.cg.marvel_app.ui.allcharacters

import android.annotation.SuppressLint
import android.graphics.drawable.Drawable
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.animation.AnimationUtils
import androidx.core.view.isVisible
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.cg.marvel_app.R
import com.cg.marvel_app.data.characters.CharacterResult
import com.cg.marvel_app.databinding.ItemCharacterBinding
import com.cg.marvel_app.utils.Mapper.CHARACTER_MAPPER

class AllCharacterAdapter(private val listener: CharacterClickListener) :
    PagingDataAdapter<CharacterResult, AllCharacterAdapter.AllCharacterViewHolder>(CHARACTER_MAPPER) {

    var favourites: List<CharacterResult> = ArrayList()

    inner class AllCharacterViewHolder(private val binding: ItemCharacterBinding) :
        RecyclerView.ViewHolder(binding.root) {
        init {
            binding.characterCardView.animation =
                AnimationUtils.loadAnimation(binding.characterCardView.context, R.anim.scale)
            binding.characterCardView.setOnClickListener {
                val position = bindingAdapterPosition
                if (position != RecyclerView.NO_POSITION) {
                    val character = getItem(position)
                    if (character != null) {
                        listener.onClick(character)
                    }
                }
            }
        }

        @SuppressLint("SetTextI18n")
        fun bind(character: CharacterResult) {
            binding.apply {
                Glide.with(itemView)
                    .load(character.thumbnail.path + "." + character.thumbnail.extension)
                    .listener(object : RequestListener<Drawable> {
                        override fun onLoadFailed(
                            e: GlideException?,
                            model: Any?,
                            target: Target<Drawable>?,
                            isFirstResource: Boolean
                        ): Boolean {
                            characterProgressbar.isVisible = false
                            return false
                        }

                        override fun onResourceReady(
                            resource: Drawable?,
                            model: Any?,
                            target: Target<Drawable>?,
                            dataSource: DataSource?,
                            isFirstResource: Boolean
                        ): Boolean {
                            characterProgressbar.isVisible = false
                            return false
                        }
                    })
                    .centerCrop().into(characterImage)
                characterName.text = character.name

            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): AllCharacterViewHolder {
        val binding =
            ItemCharacterBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        val allCharacterViewHolder = AllCharacterViewHolder(binding)
        Log.i("Fav item size", favourites.size.toString())

        return allCharacterViewHolder
    }

    override fun onBindViewHolder(holder: AllCharacterViewHolder, position: Int) {
        val currentCharacter = getItem(position)
        if (currentCharacter != null) {
            holder.bind(currentCharacter)
        }
    }

}

interface CharacterClickListener {
    fun onClick(character: CharacterResult)
}