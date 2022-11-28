package com.cg.marvel_app.ui.allcharacters

import android.graphics.drawable.Drawable
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.isVisible
import androidx.fragment.app.viewModels
import androidx.paging.LoadState
import androidx.recyclerview.widget.LinearLayoutManager
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DataSource
import com.bumptech.glide.load.engine.GlideException
import com.bumptech.glide.request.RequestListener
import com.bumptech.glide.request.target.Target
import com.cg.marvel_app.R
import com.cg.marvel_app.data.characters.CharacterResult
import com.cg.marvel_app.databinding.FragmentComicBinding
import com.cg.marvel_app.databinding.FragmentSeriesBinding
import com.cg.marvel_app.ui.charactercomics.ComicRecyclerViewAdapter
import com.cg.marvel_app.ui.charactercomics.ComicViewModel
import com.cg.marvel_app.ui.characterseries.SeriesRecyclerViewAdapter
import com.cg.marvel_app.ui.characterseries.SeriesViewModel
import com.google.android.material.bottomsheet.BottomSheetDialog
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class CharacterDet (val characterResult: CharacterResult): Fragment() {


    private lateinit var dialog: BottomSheetDialog


    private lateinit var nammeTextView:TextView
    private lateinit var imageHolder:ImageView
    private lateinit var descrptionView:TextView
    private lateinit var comicBtn:Button
    private lateinit var seriesBtn:Button
    private var _binding: FragmentSeriesBinding? = null
    private val binding get() = _binding!!
    private val seriesViewModel : SeriesViewModel by viewModels()

    private var _bindingC: FragmentComicBinding? = null
    private val bindingC get() = _bindingC!!
    private val comicViewModel: ComicViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        val view  = inflater.inflate(R.layout.fragment_character_det, container, false)

        nammeTextView = view.findViewById(R.id.character_name_det)
        descrptionView = view.findViewById(R.id.txt_description)
        imageHolder = view.findViewById(R.id.character_image2)
        seriesBtn = view.findViewById(R.id.seriesBtnGet)
        comicBtn = view.findViewById(R.id.comicsBtmGet)

        return view
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)

        nammeTextView.text = characterResult.name
        descrptionView.text = characterResult.description

        comicBtn.setOnClickListener(View.OnClickListener {
            showBottomSheetComics()
        })

        seriesBtn.setOnClickListener(View.OnClickListener {
            showBottomSheetSeries()
        })

        view?.let {
            Glide.with(it)
                .load(characterResult.thumbnail.path + "." + characterResult.thumbnail.extension)
                .listener(object : RequestListener<Drawable> {
                    override fun onLoadFailed(
                        e: GlideException?,
                        model: Any?,
                        target: Target<Drawable>?,
                        isFirstResource: Boolean
                    ): Boolean {
                        return false
                    }

                    override fun onResourceReady(
                        resource: Drawable?,
                        model: Any?,
                        target: Target<Drawable>?,
                        dataSource: DataSource?,
                        isFirstResource: Boolean
                    ): Boolean {

                        return false
                    }
                })
                .centerCrop().into(imageHolder)
        }

    }

    private fun showBottomSheetComics(){

        var dialogView = layoutInflater.inflate(R.layout.fragment_comic, null, false)

        dialog = context?.let { BottomSheetDialog(it, R.style.Theme_Design_BottomSheetDialog) }!!

        dialog.setContentView(dialogView)

        _bindingC = FragmentComicBinding.bind(dialogView)

        val comicAdapter = ComicRecyclerViewAdapter()

        comicAdapter.addLoadStateListener { loadState ->
            bindingC.apply {
                comicProgressBar.isVisible = loadState.source.refresh is LoadState.Loading

                comicRecyclerView.isVisible = loadState.source.refresh is LoadState.NotLoading
                comicRetryButton.isVisible = loadState.source.refresh is LoadState.Error
                comicNoConnection.isVisible = loadState.source.refresh is LoadState.Error
                if (loadState.source.refresh is LoadState.NotLoading &&
                    loadState.append.endOfPaginationReached && comicAdapter.itemCount < 1
                ) {
                    comicRecyclerView.isVisible = false
                    noResultFoundTextView.isVisible = true
                } else {
                    noResultFoundTextView.isVisible = false
                }
            }
        }

        bindingC.comicRecyclerView.apply {
            adapter = comicAdapter.withLoadStateHeaderAndFooter(
                header = MarvelLoadStateAdapter { comicAdapter.retry() },
                footer = MarvelLoadStateAdapter { comicAdapter.retry() }
            )
            layoutManager = LinearLayoutManager(requireContext())
            setHasFixedSize(true)
        }

        _bindingC?.apply {
            comicRetryButton.setOnClickListener {
                comicAdapter.retry()
            }
        }

        comicViewModel.getCharacterComics(characterResult.id).observe(viewLifecycleOwner) {
            comicAdapter.submitData(viewLifecycleOwner.lifecycle, it)
        }


        dialog.show()
    }

    private fun showBottomSheetSeries(){

        var dialogView = layoutInflater.inflate(R.layout.fragment_series, null, false)

        dialog = context?.let { BottomSheetDialog(it, R.style.Theme_Design_BottomSheetDialog) }!!

        dialog.setContentView(dialogView)
         _binding = FragmentSeriesBinding.bind(dialogView)

        val seriesAdapter = SeriesRecyclerViewAdapter()

    seriesAdapter.addLoadStateListener { loadState ->
        binding.apply {
            seriesProgressBar.isVisible = loadState.source.refresh is LoadState.Loading
            seriesRecyclerView.isVisible = loadState.source.refresh is LoadState.NotLoading
            seriesRetryButton.isVisible = loadState.source.refresh is LoadState.Error
            seriesNoConnection.isVisible = loadState.source.refresh is LoadState.Error
            if (loadState.source.refresh is LoadState.NotLoading &&
                loadState.append.endOfPaginationReached && seriesAdapter.itemCount < 1
            ) {
                seriesRecyclerView.isVisible = false
                noResultFoundTextView.isVisible = true
            } else {
                noResultFoundTextView.isVisible = false
            }
        }
    }

    binding.seriesRecyclerView.apply {
        adapter = seriesAdapter.withLoadStateHeaderAndFooter(                                                             
            header = MarvelLoadStateAdapter { seriesAdapter.retry() },
            footer = MarvelLoadStateAdapter { seriesAdapter.retry() }
        )
        layoutManager = LinearLayoutManager(requireContext())
        setHasFixedSize(true)
    }

    seriesViewModel.getCharacterSeries(characterResult.id).observe(viewLifecycleOwner) {
        seriesAdapter.submitData(viewLifecycleOwner.lifecycle, it)
    }


    dialog.show()
}
}