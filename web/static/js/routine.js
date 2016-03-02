var min_time = 9999;
var max_time = 0;

function renderRoutine(){
    filterRoutine(user_group_id);
    calculateMinMaxTime();
    renderTimeLine();
    renderPeriods();
}
function filterRoutine(group_id){
    if(group_id != null && group_id != ""){
        routine = [];
        for(var i=0; i<routine_all.length; i++){
            routine.push([]);
        }
        for(var i=0; i<routine_all.length; i++){
            for(var j=0; j<routine_all[i].length; j++){
                if(routine_all[i][j].groups.length == 0){
                    routine[i].push(routine_all[i][j]);
                }
                else if($.grep(routine_all[i][j].groups, function(item){
                    return item.id == group_id;
                }).length != 0){
                    routine[i].push(routine_all[i][j]);
                }
            }
        }
    } else {
        routine = routine_all;
    }
}
function getNameString(arr){
    var formattedStr = '';
    for(var i = 0; i < arr.length; i++){
        formattedStr += arr[i].name;
        if(i != arr.length-1) formattedStr += ", ";
    }
    return formattedStr;
}
function getFormattedTimeString(mins){
    var f_hrs = Math.floor(mins/60).toString();
    var f_mins = (mins%60).toString();
    if(f_mins.length == 1) f_mins = '0'+f_mins;
    return (f_hrs + ':' + f_mins);
}
function calculateMinMaxTime(){
    min_time = 9999;
    max_time = 0;
    for(var i=0; i < routine.length; i++){
        var periods = routine[i];
        for(var j=0; j < periods.length; j++){
            if( periods[j].start_time < min_time ){
                min_time = periods[j].start_time;
            }
            if( periods[j].end_time > max_time ){
                max_time = periods[j].end_time;
            }
        }
    }
}
function sortUnique(arr) {
    arr.sort();
    var last_i;
    for (var i=0;i<arr.length;i++)
        if ((last_i = arr.lastIndexOf(arr[i])) !== i)
            arr.splice(i+1, last_i-i);
    return arr;
}
function getTimeStops(){
    var timestops = [];
    for(var i=0; i < routine.length; i++){
        var periods = routine[i];
        for(var j=0; j < periods.length; j++){
            timestops.push(periods[j].start_time);
            timestops.push(periods[j].end_time);
        }
    }
    timestops = sortUnique(timestops);
    return timestops;
}
function renderTimeLine(){
    if( max_time == 0) return;
    var old_time_stops = $('.time-stop');
    if(old_time_stops){
        old_time_stops.remove();
    }
    var time_line = $('#time-line');
    var total_duration = max_time-min_time;
    var time_stop_template = $('<input class="time-stop">');
    timestops = getTimeStops();
    for(var i=0; i < timestops.length; i++){
        var time_stop = time_stop_template.clone();
        time_stop.attr('value', getFormattedTimeString(timestops[i]));
        if(edit_routine != "true"){
            time_stop.attr('disabled', true);
        }
        time_stop.appendTo(time_line);
        time_stop.css('margin-left', (100*(timestops[i]-min_time)/total_duration-i*0.6).toString()+'%');
    }
}
function renderPeriods(){
    var old_periods = $('.periods');
    if(old_periods){
        old_periods.remove();
    }
    for(var i=0; i < routine.length; i++){
        var periods = routine[i];
        if(periods.length > 0){
            periods.sort(function(a, b){
                if(a.start_time < b.start_time) return -1;
                else if(a.start_time > b.start_time) return 1;
                else return 0;
            });
            disp_periods = [];
            disp_periods.push([]);
            for(var n=0; n<periods.length; n++){
                disp_periods[0].push(periods[n]);
            }
            var current_depth = 0;
            while(current_depth < disp_periods.length){
                for(var j=1; j < disp_periods[current_depth].length; j++){
                    if(disp_periods[current_depth][j].start_time >= disp_periods[current_depth][j-1].start_time
                        && disp_periods[current_depth][j].start_time < disp_periods[current_depth][j-1].end_time){
                        if(disp_periods.length == current_depth+1){
                            disp_periods.push([]);
                        }
                        disp_periods[current_depth+1].push(disp_periods[current_depth][j]);
                        disp_periods[current_depth].splice(j, 1);
                        --j;
                    }
                }
                ++current_depth;
            }
            var periods_col = $('.row-day-'+i).find('.col-periods');
            var period_container_template = $('<div class="periods"></div>');
            var period_template = $('.template-period').clone();
            period_template.removeClass('hidden');
            period_template.removeClass('template-period');
            period_template.addClass('period');
            var total_duration = max_time - min_time;
            for(var d=0; d < disp_periods.length; d++){
                var period_container = period_container_template.clone();
                period_container.addClass('period-row-'+d);
                var prev_time = min_time;
                for(var c=0; c < disp_periods[d].length; c++){
                    var period = period_template.clone();
                    var period_data = disp_periods[d][c];
                    var period_duration = period_data.end_time - period_data.start_time;
                    var time_gap = (period_data.start_time - prev_time);
                    if( time_gap > 0 ){
                        period.css('margin-left', Math.floor(100*time_gap/total_duration).toString()+'%');
                    }
                    period.css('width', Math.floor(100*period_duration/total_duration).toString()+'%');
                    period.data('id', period_data.id);
                    period.children(".subject").text(period_data.subject.name);
                    period.children(".teachers").text(getNameString(period_data.teachers));
                    period.children(".remarks").text(period_data.remarks);
                    period.children(".type").text(period_data.period_type==1?'P':'T');
                    period.appendTo(period_container);
                    prev_time = period_data.end_time;
                }
                period_container.appendTo(periods_col);
            }
        }
    }
}

$(document).ready(function(){
    renderRoutine();
});
