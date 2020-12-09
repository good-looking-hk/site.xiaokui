var list = [];
$.post("/blog/music/1/july", function (res) {
    // console.log(res);
    for (var i in res) {
        var o = new Object();
        o.name = res[i];
        o.artist = 'July';
        o.url = '/music/1/july/' + res[i];
        o.cover = '/img/July' + (i % 2 === 0 ? 1 : 2) + '.jpg';
        list.push(o);
    }
    var ap = new APlayer({
        container: document.getElementById('aplayer'),
        fixed: true,
        autoplay: false,// false
        // theme: '#FADFA3',
        theme: '#00FF00',
        loop: 'one', //'all', 'one', 'none'
        order: 'list',// list/random
        preload: 'none',//auto/none
        volume: 0.4,
        mutex: true,
        listFolded: false,
        listMaxHeight: 90,
        lrcType: 0,
        audio: list
    });
});

