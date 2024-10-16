
const localVideo = document.getElementById('localVideo');
const remoteVideo = document.getElementById('remoteVideo');
let localStream;
let peerConnection;

// Get local video/audio stream
navigator.mediaDevices.getUserMedia({ video: true, audio: true })
    .then(stream => {
        localStream = stream;
        localVideo.srcObject = stream;
        // Initialize peer connection after acquiring local media
        initPeerConnection();
    });

function initPeerConnection() {
    const configuration = { iceServers: [{ urls: 'stun:stun.l.google.com:19302' }] };
    peerConnection = new RTCPeerConnection(configuration);

    // Add local stream to the connection
    localStream.getTracks().forEach(track => peerConnection.addTrack(track, localStream));

    // Handle remote stream
    peerConnection.ontrack = event => {
        remoteVideo.srcObject = event.streams[0];
    };

    // Handle ICE candidates
    peerConnection.onicecandidate = event => {
        if (event.candidate) {
            // Send candidate to the signaling server
            stompClient.send("/app/ice-candidate", {}, JSON.stringify(event.candidate));
        }
    };
}

// Signaling (WebSocket for signaling)
let stompClient;
function connect() {
	console.log("Connecting... ") ;
    const socket = new SockJS('http://127.0.0.0:8080/ws');
    stompClient = Stomp.over(socket);
    stompClient.connect({}, function () {
        stompClient.subscribe('/topic/video-offer', function (message) {
            handleOffer(JSON.parse(message.body));
        });

        stompClient.subscribe('/topic/video-answer', function (message) {
            handleAnswer(JSON.parse(message.body));
        });

        stompClient.subscribe('/topic/ice-candidate', function (message) {
            handleIceCandidate(JSON.parse(message.body));
        });
    });
}


window.createOffer = function () {
    console.log("Creating offer...");
    peerConnection.createOffer()
        .then(offer => {
            peerConnection.setLocalDescription(offer);
            stompClient.send("/app/video-offer", {}, JSON.stringify(offer));
        })
        .catch(error => {
            console.error('Error creating offer: ', error);
        });
}

// Handle Offer (Callee)
function handleOffer(offer) {
    peerConnection.setRemoteDescription(new RTCSessionDescription(offer));
    peerConnection.createAnswer()
        .then(answer => {
            peerConnection.setLocalDescription(answer);
            stompClient.send("/app/video-answer", {}, JSON.stringify(answer));
        });
}

// Handle Answer
function handleAnswer(answer) {
    peerConnection.setRemoteDescription(new RTCSessionDescription(answer));
}

// Handle ICE Candidate
function handleIceCandidate(candidate) {
    peerConnection.addIceCandidate(new RTCIceCandidate(candidate));
}

connect();  // Initiate WebSocket connection

