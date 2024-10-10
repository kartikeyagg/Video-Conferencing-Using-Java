let stompClient;
function connect() {
	console.log("Connecting... ") ;
    const socket = new SockJS('/ws');
	console.log("Connected there ") ;
    stompClient = Stomp.over(socket);
}
connect();  // Initiate WebSocket connection
