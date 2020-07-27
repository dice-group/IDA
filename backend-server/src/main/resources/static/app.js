var stompClient = null;
var socket = null;
var shortName = "";

function setConnected(connected) {
    $("#connect").prop("disabled", connected);
    $("#disconnect").prop("disabled", !connected);
    if (connected) {
        $("#conversation").show();
    }
    else {
        $("#conversation").hide();
    }
    $("#chatMessages").html("");
}

function connect() {
    console.log("connect")
    // create the SockJS WebSocket-like object
	socket = new SockJS('/ida-fb-ws');

	// specify that we're using the STOMP protocol on the socket
    stompClient = Stomp.over(socket);

    // implement the behavior we want whenever the client connects to the server (-or- user connects to chat app client by joining a group)
    stompClient.connect({}, function (frame) {
        setConnected(true);

        stompClient.subscribe("/topic/msgs", function (greeting) {
            showMessage(JSON.parse(greeting.body).content);
        });

        stompClient.subscribe('/topic/errors', function (greeting) {
            showErrors(JSON.parse(greeting.body).content);
        });

    });

}

function disconnect() {
    if (stompClient !== null) {
    	$("#members").append("<tr><td>" + shortName + " just left</td></tr>");
        stompClient.disconnect();
    }
    setConnected(false);
}


function sendMessage() {
  stompClient.send("/ida/msg", {}, JSON.stringify({'message': $("#message").val()}));
}

function showMessage(message) {
    $("#chatMessages").append("<tr><td>" + message + "</td></tr>");
    $("#typingUpdates").html("<tr><td>&nbsp;</td></tr>");
    $("#message").val("");
}


function showErrors(message) {
	$("#errorMessages").html("<tr><td>" + message + "</td></tr>");
}

$(function () {
    $("form").on('submit', function (e) {
        e.preventDefault();
    });

    $( "#connect" ).click(function() { connect(); });

    $( "#disconnect" ).click(function() { disconnect(); });

    $( "#send" ).click(function() { sendMessage(); });
});

