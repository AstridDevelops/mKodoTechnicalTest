package com.example.fortunes.view

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.databinding.DataBindingUtil
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.example.fortunes.R
import com.example.fortunes.databinding.ItemLotteryDrawBinding
import com.example.fortunes.model.LotteryDraw
import com.example.fortunes.viewmodel.SharedViewModel

class LotteryDrawAdapter(private val sharedViewModel: SharedViewModel) : RecyclerView.Adapter<LotteryDrawAdapter.ViewHolder>() {

    private var lotteryDraws: MutableList<LotteryDraw> = ArrayList()

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return try {
            val binding: ItemLotteryDrawBinding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.item_lottery_draw,
                parent,
                false
            )
            ViewHolder(binding)
        } catch (e: Exception) {
            sharedViewModel.updateErrorMessage("Error creating ViewHolder")
            val binding: ItemLotteryDrawBinding = DataBindingUtil.inflate(
                LayoutInflater.from(parent.context),
                R.layout.item_lottery_draw,
                parent,
                false
            )
            ViewHolder(binding)
        }
    }



    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        try {
            val lotteryDraw = lotteryDraws[position]
            holder.bindTo(lotteryDraw, sharedViewModel)
        } catch (e: Exception) {
            sharedViewModel.updateErrorMessage("Error binding ViewHolder at position $position")
        }
    }

    override fun getItemCount(): Int {
        return lotteryDraws.size
    }

    fun updateList(newList: List<LotteryDraw>) {
        try {
            if (newList == null || newList.isEmpty()) {
                // Clear the displayed list if newList is null
                val oldListSize = lotteryDraws.size
                lotteryDraws.clear()
                notifyItemRangeRemoved(0, oldListSize) // Notify the adapter that items have been removed
            } else {
                val diffResult = DiffUtil.calculateDiff(LotteryDrawDiffCallback(lotteryDraws, newList))
                lotteryDraws.clear()
                lotteryDraws.addAll(newList)
                diffResult.dispatchUpdatesTo(this)
            }
        } catch (e: Exception) {
            sharedViewModel.updateErrorMessage("Error updating list")
        }
    }

    class ViewHolder(private val binding: ItemLotteryDrawBinding) : RecyclerView.ViewHolder(binding.root) {

        fun bindTo(item: LotteryDraw, vm: SharedViewModel) {
            try {
                binding.model = item
                binding.viewModel = vm
                binding.lifecycleOwner = binding.lifecycleOwner
                binding.executePendingBindings()
            } catch (e: Exception) {
                vm.updateErrorMessage("Error binding data")
            }
        }
    }

    class LotteryDrawDiffCallback(
        private val oldList: List<LotteryDraw>,
        private val newList: List<LotteryDraw>
    ) : DiffUtil.Callback() {

        override fun getOldListSize(): Int {
            return oldList.size
        }

        override fun getNewListSize(): Int {
            return newList.size
        }

        override fun areItemsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return try {
                oldList[oldItemPosition].id == newList[newItemPosition].id
            } catch (e: Exception) {
                false
            }
        }

        override fun areContentsTheSame(oldItemPosition: Int, newItemPosition: Int): Boolean {
            return try {
                oldList[oldItemPosition] == newList[newItemPosition]
            } catch (e: Exception) {
                false
            }
        }
    }
}
