package jp.co.cyberagent.dojo2020.ui.home

import android.content.Context
import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.viewbinding.ViewBinding
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import dagger.hilt.android.AndroidEntryPoint
import jp.co.cyberagent.dojo2020.R
import jp.co.cyberagent.dojo2020.data.model.Category
import jp.co.cyberagent.dojo2020.data.model.Text
import jp.co.cyberagent.dojo2020.databinding.FragmentHomeBinding
import jp.co.cyberagent.dojo2020.ui.home.adapter.OnAppearListener
import jp.co.cyberagent.dojo2020.ui.home.adapter.OnTimerClickListener
import jp.co.cyberagent.dojo2020.ui.home.adapter.TextAdapter
import jp.co.cyberagent.dojo2020.ui.widget.CategoryFilterBottomSheet
import jp.co.cyberagent.dojo2020.ui.widget.OnChipClickListener
import kotlinx.coroutines.ExperimentalCoroutinesApi
import java.util.concurrent.TimeUnit

@AndroidEntryPoint
class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding
    private val homeViewModel by viewModels<HomeViewModel>()

    private lateinit var activityInFragment: AppCompatActivity

    override fun onAttach(context: Context) {
        super.onAttach(context)

        if (context is AppCompatActivity) activityInFragment = context
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentHomeBinding.inflate(inflater)

        return binding.root
    }

    @ExperimentalCoroutinesApi
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding) {
            addFloatingActionButton.setOnClickListener { showMemoCreate() }
            profileToolBarLayout.apply {
                homeMaterialToolBar.apply {
                    setNavigationOnClickListener { showProfile() }

                    homeViewModel.userLiveData.observe(viewLifecycleOwner) { userInfo ->
                        userInfo ?: return@observe

                        loadDrawable(binding, userInfo.imageUri) { navigationIcon = it }
                    }
                }

                filterListImageButton.setOnClickListener {
                    val onEachChipClickListener = object : OnChipClickListener {
                        override fun onClick(category: Category) {
                            homeViewModel.filter(category)
                        }
                    }

                    showDialog(onEachChipClickListener)
                }
            }

            val textAdapter = TextAdapter(viewLifecycleOwner, listeners())
            recyclerView.adapter = textAdapter
            homeViewModel.filteredTextListLiveData.observe(viewLifecycleOwner) { textAdapter.submitList(it) }
        }
    }

    private fun showProfile() {
        findNavController().navigate(R.id.action_homeFragment_to_profileFragment)
    }

    private fun showMemoCreate() {
        findNavController().navigate(R.id.action_homeFragment_to_memoCreateFragment)
    }

    private fun showDialog(onChipClickListener: OnChipClickListener) {
        CategoryFilterBottomSheet(onChipClickListener).apply {
            show(activityInFragment.supportFragmentManager, CategoryFilterBottomSheet.TAG)
        }
    }

    private fun loadDrawable(
        viewBinding: ViewBinding,
        imageUri: Uri?,
        consumer: (Drawable) -> Unit
    ) {
        Glide.with(viewBinding.root).asDrawable()
            .load(imageUri)
            .circleCrop()
            .into(object : CustomTarget<Drawable>() {
                override fun onResourceReady(
                    resource: Drawable,
                    transition: Transition<in Drawable>?
                ) {

                    consumer(resource)
                }

                override fun onLoadCleared(placeholder: Drawable?) {}
            })
    }

    @ExperimentalCoroutinesApi
    private fun listeners(): TextAdapter.Listeners = object : TextAdapter.Listeners {

        override val onAppearListener: OnAppearListener
            get() = { binding, text ->
                when (text) {
                    is Text.Left -> {
                        val draft = text.value

                        with(homeViewModel) {

                            val liveData = timeLiveData(draft)
                            binding.timeLiveData = liveData
                        }
                    }

                    is Text.Right -> {
                        val memo = text.value

                        binding.totalTime = memo.time
                    }
                }
            }

        override val onItemClickListener: View.OnClickListener
            get() = View.OnClickListener {
                val action =
                    HomeFragmentDirections.actionHomeFragmentToMemoEditFragment("test_id")
                findNavController().navigate(action)
            }

        override val onTimerClickListener: OnTimerClickListener
            get() = { text ->
                when (text) {
                    is Text.Left -> {
                        val draft = text.value

                        with(homeViewModel) {
                            val currentSeconds = TimeUnit.MILLISECONDS.toSeconds(
                                System.currentTimeMillis() - draft.startTime
                            )

                            saveMemo(draft.toMemo(currentSeconds))
                            deleteDraft(draft)
                        }
                    }

                    is Text.Right -> {
                        val memo = text.value

                        homeViewModel.saveDraft(memo.toDraft())
                    }
                }
            }

    }
}