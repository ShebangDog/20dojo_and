package jp.co.cyberagent.dojo2020.ui.create

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.*
import android.view.inputmethod.InputMethodManager
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import jp.co.cyberagent.dojo2020.R
import jp.co.cyberagent.dojo2020.databinding.FragmentMemoCreateBinding
import jp.co.cyberagent.dojo2020.ui.create.spinner.CustomOnItemSelectedListener
import jp.co.cyberagent.dojo2020.ui.create.spinner.SpinnerAdapter
import jp.co.cyberagent.dojo2020.ui.widget.CustomBottomSheetDialog
import jp.co.cyberagent.dojo2020.ui.widget.CustomBottomSheetDialog.Companion.TAG
import kotlinx.android.synthetic.main.activity_main.*

class MemoCreateFragment : Fragment() {
    private lateinit var activityInFragment: AppCompatActivity
    private lateinit var binding: FragmentMemoCreateBinding

    private val memoCreateViewModel by activityViewModels<MemoCreateViewModel> {
        MemoCreateViewModelFactory(this, Bundle(), requireContext())
    }

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

    override fun onAttach(context: Context) {
        super.onAttach(context)

        if (context is AppCompatActivity) {
            activityInFragment = context
        }
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding) {
            val spinnerAdapter = SpinnerAdapter.getInstance(requireContext()).apply {
                setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
            }

            categorySpinner.apply {
                adapter = spinnerAdapter
                onItemSelectedListener = CustomOnItemSelectedListener(
                    this@MemoCreateFragment::showDialog
                )

                setSelection(1)
            }

            addButton.setOnClickListener {
                val title = titleTextEdit.text.toString()
                val content = contentTextEdit.text.toString()
                val category = categorySpinner.selectedItem.toString()

                Log.d(TAG, category)

                memoCreateViewModel.addDraft(title, content, category)
                findNavController().navigate(R.id.action_createMemoFragment_to_homeFragment)
            }
        }
    }

    override fun onActivityCreated(savedInstanceState: Bundle?) {
        super.onActivityCreated(savedInstanceState)
        setHasOptionsMenu(true)
        
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        super.onCreateOptionsMenu(menu, inflater)
        inflater.inflate(R.menu.menu_list, menu)
    }

    private fun showKeyboard() {
        val manager =
            activityInFragment.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager

        manager.showSoftInput(binding.titleTextEdit, InputMethodManager.SHOW_IMPLICIT)
    }

    private fun showDialog() {
        CustomBottomSheetDialog().apply {
            show(activityInFragment.supportFragmentManager, TAG)
        }
    }
}
