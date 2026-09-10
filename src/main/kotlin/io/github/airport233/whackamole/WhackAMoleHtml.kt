package io.github.airport233.whackamole

/** 游戏页面。JS 里不用模板字面量，避免与 Kotlin raw string 的 $ 插值冲突。 */
internal object WhackAMoleHtml {

    fun page(bridgeScript: String): String = """
        <!DOCTYPE html>
        <html>
        <head>
        <meta charset="utf-8">
        <style>
          :root { color-scheme: light dark; }
          body {
            font-family: sans-serif; text-align: center; margin: 16px;
            background: Canvas; color: CanvasText;
          }
          #hud { display: flex; justify-content: space-between; font-size: 15px; margin-bottom: 12px; }
          #grid { display: grid; grid-template-columns: repeat(3, 72px); gap: 8px; justify-content: center; }
          .hole {
            width: 72px; height: 72px; font-size: 36px; line-height: 1;
            border: 2px solid #8884; border-radius: 12px; background: #8881; cursor: pointer;
          }
          .hole:disabled { cursor: default; opacity: 0.5; }
          #start { margin-top: 14px; font-size: 14px; padding: 6px 18px; cursor: pointer; }
        </style>
        </head>
        <body>
          <div id="hud"><span id="score">Score: 0</span><span id="time">Time: 30</span></div>
          <div id="grid"></div>
          <button id="start">Start</button>
          <script>
            window.__wamPost = function(json) { $bridgeScript };

            var HOLES = 9, ROUND_SECONDS = 30, MOLE_INTERVAL_MS = 700;
            var grid = document.getElementById('grid');
            var scoreEl = document.getElementById('score');
            var timeEl = document.getElementById('time');
            var startBtn = document.getElementById('start');
            var holes = [];
            var moleAt = -1, score = 0, timeLeft = ROUND_SECONDS;
            var moleTimer = null, roundTimer = null;

            for (var i = 0; i < HOLES; i++) {
              var b = document.createElement('button');
              b.className = 'hole';
              b.disabled = true;
              b.textContent = ' ';
              b.dataset.index = i;
              b.addEventListener('click', function() { whack(Number(this.dataset.index)); });
              grid.appendChild(b);
              holes.push(b);
            }

            function moveMole() {
              if (moleAt >= 0) holes[moleAt].textContent = ' ';
              var next = Math.floor(Math.random() * HOLES);
              if (next === moleAt) next = (next + 1) % HOLES;
              moleAt = next;
              holes[next].textContent = '🐹';
            }

            function whack(index) {
              if (index !== moleAt) return;
              score++;
              holes[moleAt].textContent = ' ';
              moleAt = -1;
              updateHud();
              moveMole();
            }

            function updateHud() {
              scoreEl.textContent = 'Score: ' + score;
              timeEl.textContent = 'Time: ' + timeLeft;
            }

            function startRound() {
              score = 0; timeLeft = ROUND_SECONDS; moleAt = -1;
              holes.forEach(function(h) { h.disabled = false; h.textContent = ' '; });
              startBtn.disabled = true;
              updateHud();
              moveMole();
              moleTimer = setInterval(moveMole, MOLE_INTERVAL_MS);
              roundTimer = setInterval(function() {
                timeLeft--;
                if (timeLeft <= 0) { endRound(); return; }
                updateHud();
              }, 1000);
            }

            function endRound() {
              clearInterval(moleTimer);
              clearInterval(roundTimer);
              holes.forEach(function(h) { h.disabled = true; h.textContent = ' '; });
              moleAt = -1;
              timeEl.textContent = 'Final: ' + score;
              startBtn.textContent = 'Play again';
              startBtn.disabled = false;
              window.__wamPost('score:' + score);
            }

            startBtn.addEventListener('click', startRound);
          </script>
        </body>
        </html>
    """.trimIndent()
}
