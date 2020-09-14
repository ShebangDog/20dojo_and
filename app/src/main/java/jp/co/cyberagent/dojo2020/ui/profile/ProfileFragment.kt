package jp.co.cyberagent.dojo2020.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.lifecycle.observe
import androidx.navigation.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import jp.co.cyberagent.dojo2020.databinding.FragmentProfileBinding
import jp.co.cyberagent.dojo2020.ui.UserAdapter
import kotlinx.coroutines.FlowPreview

class ProfileFragment : Fragment() {
    private lateinit var binding: FragmentProfileBinding

    private val profileViewModel: ProfileViewModel by viewModels {
        ProfileViewModelFactory(this, Bundle(), requireContext())
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        binding = FragmentProfileBinding.inflate(inflater)

        return binding.root
    }

    @FlowPreview
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding) {
            profileToolBarLayout.profileMaterialToolBar.setNavigationOnClickListener { view ->
                view.findNavController().navigateUp()
            }

            val profileAdapter = UserAdapter()
            profileViewModel.userItemListLiveData.observe(viewLifecycleOwner) {
                profileRecyclerView.apply {
                    layoutManager = LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)

                    adapter = profileAdapter.apply {
                        userItemList = it
                    }
                }
            }

        }
    }
}