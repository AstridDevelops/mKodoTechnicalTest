package com.example.fortunes.view

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.fortunes.databinding.FragmentDrawDetailBinding
import com.example.fortunes.viewmodel.DrawDetailsViewModel
import com.example.fortunes.viewmodel.SharedViewModel

class DrawDetailFragment : Fragment() {

    private var _binding: FragmentDrawDetailBinding? = null
    private lateinit var sharedViewModel: SharedViewModel
    private lateinit var drawDetailsViewModel: DrawDetailsViewModel

    companion object {
        private const val TAG = "DrawDetailFragment"
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            sharedViewModel = ViewModelProvider(requireActivity())[SharedViewModel::class.java]
            drawDetailsViewModel = ViewModelProvider(this).get(DrawDetailsViewModel::class.java)
        } catch (e: Exception) {
            drawDetailsViewModel.updateErrorMessage("Error initializing SharedViewModel")
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return try {
            _binding = FragmentDrawDetailBinding.inflate(inflater, container, false)
            _binding?.root


            // Set up the Toolbar with ViewBinding
            val toolbar = _binding!!.drawDetailToolbar
            (requireActivity() as AppCompatActivity).setSupportActionBar(toolbar)

            // Enable the Up button
            val actionBar = (requireActivity() as AppCompatActivity).supportActionBar
            actionBar?.apply {
                setDisplayHomeAsUpEnabled(true)
                setDisplayShowTitleEnabled(false)
                setDisplayShowHomeEnabled(false)
            }

            toolbar.setNavigationOnClickListener {
                parentFragmentManager.popBackStack()
            }

            _binding?.root

        } catch (e: Exception) {
            drawDetailsViewModel.updateErrorMessage("Error inflating the view or setting up the toolbar")
            super.onCreateView(inflater, container, savedInstanceState)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        try {
            // Set ViewModel
            _binding?.apply {
                viewmodel = drawDetailsViewModel
                lifecycleOwner = viewLifecycleOwner
            }

            val lotteryDrawSelected = sharedViewModel.selectedItem.value
            _binding!!.lotteryDraw = lotteryDrawSelected
            _binding!!.executePendingBindings()

            drawDetailsViewModel.errorMessage.observe(viewLifecycleOwner, Observer { message ->
                message?.let {
                    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                }
            })

        } catch (e: Exception) {
            drawDetailsViewModel.updateErrorMessage("Error setting up the view")
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        try {
            sharedViewModel.setSelectedItem(null)
            _binding = null
        } catch (e: Exception) {
            Log.e(TAG, "Error clearing selected item", e)
        }
    }

}
