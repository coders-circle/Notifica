$(function() {
    refreshviews();
});

function refreshviews() {
    var mintime = Number($("#col-r").data("starttime"));
    var maxtime = Number($("#col-r").data("endtime"));
    var timewidth = maxtime - mintime;

    $(".row-day").each(function(i) {
        var lasttime = mintime;
        $(this).children(".period").each(function(j) {
            var el = $(this);
            var st = Number(el.data("starttime"));
            var et = Number(el.data("endtime"));

            if (st > lasttime) {
                var t = (st-lasttime)/timewidth*100;
                el.before("<div class='period' style='float:left;width:" + t + "%;'>&nbsp;</div>")
            }

            var p = (et-st)/timewidth*100;

            el.css('float', 'left');
            el.css('width', p+'%');
            lasttime = et;
        });
    });
}
