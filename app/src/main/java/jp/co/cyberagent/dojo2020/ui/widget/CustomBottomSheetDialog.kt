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
import kotlinx.android.synthetic.main.layout_bottom_sheet.*
import kotlin.random.Random

class CustomBottomSheetDialog : BottomSheetDialogFragment() {
    companion object {
        const val TAG = "CustomBottomSheetDialog"
    }

    private lateinit var binding: LayoutBottomSheetBinding
    private val memoCreateViewModel by activityViewModels<MemoCreateViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        binding = LayoutBottomSheetBinding.inflate(inflater)

        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding) {
            val onClick: (View) -> Unit = {
                val category = addCategoryTextField.editText?.text.toString()

                memoCreateViewModel.addCategory(category)
            }

            addCategoryButton.setOnClickListener(onClick)

            with(addCategoryTextField) {
                editText?.doOnTextChanged { text, _, _, _ ->
                    add_category_button.visibility =
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