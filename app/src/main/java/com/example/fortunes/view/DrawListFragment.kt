package com.example.fortunes.view

import android.content.Context
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.fortunes.R
import com.example.fortunes.databinding.FragmentDrawListBinding
import com.example.fortunes.viewmodel.SharedViewModel

class DrawListFragment : Fragment() {

    private var binding: FragmentDrawListBinding? = null
    private lateinit var lotteryDrawRecyclerView: RecyclerView
    private lateinit var lotteryDrawAdapter: LotteryDrawAdapter
    private lateinit var sharedViewModel: SharedViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        try {
            sharedViewModel = ViewModelProvider(requireActivity())[SharedViewModel::class.java]
        } catch (e: Exception) {
            sharedViewModel.updateErrorMessage("Error initializing SharedViewModel")
        }
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return try {
            binding = FragmentDrawListBinding.inflate(inflater, container, false)
            binding?.root
        } catch (e: Exception) {
            sharedViewModel.updateErrorMessage("Error inflating the view")
            super.onCreateView(inflater, container, savedInstanceState)
        }
    }


    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        try {
            val context = context

            // Set ViewModel
            binding?.apply {
                viewmodel = sharedViewModel
                lifecycleOwner = viewLifecycleOwner
            }

            lotteryDrawAdapter = LotteryDrawAdapter(sharedViewModel)
            lotteryDrawRecyclerView = binding?.recyclerView ?: return
            lotteryDrawRecyclerView.layoutManager = LinearLayoutManager(context)
            lotteryDrawRecyclerView.adapter = lotteryDrawAdapter

            // Observer
            sharedViewModel.lotteryDrawList.observe(viewLifecycleOwner) { list ->
                try {
                    lotteryDrawAdapter.updateList(list)
                } catch (e: Exception) {
                    sharedViewModel.updateErrorMessage("Error updating lottery draw list")
                }finally {
                    sharedViewModel.setLoadingVisibilityState(false)
                }
            }

            sharedViewModel.selectedItem.observe(viewLifecycleOwner) { item ->
                try {
                    if(item != null) {
                        navigateToDrawDetailFragment(context)
                    }
                } catch (e: Exception) {
                    sharedViewModel.updateErrorMessage("Error navigating to DrawDetailFragment")
                }
            }

            sharedViewModel.errorMessage.observe(viewLifecycleOwner, Observer { message ->
                message?.let {
                    Toast.makeText(context, message, Toast.LENGTH_SHORT).show()
                }
            })

            // Click listener
            binding?.buttonLotteryTicket?.setOnClickListener {
                try {
                    navigateToYourTicketFragment(context)
                } catch (e: Exception) {
                    sharedViewModel.updateErrorMessage("Error navigating to BuyTicketFragment")
                }
            }

        } catch (e: Exception) {
            sharedViewModel.updateErrorMessage("Error setting up the view")
        }
    }

    private fun navigateToDrawDetailFragment(context: Context?) {
        try {
            val transaction = (context as? FragmentActivity)?.supportFragmentManager?.beginTransaction()
            transaction?.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left)
            transaction?.replace(android.R.id.content, DrawDetailFragment())
            transaction?.addToBackStack(null)
            transaction?.commit()
        } catch (e: Exception) {
            sharedViewModel.updateErrorMessage("Error performing fragment transaction to DrawDetailFragment")
        }
    }

    private fun navigateToYourTicketFragment(context: Context?) {
        try {
            val transaction = (context as? FragmentActivity)?.supportFragmentManager?.beginTransaction()
            transaction?.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left)
            transaction?.replace(android.R.id.content, YourTicketFragment())
            transaction?.addToBackStack(null)
            transaction?.commit()
        } catch (e: Exception) {
            sharedViewModel.updateErrorMessage("Error performing fragment transaction to YourTicketFragment")
        }
    }


    override fun onDestroyView() {
        super.onDestroyView()
        try {
            binding = null
        } catch (e: Exception) {
            sharedViewModel.updateErrorMessage("Error clearing binding")
        }
    }

}