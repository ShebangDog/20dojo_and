package jp.co.cyberagent.dojo2020.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import jp.co.cyberagent.dojo2020.R
import jp.co.cyberagent.dojo2020.databinding.FragmentProfileBinding

class ProfileFragment : Fragment() {


    private lateinit var binding: FragmentProfileBinding
    private val viewModel: ProfileViewModel by viewModels()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

        return inflater.inflate(R.layout.fragment_profile, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        with(binding) {
            viewModel.liveData.observe(viewLifecycleOwner) {
                profileUserName.text = it.name
                profileTotalTime.text = it.totalTime
            }
            reloadButton.setOnClickListener {
                viewModel.fetchUserData()
            }
        }

    }
}
