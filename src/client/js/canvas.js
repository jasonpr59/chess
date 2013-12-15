$(document).ready(function(){
  init();
  $("#execute").click(function(){
    var commandInput = document.getElementById('command');
    executeCommand(commandInput.value);
    commandInput.value = "";
    return false;
  });
  $("#command").bind('keyup', function(e){
    if (e.which == 13){
      var commandInput = document.getElementById('command');
      executeCommand(commandInput.value);
      commandInput.value = "";
    }
    return false;
  });
  $("#board").bind('click', function(e){
    var x = e.offsetX;
    var y = e.offsetY;
    if (x == null){
      //Old jQuery Hack (I'm on a plane, without internet, and forgot to download the lastest jQuery version ahead of time :/)
      x = e.pageX - e.srcElement.offsetLeft;
      y = e.pageY - e.srcElement.offsetTop;
    }
    GameUI.click(x, y, this.width);
  });
});


var game;
var algebraicConverter;

function init(){
  game = new Game();
  GameUI = new GameUI(game, $("#board")[0], $("#history"), $("#toMove"));
  var hl = new Highlights();
  GameUI.draw(hl);
}

function executeCommand(command){
  var move = game.algebraicConverter.algToMove(command);
  game.move(move.x_o, move.y_o, move.x_d, move.y_d);
  GameUI.draw(new Highlights());
}


