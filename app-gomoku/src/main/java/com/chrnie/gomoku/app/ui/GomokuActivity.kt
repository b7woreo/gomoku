package com.chrnie.gomoku.app.ui

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.graphics.Point
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.view.View.*
import com.chrnie.gomoku.Chessman
import com.chrnie.gomoku.app.R
import com.chrnie.gomoku.app.const.BANNER_ID
import com.chrnie.gomoku.app.const.INTERSTITIAL_ID
import com.chrnie.gomoku.app.view.model.GomokuViewModel
import com.google.android.gms.ads.*
import kotlinx.android.synthetic.main.activity_gomoku.*

class GomokuActivity : AppCompatActivity() {

    companion object {
        const val TAG = "GomokuActivity"
    }

    private val viewModel by lazy { ViewModelProviders.of(this)[GomokuViewModel::class.java] }
    private val adView by lazy {
        AdView(this).apply {
            adSize = AdSize.BANNER
            adUnitId = BANNER_ID
            adListener = object : AdListener() {
                override fun onAdLoaded() {
                    Log.i(TAG, "ad view loaded")
                }

                override fun onAdFailedToLoad(p0: Int) {
                    Log.i(TAG, "ad view load fail")
                    loadAd(this@apply)
                }

                override fun onAdClosed() {
                    loadAd(this@apply)
                }
            }
        }
    }

    private val interstitialAd by lazy {
        InterstitialAd(this).apply {
            adUnitId = INTERSTITIAL_ID
            adListener = object : AdListener() {
                override fun onAdLoaded() {
                    Log.i(TAG, "interstitial ad loaded")
                    super.onAdLoaded()
                }

                override fun onAdFailedToLoad(p0: Int) {
                    Log.i(TAG, "interstitial ad load fail")
                    loadAd(this@apply)
                }

                override fun onAdClosed() {
                    loadAd(this@apply)
                }
            }
        }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gomoku)

        viewModel.difficalty.observe(this, Observer {
            when (it!!) {
                GomokuViewModel.DIFFICALTY_EASY -> R.string.difficalty_easy
                GomokuViewModel.DIFFICALTY_MID -> R.string.difficalty_mid
                GomokuViewModel.DIFFICALTY_HIGH -> R.string.difficalty_hard
                else -> throw RuntimeException("unknown difficalty")
            }.let {
                btnDifficalty.setText(it)
            }
        })
        viewModel.game.observe(this, Observer { gomokuView.game = it })
        viewModel.canUndo.observe(this, Observer { btnUndo.isEnabled = it!! })
        viewModel.lastPutPoint.observe(this, Observer { gomokuView.highlightPoint = it?.let { Point(it.x, it.y) } })
        viewModel.currentChessman.observe(this, Observer {
            gomokuView.isEnabled = it == Chessman.BLACK

            llPlayer.scaleX = if (it == Chessman.BLACK) 1.2f else 1.0f
            llPlayer.scaleY = if (it == Chessman.BLACK) 1.2f else 1.0f

            llCom.scaleX = if (it == Chessman.WHITE) 1.2f else 1.0f
            llCom.scaleY = if (it == Chessman.WHITE) 1.2f else 1.0f
        })
        viewModel.winner.observe(this, Observer {
            if (it == null) {
                gomokuView.isEnabled = true
                tvGameMessage.visibility = GONE
                return@Observer
            }

            gomokuView.isEnabled = false
            tvGameMessage.visibility = View.VISIBLE

            val winner = if (it == Chessman.BLACK) getString(R.string.player) else getString(R.string.computer)
            tvGameMessage.text = getString(R.string.game_success, winner)
        })
        viewModel.displayInterstitialAd.observe(this, Observer { it!!.consume()?.run { interstitialAd.show() } })

        gomokuView.onPutChessmanListener = { x, y -> viewModel.putChessman(x, y) }

        btnRestart.setOnClickListener { viewModel.restart() }
        btnUndo.setOnClickListener { viewModel.undo() }
        btnDifficalty.setOnClickListener {
            val selectedIndex = when (viewModel.difficalty.value!!) {
                GomokuViewModel.DIFFICALTY_EASY -> 0
                GomokuViewModel.DIFFICALTY_MID -> 1
                GomokuViewModel.DIFFICALTY_HIGH -> 2
                else -> throw RuntimeException("unknown difficalty")
            }

            AlertDialog.Builder(this)
                    .setTitle(R.string.title_difficalty)
                    .setSingleChoiceItems(R.array.item_difficalty, selectedIndex) { dialog, which ->
                        when (which) {
                            0 -> GomokuViewModel.DIFFICALTY_EASY
                            1 -> GomokuViewModel.DIFFICALTY_MID
                            2 -> GomokuViewModel.DIFFICALTY_HIGH
                            else -> throw RuntimeException("unknown difficalty")
                        }.let {
                            viewModel.setDifficalty(it)
                        }
                        dialog.dismiss()
                    }
                    .show()
        }

        flBanner.addView(adView)
        loadAd(adView)
        loadAd(interstitialAd)
    }

    override fun onResume() {
        super.onResume()
        gomokuView.systemUiVisibility = (gomokuView.systemUiVisibility
                or SYSTEM_UI_FLAG_LAYOUT_STABLE
                or SYSTEM_UI_FLAG_LOW_PROFILE
                )
    }

    private fun loadAd(adView: AdView) {
        AdRequest.Builder().build().let {
            adView.loadAd(it)
        }
    }

    private fun loadAd(interstitialAd: InterstitialAd) {
        AdRequest.Builder().build().let {
            interstitialAd.loadAd(it)
        }
    }
}