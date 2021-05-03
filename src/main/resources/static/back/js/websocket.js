function openWebSocket(usid) {
	var socket;
	if (typeof(WebSocket) == undefined) {
		alert("对不起，您的浏览器暂不支持WebSocket通信...");
		return;
	}
	
	socket = new WebSocket("ws://127.0.0.1:8888/websocket/" + usid);
	
	socket.onopen = function() {
		console.info("Socket已连接...");
	}
	
	socket.onclose = function() {
		console.info("socket已断开...");
	}
	
	socket.error = function() {
		console.info("socket服务器连接失败...");
	}
	
	socket.onmessage = function(msg) {
		console.info(msg);
		if (msg.data == "101") { // 说明是挤退的信息
			alert("对不起，您的账号已经在其它地方登录，若非本人操作，请及时修改密码...");
			// 最好是先发到后台，清空session数据
			location.href="../index.html";
		}
	}
}