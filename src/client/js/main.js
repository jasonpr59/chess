var log = function(msg) {
  document.getElementById('log').innerText += msg + '\n';
};

var ws = new WebSocket('ws://localhost:8080');
ws.onopen = function() {
  log('CONNECT');
};

ws.onclose = function() {
  log('DISCONNET');
};

ws.onmessage = function(event) {
  log('RECEIVED: ' + event.data);
};

ws.onerror = function() {
  log('ERROR');
};


(function() {
  var commandField = document.getElementById('command');
  var sendButton = document.getElementById('send');

  sendButton.onclick = function() {
    var command = commandField.value;
    log('SENT: ' + command);
    ws.send(command);
  };
})();
