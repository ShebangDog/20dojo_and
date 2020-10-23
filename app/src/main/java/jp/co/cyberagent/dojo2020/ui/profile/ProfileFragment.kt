package jp.co.cyberagent.dojo2020.ui.profile

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.findNavController
import com.github.mikephil.charting.data.PieData
import dagger.hilt.android.AndroidEntryPoint
import jp.co.cyberagent.dojo2020.R
import jp.co.cyberagent.dojo2020.databinding.FragmentProfileBinding
import jp.co.cyberagent.dojo2020.ui.ext.showImage
import jp.co.cyberagent.dojo2020.util.Utility
import kotlinx.coroutines.FlowPreview

@AndroidEntryPoint
class ProfileFragment : Fragment() {
    private lateinit var binding: FragmentProfileBinding

    private val profileViewModel by viewModels<ProfileViewModel>()

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

            profileToolBarLayout.profileMaterialToolBar.setOnMenuItemClickListener {
                when (it.itemId) {
                    R.id.share_item -> true
                    else -> super.onOptionsItemSelected(it)
                }
            }

            profileViewModel.firebaseUserInfoLiveData.observe(viewLifecycleOwner) { firebaseUserInfo ->
                with(primaryAccountLayout) {
                    iconImageButton.showImage(firebaseUserInfo?.imageUri) { circleCrop() }

                    nameTextView.text = firebaseUserInfo?.name
                }
            }

            profileViewModel.graphLiveData(
                "",
                ProfileViewModel.ValueView.Default
            ).observe(viewLifecycleOwner) {

                totalTimeLayout.apply {
                    nameTextView.text = getString(R.string.total_time)
                    valueTextView.text = Utility.millsToFormattedTime(it.totalTime)
                }

                analyticGraphLayout.timeEachCategoryGraphPieChart.apply {
                    data = PieData(it.pieDataSet)
                    description.isEnabled = false
                    centerText = "TotalTime\n${Utility.millsToFormattedTime(it.totalTime)}"

                    setCenterTextSize(24f)

                    animateY(750)
                    invalidate()
                }
            }
        }
    }
}