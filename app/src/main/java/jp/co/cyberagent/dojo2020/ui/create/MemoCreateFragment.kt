package jp.co.cyberagent.dojo2020.ui.create

import android.content.Context
import android.content.res.ColorStateList
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import dagger.hilt.android.AndroidEntryPoint
import jp.co.cyberagent.dojo2020.R
import jp.co.cyberagent.dojo2020.data.model.Category
import jp.co.cyberagent.dojo2020.data.model.Color
import jp.co.cyberagent.dojo2020.databinding.FragmentMemoCreateBinding
import jp.co.cyberagent.dojo2020.ui.widget.CustomBottomSheetDialog
import jp.co.cyberagent.dojo2020.ui.widget.CustomBottomSheetDialog.Companion.TAG
import jp.co.cyberagent.dojo2020.ui.widget.OnChipClickListener
import kotlinx.coroutines.ExperimentalCoroutinesApi

@AndroidEntryPoint
class MemoCreateFragment : Fragment() {
    private lateinit var activityInFragment: AppCompatActivity
    private lateinit var binding: FragmentMemoCreateBinding

    private val memoCreateViewModel by activityViewModels<MemoCreateViewModel>()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentMemoCreateBinding.inflate(inflater)

        return binding.root
    }

    override fun onResume() {
        super.onResume()

        showKeyboard()
    }

    override fun onStop() {
        super.onStop()

        hideKeyboard()
    }

    override fun onAttach(context: Context) {
        super.onAttach(context)

        if (context is AppCompatActivity) {
            activityInFragment = context
        }
    }

    @ExperimentalCoroutinesApi
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding) {
            categoryChip.setOnClickListener {
                val onClick = object : OnChipClickListener {
                    override fun onClick(category: Category) {
                        categoryChip.text = category.name
                        categoryChip.chipBackgroundColor = ColorStateList.valueOf(
                            category.color.value
                        )
                    }
                }

                showDialog(onClick)
            }

            memoCreateToolBarLayout.addButton.setOnClickListener {
                val title = titleTextEdit.text.toString()
                val content = contentTextEdit.text.toString()
                val categoryName = categoryChip.text.toString()
                val categoryColor = Color.valueOf(categoryChip.chipBackgroundColor?.defaultColor)

                memoCreateViewModel.addDraft(title, content, categoryName, categoryColor)

                showHome()
            }

            memoCreateToolBarLayout.memoCreateMaterialToolBar.setNavigationOnClickListener { view ->
                view.findNavController().navigateUp()
            }
        }
    }

    private fun showHome() {
        findNavController().navigate(R.id.action_createMemoFragment_to_homeFragment)
    }

    private fun showKeyboard() {
        if (binding.titleTextEdit.requestFocus()) {

            val manager =
                activityInFragment.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

            manager.showSoftInput(binding.titleTextEdit, InputMethodManager.SHOW_IMPLICIT)
        }
    }

    private fun hideKeyboard() {
        if (activityInFragment.currentFocus != null) {
            val inputMethodManager =
                activityInFragment.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

            inputMethodManager.hideSoftInputFromWindow(
                activityInFragment.currentFocus?.windowToken,
                InputMethodManager.HIDE_NOT_ALWAYS
            )
        }
    }

    private fun showDialog(onClickListener: OnChipClickListener) {
        CustomBottomSheetDialog(onClickListener).apply {
            show(activityInFragment.supportFragmentManager, TAG)
        }
    }
}
