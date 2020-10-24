package jp.co.cyberagent.dojo2020.ui.home.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import jp.co.cyberagent.dojo2020.data.model.Text
import jp.co.cyberagent.dojo2020.databinding.ItemTextBinding
import jp.co.cyberagent.dojo2020.ui.home.HomeViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi

typealias OnTimerClickListener = (text: Text) -> Unit

class TextAdapter(
    private val parentLifecycleOwner: LifecycleOwner,
    private val viewModel: HomeViewModel
) : ListAdapter<Text, TextAdapter.RecyclerViewHolder>(TextDiffUtilItemCallback()) {

    interface Listeners {
        val onItemClickListener: View.OnClickListener
        val onTimerClickListener: OnTimerClickListener
    }

    data class TextState(val isShrink: Boolean, val isOneLine: Boolean) {
        companion object {
            val initial = TextState(isShrink = true, isOneLine = true)
        }
    }

    class RecyclerViewHolder(
        private val binding: ItemTextBinding,
        private val parentLifecycleOwner: LifecycleOwner,
        private val homeViewModel: HomeViewModel
    ) : RecyclerView.ViewHolder(binding.root) {

        @ExperimentalCoroutinesApi
        fun bind(text: Text) {
            binding.apply {
                lifecycleOwner = parentLifecycleOwner

                item = text
                viewModel = homeViewModel
                isDraft = text is Text.Left

                when (text) {
                    is Text.Left -> timeLiveData = homeViewModel.timeLiveData(text.value)
                    is Text.Right -> totalTime = text.value.time
                }

                textStateLiveData = homeViewModel.textStateLiveData
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemTextBinding.inflate(inflater, parent, false)

        return RecyclerViewHolder(binding, parentLifecycleOwner, viewModel)
    }

    @ExperimentalCoroutinesApi
    override fun onBindViewHolder(holder: RecyclerViewHolder, position: Int) {
        val text = getItem(position)

        holder.bind(text)
    }

    class TextDiffUtilItemCallback : DiffUtil.ItemCallback<Text>() {
        override fun areItemsTheSame(oldItem: Text, newItem: Text) = oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: Text, newItem: Text) = oldItem == newItem
    }
}