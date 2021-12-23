let websocket_helper = {
    load: function (option) {
        if ("WebSocket" in window) {
            console.log("您的浏览器支持 WebSocket!");
        } else {
            // 浏览器不支持 WebSocket
            alert("您的浏览器不支持 WebSocket!");
            return;
        }

        if (!option.key) {
            option.onerror("未登录")
            return;
        }

        var ws = new WebSocket(`ws://${window.location.host}/ws?k=${option.key}`);
        ws.onopen = function () {
            option.onopen(ws);
        };
        ws.onmessage = function (evt) {
            option.onmessage(evt)
        };

        ws.onclose = function () {
            option.onclose();
        };
        ws.onerror = function (e) {
        }
    },
    push: function (option) {
        var content = {
            content: option.content,
            to: option.to,
            action: option.action,
        }
        option.ws.send(JSON.stringify(content));
    }
}