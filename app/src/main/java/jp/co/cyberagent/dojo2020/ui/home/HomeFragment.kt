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
import jp.co.cyberagent.dojo2020.R
import jp.co.cyberagent.dojo2020.data.model.Category
import jp.co.cyberagent.dojo2020.data.model.Memo
import jp.co.cyberagent.dojo2020.databinding.FragmentHomeBinding
import jp.co.cyberagent.dojo2020.ui.TextAdapter
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlin.random.Random

class HomeFragment : Fragment() {
    private lateinit var binding: FragmentHomeBinding
    private val homeViewModel by viewModels<HomeViewModel> {
        HomeViewModelFactory(this, Bundle(), requireContext())
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

                        getDrawable(binding, userInfo.imageUri) { navigationIcon = it }
                    }
                }

                filterListImageButton.setOnClickListener {
                    homeViewModel.saveMemo(
                        Memo.createWithOutId(
                            "Title" + Random.nextInt(30),
                            "contents",
                            0,
                            Category("Kotlin")
                        )
                    )
                }
            }

            val linearLayoutManager = LinearLayoutManager(
                context,
                LinearLayoutManager.VERTICAL,
                false
            )

            val memoAdapter = TextAdapter {
                val action = HomeFragmentDirections.actionHomeFragmentToMemoEditFragment("test_id")
                findNavController().navigate(action)
            }

            homeViewModel.textListLiveData.observe(viewLifecycleOwner) { textList ->
                memoAdapter.textList = textList
            }

            recyclerView.apply {
                setHasFixedSize(true)
                layoutManager = linearLayoutManager
                adapter = memoAdapter
            }
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

}