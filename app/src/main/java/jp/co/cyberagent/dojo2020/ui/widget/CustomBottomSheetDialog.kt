package jp.co.cyberagent.dojo2020.ui.widget

import android.graphics.Color
import android.graphics.PorterDuff
import android.graphics.PorterDuffColorFilter
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.activityViewModels
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import jp.co.cyberagent.dojo2020.databinding.LayoutBottomSheetBinding
import jp.co.cyberagent.dojo2020.ui.create.MemoCreateViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlin.random.Random

interface OnClickChipListener {
    fun onClick(categoryName: String)
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
                override fun onClick(categoryName: String) {
                    onClickChipListener.onClick(categoryName)
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
                val categoryName = addCategoryTextField.editText?.text.toString()

                memoCreateViewModel.addCategory(categoryName)
                onClickChipListener.onClick(categoryName)
                dismiss()
            }

            addCategoryButton.setOnClickListener(onClick)

            with(addCategoryTextField) {
                editText?.doOnTextChanged { text, _, _, _ ->
                    addCategoryButton.visibility =
                        if (text?.length == 0) View.GONE else View.VISIBLE
                }

                setEndIconOnClickListener {
                    val color = pickColor()

                    editText?.background?.colorFilter = PorterDuffColorFilter(
                        color,
                        PorterDuff.Mode.SRC_IN
                    )
                }
            }

        }
    }

    private fun pickColor(): Int {
        fun genElement() = Random.nextInt(256)

        return Color.rgb(genElement(), genElement(), genElement())
    }
}