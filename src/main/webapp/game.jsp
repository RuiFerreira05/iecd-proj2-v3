<!DOCTYPE html>
<html>
<head>
    <title>GoBang Game</title>
    <meta charset="utf-8" />
    <meta name="viewport" content="width=device-width, initial-scale=1, shrink-to-fit=no" />

    <link rel="stylesheet" href="static/css/game.css" />
    <style>
        body {
            background: <%= session.getAttribute("playerColor") != null ? session.getAttribute("playerColor") : "#FFFFFF" %>;
        }
    </style>
    <script type="module" src="static/js/game.js"></script>
</head>
<body>
    <section class="title-section">
        <div class="title-container">
            Your Turn!
        </div>
    </section>
    <section class="board-section">
        <div class="board-container">
            <canvas id="board-canvas" width="400" height="400"></canvas>
        </div>
    </section>
    <section class="options-section">
        <div class="button-container">
            <a href="index.jsp"><button type="button" class="button-exit">Exit</button></a>
        </div>
    </section>
</body>
</html>
