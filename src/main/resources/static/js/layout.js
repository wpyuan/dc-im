/**
 * 布局通用方法
 */

window.onresize = function () {
    var iframes = document.getElementsByTagName("iframe");
    if (iframes && iframes.length > 0) {
        iframes.forEach((item) => {
            iframeAutoSize(item);
        })
    }
}
function iframeAutoSize(iframe) {
    if (iframe) {
        iframe.height = document.documentElement.clientHeight;
    }
}