jQuery(document).ready(function($) {
	var min_time = 9999;
	var max_time = 0;
	renderPeriods();
	var $subject_select = $('#subject-input').selectize({
		placeholder: 'Subject',
        valueField: 'id',
        labelField: 'name',
        searchField: ['name'],
        sortField: 'name',
		persist: false,
		openOnFocus: false,
		maxItems: 1,
		closeAfterSelect: true,
		create: function(input){
			return{
				id: -1,
				name: input
			}
		},
        render: {
            option: function(item, escape) {
				return '<div class="li-suggestion">' +
                    (item.name ? '<span class="name">' + escape(item.name) + '</span>' : 'oops! something is wrong') +
                '</div>';
            }
        },
        load: function(query, callback) {
            if (!query.length) return callback();
            $.ajax({
                url: '/classroom/api/v1/subjects/?format=json&q=' + encodeURIComponent(query),
                type: 'GET',
                error: function() {
                    callback();
                },
                success: function(res) {
                    callback(res.slice(0, 10));
                }
            });
        }
    });

	var newID = -1;
	function getNewID(){
		return newID--;
	}

	var $teachers_select = $('#teachers-input').selectize({
		placeholder: 'Teachers',
		labelField: 'name',
        valueField: 'id',
        searchField: ['name', 'username'],
        sortField: 'name',
		persist: false,
		closeAfterSelect: true,
		openOnFocus: false,
        create: function(input){
			return{
				id: getNewID(),
				name: input
			}
		},
        render: {
            option: function(item, escape) {
				return '<div class="li-suggestion">' +
                    (item.name ? '<span class="name">' + escape(item.name) + '&nbsp;</span>' : '') +
                    (item.username ? '<br><span class="username">' + escape(item.username) + '</span>' : '') +
                    (item.email ? '<br><span class="email">' + escape(item.email) + '</span>' : '') +
                '</div>';
            }
        },
        load: function(query, callback) {
            if (!query.length) return callback();
            $.ajax({
                url: '/classroom/api/v1/teachers/?format=json&q=' + encodeURIComponent(query),
                type: 'GET',
                error: function() {
                    callback();
                },
                success: function(res) {
					var newRes = res.slice(0, 10);
					var newArr = [];
					for (var i = 0; i < newRes.length; i++ ){
						var item = newRes[i];
						newArr.push({
							id: item.id,
							username: item.user.username,
							name: item.user.first_name + ' ' + item.user.last_name,
							email: item.user.email
						});
					}
                    callback(newArr);
                }
            });
        }
    });

	var teachers_control = $teachers_select[0].selectize;
	var subject_control = $subject_select[0].selectize;

	function getNameString(arr){
		var formattedStr = '';
		for(var i = 0; i < arr.length; i++){
			formattedStr += arr[i].name;
			if(i != arr.length-1) formattedStr += ", ";
		}
		return formattedStr;
	}
	function getFormattedTimeString(mins){
		return (Math.floor(mins/60)).toString() + ':' + (mins%60).toString();
	}
	function getMinutes(formattedTimeStr){
		timeSlices = formattedTimeStr.split(':');
		return parseInt(timeSlices[0]*60) + parseInt(timeSlices[1]);
	}

	function calculateMinMaxTime(){
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
	function renderTimeLine(){
		var old_time_stops = $('.time-stop');
		if(old_time_stops){
			old_time_stops.remove();
		}
		var time_line = $('#time-line');
		var num_time_stops = 5;
		var total_duration = max_time-min_time;
		var time_stop_template = $('<div class="time-stop"></div>');
		for(var i=0; i < num_time_stops; i++){
			var time_stop = time_stop_template.clone();
			time_stop.text(getFormattedTimeString(Math.floor(min_time+i*total_duration/(num_time_stops-1))));
			time_stop.appendTo(time_line);
			time_stop.css('padding-left', (99*i/(num_time_stops-1)).toString()+'%');
		}

	}
	function renderPeriods(){
		var old_periods = $('.periods');
		if(old_periods){
			old_periods.remove();
		}
		calculateMinMaxTime();
		renderTimeLine();
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
							period.css('margin-left', Math.ceil(100*time_gap/total_duration).toString()+'%');
						}
						period.css('width', Math.ceil(100*period_duration/total_duration).toString()+'%');
						period.data('id', period_data.id);
						period.children(".subject").text(period_data.subject.name);
						period.children(".teachers").text(getNameString(period_data.teachers));
						period.children(".remarks").text(period_data.remarks);
						period.appendTo(period_container);
						prev_time = period_data.end_time;
					}
					period_container.appendTo(periods_col);
				}

			}
		}
	}

	$('body').on( 'click', '.btn-add-period', function(e){
		var add_subject_dialog = $('body').find("#add-subject-dialog");
		var start_time = add_subject_dialog.find(".input-start-time");
		var end_time = add_subject_dialog.find(".input-end-time");
		var remarks = add_subject_dialog.find(".input-remarks");

		start_time.val("");
		end_time.val("");
		remarks.val("");

		teachers_control.clear();
		subject_control.clear();

		add_subject_dialog.data("new-period", true);
		add_subject_dialog.data("index-day", $(this).parent().parent().data('index-day'));
		add_subject_dialog.modal('show');
    });

	$('body').on( 'click', '.period', function(e){
		var add_subject_dialog = $('body').find("#add-subject-dialog");
		add_subject_dialog.data("new-period", false);
		add_subject_dialog.data("id-period", $(this).data('id'));
		var day_index = $(this).parent().parent().parent().data('index-day');
		add_subject_dialog.data("index-day", day_index);
		var periods = routine[day_index];
		var period = $(this);
		var period_data = $.grep(periods, function(item){
			return item.id == period.data('id');
		})[0];

		var ip_start_time = add_subject_dialog.find(".input-start-time");
		var ip_end_time = add_subject_dialog.find(".input-end-time");
		var ip_remarks = add_subject_dialog.find(".input-remarks");

		ip_start_time.val(getFormattedTimeString(period_data.start_time));
		ip_end_time.val(getFormattedTimeString(period_data.end_time));
		ip_remarks.val(period_data.remarks);
		teachers_control.clear();
		subject_control.clear();
		for(var i=0; i < period_data.teachers.length; i++){
			teachers_control.addOption(period_data.teachers[i]);
			teachers_control.addItem(period_data.teachers[i].id);
		}
		subject_control.addOption(period_data.subject);
		subject_control.addItem(period_data.subject.id);

		add_subject_dialog.modal('show');
	});

	$('body').on( 'click', '.btn-dlg-ok', function(e){
		var add_subject_dialog = $('body').find("#add-subject-dialog");
		var arr_teachers = [];
		for(var i=0; i < teachers_control.items.length; i++){
			arr_teachers.push({
				id:teachers_control.items[i],
				name: teachers_control.getItem(teachers_control.items[i]).text()
			});
		}
		var obj_subject = {
			id: subject_control.items[0],
			name: subject_control.getItem(subject_control.items[0]).text()
		};
		var day_index = add_subject_dialog.data('index-day');
		if(add_subject_dialog.data("new-period")){
			var new_period = {
				id: periodsCount++,
				subject: obj_subject,
				teachers: arr_teachers,
				start_time: getMinutes(add_subject_dialog.find(".input-start-time").val()),
				end_time: getMinutes(add_subject_dialog.find(".input-end-time").val()),
				remarks: add_subject_dialog.find(".input-remarks").val()
			};
			routine[day_index].push(new_period);
		}else{
			var period_data = $.grep(routine[add_subject_dialog.data('index-day')], function(item){
				return item.id == add_subject_dialog.data('id-period');
			})[0];
			period_data.subject = obj_subject;
			period_data.teachers = arr_teachers;
			period_data.start_time = getMinutes(add_subject_dialog.find(".input-start-time").val());
			period_data.end_time = getMinutes(add_subject_dialog.find(".input-end-time").val());
			period_data.remarks = add_subject_dialog.find(".input-remarks").val();
		}
		renderPeriods();
		add_subject_dialog.modal('hide');
	});
	$('body').on( 'click', '.btn-dlg-cancel', function(e){
		var add_subject_dialog = $('body').find("#add-subject-dialog");
		add_subject_dialog.modal('hide');
	});
});
