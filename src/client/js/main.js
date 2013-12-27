// TODO(jasonpr): Figure out the correct way to do classes.
// Currently, I'm just following the (bad?) example of all the
// other files, which I wrote a year ago, for the sake of
// consistency.
function ServerLink(moveCallback) {
  this.ws = new WebSocket('ws://' + location.hostname + ':8080');
  this.ws.onopen = function() {
    console.log('CONNECT');
  };

  this.ws.onclose = function() {
    console.log('DISCONNET');
  };

  this.ws.onmessage = function(event) {
    console.log('RECEIVED: ' + event.data);
    moveCallback(event.data);
  };

  this.ws.onerror = function() {
    console.log('ERROR');
  };
};

ServerLink.prototype.sendMove = function(startFile, startRank,
                                         endFile, endRank, promotionType, castles) {
  var message = "" + startFile + startRank + endFile + endRank;
  if (promotionType) {
    var typeLetter;
    if (promotionType == "knight") {
      typeLetter = "N";
    } else if (promotionType == "bishop") {
      typeLetter = "B";
    } else if (promotionType == "rook") {
      typeLetter = "R";
    } else if (promotionType == "queen") {
      typeLetter = "Q";
    } else {
      throw "Invalid promotion type.";
    }
    message += typeLetter;
  }
  if (castles) {
    message += "C";
  }

  this.ws.send(message);
};
