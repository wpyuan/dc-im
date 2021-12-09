/**
 * 布局通用方法
 */

window.onresize = function () {
    var iframes = document.getElementsByTagName("iframe");
    if (iframes && iframes.length > 0) {
        for (let i = 0; i <  iframes.length; i++) {
            iframeAutoSize(iframes[i]);
        }
    }
}
function iframeAutoSize(iframe) {
    if (iframe) {
        iframe.height = document.documentElement.clientHeight;
    }
}