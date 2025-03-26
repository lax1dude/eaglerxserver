"use strict";
window.serverMessageAPI = (function() {
	var channelOpen = null;
	var messageHandlers = [];
	window.addEventListener("message", function(evt) {
		var dat = evt.data;
		if((typeof dat === "object") && dat.ver === 1 && (typeof dat.type === "string") && (typeof dat.channel === "string") && dat.channel.length > 0) {
			for(var i = 0; i < messageHandlers.length; ++i) {
				messageHandlers[i](dat);
			}
		}
	});
	var ServerMessageAPIError = function(message) {
		this.name = "ServerMessageAPIError";
		this.message = message;
	};
	ServerMessageAPIError.prototype = Error.prototype;
	var openCh = function(chName) {
		if(channelOpen !== null) throw new ServerMessageAPIError("Cannot open multiple channels, this feature is not supported!");
		channelOpen = chName;
		window.parent.postMessage({ver:1,channel:chName,open:true}, "*");
	};
	var closeCh = function(chName) {
		if(channelOpen !== chName) throw new ServerMessageAPIError("Cannot close channel \"" + chName + "\", that channel is not open!");
		channelOpen = null;
		window.parent.postMessage({ver:1,channel:chName,open:false}, "*");
	};
	var addListener = function(name, handler) {
		if(name === "message") messageHandlers.push(handler);
	};
	var remListener = function(name, handler) {
		if(name === "message") messageHandlers = messageHandlers.filter(function(o) { return o !== handler; });
	};
	var fixTypedArray = function(arr) {
		if(arr.length === arr.buffer.byteLength) {
			return arr.buffer;
		}else {
			var toSend = (data instanceof Uint8Array) ? new Uint8Array(arr.length) : new Int8Array(arr.length);
			toSend.set(arr);
			return toSend.buffer;
		}
	};
	var send = function(chName, data) {
		if(channelOpen !== chName) throw new ServerMessageAPIError("Cannot send message on channel \"" + chName + "\", that channel is not open!");
		if(typeof data === "string") {
			window.parent.postMessage({ver:1,channel:chName,data:data}, "*");
		}else if(data instanceof ArrayBuffer) {
			window.parent.postMessage({ver:1,channel:chName,data:data}, "*");
		}else if((data instanceof Uint8Array) || (data instanceof Int8Array)) {
			window.parent.postMessage({ver:1,channel:chName,data:fixTypedArray(data)}, "*");
		}else {
			throw new ServerMessageAPIError("Only strings, ArrayBuffers, Uint8Arrays, and Int8Arrays can be sent with this function!");
		}
	};
	return {
		ServerMessageAPIError: ServerMessageAPIError,
		openChannel: openCh,
		closeChannel: closeCh,
		addEventListener: addListener,
		removeEventListener: remListener,
		send: send
	};
})();
