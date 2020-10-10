package jp.co.cyberagent.dojo2020.ui.widget

import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.graphics.drawable.ColorDrawable
import android.graphics.drawable.GradientDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.core.view.ViewCompat
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.activityViewModels
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import jp.co.cyberagent.dojo2020.R
import jp.co.cyberagent.dojo2020.data.model.Category
import jp.co.cyberagent.dojo2020.databinding.LayoutBottomSheetBinding
import jp.co.cyberagent.dojo2020.ui.create.MemoCreateViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlin.random.Random

interface OnClickChipListener {
    fun onClick(category: Category)
}

class CustomBottomSheetDialog(private val onClickChipListener: OnClickChipListener) :
    BottomSheetDialogFragment() {
    companion object {
        const val TAG = "CustomBottomSheetDialog"
    }

    private lateinit var binding: LayoutBottomSheetBinding
    private val memoCreateViewModel by activityViewModels<MemoCreateViewModel>()

    @ExperimentalCoroutinesApi
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = LayoutBottomSheetBinding.inflate(inflater).apply {
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
                val color = Color.valueOf(addCategoryTextFieldLayout.boxBackgroundColor)
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

                    addCategoryButton.setBackgroundColor(context.getColor(R.color.colorGray))
                }

                setEndIconOnClickListener {
                    val color = pickColor().toArgb()

                    boxBackgroundColor = color
                    editText?.background?.colorFilter = PorterDuffColorFilter(
                        color,
                        PorterDuff.Mode.SRC_IN
                    )
                }
            }

        }
    }

    private fun pickColor(): Color {
        fun randomElem() = Random.nextInt(256).toFloat()

        return Color.valueOf(randomElem(), randomElem(), randomElem())
    }
}