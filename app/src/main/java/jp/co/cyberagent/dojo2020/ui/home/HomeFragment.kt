package jp.co.cyberagent.dojo2020.ui.home

import android.graphics.drawable.Drawable
import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.viewbinding.ViewBinding
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import dagger.hilt.android.AndroidEntryPoint
import jp.co.cyberagent.dojo2020.R
import jp.co.cyberagent.dojo2020.data.model.Text
import jp.co.cyberagent.dojo2020.databinding.FragmentHomeBinding
import kotlinx.coroutines.ExperimentalCoroutinesApi
import java.util.concurrent.TimeUnit

@AndroidEntryPoint
class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding
    private val homeViewModel by viewModels<HomeViewModel>()

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

                        getDrawable(binding, userInfo.imageUri) { navigationIcon = it }
                    }
                }

                filterListImageButton.setOnClickListener {

                }
            }

            val textAdapter = TextAdapter(viewLifecycleOwner, listeners())
            recyclerView.adapter = textAdapter
            homeViewModel.textListLiveData.observe(viewLifecycleOwner) { textAdapter.submitList(it) }
        }
    }

    private fun showProfile() {
        findNavController().navigate(R.id.action_homeFragment_to_profileFragment)
    }

    private fun showMemoCreate() {
        findNavController().navigate(R.id.action_homeFragment_to_memoCreateFragment)
    }

    private fun getDrawable(
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