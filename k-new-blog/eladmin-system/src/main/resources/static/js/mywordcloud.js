$(function () {
    var wordData = {};
    $('#main-blog ul').each(function () {
       $(this).find('li').each(function () {
          var aTag = $(this).find('a').text();
          var dir = aTag.split('：')[0];
          var title = aTag.split('：')[1];
          if (!wordData[dir]) {
            wordData[dir] = 1
          } else {
            wordData[dir] += 1
          }
//          var size = $(this).nextUntil('h3').filter('li').length;
//          console.error(title, size);
//          arrData.push([title, size]);
       })
    });
    var arrData = [];
    for (let key in wordData) {
        var count = wordData[key];
        if (wordData[key] >= 10) {
            count = 5;
        } else if (wordData[key] >= 5) {
            count = 4;
        } else if (wordData[key] >= 2) {
            count = 3;
        } else if (wordData[key] >= 0) {
            count = 2;
        }
        arrData.push([key, count]);
    }
    // console.error(wordData, arrData);
//    var wordFreqData = [['各位观众',45],['词云', 21],['来啦!!!',13]];
    var canvas = document.getElementById('canvas');
    var options = {
        list: arrData,
        gridSize: 13, // 单词之间的间隔越大。
        weightFactor: 6, // 用于调用或为size列表中的每个单词相乘的数字的功能。
        fontFamily: '微软雅黑, 楷体, Helvetica Neue, Helvetica, Arial,sans-serif, Finger Paint, cursive, sans-serif',
        color: '#f0f0c0',
        click: function(item) {
          search(item[0]);
        },
        backgroundColor: '#white',
        rotateRatio: 0 // 字体倾斜(旋转)概率，1代表总是倾斜(旋转)
        };
    // 生成
    WordCloud(canvas, options);
})