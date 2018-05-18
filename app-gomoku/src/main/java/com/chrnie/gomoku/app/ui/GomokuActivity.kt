package com.chrnie.gomoku.app.ui

import android.arch.lifecycle.Observer
import android.arch.lifecycle.ViewModelProviders
import android.graphics.Point
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.View
import android.view.View.GONE
import android.view.View.SYSTEM_UI_FLAG_LOW_PROFILE
import com.chrnie.gomoku.Chessman
import com.chrnie.gomoku.app.R
import com.chrnie.gomoku.app.view.model.GomokuViewModel
import kotlinx.android.synthetic.main.activity_gomoku.*

class GomokuActivity : AppCompatActivity() {

    private val viewModel by lazy { ViewModelProviders.of(this)[GomokuViewModel::class.java] }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_gomoku)
        gomokuView.systemUiVisibility = (gomokuView.systemUiVisibility or SYSTEM_UI_FLAG_LOW_PROFILE)

        viewModel.game.observe(this, Observer { gomokuView.game = it })
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
        gomokuView.onPutChessmanListener = { x, y -> viewModel.putChessman(x, y) }

        btnRestart.setOnClickListener { viewModel.restart() }
        btnUndo.setOnClickListener { viewModel.undo() }
    }
}