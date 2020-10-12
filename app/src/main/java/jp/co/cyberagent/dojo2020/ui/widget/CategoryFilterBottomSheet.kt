package jp.co.cyberagent.dojo2020.ui.widget

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.chip.Chip
import jp.co.cyberagent.dojo2020.data.model.Category
import jp.co.cyberagent.dojo2020.databinding.FragmentCategoryFilterBottomSheetBinding
import jp.co.cyberagent.dojo2020.ui.create.MemoCreateViewModel
import jp.co.cyberagent.dojo2020.ui.home.HomeViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi

class CategoryFilterBottomSheet(
    private val onEachChipClickListener: OnChipClickListener
) : BottomSheetDialogFragment() {

    data class ChipState(val isChecked: Boolean) {
        companion object {
            val None = ChipState(false)
        }
    }

    interface OnChipClickListener {
        fun onClick(chip: Chip, category: Category)
    }

    companion object {
        const val TAG = "CategoryFilterBottomSheet"
    }

    private lateinit var binding: FragmentCategoryFilterBottomSheetBinding
    private val homeViewModel by activityViewModels<HomeViewModel>()
    private val memoCreateViewModel by activityViewModels<MemoCreateViewModel>()

    @ExperimentalCoroutinesApi
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentCategoryFilterBottomSheetBinding.inflate(inflater).apply {
            lifecycleOwner = viewLifecycleOwner

            viewModel = memoCreateViewModel

            chipStateSet = homeViewModel.chipStateLiveData
            onChipClickListener = object : OnChipClickListener {
                override fun onClick(chip: Chip, category: Category) {
                    onEachChipClickListener.onClick(chip, category)
                }
            }
        }

        return binding.root
    }


}