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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.viewbinding.ViewBinding
import com.bumptech.glide.Glide
import com.bumptech.glide.request.target.CustomTarget
import com.bumptech.glide.request.transition.Transition
import dagger.hilt.android.AndroidEntryPoint
import jp.co.cyberagent.dojo2020.R
import jp.co.cyberagent.dojo2020.data.model.Category
import jp.co.cyberagent.dojo2020.data.model.Memo
import jp.co.cyberagent.dojo2020.data.model.Text
import jp.co.cyberagent.dojo2020.databinding.FragmentHomeBinding
import kotlinx.coroutines.ExperimentalCoroutinesApi
import java.util.concurrent.TimeUnit
import kotlin.random.Random

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

        val linearLayoutManager = LinearLayoutManager(
            context,
            LinearLayoutManager.VERTICAL,
            false
        )

        val memoAdapter = TextAdapter(listeners())

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
                    homeViewModel.saveMemo(
                        Memo.createMemo(
                            "Title" + Random.nextInt(30),
                            "contents",
                            0,
                            Category("Kotlin")
                        )
                    )
                }
            }

            recyclerView.apply {
                setHasFixedSize(true)
                layoutManager = linearLayoutManager
                adapter = memoAdapter
            }

            homeViewModel.textListLiveData.observe(viewLifecycleOwner) { textList ->
                memoAdapter.textList = textList
            }
        }
    }

    private var remove: () -> Unit = {}

    override fun onStop() {
        super.onStop()

        remove()
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
        private fun millsToFormattedTime(totalTime: Long): String {
            val hours = TimeUnit.SECONDS.toHours(totalTime)
            val minutes = TimeUnit.SECONDS.toMinutes(totalTime - TimeUnit.HOURS.toSeconds(hours))
            val seconds =
                totalTime - TimeUnit.HOURS.toSeconds(hours) - TimeUnit.MINUTES.toSeconds(minutes)

            return listOf(hours, minutes, seconds)
                .map { it.toString() }
                .joinToString(":") { if (it.length == 1) "0$it" else it }
        }

        override val onAppearListener: OnAppearListener
            get() = { binding, text ->
                when (text) {
                    is Text.Left -> {
                        val draft = text.value
                        val currentSeconds = TimeUnit.MILLISECONDS.toSeconds(
                            System.currentTimeMillis() - draft.startTime
                        )

                        homeViewModel.timeLiveData(currentSeconds).apply {
                            observe(viewLifecycleOwner) {
                                binding.timeTextView.text = millsToFormattedTime(it)
                            }
                        }
                    }

                    is Text.Right -> {
                        val memo = text.value

                        null
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
            get() = { text, timeLiveData ->
                when (text) {
                    is Text.Left -> {
                        val draft = text.value

                        timeLiveData?.removeObservers(viewLifecycleOwner)
                        timeLiveData?.value?.also {
                            homeViewModel.saveMemo(draft.toMemo(it))
                            homeViewModel.deleteDraft(draft)
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