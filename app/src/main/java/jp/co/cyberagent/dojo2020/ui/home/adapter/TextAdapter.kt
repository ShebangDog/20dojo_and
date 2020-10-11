package jp.co.cyberagent.dojo2020.ui.home.adapter

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.annotation.DrawableRes
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import jp.co.cyberagent.dojo2020.R
import jp.co.cyberagent.dojo2020.data.model.Text
import jp.co.cyberagent.dojo2020.databinding.ItemTextBinding
import jp.co.cyberagent.dojo2020.ui.ext.showImage
import kotlinx.coroutines.ExperimentalCoroutinesApi

typealias OnAppearListener = (ItemTextBinding, Text) -> Unit
typealias OnTimerClickListener = (text: Text) -> Unit

class TextAdapter(
    private val parentLifecycleOwner: LifecycleOwner,
    private val listeners: Listeners
) : ListAdapter<Text, TextAdapter.RecyclerViewHolder>(TextDiffUtilItemCallback()) {

    interface Listeners {
        val onAppearListener: OnAppearListener
        val onItemClickListener: View.OnClickListener
        val onTimerClickListener: OnTimerClickListener
    }

    class RecyclerViewHolder(
        private val binding: ItemTextBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        @DrawableRes
        private val expandLessIcon = R.drawable.ic_expand_less

        @DrawableRes
        private val expandMoreIcon = R.drawable.ic_expand_more

        fun bind(text: Text, parentLifecycleOwner: LifecycleOwner, viewListeners: Listeners) {
            binding.apply {
                lifecycleOwner = parentLifecycleOwner
                isDraft = text is Text.Left
                item = text

                listeners = viewListeners
            }
        }

        @ExperimentalCoroutinesApi
        fun setText(
            text: Text,
            onAppearListener: OnAppearListener,
        ) {
            binding.apply {
                expandImageButton.setOnClickListener {
                    it.isSelected = !it.isSelected

                    text.content.also { contents ->
                        contentsTextView.text =
                            if (it.isSelected) contents.text else contents.toOneLine()
                    }

                    expandImageButton.showImage(
                        if (it.isSelected) expandLessIcon else expandMoreIcon
                    )
                }

                onAppearListener(binding, text)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding = ItemTextBinding.inflate(inflater, parent, false)

        return RecyclerViewHolder(binding)
    }

    @ExperimentalCoroutinesApi
    override fun onBindViewHolder(holder: RecyclerViewHolder, position: Int) {
        val text = getItem(position)

        holder.bind(text, parentLifecycleOwner, listeners)
        holder.setText(text, listeners.onAppearListener)
    }

    class TextDiffUtilItemCallback : DiffUtil.ItemCallback<Text>() {
        override fun areItemsTheSame(oldItem: Text, newItem: Text) = oldItem.id == newItem.id

        override fun areContentsTheSame(oldItem: Text, newItem: Text) = oldItem == newItem
    }
}