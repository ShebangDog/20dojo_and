package jp.co.cyberagent.dojo2020.ui.widget

import android.content.res.ColorStateList
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.view.children
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.activityViewModels
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.chip.Chip
import jp.co.cyberagent.dojo2020.R
import jp.co.cyberagent.dojo2020.data.model.Category
import jp.co.cyberagent.dojo2020.data.model.Color
import jp.co.cyberagent.dojo2020.databinding.FragmentCustomBottomSheetDialogBinding
import jp.co.cyberagent.dojo2020.ui.create.MemoCreateViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi

interface OnClickChipListener {
    fun onClick(category: Category)
}

class CustomBottomSheetDialog(private val onClickChipListener: OnClickChipListener) :
    BottomSheetDialogFragment() {
    companion object {
        const val TAG = "CustomBottomSheetDialog"
    }

    private lateinit var binding: FragmentCustomBottomSheetDialogBinding
    private val memoCreateViewModel by activityViewModels<MemoCreateViewModel>()

    @ExperimentalCoroutinesApi
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = FragmentCustomBottomSheetDialogBinding.inflate(inflater).apply {
            lifecycleOwner = viewLifecycleOwner

            viewModel = memoCreateViewModel
            onChipClickListener = object : OnClickChipListener {
                override fun onClick(category: Category) {
                    onClickChipListener.onClick(category)
                    dismiss()
                }
            }
        }

        return binding.root
    }

    @ExperimentalCoroutinesApi
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding) {

            val onClick: (View) -> Unit = {
                val name = addCategoryTextField.text.toString()
                val color = Color.valueOf(addCategoryTextField.backgroundTintList?.defaultColor)
                val category = Category(name, color)

                memoCreateViewModel.addCategory(name, color)
                onClickChipListener.onClick(category)
                dismiss()
            }

            addCategoryButton.setOnClickListener(onClick)

            with(addCategoryTextFieldLayout) {
                editText?.doOnTextChanged { text, _, _, _ ->
                    addCategoryButton.visibility =
                        if (text?.length == 0) View.GONE else View.VISIBLE

                    val contains = chipGroup.children
                        .map { if (it is Chip) it.text.toString() else null }
                        .filterNotNull()
                        .contains(text?.toString())

                    val isOver = text.toString().length > Category.maxLength

                    addCategoryButton.background.setTint(
                        if (contains || isOver) android.graphics.Color.GRAY else context.getColor(R.color.secondaryColor)
                    )

                    addCategoryButton.isClickable = !contains && !isOver
                }

                setEndIconOnClickListener {
                    val color = Color.pickColor()

                    editText?.backgroundTintList = ColorStateList.valueOf(color.value)
                }
            }

        }
    }

    private fun formatForHelper(text: CharSequence?): String {
        val length = text.toString().length

        return "$length/${Category.maxLength}"
    }
}