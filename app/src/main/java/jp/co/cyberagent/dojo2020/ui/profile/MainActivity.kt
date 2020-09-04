package jp.co.cyberagent.dojo2020.ui.profile

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import jp.co.cyberagent.dojo2020.R
import jp.co.cyberagent.dojo2020.databinding.ActivityMainBinding


class MainActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        with(binding) {
            goProfileButton.setOnClickListener {
                supportFragmentManager.beginTransaction()
                    .replace(R.id.fragment_profile, ProfileFragment())
                    .commit()
            }
        }
    }
}