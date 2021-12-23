var db_helper = {
    check: function () {
        if ("indexedDB" in window) {
            // 支持
            return true;
        } else {
            // 不支持
            alert("您的浏览器不支持 indexedDB!");
            return false;
        }
    },
    onopen: function (option) {
        if (!this.check()) {
            return;
        }

        var openRequest = indexedDB.open(option.schema || 'local', option.version || 1);

        // blocked：上一次的数据库连接还未关闭。
        // upgradeneeded：第一次打开该数据库，或者数据库版本发生变化。
        openRequest.onupgradeneeded = function (e) {
            if (option.onupgradeneeded && typeof option.onupgradeneeded == "function") {
                option.onupgradeneeded(e);
            }
        }

        // success：打开成功。
        openRequest.onsuccess = function (e) {
            if (option.onsuccess && typeof option.onsuccess == "function") {
                option.onsuccess(e, e.target.result);
            }
        }

        // error：打开失败。
        openRequest.onerror = function (e) {
            console.dir(e);
            if (option.onerror && typeof option.onerror == "function") {
                option.onerror(e);
            }
        }
    },
    createTable: function (option) {
        if (!option.db) {
            this.onopen({
                schema: option.schema,
                version: option.version,
                onerror: option.onerror,
                onupgradeneeded: function (e) {
                    var db = e.target.result;
                    if (!db.objectStoreNames.contains(option.table)) {
                        if (!option.autoIncrement) {
                            db.createObjectStore(option.table, {keyPath: option.pkName||'id'});
                        } else {
                            db.createObjectStore(option.table, { autoIncrement: true });
                        }
                    }
                    if (option.onupgradeneeded && typeof option.onupgradeneeded == "function") {
                        option.onupgradeneeded(e, db);
                    }
                },
                onsuccess: function (e, db) {
                    if (option.onsuccess && typeof option.onsuccess == "function") {
                        option.onsuccess(e, db);
                    }
                }
            })
            return;
        }
        if (!option.db.objectStoreNames.contains(option.table)) {
            option.db.createObjectStore(option.table, {keyPath: option.pkName||'id'});
        }
    },
    insert: function (option) {
        // transaction方法有三个事件，可以用来定义回调函数。
        // abort：事务中断。
        // complete：事务完成。
        // error：事务出错。
        var t = option.db.transaction([option.table], "readwrite");
        t.oncomplete = function (event) {
            // some code
            if (option.oncomplete && typeof option.oncomplete == "function") {
                option.oncomplete(event)
            }
        };
        var store = t.objectStore(option.table);
        var o = option.data;
        var request = store.add(o);
        request.onerror = function (e) {
            console.log("Error", e.target.error.name);
            // error handler
            if (option.onerror && typeof option.onerror == "function") {
                option.onerror(e)
            }
        }

        request.onsuccess = function (e) {
            if (option.onsuccess && typeof option.onsuccess == "function") {
                option.onsuccess(e)
            }
        }
    },
    selectById: function (option) {
        var t = option.db.transaction([option.table], "readonly");
        t.oncomplete = function (event) {
            // some code
            if (option.oncomplete && typeof option.oncomplete == "function") {
                option.oncomplete(event)
            }
        };
        var store = t.objectStore(option.table);
        var ob = store.get(option.pkValue);
        ob.onsuccess = function (e) {
            // ...
            if (option.onsuccess && typeof option.onsuccess == "function") {
                option.onsuccess(e)
            }
        }
        ob.onerror = function (e) {
            // ...
            if (option.onerror && typeof option.onerror == "function") {
                option.onerror(e);
            }
        }
    },
    selectAll: function (option) {
        var t = option.db.transaction([option.table], "readonly");
        t.oncomplete = function (event) {
            // some code
            if (option.oncomplete && typeof option.oncomplete == "function") {
                option.oncomplete(event)
            }
        };
        var store = t.objectStore(option.table);
        var cursor = store.openCursor();
        cursor.onsuccess = function(e) {
            // var res = e.target.result;
            // if(res) {
            //     console.log("Key", res.key);
            //     console.dir("Data", res.value);
            //     res.continue();
            // }
            if (option.onsuccess && typeof option.onsuccess == "function") {
                option.onsuccess(e)
            }
        }

    },
    update: function (option) {
        var t = option.db.transaction([option.table], "readwrite");
        t.oncomplete = function (event) {
            // some code
            if (option.oncomplete && typeof option.oncomplete == "function") {
                option.oncomplete(event)
            }
        };
        var store = t.objectStore(option.table);
        var o = option.data;
        var request = store.put(o);
        request.onerror = function (e) {
            console.log("Error", e.target.error.name);
            // error handler
            if (option.onerror && typeof option.onerror == "function") {
                option.onerror(e)
            }
        }

        request.onsuccess = function (e) {
            if (option.onsuccess && typeof option.onsuccess == "function") {
                option.onsuccess(e)
            }
        }
    },
    delete: function (option) {
        var t = option.db.transaction([option.table], "readwrite");
        t.oncomplete = function (event) {
            // some code
            if (option.oncomplete && typeof option.oncomplete == "function") {
                option.oncomplete(event)
            }
        };
        var request = t.objectStore(option.table).delete(option.pkValue);
        request.onerror = function (e) {
            console.log("Error", e.target.error.name);
            // error handler
            if (option.onerror && typeof option.onerror == "function") {
                option.onerror(e)
            }
        }

        request.onsuccess = function (e) {
            if (option.onsuccess && typeof option.onsuccess == "function") {
                option.onsuccess(e)
            }
        }
    },
    put: function (option) {
        let that = this;
        // 读，有则更新，无则插入
        this.selectById({
            db: option.db,
            table: option.table,
            pkValue: option.data[option.pkName||'id'],
            onerror: option.onerror,
            onsuccess: function (e) {
                if (e.target.result) {
                    // 存在
                    that.update(option)
                    return;
                }
                that.insert(option)
            }
        })
    },

}
