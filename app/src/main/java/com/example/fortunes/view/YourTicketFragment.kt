package com.example.fortunes.view

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.fortunes.databinding.FragmentYourTicketBinding
import com.example.fortunes.viewmodel.YourTicketViewModel
import com.example.fortunes.viewmodel.SharedViewModel

class YourTicketFragment : Fragment() {

    private val TAG = "YourTicketFragment"
    private lateinit var yourTicketViewModel: YourTicketViewModel
    private lateinit var sharedViewModel: SharedViewModel
    private var _binding: FragmentYourTicketBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            sharedViewModel = ViewModelProvider(requireActivity())[SharedViewModel::class.java]
            yourTicketViewModel = ViewModelProvider(this)[YourTicketViewModel::class.java]
        } catch (e: Exception) {
            yourTicketViewModel.updateErrorMessage("Error initializing ViewModels")
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return try {
            _binding = FragmentYourTicketBinding.inflate(inflater, container, false)
            binding.viewmodel = yourTicketViewModel
            binding.lifecycleOwner = viewLifecycleOwner

            // Set up the Toolbar with ViewBinding
            val toolbar: Toolbar = binding.buyTicketToolbar
            (requireActivity() as AppCompatActivity).setSupportActionBar(toolbar)
            val actionBar: ActionBar? = (requireActivity() as AppCompatActivity).supportActionBar
            actionBar?.apply {
                setDisplayHomeAsUpEnabled(true)
                setDisplayShowTitleEnabled(false)
                setDisplayShowHomeEnabled(false)
            }
            toolbar.setNavigationOnClickListener {
                parentFragmentManager.popBackStack()
            }
            binding.root
        } catch (e: Exception) {
            yourTicketViewModel.updateErrorMessage("Error inflating the view")
            super.onCreateView(inflater, container, savedInstanceState)
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        try {
            yourTicketViewModel.generateRandomLotteryTicket()

            // observers
            yourTicketViewModel.lotteryTicket.observe(viewLifecycleOwner, {
                yourTicketViewModel.setWinnerStatus(sharedViewModel.lotteryDrawList.value)
            })

            yourTicketViewModel.errorMessage.observe(viewLifecycleOwner, Observer { message ->
                message?.let {
                    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                }
            })

        } catch (e: Exception) {
            yourTicketViewModel.updateErrorMessage("Error initiating ViewModel")
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        try {
            _binding = null
        } catch (e: Exception) {
            Log.e(TAG, "Error clearing binding", e)
        }
    }
}

