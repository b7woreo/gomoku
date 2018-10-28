package com.chrnie.gomoku.app.view.model

import android.app.Application
import android.arch.lifecycle.AndroidViewModel
import android.arch.lifecycle.MutableLiveData
import android.support.annotation.GuardedBy
import android.util.Log
import com.chrnie.gomoku.Chessman
import com.chrnie.gomoku.GomokuGame
import com.chrnie.gomoku.ai.GomokuAi
import com.chrnie.gomoku.ai.Point
import com.chrnie.gomoku.app.common.Event
import com.chrnie.gomoku.app.model.Setting
import kotlinx.coroutines.experimental.android.UI
import kotlinx.coroutines.experimental.launch
import kotlinx.coroutines.experimental.runBlocking
import kotlinx.coroutines.experimental.sync.Mutex
import kotlinx.coroutines.experimental.sync.withLock

class GomokuViewModel(app: Application) : AndroidViewModel(app) {

    companion object {
        private val TAG = "GomokuViewModel"

        const val DIFFICULTY_EASY = 1
        const val DIFFICULTY_MID = 2
        const val DIFFICULTY_HIGH = 3
    }

    private val _setting = Setting(app)
    private var _game: GomokuGame

    private val _mutex = Mutex()
    @GuardedBy("_mutex")
    private lateinit var _gameAi: GomokuAi

    val difficulty = MutableLiveData<Int>()
    val game = MutableLiveData<GomokuGame>()
    val canUndo = MutableLiveData<Boolean>()
    val lastPutPoint = MutableLiveData<Point>()
    val currentChessman = MutableLiveData<Chessman>()
    val winner = MutableLiveData<Chessman>()
    val displayInterstitialAd = MutableLiveData<Event<Unit>>()

    init {
        difficulty.value = _setting.difficalty
        _game = GomokuGame()
        runBlocking { _mutex.withLock { _gameAi = GomokuAi(GomokuGame(), difficulty.value!!) } }
        synchronized(this) { }

        updateView(null)
    }

    fun setDifficulty(difficulty: Int) {
        _setting.difficalty = difficulty
        this.difficulty.value = difficulty

        _game = GomokuGame()
        runBlocking { _mutex.withLock { _gameAi = GomokuAi(GomokuGame(), difficulty) } }
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
            aiPutChessman()
        }
    }

    fun undo() {
        do {
            val playerSuccess = _game.undo()
            val aiSuccess = runBlocking { _mutex.withLock { _gameAi.game.undo() } }

            if (aiSuccess != playerSuccess) {
                throw RuntimeException("invalid state: ai action:$aiSuccess - game action: $playerSuccess")
            }
        } while (_game.chessman != Chessman.BLACK)

        updateView(null)
    }

    fun restart() {
        displayInterstitialAd.value = Event(Unit)
        _game.restart()
        runBlocking { _mutex.withLock { _gameAi.game.restart() } }
        updateView(null)
    }

    private fun putChessmanInternal(x: Int, y: Int): Boolean {
        val playerSuccess = _game.putChessman(x, y)
        val aiSuccess = runBlocking { _mutex.withLock { _gameAi.game.putChessman(x, y) } }

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

    private fun aiPutChessman() = launch {
        val p = _mutex.withLock { _gameAi.next() }
        launch(UI) {
            if (!putChessmanInternal(p.x, p.y)) {
                throw RuntimeException("ai error")
            }
            updateView(p)
        }
    }
}