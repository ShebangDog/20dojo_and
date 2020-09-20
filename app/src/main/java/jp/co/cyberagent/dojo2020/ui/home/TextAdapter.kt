package jp.co.cyberagent.dojo2020.ui.home

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageButton
import androidx.annotation.DrawableRes
import androidx.lifecycle.LifecycleOwner
import androidx.recyclerview.widget.RecyclerView
import jp.co.cyberagent.dojo2020.R
import jp.co.cyberagent.dojo2020.data.model.Draft
import jp.co.cyberagent.dojo2020.data.model.Memo
import jp.co.cyberagent.dojo2020.data.model.Text
import jp.co.cyberagent.dojo2020.databinding.ItemMemoBinding
import jp.co.cyberagent.dojo2020.ui.ext.showImage
import kotlinx.coroutines.ExperimentalCoroutinesApi
import java.util.Collections.emptyList
import java.util.concurrent.TimeUnit

class TextAdapter(
    private val homeViewModel: HomeViewModel,
    private val lifecycleOwner: LifecycleOwner,
    private val onItemClickListener: View.OnClickListener
) : RecyclerView.Adapter<TextAdapter.RecyclerViewHolder>() {

    var textList: List<Text> = emptyList()
        set(value) {
            field = value
            notifyDataSetChanged()
        }

    class RecyclerViewHolder(
        private val binding: ItemMemoBinding,
        private val homeViewModel: HomeViewModel,
        private val lifecycleOwner: LifecycleOwner
    ) : RecyclerView.ViewHolder(binding.root) {

        @DrawableRes
        private val isStartingIcon = R.drawable.ic_starting_timer

        @DrawableRes
        private val isStoppingIcon = R.drawable.ic_stopping_timer

        @DrawableRes
        private val expandLessIcon = R.drawable.ic_expand_less

        @DrawableRes
        private val expandMoreIcon = R.drawable.ic_expand_more

        fun setOnItemClickListener(onItemClickListener: View.OnClickListener) {
            itemView.setOnClickListener(onItemClickListener)
        }

        @ExperimentalCoroutinesApi
        fun setText(text: Text) {
            binding.apply {
                titleTextView.text = text.title
                categoryTextView.text = text.category.name

                expandImageButton.setOnClickListener {
                    it.isSelected = !it.isSelected

                    text.contents.also { contents ->
                        contentsTextView.text =
                            if (it.isSelected) contents else contents.toOneLine()
                    }

                    expandImageButton.showImage(
                        this,
                        if (it.isSelected) expandLessIcon else expandMoreIcon
                    )
                }

                text.contents.also { contents ->
                    contentsTextView.text = contents.toOneLine().also {
                        expandImageButton.visibility =
                            visibleOrGone(it.takeLastWhile { ch -> ch == addedPostFix }.length >= 3)
                    }
                }
            }

            when (text) {
                is Text.Left -> setDraft(text.value)
                is Text.Right -> setMemo(text.value)
            }
        }

        @ExperimentalCoroutinesApi
        private fun setMemo(memo: Memo) = binding.apply {
            timeTextView.text = millsToFormattedTime(memo.time)
            timerImageButton.showImage(this, isStoppingIcon)
        }

        @ExperimentalCoroutinesApi
        private fun setDraft(draft: Draft) = binding.apply {
            val currentSeconds = TimeUnit.MILLISECONDS.toSeconds(
                System.currentTimeMillis() - draft.startTime
            )

            timerImageButton.showImage(this, isStartingIcon)

            val timeLiveData = homeViewModel.timeLiveData(draft.id, currentSeconds)

            timeLiveData.removeObservers(lifecycleOwner)
            timeLiveData.observe(lifecycleOwner) { timeTextView.text = millsToFormattedTime(it) }

            timerImageButton.setOnClickListener {
                timeLiveData.value?.also {
                    homeViewModel.saveMemo(draft.toMemo(it))
                    homeViewModel.deleteDraft(draft)
                }

                timeLiveData.removeObservers(lifecycleOwner)
            }
        }

        private fun visibleOrGone(isVisible: Boolean) = if (isVisible) View.VISIBLE else View.GONE

        private fun String.toOneLine(): String =
            if (this.contains("\n")) takeWhile { it != '\n' } + stringTerminated else this

        private fun millsToFormattedTime(totalTime: Long): String {
            val hours = TimeUnit.SECONDS.toHours(totalTime)
            val minutes = TimeUnit.SECONDS.toMinutes(totalTime - TimeUnit.HOURS.toSeconds(hours))
            val seconds =
                totalTime - TimeUnit.HOURS.toSeconds(hours) - TimeUnit.MINUTES.toSeconds(minutes)

            return listOf(hours, minutes, seconds)
                .map { it.toString() }
                .joinToString(":") { if (it.length == 1) "0$it" else it }
        }

        companion object {
            private const val stringTerminated = "..."
            private const val addedPostFix = '.'
        }

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerViewHolder {
        val inflater = LayoutInflater.from(parent.context)
        val binding: ItemMemoBinding = ItemMemoBinding.inflate(inflater, parent, false)

        return RecyclerViewHolder(binding, homeViewModel, lifecycleOwner)
    }

    @ExperimentalCoroutinesApi
    override fun onBindViewHolder(holder: RecyclerViewHolder, position: Int) {
        val text = textList[position]

        holder.setText(text)
        holder.setOnItemClickListener(onItemClickListener)
    }

    override fun getItemCount() = textList.size
}