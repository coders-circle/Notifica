$(function() {
    refreshviews();
});

function refreshviews() {
    var mintime = Number($("#col-r").data("starttime"));
    var maxtime = Number($("#col-r").data("endtime"));
    var timewidth = maxtime - mintime;

    $("#col-r .row-day").each(function(i) {
        // parent row element
        var pel = $(this);
        pel.data("lasttime", mintime);

        $(this).children(".subject-element").each(function(j) {
            // period element
            var el = $(this);
            var st = Number(el.data("starttime"));
            var et = Number(el.data("endtime"));

            // percentage of coverage in width
            var p = (et-st)/timewidth*100;

            // check if two periods overlap
            var currentParent = pel;
            var rownum = 1;
            while (st < Number(currentParent.data("lasttime"))) {
                rownum++;
                // get next row if overlaps, create next row if doesn't exist
                var nextrow = currentParent.nextrow;
                if (!nextrow) {
                    nextrow = $("<div class='row row-day-n"+rownum+"' data-lasttime='" +mintime+ "'></div>");
                    currentParent.after(nextrow);
                    currentParent.nextrow = nextrow;
                }
                currentParent = nextrow;
            }

            // move to next row if overlap occurs
            if (currentParent != pel) {
                el.detach().appendTo(currentParent);
            }
            
            // if last time of this row isn't starting time, we may need to add some gap
            var nlt = Number(currentParent.data("lasttime"))
            if (st > nlt) {
                var t = (st-nlt)/timewidth*100;
                el.before("<div class='period' style='float:left;width:" + t + "%;'>&nbsp;</div>")
            }
            currentParent.data("lasttime", ""+et+"");

            // finally position the element properly
            el.css('float', 'left');
            el.css('width', p+'%');
        });
        
        var current = pel;
        var ht = 0;
        while(current) {
            // calculate total height
            ht += current.height();
            /*
            // add final extra space
            var lt = current.data("lasttime");
            if (lt < maxtime) {
                    var t = (maxtime-lt)/timewidth*100;
                    current.append("<div class='period' style='float:left;width:" + t + "%;'>&nbsp;</div>")
            }
            */
            current = current.nextrow;
        }

        // change days' heights as well
        var dayrow = $("#col-l div:nth-child("+(i+1)+")");
        dayrow.css("height", ht+"px");
    });
}
