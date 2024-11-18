package com.tghrsyahptra.dicodingeventapp.ui.activities

import android.net.Uri
import android.os.Bundle
import android.text.Html
import android.text.method.LinkMovementMethod
import android.view.MenuItem
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.browser.customtabs.CustomTabsIntent
import com.bumptech.glide.Glide
import com.tghrsyahptra.dicodingeventapp.R
import com.tghrsyahptra.dicodingeventapp.data.local.entity.EventEntity
import com.tghrsyahptra.dicodingeventapp.databinding.ActivityDetailBinding
import com.tghrsyahptra.dicodingeventapp.ui.viewmodels.MainViewModel
import com.tghrsyahptra.dicodingeventapp.ui.viewmodels.ViewModelFactory
import java.text.SimpleDateFormat
import java.util.Locale

class DetailActivity : AppCompatActivity() {

    private lateinit var binding: ActivityDetailBinding

    private val customTabs by lazy { CustomTabsIntent.Builder().build() }
    private val viewModel by viewModels<MainViewModel> {
        ViewModelFactory.getInstance(application)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)

        configureToolbar()

        val event = intent.getParcelableExtra<EventEntity>("EXTRA_EVENT")
        event?.let { displayEventDetails(it) }
    }

    private fun configureToolbar() {
        setSupportActionBar(binding.toolbar)
        supportActionBar?.apply {
            setDisplayHomeAsUpEnabled(true)
            setDisplayShowHomeEnabled(true)
        }
    }

    private fun displayEventDetails(event: EventEntity) {
        binding.apply {
            updateFavoriteIcon(event.isFavorite)

            titleTextView.text = event.name
            ownerTextView.text = getString(R.string.organized_by, event.ownerName)
            remainingQuotaTextView.text = getString(
                R.string.remaining_quota,
                event.quota?.minus(event.registrants ?: 0).toString()
            )
            beginTimeTextView.text = formatEventDate(event.beginTime)
            summaryTextView.text = event.summary
            descriptionTextView.apply {
                text = Html.fromHtml(event.description, Html.FROM_HTML_MODE_COMPACT)
                movementMethod = LinkMovementMethod.getInstance()
            }

            // Set additional fields here
            categoryTextView.text = event.category ?: "Category not specified"
            cityTextView.text = event.cityName ?: "City not specified"
            endTimeTextView.text = formatEventDate(event.endTime)


            Glide.with(this@DetailActivity).load(event.mediaCover).into(imageView)

            favoriteFab.setOnClickListener { toggleEventFavorite(event) }
            webpageFab.setOnClickListener { openEventWebPage(event.link) }
        }
    }


    private fun formatEventDate(dateString: String?): String? {
        return dateString?.let {
            val inputFormatter = SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.getDefault())
            val outputFormatter = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
            inputFormatter.parse(it)?.let { date -> outputFormatter.format(date) }
        }
    }

    private fun updateFavoriteIcon(isFavorite: Boolean?) {
        binding.favoriteFab.setImageResource(
            if (isFavorite == true) R.drawable.ic_favorite else R.drawable.ic_favorite_border
        )
    }

    private fun toggleEventFavorite(event: EventEntity) {
        event.isFavorite = !(event.isFavorite ?: false)
        updateFavoriteIcon(event.isFavorite)

        if (event.isFavorite == true) {
            viewModel.saveEvents(event)
        } else {
            viewModel.deleteEvents(event)
        }
    }

    private fun openEventWebPage(url: String?) {
        url?.let {
            customTabs.launchUrl(this, Uri.parse(it))
        }
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            android.R.id.home -> {
                finish()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }
}