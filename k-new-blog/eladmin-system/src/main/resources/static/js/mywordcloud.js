$(function () {
    var arrData = [];
    $('#main-blog ul').each(function () {
       $(this).find('h3').each(function () {
          var title = $(this).text();
          var size = $(this).nextUntil('h3').filter('li').length;
//          console.error(title, size);
          arrData.push([title, size]);
       })
    });
    console.error(arrData);

//    var wordFreqData = [['各位观众',45],['词云', 21],['来啦!!!',13]];
    var canvas = document.getElementById('canvas');
    var options = {
        list: arrData,
        gridSize: 12, // 单词之间的间隔越大。
        weightFactor: 4, // 用于调用或为size列表中的每个单词相乘的数字的功能。
        fontFamily: 'Finger Paint, cursive, sans-serif',
        color: '#f0f0c0',
        click: function(item) {
          search(item[0]);
        },
        backgroundColor: '#white'};
    // 生成
    WordCloud(canvas, options);
})