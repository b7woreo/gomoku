package com.chrnie.gomoku.app.view.model

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.MutableLiveData
import android.os.Handler
import android.os.Looper
import android.support.annotation.GuardedBy
import android.util.Log
import com.chrnie.gomoku.Chessman
import com.chrnie.gomoku.GomokuGame
import com.chrnie.gomoku.ai.GomokuAi
import com.chrnie.gomoku.ai.Point
import com.chrnie.gomoku.app.model.Setting
import java.util.concurrent.Executors

class GomokuViewModel(app: Application) : AndroidViewModel(app) {

    companion object {
        private val TAG = "GomokuViewModel"

        const val DIFFICALTY_EASY = 2
        const val DIFFICALTY_MID = 4
        const val DIFFICALTY_HIGH = 6

        private val mainHandler = Handler(Looper.getMainLooper())
        private val calculateExecutor = Executors.newSingleThreadExecutor {
            Thread(it, "gomoku-ai-thread")
        }
    }

    private val _setting = Setting(app)
    private var _game: GomokuGame

    @GuardedBy("this")
    private lateinit var _gameAi: GomokuAi

    val difficalty = MutableLiveData<Int>()
    val game = MutableLiveData<GomokuGame>()
    val canUndo = MutableLiveData<Boolean>()
    val lastPutPoint = MutableLiveData<Point>()
    val currentChessman = MutableLiveData<Chessman>()
    val winner = MutableLiveData<Chessman>()

    init {
        difficalty.value = _setting.difficalty
        _game = GomokuGame()
        synchronized(this) { _gameAi = GomokuAi(GomokuGame(), difficalty.value!!) }

        updateView(null)
    }

    fun setDifficalty(difficalty: Int) {
        _setting.difficalty = difficalty
        this.difficalty.value = difficalty

        _game = GomokuGame()
        synchronized(this) { _gameAi = GomokuAi(GomokuGame(), difficalty) }
        restart()
    }

    fun putChessman(x: Int, y: Int) {
        if (_game.chessman != Chessman.BLACK) {
            Log.w(TAG, "not player round")
            return
        }

        if (!putChessmanInternal(x, y)) {
            return
        }

        updateView(Point(x, y))

        if (!_game.isWin) {
            calculateExecutor.submit { aiPutChessman() }
        }
    }

    fun undo() {
        do {
            val playerSuccess = _game.undo()
            val aiSuccess = synchronized(this) { _gameAi.game.undo() }

            if (aiSuccess != playerSuccess) {
                throw RuntimeException("invalid state: ai action:$aiSuccess - game action: $playerSuccess")
            }
        } while (_game.chessman != Chessman.BLACK)

        updateView(null)
    }

    fun restart() {
        _game.restart()
        synchronized(this) { _gameAi.game.restart() }
        updateView(null)
    }

    private fun putChessmanInternal(x: Int, y: Int): Boolean {
        val playerSuccess = _game.putChessman(x, y)
        val aiSuccess = synchronized(this) { _gameAi.game.putChessman(x, y) }

        if (aiSuccess != playerSuccess) {
            throw RuntimeException("invalid state: ai action:$aiSuccess - game action: $playerSuccess")
        }

        return playerSuccess
    }

    private fun updateView(point: Point?) {
        game.value = _game
        canUndo.value = _game.canUndo
        lastPutPoint.value = point
        currentChessman.value = _game.chessman
        winner.value = _game.winner
    }

    private fun aiPutChessman() {
        val p = synchronized(this) { _gameAi.next() }

        mainHandler.post {
            if (!putChessmanInternal(p.x, p.y)) {
                throw RuntimeException("ai error")
            }
            updateView(p)
        }
    }
}